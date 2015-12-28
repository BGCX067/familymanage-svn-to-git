package com.exuan.enotes;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class EAlertActivity extends Activity
{
	public static final String LOGNAME = EAlertActivity.class.getSimpleName();
	private Context mContext;
	private int mID;
	private String mMessage;
	private boolean mIsClick;
	private MediaPlayer mPlayer;
	private Vibrator mVibrator;
	private PowerManager.WakeLock mCpuWakeLock;
	private KeyguardLock mKeyguardLock;
	private Timer mTimer = new Timer();
	private static final int TOAST_CANCEL = 0;
	private static final int ALERT_ALARM_DIALOG_ID = 0;
	private static final float IN_CALL_VOLUME = 0.125f;
	private TelephonyManager mTelephonyManager;
	
	private TeleListener mTeleListener;
	private Uri mUri;
	private Uri mUri1;
	
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
		mUri = RingtoneManager.getActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_ALARM);
		mUri1 = RingtoneManager.getActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_RINGTONE);
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
		Cursor cursor = getContentResolver().query(NotesProvider.CONTENT_URI_NOTES, new String[]{NotesDatabaseHelper.DETAIL}, "_id = " + mID, null, null);
    	if(cursor != null && cursor.getCount() > 0)
    	{
    		cursor.moveToNext();
    		mMessage = cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.DETAIL));
	    	
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
	    			
	    			mPlayer.setDataSource(mContext, mUri);
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
	    			mPlayer.setDataSource(mContext, mUri);
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
					mPlayer.setDataSource(mContext, mUri1);
					mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
    				mPlayer.setLooping(true);
					mPlayer.prepare();
					mPlayer.start();
		    	} catch (Exception e1) {
		    	}
			}
			mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);   
			mVibrator.vibrate(new long[]{700,1500,700,1500}, 0); 
			showDialog(ALERT_ALARM_DIALOG_ID);
			mTimer.schedule(new DelayTimerTask(mContext), 60 * 1000);
			cursor.close();
    	}
    	if (null != cursor) {
    	    cursor.close();
    	}
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
			    			mPlayer.setDataSource(mContext, mUri);
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
								mPlayer.setDataSource(mContext, mUri1);
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
			EDetailActivity.cancelAlarm(mContext, mID);
			mTimer.cancel();
			mTimer = null;
			clearStatus();
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
				LayoutInflater factory = LayoutInflater.from(mContext);
				View layout = factory.inflate(R.layout.layout_alert, null);
				TextView tv = (TextView)layout.findViewById(R.id.textview_alert);
				tv.setText(mMessage);
				return new AlertDialog.Builder(mContext)
				.setTitle(getString(R.string.app_name))
				.setIcon(R.drawable.icon)
				.setView(layout)
				.setPositiveButton(R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										EDetailActivity.cancelAlarm(mContext, mID);
										mTimer.cancel();
										mTimer = null;
										clearStatus();
									}
								})
				.setOnCancelListener(new OnCancelListener(){

					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						EDetailActivity.cancelAlarm(mContext, mID);
						mTimer.cancel();
						mTimer = null;
						clearStatus();
					}})
				.create();				
			}
		}
		return super.onCreateDialog(id);
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
		public DelayTimerTask(Context context)
		{
			mContext = context;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			EDetailActivity.cancelAlarm(mContext, mID);
			Message msg = new Message();
			msg.what = TOAST_CANCEL;
			mToastHandler.sendMessage(msg);
			return;
		}
	}
	
	private Handler mToastHandler = new Handler(){
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case TOAST_CANCEL:
				{
					clearStatus();
				}
				break;
			}
		}
	};
	
}
