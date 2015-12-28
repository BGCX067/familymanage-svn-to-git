package com.exuan.ecalendar;

import java.io.File;
import java.util.Calendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;

public class CalendarWidgetProvider extends AppWidgetProvider
{
	private static final String TIME_SET = "android.intent.action.TIME_SET";
	private static final String DATE_CHANGED = "com.exuan.ecalendar.DATE_CHANGED";
	private static final String TIMEZONE_CHANGED = "android.intent.action.TIMEZONE_CHANGED";
	private static final String UPDATE = "android.appwidget.action.APPWIDGET_UPDATE";
	private static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
	private static final String UPDATE_IMAGE = "com.exuan.ecalendar.UPDATE_IMAGE";
	public static final String UPDATE_HEAD = "com.exuan.ecalendar.UPDATE_HEAD";
	private static final String KEY_IMAGE_ID = "image_id";
	RemoteViews mRemoteViews;
	AppWidgetManager mAppWidgetManager;
	ComponentName mThisWidget;
	public static final int[] mDrawableArray = {R.drawable.luffy, R.drawable.ice, R.drawable.hebe, 
		R.drawable.zoro, R.drawable.rob, R.drawable.sanji};
	private String[] mMonth;
	private String[] mDay;
	private String[] mWeek;
	private int mImageID;
	public void onReceive(Context arg0, Intent arg1)
	{
		setUp(arg0);
		SharedPreferences pref = arg0.getSharedPreferences(ECalendarActivity.CALENDAR_PREFS, Context.MODE_PRIVATE);
		int pori = pref.getInt("orientation", Configuration.ORIENTATION_PORTRAIT);
		int ori = arg0.getResources().getConfiguration().orientation;
		mImageID = pref.getInt(KEY_IMAGE_ID, 0);
		if(pori != ori)
		{
			setText(arg0);
			setImage(arg0);
			setAlarm(arg0);
			pref.edit().putInt("orientation", ori);
		}
		if(arg1.getAction().equals(UPDATE)
				|| arg1.getAction().equals(BOOT_COMPLETED))
		{
			setText(arg0);
			setImage(arg0);
			setAlarm(arg0);
		}
		else if(arg1.getAction().equals(TIME_SET)
				|| arg1.getAction().equals(TIMEZONE_CHANGED)
				|| arg1.getAction().equals(DATE_CHANGED))
		{
			setText(arg0);
			setAlarm(arg0);
		}
		else if(arg1.getAction().equals(UPDATE_IMAGE))
		{
			int id = pref.getInt(KEY_IMAGE_ID, 0);
			mImageID = (mDrawableArray.length <= id) ? 0 : (id + 1);
			pref.edit().putInt(KEY_IMAGE_ID, mImageID).commit();
			setImage(arg0);
		}
		else if(arg1.getAction().equals(UPDATE_HEAD))
		{
			mImageID = mDrawableArray.length;
			pref.edit().putInt(KEY_IMAGE_ID, mImageID).commit();
			setImage(arg0);
		}
		mAppWidgetManager.updateAppWidget(mThisWidget, mRemoteViews);
	}
	
	private void setUp(Context context)
	{
		mAppWidgetManager = AppWidgetManager.getInstance(context);
		mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
		mThisWidget = new ComponentName(context, CalendarWidgetProvider.class);
		Intent intent = new Intent(context, ECalendarActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.linearlayout_date, pendingIntent);
        mWeek = context.getResources().getStringArray(R.array.week_array);
		mMonth = context.getResources().getStringArray(R.array.lunar_month_array);
		mDay = context.getResources().getStringArray(R.array.lunar_day_array);
		Intent updateImage = new Intent(context, CalendarWidgetProvider.class);
		updateImage.setAction(UPDATE_IMAGE);
		PendingIntent pending = PendingIntent.getBroadcast(
	            context, 0, updateImage, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.imageView_widget, pending);
	}
	
	private void setText(Context context)
	{
		Calendar c = Calendar.getInstance();
        int w = (c.get(Calendar.DAY_OF_WEEK) == 1) ? 6 : (c.get(Calendar.DAY_OF_WEEK) - 2);
		mRemoteViews.setTextViewText(R.id.textview_day_week,
				context.getString(R.string.week) + mWeek[w]);
		mRemoteViews.setTextViewText(R.id.textview_solar_time,
				 c.get(Calendar.YEAR) + context.getString(R.string.div) + (c.get(Calendar.MONTH) + 1) 
				 + context.getString(R.string.div) + c.get(Calendar.DAY_OF_MONTH));
		int[] lunar = SolarToLunar.getLunar(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
		String lu = mMonth[lunar[1] - 1] + mDay[lunar[2] - 1];
		mRemoteViews.setTextViewText(R.id.textview_lunar_time, lu);
	}
	
	private void setImage(Context context)
	{
		if(mDrawableArray.length <= mImageID)
		{
			File head = new File(context.getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "head.jpg");
			if(head.exists())
			{
				mRemoteViews.setImageViewBitmap(R.id.imageView_widget, BitmapFactory.decodeFile(head.getAbsolutePath()));
			}
			else
			{
				mRemoteViews.setImageViewResource(R.id.imageView_widget, mDrawableArray[0]);
				SharedPreferences pref = context.getSharedPreferences(ECalendarActivity.CALENDAR_PREFS, Context.MODE_PRIVATE);
				pref.edit().putInt(KEY_IMAGE_ID, 0).commit();
			}
		}
		else
		{
			mRemoteViews.setImageViewResource(R.id.imageView_widget, mDrawableArray[mImageID]);
		}
	}
	
	private void setAlarm(Context context)
	{
		Calendar cu = Calendar.getInstance();
		cu.set(Calendar.HOUR_OF_DAY, 0);
    	cu.set(Calendar.MINUTE, 0);
    	cu.set(Calendar.SECOND, 0);
    	cu.set(Calendar.MILLISECOND, 0);
    	cu.add(Calendar.DATE, 1);
		Intent intent = new Intent(context, CalendarWidgetProvider.class);
    	intent.setAction(DATE_CHANGED);
    	PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    	AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    	am.set(AlarmManager.RTC_WAKEUP, cu.getTimeInMillis(), pendingIntent);
	}
}