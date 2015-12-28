package com.haolianluo.sms2.ui.sms2;

import java.util.Iterator;
import java.util.Set;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.data.HSharedPreferences;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;
import com.haolianluo.sms2.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class HStartLianluosms extends HActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences spU = getSharedPreferences(HConst.UPDATE_FLAG, 0);
		spU.edit().remove(HConst.ISSHOW_NOTIFA).commit();
		
		HLog.d("firstStart", "....................");
		Set<String> s = getIntent().getCategories();
		boolean b = false;
		if(s != null){
			Iterator<String> i = s.iterator();
			while(i.hasNext()){
				if(i.next().equals(Intent.CATEGORY_LAUNCHER)){
					b = true;
				}
			}
			}
			if(!b){
		        finish();
				Intent intent = new Intent();
		        intent.setAction(Intent.ACTION_MAIN);
		        intent.addCategory(Intent.CATEGORY_LAUNCHER);
		        intent.setClassName("com.haolianluo.sms2", "com.haolianluo.sms2.ui.sms2.HStartLianluosms");
		        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        startActivityIfNeeded(intent, 0);
		        return;
			}
			setContentView(R.layout.loading);
			
			
			startProgressBar();
		
	}
	
	private void startProgressBar(){
		new Thread(){
			final HSharedPreferences sp = new HSharedPreferences(HStartLianluosms.this);
			final String version = ToolsUtil.getVersion(HStartLianluosms.this);
			public void run(){
				try {
					if(sp.getIsFirst() || !version.equals(sp.getPreVersion())){
						int height = HStartLianluosms.this.getWindowManager().getDefaultDisplay().getHeight();
						int width = HStartLianluosms.this.getWindowManager().getDefaultDisplay().getWidth();
						HConst.width = width;
						HConst.height = height;
						if (height < 400 || width < 400) {
							sp.setHighDen(false);
						} else {
							sp.setHighDen(true);
						}
					}
					Thread.sleep(1800);
					handler.sendEmptyMessage(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 0:
				startActivity(new Intent().setClass(HStartLianluosms.this, HLeadActivity.class));
				finish();
				break;
			}
		};
	};
}
