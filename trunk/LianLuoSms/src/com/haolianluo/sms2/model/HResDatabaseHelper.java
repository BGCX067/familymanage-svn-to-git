package com.haolianluo.sms2.model;

import com.haolianluo.sms2.data.HConst;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class HResDatabaseHelper extends SQLiteOpenHelper{
	//table download columns
	public static final String DISPLAY_NAME = "name";
	public static final String ICON_URL = "icon";
	public static final String FILE_NAME = "filename";
	public static final String TASK_STATUS = "status";
	public static final String TOTAL_SIZE = "totalsize";
	public static final String CURRENT_SIZE = "currentsize";
	public static final String CHARGE = "charge";
	public static final String CHARGE_MSG = "chargemsg";
	public static final String RES_ID = "resid";
	public static final String PACKAGENAME = "packagename";
	
	//table skin columns
	public static final String RES_KEY = "reskey";
	public static final String RES_USE = "resuse";
	
	public HResDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		HStatistics mHStatistics = new HStatistics(context);
		mHStatistics.add(HStatistics.Z12_1, "0", "0", "1");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS " + HResProvider.TABLE_DOWNLOAD +
				 " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				 DISPLAY_NAME + " VARCHAR NOT NULL, " +
				 FILE_NAME + " VARCHAR, " +
				 TASK_STATUS + " INTEGER, " +
				 TOTAL_SIZE + " INTEGER, " +
				 CURRENT_SIZE + " INTEGER, " +
				 RES_ID + " VARCHAR, " +
				 CHARGE + " VARCHAR, " +
				 ICON_URL + " VARCHAR, " +
				 CHARGE_MSG + " VARCHAR, " +
				 PACKAGENAME + " VARCHAR);"
				 );
		
		db.execSQL("CREATE TABLE IF NOT EXISTS " + HResProvider.TABLE_SKIN +
				 " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				 FILE_NAME + " VARCHAR, " +
				 PACKAGENAME + " VARCHAR, " +
				 DISPLAY_NAME + " VARCHAR, " +
				 RES_KEY + " VARCHAR, " +
				 RES_USE + " INTEGER, " +
				 TOTAL_SIZE + " INTEGER, " +
				 CHARGE + " INTEGER, " +
				 RES_ID + " VARCHAR);"
				 );
		ContentValues values = new ContentValues();
		values.put(PACKAGENAME, "com.haolianluo.sms2");
		values.put(RES_USE, 1);
		db.insert(HResProvider.TABLE_SKIN, null, values);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + HResProvider.TABLE_SKIN);
		db.execSQL("DROP TABLE IF EXISTS " + HResProvider.TABLE_DOWNLOAD);
		onCreate(db);
	}
}