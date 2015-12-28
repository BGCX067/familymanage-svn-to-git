package com.aplixcorp.intelliprofile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

public class DisableBTActivity extends Activity{	
	public static final int DISABLE_BT_DIALOG_ID = 0;
	private Context mContext;
	private boolean isCloseBT;
	private BluetoothAdapter btAdapter;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext = this;
		isCloseBT = getIntent().getExtras().getBoolean("close_BT");
	        btAdapter = BluetoothAdapter.getDefaultAdapter();

		getWindow().setLayout(0, 0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

                if (isCloseBT && btAdapter.isEnabled()){
		    showDialog(DISABLE_BT_DIALOG_ID);
                }
	}
	
	public Dialog onCreateDialog(int id)
    {
    	switch(id)
    	{
	    	case DISABLE_BT_DIALOG_ID:
	    	{
	    		return new AlertDialog.Builder(mContext)
	    		.setTitle(getString(R.string.diable_bt))
	    		.setMessage(getString(R.string.turn_off_bt))
	    		.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						btAdapter.disable();
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
