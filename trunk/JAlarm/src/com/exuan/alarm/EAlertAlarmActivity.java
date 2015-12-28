package com.exuan.alarm;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class EAlertAlarmActivity extends Activity
{
	public static final String LOGNAME = EAlertAlarmActivity.class.getSimpleName();
	private Context mContext;
	private int mID;
	private boolean mIsClick;
	private MediaPlayer mPlayer;
	private boolean mIsRing;
	private boolean mIsVibrate;
	private Vibrator mVibrator;
	private String mRepeat;
	private int mHour;
	private int mMinute;
	private int mDelay;
	private String mAlertInfo;
	private String mRingPath;
	private PowerManager.WakeLock mCpuWakeLock;
	private KeyguardLock mKeyguardLock;
	private Timer mTimer = new Timer();
	
	private static final int ALERT_ALARM_DIALOG_ID = 0;
	private static final int TOAST_DELAY = 0;
	private static final float IN_CALL_VOLUME = 0.125f;
	private TelephonyManager mTelephonyManager;
	private static final String IN_CALL_ALARM_PATH = "android.resource://com.exuan.alarm/raw/in_call_alarm";
	private static final String DEFAULT_ALARM_PATH = "android.resource://com.exuan.alarm/raw/luffy";
	
	private TeleListener mTeleListener;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext = this;
		mIsClick = false;
		mID = getIntent().getExtras().getInt("_id");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setLayout(0, 0);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
				  WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		if (mCpuWakeLock == null) 
		{
	        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
	        mCpuWakeLock = pm.newWakeLock(
	                PowerManager.PARTIAL_WAKE_LOCK |
	                PowerManager.ACQUIRE_CAUSES_WAKEUP |
	                PowerManager.ON_AFTER_RELEASE, LOGNAME);
	        mCpuWakeLock.acquire();
	        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);
	        mKeyguardLock  = keyguardManager.newKeyguardLock(LOGNAME);
	        mKeyguardLock.disableKeyguard();
		}
		Cursor cursor = getContentResolver().query(AlarmInfoProvider.CONTENT_URI_ALARMS, null, "_id = " + mID, null, null);
    	if(cursor != null && cursor.getCount() > 0)
    	{
    		cursor.moveToNext();
    		mIsRing = (cursor.getInt(cursor.getColumnIndex(AlarmDatabaseHelper.IS_RING)) == 1);
    		mIsVibrate = (cursor.getInt(cursor.getColumnIndex(AlarmDatabaseHelper.IS_VIBRATE)) == 1);
    		mRepeat = cursor.getString(cursor.getColumnIndex(AlarmDatabaseHelper.REPEAT));
    		mHour = cursor.getInt(cursor.getColumnIndex(AlarmDatabaseHelper.HOUR));
    		mMinute = cursor.getInt(cursor.getColumnIndex(AlarmDatabaseHelper.MINUTE));
    		mAlertInfo = cursor.getString(cursor.getColumnIndex(AlarmDatabaseHelper.ALERT_INFO));
    		mRingPath = cursor.getString(cursor.getColumnIndex(AlarmDatabaseHelper.ALERT_RING));
    		if(mAlertInfo == null || mAlertInfo.trim().length() == 0)
    		{
    			mAlertInfo = getString(R.string.alarm_ringing);
    		}
    		mDelay = cursor.getInt(cursor.getColumnIndex(AlarmDatabaseHelper.DELAY));
    		if(mIsRing)
	    	{
    				
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
	    			mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
	    			
	    			if (mTelephonyManager.getCallState() != TelephonyManager.CALL_STATE_IDLE)
	    			{
	    				mPlayer.setDataSource(mContext, Uri.parse(IN_CALL_ALARM_PATH));
	    				mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
    					mPlayer.setVolume(IN_CALL_VOLUME, IN_CALL_VOLUME);
    					mPlayer.setLooping(true);
						mPlayer.prepare();
						mPlayer.start();
						mTeleListener = new TeleListener();
						mTelephonyManager.listen(mTeleListener, PhoneStateListener.LISTEN_CALL_STATE);
	    			}
	    			else
	    			{
	    				mPlayer.setDataSource(mContext, Uri.parse(mRingPath));
	    				mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
	    				mPlayer.setLooping(true);
						mPlayer.prepare();
						mPlayer.start();
					}
				} catch (IllegalStateException e) 
				{
					// TODO Auto-generated catch block
				} catch (Exception e) {
					// TODO Auto-generated catch block
					try 
		    		{
						mPlayer.setDataSource(mContext, Uri.parse(DEFAULT_ALARM_PATH));
						mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
    					mPlayer.setLooping(true);
						mPlayer.prepare();
						mPlayer.start();
		    		} catch (Exception e1) {
		    		}
				}
	    	}
			if(mIsVibrate)
			{
				mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);   
				mVibrator.vibrate(new long[]{700,1500,700,1500}, 0); 
			}
			showDialog(ALERT_ALARM_DIALOG_ID);
			mTimer.schedule(new DelayTimerTask(mContext), 60 * 1000);
    	}
    	cursor.close();
	}
	
	private class TeleListener extends PhoneStateListener
	{
		@Override           
		public void onCallStateChanged(int state, String incomingNumber) 
		{   
			try 
			{                     
				switch (state) 
				{                  
					case TelephonyManager.CALL_STATE_IDLE:
					{
						if(mPlayer != null)
						{
							mPlayer.stop();
							mPlayer.release();
							mPlayer = null;
						}
						try 
			    		{
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
			    			mPlayer.setDataSource(mContext, Uri.parse(mRingPath));
			    			mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
			    			mPlayer.setLooping(true);
							mPlayer.prepare();
							mPlayer.start();
						} catch (IllegalStateException e) {
							// TODO Auto-generated catch block
						} catch (Exception e) {
							// TODO Auto-generated catch block
							try 
				    		{
								mPlayer.setDataSource(mContext, Uri.parse(DEFAULT_ALARM_PATH));
								mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
		    					mPlayer.setLooping(true);
								mPlayer.prepare();
								mPlayer.start();
				    		} 
							catch (Exception e1) 
							{
				    		}
						}
					}                      
					break;           
				}
			}
			catch (Exception e) 
			{                      
				e.printStackTrace();      
			}                
			super.onCallStateChanged(state, incomingNumber);   
		}
	}
	
	public void onStop()
	{
		super.onStop();
		if(!mIsClick)
		{
			delayAlarm(mContext);
			mTimer.cancel();
			mTimer = null;
			clearStatus();
			
			Toast.makeText(mContext, String.format(mContext.getString(R.string.push), mDelay), Toast.LENGTH_LONG).show();
			
		}
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		if(mTeleListener != null)
		{
			mTelephonyManager.listen(mTeleListener, PhoneStateListener.LISTEN_NONE);
			mTeleListener = null;
		}
		if(mPlayer != null)
		{
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}
		if(mVibrator != null)
		{
			mVibrator.cancel();
			mVibrator = null;
		}
		if (mCpuWakeLock != null) 
		{
			mCpuWakeLock.release();
			mCpuWakeLock = null;
        }
		if(mKeyguardLock != null)
		{
			mKeyguardLock.reenableKeyguard();
			mKeyguardLock = null;
		}
	}
	
	public Dialog onCreateDialog(int id)
	{
		switch(id)
		{
			case ALERT_ALARM_DIALOG_ID:
			{
				Calendar c = Calendar.getInstance();
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int minute = c.get(Calendar.MINUTE);
				String time = (hour < 10 ? ("0" + Integer.toString(hour)) : Integer.toString(hour))+ ":" + (minute < 10 ? ("0" + Integer.toString(minute)) : Integer.toString(minute));
				return new AlertDialog.Builder(mContext)
				.setTitle(time)
				.setIcon(R.drawable.icon)
				.setMessage(mAlertInfo)
				.setPositiveButton(R.string.delay,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										delayAlarm(mContext);
										Toast.makeText(mContext, String.format(mContext.getString(R.string.push), mDelay), Toast.LENGTH_LONG).show();
										mTimer.cancel();
										mTimer = null;
										clearStatus();
									}
								})
				.setNegativeButton(R.string.stop,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										stopAlarm(mContext);
										mTimer.cancel();
										mTimer = null;
										clearStatus();
									}
								})
				.setOnCancelListener(new OnCancelListener(){

					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						delayAlarm(mContext);
						Toast.makeText(mContext, String.format(mContext.getString(R.string.push), mDelay), Toast.LENGTH_LONG).show();
						mTimer.cancel();
						mTimer = null;
						clearStatus();
					}})
				.create();				
			}
		}
		return super.onCreateDialog(id);
	}
	
	private void delayAlarm(Context context)
	{
		ESetAlarmActivity.registerAlarmRepeat(context, mID);
	}
	
	private void stopAlarm(Context context)
	{
    	if(mRepeat.equals("0000000"))
    	{
    		ESetAlarmActivity.cancelAlarm(context, mID);
    		
    		Cursor cursor = context.getContentResolver().query(AlarmInfoProvider.CONTENT_URI_ALARMS, null, "_id = " + mID, null, null);
        	if(cursor != null && cursor.getCount() > 0)
        	{
        		cursor.moveToNext();
        		String repeat = cursor.getString(cursor.getColumnIndex(AlarmDatabaseHelper.REPEAT));
        		if(repeat.equals("0000000"))
        		{
        			ContentValues values = new ContentValues();
    				values.put(AlarmDatabaseHelper.IS_ACTIVE, false);
    				context.getContentResolver().update(AlarmInfoProvider.CONTENT_URI_ALARMS, values, "_id = " + mID, null);
    				Cursor c = context.getContentResolver().query(AlarmInfoProvider.CONTENT_URI_ALARMS, null, AlarmDatabaseHelper.IS_ACTIVE + " = '1'", null, null);
    				if(c != null)
    				{
    					if(c.getCount() == 0)
    					{
    						ESetAlarmActivity.setNotification(context, false);
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
        		}
        	}
        	cursor.close();
    	}
    	else
    	{
	    	ESetAlarmActivity.registerAlarm(context, mHour, mMinute, mID, mRepeat);
    	}
	}
	
	private void clearStatus()
	{
		if(mTeleListener != null)
		{
			mTelephonyManager.listen(mTeleListener, PhoneStateListener.LISTEN_NONE);
			mTeleListener = null;
		}
		if(mPlayer != null)
		{
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}
		if(mVibrator != null)
		{
			mVibrator.cancel();
			mVibrator = null;
		}
		if (mCpuWakeLock != null) 
		{
			mCpuWakeLock.release();
			mCpuWakeLock = null;
        }
		mIsClick = true;
    	finish();
	}
	
	private class DelayTimerTask extends TimerTask
	{
		private Context mContext;
		public DelayTimerTask(Context context)
		{
			mContext = context;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			delayAlarm(mContext);
			Message msg = new Message();
			msg.what = TOAST_DELAY;
			mToastHandler.sendMessage(msg);
			return;
		}
	}
	
	private Handler mToastHandler = new Handler(){
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case TOAST_DELAY:
				{
					Toast.makeText(mContext, String.format(mContext.getString(R.string.push), mDelay), Toast.LENGTH_LONG).show();
					clearStatus();
				}
				break;
			}
		}
	};
}