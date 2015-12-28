package com.haolianluo.sms2.model;


import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.data.HSharedPreferences;
import com.haolianluo.sms2.data.HSmsApplication;
import com.lianluo.core.util.HLog;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;

/***
 * 监听数据库发生变化------->>>
 * @author jianhua 2011年11月18日10:50:44
 *
 */
public class HSmsChange extends ContentObserver {
	
	private ActivityManager mActivityManager = null;
	private HSmsApplication mSmsApplication;

	public HSmsChange(Handler handler,Application application) {
		super(handler);
		mSmsApplication = (HSmsApplication) application;
		mActivityManager = (ActivityManager)mSmsApplication.getSystemService(Context.ACTIVITY_SERVICE);
	}
	
	@Override
	public void onChange(boolean selfChange) {
		if(HConst.isReceiverSms){
			HConst.isReceiverSms = false;
			return;
		}
		ComponentName cn = mActivityManager.getRunningTasks(1).get(0).topActivity;
		HSharedPreferences sharedPreferences = new HSharedPreferences(mSmsApplication);
		String strName = cn.getClassName();
		HSms sms = new HSms();
		if(strName.length() > 18){
			strName = strName.substring(0, 18);
			if(!strName.equals("com.haolianluo.sms")){
				sms.notification_updata(mSmsApplication);
				sharedPreferences.setIsReadBuffer(false);
//				if(mSmsApplication.adapter != null){
//					mSmsApplication.adapter = null;
//				}
			}
		}else{
			sms.notification_updata(mSmsApplication);
			sharedPreferences.setIsReadBuffer(false);
//			if(mSmsApplication.adapter != null){
//				mSmsApplication.adapter = null;
//			}
		}
		super.onChange(selfChange);
	}
	
}
