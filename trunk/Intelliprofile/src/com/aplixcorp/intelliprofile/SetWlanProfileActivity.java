package com.aplixcorp.intelliprofile;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SetWlanProfileActivity extends Activity
{
	private Context mContext;
	private TextView mNameTextView;
	private TextView mAddressTextView;
	private TextView mSignalTextView;
	private TextView mRssiTextView;
	private LinearLayout mSignalLinearLayout;
	private TextView mProfileTextView;
	private LinearLayout mProfileLinearLayout;
	private LinearLayout mNumberLinearLayout;
	private TextView mBlockNumberTextView;
	private Button mStoreButton;
	private Button mCancelButton;
	private CheckBox mEnabledProfile;
	private Button mDeleteCfgButton;	
	
	private String[] mStrengthArray;
	private int mSignalStrength;
	
	private int mForwardId;
	private int mSelectNumberId;
	private int mContactId;
	
	private static final int[] mSignalValues = {-60, -110};
	
	private static final int SELECT_FORWARD_DIALOG_ID = 0;
	private static final int SELECT_KIND_DIALOG_ID = 1;
	private static final int EDIT_NUMBER_DIALOG_ID = 2;
	private static final int CONTACT_NUMBER_DIALOG_ID = 3;
	private static final int SIGNAL_STRENGTH_DIALOG_ID = 4;
	
	private String[] mForwardArray;
	
	private String mDeviceName;
	private String mDeviceAddress;
	private String mBlockNumber;
	private boolean mEnabled;
	
	private static final String[] mForwardNumber = {"tel:%23%2367%23", "tel:**67*13800000000%23", "tel:**67*13810538911%23", "tel:**67*13701110216%23"};
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_set_wlan);
        mContext = this;
        Bundle extras = getIntent().getExtras();
        
        mDeviceName = extras.getString(ProfileDatabaseHelper.WLAN_NAME);
        mDeviceAddress = extras.getString(ProfileDatabaseHelper.WLAN_MAC);
        mSignalStrength = extras.getInt(ProfileDatabaseHelper.WLAN_RSSI);
        mForwardId = extras.getInt(ProfileDatabaseHelper.PROFILE_VALUE);
        mBlockNumber = extras.getString(ProfileDatabaseHelper.WLAN_PHONE_NUMBER);
        mEnabled  = (extras.getString(ProfileDatabaseHelper.ENABLED)).equals("1") ? true : false;
        
        mForwardArray = mContext.getResources().getStringArray(R.array.forward);
        mStrengthArray = mContext.getResources().getStringArray(R.array.strength);
        
        mNameTextView = (TextView)findViewById(R.id.wlan_name_textview);
        mAddressTextView = (TextView)findViewById(R.id.wlan_address_textview);
        mProfileTextView = (TextView)findViewById(R.id.wlan_profile_textview);
        mBlockNumberTextView = (TextView)findViewById(R.id.block_number_textview);
        mSignalTextView = (TextView)findViewById(R.id.wlan_signal_textview);
        mRssiTextView = (TextView)findViewById(R.id.wlan_signal_strength_textview);
        
        mProfileLinearLayout = (LinearLayout)findViewById(R.id.wlan_profile_linearlayout);
        mSignalLinearLayout = (LinearLayout)findViewById(R.id.wlan_signal_linearlayout);
        mNumberLinearLayout = (LinearLayout)findViewById(R.id.block_number_linearlayout);
        mStoreButton = (Button)findViewById(R.id.wlan_store_button);
        mCancelButton = (Button)findViewById(R.id.wlan_cancel_button);
        mEnabledProfile = (CheckBox)findViewById(R.id.WlanEnable);
        mDeleteCfgButton = (Button)findViewById(R.id.wlan_DeleteConfig_button);
        mEnabledProfile.setChecked(mEnabled);
        mEnabledProfile.setText(mEnabled?R.string.config_enable:R.string.config_disable);
        mBlockNumberTextView.setText(mBlockNumber);
        
        mNameTextView.setText(getString(R.string.device_name) + ": " + mDeviceName);
        mAddressTextView.setText(getString(R.string.device_address) + ": " + mDeviceAddress);
        mProfileTextView.setText(mForwardArray[mForwardId]);
        
        String mCurrentRssi = extras.getString("currentRssi");
        if(mCurrentRssi != null){
        	mRssiTextView.setText(getString(R.string.signal_strength) + ": " +mCurrentRssi);
        }
        mSignalTextView.setText(mStrengthArray[mSignalStrength]);
        
        Cursor c = getContentResolver().query(
        		ProfileInfoProvider.CONTENT_URI_WLAN_PROFILES, 
        		null, 
        		ProfileDatabaseHelper.WLAN_MAC + " = '" + mDeviceAddress +"' ", 
        		null, 
        		null);
        if((c == null) || (c.getCount() == 0))
        {
        	mDeleteCfgButton.setClickable(false);
        	mDeleteCfgButton.setEnabled(false);
        }
        
        mProfileLinearLayout.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			showDialog(SELECT_FORWARD_DIALOG_ID);
		}});
        
        mSignalLinearLayout.setOnClickListener(new OnClickListener(){

    		@Override
    		public void onClick(View v) {
    			// TODO Auto-generated method stub
    			showDialog(SIGNAL_STRENGTH_DIALOG_ID);
    		}});
        
        mNumberLinearLayout.setOnClickListener(new OnClickListener(){

    		@Override
    		public void onClick(View v) {
    			// TODO Auto-generated method stub
    			showDialog(SELECT_KIND_DIALOG_ID);
    		}});

        mEnabledProfile.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {   
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1){
					mEnabledProfile.setText(R.string.config_enable);
					mEnabled = true;
				}else{
					mEnabledProfile.setText(R.string.config_disable);
					mEnabled = false;
				}
			}
        });
        mStoreButton.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			storeProfile();
			finish();
			return;
		}});
        
        mCancelButton.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
			return;
		}});
        
        mDeleteCfgButton.setOnClickListener(new OnClickListener(){

    		@Override
    		public void onClick(View v) {
    			// TODO Auto-generated method stub
    			//update the context value in database
    			mContext.getContentResolver().delete(
    					ProfileInfoProvider.CONTENT_URI_WLAN_PROFILES,
    					ProfileInfoProvider.WLAN_MAC + " = '" + mDeviceAddress +"'", null);	
    			finish();
    			return;
    		}});        
    }
    
    protected Dialog onCreateDialog(int id)
    {
    	switch(id)
    	{
    		case SELECT_FORWARD_DIALOG_ID:
    		{
    			return new AlertDialog.Builder(mContext)
    			.setTitle(getString(R.string.select_forward))
    			.setSingleChoiceItems(R.array.forward, mForwardId, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					mForwardId = which;
				}})
				.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					mProfileTextView.setText(mForwardArray[mForwardId]);	
				}})
				.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}})		
    			.create();
    		}
    		case SIGNAL_STRENGTH_DIALOG_ID:
    		{
				return new AlertDialog.Builder(mContext)
				.setTitle(getString(R.string.signal_strength))
				.setSingleChoiceItems(R.array.strength, mSignalStrength, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					mSignalStrength = which;
				}})
				.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						mSignalTextView.setText(mStrengthArray[mSignalStrength]);
					}})
				.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}})
				.create();	
    		}
    		case SELECT_KIND_DIALOG_ID:
    		{
    			return new AlertDialog.Builder(mContext)
    			.setTitle(getString(R.string.select_block_number))
    			.setSingleChoiceItems(R.array.select_number, mSelectNumberId, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					mSelectNumberId = which;
				}})
				.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if(0 == mSelectNumberId)
					{
						showDialog(EDIT_NUMBER_DIALOG_ID);	
					}
					else
					{
						showDialog(CONTACT_NUMBER_DIALOG_ID);
					}
				}})
				.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}})		
    			.create();
    		}
    		case EDIT_NUMBER_DIALOG_ID:
    		{
    			final EditText eb = new EditText(mContext);
    			return new AlertDialog.Builder(mContext)
    			.setTitle(getString(R.string.select_block_number))
    			.setView(eb)
				.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					mBlockNumber = eb.getEditableText().toString();
					mBlockNumberTextView.setText(mBlockNumber);
				}})
				.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}})		
    			.create();
    		}
    		case CONTACT_NUMBER_DIALOG_ID:
    		{
    			List<String> contact = getContact(mContext);
    			if(contact == null || contact.size() == 0)
    			{
    				Toast.makeText(mContext, getString(R.string.contact_none), Toast.LENGTH_SHORT).show();
    				return null;
    			}
    			String[] number = new String[contact.size()]; 
    			number = contact.toArray(number);
    			final String[] numbers = number;
    			return new AlertDialog.Builder(mContext)
    			.setTitle(getString(R.string.select_block_number))
    			.setSingleChoiceItems(numbers, mContactId, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					mContactId = which;
				}})
				.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					mBlockNumber = numbers[mContactId];
					mBlockNumberTextView.setText(mBlockNumber);
				}})
				.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}})		
    			.create();
    		}
    	}
    	return super.onCreateDialog(id);
    }

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}	
	private void storeProfile()
	{
		Cursor c = getContentResolver().query(
		ProfileInfoProvider.CONTENT_URI_WLAN_PROFILES, 
			null, 
			ProfileDatabaseHelper.WLAN_MAC + " = '" + mDeviceAddress +"' ", 
			null, 
			null);
		if((c != null) && (c.getCount() != 0))
		{
			//update the context value in database
			ContentValues values = new ContentValues();
			values.put(ProfileDatabaseHelper.WLAN_NAME, mDeviceName);
			values.put(ProfileDatabaseHelper.PROFILE_VALUE, mForwardId);
			values.put(ProfileDatabaseHelper.WLAN_RSSI, mSignalValues[mSignalStrength]);
			values.put(ProfileDatabaseHelper.ENABLED, mEnabled);
			values.put(ProfileDatabaseHelper.WLAN_PHONE_NUMBER, mBlockNumber);
			mContext.getContentResolver().update(
			ProfileInfoProvider.CONTENT_URI_WLAN_PROFILES, values, 
			ProfileInfoProvider.WLAN_MAC + " = '" + mDeviceAddress +"'", null);	
		}else{

			//insert a new recoder into database
			ContentValues values = new ContentValues();
			values.put(ProfileDatabaseHelper.WLAN_NAME, mDeviceName);
			values.put(ProfileDatabaseHelper.WLAN_MAC, mDeviceAddress);
			values.put(ProfileDatabaseHelper.PROFILE_VALUE, mForwardId);
			values.put(ProfileDatabaseHelper.WLAN_RSSI, mSignalValues[mSignalStrength]);
			values.put(ProfileDatabaseHelper.WLAN_PHONE_NUMBER, mBlockNumber);
			values.put(ProfileDatabaseHelper.ENABLED, mEnabled);
			mContext.getContentResolver().insert(ProfileInfoProvider.CONTENT_URI_WLAN_PROFILES, values);
		}
		
		SharedPreferences prefs = getSharedPreferences("ContextAware", MODE_PRIVATE);
		String service_started = prefs.getString("StartContextAware", "false");
    	if(service_started.equals("true") && mEnabled)
    	{
    		Intent localIntent = new Intent();  
	        localIntent.setAction("android.intent.action.CALL");  
	        Uri uri = Uri.parse(mForwardNumber[mForwardId]);  
	        localIntent.setData(uri);
	        localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        startActivity(localIntent); 
    	}
	}
	
	public static List<String> getContact(Context context)
    {
        ContentResolver content = context.getContentResolver();                                 
        Uri uri = ContactsContract.Contacts.CONTENT_URI;              
        Cursor cursor = content.query(uri,null, null, null, null);
        int contactCount = cursor.getCount();               
 
        List<String> contacts = new ArrayList<String>();         
 
        if (cursor.moveToFirst())
        {
                for (;!cursor.isAfterLast();cursor.moveToNext())
                {
                        int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                        int displayNameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                        int phoneColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);                         
                 
                        String contactId = cursor.getString(idColumn);
                        String disPlayName = cursor.getString(displayNameColumn);
                 
                        //String phoneString = cursor.getString(phoneColumn);
                        int phoneNum = cursor.getInt(phoneColumn);
                                                 
                        if (phoneNum > 0)
                        {
                                Cursor phones = content.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,  ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + contactId,   null, null);
                                int phoneCount = phones.getCount();
                                if (phones.moveToFirst())
                                {
                                        for (;!phones.isAfterLast();phones.moveToNext())
                                        {                                                 
                                                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                                if(!contacts.contains(phoneNumber))
                                                {
                                                	contacts.add(phoneNumber);
                                                }
                                        }
                                 
                                        if (!phones.isClosed())
                                        {
                                                phones.close();
                                        }
                                }
                        }
                 
                }
         
                if (!cursor.isClosed())
                {
                        cursor.close();
                }
        }
        return contacts;

    }
	
}
