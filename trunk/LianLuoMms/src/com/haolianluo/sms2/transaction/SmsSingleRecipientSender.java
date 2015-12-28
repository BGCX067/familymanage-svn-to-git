package com.haolianluo.sms2.transaction;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.TelephonyMy.Mms;
import android.provider.TelephonyMy.Sms;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.android.mms.MmsException;
import com.google.android.mms.util.SqliteWrapper;
import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.model.SmsSendService;
import com.lianluo.core.util.ToolsUtil;
import com.haolianluo.sms2.LogTag;
import com.haolianluo.sms2.MmsConfig;
import com.haolianluo.sms2.ui.MessageUtils;

public class SmsSingleRecipientSender extends SmsMessageSender {

    private final boolean mRequestDeliveryReport;
    private String mDest;
    private Uri mUri;
    private Context mContext;

    public SmsSingleRecipientSender(Context context, String dest, String msgText, long threadId,
            boolean requestDeliveryReport, Uri uri) {
        super(context, null, msgText, threadId);
        this.mContext = context;
        mRequestDeliveryReport = requestDeliveryReport;
        mDest = dest;
        mUri = uri;
    }

    public boolean sendMessage(long token) throws MmsException {
        if (mMessageText == null) {
            // Don't try to send an empty message, and destination should be just
            // one.
            throw new MmsException("Null message body or have multiple destinations.");
        }
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> messages = null;
        if ((MmsConfig.getEmailGateway() != null) &&
                (Mms.isEmailAddress(mDest) || MessageUtils.isAlias(mDest))) {
            String msgText;
            msgText = mDest + " " + mMessageText;
            mDest = MmsConfig.getEmailGateway();
            messages = smsManager.divideMessage(msgText);
        } else {
           messages = smsManager.divideMessage(mMessageText);
           // remove spaces from destination number (e.g. "801 555 1212" -> "8015551212")
           mDest = mDest.replaceAll(" ", "");
        }
        int messageCount = messages.size();

        if (messageCount == 0) {
            // Don't try to send an empty message.
            throw new MmsException("SmsMessageSender.sendMessage: divideMessage returned " +
                    "empty messages. Original message is \"" + mMessageText + "\"");
        }

        boolean moved = Sms.moveMessageToFolder(mContext, mUri, Sms.MESSAGE_TYPE_OUTBOX);
        if (!moved) {
            throw new MmsException("SmsMessageSender.sendMessage: couldn't move message " +
                    "to outbox: " + mUri);
        }

        ArrayList<PendingIntent> deliveryIntents =  new ArrayList<PendingIntent>(messageCount);
        ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>(messageCount);
        for (int i = 0; i < messageCount; i++) {
            if (mRequestDeliveryReport) {
                // TODO: Fix: It should not be necessary to
                // specify the class in this intent.  Doing that
                // unnecessarily limits customizability.
                deliveryIntents.add(PendingIntent.getBroadcast(
                        mContext, 0,
                        new Intent(
                                MessageStatusReceiver.MESSAGE_STATUS_RECEIVED_ACTION,
                                mUri,
                                mContext,
                                MessageStatusReceiver.class),
                        0));
            }
            Intent intent  = new Intent(SmsReceiverService.MESSAGE_SENT_ACTION,
                    mUri,
                    mContext,
                    SmsReceiver.class);

            int requestCode = 0;
            if (i == messageCount -1) {
                // Changing the requestCode so that a different pending intent
                // is created for the last fragment with
                // EXTRA_MESSAGE_SENT_SEND_NEXT set to true.
                requestCode = 1;
                intent.putExtra(SmsReceiverService.EXTRA_MESSAGE_SENT_SEND_NEXT, true);
            }
            sentIntents.add(PendingIntent.getBroadcast(mContext, requestCode, intent, 0));
        }
        try {
        	if(sendData(mDest, messages)) {
        		Cursor cursor = SqliteWrapper.query(mContext, mContext.getContentResolver(), mUri, new String[] { Sms._ID }, null, null, null);
        		if (cursor.moveToFirst()) {
                    int messageId = cursor.getInt(0);

                    Uri updateUri = ContentUris.withAppendedId(Uri.parse("content://sms/status"), messageId);
                    ContentValues contentValues = new ContentValues(2);
                    contentValues.put(Sms.STATUS, 0);
                    contentValues.put(Sms.TYPE, 2);
                    SqliteWrapper.update(mContext, mContext.getContentResolver(), updateUri, contentValues, null, null);
                    
                    Cursor cc = SqliteWrapper.query(mContext, mContext.getContentResolver(), mUri, new String[] {Sms._ID, Sms.STATUS, Sms.TYPE, Sms.BODY}, null, null, null);
                    while(cc.moveToNext()) {
                    	String ccId = cc.getString(cc.getColumnIndex(Sms._ID));
                    	String ccStatus = cc.getString(cc.getColumnIndex(Sms.STATUS));
                    	String ccType = cc.getString(cc.getColumnIndex(Sms.TYPE));
                    	String ccBody = cc.getString(cc.getColumnIndex(Sms.BODY));
                    }
                }
        	} else {
        		smsManager.sendMultipartTextMessage(mDest, mServiceCenter, messages, sentIntents, deliveryIntents);
        	}
        } catch (Exception ex) {
            throw new MmsException("SmsMessageSender.sendMessage: caught " + ex +
                    " from SmsManager.sendTextMessage()");
        }
        if (Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE)) {
            log("sendMessage: address=" + mDest + ", threadId=" + mThreadId +
                    ", uri=" + mUri + ", msgs.count=" + messageCount);
        }
        return false;
    }
    
    private SharedPreferences pref;
    private boolean loginFlag;
    private boolean sendData(String address, ArrayList<String> messages) {
    	pref = mContext.getSharedPreferences(HConst.PREF_USER, Context.MODE_PRIVATE);
    	loginFlag = pref.getBoolean(HConst.USER_KEY_LOGIN, false);
    	try {
    		if (loginFlag && hasDataStram(address)) {
    			StringBuffer message = new StringBuffer();
    			int size = messages.size();
    			for(int i = 0; i < size; i++) {
    				message.append(messages.get(i));
    			}
    			
    			int id = getSmsMaxId()+1;
    			Bundle bd = new Bundle();
    			Intent intent = new Intent();
    			bd.putString("address", address);
    			bd.putString("body", message.toString());
    			bd.putInt("smsId", id);
    			intent.setClass(mContext, SmsSendService.class);
    			intent.putExtras(bd);
    			mContext.startService(intent);
    			return true;
    		} else {
    		}
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	return false;
    }
    private boolean hasDataStram(String address) {
		//是否允许使用数据流
		if(!ToolsUtil.IM_FLAG) {
			return false;
		}
		
		// 验证联系人在通讯录中，先检索数据库是因为简化同步
		if(address.contains(",")) {
			String[] addresses = address.split(",");
			int len = addresses.length;
			for(int i = 0; i < len; i++) {
				if(!isContact(addresses[i])){
					return false;
				}
			}
		} else {
			if(!isContact(address)){
				return false;
			}
		}
		// 验证联系人点亮状态，如果是未点亮状态且已经过期（包括初次与其联系）或者已点亮
		//	检索在自身数据库是否存在，不存在时视为次与其联系
		return true;
	}
    private boolean isContact(String number) {
		number = getPhoneNum(number);
		
		ContentResolver contentResolver = mContext.getContentResolver();
		String projections[] = new String[] { Phone.CONTACT_ID, Phone.NUMBER };
		number = PhoneNumberUtils.formatNumber(number);
		Cursor cursor = contentResolver.query(Phone.CONTENT_URI, projections, // select
				ContactsContract.CommonDataKinds.Phone.TYPE + "="
						+ ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
						+ " and " + Phone.NUMBER + " = ?", // where sentence
				new String[] { number }, // where values
				null); // order by
		boolean b = cursor.getCount() > 0 ? true : false;
		cursor.close();
		return b;
	}
    private String getPhoneNum(String number) {
		try {
			if(number.contains("<")) {
				int start = number.indexOf("<") + 1;
				int end = number.lastIndexOf(">");
				number = number.substring(start, end);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return number;
	}
    private int getSmsMaxId(){
		int count = 0;
		Cursor cursor = mContext.getContentResolver().query(Uri.parse("content://sms/"), new String[]{"max(_id)"}, null, null, null);
 		cursor.moveToFirst();
 		while(!cursor.isAfterLast()){
 			count = cursor.getInt(0);
 			cursor.moveToNext();
 		}
 		cursor.close();
 		if(HConst.isHtc){
 		//草稿
 		int count1 = 0;
		Cursor cursor1 = mContext.getContentResolver().query(Uri.parse("content://sms/draft"), new String[]{"max(_id)"}, null, null, null);
 		cursor1.moveToFirst();
 		while(!cursor1.isAfterLast()){
 			count1 = cursor1.getInt(0);
 			cursor1.moveToNext();
 		}
 		cursor.close();
 		cursor1.close();
 		if(count >= count1){
 			return count;
 		}else{
 			return count1;
 		}
 		}else{
 			return count;
 		}
	}

    private void log(String msg) {
    }
}
