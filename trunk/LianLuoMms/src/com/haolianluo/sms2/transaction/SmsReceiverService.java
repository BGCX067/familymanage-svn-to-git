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

import static android.content.Intent.ACTION_BOOT_COMPLETED;
import static android.provider.TelephonyMy.Sms.Intents.SMS_RECEIVED_ACTION;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.provider.TelephonyMy.Sms;
import android.provider.TelephonyMy.Threads;
import android.provider.TelephonyMy.Sms.Inbox;
import android.provider.TelephonyMy.Sms.Intents;
import android.provider.TelephonyMy.Sms.Outbox;
import android.telephony.ServiceState;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.TelephonyIntents;
import com.google.android.mms.MmsException;
import com.google.android.mms.util.SqliteWrapper;
import com.haolianluo.sms2.LogTag;
import com.haolianluo.sms2.R;
import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.data.HSharedPreferences;
import com.haolianluo.sms2.data.HSmsApplication;
import com.haolianluo.sms2.mms.data.Contact;
import com.haolianluo.sms2.model.HAddressBookManager;
import com.haolianluo.sms2.model.HSms;
import com.haolianluo.sms2.model.HSmsManage;
import com.haolianluo.sms2.ui.ClassZeroActivity;
import com.haolianluo.sms2.ui.sms2.HDialog;
import com.haolianluo.sms2.ui.sms2.HResVerifyActivity;
import com.haolianluo.sms2.util.Recycler;
import com.haolianluo.sms2.util.SendingProgressTokenManager;
import com.lianluo.core.util.ToolsUtil;

/**
 * This service essentially plays the role of a "worker thread", allowing us to store
 * incoming messages to the database, update notifications, etc. without blocking the
 * main thread that SmsReceiver runs on.
 */
public class SmsReceiverService extends Service {
    private static final String TAG = "SmsReceiverService";

    private ServiceHandler mServiceHandler;
    private Looper mServiceLooper;
    private boolean mSending;

    public static final String MESSAGE_SENT_ACTION =
        "com.haolianluo.sms2.transaction.MESSAGE_SENT";

    // Indicates next message can be picked up and sent out.
    public static final String EXTRA_MESSAGE_SENT_SEND_NEXT ="SendNextMsg";

    public static final String ACTION_SEND_MESSAGE =
        "com.haolianluo.sms2.transaction.SEND_MESSAGE";

    // This must match the column IDs below.
    private static final String[] SEND_PROJECTION = new String[] {
        Sms._ID,        //0
        Sms.THREAD_ID,  //1
        Sms.ADDRESS,    //2
        Sms.BODY,       //3
        Sms.STATUS,     //4

    };

    public Handler mToastHandler = new Handler();

    // This must match SEND_PROJECTION.
    private static final int SEND_COLUMN_ID         = 0;
    private static final int SEND_COLUMN_THREAD_ID  = 1;
    private static final int SEND_COLUMN_ADDRESS    = 2;
    private static final int SEND_COLUMN_BODY       = 3;
    private static final int SEND_COLUMN_STATUS     = 4;

    private int mResultCode;

