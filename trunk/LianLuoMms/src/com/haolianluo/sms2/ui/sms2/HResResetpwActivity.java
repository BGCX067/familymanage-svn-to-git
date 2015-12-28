package com.haolianluo.sms2.ui.sms2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.haolianluo.sms2.R;

public class HResResetpwActivity extends SkinActivity
{
	private static final String TAG = "HResResetpwActivity";
	//private LinearLayout mSkinLayout;
	LayoutInflater mInflater;
	private Context mContext;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext = this;
		mInflater = LayoutInflater.from(mContext);
		setContentView(R.layout.res_resetpw);
	}
	
	public void onStart()
	{
		super.onStart();
		initView();
	}
	
	private void initView()
	{/*
		if(mSkinLayout != null)
		{
			mSkinLayout.removeAllViews();
		}
		mSkinLayout = (SkinLinearLayout)SkinLinearLayout.inflate(mContext, R.layout.res_resetpw, null);
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
		tv_title.setText(R.string.reset_pw);
		ImageView sc = (ImageView)title.findViewById(R.id.res_title_button);
		sc.setVisibility(View.GONE);
		
		Button bt_ok = (Button)findViewById(R.id.button_ok);
		bt_ok.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// TODO Send the password to server to reset password
				Intent i = new Intent(mContext, HResLoginActivity.class);
				startActivity(i);
			}});
	}
}