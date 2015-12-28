package com.exuan.alarm;

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

public class AlarmInfoProvider extends ContentProvider {

	private static final String DB_FILENAME = "alarminfo.db";
	private static final int DB_VERSION = 1;
	private static final String URI_AUTHORITY = "com.exuan.alarm.alarminfo";
	private static final String TABLE_ALARMS = "alarms";
	private static final int ALARMS = 1;
	private static final int ALARMS_ID = 2;
	private static UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	private SQLiteDatabase db;
	public static final Uri CONTENT_URI_ALARMS = Uri.parse("content://" + URI_AUTHORITY + "/" + TABLE_ALARMS);
	
	static {
		URI_MATCHER.addURI(URI_AUTHORITY, TABLE_ALARMS, ALARMS);
		URI_MATCHER.addURI(URI_AUTHORITY, TABLE_ALARMS+"/#", ALARMS_ID);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		// TODO Auto-generated method stub
		int count;

		try
		{
			if (URI_MATCHER.match(uri) == ALARMS_ID) 
			{
				Long id = Long.parseLong(uri.getLastPathSegment());
				count = db.delete(TABLE_ALARMS, "_id=" + id, null);
	
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else if(URI_MATCHER.match(uri) == ALARMS) 
			{
				count = db.delete(TABLE_ALARMS, selection, null);
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
		case ALARMS:
			return "vnd.android.cursor.dir/vnd.exuan.calendar.alarm";
		case ALARMS_ID:
			return "vnd.android.cursor.item/vnd.exuan.calendar.alarm";
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
			if(URI_MATCHER.match( uri ) == ALARMS) 
			{
				id = db.insert(TABLE_ALARMS, "", values);
				if (id == -1)
				{
					throw new SQLException();
				}
	
				Uri newUri = ContentUris.withAppendedId(CONTENT_URI_ALARMS, id);
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
		AlarmDatabaseHelper dbHelper = new AlarmDatabaseHelper( getContext(), DB_FILENAME, null, DB_VERSION );
		
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
			if(URI_MATCHER.match(uri) == ALARMS || URI_MATCHER.match(uri) == ALARMS_ID)
			{
				SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
				qb.setTables(TABLE_ALARMS);
				if (URI_MATCHER.match(uri) == ALARMS_ID) 
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
			if(URI_MATCHER.match(uri) == ALARMS_ID)
			{
				Long id = Long.parseLong(uri.getLastPathSegment());
				count = db.update(TABLE_ALARMS, values, "_id=" + id, null);
	
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else if(URI_MATCHER.match(uri) == ALARMS) 
			{
				count = db.update(TABLE_ALARMS, values, selection, null);
				
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