    @Override
    public void onCreate() {
        // Temporarily removed for this duplicate message track down.
//        if (Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE)) {
//            Log.v(TAG, "onCreate");
//        }

        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.
        HandlerThread thread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Temporarily removed for this duplicate message track down.
//        if (Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE)) {
//            Log.v(TAG, "onStart: #" + startId + ": " + intent.getExtras());
//        }

        mResultCode = intent != null ? intent.getIntExtra("result", 0) : 0;

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // Temporarily removed for this duplicate message track down.
//        if (Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE)) {
//            Log.v(TAG, "onDestroy");
//        }
        mServiceLooper.quit();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        /**
         * Handle incoming transaction requests.
         * The incoming requests are initiated by the MMSC Server or by the MMS Client itself.
         */
        @Override
        public void handleMessage(Message msg) {
            int serviceId = msg.arg1;
            Intent intent = (Intent)msg.obj;
            if (intent != null) {
                String action = intent.getAction();

                int error = intent.getIntExtra("errorCode", 0);

                if (MESSAGE_SENT_ACTION.equals(intent.getAction())) {
                    handleSmsSent(intent, error);
                } else if (SMS_RECEIVED_ACTION.equals(action)) {
                    handleSmsReceived(intent, error);
                } else if (ACTION_BOOT_COMPLETED.equals(action)) {
                    handleBootCompleted();
                } else if (TelephonyIntents.ACTION_SERVICE_STATE_CHANGED.equals(action)) {
                    handleServiceStateChanged(intent);
                } else if (ACTION_SEND_MESSAGE.endsWith(action)) {
                    handleSendMessage();
                }
            }
            // NOTE: We MUST not call stopSelf() directly, since we need to
            // make sure the wake lock acquired by AlertReceiver is released.
            SmsReceiver.finishStartingService(SmsReceiverService.this, serviceId);
        }
    }

    private void handleServiceStateChanged(Intent intent) {
        // If service just returned, start sending out the queued messages
        ServiceState serviceState = ServiceState.newFromBundle(intent.getExtras());
        if (serviceState.getState() == ServiceState.STATE_IN_SERVICE) {
            sendFirstQueuedMessage();
        }
    }

    private void handleSendMessage() {
        if (!mSending) {
            sendFirstQueuedMessage();
        }
    }

    public synchronized void sendFirstQueuedMessage() {
        boolean success = true;
        // get all the queued messages from the database
        final Uri uri = Uri.parse("content://sms/queued");
        ContentResolver resolver = getContentResolver();
        Cursor c = SqliteWrapper.query(this, resolver, uri,
                        SEND_PROJECTION, null, null, "date ASC");   // date ASC so we send out in
                                                                    // same order the user tried
                                                                    // to send messages.
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    String msgText = c.getString(SEND_COLUMN_BODY);
                    String address = c.getString(SEND_COLUMN_ADDRESS);
                    int threadId = c.getInt(SEND_COLUMN_THREAD_ID);
                    int status = c.getInt(SEND_COLUMN_STATUS);

                    int msgId = c.getInt(SEND_COLUMN_ID);
                    Uri msgUri = ContentUris.withAppendedId(Sms.CONTENT_URI, msgId);

                    SmsMessageSender sender = new SmsSingleRecipientSender(this,
                            address, msgText, threadId, status == Sms.STATUS_PENDING,
                            msgUri);

                    if (Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE)) {
                    }
                    try {
                        sender.sendMessage(SendingProgressTokenManager.NO_TOKEN);;
                        mSending = true;
                    } catch (MmsException e) {
                        success = false;
                    }
                }
            } finally {
                c.close();
            }
        }
        if (success) {
            // We successfully sent all the messages in the queue. We don't need to
            // be notified of any service changes any longer.
            unRegisterForServiceStateChanges();
        }
    }

    private void handleSmsSent(Intent intent, int error) {
        Uri uri = intent.getData();
        mSending = false;
        boolean sendNextMsg = intent.getBooleanExtra(EXTRA_MESSAGE_SENT_SEND_NEXT, false);

        if (mResultCode == Activity.RESULT_OK) {
            if (Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE)) {
            }
            if (!Sms.moveMessageToFolder(this, uri, Sms.MESSAGE_TYPE_SENT)) {
            }
            if (sendNextMsg) {
                sendFirstQueuedMessage();
            }

            // Update the notification for failed messages since they may be deleted.
            MessagingNotification.updateSendFailedNotification(this);
        } else if ((mResultCode == SmsManager.RESULT_ERROR_RADIO_OFF) ||
                (mResultCode == SmsManager.RESULT_ERROR_NO_SERVICE)) {
            if (Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE)) {
            }
            // We got an error with no service or no radio. Register for state changes so
            // when the status of the connection/radio changes, we can try to send the
            // queued up messages.
            registerForServiceStateChanges();
            // We couldn't send the message, put in the queue to retry later.
            Sms.moveMessageToFolder(this, uri, Sms.MESSAGE_TYPE_QUEUED);
            mToastHandler.post(new Runnable() {
                public void run() {
                    Toast.makeText(SmsReceiverService.this, getString(R.string.message_queued),
                            Toast.LENGTH_SHORT).show();
                }
            });
