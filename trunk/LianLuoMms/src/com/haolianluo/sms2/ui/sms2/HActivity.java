package com.haolianluo.sms2.ui.sms2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;

import com.haolianluo.sms2.data.HConst;
import com.lianluo.core.util.ToolsUtil;
import com.haolianluo.sms2.R;

public class HActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerReceiver(killOneself, new IntentFilter(HConst.ACTION_KILL_ONESELF));
		registerReceiver(updateDialog, new IntentFilter(HConst.ACTION_UPDATE_DIALOG));
	}
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode == KeyEvent.KEYCODE_SEARCH)
    	{
    		return true;
    	}
        return super.onKeyDown(keyCode, event);
    }
    
	@Override
	protected void onDestroy() {
		unregisterReceiver(killOneself);
		unregisterReceiver(updateDialog);
		super.onDestroy();
	}
	
	
    BroadcastReceiver killOneself = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};
    BroadcastReceiver updateDialog = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			intent.setClass(context, HUpdateDialog.class);
			context.startActivity(intent);
		}
	};
	
	@SuppressWarnings("unused")
	public void isLogin(){
		// 增加是否登录的判断----
		final SharedPreferences pref = getSharedPreferences(HConst.PREF_USER, Context.MODE_PRIVATE);
		boolean isLogin = pref.getBoolean(HConst.USER_KEY_LOGIN, false);
		boolean isShow = pref.getBoolean(HConst.AFTER_NO_SHOW, false);
		if (!isLogin && !isShow && ToolsUtil.IM_FLAG) {// 没有登录提示
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			//builder.setTitle(getString(R.string.empty));
			builder.setMessage(getString(R.string.loging_after));
			builder.setPositiveButton(R.string.after_noshow, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					pref.edit().putBoolean(HConst.AFTER_NO_SHOW, true).commit();
				}
			});
			builder.setNeutralButton(R.string.register, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent();
					intent.setClass(HActivity.this, HResLoginActivity.class);
					startActivity(intent);
				}
			});
			builder.setNegativeButton(R.string.ensure, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					loginAfterSendSms();
				}
			});
			builder.create().show();
		}else{
			loginAfterSendSms();
		}
	}
	
	
	public void loginAfterSendSms(){};
	
	
}
