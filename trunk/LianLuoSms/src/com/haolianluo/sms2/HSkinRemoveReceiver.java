package com.haolianluo.sms2;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.model.HResDatabaseHelper;
import com.haolianluo.sms2.model.HResProvider;
import com.haolianluo.sms2.model.HStatistics;
import com.lianluo.core.skin.SkinManage;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Process;
import android.util.Log;

public class HSkinRemoveReceiver extends BroadcastReceiver{
	private static final String TAG = "HSkinRemoveReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		HLog.e(TAG, "onReceive,action:" + intent.getAction());
		if(context.getContentResolver() == null)
		{
			return;
		}
		String action = intent.getAction();
		if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
			String packageName = intent.getDataString().replace("package:", "");
			if(packageName.equals("com.haolianluo.sms2"))
			{
				return;
			}
			Cursor c = context.getContentResolver().query(HResProvider.CONTENT_URI_SKIN, new String[]{HResDatabaseHelper.RES_USE}, 
					HResDatabaseHelper.PACKAGENAME + " = '" + packageName + "'", null, null);
			if(c != null && c.getCount() > 0)
			{
				c.moveToNext();
				if((1 == c.getInt(c.getColumnIndex(HResDatabaseHelper.RES_USE))))
				{
					ContentValues values = new ContentValues();
					values.put(HResDatabaseHelper.RES_USE, 1);
					context.getContentResolver().update(HResProvider.CONTENT_URI_SKIN, values, 
							HResDatabaseHelper.PACKAGENAME + " = 'com.haolianluo.sms2'", null);
					SkinManage.mCurrentSkin = HConst.DEFAULT_PACKAGE_NAME;
					HStatistics mHStatistics = new HStatistics(context);
					mHStatistics.add(HStatistics.Z12_1, "0", "0", "1");
					HLog.e(TAG, "Apply, id:" + 0);
				}
			}
			c.close();
			context.getContentResolver().delete(HResProvider.CONTENT_URI_SKIN, 
					HResDatabaseHelper.PACKAGENAME + " = '" + packageName + "'", null);
		}
		else if (Intent.ACTION_PACKAGE_CHANGED.equals(action) || Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
			int i = Process.myUid();
			int j = intent.getIntExtra("android.intent.extra.UID", -1);
			if(i == j)
			{
				HLog.e(TAG, "packge change package replace");
				Cursor c = context.getContentResolver().query(HResProvider.CONTENT_URI_SKIN, null, null, null, null);
				if(c != null && c.getCount() > 1)
				{
					c.close();
					return;
				}
				else
				{
					c.close();
					final Context con = context;
					new Thread()
					{
						public void run()
						{
							ToolsUtil.scanSkins(con);
						}
					}.start();
				}
			}
		}
	}
}
