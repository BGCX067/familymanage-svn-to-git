package com.haolianluo.sms2;



import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.model.HCloseInterface;
import com.haolianluo.sms2.model.HMyScrollLayout;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;


public class HGuideActivity extends HActivity implements HCloseInterface {
	
	private HMyScrollLayout mScrollLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.hguide);
		 mScrollLayout = (HMyScrollLayout) findViewById(R.id.ScrollLayout);
		 mScrollLayout.setInterface(this);
		 
		//更新后第一次启动的时候把强制更新标志位重置
		SharedPreferences spU = getSharedPreferences(HConst.UPDATE_FLAG, 0);
		Editor edit = spU.edit();
		edit.putBoolean(HConst.IS_UPDATE, false);
		edit.clear();
		edit.commit();
	}

	@Override
	public void close() {
		finish();
	}
	
}
