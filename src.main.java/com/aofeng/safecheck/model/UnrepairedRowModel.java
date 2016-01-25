package com.aofeng.safecheck.model;

import gueei.binding.Command;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.StringObservable;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.aofeng.safecheck.activity.RepairActivity;
import com.aofeng.safecheck.modelview.MySecurityModel;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

public class UnrepairedRowModel {
	
	Activity context;
	// 下发的安检单ID
	public StringObservable ID = new StringObservable("");
	// 地址
	public StringObservable ROAD = new StringObservable("");
	public StringObservable UNIT_NAME = new StringObservable("");
	public StringObservable CUS_DOM = new StringObservable("");
	public StringObservable CUS_DY = new StringObservable("");
	public StringObservable CUS_FLOOR = new StringObservable("");
	public StringObservable CUS_ROOM = new StringObservable("");
	public String CHECKPLAN_ID;

	public UnrepairedRowModel(Activity context, String id) {
		this.context = context;
		ID.set(id);
	}

	/**
	 * 进入维修主界面
	 */

		public Command GoMending = new Command() {
			@Override
			public void Invoke(View view, Object... args) {
				Util.ClearCache(context, Util.getSharedPreference(context, Vault.USER_ID) + "_" + ID.get());
				Intent intent = new Intent();
				// 利用包袱传递参数给Activity
				Bundle bundle = new Bundle();

				bundle.putString("ID", ID.get());
				bundle.putString("CHECKPLAN_ID", CHECKPLAN_ID);
				bundle.putString("CUS_FLOOR", CUS_FLOOR.get());
				bundle.putString("CUS_ROOM", CUS_ROOM.get());
				bundle.putString("CUS_DY", CUS_DY.get());
				bundle.putString("ROAD", ROAD.get());
				bundle.putString("UNIT_NAME", UNIT_NAME.get());
				bundle.putString("CUS_DOM", CUS_DOM.get());
				bundle.putBoolean("INSPECTED", true);
				bundle.putBoolean("READONLY", true);

				intent.setClass(context, RepairActivity.class);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		};
}
