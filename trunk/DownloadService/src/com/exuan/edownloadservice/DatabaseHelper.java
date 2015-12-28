package com.exuan.edownloadservice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseHelper extends SQLiteOpenHelper
{
	public static final String URL = "url";
	public static final String TOTAL_SIZE = "totalsize";
	public static final String CURRENT_SIZE = "currentsize";
	public static final String TASK_STATUS = "taskstatus";
	
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS " + DataProvider.TABLE_DOWNLOAD +
				 " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				 URL + " VARCHAR, " +
				 TASK_STATUS + " INTEGER, " +
				 TOTAL_SIZE + " INTEGER, " +
				 CURRENT_SIZE + " INTEGER);"
				 );
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + DataProvider.TABLE_DOWNLOAD);
		onCreate(db);
	}
	
}