package com.GetPosition;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class LoginService extends Service {
	
	private static final String LOGTAG = "LoginService";
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}
	// @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent == null){
			return Service.START_STICKY;
		}	
		String action = intent.getAction();
		if(action == null){
			return Service.START_STICKY;
		}
		return Service.START_STICKY;
	}
	
	class PostThread extends Thread
	{
		private String mInfo;
		
		public PostThread(String info)
		{
			mInfo = info;
		}
		public void run()
		{
			Log.d(LOGTAG, "post info in thread");
		}
	}
	
}
