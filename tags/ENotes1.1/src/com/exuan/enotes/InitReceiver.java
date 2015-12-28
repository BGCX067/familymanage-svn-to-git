package com.exuan.enotes;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
				Cursor c = arg0.getContentResolver().query(NotesProvider.CONTENT_URI_NOTES, new String[]{"_id", NotesDatabaseHelper.ALARM_TIME}
				, NotesDatabaseHelper.ALARM_TIME + " > '0'", null, null);
				if(c != null && c.getCount() > 0)
				{
					while(c.moveToNext())
					{
						long time = c.getLong(c.getColumnIndex(NotesDatabaseHelper.ALARM_TIME));
						int id = c.getInt(c.getColumnIndex("_id"));
						Calendar ca = Calendar.getInstance();
						if(time > ca.getTimeInMillis())
						{
							EDetailActivity.setAlarm(arg0, id, time);
						}
						else
						{
							EDetailActivity.cancelAlarm(arg0, id);
						}
					}
				}
				c.close();
			}
		}
	}
}
