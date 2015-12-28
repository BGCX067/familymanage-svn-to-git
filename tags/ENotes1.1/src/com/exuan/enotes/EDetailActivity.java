package com.exuan.enotes;

import java.util.Calendar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class EDetailActivity extends Activity
{
	private EditText mEditTextDetail;
	private SeekBar mSeekBarTextSize;
	private PopupWindow mPopupWindow;
	private TextView mTextViewFolder;
	private ImageView mTextViewBack;
	private TextView mTextViewTime;
	private TextView mTextViewAlarm;
	private Button mButtonDate;
	private Button mButtonTime;
	private LinearLayout mRelativeLayoutTitle;
	
	private int mNoteId = -1;
	private int mFolderId;
	private int mBackId;
	private String mDetail;
	private long mAlarm;
	private long mModify;
	private int mWidth;
	private static final int MENU_TEXT_SIZE = 0;
	private static final int MENU_SET_ALARM = 1;
	private static final int MENU_SEND_TEXT = 2;
	
	private static final int REQUEST_CODE_SEND = 1;
	private int MAX_TEXT_SIZE;
	private int MIN_TEXT_SIZE;
	
	private static final int DIALOG_SET_ALARM_ID = 0;
	private static final int DIALOG_SET_DATE_ID = 1;
	private static final int DIALOG_SET_TIME_ID = 2;
	private static final int DIALOG_SELECT_FOLDER_ID = 3;
	private Context mContext;
	
	private static final String KEY_TEXT_SIZE = "text_size";
	private static final String KEY_BACK_ID = "back_id";
	
	private int mTextSize;
	private Calendar mAlarmTime;
	private Integer[] mBackupId = {
        0xfff6cece, 0xffedcbaa,
        0xfff1e1a2, 0xffd5dfa8,
        0xffc0bfbe, 0xffbeeeee,
        0xffdad2e7
	};
	
	private Integer[] mBackdownId = {
			0xfff49898, 0xfff0ab68,
	        0xffeed575, 0xffd0e66d,
	        0xff9b9895, 0xff64e7e7,
	        0xffbea6e6
		};
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_edit);
		mContext = this;
		int width = getWindowManager().getDefaultDisplay().getWidth();
		int height = getWindowManager().getDefaultDisplay().getHeight();
		mWidth = width < height ? width : height;
		mWidth /= 9; 
		mEditTextDetail = (EditText)findViewById(R.id.edittext_detail);
		mTextViewFolder = (TextView)findViewById(R.id.textview_select_folder);
		mTextViewBack = (ImageView)findViewById(R.id.textview_select_back);
		mTextViewTime = (TextView)findViewById(R.id.textview_modify_time);
		mTextViewAlarm = (TextView)findViewById(R.id.textview_alarm);
		mTextViewAlarm.setTextSize(10);
		mRelativeLayoutTitle = (LinearLayout)findViewById(R.id.relativelayout_title_text);
		SharedPreferences pref = mContext.getSharedPreferences(ENotesActivity.PREFERENCES_NAME, MODE_PRIVATE);
		MIN_TEXT_SIZE = (int) (mEditTextDetail.getTextSize()/2);
		MAX_TEXT_SIZE = (int) (mEditTextDetail.getTextSize()*4);
		mTextSize = pref.getInt(KEY_TEXT_SIZE, (int) mEditTextDetail.getTextSize());
		mEditTextDetail.setTextSize(mTextSize);
		mNoteId = getIntent().getIntExtra("_id", -1);
		mFolderId = getIntent().getIntExtra(NotesDatabaseHelper.FOLDER_ID, 1);
		mModify = Calendar.getInstance().getTimeInMillis();
		if(mNoteId > 0)
		{
			Cursor cursor = getContentResolver().query(NotesProvider.CONTENT_URI_NOTES, null, "_id = '" + mNoteId + "'", null, null);
			cursor.moveToNext();
			mDetail = cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.DETAIL));
			mBackId = cursor.getInt(cursor.getColumnIndex(NotesDatabaseHelper.BACKGROUND_ID));
			mModify = cursor.getLong(cursor.getColumnIndex(NotesDatabaseHelper.MODIFY_TIME));
			mAlarm = cursor.getLong(cursor.getColumnIndex(NotesDatabaseHelper.ALARM_TIME));
		}
		else
		{
			mBackId = pref.getInt(KEY_BACK_ID, 6);
			if(6 == mBackId)
			{
				mBackId = 0;
			}
			else
			{
				mBackId += 1;
			}
			pref.edit().putInt(KEY_BACK_ID, mBackId).commit();
		}
		mEditTextDetail.setText(mDetail);
		mEditTextDetail.setBackgroundColor(mBackupId[mBackId]);
		mRelativeLayoutTitle.setBackgroundColor(mBackdownId[mBackId]);
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(mModify);
		mTextViewTime.setText(c.get(Calendar.YEAR) + getString(R.string.div) 
				+ ((c.get(Calendar.MONTH) + 1) < 10 ? ("0" + (c.get(Calendar.MONTH) + 1)) : (c.get(Calendar.MONTH) + 1))
				+ getString(R.string.div)
				+ (c.get(Calendar.DAY_OF_MONTH) < 10 ? ("0" + c.get(Calendar.DAY_OF_MONTH)) : c.get(Calendar.DAY_OF_MONTH)) + "  "
				+ (c.get(Calendar.HOUR_OF_DAY) < 10 ? ("0" + c.get(Calendar.HOUR_OF_DAY)) : c.get(Calendar.HOUR_OF_DAY))
				+ ":" + (c.get(Calendar.MINUTE) < 10 ? ("0" + c.get(Calendar.MINUTE)) : c.get(Calendar.MINUTE)) + "  "
				+ ENotesActivity.mWeek[c.get(Calendar.DAY_OF_WEEK) - 1]
				);
		if(mAlarm > Calendar.getInstance().getTimeInMillis())
		{
			mAlarmTime = Calendar.getInstance();
			mAlarmTime.setTimeInMillis(mAlarm);
			mTextViewAlarm.setText(mAlarmTime.get(Calendar.YEAR) + getString(R.string.div) 
					+ ((mAlarmTime.get(Calendar.MONTH) + 1) < 10 ? ("0" + (mAlarmTime.get(Calendar.MONTH) + 1)) : (mAlarmTime.get(Calendar.MONTH) + 1))
					+ getString(R.string.div)
					+ (mAlarmTime.get(Calendar.DAY_OF_MONTH) < 10 ? ("0" + mAlarmTime.get(Calendar.DAY_OF_MONTH)) : mAlarmTime.get(Calendar.DAY_OF_MONTH)) + "  "
					+ (mAlarmTime.get(Calendar.HOUR_OF_DAY) < 10 ? ("0" + mAlarmTime.get(Calendar.HOUR_OF_DAY)) : mAlarmTime.get(Calendar.HOUR_OF_DAY))
					+ ":" + (mAlarmTime.get(Calendar.MINUTE) < 10 ? ("0" + mAlarmTime.get(Calendar.MINUTE)) : mAlarmTime.get(Calendar.MINUTE)) + "  "
					+ ENotesActivity.mWeek[mAlarmTime.get(Calendar.DAY_OF_WEEK) - 1]
					);
		}
		else
		{
			mTextViewAlarm.setText(getString(R.string.no_alarm));
		}
		Cursor cur = getContentResolver().query(NotesProvider.CONTENT_URI_FOLDERS, null, "_id = '" + mFolderId + "'", null, null);
		cur.moveToNext();
		mTextViewFolder.setText(cur.getString(cur.getColumnIndex(NotesDatabaseHelper.FOLDER_NAME)));
		mTextViewFolder.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				removeDialog(DIALOG_SELECT_FOLDER_ID);
				showDialog(DIALOG_SELECT_FOLDER_ID);
		}});
		
		mTextViewBack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(mPopupWindow != null && mPopupWindow.isShowing())
				{
					mPopupWindow.dismiss();
					mPopupWindow = null;
				}
				LayoutInflater factory = LayoutInflater.from(mContext);
				View layout = factory.inflate(R.layout.layout_back, null);
				LinearLayout ll = (LinearLayout)layout.findViewById(R.id.linearlayout_back);
				ImageView[] img = new ImageView[mBackupId.length];
				for(int i = 0; i < mBackupId.length; i++)
				{
					img[i] = new ImageView(mContext);
					img[i].setLayoutParams(new LinearLayout.LayoutParams(mWidth, mWidth));
					img[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
					img[i].setBackgroundColor(mBackupId[i]);
					img[i].setOnClickListener(new OnBackListener(i));
					ll.addView(img[i]);
				}
				mPopupWindow = new PopupWindow(layout, mWidth * 8, mWidth * 2);
				mPopupWindow.setFocusable(true);
				mPopupWindow.setTouchable(true);
				BitmapDrawable bitmap = new BitmapDrawable();
				mPopupWindow.setBackgroundDrawable(bitmap);
				mPopupWindow.showAsDropDown(mTextViewBack, 0, 20);
			}});
	}
	
	public void onResume()
	{
		super.onResume();
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
	
	class OnBackListener implements OnClickListener
	{
		private int mId;
		public OnBackListener(int id)
		{
			mId = id;
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mBackId = mId;
			mEditTextDetail.setBackgroundColor(mBackupId[mBackId]);
			mRelativeLayoutTitle.setBackgroundColor(mBackdownId[mBackId]);
			mPopupWindow.dismiss();
			mPopupWindow = null;
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			String detail = mEditTextDetail.getText().toString();
			if(detail.trim().length() > 0)
			{
				Calendar c = Calendar.getInstance();
				ContentValues values = new ContentValues();
				
				if(mNoteId > 0)
				{
					Cursor cursor = getContentResolver().query(NotesProvider.CONTENT_URI_NOTES, null, "_id = '" + mNoteId + "'", null, null);
					cursor.moveToNext();
					values.put(NotesDatabaseHelper.ALARM_TIME, mAlarm > c.getTimeInMillis() ? mAlarm : 0);
					values.put(NotesDatabaseHelper.FOLDER_ID, mFolderId);
					values.put(NotesDatabaseHelper.BACKGROUND_ID, mBackId);
					if(!cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.DETAIL)).equals((mEditTextDetail.getText().toString())))
					{
						values.put(NotesDatabaseHelper.MODIFY_TIME, c.getTimeInMillis());
						values.put(NotesDatabaseHelper.DETAIL, mEditTextDetail.getText().toString());
					}
					getContentResolver().update(NotesProvider.CONTENT_URI_NOTES, values, "_id = '" + mNoteId + "'", null);
				}
				else
				{
					values.put(NotesDatabaseHelper.ALARM_TIME, mAlarm > c.getTimeInMillis() ? mAlarm : 0);
					values.put(NotesDatabaseHelper.MODIFY_TIME, c.getTimeInMillis());
					values.put(NotesDatabaseHelper.FOLDER_ID, mFolderId);
					values.put(NotesDatabaseHelper.BACKGROUND_ID, mBackId);
					values.put(NotesDatabaseHelper.DETAIL, mEditTextDetail.getText().toString());
					getContentResolver().insert(NotesProvider.CONTENT_URI_NOTES, values);
					Cursor cu = getContentResolver().query(NotesProvider.CONTENT_URI_NOTES, new String[]{"_id"}, null, null, null);
					cu.moveToLast();
					mNoteId = cu.getInt(cu.getColumnIndex("_id"));
				}
				if(mAlarm > c.getTimeInMillis())
				{
			    	setAlarm(mContext, mNoteId, mAlarm);
				}
			}
			else
			{
				if(mNoteId > 0)
				{
					getContentResolver().delete(NotesProvider.CONTENT_URI_NOTES, "_id = '" + mNoteId + "'", null);
					cancelAlarm(mContext, mNoteId);
				}
			}
			SharedPreferences pref = mContext.getSharedPreferences(ENotesActivity.PREFERENCES_NAME, MODE_PRIVATE);
			pref.edit().putInt(KEY_TEXT_SIZE, mTextSize).commit();
			finish();
			return true;
		}
		return false;
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_TEXT_SIZE, 0, R.string.text_size).setIcon(
				R.drawable.text_size);
		menu.add(1, MENU_SET_ALARM, 0, R.string.alarm).setIcon(
				R.drawable.alarm_sig);
		menu.add(1, MENU_SEND_TEXT, 0, R.string.send).setIcon(
				R.drawable.menu_send);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case MENU_TEXT_SIZE:
			{
				LayoutInflater factory = LayoutInflater.from(mContext);
				View layout = factory.inflate(R.layout.seekbar_text_size, null);
				mSeekBarTextSize = (SeekBar)layout.findViewById(R.id.seekbar_textsize);
				mSeekBarTextSize.setPadding(mWidth/2, 0, mWidth/2, 0);
				mSeekBarTextSize.setMax(MAX_TEXT_SIZE - MIN_TEXT_SIZE);
				mSeekBarTextSize
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						mTextSize = progress + MIN_TEXT_SIZE;
						mEditTextDetail.setTextSize(mTextSize);
					}
	
					public void onStartTrackingTouch(SeekBar seekBar) {
					}
	
					public void onStopTrackingTouch(SeekBar seekBar) {
					}
				});
				if(mPopupWindow != null && mPopupWindow.isShowing())
				{
					mPopupWindow.dismiss();
					mPopupWindow = null;
				}
				mPopupWindow = new PopupWindow(layout, mWidth * 8, mWidth * 2, false);
				mPopupWindow.setFocusable(true);
				mPopupWindow.setTouchable(true);
				BitmapDrawable bitmap = new BitmapDrawable();
				mPopupWindow.setBackgroundDrawable(bitmap);	
				mSeekBarTextSize.setProgress(mTextSize - MIN_TEXT_SIZE);
				mPopupWindow.showAtLocation(mEditTextDetail, Gravity.BOTTOM, 0, 30);
				return true;
			}
			case MENU_SET_ALARM:
			{
				removeDialog(DIALOG_SET_ALARM_ID);
				showDialog(DIALOG_SET_ALARM_ID);
				return true;
			}
			case MENU_SEND_TEXT:
			{
				String detail = mEditTextDetail.getText().toString();
				if(detail.trim().length() > 0)
				{
					Intent intent = new Intent("android.intent.action.SEND");
					intent.setType("text/*");
					intent.putExtra("android.intent.extra.SUBJECT", getString(R.string.app_name));
					intent.putExtra("android.intent.extra.TEXT", mEditTextDetail.getText().toString());
					Intent i = Intent.createChooser(intent, getString(R.string.send_way));
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

	public Dialog onCreateDialog(int id)
    {
    	switch(id)
    	{
	    	case DIALOG_SET_ALARM_ID:
	    	{
	    		LayoutInflater factory = LayoutInflater.from(mContext);
				View layout = factory.inflate(R.layout.layout_set_alarm, null);
				mButtonDate = (Button)layout.findViewById(R.id.button_date);
				mButtonTime = (Button)layout.findViewById(R.id.button_time);
				mButtonDate.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						removeDialog(DIALOG_SET_DATE_ID);
						showDialog(DIALOG_SET_DATE_ID);
					}});
				mButtonTime.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						removeDialog(DIALOG_SET_TIME_ID);
						showDialog(DIALOG_SET_TIME_ID);
					}});
				mAlarmTime = Calendar.getInstance();
				mAlarmTime.set(Calendar.SECOND, 0);
				mAlarmTime.set(Calendar.MILLISECOND, 0);
				mButtonDate.setText(mAlarmTime.get(Calendar.YEAR) + getString(R.string.div) 
						+ ((mAlarmTime.get(Calendar.MONTH) + 1) < 10 ? ("0" + (mAlarmTime.get(Calendar.MONTH) + 1)) : (mAlarmTime.get(Calendar.MONTH) + 1))
						+ getString(R.string.div)
						+ (mAlarmTime.get(Calendar.DAY_OF_MONTH) < 10 ? ("0" + mAlarmTime.get(Calendar.DAY_OF_MONTH)) : mAlarmTime.get(Calendar.DAY_OF_MONTH)) + "  " 
						+ "  " + ENotesActivity.mWeek[mAlarmTime.get(Calendar.DAY_OF_WEEK) - 1]);
				mButtonTime.setText((mAlarmTime.get(Calendar.HOUR_OF_DAY) < 10 ? ("0" + mAlarmTime.get(Calendar.HOUR_OF_DAY)) : mAlarmTime.get(Calendar.HOUR_OF_DAY))
						+ ":" + (mAlarmTime.get(Calendar.MINUTE) < 10 ? ("0" + mAlarmTime.get(Calendar.MINUTE)) : mAlarmTime.get(Calendar.MINUTE)));
				return new AlertDialog.Builder(mContext)
	    		.setTitle(R.string.set_alarm)
	    		.setView(layout)
	    		.setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						Calendar c = Calendar.getInstance();
						if(c.after(mAlarmTime))
						{
							Toast.makeText(mContext, getString(R.string.overdue), Toast.LENGTH_LONG).show();
						}
						else
						{
							mAlarm = mAlarmTime.getTimeInMillis();
							mTextViewAlarm.setText(mAlarmTime.get(Calendar.YEAR) + getString(R.string.div) 
									+ ((mAlarmTime.get(Calendar.MONTH) + 1) < 10 ? ("0" + (mAlarmTime.get(Calendar.MONTH) + 1)) : (mAlarmTime.get(Calendar.MONTH) + 1))
									+ getString(R.string.div)
									+ (mAlarmTime.get(Calendar.DAY_OF_MONTH) < 10 ? ("0" + mAlarmTime.get(Calendar.DAY_OF_MONTH)) : mAlarmTime.get(Calendar.DAY_OF_MONTH)) + "  "
									+ (mAlarmTime.get(Calendar.HOUR_OF_DAY) < 10 ? ("0" + mAlarmTime.get(Calendar.HOUR_OF_DAY)) : mAlarmTime.get(Calendar.HOUR_OF_DAY))
									+ ":" + (mAlarmTime.get(Calendar.MINUTE) < 10 ? ("0" + mAlarmTime.get(Calendar.MINUTE)) : mAlarmTime.get(Calendar.MINUTE)) + "  "
									+ ENotesActivity.mWeek[mAlarmTime.get(Calendar.DAY_OF_WEEK) - 1]
									);
						}
					}
				})
				.setNeutralButton(R.string.clear,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						mAlarm = 0;
						mTextViewAlarm.setText(getString(R.string.no_alarm));
						cancelAlarm(mContext, mNoteId);
					}
				})
	    		.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
					}
				})
	    		.create();
	    	}
	    	case DIALOG_SET_DATE_ID:
	    	{
	    		return new DatePickerDialog(mContext, mDateSetListener, mAlarmTime.get(Calendar.YEAR), 
	    				mAlarmTime.get(Calendar.MONTH), mAlarmTime.get(Calendar.DAY_OF_MONTH));
	    	}
	    	case DIALOG_SET_TIME_ID:
	    	{
	    		return new TimePickerDialog(this, mSetTimeListener, mAlarmTime.get(Calendar.HOUR_OF_DAY), mAlarmTime.get(Calendar.MINUTE), true);
	    	}
	    	case DIALOG_SELECT_FOLDER_ID:
	    	{
	    		final Cursor c = getContentResolver().query(NotesProvider.CONTENT_URI_FOLDERS, null, null, null, null);
	    		String[] name = new String[c.getCount()];
	    		for(int i = 0; i < c.getCount(); i++)
	    		{
	    			c.moveToPosition(i);
	    			name[i] = c.getString(c.getColumnIndex(NotesDatabaseHelper.FOLDER_NAME));
	    		}
	    		return new AlertDialog.Builder(mContext)
	    		.setTitle(R.string.select_folder)
	    		.setItems(name, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						c.moveToPosition(whichButton);
						mTextViewFolder.setText(c.getString(c.getColumnIndex(NotesDatabaseHelper.FOLDER_NAME)));
						mFolderId = c.getInt(c.getColumnIndex("_id"));
						
					}})
	    		.create();
	    	}
    	}
    	return super.onCreateDialog(id);
    }
	
	private OnDateSetListener mDateSetListener = new OnDateSetListener(){

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			mAlarmTime.set(Calendar.YEAR, year);
			mAlarmTime.set(Calendar.MONTH, monthOfYear);
			mAlarmTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			mButtonDate.setText(mAlarmTime.get(Calendar.YEAR) + getString(R.string.div) 
					+ ((mAlarmTime.get(Calendar.MONTH) + 1) < 10 ? ("0" + (mAlarmTime.get(Calendar.MONTH) + 1)) : (mAlarmTime.get(Calendar.MONTH) + 1))
					+ getString(R.string.div)
					+ (mAlarmTime.get(Calendar.DAY_OF_MONTH) < 10 ? ("0" + mAlarmTime.get(Calendar.DAY_OF_MONTH)) : mAlarmTime.get(Calendar.DAY_OF_MONTH)) + "  " 
					+ "  " + ENotesActivity.mWeek[mAlarmTime.get(Calendar.DAY_OF_WEEK) - 1]);
		}
		
	};
	
	private TimePickerDialog.OnTimeSetListener mSetTimeListener =
        new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				mAlarmTime.set(Calendar.HOUR_OF_DAY, arg1);
				mAlarmTime.set(Calendar.MINUTE, arg2);
				mButtonTime.setText((mAlarmTime.get(Calendar.HOUR_OF_DAY) < 10 ? ("0" + mAlarmTime.get(Calendar.HOUR_OF_DAY)) : mAlarmTime.get(Calendar.HOUR_OF_DAY))
						+ ":" + (mAlarmTime.get(Calendar.MINUTE) < 10 ? ("0" + mAlarmTime.get(Calendar.MINUTE)) : mAlarmTime.get(Calendar.MINUTE)));
			}
        };

	public static void setAlarm(Context context, int id, long time)
	{
		Intent intent = new Intent(context, AlarmReceiver.class);
    	intent.putExtra("_id", id);
    	PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    	AlarmManager am = (AlarmManager)context.getSystemService(ALARM_SERVICE);
    	am.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
	}
	
	public static void cancelAlarm(Context context, int id)
	{
		Intent intent = new Intent(context, AlarmReceiver.class);
    	intent.putExtra("_id", id);
    	PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    	AlarmManager am = (AlarmManager)context.getSystemService(ALARM_SERVICE);
    	am.cancel(pendingIntent);
	}
    
}