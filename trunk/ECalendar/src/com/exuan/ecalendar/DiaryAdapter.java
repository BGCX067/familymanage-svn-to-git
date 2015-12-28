package com.exuan.ecalendar;

import java.util.Calendar;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class DiaryAdapter extends CursorAdapter
{
	private Context mContext;
	private boolean mIsDeleteMode;
	private int mResLayout;
	public DiaryAdapter(Context context, Cursor c) {
		super(context, c);
		// TODO Auto-generated constructor stub
		mContext = context;
		mIsDeleteMode = false;
		SharedPreferences pref = context.getSharedPreferences(ECalendarActivity.CALENDAR_PREFS, Context.MODE_PRIVATE);
		if(pref.getBoolean(ESettingActivity.KEY_SUMMARY, true))
		{
			mResLayout = R.layout.diary_item;
		}
		else
		{
			mResLayout = R.layout.diary_item_full;
		}
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		TextView tv_time = (TextView)view.findViewById(R.id.textview_time);
		TextView tv_content = (TextView)view.findViewById(R.id.textview_content);
		CheckBox cb_delete = (CheckBox)view.findViewById(R.id.checkbox_delete);
		String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE));
		int year = Integer.valueOf(date.substring(0, 4));
		int month = Integer.valueOf(date.substring(4, 6)) - 1;
		int day = Integer.valueOf(date.substring(6));
		Calendar ca = Calendar.getInstance();
		ca.set(year, month, day);
		String[] week = mContext.getResources().getStringArray(R.array.week_array);
		int w = (ca.get(Calendar.DAY_OF_WEEK) == 1) ? 6 : (ca.get(Calendar.DAY_OF_WEEK) - 2);
		tv_time.setTextColor(ECalendarActivity.mHolidayColor);
		tv_time.setText(date.substring(0, 4) + mContext.getString(R.string.div) + date.substring(4, 6) + 
				mContext.getString(R.string.div) + date.substring(6) + " " + mContext.getString(R.string.week) + week[w]);
		tv_content.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CONTENT)));
		cb_delete.setOnClickListener(new CheckedChangeListener(cursor.getPosition()));
		if(mIsDeleteMode)
		{
			cb_delete.setVisibility(View.VISIBLE);
			cb_delete.setChecked(EDiaryListActivity.mIsCheck[cursor.getPosition()]);
		}
		else
		{
			cb_delete.setVisibility(View.GONE);
		}
	}

	class CheckedChangeListener implements OnClickListener
	{
		private int mPosition;
		
		public CheckedChangeListener(int position)
		{
			mPosition = position;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			CheckBox cb = (CheckBox)v;
			if(!cb.isChecked())
			{
				cb.setChecked(false);
				EDiaryListActivity.mIsCheck[mPosition] = false;
			}
			else
			{
				cb.setChecked(true);
				EDiaryListActivity.mIsCheck[mPosition] = true;
			}
		}
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = LayoutInflater.from(mContext).inflate(mResLayout, null);
		return v;
	}
	
	public void setDeleteMode(boolean delete)
	{
		mIsDeleteMode = delete;
		notifyDataSetChanged();
	}
	
	public void updateData(Cursor cursor)
	{
		this.changeCursor(cursor);
		notifyDataSetChanged();
	}
	
}