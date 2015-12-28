package com.haolianluo.sms2.ui.sms2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import com.haolianluo.sms2.R;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class HBackupActivity extends SkinActivity
{
	private ProgressDialog mProgressDialog;
	private Context mContext;
	private static final String[] COLUMN = {"address", "read", "status", "type", "service_center", "body", "date"};
	private static final int MSG_BACKUP = 0;
	private static final int MSG_RESTORE = 1;
	private static final int MSG_FAIL = 2;
	private static final int MSG_MESSAGE = 3;
	private int mTotalCount;
	private TextView mLastBackupTextView;
	private TextView mLastTimeTextView;
	private String mPath = Environment.getExternalStorageDirectory() + File.separator + "lianluosms2" + File.separator + "backup";
	private Cursor mCursorSms;
	private Cursor mCursorCollect;
	private ContentObserver mMessageObserver;
	private int mSmsCount = 0;
	private int mCollectCount = 0;
	private TextView mSmsTextView;
	private TextView mTotalTextView;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_backup);
		mContext = this;
		mMessageObserver = new MessageObserver(mHandler);
		getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, mMessageObserver);  
		mTotalTextView = (TextView)findViewById(R.id.textview_total_msg);
		mSmsTextView = (TextView)findViewById(R.id.textview_in_msg);
		TextView tv_collect = (TextView)findViewById(R.id.textview_collect_msg);
		mTotalCount = 0;
		mCursorSms = getContentResolver().query(Uri.parse("content://sms"), COLUMN, null, null, null);
		if(null != mCursorSms && 0 < mCursorSms.getCount())
		{
			mSmsCount = mCursorSms.getCount();
		}
		mSmsTextView.setText(getString(R.string.inbox) + mSmsCount + getString(R.string.piece));
		mTotalCount += mSmsCount;
		mCursorCollect = getContentResolver().query(Uri.parse("content://com.haolianluo.sms2.collect-sms"), COLUMN, null, null, null);
		if(null != mCursorCollect && 0 < mCursorCollect.getCount())
		{
			mCollectCount = mCursorCollect.getCount();
		}
		tv_collect.setText(getString(R.string.collectbox) + mCollectCount + getString(R.string.piece));
		mTotalCount += mCollectCount;
		if(0 < mTotalCount)
		{
			mTotalTextView.setText("" + mTotalCount);
		}
		final SharedPreferences pref = mContext.getSharedPreferences("backup", Context.MODE_PRIVATE);
		mLastBackupTextView = (TextView)findViewById(R.id.textview_last_back_count);
		int last_count = pref.getInt("last_backup", 0);
		if(0 < last_count)
		{
			mLastBackupTextView.setText("" + last_count);
		}
		mLastTimeTextView = (TextView)findViewById(R.id.textview_backup_time);
		long time = pref.getLong("last_time", 0);
		if(0 < time)
		{
			Date date = new Date(time);
			mLastTimeTextView.setText((1900 + date.getYear()) + "/" + (1 + date.getMonth()) + "/" + 
					date.getDate() + " " + date.getHours() + ":" + date.getMinutes());
		}
		Button bt_backup = (Button)findViewById(R.id.button_backup);
		Button bt_restore = (Button)findViewById(R.id.button_restore);
		bt_backup.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(0 >= mTotalCount)
				{
					Toast.makeText(mContext, mContext.getString(R.string.backup_empty), Toast.LENGTH_LONG).show();
					return;
				}
				View layout = LayoutInflater.from(mContext).inflate(R.layout.dialog_backup, null);
				Button bt_ok = (Button)layout.findViewById(R.id.button_ok);
				Button bt_cancel = (Button)layout.findViewById(R.id.button_cancel);
				final Dialog dialog = new Dialog(mContext, R.style.about_style);
				dialog.setContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				bt_cancel.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}});
				bt_ok.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						if(null != mProgressDialog)
						{
							if(mProgressDialog.isShowing())
							{
								mProgressDialog.dismiss();
							}
							mProgressDialog = null;
						}
						mProgressDialog = new ProgressDialog(mContext);
						mProgressDialog.setMessage(mContext.getString(R.string.backuping));
						mProgressDialog.show();
						mProgressDialog.setCancelable(false);
						new Thread(){
							public void run()
							{
								try {
									File dir = new File(mPath);
									if(!dir.exists())
									{
										dir.mkdirs();
									}
									XmlSerializer serializer = Xml.newSerializer();
									StringWriter writer = new StringWriter();
									serializer.setOutput(writer);
									serializer.startDocument("UTF-8", true);
									serializer.startTag("", "msg");
									serializer.startTag("", "smss");
									if(null != mCursorSms && 0 < mCursorSms.getCount())
									{
										for(int i = 0; i < mCursorSms.getCount(); i++)
										{
											mCursorSms.moveToPosition(i);
											serializer.startTag("", "sms");
											for(int j = 0; j < mCursorSms.getColumnCount(); j++)
											{
												serializer.attribute("", COLUMN[j], mCursorSms.getString(j) == null ? "" : mCursorSms.getString(j));
											}
											serializer.endTag("", "sms");
										}
									}
									serializer.endTag("", "smss");
									serializer.startTag("", "collect");
									if(null != mCursorCollect && 0 < mCursorCollect.getCount())
									{
										for(int i = 0; i < mCursorCollect.getCount(); i++)
										{
											mCursorCollect.moveToPosition(i);
											serializer.startTag("", "sms");
											for(int j = 0; j < mCursorCollect.getColumnCount(); j++)
											{
												serializer.attribute("", COLUMN[j], mCursorCollect.getString(j) == null ? "" : mCursorCollect.getString(j));
											}
											serializer.endTag("", "sms");
										}
									}
									serializer.endTag("", "collect");
									serializer.endTag("", "msg");
									serializer.endDocument();
									File f = new File(mPath + File.separator + "smsback.xml");
									if(f.exists())
									{
										f.delete();
									}
									f.createNewFile();
									FileOutputStream out = new FileOutputStream(f);
									out.write(writer.toString().getBytes());
									out.close();
									pref.edit().putInt("last_backup", mTotalCount).commit();
									pref.edit().putLong("last_time", System.currentTimeMillis()).commit();
									Message msg = new Message();
									msg.what = MSG_BACKUP;
									mHandler.sendMessage(msg);
								} catch (IllegalArgumentException e) {
									// TODO Auto-generated catch block
									Message msg = new Message();
									msg.what = MSG_FAIL;
									mHandler.sendMessage(msg);
									e.printStackTrace();
								} catch (IllegalStateException e) {
									// TODO Auto-generated catch block
									Message msg = new Message();
									msg.what = MSG_FAIL;
									mHandler.sendMessage(msg);
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									Message msg = new Message();
									msg.what = MSG_FAIL;
									mHandler.sendMessage(msg);
									e.printStackTrace();
								}	
							}
						}.start();
					}});
				dialog.show();
			}});
		bt_restore.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File f = new File(mPath + File.separator + "smsback.xml");
				if(!f.exists())
				{
					Toast.makeText(mContext, mContext.getString(R.string.no_backup), Toast.LENGTH_LONG).show();
					return;
				}
				if(null != mProgressDialog)
				{
					if(mProgressDialog.isShowing())
					{
						mProgressDialog.dismiss();
					}
					mProgressDialog = null;
				}
				mProgressDialog = new ProgressDialog(mContext);
				mProgressDialog.setMessage(mContext.getString(R.string.restoring));
				mProgressDialog.show();
				mProgressDialog.setCancelable(false);
				new Thread(){
					public void run()
					{
				         try {
				        	 FileInputStream in = new FileInputStream(mPath + File.separator + "smsback.xml");
				             XmlPullParser parser = Xml.newPullParser();
				             parser.setInput(in, "UTF-8");
				             int eventType = parser.getEventType();
				             String uri = "";
				             while(eventType != XmlPullParser.END_DOCUMENT)
				             {
				                 switch(eventType)
				                 {
				                     case XmlPullParser.START_TAG:
				                     {
				                         String tag = parser.getName();
				                         if(tag.equalsIgnoreCase("smss"))
				                         {
				                        	 uri = "content://sms";
				                         }
				                         if(tag.equalsIgnoreCase("collect"))
				                         {
				                        	 uri = "content://com.haolianluo.sms2.collect-sms";
				                         }
				                         if(tag.equalsIgnoreCase("sms"))
				                         {
				                        	 ContentValues values = new ContentValues();
				                        	 for(int i = 0; i < COLUMN.length; i++)
				                         	 {
				                         		 values.put(COLUMN[i], parser.getAttributeValue("", COLUMN[i]));
				                         	 }
				                         	 Cursor c = getContentResolver().query(Uri.parse(uri), null, 
				                         			COLUMN[0] + " = '" + values.get(COLUMN[0]) + "'" + " AND " +
				                         			COLUMN[5] + " = '" + values.get(COLUMN[5]) + "'" + " AND " +
				                         			COLUMN[6] + " = '" + values.get(COLUMN[6]) + "'"
				                         			, null, null);
				                         	 if(null == c || 0 >= c.getCount())
				                         	 {
				                         		 getContentResolver().insert(Uri.parse(uri), values);
				                         	 }
				                         	 c.close();
				                         }
				                     }
				                     break;
				                 }
				                 eventType = parser.next();
				             }
				             in.close();
				             
				             Message msg = new Message();
							 msg.what = MSG_RESTORE;
							 mHandler.sendMessage(msg);
				         } catch (XmlPullParserException e) {
				             // TODO Auto-generated catch block
				             e.printStackTrace();
				             Message msg = new Message();
								msg.what = MSG_FAIL;
								mHandler.sendMessage(msg);
				         } catch (IOException e) {
				             // TODO Auto-generated catch block
				             e.printStackTrace();
				             Message msg = new Message();
								msg.what = MSG_FAIL;
								mHandler.sendMessage(msg);
				         }
					}
				}.start();
			}});
	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg)
		{
			if(msg.what != MSG_MESSAGE)
			{
				if(null != mProgressDialog)
				{
					if(mProgressDialog.isShowing())
					{
						mProgressDialog.dismiss();
					}
					mProgressDialog = null;
				}
			}
			switch(msg.what)
			{
				case MSG_BACKUP:
				{
					SharedPreferences pref = mContext.getSharedPreferences("backup", Context.MODE_PRIVATE);
					int last_count = pref.getInt("last_backup", 0);
					if(0 < last_count)
					{
						mLastBackupTextView.setText("" + last_count);
					}
					long time = pref.getLong("last_time", 0);
					if(0 < time)
					{
						Date date = new Date(time);
						mLastTimeTextView.setText((1900 + date.getYear()) + "/" + (1 + date.getMonth()) + "/" + 
						date.getDate() + " " + date.getHours() + ":" + date.getMinutes());
					}
					Toast.makeText(mContext, getString(R.string.backup_success), Toast.LENGTH_LONG).show();
				}
				break;
				case MSG_RESTORE:
				{
					Toast.makeText(mContext, getString(R.string.restore_success), Toast.LENGTH_LONG).show();
				}
				break;
				case MSG_FAIL:
				{
					Toast.makeText(mContext, getString(R.string.operation_fail), Toast.LENGTH_LONG).show();
				}
				break;
				case MSG_MESSAGE:
				{
					mCursorSms = getContentResolver().query(Uri.parse("content://sms"), COLUMN, null, null, null);
					if(null != mCursorSms)
					{
						mSmsCount = mCursorSms.getCount();
						mSmsTextView.setText(getString(R.string.inbox) + mSmsCount + getString(R.string.piece));
						mTotalCount = mSmsCount + mCollectCount;
						mTotalTextView.setText("" + mTotalCount);
					}
				}
				break;
			}
		}
	};
	
	private class MessageObserver extends ContentObserver
	{
		private Handler mHandler;
		public MessageObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
			mHandler = handler;
		}
		
		public void onChange(boolean selfChange){  
			Message msg = new Message();
			msg.what = MSG_MESSAGE;
			mHandler.sendMessage(msg);
		}
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		mCursorSms.close();
		mCursorCollect.close();
		getContentResolver().unregisterContentObserver(mMessageObserver);
	}
}