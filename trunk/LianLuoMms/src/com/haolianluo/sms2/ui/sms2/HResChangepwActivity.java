package com.haolianluo.sms2.ui.sms2;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.model.HResponseModel;
import com.haolianluo.sms2.model.HUserUpdatePasswdParser;
import com.lianluo.core.util.ToolsUtil;
import com.haolianluo.sms2.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HResChangepwActivity extends SkinActivity
{
	private static final String TAG = "HResChangepwActivity";
	//private LinearLayout mSkinLayout;
	LayoutInflater mInflater;
	private Context mContext;
	Thread mThread;
	ProgressDialog mDialog;
	private static final int MSG_SUCCESS = 0;
	private static final int MSG_FAIL = 1;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext = this;
		mInflater = LayoutInflater.from(mContext);
		setContentView(R.layout.res_changepw);
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
					Toast.makeText(mContext, getString(R.string.change_success), Toast.LENGTH_LONG).show();
					SharedPreferences pref = getSharedPreferences(HConst.PREF_USER, Context.MODE_PRIVATE);
					pref.edit().putBoolean(HConst.USER_KEY_LOGIN, false).commit();
					pref.edit().putString(HConst.USER_KEY_PHONE, "").commit();
					setResult(Activity.RESULT_OK);
					finish();
				}
				break;
				case MSG_FAIL:
				{
					Toast.makeText(mContext, getString(R.string.opw_error), Toast.LENGTH_LONG).show();
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
		mSkinLayout = (SkinLinearLayout)SkinLinearLayout.inflate(mContext, R.layout.res_changepw, null);
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
		tv_title.setText(R.string.change_pw);
		ImageView sc = (ImageView)title.findViewById(R.id.res_title_button);
		sc.setVisibility(View.GONE);
		
		final EditText et_old = (EditText)findViewById(R.id.edit_oldpw);
		final EditText et_new = (EditText)findViewById(R.id.edit_newpw);
		final EditText et_newc = (EditText)findViewById(R.id.edit_repeatpw);
		
		Button bt_ok = (Button)findViewById(R.id.button_changepw);
		bt_ok.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// TODO Send the old and new password to server for verification
				final String pwo = et_old.getEditableText().toString();
				if(pwo.trim().length() == 0)
				{
					Toast.makeText(mContext, mContext.getString(R.string.pw_null), Toast.LENGTH_SHORT).show();
					return;
				}
				final String pwn = et_new.getEditableText().toString();
				if(pwn.trim().length() == 0)
				{
					Toast.makeText(mContext, mContext.getString(R.string.pw_null), Toast.LENGTH_SHORT).show();
					return;
				}
				if(!ToolsUtil.checkPassword(pwn))
				{
					Toast.makeText(mContext, mContext.getString(R.string.pwc_message), Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(!pwn.equals(et_newc.getEditableText().toString()))
				{
					Toast.makeText(mContext, mContext.getString(R.string.pw_not_same), Toast.LENGTH_SHORT).show();
					return;
				}
				if(mDialog == null)
				{
					mDialog = new ProgressDialog(mContext);
					mDialog.setMessage(getString(R.string.waiting));
					mDialog.show();
				}
				// TODO Add the verify operation
				SharedPreferences pref = mContext.getSharedPreferences(HConst.PREF_USER, Context.MODE_PRIVATE);
				final String phone = pref.getString(HConst.USER_KEY_PHONE, "");
				mThread = new Thread(){
					public void run()
					{
						try
						{
							HResponseModel update = new HUserUpdatePasswdParser(mContext, phone, pwo, pwn).getUserUpdatePasswd();
							if(update.isResponse())
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
			}});
	}
}