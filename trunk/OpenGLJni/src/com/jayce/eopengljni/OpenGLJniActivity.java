package com.jayce.eopengljni;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class OpenGLJniActivity extends Activity
{
	private GLSurfaceView mGLSurfaceView;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mGLSurfaceView = new GLSurfaceView(this);
		final ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configInfo = activityManager.getDeviceConfigurationInfo();
		if(configInfo.reqGlEsVersion >= 0x20000)
		{
			mGLSurfaceView.setEGLContextClientVersion(2);
			OpenGLJniRenderer renderer = new OpenGLJniRenderer();
			mGLSurfaceView.setRenderer(renderer);
		}
		
		setContentView(mGLSurfaceView);
	}
	
	@Override
	protected void onResume() 
	{
		// The activity must call the GL surface view's onResume() on activity onResume().
		super.onResume();
		mGLSurfaceView.onResume();
	}

	@Override
	protected void onPause() 
	{
		// The activity must call the GL surface view's onPause() on activity onPause().
		super.onPause();
		mGLSurfaceView.onPause();
	}	
}