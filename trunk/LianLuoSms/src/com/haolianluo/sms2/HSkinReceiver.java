package com.haolianluo.sms2;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.model.HResDatabaseHelper;
import com.haolianluo.sms2.model.HResProvider;
import com.haolianluo.sms2.model.HStatistics;
import com.lianluo.core.net.download.DLData;
import com.lianluo.core.net.download.DLManager;
import com.lianluo.core.skin.SkinManage;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class HSkinReceiver extends BroadcastReceiver{

	public static final String ACTION_CHARGE = "com.haolianluo.sms2.ACTION_CHARGE";
	public static final String ACTION_INSTALLED = "com.haolianluo.sms2.ACTION_INSTALLED";
	private static final String TAG = "HSkinReceiver";
	public static String APP_KEY = "";
	public String mFileName = null;
	public String mPackageName = null;
	public String mDisplayName = null;
	public String mResKey = null;
	public int mChargeStatus = 0;
	public int mFileSize = 0;
	public String mResId = "";
	public Context mContext;
	
	public HSkinReceiver(Context context, String appKey) {
		if (appKey.equals("") || appKey == null) {
			throw new NullPointerException("appkey is null");
		}
		mContext = context;
		APP_KEY = appKey;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
			HLog.e(TAG, "package added");
			final Intent i = intent;
			final Context cont = context;
			final Context con = cont;
			new Thread(){
				public void run()
				{
					try
					{
					mPackageName = i.getDataString().replace("package:", "");
					mDisplayName = readName(cont, mPackageName);
					mResKey = readKey(cont, mPackageName);
					if (mDisplayName != null && mResKey != null) {
						if (mPackageName != null) {
							DLData task = DLManager.getInstance(mContext).deleteTask(
									mPackageName);
							if (null != task) {
								mFileName = task.getFileName();
								mFileSize = task.getTotalSize();
								mResId = task.getResID();
								if(task.getCharge() != null && Float.valueOf(task.getCharge()) > 0)
								{
									mChargeStatus = HConst.CHARGE_PENDING;
									try {
										new Thread(){
											public void run()
											{
												HLog.d("HSkinReceiver", "packageName:" + mPackageName + ",redId:"
														+ mResId + ",sdkLevel:" + ToolsUtil.getSDKLevel(con) + ",channelNum" + ToolsUtil.getChannelNum(con) + ",version" + ToolsUtil.getVersion(con));
												try {
													Thread.sleep(5000);
												} catch (InterruptedException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
												Intent i = new Intent(ACTION_CHARGE);
												i.putExtra("packageName", mPackageName);
												i.putExtra("redId", mResId);
												i.putExtra("sdkLevel", ToolsUtil.getSDKLevel(con));
												i.putExtra("channelNum", ToolsUtil.getChannelNum(con));
												i.putExtra("version", ToolsUtil.getVersion(con));
												i.putExtra("phoneNum", ToolsUtil.getPhoneNum(con));
												con.sendBroadcast(i);
												HLog.d("HSkinReceiver", "send pay Broadcast");
												ContentValues values = new ContentValues();
												values.put(HResDatabaseHelper.CHARGE, HConst.CHARGE_CHARGED);
												mContext.getContentResolver().update(HResProvider.CONTENT_URI_SKIN, values,
													HResDatabaseHelper.RES_ID + " = '" + HResDatabaseHelper.RES_ID + "'", null);
											} 
										}.start();
									}catch (Exception e) {
										e.printStackTrace();
									}
								}
								else
								{
									mChargeStatus = HConst.CHARGE_FREE;
								}
							}
						}
					}
					if (mPackageName == null || mDisplayName == null || mResKey == null) {
						return;
					}
					addRes(con);
					}
					catch(Exception e)
					{}
				}
			}.start();/*
			mPackageName = intent.getDataString().replace("package:", "");
			mDisplayName = readName(context, mPackageName);
			mResKey = readKey(context, mPackageName);
			if (mDisplayName != null && mResKey != null) {
				if (mPackageName != null) {
					DLData task = DLManager.getInstance(mContext).deleteTask(
							mPackageName);
					if (null != task) {
						mFileName = task.getFileName();
						mFileSize = task.getTotalSize();
						mResId = task.getResID();
						if(task.getCharge() != null && Float.valueOf(task.getCharge()) > 0)
						{
							mChargeStatus = HConst.CHARGE_PENDING;
							final Context con = context;
							new Handler().post(new Runnable(){

								@Override
								public void run() {
									// TODO Auto-generated method stub
									try {
										HLog.d("HSkinReceiver", "packageName:" + mPackageName + ",redId:"
									+ mResId + ",sdkLevel:" + ToolsUtil.getSDKLevel(con) + ",channelNum" + ToolsUtil.getChannelNum(con) + ",version" + ToolsUtil.getVersion(con));
										Thread.sleep(30000);
										Intent i = new Intent(ACTION_CHARGE);
										i.putExtra("packageName", mPackageName);
										i.putExtra("redId", mResId);
										i.putExtra("sdkLevel", ToolsUtil.getSDKLevel(con));
										i.putExtra("channelNum", ToolsUtil.getChannelNum(con));
										i.putExtra("version", ToolsUtil.getVersion(con));
										con.sendBroadcast(i);
										ContentValues values = new ContentValues();
										values.put(HResDatabaseHelper.CHARGE, HConst.CHARGE_CHARGED);
										mContext.getContentResolver().update(HResProvider.CONTENT_URI_SKIN, values,
												HResDatabaseHelper.RES_ID + " = '" + HResDatabaseHelper.RES_ID + "'", null);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}});
						}
						else
						{
							mChargeStatus = HConst.CHARGE_FREE;
						}
					}
				}
			}
			if (mPackageName == null || mDisplayName == null || mResKey == null) {
				return;
			}
			addRes();*/
			HLog.e(TAG, "receive end");
		}
	}
	
	private void addRes(Context con)
	{
		HStatistics mHStatistics = new HStatistics(mContext);
		mHStatistics.add(HStatistics.Z10_5_3, mResId, "", "");
		mHStatistics.add(HStatistics.Z12_1, mResId, (mChargeStatus == HConst.CHARGE_FREE) ? "0" : "1", "1");
		HLog.e(TAG, "Apply, id:" + mResId);
		HLog.e(TAG, "add new skin");
		ContentValues values = new ContentValues();
		values.put(HResDatabaseHelper.RES_USE, 0);
		mContext.getContentResolver().update(HResProvider.CONTENT_URI_SKIN, values,
				null, null);
		ContentValues contentValues = new ContentValues();
		Cursor c = mContext.getContentResolver().query(HResProvider.CONTENT_URI_SKIN, null, 
				HResDatabaseHelper.PACKAGENAME + " = '" + mPackageName + "'", null, null);
		if(c.getCount() > 0)
		{
			contentValues.put(HResDatabaseHelper.RES_USE, 1);
			mContext.getContentResolver().update(HResProvider.CONTENT_URI_SKIN, values, 
					HResDatabaseHelper.PACKAGENAME + " = '" + mPackageName + "'", null);
		}
		else
		{
			contentValues.put(HResDatabaseHelper.PACKAGENAME, mPackageName);
			contentValues.put(HResDatabaseHelper.DISPLAY_NAME, mDisplayName);
			contentValues.put(HResDatabaseHelper.RES_KEY, mResKey);
			contentValues.put(HResDatabaseHelper.RES_USE, 1);
			contentValues.put(HResDatabaseHelper.FILE_NAME, mFileName);
			contentValues.put(HResDatabaseHelper.TOTAL_SIZE, mFileSize);
			contentValues.put(HResDatabaseHelper.CHARGE, mChargeStatus);
			contentValues.put(HResDatabaseHelper.RES_ID, mResId);
			mContext.getContentResolver().insert(HResProvider.CONTENT_URI_SKIN, contentValues);
			SkinManage.mCurrentSkin = mPackageName;
		}
		c.close();
		Intent i = new Intent(ACTION_INSTALLED);
		con.sendBroadcast(i);
	}
	
	private String readName(Context context, String packageName) {
		try {
			PackageManager pm = context.getPackageManager();

			ApplicationInfo appinfo = pm.getApplicationInfo(packageName,
					PackageManager.GET_META_DATA);

			return pm.getApplicationLabel(appinfo).toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private String readKey(Context context, String packageName) {
		try {
			PackageManager pm = context.getPackageManager();
			ApplicationInfo appinfo = pm.getApplicationInfo(packageName,
					PackageManager.GET_META_DATA);
			if (appinfo.metaData == null) {
				return null;
			} else {
				Bundle bundle = appinfo.metaData;
				Object obj = bundle.get(APP_KEY);
				if (obj == null) {
					return null;
				} else {
					return obj.toString();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
