package com.exuan.alarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class EAlarmActivity extends Activity {
	private Context mContext;
	private LinearLayout mAddAlarmLinearLayout;
	private ListView mAlarmInfoList;
	private AlarmInfoAdapter mAlarmAdapter;
	private Cursor mCursor;
	private TextView mNoAlarmText;
	private int mID;
	private static final int REQUEST_CODE_RECOMMEND = 1;
	public static final int DELETE_ALARM_DIALOG_ID = 0;
	public static final int ENABLE_ALARM = 1;
	public static final int DISABLE_ALARM = 2;
	
	private static final int MENU_SETTING_ALARM = 0;
	private static final int MENU_RECOMMEND_ALARM = 1;
	private static final int MENU_ABOUT_ALARM = 2;
	public static AdapterHandler mHander;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = this;
        
        mAlarmInfoList = (ListView)findViewById(R.id.listview_alarm_info);
        mAddAlarmLinearLayout = (LinearLayout)findViewById(R.id.linearlayout_add_alarm);
        mNoAlarmText = (TextView)findViewById(R.id.textview_no_alarm);
        mHander = new AdapterHandler();
        mAddAlarmLinearLayout.setOnClickListener(new OnClickListener(){
        
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, ESetAlarmActivity.class);
				intent.putExtra("isNew", true);
				mContext.startActivity(intent);
			}});
        
        mCursor = getContentResolver().query(AlarmInfoProvider.CONTENT_URI_ALARMS, null, null,
        		null, "hour ASC , minute ASC");
        
        mAlarmAdapter = new AlarmInfoAdapter(this);
        mAlarmInfoList.setAdapter(mAlarmAdapter);
	}
	
	public void onResume()
    {	
    	super.onResume();
    	updateView();
    }
	
	public void onDestroy()
	{
		super.onDestroy();
		mCursor.close();
		mCursor = null;
	}
	
	private void updateView()
	{
		mCursor = getContentResolver().query(AlarmInfoProvider.CONTENT_URI_ALARMS, null, null,
        		null, "hour ASC , minute ASC");
		mAlarmAdapter.updateAdapter(mCursor);
    	if(0 == mCursor.getCount())
    	{
    		mNoAlarmText.setVisibility(View.VISIBLE);
    		ESetAlarmActivity.setNotification(mContext, false);
    	}
    	else
    	{
    		mNoAlarmText.setVisibility(View.GONE);
    	}
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_SETTING_ALARM, 0, R.string.setting).setIcon(
				R.drawable.setting);
		menu.add(0, MENU_RECOMMEND_ALARM, 0, R.string.share).setIcon(
				R.drawable.share);
		menu.add(0, MENU_ABOUT_ALARM, 0, R.string.about).setIcon(
				R.drawable.about);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case MENU_SETTING_ALARM:
			{
				Intent intent = new Intent(mContext, ESettingActivity.class);
				startActivity(intent);
				return true;
			}
			case MENU_RECOMMEND_ALARM:
			{
				Intent intent = new Intent("android.intent.action.SEND");
				intent.setType("text/*");
				intent.putExtra("android.intent.extra.SUBJECT", getString(R.string.app_name));
				intent.putExtra("android.intent.extra.TEXT", getString(R.string.share_content));
				Intent i = Intent.createChooser(intent, getString(R.string.share_way));
				startActivityForResult(i, REQUEST_CODE_RECOMMEND);
				return true;
			}
			case MENU_ABOUT_ALARM:
			{
				Intent i = new Intent(mContext, EAboutActivity.class);
				startActivity(i);
				return true;
			}
		}
		
		return false;
	}
	
	protected Dialog onCreateDialog(int id)
    {
    	switch(id)
    	{
	    	case DELETE_ALARM_DIALOG_ID:
	    		return new AlertDialog.Builder(this)
				.setTitle(R.string.delete)
				.setMessage(R.string.delete_message)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								getContentResolver().delete(AlarmInfoProvider.CONTENT_URI_ALARMS, "_id = " + mID, null);
								ESetAlarmActivity.cancelAlarm(mContext, mID);
								updateView();
							}
						}).setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
	
							}
						}).create();
    	}
		return null;
    }

	
	public class AdapterHandler extends Handler
	{
		public void handleMessage(Message msg)
		{
			
			switch(msg.what)
			{
				case DELETE_ALARM_DIALOG_ID:
				{	
					mID = msg.arg1;
					removeDialog(DELETE_ALARM_DIALOG_ID);
					showDialog(DELETE_ALARM_DIALOG_ID);
				}
				break;
				case ENABLE_ALARM:
				{	
					mID = msg.arg1;
					Cursor c = mContext.getContentResolver().query(AlarmInfoProvider.CONTENT_URI_ALARMS, null, "_id = " + mID, null, null);
					boolean active = false;
					if(c != null && c.getCount() > 0)
					{
						c.moveToNext();
						active = (c.getInt(c.getColumnIndex(AlarmDatabaseHelper.IS_ACTIVE)) == 1);
					}
					if(active)
					{
						ContentValues values = new ContentValues();
						values.put(AlarmDatabaseHelper.IS_ACTIVE, false);
					    mContext.getContentResolver().update(AlarmInfoProvider.CONTENT_URI_ALARMS, values, "_id = " + mID, null);
					    ESetAlarmActivity.cancelAlarm(mContext, mID);
					}
					else
					{
						ContentValues values = new ContentValues();
					    values.put(AlarmDatabaseHelper.IS_ACTIVE, true);
					    mContext.getContentResolver().update(AlarmInfoProvider.CONTENT_URI_ALARMS, values, "_id = " + mID, null);
						int hour = c.getInt(c.getColumnIndex(AlarmDatabaseHelper.HOUR));
						int minute = c.getInt(c.getColumnIndex(AlarmDatabaseHelper.MINUTE));
						String repeat = c.getString(c.getColumnIndex(AlarmDatabaseHelper.REPEAT));
						int[] time = ESetAlarmActivity.registerAlarm(mContext, hour, minute, mID, repeat);
						ESetAlarmActivity.makeToast(mContext, time);
					}
					updateView();
					Cursor cursor = mContext.getContentResolver().query(AlarmInfoProvider.CONTENT_URI_ALARMS, null, AlarmDatabaseHelper.IS_ACTIVE + " = '1'", null, null);
					if(cursor != null)
					{
						if(cursor.getCount() == 0)
						{
							ESetAlarmActivity.setNotification(mContext, false);
						}
						else
						{
							SharedPreferences pref = mContext.getSharedPreferences(ESettingActivity.PREFERENCES_NAME, MODE_PRIVATE);
							boolean isNotify = pref.getBoolean(ESettingActivity.KEY_NOTIFY, true);
							if(isNotify)
							{
								ESetAlarmActivity.setNotification(mContext, true);
							}
						}
					}
					c.close();
					cursor.close();
				}
				break;
				case DISABLE_ALARM:
				{
					mID = msg.arg1;
					ContentValues values = new ContentValues();
					values.put(AlarmDatabaseHelper.IS_ACTIVE, false);
				    mContext.getContentResolver().update(AlarmInfoProvider.CONTENT_URI_ALARMS, values, "_id = " + mID, null);
				    updateView();
				}
				break;
			}
		}
	}
	
}