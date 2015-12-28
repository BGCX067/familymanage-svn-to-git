package com.jayce.calendar.adapter;

import com.jayce.calendar.*;
import com.jayce.calendar.utils.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class DayInfoAdapter extends BaseAdapter
{
	private static final String LOGNAME = DayInfoAdapter.class.getSimpleName();
	private Context mContext;
	private List<DayInfoItem> mList = new ArrayList<DayInfoItem>();
	private int mStart;
	private int mDayCount;
	private static final int TOTAL_DAY = 42;
	private int[] mSolarDate = new int[3];
	private int[] mLunarDate;
	
	private String[] mMonth;
	//private static final String[] mMonth = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
	private String[] mDay;
	//private static final String[] mDay = {};
	private String[] mLunarFesDay;
	//private static final String[] mDay = {};
	private String[] mLunarFesName;
	//private static final String[] mDay = {};
	private String[] mSolarFesDay;
	//private static final String[] mDay = {};
	private String[] mSolarFesName;
	//private static final String[] mDay = {};
	private String[] mTwoFourName;
	//private static final String[] mDay = {};
	private Map<String, String> mLunarFestival = new HashMap<String, String>();
	private Map<String, String> mSolarFestival = new HashMap<String, String>();
	
	private int mYearCurrent;
	private int mMonthCurrent;
	
	private int mHeight;
	
	public DayInfoAdapter(Context context)
	{
		mContext = context;
		mStart = 0;
		mDayCount = 0;
		mMonth = mContext.getResources().getStringArray(R.array.lunar_month_array);
		mDay = mContext.getResources().getStringArray(R.array.lunar_day_array);
		mLunarFesDay = mContext.getResources().getStringArray(R.array.lunar_festival_day);
		mLunarFesName = mContext.getResources().getStringArray(R.array.lunar_festival_name);
		mSolarFesDay = mContext.getResources().getStringArray(R.array.solar_festival_day);
		mSolarFesName = mContext.getResources().getStringArray(R.array.solar_festival_name);
		mTwoFourName = mContext.getResources().getStringArray(R.array.TwoFour_festival_name);
		SharedPreferences pref = mContext.getSharedPreferences("pirate", Context.MODE_PRIVATE);
		mHeight = pref.getInt("viewHeight", -1);
		Log.e("jayce", "mHeight:" + mHeight);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return TOTAL_DAY;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		if(arg0 >= mStart && arg0 <= mStart + mDayCount - 1)
		{
			return mList.get(arg0 - mStart);
		}
		else
		{
			return null;
		}
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View v;
		//if(null == arg1)
		//{
			v = LayoutInflater.from(mContext).inflate(R.layout.grid_day_item, null);
		//}
		//else
		//{
		//	v = arg1;
		//}
		
        //if(screenHeight > 0)
        //{
        	v.setLayoutParams(new GridView.LayoutParams(LayoutParams.FILL_PARENT, mHeight));
        //}
		String solar_text = "";
		String lunar_text = "";
		TextView solarDay = (TextView)v.findViewById(R.id.textview_solar);
		TextView lunarDay = (TextView)v.findViewById(R.id.textview_lunar);
		if(arg0 >= mStart && arg0 <= mStart + mDayCount - 1)
		{	
			solar_text = mList.get(arg0 - mStart).mSolarDay;
			lunar_text = mList.get(arg0 - mStart).mLunarDay;
			Calendar c = Calendar.getInstance();
			if(c.get(Calendar.YEAR) == mYearCurrent && 
			c.get(Calendar.MONTH) == mMonthCurrent && 
			c.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(solar_text))
			{
				Log.e("jayce", "today:" + c.get(Calendar.DAY_OF_MONTH));
				Log.e("jayce", "calendar day:" + solar_text);
				v.setBackgroundColor(Color.GREEN);
			}
			else
			{
				v.setBackgroundColor(Color.CYAN);
			}
		}
		else
		{
			v.setBackgroundColor(Color.RED);
		}
		solarDay.setText(solar_text);
		lunarDay.setText(lunar_text);
		return v;
	}	
	
	public void updateAdapter(Calendar calendar)
	{
		mYearCurrent = calendar.get(Calendar.YEAR);
		mMonthCurrent = calendar.get(Calendar.MONTH);
		mList.clear();
		calendar.set(Calendar.DATE, 1);
		mStart = calendar.get(Calendar.DAY_OF_WEEK);
		
		if(Calendar.SUNDAY == mStart)
		{
			mStart = 6;
		}
		else
		{
			mStart -= 2;
		}
		Log.e("jayce", "start" + Integer.toString(mStart));
		mDayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		Log.e("jayce", "count" + Integer.toString(mDayCount));
		
		mSolarDate[0] = mYearCurrent;
		mSolarDate[1] = mMonthCurrent + 1;
		
		for(int i = 0; i < mDayCount; i++)
		{
			mSolarDate[2] = i + 1;
			mLunarDate = SolarToLunar.getLunar(mSolarDate[0], mSolarDate[1], mSolarDate[2]);
			String lunar = getLunarString();
			DayInfoItem di = new DayInfoItem(Integer.toString(mSolarDate[2]), lunar);
			mList.add(di);
		}
		notifyDataSetChanged();
	}
	
	private String getLunarString()
	{
		String lunar = getLunarFestival();
		if(null != lunar)
		{
			return lunar;
		}
		lunar = getSolarFestival();
		if(null != lunar)
		{
			return lunar;
		}
		lunar = getTwoFour();
		if(null != lunar)
		{
			return lunar;
		}
		if(1 == mLunarDate[2])
	   	{
	   		if(1 == mLunarDate[3])
	   		{
	   			return mContext.getString(R.string.leap_month) + mMonth[mLunarDate[1] - 1];
	   		}
	   		else
	   		{
	   			return mMonth[mLunarDate[1] - 1];
	   		}
	   	}
	   	else
	   	{
	   		return mDay[mLunarDate[2] - 2];
	   	}
	}
	
	private String getSolarFestival()
	{
		String date = (mSolarDate[1] < 10 ? ("0" + Integer.toString(mSolarDate[1])) : Integer.toString(mSolarDate[1])) + ((mSolarDate[2] + 1) < 10 ? ("0" + Integer.toString(mSolarDate[2])) : Integer.toString(mSolarDate[2]));
		for(int i = 0; i < mSolarFesDay.length; i++)
		{
			if(date.equals(mSolarFesDay[i]))
			{
				return mSolarFesName[i];
			}
		}
		return null;
	}
	
	private String getLunarFestival()
	{
		String date = (mLunarDate[1] < 10 ? ("0" + Integer.toString(mLunarDate[1])) : Integer.toString(mLunarDate[1])) + (mLunarDate[2] < 10 ? ("0" + Integer.toString(mLunarDate[2])) : Integer.toString(mLunarDate[2]));
		for(int i = 0; i < mLunarFesDay.length; i++)
		{
			if(date.equals(mLunarFesDay[i]))
			{
				return mLunarFesName[i];
			}
		}
		return null;
	}
	
	private String getTwoFour()
	{
		int index = TwoFourFestival.getSoralTerm(mSolarDate[0], mSolarDate[1], mSolarDate[2]);
		if(-1 != index)
		{
			return mTwoFourName[index];
		}
		return null;
	}
}

class DayInfoItem
{
	String mSolarDay;
	String mLunarDay;
	public DayInfoItem(String solarDay, String lunarDay)
	{
		mSolarDay = solarDay;
		mLunarDay = lunarDay;
	}
}