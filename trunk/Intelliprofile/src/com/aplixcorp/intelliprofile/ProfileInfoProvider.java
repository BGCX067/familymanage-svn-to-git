package com.aplixcorp.intelliprofile;

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
import android.util.Log;

public class ProfileInfoProvider extends ContentProvider {

	public static final String BLUETOOTH_NAME = "bluetooth_name";
	public static final String BLUETOOTH_MAC = "bluetooth_mac";
	public static final String BLUETOOTH_RSSI = "bluetooth_rssi";
	
	public static final String WLAN_NAME = "wlan_name";
	public static final String WLAN_MAC = "wlan_mac";
	
	public static final String PROFILE_VALUE = "profile";
	public static final String PROFILE_ENABLE = "enabled";
	private static final String LOGNAME = ProfileInfoProvider.class.getSimpleName();
	private static final String DB_FILENAME = "profileinfo.db";
	private static final int DB_VERSION = 1;
	private static final String URI_AUTHORITY = "jp.aplix.profileinfo";
	
	public static final String TABLE_BT_PROFILES = "bt_profiles";
	public static final String TABLE_WLAN_PROFILES = "wlan_profiles";
	
	private static final int BT_PROFILES = 1;
	private static final int BT_PROFILES_ID = 2;
	
	private static final int WLAN_PROFILES = 3;
	private static final int WLAN_PROFILES_ID = 4;
	
	private static UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	private SQLiteDatabase db;
	public static final Uri CONTENT_URI_BT_PROFILES = Uri.parse("content://" + URI_AUTHORITY + "/" + TABLE_BT_PROFILES);
	public static final Uri CONTENT_URI_WLAN_PROFILES = Uri.parse("content://" + URI_AUTHORITY + "/" + TABLE_WLAN_PROFILES);
	
	static {
		URI_MATCHER.addURI(URI_AUTHORITY, TABLE_BT_PROFILES, BT_PROFILES);
		URI_MATCHER.addURI(URI_AUTHORITY, TABLE_BT_PROFILES+"/#", BT_PROFILES_ID);
		URI_MATCHER.addURI(URI_AUTHORITY, TABLE_WLAN_PROFILES, WLAN_PROFILES);
		URI_MATCHER.addURI(URI_AUTHORITY, TABLE_WLAN_PROFILES+"/#", WLAN_PROFILES_ID);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		// TODO Auto-generated method stub
		int count;

		try
		{
			if (URI_MATCHER.match(uri) == BT_PROFILES_ID) 
			{
				Long id = Long.parseLong(uri.getLastPathSegment());
				count = db.delete(TABLE_BT_PROFILES, "_id=" + id, null);
	
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else if(URI_MATCHER.match(uri) == BT_PROFILES) 
			{
				count = db.delete(TABLE_BT_PROFILES, selection, null);
				getContext().getContentResolver().notifyChange(uri, null);
				return count;
			}
			if (URI_MATCHER.match(uri) == WLAN_PROFILES_ID) 
			{
				Long id = Long.parseLong(uri.getLastPathSegment());
				count = db.delete(TABLE_WLAN_PROFILES, "_id=" + id, null);
	
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else if(URI_MATCHER.match(uri) == WLAN_PROFILES) 
			{
				count = db.delete(TABLE_WLAN_PROFILES, selection, null);
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
		case BT_PROFILES:
			return "vnd.android.cursor.dir/vnd.jp.aplix.btprofile";
		case BT_PROFILES_ID:
			return "vnd.android.cursor.item/vnd.jp.aplix.btprofile";
		case WLAN_PROFILES:
			return "vnd.android.cursor.dir/vnd.jp.aplix.wlanprofile";
		case WLAN_PROFILES_ID:
			return "vnd.android.cursor.item/vnd.jp.aplix.wlanprofile";
		default:
			throw new IllegalArgumentException("Unknown URL : " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		// TODO Auto-generated method stub
		long id = -1;
		try
		{
			if(URI_MATCHER.match( uri ) == BT_PROFILES) 
			{
				id = db.insert(TABLE_BT_PROFILES, "", values);
				if (id == -1)
				{
					throw new SQLException();
				}
	
				Uri newUri = ContentUris.withAppendedId(CONTENT_URI_BT_PROFILES, id);
				getContext().getContentResolver().notifyChange(newUri, null);
	
				return newUri;
			}
			if(URI_MATCHER.match( uri ) == WLAN_PROFILES) 
			{
				id = db.insert(TABLE_WLAN_PROFILES, "", values);
				if (id == -1)
				{
					throw new SQLException();
				}
	
				Uri newUri = ContentUris.withAppendedId(CONTENT_URI_WLAN_PROFILES, id);
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
		Log.d(LOGNAME, "on create");
		// TODO Auto-generated method stub
		ProfileDatabaseHelper dbHelper = new ProfileDatabaseHelper( getContext(), DB_FILENAME, null, DB_VERSION );
		
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
			if(URI_MATCHER.match(uri) == BT_PROFILES || URI_MATCHER.match(uri) == BT_PROFILES_ID)
			{
				SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
				qb.setTables(TABLE_BT_PROFILES);
				if (URI_MATCHER.match(uri) == BT_PROFILES_ID) 
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
			if(URI_MATCHER.match(uri) == WLAN_PROFILES || URI_MATCHER.match(uri) == WLAN_PROFILES_ID)
			{
				SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
				qb.setTables(TABLE_WLAN_PROFILES);
				if (URI_MATCHER.match(uri) == WLAN_PROFILES_ID) 
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
			if(URI_MATCHER.match(uri) == BT_PROFILES_ID)
			{
				Long id = Long.parseLong(uri.getLastPathSegment());
				count = db.update(TABLE_BT_PROFILES, values, "_id=" + id, null);
	
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else if(URI_MATCHER.match(uri) == BT_PROFILES) 
			{
				count = db.update(TABLE_BT_PROFILES, values, selection, null);
				
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			if(URI_MATCHER.match(uri) == WLAN_PROFILES_ID)
			{
				Long id = Long.parseLong(uri.getLastPathSegment());
				count = db.update(TABLE_WLAN_PROFILES, values, "_id=" + id, null);
	
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else if(URI_MATCHER.match(uri) == WLAN_PROFILES) 
			{
				count = db.update(TABLE_WLAN_PROFILES, values, selection, null);
				
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