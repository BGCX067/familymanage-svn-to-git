package com.jayce.aidltest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class AIDLtestService extends Service
{

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return new TestService();
	}
	
	public class TestService extends ITestService.Stub
	{

		@Override
		public String getStringValue() throws RemoteException {
			// TODO Auto-generated method stub
			return "Hello world";
		}
	}
	
}