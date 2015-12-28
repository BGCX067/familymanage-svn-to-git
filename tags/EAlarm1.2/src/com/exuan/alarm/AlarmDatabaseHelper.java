package com.exuan.alarm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class AlarmDatabaseHelper extends SQLiteOpenHelper{

	public static final String TABLE_ALARMS = "alarms";
	
	public static final String HOUR = "hour";
	public static final String MINUTE = "minute";
	public static final String REPEAT = "repeat";
	public static final String DELAY = "delay";
	public static final String ALERT_INFO = "alertinfo";
	public static final String ALERT_RING = "alertring";
	public static final String IS_RING = "isring";
	public static final String IS_VIBRATE = "isvibrate";
	public static final String IS_ACTIVE = "isactive";
	
	public AlarmDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS alarms " +
				 "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				 HOUR + " INTEGER NOT NULL, " +
				 MINUTE + " INTEGER NOT NULL, " +
				 REPEAT + " VARCHAR NOT NULL, " +
				 DELAY + " INTEGER DEFAULT 0, " +
				 ALERT_INFO + " VARCHAR, " +
				 ALERT_RING + " VARCHAR, " +
				 IS_RING + " BOOLEAN DEFAULT TRUE, " +
				 IS_VIBRATE + " BOOLEAN DEFAULT TRUE, " +
				 IS_ACTIVE + " BOOLEAN DEFAULT TRUE);" 
				 );
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMS);
		onCreate(db);
	}
	
}