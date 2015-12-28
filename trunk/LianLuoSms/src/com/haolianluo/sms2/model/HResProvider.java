package com.haolianluo.sms2.model;

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

public class HResProvider extends ContentProvider {

	private static final String DB_FILENAME = "res_info.db";
	private static final int DB_VERSION = 1;
	private static final String URI_AUTHORITY = "com.haolianluo.sms.resinfo";
	public static final String TABLE_SKIN = "res_skin";
	public static final String TABLE_DOWNLOAD = "res_download";
	private static final int SKIN = 1;
	private static final int SKIN_ID = 2;
	private static final int DOWNLOAD = 3;
	private static final int DOWNLOAD_ID = 4;
	private static UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	private SQLiteDatabase db;
	public static final Uri CONTENT_URI_SKIN = Uri.parse("content://" + URI_AUTHORITY + "/" + TABLE_SKIN);
	public static final Uri CONTENT_URI_DOWNLOAD = Uri.parse("content://" + URI_AUTHORITY + "/" + TABLE_DOWNLOAD);
	
	static {
		URI_MATCHER.addURI(URI_AUTHORITY, TABLE_SKIN, SKIN);
		URI_MATCHER.addURI(URI_AUTHORITY, TABLE_SKIN+"/#", SKIN_ID);
		URI_MATCHER.addURI(URI_AUTHORITY, TABLE_DOWNLOAD, DOWNLOAD);
		URI_MATCHER.addURI(URI_AUTHORITY, TABLE_DOWNLOAD+"/#", DOWNLOAD_ID);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		// TODO Auto-generated method stub
		int count;

		try
		{
			if (URI_MATCHER.match(uri) == SKIN_ID) 
			{
				Long id = Long.parseLong(uri.getLastPathSegment());
				count = db.delete(TABLE_SKIN, "_id=" + id, null);
	
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else if(URI_MATCHER.match(uri) == SKIN) 
			{
				count = db.delete(TABLE_SKIN, selection, null);
				getContext().getContentResolver().notifyChange(uri, null);
				return count;
			}
			if (URI_MATCHER.match(uri) == DOWNLOAD_ID) 
			{
				Long id = Long.parseLong(uri.getLastPathSegment());
				count = db.delete(TABLE_DOWNLOAD, "_id=" + id, null);
	
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else if(URI_MATCHER.match(uri) == DOWNLOAD) 
			{
				count = db.delete(TABLE_DOWNLOAD, selection, null);
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
		case SKIN:
			return "vnd.android.cursor.dir/vnd.haolianluo.sms.skin";
		case SKIN_ID:
			return "vnd.android.cursor.item/vnd.haolianluo.sms.skin";
		case DOWNLOAD:
			return "vnd.android.cursor.dir/vnd.haolianluo.sms.download";
		case DOWNLOAD_ID:
			return "vnd.android.cursor.item/vnd.haolianluo.sms.download";
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
			if(URI_MATCHER.match( uri ) == SKIN) 
			{
				id = db.insert(TABLE_SKIN, "", values);
				if (id == -1)
				{
					throw new SQLException();
				}
	
				Uri newUri = ContentUris.withAppendedId(CONTENT_URI_SKIN, id);
				getContext().getContentResolver().notifyChange(newUri, null);
	
				return newUri;
			}
			if(URI_MATCHER.match( uri ) == DOWNLOAD) 
			{
				id = db.insert(TABLE_DOWNLOAD, "", values);
				if (id == -1)
				{
					throw new SQLException();
				}
	
				Uri newUri = ContentUris.withAppendedId(CONTENT_URI_DOWNLOAD, id);
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
		HResDatabaseHelper dbHelper = new HResDatabaseHelper( getContext(), DB_FILENAME, null, DB_VERSION );
		
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
			if(URI_MATCHER.match(uri) == SKIN || URI_MATCHER.match(uri) == SKIN_ID)
			{
				SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
				qb.setTables(TABLE_SKIN);
				if (URI_MATCHER.match(uri) == SKIN_ID) 
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
			if(URI_MATCHER.match(uri) == DOWNLOAD || URI_MATCHER.match(uri) == DOWNLOAD_ID)
			{
				SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
				qb.setTables(TABLE_DOWNLOAD);
				if (URI_MATCHER.match(uri) == DOWNLOAD_ID) 
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
			if(URI_MATCHER.match(uri) == SKIN_ID)
			{
				Long id = Long.parseLong(uri.getLastPathSegment());
				count = db.update(TABLE_SKIN, values, "_id=" + id, null);
	
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else if(URI_MATCHER.match(uri) == SKIN) 
			{
				count = db.update(TABLE_SKIN, values, selection, null);
				
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			if(URI_MATCHER.match(uri) == DOWNLOAD_ID)
			{
				Long id = Long.parseLong(uri.getLastPathSegment());
				count = db.update(TABLE_DOWNLOAD, values, "_id=" + id, null);
	
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else if(URI_MATCHER.match(uri) == DOWNLOAD) 
			{
				count = db.update(TABLE_DOWNLOAD, values, selection, null);
				
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