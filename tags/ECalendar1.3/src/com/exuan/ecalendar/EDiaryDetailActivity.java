package com.exuan.ecalendar;

import java.util.Calendar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EDiaryDetailActivity extends Activity
{
	private static final int MENU_STORE = 0;
	private static final int MENU_CLEAR = 1;
	private static final int MENU_SEND = 2;
	private static final int REQUEST_CODE_SEND = 0;
	private Context mContext;
	private String mDate;
	private long mID;
	private EditText mEditText;
	private TextView mDateTextView;
	private String[] mWeek;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_edit);
		mContext = this;
		mWeek = getResources().getStringArray(R.array.week_array);
		mDateTextView = (TextView)findViewById(R.id.textview_date);
		mDateTextView.setTextColor(ECalendarActivity.mHolidayColor);
		mEditText = (EditText)findViewById(R.id.edittext_detail);
		Bundle extras = getIntent().getExtras();
		mDate = extras.getString(DatabaseHelper.DATE);
		int year = Integer.valueOf(mDate.substring(0, 4));
		int month = Integer.valueOf(mDate.substring(4, 6)) - 1;
		int day = Integer.valueOf(mDate.substring(6));
		Calendar ca = Calendar.getInstance();
		ca.set(year, month, day);
		int w = (ca.get(Calendar.DAY_OF_WEEK) == 1) ? 6 : (ca.get(Calendar.DAY_OF_WEEK) - 2);
		mDateTextView.setText(mDate.substring(0, 4) + getString(R.string.div) + mDate.substring(4, 6) + 
				getString(R.string.div) + mDate.substring(6) + " " + getString(R.string.week) + mWeek[w]);
		mID = -1;
		Cursor c = getContentResolver().query(CalendarProvider.CONTENT_URI_DIARYS, null, DatabaseHelper.DATE + " = '" + mDate+ "'", null, null);
		if(c != null && c.getCount() > 0)
		{
			c.moveToNext();
			mID = c.getLong(c.getColumnIndex("_id"));
			String content = c.getString(c.getColumnIndex(DatabaseHelper.CONTENT));
			mEditText.setText(content);
		}
		c.close();
	}
	
	public void onConfigurationChanged(Configuration newConfig)
	{
		try 
    	{
    		super.onConfigurationChanged(newConfig);
    	}
    	catch (Exception ex)
    	{

    	}
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, MENU_STORE, 0, R.string.store).setIcon(
				R.drawable.menu_store);
		menu.add(0, MENU_CLEAR, 0, R.string.clear).setIcon(
				R.drawable.menu_clear);
		menu.add(0, MENU_SEND, 0, R.string.send).setIcon(
				R.drawable.menu_send);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case MENU_STORE:
			{
				storeDiary();
				finish();
				return true;
			}
			case MENU_CLEAR:
			{
				mEditText.setText("");
				return true;
			}
			case MENU_SEND:
			{
				String detail = mEditText.getText().toString();
				if(detail.trim().length() > 0)
				{
					Intent intent = new Intent("android.intent.action.SEND");
					intent.setType("text/*");
					intent.putExtra("android.intent.extra.SUBJECT", getString(R.string.app_name));
					intent.putExtra("android.intent.extra.TEXT", mEditText.getText().toString());
					Intent i = Intent.createChooser(intent, getString(R.string.send_with));
					startActivityForResult(i, REQUEST_CODE_SEND);
				}
				else
				{
					Toast.makeText(mContext, getString(R.string.input_content), Toast.LENGTH_LONG).show();
				}
				return true;
			}
		}
		
		return false;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			storeDiary();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void storeDiary()
	{
		if(0 >= mEditText.getEditableText().toString().trim().length())
		{
			if((-1 == mID))
			{
				return;
			}
			else
			{
				getContentResolver().delete(CalendarProvider.CONTENT_URI_DIARYS, "_id = '" + mID + "'", null);
				return;
			}
		}
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.CONTENT, mEditText.getEditableText().toString());
		if(-1 == mID)
		{
			values.put(DatabaseHelper.DATE, mDate);
			getContentResolver().insert(CalendarProvider.CONTENT_URI_DIARYS, values);
		}
		else
		{
			getContentResolver().update(CalendarProvider.CONTENT_URI_DIARYS, values, "_id = '" + mID + "'", null);
		}
	}
}