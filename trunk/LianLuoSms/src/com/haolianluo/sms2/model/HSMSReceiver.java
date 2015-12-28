package com.haolianluo.sms2.model;



import com.haolianluo.sms2.HDialog;
import com.haolianluo.sms2.HResVerifyActivity;
import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.data.HSharedPreferences;
import com.haolianluo.sms2.data.HSmsApplication;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

/**
 * 收到信息的处理
 * @author jianhua 2011年11月21日15:57:11
 *
 */

public class HSMSReceiver extends BroadcastReceiver {
	
	private final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	private HSmsManage mSmsManager;
	private ActivityManager mActivityManager = null;
	
    public void onReceive(Context context, Intent intent) {
       if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
  			abortBroadcast();
    	   if(mSmsManager == null){
    		   mSmsManager = new HSmsManage((HSmsApplication)context.getApplicationContext());
    	   }
    	   if(mActivityManager == null){
    		   mActivityManager  = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
    	   }
    	   HAddressBookManager abm = new HAddressBookManager(context);
    	   HSharedPreferences spf = new HSharedPreferences(context);
    	   //信息的读取
    	   SmsMessage[] messages = getMessagesFromIntent(intent);
    	   StringBuffer sb = new StringBuffer();
   		   String[] arrStr = new String[6];
   		   int size = messages.length;
   		if (messages != null && size != 0) {
   			try{
   				for (int i = 0; i < size; i++) {
   	   				if (messages[i] != null) {
   						arrStr[0] = messages[i].getOriginatingAddress();
   						if (arrStr[0].startsWith("+86")) {
   							arrStr[0] = arrStr[0].substring(3, arrStr[0].length());
   						} 
   						arrStr[1] = sb.append(messages[i].getDisplayMessageBody()).toString();
   						arrStr[2] = String.valueOf(System.currentTimeMillis());
   						arrStr[3] = "1";
   						arrStr[4] = "0";
   						if (i == 0) {
   							arrStr[5] = mSmsManager.getThreadIdForAndress(arrStr[0].split(","));
   						}
   					}
   	   			}
   	   			
   			}catch(Exception ex){//me811有问题----->>
   			}
   			
   			String str1 = "联络短信验证码：";
   			String str2 = "联络短信注册验证码：";
   			if(arrStr[1].startsWith(str1)){
   				String strYzm = arrStr[1].substring(8, 12);
   				Intent in = new Intent();
   				in.putExtra("yzm", strYzm);
   				in.setAction(HResVerifyActivity.ACTION_VERIFY);
   				context.sendBroadcast(in);
   				return;
   			}else if(arrStr[1].startsWith(str2)){
   				String strYzm = arrStr[1].substring(10, 14);
   				Intent in = new Intent();
   				in.putExtra("yzm", strYzm);
   				in.setAction(HResVerifyActivity.ACTION_VERIFY);
   				context.sendBroadcast(in);
   				return;
   			}
   			
//   			HStatistics hss = new HStatistics(context);
//			hss.add(HStatistics.Z21,String.valueOf(arrStr[1].length()),System.currentTimeMillis() + "."+ System.currentTimeMillis(), "");
   			//生成一个sms
   			HSms sms = new HSms();
   			sms.address = arrStr[0];
   			sms.body = arrStr[1];
   			sms.ismms = "0";
   			sms.read =  arrStr[4];
   			sms.time = arrStr[2];
   			sms.type = arrStr[3];
   			sms.threadid = arrStr[5];
   			sms.name = abm.getNameByNumber(sms.address);
   			sms.smsid = String.valueOf(mSmsManager.getSmsMaxId() + 1);
   			if(!HConst.isReceiverSms)HConst.isReceiverSms = true;
   			//boolean isFlashSwitch = spf.getFlashSwitch();//提醒动画
   			boolean isShortcutSwitch = spf.getShortcutSwitch();//快捷短信
   			ComponentName cn = mActivityManager.getRunningTasks(1).get(0).topActivity;
   			String strName = cn.getClassName().substring(0,18);
   			
   			if(!strName.equals("com.haolianluo.sms") && !HConst.isShowAlertAnimation){
   				/*if(isFlashSwitch && isShortcutSwitch){
   					clearList();
   					HConst.isShowAlertAnimation = true;
   					intent.setClass(context, HDeskFlashActivity.class);
   					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
   					context.startActivity(intent);
   				}else */if(/*!isFlashSwitch && */isShortcutSwitch && !HConst.isShowAlertAnimation){
   					clearList();
					HConst.isShowAlertAnimation = true;
					intent.setClass(context, HDialog.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
					context.startActivity(intent);
					sms.read =  "1";
   				}/*else if(isFlashSwitch && !isShortcutSwitch && !HConst.isShowAlertAnimation){
   					clearList();
					HConst.isShowAlertAnimation = true;
					intent.setClass(context, HDeskFlashActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
					context.startActivity(intent);
   				}*/
   				mSmsManager.getReceiverSmsList().add(sms);
   			}else{
   				if((/*isFlashSwitch && */isShortcutSwitch)/* || (!isFlashSwitch && isShortcutSwitch)*/){
   					if (HConst.isShowAlertAnimation) {
   						sms.read =  "1";
   						mSmsManager.getReceiverSmsList().add(sms);
						Intent intre = new Intent();
						intre.setAction(HConst.ACTION_UPDATA_RECEIVER_DIALOG_UI);
						context.sendBroadcast(intre);
   					}
   				}
   			}
   			
   			Intent intre = new Intent();
			intre.setAction(HConst.ACTION_UPDATA_TITLE_SMS_NUMBER);
			context.sendBroadcast(intre);
			
			
			mSmsManager.updataList(sms,true,false,mSmsManager.getSmsMaxId() + 1,false);
   		
   		}
   		
   		
       }
    }
    
    private void clearList(){
    	if(mSmsManager.getReceiverSmsList().size() > 0){
    		mSmsManager.getReceiverSmsList().clear();
    	}
    }
    
    
    
    private SmsMessage[] getMessagesFromIntent(Intent intent) {
        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
        SmsMessage[] msgs = null;
        if(messages != null){
        byte[][] pduObjs = new byte[messages.length][];
        for (int i = 0; i < messages.length; i++){
            pduObjs[i] = (byte[]) messages[i];
        }
        byte[][] pdus = new byte[pduObjs.length][];
        int pduCount = pdus.length;
        msgs = new SmsMessage[pduCount];
        for (int i = 0; i < pduCount; i++){
            pdus[i] = pduObjs[i];
            msgs[i] = SmsMessage.createFromPdu(pdus[i]);
        }
    	}
        return msgs;
    }
    
    
}