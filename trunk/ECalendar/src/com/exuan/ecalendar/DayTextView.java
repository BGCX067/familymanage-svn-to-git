package com.exuan.ecalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;

public class DayTextView extends TextView
{
	private DayInfoItem mDay;
	private boolean mIsToday;
	
	public DayTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		setDrawingCacheEnabled(true);
	}

	public DayTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setDrawingCacheEnabled(true);
	}
	
	public DayTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		setDrawingCacheEnabled(true);
	}
	
	protected void onDraw(Canvas paramCanvas)
	{
		//today round
		if(mIsToday)
		{
			Paint painta = new Paint();
			painta.setStyle(Paint.Style.STROKE);
			painta.setColor(Color.BLACK);
			painta.setAntiAlias(true);
			Rect rect = paramCanvas.getClipBounds();
			RectF rt = new RectF(rect);
			rt.inset(getMeasuredWidth()/12, getMeasuredHeight()/12);
			paramCanvas.drawRoundRect(rt, getMeasuredWidth()/5, getMeasuredHeight()/5, painta);
			Paint paintb = new Paint();
			paintb.setColor(Color.GRAY);
			paintb.setAntiAlias(true);
			paintb.setAlpha(50);
			paramCanvas.drawRoundRect(rt, getMeasuredWidth()/6, getMeasuredWidth()/6, paintb);
		}
		//solar text
		Paint paint1 = new Paint();
		paint1.setAntiAlias(true);
		paint1.setFakeBoldText(true);
		paint1.setTextSize(getMeasuredWidth()/2);
		if(mDay.mIsWeekEnd)
		{
			paint1.setColor(ECalendarActivity.mHolidayColor);
		}
		else
		{
			paint1.setColor(Color.BLACK);
		}
		Rect text = new Rect();
		paint1.getTextBounds(mDay.mSolarDay, 0, mDay.mSolarDay.length(), text);
		int w = getMeasuredWidth();
		int w1 = text.width();
		float fw = (w - w1) / 2;
	    float fh = getMeasuredHeight() / 2 + 1;
		paramCanvas.drawText(mDay.mSolarDay, fw, fh, paint1);
		//lunar text
		Paint paint2 = new Paint();
		paint2.setAntiAlias(true);
		paint2.setTextSize(getMeasuredWidth()/4);
		if(mDay.mIsFestival)
		{
			paint2.setColor(ECalendarActivity.mHolidayColor);
		}
		else
		{
			paint2.setColor(Color.BLACK);
		}
		paint2.getTextBounds(mDay.mLunarDay, 0, mDay.mLunarDay.length(), text);
		w1 = text.width();
		fw = (w - w1) / 2;
		fh = getMeasuredHeight() / 2 + 2 + text.height();
		paramCanvas.drawText(mDay.mLunarDay, fw, fh, paint2);
		if(mDay.mHasDiary)
		{
			Paint pd = new Paint();
			pd.setStyle(Paint.Style.STROKE);
			pd.setColor(ECalendarActivity.mHolidayColor);
			pd.setStrokeWidth(2);
			Rect rect = paramCanvas.getClipBounds();
			RectF rt = new RectF(rect);
			rt.inset(getMeasuredWidth()/12, getMeasuredHeight()/12);
			paramCanvas.drawLine(rt.left + rt.width()/4, rt.top, rt.right - rt.width()/4, rt.top, pd);
		}
	}
	
	public void setDate(DayInfoItem day, boolean today)
	{
		mDay = day;
		mIsToday = today;
		invalidate();
	}
}