package com.exuan.ecalendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseHelper extends SQLiteOpenHelper{

	public static final String TABLE_DIARYS = "diarys";
	
	public static final String DATE = "date";
	public static final String CONTENT = "content";
	
	public DatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DIARYS +
				 " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				 DATE + " VARCHAR NOT NULL, " +
				 CONTENT + " VARCHAR NOT NULL);" 
				 );
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIARYS);
		onCreate(db);
	}
	
}