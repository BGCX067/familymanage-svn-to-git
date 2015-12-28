package com.jayce.calendar;

import java.util.Calendar;

import com.jayce.calendar.adapter.DayInfoAdapter;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ViewSwitcher;

public class CalendarGridView extends ViewSwitcher
{
	//private GridView mGridView = null;
	private DayInfoAdapter mDayAdapter;
	private Context mContext;
	
	private static final int FROM_NONE = 0;
    private static final int FROM_ABOVE = 1;
    private static final int FROM_BELOW = 2;
    private static final int FROM_LEFT = 4;
    private static final int FROM_RIGHT = 8;
    
    private static final int TOUCH_MODE_INITIAL_STATE = 0;
    private static final int TOUCH_MODE_DOWN = 1;
    private static final int TOUCH_MODE_VSCROLL = 0x20;
    private static final int TOUCH_MODE_HSCROLL = 0x40;
    private int mTouchMode = TOUCH_MODE_INITIAL_STATE;
	
	public CalendarGridView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		mDayAdapter = new DayInfoAdapter(mContext);
	}
	
	public CalendarGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mDayAdapter = new DayInfoAdapter(mContext);
	}
	
	public void setGridData()
	{
		GridView mGridView = (GridView)this.getCurrentView();
		mDayAdapter = new DayInfoAdapter(mContext);
		mGridView.setAdapter(mDayAdapter);
	}
	
	public void updateGridData(Calendar c)
	{
		//GridView mGridView = (GridView)this.getCurrentView();
		//mGridView.setAdapter(mDayAdapter);
		mDayAdapter.updateAdapter(c);
		//showNext();
	}
	
	public void nextGridData(Calendar c)
	{
		GridView mGridView = (GridView)this.getNextView();
		mGridView.setAdapter(mDayAdapter);
		mDayAdapter.updateAdapter(c);
		showNext();
	}
	
}