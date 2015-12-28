package com.aplixcorp.intelliprofile;


import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;


public class ProfileSettingReceiver extends BroadcastReceiver {
        private static final String LOGNAME = ProfileSettingReceiver.class.getSimpleName();	
	private static HashMap<String, Short> BtConfigMap = new HashMap<String, Short>();
	private static HashMap<String, Short> BtScanResultMap = new HashMap<String, Short>();
	private final static String discovery_start="android.bluetooth.adapter.action.DISCOVERY_STARTED";
	private final static String discovery_finish="android.bluetooth.adapter.action.DISCOVERY_FINISHED";
	private final static String bt_device_state_change="android.bluetooth.adapter.action.STATE_CHANGED";
	private final static String wlan_result = WifiManager.SCAN_RESULTS_AVAILABLE_ACTION;
	public final static int REQUEST_ENABLE_BT = 10010;
	public final static int REQUEST_DISABLE_BT = 10011;
	private final static int REQUEST_RESET_PROFILE = 10022;
	private static boolean isStopContextAware = false;
	
	private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	
	private boolean mFoundWlan;
	
	ConnectivityManager connMgr;
	private WifiManager mWifiManager;

        private final static boolean DEBUG = true;
        private void LOG_D(String TAG, String value){
	    if (DEBUG){
		Log.d(TAG, value);
	    }
        }
	@Override
	public void onReceive(Context context, Intent intent) {
	    // TODO Auto-generated method stub
		String action = intent.getAction();
		Log.i(LOGNAME,"action = " + action);
		if (action.equals("android.net.wifi.STATE_CHANGE")){
                          ConnectivityManager connMgr = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                          NetworkInfo netInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			  if (null != netInfo && netInfo.isConnected()){
			  	LOG_D(LOGNAME, "start to scan WLAN due to WLAN is connected");
	    			Intent intent_new = new Intent();
	    			intent_new.setClassName("com.aplixcorp.intelliprofile", ProfileService.class.getName());
	    			intent_new.setAction(ProfileService.WLAN_START_TO_SCAN);
	    			context.startService(intent_new);
		                return;			
                          }
		}else if(action.equals("android.intent.action.BOOT_COMPLETED")){
			reloadcfg(context);
			//First to check if SIM card was changed. If changed, then send notification message to the designated phone number
	    		SharedPreferences prefs = context.getSharedPreferences("ContextAware", Context.MODE_PRIVATE);
                        String originalIMSI = prefs.getString(MessageControlActivity.SIM_CARD_IMSI, "");
		        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String imsi = tm.getSubscriberId();
			if (null != imsi && !imsi.equals(originalIMSI)){
				//means SIM card was changed.
				String num = prefs.getString(MessageControlActivity.SMS_NOTIFICATION_RECEIVER_NUMBER, "");
				String mess = context.getString(R.string.sim_card_changed_notification, "");
	    		        Intent intent_new = new Intent();
	    			intent_new.setClassName("com.aplixcorp.intelliprofile", ProfileService.class.getName());
	    			intent_new.setAction(ProfileService.SIMCARD_CHANGED_NOTIFICATION);
	    			intent_new.putExtra("receiver_num", num);
	    			intent_new.putExtra("sms_message", mess);
	    			context.startService(intent_new);				
				return;
			}
			
	    		String isAllowedToStartContextAware = prefs.getString("StartContextAware", "true");
	   	 	if(isAllowedToStartContextAware.equals("true")){
	    			isStopContextAware = true;
			    	BluetoothAdapter _myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			    	if(_myBluetoothAdapter != null){
			    		if (!_myBluetoothAdapter.isEnabled()) {
		 	   			Intent TurnOnBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    				TurnOnBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		  	  			context.startActivity(TurnOnBtIntent);
		    			}else{
		    				Log.i(LOGNAME,"startDiscovery");
		    				_myBluetoothAdapter.startDiscovery();
		    			}
		    		}	    		
	    		}else{
	    			isStopContextAware = false;
	    		}
		}else if(action.equals("jp.aplix.contextaware.StartContextAware")){
			LOG_D(LOGNAME,"action to start Context Aware");
			isStopContextAware = false;
			reloadcfg(context);
	    	BluetoothAdapter _myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    	if(_myBluetoothAdapter != null){
	    		if (!_myBluetoothAdapter.isEnabled() && BtConfigMap.size() >= 1) {
	    			Intent TurnOnBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	    			TurnOnBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
	    			context.startActivity(TurnOnBtIntent);
	    		}else{
	    			LOG_D(LOGNAME,"BT startDiscovery");
	    			_myBluetoothAdapter.startDiscovery();
	    		}
	    	}

		}else if(action.equals("jp.aplix.contextaware.StopContextAware")){
			LOG_D(LOGNAME,"action to start Context Aware");
			synchronized (BtScanResultMap) {
				BtScanResultMap.clear();
				setprofile(context,REQUEST_RESET_PROFILE);
			}
			isStopContextAware = true;
		}else if(action.equals(bt_device_state_change)){
			try {
		    	BluetoothAdapter _myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		    	if(_myBluetoothAdapter == null){
		    		LOG_D(LOGNAME,"cannot get any device for bluetooth");
		    	}else{
		    		if (_myBluetoothAdapter.isEnabled()) {
		    			LOG_D(LOGNAME,"startDiscovery");
		    			_myBluetoothAdapter.startDiscovery();
		    		}
		    	}
			} catch (Exception e1) {
				e1.printStackTrace();
			}			
		}else if (action.equals(discovery_start)){
			synchronized (BtConfigMap) {
				BtScanResultMap.clear();
			}
		}else if(action.equals(BluetoothDevice.ACTION_FOUND)){
			if(isStopContextAware){
				return;
			}
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			Short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,(short) 0);
			String bt_address = device.getAddress();
			String bt_name = device.getName();
			synchronized (BtConfigMap) {
				BtScanResultMap.put(bt_address, rssi);
			}
			LOG_D(LOGNAME,"new found bluetooth mac = " +
					 bt_address + " name =  " + 
					 bt_name + " rssi = " + rssi);

		}else if(action.equals(discovery_finish)){
			if(isStopContextAware){
				return;
			}
			Iterator iterator = BtScanResultMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next();
				String key = (String)entry.getKey();
				Short val = (Short)entry.getValue();
				LOG_D(LOGNAME,"key = " + key);
				LOG_D(LOGNAME,"val = " + val);
			}
			LOG_D("ContextAware","BtScanResultMap===============================");

