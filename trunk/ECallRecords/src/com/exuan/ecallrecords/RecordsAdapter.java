package com.exuan.ecallrecords;

import java.util.Calendar;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RecordsAdapter extends CursorAdapter
{
	private Context mContext;
	private String[] mWeekArray;
	private static final int TYPE_CALL = 0;
	private static final int TYPE_MSG = 1;
	
	//ViewHolder mViewHolder;
	public RecordsAdapter(Context context, Cursor c) {
		super(context, c);
		// TODO Auto-generated constructor stub
		mContext = context;
		mWeekArray = mContext.getResources().getStringArray(R.array.week_array);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		ViewHolder mViewHolder = (ViewHolder)view.getTag();
		switch(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)))
		{
			case CallLog.Calls.INCOMING_TYPE:
			{
				mViewHolder.mPhotoImageView.setBackgroundResource(R.drawable.incoming_call);
				mViewHolder.mStateImageView.setBackgroundResource(R.drawable.incoming_state);
			}
			break;
			case CallLog.Calls.OUTGOING_TYPE:
			{
				mViewHolder.mPhotoImageView.setBackgroundResource(R.drawable.outgoing_call);
				mViewHolder.mStateImageView.setBackgroundResource(R.drawable.outgoing_state);
			}
			break;
			case CallLog.Calls.MISSED_TYPE:
			{
				mViewHolder.mPhotoImageView.setBackgroundResource(R.drawable.missed_call);
				mViewHolder.mStateImageView.setBackgroundResource(R.drawable.missed_state);
			}
			break;
		}
		String phoneNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
		mViewHolder.mNumberTextView.setText(phoneNumber);
		String name = null;
		Cursor cu = mContext.getContentResolver().query(  
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,  
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME},   
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + phoneNumber + "'",  
                null, null); 
		if(null != cu && 0 < cu.getCount())
		{
			cu.moveToNext();
			name = cu.getString(cu.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
		}
		else
		{
			name = mContext.getString(R.string.unknown);
		}
		cu.close();
		cu = null;
		mViewHolder.mNameTextView.setText(name);
		
		long date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(date);
		String d = c.get(Calendar.YEAR) + mContext.getString(R.string.date_div)
		+ ((c.get(Calendar.MONTH) + 1) < 10 ? ("0" + (c.get(Calendar.MONTH) + 1)) : (c.get(Calendar.MONTH) + 1))
		+ mContext.getString(R.string.date_div) + (c.get(Calendar.DATE) < 10 ? ("0" + c.get(Calendar.DATE)) : c.get(Calendar.DATE)) 
		+ " " + mWeekArray[c.get(Calendar.DAY_OF_WEEK) - 1];
		mViewHolder.mDateTextView.setText(d);
		String t = (c.get(Calendar.HOUR_OF_DAY) < 10 ? ("0" + c.get(Calendar.HOUR_OF_DAY)) : c.get(Calendar.HOUR_OF_DAY))
		+ mContext.getString(R.string.time_div) + (c.get(Calendar.MINUTE) < 10 ? ("0" + c.get(Calendar.MINUTE)) : c.get(Calendar.MINUTE));
		mViewHolder.mTimeTextView.setText(t);
		long duration = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION));
		int hour = (int)(duration / 3600);
		int minute = (int)((duration - hour * 3600) / 60);
		int second = (int)(duration - hour * 3600 - minute * 60);
		String dr = mContext.getString(R.string.duration) + "  " + (hour > 0 ? (hour + mContext.getString(R.string.time_div)) : "") 
		+ (minute < 10 ? ("0" + minute) : minute) + mContext.getString(R.string.time_div) + (second < 10 ? ("0" + second) : second);
		mViewHolder.mDurationTexView.setText(dr);
		mViewHolder.mCallImageView.setOnClickListener(new OnResListener(phoneNumber, TYPE_CALL));
		mViewHolder.mMsgImageView.setOnClickListener(new OnResListener(phoneNumber, TYPE_MSG));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = LayoutInflater.from(mContext).inflate(R.layout.list_item_record, null);;
		ViewHolder mViewHolder = new ViewHolder();
		//if(null == mViewHolder)
		//{
			mViewHolder = new ViewHolder();
			mViewHolder.mPhotoImageView = (ImageView)v.findViewById(R.id.imageview_photo);
			mViewHolder.mStateImageView = (ImageView)v.findViewById(R.id.imageview_state);
			mViewHolder.mNameTextView = (TextView)v.findViewById(R.id.textview_name);
			mViewHolder.mNumberTextView = (TextView)v.findViewById(R.id.textview_number);
			mViewHolder.mDateTextView = (TextView)v.findViewById(R.id.textview_date);
			mViewHolder.mTimeTextView = (TextView)v.findViewById(R.id.textview_time);
			mViewHolder.mDurationTexView = (TextView)v.findViewById(R.id.textview_duration);
			mViewHolder.mMsgImageView = (ImageView)v.findViewById(R.id.imageview_msg);
			mViewHolder.mCallImageView = (ImageView)v.findViewById(R.id.imageview_call);
			v.setTag(mViewHolder);
		//}
    	return v;
	}
	
	private static class ViewHolder
	{
		ImageView mPhotoImageView;
		ImageView mStateImageView;
		TextView mNameTextView;
		TextView mNumberTextView;
		TextView mDateTextView;
		TextView mTimeTextView;
		TextView mDurationTexView;
		ImageView mMsgImageView;
		ImageView mCallImageView;
	}
	
	private class OnResListener implements OnClickListener
	{
		private String mNumber;
		private int mType;
		public OnResListener(String phoneNumber, int type)
		{
			mNumber = phoneNumber;
			mType = type;
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(mType)
			{
				case TYPE_CALL:
				{
					Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + mNumber));
					mContext.startActivity(intent);
				}
				break;
				case TYPE_MSG:
				{
					Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + mNumber));
					mContext.startActivity(intent);
				}
				break;
			}
		}
	}
	
	public void updateAdapter(Cursor c)
	{
		this.changeCursor(c);
		this.notifyDataSetChanged();
	}
}