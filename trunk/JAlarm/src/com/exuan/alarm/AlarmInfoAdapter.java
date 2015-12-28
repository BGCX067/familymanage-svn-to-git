package com.exuan.alarm;

import java.util.ArrayList;
import java.util.List;

import com.exuan.alarm.EAlarmActivity;
import com.exuan.alarm.ESetAlarmActivity;
import com.exuan.alarm.R;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlarmInfoAdapter extends BaseAdapter
{
	private Context mContext;
	private String[] mWeekArray;
	private List<AlarmData> mList = new ArrayList<AlarmData>();
	private static final String[] mRepeatDigit = {"0000000", "1111111", "1111100", "0000011"};
    private String[] mRepeatArray;
	
	public AlarmInfoAdapter(Context context)
	{
		mContext = context;
		mWeekArray = mContext.getResources().getStringArray(R.array.week_short_array);
		mRepeatArray = mContext.getResources().getStringArray(R.array.repeat_array);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View v;
		if(null == arg1)
		{
			v = LayoutInflater.from(mContext).inflate(R.layout.list_alarm_item, null);
		}
		else
		{
			v = arg1;
		}
		LinearLayout mTimeAreaLinearLayout = (LinearLayout)v.findViewById(R.id.linearlayout_time);
		TextView mTimeText = (TextView)v.findViewById(R.id.textview_time);
		TextView mRepeatText = (TextView)v.findViewById(R.id.textview_repeat);
		Button mAliveButton = (Button)v.findViewById(R.id.button_on_off);
		Button mDeleteButton = (Button)v.findViewById(R.id.button_delete_alarm);
		
		AlarmData data = mList.get(arg0);
		//set time
		String time_text = (data.mHour < 10 ? ("0" + Integer.toString(data.mHour)) : Integer.toString(data.mHour))
		+ ":" + (data.mMinute < 10 ? ("0" + Integer.toString(data.mMinute)) : Integer.toString(data.mMinute));
		mTimeText.setText(time_text);
		
		//set repeat
		StringBuilder ret = new StringBuilder();
		for(int i = 0; i < mRepeatDigit.length; i++)
    	{
    		if(data.mRepeat.equals(mRepeatDigit[i]))
    		{
    			ret.append(mRepeatArray[i]);
    		}
    	}
    	if(ret.length() <= 0)
    	{
    		for(int i = 0; i < data.mRepeat.length(); i++)
    		{
    			char c  = data.mRepeat.charAt(i);
    			if('1' == c)
    			{
    				ret.append(mWeekArray[i]);
    				ret.append(", ");
    			}
    		}
    		ret.delete(ret.lastIndexOf(","), ret.length() - 1);
    	}
    	mRepeatText.setText(ret.toString());
    	
    	//set checked or not
    	if(data.mIsActive)
    	{
    		mAliveButton.setBackgroundResource(R.drawable.check_on_button);
    	}
    	else
    	{
    		mAliveButton.setBackgroundResource(R.drawable.check_off_button);
    	}
    	
    	//set click listener
    	mDeleteButton.setOnClickListener(new OnDeleteListener(data.mId));
		mAliveButton.setOnClickListener(new OnCheckListener(data.mId));
    	mTimeAreaLinearLayout.setOnClickListener(new OnTextClickListener(data.mId));
    	
		return v;
	}
	
	class OnDeleteListener implements OnClickListener
	{
		private int mId;
		public OnDeleteListener(int id)
		{
			mId = id;
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Message msg = new Message();
			msg.arg1 = mId;
			msg.what = EAlarmActivity.DELETE_ALARM_DIALOG_ID;
			EAlarmActivity.mHander.sendMessage(msg);
		}
	}
	
	class OnCheckListener implements OnClickListener
	{
		private int mId;
		//private String mText;
		public OnCheckListener(int id)
		{
			mId = id;
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Message msg = new Message();
			msg.arg1 = mId;
			msg.what = EAlarmActivity.ENABLE_ALARM;
			EAlarmActivity.mHander.sendMessage(msg);
		}
	}
	
	class OnTextClickListener implements OnClickListener
	{
		private int mId;
		
		public OnTextClickListener(int id)
		{
			mId = id;
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(mContext, ESetAlarmActivity.class);
			intent.putExtra("isNew", false);
			intent.putExtra("_id", mId);
			mContext.startActivity(intent);
		}
		
	}
	
	public void updateAdapter(Cursor cursor)
	{
		if(null == cursor)
		{
			return;
		}
		mList.clear();
		for(int i = 0; i < cursor.getCount(); i++)
		{
			cursor.moveToNext();
			AlarmData data= new AlarmData();
			data.mId = cursor.getInt(cursor.getColumnIndex("_id"));
			data.mHour = cursor.getInt(cursor.getColumnIndex(AlarmDatabaseHelper.HOUR));
			data.mMinute = cursor.getInt(cursor.getColumnIndex(AlarmDatabaseHelper.MINUTE));
			data.mRepeat = cursor.getString(cursor.getColumnIndex(AlarmDatabaseHelper.REPEAT));
			data.mIsActive = ((cursor.getInt(cursor.getColumnIndex(AlarmDatabaseHelper.IS_ACTIVE))) == 1);
			mList.add(data);
		}
		notifyDataSetChanged();
	}
	
}

class AlarmData
{
	public int mId;
	public int mHour;
	public int mMinute;
	public String mRepeat;
	public boolean mIsActive;
}