package jp.aplix.contextaware;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class btservice extends Service {
    public static final String Bt_Start_Scan       = "jp.aplix.contextaware.StartbtScan";
    public static final String Bt_Finish_Scan       = "jp.aplix.contextaware.FinishbtScan";
    public static final String Bt_Stop_Scan       = "jp.aplix.contextaware.StopbtSca";
    public static final String Bt_Set_Profile_Silence       = "jp.aplix.contextaware.SilenceProfile";
    public static final String Bt_Set_Profile_Normal       = "jp.aplix.contextaware.NormalProfile";
    public static final String Bt_Set_Profile_Vibrator       = "jp.aplix.contextaware.VibratorProfile";
	public final static int REQUEST_ENABLE_BT = 10010;
	public final static int REQUEST_DISABLE_BT = 10011;
	private final static int ID_NOTIFICATION = 10012;
	private MyThread thread;
	private int TOAST_MSG_START_THREAD = 3;
    private int TOAST_MSG_SILENT = 10;
    private int TOAST_MSG_NORMAL = 11;
    private int TOAST_MSG_VIBRATE = 12;
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
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}
	// @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = intent.getAction();
		Log.d("btservice","action = " + action);
		if(action == null){
			return Service.START_STICKY;
		}
		
		if(action.equals(Bt_Set_Profile_Silence)){
			try
			{
			   Log.d("btservice", "111111 start to get system service");
			   AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			   if (audioManager != null)
			   {
			     /* RINGER_MODE_NORMAL | 
			       RINGER_MODE_SILENT | 
			       RINGER_MODE_VIBRATE */
			 	  if(audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT){
			  		  Log.d("btservice", "111111 to show Toast for Set Silent");			    		  
			   		  audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			    		  mToastHandler.sendEmptyMessage(TOAST_MSG_SILENT);
			   	  }
  		       }
		    }
		    catch(Exception e)
		    {
		      e.printStackTrace();
		    }
		}else if(action.equals(Bt_Set_Profile_Normal)){
			   try
			    {
				  Log.d("btservice", "111111 to get system service for normal");
			      AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			      if (audioManager != null)
			      {
				       /* RINGER_MODE_NORMAL | 
			           RINGER_MODE_SILENT | 
			           RINGER_MODE_VIBRATE */
			    	  if(audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL){
			    		  Log.d("btservice", "111111 to show Toast for Set Normal");
			    		  audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			    		  mToastHandler.sendEmptyMessage(TOAST_MSG_NORMAL);

			    	  }
			      }
			    }
			    catch(Exception e)
			    {
			      e.printStackTrace();
			    }	

		}else if(action.equals(Bt_Set_Profile_Vibrator)){
			   try
			    {
				  Log.d("btservice", "111111 to get system service for normal");
			      AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			      if (audioManager != null)
			      {
				       /* RINGER_MODE_NORMAL | 
			           RINGER_MODE_SILENT | 
			           RINGER_MODE_VIBRATE */
			    	  if(audioManager.getRingerMode() != AudioManager.RINGER_MODE_VIBRATE){
			    		  Log.d("btservice", "111111 to show Toast for Set Normal");
			    		  audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			    		  mToastHandler.sendEmptyMessage(TOAST_MSG_VIBRATE);
			    	  }
			      }
			    }
			    catch(Exception e)
			    {
			      e.printStackTrace();
			    }				
		}else if(action.equals(Bt_Finish_Scan)){
    		thread = new MyThread();
    		thread.start();   			
		}
		return Service.START_STICKY;
	}
	class MyThread extends Thread {
		MyThread() {;}
		@Override
		synchronized public void run() {
			super.run();
			try {
				SharedPreferences prefs = getSharedPreferences("ContextAware", MODE_PRIVATE);
	    		int mInterval = (prefs.getInt("interval", 1)) * 60000;Log.e("jayce", String.valueOf(mInterval));
				Thread.sleep(mInterval);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try {
				BluetoothAdapter  _myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		    	if(_myBluetoothAdapter == null){
		    		Log.d("btservice","cannot get any device for bluetooth");
		    	}else{
		    		if (!_myBluetoothAdapter.isEnabled()) {
		    			Intent TurnOnBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    			Context ctx = btservice.this;
		    			((Activity) ctx).startActivityForResult(TurnOnBtIntent, REQUEST_ENABLE_BT);
		    		}else{
		    			Log.d("btservice","ReStart bt found");
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
            }

            if (str != null) {
                Toast.makeText(btservice.this, str,Toast.LENGTH_LONG).show();
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
    
}
