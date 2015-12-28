/*
 * Copyright (C) 2007-2008 Esmertec AG.
 * Copyright (C) 2007-2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haolianluo.sms2.transaction;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.provider.TelephonyMy.Mms;
import android.util.Log;

import com.android.internal.telephony.TelephonyIntents;
import com.google.android.mms.util.PduCache;
import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.data.HSharedPreferences;
import com.haolianluo.sms2.data.HSmsApplication;
import com.haolianluo.sms2.model.HSms;
import com.haolianluo.sms2.model.HSmsManage;
import com.haolianluo.sms2.LogTag;
import com.haolianluo.sms2.ui.sms2.HDialog;

/**
 * MmsSystemEventReceiver receives the
 * {@link android.content.intent.ACTION_BOOT_COMPLETED},
 * {@link com.android.internal.telephony.TelephonyIntents.ACTION_ANY_DATA_CONNECTION_STATE_CHANGED}
 * and performs a series of operations which may include:
 * <ul>
 * <li>Show/hide the icon in notification area which is used to indicate
 * whether there is new incoming message.</li>
 * <li>Resend the MM's in the outbox.</li>
 * </ul>
 */
public class MmsSystemEventReceiver extends BroadcastReceiver {
    private static final String TAG = "MmsSystemEventReceiver";
    private static MmsSystemEventReceiver sMmsSystemEventReceiver;

    private static void wakeUpService(Context context) {
        if (Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE)) {
        }

        context.startService(new Intent(context, TransactionService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE)) {
        }

        String action = intent.getAction();
        if (action.equals(Mms.Intents.CONTENT_CHANGED_ACTION)) {
            Uri changed = (Uri) intent.getParcelableExtra(Mms.Intents.DELETED_CONTENTS);
            PduCache.getInstance().purge(changed);
            
            try {
            	HSharedPreferences spf = new HSharedPreferences(context);
            	MessagingNotification.blockingUpdateNewMessageIndicator(context, true, false);
            	
            	
            	String str = changed.getPath();
        		String _id = String.valueOf(Integer.parseInt(str.split("/")[2]) + 1);
    			
    			HSmsManage mSmsManager = new HSmsManage((HSmsApplication)context.getApplicationContext());
            	ActivityManager activityManager  = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            	ComponentName cn = activityManager.getRunningTasks(1).get(0).topActivity;
    			
            	//boolean isFlashSwitch = spf.getFlashSwitch();//提醒动画
    			boolean isShortcutSwitch = spf.getShortcutSwitch();//快捷短信
    			
    			HSms sms = mSmsManager.getMMS(_id);
    			
    			String strName = cn.getClassName().substring(0,"com.haolianluo.sms2".length());
    			if(!strName.equals("com.haolianluo.sms2") && !HConst.isShowAlertAnimation){
       				/*if(isFlashSwitch && isShortcutSwitch){
       					clearList();
       					HConst.isShowAlertAnimation = true;
       					intent.setClass(context, HDeskFlashActivity.class);
       					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
       					context.startActivity(intent);
       				}else */if(/*!isFlashSwitch && */isShortcutSwitch && !HConst.isShowAlertAnimation){
       					if(mSmsManager.getReceiverSmsList().size() > 0){
       			    		mSmsManager.getReceiverSmsList().clear();
       			    	}
       					
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
       				addList(mSmsManager,sms);
       				//mSmsManager.getReceiverSmsList().add(sms);
       			}else{
       				if((/*isFlashSwitch && */isShortcutSwitch)/* || (!isFlashSwitch && isShortcutSwitch)*/){
       					if (HConst.isShowAlertAnimation) {
       						//sms.read = "0";
       						addList(mSmsManager,sms);
       						//mSmsManager.getReceiverSmsList().add(sms);
    						Intent intre = new Intent();
    						intre.setAction(HConst.ACTION_UPDATA_RECEIVER_DIALOG_UI);
    						context.sendBroadcast(intre);
       					}
       				}
       			}
            } catch(Exception ex) {
            }
        } else if (action.equals(TelephonyIntents.ACTION_ANY_DATA_CONNECTION_STATE_CHANGED)) {
            String state = intent.getStringExtra("state");

            if (Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE)) {
            }

            if (state.equals("CONNECTED")) {
                wakeUpService(context);
            }
        } else if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            // We should check whether there are unread incoming
            // messages in the Inbox and then update the notification icon.
            // Called on the UI thread so don't block.
            MessagingNotification.nonBlockingUpdateNewMessageIndicator(context, false, false);
        }
    }
    
    
    
    private void addList(HSmsManage mSmsManager,HSms sms){
    	String smsID = sms.smsid;
    	if(mSmsManager.getReceiverSmsList().size() == 0){
    		mSmsManager.getReceiverSmsList().add(sms);
    		return;
    	}
    	boolean flag = false;
    	for(int i = 0;i < mSmsManager.getReceiverSmsList().size();i++){
    		if(mSmsManager.getReceiverSmsList() != null)
        	{
        		if(mSmsManager.getReceiverSmsList().get(i) != null)
            	{
        			if(mSmsManager.getReceiverSmsList().get(i).smsid != null)
                	{
    	    	if(mSmsManager.getReceiverSmsList().get(i).smsid.equals(smsID)){
    	    			flag = true;
    	    			break;
    	    		}
                	}
    	    	}
        	}
    		
    	}
    	if(!flag) {
    		mSmsManager.getReceiverSmsList().add(sms);
    	}
    }

    public static void registerForConnectionStateChanges(Context context) {
        unRegisterForConnectionStateChanges(context);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TelephonyIntents.ACTION_ANY_DATA_CONNECTION_STATE_CHANGED);
        if (Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE)) {
        }
        if (sMmsSystemEventReceiver == null) {
            sMmsSystemEventReceiver = new MmsSystemEventReceiver();
        }

        context.registerReceiver(sMmsSystemEventReceiver, intentFilter);
    }

    public static void unRegisterForConnectionStateChanges(Context context) {
        if (Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE)) {
        }
        if (sMmsSystemEventReceiver != null) {
            try {
                context.unregisterReceiver(sMmsSystemEventReceiver);
            } catch (IllegalArgumentException e) {
                // Allow un-matched register-unregister calls
            }
        }
    }
}
