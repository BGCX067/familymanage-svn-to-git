package com.haolianluo.sms2.ui.sms2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SkinLinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haolianluo.sms2.R;
import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.data.HSharedPreferences;
import com.haolianluo.sms2.model.HStatistics;
import com.haolianluo.sms2.model.HUpdateModel;
import com.haolianluo.sms2.model.HUpdateParser;
import com.haolianluo.sms2.ui.ComposeMessageActivity;
import com.lianluo.core.event.IEvent;
import com.lianluo.core.task.BaseTask;
import com.lianluo.core.task.TaskManagerFactory;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;

public class HSettingActivity extends SkinActivity{
	
	private String p;
	private String url;
	private HStatistics mStatistics;
	/**
	 * 0.未出现
	 * 1.出现中
	 */
	private int dialogShowStatus = 0;
	private SkinLinearLayout setting_layout_bg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		HConst.markActivity = 7;
		mStatistics = new HStatistics(this);
		setContentView(R.layout.settinglayout);
		setting_layout_bg = (SkinLinearLayout) findViewById(R.id.setting_layout_bg);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		setting_layout_bg.changeSkin();
		init();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		HConst.markActivity = 7;
	}
	
	/***
	 * 1、消息栏提醒开关 2、动画短信提醒开关 3、短信震动提醒开关 4、快捷短信提醒开关 8、账户管理 5、资源库 6、检查更新7、分享 
	 */
	private void init(){
	   final HSharedPreferences spf = new HSharedPreferences(this);
	   
	   //1、消息栏提醒开关 
       final Button bt_1 = (Button) findViewById(R.id.bt_1);
       if(spf.getMessageSwitch()){
			bt_1.setBackgroundResource(R.drawable.on);
		}else{
			bt_1.setBackgroundResource(R.drawable.off);
		}
       
       bt_1.setOnClickListener(new OnClickListener() {
	
		@Override
		public void onClick(View v) {
			if(spf.getMessageSwitch()){
				bt_1.setBackgroundResource(R.drawable.off);
				spf.setMessageSwitch(false);
				
			}else{
				bt_1.setBackgroundResource(R.drawable.on);
				spf.setMessageSwitch(true);
				
			}
		}
	   });
       
       //2、动画短信提醒开关 
       final Button bt_2 = (Button) findViewById(R.id.bt_2);
       if(spf.getFlashSwitch()){
				bt_2.setBackgroundResource(R.drawable.on);
		}else{
			bt_2.setBackgroundResource(R.drawable.off);
		}
       bt_2.setOnClickListener(new OnClickListener() {
   		
   		@Override
   		public void onClick(View v) {
   			if(spf.getFlashSwitch()){
   				bt_2.setBackgroundResource(R.drawable.off);
				spf.setFlashSwitch(false);
			}else{
				bt_2.setBackgroundResource(R.drawable.on);
				spf.setFlashSwitch(true);
			}
   		}
   	   });
       
       //3、短信震动提醒开关
       final Button bt_3 = (Button) findViewById(R.id.bt_3);
       if(spf.getVibrationSwitch()){
				bt_3.setBackgroundResource(R.drawable.on);
		}else{
			bt_3.setBackgroundResource(R.drawable.off);
		}
       bt_3.setOnClickListener(new OnClickListener() {
   		
   		@Override
   		public void onClick(View v) {
   			if(spf.getVibrationSwitch()){
   				bt_3.setBackgroundResource(R.drawable.off);
				spf.setVibrationSwitch(false);
			}else{
				bt_3.setBackgroundResource(R.drawable.on);
				spf.setVibrationSwitch(true);
			}
   		}
   	   });
       
       //4、快捷短信提醒开关
       final Button bt_4 = (Button) findViewById(R.id.bt_4);
        if(spf.getShortcutSwitch()){
				bt_4.setBackgroundResource(R.drawable.on);
		}else{
			bt_4.setBackgroundResource(R.drawable.off);
		}
       bt_4.setOnClickListener(new OnClickListener() {
   		
   		@Override
   		public void onClick(View v) {
   			
   			if(spf.getShortcutSwitch()){
   				bt_4.setBackgroundResource(R.drawable.off);
				spf.setShortcutSwitch(false);
				
			}else{
				bt_4.setBackgroundResource(R.drawable.on);
				spf.setShortcutSwitch(true);
				
			}
   		}
   	   });
       
    // 8、资源库 
       final RelativeLayout bt_8 = (RelativeLayout) findViewById(R.id.rl_setting8);
       final ImageView  im_8  = (ImageView)findViewById(R.id.im_s8);
       if(ToolsUtil.IM_FLAG)
       {    im_8.setVisibility(View.VISIBLE);
    	   bt_8.setVisibility(View.VISIBLE);
       }
       else
       {   im_8.setVisibility(View.GONE);
    	   bt_8.setVisibility(View.GONE);
       }
       bt_8.setOnClickListener(new OnClickListener() {
   		
   		@Override
   		public void onClick(View v) {
   			mStatistics.add(HStatistics.Z5_8, "", "", "");
   			SharedPreferences pref = getSharedPreferences(HConst.PREF_USER, Context.MODE_PRIVATE);
   			boolean login = pref.getBoolean(HConst.USER_KEY_LOGIN, false);
   			if(login)
   			{
	   			Intent intent = new Intent();
	   			intent.setClass(HSettingActivity.this, HResAccountActivity.class);
	   			String phone = pref.getString(HConst.USER_KEY_PHONE, "");
	   			intent.putExtra("phone", phone);
	   			startActivityForResult(intent, HConst.REQUEST_RESOURCEACCOUNT);
   			}
   			else
   			{
   				Intent intent = new Intent();
	   			intent.setClass(HSettingActivity.this, HResLoginActivity.class);
	   			startActivityForResult(intent, HConst.REQUEST_RESOURCEACCOUNT);
   			}
   		}
   	   });
       
//       // 5、资源库 
//       final RelativeLayout bt_5 = (RelativeLayout) findViewById(R.id.rl_setting5);
//       bt_5.setOnClickListener(new OnClickListener() {
//   		
//   		@Override
//   		public void onClick(View v) {
//   			Intent intent = new Intent();
//   			intent.setClass(HSettingActivity.this, HResLibActivity.class);
//   			startActivityForResult(intent, HConst.REQUEST_RESOURCELIB);
//   		}
//   	   });
       
       //6、检查更新
       final RelativeLayout bt_6 = (RelativeLayout) findViewById(R.id.rl_setting6);
       bt_6.setOnClickListener(new OnClickListener() {
   		
   		@Override
   		public void onClick(View v) {
   			mStatistics.add(HStatistics.Z5_11, "", "", "");
   			if(ToolsUtil.checkNet(HSettingActivity.this)) {
   				showDialog(0);
   				dialogShowStatus = 1;
   	   			TaskManagerFactory.createParserTaskManager().addTask(new BaseTask(null) {
   					@Override
	   					public void doTask(IEvent event) throws Exception {
	   						HUpdateModel model = new HUpdateParser(HSettingActivity.this).update();
	   						
	   						if(dialogShowStatus == 0)
	   						{
	   							return;
	   						}
	   						dismissDialog(0);
	   						removeDialog(0);
	   						
	   						SharedPreferences sp = getSharedPreferences(HConst.UPDATE_FLAG, 0);
	   						Editor editor = sp.edit();
	   						if(model == null)
	   						{
	   							Toast.makeText(HSettingActivity.this, R.string.connect_fail, Toast.LENGTH_SHORT).show();
	   						}
	   						else if(model.isUpdate() && model.getSt().equals("1")){
	   							//发现强制需要更新
	   							if(!ToolsUtil.FORCEUPDATE_FLAG)
	   							{
				   					setUpdateFlag();
				   					//无需更新
				   					handler.sendEmptyMessage(2);
				   					return;
	   							}	
			   					editor.putBoolean(HConst.UPDATE, true);
			   					editor.putBoolean(HConst.IS_UPDATE, true);
			   					editor.putInt(HConst.SHOW_TIMES, 1);
			   					editor.commit();
			   					setUpdateFlag();
			   					
			   					p = model.getP();
			   					url = model.getUrl();
			   					handler.sendEmptyMessage(1);
			   					HLog.d("update_int", "1");
			   				}else if(model.isUpdate() && model.getSt().equals("0"))
			   				{
			   					editor.putBoolean(HConst.UPDATE, true);
			   					editor.commit();
			   					setUpdateFlag();
			   					
			   					p = model.getP();
			   					url = model.getUrl();
			   					handler.sendEmptyMessage(0);
			   					HLog.d("update_int", "0");
			   				}else if(!model.isUpdate()){
			   					setUpdateFlag();
			   					//无需更新
			   					handler.sendEmptyMessage(2);
			   					HLog.d("update_int", "2");
			   				}
	   					}
   				});
   			} else {
   				Toast.makeText(HSettingActivity.this, R.string.connect_fail, Toast.LENGTH_SHORT).show();
   			}
   		}
   	   });
       
       //7、分享 
       final RelativeLayout bt_7 = (RelativeLayout) findViewById(R.id.rl_setting7);
       bt_7.setOnClickListener(new OnClickListener() {
   		
   		@Override
   		public void onClick(View v) {
   			ComposeMessageActivity.setSendFlag(true);
   			
   			mStatistics.add(HStatistics.Z5_12, "", "", "");
//   		Intent intentShare  = new Intent();
//			intentShare.putExtra("setting", true);
//			intentShare.setClass(HSettingActivity.this, HEditSmsActivity.class);
//			startActivity(intentShare);
   			
   			Intent intent = new Intent(HSettingActivity.this, HTalkActivity.class);

   	        intent.putExtra("exit_on_sent", true);
   	        intent.putExtra("forwarded_message", true);

   	        intent.putExtra("sms_body", getString(R.string.inviteBody));
   	        intent.putExtra("editsms", true);
   	     
   	        intent.setClassName(HSettingActivity.this, "com.haolianluo.sms2.ui.ForwardMessageActivity");
   	        startActivity(intent);
   	        //finish();
   		}
   	   });
       
       //8、关于
       final RelativeLayout bt_9 = (RelativeLayout) findViewById(R.id.rl_setting9);
       bt_9.setOnClickListener(new OnClickListener() {
   		
   		@Override
   		public void onClick(View v) {
   			mStatistics.add(HStatistics.Z5_14, "", "", "");
   			LayoutInflater factory = LayoutInflater.from(HSettingActivity.this);
			View layout = factory.inflate(R.layout.about_dialog, null);
			TextView smsBody = (TextView) layout.findViewById(R.id.version);
			// 正式版本号
			String version = getString(R.string.version_about).replaceAll("\\#",ToolsUtil.getVersion(HSettingActivity.this));
			smsBody.setText(version);
			Dialog dialog = new Dialog(HSettingActivity.this,R.style.about_style);
			dialog.setContentView(layout);
			dialog.show();
   		}
   	   });
       
       //9、我说两句
       final RelativeLayout bt_10 = (RelativeLayout) findViewById(R.id.rl_setting10);
//       String str = ToolsUtil.getChannel(HSettingActivity.this).trim();
//       if(str.equals("qq")){
    	   bt_10.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mStatistics.add(HStatistics.Z5_16, "", "", "");
				Intent intentSpeek  = new Intent();
				intentSpeek.setClass(HSettingActivity.this, HProblemActivity.class);
				startActivity(intentSpeek);
			}
		});
