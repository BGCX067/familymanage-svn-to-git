package com.exuan.ecalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class EDiaryListActivity extends Activity
{
	private static final int MENU_DELETE = 0;
	private boolean mIsDeleteMode = false;
	private Context mContext;
	private TextView mNoDiaryTextView;
	private ListView mDiaryListView;
	private LinearLayout mDeleteLinearLayout;
	private Button mDeleteButton;
	private Button mCancelButton;
	private DiaryAdapter mDiaryAdapter;
	private Cursor mCursor;
	private int mLongIndex = 0;
	public static boolean[] mIsCheck = null;
	
	public static final int ACTION_DIALOG_ID = 0;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_diary);
		mContext = this;
		mDiaryListView = (ListView)findViewById(R.id.listview_diary);
		mNoDiaryTextView = (TextView)findViewById(R.id.text_no_diary);
		mCursor = getContentResolver().query(CalendarProvider.CONTENT_URI_DIARYS, null, null, null, DatabaseHelper.DATE + " DESC");
		if(mCursor.getCount() > 0)
		{
			mNoDiaryTextView.setVisibility(View.GONE);
		}
		else
		{
			mNoDiaryTextView.setVisibility(View.VISIBLE);
		}
		mDiaryAdapter = new DiaryAdapter(mContext, mCursor);
		mDiaryListView.setAdapter(mDiaryAdapter);
		mDeleteLinearLayout = (LinearLayout)findViewById(R.id.linearlayout_delete);
		mDeleteButton = (Button)findViewById(R.id.button_delete);
		mCancelButton = (Button)findViewById(R.id.button_cancel);
		mDeleteButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				for(int i = 0; i < mIsCheck.length; i++)
				{
					if(mIsCheck[i])
					{
						mCursor.moveToPosition(i);
						long id = mCursor.getLong(mCursor.getColumnIndex("_id"));
						getContentResolver().delete(CalendarProvider.CONTENT_URI_DIARYS, "_id = '" + id + "'", null);
					}
				}
				mCursor = getContentResolver().query(CalendarProvider.CONTENT_URI_DIARYS, null, null, null, DatabaseHelper.DATE + " DESC");
				mDiaryAdapter.updateData(mCursor);
				if(mCursor.getCount() > 0)
				{
					mNoDiaryTextView.setVisibility(View.GONE);
				}
				else
				{
					mNoDiaryTextView.setVisibility(View.VISIBLE);
				}
				setDeleteMode(false);
			}});
		
		mCancelButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setDeleteMode(false);
			}});
		
		mDiaryListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				mCursor.moveToPosition(arg2);
				String date = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.DATE));
				Intent intent = new Intent(mContext, EDiaryDetailActivity.class);
				intent.putExtra(DatabaseHelper.DATE, date);
				startActivity(intent);
			}});
		mDiaryListView.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				mLongIndex = arg2;
				removeDialog(ACTION_DIALOG_ID);
				showDialog(ACTION_DIALOG_ID);
				return false;
			}});
	}
	
	public Dialog onCreateDialog(int id)
	{
		switch(id)
		{
			case ACTION_DIALOG_ID:
			{
				mCursor.moveToPosition(mLongIndex);
				final String date = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.DATE));
				return new AlertDialog.Builder(mContext)
	    		.setTitle(date.substring(0, 4) + getString(R.string.div) + date.substring(4, 6) + 
	    				getString(R.string.div) + date.substring(6))
	    		.setItems(R.array.action_array, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch(which)
						{
							case 0:
							{
								Intent intent = new Intent(mContext, EDiaryDetailActivity.class);
								intent.putExtra(DatabaseHelper.DATE, date);
								mLongIndex = 0;
								startActivity(intent);
							}
							break;
							case 1:
							{
								long id = mCursor.getLong(mCursor.getColumnIndex("_id"));
								getContentResolver().delete(CalendarProvider.CONTENT_URI_DIARYS, "_id = '" + id + "'", null);
								mCursor = getContentResolver().query(CalendarProvider.CONTENT_URI_DIARYS, null, null, null, DatabaseHelper.DATE + " DESC");
								mDiaryAdapter.updateData(mCursor);
								if(mCursor.getCount() > 0)
								{
									mNoDiaryTextView.setVisibility(View.GONE);
								}
								else
								{
									mNoDiaryTextView.setVisibility(View.VISIBLE);
								}
								mLongIndex = 0;
							}
							break;
						}
					}
				})
	    		.create();
			}
		}
		return super.onCreateDialog(id);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_DELETE, 0, R.string.delete).setIcon(
				R.drawable.menu_delete);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case MENU_DELETE:
			{
				if(!mIsDeleteMode)
				{
					setDeleteMode(true);
				}
				return true;
			}
		}
		return false;
	}
	
	public void onResume()
	{
		super.onResume();
		if(!mIsDeleteMode)
		{
			mCursor = getContentResolver().query(CalendarProvider.CONTENT_URI_DIARYS, null, null, null, DatabaseHelper.DATE + " DESC");
			mDiaryAdapter.updateData(mCursor);
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
	
	public void onDestroy()
	{
		super.onDestroy();
		mCursor.close();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			if(mIsDeleteMode)
	    	{
	    		setDeleteMode(false);
	    		return true;
	    	}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void setDeleteMode(boolean mode)
	{
		mIsDeleteMode = mode;
		if(mode)
		{
			mIsCheck = new boolean[mCursor.getCount()];
			mDiaryAdapter.setDeleteMode(mIsDeleteMode);
			mDeleteLinearLayout.setVisibility(View.VISIBLE);
		}
		else
		{
			mDiaryAdapter.setDeleteMode(mIsDeleteMode);
			mIsCheck = null;
			mDeleteLinearLayout.setVisibility(View.GONE);
		}
	}
}