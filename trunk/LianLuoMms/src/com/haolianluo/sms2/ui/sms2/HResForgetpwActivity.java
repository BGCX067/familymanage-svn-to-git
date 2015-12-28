package com.haolianluo.sms2.ui.sms2;

import com.haolianluo.sms2.model.HResponseModel;
import com.haolianluo.sms2.model.HUserSendCodeParser;
import com.lianluo.core.util.ToolsUtil;
import com.haolianluo.sms2.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HResForgetpwActivity extends SkinActivity
{
	private static final String TAG = "HResForgetpwActivity";
	//private LinearLayout mSkinLayout;
	LayoutInflater mInflater;
	private Context mContext;
	private String mPhone = "";
	Thread mThread;
	ProgressDialog mDialog;
	private static final int MSG_SUCCESS = 0;
	private static final int MSG_FAIL = 1;
	private String mMsg;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext = this;
		mInflater = LayoutInflater.from(mContext);
		setContentView(R.layout.res_forgetpw);
	}
	
	public void onStart()
	{
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
					Intent i = new Intent(mContext, HResVerifyActivity.class);
					i.putExtra("phone", mPhone);
					i.putExtra("type", "forgetpw");
					i.putExtra("verify", mMsg);
					finish();
					startActivity(i);
				}
				break;
				case MSG_FAIL:
				{
					Toast.makeText(mContext, mMsg, Toast.LENGTH_LONG).show();
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
		mSkinLayout = (SkinLinearLayout)SkinLinearLayout.inflate(mContext, R.layout.res_forgetpw, null);
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
		tv_title.setText(R.string.find_pw);
		ImageView sc = (ImageView)title.findViewById(R.id.res_title_button);
		sc.setVisibility(View.GONE);
		final EditText et_phone = (EditText)findViewById(R.id.edit_phone_number);
		Button bt_ok = (Button)findViewById(R.id.button_fpw);
		bt_ok.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// TODO Send the phone number to server and return a verify code
				mPhone = et_phone.getEditableText().toString();
				if(mPhone.trim().length() == 0)
				{
					Toast.makeText(mContext, mContext.getString(R.string.phone_null), Toast.LENGTH_SHORT).show();
					return;
				}
				if(mPhone.trim().length() < 10)
				{
					Toast.makeText(mContext, mContext.getString(R.string.input_right_phone), Toast.LENGTH_SHORT).show();
					return;
				}
				
				String ph = mPhone.substring(0, 2);
				if(ph.equals("86"))
				{
					mPhone = mPhone.substring(2);
				}
				ph = mPhone.substring(0, 3);
				if(ph.equals("+86"))
				{
					mPhone = mPhone.substring(3);
				}
				if(!ToolsUtil.checkPhoneNumber(mPhone))
				{
					Toast.makeText(mContext, mContext.getString(R.string.input_right_phone), Toast.LENGTH_SHORT).show();
					return;
				}
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
							HResponseModel sms = new HUserSendCodeParser(mContext, mPhone).getSendCode();
							if(sms.isResponse())
							{
								mMsg = sms.getMessage();
								Message msg = new Message();
								msg.what = MSG_SUCCESS;
								mHandler.sendMessage(msg);
							}
							else
							{
								Message msg = new Message();
								msg.what = MSG_FAIL;
								if(sms.getMessage().contains("未注册") || sms.getMessage().contains("not"))
								{
									mMsg = getString(R.string.not_register);
								}
								else if(sms.getMessage().contains("3"))
								{
									mMsg = getString(R.string.verify_limit);
								}
								else
								{
									mMsg = getString(R.string.verify_over);
								}
								mHandler.sendMessage(msg);
							}
						}catch(Exception e)
						{
							Message msg = new Message();
							msg.what = MSG_FAIL;
							mMsg = getString(R.string.connect_fail);
							mHandler.sendMessage(msg);
						}
					}
				};
				mThread.start();
			}});
	}
}