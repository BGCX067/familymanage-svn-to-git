package com.haolianluo.sms2;


import com.haolianluo.sms2.data.HSharedPreferences;
import com.haolianluo.sms2.model.HFirstInstallParser;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class HLeadActivity extends SkinActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String str = ToolsUtil.getChannel(HLeadActivity.this).trim();
		if(str.equals("qq")){
			setContentView(R.layout.lead);
			selectActivityRun();
		}else{
			seletcActivity();
		}
	}
	
	
	private void selectActivityRun(){
		new Thread(){
			public void run() {
				try {
					Thread.sleep(2000);
					seletcActivity();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
	
	private void seletcActivity(){
		final Context con = this;
		HSharedPreferences sp = new HSharedPreferences(HLeadActivity.this);
		String version = ToolsUtil.getVersion(HLeadActivity.this);
		if(sp.getIsFirst() || !version.equals(sp.getPreVersion())){
			new Thread(){
				public void run()
				{
					new HFirstInstallParser(con).getFirstInstall();
					HLog.e("HLeadActivity", "send first install");
				}
			}.start();
			sp.setFirst(false);
			sp.setPreVersion(version);
			Intent intent = new Intent();
			intent.setClass(HLeadActivity.this, HGuideActivity.class);
			startActivity(intent);
			finish();
		}else{
			Intent intent = new Intent();
			intent.setClass(HLeadActivity.this, HThreadActivity.class);
			startActivity(intent);
			finish();
		}

	}
	
	
	
//	private void startSelectActivity(){
//		new Thread(){
//			final HSharedPreferences sp = new HSharedPreferences(HLeadActivity.this);
//			final String str = ToolsUtil.getChannel(HLeadActivity.this).trim();
//			final String version = ToolsUtil.getVersion(HLeadActivity.this);
//			public void run(){
//				if (sp.getIsFirst() || !version.equals(sp.getPreVersion())) {
//					sp.setPreVersion(version);
//					if(str.equals("qq")){
//						try {
//							Thread.sleep(1000);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//						Intent intent = new Intent();
//						intent.setClass(HLeadActivity.this, HGuideActivity.class);
//						startActivity(intent);
//						finish();
//					}else{
//						Intent intent = new Intent();
//						intent.setClass(HLeadActivity.this, HGuideActivity.class);
//						startActivity(intent);
//						finish();
//					}
//					
//					sp.setFirst(false);
//				}else{
//					if(str.equals("qq")){
//						try {
//							Thread.sleep(1000);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//						Intent intent = new Intent();
//						intent.setClass(HLeadActivity.this, HThreadActivity.class);
//						startActivity(intent);
//						finish();
//					}else{
//						Intent intent = new Intent();
//						intent.setClass(HLeadActivity.this, HThreadActivity.class);
//						startActivity(intent);
//						finish();
//					}
//				}
//				
//			}
//		}.start();
//	}
	
}
