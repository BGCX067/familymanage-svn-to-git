package com.haolianluo.sms2.mms.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.TelephonyMy;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.mms.util.SqliteWrapper;
import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.LogTag;
import com.lianluo.core.util.HLog;

public class RecipientIdCache {
    private static final boolean LOCAL_DEBUG = false;
    private static final String TAG = "Mms/cache";

    private static Uri sAllCanonical =
            Uri.parse("content://mms-sms/canonical-addresses");

    private static Uri sSingleCanonicalAddressUri =
            Uri.parse("content://mms-sms/canonical-address");
    
    
    private static Uri sAllCanonicalCollect =
            Uri.parse("content://com.haolianluo.sms2.collect-mms-sms/canonical-addresses");

    private static RecipientIdCache sInstance;
    static RecipientIdCache getInstance() { return sInstance; }
    private final Map<Long, String> mCache;
    private final Context mContext;

    public static class Entry {
        public long id;
        public String number;

        public Entry(long id, String number) {
            this.id = id;
            this.number = number;
        }
    };

    static void init(Context context) {
        sInstance = new RecipientIdCache(context);
        new Thread(new Runnable() {
            public void run() {
                fill();
            }
        }).start();
    }

    RecipientIdCache(Context context) {
        mCache = new HashMap<Long, String>();
        mContext = context;
    }

    public static void fill() {
        if (Log.isLoggable(LogTag.THREAD_CACHE, Log.VERBOSE)) {
            LogTag.debug("[RecipientIdCache] fill: begin");
        }

        Context context = sInstance.mContext;
        
        Cursor c = null;
        System.out.println("HConst.iscollect ===" + HConst.iscollect);
        if(HConst.iscollect){
        	c = SqliteWrapper.query(context, context.getContentResolver(),sAllCanonicalCollect, null, null, null, null);
        }else{
        	c = SqliteWrapper.query(context, context.getContentResolver(),sAllCanonical, null, null, null, null);
        }
        
        if (c == null) {
            return;
        }

        try {
            synchronized (sInstance) {
                // Technically we don't have to clear this because the stupid
                // canonical_addresses table is never GC'ed.
                sInstance.mCache.clear();
                while (c.moveToNext()) {
                    // TODO: don't hardcode the column indices
                    long id = c.getLong(0);
                    String number = c.getString(1);
                    sInstance.mCache.put(id, number);
                }
            }
        } finally {
            c.close();
        }

        if (Log.isLoggable(LogTag.THREAD_CACHE, Log.VERBOSE)) {
            LogTag.debug("[RecipientIdCache] fill: finished");
            dump();
        }
    }

    public static List<Entry> getAddresses(String spaceSepIds) {
        synchronized (sInstance) {
            List<Entry> numbers = new ArrayList<Entry>();
            String[] ids = spaceSepIds.split(" ");
            for (String id : ids) {
                long longId;

                try {
                    longId = Long.parseLong(id);
                } catch (NumberFormatException ex) {
                    // skip this id
                    continue;
                }

                String number = sInstance.mCache.get(longId);
                
                if(HConst.iscollect){
                	 sInstance.mCache.clear();
                }

                if (number == null) {
                    if (Log.isLoggable(LogTag.THREAD_CACHE, Log.VERBOSE)) {
                        dump();
                    }
                    
                    fill();
                    number = sInstance.mCache.get(longId);
                }

                if (TextUtils.isEmpty(number)) {
                } else {
                    numbers.add(new Entry(longId, number));
                }
            }
            return numbers;
        }
    }

    public static void updateNumbers(long threadId, ContactList contacts) {
        long recipientId = 0;

        for (Contact contact : contacts) {
            if (contact.isNumberModified()) {
                contact.setIsNumberModified(false);
            } else {
                // if the contact's number wasn't modified, don't bother.
                continue;
            }

            recipientId = contact.getRecipientId();
            if (recipientId == 0) {
                continue;
            }

            String number1 = contact.getNumber();
            String number2 = sInstance.mCache.get(recipientId);

            if (Log.isLoggable(LogTag.APP, Log.VERBOSE)) {
            }

            // if the numbers don't match, let's update the RecipientIdCache's number
            // with the new number in the contact.
            if (!number1.equalsIgnoreCase(number2)) {
            	HLog.d("updateCanonicalAddressInDb", "n1 = " + number1 + " n2 = " + number2);
                sInstance.mCache.put(recipientId, number1);
               
                //TODO 注意此处HTC系列手机更新数据库的时候会crash，提示MmsProvider不支持相关CRUD操作
                sInstance.updateCanonicalAddressInDb(recipientId, number1);
            }
        }
    }

    private void updateCanonicalAddressInDb(long id, String number) {
        if (Log.isLoggable(LogTag.APP, Log.VERBOSE)) {
        }

        final ContentValues values = new ContentValues();
        values.put(TelephonyMy.CanonicalAddressesColumns.ADDRESS, number);

        final StringBuilder buf = new StringBuilder(TelephonyMy.CanonicalAddressesColumns._ID);
        buf.append('=').append(id);

        final Uri uri = ContentUris.withAppendedId(sSingleCanonicalAddressUri, id);
        mContext.getContentResolver().update(uri, values, buf.toString(), null);
        
        // We're running on the UI thread so just fire & forget, hope for the best.
        // (We were ignoring the return value anyway...)
        new Thread("updateCanonicalAddressInDb") {
            public void run() {
            	mContext.getContentResolver().update(uri, values, buf.toString(), null);
            }
        }.start();
        
    }

    public static void dump() {
        // Only dump user private data if we're in special debug mode
        synchronized (sInstance) {
            for (Long id : sInstance.mCache.keySet()) {
            }
        }
    }
}
