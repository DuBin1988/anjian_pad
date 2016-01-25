package com.aofeng.safecheck.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import gueei.binding.app.BindingActivity;
import gueei.binding.collections.ArrayListObservable;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.SafeCheckApp;
import com.aofeng.safecheck.model.UnrepairedRowModel;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ToBeRepairedActivity extends BindingActivity{
	
	private boolean canceled = false;
	ProgressDialog pd;
	private int count;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setAndBindRootView(R.layout.to_be_repaired, this);
		count = this.getIntent().getExtras().getInt("count");
	}

	// 计划列表
	public ArrayListObservable<UnrepairedRowModel> toBeRepairedList = new ArrayListObservable<UnrepairedRowModel>(UnrepairedRowModel.class);
	
	@Override
	protected void onResume() {
		super.onResume();
		if(((SafeCheckApp)getApplication()).IsRepairFirstEntry && count>0)
		{
			((SafeCheckApp)getApplication()).IsRepairFirstEntry = false;
			GetRemoteRepairList();
		}
		else
			ShowToBeRepairedList();
	}

	private final Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			//下载字节进度
			if(msg.what==0)
			{
				pd.setMessage(msg.obj.toString());
			}
			//下载整体进度
			else if(msg.what==1)
			{
				int progress = Integer.parseInt(msg.obj.toString());
				pd.setProgress(progress);
				if(progress == 100)
				{
					pd.dismiss();
					//显示列表
					ShowToBeRepairedList();
				}
			}
			//下载图片进度
			else if(msg.what==2)
			{
				pd.setMessage(msg.obj.toString());
			}
			//异常
			else if(msg.what==-1)
			{
				Toast.makeText(ToBeRepairedActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
				pd.dismiss();
			}
		}
	};
	
	/**
	 * 异步提取列表
	 */
	private void GetRemoteRepairList() {
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("开始下载......");
		pd.setCancelable(false);
		pd.setCanceledOnTouchOutside(false);
		pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", (DialogInterface.OnClickListener)null);
		pd.setOnShowListener(new DialogInterface.OnShowListener() {
			
			@Override
			public void onShow(DialogInterface dialog) {
				((Button)pd.getButton(DialogInterface.BUTTON_NEGATIVE)).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						canceled = true;
					}
				});
			}
		});
		pd.show();
		
		Thread th = new Thread(new Runnable() {	
			@Override
			public void run() {
				execute();
			}
		});
		th.start();
	}
	
	/**
	 * 执行下载过程
	 */
	private void execute()
	{
		try {
			String url = Vault.DB_URL + URLEncoder.encode("select distinct i from T_INSPECTION i left join fetch i.LINES" +
					" where i.REPAIRMAN_ID='" + Util.getSharedPreference(this, Vault.USER_ID) + "' and i.REPAIR_STATE='" + Vault.REPAIRED_NOT + "'", "UTF8")
					.replace("+", "%20");
			URL myURL = new URL(url);
			URLConnection conn = myURL.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			ByteBuffer bucket = ByteBuffer.allocate(1024 * 1024 * 2);
			byte buf[] = new byte[1024];
			int progress = 0;
			sendMsg(0, "已下载：" + progress);
			do {
				int numread = is.read(buf);
				if(canceled)
				{
					sendMsg(-1, "取消下载！");
					return;
				}
				if (numread == -1) {
					break;
				}
				progress += numread;
				bucket.put(buf, 0, numread);
				sendMsg(0, "已下载：" + progress + "字节");
			} while (true);
			sendMsg(1, 25);
			String json = new String(bucket.array(),0, progress, Charset.forName("UTF-8"));
			bucket = null;
			System.gc();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			JSONArray array = new JSONArray(json);
			int n = array.length();
			for(int i =0; i< n; i++)
			{
				sendMsg(2,"下载图片...");
				if(canceled)
				{
					sendMsg(-1, "取消下载！");
					return;
				}
				JSONObject row = array.getJSONObject(i);
				//是否需要添加到本地
				int needToSave = NeedToSave(row);
				if(needToSave==-1)
				{
					sendMsg(-1,"下载出错！");
					return;
				}
				else if(needToSave==1)
				{
					if(!DownloadAndSavePics(row))
					{
						sendMsg(-1, "下载出错！");
						return;
					}
					if(!SaveRow(row))
					{
						sendMsg(-1,"下载出错！");
						return;
					}					
					sendMsg(1, 25 + (int)((i+1)*1.0/n*75.0));
				}
			}
			if(!GetRepairParam())
			{
				sendMsg(-1,"下载出错！");
				return;
			}					
			sendMsg(1,100);
		} catch (Exception e) {
			sendMsg(-1, "下载出错！");
		}
	}

