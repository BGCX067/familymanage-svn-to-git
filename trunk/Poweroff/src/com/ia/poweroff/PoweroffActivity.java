package com.ia.poweroff;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class PoweroffActivity extends Activity {
    /** Called when the activity is first created. */
	
	KeyguardManager mKeyguardManager = null;
    KeyguardLock mKeyguardLock = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
       
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
         /*
        Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");

        //intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);*/
        new AlertDialog.Builder(this)
        .setMessage("Message")
        .setOnCancelListener(new OnCancelListener() {
          public void onCancel(DialogInterface dialog) {
           // return false;
          }})
        .show();
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
    	switch(keyCode) {
    	                case KeyEvent.KEYCODE_BACK:
    	                         Log.e("Test01", "onKeyDown KEYCODE_BACK");
    	                 case KeyEvent.KEYCODE_HOME:
    	                         Log.e("Test01", "onKeyDown KEYCODE_HOME");
    	                         return true;
    	                 }
    	                 return true;
    }
    
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
    	switch(keyCode) {
    	                case KeyEvent.KEYCODE_BACK:
    	                         Log.e("Test01", "onKeyDown KEYCODE_BACK");
    	                 case KeyEvent.KEYCODE_HOME:
    	                         Log.e("Test01", "onKeyDown KEYCODE_HOME");
    	                         return true;
    	                 }
    	                 return true;
    }
    /*
    public void onAttachedToWindow() 
    {
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
    	super.onAttachedToWindow();
    }
   */
}