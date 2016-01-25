package com.aofeng.safecheck.modelview;

import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.aofeng.safecheck.activity.QueryUserInfoActivity;
import com.aofeng.safecheck.activity.ShootActivity;
import com.aofeng.safecheck.model.UserRow;
import com.aofeng.utils.Vault;

import gueei.binding.Command;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.StringObservable;
import android.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

//@SuppressLint("HandlerLeak")
public class QueryUserInfoModel {
	public final QueryUserInfoActivity mContext;
	
	public QueryUserInfoModel(QueryUserInfoActivity Context) {
		this.mContext = Context;
		Bundle bundle = new Bundle();
		bundle = mContext.getIntent().getExtras();
		this.UnitName.set(bundle.getString("UnitName"));
		this.BuildingNo.set(bundle.getString("BuildingNo"));
		this.UnitNo.set(bundle.getString("UnitNo"));
		this.LevelNo.set(bundle.getString("LevelNo"));
		this.RoomNo.set(bundle.getString("RoomNo"));
		this.txtUserName.set(bundle.getString("userName"));
		this.txtTelephone.set(bundle.getString("telephone"));
		this.txtAddress.set(bundle.getString("address"));
	}
	
	public StringObservable UnitName = new StringObservable("");
	public StringObservable BuildingNo = new StringObservable("");
	public StringObservable UnitNo = new StringObservable("");
	public StringObservable LevelNo = new StringObservable("");
	public StringObservable RoomNo = new StringObservable("");
	public StringObservable txtUserName = new StringObservable("");
	public StringObservable txtTelephone = new StringObservable("");
	public StringObservable txtAddress = new StringObservable("");
		
	public ArrayListObservable<UserRow> userList = new ArrayListObservable<UserRow>(UserRow.class);
	
	public Command SearchUserInfo = new Command()
	{

		@Override
		public void Invoke(View arg0, Object... arg1) {
			execute2();
		}
		
	};

