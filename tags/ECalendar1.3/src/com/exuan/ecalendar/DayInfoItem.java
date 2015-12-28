package com.exuan.ecalendar;

public class DayInfoItem
{
	public String mSolarDay;
	public String mLunarDay;
	public boolean mIsFestival;
	public boolean mIsWeekEnd;
	public boolean mHasDiary;
	
	public DayInfoItem(String solarDay, String lunarDay, boolean festival)
	{
		mSolarDay = solarDay;
		mLunarDay = lunarDay;
		mIsFestival = festival;
		mIsWeekEnd = false;
		mHasDiary = false;
	}
}