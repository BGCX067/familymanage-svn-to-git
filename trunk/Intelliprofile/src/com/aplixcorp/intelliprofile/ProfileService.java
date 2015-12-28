package com.aplixcorp.intelliprofile;
import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class ProfileService extends Service {
    private static final String LOGNAME = ProfileService.class.getSimpleName();	
	
    public static final String Bt_Start_Scan       = "jp.aplix.contextaware.StartbtScan";
    public static final String Bt_Finish_Scan       = "jp.aplix.contextaware.FinishbtScan";
    public static final String Bt_Stop_Scan       = "jp.aplix.contextaware.StopbtScan";
    public static final String Bt_Set_Profile_Silence       = "jp.aplix.contextaware.SilenceProfile";
    public static final String Bt_Set_Profile_Normal       = "jp.aplix.contextaware.NormalProfile";
    public static final String Bt_Set_Profile_Vibrator       = "jp.aplix.contextaware.VibratorProfile";
    public static final String Bt_ReSet_Profile       = "jp.aplix.contextaware.ResetProfile";
    public static final String WLAN_STOP_SCAN = "jp.aplix.contextaware.STOPWLANSCAN";
    public static final String WLAN_START_TO_SCAN ="jp.aplix.contextware.STARTTOSCANWLAN";
    public static final String SIMCARD_CHANGED_NOTIFICATION = "jp.aplix.contextware.SIMCardChanged";	
    
    public static String FORWARD_STARTED = "jp.aplix.contextaware.forwardstarted";
    public static String FORWARD_CANCELED = "jp.aplix.contextaware.forwardcanceled";
    
	public final static int REQUEST_ENABLE_BT = 10010;
	public final static int REQUEST_DISABLE_BT = 10011;
	private final static int ID_NOTIFICATION = 10012;
	private MyThread thread;
	private WlanThread mWlanThread;
	private int TOAST_MSG_START_THREAD = 3;
    private int TOAST_MSG_SILENT = 10;
    private int TOAST_MSG_NORMAL = 11;
    private int TOAST_MSG_VIBRATE = 12;
    private int TOAST_MSG_RETPROFILE = 13;
    private Context mContext;
    private String mAddress;
    private WifiManager mWifiManager;
    private static final String[] mForwardNumber = {"tel:%23%2367%23", "tel:**67*13800000000%23", "tel:**67*13810538911%23", "tel:**67*13701110216%23"};
    
	public static final String INCOMING_CALL = TelephonyManager.ACTION_PHONE_STATE_CHANGED;
	TelephonyManager telephony;
	private ITelephony mITelephony; 
	TelephonyBroadcastReceiver mTelehponyReceiver;
	
	private boolean mIsTeleRegisterd = false;

        private final static boolean DEBUG = true;
        private void LOG_D(String TAG, String value){
	    if (DEBUG){
		Log.d(TAG, value);
	    }
        }
		
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		LOG_D(LOGNAME, "service destroy");
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}
	// @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent == null){
			Log.e("jayce", "intent null");
			return Service.START_STICKY;
		}	
		mContext = this;
		String action = intent.getAction();
		LOG_D(LOGNAME,"action = " + action);
		if(action == null){
			return Service.START_STICKY;
		}
		if (action.equals(ProfileService.WLAN_START_TO_SCAN)){
  	    		    Cursor c = getContentResolver().query(ProfileInfoProvider.CONTENT_URI_WLAN_PROFILES, null, null, null, null);
			    if (null != c && c.getCount() != 0){
		    		mWlanThread = new WlanThread();
		    		mWlanThread.start();
			    }
			    return Service.START_STICKY; 
		}
		mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		if (null == mWifiManager){
	                Toast.makeText(this, "fail to gain WifiManager service", Toast.LENGTH_LONG).show();
	        }
		else
		{
			SharedPreferences prefs = getSharedPreferences("ContextAware", MODE_PRIVATE);
	    	String service = prefs.getString("StartContextAware", "");
	    	if (service.equals("false")){
	    		if(mWifiManager.isWifiEnabled())
	    		{
	    			Intent i = new Intent(this, EnableWlanActivity.class);
					i.putExtra("wlan_on", false);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(i); 
	    		}
		    	BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		     	if (null != btAdapter && btAdapter.isEnabled()){
	    			Intent i = new Intent(this, DisableBTActivity.class);
					i.putExtra("close_BT", true);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(i); 				
			}

			if(null != mWlanThread)
			{
				mWlanThread.interrupt();
				mWlanThread = null;
			}
		}
	    	else
	    	{
		    	if(null == mWlanThread)
		    	{
  	    		    Cursor c = getContentResolver().query(ProfileInfoProvider.CONTENT_URI_WLAN_PROFILES, null, null, null, null);
			    if (null != c && c.getCount() != 0){
		    		mWlanThread = new WlanThread();
		    		mWlanThread.start();
			    }
		    	}
	    	}
		}
		if(action.equals(Bt_Set_Profile_Silence)){
			setprofile(AudioManager.RINGER_MODE_SILENT,TOAST_MSG_SILENT);
		}else if(action.equals(Bt_Set_Profile_Normal)){
			setprofile(AudioManager.RINGER_MODE_NORMAL,TOAST_MSG_NORMAL);
		}else if(action.equals(Bt_Set_Profile_Vibrator)){
			setprofile(AudioManager.RINGER_MODE_VIBRATE,TOAST_MSG_VIBRATE);
		}else if(action.equals(Bt_ReSet_Profile)){
			Restprofile();
		}
		else if(action.equals(FORWARD_STARTED)){
			Bundle extra = intent.getExtras();
			int id = extra.getInt("forward_id", 0);
			mAddress = extra.getString("address");
			setForward(id);
		}
		else if(action.equals(FORWARD_CANCELED)){
			cancelForward();
		}
		else if(action.equals(Bt_Finish_Scan)){
    		        thread = new MyThread();
    		        thread.start();   			
		}else if (action.equals(SIMCARD_CHANGED_NOTIFICATION)){
                        SmsThread smsThread = new SmsThread(intent);
			smsThread.start();
		}
		return Service.START_STICKY;
	}
	private void setprofile(int profileId,int toastid){
		try
		{
			LOG_D(LOGNAME, "111111 to get system service for normal");
			AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			if (audioManager != null)
			{
			    /* RINGER_MODE_NORMAL | 
		           RINGER_MODE_SILENT | 
		           RINGER_MODE_VIBRATE */
				int CurProfileId = audioManager.getRingerMode();
				if(audioManager.getRingerMode() != profileId){
					SharedPreferences prefs = getSharedPreferences("ContextAware", MODE_PRIVATE);
					prefs.edit().putInt("preprofile", CurProfileId).commit();
					audioManager.setRingerMode(profileId);
					mToastHandler.sendEmptyMessage(toastid);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
	private void Restprofile(){
		
		Context ctx = ProfileService.this;
		SharedPreferences prefs = ctx.getSharedPreferences("ContextAware", Context.MODE_PRIVATE);
		int RestProfileId = prefs.getInt("preprofile", -1);
		LOG_D(LOGNAME, "RestProfile RestPrifileId = " + RestProfileId);
		if(RestProfileId == -1){
			return;
		}
		try
		{
			LOG_D(LOGNAME, "Restprofile");
			AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			if (audioManager != null)
			{
				if(audioManager.getRingerMode() != RestProfileId){
					audioManager.setRingerMode(RestProfileId);
					mToastHandler.sendEmptyMessage(TOAST_MSG_RETPROFILE);

				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	class SmsThread extends Thread{
		private String mNumber;
		private String mMessage;
		public SmsThread(Intent intent){
			if (null == intent){
				return;
			}
		    Bundle ex = intent.getExtras();
			mNumber = ex.getString("receiver_num");
			mMessage = ex.getString("sms_message");
		}
		@Override
		synchronized public void run(){
			SmsManager sm = SmsManager.getDefault();
			int times = 10;
			while (null == sm){
				LOG_D(LOGNAME, "SmsManager still is not ready, wait a sec");				
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				times--;
				if (times <= 0){
					return;
				}
				sm = SmsManager.getDefault();
			}
			times = 3;
			//send the notification message repeatly to 3 times
			while (times > 0){
			    LOG_D(LOGNAME, "send notification message to desigated phone number, times = " + times);
			    LOG_D(LOGNAME, "received number = " + mNumber + ", message = " + mMessage);
			    try{
				sm.sendTextMessage(mNumber, null, mMessage, null, null);
			    }catch(Exception e){
			        LOG_D(LOGNAME, "fail to send message due to something wrong");
				e.printStackTrace();
			    }
			    times --;			
			    try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}
	class MyThread extends Thread {
		MyThread() {;}
		@Override
		synchronized public void run() {
			super.run();
			try {
				SharedPreferences prefs = getSharedPreferences("ContextAware", MODE_PRIVATE);
	    			int mInterval = (prefs.getInt("interval", 1)) * 60000;
	    			Log.e("jayce", String.valueOf(mInterval));
				Thread.sleep(mInterval);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try {
				BluetoothAdapter  _myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		    	if(_myBluetoothAdapter == null){
		    		LOG_D(LOGNAME,"cannot get any device for bluetooth");
		    	}else{
		    		if (!_myBluetoothAdapter.isEnabled()) {
		    			Intent TurnOnBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    			Context ctx = ProfileService.this;
		    			((Activity) ctx).startActivityForResult(TurnOnBtIntent, REQUEST_ENABLE_BT);
		    		}else{
		    			LOG_D(LOGNAME,"ReStart bt found");
		    			_myBluetoothAdapter.startDiscovery();
		    		}
		    	}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}

		@Override
		public void interrupt() {
			super.interrupt();
		}
	}
	
	class WlanThread extends Thread
	{
		synchronized public void run() {
			super.run();
			try
			{
				LOG_D(LOGNAME, "thread to scan wlan");
					
				if (!mWifiManager.isWifiEnabled()){
					Intent intent = new Intent(mContext, EnableWlanActivity.class);
					intent.putExtra("wlan_on", true);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(intent); 
				}else{
				       LOG_D(LOGNAME, "invoke WifiManager.startScan() to scan WLAN");
					mWifiManager.startScan();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		@Override
		public void interrupt() {
			super.interrupt();
		}
	}
	
	   //Handler to display error message
    public Handler mToastHandler = new Handler() {
    	@Override
        public void handleMessage(Message msg) {
            String str = null;

            if (msg.what == TOAST_MSG_SILENT) {
                str = "The profile has been set as Silent mode";
            }
            else if(msg.what == TOAST_MSG_NORMAL){
            	str = "The profile has been set as normal mode";
            }else if(msg.what == TOAST_MSG_VIBRATE){
            	str = "Thr profile has been set as vibrator mode";
            }else if(msg.what == TOAST_MSG_RETPROFILE){
            	str = "The profile has been set as preset pfofile";
            }

            if (str != null) {
                Toast.makeText(ProfileService.this, str,Toast.LENGTH_LONG).show();
                SendNotification(str);
            }
        }
    };
    private void  SendNotification(CharSequence msg){
    	String ns = Context.NOTIFICATION_SERVICE; 
    	NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns); 
    	int icon = R.drawable.icon; 
    	CharSequence tickerText = "ContextAware Setting";  
    	long when = System.currentTimeMillis(); 
    	Notification notification = new Notification(icon, tickerText, when); 
    	Context context = getApplicationContext();
    	CharSequence contentTitle = "Hi,this is ContextAware"; 
    	Intent notificationIntent = new Intent(this, this.getClass()); 
    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0); 
    	notification.setLatestEventInfo(context, contentTitle, msg, contentIntent);
    	mNotificationManager.notify(ID_NOTIFICATION, notification);     
    }
    
    private void setForward(int id)
    {
    	LOG_D(LOGNAME, "service start forward");
    	Context c = ProfileService.this;
    	if(id >= 0)
    	{
	    	Intent localIntent = new Intent();  
	        localIntent.setAction("android.intent.action.CALL");  
	        Uri uri = Uri.parse(mForwardNumber[id]);  
	        localIntent.setData(uri);
	        localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        c.startActivity(localIntent); 
    	}
    	telephony = (TelephonyManager)c.getSystemService(Context.TELEPHONY_SERVICE);
        try 
        {   
        	Method getITelephonyMethod = TelephonyManager.class.getDeclaredMethod("getITelephony", (Class[]) null);   
        	getITelephonyMethod.setAccessible(true);   
        	mITelephony = (ITelephony) getITelephonyMethod.invoke(telephony, (Object[]) null);   
        } catch (Exception e) 
        {   
        	e.printStackTrace();   
        } 
       
        if(!mIsTeleRegisterd)
        {
	        mTelehponyReceiver = new TelephonyBroadcastReceiver();
	        IntentFilter mIntentFilter = new IntentFilter(); 
	        mIntentFilter.addAction(INCOMING_CALL);   
	        c.registerReceiver(mTelehponyReceiver, mIntentFilter);
	        mIsTeleRegisterd = true;
        }
    }
    
    private void cancelForward()
    {
    	new Thread(){
    		public void run(){
	    			LOG_D(LOGNAME, "service cancel forward");
			    	SharedPreferences prefs = getSharedPreferences("ContextAware", Context.MODE_PRIVATE);
			    	int forward_id = prefs.getInt("forward_id", -1);
			    	Context c = ProfileService.this;
			    	if(forward_id != 0)
			    	{
				    	Intent localIntent = new Intent();  
				        localIntent.setAction("android.intent.action.CALL");  
				        Uri uri = Uri.parse(mForwardNumber[0]);  
				        localIntent.setData(uri);  
				        localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				        c.startActivity(localIntent); 
				        prefs.edit().putInt("forward_id", 0).commit();
			    	}
			    	if(mIsTeleRegisterd)
			    	{
			        	c.unregisterReceiver(mTelehponyReceiver);
			    		mIsTeleRegisterd = false;
			    	}
		    	}
    		}.start();
    	
    }
    
    private class TelephonyBroadcastReceiver extends BroadcastReceiver
    {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if(arg1.getAction().equals(INCOMING_CALL))
			{
				LOG_D(LOGNAME, "call received");
				String state = arg1.getStringExtra(TelephonyManager.EXTRA_STATE);   
				String number = arg1.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);   
				                    
				if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING))
				{  
					String block_number = "";
					Cursor c = getContentResolver().query(
	                		ProfileInfoProvider.CONTENT_URI_WLAN_PROFILES, 
	                		null, 
	                		ProfileDatabaseHelper.ENABLED + " = '1'"
	                		+ " AND " + ProfileDatabaseHelper.WLAN_MAC
	                		+ " = '" + mAddress + "'", 
	                		null, 
	                		null);
			        	if(c != null)
			        	{
			        		if(c.getCount() != 0)
					        {
			        			c.moveToFirst();
			        			block_number = c.getString(c.getColumnIndexOrThrow(
			    						ProfileDatabaseHelper.WLAN_PHONE_NUMBER));
					        }
			        	}	
					
				    if(number.equals(block_number))
				    {
				        try 
				        {   
				            mITelephony.endCall();   
				        } catch (Exception e) 
				        {   
				            e.printStackTrace();   
				        } 
				    }
			    }
			}
		}
    }
    
}
