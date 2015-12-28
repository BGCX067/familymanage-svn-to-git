package com.haolianluo.sms2;

import java.io.FileInputStream;

import com.lianluo.core.cache.CacheManager;
import com.lianluo.core.util.HLog;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewSwitcher;
import android.widget.FrameLayout.LayoutParams;

public class ImagePreviewActivity extends HActivity implements View.OnTouchListener, ViewSwitcher.ViewFactory {
	private ViewSwitcher mViewSwitcher;
	private Intent intent = null;
	private Context mContext;
	private GestureDetector mGestureDetector;
	
	private String img_pre;
	private String img_next;
	private String[] img = new String[3];
	//private Bitmap[] bmp = new Bitmap[3];
	private Bitmap[] bmp = null;
	private int nowFlag = 0;	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//全屏显示
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.imagepreview);
		mContext = this;

		
		mViewSwitcher = (ViewSwitcher) findViewById(R.id.viewswitcher_image);
		mViewSwitcher.setFactory(this);
		mViewSwitcher.setLongClickable(true);
		mViewSwitcher.setOnTouchListener(this);
		mGestureDetector = new GestureDetector(new MyGestureListener());
		ImageView img_preview = (ImageView) mViewSwitcher.getCurrentView();
		HLog.e("tag", "sw:" + mViewSwitcher + ",iv:" + img_preview);
		
		
		intent = this.getIntent();
		String img_path = intent.getStringExtra("path");
		nowFlag = 1;
		
		img_pre = intent.getStringExtra("path_pre");
		img_next = intent.getStringExtra("path_next");
		
		img[0] = img_pre;
		img[1] = img_path;
		img[2] = img_next;
		HLog.d("tag2", "img_next:" + img_next);
		
		bmp = null;
		bmp = new Bitmap[3];
		
		if(img_path == null || img_path.equals("null") || img_path.equals(""))
		{
			img_preview.setImageResource(R.drawable.shop_loading);
		}else
		{
			if(bmp[1] == null)
			{	
				Bitmap bt = decodeFile(img_path);
				if(bt == null) {
					HLog.e("tag", "" + bt);
					img_preview.setImageResource(R.drawable.shop_loading);
				} else {
					HLog.e("tag", "" + bt);
					bmp[1] = bt;
					img_preview.setImageBitmap(bmp[1]);
				}
				
			}else
			{
				img_preview.setImageBitmap(bmp[1]);
			}
			
		}
		
	}
	
	@Override
	public View makeView() {
		ImageView img = new ImageView(mContext);
		img.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		return img;
	}
	
	@Override
	public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
		if(mGestureDetector != null) {
			return mGestureDetector.onTouchEvent(paramMotionEvent);
		} else {
			return true;
		}
	}

	public class MyGestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
				int distanceX = (int) e1.getX() - (int) e2.getX();
				int distanceY = (int) e1.getY() - (int) e2.getY();
				if (Math.abs(distanceX) > Math.abs(distanceY)) {
					// show previous
					if (distanceX < 0) {
						showPreImage();
					}// show next
					else {
						showNextImage();
					}
				}
			super.onFling(e1, e2, velocityX, velocityY);
			return true;
		}
	}

	private void showPreImage() {
		nowFlag --;
		if(nowFlag == -1)
		{
			nowFlag = 2;
		}else if(nowFlag == 3)
		{
			nowFlag = 0;
		}
		
		final ImageView iv = (ImageView) mViewSwitcher.getNextView();
		
		if(bmp[nowFlag] == null)
		{	
			Bitmap bt = decodeFile(img[nowFlag]);
			if(bt == null) {
				iv.setImageResource(R.drawable.shop_loading);
			} else {
				bmp[nowFlag] = bt;
				iv.setImageBitmap(bmp[nowFlag]);
			}
		}else
		{
			iv.setImageBitmap(bmp[nowFlag]);
		}
		
		mViewSwitcher.setInAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.pre_left_in));
		mViewSwitcher.setOutAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.pre_right_out));
		mViewSwitcher.showNext();
	}

	private void showNextImage() {
		
			final ImageView iv = (ImageView) mViewSwitcher.getNextView();
			
			nowFlag ++;
			if(nowFlag == -1)
			{
				nowFlag = 2;
			}else if(nowFlag == 3)
			{
				nowFlag = 0;
			}
			
			
			if(bmp[nowFlag] == null)
			{	
				Bitmap bt = decodeFile(img[nowFlag]);
				if(bt == null) {
					iv.setImageResource(R.drawable.shop_loading);
				} else {
					bmp[nowFlag] = bt;
					iv.setImageBitmap(bmp[nowFlag]);
				}
			}else
			{
				iv.setImageBitmap(bmp[nowFlag]);
			}
			
			mViewSwitcher.setInAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.pre_right_in));
			mViewSwitcher.setOutAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.pre_left_out));
			mViewSwitcher.showNext();

	}

	/**
	 * 解决获取图片流时内存溢出问题
	 * @param f
	 * @return
	 */
	private Bitmap decodeFile(String path){
		
		int IMAGE_MAX_SIZE = 500;

	    Bitmap b = null;
	    try {
	        //Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;

	        FileInputStream fis = new FileInputStream(path);
	        BitmapFactory.decodeStream(fis, null, o);
	        fis.close();

	        int scale = 1;
	        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
	            scale = (int)Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
	        }

	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize = scale;
	        HLog.d("scale", scale + "");
	        fis = new FileInputStream(path);
	        b = BitmapFactory.decodeStream(fis, null, o2);
	        fis.close();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	System.gc();
	    }
	    
	    return b;
	}
	
	@Override
	protected void onDestroy() {
		CacheManager.newInstance().clearMemoryCache();
		reBitmap();
		HLog.d("Pre_onDestroy", "onDestroy");
		super.onDestroy();
	}
	

	@Override
	protected void onPause() {
		CacheManager.newInstance().clearMemoryCache();
		HLog.d("Pre_onPause", "onPause");
		super.onPause();
	}
	private void reBitmap()
	{
		CacheManager.newInstance().clearMemoryCache();
		if(bmp.length == 3)
		{
			if(bmp[0] != null && !bmp[0].isRecycled())
			{	
				bmp[0].recycle();
				bmp[0] = null;
			}
			if(bmp[1] != null && !bmp[1].isRecycled())
			{	
				bmp[1].recycle();
				bmp[1] = null;
			}		
			if(bmp[2] != null && !bmp[2].isRecycled())
			{	
				bmp[2].recycle();
				bmp[2] = null;
			}
		}
		System.gc();
	}
	
}
