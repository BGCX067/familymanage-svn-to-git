package com.GetPosition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GetPosition extends Activity {
    /** Called when the activity is first created. */
	private static final String LOGTAG = "GetPosition";
    private final int Login_View_id = R.layout.login;
    private final int Login_User_Error = 10000;
    private final int Login_Pwd_Error = 10001;
    private final int Login_Error = 10002;
    private final int Login_Processing = 10003;
    private static String sUserName = null;
    private static String sUserPwd = null;
    private EditText vUserName;
    private EditText vUserPwd;
    private Button vLoginIn;
    private AlertDialog vAlertDialog;
    private ProgressDialog vProDialog;
    
    private static String address = "10.90.0.20";
    private static int port = 389;
    private static String UserId = "Jack.Liu";
    private static String UserPwd = "laxMissth22";
    private static String UserGroup = "R&D Task Force 7";
    private static String CompanyInformation = "OU=iaSolution,DC=iaSolution,DC=net";
    private static String WlanRequest = "WLAN";
	private ldap client = null ;
	private Context mContext;
	private WifiManager mWifiManager;
	public final static int REQUEST_ENABLE_BT = 10000;
	private ArrayList<HashMap<String, Object>> WlanNewScanningListArray = new ArrayList<HashMap<String, Object>>();
	private boolean mSuccess = false;
	private boolean mConnected = false;
	private static final int SUCCESS = 0;
	private static final int ERROR = 1;
	private static int LoginResult = SUCCESS;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(Login_View_id);
        vUserName = (EditText)findViewById(R.id.username);
        vUserPwd = (EditText)findViewById(R.id.userpwd);
        vLoginIn = (Button)findViewById(R.id.Login_In);
        vLoginIn.setOnClickListener(mButtonClickListener);
        mContext = this;
        
    }
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch(id){
    	case Login_Processing:
    	{
    		if(vProDialog != null){
    			vProDialog.dismiss();
    			vProDialog = null;
    		}
			ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(R.string.Login);
            progressDialog.setMessage(getString(R.string.Login_Processing));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                getText(R.string.Cancel),
                new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int whichButton) {
						vProDialog.dismiss();
						vProDialog = null;
					}
                });
			progressDialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {
						public void onCancel(DialogInterface dialog) {
							vProDialog.dismiss();
							vProDialog = null;
						}
					});
			vProDialog = progressDialog;
            return vProDialog;           
    		
    	}
    	case Login_User_Error:
    	{
    		if(vAlertDialog != null){
    			vAlertDialog.dismiss();
    			vAlertDialog = null;
    		}
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(GetPosition.this);
    		builder.setTitle(R.string.Warning);
    		builder.setMessage(R.string.Login_UserName_Error);
    		builder.setPositiveButton(R.string.Confirm, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					vAlertDialog.dismiss();
					vAlertDialog = null;
				}
    		});
    		vAlertDialog = builder.create();
    		return vAlertDialog;
    	}
    	case Login_Pwd_Error:
    	{
    		if(vAlertDialog != null){
    			vAlertDialog.dismiss();
    			vAlertDialog = null;
    		}
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(GetPosition.this);
    		builder.setTitle(R.string.Warning);
    		builder.setMessage(R.string.Login_Pwd_Error);
    		builder.setPositiveButton(R.string.Confirm, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					vAlertDialog.dismiss();
					vAlertDialog = null;
				}
    		});
    		vAlertDialog = builder.create();
    		return vAlertDialog;
    	}
    	case Login_Error:
    	{
    		if(vAlertDialog != null){
    			vAlertDialog.dismiss();
    			vAlertDialog = null;
    		}
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(GetPosition.this);
    		builder.setTitle(R.string.Warning);
    		builder.setMessage(R.string.Login_Error);
    		builder.setPositiveButton(R.string.Confirm, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					vAlertDialog.dismiss();
					vAlertDialog = null;
				}
    		});
    		vAlertDialog = builder.create();
    		return vAlertDialog;
    	}
    	default:
    		return null;
    	}
    }
    
    private View.OnClickListener mButtonClickListener = new View.OnClickListener(){
    	public void onClick(View v){
    		switch(v.getId()){
    		case R.id.Login_In:{
    			removeDialog(Login_Processing);
				showDialog(Login_Processing);
    			sUserName = vUserName.getText().toString().trim();
    			sUserPwd = vUserPwd.getText().toString().trim();
    			Log.d("Seth Log","userName = " + sUserName);
    			Log.d("Seth Log","userPwd = " + sUserPwd);
    			if(sUserName == null){
    				removeDialog(Login_User_Error);
    				showDialog(Login_User_Error);	
    			}else if(sUserPwd == null){
    				removeDialog(Login_Pwd_Error);
    				showDialog(Login_Pwd_Error);
    			}else{
    				removeDialog(Login_Processing);
    				showDialog(Login_Processing);
    				MyThread mythread = new MyThread();
    				mythread.start();
    			}
    		}
    		default:
    			break;
    		}
    	}
    };
    private boolean isConnectWifi(){
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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
    
	class MyThread extends Thread {
		MyThread() {;}
		private WifiManager mThreadWifi = null;
		private boolean bWifiConnected = false;
		@Override
		synchronized public void run() {
			super.run();
			mThreadWifi = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
			if(mThreadWifi == null){
				LoginResult = ERROR;
				removeDialog(Login_Processing);
				return;
			}
		    if (!mThreadWifi.isWifiEnabled()){
		    	boolean success = mWifiManager.setWifiEnabled(true);
	        }
		    for(int index = 0; index < 3; index ++){
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(isConnectWifi()){
					bWifiConnected = true;
					break;
				}
		    }
		    if(!bWifiConnected){
		    	LoginResult = ERROR;
				removeDialog(Login_Processing);
		    	
		    	return;
		    }
		    
		    LDAPConnection LoginConnect = ldap.ldapConnect(address, port);
		    if(LoginConnect == null){
		    	LoginResult = ERROR;
				removeDialog(Login_Processing);
		    	return ;
		    }

		    //clear the wlan info form database
		    getContentResolver().delete(DataBaseProvider.CONTENT_URI_WLAN_PROFILES, null,null);
		    //clear the bluetooth info form database
		    getContentResolver().delete(DataBaseProvider.CONTENT_URI_BT_PROFILES, null,null);

		    int iResult = 0;
			String baseDN = ldap.GetbaseDN(LoginConnect,UserId);
			Log.d("Seth Log","baseDN = " + baseDN);
			if(iResult == ldap.SUCCESS){
				SearchResult searchBtResult = ldap.getBlueToothInfo(LoginConnect, baseDN, UserPwd);
				for (SearchResultEntry entry : searchBtResult.getSearchEntries()) {
					for (Attribute att : entry.getAttributes()) {
						String name = att.getBaseName();
						String value = att.getValue();
						if(name.equals("ipPhone")){
							ContentValues values = new ContentValues();
							values.put(DataBaseHelper.BlueTooth_Mac, att.getValue());
							getContentResolver().insert(DataBaseProvider.CONTENT_URI_BT_PROFILES, values);
							Log.d("Seth Log","Bt Mac Address = " + att.getValue());
						}
					}
				}
				
				SearchResult searchResult = ldap.getWifiInfo(LoginConnect,baseDN, UserPwd);
				for (SearchResultEntry entry : searchResult.getSearchEntries()) {
					for (Attribute att : entry.getAttributes()) {
						String name = att.getBaseName();
						if(name.equals("targetAddress"))
						{
							ContentValues values = new ContentValues();
							values.put(DataBaseHelper.Wlan_Mac, att.getValue());
							getContentResolver().insert(DataBaseProvider.CONTENT_URI_WLAN_PROFILES, values);
						}
					}
				}
			}
			removeDialog(Login_Processing);
			vLoginIn.setClickable(false);
			vLoginIn.setText("has Login in");
			if(LoginConnect != null){
				LoginConnect.close();
				LoginConnect = null;
			}
			
		}

		@Override
		public void interrupt() {
			super.interrupt();
		}
	} 
}