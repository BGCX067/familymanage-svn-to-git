
package com.exuan.enotes;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

public class AlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // TODO Auto-generated method stub
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.MILLISECOND, 0);
        Bundle extras = arg1.getExtras();
        int _id = extras.getInt("_id");
        Cursor c = arg0.getContentResolver().query(NotesProvider.CONTENT_URI_NOTES, new String[] {
            NotesDatabaseHelper.ALARM_TIME
        }, "_id = '" + _id + "'", null, null);
        if (c == null)
        {
            return;
        }
        if (0 == c.getCount()) {
            c.close();
            return;
        }
        c.moveToNext();
        long time = c.getLong(c.getColumnIndex(NotesDatabaseHelper.ALARM_TIME));
        c.close();
        if (time != ca.getTimeInMillis())
        {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(NotesDatabaseHelper.ALARM_TIME, 0);
        arg0.getContentResolver().update(NotesProvider.CONTENT_URI_NOTES, values, "_id = " + _id,
                null);
        Intent intent = new Intent(arg0.getApplicationContext(), EAlertActivity.class);
        intent.putExtra("_id", _id);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                );
        arg0.startActivity(intent);
    }
}
