package com.exuan.alarm;

import java.io.IOException;
import java.util.Calendar;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ESetAlarmActivity extends PreferenceActivity {
	private int mID;
	private int mHour;
	private int mMinute;
	private String mRepeat;
	private int mDelay;
	private String mAlertInfo;
	private String mRingPath;
	private boolean mIsRing;
	private boolean mIsVibrate;
	private boolean mIsActive;
	private static final int SET_TIME_DIALOG_ID = 0;
	private static final int SET_DELAY_DIALOG_ID = 1;
	private static final int SET_RING_DIALOG_ID = 2;
	private static final int CUSTOM_RING_DIALOG_ID = 3;
	private static final int EXTERNAL_RING_DIALOG_ID = 4;
	private static final int SET_REPEAT_DIALOG_ID = 5;
	private static final int CUSTOM_REPEAT_DIALOG_ID = 6;
	
	private static final int MAX_DELAY_TIME = 60;
	private static final int MIN_DELAY_TIME = 2;
	public static final int ALARM_NOTIFICATION_ID = 0;
	private SeekBar mDelaySeekBar;
	private TextView mDelayTime;
	private Preference mDialogSetTime;
	private Preference mListRepeat;
	private Preference mDialogSetDelay;
	private EditTextPreference mEditTextAlertInfo;
	private Preference mRingtoneSelect;
	private CheckBoxPreference mCheckBoxIsRing;
	private CheckBoxPreference mCheckBoxIsVibrate;
	private Button mStoreButton;
	private Button mCancelButton;
	private Context mContext;
    private String[] mWeekArray;
    private String[] mCustomRingtoneArray;
    private static final String DEFAULT_RING_PATH = "android.resource://com.exuan.alarm/raw/";
    private static final String DEFAULT_RING = "android.resource://com.exuan.alarm/raw/luffy";
    private static final String DEFAULT_RING_NAME = "luffy";
    private static final String EXTERNAL_RING_PATH = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString() + "/";
    private boolean mIsNew;
    private int mCustomRingtoneID;
    private static final int REQUEST_CODE_RINGTONE = 0;
    private MediaPlayer mPlayer;
    private Cursor mExternalCursor;
    private int mExternalId;
    private int mRepeatId;
    private static final String[] mRepeatDigit = {"0000000", "1111111", "1111100", "0000011"};
    private String[] mRepeatArray;
    private boolean[] mIsChecked = new boolean[7];
    
	protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.alarm_preference);
        setContentView(R.layout.layout_set_alarm);
        mContext = this;
        mRepeatArray = mContext.getResources().getStringArray(R.array.repeat_array);
        mWeekArray = mContext.getResources().getStringArray(R.array.week_short_array);
        mCustomRingtoneArray = mContext.getResources().getStringArray(R.array.ringtone_array);
        
        mStoreButton = (Button)findViewById(R.id.button_store_alarm);
        mCancelButton = (Button)findViewById(R.id.button_cancel);
        android.view.ViewGroup.LayoutParams paral = mStoreButton.getLayoutParams();
        paral.width = getWindowManager().getDefaultDisplay().getWidth() / 2;
        mStoreButton.setLayoutParams(paral);
        android.view.ViewGroup.LayoutParams parar = mCancelButton.getLayoutParams();
        parar.width = getWindowManager().getDefaultDisplay().getWidth() / 2;
        mCancelButton.setLayoutParams(parar);
        
        mDialogSetTime = findPreference("DialogSetTime");
        mListRepeat = findPreference("ListRepeat");
        mDialogSetDelay = findPreference("DialogSetDelay");
        mEditTextAlertInfo = (EditTextPreference) findPreference("EditTextAlertInfo");
        mRingtoneSelect = findPreference("RingtoneSelect");
        mCheckBoxIsRing = (CheckBoxPreference) findPreference("CheckBoxIsRing");
        mCheckBoxIsVibrate = (CheckBoxPreference) findPreference("CheckBoxIsVibrate");
        mEditTextAlertInfo.setOnPreferenceChangeListener(mOnPrefernceChangeListener);
        mCheckBoxIsRing.setOnPreferenceChangeListener(mOnPrefernceChangeListener);
        mCheckBoxIsVibrate.setOnPreferenceChangeListener(mOnPrefernceChangeListener);
        mListRepeat.setOnPreferenceChangeListener(mOnPrefernceChangeListener);
        
        Bundle extras = getIntent().getExtras();
        mIsNew = extras.getBoolean("isNew", true);
        mID = extras.getInt("_id", -1);
        if(mIsNew)
        {
        	setDefaultValue();
        }
        else
        {
        	setPreferenceValue();
        }
        //mListRepeat.setInitValues(mRepeat);
        updateView();
        
        mStoreButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				sotreAlarmSetting();
				int[] time = registerAlarm(mContext, mHour, mMinute, mID, mRepeat);
				makeToast(mContext, time);
				SharedPreferences pref = mContext.getSharedPreferences(ESettingActivity.PREFERENCES_NAME, MODE_PRIVATE);
				boolean isNotify = pref.getBoolean(ESettingActivity.KEY_NOTIFY, true);
				if(isNotify)
				{
					setNotification(mContext, true);
				}
				
				finish();
				return;
			}});
        
        mCancelButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
				return;
			}});
	}
	
	private OnPreferenceChangeListener mOnPrefernceChangeListener = new OnPreferenceChangeListener() 
    {
		@Override
		public boolean onPreferenceChange(Preference arg0, Object arg1) {
			// TODO Auto-generated method stub
			if(arg0.getKey().equals("EditTextAlertInfo"))
			{
				mAlertInfo = (String)arg1;
				mEditTextAlertInfo.setSummary(mAlertInfo);
				return true;
			}
			if(arg0.getKey().equals("CheckBoxIsRing"))
			{
				mIsRing = ((Boolean)arg1).booleanValue();
				mCheckBoxIsRing.setChecked(mIsRing);
				return true;
			}/*
			if(arg0.getKey().equals("ListRepeat"))
			{
				mRepeat = (String)arg1;
				return true;
			}*/
			if(arg0.getKey().equals("CheckBoxIsVibrate"))
			{
				mIsVibrate = ((Boolean)arg1).booleanValue();
				mCheckBoxIsVibrate.setChecked(mIsVibrate);
				return true;
			}
			return false;
		}
    };
	
	@Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) 
    {
        super.onPreferenceTreeClick(preferenceScreen, preference);
        if(preference.getKey().equals("DialogSetTime"))
        {
        	removeDialog(SET_TIME_DIALOG_ID);
            showDialog(SET_TIME_DIALOG_ID);
        }
        if(preference.getKey().equals("ListRepeat"))
        {
        	removeDialog(SET_REPEAT_DIALOG_ID);
            showDialog(SET_REPEAT_DIALOG_ID);
        }
        if(preference.getKey().equals("DialogSetDelay"))
        {
        	removeDialog(SET_DELAY_DIALOG_ID);
            showDialog(SET_DELAY_DIALOG_ID);
        }
        if(preference.getKey().equals("RingtoneSelect"))
        {
        	removeDialog(SET_RING_DIALOG_ID);
            showDialog(SET_RING_DIALOG_ID);
        }
        return false;
    }
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(mPlayer != null)
		{
			mPlayer.stop();
			mPlayer = null;
		}
	}
	
	@Override
    protected Dialog onCreateDialog(int id) {
		switch(id)
		{
			case SET_TIME_DIALOG_ID:
			{
				return new TimePickerDialog(this, mSetTimeListener, mHour, mMinute, true);
			}
			case SET_DELAY_DIALOG_ID:
			{
				LayoutInflater factory = LayoutInflater.from(this);
				View layout = factory.inflate(R.layout.delay_setting_bar, null);
				mDelaySeekBar = (SeekBar) layout.findViewById(R.id.seekbar);
				mDelayTime = (TextView) layout.findViewById(R.id.status);
				mDelayTime.setText(mDelay + " " + getString(R.string.minutes));
				mDelaySeekBar.setMax(MAX_DELAY_TIME - MIN_DELAY_TIME);
				mDelaySeekBar.setProgress(mDelay - MIN_DELAY_TIME);

				mDelaySeekBar
						.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
							public void onProgressChanged(SeekBar seekBar,
									int progress, boolean fromUser) {
								mDelayTime.setText((progress + MIN_DELAY_TIME)
										+ " " + getString(R.string.minutes));
							}

							public void onStartTrackingTouch(SeekBar seekBar) {
							}

							public void onStopTrackingTouch(SeekBar seekBar) {
							}
						});

				AlertDialog.Builder adb = new AlertDialog.Builder(this);
				adb = adb.setTitle(R.string.delay_time).setView(layout)
						.setPositiveButton(R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										mDelay = mDelaySeekBar.getProgress()
												+ MIN_DELAY_TIME;
										String delay = Integer.toString(mDelay) + " " + getString(R.string.minutes);
								        mDialogSetDelay.setSummary(delay);
									}
								}).setNegativeButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {

									}
								});

				return adb.create();
			}
			case SET_REPEAT_DIALOG_ID:
			{
				return new AlertDialog.Builder(this)
				.setTitle(R.string.repeat)
				.setSingleChoiceItems(R.array.repeat_array, mRepeatId, 
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						mRepeatId = whichButton;
					}
				})
				.setPositiveButton(R.string.ok,
					  new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						if(4 == mRepeatId)
						{
							removeDialog(CUSTOM_REPEAT_DIALOG_ID);
							showDialog(CUSTOM_REPEAT_DIALOG_ID);
						}
						else
						{
							mRepeat = mRepeatDigit[mRepeatId];
							mListRepeat.setSummary(mRepeatArray[mRepeatId]);
						}
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
			case CUSTOM_REPEAT_DIALOG_ID:
			{
				for(int i = 0; i < mRepeat.length(); i++)
				{
					char c  = mRepeat.charAt(i);
					if('1' == c)
					{
						mIsChecked[i] = true;
					}
					else
					{
						mIsChecked[i] = false;
					}
				}
				return new AlertDialog.Builder(this)
				.setTitle(R.string.repeat)
				.setMultiChoiceItems(R.array.week_array, mIsChecked, 
                new DialogInterface.OnMultiChoiceClickListener() {
            	public void  onClick(DialogInterface dialog, 
                                 int which,
                                 boolean isChecked) {
            		mIsChecked[which] = isChecked;
            	}})
            	.setPositiveButton(R.string.ok,
  					  new DialogInterface.OnClickListener() {
  					public void onClick(DialogInterface dialog,
  							int whichButton) {
  						char[] c = {'0','0','0','0','0','0','0'};
  						for(int i = 0; i < 7; i++)
  						{
  							if(mIsChecked[i])
  							{
  								c[i] = '1';
  							}
  						}
  						mRepeat = new String(c);
  						StringBuilder ret = new StringBuilder();
  				    	for(int i = 0; i < mRepeatDigit.length; i++)
  				    	{
  				    		if(mRepeat.equals(mRepeatDigit[i]))
  				    		{
  				    			ret.append(mRepeatArray[i]);
  				    			mRepeatId = i;
  				    		}
  				    	}
  				    	if(ret.length() <= 0)
  				    	{
  				    		mRepeatId = 4;
  				    		for(int i = 0; i < mRepeat.length(); i++)
  				    		{
  				    			char c1  = mRepeat.charAt(i);
  				    			if('1' == c1)
  				    			{
  				    				ret.append(mWeekArray[i]);
  				    				ret.append(", ");
  				    			}
  				    		}
  				    		ret.delete(ret.lastIndexOf(","), ret.length() - 1);
  				    	}
  				        mListRepeat.setSummary(ret.toString());
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
			case SET_RING_DIALOG_ID:
			{
				return new AlertDialog.Builder(this)
				.setTitle(R.string.select_ringtone)
				.setItems(R.array.ringtone_type, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						if(whichButton == 0)
						{
							showDialog(CUSTOM_RING_DIALOG_ID);
						}
						else if(whichButton == 1)
						{
							Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
							intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
							intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
							intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
							startActivityForResult(intent, REQUEST_CODE_RINGTONE);
						}
						else if(whichButton == 2)
						{
							try
				        	{
					        	String[] mCursorCols = new String[]{MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME
					        			,MediaStore.Audio.Media.MIME_TYPE}; 
					        	Uri MUSIC_URL = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
					        	String selection = MediaStore.Audio.Media.MIME_TYPE + " = 'audio/x-mpeg' OR "
					        	+ MediaStore.Audio.Media.MIME_TYPE + " = 'audio/mpeg' OR "
					        	+ MediaStore.Audio.Media.MIME_TYPE + " = 'audio/mp3'";
					        	String orderBy = MediaStore.Audio.Media.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
					        	mExternalCursor = getContentResolver().query(MUSIC_URL, mCursorCols, selection, null, orderBy);
					        	if(mExternalCursor != null && mExternalCursor.getCount() > 0)
					        	{
					        		mExternalCursor.moveToPosition(0);
					        		showDialog(EXTERNAL_RING_DIALOG_ID);
					        	}
					        	else
					        	{
					        		Toast.makeText(mContext, getString(R.string.insert_sd_card), Toast.LENGTH_LONG).show();
					        	}
				        	}
				        	catch(Exception e)
				        	{
				        		Toast.makeText(mContext, getString(R.string.insert_sd_card), Toast.LENGTH_LONG).show();
				        	}
						}
					}})
				.create();
			}
			case CUSTOM_RING_DIALOG_ID:
			{
				return new AlertDialog.Builder(this)
				.setTitle(R.string.select_ringtone)
				.setSingleChoiceItems(R.array.ringtone_array, mCustomRingtoneID, 
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						if(mPlayer != null)
						{
							mPlayer.stop();
							mPlayer.release();
							mPlayer = null;
						}
						mCustomRingtoneID = whichButton;
						mPlayer = new MediaPlayer();
						mPlayer.setOnErrorListener(new OnErrorListener() {
			    	        public boolean onError(MediaPlayer mp, int what, int extra) 
			    	        {
			    	        	mp.stop();
			    	            mp.release();
			    	            mPlayer = null;
			    	            return true;
			    	        }
		    	        });
						try 
			    		{
							mPlayer.setDataSource(mContext, Uri.parse(DEFAULT_RING_PATH + mCustomRingtoneArray[mCustomRingtoneID]));
		    				mPlayer.setAudioStreamType(AudioManager.STREAM_RING);
							mPlayer.prepare();
							mPlayer.start();
			    		}
						catch (IllegalStateException e) 
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				})
				.setPositiveButton(R.string.ok,
					  new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						if(mPlayer != null)
						{
							mPlayer.stop();
							mPlayer.release();
							mPlayer = null;
						}
						mRingPath = DEFAULT_RING_PATH + mCustomRingtoneArray[mCustomRingtoneID];
						String ringtone = "";
				        if(mRingPath.contains(DEFAULT_RING_PATH))
				        {	
				        	ringtone = mRingPath.substring(mRingPath.lastIndexOf("/") + 1);
				        }
				        else
				        {
				        	ringtone = RingtoneManager.getRingtone(mContext, Uri.parse(mRingPath)).getTitle(mContext);
				        }
				        mRingtoneSelect.setSummary(ringtone);
					}
				})
				.setNegativeButton(R.string.cancel,
					  new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						if(mPlayer != null)
						{
							mPlayer.stop();
							mPlayer.release();
							mPlayer = null;
						}
					}
				})
				.create();
			}
			case EXTERNAL_RING_DIALOG_ID:
			{
				return new AlertDialog.Builder(this)
				.setTitle(R.string.select_ringtone)
				.setSingleChoiceItems(mExternalCursor, mExternalId, MediaStore.Audio.Media.DISPLAY_NAME,
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						if(mPlayer != null)
						{
							mPlayer.stop();
							mPlayer.release();
							mPlayer = null;
						}
						mExternalId = whichButton;
						mPlayer = new MediaPlayer();
						mPlayer.setOnErrorListener(new OnErrorListener() {
			    	        public boolean onError(MediaPlayer mp, int what, int extra) 
			    	        {
			    	        	mp.stop();
			    	            mp.release();
			    	            mPlayer = null;
			    	            return true;
			    	        }
		    	        });
						try 
			    		{
							mExternalCursor.moveToPosition(mExternalId);
							int id = mExternalCursor.getInt(mExternalCursor.getColumnIndex(MediaStore.Audio.Media._ID));
							mPlayer.setDataSource(mContext, Uri.parse(EXTERNAL_RING_PATH + id));
		    				mPlayer.setAudioStreamType(AudioManager.STREAM_RING);
							mPlayer.prepare();
							mPlayer.start();
			    		}
						catch (IllegalStateException e) 
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				})
				.setPositiveButton(R.string.ok,
					  new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						if(mPlayer != null)
						{
							mPlayer.stop();
							mPlayer.release();
							mPlayer = null;
						}
						mExternalCursor.moveToPosition(mExternalId);
						int id = mExternalCursor.getInt(mExternalCursor.getColumnIndex(MediaStore.Audio.Media._ID));
						mRingPath = EXTERNAL_RING_PATH + id;
						String ringtone = "";
						ringtone = mExternalCursor.getString(mExternalCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
				        mRingtoneSelect.setSummary(ringtone);
					}
				})
				.setNegativeButton(R.string.cancel,
					  new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						if(mPlayer != null)
						{
							mPlayer.stop();
							mPlayer.release();
							mPlayer = null;
						}
					}
				})
				.create();
			}
		}
		return super.onCreateDialog(id);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode != RESULT_OK)
		{
			return;
		}
		else
		{
			Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
			if(uri != null)
			{
				mRingPath = uri.toString();
				String ringtone = "";
		        if(mRingPath.contains(DEFAULT_RING_PATH))
		        {	
		        	ringtone = mRingPath.substring(mRingPath.lastIndexOf("/") + 1);
		        }
		        else
		        {
		        	ringtone = RingtoneManager.getRingtone(mContext, Uri.parse(mRingPath)).getTitle(mContext);
		        }
		        mRingtoneSelect.setSummary(ringtone);
			}
		}
	}
	
	private TimePickerDialog.OnTimeSetListener mSetTimeListener =
        new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				mHour = arg1;
				mMinute = arg2;
				String time = (mHour < 10 ? ("0" + Integer.toString(mHour)) : Integer.toString(mHour))+ ":" + (mMinute < 10 ? ("0" + Integer.toString(mMinute)) : Integer.toString(mMinute));
		    	mDialogSetTime.setSummary(time);
			}
        };
        
    
        
    private void sotreAlarmSetting()
    {
    	ContentValues values = new ContentValues();
    	values.put(AlarmDatabaseHelper.HOUR, mHour);
    	values.put(AlarmDatabaseHelper.MINUTE, mMinute);
    	values.put(AlarmDatabaseHelper.REPEAT, mRepeat);
    	values.put(AlarmDatabaseHelper.DELAY, mDelay);
    	values.put(AlarmDatabaseHelper.ALERT_INFO, mAlertInfo);
    	values.put(AlarmDatabaseHelper.ALERT_RING, mRingPath);
    	values.put(AlarmDatabaseHelper.IS_RING, mIsRing);
    	values.put(AlarmDatabaseHelper.IS_VIBRATE, mIsVibrate);
    	values.put(AlarmDatabaseHelper.IS_ACTIVE, mIsActive);
    	if(mIsNew)
    	{
    		this.getContentResolver().insert(AlarmInfoProvider.CONTENT_URI_ALARMS, values);
    		Cursor c = this.getContentResolver().query(AlarmInfoProvider.CONTENT_URI_ALARMS, null, null, null, null);
    		c.moveToLast();
    		mID = c.getInt(c.getColumnIndex("_id"));
    		c.close();
    	}
    	else
    	{
    		this.getContentResolver().update(AlarmInfoProvider.CONTENT_URI_ALARMS, values, "_id = " + mID, null);
    	}
    }
    
    private void setDefaultValue()
    {
    	Calendar c = Calendar.getInstance();
    	mHour = c.get(Calendar.HOUR_OF_DAY);
    	mMinute = c.get(Calendar.MINUTE);
    	mRepeat = "0000000";
    	mDelay = 5;
    	mAlertInfo = "";
    	mRingPath = DEFAULT_RING_PATH + mCustomRingtoneArray[0];
    	mIsRing = true;
    	mIsVibrate = true;
    	mIsActive = true;
    }
    
    private void setPreferenceValue()
    {
    	Cursor c = getContentResolver().query(AlarmInfoProvider.CONTENT_URI_ALARMS, null, "_id = " + mID, null, null);
    	if(null != c && c.moveToNext())
    	{
	    	mHour = c.getInt(c.getColumnIndex(AlarmDatabaseHelper.HOUR));
	    	mMinute = c.getInt(c.getColumnIndex(AlarmDatabaseHelper.MINUTE));
	    	mRepeat = c.getString(c.getColumnIndex(AlarmDatabaseHelper.REPEAT));
	    	mDelay = c.getInt(c.getColumnIndex(AlarmDatabaseHelper.DELAY));
	    	mAlertInfo = c.getString(c.getColumnIndex(AlarmDatabaseHelper.ALERT_INFO));
	    	mRingPath = c.getString(c.getColumnIndex(AlarmDatabaseHelper.ALERT_RING));
	    	mIsRing = (c.getInt(c.getColumnIndex(AlarmDatabaseHelper.IS_RING)) == 1);
	    	mIsVibrate = (c.getInt(c.getColumnIndex(AlarmDatabaseHelper.IS_VIBRATE)) == 1);
	    	mIsActive = true;
    	}
    	c.close();
    }
    
    private void updateView()
    {
    	String time = (mHour < 10 ? ("0" + Integer.toString(mHour)) : Integer.toString(mHour))+ ":" + (mMinute < 10 ? ("0" + Integer.toString(mMinute)) : Integer.toString(mMinute));
    	mDialogSetTime.setSummary(time);
    	StringBuilder ret = new StringBuilder();
    	for(int i = 0; i < mRepeatDigit.length; i++)
    	{
    		if(mRepeat.equals(mRepeatDigit[i]))
    		{
    			ret.append(mRepeatArray[i]);
    			mRepeatId = i;
    		}
    	}
    	if(ret.length() <= 0)
    	{
    		mRepeatId = 4;
    		for(int i = 0; i < mRepeat.length(); i++)
    		{
    			char c  = mRepeat.charAt(i);
    			if('1' == c)
    			{
    				ret.append(mWeekArray[i]);
    				ret.append(", ");
    			}
    		}
    		ret.delete(ret.lastIndexOf(","), ret.length() - 1);
    	}
        mListRepeat.setSummary(ret.toString());
        String delay = Integer.toString(mDelay) + " " + getString(R.string.minutes);
        mDialogSetDelay.setSummary(delay);
        mEditTextAlertInfo.setSummary(mAlertInfo);
        String ringtone = "";
        if(mRingPath.contains(DEFAULT_RING_PATH))
        {	
        	ringtone = mRingPath.substring(mRingPath.lastIndexOf("/") + 1);
        }
        else if(mRingPath.contains(EXTERNAL_RING_PATH))
        {
        	String id = mRingPath.substring(mRingPath.lastIndexOf("/") + 1);
        	String[] mCursorCols = new String[]{MediaStore.Audio.Media.DISPLAY_NAME}; 
        	Uri MUSIC_URL = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        	String selection = MediaStore.Audio.Media._ID + " = '" + id + "'";
        	Cursor c = getContentResolver().query(MUSIC_URL, mCursorCols, selection, null, null);
        	if(c != null && c.getCount() > 0)
        	{
        		c.moveToNext();
        		ringtone = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
        	}
        	else
        	{
        		mRingPath = DEFAULT_RING;
        		ringtone = DEFAULT_RING_NAME;
        		ContentValues values = new ContentValues();
            	values.put(AlarmDatabaseHelper.ALERT_RING, mRingPath);
            	this.getContentResolver().update(AlarmInfoProvider.CONTENT_URI_ALARMS, values, "_id = " + mID, null);
        	}
        }
        else 
        {
        	ringtone = RingtoneManager.getRingtone(mContext, Uri.parse(mRingPath)).getTitle(mContext);
        }
        mRingtoneSelect.setSummary(ringtone);
        mCheckBoxIsRing.setChecked(mIsRing);
        mCheckBoxIsVibrate.setChecked(mIsVibrate);
    }
    
    
    public static int[] registerAlarm(Context context, int hour, int minute, int id, String repeat)
    {
    	Calendar c = Calendar.getInstance();
    	Calendar cu = Calendar.getInstance();
    	int week = c.get(Calendar.DAY_OF_WEEK) == 1 ? 7 : (c.get(Calendar.DAY_OF_WEEK) - 1);
    	int roll = 0;
    	
    	int i = week - 1;
    	cu.set(Calendar.HOUR_OF_DAY, hour);
    	cu.set(Calendar.MINUTE, minute);
    	cu.set(Calendar.SECOND, 0);
    	cu.set(Calendar.MILLISECOND, 0);
    	if(c.after(cu))
    	{
    		if(i == 6)
    		{
    			i = 0;
    		}
    		else
    		{
    			i++;
    		}
    		roll++;
    	}
    	if(!repeat.equals("0000000"))
    	{
			while(true)
			{
				char ch  = repeat.charAt(i);
				if('1' == ch)
				{
					break;
				}
				if(i == 6)
				{
					i = 0;
				}
				else
				{
					i++;
				}
				roll++;
			}
    	}
    	
    	cu.add(Calendar.DAY_OF_MONTH, roll);
    	Intent intent = new Intent(context, AlarmReceiver.class);
    	intent.putExtra("_id", id);
    	PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    	AlarmManager am = (AlarmManager)context.getSystemService(ALARM_SERVICE);
    	am.set(AlarmManager.RTC_WAKEUP, cu.getTimeInMillis(), pendingIntent);
    	int[] time= new int[4];
    	if(cu.get(Calendar.SECOND) >= c.get(Calendar.SECOND))
    	{
    		
    		time[3] = cu.get(Calendar.SECOND) - c.get(Calendar.SECOND);
    		time[2] = cu.get(Calendar.MINUTE);
    	}
    	else
    	{
    		time[3] = 60 + cu.get(Calendar.SECOND) - c.get(Calendar.SECOND);
    		time[2] = cu.get(Calendar.MINUTE) - 1;
    	}
    	if(time[2] >= c.get(Calendar.MINUTE))
    	{
    		time[2] -= c.get(Calendar.MINUTE);
    		time[1] = cu.get(Calendar.HOUR_OF_DAY);
    	}
    	else
    	{
    		time[2] = 60 + time[2] - c.get(Calendar.MINUTE);
    		time[1] = cu.get(Calendar.HOUR_OF_DAY) - 1;
    	}
    	if(time[1] >= c.get(Calendar.HOUR_OF_DAY))
    	{
    		time[1] -= c.get(Calendar.HOUR_OF_DAY);
    		time[0] = roll;
    	}
    	else
    	{
    		time[1] = 24 + time[1] - c.get(Calendar.HOUR_OF_DAY);
    		time[0] = roll - 1;
    	}
    	return time;
    }
    
    public static void registerAlarmRepeat(Context context, int id)
    {
    	Cursor cursor = context.getContentResolver().query(AlarmInfoProvider.CONTENT_URI_ALARMS, null, "_id = " + id, null, null);
    	if(cursor != null && cursor.getCount() > 0)
    	{
    		cursor.moveToNext();
    		int delay = cursor.getInt(cursor.getColumnIndex(AlarmDatabaseHelper.DELAY));
    		Calendar c = Calendar.getInstance();
        	c.add(Calendar.MINUTE, delay);
        	Intent intent = new Intent(context, AlarmReceiver.class);
        	intent.putExtra("_id", id);
        	PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        	AlarmManager am = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        	am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    	}
    	cursor.close();
    }
    
    public static void cancelAlarm(Context context, int id)
    {
    	Intent intent = new Intent(context, AlarmReceiver.class);
    	intent.putExtra("_id", id);
    	PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    	AlarmManager am = (AlarmManager)context.getSystemService(ALARM_SERVICE);
    	am.cancel(pendingIntent);
    }
    
    public static void makeToast(Context context, int[] time)
    {
    	if(time[0] == 0 && time[1] == 0 && time[2] == 0)
    	{
    		Toast.makeText(context, context.getString(R.string.in_one_minute), Toast.LENGTH_LONG).show();
    		return;
    	}
    	String day = (time[0] == 0) ? "" : 
    		(time[0] == 1) ? (time[0] + " " + context.getString(R.string.day)) : 
    		(time[0] + " " + context.getString(R.string.days));
    	
    	String hour = (time[1] == 0) ? "" : 
        	(time[1] == 1) ? (time[1] + " " + context.getString(R.string.hour)) : 
        	(time[1] + " " + context.getString(R.string.hours));
        		
        String minute = (time[2] == 0) ? "" : 
            (time[2] == 1) ? (time[2] + " " + context.getString(R.string.minute)) : 
            (time[2] + " " + context.getString(R.string.minutes));
    	
        int index = -1;
        for(int i = 0; i < 3; i++)
        {
        	if(time[i] > 0)
        		index++;
        }
        String[] text = new String[index + 1];
        int j = 0;
        if(day != "")
        {
        	text[j++] = day;
        }
        if(hour != "")
        {
        	text[j++] = hour;
        }
        if(minute != "")
        {
        	text[j++] = minute;
        }
            
        String[] formats = context.getResources().getStringArray(R.array.alarm_toast);
        String toast = "";
        if(index == 0)
        {
        	toast = String.format(formats[0], text[0]);
        }
        if(index == 1)
        {
        	toast = String.format(formats[1], text[0], text[1]);
        }
        if(index == 2)
        {
        	toast = String.format(formats[2], text[0], text[1], text[2]);
        }
        Toast.makeText(context, toast, Toast.LENGTH_LONG).show();
    }
    
    public static void setNotification(Context context, boolean enable)
    {
    	NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
		if(enable)
		{
	    	int icon = R.drawable.icon;
			CharSequence tickerText = context.getString(R.string.alarm_set);
			Notification  notification = new Notification(icon, tickerText, 0);
			notification.iconLevel = 1;
			CharSequence from = context.getString(R.string.alarm);        
			CharSequence message = context.getString(R.string.alarm_set);   
			Intent intent = new Intent(context, EAlarmActivity.class);        
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);        
			notification.setLatestEventInfo(context, from, message, contentIntent); 
			notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
			nm.notify(ALARM_NOTIFICATION_ID, notification);
		}
		else
		{
			nm.cancel(ALARM_NOTIFICATION_ID);
		}
    }
}