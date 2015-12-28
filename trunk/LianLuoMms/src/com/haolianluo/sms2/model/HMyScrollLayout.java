package com.haolianluo.sms2.model;


import java.io.File;

import com.haolianluo.sms2.data.HConst;
import com.lianluo.core.net.download.DLManager;
import com.lianluo.core.skin.SkinManage;
import com.lianluo.core.util.HLog;
import com.haolianluo.sms2.ui.sms2.HThreadActivity;

import com.haolianluo.sms2.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Scroller;
import android.widget.TextView;

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
	                if(i == childCount - 1)
	                {/*
	                	FrameLayout fa = (FrameLayout) childView.findViewById(R.id.fl_try_a);
	                	FrameLayout fb = (FrameLayout) childView.findViewById(R.id.fl_try_b);
	                	FrameLayout fc = (FrameLayout) childView.findViewById(R.id.fl_try_c);
	                	final ImageView iv_try = (ImageView)childView.findViewById(R.id.imageview_try);
	                	final ImageView iv_on_a = (ImageView)childView.findViewById(R.id.iv_try_a);
	                	final ImageView iv_on_b = (ImageView)childView.findViewById(R.id.iv_try_b);
	                	final ImageView iv_on_c = (ImageView)childView.findViewById(R.id.iv_try_c);
	                	Button bt_try = (Button)childView.findViewById(R.id.button_try_now);
	                	fa.setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								iv_try.setBackgroundResource(R.drawable.try_big_a);
								iv_on_a.setVisibility(View.VISIBLE);
								iv_on_b.setVisibility(View.GONE);
								iv_on_c.setVisibility(View.GONE);
								mTryID = 0;
							}});
	                	
	                	fb.setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								iv_try.setBackgroundResource(R.drawable.try_big_b);
								iv_on_a.setVisibility(View.GONE);
								iv_on_b.setVisibility(View.VISIBLE);
								iv_on_c.setVisibility(View.GONE);
								mTryID = 1;
							}});
	                	fc.setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								iv_try.setBackgroundResource(R.drawable.try_big_c);
								iv_on_a.setVisibility(View.GONE);
								iv_on_b.setVisibility(View.GONE);
								iv_on_c.setVisibility(View.VISIBLE);
								mTryID = 2;
							}});
	                	bt_try.setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								final Cursor cu = mContext.getContentResolver().query(HResProvider.CONTENT_URI_SKIN, null, 
				            			null, null, null);
								cu.moveToPosition(mTryID);
								ContentValues cvalues = new ContentValues();
								cvalues.put(HResDatabaseHelper.RES_USE, 0);
								mContext.getContentResolver().update(
										HResProvider.CONTENT_URI_SKIN, cvalues, null, null);
								ContentValues values = new ContentValues();
								values.put(HResDatabaseHelper.RES_USE, 1);
								mContext.getContentResolver().update(
										HResProvider.CONTENT_URI_SKIN, values,
										"_id = '" + cu.getLong(cu.getColumnIndex("_id")) + "'", null);
								SkinManage.mCurrentSkin = cu
										.getString(cu
												.getColumnIndex(HResDatabaseHelper.PACKAGENAME));
								SkinManage.mCurrentFile = cu.getString(cu
										.getColumnIndex(HResDatabaseHelper.FILE_NAME));
								mCurScreen = 0;
			            		Intent intent = new Intent();
			            		intent.setClass(mContext, HThreadActivity.class);
			            		mContext.startActivity(intent);
			            		if(closeInterface != null){
			            			closeActivity(closeInterface);
			            		cu.close();
			            		}}});
			            		*/
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
	 private int mTryID = 0;
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
	            }/*
	            if(mCurScreen == 2){
	            	if(x > width_px(100) && x < width_px(380) && y > height_px(540) && y < height_px(620)){
	            		mCurScreen = 0;
//	            		Intent intent = new Intent();
//	            		intent.setClass(mContext, HThreadActivity.class);
//	            		mContext.startActivity(intent);
//	            		if(closeInterface != null){
//	            			closeActivity(closeInterface);
//	            		}
	            		return true;
	            	}
	            } */  
	            
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
	            } /*else if(mCurScreen >= getChildCount() - 1){
	            	return true;
	            } */else {       
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
	
}
