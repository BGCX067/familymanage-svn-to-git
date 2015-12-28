package com.hammer.notes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class NotesDatabaseHelper extends SQLiteOpenHelper{

	public static final String TABLE_NOTES = "notes_file";
	public static final String MODIFY_TIME = "modify_time";
	public static final String DETAIL = "detail";
	public static final String POSITION = "pos";
	public static final String LOCATION = "location";
	public static final String WEATHER = "weather";
	public static final String FAVORITE = "favorite";
	
	public NotesDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		/*
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NOTES +
				 "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				 MODIFY_TIME + " INTEGER, " +
				 DETAIL + " VARCHAR);" 
				 );
		*/
		db.execSQL("CREATE VIRTUAL TABLE " + TABLE_NOTES + " USING FTS3" + 
				 "(_id INTEGER PRIMARY KEY UNIQUE, " +
				 MODIFY_TIME + " INTEGER, " +
				 POSITION + " INTEGER UNIQUE, " +
				 LOCATION + " TEXT, " +
				 WEATHER + " INTEGER, " +
				 FAVORITE + " INTEGER, " +
				 DETAIL + " TEXT);" 
				 );
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
		onCreate(db);
	}
}