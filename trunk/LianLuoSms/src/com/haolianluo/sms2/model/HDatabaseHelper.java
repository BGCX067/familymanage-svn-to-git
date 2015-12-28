package com.haolianluo.sms2.model;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * 
 * 数据库辅助类
 *
 * 2011年9月6日13:17:44
 *
 */

public class HDatabaseHelper  extends SQLiteOpenHelper{
	
	/**数据库名*/
    private static final String DATABASE_NAME = "data"; 
    /**数据库版本号*/
    private static final int DATABASE_VERSION = 6;
    public HDatabaseHelper(Context context) {
    	 super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    
    /**创建表*/
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HBufferList.CREATE_TABLE);
        db.execSQL(HCollectTable.CREATE_COLLECT_TABLE);
        db.execSQL(HHistoryLinkman.CREATE_TABLE);
        db.execSQL(HStatistics.TABLE_CREATE);
    }


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
