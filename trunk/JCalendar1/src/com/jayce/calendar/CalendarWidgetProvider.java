package com.jayce.calendar;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class CalendarWidgetProvider extends AppWidgetProvider
{
	private static final String TIME_SET = "android.intent.action.TIME_SET";
	private static final String DATE_CHANGED = "android.intent.action.DATE_CHANGED";
	private static final String UPDATE = "android.appwidget.action.APPWIDGET_UPDATE";
	Timer timer = new Timer();
	RemoteViews mRemoteViews;
	AppWidgetManager mAppWidgetManager;
	ComponentName mThisWidget;
	long delay;
	public void onReceive(Context arg0, Intent arg1)
	{
		
		if(arg1.getAction().equals(TIME_SET) || arg1.getAction().equals(UPDATE) 
				|| arg1.getAction().equals(DATE_CHANGED))
		{
			mAppWidgetManager = AppWidgetManager.getInstance(arg0);
			mRemoteViews = new RemoteViews(arg0.getPackageName(), R.layout.widget);
			mThisWidget = new ComponentName(arg0, CalendarWidgetProvider.class);
			Intent intent = new Intent(arg0, JCalendarActivity.class);
	        PendingIntent pendingIntent = PendingIntent.getActivity(arg0, 0, intent, 0);
	        
	        //Get the layout for the App Widget and attach an on-click listener to the button
	        mRemoteViews.setOnClickPendingIntent(R.id.linearlayout_date, pendingIntent);
	        Calendar c = Calendar.getInstance();
			mRemoteViews.setTextViewText(R.id.textview_day_week,
					"week:" + c.get(Calendar.DAY_OF_WEEK));
			mRemoteViews.setTextViewText(R.id.textview_solar_time,
					 c.get(Calendar.YEAR) + " " + (c.get(Calendar.MONTH) + 1) + " " + c.get(Calendar.DAY_OF_MONTH));
			mAppWidgetManager.updateAppWidget(mThisWidget, mRemoteViews);
			//new SetDateTimer(arg0).run();
		}
	}
	/*
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.e("jayce", "onupdate");
		Timer timer = new Timer();
		   
		//timer.scheduleAtFixedRate(new SetDateTimer(context, appWidgetManager), 0, 1000);
		  
		final int N = appWidgetIds.length;

	    // Perform this loop procedure for each App Widget that belongs to this provider
	    for (int i=0; i<N; i++) {
	        int appWidgetId = appWidgetIds[i];

	        // Create an Intent to launch ExampleActivity
	        Intent intent = new Intent(context, JCalendarActivity.class);
	        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

	        // Get the layout for the App Widget and attach an on-click listener to the button
	        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
	        views.setOnClickPendingIntent(R.id.linearlayout_date, pendingIntent);

	        // Tell the AppWidgetManager to perform an update on the current App Widget
	        appWidgetManager.updateAppWidget(appWidgetId, views);
	    }
	    
	}
	*/
	public class SetDateTimer extends TimerTask
	{

		public SetDateTimer(Context context) {
			Log.e("jayce", "SetDateTimer");
			delay = 0;
			mAppWidgetManager = AppWidgetManager.getInstance(context);
			mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
			mThisWidget = new ComponentName(context, CalendarWidgetProvider.class);
			Intent intent = new Intent(context, JCalendarActivity.class);
	        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
	        
	        //Get the layout for the App Widget and attach an on-click listener to the button
	        mRemoteViews.setOnClickPendingIntent(R.id.linearlayout_date, pendingIntent);
		}
		
		@Override
		public void run() {
			Log.e("jayce", "run");
			Calendar c = Calendar.getInstance();
			mRemoteViews.setTextViewText(R.id.textview_day_week,
					"week:" + c.get(Calendar.DAY_OF_WEEK));
			mRemoteViews.setTextViewText(R.id.textview_solar_time,
					 c.get(Calendar.YEAR) + " " + (c.get(Calendar.MONTH) + 1) + " " + c.get(Calendar.DAY_OF_MONTH));
			mAppWidgetManager.updateAppWidget(mThisWidget, mRemoteViews);
		}
		
	}
}