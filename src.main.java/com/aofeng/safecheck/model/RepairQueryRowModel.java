package com.aofeng.safecheck.model;

import gueei.binding.Command;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.FloatObservable;
import gueei.binding.observables.StringObservable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.aofeng.safecheck.activity.RepairActivity;
import com.aofeng.safecheck.modelview.RepairQueryModel;
import com.aofeng.safecheck.modelview.RepairUploadModel;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

public class RepairQueryRowModel {
	RepairQueryModel model;
	// 下发的安检单ID
	public StringObservable ID = new StringObservable("");
	public StringObservable Address = new StringObservable("");
	public String ROAD = ""; // 街道
	public String UNIT_NAME = ""; // 小区
	public String CUS_DOM = ""; // 楼号
	public String CUS_DY = ""; // 单元
	public String CUS_FLOOR = "";
	// 房间
	public String CUS_ROOM = "";
	public String CHECKPLAN_ID = "";
	
	//上传成功、不成功、已检、未检、拒绝、维修、新增、删除
	public BooleanObservable UPLOADED = new BooleanObservable(false);
	public BooleanObservable UN_UPLOADED = new BooleanObservable(true);

	public RepairQueryRowModel(RepairQueryModel repairModel, String id, 
			String ROAD, String UNIT_NAME, String CUS_DOM,String CUS_DY, String CUS_FLOOR, String CUS_ROOM, String state, String CHECKPLAN_ID) {
		model = repairModel;
		Address.set(ROAD +" " + UNIT_NAME +" " + CUS_DOM +" " + CUS_DY +" " + CUS_FLOOR +" " + CUS_ROOM +" " );
		ID.set(id);
		this.ROAD = ROAD;
		this.UNIT_NAME = UNIT_NAME;
		this.CUS_DOM = CUS_DOM;
		this.CUS_DY =CUS_DY;
		this.CUS_FLOOR = CUS_FLOOR;
		this.CUS_ROOM =CUS_ROOM;
		this.CHECKPLAN_ID = CHECKPLAN_ID;
		if(state.equals(Vault.REPAIRED_UPLOADED))
		{
			this.UN_UPLOADED.set(false);
			this.UPLOADED.set(true);
		}
		else
		{
			this.UN_UPLOADED.set(true);
			this.UPLOADED.set(false);
		}
	}
	
	public Command InspectApartment = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			Util.ClearCache(model.mContext, Util.getSharedPreference(model.mContext, Vault.USER_ID) + "_" + ID.get());
			Intent intent = new Intent();
			// 利用包袱传递参数给Activity
			Bundle bundle = new Bundle();

			bundle.putString("ID", ID.get());
			bundle.putString("CHECKPLAN_ID", CHECKPLAN_ID);
			bundle.putString("CUS_FLOOR", CUS_FLOOR);
			bundle.putString("CUS_ROOM", CUS_ROOM);
			bundle.putString("CUS_DY", CUS_DY);
			bundle.putString("ROAD", ROAD);
			bundle.putString("UNIT_NAME", UNIT_NAME);
			bundle.putString("CUS_DOM", CUS_DOM);
			bundle.putBoolean("INSPECTED", true);
			bundle.putBoolean("READONLY", true);

			intent.setClass(model.mContext, RepairActivity.class);
			intent.putExtras(bundle);
			model.mContext.startActivity(intent);
		}
	};
}
