package com.aofeng.safecheck.model;

import gueei.binding.Command;
import gueei.binding.cursor.CursorRowModel;
import gueei.binding.observables.StringObservable;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.aofeng.safecheck.modelview.JobDownModel;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

public class JobRowModel extends CursorRowModel {
	// ��Ӧ��ModelView
	private final JobDownModel model;
	
	//����ƻ�����Ҫ�����С��
	private JSONObject UnitGroup = new JSONObject();
	
	private JSONArray AddrGroup = new JSONArray();

	// �ƻ�id
	public final String ID;

	public JobRowModel(String ID, JobDownModel model) {
		this.ID = ID;
		this.model = model;
	}

	// ��ʾ������
	public StringObservable Name = new StringObservable("");

	// ����״̬
	public StringObservable State = new StringObservable("");

	// ������������
	public Command JobDown = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
		
			// �鿴�����ļ��Ƿ����
			if(!Util.DBExists(model.mContext))
				return;
			
			if(model.mContext.isBusy)
			{
				Toast.makeText(model.mContext, "��ȴ�������ɡ�", Toast.LENGTH_SHORT).show();
				return;
			}
			model.mContext.isBusy = true;
			// ���ú�̨���񣬻�ȡ�ƻ�����
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						GetCheckInfo();
						SearchUnitFromDB();
						GetUserInfo();
						SearchAddress();
						GetSafeInfo();
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = -1;
						jobHandler.sendMessage(msg);
					}
				}

			private void SearchAddress() {
				try {
					SQLiteDatabase db = model.getActivity().openOrCreateDatabase("safecheck.db",
							Context.MODE_PRIVATE, null);

					Cursor c = db.rawQuery("select ROAD, UNIT_NAME, CUS_DOM, CUS_DY, CUS_FLOOR, CUS_ROOM from T_IC_SAFECHECK_PAPAER", new String[] {});
					for (int i = 0; i < c.getCount(); i++)
					{
						while (c.moveToNext()) {
							if(c.isNull(c.getColumnIndex("ROAD")) && c.isNull(c.getColumnIndex("UNIT_NAME")) && c.isNull(c.getColumnIndex("CUS_DOM")) && c.isNull(c.getColumnIndex("CUS_DY")) && c.isNull(c.getColumnIndex("CUS_FLOOR")) && c.isNull(c.getColumnIndex("CUS_ROOM")))
								continue;
							JSONObject json = new JSONObject();
							json.put("ROAD", c.getString(c.getColumnIndex("ROAD")));
							json.put("UNIT_NAME", c.getString(c.getColumnIndex("UNIT_NAME")));
							json.put("CUS_DOM", c.getString(c.getColumnIndex("CUS_DOM")));
							json.put("CUS_DY", c.getString(c.getColumnIndex("CUS_DY")));
							json.put("CUS_FLOOR", c.getString(c.getColumnIndex("CUS_FLOOR")));
							json.put("CUS_ROOM", c.getString(c.getColumnIndex("CUS_ROOM")));
							AddrGroup.put(json);
						}
					}
					db.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			private void GetSafeInfo() {
				if(null == AddrGroup || 0 == AddrGroup.length())
				{
					return;
				}
				try {
					String myurl = Vault.SEARCH_URL + "gcis/getsafeinfo";
					URL url = new URL(myurl);  
				    URLConnection conn = url.openConnection(); 
				    conn.setDoOutput(true);  
				    conn.setDoInput(true);  
				    conn.getOutputStream().write(AddrGroup.toString().getBytes("utf8"));  
				    conn.getOutputStream().flush();  
				    conn.getOutputStream().close();  
					InputStream is = conn.getInputStream();
					FileOutputStream fos = model.mContext.openFileOutput("download1.tmp", Context.MODE_PRIVATE);
				    byte buf[] = new byte[1024];
				    int bytesDownloaded = 0;
					Message msg = new Message();
					msg.what =0;
					msg.obj = 0;
					jobHandler.sendMessage(msg);
				    do
				    {
				        int numread = is.read(buf);
				        if (numread == -1)
				        {
				          break;
				        }
				        bytesDownloaded += numread;
				        fos.write(buf, 0, numread);
				    	msg = new Message();
						msg.obj = bytesDownloaded;
						msg.what = 0;
						jobHandler.sendMessage(msg);
				    }while(true);
				    fos.close();
				    BufferedReader br = new BufferedReader(new InputStreamReader(model.mContext.openFileInput("download1.tmp")));
				    String text = br.readLine();
				    br.close();
				    ImportsafeInfo(text);
					msg = new Message();
					msg.what = 2;
					jobHandler.sendMessage(msg);
					AddrGroup = new JSONArray();
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = -1;
					jobHandler.sendMessage(msg);
				}
			}

			private void ImportsafeInfo(String text) throws Exception {
				try {
					Message msg = new Message();
					msg.what = 1;
					msg.obj = "���밲��";
					jobHandler.sendMessage(msg);
					JSONArray jsons = new JSONArray(text);
					if(jsons.getJSONObject(0).has("ok"))
						if(jsons.getJSONObject(0).getString("ok").equals("nok"))
							return;
					SQLiteDatabase db = model.getActivity().openOrCreateDatabase(
							"safecheck.db", Context.MODE_PRIVATE, null);
					db.execSQL("delete from T_SAFECHECK");
					for(int j = 0; j < jsons.length(); j++)
					{
						JSONObject json = jsons.getJSONObject(j);
						//���氲����Ϣ�����ؿ�
						msg = new Message();
						msg.what = 1;
						msg.obj = "���밲��" + (j+1);
						jobHandler.sendMessage(msg);
						String COOK_BRAND = "null";
						if(json.has("col0"))
							COOK_BRAND = json.getString("col0");					//���Ʒ��
						String COOK_TYPE = "null";
						if(json.has("col1"))
							COOK_TYPE = json.getString("col1"); 					//����ͺ�
						String COOK_DATE = "null";
						if(json.has("col2"))
							COOK_DATE = json.getString("col2"); 					//��߹�������
						String WATER_BRAND = "null";
						if(json.has("col3"))
							WATER_BRAND = json.getString("col3"); 					//��ˮ��Ʒ��
						String WATER_TYPE = "null";
						if(json.has("col4"))
							WATER_TYPE = json.getString("col4");					//��ˮ���ͺ�
						String WATER_FLUE = "null";
						if(json.has("col5"))
							WATER_FLUE = json.getString("col5");					//��ˮ���̵�
						String WATER_DATE = "null";
						if(json.has("col6"))
							WATER_DATE = json.getString("col6");					//��ˮ����������
						String WHE_BRAND = "null";
						if(json.has("col7"))
							WHE_BRAND = json.getString("col7");						//�ڹ�¯Ʒ��
						String WHE_TYPE = "null";
						if(json.has("col8"))
							WHE_TYPE = json.getString("col8"); 						//�ڹ�¯�ͺ� 
						String WHE_DATE = "null";
						if(json.has("col9"))
							WHE_DATE = json.getString("col9"); 						//�ڹ�¯���� 
						String IC_METER_NAME = "null";
						if(json.has("col10"))
							IC_METER_NAME = json.getString("col10"); 				//IC��������
						String JB_METER_NAME = "null";
						if(json.has("col11"))
							JB_METER_NAME = json.getString("col11"); 				//����������
						String METER_TYPE = "null";
						if(json.has("col12"))
							METER_TYPE = json.getString("col12"); 					//���� 
						String gas_quantity = "null";
						if(json.has("col13"))
							gas_quantity = json.getString("col13"); 				//������
						String f_road = "null";
						if(json.has("col14"))
							f_road = json.getString("col14"); 						//�ֵ�
						String f_districtname = "null";
						if(json.has("col15"))
							f_districtname = json.getString("col15"); 				//С��
						String f_cusDom = "null";
						if(json.has("col16"))
							f_cusDom = json.getString("col16"); 					//¥��
						String f_cusDy = "null";
						if(json.has("col17"))
							f_cusDy = json.getString("col17"); 						//��Ԫ
						String f_cusFloor = "null";
						if(json.has("col18"))
							f_cusFloor = json.getString("col18"); 					//¥��
						String f_apartment = "null";
						if(json.has("col19"))
							f_apartment = json.getString("col19"); 					//����
						UUID uuid = UUID.randomUUID();
						String id = uuid.toString().replace("-", "");				//ID

						db.execSQL("INSERT INTO T_SAFECHECK("
								+ "id, "
								+ //���Ʒ��
								" COOK_BRAND, "
								+ //����ͺ�
								" COOK_TYPE, "
								+ //��߹�������
								" COOK_DATE ,"
								+ //��ˮ��Ʒ��
								" WATER_BRAND,"
								+ //��ˮ���ͺ�
								" WATER_TYPE,"
								+ //��ˮ���̵�
								" WATER_FLUE,"
								+ //��ˮ����������
								" WATER_DATE,"
								+ //�ڹ�¯Ʒ��
								" WHE_BRAND ,"
								+ //�ڹ�¯�ͺ� 
								" WHE_TYPE ,"
								+ //�ڹ�¯���� 
								" WHE_DATE ,"
								+ //IC��������
								" IC_METER_NAME ,"
								+ //����������
								" JB_METER_NAME,"
								+ //���� 
								" METER_TYPE, "
								+ //������
								" gas_quantity, "
								+ //�ֵ�
								" f_road, "
								+ //С��
								" f_districtname, "
								+ //¥��
								" f_cusDom, "
								+ //��Ԫ
								" f_cusDy, "
								+ //¥��
								" f_cusFloor, "
								+ //����
								" f_apartment"
								+ ") " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
								new Object[] { id,
										COOK_BRAND,
										COOK_TYPE,
										COOK_DATE,
										WATER_BRAND,
										WATER_TYPE,
										WATER_FLUE,
										WATER_DATE,
										WHE_BRAND,
										WHE_TYPE,
										WHE_DATE,
										IC_METER_NAME,
										JB_METER_NAME,
										METER_TYPE,
										gas_quantity,
										f_road,
										f_districtname,
										f_cusDom,
										f_cusDy,
										f_cusFloor,
										f_apartment});
					}
					db.close();
				} catch (Exception e) {
					throw new Exception();
				}
			}

			private void SearchUnitFromDB() throws Exception {
				try {
					SQLiteDatabase db = model.getActivity().openOrCreateDatabase("safecheck.db",
							Context.MODE_PRIVATE, null);

					Cursor c = db.rawQuery("select distinct UNIT_NAME from T_IC_SAFECHECK_PAPAER", new String[] {});
					for (int i = 0; i < c.getCount(); i++)
					{
						while (c.moveToNext()) {
							if(!c.isNull(c.getColumnIndex("UNIT_NAME")))
								UnitGroup.put(i + "", c.getString(c.getColumnIndex("UNIT_NAME")));
						}
					}
					db.close();
				} catch (Exception e) {
					throw new Exception();
				}
			}

			private void GetUserInfo() {
				if(null == UnitGroup || 0 == UnitGroup.length())
				{
					return;
				}
				try {
					for(int i = 0; i < UnitGroup.length(); i++)
					{
						String Unit = UnitGroup.getString(i + "");
						String url = Vault.SEARCH_URL
								+ "gcis/getuserinfo/"
								+ URLEncoder.encode(Unit, "UTF8").replace("+",
										"%20");
						URL myURL = new URL(url);
						URLConnection conn = myURL.openConnection();
						conn.connect();
						InputStream is = conn.getInputStream();
						FileOutputStream fos = model.mContext.openFileOutput("download1.tmp", Context.MODE_PRIVATE);
					    byte buf[] = new byte[1024];
					    int bytesDownloaded = 0;
						Message msg = new Message();
						msg.what =0;
						msg.obj = 0;
						jobHandler.sendMessage(msg);
					    do
					    {
					        int numread = is.read(buf);
					        if (numread == -1)
					        {
					          break;
					        }
					        bytesDownloaded += numread;
					        fos.write(buf, 0, numread);
					    	msg = new Message();
							msg.obj = bytesDownloaded;
							msg.what = 0;
							jobHandler.sendMessage(msg);
					    }while(true);
					    fos.close();
					    BufferedReader br = new BufferedReader(new InputStreamReader(model.mContext.openFileInput("download1.tmp")));
					    String text = br.readLine();
					    br.close();
					    ImportUserInfo(text, i);
					}
					UnitGroup = new JSONObject();
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = -1;
					jobHandler.sendMessage(msg);
				}
			}

			private void ImportUserInfo(String text, int i) throws Exception {
				try {
					Message msg = new Message();
					msg.what = 1;
					msg.obj = "���뵵��";
					jobHandler.sendMessage(msg);
					JSONArray jsons = new JSONArray(text);
					if(jsons.getJSONObject(0).has("ok"))
						if(jsons.getJSONObject(0).getString("ok").equals("nok"))
							return;
					SQLiteDatabase db = model.getActivity().openOrCreateDatabase(
							"safecheck.db", Context.MODE_PRIVATE, null);
					db.execSQL("delete from T_USERFILES");
					for(int j = 0; j < jsons.length(); j++)
					{
						JSONObject json = jsons.getJSONObject(j);
						//�����û����������ؿ�
						msg = new Message();
						msg.what = 1;
						msg.obj = "���뵵��" + (j+1);
						jobHandler.sendMessage(msg);
						String f_username = "null";
						if(json.has("col0"))
							f_username = json.getString("col0");					//�û�����
						String f_phone = "null";
						if(json.has("col1"))
							f_phone = json.getString("col1"); 						//�绰
						String f_address = "null";
						if(json.has("col2"))
							f_address = json.getString("col2"); 					//������ַ
						String f_cardid = "null";
						if(json.has("col3"))
							f_cardid = json.getString("col3"); 						//����
						String f_meternumber = "null";
						if(json.has("col4"))
							f_meternumber = json.getString("col4");					//���
						String f_aroundmeter = "null";
						if(json.has("col5"))
							f_aroundmeter = json.getString("col5");					//���ұ�
						String f_jbfactory = "null";
						if(json.has("col6"))
							f_jbfactory = json.getString("col6");					//������
						String f_road = "null";
						if(json.has("col7"))
							f_road = json.getString("col7");						//�ֵ�
						String f_districtname = "null";
						if(json.has("col8"))
							f_districtname = json.getString("col8"); 				//С��
						String f_cusDom = "null";
						if(json.has("col9"))
							f_cusDom = json.getString("col9"); 						//¥��
						String f_cusDy = "null";
						if(json.has("col10"))
							f_cusDy = json.getString("col10"); 						//��Ԫ
						String f_cusFloor = "null";
						if(json.has("col11"))
							f_cusFloor = json.getString("col11"); 					//¥��
						String f_apartment = "null";
						if(json.has("col12"))
							f_apartment = json.getString("col12"); 					//����
						String tsum = "null";
						if(json.has("col13"))
							tsum = json.getString("col13"); 						//�ܹ�����
						String tcount = "null";
						if(json.has("col14"))
							tcount = json.getString("col14"); 						//������
						String UserID = "null";
						if(json.has("col15"))
							UserID = json.getString("col15");						//�û����
						String UserState = "null";
						if(json.has("col16"))
							UserState = json.getString("col16");					//�û�״̬
						UUID uuid = UUID.randomUUID();
						String id = uuid.toString().replace("-", "");				//ID

						db.execSQL("INSERT INTO T_USERFILES("
								+ "id, "
								+ //�û�����
								" f_username, "
								+ //�绰
								" f_phone, "
								+ //������ַ
								" f_address ,"
								+ //����
								" f_cardid,"
								+ //���
								" f_meternumber,"
								+ //���ұ�
								" f_aroundmeter,"
								+ //������
								" f_jbfactory,"
								+ //�ֵ�
								" f_road ,"
								+ //С��
								" f_districtname ,"
								+ //¥��
								" f_cusDom ,"
								+ //��Ԫ
								" f_cusDy ,"
								+ //¥��
								" f_cusFloor,"
								+ //����
								" f_apartment, "
								+ //�ܹ�����
								" tsum, "
								+ //��������
								" tcount, "
								+ //�û����
								" f_userid, "
								+ //�û�״̬
								" f_userstate"
								+ ") " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
								new Object[] { id,
										f_username,
										f_phone,
										f_address,
										f_cardid,
										f_meternumber,
										f_aroundmeter,
										f_jbfactory,
										f_road,
										f_districtname,
										f_cusDom,
										f_cusDy,
										f_cusFloor,
										f_apartment,
										tsum,
										tcount,
										UserID,
										UserState});
					}
					db.close();
				} catch (Exception e) {
					throw new Exception();
				}
			}

			private void GetCheckInfo() {
				try {
					String hql = "select distinct c from T_CHECKPLAN c left join fetch c.f_checks"
							+ " where c.id='" + ID + "'";
					String url = Vault.DB_URL
							+ "one/"
							+ URLEncoder.encode(hql, "UTF8").replace("+",
									"%20");
			    	URL myURL = new URL(url);
			    	URLConnection conn = myURL.openConnection();
			    	conn.connect();
			    	InputStream is = conn.getInputStream();
			    	FileOutputStream fos = model.mContext.openFileOutput("download.tmp", Context.MODE_PRIVATE);
				    byte buf[] = new byte[1024];
				    int bytesDownloaded = 0;
					Message msg = new Message();
					msg.what =0;
					msg.obj = 0;
					jobHandler.sendMessage(msg);
				    do
				    {
				        int numread = is.read(buf);
				        if (numread == -1)
				        {
				          break;
				        }
				        bytesDownloaded += numread;
				        fos.write(buf, 0, numread);
				    	msg = new Message();
						msg.obj = bytesDownloaded;
						msg.what = 0;
						jobHandler.sendMessage(msg);
				    }while(true);
				    fos.close();
				    BufferedReader br = new BufferedReader(new InputStreamReader(model.mContext.openFileInput("download.tmp")));
				    String text = br.readLine();
				    br.close();
				    ImportPlan(text);
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = -1;
					jobHandler.sendMessage(msg);
				}					
			}

			private void ImportPlan(String text) {
				try {
					JSONObject json = new JSONObject(text);
					// ����ƻ������ؿ�
					String id = json.getString("id");
					String name = json.getString("f_name");
					SQLiteDatabase db = model.getActivity().openOrCreateDatabase(
							"safecheck.db", Context.MODE_PRIVATE, null);
					db.execSQL("delete from t_checkplan"); // where id=?",
							//new Object[] { id });
					db.execSQL("INSERT INTO t_checkplan(id, f_name) VALUES (?, ?)",
							new Object[] { id, name });
			    	Message msg = new Message();
					msg.what = 1;
					msg.obj = "����ƻ�";
					jobHandler.sendMessage(msg);

					// ɾ������
					db.execSQL(
							"delete from T_IC_SAFECHECK_HIDDEN");
					// where id in (select id from T_INSPECTION where CHECKPLAN_ID=?)",
					//		new Object[] { id });
					Cursor c = db.rawQuery(
							"SELECT id from T_INSPECTION where CHECKPLAN_ID =?", new String[] { id });
					if(c.moveToNext())
						Util.deleteFiles(model.mContext, c.getString(c.getColumnIndex("id")));
					// ɾ���뻧�����¼				
					db.execSQL(
							"delete from T_INSPECTION");
					//where CHECKPLAN_ID =?",
					//		new Object[] { id });
					// ɾ��ԭ���а�������
					db.execSQL(
							"delete from T_IC_SAFECHECK_PAPAER");
					// where CHECKPLAN_ID=?",
					//		new Object[] { id });
					// ����ƻ��еİ�����Ŀ�����ؿ�
					JSONArray array = json.getJSONArray("f_checks");
					for (int i = 0; i < array.length(); i++) {
				    	msg = new Message();
						msg.what = 1;
						msg.obj = "����ƻ�" + (i+1);
						jobHandler.sendMessage(msg);
						JSONObject aJson = array.getJSONObject(i);
						String aId = aJson.getString("id");
						String CARD_ID = aJson.getString("CARD_ID"); // ����
						String USER_NAME = aJson.getString("USER_NAME"); // �û�����
						String TELPHONE = aJson.getString("TELPHONE"); // �绰
						String ROAD = aJson.getString("ROAD");// �ֵ�
						String UNIT_NAME = aJson.getString("UNIT_NAME");// С��
						String CUS_DOM = aJson.getString("CUS_DOM");// ¥��
						String CUS_DY = aJson.getString("CUS_DY");// ��Ԫ
						String CUS_FLOOR = aJson.getString("CUS_FLOOR");// ¥��
						String CUS_ROOM = aJson.getString("CUS_ROOM");// ����
						String OLD_ADDRESS = aJson.getString("OLD_ADDRESS"); // �û�������ַ
						String SAVE_PEOPLE = aJson.getString("SAVE_PEOPLE"); // ����Ա

						db.execSQL("INSERT INTO T_IC_SAFECHECK_PAPAER("
								+ "id, CARD_ID, "
								+ // ����
								" USER_NAME ,"
								+ // �û�����
								" TELPHONE ,"
								+ // �绰
								" ROAD,"
								+ // �ֵ�
								" UNIT_NAME,"
								+ // С��
								" CUS_DOM,"
								+ // ¥��
								" CUS_DY,"
								+ // ��Ԫ
								" CUS_FLOOR ,"
								+ // ¥��
								" CUS_ROOM ,"
								+ // ����
								" OLD_ADDRESS ,"
								+ // �û�������ַ
								" SAVE_PEOPLE ,"
								//����״̬
								+ "CONDITION,"
								+ // ����ƻ�ID
								"CHECKPLAN_ID" + ") " + "VALUES (?, "
								+ "?, ?, ?, ?, ?, ?,?,  ?, ?, ?, ?, ?, ?)",
								new Object[] { aId, CARD_ID, // ����
										USER_NAME, // �û�����
										TELPHONE, // �绰
										ROAD, // �ֵ�
										UNIT_NAME, // С��
										CUS_DOM, // ¥��
										CUS_DY, // ��Ԫ
										CUS_FLOOR, // ¥��
										CUS_ROOM, // ����
										OLD_ADDRESS, // �û�������ַ
										SAVE_PEOPLE, // ����Ա
										"0",   //����״̬
										id // ����ƻ�ID
								});
					}
					db.close();
					Util.deleteAllPics(model.mContext);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
			th.start();
		}
	};

	// ��ȡĳ���ƻ���Ĵ������
	private final Handler jobHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			//���
			if(msg.what == 2)
			{
				State.set("������");
				model.clearPriorState(JobRowModel.this.ID);
				model.mContext.isBusy = false;
			}
			else if(msg.what == 1)
			{
				State.set(msg.obj.toString());
			}
			//������
			else if(msg.what==0)
			{
				State.set(msg.obj.toString() + "�ֽ�");
			}
			else
			{
				Toast.makeText(model.mContext, "���س���", Toast.LENGTH_SHORT).show();
				State.set("����");
				model.mContext.isBusy = false;
			}
		}

	};
}
