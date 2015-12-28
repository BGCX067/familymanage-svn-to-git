package com.haolianluo.sms2;


import com.haolianluo.sms2.data.HConst;
import com.lianluo.core.skin.ISkinManage;
import com.lianluo.core.util.HLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class SkinActivity extends HActivity
{
	private static final String TAG = "SkinActivity";
	private String mCurrentSkin = "";
	protected ViewGroup mView;
	protected int mContentView;
	
	public static ISkinManage mSkinManage = null;
	private static final String SKIN_MANAGE_CLASS = "com.lianluo.core.skin.SkinManage";
	static
	{
		try {
			mSkinManage = (ISkinManage)Class.forName(SKIN_MANAGE_CLASS).newInstance();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		registerReceiver(killOneself, new IntentFilter(HConst.ACTION_KILL_ONESELF));
		mCurrentSkin = mSkinManage.getSkin();
	}
	
	protected void onStart()
	{
		super.onStart();
		if(!mCurrentSkin.equals(mSkinManage.getSkin()))
		{
			HLog.e(TAG, "change skin");
			if(mContentView != 0)
			{
				mCurrentSkin = mSkinManage.getSkin();
				if(mView != null)
				{
					mView.removeAllViews();
				}
				mView = (ViewGroup)LayoutInflater.from(this).inflate(mContentView, null);
				setContentView(mView);
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(killOneself);
		super.onDestroy();
	}
	
	public void setContentView(int id)
	{
		mContentView  = id;
		mView = (ViewGroup) LayoutInflater.from(this).inflate(mContentView, null);
		super.setContentView(id);
	}
	
	BroadcastReceiver killOneself = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};
	
}