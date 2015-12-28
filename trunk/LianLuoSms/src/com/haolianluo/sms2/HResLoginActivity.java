package com.haolianluo.sms2;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.model.HStatistics;
import com.haolianluo.sms2.model.HUserLoginParser;
import com.haolianluo.sms2.model.HUserModel;
import com.haolianluo.sms2.model.SmsSendService;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SkinButton;
import android.widget.TextView;
import android.widget.Toast;

public class HResLoginActivity extends SkinActivity
{
	private static final String TAG = "HResLoginActivity";
	//private LinearLayout mSkinLayout;
	LayoutInflater mInflater;
	private Context mContext;
	private String mPhone = "";
	private String mPassword = "";
	Thread mThread;
	ProgressDialog mDialog;
	private static final int MSG_SUCCESS = 0;
	private static final int MSG_FAIL = 1;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext = this;
		mInflater = LayoutInflater.from(mContext);
		setContentView(R.layout.res_login);
	}
	
	public void onStart()
	{
		HLog.e(TAG, "onStart");
		super.onStart();
		initView();
	}
	
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if(mDialog != null)
			{
				mDialog.dismiss();
				mDialog = null;
			}
			switch(msg.what)
			{
				case MSG_SUCCESS:
				{
					SharedPreferences pref = mContext.getSharedPreferences(HConst.PREF_USER, Context.MODE_PRIVATE);
					pref.edit().putBoolean(HConst.USER_KEY_LOGIN, true).commit();
					pref.edit().putString(HConst.USER_KEY_PHONE, mPhone).commit();
					Intent i = new Intent(mContext, HResAccountActivity.class);
					i.putExtra("phone", mPhone);
					
					//初始化数据流
					Intent service = new Intent();
			        service.setClass(HResLoginActivity.this, SmsSendService.class);
			        startService(service);
			        
					finish();
					startActivity(i);
				}
				break;
				case MSG_FAIL:
				{
					Toast.makeText(mContext, getString(R.string.account_error), Toast.LENGTH_LONG).show();
				}
				break;
			}
		}
	};
	
	private void initView()
	{/*
		if(mSkinLayout != null)
		{
			mSkinLayout.removeAllViews();
		}
		mSkinLayout = (SkinLinearLayout)SkinLinearLayout.inflate(mContext, R.layout.res_login, null);
		setContentView(mSkinLayout);*/
		View title = findViewById(R.id.top);
		SkinButton sb = (SkinButton) title.findViewById(R.id.bt_shortcut);
		sb.setVisibility(View.GONE);
		
		Button bt_register = (Button) findViewById(R.id.button_register);
		TextView tv_forgetpw = (TextView)findViewById(R.id.text_forget_pw);
		tv_forgetpw.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				HStatistics mHStatistics = new HStatistics(HResLoginActivity.this);
				if(ToolsUtil.IM_FLAG) {
					mHStatistics.add(HStatistics.Z9_4, "", "", "");
				}
				Intent i = new Intent(mContext, HResForgetpwActivity.class);
				startActivity(i);
			}});
		bt_register.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				HStatistics mHStatistics = new HStatistics(HResLoginActivity.this);
				if(ToolsUtil.IM_FLAG) {
					mHStatistics.add(HStatistics.Z9_2, "", "", "");
				}
				Intent i = new Intent(mContext, HResRegistActivity.class);
				startActivity(i);
			}});
		final EditText et_phone = (EditText) findViewById(R.id.edit_account);
		final EditText et_passwd = (EditText) findViewById(R.id.edit_password);
		Button bt_login = (Button)findViewById(R.id.button_login);
		bt_login.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				HStatistics mHStatistics = new HStatistics(HResLoginActivity.this);
				if(ToolsUtil.IM_FLAG) {
					mHStatistics.add(HStatistics.Z9_1, "", "", "");
				}
				mPhone = et_phone.getEditableText().toString();
				mPassword = et_passwd.getEditableText().toString();
				HLog.e(TAG, "phone:" + mPhone);
				HLog.e(TAG, "password:" + mPassword);
				if(mPhone.trim().length() == 0)
				{
					Toast.makeText(mContext, mContext.getString(R.string.phone_null), Toast.LENGTH_SHORT).show();
				}
				else if(mPassword.trim().length() == 0)
				{
					Toast.makeText(mContext, mContext.getString(R.string.pw_null), Toast.LENGTH_SHORT).show();
				}
				else
				{
					if(mDialog == null)
					{
						mDialog = new ProgressDialog(mContext);
						mDialog.setMessage(getString(R.string.waiting));
						mDialog.show();
					}
					// TODO Add the login operation
					mThread = new Thread(){
						public void run()
						{
							HLog.e(TAG, "RUN");
							try
							{
								HUserModel user = new HUserLoginParser(mContext, mPhone, mPassword).getUserLogin();
								if(user.isResponse())
								{
									Message msg = new Message();
									msg.what = MSG_SUCCESS;
									mHandler.sendMessage(msg);
								}
								else
								{
									Message msg = new Message();
									msg.what = MSG_FAIL;
									mHandler.sendMessage(msg);
								}
							}catch(Exception e)
							{
								Message msg = new Message();
								msg.what = MSG_FAIL;
								mHandler.sendMessage(msg);
							}
						}
					};
					mThread.start();
				}
			}});
	}
}