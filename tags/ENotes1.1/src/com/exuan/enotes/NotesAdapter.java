package com.exuan.enotes;

import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NotesAdapter extends CursorAdapter
{
	private Context mContext;
	private boolean mIsDeleteMode;
	
	public NotesAdapter(Context context, Cursor c) {
		super(context, c);
		// TODO Auto-generated constructor stub
		mContext = context;
		mIsDeleteMode = false;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		ViewHolder holder = (ViewHolder)view.getTag();
		int backId = cursor.getInt(cursor.getColumnIndex(NotesDatabaseHelper.BACKGROUND_ID));
		view.setBackgroundResource(ENotesActivity.mThumbIds[backId]);
		int pos = cursor.getPosition();
		holder.mDeleteCheckBox.setOnClickListener(
				new CheckedListener(pos));
		if(mIsDeleteMode)
		{
			holder.mDeleteCheckBox.setVisibility(View.VISIBLE);
			holder.mDeleteCheckBox.setChecked(ENotesActivity.mNoteCheck[pos]);
		}
		else
		{
			holder.mDeleteCheckBox.setVisibility(View.GONE);
		}
		long time = cursor.getLong(cursor.getColumnIndex(NotesDatabaseHelper.ALARM_TIME));
		Calendar c = Calendar.getInstance();
		if(time > c.getTimeInMillis())
		{
			holder.mAlarmImageView.setVisibility(View.VISIBLE);
		}
		else
		{
			holder.mAlarmImageView.setVisibility(View.GONE);
		}
		c.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(NotesDatabaseHelper.MODIFY_TIME)));
		holder.mTimeTextView.setText(c.get(Calendar.YEAR) + context.getString(R.string.div) 
				+ ((c.get(Calendar.MONTH) + 1) < 10 ? ("0" + (c.get(Calendar.MONTH) + 1)) : (c.get(Calendar.MONTH) + 1))
				+ context.getString(R.string.div)
				+ (c.get(Calendar.DAY_OF_MONTH) < 10 ? ("0" + c.get(Calendar.DAY_OF_MONTH)) : c.get(Calendar.DAY_OF_MONTH)) + "  "
				+ (c.get(Calendar.HOUR_OF_DAY) < 10 ? ("0" + c.get(Calendar.HOUR_OF_DAY)) : c.get(Calendar.HOUR_OF_DAY))
				+ ":" + (c.get(Calendar.MINUTE) < 10 ? ("0" + c.get(Calendar.MINUTE)) : c.get(Calendar.MINUTE)) + "  "
				+ ENotesActivity.mWeek[c.get(Calendar.DAY_OF_WEEK) - 1]);
		String detail = cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.DETAIL));
		holder.mSummaryTextView.setText(detail);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = new ViewHolder();
		View v = LayoutInflater.from(mContext).inflate(R.layout.list_notes_item, null);
		holder.mAlarmImageView = (ImageView)v.findViewById(R.id.imageview_alarm);
		holder.mTimeTextView = (TextView)v.findViewById(R.id.textview_time);
		holder.mSummaryTextView = (TextView)v.findViewById(R.id.textview_summary);
		holder.mDeleteCheckBox = (CheckBox)v.findViewById(R.id.checkbox_note);
		v.setTag(holder);
    	return v;
	}
	
	private class CheckedListener implements OnClickListener
	{
		private int mPos;
		public CheckedListener(int id)
		{
			mPos = id;
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			CheckBox cb = (CheckBox)v;
			if(!cb.isChecked())
			{
				cb.setChecked(false);
				ENotesActivity.mNoteCheck[mPos] = false;
			}
			else
			{
				cb.setChecked(true);
				ENotesActivity.mNoteCheck[mPos] = true;
			}
		}
		
	}
	
	public void updateData(Cursor c)
	{
		changeCursor(c);
		this.notifyDataSetChanged();
	}
	
	public void updateMode(boolean delete)
	{
		mIsDeleteMode = delete;
		this.notifyDataSetChanged();
	}
	
	private static class ViewHolder
	{
		ImageView mAlarmImageView;
		TextView mTimeTextView;
		TextView mSummaryTextView;
		CheckBox mDeleteCheckBox;
	}
}