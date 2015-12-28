package com.haolianluo.sms2.model;

import com.haolianluo.sms2.HDialog;
import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.data.HSharedPreferences;
import com.haolianluo.sms2.data.HSmsApplication;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class HMmsDownloadFinish extends BroadcastReceiver {
	
	private HSmsManage mSmsManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		try{
			if(HConst.isDeleteMMS){
				HConst.isDeleteMMS = false;
				return;
			}
			
			Uri changed = (Uri) intent.getParcelableExtra("deleted_contents");
			String str = changed.getPath();
			String _id = String.valueOf(Integer.parseInt(str.split("/")[2]) + 1);
			
			mSmsManager = new HSmsManage((HSmsApplication)context.getApplicationContext());
			ActivityManager activityManager  = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
			ComponentName cn = activityManager.getRunningTasks(1).get(0).topActivity;
			String strName = cn.getClassName().substring(0,18);
			HSharedPreferences spf = new HSharedPreferences(context);
			//boolean isFlashSwitch = spf.getFlashSwitch();//提醒动画
			boolean isShortcutSwitch = spf.getShortcutSwitch();//快捷短信
			
			HSms sms = mSmsManager.getMMS(_id);
			mSmsManager.updataList(sms,false,false,Integer.parseInt(sms.smsid),false);
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
   				}/*else if(isFlashSwitch && !isShortcutSwitch && !HConst.isShowAlertAnimation){
   					clearList();
					HConst.isShowAlertAnimation = true;
					intent.setClass(context, HDeskFlashActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
					context.startActivity(intent);
   				}*/
   				sms.read = "1";
   				mSmsManager.getReceiverSmsList().add(sms);
   			}else{
   				if((/*isFlashSwitch && */isShortcutSwitch)/* || (!isFlashSwitch && isShortcutSwitch)*/){
   					if (HConst.isShowAlertAnimation) {
   						sms.read = "0";
   						mSmsManager.getReceiverSmsList().add(sms);
						Intent intre = new Intent();
						intre.setAction(HConst.ACTION_UPDATA_RECEIVER_DIALOG_UI);
						context.sendBroadcast(intre);
   					}
   				}
   			}

		}catch(Exception ex){
		}
	}
	
	
	  private void clearList(){
	    	if(mSmsManager.getReceiverSmsList().size() > 0){
	    		mSmsManager.getReceiverSmsList().clear();
	    	}
	    }

}
