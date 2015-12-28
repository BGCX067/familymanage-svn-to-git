package com.haolianluo.sms2.ui.sms2;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.model.HResponseModel;
import com.haolianluo.sms2.model.HUserForgetParser;
import com.haolianluo.sms2.model.HUserResSmsParser;
import com.lianluo.core.util.ToolsUtil;
import com.haolianluo.sms2.R;

public class HResVerifyActivity extends SkinActivity
{
	private static final String TAG = "HResVerifyActivity";
	//private LinearLayout mSkinLayout;
	LayoutInflater mInflater;
	private Context mContext;
	private String mPhone = "";
	private String mPassword = "";
	private String mCountry = "";
	private String mVerifyCode = "";
	Thread mThread;
	ProgressDialog mDialog;
	private String mType;
	private static final int MSG_SUCCESS = 0;
	private static final int MSG_FAIL = 1;
	private EditText mEditPw;
	private EditText mEditConPw;
	private EditText mEditCode1;
	private EditText mEditCode2;
	private EditText mEditCode3;
	private EditText mEditCode4;
	public static final String ACTION_VERIFY = "com.haolianluo.sms2.ACTION_VERIFY";
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext = this;
		mInflater = LayoutInflater.from(mContext);
		IntentFilter filter = new IntentFilter(ACTION_VERIFY);
		registerReceiver(mReceiver, filter);
		Bundle extra = getIntent().getExtras();
		if(extra != null)
		{
			mType = extra.getString("type");
			mPhone = extra.getString("phone");
			mPassword = extra.getString("password");
			mCountry = extra.getString("country");
			mVerifyCode = extra.getString("verify");
		};
		setContentView(R.layout.res_verify);
	}
	
	public void onStart()
	{
		super.onStart();
		initView();
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
	
	BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(ACTION_VERIFY))
			{
				Bundle extra = intent.getExtras();
				if(extra != null)
				{
					String code = extra.getString("yzm");
					if(code != null && code.length() == 4)
					{
						mEditCode1.setText(code.substring(0, 1));
						mEditCode2.setText(code.substring(1, 2));
						mEditCode3.setText(code.substring(2, 3));
						mEditCode4.setText(code.substring(3, 4));
					}
				}
			}
		}};
	
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
					if(mType.equals("forgetpw"))
					{
						Toast.makeText(mContext, getString(R.string.reset_success), Toast.LENGTH_LONG).show();
						SharedPreferences pref = getSharedPreferences(HConst.PREF_USER, Context.MODE_PRIVATE);
						pref.edit().putBoolean(HConst.USER_KEY_LOGIN, false).commit();
						pref.edit().putString(HConst.USER_KEY_PHONE, "").commit();
						finish();
					}
					else
					{
						Toast.makeText(mContext, getString(R.string.regist_success), Toast.LENGTH_LONG).show();
						finish();
					}
				}
				break;
				case MSG_FAIL:
				{
					Toast.makeText(mContext, getString(R.string.verify_fail), Toast.LENGTH_LONG).show();
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
		mSkinLayout = (SkinLinearLayout)SkinLinearLayout.inflate(mContext, R.layout.res_verify, null);
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
		tv_title.setText(R.string.verify);
		ImageView sc = (ImageView)title.findViewById(R.id.res_title_button);
		sc.setVisibility(View.GONE);
		mEditCode1 = (EditText)findViewById(R.id.edit_verifya);
		mEditCode2 = (EditText)findViewById(R.id.edit_verifyb);
		mEditCode3 = (EditText)findViewById(R.id.edit_verifyc);
		mEditCode4 = (EditText)findViewById(R.id.edit_verifyd);
		mEditCode1.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if(mEditCode1.getText().toString().trim().length() == 1)
				{
					mEditCode2.requestFocus();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
			}});
		mEditCode2.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if(mEditCode2.getText().toString().trim().length() == 1)
				{
					mEditCode3.requestFocus();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
			}});
		mEditCode3.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if(mEditCode3.getText().toString().trim().length() == 1)
				{
					mEditCode4.requestFocus();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
			}});
		if(mVerifyCode != null && mVerifyCode.length() == 4)
		{
			mEditCode1.setText(mVerifyCode.substring(0, 1));
			mEditCode2.setText(mVerifyCode.substring(1, 2));
			mEditCode3.setText(mVerifyCode.substring(2, 3));
			mEditCode4.setText(mVerifyCode.substring(3, 4));
		}
		TextView tv_mes = (TextView)findViewById(R.id.textview_verify_mes);
		if(mType.equals("forgetpw"))
		{
			tv_mes.setText(getString(R.string.verify_message));
			LinearLayout ll_pw = (LinearLayout)findViewById(R.id.linearlayout_pw);
			ll_pw.setVisibility(View.VISIBLE);
			mEditPw = (EditText) ll_pw.findViewById(R.id.edit_passws);
			mEditConPw = (EditText) ll_pw.findViewById(R.id.edit_confirm_pw);
		}
		Button bt_verify = (Button)findViewById(R.id.button_verify);
		bt_verify.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String ve_code1 = mEditCode1.getEditableText().toString();
				final String ve_code2 = mEditCode2.getEditableText().toString();
				final String ve_code3 = mEditCode3.getEditableText().toString();
				final String ve_code4 = mEditCode4.getEditableText().toString();
				if(ve_code1.trim().length() == 0 || ve_code2.trim().length() == 0 
						|| ve_code3.trim().length() == 0 || ve_code4.trim().length() == 0)
				{
					Toast.makeText(mContext, mContext.getString(R.string.verify_null), Toast.LENGTH_SHORT);
					return;
				}
				final String ve_code = ve_code1 + ve_code2 + ve_code3 + ve_code4;
				if(mType.equals("forgetpw"))
				{
					mPassword = mEditPw.getEditableText().toString();
					if(mPassword.trim().length() == 0)
					{
						Toast.makeText(mContext, mContext.getString(R.string.pw_null), Toast.LENGTH_SHORT).show();
						return;
					}
					if(!ToolsUtil.checkPassword(mPassword))
					{
						Toast.makeText(mContext, mContext.getString(R.string.pwr_message), Toast.LENGTH_SHORT).show();
						return;
					}
					
					if(!mPassword.equals(mEditConPw.getEditableText().toString()))
					{
						Toast.makeText(mContext, mContext.getString(R.string.pw_not_same), Toast.LENGTH_SHORT).show();
						return;
					}
				}
				{
					if(mDialog == null)
					{
						mDialog = new ProgressDialog(mContext);
						mDialog.setMessage(getString(R.string.waiting));
						mDialog.show();
					}
					// TODO Add the verify operation
					mThread = new Thread(){
						public void run()
						{
							try
							{
								HResponseModel sms;
								if(mType.equals("forgetpw"))
								{
									sms = new HUserForgetParser(mContext, mPhone, mPassword, ve_code).getUserForget();
								}
								else
								{
									sms= new HUserResSmsParser(mContext, mPhone, mCountry, mPassword, ve_code).getUserResSms();
								}
								if(sms.isResponse())
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