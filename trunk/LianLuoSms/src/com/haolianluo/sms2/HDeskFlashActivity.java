package com.haolianluo.sms2;


import com.haolianluo.sms2.data.HConst;
import com.haolianluo.swfp.GLSurfaceView;
import com.haolianluo.swfp.HGLSufaceViewRenderer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

/***
 * 提醒动画
 * 2011年12月7日15:20:11
 * @author jianhua
 */
public class HDeskFlashActivity extends Activity {
	
	private ActivityManager mActivityManager = null;
	private boolean isRunDialog = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deskflash);
		HConst.markActivity = 6;
		init();
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 0:
				startDialog();
				break;
			}
		}
	};
	
	
	private void init(){
		mActivityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		GLSurfaceView glSurfaceView = (GLSurfaceView) findViewById(R.id.play);
		HGLSufaceViewRenderer gvr = new HGLSufaceViewRenderer(this);
		glSurfaceView.setRenderer(gvr);
		clock();
		glSurfaceView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isRun = false;
				startDialog();
			}
		});
	}
	
	@Override
	protected void onRestart() {
		HConst.markActivity = 6;
		super.onRestart();
	}
	
	//计时  3秒
	boolean isRun = true;
	private void clock(){
		new Thread(){
			int index = 0;
			public void run(){
				while(isRun){
					ComponentName cn = mActivityManager.getRunningTasks(1).get(0).topActivity;
					String strName = cn.getClassName();
					if(!"com.haolianluo.sms2.H".equals(strName.substring(0,21))){//按下HOME键
						isRun = false;
						handler.sendEmptyMessage(0);
						return;
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					index++;
					if(index == 6){
						isRun = false;
						handler.sendEmptyMessage(0);
					}
				}
			}
		}.start();
	}
	
	private void startDialog() {
		if(isRunDialog){
			return;
		}
		isRunDialog = true;
		finish();
		Intent intent = new Intent();
		intent.setClass(HDeskFlashActivity.this, HDialog.class);
		startActivity(intent);
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_SEARCH){
			handler.sendEmptyMessage(0);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
