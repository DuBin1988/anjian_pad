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

	//执行参数更新命令
	public Command UpdateParam = new Command(){
		public void Invoke(View view, Object... args) {
			if(mContext.isBusy)
			{
				Toast.makeText(mContext, "请等待上次操作完成。", Toast.LENGTH_SHORT).show();
				return;
			}	
			mContext.isBusy = true;
			if (!(Util.fileExists(mContext.getDatabasePath("safecheck.db")))) 
				GetRepairManList(true);
			else
				GetRepairManList(false);
		}
	};
	
	//执行系统初始化命令
	public Command Init = new Command(){

		public void Invoke(View view, Object... args) {
			Dialog alertDialog = new AlertDialog.Builder(mContext).   
					setMessage("确认要初始化吗？初始化会清除数据！").
					setTitle("确认").   
					setIcon(android.R.drawable.ic_dialog_info).
					setPositiveButton("确定", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(mContext.isBusy)
							{
								Toast.makeText(mContext, "请等待上次操作完成。", Toast.LENGTH_SHORT).show();
								return;
							}	
							mContext.isBusy = true;
							GetRepairManList(true);
						}
					}).setNegativeButton("取消", null).
					create();  
			alertDialog.setCancelable(false);
			alertDialog.show();
		}
	};

	private void GetRepairManList(final boolean toCreateDB) {
		RemoteReader reader = new RemoteReader(Vault.DB_URL + "sql/",
				"select ID, NAME, ENAME from t_user where charindex((select id from t_role where NAME='维修人员'),roles,1)>0");
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
				Toast.makeText(mContext, "获取维修人员失败，请检查网络连接。", Toast.LENGTH_SHORT).show();
				mContext.isBusy = false;
			}

		});
		reader.start();
	}

	private void CreateDatabase(ArrayList<RepairMan> RepairManList) {
			try {
				//建立数据库
				SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
				db.execSQL("DROP TABLE IF EXISTS t_version");
				String   sql = "CREATE TABLE t_version (" +
						"id VARCHAR PRIMARY KEY, " +
						"ver integer )";
				db.execSQL(sql);
				sql = "insert into t_version values('1', " + Util.getVersionCode(mContext) + ")";
				db.execSQL(sql);
				//创建计划表
				db.execSQL("DROP TABLE IF EXISTS t_checkplan");
				sql = "CREATE TABLE t_checkplan (" +
						"id VARCHAR PRIMARY KEY, " +
						"f_date varchar," +
						"f_name VARCHAR)";
				db.execSQL(sql);
				//创建安检单
				db.execSQL("DROP TABLE IF EXISTS T_IC_SAFECHECK_PAPAER");
				sql = "CREATE TABLE T_IC_SAFECHECK_PAPAER (" +
						"id VARCHAR(80) PRIMARY KEY, " +
						" CONDITION            varchar(80)                    null," +             //检查情况
						"HasNotified				varchar(80)						null,"+				//到访不遇卡
						" USER_NAME            varchar(80)                    null," +             	 	//用户名称
						" TELPHONE             varchar(60)                   	 null," +          		//电话
						" SIGNTELEPHONE             varchar(60)                   	 null," +          		//签名人电话
						"ARRIVAL_TIME       varchar(80)                    	 null,"+			       //到达时间
						"DEPARTURE_TIME   varchar(80)             	    null,"+		   		   //离开时间
						" ROAD                 varchar(80)                    		null," +                  	//街道
						" UNIT_NAME            varchar(80)                    null," +          			//小区
						" CUS_DOM              varchar(20)                    null," +           			//楼号
						" CUS_DY               varchar(20)                 		   null," +               			//单元
						" CUS_FLOOR            varchar(20)                    null," +        		//楼层
						" CUS_ROOM             varchar(20)                    null," +       		//房号
						" OLD_ADDRESS          varchar(500)                 null," +  			//用户档案地址

					  " ROOM_STRUCTURE       varchar(80)                    null," +   	//房屋结构
					  " WARM                 varchar(80)                    null," +                		 //供暖方式
					  " SAVE_PEOPLE          varchar(20)                    null," +     			//安检员

					  " IC_METER_NAME        varchar(20)                    null," +        //IC卡表厂名称
					  " JB_METER_NAME        varchar(20)                    null," +       //基表厂家名称
					  "METER_TYPE                varchar(80)                      null,"+			//表型
					  "  CARD_ID            varchar(80)                    null," +             	 	//卡号
					  " JB_NUMBER            integer                        null," +          		//基表数
					  " SURPLUS_GAS          integer                        null," +              //剩余气量
					  " gas_quantity         integer                        null," +              //用气量
					  " buy_gas_quantity     integer                        null," +              //累计购气量
					  " RQB_AROUND           varchar(80)                    null," +		//燃气表左右表
					  " RQB_JBCODE           varchar(80)                    null," +			//燃气表基表号
					  "METERMADEYEAR   varchar(80)              null,"	+			//燃气表生产年份
					  " RQB                  varchar(80)                    null," +						//	燃气表

					  " STANDPIPE            varchar(80)                    null," +				//立管
					  " RIGIDITY             varchar(80)                    null," +					//严密性测试
					  " STATIC               varchar(80)                    null," +					//静止压力
					  " STATIC_DATA          varchar(80)                    null," +			//静止压力值
					  " TABLE_TAP            varchar(80)                    null," +				//表前阀
					  " COOK_TAP             varchar(80)                    null," +				//灶前阀
					  " CLOSE_TAP            varchar(80)                    null," +				//自闭阀
					  " INDOOR               varchar(80)                    null," +				//户内管
					  "LEAKAGE_COOKER                       varchar(80)                   null,"+					//灶具漏气
					  "LEAKAGE_HEATER                       varchar(80)                   null,"+					//热水器漏气
					  "LEAKAGE_BOILER                      varchar(80)                   null,"+					//壁挂炉漏气
					  "LEAKAGE_NOTIFIED                     varchar(80)                   null,"+					//安检告知书
					  "LEAKGEPLACE    varchar(80)						null,"+					//漏气位置

					  " COOK_BRAND           varchar(80)                    null," +			//灶具品牌
					  "COOK_TYPE        			varchar(80)					null,"	+			//灶具型号
					  "COOKPIPE_NORMAL               	varchar(80)                 null," 	+ 			//灶具软管
					  "COOKERPIPECLAMPCOUNT             varchar(80)  					null, "	+			//安装管卡个数
					  "COOKERPIPYLENGTH				varchar(80)					null,"  +			//更换软管长度
					  "COOK_DATE            varchar(80)                    null," +			//灶具购置日期

					  "WATER_BRAND          varchar(80)                    null," +		//热水器品牌
					  "WATER_TYPE          varchar(80)                    null,"+				//热水器型号
					  "WATER_PIPE          varchar(80)                    null,"+	 			//热水器软管
					  "WATER_FLUE          varchar(80)                    null,"+				//热水器烟道
					  "WATER_NUME          varchar(80)                    null,"+				//更换管卡数
					  "WATER_DATE           varchar(80)                    null," +			//热水器购置日期
					  "WATER_HIDDEN         varchar(80)                    null," +		//热水器隐患

					  " WHE_BRAND            varchar(80)                    null," +			//	壁挂炉品牌
					  " WHE_TYPE            varchar(80)                    null," +	  		//壁挂炉型号
					  " WHE_DATE             varchar(80)                    null," +			//壁挂炉购置日期
					  " WHE_HIDDEN         varchar(80)                    null," +		//壁挂楼隐患

 					 " USER_SUGGESTION             varchar(80)         null," +			//客户意见
 					" Remark             varchar(100)         null," +			//备注
 					 " USER_SATISFIED             varchar(80)                 null,"	+			//客户满意度
 					 " USER_SIGN             varchar(80)                    		null," +			//客户签名

					  "THREAT            	  varchar(80)                    					null,"	+			//隐患
					  "PHOTO_FIRST           	  varchar(80)                    		null,"	+	  			//照片1
					  "PHOTO_SECOND           	  varchar(80)                    	null,"	+			//照片2
					  "PHOTO_THIRD           	  varchar(80)                    		null,"	+				//照片3
					  "PHOTO_FOUTH           	  varchar(80)                    	null,"	+			//照片4
					  "PHOTO_FIFTH        	  varchar(80)                    		null,"	+	 			//照片5
					  "PHOTO_SIXTH        	  varchar(80)                    		null,"	+	 			//照片6
					  "PHOTO_SEVENTH        	  varchar(80)                    		null,"	+	 			//照片7
					  "NEEDS_REPAIR        	  varchar(80)                    		null,"	+	 			//是否需要维修
					  "REPAIRMAN        	  varchar(80)                    		null,"	+	 			//维修人
					  "REPAIRMAN_ID        	  varchar(80)                    		null,"	+	 			//维修人ID
					  "REPAIR_STATE	     varchar(80)                  null," +              //维修状态
					  "f_userid             varchar(80)                    null," + //用户编号
					  "CHECKPLAN_ID VARCHAR(80) null)";                                    //安检计划ID
				db.execSQL(sql);
				//入户安检表
				db.execSQL("DROP TABLE IF EXISTS T_INSPECTION");
				sql = "create table T_INSPECTION as select * from T_IC_SAFECHECK_PAPAER ";
				db.execSQL(sql);
				//增加安检单ID
				sql = "alter table T_INSPECTION add CHECKPAPER_ID varchar(80)";
				db.execSQL(sql);
				//增加维修日期
				sql = "alter table T_INSPECTION add REPAIR_DATE varchar(80)";
				db.execSQL(sql);
				//隐患表
				db.execSQL("DROP TABLE IF EXISTS T_IC_SAFECHECK_HIDDEN");
				sql = "create table T_IC_SAFECHECK_HIDDEN (" +
						"id VARCHAR(80) not null," +
						"EQUIPMENT            varchar(80)                    not null,"+     //故障设备
						"CONTENT              varchar(80)                    not null,"+        //故障内容
						"SERVICE_PEOPLE       varchar(80)                    null,"+      //维修人员
						"SERVICE_DATE         varchar(80)                    null,"+		  //维修日期
						"SERVICE_RESULT       varchar(80)                    null,"+      //维修结果
						"SERVICE_TXT          varchar(80)                    null,"+          //维修说明
						"CHECK_CARD_ID        CHAR(10)                       null,"+     //检查卡号
						"CARD_ID              CHAR(10)                       null,"+              //卡号
						"USER_NAME            varchar(80)                    null,"+        //用户名
						"REGION_NAME          varchar(60)                    null,"+    //网格编码
						"ROAD                 varchar(80)                    null,"+           		//街道
						"UNIT_NAME            varchar(80)                    null,"+        //
						"CUS_DOM              varchar(20)                    null,"+
						"CUS_DY               varchar(20)                    null,"+
						"CUS_FLOOR            varchar(20)                    null,"+
						"CUS_ROOM             varchar(20)                    null,"+
						"TELPHONE             varchar(60)                    null,"+
						"SAVE_PEOPLE          varchar(20)                    null,"+        //安检人员
						"SAVE_DATE            varchar(80)                    null,"+			//安检日期
						"OP_SPOT              varchar(20)                    null,"+           //操作地点
						"IC_METER_NAME        varchar(20)                    null,"+   //IC卡表厂名称
						"JB_METER_NAME        varchar(20)                    null,"+   //基表厂家名称
						"JB_NUMBER            integer                        null,"+              //基表数
						"SURPLUS_GAS          integer                        null,"+           //表内剩余气量
						"BZ                   varchar(80)                    null," +
						" PRIMARY KEY  (id, EQUIPMENT, CONTENT))";
				db.execSQL(sql);
				
				//用户档案信息表
				db.execSQL("DROP TABLE IF EXISTS T_USERFILES");
				sql = "create table T_USERFILES (" +
						"id VARCHAR(80) not null," +
						"f_username           varchar(80)                    null," + //用户姓名
						"f_phone              varchar(80)                    null," + //用户电话
						"f_address            varchar(80)                    null," + //档案地址
						"f_cardid             varchar(80)                    null," + //卡号
						"f_meternumber        varchar(80)                    null," + //表号
						"f_aroundmeter        varchar(80)                    null," + //左右表
						"f_jbfactory          varchar(80)                    null," + //基表厂家
						"f_road               varchar(80)                    null," + //街道
						"f_districtname       varchar(80)                    null," + //小区
						"f_cusDom             varchar(80)                    null," + //楼号
						"f_cusDy              varchar(80)                    null," + //单元
						"f_cusFloor           varchar(80)                    null," + //楼层
						"f_apartment          varchar(80)                    null," + //房号
						"tsum                 varchar(80)                    null," + //总购气量
						"tcount               varchar(80)                    null," + //购气次数
						"f_userid             varchar(80)                    null," + //用户编号
						"f_userstate          varchar(80)                    null," + //用户状态
						" PRIMARY KEY  (id))";
				db.execSQL(sql);

				//安检信息表
				db.execSQL("DROP TABLE IF EXISTS T_SAFECHECK");
				sql = "create table T_SAFECHECK (" +
						"id VARCHAR(80) not null," +
						"COOK_BRAND           varchar(80)                    null," + //灶具品牌
						"COOK_TYPE            varchar(80)                    null," + //灶具型号
						"COOK_DATE            varchar(80)                    null," + //灶具购置日期
						"WATER_BRAND          varchar(80)                    null," + //热水器品牌
						"WATER_TYPE           varchar(80)                    null," + //热水器型号
						"WATER_FLUE           varchar(80)                    null," + //热水器烟道
						"WATER_DATE           varchar(80)                    null," + //热水器购置日期
						"WHE_BRAND            varchar(80)                    null," + //壁挂炉品牌
						"WHE_TYPE             varchar(80)                    null," + //壁挂炉型号 
						"WHE_DATE             varchar(80)                    null," + //壁挂炉日期 
						"IC_METER_NAME        varchar(80)                    null," + //IC卡表厂名称
						"JB_METER_NAME        varchar(80)                    null," + //基表厂家名称
						"METER_TYPE           varchar(80)                    null," + //表型 
						"gas_quantity         varchar(80)                    null," + //用气量
						"f_road               varchar(80)                    null," + //街道
						"f_districtname       varchar(80)                    null," + //小区
						"f_cusDom             varchar(80)                    null," + //楼号
						"f_cusDy              varchar(80)                    null," + //单元
						"f_cusFloor           varchar(80)                    null," + //楼层
						"f_apartment          varchar(80)                    null," + //房号
						" PRIMARY KEY  (id))";
				db.execSQL(sql);
				
				//维修安检单表
				db.execSQL("DROP TABLE IF EXISTS T_REPAIR_TASK");
				sql = "create table T_REPAIR_TASK as select * from T_INSPECTION ";
				db.execSQL(sql);
				//维修隐患
				db.execSQL("DROP TABLE IF EXISTS T_REPAIR_ITEM");
				sql = "create table T_REPAIR_ITEM as select * from T_IC_SAFECHECK_HIDDEN ";
				db.execSQL(sql);
				
				//保存安检临时表
				db.execSQL("DROP TABLE IF EXISTS T_INP");
				sql = "create table T_INP as select * from T_INSPECTION ";
				db.execSQL(sql);
				//保存安检临时表
				db.execSQL("DROP TABLE IF EXISTS T_INP_LINE");
				sql = "create table T_INP_LINE as select * from T_IC_SAFECHECK_HIDDEN ";
				db.execSQL(sql);
				
				//保存维修参数
				db.execSQL("DROP TABLE IF EXISTS T_PARAMS");
				sql = "create table T_PARAMS (" +
						"ID                  varchar(80)                      null,"+  //参数编号
						"NAME             varchar(80)                      null,"+  //参数名称
						"CODE           varchar(80)                      null,"+  //参数代码
						"PRIMARY KEY  (ID, CODE))";
				db.execSQL(sql);
				for(RepairMan rm : RepairManList)
					db.execSQL("INSERT INTO T_PARAMS(ID, CODE, NAME) VALUES(?,?,?)", new String[]{"维修人员", rm.id, rm.name});
				
				//维修结果，把维修选项放到此表
				db.execSQL("DROP TABLE IF EXISTS T_REPAIR_RESULT");
				sql = "create table T_REPAIR_RESULT (" +
						"ID                  varchar(80)                      null,"+  //安检编号
						"CONTENT             varchar(200)                      null,"+  //维修内容
						"PRIMARY KEY  (ID, CONTENT))";
				db.execSQL(sql);
				//维修结果临时表
				db.execSQL("DROP TABLE IF EXISTS T_REPAIR_RESULT2");
				sql = "create table T_REPAIR_RESULT2 (" +
						"ID                  varchar(80)                      null,"+  //安检编号
						"CONTENT             varchar(200)                      null,"+  //维修内容
						"PRIMARY KEY  (ID, CONTENT))";
				db.execSQL(sql);
				db.close();
				
				//提示创建成功
				Toast toast = Toast.makeText(mContext, "初始化完成。", Toast.LENGTH_SHORT);
				toast.show();
			} catch(Exception e) {
				Toast.makeText(mContext, "初始化失败！", Toast.LENGTH_SHORT).show();
			}
		}

	private void UpdateParam(ArrayList<RepairMan> RepairManList) {
		try {
			//建立数据库
			SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);		
			//保存维修参数
			db.execSQL("DROP TABLE IF EXISTS T_PARAMS");
			String sql = "create table T_PARAMS (" +
					"ID                  varchar(80)                      null,"+  //参数编号
					"NAME             varchar(80)                      null,"+  //参数名称
					"CODE           varchar(80)                      null,"+  //参数代码
					"PRIMARY KEY  (ID, CODE))";
			db.execSQL(sql);
			for(RepairMan rm : RepairManList)
				db.execSQL("INSERT INTO T_PARAMS(ID, CODE, NAME) VALUES(?,?,?)", new String[]{"维修人员", rm.id, rm.name});
			db.close();
			
			//提示创建成功
			Toast toast = Toast.makeText(mContext, "参数提取完成。", Toast.LENGTH_SHORT);
			toast.show();
		} catch(Exception e) {
			Toast.makeText(mContext, "参数提取失败！", Toast.LENGTH_SHORT).show();
		}
	}
	
	//用户姓名
	public StringObservable UseName = new StringObservable("");

	// 旧密码
	public StringObservable OldPassword = new StringObservable("");
	// 新密码
	public StringObservable NewPassword = new StringObservable("");
	// 再次输入新密码
	public StringObservable NewPasswordAgain = new StringObservable("");

	public Command ChangePassword = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			//输入验证
			if (CheckPassword()) {
				//调用服务
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

							// 数据下载完成
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
				Toast.makeText(mContext, "密码修改成功！", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(mContext, "密码修改失败！", Toast.LENGTH_SHORT).show();
			}
		}
	};

	private boolean CheckPassword() {
		if ((OldPassword.get()).equals(Util.getSharedPreference(mContext,Vault.PASSWORD))) {
			if ((NewPassword).get().equals(NewPasswordAgain.get()) && (!(NewPassword.get().equals("")))) {
				return true;
			} else {
				Toast.makeText(mContext, "新密码输入有误，请重输！", Toast.LENGTH_SHORT).show();
				return false; 
			}
		} else {
			Toast.makeText(mContext, "原密码输入错误！", Toast.LENGTH_SHORT).show();
			return false;
		}
	}
}