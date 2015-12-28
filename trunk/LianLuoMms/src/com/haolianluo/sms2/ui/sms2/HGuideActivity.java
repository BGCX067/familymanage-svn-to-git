package com.haolianluo.sms2.ui.sms2;



import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.model.HCloseInterface;
import com.haolianluo.sms2.model.HMyScrollLayout;
import com.haolianluo.sms2.model.HResDatabaseHelper;
import com.haolianluo.sms2.model.HResProvider;
import com.haolianluo.sms2.model.HStatistics;
import com.haolianluo.sms2.R;
import com.lianluo.core.skin.SkinManage;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;


public class HGuideActivity extends SkinActivity implements HCloseInterface {
	
	private HMyScrollLayout mScrollLayout;
	private int mTryID = 0;
	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.hguide);
		 mContext = this;
		 mScrollLayout = (HMyScrollLayout) findViewById(R.id.ScrollLayout);
		 
		 final HCloseInterface closeInterface = this;
		 FrameLayout fa = (FrameLayout) mScrollLayout.findViewById(R.id.fl_try_a);
     	FrameLayout fb = (FrameLayout) mScrollLayout.findViewById(R.id.fl_try_b);
     	final FrameLayout fc = (FrameLayout) mScrollLayout.findViewById(R.id.fl_try_c);
     	final ImageView iv_try = (ImageView)mScrollLayout.findViewById(R.id.imageview_try);
     	final ImageView iv_on_a = (ImageView)mScrollLayout.findViewById(R.id.iv_try_a);
     	final ImageView iv_on_b = (ImageView)mScrollLayout.findViewById(R.id.iv_try_b);
     	final ImageView iv_on_c = (ImageView)mScrollLayout.findViewById(R.id.iv_try_c);
     	Button bt_try = (Button)findViewById(R.id.button_try_now);
     	fa.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					iv_try.setBackgroundResource(R.drawable.try_big_a);
					iv_on_a.setBackgroundResource(R.drawable.try_on);
					iv_on_b.setBackgroundColor(0x00ffffff);
					iv_on_c.setBackgroundColor(0x00ffffff);
					mTryID = 0;
				}});
     	
     	fb.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					iv_try.setBackgroundResource(R.drawable.try_big_b);
					iv_on_a.setBackgroundColor(0x00ffffff);
					iv_on_b.setBackgroundResource(R.drawable.try_on);
					iv_on_c.setBackgroundColor(0x00ffffff);
					mTryID = 1;
				}});
     	fc.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					iv_try.setBackgroundResource(R.drawable.try_big_c);
					iv_on_a.setBackgroundColor(0x00ffffff);
					iv_on_b.setBackgroundColor(0x00ffffff);
					iv_on_c.setBackgroundResource(R.drawable.try_on);
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
         		Intent intent = new Intent();
         		intent.setClass(mContext, HThreadActivity.class);
         		mContext.startActivity(intent);
         		if(closeInterface != null){
         			//closeActivity(closeInterface);
         			closeInterface.close();
         		cu.close();
         		HStatistics mStatistics = new HStatistics(mContext);
         		mStatistics.add(HStatistics.Z2_2, "" + (mTryID + 1), "", "");
         		}}});
		 mScrollLayout.setInterface(this);
		 
		//更新后第一次启动的时候把强制更新标志位重置
		SharedPreferences spU = getSharedPreferences(HConst.UPDATE_FLAG, 0);
		spU.edit().putBoolean(HConst.IS_UPDATE, false).commit();
		//spU.edit().putInt(HConst.SHOW_TIMES, 0).commit();
		spU.edit().clear();
	}

	@Override
	public void close() {
		finish();
	}
	
}
