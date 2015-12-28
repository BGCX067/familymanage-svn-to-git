package com.exuan.enotes;

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

public class NotesProvider extends ContentProvider {

	private static final String DB_FILENAME = "notes_info.db";
	private static final int DB_VERSION = 1;
	private static final String URI_AUTHORITY = "com.exuan.enotes.notesinfo";
	public static final String TABLE_NOTES = "notes_file";
	public static final String TABLE_FOLDERS = "notes_folder";
	private static final int NOTES = 1;
	private static final int NOTES_ID = 2;
	private static final int FOLDERS = 3;
	private static final int FOLDERS_ID = 4;
	private static UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	private SQLiteDatabase db;
	public static final Uri CONTENT_URI_NOTES = Uri.parse("content://" + URI_AUTHORITY + "/" + TABLE_NOTES);
	public static final Uri CONTENT_URI_FOLDERS = Uri.parse("content://" + URI_AUTHORITY + "/" + TABLE_FOLDERS);
	
	static {
		URI_MATCHER.addURI(URI_AUTHORITY, TABLE_NOTES, NOTES);
		URI_MATCHER.addURI(URI_AUTHORITY, TABLE_NOTES+"/#", NOTES_ID);
		URI_MATCHER.addURI(URI_AUTHORITY, TABLE_FOLDERS, FOLDERS);
		URI_MATCHER.addURI(URI_AUTHORITY, TABLE_FOLDERS+"/#", FOLDERS_ID);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		// TODO Auto-generated method stub
		int count;

		try
		{
			if (URI_MATCHER.match(uri) == NOTES_ID) 
			{
				Long id = Long.parseLong(uri.getLastPathSegment());
				count = db.delete(TABLE_NOTES, "_id=" + id, null);
	
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else if(URI_MATCHER.match(uri) == NOTES) 
			{
				count = db.delete(TABLE_NOTES, selection, null);
				getContext().getContentResolver().notifyChange(uri, null);
				return count;
			}
			if (URI_MATCHER.match(uri) == FOLDERS_ID) 
			{
				Long id = Long.parseLong(uri.getLastPathSegment());
				count = db.delete(TABLE_FOLDERS, "_id=" + id, null);
	
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else if(URI_MATCHER.match(uri) == FOLDERS) 
			{
				count = db.delete(TABLE_FOLDERS, selection, null);
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
		case NOTES:
			return "vnd.android.cursor.dir/vnd.exuan.enotes.notes";
		case NOTES_ID:
			return "vnd.android.cursor.item/vnd.exuan.enotes.notes";
		case FOLDERS:
			return "vnd.android.cursor.dir/vnd.exuan.enotes.folders";
		case FOLDERS_ID:
			return "vnd.android.cursor.item/vnd.exuan.enotes.folders";
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
			if(URI_MATCHER.match( uri ) == NOTES) 
			{
				id = db.insert(TABLE_NOTES, "", values);
				if (id == -1)
				{
					throw new SQLException();
				}
	
				Uri newUri = ContentUris.withAppendedId(CONTENT_URI_NOTES, id);
				getContext().getContentResolver().notifyChange(newUri, null);
	
				return newUri;
			}
			if(URI_MATCHER.match( uri ) == FOLDERS) 
			{
				id = db.insert(TABLE_FOLDERS, "", values);
				if (id == -1)
				{
					throw new SQLException();
				}
	
				Uri newUri = ContentUris.withAppendedId(CONTENT_URI_FOLDERS, id);
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
		NotesDatabaseHelper dbHelper = new NotesDatabaseHelper( getContext(), DB_FILENAME, null, DB_VERSION );
		
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
			if(URI_MATCHER.match(uri) == NOTES || URI_MATCHER.match(uri) == NOTES_ID)
			{
				SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
				qb.setTables(TABLE_NOTES);
				if (URI_MATCHER.match(uri) == NOTES_ID) 
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
			if(URI_MATCHER.match(uri) == FOLDERS || URI_MATCHER.match(uri) == FOLDERS_ID)
			{
				SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
				qb.setTables(TABLE_FOLDERS);
				if (URI_MATCHER.match(uri) == FOLDERS_ID) 
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
			if(URI_MATCHER.match(uri) == NOTES_ID)
			{
				Long id = Long.parseLong(uri.getLastPathSegment());
				count = db.update(TABLE_NOTES, values, "_id=" + id, null);
	
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else if(URI_MATCHER.match(uri) == NOTES) 
			{
				count = db.update(TABLE_NOTES, values, selection, null);
				
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			if(URI_MATCHER.match(uri) == FOLDERS_ID)
			{
				Long id = Long.parseLong(uri.getLastPathSegment());
				count = db.update(TABLE_FOLDERS, values, "_id=" + id, null);
	
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else if(URI_MATCHER.match(uri) == FOLDERS) 
			{
				count = db.update(TABLE_FOLDERS, values, selection, null);
				
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