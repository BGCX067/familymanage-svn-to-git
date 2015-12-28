package com.GetPosition;

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

public class DataBaseProvider  extends ContentProvider {

	public static final String Table_User = "User";
	public static final String Table_Wlan ="WLAN";
	public static final String Table_BlueTooth = "BlutTooth";
	
	private static final int BT_PROFILES = 1;
	private static final int BT_PROFILES_ID = 2;
	
	private static final int WLAN_PROFILES = 3;
	private static final int WLAN_PROFILES_ID = 4;
	
	private static final int USER_PROFILES = 5;
	private static final int USER_PROFILES_ID = 6;
	
	private static final String LOGNAME = DataBaseProvider.class.getSimpleName();
	private static final String DB_FILENAME = "profileinfo.db";
	private static final int DB_VERSION = 1;
	private static final String URI_AUTHORITY = "com.GetPosition";
	private static UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	private SQLiteDatabase db;
	public static final Uri CONTENT_URI_USER_PROFILES = Uri.parse("content://" + URI_AUTHORITY + "/" + Table_User);
	public static final Uri CONTENT_URI_WLAN_PROFILES = Uri.parse("content://" + URI_AUTHORITY + "/" + Table_Wlan);
	public static final Uri CONTENT_URI_BT_PROFILES = Uri.parse("content://" + URI_AUTHORITY + "/" + Table_BlueTooth);
	
	static {
		URI_MATCHER.addURI(URI_AUTHORITY, Table_User, USER_PROFILES);
		URI_MATCHER.addURI(URI_AUTHORITY, Table_User+"/#", USER_PROFILES_ID);
		URI_MATCHER.addURI(URI_AUTHORITY, Table_Wlan, WLAN_PROFILES);
		URI_MATCHER.addURI(URI_AUTHORITY, Table_Wlan+"/#", WLAN_PROFILES_ID);
		URI_MATCHER.addURI(URI_AUTHORITY, Table_BlueTooth, BT_PROFILES);
		URI_MATCHER.addURI(URI_AUTHORITY, Table_BlueTooth+"/#", BT_PROFILES_ID);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		int count;

		try
		{
			if (URI_MATCHER.match(uri) == BT_PROFILES_ID) 
			{
				Long id = Long.parseLong(uri.getLastPathSegment());
				count = db.delete(Table_BlueTooth, "_id=" + id, null);
	
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else if(URI_MATCHER.match(uri) == BT_PROFILES) 
			{
				count = db.delete(Table_BlueTooth, selection, null);
				getContext().getContentResolver().notifyChange(uri, null);
				return count;
			}
			if (URI_MATCHER.match(uri) == WLAN_PROFILES_ID) 
			{
				Long id = Long.parseLong(uri.getLastPathSegment());
				count = db.delete(Table_Wlan, "_id=" + id, null);
	
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else if(URI_MATCHER.match(uri) == WLAN_PROFILES) 
			{
				count = db.delete(Table_Wlan, selection, null);
				getContext().getContentResolver().notifyChange(uri, null);
				return count;
			}
			if (URI_MATCHER.match(uri) == USER_PROFILES_ID) 
			{
				Long id = Long.parseLong(uri.getLastPathSegment());
				count = db.delete(Table_User, "_id=" + id, null);
	
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else if(URI_MATCHER.match(uri) == USER_PROFILES) 
			{
				count = db.delete(Table_User, selection, null);
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
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		switch (URI_MATCHER.match(uri)) 
		{
		case BT_PROFILES:
			return "vnd.android.cursor.dir/vnd.com.getposition.btprofile";
		case BT_PROFILES_ID:
			return "vnd.android.cursor.item/vnd.com.getposition.btprofile";
		case WLAN_PROFILES:
			return "vnd.android.cursor.dir/vnd.com.getposition.wlanprofile";
		case WLAN_PROFILES_ID:
			return "vnd.android.cursor.item/vnd.com.getposition.wlanprofile";
		case USER_PROFILES:
			return "vnd.android.cursor.dir/vnd.com.getposition.userprofile";
		case USER_PROFILES_ID:
			return "vnd.android.cursor.item/vnd.com.getposition.userprofile";
		default:
			throw new IllegalArgumentException("Unknown URL : " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		long id = -1;
		try
		{
			if(URI_MATCHER.match( uri ) == BT_PROFILES) 
			{
				id = db.insert(Table_BlueTooth, "", values);
				if (id == -1)
				{
					throw new SQLException();
				}
	
				Uri newUri = ContentUris.withAppendedId(CONTENT_URI_BT_PROFILES, id);
				getContext().getContentResolver().notifyChange(newUri, null);
	
				return newUri;
			}
			if(URI_MATCHER.match( uri ) == USER_PROFILES) 
			{
				id = db.insert(Table_User, "", values);
				if (id == -1)
				{
					throw new SQLException();
				}
	
				Uri newUri = ContentUris.withAppendedId(CONTENT_URI_USER_PROFILES, id);
				getContext().getContentResolver().notifyChange(newUri, null);
	
				return newUri;
			}
			if(URI_MATCHER.match( uri ) == WLAN_PROFILES) 
			{
				id = db.insert(Table_Wlan, "", values);
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
	public boolean onCreate() {
		// TODO Auto-generated method stub
		DataBaseHelper dbHelper = new DataBaseHelper( getContext(), DB_FILENAME, null, DB_VERSION );
		
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
				qb.setTables(Table_BlueTooth);
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
				qb.setTables(Table_Wlan);
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
			if(URI_MATCHER.match(uri) == USER_PROFILES || URI_MATCHER.match(uri) == USER_PROFILES_ID)
			{
				SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
				qb.setTables(Table_User);
				if (URI_MATCHER.match(uri) == USER_PROFILES_ID) 
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
				count = db.update(Table_BlueTooth, values, "_id=" + id, null);
	
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else if(URI_MATCHER.match(uri) == BT_PROFILES) 
			{
				count = db.update(Table_BlueTooth, values, selection, null);
				
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			if(URI_MATCHER.match(uri) == WLAN_PROFILES_ID)
			{
				Long id = Long.parseLong(uri.getLastPathSegment());
				count = db.update(Table_Wlan, values, "_id=" + id, null);
	
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else if(URI_MATCHER.match(uri) == WLAN_PROFILES) 
			{
				count = db.update(Table_Wlan, values, selection, null);
				
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			if(URI_MATCHER.match(uri) == USER_PROFILES_ID)
			{
				Long id = Long.parseLong(uri.getLastPathSegment());
				count = db.update(Table_User, values, "_id=" + id, null);
	
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else if(URI_MATCHER.match(uri) == USER_PROFILES) 
			{
				count = db.update(Table_User, values, selection, null);
				
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
