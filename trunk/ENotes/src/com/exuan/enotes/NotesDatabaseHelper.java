package com.exuan.enotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class NotesDatabaseHelper extends SQLiteOpenHelper{

	public static final String TABLE_NOTES = "notes_file";
	public static final String TABLE_FOLDERS = "notes_folder";
	
	public static final String ALARM_TIME = "alarm_time";
	public static final String MODIFY_TIME = "modify_time";
	public static final String BACKGROUND_ID = "background_id";
	public static final String DETAIL = "detail";
	public static final String FOLDER_ID = "folder_id";
	public static final String FOLDER_NAME = "folder_name";
	
	public NotesDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NOTES +
				 "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				 ALARM_TIME + " INTEGER, " +
				 MODIFY_TIME + " INTEGER, " +
				 FOLDER_ID + " INTEGER, " +
				 BACKGROUND_ID + " INTEGER, " +
				 DETAIL + " VARCHAR);" 
				 );
		
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_FOLDERS +
				 "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				 FOLDER_NAME + " VARCHAR);"
				 );
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLDERS);
		onCreate(db);
	}
}