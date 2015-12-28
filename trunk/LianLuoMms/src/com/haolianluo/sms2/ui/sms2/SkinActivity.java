package com.haolianluo.sms2.ui.sms2;


import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.haolianluo.sms2.data.HConst;
import com.lianluo.core.skin.ISkinManage;
import com.lianluo.core.skin.SkinManage;

public abstract class SkinActivity extends HActivity
{
	public static boolean useNewChangeSkin = false;
	public static HashMap<String, Integer> skin_map = new HashMap<String, Integer>();
	private static final String TAG = "SkinActivity";
	private String mCurrentSkin = "";
	protected ViewGroup mView;
	protected int mContentView;
	
	protected boolean changeAdapter = false;
	
	public static ISkinManage mSkinManage = null;
//	private static final String SKIN_MANAGE_CLASS = "com.lianluo.core.skin.SkinManage";
	static
	{
		try {
//			mSkinManage = (ISkinManage)Class.forName(SKIN_MANAGE_CLASS).newInstance();
			
			mSkinManage = SkinManage.newInstance();
		} catch (Exception e) {
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
			//changeAdapter字段标识thread页面item缓存换肤，如果换肤后需要重新初始化adapter，否则有缓存项使用的还是以前的皮肤
			changeAdapter = true;
			
			if(mContentView != 0)
			{
				mCurrentSkin = mSkinManage.getSkin();
				if(useNewChangeSkin) {
					changeSkin();
				} else {
					if(mView != null)
					{
						mView.removeAllViews();
					}
					mView = (ViewGroup)LayoutInflater.from(this).inflate(mContentView, null);
					setContentView(mView);
				}
			}
		}
	}
	
	public void changeSkin() {};
	
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