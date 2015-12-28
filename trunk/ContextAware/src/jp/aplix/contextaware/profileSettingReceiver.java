package jp.aplix.contextaware;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.os.DropBoxManager.Entry;
import android.util.Log;
import android.content.Context;
public class profileSettingReceiver extends BroadcastReceiver {
	private static HashMap<String, Short> BtConfigMap = new HashMap<String, Short>();
	private static HashMap<String, Short> BtScanResultMap = new HashMap<String, Short>();
	private final static String discovery_start="android.bluetooth.adapter.action.DISCOVERY_STARTED";
	private final static String discovery_finish="android.bluetooth.adapter.action.DISCOVERY_FINISHED";
	private final static String bt_device_state_change="android.bluetooth.adapter.action.STATE_CHANGED";
	public final static int REQUEST_ENABLE_BT = 10010;
	public final static int REQUEST_DISABLE_BT = 10011;
	private static boolean isStopContextAware = false;
	@Override
	public void onReceive(Context context, Intent intent) {
	    // TODO Auto-generated method stub
		String action = intent.getAction();
		Log.d("ContextAware","action = " + action);
		if(action.equals("android.intent.action.BOOT_COMPLETED")){
			reloadcfg(context);
	    	SharedPreferences prefs = context.getSharedPreferences("ContextAware", Context.MODE_PRIVATE);
	    	String isAllowedToStartContextAware = prefs.getString("StartContextAware", "true");
	    	if(isAllowedToStartContextAware.equals("true")){
	    		isStopContextAware = true;
		    	BluetoothAdapter _myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		    	if(_myBluetoothAdapter != null){
		    		if (!_myBluetoothAdapter.isEnabled()) {
		    			Intent TurnOnBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    			context.startActivity(TurnOnBtIntent);
		    		}else{
		    			Log.d("btservice","startDiscovery");
		    			_myBluetoothAdapter.startDiscovery();
		    		}
		    	}	    		
	    	}else{
	    		isStopContextAware = false;
	    	}
			
		}else if(action.equals("jp.aplix.contextaware.StartContextAware")){
			Log.d("ContextAwate ","action to start Context Aware");
			isStopContextAware = false;
	    	BluetoothAdapter _myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    	if(_myBluetoothAdapter != null){
	    		if (!_myBluetoothAdapter.isEnabled()) {
	    			Intent TurnOnBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	    			context.startActivity(TurnOnBtIntent);
	    		}else{
	    			Log.d("btservice","startDiscovery");
	    			_myBluetoothAdapter.startDiscovery();
	    		}
	    	}

		}else if(action.equals("jp.aplix.contextaware.StopContextAware")){
			Log.d("ContextAwate ","action to start Context Aware");
			synchronized (BtScanResultMap) {
				BtScanResultMap.clear();
			}
			isStopContextAware = true;
		}else if(action.equals(bt_device_state_change)){
			try {
		    	BluetoothAdapter _myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		    	if(_myBluetoothAdapter == null){
		    		Log.d("btservice","cannot get any device for bluetooth");
		    	}else{
		    		if (_myBluetoothAdapter.isEnabled()) {
		    			Log.d("btservice","startDiscovery");
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
			Log.d("ContextAware","new found bluetooth mac = " +
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
				Log.d("Seth Log","key = " + key);
				Log.d("Seth Log","val = " + val);
			}
			Log.d("ContextAware","===============================");

			Iterator iterator_c = BtConfigMap.entrySet().iterator();
			while (iterator_c.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator_c.next();
				String key = (String)entry.getKey();
				Short val = (Short)entry.getValue();
				Log.d("Seth Log","key = " + key);
				Log.d("Seth Log","val = " + val);
			}
			Log.d("ContextAware","rssi set normal");
			Log.d("ContextAware","*******************************");
			
			if(BtConfigMap.size() >= 1){
				Iterator iter = BtConfigMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = (String)entry.getKey();
					Short val = (Short)entry.getValue();
					if(BtScanResultMap.containsKey(key)){
						Short rssi = BtScanResultMap.get(key);
						
						if(Math.abs(rssi) <= Math.abs(val)){
							Log.d("ContextAware","config bt found ,and set profile now");
				        	Cursor c = context.getContentResolver().query(
					        		ProfileInfoProvider.CONTENT_URI_PROFILES, 
					        		null, 
					        		ProfileDatabaseHelper.BLUETOOTH_MAC + " = '" + key +"' ", 
					        		null, 
					        		null);
				        	if(c != null)
				        	{
				        		if(c.getCount() != 0)
				        		{
				        			c.moveToFirst();
			        				int profile = c.getInt(c.getColumnIndexOrThrow(
			        						ProfileDatabaseHelper.PROFILE_VALUE));
			        				Log.d("Seth Log","profile = " + profile);
			        				// 0 normal 1 silent 2 virbator
			        				if(profile == 0){
			        					//set normal
			        					Log.d("ContextAware","rssi set normal");
			        					Intent bt_intent = new Intent();
			        					bt_intent.setClassName("jp.aplix.contextaware", btservice.class.getName());
			        					bt_intent.setAction(btservice.Bt_Set_Profile_Normal);
			        					context.startService(bt_intent);
			        				}else if(profile ==1){

			        					//Set silent
			        					Log.d("ContextAware","rssi set slient");
			        					Intent bt_intent = new Intent();
			        					bt_intent.setClassName("jp.aplix.contextaware", btservice.class.getName());
			        					bt_intent.setAction(btservice.Bt_Set_Profile_Silence);
			        					context.startService(bt_intent);				        				
			        				}else{	
			        					//Set silent
			        					Log.d("ContextAware","rssi set vibrator");
			        					Intent bt_intent = new Intent();
			        					bt_intent.setClassName("jp.aplix.contextaware", btservice.class.getName());
			        					bt_intent.setAction(btservice.Bt_Set_Profile_Vibrator);
			        					context.startService(bt_intent);		
			        				}
				        		}
				        	}
						}
					}
					
				}
			}
			Intent bt_intent = new Intent();
			bt_intent.setClassName("jp.aplix.contextaware", btservice.class.getName());
			bt_intent.setAction(btservice.Bt_Finish_Scan);
			context.startService(bt_intent);
		}else if(action.equals("jp.aplix.contextaware.ConfigReload")){
			reloadcfg(context);
		}
		return;
	}
	void reloadcfg(Context context){

        synchronized (BtConfigMap) {
        	BtConfigMap.clear(); 
        	Cursor c = context.getContentResolver().query(
        		ProfileInfoProvider.CONTENT_URI_PROFILES, 
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
        				Log.d("ContextAware","config setting mac address = " + BlueToothMacAddress);
        				Short BlueToothRssi = c.getShort(c.getColumnIndexOrThrow(
        						ProfileDatabaseHelper.BLUETOOTH_RSSI));
        				Log.d("ContextAware","config setting BlueToothRssi = " + BlueToothRssi);
        				BtConfigMap.put(BlueToothMacAddress, BlueToothRssi);
        			}while(c.moveToNext());
        		}
        	c.close();
        	}
        }
			
	}

}
