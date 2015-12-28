package com.exuan.ecalendar;

import java.io.File;
import java.io.FileOutputStream;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ESettingActivity extends Activity{
	private Context mContext;
	private ImageView mImageViewColor;
	private CheckBox mCheckBoxSummary;
	private ImageView mImageViewHead;
	public static final Integer[] HOLIDAY_COLOR = 
		{0xfffb466c, 0xfffa3ad8,
         0xff1025f7, 0xff0bc413,
         0xffcfc304, 0xffff6600,
         0xffe62809};
	public static final String KEY_COLOR = "color";
	public static final String KEY_SUMMARY = "summary";
	private SharedPreferences mPrefs;
	private File mHeadFile;
	private static final int REQUEST_IMAGE = 0;
	private static final int DIALOG_COLOR_ID = 0;
	
	protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.layout_setting);
        mContext = this;
        mHeadFile = new File(mContext.getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "head.jpg");
        LinearLayout linearLayoutColor = (LinearLayout)findViewById(R.id.linearlayout_color);
        mImageViewColor = (ImageView)findViewById(R.id.imageview_color);
        mCheckBoxSummary = (CheckBox)findViewById(R.id.checkbox_summary);
        LinearLayout linearlayoutHead = (LinearLayout)findViewById(R.id.linearlayout_head);
        mImageViewHead = (ImageView)findViewById(R.id.imageview_head);
        
        mPrefs = mContext.getSharedPreferences(ECalendarActivity.CALENDAR_PREFS, Context.MODE_PRIVATE);
        int index = mPrefs.getInt(KEY_COLOR, 0);
        mImageViewColor.setBackgroundColor(HOLIDAY_COLOR[index]);
        mCheckBoxSummary.setChecked(mPrefs.getBoolean(KEY_SUMMARY, true));
        if(mHeadFile.exists())
        {
        	mImageViewHead.setImageBitmap(BitmapFactory.decodeFile(mHeadFile.getAbsolutePath()));
        }
        linearLayoutColor.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				removeDialog(DIALOG_COLOR_ID);
				showDialog(DIALOG_COLOR_ID);
			}});
        
        linearlayoutHead.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*"); 
				intent.putExtra("crop", "true");
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
				intent.putExtra("outputX", 300);
			    intent.putExtra("outputY", 300);
		        intent.putExtra("return-data", true);
				Intent wrapperIntent = Intent.createChooser(intent, null);
				startActivityForResult(wrapperIntent, REQUEST_IMAGE);
			}});
        
        mCheckBoxSummary.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!mCheckBoxSummary.isChecked())
				{
					mCheckBoxSummary.setChecked(false);
					mPrefs.edit().putBoolean(KEY_SUMMARY, false).commit();
				}
				else
				{
					mCheckBoxSummary.setChecked(true);
					mPrefs.edit().putBoolean(KEY_SUMMARY, true).commit();
				}
			}});
	}
	
	public Dialog onCreateDialog(int id)
    {
    	switch(id)
    	{
	    	case DIALOG_COLOR_ID:
	    	{
	    		final ScrollView ScrollViewSetColor = (ScrollView) LayoutInflater.from(mContext).inflate(R.layout.layout_set_color, null);
				for(int i = 0; i < HOLIDAY_COLOR.length; i++)
				{
					final TextView tv = (TextView) ((LinearLayout)ScrollViewSetColor.getChildAt(0)).getChildAt(i);
					final int in = i;
					tv.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							removeDialog(DIALOG_COLOR_ID);
							int index = in;
							mPrefs.edit().putInt(KEY_COLOR, index).commit();
							mImageViewColor.setBackgroundColor(HOLIDAY_COLOR[index]);
							ECalendarActivity.mHolidayColor = HOLIDAY_COLOR[index];
						}});
				}
	    		return new AlertDialog.Builder(mContext)
	    		.setTitle(R.string.holiday_color)
				.setView(ScrollViewSetColor)
				.create();
	    	}
    	}
    	return super.onCreateDialog(id);
    }
	
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK)
		{
			if(REQUEST_IMAGE == requestCode)
			{
				Bundle extras = data.getExtras();
				if(null != extras)
				{
					Bitmap photo = extras.getParcelable("data"); 
					if(null != photo)
					{
						try
						{
							if(mHeadFile.exists())
							{
								mHeadFile.delete();
							}
						    FileOutputStream out = new FileOutputStream(mHeadFile);
						    photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
						    Drawable drawable = new BitmapDrawable(photo); 
							mImageViewHead.setImageDrawable(drawable);
							Intent i = new Intent(CalendarWidgetProvider.UPDATE_HEAD);
							sendBroadcast(i);
						} catch (Exception e) {
						}
					}
				}
			}
		}
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
}