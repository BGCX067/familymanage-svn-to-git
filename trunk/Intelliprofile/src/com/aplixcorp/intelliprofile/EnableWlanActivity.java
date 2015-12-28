package com.aplixcorp.intelliprofile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

public class EnableWlanActivity extends Activity
{
	
	public static final int TURN_WLAN_DIALOG_ID = 0;
	private Context mContext;
	private WifiManager mWifiManager;
	private boolean mOnOff;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext = this;
		mOnOff = getIntent().getExtras().getBoolean("wlan_on");
		mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		if (null == mWifiManager){
	        Toast.makeText(this, "fail to gain WifiManager service", Toast.LENGTH_LONG).show();
	    }
		getWindow().setLayout(0, 0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		removeDialog(TURN_WLAN_DIALOG_ID);
		showDialog(TURN_WLAN_DIALOG_ID);
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}	
	public Dialog onCreateDialog(int id)
    {
    	switch(id)
    	{
	    	case TURN_WLAN_DIALOG_ID:
	    	{
	    		return new AlertDialog.Builder(mContext)
	    		.setTitle(getString(R.string.wlan))
	    		.setMessage(mOnOff ? getString(R.string.turn_on_wlan) : getString(R.string.turn_off_wlan))
	    		.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener(){
	
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						boolean success = mWifiManager.setWifiEnabled(mOnOff);
						finish();
					}})
				.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener(){
	
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
						// TODO Auto-generated method stub
					}})
				.create();
	    	}
    	}
    	return super.onCreateDialog(id);
    }
}