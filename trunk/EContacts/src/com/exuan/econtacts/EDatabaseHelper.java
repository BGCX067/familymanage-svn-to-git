package com.exuan.econtacts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class EDatabaseHelper extends SQLiteOpenHelper{

	public static final String TABLE_GROUP = "group";
	public static final String TABLE_CONTACTS = "contacts";
	
	public static final String GROUP_NAME = "group_name";
	public static final String CONTACT_NAME = "contact_name";
	
	public EDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		/*
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
				 */
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		onCreate(db);
	}
	
}