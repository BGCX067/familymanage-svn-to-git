package com.haolianluo.sms2.model;

import com.lianluo.core.util.HLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/***
 * 开机启动Service
 * 
 * @author jianhua 2011年10月18日10:56:31
 * 
 */

public class HStartServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		HLog.i("启动service--------------->>>");
		Intent serviceIntent = new Intent();
		serviceIntent.setAction("com.haolianluo.sms2.HService");
		context.startService(serviceIntent);
	}
	

}
