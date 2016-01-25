package com.aofeng.safecheck.activity;

import gueei.binding.Binder;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.modelview.RepairQueryModel;
import android.app.Activity;
import android.os.Bundle;

public class RepairQueryActivity extends Activity{
	RepairQueryModel model;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		model = new RepairQueryModel(this);
		Binder.setAndBindContentView(this, R.layout.repair_query, model);		
	}


	
}