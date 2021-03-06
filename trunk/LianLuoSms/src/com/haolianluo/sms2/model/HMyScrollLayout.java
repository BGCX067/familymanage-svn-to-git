package com.haolianluo.sms2.model;


import com.haolianluo.sms2.HThreadActivity;
import com.haolianluo.sms2.data.HConst;
import com.lianluo.core.util.HLog;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class HMyScrollLayout extends ViewGroup{

    private static final String TAG = "ScrollLayout";   
    
    private VelocityTracker mVelocityTracker;  			// 用于判断甩动手势
    
    private static final int SNAP_VELOCITY = 600;    
    
    private Scroller  mScroller;						// 滑动控制器
	
    private int mCurScreen;    						
    
	private int mDefaultScreen = 0;    					
	 
    private float mLastMotionX;    
    
    private Context mContext;
    
 //   private int mTouchSlop;							
    
//    private static final int TOUCH_STATE_REST = 0;
//    private static final int TOUCH_STATE_SCROLLING = 1;
//    private int mTouchState = TOUCH_STATE_REST;
    
//    private HOnViewChangeListener mOnViewChangeListener;
    private HCloseInterface closeInterface;
	 
	public HMyScrollLayout(Context context) {
		super(context);
		mContext = context;
		init(context);
	}
	
	public HMyScrollLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init(context);
	}
	
	public HMyScrollLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init(context);
	}
	
	private void init(Context context)
	{
		mCurScreen = mDefaultScreen;    
	  
	 //   mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();    
	        
	    mScroller = new Scroller(context); 
	    
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		
		
		 if (changed) {    
	            int childLeft = 0;    
	            final int childCount = getChildCount();    
	                
	            for (int i=0; i<childCount; i++) {    
	                final View childView = getChildAt(i);    
	                if (childView.getVisibility() != View.GONE) {    
	                    final int childWidth = childView.getMeasuredWidth();    
	                    childView.layout(childLeft, 0,     
	                            childLeft+childWidth, childView.getMeasuredHeight());    
	                    childLeft += childWidth;    
	                }    
	            }    
	        }    
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
	
		
		final int width = MeasureSpec.getSize(widthMeasureSpec);       
	    //final int widthMode = MeasureSpec.getMode(widthMeasureSpec);      
	    
		
		final int count = getChildCount();       
        for (int i = 0; i < count; i++) {       
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);       
        }         
        
        scrollTo(mCurScreen * width, 0);
		
	}


	 public void snapToDestination() {    
	        final int screenWidth = getWidth();    

	        final int destScreen = (getScrollX()+ screenWidth/2)/screenWidth;    
	        snapToScreen(destScreen);    
	 }  
	
	 public void snapToScreen(int whichScreen) {    
	
	        // get the valid layout page    
	        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount()-1));    
	        if (getScrollX() != (whichScreen*getWidth())) {    
	                
	            final int delta = whichScreen*getWidth()-getScrollX();    
	        
	            mScroller.startScroll(getScrollX(), 0,     
	                    delta, 0, Math.abs(delta)*2);
 
	            
	            mCurScreen = whichScreen;    
	            invalidate();       // Redraw the layout    
	            
//	            if (mOnViewChangeListener != null)
//	            {
//	            	mOnViewChangeListener.OnViewChange(mCurScreen);
//	            }
	        }    
	    }    


	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if (mScroller.computeScrollOffset()) {    
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());  
            postInvalidate();    
        }   
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
	        final int action = event.getAction();    
	        final float x = event.getX();    
	        final float y = event.getY();    

	        switch (action) {    
	        case MotionEvent.ACTION_DOWN: 
	        	if (mVelocityTracker == null) {   
			         mVelocityTracker = VelocityTracker.obtain();    
			         mVelocityTracker.addMovement(event); 
			    }
	        	 
	            if (!mScroller.isFinished()){    
	                mScroller.abortAnimation();   
	            }
	            HLog.i("TAG", "x:" + x + ", y:" + y + ", x1:" + width_px(65) + ", x2:" + width_px(250) + ", y1:" + height_px(330) + ", y2:" + height_px(430));
	            HLog.i("-------------------------------------------->>>" + mCurScreen);
	            if(mCurScreen == 2){
	            	if(x > width_px(100) && x < width_px(380) && y > height_px(540) && y < height_px(620)){
	            		mCurScreen = 0;
	            		Intent intent = new Intent();
	            		intent.setClass(mContext, HThreadActivity.class);
	            		mContext.startActivity(intent);
	            		if(closeInterface != null){
	            			closeActivity(closeInterface);
	            		}
	            	}
	            }
	            
	            mLastMotionX = x;	           
	            break;    
	                
	        case MotionEvent.ACTION_MOVE:  
	           int deltaX = (int)(mLastMotionX - x);
	           
        	   if (IsCanMove(deltaX))
        	   {
        		 if (mVelocityTracker != null)
  		         {
  		            	mVelocityTracker.addMovement(event); 
  		         }   

  	            mLastMotionX = x;    
 
  	            scrollBy(deltaX, 0);	
        	   }
         
	           break;    
	                
	        case MotionEvent.ACTION_UP:       
	        	
	        	int velocityX = 0;
	            if (mVelocityTracker != null)
	            {
	            	mVelocityTracker.addMovement(event); 
	            	mVelocityTracker.computeCurrentVelocity(1000);  
	            	velocityX = (int) mVelocityTracker.getXVelocity();
	            }
	                    
	                
	            if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {       
	                // Fling enough to move left       
	                HLog.e(TAG, "snap left");    
	                snapToScreen(mCurScreen - 1);       
	            } else if (velocityX < -SNAP_VELOCITY       
	                    && mCurScreen < getChildCount() - 1) {       
	                // Fling enough to move right       
	                HLog.e(TAG, "snap right");    
	                snapToScreen(mCurScreen + 1);       
	            } else {       
	                snapToDestination();       
	            }      
	            
	           
	            
	            if (mVelocityTracker != null) {       
	                mVelocityTracker.recycle();       
	                mVelocityTracker = null;       
	            }       
	            
	      //      mTouchState = TOUCH_STATE_REST;
	            break;      
	        }    
	            
	        return true;    
	}
	
