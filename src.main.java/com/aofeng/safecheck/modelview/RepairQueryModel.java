package com.aofeng.safecheck.modelview;

import java.io.File;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import gueei.binding.Command;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.activity.ShootActivity;
import com.aofeng.safecheck.model.RepairQueryRowModel;
import com.aofeng.safecheck.model.RepairUploadRowModel;
import com.aofeng.safecheck.model.UploadRowModel;
import com.aofeng.utils.CountableFileEntity;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class RepairQueryModel {
		public Activity mContext;
		public boolean cancelable;
		public volatile boolean canceled;
		private int idx = 0;
		
		public StringObservable txtCardNo = new StringObservable("");
		public StringObservable txtUserName = new StringObservable("");
		public StringObservable txtAddress = new StringObservable("");
		
		public Command SearchByExample = new Command(){
			public void Invoke(View view, Object... args) {
				if(idx == 0)
					listByExample(null);
				else if( idx ==1)
					listByExample(Vault.REPAIRED_UNUPLOADED);
				else if(idx ==2)
					listByExample(Vault.REPAIRED_UPLOADED);					
			}
		};
		
		public Activity getActivity() {
			return mContext;
		}

		public RepairQueryModel(Activity context) {
			this.mContext = context;
			if(Util.DBExists(mContext))
				listByExample(null);
		}
		/**
		 * 加亮当前选择
		 * @param imgId
		 */
		private void HilightChosenImg(int imgId) {

			allImgId.set(R.drawable.all_btn);
			weiImgId.set(R.drawable.unupload_btn);
			yiImgId.set(R.drawable.uploaded_btn);
			if(imgId == R.drawable.all_btn_hover)
			{
				idx = 0;
				allImgId.set(imgId);
			}
			else if(imgId == R.drawable.unupload_btn_hover)
			{
				idx  =1;
				weiImgId.set(imgId);
			}
			else if(imgId == R.drawable.uploaded_btn_hover)
			{
				idx = 2;
				yiImgId.set(imgId);
			}			
		}
		
		public IntegerObservable allImgId = new IntegerObservable(R.drawable.all_btn_hover);
		public Command AllClicked = new Command(){
			public void Invoke(View view, Object... args) {
				RepairQueryModel.this.HilightChosenImg(R.drawable.all_btn_hover);
				listByExample(null);
			}
		};
		
		public IntegerObservable weiImgId = new IntegerObservable(R.drawable.unupload_btn);
		public Command WeiImgClicked = new Command(){
			public void Invoke(View view, Object... args) {
				RepairQueryModel.this.HilightChosenImg(R.drawable.unupload_btn_hover);
				listByExample(Vault.REPAIRED_UNUPLOADED);
			}
		};

		public IntegerObservable yiImgId = new IntegerObservable(R.drawable.uploaded_btn);
		public Command YiImgClicked = new Command(){
			public void Invoke(View view, Object... args) {
				RepairQueryModel.this.HilightChosenImg(R.drawable.uploaded_btn_hover);
				listByExample(Vault.REPAIRED_UPLOADED);
			}
		};


		//绑定未上传的列表
		public ArrayListObservable<RepairQueryRowModel> repairList = new ArrayListObservable<RepairQueryRowModel>(
				RepairQueryRowModel.class);
		
		/**
		 * 按条件查找记录
		 */
		public void listByExample(String state) {
			SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
			
			String sql = "SELECT id, ROAD, UNIT_NAME, CUS_DOM, CUS_DY, CUS_FLOOR, CUS_ROOM, REPAIR_STATE, CHECKPLAN_ID " +
					"  FROM T_REPAIR_TASK  where 1=1 ";
			if(state != null)
				sql += " and  REPAIR_STATE='" + state +"'";
			//检查查询条件
			if(txtCardNo.get().trim().length()>0)
				sql += " and CARD_ID like '%" + txtCardNo.get().trim() + "%'";
			if(txtUserName.get().trim().length()>0)
				sql += " and USER_NAME like '%" + txtUserName.get().trim() + "%'";
			String address = txtAddress.get().trim();
			if(address.length()>0)
				sql += " and (ROAD like '%" + address + "%' OR UNIT_NAME like '%" + address 
				+ "%' OR CUS_DOM like '%" + address + "%' OR CUS_DY like '%" + address 
				+ "%' OR CUS_FLOOR like '%" + address + "%' OR CUS_ROOM like '%" + address + "%' ) " ;
			sql += " order by ROAD, UNIT_NAME, CUS_DOM, CUS_DY, CUS_FLOOR, CUS_ROOM";	
			// 从安检单里获取所有街道名
			Cursor c = db.rawQuery(
							sql,
									new String[]{}); 
			repairList.clear();
			while (c.moveToNext()) {
				RepairQueryRowModel row = new RepairQueryRowModel(this,  c.getString(0),
						c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6),
						c.getString(7), c.getString(8)); 
				repairList.add(row);
			}
			db.close();
		}

}
