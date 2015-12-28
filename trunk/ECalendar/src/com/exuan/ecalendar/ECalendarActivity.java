package com.exuan.ecalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.DatePicker.OnDateChangedListener;

public class ECalendarActivity extends Activity 
implements View.OnTouchListener{
    /** Called when the activity is first created. */
	public static final String CALENDAR_PREFS = "pirate_calendar";
	LinearLayout mLinearLayoutMain;
	LinearLayout mLinearLayoutWeek;
	RelativeLayout mRelativeLayoutTitle;
	private TextView mYearText;
	private TextView mMonthText;
	private ImageView mImageViewJump;
	private ImageView mImageViewToday;
	private ImageView mImageViewMeili;
	TextView[] mTextViewWeek = new TextView[7];
	private int mHeight;
	private int mWidth;
	private boolean mIsQVGA;
	private GestureDetector mGestureDetector;
	private DatePicker mDatePicker;
	private Context mContext;
	private RelativeLayout mRelativeLayoutCalendar;
	
	private List<DayInfoItem> mList = new ArrayList<DayInfoItem>();
	private int mStart;
	private int mDayCount;
	private static final int TOTAL_DAY = 42;
	private int[] mSolarDate = new int[3];
	private int[] mLunarDate;
	
	private String[] mMonth;
	private String[] mDay;
	private String[] mLunarFesDay;
	private String[] mLunarFesName;
	private String[] mSolarFesDay;
	private String[] mSolarFesName;
	private String[] mTwoFourName;
	private int mYearCurrent;
	private int mMonthCurrent;
	
	private int mCurrentYear;
	private int mCurrentMonth;
	private int mCurrentDay;
	private Calendar mCalendar;
	private String mClickDay = null;
	private static final int[] mTitleHeight = {80, 50};
	
	private static final int DATE_PICKER_DIALOG_ID = 0;
	
	private static final int MENU_ALL_DIARY = 0;
	private static final int MENU_RECOMMEND_CALENDAR = 1;
	private static final int MENU_ABOUT_CALENDAR = 2;
	private static final int MENU_SETTING_CALENDAR = 3;
	
	private static final int REQUEST_CODE_RECOMMEND = 0;
	private static final int REQUEST_SETTING = 1;
	private static final int REQUEST_DIARY = 2;
	public static int mHolidayColor = 0xfffb466c;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = this;
        SharedPreferences pref = mContext.getSharedPreferences(CALENDAR_PREFS, Context.MODE_PRIVATE);
        int index = pref.getInt(ESettingActivity.KEY_COLOR, 0);
        mHolidayColor = ESettingActivity.HOLIDAY_COLOR[index];
        mStart = 0;
		mDayCount = 0;
		mMonth = mContext.getResources().getStringArray(R.array.lunar_month_array);
		mDay = mContext.getResources().getStringArray(R.array.lunar_day_array);
		mLunarFesDay = mContext.getResources().getStringArray(R.array.lunar_festival_day);
		mLunarFesName = mContext.getResources().getStringArray(R.array.lunar_festival_name);
		mSolarFesDay = mContext.getResources().getStringArray(R.array.solar_festival_day);
		mSolarFesName = mContext.getResources().getStringArray(R.array.solar_festival_name);
		mTwoFourName = mContext.getResources().getStringArray(R.array.TwoFour_festival_name);
		
        mHeight = getWindowManager().getDefaultDisplay().getHeight();
        mWidth = getWindowManager().getDefaultDisplay().getWidth();
        mIsQVGA = (mHeight < 300 || mWidth < 300);
        
        mWidth = (mWidth - 10)/7;
        if(mIsQVGA)
        {
        	mHeight = (mHeight - mTitleHeight[1])/8;
        }
        else
        {
        	mHeight = (mHeight - mTitleHeight[0])/8;
        }
        mCalendar = Calendar.getInstance();
        mCurrentYear = mCalendar.get(Calendar.YEAR);
        mCurrentMonth = mCalendar.get(Calendar.MONTH);
    	mCurrentDay = mCalendar.get(Calendar.DATE);
        
        mLinearLayoutMain = (LinearLayout)findViewById(R.id.linearlayout_main);
        mLinearLayoutWeek = (LinearLayout)findViewById(R.id.LinearLayout_week);
        mRelativeLayoutTitle = (RelativeLayout)findViewById(R.id.relativelayout_title);
        mRelativeLayoutTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, mHeight*2/3));
        String[] week = getResources().getStringArray(R.array.week_array);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mWidth, mHeight);
        mYearText = (TextView)findViewById(R.id.textview_year);
        mMonthText = (TextView)findViewById(R.id.textview_month);
        if(!mIsQVGA)
        {
	        mYearText.setTextSize(mHeight/3);
	        mMonthText.setTextSize(mHeight/3);
        }
        mMonthText.setLayoutParams(new LinearLayout.LayoutParams(mHeight*2/3, LinearLayout.LayoutParams.FILL_PARENT));
        mYearText.setTextColor(ECalendarActivity.mHolidayColor);
        mMonthText.setTextColor(ECalendarActivity.mHolidayColor);
        
        mImageViewMeili = (ImageView)findViewById(R.id.imageview_meili);
        if(mIsQVGA)
        {
        	RelativeLayout.LayoutParams mlp = new RelativeLayout.LayoutParams(3*mHeight/2, 3*mHeight/2);
            mlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            mlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        	mImageViewMeili.setLayoutParams(mlp);
        }
        RelativeLayout.LayoutParams llp = new RelativeLayout.LayoutParams(mHeight*2/3, mHeight*2/3);
        llp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(mHeight*2/3, mHeight*2/3);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mImageViewJump = (ImageView)findViewById(R.id.imageview_jump_date);
        mImageViewJump.setLayoutParams(llp);
    	mImageViewToday = (ImageView)findViewById(R.id.imageview_today);
    	mImageViewToday.setLayoutParams(rlp);
    	mImageViewJump.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DATE_PICKER_DIALOG_ID);
			}});
    	
    	mImageViewToday.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCalendar.setTime(Calendar.getInstance().getTime());
				mCurrentYear = mCalendar.get(Calendar.YEAR);
		        mCurrentMonth = mCalendar.get(Calendar.MONTH);
		    	mCurrentDay = mCalendar.get(Calendar.DATE);
		    	initCalendarList(mCalendar);
        		updateCalendar();
			}});
    	
        for(int i = 0; i < 7; i++)
        {
        	mTextViewWeek[i] = new TextView(mContext);
        	mTextViewWeek[i].setText(week[i]);
        	if(i == 5 || i == 6)
        	{
        		mTextViewWeek[i].setTextColor(mHolidayColor);
        	}
        	else
        	{
        		mTextViewWeek[i].setTextColor(Color.BLACK);
        	}
        	mTextViewWeek[i].setGravity(Gravity.CENTER);
        	mLinearLayoutWeek.addView(mTextViewWeek[i], lp);
        }
        mRelativeLayoutCalendar = new RelativeLayout(mContext);
        mGestureDetector = new GestureDetector(new MyGestureListener());
        mRelativeLayoutCalendar.setLongClickable(true);
        mRelativeLayoutCalendar.setOnTouchListener(this);
        new Thread(){
        	public void run()
        	{
        		initCalendarView();
        		initCalendarList(mCalendar);
        		mHandler.sendEmptyMessage(0);
        	}
        }.start();
    }
    
    private Handler mHandler = new Handler()
    {
    	public void handleMessage(Message msg)
    	{
    		updateCalendar();
    		mLinearLayoutMain.addView(mRelativeLayoutCalendar);
    	}
    };
    
    public void onResume()
    {
    	super.onResume();
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, MENU_ALL_DIARY, 0, R.string.diarys).setIcon(
				R.drawable.menu_diarys);
		menu.add(0, MENU_RECOMMEND_CALENDAR, 0, R.string.share).setIcon(
				R.drawable.menu_share);
		menu.add(0, MENU_ABOUT_CALENDAR, 0, R.string.about).setIcon(
				R.drawable.menu_about);
		menu.add(0, MENU_SETTING_CALENDAR, 0, R.string.setting).setIcon(
				R.drawable.menu_set);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case MENU_ALL_DIARY:
			{
				Intent intent = new Intent(mContext, EDiaryListActivity.class);
				startActivityForResult(intent, REQUEST_DIARY);
				return true;
			}
			case MENU_RECOMMEND_CALENDAR:
			{
				Intent intent = new Intent("android.intent.action.SEND");
				intent.setType("text/*");
				intent.putExtra("android.intent.extra.SUBJECT", getString(R.string.app_name));
				intent.putExtra("android.intent.extra.TEXT", getString(R.string.share_content));
				Intent i = Intent.createChooser(intent, getString(R.string.share_way));
				startActivityForResult(i, REQUEST_CODE_RECOMMEND);
				return true;
			}
			case MENU_ABOUT_CALENDAR:
			{
				Intent i = new Intent(mContext, EAboutActivity.class);
				startActivity(i);
				return true;
			}
			case MENU_SETTING_CALENDAR:
			{
				Intent i = new Intent(mContext, ESettingActivity.class);
				startActivityForResult(i, REQUEST_SETTING);
				return true;
			}
		}
		
		return false;
	}
    
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		try
		{
			if(REQUEST_SETTING == requestCode)
			{
				mYearText.setTextColor(ECalendarActivity.mHolidayColor);
		        mMonthText.setTextColor(ECalendarActivity.mHolidayColor);
				mTextViewWeek[5].setTextColor(mHolidayColor);
				mTextViewWeek[6].setTextColor(mHolidayColor);
				updateCalendar();
			}
			else if(REQUEST_DIARY == requestCode)
			{
				initCalendarList(mCalendar);
				updateCalendar();
			}
		}
		catch(Exception e){
			
		}
	}
	
    private OnDateChangedListener mDateSetListener = new OnDateChangedListener(){

		@Override
		public void onDateChanged(DatePicker arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			mCurrentYear = arg0.getYear();
			mCurrentMonth = arg0.getMonth();
			mCurrentDay = arg0.getDayOfMonth();
		}};
       
    protected Dialog onCreateDialog(int id)
    {
    	switch(id)
    	{
	    	case DATE_PICKER_DIALOG_ID:
	    	{
	    		LayoutInflater factory = LayoutInflater.from(mContext);
				View layout = factory.inflate(R.layout.date_picker, null);
	    		mDatePicker = (DatePicker)layout.findViewById(R.id.date_picker);
	    		mDatePicker.init(mCurrentYear, mCurrentMonth, mCurrentDay, mDateSetListener);
	    		return new AlertDialog.Builder(mContext)
	    		.setTitle(getString(R.string.choose_time))
	    		.setView(mDatePicker)
	    		.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}})
				.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						mCalendar.set(mCurrentYear, mCurrentMonth, mCurrentDay);
						initCalendarList(mCalendar);
		        		updateCalendar();
					}})	
	    		.create();
	    	}
    	}
		return super.onCreateDialog(id);
    }
    
    public void onConfigurationChanged(Configuration newConfig) 
    {
    	try 
    	{
    		super.onConfigurationChanged(newConfig);
    	}
    	catch (Exception ex)
    	{

    	}
    }
    
    public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
    {
    	return mGestureDetector.onTouchEvent(paramMotionEvent);
    }
    
    public class MyGestureListener extends SimpleOnGestureListener {
    	public boolean onSingleTapConfirmed(MotionEvent e)
    	{
    		if(null != mClickDay)
    		{
    			Intent intent = new Intent(mContext, EDiaryDetailActivity.class);
				intent.putExtra(DatabaseHelper.DATE, mClickDay);
				startActivityForResult(intent, REQUEST_DIARY);
    		}
    		return false;
    	}
    	
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        	int distanceX = (int) e1.getX() - (int) e2.getX();
            int distanceY = (int) e1.getY() - (int) e2.getY();
        	//left and right, change year
        	if(Math.abs(distanceX) > Math.abs(distanceY))
        	{
        		
        		if(distanceX < 0)//to right
        		{
        			reduceYear();
        		}
        		else//to left
        		{
        			addYear();
        		}
        	}
        	else//up and down, change month
        	{
        		if(distanceY < 0)//to down
        		{
        			reduceMonth();
        		}
        		else//to up
        		{
        			addMonth();
        		}
        	}
            super.onFling(e1, e2, velocityX, velocityY);
        	return true;
        }
    }

	public void updateCalendar()
	{
		int month = mCurrentMonth + 1;
		String mon = month < 10 ? "0" + month : "" + month;
		String date = mCurrentYear + mon;
		Cursor cursor = null;
        for(int i = 0; i < TOTAL_DAY; i++)
        {
        	DayTextView tv = (DayTextView)mRelativeLayoutCalendar.getChildAt(i);
            DayInfoItem day = new DayInfoItem("", "", false);
    		boolean today = false;
    		if(i >= mStart && i <= mStart + mDayCount - 1)
    		{	
    			day = mList.get(i - mStart);
    			Calendar c = Calendar.getInstance();
    			if(c.get(Calendar.YEAR) == mYearCurrent && 
    			c.get(Calendar.MONTH) == mMonthCurrent && 
    			c.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(day.mSolarDay))
    			{
    				today = true;
    			}
    			if((i + 1)% 7 == 0 || (i + 2)% 7 == 0)
    			{
    				day.mIsWeekEnd = true;
    			}
    			String d = 1 == day.mSolarDay.length() ? "0" + day.mSolarDay : day.mSolarDay;
    			String dt = date + d;
    			cursor = getContentResolver().query(CalendarProvider.CONTENT_URI_DIARYS, null, DatabaseHelper.DATE + " = '" + dt + "'", null, null);
    			if(cursor.getCount() > 0)
    			{
    				day.mHasDiary = true;
    			}
    			cursor.close();
    		}
    		tv.setDate(day, today);
    		if(day.mSolarDay.length() > 0)
    		{
    			tv.setOnTouchListener(new DayClickListener(date + (1 == day.mSolarDay.length() ? ("0" + day.mSolarDay) : day.mSolarDay)));
    		}
    		else
    		{
    			tv.setClickable(false);
    		}
        }
	    mYearText.setText(Integer.toString(mCurrentYear));
	    mMonthText.setText(Integer.toString(mCurrentMonth + 1));
	}
	
	public class DayClickListener implements OnTouchListener
	{
		private String mDay;
		public DayClickListener(String day)
		{
			mDay = day;
		}
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			mClickDay = mDay;
			return false;
		}
		
	}
	
	private void initCalendarView()
	{
		int id = 1000;
        for(int i = 0; i < TOTAL_DAY; i++)
        {
        	DayTextView tvc = new DayTextView(mContext);
	        int vid = id + i;
	        tvc.setId(vid);
	        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mWidth, mHeight);
	        if((vid - 1000)%7 == 0)
	        {
	            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,-1);
	            if(vid != 1000)
	            {
	            	lp.addRule(RelativeLayout.BELOW, vid - 7);
	            }
	        }
	        else
	        {
	            lp.addRule(RelativeLayout.RIGHT_OF, vid - 1);
	            if(vid > 1007)
	            {
	            	lp.addRule(RelativeLayout.BELOW, vid - 7);
	            }
	        }
	        mRelativeLayoutCalendar.addView(tvc, lp);
        }
	}
	
	private void initCalendarList(Calendar calendar)
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
		mDayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		mSolarDate[0] = mYearCurrent;
		mSolarDate[1] = mMonthCurrent + 1;
		
		for(int i = 0; i < mDayCount; i++)
		{
			mSolarDate[2] = i + 1;
			mLunarDate = SolarToLunar.getLunar(mSolarDate[0], mSolarDate[1], mSolarDate[2]);
			StringBuilder lunar = new StringBuilder();
			boolean isFestival = getLunarString(lunar);
			DayInfoItem di = new DayInfoItem(Integer.toString(mSolarDate[2]), lunar.toString(), isFestival);
			mList.add(di);
		}
	}
	
	private boolean getLunarString(StringBuilder lunar)
	{
		String lu;
		lu = getLunarFestival();
		if(lu != null)
		{
			lunar.append(lu);
			return true;
		}
		lu = getSolarFestival();
		if(lu != null)
		{
			lunar.append(lu);
			return true;
		}
		lu = getTwoFour();
		if(lu != null)
		{
			lunar.append(lu);
			return true;
		}
		if(1 == mLunarDate[2])
	   	{
	   		if(1 == mLunarDate[3])
	   		{
	   			lu = mContext.getString(R.string.leap_month) + mMonth[mLunarDate[1] - 1];
	   		}
	   		else
	   		{
	   			lu = mMonth[mLunarDate[1] - 1];
	   		}
	   	}
	   	else
	   	{
	   		lu = mDay[mLunarDate[2] - 1];
	   	}
		lunar.append(lu);
		return false;
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
	
	private void addYear()
	{
		if(2050 == mCurrentYear)
		{
			return;
		}
		mCurrentYear++;
		mCalendar.set(Calendar.YEAR, mCurrentYear);
		Animation left = AnimationUtils.loadAnimation(mContext, R.anim.left); 
		mRelativeLayoutCalendar.startAnimation(left);
		initCalendarList(mCalendar);
		updateCalendar();
	}
	
	private void addMonth()
	{
		if(2050 == mCurrentYear && 11 == mCurrentMonth)
		{
			return;
		}
		if(mCalendar.getActualMaximum(Calendar.MONTH) == mCurrentMonth)
		{
			mCurrentYear++;
			mCalendar.set(Calendar.YEAR, mCurrentYear);
			mCurrentMonth = 0;
		}
		else
		{
			mCurrentMonth++;
		}
		mCalendar.set(Calendar.MONTH, mCurrentMonth);
		Animation down = AnimationUtils.loadAnimation(mContext, R.anim.down); 
		mRelativeLayoutCalendar.startAnimation(down);
		initCalendarList(mCalendar);
		updateCalendar();
	}
	
	private void reduceYear()
	{
		if(1901 >= mCurrentYear)
		{
			return;
		}
		mCurrentYear--;
		mCalendar.set(Calendar.YEAR, mCurrentYear);
		Animation right = AnimationUtils.loadAnimation(mContext, R.anim.right); 
		mRelativeLayoutCalendar.startAnimation(right);
		initCalendarList(mCalendar);
		updateCalendar();
	}
	
	private void reduceMonth()
	{
		if(1901 == mCurrentYear && 0 == mCurrentMonth)
		{
			return;
		}
		if(0 == mCurrentMonth)
		{
			mCurrentYear--;
			mCalendar.set(Calendar.YEAR, mCurrentYear);
			mCurrentMonth = mCalendar.getActualMaximum(Calendar.MONTH);
		}
		else
		{
			mCurrentMonth--;
		}
		mCalendar.set(Calendar.MONTH, mCurrentMonth);
		Animation up = AnimationUtils.loadAnimation(mContext, R.anim.up); 
		mRelativeLayoutCalendar.startAnimation(up);
		initCalendarList(mCalendar);
		updateCalendar();
	}
	
}