	public void execute() {
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					
					if(UnitName.get().length() != 0 && BuildingNo.get().length() != 0 && UnitNo.get().length() != 0 && LevelNo.get().length() != 0 && RoomNo.get().length() != 0)
					{
						//Nothing to do.
					}
					else
					{
						Message msg = new Message();
						msg.what = 3;
						listHandler.sendMessage(msg);
						return;
					}
					
					String AddressForSearch = UnitName.get() + "%" + BuildingNo.get() + "%" + UnitNo.get() + "_" + LevelNo.get() + '_' + RoomNo.get();
					String sql = "SELECT t1.f_username, f_phone, f_address, f_cardid, f_city, f_area, '',f_meternumber 基表号,f_aroundmeter 左右表, f_jbfactory 基表厂家, f_road, f_districtname, f_cusDom, f_cusDy, f_cusFloor, f_apartment, tsum, tcount, t1.f_userid, f_userstate from (select * from t_userfiles WHERE f_address like '%" + AddressForSearch + "%') t1 left join (SELECT f_userid, f_username, SUM (f_pregas) tsum, count(f_pregas) tcount FROM t_sellinggas WHERE f_username like '%%' group by f_userid, f_username) t2 on t1.f_userid = t2.f_userid";
					String url = Vault.DB_URL + "sql/"
							+ URLEncoder
							.encode(sql, "UTF8")
									.replace("+", "%20");
					HttpGet getMethod = new HttpGet(url);
					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse response = httpClient.execute(getMethod);

					int code = response.getStatusLine().getStatusCode();

					// 数据下载完成
					if (code == 200) {
						String strResult = EntityUtils.toString(
								response.getEntity(), "UTF8");
						Message msg = new Message();
						msg.obj = strResult;
						msg.what = 1;
						listHandler.sendMessage(msg);
					} else {
						Message msg = new Message();
						msg.what = 2;
						listHandler.sendMessage(msg);
					}
				}catch (Exception e) {
					Message msg = new Message();
					msg.what = 0;
					listHandler.sendMessage(msg);
				}
			}
		});
		th.start();
	}
	
	// 显示用户信息
	private final Handler listHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (1 == msg.what) {
				super.handleMessage(msg);
				try {
					userList.clear();
					JSONArray array = new JSONArray((String) msg.obj);
					if(array.length() == 0)
					{
						execute1();
						return;
					}
					for(int i=0; i<array.length(); i++)
					{
						JSONObject obj = array.getJSONObject(i);
						UserRow profile = new UserRow();
						profile.model = QueryUserInfoModel.this;
						if (!obj.has("col0")) {
							Toast.makeText(mContext, "无此用户信息！", Toast.LENGTH_SHORT).show();
						} 
						else
						{
							if(obj.has("col0"))
								profile.userName.set(obj.getString("col0"));
							if(obj.has("col1"))
								profile.telephone.set(obj.getString("col1"));
							if(obj.has("col2"))
								profile.address.set(obj.getString("col2"));
							if(obj.has("col3"))
								profile.cardID.set(obj.getString("col3"));
							if(obj.has("col4"))
								profile.city.set(obj.getString("col4"));
							if(obj.has("col5"))
								profile.area.set(obj.getString("col5"));
							if(obj.has("col7"))
								profile.biaohao.set(obj.getString("col7"));
							if(obj.has("col8"))
								profile.zuoyoubiao.set(obj.getString("col8"));
							if(obj.has("col9"))
								profile.biaochang.set(obj.getString("col9"));
							if(obj.has("col10"))
								profile.road.set(obj.getString("col10"));
							if(obj.has("col11"))
								profile.districtname.set(obj.getString("col11"));
							if(obj.has("col12"))
								profile.cusDom.set(obj.getString("col12"));
							if(obj.has("col13"))
								profile.cusDy.set(obj.getString("col13"));
							if(obj.has("col14"))
								profile.cusFloor.set(obj.getString("col14"));
							if(obj.has("col15"))
								profile.apartment.set(obj.getString("col15"));
							if(obj.has("col16"))
								profile.tsum.set(obj.getString("col16"));
							if(obj.has("col17"))
								profile.tcount.set(obj.getString("col17"));
							if(obj.has("col18"))
								profile.userID.set(obj.getString("col18"));
							if(obj.has("col19"))
								profile.UserState.set(obj.getString("col19"));
							String archiveAddress = "";
							for(int i1=10; i1<16; i1++)
								if(obj.has("col" + i1))
									archiveAddress += obj.getString("col"+i1) + "---";
								else
									archiveAddress += "---";
							if(archiveAddress.endsWith("---"))
								archiveAddress = archiveAddress.substring(0, archiveAddress.length()-3);
							profile.f_archiveaddress.set(archiveAddress);
							
							userList.add(profile);
						}
					}
				} catch (Exception e) {
					Toast.makeText(mContext, "联网查询失败,正在查询本地库", Toast.LENGTH_SHORT).show();
					JSONArray jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get() + '_' + RoomNo.get());
					if(null == jsons || 0 == jsons.length())
						jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]" + '_' + RoomNo.get());
					if(null == jsons || 0 == jsons.length())
						jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get());
					if(null == jsons || 0 == jsons.length())
						jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]");
					else if(null == jsons || 0 == jsons.length())
						Toast.makeText(mContext, "请手动查询,若没有找到用户则无此用户", Toast.LENGTH_SHORT).show();
					else
					{
						Message msg1 = new Message();
						msg1.obj = jsons.toString();
						msg1.what = 1;
						listHandler.sendMessage(msg1);
					}
				}
			} else if (0 == msg.what) {
				Toast.makeText(mContext, "联网查询失败,正在查询本地库", Toast.LENGTH_LONG).show();
				JSONArray jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get() + '_' + RoomNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]" + '_' + RoomNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]");
				else if(null == jsons || 0 == jsons.length())
					Toast.makeText(mContext, "请手动查询,若没有找到用户则无此用户", Toast.LENGTH_SHORT).show();
				else
				{
					Message msg1 = new Message();
					msg1.obj = jsons.toString();
					msg1.what = 1;
					listHandler.sendMessage(msg1);
				}
			} else if (2 == msg.what) {
				Toast.makeText(mContext, "正在查询本地库", Toast.LENGTH_LONG).show();
				JSONArray jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get() + '_' + RoomNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]" + '_' + RoomNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]");
				else if(null == jsons || 0 == jsons.length())
					Toast.makeText(mContext, "请手动查询,若没有找到用户则无此用户", Toast.LENGTH_SHORT).show();
				else
				{
					Message msg1 = new Message();
					msg1.obj = jsons.toString();
					msg1.what = 1;
					listHandler.sendMessage(msg1);
				}
			} else if (3 == msg.what) {
				Toast.makeText(mContext, "请手动查询", Toast.LENGTH_LONG).show();
			}
		}
	};	
	
	protected void execute1() {
		Thread th1 = new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					if(UnitName.get().length() != 0 && BuildingNo.get().length() != 0 && UnitNo.get().length() != 0 && LevelNo.get().length() != 0 && RoomNo.get().length() != 0)
					{
						//Nothing to do.
					}
					else
					{
						Message msg = new Message();
						msg.what = 3;
						listHandler1.sendMessage(msg);
						return;
					}
					
					String AddressForSearch1 = UnitName.get() + "%" + BuildingNo.get() + "%" + "[东西南北中" + LevelNo.get() + "]" + '_' + RoomNo.get();

					String sql = "SELECT t1.f_username, f_phone, f_address, f_cardid, f_city, f_area, '',f_meternumber 基表号,f_aroundmeter 左右表, f_jbfactory 基表厂家, f_road, f_districtname, f_cusDom, f_cusDy, f_cusFloor, f_apartment, tsum, tcount, t1.f_userid, f_userstate from (select * from t_userfiles WHERE f_address like '%" + AddressForSearch1 + "%') t1 left join (SELECT f_userid, f_username, SUM (f_pregas) tsum, count(f_pregas) tcount FROM t_sellinggas WHERE f_username like '%%' group by f_userid, f_username) t2 on t1.f_userid = t2.f_userid";
					String url = Vault.DB_URL + "sql/"
							+ URLEncoder
							.encode(sql, "UTF8")
									.replace("+", "%20");
					HttpGet getMethod = new HttpGet(url);
					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse response = httpClient.execute(getMethod);

					int code = response.getStatusLine().getStatusCode();

					// 数据下载完成
					if (code == 200) {
						String strResult = EntityUtils.toString(
								response.getEntity(), "UTF8");
						Message msg = new Message();
						msg.obj = strResult;
						msg.what = 1;
						listHandler1.sendMessage(msg);
					} else {
						Message msg = new Message();
						msg.what = 2;
						listHandler1.sendMessage(msg);
					}
				}catch (Exception e) {
					Message msg = new Message();
					msg.what = 0;
					listHandler1.sendMessage(msg);
				}
			}
		});
		th1.start();
	}
	
	// 显示用户信息
	private final Handler listHandler1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (1 == msg.what) {
				super.handleMessage(msg);
				try {
					userList.clear();
					JSONArray array = new JSONArray((String) msg.obj);
					if(array.length() == 0)
					{
						execute4();
						return;
					}
					for(int i=0; i<array.length(); i++)
					{
						JSONObject obj = array.getJSONObject(i);
						UserRow profile = new UserRow();
						profile.model = QueryUserInfoModel.this;
						if (!obj.has("col0")) {
							// 查不到此IC卡用户
							Toast.makeText(mContext, "无此用户信息！", Toast.LENGTH_SHORT).show();
						} 
						else
						{
							if(obj.has("col0"))
								profile.userName.set(obj.getString("col0"));
							if(obj.has("col1"))
								profile.telephone.set(obj.getString("col1"));
							if(obj.has("col2"))
								profile.address.set(obj.getString("col2"));
							if(obj.has("col3"))
								profile.cardID.set(obj.getString("col3"));
							if(obj.has("col4"))
								profile.city.set(obj.getString("col4"));
							if(obj.has("col5"))
								profile.area.set(obj.getString("col5"));
							if(obj.has("col7"))
								profile.biaohao.set(obj.getString("col7"));
							if(obj.has("col8"))
							{
								profile.zuoyoubiao.set(obj.getString("col8"));
							}
							if(obj.has("col9"))
								profile.biaochang.set(obj.getString("col9"));
							if(obj.has("col10"))
								profile.road.set(obj.getString("col10"));
							if(obj.has("col11"))
								profile.districtname.set(obj.getString("col11"));
							if(obj.has("col12"))
								profile.cusDom.set(obj.getString("col12"));
							if(obj.has("col13"))
								profile.cusDy.set(obj.getString("col13"));
							if(obj.has("col14"))
								profile.cusFloor.set(obj.getString("col14"));
							if(obj.has("col15"))
								profile.apartment.set(obj.getString("col15"));
							if(obj.has("col16"))
								profile.tsum.set(obj.getString("col16"));
							if(obj.has("col17"))
								profile.tcount.set(obj.getString("col17"));
							if(obj.has("col18"))
								profile.userID.set(obj.getString("col18"));
							if(obj.has("col19"))
								profile.UserState.set(obj.getString("col19"));
							String archiveAddress = "";
							for(int i1=10; i1<16; i1++)
								if(obj.has("col" + i1))
									archiveAddress += obj.getString("col"+i1) + "---";
								else
									archiveAddress += "---";
							if(archiveAddress.endsWith("---"))
								archiveAddress = archiveAddress.substring(0, archiveAddress.length()-3);
							profile.f_archiveaddress.set(archiveAddress);
							
							userList.add(profile);
						}
					}
				} catch (Exception e) {
					Toast.makeText(mContext, "联网查询失败,正在查询本地库", Toast.LENGTH_SHORT).show();
					JSONArray jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get() + '_' + RoomNo.get());
					if(null == jsons || 0 == jsons.length())
						jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]" + '_' + RoomNo.get());
					if(null == jsons || 0 == jsons.length())
						jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get());
					if(null == jsons || 0 == jsons.length())
						jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]");
					else if(null == jsons || 0 == jsons.length())
						Toast.makeText(mContext, "请手动查询,若没有找到用户则无此用户", Toast.LENGTH_SHORT).show();
					else
					{
						Message msg1 = new Message();
						msg1.obj = jsons.toString();
						msg1.what = 1;
						listHandler.sendMessage(msg1);
					}
				}
			} else if (0 == msg.what) {
				Toast.makeText(mContext, "联网查询失败,正在查询本地库", Toast.LENGTH_LONG).show();
				JSONArray jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get() + '_' + RoomNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]" + '_' + RoomNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]");
				else if(null == jsons || 0 == jsons.length())
					Toast.makeText(mContext, "请手动查询,若没有找到用户则无此用户", Toast.LENGTH_SHORT).show();
				else
				{
					Message msg1 = new Message();
					msg1.obj = jsons.toString();
					msg1.what = 1;
					listHandler.sendMessage(msg1);
				}
			} else if (2 == msg.what) {
				Toast.makeText(mContext, "正在查询本地库", Toast.LENGTH_LONG).show();
				JSONArray jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get() + '_' + RoomNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]" + '_' + RoomNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]");
				else if(null == jsons || 0 == jsons.length())
					Toast.makeText(mContext, "请手动查询,若没有找到用户则无此用户", Toast.LENGTH_SHORT).show();
				else
				{
					Message msg1 = new Message();
					msg1.obj = jsons.toString();
					msg1.what = 1;
					listHandler.sendMessage(msg1);
				}
			} else if (3 == msg.what) {
				Toast.makeText(mContext, "请手动查询", Toast.LENGTH_LONG).show();
			}
		}
	};	
	
	protected void execute2() {
		Thread th2 = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if(txtUserName.get().length() != 0 || txtTelephone.get().length() >= 6 || txtAddress.get().length() != 0)
					{
						//Nothing to do.
					}
					else
					{
						Message msg = new Message();
						msg.what = 3;
						listHandler2.sendMessage(msg);
						return;
					}
								
					String sql = "SELECT t1.f_username, f_phone, f_address, f_cardid, f_city, f_area, '',f_meternumber 基表号,f_aroundmeter 左右表, f_jbfactory 基表厂家, f_road, f_districtname, f_cusDom, f_cusDy, f_cusFloor, f_apartment, tsum, tcount, t1.f_userid, f_userstate from (select * from t_userfiles WHERE f_username like '%"+ txtUserName.get() +"%' and (f_phone like '%"+ txtTelephone.get() +"%' or f_phone is null) and (f_address like '%"+ txtAddress.get() +"%' or f_address is null)) t1 left join (SELECT f_userid, f_username, SUM (f_pregas) tsum, count(f_pregas) tcount FROM t_sellinggas WHERE f_username like '%"+ txtUserName.get() +"%' group by f_userid, f_username) t2 on t1.f_userid = t2.f_userid";
					String url = Vault.DB_URL + "sql/"
							+ URLEncoder
							.encode(sql, "UTF8")
									.replace("+", "%20");
					HttpGet getMethod = new HttpGet(url);
					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse response = httpClient.execute(getMethod);

					int code = response.getStatusLine().getStatusCode();

					// 数据下载完成
					if (code == 200) {
						String strResult = EntityUtils.toString(
								response.getEntity(), "UTF8");
						Message msg = new Message();
						msg.obj = strResult;
						msg.what = 1;
						listHandler2.sendMessage(msg);
					} else {
						Message msg = new Message();
						msg.what = 2;
						listHandler2.sendMessage(msg);
					}
				}catch (Exception e) {
					Message msg = new Message();
					msg.what = 0;
					listHandler2.sendMessage(msg);
				}
			}
		});
		th2.start();
	}
	
	// 显示用户信息
	private final Handler listHandler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (1 == msg.what) {
				super.handleMessage(msg);
				try {
					userList.clear();
					JSONArray array = new JSONArray((String) msg.obj);
					for(int i=0; i<array.length(); i++)
					{
						JSONObject obj = array.getJSONObject(i);
						UserRow profile = new UserRow();
						profile.model = QueryUserInfoModel.this;
						if (!obj.has("col0")) {
							// 查不到此IC卡用户
							Toast.makeText(mContext, "无此用户信息！", Toast.LENGTH_SHORT).show();
						} 
						else
						{
							if(obj.has("col0"))
								profile.userName.set(obj.getString("col0"));
							if(obj.has("col1"))
								profile.telephone.set(obj.getString("col1"));
							if(obj.has("col2"))
								profile.address.set(obj.getString("col2"));
							if(obj.has("col3"))
								profile.cardID.set(obj.getString("col3"));
							if(obj.has("col4"))
								profile.city.set(obj.getString("col4"));
							if(obj.has("col5"))
								profile.area.set(obj.getString("col5"));
							if(obj.has("col7"))
								profile.biaohao.set(obj.getString("col7"));
							if(obj.has("col8"))
							{
								/*int idx = f_rqbiaoxing.indexOf(obj.getString("col8"));
								if(idx>=0)
									((Spinner)mContext.findViewById(R.id.f_rqbiaoxing)).setSelection(idx);*/
								profile.zuoyoubiao.set(obj.getString("col8"));
							}
							if(obj.has("col9"))
								profile.biaochang.set(obj.getString("col9"));
							if(obj.has("col10"))
								profile.road.set(obj.getString("col10"));
							if(obj.has("col11"))
								profile.districtname.set(obj.getString("col11"));
							if(obj.has("col12"))
								profile.cusDom.set(obj.getString("col12"));
							if(obj.has("col13"))
								profile.cusDy.set(obj.getString("col13"));
							if(obj.has("col14"))
								profile.cusFloor.set(obj.getString("col14"));
							if(obj.has("col15"))
								profile.apartment.set(obj.getString("col15"));
							if(obj.has("col16"))
								profile.tsum.set(obj.getString("col16"));
							if(obj.has("col17"))
								profile.tcount.set(obj.getString("col17"));
							if(obj.has("col18"))
								profile.userID.set(obj.getString("col18"));
							if(obj.has("col19"))
								profile.UserState.set(obj.getString("col19"));
							String archiveAddress = "";
							for(int i1=10; i1<16; i1++)
								if(obj.has("col" + i1))
									archiveAddress += obj.getString("col"+i1) + "---";
								else
									archiveAddress += "---";
							if(archiveAddress.endsWith("---"))
								archiveAddress = archiveAddress.substring(0, archiveAddress.length()-3);
							profile.f_archiveaddress.set(archiveAddress);
							
							userList.add(profile);
						}
					}
				} catch (Exception e) {
					Toast.makeText(mContext, "联网查询失败,正在查询本地库", Toast.LENGTH_SHORT).show();
					JSONArray jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get() + '_' + RoomNo.get());
					if(null == jsons || 0 == jsons.length())
						jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]" + '_' + RoomNo.get());
					if(null == jsons || 0 == jsons.length())
						jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get());
					if(null == jsons || 0 == jsons.length())
						jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]");
					else if(null == jsons || 0 == jsons.length())
						Toast.makeText(mContext, "请手动查询,若没有找到用户则无此用户", Toast.LENGTH_SHORT).show();
					else
					{
						Message msg1 = new Message();
						msg1.obj = jsons.toString();
						msg1.what = 1;
						listHandler.sendMessage(msg1);
					}
				}
			} else if (0 == msg.what) {
				Toast.makeText(mContext, "联网查询失败,正在查询本地库", Toast.LENGTH_LONG).show();
				JSONArray jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get() + '_' + RoomNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]" + '_' + RoomNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]");
				else if(null == jsons || 0 == jsons.length())
					Toast.makeText(mContext, "请手动查询,若没有找到用户则无此用户", Toast.LENGTH_SHORT).show();
				else
				{
					Message msg1 = new Message();
					msg1.obj = jsons.toString();
					msg1.what = 1;
					listHandler.sendMessage(msg1);
				}
			} else if (2 == msg.what) {
				Toast.makeText(mContext, "正在查询本地库", Toast.LENGTH_LONG).show();
				JSONArray jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get() + '_' + RoomNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]" + '_' + RoomNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]");
				else if(null == jsons || 0 == jsons.length())
					Toast.makeText(mContext, "请手动查询,若没有找到用户则无此用户", Toast.LENGTH_SHORT).show();
				else
				{
					Message msg1 = new Message();
					msg1.obj = jsons.toString();
					msg1.what = 1;
					listHandler.sendMessage(msg1);
				}
			} else if (3 == msg.what) {
				Toast.makeText(mContext, "请输入查询条件, 电话号码必须至少6位", Toast.LENGTH_LONG).show();
			}
		}
	};	
	
	protected void execute3() {
		Thread th1 = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					
					if(UnitName.get().length() != 0 && BuildingNo.get().length() != 0 && UnitNo.get().length() != 0 && LevelNo.get().length() != 0 && RoomNo.get().length() != 0)
					{
						//Nothing to do.
					}
					else
					{
						Message msg = new Message();
						msg.what = 3;
						listHandler3.sendMessage(msg);
						return;
					}

					String AddressForSearch2 = UnitName.get() + "%" + BuildingNo.get() + "%" + "[东西南北中" + LevelNo.get() + "]";

					String sql = "SELECT t1.f_username, f_phone, f_address, f_cardid, f_city, f_area, '',f_meternumber 基表号,f_aroundmeter 左右表, f_jbfactory 基表厂家, f_road, f_districtname, f_cusDom, f_cusDy, f_cusFloor, f_apartment, tsum, tcount, t1.f_userid, f_userstate from (select * from t_userfiles WHERE f_address like '%" + AddressForSearch2 + "%') t1 left join (SELECT f_userid, f_username, SUM (f_pregas) tsum, count(f_pregas) tcount FROM t_sellinggas WHERE f_username like '%%' group by f_userid, f_username) t2 on t1.f_userid = t2.f_userid";
					String url = Vault.DB_URL + "sql/"
							+ URLEncoder
							.encode(sql, "UTF8")
									.replace("+", "%20");
					HttpGet getMethod = new HttpGet(url);
					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse response = httpClient.execute(getMethod);

					int code = response.getStatusLine().getStatusCode();

					// 数据下载完成
					if (code == 200) {
						String strResult = EntityUtils.toString(
								response.getEntity(), "UTF8");
						Message msg = new Message();
						msg.obj = strResult;
						msg.what = 1;
						listHandler3.sendMessage(msg);
					} else {
						Message msg = new Message();
						msg.what = 2;
						listHandler3.sendMessage(msg);
					}
				}catch (Exception e) {
					Message msg = new Message();
					msg.what = 0;
					listHandler3.sendMessage(msg);
				}
			}
		});
		th1.start();
	}
	
	// 显示用户信息
	private final Handler listHandler3 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (1 == msg.what) {
				super.handleMessage(msg);
				try {
					userList.clear();
					JSONArray array = new JSONArray((String) msg.obj);
					if(array.length() == 0)
					{
						Toast.makeText(mContext, "没有自动查出用户，请手动查询用户!", Toast.LENGTH_SHORT).show();
						return;
					}
					for(int i=0; i<array.length(); i++)
					{
						JSONObject obj = array.getJSONObject(i);
						UserRow profile = new UserRow();
						profile.model = QueryUserInfoModel.this;
						if (!obj.has("col0")) {
							// 查不到此IC卡用户
							Toast.makeText(mContext, "无此用户信息！", Toast.LENGTH_SHORT).show();
						} 
						else
						{
							if(obj.has("col0"))
								profile.userName.set(obj.getString("col0"));
							if(obj.has("col1"))
								profile.telephone.set(obj.getString("col1"));
							if(obj.has("col2"))
								profile.address.set(obj.getString("col2"));
							if(obj.has("col3"))
								profile.cardID.set(obj.getString("col3"));
							if(obj.has("col4"))
								profile.city.set(obj.getString("col4"));
							if(obj.has("col5"))
								profile.area.set(obj.getString("col5"));
							if(obj.has("col7"))
								profile.biaohao.set(obj.getString("col7"));
							if(obj.has("col8"))
							{
								profile.zuoyoubiao.set(obj.getString("col8"));
							}
							if(obj.has("col9"))
								profile.biaochang.set(obj.getString("col9"));
							if(obj.has("col10"))
								profile.road.set(obj.getString("col10"));
							if(obj.has("col11"))
								profile.districtname.set(obj.getString("col11"));
							if(obj.has("col12"))
								profile.cusDom.set(obj.getString("col12"));
							if(obj.has("col13"))
								profile.cusDy.set(obj.getString("col13"));
							if(obj.has("col14"))
								profile.cusFloor.set(obj.getString("col14"));
							if(obj.has("col15"))
								profile.apartment.set(obj.getString("col15"));
							if(obj.has("col16"))
								profile.tsum.set(obj.getString("col16"));
							if(obj.has("col17"))
								profile.tcount.set(obj.getString("col17"));
							if(obj.has("col18"))
								profile.userID.set(obj.getString("col18"));
							if(obj.has("col19"))
								profile.UserState.set(obj.getString("col19"));
							String archiveAddress = "";
							for(int i1=10; i1<16; i1++)
								if(obj.has("col" + i1))
									archiveAddress += obj.getString("col"+i1) + "---";
								else
									archiveAddress += "---";
							if(archiveAddress.endsWith("---"))
								archiveAddress = archiveAddress.substring(0, archiveAddress.length()-3);
							profile.f_archiveaddress.set(archiveAddress);
							
							userList.add(profile);
						}
					}
				} catch (Exception e) {
					Toast.makeText(mContext, "联网查询失败,正在查询本地库", Toast.LENGTH_LONG).show();
					JSONArray jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get() + '_' + RoomNo.get());
					if(null == jsons || 0 == jsons.length())
						jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]" + '_' + RoomNo.get());
					if(null == jsons || 0 == jsons.length())
						jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get());
					if(null == jsons || 0 == jsons.length())
						jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]");
					else if(null == jsons || 0 == jsons.length())
						Toast.makeText(mContext, "请手动查询,若没有找到用户则无此用户", Toast.LENGTH_SHORT).show();
					else
					{
						Message msg1 = new Message();
						msg1.obj = jsons.toString();
						msg1.what = 1;
						listHandler.sendMessage(msg1);
					}
				}
			} else if (0 == msg.what) {
				Toast.makeText(mContext, "联网查询失败,正在查询本地库", Toast.LENGTH_LONG).show();
				JSONArray jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get() + '_' + RoomNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]" + '_' + RoomNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]");
				else if(null == jsons || 0 == jsons.length())
					Toast.makeText(mContext, "请手动查询,若没有找到用户则无此用户", Toast.LENGTH_SHORT).show();
				else
				{
					Message msg1 = new Message();
					msg1.obj = jsons.toString();
					msg1.what = 1;
					listHandler.sendMessage(msg1);
				}
			} else if (2 == msg.what) {
				Toast.makeText(mContext, "正在查询本地库", Toast.LENGTH_LONG).show();
				JSONArray jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get() + '_' + RoomNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]" + '_' + RoomNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]");
				else if(null == jsons || 0 == jsons.length())
					Toast.makeText(mContext, "请手动查询,若没有找到用户则无此用户", Toast.LENGTH_SHORT).show();
				else
				{
					Message msg1 = new Message();
					msg1.obj = jsons.toString();
					msg1.what = 1;
					listHandler.sendMessage(msg1);
				}
			} else if (3 == msg.what) {
				Toast.makeText(mContext, "请手动查询", Toast.LENGTH_LONG).show();
			}
		}
	};	
	
	protected void execute4() {
		Thread th1 = new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					if(UnitName.get().length() != 0 && BuildingNo.get().length() != 0 && UnitNo.get().length() != 0 && LevelNo.get().length() != 0 && RoomNo.get().length() != 0)
					{
						//Nothing to do.
					}
					else
					{
						Message msg = new Message();
						msg.what = 3;
						listHandler4.sendMessage(msg);
						return;
					}
					
					String AddressForSearch3 = UnitName.get() + "%" + BuildingNo.get() + "%" + UnitNo.get() + "_" + LevelNo.get();

					String sql = "SELECT t1.f_username, f_phone, f_address, f_cardid, f_city, f_area, '',f_meternumber 基表号,f_aroundmeter 左右表, f_jbfactory 基表厂家, f_road, f_districtname, f_cusDom, f_cusDy, f_cusFloor, f_apartment, tsum, tcount, t1.f_userid, f_userstate from (select * from t_userfiles WHERE f_address like '%" + AddressForSearch3 + "%') t1 left join (SELECT f_userid, f_username, SUM (f_pregas) tsum, count(f_pregas) tcount FROM t_sellinggas WHERE f_username like '%%' group by f_userid, f_username) t2 on t1.f_userid = t2.f_userid";
					String url = Vault.DB_URL + "sql/"
							+ URLEncoder
							.encode(sql, "UTF8")
									.replace("+", "%20");
					HttpGet getMethod = new HttpGet(url);
					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse response = httpClient.execute(getMethod);

					int code = response.getStatusLine().getStatusCode();

					// 数据下载完成
					if (code == 200) {
						String strResult = EntityUtils.toString(
								response.getEntity(), "UTF8");
						Message msg = new Message();
						msg.obj = strResult;
						msg.what = 1;
						listHandler4.sendMessage(msg);
					} else {
						Message msg = new Message();
						msg.what = 2;
						listHandler4.sendMessage(msg);
					}
				}catch (Exception e) {
					Message msg = new Message();
					msg.what = 0;
					listHandler4.sendMessage(msg);
				}
			}
		});
		th1.start();
	}
	
	// 显示用户信息
	private final Handler listHandler4 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (1 == msg.what) {
				super.handleMessage(msg);
				try {
					userList.clear();
					JSONArray array = new JSONArray((String) msg.obj);
					if(array.length() == 0)
					{
						execute3();
						return;
					}
					for(int i=0; i<array.length(); i++)
					{
						JSONObject obj = array.getJSONObject(i);
						UserRow profile = new UserRow();
						profile.model = QueryUserInfoModel.this;
						if (!obj.has("col0")) {
							// 查不到此IC卡用户
							Toast.makeText(mContext, "无此用户信息！", Toast.LENGTH_SHORT).show();
						} 
						else
						{
							if(obj.has("col0"))
								profile.userName.set(obj.getString("col0"));
							if(obj.has("col1"))
								profile.telephone.set(obj.getString("col1"));
							if(obj.has("col2"))
								profile.address.set(obj.getString("col2"));
							if(obj.has("col3"))
								profile.cardID.set(obj.getString("col3"));
							if(obj.has("col4"))
								profile.city.set(obj.getString("col4"));
							if(obj.has("col5"))
								profile.area.set(obj.getString("col5"));
							if(obj.has("col7"))
								profile.biaohao.set(obj.getString("col7"));
							if(obj.has("col8"))
							{
								profile.zuoyoubiao.set(obj.getString("col8"));
							}
							if(obj.has("col9"))
								profile.biaochang.set(obj.getString("col9"));
							if(obj.has("col10"))
								profile.road.set(obj.getString("col10"));
							if(obj.has("col11"))
								profile.districtname.set(obj.getString("col11"));
							if(obj.has("col12"))
								profile.cusDom.set(obj.getString("col12"));
							if(obj.has("col13"))
								profile.cusDy.set(obj.getString("col13"));
							if(obj.has("col14"))
								profile.cusFloor.set(obj.getString("col14"));
							if(obj.has("col15"))
								profile.apartment.set(obj.getString("col15"));
							if(obj.has("col16"))
								profile.tsum.set(obj.getString("col16"));
							if(obj.has("col17"))
								profile.tcount.set(obj.getString("col17"));
							if(obj.has("col18"))
								profile.userID.set(obj.getString("col18"));
							if(obj.has("col19"))
								profile.UserState.set(obj.getString("col19"));
							String archiveAddress = "";
							for(int i1=10; i1<16; i1++)
								if(obj.has("col" + i1))
									archiveAddress += obj.getString("col"+i1) + "---";
								else
									archiveAddress += "---";
							if(archiveAddress.endsWith("---"))
								archiveAddress = archiveAddress.substring(0, archiveAddress.length()-3);
							profile.f_archiveaddress.set(archiveAddress);
							
							userList.add(profile);
						}
					}
				} catch (Exception e) {
					Toast.makeText(mContext, "联网查询失败,正在查询本地库", Toast.LENGTH_LONG).show();
					JSONArray jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get() + '_' + RoomNo.get());
					if(null == jsons || 0 == jsons.length())
						jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]" + '_' + RoomNo.get());
					if(null == jsons || 0 == jsons.length())
						jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get());
					if(null == jsons || 0 == jsons.length())
						jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]");
					else if(null == jsons || 0 == jsons.length())
						Toast.makeText(mContext, "请手动查询,若没有找到用户则无此用户", Toast.LENGTH_SHORT).show();
					else
					{
						Message msg1 = new Message();
						msg1.obj = jsons.toString();
						msg1.what = 1;
						listHandler.sendMessage(msg1);
					}
				}
			} else if (0 == msg.what) {
				Toast.makeText(mContext, "联网查询失败,正在查询本地库", Toast.LENGTH_LONG).show();
				JSONArray jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get() + '_' + RoomNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]" + '_' + RoomNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]");
				else if(null == jsons || 0 == jsons.length())
					Toast.makeText(mContext, "请手动查询,若没有找到用户则无此用户", Toast.LENGTH_SHORT).show();
				else
				{
					Message msg1 = new Message();
					msg1.obj = jsons.toString();
					msg1.what = 1;
					listHandler.sendMessage(msg1);
				}
			} else if (2 == msg.what) {
				Toast.makeText(mContext, "正在查询本地库", Toast.LENGTH_LONG).show();
				JSONArray jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get() + '_' + RoomNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]" + '_' + RoomNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + UnitNo.get() + '_' + LevelNo.get());
				if(null == jsons || 0 == jsons.length())
					jsons = SafeCheckFromDB(UnitName.get() + "%" + BuildingNo.get() + "_%" + "[东西南北中" + LevelNo.get() + "]");
				else if(null == jsons || 0 == jsons.length())
					Toast.makeText(mContext, "请手动查询,若没有找到用户则无此用户", Toast.LENGTH_SHORT).show();
				else
				{
					Message msg1 = new Message();
					msg1.obj = jsons.toString();
					msg1.what = 1;
					listHandler.sendMessage(msg1);
				}
			} else if (3 == msg.what) {
				Toast.makeText(mContext, "请手动查询", Toast.LENGTH_LONG).show();
			}
		}
	};
	
	private JSONArray SafeCheckFromDB(String AddressForSearch) {
		SQLiteDatabase db = null;
		try {
			JSONArray jsons = new JSONArray();
			db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
			String sql = "SELECT f_username, f_phone, f_address, f_cardid, f_meternumber, f_aroundmeter, f_jbfactory, f_road, f_districtname, f_cusDom, f_cusDy, f_cusFloor, f_apartment, tsum, tcount, f_userstate FROM T_USERFILES WHERE f_address LIKE '%" + AddressForSearch + "%'";
			Cursor c = db.rawQuery(sql, new String[] {});
			while (c.moveToNext()) {
				JSONObject json = new JSONObject();
				json.put("col0", c.getString(c.getColumnIndex("f_username")));
				json.put("col1", c.getString(c.getColumnIndex("f_phone")));
				json.put("col2", c.getString(c.getColumnIndex("f_address")));
				json.put("col3", c.getString(c.getColumnIndex("f_cardid")));
				json.put("col7", c.getString(c.getColumnIndex("f_meternumber")));
				json.put("col8", c.getString(c.getColumnIndex("f_aroundmeter")));
				json.put("col9", c.getString(c.getColumnIndex("f_jbfactory")));
				json.put("col10", c.getString(c.getColumnIndex("f_road")));
				json.put("col11", c.getString(c.getColumnIndex("f_districtname")));
				json.put("col12", c.getString(c.getColumnIndex("f_cusDom")));
				json.put("col13", c.getString(c.getColumnIndex("f_cusDy")));
				json.put("col14", c.getString(c.getColumnIndex("f_cusFloor")));
				json.put("col15", c.getString(c.getColumnIndex("f_apartment")));
				json.put("col16", c.getString(c.getColumnIndex("tsum")));
				json.put("col17", c.getString(c.getColumnIndex("tcount")));
				json.put("col19", c.getString(c.getColumnIndex("f_userstate")));
				jsons.put(json);
			}
			db.close();
			return jsons;
		} catch (Exception e) {
			return null;
		}
	}
	
}