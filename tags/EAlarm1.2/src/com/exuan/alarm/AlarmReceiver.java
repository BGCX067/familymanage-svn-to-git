package com.exuan.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlarmReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		Bundle extras = arg1.getExtras();
		int _id = extras.getInt("_id");
		
		Intent intent = new Intent(arg0.getApplicationContext(), EAlertAlarmActivity.class);
		intent.putExtra("_id", _id);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
				| Intent.FLAG_ACTIVITY_MULTIPLE_TASK 
				);
		arg0.startActivity(intent);
	}
}