//	private  int dip2px(Context context, float dpValue) {  
//        final float scale = context.getResources().getDisplayMetrics().density;  
//        return (int) (dpValue * scale + 0.5f);  
//    }
	private  int width_px(float dpValue) {  
        return (int) (dpValue * HConst.width / 480);  
    }
	private  int height_px(float dpValue) {  
        return (int) (dpValue * HConst.height / 800);  
    }
	
	private void closeActivity(HCloseInterface closeInterface){
		closeInterface.close();
	}
	
	public void setInterface(HCloseInterface closeInderface){
		this.closeInterface = closeInderface;
	}
	
//	
//	  public boolean onInterceptTouchEvent(MotionEvent ev) {
//          // TODO Auto-generated method stub
//          final int action = ev.getAction();
//          if ((action == MotionEvent.ACTION_MOVE)
//                          && (mTouchState != TOUCH_STATE_REST)) {
//        	  HLog.i("", "onInterceptTouchEvent  return true");
//                  return true;
//          }
//          final float x = ev.getX();
//          final float y = ev.getY();
//          switch (action) {
//          case MotionEvent.ACTION_MOVE:
//                  final int xDiff = (int) Math.abs(mLastMotionX - x);
//                  if (xDiff > mTouchSlop) {
//                          mTouchState = TOUCH_STATE_SCROLLING;
//                  }
//                  break;
//
//          case MotionEvent.ACTION_DOWN:
//                  mLastMotionX = x;
//
//                  mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
//                                  : TOUCH_STATE_SCROLLING;
//                  break;
//
//          case MotionEvent.ACTION_CANCEL:
//          case MotionEvent.ACTION_UP:
//                  mTouchState = TOUCH_STATE_REST;
//                  break;
//          }
//          
//          if (mTouchState != TOUCH_STATE_REST)
//          {
//        	  HLog.i("", "mTouchState != TOUCH_STATE_REST  return true");
//          }
//
//    
//          return mTouchState != TOUCH_STATE_REST;
//  }
	
	private boolean IsCanMove(int deltaX)
	{
	
		if (getScrollX() <= 0 && deltaX < 0 )
		{
			return false;
		}
		
		if  (getScrollX() >=  (getChildCount() - 1) * getWidth() && deltaX > 0)
		{
			return false;
		}
			
		
		return true;
	}
	
//	public void SetOnViewChangeListener(HOnViewChangeListener listener)
//	{
//		mOnViewChangeListener = listener;
//	}

}
