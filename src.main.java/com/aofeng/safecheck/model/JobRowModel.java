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
	// 对应的ModelView
	private final JobDownModel model;
	
	//安检计划中需要安检的小区
	private JSONObject UnitGroup = new JSONObject();
	
	private JSONArray AddrGroup = new JSONArray();

	// 计划id
	public final String ID;

	public JobRowModel(String ID, JobDownModel model) {
		this.ID = ID;
		this.model = model;
	}

	// 显示的名称
	public StringObservable Name = new StringObservable("");

	// 任务状态
	public StringObservable State = new StringObservable("");

	// 任务下载命令
	public Command JobDown = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
		
			// 查看数据文件是否存在
			if(!Util.DBExists(model.mContext))
				return;
			
			if(model.mContext.isBusy)
			{
				Toast.makeText(model.mContext, "请等待下载完成。", Toast.LENGTH_SHORT).show();
				return;
			}
			model.mContext.isBusy = true;
			// 调用后台服务，获取计划数据
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
					msg.obj = "插入安检";
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
						//保存安检信息到本地库
						msg = new Message();
						msg.what = 1;
						msg.obj = "插入安检" + (j+1);
						jobHandler.sendMessage(msg);
						String COOK_BRAND = "null";
						if(json.has("col0"))
							COOK_BRAND = json.getString("col0");					//灶具品牌
						String COOK_TYPE = "null";
						if(json.has("col1"))
							COOK_TYPE = json.getString("col1"); 					//灶具型号
						String COOK_DATE = "null";
						if(json.has("col2"))
							COOK_DATE = json.getString("col2"); 					//灶具购置日期
						String WATER_BRAND = "null";
						if(json.has("col3"))
							WATER_BRAND = json.getString("col3"); 					//热水器品牌
						String WATER_TYPE = "null";
						if(json.has("col4"))
							WATER_TYPE = json.getString("col4");					//热水器型号
						String WATER_FLUE = "null";
						if(json.has("col5"))
							WATER_FLUE = json.getString("col5");					//热水器烟道
						String WATER_DATE = "null";
						if(json.has("col6"))
							WATER_DATE = json.getString("col6");					//热水器购置日期
						String WHE_BRAND = "null";
						if(json.has("col7"))
							WHE_BRAND = json.getString("col7");						//壁挂炉品牌
						String WHE_TYPE = "null";
						if(json.has("col8"))
							WHE_TYPE = json.getString("col8"); 						//壁挂炉型号 
						String WHE_DATE = "null";
						if(json.has("col9"))
							WHE_DATE = json.getString("col9"); 						//壁挂炉日期 
						String IC_METER_NAME = "null";
						if(json.has("col10"))
							IC_METER_NAME = json.getString("col10"); 				//IC卡表厂名称
						String JB_METER_NAME = "null";
						if(json.has("col11"))
							JB_METER_NAME = json.getString("col11"); 				//基表厂家名称
						String METER_TYPE = "null";
						if(json.has("col12"))
							METER_TYPE = json.getString("col12"); 					//表型 
						String gas_quantity = "null";
						if(json.has("col13"))
							gas_quantity = json.getString("col13"); 				//用气量
						String f_road = "null";
						if(json.has("col14"))
							f_road = json.getString("col14"); 						//街道
						String f_districtname = "null";
						if(json.has("col15"))
							f_districtname = json.getString("col15"); 				//小区
						String f_cusDom = "null";
						if(json.has("col16"))
							f_cusDom = json.getString("col16"); 					//楼号
						String f_cusDy = "null";
						if(json.has("col17"))
							f_cusDy = json.getString("col17"); 						//单元
						String f_cusFloor = "null";
						if(json.has("col18"))
							f_cusFloor = json.getString("col18"); 					//楼层
						String f_apartment = "null";
						if(json.has("col19"))
							f_apartment = json.getString("col19"); 					//房号
						UUID uuid = UUID.randomUUID();
						String id = uuid.toString().replace("-", "");				//ID

						db.execSQL("INSERT INTO T_SAFECHECK("
								+ "id, "
								+ //灶具品牌
								" COOK_BRAND, "
								+ //灶具型号
								" COOK_TYPE, "
								+ //灶具购置日期
								" COOK_DATE ,"
								+ //热水器品牌
								" WATER_BRAND,"
								+ //热水器型号
								" WATER_TYPE,"
								+ //热水器烟道
								" WATER_FLUE,"
								+ //热水器购置日期
								" WATER_DATE,"
								+ //壁挂炉品牌
								" WHE_BRAND ,"
								+ //壁挂炉型号 
								" WHE_TYPE ,"
								+ //壁挂炉日期 
								" WHE_DATE ,"
								+ //IC卡表厂名称
								" IC_METER_NAME ,"
								+ //基表厂家名称
								" JB_METER_NAME,"
								+ //表型 
								" METER_TYPE, "
								+ //用气量
								" gas_quantity, "
								+ //街道
								" f_road, "
								+ //小区
								" f_districtname, "
								+ //楼号
								" f_cusDom, "
								+ //单元
								" f_cusDy, "
								+ //楼层
								" f_cusFloor, "
								+ //房号
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
					msg.obj = "插入档案";
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
						//保存用户档案到本地库
						msg = new Message();
						msg.what = 1;
						msg.obj = "插入档案" + (j+1);
						jobHandler.sendMessage(msg);
						String f_username = "null";
						if(json.has("col0"))
							f_username = json.getString("col0");					//用户姓名
						String f_phone = "null";
						if(json.has("col1"))
							f_phone = json.getString("col1"); 						//电话
						String f_address = "null";
						if(json.has("col2"))
							f_address = json.getString("col2"); 					//档案地址
						String f_cardid = "null";
						if(json.has("col3"))
							f_cardid = json.getString("col3"); 						//卡号
						String f_meternumber = "null";
						if(json.has("col4"))
							f_meternumber = json.getString("col4");					//表号
						String f_aroundmeter = "null";
						if(json.has("col5"))
							f_aroundmeter = json.getString("col5");					//左右表
						String f_jbfactory = "null";
						if(json.has("col6"))
							f_jbfactory = json.getString("col6");					//基表厂家
						String f_road = "null";
						if(json.has("col7"))
							f_road = json.getString("col7");						//街道
						String f_districtname = "null";
						if(json.has("col8"))
							f_districtname = json.getString("col8"); 				//小区
						String f_cusDom = "null";
						if(json.has("col9"))
							f_cusDom = json.getString("col9"); 						//楼号
						String f_cusDy = "null";
						if(json.has("col10"))
							f_cusDy = json.getString("col10"); 						//单元
						String f_cusFloor = "null";
						if(json.has("col11"))
							f_cusFloor = json.getString("col11"); 					//楼层
						String f_apartment = "null";
						if(json.has("col12"))
							f_apartment = json.getString("col12"); 					//房号
						String tsum = "null";
						if(json.has("col13"))
							tsum = json.getString("col13"); 						//总购气量
						String tcount = "null";
						if(json.has("col14"))
							tcount = json.getString("col14"); 						//购气量
						String UserID = "null";
						if(json.has("col15"))
							UserID = json.getString("col15");						//用户编号
						String UserState = "null";
						if(json.has("col16"))
							UserState = json.getString("col16");					//用户状态
						UUID uuid = UUID.randomUUID();
						String id = uuid.toString().replace("-", "");				//ID

						db.execSQL("INSERT INTO T_USERFILES("
								+ "id, "
								+ //用户姓名
								" f_username, "
								+ //电话
								" f_phone, "
								+ //档案地址
								" f_address ,"
								+ //卡号
								" f_cardid,"
								+ //表号
								" f_meternumber,"
								+ //左右表
								" f_aroundmeter,"
								+ //基表厂家
								" f_jbfactory,"
								+ //街道
								" f_road ,"
								+ //小区
								" f_districtname ,"
								+ //楼号
								" f_cusDom ,"
								+ //单元
								" f_cusDy ,"
								+ //楼层
								" f_cusFloor,"
								+ //房号
								" f_apartment, "
								+ //总购气量
								" tsum, "
								+ //购气次数
								" tcount, "
								+ //用户编号
								" f_userid, "
								+ //用户状态
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
					// 插入计划到本地库
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
					msg.obj = "插入计划";
					jobHandler.sendMessage(msg);

					// 删除隐患
					db.execSQL(
							"delete from T_IC_SAFECHECK_HIDDEN");
					// where id in (select id from T_INSPECTION where CHECKPLAN_ID=?)",
					//		new Object[] { id });
					Cursor c = db.rawQuery(
							"SELECT id from T_INSPECTION where CHECKPLAN_ID =?", new String[] { id });
					if(c.moveToNext())
						Util.deleteFiles(model.mContext, c.getString(c.getColumnIndex("id")));
					// 删除入户安检记录				
					db.execSQL(
							"delete from T_INSPECTION");
					//where CHECKPLAN_ID =?",
					//		new Object[] { id });
					// 删除原所有安检内容
					db.execSQL(
							"delete from T_IC_SAFECHECK_PAPAER");
					// where CHECKPLAN_ID=?",
					//		new Object[] { id });
					// 保存计划中的安检项目到本地库
					JSONArray array = json.getJSONArray("f_checks");
					for (int i = 0; i < array.length(); i++) {
				    	msg = new Message();
						msg.what = 1;
						msg.obj = "插入计划" + (i+1);
						jobHandler.sendMessage(msg);
						JSONObject aJson = array.getJSONObject(i);
						String aId = aJson.getString("id");
						String CARD_ID = aJson.getString("CARD_ID"); // 卡号
						String USER_NAME = aJson.getString("USER_NAME"); // 用户名称
						String TELPHONE = aJson.getString("TELPHONE"); // 电话
						String ROAD = aJson.getString("ROAD");// 街道
						String UNIT_NAME = aJson.getString("UNIT_NAME");// 小区
						String CUS_DOM = aJson.getString("CUS_DOM");// 楼号
						String CUS_DY = aJson.getString("CUS_DY");// 单元
						String CUS_FLOOR = aJson.getString("CUS_FLOOR");// 楼层
						String CUS_ROOM = aJson.getString("CUS_ROOM");// 房号
						String OLD_ADDRESS = aJson.getString("OLD_ADDRESS"); // 用户档案地址
						String SAVE_PEOPLE = aJson.getString("SAVE_PEOPLE"); // 安检员

						db.execSQL("INSERT INTO T_IC_SAFECHECK_PAPAER("
								+ "id, CARD_ID, "
								+ // 卡号
								" USER_NAME ,"
								+ // 用户名称
								" TELPHONE ,"
								+ // 电话
								" ROAD,"
								+ // 街道
								" UNIT_NAME,"
								+ // 小区
								" CUS_DOM,"
								+ // 楼号
								" CUS_DY,"
								+ // 单元
								" CUS_FLOOR ,"
								+ // 楼层
								" CUS_ROOM ,"
								+ // 房号
								" OLD_ADDRESS ,"
								+ // 用户档案地址
								" SAVE_PEOPLE ,"
								//安检状态
								+ "CONDITION,"
								+ // 安检计划ID
								"CHECKPLAN_ID" + ") " + "VALUES (?, "
								+ "?, ?, ?, ?, ?, ?,?,  ?, ?, ?, ?, ?, ?)",
								new Object[] { aId, CARD_ID, // 卡号
										USER_NAME, // 用户名称
										TELPHONE, // 电话
										ROAD, // 街道
										UNIT_NAME, // 小区
										CUS_DOM, // 楼号
										CUS_DY, // 单元
										CUS_FLOOR, // 楼层
										CUS_ROOM, // 房号
										OLD_ADDRESS, // 用户档案地址
										SAVE_PEOPLE, // 安检员
										"0",   //安检状态
										id // 安检计划ID
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

	// 获取某个计划后的处理过程
	private final Handler jobHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			//完成
			if(msg.what == 2)
			{
				State.set("已下载");
				model.clearPriorState(JobRowModel.this.ID);
				model.mContext.isBusy = false;
			}
			else if(msg.what == 1)
			{
				State.set(msg.obj.toString());
			}
			//进行中
			else if(msg.what==0)
			{
				State.set(msg.obj.toString() + "字节");
			}
			else
			{
				Toast.makeText(model.mContext, "下载出错！", Toast.LENGTH_SHORT).show();
				State.set("出错");
				model.mContext.isBusy = false;
			}
		}

	};
}
