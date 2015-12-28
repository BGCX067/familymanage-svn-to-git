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

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.TelephonyMy;
import android.text.TextUtils;
import android.util.Config;
import android.util.Log;

import com.google.android.mms.util.SqliteWrapper;


/**
 * Container of transaction settings. Instances of this class are contained
 * within Transaction instances to allow overriding of the default APN
 * settings or of the MMS Client.
 */
public class TransactionSettings {
    private static final String TAG = "TransactionSettings";
    private static final boolean DEBUG = true;
    private static final boolean LOCAL_LOGV = DEBUG ? Config.LOGD : Config.LOGV;

    private String mServiceCenter;
    private String mProxyAddress;
    private int mProxyPort = -1;

    private static final String[] APN_PROJECTION = {
            TelephonyMy.Carriers.TYPE,            // 0
            TelephonyMy.Carriers.MMSC,            // 1
            TelephonyMy.Carriers.MMSPROXY,        // 2
            TelephonyMy.Carriers.MMSPORT          // 3
    };
    private static final int COLUMN_TYPE         = 0;
    private static final int COLUMN_MMSC         = 1;
    private static final int COLUMN_MMSPROXY     = 2;
    private static final int COLUMN_MMSPORT      = 3;

    /**
     * Constructor that uses the default settings of the MMS Client.
     *
     * @param context The context of the MMS Client
     */
    public TransactionSettings(Context context, String apnName) {
        String selection = (apnName != null)?
                TelephonyMy.Carriers.APN + "='"+apnName+"'": null;

        Cursor cursor = SqliteWrapper.query(context, context.getContentResolver(),
                            Uri.withAppendedPath(TelephonyMy.Carriers.CONTENT_URI, "current"),
                            APN_PROJECTION, selection, null, null);

        if (cursor == null) {
            return;
        }

        boolean sawValidApn = false;
        try {
            while (cursor.moveToNext() && TextUtils.isEmpty(mServiceCenter)) {
                // Read values from APN settings
//                if (isValidApnType(cursor.getString(COLUMN_TYPE), Phone.APN_TYPE_MMS)) {
            	if (isValidApnType(cursor.getString(COLUMN_TYPE), "mms")) {
                    sawValidApn = true;
                    mServiceCenter = cursor.getString(COLUMN_MMSC);
                    mProxyAddress = cursor.getString(COLUMN_MMSPROXY);
                    if (isProxySet()) {
                        String portString = cursor.getString(COLUMN_MMSPORT);
                        try {
                            mProxyPort = Integer.parseInt(portString);
                        } catch (NumberFormatException e) {
                            if (TextUtils.isEmpty(portString)) {
                            } else {
                            }
                        }
                    }
                }
            }
        } finally {
            cursor.close();
        }

        if (sawValidApn && TextUtils.isEmpty(mServiceCenter)) {
        }
    }

    /**
     * Constructor that overrides the default settings of the MMS Client.
     *
     * @param mmscUrl The MMSC URL
     * @param proxyAddr The proxy address
     * @param proxyPort The port used by the proxy address
     * immediately start a SendTransaction upon completion of a NotificationTransaction,
     * false otherwise.
     */
    public TransactionSettings(String mmscUrl, String proxyAddr, int proxyPort) {
        mServiceCenter = mmscUrl;
        mProxyAddress = proxyAddr;
        mProxyPort = proxyPort;
    }

    public String getMmscUrl() {
        return mServiceCenter;
    }

    public String getProxyAddress() {
        return mProxyAddress;
    }

    public int getProxyPort() {
        return mProxyPort;
    }

    public boolean isProxySet() {
        return (mProxyAddress != null) && (mProxyAddress.trim().length() != 0);
    }

    static private boolean isValidApnType(String types, String requestType) {
        // If APN type is unspecified, assume APN_TYPE_ALL.
        if (TextUtils.isEmpty(types)) {
            return true;
        }

        for (String t : types.split(",")) {
//            if (t.equals(requestType) || t.equals(Phone.APN_TYPE_ALL)) {
        	if (t.equals(requestType) || t.equals("*")) {
                return true;
            }
        }
        return false;
    }
}
