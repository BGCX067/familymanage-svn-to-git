package com.haolianluo.sms2;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.model.HStatistics;
import com.haolianluo.sms2.model.SmsSendService;
import com.lianluo.core.util.HLog;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HResAccountActivity extends SkinActivity
{
	private static final String TAG = "HResAccountActivity";
	//private LinearLayout mSkinLayout;
	LayoutInflater mInflater;
	private Context mContext;
	private static final int REQUST_CHANGE_PW = 0;
	private String mPhone;
	Thread mThread;
	ProgressDialog mDialog;
	private static final int MSG_SUCCESS = 0;
	private static final int MSG_FAIL = 1;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext = this;
		mInflater = LayoutInflater.from(mContext);
		mPhone = getIntent().getStringExtra("phone");
		if(mPhone == null || mPhone.trim().length() == 0)
		{
			SharedPreferences pref = mContext.getSharedPreferences(HConst.PREF_USER, Context.MODE_PRIVATE);
			mPhone = pref.getString(HConst.USER_KEY_PHONE, "");
		}
		setContentView(R.layout.res_account);
	}
	
	public void onStart()
	{
		HLog.e(TAG, "onStart");
		super.onStart();
		initView();
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == REQUST_CHANGE_PW)
		{
			if(RESULT_OK == resultCode)
			{
				Intent i = new Intent(mContext, HResLoginActivity.class);
				finish();
				startActivity(i);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
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
				}
				break;
				case MSG_FAIL:
				{
					//Toast.makeText(mContext, getString(R.string.account_error), Toast.LENGTH_LONG).show();
				}
				break;
			}
		}
	};
	
	protected void initView()
	{/*
		if(mSkinLayout != null)
		{
			mSkinLayout.removeAllViews();
		}
		mSkinLayout = (SkinLinearLayout)SkinLinearLayout.inflate(mContext, R.layout.res_account, null);
		setContentView(mSkinLayout);*/
		View title = findViewById(R.id.top);
		ImageView iv_title = (ImageView) title.findViewById(R.id.res_title_icon);
		iv_title.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		TextView tv_title = (TextView) title.findViewById(R.id.res_title_text);
		tv_title.setText(R.string.account_manage);
		ImageView sc = (ImageView)title.findViewById(R.id.res_title_button);
		sc.setVisibility(View.GONE);
		ImageView sv = (ImageView)title.findViewById(R.id.res_title_save);
		//sv.setVisibility(View.VISIBLE);
		
		TextView tv_phone = (TextView)findViewById(R.id.phone_number);
		tv_phone.setText(mPhone);
		LinearLayout ll_changepw = (LinearLayout)findViewById(R.id.linearlayout_changepw);
		ll_changepw.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// TODO Send the phone number to server and return a verify code
				Intent i = new Intent(mContext, HResChangepwActivity.class);
				startActivityForResult(i, REQUST_CHANGE_PW);
			}});
		Button bt_logout = (Button)findViewById(R.id.logout_bt);
		bt_logout.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// TODO Send the phone number to server and return a verify code
				new AlertDialog.Builder(mContext)
				.setTitle(R.string.logout)
				.setMessage(R.string.logout_confirm)
				.setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						SharedPreferences pref = getSharedPreferences(HConst.PREF_USER, Context.MODE_PRIVATE);
						pref.edit().putBoolean(HConst.USER_KEY_LOGIN, false).commit();
						pref.edit().putString(HConst.USER_KEY_PHONE, "").commit();
						Intent service = new Intent(mContext, SmsSendService.class);
						service.putExtra("logout", true);
						mContext.startService(service);
						Intent i = new Intent(mContext, HResLoginActivity.class);
						finish();
						startActivity(i);
					}})
				.setNegativeButton(R.string.calcel, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}})
				.create()
				.show();
			}});
		
		sv.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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
						{/*
							HUserModel user = new HUserLoginParser(mContext, mPhone, "").getUserLogin();
							if(user.isResponse())
							{
								Message msg = new Message();
								msg.what = MSG_SUCCESS;
								mHandler.sendMessage(msg);
							}
							else*/
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
			}});
	}
}