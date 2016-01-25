package com.aofeng.safecheck.activity;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import gueei.binding.Binder;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.modelview.RepairMainModel;
import com.aofeng.safecheck.modelview.RepairMan;
import com.aofeng.utils.RemoteReader;
import com.aofeng.utils.RemoteReaderListener;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

public class RepairMainActivity extends Activity {
	RepairMainModel model;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		model = new RepairMainModel(this);
		Binder.setAndBindContentView(this, R.layout.repair_main, model);	

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(Util.DBExists(this))
			this.getUnrepairedCount();
	}


	/**
	 * ��ǰ��¼�û���������δά�޼�¼������Զ�����ݿ��е�δά�޼�¼�� - ���ؿ��е�δά�޼�¼��
	 * @return
	 */
	private void getUnrepairedCount()
	{
		this.findViewById(R.id.btnUnrepared).setEnabled(false);
		//�õ�����˸ÿͻ�δά�޵ļ�¼����
		RemoteReader reader = new RemoteReader(Vault.DB_URL+ 
				URLEncoder.encode("from T_INSPECTION where REPAIRMAN_ID='" + Util.getSharedPreference(this, Vault.USER_ID) + "' and REPAIR_STATE='" + Vault.REPAIRED_NOT + "'")	.replace("+", "%20") +"/,");
		reader.setRemoteReaderListener(new RemoteReaderListener() {

			@Override
			public void onSuccess(List<Map<String, Object>> map) {
				super.onSuccess(map);
				int count = 0;
				if(map.size()>0)
				{
					Map<String, Object> aMap = map.get(0);
					count = Integer.parseInt(aMap.get("Count").toString());
				}

				count = count - GetLocalCount();
				if(count<0)
					count =0;
				model.count.set(count+"");
				findViewById(R.id.btnUnrepared).setEnabled(true);
			}

			@Override
			public void onFailure(String errMsg) {
				super.onFailure(errMsg);
				findViewById(R.id.btnUnrepared).setEnabled(true);
				model.count.set("0");
			}

		});
		reader.start();
	}
	
	private int GetLocalCount() {
		SQLiteDatabase db = null;
		try
		{
			db = openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
			//��������ϴ�״̬�ĸ���
			Cursor c  = db.rawQuery("select count(*) from T_REPAIR_TASK " +
					"where REPAIRMAN_ID=? and REPAIR_STATE <> ?",
					new String[]{Util.getSharedPreference(this, Vault.USER_ID), Vault.REPAIRED_UPLOADED});
			if(c.moveToNext())
				return c.getInt(0);
			else
				return 0;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Toast.makeText(this, "�������ݿ��������ϵϵͳ����Ա��", Toast.LENGTH_SHORT).show();
			return 0;
		}
		finally 
		{
			if(db != null)
				db.close();
		}
	}
}
