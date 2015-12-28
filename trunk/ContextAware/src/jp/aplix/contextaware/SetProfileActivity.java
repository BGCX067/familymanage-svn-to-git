package jp.aplix.contextaware;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class SetProfileActivity extends Activity
{
	private Context mContext;
	private TextView mNameTextView;
	private TextView mAddressTextView;
	private TextView mProfileTextView;
	private TextView mSignalTextView;
	private TextView mRssiTextView;
	private LinearLayout mProfileLinearLayout;
	private LinearLayout mSignalLinearLayout;
	private Button mStoreButton;
	private Button mCancelButton;
	private CheckBox mEnabledProfile;
	private Button mDeleteCfgButton;	
	private String[] mStrengthArray;
	
	private int mProfileId;
	private int mSignalStrength;
	
	private static final int SELECT_PROFILE_DIALOG_ID = 0;
	private static final int SIGNAL_STRENGTH_DIALOG_ID = 1;
	
	private String[] mProfileArray;
	private String mDeviceName;
	private String mDeviceAddress;
	private boolean mEnabled;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_setprofile);
        mContext = this;
        Bundle extras = getIntent().getExtras();
        
        mDeviceName = extras.getString(ProfileDatabaseHelper.BLUETOOTH_NAME);
        mDeviceAddress = extras.getString(ProfileDatabaseHelper.BLUETOOTH_MAC);
        mSignalStrength = extras.getInt(ProfileDatabaseHelper.BLUETOOTH_RSSI);
        mProfileId = extras.getInt(ProfileDatabaseHelper.PROFILE_VALUE);
        mEnabled  = (extras.getString(ProfileDatabaseHelper.ENABLED)).equals("1") ? true : false;
        
        mProfileArray = mContext.getResources().getStringArray(R.array.profile);
        mStrengthArray = mContext.getResources().getStringArray(R.array.strength);
        
        mNameTextView = (TextView)findViewById(R.id.name_textview);
        mAddressTextView = (TextView)findViewById(R.id.address_textview);
        mProfileTextView = (TextView)findViewById(R.id.profile_textview);
        mSignalTextView = (TextView)findViewById(R.id.signal_textview);
        mRssiTextView = (TextView)findViewById(R.id.signal_strength_textview);
		
        mProfileLinearLayout = (LinearLayout)findViewById(R.id.profile_linearlayout);
        mSignalLinearLayout = (LinearLayout)findViewById(R.id.signal_linearlayout);
        mStoreButton = (Button)findViewById(R.id.store_button);
        mCancelButton = (Button)findViewById(R.id.cancel_button);
        mEnabledProfile = (CheckBox)findViewById(R.id.BlueToothEnable);
        mDeleteCfgButton = (Button)findViewById(R.id.DeleteConfig_button);
        mEnabledProfile.setChecked(mEnabled);
        if(mEnabled){
        	mEnabledProfile.setText(mEnabled?R.string.BlueToothDisable:R.string.BlueToothEnable);
        }else{
        	mEnabledProfile.setText(mEnabled?R.string.BlueToothDisable:R.string.BlueToothDisable);
        }
       	
        mNameTextView.setText(getString(R.string.name) + ": " + mDeviceName);
        mAddressTextView.setText(getString(R.string.mac) + ": " + mDeviceAddress);
        String mCurrentRssi = extras.getString("currentRssi");
        if(mCurrentRssi != null){
        	mRssiTextView.setText(getString(R.string.signal_strength) + ": " +mCurrentRssi);
        }
        mProfileTextView.setText(mProfileArray[mProfileId]);
        mSignalTextView.setText(mStrengthArray[mSignalStrength]);
        Cursor c = getContentResolver().query(
        		ProfileInfoProvider.CONTENT_URI_PROFILES, 
        		null, 
        		ProfileDatabaseHelper.BLUETOOTH_MAC + " = '" + mDeviceAddress +"' ", 
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
			showDialog(SELECT_PROFILE_DIALOG_ID);
		}});
        
        mSignalLinearLayout.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			showDialog(SIGNAL_STRENGTH_DIALOG_ID);
		}});
        
        mEnabledProfile.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {   
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1){
					mEnabledProfile.setText(R.string.BlueToothEnable);
					mEnabled = true;
				}else{
					mEnabledProfile.setText(R.string.BlueToothDisable);
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
    					ProfileInfoProvider.CONTENT_URI_PROFILES,
    					ProfileInfoProvider.BLUETOOTH_MAC + " = '" + mDeviceAddress +"'", null);	
    			finish();
    			return;
    		}});        
    }
    
    protected Dialog onCreateDialog(int id)
    {
    	switch(id)
    	{
    		case SELECT_PROFILE_DIALOG_ID:
    		{
    			return new AlertDialog.Builder(mContext)
    			.setTitle(getString(R.string.profile))
    			.setSingleChoiceItems(R.array.profile, mProfileId, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					mProfileId = which;
				}})
				.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					mProfileTextView.setText(mProfileArray[mProfileId]);	
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
    	}
    	return super.onCreateDialog(id);
    }
    
	private void storeProfile()
	{
		Cursor c = getContentResolver().query(
		ProfileInfoProvider.CONTENT_URI_PROFILES, 
			null, 
			ProfileDatabaseHelper.BLUETOOTH_MAC + " = '" + mDeviceAddress +"' ", 
			null, 
			null);
		if((c != null) && (c.getCount() != 0))
		{
			//update the context value in database
			ContentValues values = new ContentValues();
			values.put(ProfileDatabaseHelper.BLUETOOTH_NAME, mDeviceName);
			values.put(ProfileDatabaseHelper.BLUETOOTH_RSSI, mSignalStrength);
			values.put(ProfileDatabaseHelper.PROFILE_VALUE, mProfileId);
			values.put(ProfileDatabaseHelper.ENABLED, mEnabled);
			mContext.getContentResolver().update(
			ProfileInfoProvider.CONTENT_URI_PROFILES, values, 
			ProfileInfoProvider.BLUETOOTH_MAC + " = '" + mDeviceAddress +"'", null);	
		}else{

			//insert a new recoder into database
			ContentValues values = new ContentValues();
			values.put(ProfileDatabaseHelper.BLUETOOTH_NAME, mDeviceName);
			values.put(ProfileDatabaseHelper.BLUETOOTH_MAC, mDeviceAddress);
			values.put(ProfileDatabaseHelper.BLUETOOTH_RSSI, mSignalStrength);
			values.put(ProfileDatabaseHelper.PROFILE_VALUE, mProfileId);
			values.put(ProfileDatabaseHelper.ENABLED, mEnabled);
			mContext.getContentResolver().insert(ProfileInfoProvider.CONTENT_URI_PROFILES, values);
		}
	}
}
