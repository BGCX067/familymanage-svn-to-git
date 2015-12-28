package com.haolianluo.sms2.model;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/***
 * 2011年12月5日21:18:34
 * @author jianhua
 * mms的收取
 */

public class HMMSReceiver extends BroadcastReceiver{
	
	private final String MMS_RECEIVED_ACTION = "android.provider.Telephony.WAP_PUSH_RECEIVED";

	@Override
	public void onReceive(Context context, Intent intent) {
		 if (intent.getAction().equals(MMS_RECEIVED_ACTION)) {
//			 byte[] pushData = intent.getByteArrayExtra("data");
//			 HLog.i("------------>>>" + pushData.length);
//			 PduParser parser = new PduParser(pushData);
//	         GenericPdu pdu = parser.parse();
//			try {
//				FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() + "/zhaojianhua");
//				fos.write(pushData);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		 }
	}

}
