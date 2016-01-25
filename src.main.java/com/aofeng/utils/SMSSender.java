package com.aofeng.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SMSSender {
	//保存的上下文
	private Context context;
	
	public SMSSender(Context context)
	{
		this.context = context;
	}
	
	/**
	 * 发送短信
	 * 
	 * @param phoneNumber
	 * @param message
	 */
	public void sendSMS(String phoneNumber, String message) {
	    String SENT = "SMS_SENT";
	    String DELIVERED = "SMS_DELIVERED";
	    
	    //已发送等待消解意图
	    PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);
	    //已送达等待消解意图
	    PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,  new Intent(DELIVERED), 0);

	    //已发送广播接收器
	    BroadcastReceiver sendSMS = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context arg0, Intent arg1) {
	            switch (getResultCode()) {
	            case Activity.RESULT_OK:
	                Toast.makeText(context, "短信已发送。", Toast.LENGTH_SHORT).show();
	                break;
	            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
	                Toast.makeText(context, "普通错误。", Toast.LENGTH_SHORT).show();
	                break;
	            case SmsManager.RESULT_ERROR_NO_SERVICE:
	                Toast.makeText(context, "服务不存在。", Toast.LENGTH_SHORT).show();
	                break;
	            case SmsManager.RESULT_ERROR_NULL_PDU:
	                Toast.makeText(context, "无PDU。", Toast.LENGTH_SHORT).show();
	                break;
	            case SmsManager.RESULT_ERROR_RADIO_OFF:
	                Toast.makeText(context, "无线被关闭。", Toast.LENGTH_SHORT).show();
	                break;
	            }
	        }
	    };

	    //送达广播接收器
	    BroadcastReceiver deliverSMS = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context arg0, Intent intent) {
	            switch (getResultCode()) {
	            case Activity.RESULT_OK:
	                Toast.makeText(context, "短信已被接收。", Toast.LENGTH_SHORT).show();
	                break;
	            case Activity.RESULT_CANCELED:
	                Toast.makeText(context, "短信发送失败。", Toast.LENGTH_SHORT).show();
	                break;
	            }
	        }
	    };

	    //注册收听短信已发送广播
	    context.registerReceiver(sendSMS, new IntentFilter(SENT));
	    //注册收听短信已送达广播
	    context.registerReceiver(deliverSMS, new IntentFilter(DELIVERED));

	    SmsManager sms = SmsManager.getDefault();
	    sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
	 }
}
