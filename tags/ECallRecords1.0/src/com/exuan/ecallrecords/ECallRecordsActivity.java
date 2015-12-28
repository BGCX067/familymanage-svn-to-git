package com.exuan.ecallrecords;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;

public class ECallRecordsActivity extends Activity {
	
	private static final String TAG = "ECallRecordsActivity";
	private Context mContext;
	private static final int MENU_DELETE = 0;
	private static final int MENU_RECOMMEND = 1;
	private static final int MENU_ABOUT = 2;
	private static final int REQUEST_CODE_RECOMMEND = 1;
	private static final int DIALOG_DELETE = 0;
	private static final int DIALOG_STORE = 1;
	private static final int DIALOG_VIEW = 2;
	private static final int DIALOG_STORE_OPTION = 3;
	private static final int DIALOG_OPTION = 4;
	private static final int ALL_TYPE = CallLog.Calls.MISSED_TYPE + 1;
	private int mCallType = ALL_TYPE;
	private Cursor mRecordCursor;
	private ListView mRecordListView;
	private RecordsAdapter mRecordAdapter;
	private TextView mMissedTextView;
	private TextView mComeTextView;
	private TextView mOutTextView;
	private TextView mAllTextView;
	private int mPosition;
	private String[] mOperation;
	private String mTitle;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = this;
        mMissedTextView = (TextView)findViewById(R.id.button_missed);
        mComeTextView = (TextView)findViewById(R.id.button_incoming);
        mOutTextView = (TextView)findViewById(R.id.button_outgoing);
        mAllTextView = (TextView)findViewById(R.id.button_all);
        mRecordListView = (ListView)findViewById(R.id.listview_record);
        mRecordCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.TYPE + " = '" + mCallType + "'", null, CallLog.Calls.DEFAULT_SORT_ORDER);
        mRecordAdapter = new RecordsAdapter(mContext, mRecordCursor);
        mRecordListView.setAdapter(mRecordAdapter);
        mRecordListView.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				mPosition = arg2;
				mRecordCursor.moveToPosition(mPosition);
				String phoneNumber = mRecordCursor.getString(mRecordCursor.getColumnIndex(CallLog.Calls.NUMBER));
				mTitle = null;
				Cursor cu = mContext.getContentResolver().query(  
		                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,  
		                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME},   
		                ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + phoneNumber + "'",  
		                null, null); 
				if(cu != null && 0 < cu.getCount())
				{
					cu.moveToNext();
					mTitle = cu.getString(cu.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
					mOperation = new String[]{mContext.getString(R.string.call),
							mContext.getString(R.string.msg),
							mContext.getString(R.string.view),
							mContext.getString(R.string.delete_record)};
					cu.close();
					cu = null;
					removeDialog(DIALOG_VIEW);
					showDialog(DIALOG_VIEW);
				}
				else
				{
					mTitle = phoneNumber;
					mOperation = new String[]{mContext.getString(R.string.call),
							mContext.getString(R.string.msg),
							mContext.getString(R.string.store),
							mContext.getString(R.string.delete_record)};
					removeDialog(DIALOG_STORE);
					showDialog(DIALOG_STORE);
				}
				return true;
			}});
        mMissedTextView.setOnClickListener(new BtClickListener(CallLog.Calls.MISSED_TYPE));
        mComeTextView.setOnClickListener(new BtClickListener(CallLog.Calls.INCOMING_TYPE));
        mOutTextView.setOnClickListener(new BtClickListener(CallLog.Calls.OUTGOING_TYPE));
        mAllTextView.setOnClickListener(new BtClickListener(ALL_TYPE));
    }
    
    public void onResume()
    {
    	super.onResume();
    	updateView(mCallType);
    }
    
    private class BtClickListener implements OnClickListener
    {
    	private int mType;
    	public BtClickListener(int type)
    	{
    		mType = type;
    	}
    	
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.d(TAG, "type:" + mType);
			mCallType = mType;
			updateView(mType);
		}
    };
    
    private void updateView(int type)
    {
    	switch(type)
    	{
	    	case CallLog.Calls.MISSED_TYPE:
	    	{
	    		mMissedTextView.setBackgroundResource(R.drawable.btn_press);
	    		mComeTextView.setBackgroundColor(Color.TRANSPARENT);
	    		mOutTextView.setBackgroundColor(Color.TRANSPARENT);
	    		mAllTextView.setBackgroundColor(Color.TRANSPARENT);
	    	}
	    	break;
	    	case CallLog.Calls.INCOMING_TYPE:
	    	{
	    		mMissedTextView.setBackgroundColor(Color.TRANSPARENT);
	    		mComeTextView.setBackgroundResource(R.drawable.btn_press);
	    		mOutTextView.setBackgroundColor(Color.TRANSPARENT);
	    		mAllTextView.setBackgroundColor(Color.TRANSPARENT);
	    	}
	    	break;
	    	case CallLog.Calls.OUTGOING_TYPE:
	    	{
	    		mMissedTextView.setBackgroundColor(Color.TRANSPARENT);
	    		mComeTextView.setBackgroundColor(Color.TRANSPARENT);
	    		mOutTextView.setBackgroundResource(R.drawable.btn_press);
	    		mAllTextView.setBackgroundColor(Color.TRANSPARENT);
	    	}
	    	break;
	    	case ALL_TYPE:
	    	{
	    		mMissedTextView.setBackgroundColor(Color.TRANSPARENT);
	    		mComeTextView.setBackgroundColor(Color.TRANSPARENT);
	    		mOutTextView.setBackgroundColor(Color.TRANSPARENT);
	    		mAllTextView.setBackgroundResource(R.drawable.btn_press);
	    	}
	    	break;
    	}
    	if(ALL_TYPE == type)
		{
			mRecordCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
		}
		else
		{
			mRecordCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.TYPE + " = '" + type + "'", null, CallLog.Calls.DEFAULT_SORT_ORDER);
		}
		mRecordAdapter.updateAdapter(mRecordCursor);
    }
    
    public void onDestroy()
    {
    	super.onDestroy();
    	mRecordCursor.close();
    	mRecordCursor = null;
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_DELETE, 0, R.string.delete).setIcon(
				R.drawable.ic_menu_delete);
		menu.add(0, MENU_RECOMMEND, 0, R.string.share).setIcon(
				R.drawable.menu_share);
		menu.add(0, MENU_ABOUT, 0, R.string.about).setIcon(
				R.drawable.menu_about);
		return super.onCreateOptionsMenu(menu);
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case MENU_DELETE:
			{
				removeDialog(DIALOG_DELETE);
				showDialog(DIALOG_DELETE);
				return true;
			}
			case MENU_RECOMMEND:
			{
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setType("text/*");
				intent.putExtra("android.intent.extra.SUBJECT", getString(R.string.app_name));
				intent.putExtra("android.intent.extra.TEXT", getString(R.string.share_content));
				Intent i = Intent.createChooser(intent, getString(R.string.share_way));
				startActivityForResult(i, REQUEST_CODE_RECOMMEND);
				return true;
			}
			case MENU_ABOUT:
			{
				Intent i = new Intent(mContext, EAboutActivity.class);
				startActivity(i);
				return true;
			}
		}
		return false;
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
		switch(id)
		{
			case DIALOG_OPTION:
			{
				return new AlertDialog.Builder(this)
				.setTitle(R.string.delete)
				.setItems(R.array.delete_array, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog,
								int whichButton) {
							switch(whichButton)
							{
								case 0:
								{
									long date = mRecordCursor.getLong(mRecordCursor.getColumnIndex(CallLog.Calls.DATE));
									getContentResolver().delete(CallLog.Calls.CONTENT_URI, CallLog.Calls.DATE + " = '" + date + "'", null);
								}
								break;
								case 1:
								{
									String where = null;
									if(ALL_TYPE != mCallType)
									{
										where = " AND " + CallLog.Calls.TYPE + " = '" + mCallType + "'";
									}
									String number = mRecordCursor.getString(mRecordCursor.getColumnIndex(CallLog.Calls.NUMBER));
									getContentResolver().delete(CallLog.Calls.CONTENT_URI, CallLog.Calls.NUMBER + " = '" + number + "'" + where, null);
								}
								break;
							}
						}
					})
				.create();		
			}
			case DIALOG_DELETE:
			{
				return new AlertDialog.Builder(this)
				.setTitle(R.string.delete_confirm)
				.setMessage(R.string.delete_info)
				.setPositiveButton(R.string.ok,
						  new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							if(ALL_TYPE == mCallType)
							{
								getContentResolver().delete(CallLog.Calls.CONTENT_URI, null, null);
							}
							else
							{
								getContentResolver().delete(CallLog.Calls.CONTENT_URI, CallLog.Calls.TYPE + " = '" + mCallType + "'", null);
							}
						}
					})
					.setNegativeButton(R.string.cancel,
						  new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							
						}
					})
			   .create();
			}
			case DIALOG_VIEW:
			{
				return new AlertDialog.Builder(this)
				.setTitle(mTitle)
				.setItems(mOperation, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog,
								int whichButton) {
							mRecordCursor.moveToPosition(mPosition);
							String phoneNumber = mRecordCursor.getString(mRecordCursor.getColumnIndex(CallLog.Calls.NUMBER));
							switch(whichButton)
							{
								case 0:
								{
									Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + phoneNumber));
									mContext.startActivity(intent);
								}
								break;
								case 1:
								{
									Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + phoneNumber));
									mContext.startActivity(intent);
								}
								break;
								case 2:
								{
									Log.v(TAG, "name:" + mTitle);
									Cursor cu = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, 
											ContactsContract.Contacts.DISPLAY_NAME + " = '" + mTitle + "'", null, 
											ContactsContract.Contacts.DISPLAY_NAME + " COLLATE NOCASE");
									if(cu != null && 0 < cu.getCount())
									{
										cu.moveToNext();
										long id = cu.getLong(cu.getColumnIndex(ContactsContract.Contacts._ID));
										String lookup_key = cu.getString(cu.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
										cu.close();
										cu = null;
										Uri uri = ContactsContract.Contacts.getLookupUri(id, lookup_key);
										Log.e(TAG, "uri:" + uri);
										Intent intent = new Intent(Intent.ACTION_VIEW);
										intent.setData(uri);
										mContext.startActivity(intent);
									}
								}
								break;
								case 3:
								{
									removeDialog(DIALOG_OPTION);
									showDialog(DIALOG_OPTION);
								}
								break;
							}
						}
					})
				.create();		
			}
			case DIALOG_STORE:
			{
				return new AlertDialog.Builder(this)
				.setTitle(mTitle)
				.setItems(mOperation, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog,
								int whichButton) {
							mRecordCursor.moveToPosition(mPosition);
							String phoneNumber = mRecordCursor.getString(mRecordCursor.getColumnIndex(CallLog.Calls.NUMBER));
							switch(whichButton)
							{
								case 0:
								{
									Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + phoneNumber));
									mContext.startActivity(intent);
								}
								break;
								case 1:
								{
									Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + phoneNumber));
									mContext.startActivity(intent);
								}
								break;
								case 2:
								{
									removeDialog(DIALOG_STORE_OPTION);
									showDialog(DIALOG_STORE_OPTION);
								}
								break;
								case 3:
								{
									removeDialog(DIALOG_OPTION);
									showDialog(DIALOG_OPTION);
								}
								break;
							}
						}
					})
				.create();		
			}
			case DIALOG_STORE_OPTION:
			{
				return new AlertDialog.Builder(this)
				.setTitle(mTitle)
				.setItems(R.array.store_array, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog,
								int whichButton) {
							switch(whichButton)
							{
								case 0:
								{
									Intent intent = new Intent(Intent.ACTION_INSERT);
									intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
									intent.putExtra(ContactsContract.Intents.Insert.PHONE, mTitle);
									intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
									mContext.startActivity(intent);
								}
								break;
								case 1:
								{
									Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
									intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
									intent.putExtra(ContactsContract.Intents.Insert.PHONE, mTitle);
									intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
									mContext.startActivity(intent);
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
}