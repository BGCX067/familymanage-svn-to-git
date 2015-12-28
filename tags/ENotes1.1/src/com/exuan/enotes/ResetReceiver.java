package com.exuan.enotes;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

public class ResetReceiver extends BroadcastReceiver
{
	private static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
	private static final String TIMEZONE_CHANGED = "android.intent.action.TIMEZONE_CHANGED";
	private static final String TIME_SET = "android.intent.action.TIME_SET";
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		if(arg0.getContentResolver() == null)
		{
			return;
		}
		String action = arg1.getAction();
		if(action.equals(BOOT_COMPLETED) || action.equals(TIME_SET) || action.equals(TIMEZONE_CHANGED))
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
