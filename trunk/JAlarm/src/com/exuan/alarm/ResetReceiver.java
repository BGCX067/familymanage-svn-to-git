package com.exuan.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

public class ResetReceiver extends BroadcastReceiver
{
	private static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
	private static final String TIME_SET = "android.intent.action.TIME_SET";
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		if(arg0.getContentResolver() == null)
		{
			return;
		}
		String action = arg1.getAction();
		if(action.equals(BOOT_COMPLETED) || action.equals(TIME_SET))
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
				if(action.equals(BOOT_COMPLETED))
				{
					SharedPreferences pref = arg0.getSharedPreferences(ESettingActivity.PREFERENCES_NAME, arg0.MODE_PRIVATE);
					boolean isNotify = pref.getBoolean(ESettingActivity.KEY_NOTIFY, true);
					if(isNotify)
					{
						ESetAlarmActivity.setNotification(arg0, true);
					}
				}
			}
			c.close();
		}
	}
}