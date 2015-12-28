package com.jayce.aidltest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class AIDLtestActivity extends Activity {
	private ITestService mTestService = null;
	private Button mTestButton;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mTestButton = (Button)findViewById(R.id.button_test);
        mTestButton.setEnabled(false);
        Button bt_bind = (Button)findViewById(R.id.button_bind);
        bt_bind.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent service = new Intent("com.jayce.aidltest.AIDLtestService");
				bindService(service, mConnection, Context.BIND_AUTO_CREATE);
			}});
        
        mTestButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					Toast.makeText(AIDLtestActivity.this, mTestService.getStringValue(), Toast.LENGTH_LONG).show();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}});
    }
    
    private ServiceConnection mConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mTestService = ITestService.Stub.asInterface(service);
			Log.e("jayce", "connect");
			mTestButton.setEnabled(true);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
    	
    };
    
    public void onDestroy()
    {
    	super.onDestroy();
    	unbindService(mConnection);
    	Intent service = new Intent(this, AIDLtestService.class);
    	stopService(service);
    }
}