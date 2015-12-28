package com.haolianluo.sms2;

import com.haolianluo.sms2.model.HResponseModel;
import com.haolianluo.sms2.model.HStatistics;
import com.haolianluo.sms2.model.HUserCheckParser;
import com.haolianluo.sms2.model.HUserResCodeParser;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class HResRegistActivity extends SkinActivity
{
	private static final String TAG = "HResRegistActivity";
	//private LinearLayout mSkinLayout;
	LayoutInflater mInflater;
	private Context mContext;
	Thread mThread;
	ProgressDialog mDialog;
	private static final int MSG_USER_EXIST = 0;
	private static final int MSG_REQUEST_VERIFY = 1;
	private static final int MSG_FAIL = 2;
	private String mPhone = "";
	private String mPassword = "";
	private String mCountry = "";
	private String mMsg;
	int mNumber = 11;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext = this;
		mInflater = LayoutInflater.from(mContext);
		setContentView(R.layout.res_register);
	}
	
	public void onStart()
	{
		HLog.e(TAG, "onStart");
		super.onStart();
		initView();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		HStatistics mHStatistics = new HStatistics(HResRegistActivity.this);
		if(ToolsUtil.IM_FLAG) {
			mHStatistics.add(HStatistics.Z9_3, "", "", "");
		}
		return super.onKeyDown(keyCode, event);
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
				case MSG_USER_EXIST:
				{
					Toast.makeText(mContext, getString(R.string.registed), Toast.LENGTH_LONG).show();
				}
				break;
				case MSG_REQUEST_VERIFY:
				{
					Intent i = new Intent(mContext, HResVerifyActivity.class);
					i.putExtra("phone", mPhone);
					i.putExtra("password", mPassword);
					i.putExtra("country", mCountry);
					i.putExtra("type", "regist");
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
		mSkinLayout = (SkinLinearLayout)SkinLinearLayout.inflate(mContext, R.layout.res_register, null);
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
		tv_title.setText(R.string.register);
		ImageView sc = (ImageView)title.findViewById(R.id.res_title_button);
		sc.setVisibility(View.GONE);
		
		Spinner spin_cou = (Spinner)findViewById(R.id.spinner_country);
		spin_cou.setBackgroundDrawable(getResources().getDrawable(R.drawable.spin_drop));
		ArrayAdapter<CharSequence> adapter_cou = ArrayAdapter.createFromResource(mContext, R.array.countrys_array, android.R.layout.simple_spinner_item);
		adapter_cou.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_cou.setAdapter(adapter_cou);
		
		final EditText et_phone = (EditText)findViewById(R.id.edit_phone_number);
		final EditText et_pw = (EditText)findViewById(R.id.edit_passws);
		final EditText et_pwc = (EditText)findViewById(R.id.edit_confirm_pw);
		spin_cou.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				TextView ct = (TextView)arg1;
				mCountry = (String) ct.getText();
				switch(arg2)
				{
					case 0:
					{
						et_phone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
						mNumber = 11;
					}
					break;
					case 1:
					{
						et_phone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
						mNumber = 10;
					}
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}});
		Button bt_reg = (Button)findViewById(R.id.button_register);
		bt_reg.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				HLog.e(TAG, "CLICK");
				// TODO Auto-generated method stub
				HLog.e(TAG, "phone:" + et_phone.getEditableText().toString());
				HLog.e(TAG, "password:" + et_pw.getEditableText().toString());
				mPhone = et_phone.getEditableText().toString();
				if(mPhone.trim().length() == 0)
				{
					Toast.makeText(mContext, mContext.getString(R.string.phone_null), Toast.LENGTH_SHORT).show();
					return;
				}
				if(mPhone.trim().length() != mNumber)
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
				mPassword = et_pw.getEditableText().toString();
				if(mPassword.trim().length() == 0)
				{
					Toast.makeText(mContext, mContext.getString(R.string.pw_null), Toast.LENGTH_SHORT).show();
					return;
				}
				if(!ToolsUtil.checkPassword(mPassword))
				{
					Toast.makeText(mContext, mContext.getString(R.string.pw_message), Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(!mPassword.equals(et_pwc.getEditableText().toString()))
				{
					Toast.makeText(mContext, mContext.getString(R.string.pw_not_same), Toast.LENGTH_SHORT).show();
					return;
				}
				//mCountry = spin_cou.get
				// TODO Add the register operation
				if(mDialog == null)
				{
					mDialog = new ProgressDialog(mContext);
					mDialog.setMessage(getString(R.string.waiting));
					mDialog.show();
				}
				mThread = new Thread(){
					public void run()
					{
						HLog.e(TAG, "RUN");
						try
						{
							HResponseModel check = new HUserCheckParser(mContext, mPhone).getUserCheck();
							if(check.isResponse())
							{
								Message msg = new Message();
								msg.what = MSG_USER_EXIST;
								mHandler.sendMessage(msg);
							}
							else
							{
								HResponseModel code = new HUserResCodeParser(mContext, mPhone).getUserResCode();
								HLog.e(TAG, "re:" + code.isResponse() + ",msg:" + code.getMessage());
								Message msg = new Message();
								if(code.isResponse())
								{
									mMsg = code.getMessage();
									msg.what = MSG_REQUEST_VERIFY;
								}
								else
								{
									if(code.getMessage().contains("3"))
									{
										mMsg = getString(R.string.verify_limit);
									}
									else
									{
										mMsg = getString(R.string.verify_over);
									}
									msg.what = MSG_FAIL;
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