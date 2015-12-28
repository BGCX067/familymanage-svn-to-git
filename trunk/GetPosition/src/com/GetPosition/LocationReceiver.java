package com.GetPosition;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;
import android.widget.Toast;

public class LocationReceiver extends BroadcastReceiver {
	private static final String Boot_Intent = 
		"android.intent.action.BOOT_COMPLETED";
	private static final String BT_Discover_Start_Intent = 
		"android.bluetooth.adapter.action.DISCOVERY_STARTED";
	private static final String BT_Discover_Found_Intent = 
		"android.bluetooth.device.action.FOUND";
	private static final String BT_Discover_Finished_Intent = 
		"android.bluetooth.adapter.action.DISCOVERY_FINISHED";
	private static final String BT_STATE_CHANGED = 
		"android.bluetooth.adapter.action.STATE_CHANGED";
	private static final String WIFI_Scan_Intent = 
		"android.net.wifi.SCAN_RESULTS";
	private static final String WIFI_State_Change_Inetent = 
		"android.net.wifi.STATE_CHANGE";
	
	private static WifiManager mWifiManager = null;
	private static WifiLock  mWifiLock = null;
	private static BluetoothAdapter mBtAdapter = null;
	private static boolean bConnectedToServer = false;
	private static final String TAG = "Wifi_Tag";
	private static final String default_BindId = "CN=Facebook JBlend,OU=Misc,OU=iaSolution,DC=iaSolution,DC=net";
	private static final String default_BindPwd = "iafb3727";
	private static final String service_url = "neuron.iasolution.net";
	private static final int service_port = 389;
	private static final String filter = "(objectClass=user)";
    private static String address = "10.90.0.20";
    private static int port = 389;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action  = intent.getAction();
		if(action == null )
			return;
		if(action.equals(Boot_Intent)){
			if(isWifiEnabled(context)){
				//Wifi was enabled
				if(isWifiConnected(context)){
					//check if android net was connected
					//check if can connect to the ldap server
					//to add code here
					if(!checkifconnected()){
						//Stop BlueTooth
						if(mBtAdapter != null){
							Stop_BlueTooth_Scan(context,mBtAdapter);
						}
						return;
					}else{
						bConnectedToServer = true;
						post_Connect_Ldap_Server();
						//Start BlueTooth DisCovery
						if(mBtAdapter == null){
							mBtAdapter = BluetoothAdapter.getDefaultAdapter();
							if(mBtAdapter == null){
								return;
							}else{
								//check if Bt enabled
								if(isBlueToothEnabled(context,mBtAdapter)){
									//Start BlueTooth Scan
									Start_BlueTooth_Scan(context,mBtAdapter);
								}else{
									EnableBlueTooth(context,mBtAdapter);
								}
							}
						}
					}
				}
			}else{
				bConnectedToServer = false;
				EnableWifi(context);
			}
		}else if(action.equals(WIFI_State_Change_Inetent)){
			if(isWifiConnected(context)){
				//check if android net was connected
				//check if can connect to the ldap server
				//to add code here
				//Start to Scan BlueTooth
				if(!checkifconnected()){
					//Stop BlueTooth
					bConnectedToServer = false;
					if(mBtAdapter != null){
						Stop_BlueTooth_Scan(context,mBtAdapter);
					}
					return;
				}else{
					bConnectedToServer = true;
					post_Connect_Ldap_Server();
					//Start BlueTooth DisCovery
					if(mBtAdapter == null){
						mBtAdapter = BluetoothAdapter.getDefaultAdapter();
						if(mBtAdapter == null){
							return;
						}else{
							//check if Bt enabled
							if(isBlueToothEnabled(context,mBtAdapter)){
								//Start BlueTooth Scan
								Start_BlueTooth_Scan(context,mBtAdapter);
							}else{
								EnableBlueTooth(context,mBtAdapter);
							}
						}
					}
				}
			}else{
				//check if Bt Scan was started
				//if yes , stop the BlueTooth Scan
				bConnectedToServer = false;
				if(mBtAdapter != null){
					Stop_BlueTooth_Scan(context,mBtAdapter);
				}
				EnableWifi(context);
				return;
			}
			return;
		}else if(action.equals(BT_STATE_CHANGED)){
			if(mBtAdapter == null){
				mBtAdapter = BluetoothAdapter.getDefaultAdapter();
				if(mBtAdapter == null){
					return;
				}else{
					//check if Bt enabled
					if(!isBlueToothEnabled(context,mBtAdapter)){
						EnableBlueTooth(context,mBtAdapter);
					}
				}
			}
			if(bConnectedToServer){
				Start_BlueTooth_Scan(context,mBtAdapter);
			}
		}else if(action.equals(BT_Discover_Start_Intent)){
			return;			
		}else if(action.equals(BT_Discover_Found_Intent)){
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			Short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,(short) 0);
			String bt_address = device.getAddress();
			String bt_name = device.getName();
			if(bConnectedToServer){
				post_BlueTooth_device_found(bt_name,bt_address);
			}
			return;
		}else if(action.equals(BT_Discover_Finished_Intent)){
			if(bConnectedToServer){
				BlueToothScanThread thread = new BlueToothScanThread(context);
				thread.start();
			}
			return;
		}else if(action.equals(WIFI_Scan_Intent)){
			Log.d("Seth Log","Wifi WIFI_Scan_Intent");
			return;
		}else if(action.equals("android.net.wifi.WIFI_STATE_CHANGED")){
			Log.d("Seth Log","wifi enable/disable");
			//To check if the Wifi was closed ,if true ,endbale the wifi
			//
		}else{
			return;
		}
	}
	private boolean isWifiEnabled(Context ctx){
		if(mWifiManager == null){
			mWifiManager = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
			if(mWifiManager == null){
				bConnectedToServer = false;
				return false;
			}
		}
        return mWifiManager.isWifiEnabled();		
	}
	
	private void EnableWifi(Context ctx){
	    mWifiLock = mWifiManager.createWifiLock(TAG);
        mWifiLock.acquire();
		Intent TurnOnBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		TurnOnBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		ctx.startActivity(TurnOnBtIntent);
        return;		
	}

	private boolean isWifiConnected(Context ctx)
	{
		ConnectivityManager connMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfos = connMgr.getAllNetworkInfo();
	    for (NetworkInfo netInfo : netInfos) {
	      	if(netInfo.getType() == ConnectivityManager.TYPE_WIFI){
	       		if(netInfo.isConnected()){
	       			return true;
	       		}
	       	}
	    }
	    return false;
	}

	private void reloadWlanInfo(){
        //try to connect with default account and then search DN related to specific user ID
		LDAPConnection c = null;
		Filter mFilter = null;
		Filter mFilter1;
		Filter mFilter2;
		try {
			c = new LDAPConnection(service_url, service_port);
			BindResult bindResult = c.bind(default_BindId,default_BindPwd);
		} catch (LDAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			mFilter1 = Filter.create("(objectClass=user)");
			mFilter2 = Filter.createEqualityFilter("sAMAccountName", "Jack.liu"); 
			mFilter = Filter.createANDFilter(mFilter1, mFilter2);
		} catch (LDAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       	String baseDN = null;                
		SearchRequest searchRequest = new SearchRequest(
				"OU=iaSolution,DC=iaSolution,DC=net",
				SearchScope.SUB, mFilter, "distinguishedName");
		try {
			SearchResult searchResult = c.search(searchRequest);
			for (SearchResultEntry entry : searchResult.getSearchEntries()) {
				for (Attribute att : entry.getAttributes()) {
					//baseDN = att.getValueAsDN();
					String name = att.getBaseName();
					baseDN = att.getValue();
				}
			}			
		}catch(Exception e){
			System.err.println("The search was failure.");
		}
		c.close();
       return;
	}
	
	private boolean isBlueToothEnabled(Context ctx,BluetoothAdapter _btAdapter){
    	if(_btAdapter == null){
    		return false;
    	}
    	return _btAdapter.isEnabled();
	}
	private void EnableBlueTooth(Context ctx,BluetoothAdapter _btAdapter){
    	if(_btAdapter != null){
    		if (!_btAdapter.isEnabled()) {
    			Intent TurnOnBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    			TurnOnBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
    			ctx.startActivity(TurnOnBtIntent);
    		}
    	}
	}
	private void Start_BlueTooth_Scan(Context ctx,BluetoothAdapter _btAdapter){
    	if(_btAdapter != null){
    		if (_btAdapter.isEnabled()) {
    			_btAdapter.startDiscovery();
    		}
    	}
	}
	private void Stop_BlueTooth_Scan(Context ctx,BluetoothAdapter _btAdapter){
    	if(_btAdapter != null){
    		if (_btAdapter.isEnabled()) {
    			_btAdapter.startDiscovery();
    		}
    	}
	}

	private boolean checkifconnected(){
	    LDAPConnection LoginConnect = ldap.ldapConnect(address, port);
		if(LoginConnect != null){
			LoginConnect.close();
			LoginConnect = null;
			return false;
	    }
    	return false;
	}
	private void post_Connect_Ldap_Server(){
		return;
	}
	private void post_BlueTooth_device_found(String name,String mac){
		return;
	}
	class BlueToothScanThread extends Thread {
		private Context ctx = null;
		BlueToothScanThread(Context context) {
			ctx = context;
		}
		@Override
		synchronized public void run() {
			super.run();
			try {
				if(ctx != null){
					sleep(50000);
					if(mBtAdapter == null){
						mBtAdapter = BluetoothAdapter.getDefaultAdapter();
						if(mBtAdapter == null){
							return;
						}else{
							//check if Bt enabled
							if(isBlueToothEnabled(ctx,mBtAdapter)){
								//Start BlueTooth Scan
								Start_BlueTooth_Scan(ctx,mBtAdapter);
							}else{
								EnableBlueTooth(ctx,mBtAdapter);
							}
						}
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		@Override
		public void interrupt() {
			super.interrupt();
		}
	} 	
}
