package com.aplixcorp.intelliprofile;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class ProfileDatabaseHelper extends SQLiteOpenHelper{
	private static final String LOGNAME = ProfileDatabaseHelper.class.getSimpleName();
	public static final String TABLE_BT_PROFILES = "bt_profiles";
	public static final String TABLE_WLAN_PROFILES = "wlan_profiles";
	
	public static final String BLUETOOTH_NAME = "bluetooth_name";
	public static final String BLUETOOTH_MAC = "bluetooth_mac";
	public static final String BLUETOOTH_RSSI = "bluetooth_rssi";
	
	public static final String WLAN_NAME = "wlan_name";
	public static final String WLAN_MAC = "wlan_mac";
	public static final String WLAN_RSSI = "wlan_rssi";
	public static final String WLAN_PHONE_NUMBER = "wlan_phone_number";
	
	public static final String PROFILE_VALUE = "profile_value";
	public static final String ENABLED = "enabled";
	
	public ProfileDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		Log.d(LOGNAME, "database helper");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.d(LOGNAME, "on create");
			db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_BT_PROFILES +
					 "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
					 BLUETOOTH_NAME + " VARCHAR NOT NULL, " +
					 BLUETOOTH_MAC + " VARCHAR NOT NULL, " +
					 BLUETOOTH_RSSI + " INTEGER DEFAULT 0, " +
					 PROFILE_VALUE + " INTEGER DEFAULT 0, " +
					 ENABLED + " BOOLEAN DEFAULT FALSE)"
				 );
			
			db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_WLAN_PROFILES +
					 "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
					 WLAN_NAME + " VARCHAR NOT NULL, " +
					 WLAN_MAC + " VARCHAR NOT NULL, " +
					 WLAN_RSSI + " INTEGER DEFAULT 0, " +
					 PROFILE_VALUE + " INTEGER DEFAULT 0, " +
					 WLAN_PHONE_NUMBER + " VARCHAR, " +
					 ENABLED + " BOOLEAN DEFAULT FALSE)"
				 );
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BT_PROFILES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WLAN_PROFILES);
		onCreate(db);
	}
	
}