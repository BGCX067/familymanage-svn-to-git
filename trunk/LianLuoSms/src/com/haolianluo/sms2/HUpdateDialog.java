package com.haolianluo.sms2;
import com.haolianluo.sms2.data.HConst;
import com.lianluo.core.util.HLog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

public class HUpdateDialog extends HActivity {
	private String p;
	private String url;
	private int cancel_exit;	//0为强制更新取消, 1为强制更新退出，2为普通更新取消
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.hupdate_dialog);
        
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		
		SharedPreferences spU = getSharedPreferences(HConst.UPDATE_FLAG, 0);
		if(getIntent().getStringExtra("showNotification") != null 
				&& getIntent().getStringExtra("showNotification").equals("show")) 
				//&& spU.getBoolean(HConst.ISSHOW_NOTIFA, true))
		{
			cancel_exit = 2;
			//普通更新提醒：更新-取消
			builder.setTitle(R.string.havenewTitle);
			builder.setMessage(R.string.havenew);
			builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					setAutoDilaog(false);
					SharedPreferences spU = getSharedPreferences(HConst.UPDATE_FLAG, 0);
					p = spU.getString(HConst.P, "");
					url = spU.getString(HConst.URL, "");
					spU.edit().putBoolean(HConst.ISSHOW_NOTIFA, false).commit();
					
					Uri apk = Uri.parse(p + url); 
					Intent intent = new Intent(Intent.ACTION_VIEW, apk); 
					startActivity(intent);
					
					finish();
					
				}
			});
			builder.setNegativeButton(R.string.calcel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
					setAutoDilaog(false);
					//dialog.dismiss();
					//设置标志，不再次进行提醒,false则不再进行提醒
					SharedPreferences sp = getSharedPreferences(HConst.UPDATE_FLAG, 0);
					sp.edit().putBoolean(HConst.ISSHOW_NOTIFA, false).commit();
					
				}
			});
		}else if(spU.getInt(HConst.SHOW_TIMES, 0) == 2)
		{
			cancel_exit = 1;
			builder.setTitle(R.string.havenewTitle);
			builder.setMessage(R.string.force_update_s);
			builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					setAutoDilaog(false);
					SharedPreferences sp = getSharedPreferences(HConst.UPDATE_FLAG, 0);
					sp.edit().putInt(HConst.SHOW_TIMES, 1).commit();
					p = sp.getString(HConst.P, "");
					url = sp.getString(HConst.URL, "");
					
					finish();
					
					Uri apk = Uri.parse(p + url); 
					Intent intent = new Intent(Intent.ACTION_VIEW, apk); 
					startActivity(intent);
					
					
				}
			});
			builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
					setAutoDilaog(false);
					//dialog.dismiss();
					SharedPreferences sp = getSharedPreferences(HConst.UPDATE_FLAG, 0);
					sp.edit().putInt(HConst.SHOW_TIMES, 2).commit();
					
					
					Intent intent = new Intent();
					intent.setAction(HConst.ACTION_KILL_ONESELF);
					sendBroadcast(intent);
				}
			});
		}
		else
		{	
			cancel_exit = 0;
			//强制更新提醒：更新-取消  首次提醒；
			builder.setTitle(R.string.havenewTitle);
			builder.setMessage(R.string.force_update_first);
			builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					setAutoDilaog(false);
					SharedPreferences sp = getSharedPreferences(HConst.UPDATE_FLAG, 0);
					p = sp.getString(HConst.P, "");
					url = sp.getString(HConst.URL, "");
					
					Uri apk = Uri.parse(p + url); 
					Intent intent = new Intent(Intent.ACTION_VIEW, apk); 
					startActivity(intent);
					finish();
				}
			});
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
					setAutoDilaog(false);
					//dialog.dismiss();
					SharedPreferences sp = getSharedPreferences(HConst.UPDATE_FLAG, 0);
					sp.edit().putBoolean(HConst.ISSHOW_FORCEUPDATE, false).commit();
					
					HLog.d("dialog_0", "0");
				}
			});
		}
		
		//监听dialog被返回键取消事件，根据cancel_exit的值做出相应动作
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				setAutoDilaog(false);
				SharedPreferences sp = getSharedPreferences(HConst.UPDATE_FLAG, 0);
				switch (cancel_exit) {
				case 0:
					finish();
					sp.edit().putBoolean(HConst.ISSHOW_FORCEUPDATE, false).commit();
					break;
					
				case 1:
					finish();
					
					sp.edit().putInt(HConst.SHOW_TIMES, 2).commit();
					Intent intent = new Intent();
					intent.setAction(HConst.ACTION_KILL_ONESELF);
					sendBroadcast(intent);
					break;
					
				case 2:
					finish();
					//设置标志，不再次进行提醒,false则不再进行提醒
					sp.edit().putBoolean(HConst.ISSHOW_NOTIFA, false).commit();
					break;
					
				default:
					finish();
					break;
				}
			}
		});
		
		builder.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_SEARCH)
				{
					return true;
				}
				return false;
			}
		});
		
		builder.create();
		
		if(!getSettingDialogStatus())
		{	
			setAutoDilaog(true);
			builder.show();
			setUpdateFlag();
		}
    }
    
    private boolean getSettingDialogStatus()
    {
    	boolean flag = false;
    	SharedPreferences sp = getSharedPreferences(HConst.UPDATE_FLAG, 0);
    	flag = sp.getBoolean(HConst.SETTING_DIALOG_SHOW, false);
    	return flag;
    }
    
    private void setAutoDilaog(boolean flag)
    {
		SharedPreferences sp = getSharedPreferences(HConst.UPDATE_FLAG, 0);
		Editor editor = sp.edit();
		editor.putBoolean(HConst.AUTO_DIALOGS_SHOW, flag);
		editor.commit();
    }
    
    private void setUpdateFlag()
    {
		SharedPreferences sp = getSharedPreferences(HConst.UPDATE_FLAG, 0);
		Editor editor = sp.edit();
		editor.putBoolean(HConst.UPDATE, true);
		editor.commit();
    }
}
