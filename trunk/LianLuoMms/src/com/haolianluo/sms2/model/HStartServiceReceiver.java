package com.haolianluo.sms2.model;

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
		Intent serviceIntent = new Intent();
		serviceIntent.setAction("com.haolianluo.sms2.ui.sms2.HService");
		context.startService(serviceIntent);
	}
	

}
