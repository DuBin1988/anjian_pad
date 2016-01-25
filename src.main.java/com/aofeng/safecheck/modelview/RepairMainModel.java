package com.aofeng.safecheck.modelview;

import com.aofeng.safecheck.SafeCheckApp;
import com.aofeng.safecheck.activity.RepairQueryActivity;
import com.aofeng.safecheck.activity.RepairUploadActivity;
import com.aofeng.safecheck.activity.SetupActivity;
import com.aofeng.safecheck.activity.ToBeRepairedActivity;
import gueei.binding.Command;
import gueei.binding.observables.StringObservable;
import android.app.Activity;
import android.content.Intent;
import android.view.View;


public  class RepairMainModel {
	private Activity mContext;

	public RepairMainModel(Activity context) {
		this.mContext = context;
	}
	
	//进入重置界面
	public Command Reset = new Command(){
		public void Invoke(View view, Object... args) {
			Intent intent = new Intent(mContext,SetupActivity.class);
			mContext.startActivity(intent);			
		}
	};
		
	//进入维修任务列表界面
	public Command RepairView = new Command(){
		public void Invoke(View view, Object... args) {
			((SafeCheckApp)mContext.getApplication()).IsRepairFirstEntry = true;
			Intent intent = new Intent(mContext, ToBeRepairedActivity.class);
			intent.putExtra("count", Integer.parseInt(count.get()));
			mContext.startActivity(intent);			
		}
	};
	
	//进入维修上传界面
	public Command RepairUpload = new Command(){
		public void Invoke(View view, Object... args){
			Intent intent = new Intent(mContext, RepairUploadActivity.class);
			mContext.startActivity(intent);			
		}
	};
	
	//进入维修查询界面
	public Command RepairQuery = new Command(){
		public void Invoke(View view, Object... args){
			Intent intent = new Intent(mContext, RepairQueryActivity.class);
			mContext.startActivity(intent);			
		}
	};
	
	//进入修改密码界面
	public Command ModifyPassword = new Command(){
		public void Invoke(View view, Object... args){
		}
	};

	public StringObservable count = new StringObservable();
}
