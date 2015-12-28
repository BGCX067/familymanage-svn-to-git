package com.hammer.notes;

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

public class NotesProvider extends ContentProvider {

	private static final String DB_FILENAME = "notes_info.db";
	private static final int DB_VERSION = 1;
	private static final String URI_AUTHORITY = "com.hammer.notes.notesinfo";
	private static final int NOTES = 1;
	private static final int NOTES_ID = 2;
	private static UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	private SQLiteDatabase db;
	public static final Uri CONTENT_URI_NOTES = Uri.parse("content://" + URI_AUTHORITY + "/" + NotesDatabaseHelper.TABLE_NOTES);
	
	static {
		URI_MATCHER.addURI(URI_AUTHORITY, NotesDatabaseHelper.TABLE_NOTES, NOTES);
		URI_MATCHER.addURI(URI_AUTHORITY, NotesDatabaseHelper.TABLE_NOTES+"/#", NOTES_ID);
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
				//count = db.delete(NotesDatabaseHelper.TABLE_NOTES, "_id=" + id, null);
				count = db.delete(NotesDatabaseHelper.TABLE_NOTES, "rowid=" + id, null);
	
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else if(URI_MATCHER.match(uri) == NOTES) 
			{
				count = db.delete(NotesDatabaseHelper.TABLE_NOTES, selection, null);
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
			return "vnd.android.cursor.dir/vnd.com.hammer.notes";
		case NOTES_ID:
			return "vnd.android.cursor.item/vnd.com.hammer.notes";
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
				id = db.insert(NotesDatabaseHelper.TABLE_NOTES, "", values);
				values.put("_id", id);
				values.put(NotesDatabaseHelper.POSITION, id);
				db.update(NotesDatabaseHelper.TABLE_NOTES, values, "rowid='" + id + "'", null);
				if (id == -1)
				{
					throw new SQLException();
				}
				Uri newUri = ContentUris.withAppendedId(CONTENT_URI_NOTES, id);
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
				qb.setTables(NotesDatabaseHelper.TABLE_NOTES);
				if (URI_MATCHER.match(uri) == NOTES_ID) 
				{
					//qb.appendWhere("_id=" + uri.getLastPathSegment());
					qb.appendWhere("rowid=" + uri.getLastPathSegment());
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
			if(selection.equals("UPDATE_POSITION"))
			{
				if(URI_MATCHER.match(uri) == NOTES_ID || URI_MATCHER.match(uri) == NOTES)
				{
					int from = values.getAsInteger("from");
					int to = values.getAsInteger("to");
					String up_a = "update " + NotesDatabaseHelper.TABLE_NOTES + 
					" set " + NotesDatabaseHelper.POSITION + "=" + 0 + " where " + 
					NotesDatabaseHelper.POSITION + "=" + from + "";
					//Log.e("jayce", "sea:" + up_a);
					db.execSQL(up_a);
					String up_b = "";
					if(from > to)
					{
						up_b = "update " + NotesDatabaseHelper.TABLE_NOTES + 
						" set " + NotesDatabaseHelper.POSITION + "=" + NotesDatabaseHelper.POSITION + "+1"
						+ " where " + NotesDatabaseHelper.POSITION + ">=" + to + " and " + 
						NotesDatabaseHelper.POSITION + "<" + from;
					}
					else
					{
						up_b = "update " + NotesDatabaseHelper.TABLE_NOTES + 
						" set " + NotesDatabaseHelper.POSITION + "=" + NotesDatabaseHelper.POSITION + "-1"
						+ " where " + NotesDatabaseHelper.POSITION + ">" + from + " and " + 
						NotesDatabaseHelper.POSITION + "<=" + to;
					}
					//Log.e("jayce", "seb:" + up_b);
					db.execSQL(up_b);
					up_a = "update " + NotesDatabaseHelper.TABLE_NOTES + 
					" set " + NotesDatabaseHelper.POSITION + "=" + to + " where " + 
					NotesDatabaseHelper.POSITION + "=" + 0;
					//Log.e("jayce", "sec:" + up_a);
					db.execSQL(up_a);
					getContext().getContentResolver().notifyChange(uri, null);
					return Math.abs(from - to) + 1;
				}
				else
				{
					throw new IllegalArgumentException();
				}
			}
			else if(URI_MATCHER.match(uri) == NOTES_ID)
			{
				Long id = Long.parseLong(uri.getLastPathSegment());
				//count = db.update(NotesDatabaseHelper.TABLE_NOTES, values, "_id=" + id, null);
				count = db.update(NotesDatabaseHelper.TABLE_NOTES, values, "rowid=" + id, null);
				getContext().getContentResolver().notifyChange(uri, null);
	
				return count;
			}
			else if(URI_MATCHER.match(uri) == NOTES) 
			{
				count = db.update(NotesDatabaseHelper.TABLE_NOTES, values, selection, null);
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