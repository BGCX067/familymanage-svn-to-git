package com.exuan.ecalendar;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class CalendarProvider extends ContentProvider {

	private static final String DB_FILENAME = "calendar_info.db";
	private static final int DB_VERSION = 1;
	private static final String URI_AUTHORITY = "com.exuan.ecalendar.calendarinfo";
	public static final String TABLE_DIARYS = "diarys";
	private static final int DIARYS = 1;
	private static final int DIARYS_ID = 2;
	private static UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	private SQLiteDatabase db;
	public static final Uri CONTENT_URI_DIARYS = Uri.parse("content://" + URI_AUTHORITY + "/" + TABLE_DIARYS);
	
	static {
		URI_MATCHER.addURI(URI_AUTHORITY, TABLE_DIARYS, DIARYS);
		URI_MATCHER.addURI(URI_AUTHORITY, TABLE_DIARYS+"/#", DIARYS_ID);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		// TODO Auto-generated method stub
		int count;

		try
		{
			if (URI_MATCHER.match(uri) == DIARYS_ID) 
			{
				Long id = Long.parseLong(uri.getLastPathSegment());
				count = db.delete(TABLE_DIARYS, "_id=" + id, null);
	
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else if(URI_MATCHER.match(uri) == DIARYS) 
			{
				count = db.delete(TABLE_DIARYS, selection, null);
				getContext().getContentResolver().notifyChange(uri, null);
				return count;
			}
			else 
			{
				throw new IllegalArgumentException();
			}
		}
		catch(SQLiteException e)
		{
			return 0;
		}
	}

	@Override
	public String getType(Uri uri) 
	{
		// TODO Auto-generated method stub
		switch (URI_MATCHER.match(uri)) 
		{
		case DIARYS:
			return "vnd.android.cursor.dir/vnd.exuan.ecalendar.calendars";
		case DIARYS_ID:
			return "vnd.android.cursor.item/vnd.exuan.ecalendar.calendars";
		default:
			throw new IllegalArgumentException("Unknown URL : " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		// TODO Auto-generated method stub
		long id;
		try
		{
			if(URI_MATCHER.match( uri ) == DIARYS) 
			{
				id = db.insert(TABLE_DIARYS, "", values);
				if (id == -1)
				{
					throw new SQLException();
				}
	
				Uri newUri = ContentUris.withAppendedId(CONTENT_URI_DIARYS, id);
				getContext().getContentResolver().notifyChange(newUri, null);
	
				return newUri;
			}
			else 
			{
				throw new IllegalArgumentException();
			}
		}
		catch(SQLiteException e)
		{
			return null;
		}
	}

	@Override
	public boolean onCreate() 
	{
		// TODO Auto-generated method stub
		DatabaseHelper dbHelper = new DatabaseHelper( getContext(), DB_FILENAME, null, DB_VERSION );
		
		try 
		{
			db = dbHelper.getWritableDatabase();
			return true;
		}
		catch ( SQLiteException sqle ) 
		{
			throw sqle;
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		try
		{	
			if(URI_MATCHER.match(uri) == DIARYS || URI_MATCHER.match(uri) == DIARYS_ID)
			{
				SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
				qb.setTables(TABLE_DIARYS);
				if (URI_MATCHER.match(uri) == DIARYS_ID) 
				{
					qb.appendWhere("_id=" + uri.getLastPathSegment());
				}
				Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
				if(null != c)
				{
					c.setNotificationUri( getContext().getContentResolver(), uri );
				}
				return c;
			}
			else 
			{
				throw new IllegalArgumentException();
			}
		}
		catch(SQLiteException e)
		{
			return null;
		}
		catch(Exception e)
		{
			return null;
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		int count;
		try
		{
			if(URI_MATCHER.match(uri) == DIARYS_ID)
			{
				Long id = Long.parseLong(uri.getLastPathSegment());
				count = db.update(TABLE_DIARYS, values, "_id=" + id, null);
	
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else if(URI_MATCHER.match(uri) == DIARYS) 
			{
				count = db.update(TABLE_DIARYS, values, selection, null);
				
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else
			{
				throw new IllegalArgumentException();
			}
		}
		catch(SQLiteException e)
		{
			return 0; 
		}
	}
}