package com.jayce.calendar;

import com.jayce.calendar.adapter.*;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout.LayoutParams;

public class JCalendarActivity extends Activity 
implements ViewSwitcher.ViewFactory, View.OnTouchListener
{
	
	private static final String LOGNAME = JCalendarActivity.class.getSimpleName();
	private int mCurrentYear;
	private int mCurrentMonth;
	private int mCurrentDay;
	
	private GridView mWeekGrid;
	private GridView mDayGrid;
	
	private TextView mYearText;
	private TextView mMonthText;
	private Button mJumpButton;
	private Button mTodayButton;
	
	private Calendar mCalendar;
	private Calendar mCalendarToday;
	private Date mToday;
	private DayInfoAdapter mDayAdapter;
	private DatePicker mDatePicker;
	private LinearLayout mDayLinearLayout;
	private LinearLayout mMainLinearLayout;
	private Context mContext;
	
	private CalendarGridView mGridSwitcher;
	private GestureDetector mGestureDetector;
	
	private static final int DATE_PICKER_DIALOG_ID = 0;
	private static final int GET_HEIGHT = 0;
	
	private int mScroll;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOGNAME, "onCreate");
        setContentView(R.layout.main);
        mContext = this;
        SharedPreferences pref = getSharedPreferences("pirate", Context.MODE_PRIVATE);
        int viewHeight = pref.getInt("viewHeight", -1);
        if(viewHeight < 0)
        {
        	viewHeight = (getWindowManager().getDefaultDisplay().getHeight() - 60) / 8;
        	pref.edit().putInt("viewHeight", viewHeight).commit();
        }
        
        mDayLinearLayout = (LinearLayout)findViewById(R.id.linearlayout_main);
        mDayLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, viewHeight));
        mWeekGrid = (GridView)findViewById(R.id.gridview_week);
        //mDayGrid = (GridView)findViewById(R.id.gridview_day);
        mYearText = (TextView)findViewById(R.id.textview_year);
        mMonthText = (TextView)findViewById(R.id.textview_month);
        mJumpButton = (Button)findViewById(R.id.button_jump_date);
        mTodayButton = (Button)findViewById(R.id.button_today);
        //mDayLinearLayout = (LinearLayout)findViewById(R.id.linearlayout_day);
        
        //mGridSwitcher = new CalendarGridView(mContext);
        //mDayLinearLayout.addView(mGridSwitcher);
        mGridSwitcher = (CalendarGridView)findViewById(R.id.calendargridview_day);
        mGridSwitcher.setFactory(this);
        mGestureDetector = new GestureDetector(new CalendarGestureListener());
        mGridSwitcher.getCurrentView().setOnTouchListener(this);
        mGridSwitcher.getNextView().setOnTouchListener(this);
        mGridSwitcher.setGridData();
        
        //mGridSwitcher.updateGridData(mCalendar);
        mScroll = 0;
        //mDayGrid.setOnTouchListener(this);
        mJumpButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DATE_PICKER_DIALOG_ID);
			}});
        
        mTodayButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCalendar.setTime(mCalendarToday.getTime());
				mCurrentYear = mCalendar.get(Calendar.YEAR);
		        mCurrentMonth = mCalendar.get(Calendar.MONTH);
		    	mCurrentDay = mCalendar.get(Calendar.DATE);
		    	mGridSwitcher.setInAnimation(null);
				mGridSwitcher.setOutAnimation(null);
		    	updateView();
			}});
        
        mCalendar = Calendar.getInstance();
        mCalendarToday = Calendar.getInstance();
        mToday = mCalendar.getTime();
        mCurrentYear = mCalendar.get(Calendar.YEAR);
        mCurrentMonth = mCalendar.get(Calendar.MONTH);
    	mCurrentDay = mCalendar.get(Calendar.DATE);
    	//mDayAdapter = new DayInfoAdapter(this);
    	//mDayGrid.setAdapter(mDayAdapter);
    }
    
    public void onResume()
    {	
    	Log.e(LOGNAME, "onResume");
    	super.onResume();
    	updateView();
    }
    
    private void updateView()
    {
    	String[] week = getResources().getStringArray(R.array.week_array);
    	mWeekGrid.setAdapter(new ArrayAdapter<String>(this, R.layout.grid_week_item, week));
    	//mDayAdapter.updateAdapter(mCalendar);
    	//mGridSwitcher.updateGridData(mCalendar);
    	mGridSwitcher.nextGridData(mCalendar);
    	mYearText.setText(Integer.toString(mCurrentYear));
    	mMonthText.setText(Integer.toString(mCurrentMonth + 1));
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
						mGridSwitcher.setInAnimation(null);
						mGridSwitcher.setOutAnimation(null);
						updateView();
					}})	
	    		.create();
	    	}
    	}
		return super.onCreateDialog(id);
    }
    
    public void onConfigurationChanged(Configuration newConfig) 
    {
    	Log.e(LOGNAME, "onConfigurationChanged");
    	try 
    	{
    		super.onConfigurationChanged(newConfig);/*
    		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
    		{
    			Log.e(LOGNAME, "Configuration.ORIENTATION_LANDSCAPE");
    		} 
    		else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
    		{
    			Log.e(LOGNAME, "Configuration.ORIENTATION_PORTRAIT");
    		}*/
    	}
    	catch (Exception ex)
    	{

    	}
    }
    /*
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
    	Log.e("jayce", "onTouchEvent");
        if (mGestureDetector.onTouchEvent(ev)) {
            return true;
        }
        return super.onTouchEvent(ev);
    }
    */
    
    public class CalendarGestureListener extends SimpleOnGestureListener {
    	
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float deltaX, float deltaY) {
        	//super.onScroll(e1, e2, deltaX, deltaY);
        	return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        	Log.e("jayce", "onFling");
        	Log.e("jayce", "e1.getX():   " + e1.getX());
        	Log.e("jayce", "e2.getX():   " + e2.getX());
        	
        	Log.e("jayce", "e1.getY():   " + e1.getY());
        	Log.e("jayce", "e2.getY():   " + e2.getY());
        	
        	int distanceX = (int) e1.getX() - (int) e2.getX();
            int distanceY = (int) e1.getY() - (int) e2.getY();
            Log.e("jayce", "distanceX:   " + distanceX);
        	Log.e("jayce", "distanceY:   " + distanceY);
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

	@Override
	public View makeView() {
		// TODO Auto-generated method stub
		LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		return mInflater.inflate(R.layout.grid_view, null); 
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		Log.e("jayce", "onTouch");
		return mGestureDetector.onTouchEvent(arg1);
	}

	private void addYear()
	{
		if(2050 == mCurrentYear)
		{
			return;
		}
		mCurrentYear++;
		mCalendar.set(Calendar.YEAR, mCurrentYear);
		mGridSwitcher.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.right_in));
		mGridSwitcher.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_out));
		updateView();
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
		mGridSwitcher.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.down_in));
		mGridSwitcher.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.up_out));
		updateView();
	}
	
	private void reduceYear()
	{
		if(1901 >= mCurrentYear)
		{
			return;
		}
		mCurrentYear--;
		mCalendar.set(Calendar.YEAR, mCurrentYear);
		mGridSwitcher.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_in));
		mGridSwitcher.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.right_out));
		updateView();
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
		mGridSwitcher.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.up_in));
		mGridSwitcher.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.down_out));
		updateView();
	}
	
}