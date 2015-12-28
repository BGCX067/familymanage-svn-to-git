package com.exuan.texsttextview;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TestActivity extends Activity 
implements View.OnTouchListener{
    /** Called when the activity is first created. */
	
	LinearLayout mLinearLayoutMain;
	RelativeLayout mRelativeLayout;
	TextView mTextView;
	private int mHeight;
	private int mWidth;
	private GestureDetector mGestureDetector;
	
	private static final String LOGTAG = "TestActivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mHeight = getWindowManager().getDefaultDisplay().getHeight();
        mWidth = getWindowManager().getDefaultDisplay().getWidth();
        
        Log.e(LOGTAG, "h:" + mHeight);
        Log.e(LOGTAG, "w:" + mWidth);
        
        int width = (mWidth - 10)/7;
        int height = (mHeight - 80)/8;
        
        mLinearLayoutMain = (LinearLayout)findViewById(R.id.linearlayout_main);
        mRelativeLayout = (RelativeLayout)findViewById(R.id.relativelayout_main);
        mTextView = (TextView)findViewById(R.id.textview);
        mGestureDetector = new GestureDetector(new CalendarGestureListener());
        mRelativeLayout.setOnTouchListener(this);
        mTextView.setOnTouchListener(this);
        int id = 1000;
        for(int i = 0; i < 42; i++)
        {
        	Log.e(LOGTAG, "i:" + i);
        	TestTextView tv = new TestTextView(this);
        	int vid = id + i;
            tv.setId(vid);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
            if((vid - 1000)%7 == 0)
            {
            	Log.e(LOGTAG, "(id - 1000)%7 == 0");
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
            mRelativeLayout.addView(tv, lp);
        }
    }
    
    public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
    {
    	Log.e(LOGTAG, "onTouch");
    	return mGestureDetector.onTouchEvent(paramMotionEvent);
    }
    
    public class CalendarGestureListener extends SimpleOnGestureListener {
    	
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float deltaX, float deltaY) {
        	//super.onScroll(e1, e2, deltaX, deltaY);
        	Log.e(LOGTAG, "onScroll");
        	return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        	Log.e(LOGTAG, "onFling");
            super.onFling(e1, e2, velocityX, velocityY);
        	return true;
        }
    }
}