//       }else{
//    	   bt_10.setVisibility(View.GONE);
//       }
    	   
    	final RelativeLayout bt_11 = (RelativeLayout) findViewById(R.id.rl_setting11);
        bt_11.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(HSettingActivity.this, HBackupActivity.class);
				startActivity(i);
			}
        	
        });
       
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case 0:
			ProgressDialog dialog = new ProgressDialog(this);
			//dialog.setCancelable(false);
			dialog.setMessage(getString(R.string.checkUpdate));
			dialog.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(DialogInterface dialog, int keyCode,KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_SEARCH) {
						dismissDialog(0);
   						removeDialog(0);
   						dialogShowStatus = 0;
						return true;
					}
					return false;
				}
			});
			return dialog;
		}
		return super.onCreateDialog(id);
	}
	
	private Handler handler = new Handler(){
		@Override
		public void dispatchMessage(Message msg) {
			AlertDialog.Builder builder = new AlertDialog.Builder(HSettingActivity.this);
			builder.setTitle(R.string.havenewTitle);
			builder.setIcon(android.R.drawable.ic_dialog_alert);
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
			
			switch(msg.what){
			case 0:
				builder.setMessage(R.string.havenew);
				builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						setSettingDilaog(false);
						//点击普通更新按钮之后停止通知栏提醒用户更新；
						//getIntent().removeExtra("showNotification");
						
						SharedPreferences sp = getSharedPreferences(HConst.UPDATE_FLAG, 0);
						sp.edit().putBoolean(HConst.ISSHOW_NOTIFA, false).commit();
						
						Uri apk = Uri.parse(p + url); 
						Intent intent = new Intent(Intent.ACTION_VIEW, apk); 
						startActivity(intent);
					}
				});
				builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//设置标志，不再次进行提醒,false则不再进行提醒
						SharedPreferences sp = getSharedPreferences(HConst.UPDATE_FLAG, 0);
						sp.edit().putBoolean(HConst.ISSHOW_NOTIFA, false).commit();
						dialog.dismiss();
						setSettingDilaog(false);
					}
				});
				//监听dialog被返回键取消事件，根据cancel_exit的值做出相应动作
				builder.setOnCancelListener(new OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						//设置标志，不再次进行提醒,false则不再进行提醒
						SharedPreferences sp = getSharedPreferences(HConst.UPDATE_FLAG, 0);
						sp.edit().putBoolean(HConst.ISSHOW_NOTIFA, false).commit();
						dialog.dismiss();
						setSettingDilaog(false);
					}
				});
				if(!getAutoDialogStatus())
				{
					setSettingDilaog(true);
					builder.show();
				}
				break;
			case 1:
				builder.setMessage(R.string.force_update_first);
				builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						setSettingDilaog(false);
						Uri apk = Uri.parse(p + url); 
						Intent intent = new Intent(Intent.ACTION_VIEW, apk); 
						startActivity(intent);
					}
				});
				builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						setSettingDilaog(false);
						SharedPreferences sp = getSharedPreferences(HConst.UPDATE_FLAG, 0);
						sp.edit().putBoolean(HConst.ISSHOW_FORCEUPDATE, false).commit();
					}
				});
				//监听dialog被返回键取消事件，根据cancel_exit的值做出相应动作
				builder.setOnCancelListener(new OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						setSettingDilaog(false);
						SharedPreferences sp = getSharedPreferences(HConst.UPDATE_FLAG, 0);
						sp.edit().putBoolean(HConst.ISSHOW_FORCEUPDATE, false).commit();
					}
				});
				if(!getAutoDialogStatus())
				{
					setSettingDilaog(true);
					builder.show();
				}
				
				SharedPreferences sp = getSharedPreferences(HConst.UPDATE_FLAG, 0);
				Editor editor = sp.edit();
				editor.putBoolean(HConst.IS_UPDATE, true);	//已经发现有强制更新
				editor.putInt(HConst.SHOW_TIMES, 1);		//标识第一次，商店和预览界面无法接入
				//缓存更新地址
				editor.putString(HConst.P, p);
				editor.putString(HConst.URL, url);
				editor.commit();
				
				break;
			case 2:
				Toast.makeText(HSettingActivity.this, R.string.nonew, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
    private void setUpdateFlag()
    {
		SharedPreferences sp = getSharedPreferences(HConst.UPDATE_FLAG, 0);
		Editor editor = sp.edit();
		editor.putBoolean(HConst.UPDATE, true);
		editor.commit();
    }
    
    private void setSettingDilaog(boolean flag)
    {
		SharedPreferences sp = getSharedPreferences(HConst.UPDATE_FLAG, 0);
		Editor editor = sp.edit();
		editor.putBoolean(HConst.SETTING_DIALOG_SHOW, flag);
		editor.commit();
    }
    
    private boolean getAutoDialogStatus()
    {
    	boolean flag = false;
    	SharedPreferences sp = getSharedPreferences(HConst.UPDATE_FLAG, 0);
    	flag = sp.getBoolean(HConst.AUTO_DIALOGS_SHOW, false);
    	return flag;
    }
    
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		HLog.i("resultCode " +resultCode);
		if(resultCode == 105){
			finish();
		}
	};
}