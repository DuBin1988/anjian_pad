package com.aofeng.safecheck.modelview;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.activity.IndoorInspectActivity;
import com.aofeng.safecheck.activity.SetupActivity;
import com.aofeng.utils.RemoteReader;
import com.aofeng.utils.RemoteReaderListener;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

import gueei.binding.Command;
import gueei.binding.converters.FALSE;
import gueei.binding.observables.StringObservable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SetupModel {
	private SetupActivity mContext;

	public SetupModel(SetupActivity context) {
		this.mContext = context;
		UseName.set(Util.getSharedPreference(mContext, Vault.USER_NAME));
	}

	//ִ�в�����������
	public Command UpdateParam = new Command(){
		public void Invoke(View view, Object... args) {
			if(mContext.isBusy)
			{
				Toast.makeText(mContext, "��ȴ��ϴβ�����ɡ�", Toast.LENGTH_SHORT).show();
				return;
			}	
			mContext.isBusy = true;
			if (!(Util.fileExists(mContext.getDatabasePath("safecheck.db")))) 
				GetRepairManList(true);
			else
				GetRepairManList(false);
		}
	};
	
	//ִ��ϵͳ��ʼ������
	public Command Init = new Command(){

		public void Invoke(View view, Object... args) {
			Dialog alertDialog = new AlertDialog.Builder(mContext).   
					setMessage("ȷ��Ҫ��ʼ���𣿳�ʼ����������ݣ�").
					setTitle("ȷ��").   
					setIcon(android.R.drawable.ic_dialog_info).
					setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(mContext.isBusy)
							{
								Toast.makeText(mContext, "��ȴ��ϴβ�����ɡ�", Toast.LENGTH_SHORT).show();
								return;
							}	
							mContext.isBusy = true;
							GetRepairManList(true);
						}
					}).setNegativeButton("ȡ��", null).
					create();  
			alertDialog.setCancelable(false);
			alertDialog.show();
		}
	};

	private void GetRepairManList(final boolean toCreateDB) {
		RemoteReader reader = new RemoteReader(Vault.DB_URL + "sql/",
				"select ID, NAME, ENAME from t_user where charindex((select id from t_role where NAME='ά����Ա'),roles,1)>0");
		reader.setRemoteReaderListener(new RemoteReaderListener() {

			@Override
			public void onSuccess(List<Map<String, Object>> map) {
				super.onSuccess(map);
				ArrayList<RepairMan> RepairManList =new ArrayList<RepairMan>(); 
				for(int i=0; i<map.size(); i++)
				{
					Map<String, Object> aMap = map.get(i);
					RepairMan rm = new RepairMan();
					rm.name = (String)aMap.get("col1");
					rm.id = (String)aMap.get("col0");
					RepairManList.add(rm);
				}
				if(toCreateDB)
					CreateDatabase(RepairManList);
				else
					UpdateParam(RepairManList);
				mContext.isBusy = false;				
			}

			@Override
			public void onFailure(String errMsg) {
				super.onFailure(errMsg);
				Toast.makeText(mContext, "��ȡά����Աʧ�ܣ������������ӡ�", Toast.LENGTH_SHORT).show();
				mContext.isBusy = false;
			}

		});
		reader.start();
	}

	private void CreateDatabase(ArrayList<RepairMan> RepairManList) {
			try {
				//�������ݿ�
				SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
				db.execSQL("DROP TABLE IF EXISTS t_version");
				String   sql = "CREATE TABLE t_version (" +
						"id VARCHAR PRIMARY KEY, " +
						"ver integer )";
				db.execSQL(sql);
				sql = "insert into t_version values('1', " + Util.getVersionCode(mContext) + ")";
				db.execSQL(sql);
				//�����ƻ���
				db.execSQL("DROP TABLE IF EXISTS t_checkplan");
				sql = "CREATE TABLE t_checkplan (" +
						"id VARCHAR PRIMARY KEY, " +
						"f_date varchar," +
						"f_name VARCHAR)";
				db.execSQL(sql);
				//�������쵥
				db.execSQL("DROP TABLE IF EXISTS T_IC_SAFECHECK_PAPAER");
				sql = "CREATE TABLE T_IC_SAFECHECK_PAPAER (" +
						"id VARCHAR(80) PRIMARY KEY, " +
						" CONDITION            varchar(80)                    null," +             //������
						"HasNotified				varchar(80)						null,"+				//���ò�����
						" USER_NAME            varchar(80)                    null," +             	 	//�û�����
						" TELPHONE             varchar(60)                   	 null," +          		//�绰
						" SIGNTELEPHONE             varchar(60)                   	 null," +          		//ǩ���˵绰
						"ARRIVAL_TIME       varchar(80)                    	 null,"+			       //����ʱ��
						"DEPARTURE_TIME   varchar(80)             	    null,"+		   		   //�뿪ʱ��
						" ROAD                 varchar(80)                    		null," +                  	//�ֵ�
						" UNIT_NAME            varchar(80)                    null," +          			//С��
						" CUS_DOM              varchar(20)                    null," +           			//¥��
						" CUS_DY               varchar(20)                 		   null," +               			//��Ԫ
						" CUS_FLOOR            varchar(20)                    null," +        		//¥��
						" CUS_ROOM             varchar(20)                    null," +       		//����
						" OLD_ADDRESS          varchar(500)                 null," +  			//�û�������ַ

					  " ROOM_STRUCTURE       varchar(80)                    null," +   	//���ݽṹ
					  " WARM                 varchar(80)                    null," +                		 //��ů��ʽ
					  " SAVE_PEOPLE          varchar(20)                    null," +     			//����Ա

					  " IC_METER_NAME        varchar(20)                    null," +        //IC��������
					  " JB_METER_NAME        varchar(20)                    null," +       //����������
					  "METER_TYPE                varchar(80)                      null,"+			//����
					  "  CARD_ID            varchar(80)                    null," +             	 	//����
					  " JB_NUMBER            integer                        null," +          		//������
					  " SURPLUS_GAS          integer                        null," +              //ʣ������
					  " gas_quantity         integer                        null," +              //������
					  " buy_gas_quantity     integer                        null," +              //�ۼƹ�����
					  " RQB_AROUND           varchar(80)                    null," +		//ȼ�������ұ�
					  " RQB_JBCODE           varchar(80)                    null," +			//ȼ��������
					  "METERMADEYEAR   varchar(80)              null,"	+			//ȼ�����������
					  " RQB                  varchar(80)                    null," +						//	ȼ����

					  " STANDPIPE            varchar(80)                    null," +				//����
					  " RIGIDITY             varchar(80)                    null," +					//�����Բ���
					  " STATIC               varchar(80)                    null," +					//��ֹѹ��
					  " STATIC_DATA          varchar(80)                    null," +			//��ֹѹ��ֵ
					  " TABLE_TAP            varchar(80)                    null," +				//��ǰ��
					  " COOK_TAP             varchar(80)                    null," +				//��ǰ��
					  " CLOSE_TAP            varchar(80)                    null," +				//�Աշ�
					  " INDOOR               varchar(80)                    null," +				//���ڹ�
					  "LEAKAGE_COOKER                       varchar(80)                   null,"+					//���©��
					  "LEAKAGE_HEATER                       varchar(80)                   null,"+					//��ˮ��©��
					  "LEAKAGE_BOILER                      varchar(80)                   null,"+					//�ڹ�¯©��
					  "LEAKAGE_NOTIFIED                     varchar(80)                   null,"+					//�����֪��
					  "LEAKGEPLACE    varchar(80)						null,"+					//©��λ��

					  " COOK_BRAND           varchar(80)                    null," +			//���Ʒ��
					  "COOK_TYPE        			varchar(80)					null,"	+			//����ͺ�
					  "COOKPIPE_NORMAL               	varchar(80)                 null," 	+ 			//������
					  "COOKERPIPECLAMPCOUNT             varchar(80)  					null, "	+			//��װ�ܿ�����
					  "COOKERPIPYLENGTH				varchar(80)					null,"  +			//������ܳ���
					  "COOK_DATE            varchar(80)                    null," +			//��߹�������

					  "WATER_BRAND          varchar(80)                    null," +		//��ˮ��Ʒ��
					  "WATER_TYPE          varchar(80)                    null,"+				//��ˮ���ͺ�
					  "WATER_PIPE          varchar(80)                    null,"+	 			//��ˮ�����
					  "WATER_FLUE          varchar(80)                    null,"+				//��ˮ���̵�
					  "WATER_NUME          varchar(80)                    null,"+				//�����ܿ���
					  "WATER_DATE           varchar(80)                    null," +			//��ˮ����������
					  "WATER_HIDDEN         varchar(80)                    null," +		//��ˮ������

					  " WHE_BRAND            varchar(80)                    null," +			//	�ڹ�¯Ʒ��
					  " WHE_TYPE            varchar(80)                    null," +	  		//�ڹ�¯�ͺ�
					  " WHE_DATE             varchar(80)                    null," +			//�ڹ�¯��������
					  " WHE_HIDDEN         varchar(80)                    null," +		//�ڹ�¥����

 					 " USER_SUGGESTION             varchar(80)         null," +			//�ͻ����
 					" Remark             varchar(100)         null," +			//��ע
 					 " USER_SATISFIED             varchar(80)                 null,"	+			//�ͻ������
 					 " USER_SIGN             varchar(80)                    		null," +			//�ͻ�ǩ��

					  "THREAT            	  varchar(80)                    					null,"	+			//����
					  "PHOTO_FIRST           	  varchar(80)                    		null,"	+	  			//��Ƭ1
					  "PHOTO_SECOND           	  varchar(80)                    	null,"	+			//��Ƭ2
					  "PHOTO_THIRD           	  varchar(80)                    		null,"	+				//��Ƭ3
					  "PHOTO_FOUTH           	  varchar(80)                    	null,"	+			//��Ƭ4
					  "PHOTO_FIFTH        	  varchar(80)                    		null,"	+	 			//��Ƭ5
					  "PHOTO_SIXTH        	  varchar(80)                    		null,"	+	 			//��Ƭ6
					  "PHOTO_SEVENTH        	  varchar(80)                    		null,"	+	 			//��Ƭ7
					  "NEEDS_REPAIR        	  varchar(80)                    		null,"	+	 			//�Ƿ���Ҫά��
					  "REPAIRMAN        	  varchar(80)                    		null,"	+	 			//ά����
					  "REPAIRMAN_ID        	  varchar(80)                    		null,"	+	 			//ά����ID
					  "REPAIR_STATE	     varchar(80)                  null," +              //ά��״̬
					  "f_userid             varchar(80)                    null," + //�û����
					  "CHECKPLAN_ID VARCHAR(80) null)";                                    //����ƻ�ID
				db.execSQL(sql);
				//�뻧�����
				db.execSQL("DROP TABLE IF EXISTS T_INSPECTION");
				sql = "create table T_INSPECTION as select * from T_IC_SAFECHECK_PAPAER ";
				db.execSQL(sql);
				//���Ӱ��쵥ID
				sql = "alter table T_INSPECTION add CHECKPAPER_ID varchar(80)";
				db.execSQL(sql);
				//����ά������
				sql = "alter table T_INSPECTION add REPAIR_DATE varchar(80)";
				db.execSQL(sql);
				//������
				db.execSQL("DROP TABLE IF EXISTS T_IC_SAFECHECK_HIDDEN");
				sql = "create table T_IC_SAFECHECK_HIDDEN (" +
						"id VARCHAR(80) not null," +
						"EQUIPMENT            varchar(80)                    not null,"+     //�����豸
						"CONTENT              varchar(80)                    not null,"+        //��������
						"SERVICE_PEOPLE       varchar(80)                    null,"+      //ά����Ա
						"SERVICE_DATE         varchar(80)                    null,"+		  //ά������
						"SERVICE_RESULT       varchar(80)                    null,"+      //ά�޽��
						"SERVICE_TXT          varchar(80)                    null,"+          //ά��˵��
						"CHECK_CARD_ID        CHAR(10)                       null,"+     //��鿨��
						"CARD_ID              CHAR(10)                       null,"+              //����
						"USER_NAME            varchar(80)                    null,"+        //�û���
						"REGION_NAME          varchar(60)                    null,"+    //�������
						"ROAD                 varchar(80)                    null,"+           		//�ֵ�
						"UNIT_NAME            varchar(80)                    null,"+        //
						"CUS_DOM              varchar(20)                    null,"+
						"CUS_DY               varchar(20)                    null,"+
						"CUS_FLOOR            varchar(20)                    null,"+
						"CUS_ROOM             varchar(20)                    null,"+
						"TELPHONE             varchar(60)                    null,"+
						"SAVE_PEOPLE          varchar(20)                    null,"+        //������Ա
						"SAVE_DATE            varchar(80)                    null,"+			//��������
						"OP_SPOT              varchar(20)                    null,"+           //�����ص�
						"IC_METER_NAME        varchar(20)                    null,"+   //IC��������
						"JB_METER_NAME        varchar(20)                    null,"+   //����������
						"JB_NUMBER            integer                        null,"+              //������
						"SURPLUS_GAS          integer                        null,"+           //����ʣ������
						"BZ                   varchar(80)                    null," +
						" PRIMARY KEY  (id, EQUIPMENT, CONTENT))";
				db.execSQL(sql);
				
				//�û�������Ϣ��
				db.execSQL("DROP TABLE IF EXISTS T_USERFILES");
				sql = "create table T_USERFILES (" +
						"id VARCHAR(80) not null," +
						"f_username           varchar(80)                    null," + //�û�����
						"f_phone              varchar(80)                    null," + //�û��绰
						"f_address            varchar(80)                    null," + //������ַ
						"f_cardid             varchar(80)                    null," + //����
						"f_meternumber        varchar(80)                    null," + //���
						"f_aroundmeter        varchar(80)                    null," + //���ұ�
						"f_jbfactory          varchar(80)                    null," + //������
						"f_road               varchar(80)                    null," + //�ֵ�
						"f_districtname       varchar(80)                    null," + //С��
						"f_cusDom             varchar(80)                    null," + //¥��
						"f_cusDy              varchar(80)                    null," + //��Ԫ
						"f_cusFloor           varchar(80)                    null," + //¥��
						"f_apartment          varchar(80)                    null," + //����
						"tsum                 varchar(80)                    null," + //�ܹ�����
						"tcount               varchar(80)                    null," + //��������
						"f_userid             varchar(80)                    null," + //�û����
						"f_userstate          varchar(80)                    null," + //�û�״̬
						" PRIMARY KEY  (id))";
				db.execSQL(sql);

				//������Ϣ��
				db.execSQL("DROP TABLE IF EXISTS T_SAFECHECK");
				sql = "create table T_SAFECHECK (" +
						"id VARCHAR(80) not null," +
						"COOK_BRAND           varchar(80)                    null," + //���Ʒ��
						"COOK_TYPE            varchar(80)                    null," + //����ͺ�
						"COOK_DATE            varchar(80)                    null," + //��߹�������
						"WATER_BRAND          varchar(80)                    null," + //��ˮ��Ʒ��
						"WATER_TYPE           varchar(80)                    null," + //��ˮ���ͺ�
						"WATER_FLUE           varchar(80)                    null," + //��ˮ���̵�
						"WATER_DATE           varchar(80)                    null," + //��ˮ����������
						"WHE_BRAND            varchar(80)                    null," + //�ڹ�¯Ʒ��
						"WHE_TYPE             varchar(80)                    null," + //�ڹ�¯�ͺ� 
						"WHE_DATE             varchar(80)                    null," + //�ڹ�¯���� 
						"IC_METER_NAME        varchar(80)                    null," + //IC��������
						"JB_METER_NAME        varchar(80)                    null," + //����������
						"METER_TYPE           varchar(80)                    null," + //���� 
						"gas_quantity         varchar(80)                    null," + //������
						"f_road               varchar(80)                    null," + //�ֵ�
						"f_districtname       varchar(80)                    null," + //С��
						"f_cusDom             varchar(80)                    null," + //¥��
						"f_cusDy              varchar(80)                    null," + //��Ԫ
						"f_cusFloor           varchar(80)                    null," + //¥��
						"f_apartment          varchar(80)                    null," + //����
						" PRIMARY KEY  (id))";
				db.execSQL(sql);
				
				//ά�ް��쵥��
				db.execSQL("DROP TABLE IF EXISTS T_REPAIR_TASK");
				sql = "create table T_REPAIR_TASK as select * from T_INSPECTION ";
				db.execSQL(sql);
				//ά������
				db.execSQL("DROP TABLE IF EXISTS T_REPAIR_ITEM");
				sql = "create table T_REPAIR_ITEM as select * from T_IC_SAFECHECK_HIDDEN ";
				db.execSQL(sql);
				
				//���氲����ʱ��
				db.execSQL("DROP TABLE IF EXISTS T_INP");
				sql = "create table T_INP as select * from T_INSPECTION ";
				db.execSQL(sql);
				//���氲����ʱ��
				db.execSQL("DROP TABLE IF EXISTS T_INP_LINE");
				sql = "create table T_INP_LINE as select * from T_IC_SAFECHECK_HIDDEN ";
				db.execSQL(sql);
				
				//����ά�޲���
				db.execSQL("DROP TABLE IF EXISTS T_PARAMS");
				sql = "create table T_PARAMS (" +
						"ID                  varchar(80)                      null,"+  //�������
						"NAME             varchar(80)                      null,"+  //��������
						"CODE           varchar(80)                      null,"+  //��������
						"PRIMARY KEY  (ID, CODE))";
				db.execSQL(sql);
				for(RepairMan rm : RepairManList)
					db.execSQL("INSERT INTO T_PARAMS(ID, CODE, NAME) VALUES(?,?,?)", new String[]{"ά����Ա", rm.id, rm.name});
				
				//ά�޽������ά��ѡ��ŵ��˱�
				db.execSQL("DROP TABLE IF EXISTS T_REPAIR_RESULT");
				sql = "create table T_REPAIR_RESULT (" +
						"ID                  varchar(80)                      null,"+  //������
						"CONTENT             varchar(200)                      null,"+  //ά������
						"PRIMARY KEY  (ID, CONTENT))";
				db.execSQL(sql);
				//ά�޽����ʱ��
				db.execSQL("DROP TABLE IF EXISTS T_REPAIR_RESULT2");
				sql = "create table T_REPAIR_RESULT2 (" +
						"ID                  varchar(80)                      null,"+  //������
						"CONTENT             varchar(200)                      null,"+  //ά������
						"PRIMARY KEY  (ID, CONTENT))";
				db.execSQL(sql);
				db.close();
				
				//��ʾ�����ɹ�
				Toast toast = Toast.makeText(mContext, "��ʼ����ɡ�", Toast.LENGTH_SHORT);
				toast.show();
			} catch(Exception e) {
				Toast.makeText(mContext, "��ʼ��ʧ�ܣ�", Toast.LENGTH_SHORT).show();
			}
		}

	private void UpdateParam(ArrayList<RepairMan> RepairManList) {
		try {
			//�������ݿ�
			SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);		
			//����ά�޲���
			db.execSQL("DROP TABLE IF EXISTS T_PARAMS");
			String sql = "create table T_PARAMS (" +
					"ID                  varchar(80)                      null,"+  //�������
					"NAME             varchar(80)                      null,"+  //��������
					"CODE           varchar(80)                      null,"+  //��������
					"PRIMARY KEY  (ID, CODE))";
			db.execSQL(sql);
			for(RepairMan rm : RepairManList)
				db.execSQL("INSERT INTO T_PARAMS(ID, CODE, NAME) VALUES(?,?,?)", new String[]{"ά����Ա", rm.id, rm.name});
			db.close();
			
			//��ʾ�����ɹ�
			Toast toast = Toast.makeText(mContext, "������ȡ��ɡ�", Toast.LENGTH_SHORT);
			toast.show();
		} catch(Exception e) {
			Toast.makeText(mContext, "������ȡʧ�ܣ�", Toast.LENGTH_SHORT).show();
		}
	}
	
	//�û�����
	public StringObservable UseName = new StringObservable("");

	// ������
	public StringObservable OldPassword = new StringObservable("");
	// ������
	public StringObservable NewPassword = new StringObservable("");
	// �ٴ�����������
	public StringObservable NewPasswordAgain = new StringObservable("");

	public Command ChangePassword = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			//������֤
			if (CheckPassword()) {
				//���÷���
				Thread th = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							HttpPost httpPost = new HttpPost(Vault.AUTH_URL + "entity");
							StringEntity entity = new StringEntity("[{data:{id:'" + Util.getSharedPreference(mContext, Vault.USER_ID) + "',password:'" + NewPassword.get() + "'}}]" );
							httpPost.setEntity(entity);
							
							HttpClient httpClient = new DefaultHttpClient();
							HttpContext httpContext = new BasicHttpContext();
							HttpResponse response = httpClient.execute(httpPost, httpContext);
							int code = response.getStatusLine().getStatusCode();

							// �����������
							if (code == 200) {
								String strResult = EntityUtils.toString(response
										.getEntity());
								Message msg = new Message();
								msg.obj = strResult;
								msg.what = 1;
								mHandler.sendMessage(msg);
							}
							else 
							{
								Message msg = new Message();
								msg.what = 2;
								mHandler.sendMessage(msg);
							}
						}
						catch(Exception e)
						{
							Message msg = new Message();
							msg.what = 0;
							mHandler.sendMessage(msg);
						}
					}
				});
				th.start();
			}
		}
	};

	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (1 == msg.what)
			{
				Toast.makeText(mContext, "�����޸ĳɹ���", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(mContext, "�����޸�ʧ�ܣ�", Toast.LENGTH_SHORT).show();
			}
		}
	};

	private boolean CheckPassword() {
		if ((OldPassword.get()).equals(Util.getSharedPreference(mContext,Vault.PASSWORD))) {
			if ((NewPassword).get().equals(NewPasswordAgain.get()) && (!(NewPassword.get().equals("")))) {
				return true;
			} else {
				Toast.makeText(mContext, "�������������������䣡", Toast.LENGTH_SHORT).show();
				return false; 
			}
		} else {
			Toast.makeText(mContext, "ԭ�����������", Toast.LENGTH_SHORT).show();
			return false;
		}
	}
}