/**
 * 维修参数
 */
	private boolean GetRepairParam() {
		try
		{
			HttpGet getMethod = new HttpGet(Vault.DB_URL +URLEncoder.encode("from paramvalue where param.name='安检维修选项' order by id", "UTF8")
					.replace("+", "%20"));
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(getMethod);
	
			int code = response.getStatusLine().getStatusCode();
			// 数据下载完成
			if (code == 200) {
				String json  = EntityUtils.toString(response.getEntity(), "UTF8");	
				return EmptyAndInsert(json);
			}
			else
				return false;			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 
	 * @param json
	 */
	private boolean EmptyAndInsert(String json) {
		SQLiteDatabase db = null;
		try
		{
			db = openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
			db.execSQL("delete from T_PARAMS where id='安检维修选项'");
			JSONArray array = new JSONArray(json);
			for(int i =0; i< array.length(); i++)
			{
				JSONObject row = array.getJSONObject(i);
				db.execSQL("INSERT INTO T_PARAMS(ID, NAME, CODE) VALUES(?,?,?)", new String[]{"安检维修选项", 
						row.getString("name"), row.getString("code")});
			}
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally 
		{
			if(db != null)
				db.close();
		}		
}

	/**
	 * 保存维修安检
	 * @param row
	 */
	private int NeedToSave(JSONObject row) {
		SQLiteDatabase db = null;
		try
		{
			db = openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
			//检查本地是否存在该安检维修记录
			String id = row.getString("id");
			Cursor c  = db.rawQuery("select * from T_REPAIR_TASK " + " where id = ?",
					new String[]{ id});
			if(c.moveToNext())
			{
				//未上传，不需要存
				if(!(c.getString(c.getColumnIndex("REPAIR_STATE")).equals(Vault.REPAIRED_NOT)))
					return 0;
				else
				{
					return 1;
				}
			}
			//不存在，增加
			else
			{
				return 1;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return -1;
		}
		finally 
		{
			if(db != null)
				db.close();
		}
	}

	/**
	 * 保存行
	 * @param row
	 * @return
	 */
	private boolean SaveRow(JSONObject row) {
		if(!DeleteFilesAndRow(row))
			return false;
		SQLiteDatabase db = null;
		try
		{
			db = openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
			String id = row.getString("id");
			String sql1 = "INSERT INTO T_REPAIR_TASK(ID";
			String sql2 = ") VALUES('" + id + "'";
			Iterator<String> itr = row.keys();
			while(itr.hasNext())
			{
				String key = itr.next();
				if(key.toUpperCase().equals("ID") || key.equals("LINES") || key.equals("EntityType"))
					continue;
				sql1 += ", " + key;
				String value = row.getString(key);
				if(value==null || value.equals("null"))
					value = "''";
				else
				{
					if(!key.equals("JB_NUMBER") && !key.equals("SURPLUS_GAS"))
						value = "'" + value + "'";
				}
				sql2 += ", " + value;
			}
			sql1 = sql1 + sql2 + ")";
			db.execSQL(sql1);
			//子表
			JSONArray lines = new JSONArray(row.getString("LINES"));
			for(int i=0; i<lines.length(); i++)
			{
				sql1 = "INSERT INTO T_REPAIR_ITEM(ID";
				sql2 = ") VALUES('" + id + "'";
				JSONObject line = lines.getJSONObject(i);
				itr = line.keys();
				while(itr.hasNext())
				{
					String key = itr.next();
					if(key.toUpperCase().equals("ID") || key.equals("EntityType"))
						continue;
					sql1 += ", " + key;
					String value = line.getString(key);
					if(value==null || value.equals("null"))
						value = "''";
					else
					{
						if(!key.equals("JB_NUMBER") && !key.equals("SURPLUS_GAS"))
							value = "'" + value + "'";
					}
					sql2 += ", " + value;
				}
				sql1 = sql1 + sql2 + ")";
				db.execSQL(sql1);				
			}
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(db != null)
				db.close();
		}
	}

	/**
	 * 删除行及相关文件
	 * @param row
	 * @return
	 */
	private boolean DeleteFilesAndRow(JSONObject row) {
		SQLiteDatabase db = null;
		try
		{
			db = openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
			String id = row.getString("id");
			Cursor c  = db.rawQuery("select * from T_REPAIR_TASK " + " where id = ?",
					new String[]{ id});
			if(c.moveToNext())
			{
				String[] keys = {"USER_SIGN","PHOTO_FIRST", "PHOTO_SECOND", "PHOTO_THIRD", "PHOTO_FOUTH", "PHOTO_FIFTH"};
				String[] suffix = {".png", ".jpg", ".jpg", ".jpg", ".jpg", ".jpg"};
				for(int i=0; i<keys.length; i++)
				{
					String value = c.getString(c.getColumnIndex(keys[i]));
					if(value != null && value.length()>0)
					{
						File file = new File(Util.getSharedPreference(this, "FileDir") +"_" + value + suffix[i]);
						file.delete();
					}
				}
			}
			
			db.execSQL("delete from T_REPAIR_ITEM " + " where id = ?",
					new String[]{ id});
			db.execSQL("delete from T_REPAIR_TASK " + " where id = ?",
					new String[]{ id});			
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally 
		{
			if(db != null)
				db.close();
		}
	}

	/**
	 * 下载并保存所有的图片
	 * @param row
	 */
	private boolean DownloadAndSavePics(JSONObject row) {
		try
		{
			String[] keys = {"USER_SIGN","PHOTO_FIRST", "PHOTO_SECOND", "PHOTO_THIRD", "PHOTO_FOUTH", "PHOTO_FIFTH"};
			String[] suffix = {".png", ".jpg", ".jpg", ".jpg", ".jpg", ".jpg"};
			for(int i=0; i<keys.length; i++)
			{
				String value = row.getString(keys[i]);
				if(value != null && value.length()>0 && !value.toLowerCase().equals("null"))
				{
					//download
					String url = Vault.DB_URL + "file/" + value;
					if(!DownloadAndSavePic(url, "_"+value + suffix[i]))
						return false;
				}
			}
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}	
	
	/**
	 * 保存单个文件
	 * @param url
	 * @param fn
	 * @return
	 */
	private boolean DownloadAndSavePic(String url, String fn) {
		try
		{
			HttpGet getMethod = new HttpGet(url);
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(getMethod);
	
			int code = response.getStatusLine().getStatusCode();
	
			// 数据下载完成
			if (code == 200) {
				byte[] fc = EntityUtils.toByteArray(response.getEntity());
				FileOutputStream out = new FileOutputStream(Util.getSharedPreference(this, "FileDir")+ fn);
				out.write(fc);
				out.close();
				return true;
			}
			else
				return false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	//发送消息
	private void sendMsg(int what, Object obj)
	{
		Message msg = new Message();
		msg.what = what;
		msg.obj = obj;
		mHandler.sendMessage(msg);
	}
	
	/**
	 * 显示要维修的安检列表
	 */
	private void ShowToBeRepairedList() {
		SQLiteDatabase db = null;
		try
		{
			db = openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
			Cursor c  = db.rawQuery("select id, ROAD, UNIT_NAME, CUS_DOM, CUS_DY, CUS_FLOOR, CUS_ROOM, CHECKPLAN_ID from T_REPAIR_TASK " + " where REPAIRMAN_ID=? and REPAIR_STATE=?",
					new String[]{ Util.getSharedPreference(this, Vault.USER_ID), Vault.REPAIRED_NOT});
			toBeRepairedList.clear();
			while(c.moveToNext())
			{
				UnrepairedRowModel row = new UnrepairedRowModel(this, c.getString(0)); 
				row.ROAD.set(c.getString(1));
				row.UNIT_NAME.set(c.getString(2));
				row.CUS_DOM.set(c.getString(3));
				row.CUS_DY.set(c.getString(4));
				row.CUS_FLOOR.set(c.getString(5));
				row.CUS_ROOM.set(c.getString(6));
				row.CHECKPLAN_ID = c.getString(7);
				this.toBeRepairedList.add(row);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally 
		{
			if(db != null)
				db.close();
		}
	}

}