package com.aofeng.safecheck;

import android.app.TabActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;

import com.aofeng.safecheck.model.TaskEntryModel;

public class TaskEntryActivity extends TabActivity{
	TaskEntryModel vm;
	@Override
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        //LayoutInflater.from(this).inflate(R.layout.startwork, tabHost.getTabContentView(), true);        
        setContentView(R.layout.startwork);
        TabHost tabHost = getTabHost();
        View view1 = getLayoutInflater().inflate(R.layout.startwork_tasks, null);
        View view2 = getLayoutInflater().inflate(R.layout.startwork_tasks, null); 
        View view3 = getLayoutInflater().inflate(R.layout.startwork_tasks, null);  
       tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator(view1).setContent(R.id.tab1));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator(view2).setContent(R.id.tab2));
       tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator(view3).setContent(R.id.tab3));
        tabHost.setCurrentTab(0);
           }  
}