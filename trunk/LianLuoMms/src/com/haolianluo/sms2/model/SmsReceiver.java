package com.haolianluo.sms2.model;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import static com.haolianluo.sms2.model.SmsSendService.ACTION_SMS_SEND;
import static com.haolianluo.sms2.model.SmsSendService.ACTION_SMS_DELIVERY;
import static com.haolianluo.sms2.model.SmsSendService.ACTION_SMS_RECEIVER;

public class SmsReceiver extends BroadcastReceiver {
	
	private String typeFail = "5";
	static final String Tag = "lianluosms";
	ArrayList<String> arrayList = new ArrayList<String>();
	@Override
	public void onReceive(Context context, Intent intent) {
		String actionName = intent.getAction();
		int resultCode = getResultCode();
		if (actionName.equals(ACTION_SMS_SEND)) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				upData(context, intent.getStringExtra("smsId"), "2");
				return;
			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
			case SmsManager.RESULT_ERROR_NO_SERVICE://无卡、无信号
			case SmsManager.RESULT_ERROR_NULL_PDU:
			case SmsManager.RESULT_ERROR_RADIO_OFF://飞行模式
				//发送失败
				upData(context, intent.getStringExtra("smsId"), typeFail);
				break;
			case 133404:
				//多次尝试无法发送
				upData(context, intent.getStringExtra("smsId"), typeFail);
				break;
			case 2500:
				upData(context, intent.getStringExtra("smsId"), typeFail);
				break;
			}
		} else if (actionName.equals(ACTION_SMS_DELIVERY)) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				return;
			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
			case SmsManager.RESULT_ERROR_NO_SERVICE://无卡、无信号
			case SmsManager.RESULT_ERROR_NULL_PDU:
			case SmsManager.RESULT_ERROR_RADIO_OFF://飞行模式
				//发送失败
//				upData(context, intent.getStringExtra("smsId"), typeFail);
				break;
			case 133404:
				//多次尝试无法发送
//				upData(context, intent.getStringExtra("smsId"), typeFail);
				break;
			}
		} else if (actionName.equals(ACTION_SMS_RECEIVER)) {
			
		}
	}
	
	
	private void upData(Context context,String smsid,String type){
		  HSmsManage sms = new HSmsManage((Application)context.getApplicationContext());
		  sms.updataSystemDB(context, smsid, type);
	}
}
