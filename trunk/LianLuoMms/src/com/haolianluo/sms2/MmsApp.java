/*
 * Copyright (C) 2008 Esmertec AG.
 * Copyright (C) 2008 The Android Open Source Project
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

package com.haolianluo.sms2;

import android.content.Context;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import com.haolianluo.sms2.data.HSmsApplication;
import com.haolianluo.sms2.drm.DrmUtils;
import com.haolianluo.sms2.layout.LayoutManager;
import com.haolianluo.sms2.mms.data.Contact;
import com.haolianluo.sms2.mms.data.Conversation;
import com.haolianluo.sms2.transaction.MessagingNotification;
import com.haolianluo.sms2.util.DownloadManager;
import com.haolianluo.sms2.util.DraftCache;
import com.haolianluo.sms2.util.RateController;
import com.haolianluo.sms2.util.SmileyParser;

public class MmsApp extends HSmsApplication {
    public static final String LOG_TAG = "Mms";

//    private SearchRecentSuggestions mRecentSuggestions;
    private TelephonyManager mTelephonyManager;
    private static MmsApp sMmsApp = null;

    @Override
    public void onCreate() {
        super.onCreate();

        sMmsApp = this;

        // Load the default preference values
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        MmsConfig.init(this);
        Contact.init(this);
        DraftCache.init(this);
        Conversation.init(this);
        DownloadManager.init(this);
        RateController.init(this);
        DrmUtils.cleanupStorage(this);
        LayoutManager.init(this);
        SmileyParser.init(this);
        MessagingNotification.init(this);
    }

    synchronized public static MmsApp getApplication() {
        return sMmsApp;
    }

    @Override
    public void onTerminate() {
        DrmUtils.cleanupStorage(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        LayoutManager.getInstance().onConfigurationChanged(newConfig);
    }

    /**
     * @return Returns the TelephonyManager.
     */
    public TelephonyManager getTelephonyManager() {
        if (mTelephonyManager == null) {
            mTelephonyManager = (TelephonyManager)getApplicationContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);
        }
        return mTelephonyManager;
    }

    /**
     * Returns the content provider wrapper that allows access to recent searches.
     * @return Returns the content provider wrapper that allows access to recent searches.
     */
//    public SearchRecentSuggestions getRecentSuggestions() {
//        /*
//        if (mRecentSuggestions == null) {
//            mRecentSuggestions = new SearchRecentSuggestions(this,
//                    SuggestionsProvider.AUTHORITY, SuggestionsProvider.MODE);
//        }
//        */
//        return mRecentSuggestions;
//    }

}