			Iterator iterator_c = BtConfigMap.entrySet().iterator();
			while (iterator_c.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator_c.next();
				String key = (String)entry.getKey();
				Short val = (Short)entry.getValue();
				LOG_D(LOGNAME,"key = " + key);
				LOG_D(LOGNAME,"val = " + val);
			}
			LOG_D("ContextAware","BtConfigMap*******************************");
			Short maxRssi = -1000;
			String maxMacAddress = null;
			if(BtConfigMap.size() >= 1){
				Iterator iter = BtConfigMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = (String)entry.getKey();
					Short val = (Short)entry.getValue();
					if(BtScanResultMap.containsKey(key)){
						Short rssi = BtScanResultMap.get(key);
						if(Math.abs(rssi) <= Math.abs(val)){
							if(rssi > maxRssi){
								maxRssi = rssi;
								maxMacAddress = key;
							}else{
								;
							}
						}
					}
					
				}
			}
			LOG_D(LOGNAME,"config bt found ,and set profile now");
			if(maxMacAddress != null){
				queryConfig(context,maxMacAddress);
			}else{
				setprofile(context,REQUEST_RESET_PROFILE);
			}

			Intent bt_intent = new Intent();
			bt_intent.setClassName("com.aplixcorp.intelliprofile", ProfileService.class.getName());
			bt_intent.setAction(ProfileService.Bt_Finish_Scan);
			context.startService(bt_intent);
		}
		else if(action.equals(wlan_result)){
			if(isStopContextAware){
				return;
			}
			Log.i(LOGNAME, "wlan result to be config");
			mFoundWlan = false;
			mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
			List<ScanResult> scanResults = mWifiManager.getScanResults();
			WlanComparator wc = new WlanComparator();
			Collections.sort(scanResults, wc);
			
			for (int i = 0; i < scanResults.size(); i++) {
                		ScanResult mScanResult = scanResults.get(i);
               			 String wlan_name = mScanResult.SSID;
    				String wlan_address = mScanResult.BSSID;
    				int wlan_rssi = mScanResult.level;
    				Log.i(LOGNAME, "wlan_name:" + wlan_name + ",wlan_address:" + wlan_address + ",wlan_rssi:" + wlan_rssi);
    				Cursor c = context.getContentResolver().query(
    		        		ProfileInfoProvider.CONTENT_URI_WLAN_PROFILES, 
    		        		null, 
    		        		ProfileDatabaseHelper.WLAN_MAC + " = '" + wlan_address +"' "
    		        		+ " AND " + ProfileDatabaseHelper.ENABLED + " = '1'"
    		        		+ " AND " + ProfileDatabaseHelper.WLAN_RSSI + " < '" + wlan_rssi + "' ",
    		        		null, 
    		        		null);
    				if(c != null)
		      		 {
		        		if(c.getCount() != 0)
					 {
		        			LOG_D(LOGNAME, "has a wlan to config");
		        			SharedPreferences prefs = context.getSharedPreferences("ContextAware", Context.MODE_PRIVATE);
	        				int forward_id = prefs.getInt("forward_id", -1);
	        				c.moveToFirst();
	    					int forward = c.getInt(c.getColumnIndexOrThrow(
	    									ProfileDatabaseHelper.PROFILE_VALUE));
	    					LOG_D(LOGNAME," forward = " + forward);
	    					prefs.edit().putInt("forward_id", forward).commit();
	    					if(forward_id == forward)
	    					{
	    						forward = -1;
	    						LOG_D(LOGNAME, "forward id has been set");
	    					}
	    					Intent intent_new = new Intent();
	    					intent_new.setClassName("com.aplixcorp.intelliprofile", ProfileService.class.getName());
	    					intent_new.setAction(ProfileService.FORWARD_STARTED);
	    					intent_new.putExtra("forward_id", forward);
	    					intent_new.putExtra("address", wlan_address);
	    					context.startService(intent_new);
	    					mFoundWlan = true;
	    					prefs.edit().putBoolean("wifi_config_stopped", false).commit();
	    					break;
				    }
		    	    }
         		 }
			 SharedPreferences prefs = context.getSharedPreferences("ContextAware", Context.MODE_PRIVATE);
         		 boolean config_stopped = prefs.getBoolean("wifi_config_stopped", true);
			  if(!mFoundWlan && !config_stopped)	
			  {
				  LOG_D(LOGNAME, "stop forward");
				  cancelForward(context);
				  prefs.edit().putBoolean("wifi_config_stopped", true).commit();
			  }
			}
		else if(action.equals("jp.aplix.contextaware.ConfigReload")){
			reloadcfg(context);
		}
		else if(action.equals(SMS_RECEIVED)){
			if(isStopContextAware){
				return;
			}
			handleSMS(context, intent);
		}
		return;
	}

	private void handleSMS(Context context, Intent intent){
			LOG_D(LOGNAME, "sms received");
			SmsMessage sms = getMessagesFromIntent(intent)[0];
			String message = sms.getDisplayMessageBody().toLowerCase().trim();
			if (null == message){
				LOG_D(LOGNAME, "message is null");
				return;
			}
			SharedPreferences prefs = context.getSharedPreferences("ContextAware", Activity.MODE_PRIVATE);
			String forwardCallKeyWords = prefs.getString(MessageControlActivity.KEYWORDS_FORWARD_CALL, "");
			String forwardSMSKeyWords = prefs.getString(MessageControlActivity.KEYWORDS_FORWARD_SMS, "");
			LOG_D(LOGNAME, "forward SMS key words = " + forwardSMSKeyWords);
			LOG_D(LOGNAME, "forward call key words = " + forwardCallKeyWords);
			
				if(message.equalsIgnoreCase(forwardCallKeyWords))
				{ //means cancel  forward incoming call
				    LOG_D(LOGNAME, "cancel forward incoming call");
				    Intent localIntent = new Intent();  
				    localIntent.setAction("android.intent.action.CALL"); 
				    Uri uri = Uri.parse("tel:%23%2321%23");  
				    localIntent.setData(uri);
				    localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				    context.startActivity(localIntent); 
				    prefs.edit().putBoolean("call_forward", false).commit();
				}
				else if(message.equalsIgnoreCase(forwardSMSKeyWords))
				{
				    //means cancel forward SMS
					LOG_D(LOGNAME, "cancel forward SMS");
				        prefs.edit().putBoolean("SMS_forward", false).commit();					
				}
				else if(message.contains(MessageControlActivity.LOCK))
				{
					LOG_D(LOGNAME, "lock");
				}				
				else if(message.contains(MessageControlActivity.UNLOCK))
				{
					LOG_D(LOGNAME, "unlock");
				}
				else if(message.contains(MessageControlActivity.POWEROFF))
				{
					LOG_D(LOGNAME, "power off");
				}
				else
				{
					String[] sub = message.split(":");
					if(sub != null && sub.length > 1)
					{
						for(int i = 0; i < sub.length; i++)
						{
							LOG_D(LOGNAME, "sub:" + sub[i]);
							sub[i] = sub[i].trim();
						}
						if(sub[0].equalsIgnoreCase(forwardCallKeyWords))
						{
						       LOG_D(LOGNAME, "set forward call:" + sub[1]);
					               if(sub[1].length() > 0)
					               {
							    Intent localIntent = new Intent();  
							    localIntent.setAction("android.intent.action.CALL"); 
							    Uri uri = Uri.parse("tel:**21*" + sub[1] + "%23");  
							    localIntent.setData(uri);
							    localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							    context.startActivity(localIntent); 
							    
							    prefs.edit().putBoolean("call_forward", true).commit();
							    prefs.edit().putString("call_forward_number", sub[1]).commit();
					                }
						}else if(sub[0].equalsIgnoreCase(forwardSMSKeyWords)){
						       LOG_D(LOGNAME, "set forward SMS:" + sub[1]);
					               if(sub[1].length() > 0)
					               {						    
							    prefs.edit().putBoolean("SMS_forward", true).commit();
							    prefs.edit().putString("sms_forward_number", sub[1]).commit();
					                }
						}else{
						        //try to forward SMS to the specified phone number
					                LOG_D(LOGNAME, "forward message 1");						        
							boolean forward = prefs.getBoolean("SMS_forward", false);
							if(forward)
							{
								String num = prefs.getString("sms_forward_number", "");
								String address = sms.getDisplayOriginatingAddress();
								String mess = "(" + address + ")" + message;
								SmsManager sm = SmsManager.getDefault();
								sm.sendTextMessage(num, null, mess, null, null);
							}
						}
					}
					else
					{						
						boolean forward = prefs.getBoolean("SMS_forward", false);
						if(forward)
						{
	        					LOG_D(LOGNAME, "forward message 2");

							String num = prefs.getString("sms_forward_number", "");
							String address = sms.getDisplayOriginatingAddress();
							String mess = "(" + address + ")" + message;
							SmsManager sm = SmsManager.getDefault();
							sm.sendTextMessage(num, null, mess, null, null);
						}
					}
				}		
				
				LOG_D(LOGNAME, "message:" + message);	
	}
	
	private void queryConfig(Context ctx,String MAC){
    	Cursor c = ctx.getContentResolver().query(
        		ProfileInfoProvider.CONTENT_URI_BT_PROFILES, 
        		null, 
        		ProfileDatabaseHelper.BLUETOOTH_MAC + " = '" + MAC +"' ", 
        		null, 
        		null);
    	if(c != null)
    	{
    		if(c.getCount() != 0)
    		{
    			        c.moveToFirst();
				int profile = c.getInt(c.getColumnIndexOrThrow(
						ProfileDatabaseHelper.PROFILE_VALUE));
				LOG_D(LOGNAME,"profile = " + profile);
				// 0 normal 1 silent 2 virbator
 			        setprofile(ctx,profile);
    		}
    	}
	}
	
	private void setprofile(Context ctx,int profileId){
		Intent bt_intent = new Intent();
	        AudioManager audioManager = (AudioManager)ctx.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
               /*  RINGER_MODE_NORMAL | 
	            RINGER_MODE_SILENT | 
	            RINGER_MODE_VIBRATE */
		int CurProfileId = audioManager.getRingerMode();
	
		switch(profileId){
		case 0:
		{
			//set to normal
			if (CurProfileId == AudioManager.RINGER_MODE_NORMAL){
				LOG_D(LOGNAME, "Current mode already is Normal profile, not set again");
				return;
			}
			LOG_D(LOGNAME,"rssi set normal");
			bt_intent.setClassName("com.aplixcorp.intelliprofile", ProfileService.class.getName());
			bt_intent.setAction(ProfileService.Bt_Set_Profile_Normal);
			ctx.startService(bt_intent);	
			break;
		}
		case 1:
		{
			//Set to silent
			if (CurProfileId == AudioManager.RINGER_MODE_SILENT){
				LOG_D(LOGNAME, "Current mode already is Silent profile, not set again");
				return;
			}			
			LOG_D("ContextAware","rssi set slient");
			bt_intent.setClassName("com.aplixcorp.intelliprofile", ProfileService.class.getName());
			bt_intent.setAction(ProfileService.Bt_Set_Profile_Silence);
			ctx.startService(bt_intent);	
			break;
		}
		case 2:{
			//Set to vibrator
			if (CurProfileId == AudioManager.RINGER_MODE_VIBRATE ){
				LOG_D(LOGNAME, "Current mode already is Vibrate profile, not set again");
				return;
			}				
			LOG_D(LOGNAME,"rssi set vibrator");
			bt_intent.setClassName("com.aplixcorp.intelliprofile", ProfileService.class.getName());
			bt_intent.setAction(ProfileService.Bt_Set_Profile_Vibrator);
			ctx.startService(bt_intent);	
			break;
		}
		case REQUEST_RESET_PROFILE:
		{
			//Set silent
			LOG_D(LOGNAME,"rssi set reset");
			bt_intent.setClassName("com.aplixcorp.intelliprofile", ProfileService.class.getName());
			bt_intent.setAction(ProfileService.Bt_ReSet_Profile);
			ctx.startService(bt_intent);
			break;
		}
		default:
			break;
		}
		return;
	}
	void reloadcfg(Context context){

        synchronized (BtConfigMap) {
        	BtConfigMap.clear(); 
        	Cursor c = context.getContentResolver().query(
        		ProfileInfoProvider.CONTENT_URI_BT_PROFILES, 
        		null, 
        		ProfileDatabaseHelper.ENABLED + " = '1'", 
        		null, 
        		null);
        	if(c != null)
        	{
        		if(c.getCount() != 0)
        		{
        			c.moveToFirst();
        			do{
        				String BlueToothMacAddress = c.getString(c.getColumnIndexOrThrow(
        						ProfileDatabaseHelper.BLUETOOTH_MAC));
        				LOG_D(LOGNAME,"config setting mac address = " + BlueToothMacAddress);
        				Short BlueToothRssi = c.getShort(c.getColumnIndexOrThrow(
        						ProfileDatabaseHelper.BLUETOOTH_RSSI));
        				LOG_D(LOGNAME,"config setting BlueToothRssi = " + BlueToothRssi);
        				BtConfigMap.put(BlueToothMacAddress, BlueToothRssi);
        			}while(c.moveToNext());
        		}
        		c.close();
        	}
        }
	}
	
	private void setForward(Context context, int id)
	{
		Intent intent = new Intent();
		intent.setClassName("com.aplixcorp.intelliprofile", ProfileService.class.getName());
		intent.setAction(ProfileService.FORWARD_STARTED);
		intent.putExtra("forward_id", id);
		context.startService(intent);
		/*
		Intent localIntent = new Intent();  
        localIntent.setAction("android.intent.action.CALL");  
        Uri uri = Uri.parse(mForwardNumber[id]);  
        localIntent.setData(uri);  
        context.startActivity(localIntent); 
        */
	}
	
	private void cancelForward(Context context)
	{
		Intent intent = new Intent();
		intent.setClassName("com.aplixcorp.intelliprofile", ProfileService.class.getName());
		intent.setAction(ProfileService.FORWARD_CANCELED);
		context.startService(intent);
	}
	
	public class WlanComparator implements Comparator
	{

		@Override
		public int compare(Object arg0, Object arg1) {
			// TODO Auto-generated method stub
			ScanResult rf = (ScanResult)arg0;
			ScanResult rl = (ScanResult)arg1;
			
			if(rf.level > rl.level)
			{
				return 1;
			}
			if(rf.level < rl.level)
			{
				return -1;
			}
			return 0;
		}
		
	}
	
	public static final SmsMessage[] getMessagesFromIntent(Intent intent) {
		Object messages[] = (Object[]) (Object[]) intent.getSerializableExtra("pdus");
		byte pduObjs[][] = new byte[messages.length][];
		for (int i = 0; i < messages.length; i++) {
			pduObjs[i] = (byte[]) (byte[]) messages[i];
		}

		byte pdus[][] = new byte[pduObjs.length][];
		int pduCount = pdus.length;
		SmsMessage msgs[] = new SmsMessage[pduCount];
		for (int i = 0; i < pduCount; i++) {
			pdus[i] = pduObjs[i];
			msgs[i] = SmsMessage.createFromPdu(pdus[i]);
		}

		return msgs;
	}
	
}
