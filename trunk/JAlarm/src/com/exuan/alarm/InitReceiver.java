package com.exuan.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Process;

public class InitReceiver extends BroadcastReceiver
{
	private static final String PACKAGE_REPLACED = "android.intent.action.PACKAGE_REPLACED";
	private static final String PACKAGE_CHANGED = "android.intent.action.PACKAGE_CHANGED";
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		if(arg0.getContentResolver() == null)
		{
			return;
		}
		String action = arg1.getAction();
		if(action.equals(PACKAGE_REPLACED) || action.equals(PACKAGE_CHANGED))
		{
			int i = Process.myUid();
			int j = arg1.getIntExtra("android.intent.extra.UID", -1);
			if(i == j)
			{
				Cursor c = arg0.getContentResolver().query(AlarmInfoProvider.CONTENT_URI_ALARMS, 
						null, AlarmDatabaseHelper.IS_ACTIVE + " = '1'", null, null);
				if(c != null && c.getCount() > 0)
				{
					while(c.moveToNext())
					{
						int hour = c.getInt(c.getColumnIndex(AlarmDatabaseHelper.HOUR));
						int minute = c.getInt(c.getColumnIndex(AlarmDatabaseHelper.MINUTE));
						int id = c.getInt(c.getColumnIndex("_id"));
						String repeat = c.getString(c.getColumnIndex(AlarmDatabaseHelper.REPEAT));
						ESetAlarmActivity.registerAlarm(arg0, hour, minute, id, repeat);
					}
					SharedPreferences pref = arg0.getSharedPreferences(ESettingActivity.PREFERENCES_NAME, arg0.MODE_PRIVATE);
					boolean isNotify = pref.getBoolean(ESettingActivity.KEY_NOTIFY, true);
					if(isNotify)
					{
						ESetAlarmActivity.setNotification(arg0, true);
					}
				}
				c.close();
			}
		}
	}
}