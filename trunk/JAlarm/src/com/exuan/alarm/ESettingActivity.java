package com.exuan.alarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;

public class ESettingActivity extends PreferenceActivity {
	
	private Context mContext;
	private CheckBoxPreference mCheckBoxIsNotify;
	private boolean mIsNotify;
	public static final String PREFERENCES_NAME = "alarm_setting";
	public static final String KEY_NOTIFY = "notify_icon";
	
	protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.setting);
        mContext = this;
        mCheckBoxIsNotify = (CheckBoxPreference) findPreference("CheckBoxNotiry");
        setValue();
        mCheckBoxIsNotify.setChecked(mIsNotify);
        mCheckBoxIsNotify.setOnPreferenceChangeListener(mOnPrefernceChangeListener);
	}
	
	private OnPreferenceChangeListener mOnPrefernceChangeListener = new OnPreferenceChangeListener() 
    {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			// TODO Auto-generated method stub
			if(preference.getKey().equals("CheckBoxNotiry"))
			{
				mIsNotify = ((Boolean)newValue).booleanValue();
				mCheckBoxIsNotify.setChecked(mIsNotify);
				if(mIsNotify)
				{
					Cursor cursor = mContext.getContentResolver().query(AlarmInfoProvider.CONTENT_URI_ALARMS, null, AlarmDatabaseHelper.IS_ACTIVE + " = '1'", null, null);
					if(cursor != null)
					{
						if(cursor.getCount() > 0)
						{
							ESetAlarmActivity.setNotification(mContext, true);
						}
					}
				}
				else
				{
					ESetAlarmActivity.setNotification(mContext, false);
				}
				return true;
			}
			return false;
		}
    };
	
	private void setValue()
	{
		SharedPreferences pref = mContext.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
		mIsNotify = pref.getBoolean(KEY_NOTIFY, true);
	}
	
	private void storeValue()
	{
		SharedPreferences pref = mContext.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
		pref.edit().putBoolean(KEY_NOTIFY, mIsNotify).commit();
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		storeValue();
	}
}