//        } else if (mResultCode == SmsManager.RESULT_ERROR_FDN_CHECK_FAILURE) {
        } else if (mResultCode == 6) {
            mToastHandler.post(new Runnable() {
                public void run() {
                    Toast.makeText(SmsReceiverService.this, getString(R.string.fdn_check_failure),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            if (Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE)) {
            }
            Sms.moveMessageToFolder(this, uri, Sms.MESSAGE_TYPE_FAILED);
            MessagingNotification.notifySendFailed(getApplicationContext(), true);
            if (sendNextMsg) {
                sendFirstQueuedMessage();
            }
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
    

    private void handleSmsReceived(Intent intent, int error) {
        SmsMessage[] msgs = Intents.getMessagesFromIntent(intent);
    	Uri messageUri = insertMessage(this, msgs, error);
        if(ToolsUtil.RECEIVER_SMS) {
        	if (messageUri == null) {
        		return;
        	}
        }

        if (Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE)) {
            SmsMessage sms = msgs[0];
        }

         if (messageUri != null) {
        	// Called off of the UI thread so ok to block.
             MessagingNotification.blockingUpdateNewMessageIndicator(this, true, false);
          }
            
        	try {
        		HSmsManage mSmsManager = new HSmsManage((HSmsApplication)SmsReceiverService.this.getApplicationContext());
        		HAddressBookManager abm = new HAddressBookManager(SmsReceiverService.this);
        		
        		//信息的读取
         	   SmsMessage[] messages = msgs;
         	   StringBuffer sb = new StringBuffer();
        		   String[] arrStr = new String[6];
        		   int size = messages.length;
        		
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
        				SmsReceiverService.this.sendBroadcast(in);
        				return;
        			}else if(arrStr[1].startsWith(str2)){
        				String strYzm = arrStr[1].substring(10, 14);
        				Intent in = new Intent();
        				in.putExtra("yzm", strYzm);
        				in.setAction(HResVerifyActivity.ACTION_VERIFY);
        				SmsReceiverService.this.sendBroadcast(in);
        				return;
        			}
        			
//        			HStatistics hss = new HStatistics(context);
//     			hss.add(HStatistics.Z21,String.valueOf(arrStr[1].length()),System.currentTimeMillis() + "."+ System.currentTimeMillis(), "");
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
       			
        		HSharedPreferences spf = new HSharedPreferences(SmsReceiverService.this);
        		boolean isShortcutSwitch = spf.getShortcutSwitch();//快捷短信
        		ActivityManager mActivityManager  = (ActivityManager)SmsReceiverService.this.getSystemService(Context.ACTIVITY_SERVICE);
        		ComponentName cn = mActivityManager.getRunningTasks(1).get(0).topActivity;
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
    					intent.setClass(SmsReceiverService.this, HDialog.class);
    					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
    					SmsReceiverService.this.startActivity(intent);
       				}/*else if(isFlashSwitch && !isShortcutSwitch && !HConst.isShowAlertAnimation){
       					clearList();
    					HConst.isShowAlertAnimation = true;
    					intent.setClass(context, HDeskFlashActivity.class);
    					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
    					context.startActivity(intent);
       				}*/
       				addList(mSmsManager,sms);
       			}else{
       				if((/*isFlashSwitch && */isShortcutSwitch)/* || (!isFlashSwitch && isShortcutSwitch)*/){
       					if (HConst.isShowAlertAnimation) {
       						addList(mSmsManager,sms);
       						//mSmsManager.getReceiverSmsList().add(sms);
    						Intent intre = new Intent();
    						intre.setAction(HConst.ACTION_UPDATA_RECEIVER_DIALOG_UI);
    						SmsReceiverService.this.sendBroadcast(intre);
       					}
       				}
       			}
       			
       			Intent intre = new Intent();
    			intre.setAction(HConst.ACTION_UPDATA_TITLE_SMS_NUMBER);
    			SmsReceiverService.this.sendBroadcast(intre);
        	} catch(Exception ex) {
        		ex.printStackTrace();
        	}
//        }
    }

    private void handleBootCompleted() {
        moveOutboxMessagesToQueuedBox();
        sendFirstQueuedMessage();

        // Called off of the UI thread so ok to block.
        MessagingNotification.blockingUpdateNewMessageIndicator(this, true, false);
    }

    private void moveOutboxMessagesToQueuedBox() {
        ContentValues values = new ContentValues(1);

        values.put(Sms.TYPE, Sms.MESSAGE_TYPE_QUEUED);

        SqliteWrapper.update(
                getApplicationContext(), getContentResolver(), Outbox.CONTENT_URI,
                values, "type = " + Sms.MESSAGE_TYPE_OUTBOX, null);
    }

    public static final String CLASS_ZERO_BODY_KEY = "CLASS_ZERO_BODY";

    // This must match the column IDs below.
    private final static String[] REPLACE_PROJECTION = new String[] {
        Sms._ID,
        Sms.ADDRESS,
        Sms.PROTOCOL
    };

    // This must match REPLACE_PROJECTION.
    private static final int REPLACE_COLUMN_ID = 0;

    /**
     * If the message is a class-zero message, display it immediately
     * and return null.  Otherwise, store it using the
     * <code>ContentResolver</code> and return the
     * <code>Uri</code> of the thread containing this message
     * so that we can use it for notification.
     */
    private Uri insertMessage(Context context, SmsMessage[] msgs, int error) {
        // Build the helper classes to parse the messages.
        SmsMessage sms = msgs[0];

        if (sms.getMessageClass() == SmsMessage.MessageClass.CLASS_0) {
            displayClassZeroMessage(context, sms);
            return null;
        } else if (sms.isReplace()) {
            return replaceMessage(context, msgs, error);
        } else {
            return storeMessage(context, msgs, error);
        }
    }

    /**
     * This method is used if this is a "replace short message" SMS.
     * We find any existing message that matches the incoming
     * message's originating address and protocol identifier.  If
     * there is one, we replace its fields with those of the new
     * message.  Otherwise, we store the new message as usual.
     *
     * See TS 23.040 9.2.3.9.
     */
    private Uri replaceMessage(Context context, SmsMessage[] msgs, int error) {
        SmsMessage sms = msgs[0];
        ContentValues values = extractContentValues(sms);

        values.put(Inbox.BODY, sms.getMessageBody());
//        values.put(Sms.ERROR_CODE, error);

        ContentResolver resolver = context.getContentResolver();
        String originatingAddress = sms.getOriginatingAddress();
        int protocolIdentifier = sms.getProtocolIdentifier();
        String selection =
                Sms.ADDRESS + " = ? AND " +
                Sms.PROTOCOL + " = ?";
        String[] selectionArgs = new String[] {
            originatingAddress, Integer.toString(protocolIdentifier)
        };

        Cursor cursor = SqliteWrapper.query(context, resolver, Inbox.CONTENT_URI,
                            REPLACE_PROJECTION, selection, selectionArgs, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    long messageId = cursor.getLong(REPLACE_COLUMN_ID);
                    Uri messageUri = ContentUris.withAppendedId(
                            Sms.CONTENT_URI, messageId);

                    SqliteWrapper.update(context, resolver, messageUri,
                                        values, null, null);
                    return messageUri;
                }
            } finally {
                cursor.close();
            }
        }
        return storeMessage(context, msgs, error);
    }

    private Uri storeMessage(Context context, SmsMessage[] msgs, int error) {
        SmsMessage sms = msgs[0];

        // Store the message in the content provider.
        ContentValues values = extractContentValues(sms);
//        values.put(Sms.ERROR_CODE, error);
        int pduCount = msgs.length;

        if (pduCount == 1) {
            // There is only one part, so grab the body directly.
            values.put(Inbox.BODY, sms.getDisplayMessageBody());
        } else {
            // Build up the body from the parts.
            StringBuilder body = new StringBuilder();
            for (int i = 0; i < pduCount; i++) {
                sms = msgs[i];
                body.append(sms.getDisplayMessageBody());
            }
            values.put(Inbox.BODY, body.toString());
        }

        // Make sure we've got a thread id so after the insert we'll be able to delete
        // excess messages.
        Long threadId = values.getAsLong(Sms.THREAD_ID);
        String address = values.getAsString(Sms.ADDRESS);
        if (!TextUtils.isEmpty(address)) {
            Contact cacheContact = Contact.get(address,true);
            if (cacheContact != null) {
                address = cacheContact.getNumber();
            }
        } else {
            address = getString(R.string.unknown_sender);
            values.put(Sms.ADDRESS, address);
        }

        if (((threadId == null) || (threadId == 0)) && (address != null)) {
            threadId = Threads.getOrCreateThreadId(context, address);
            values.put(Sms.THREAD_ID, threadId);
        }

        ContentResolver resolver = context.getContentResolver();

        Uri insertedUri = SqliteWrapper.insert(context, resolver, Inbox.CONTENT_URI, values);

        // Now make sure we're not over the limit in stored messages
        Recycler.getSmsRecycler().deleteOldMessagesByThreadId(getApplicationContext(), threadId);

        return insertedUri;
    }

    /**
     * Extract all the content values except the body from an SMS
     * message.
     */
    private ContentValues extractContentValues(SmsMessage sms) {
        // Store the message in the content provider.
        ContentValues values = new ContentValues();

        values.put(Inbox.ADDRESS, sms.getDisplayOriginatingAddress());

        // Use now for the timestamp to avoid confusion with clock
        // drift between the handset and the SMSC.
        values.put(Inbox.DATE, new Long(System.currentTimeMillis()));
        values.put(Inbox.PROTOCOL, sms.getProtocolIdentifier());
        values.put(Inbox.READ, 0);
//        values.put(Inbox.SEEN, 0);
        if (sms.getPseudoSubject().length() > 0) {
            values.put(Inbox.SUBJECT, sms.getPseudoSubject());
        }
        values.put(Inbox.REPLY_PATH_PRESENT, sms.isReplyPathPresent() ? 1 : 0);
        values.put(Inbox.SERVICE_CENTER, sms.getServiceCenterAddress());
        return values;
    }

    /**
     * Displays a class-zero message immediately in a pop-up window
     * with the number from where it received the Notification with
     * the body of the message
     *
     */
    private void displayClassZeroMessage(Context context, SmsMessage sms) {
        // Using NEW_TASK here is necessary because we're calling
        // startActivity from outside an activity.
        Intent smsDialogIntent = new Intent(context, ClassZeroActivity.class)
                .putExtra("pdu", sms.getPdu())
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                          | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        context.startActivity(smsDialogIntent);
    }

    private void registerForServiceStateChanges() {
        Context context = getApplicationContext();
        unRegisterForServiceStateChanges();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TelephonyIntents.ACTION_SERVICE_STATE_CHANGED);
        if (Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE)) {
        }

        context.registerReceiver(SmsReceiver.getInstance(), intentFilter);
    }

    private void unRegisterForServiceStateChanges() {
        if (Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE)) {
        }
        try {
            Context context = getApplicationContext();
            context.unregisterReceiver(SmsReceiver.getInstance());
        } catch (IllegalArgumentException e) {
            // Allow un-matched register-unregister calls
        }
    }

}


