package com.haolianluo.sms2.model;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/***
 * ------历史记录的操作
 * @author jianhua 2011年9月6日13:29:58
 *
 */

public class HHistoryLinkman {
	
	public static final String TABLE_NAME = "distorylinkman";//表名
	public static final String ID = "_id";
	public static final String DELETEINDEX = "deleteindex";
	public static final String PHONE_NUMBER = "phonenumber";
	public static final String SELECT_NUMBER = "select count"+ "(" + ID+") from " + TABLE_NAME;
	public Context context;
	public static final String CREATE_TABLE = "create table IF NOT EXISTS " + TABLE_NAME + " " + "(" + ID + " integer primary key autoincrement, "+ 
			PHONE_NUMBER + " text not null," + DELETEINDEX + " text not null);"; 
	
	public HHistoryLinkman(Context context){
		this.context = context;
	}
	
	
	//历史记录的插入
	public void addDistoryLinkManList(final String str){
		new Thread(){
			public void run(){
				if(str == null || str.equals("")){
					return;
				}
				HDatabaseHelper dbOpenHelper = new HDatabaseHelper(context);
				SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
				Cursor c = db.query(TABLE_NAME, new String[]{PHONE_NUMBER}, PHONE_NUMBER + " = '" + str + "'", null, null, null, null);
				if(selectLinkManList() < 10){
					if(!c.moveToNext()){//如果不是重复的
						insert(str, db);
					}
				}else{//数据库已经达到20个人了
					if(!c.moveToNext()){//如果不是重复的
						//删除数据库中的第一条
						Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
						cursor.moveToFirst();
						Cursor cursor1 = db.query(TABLE_NAME, new String[]{ID}, null, null, null, null, null);
						cursor1.moveToFirst();
						int str1 = cursor1.getInt(cursor1.getColumnIndex(ID));
						ContentValues conentvalue1 = new ContentValues();
						conentvalue1.put(DELETEINDEX, (str1 + 1));
						db.update(TABLE_NAME, conentvalue1, null, null);
						db.delete(TABLE_NAME, ID + " = " + str1, null);
						insert(str, db);
						cursor1.close();
						cursor.close();
					}
					
				}
				c.close();
				dbOpenHelper.close();
				db.close();
			}
		}.start();
		
	}

	private void insert(String str, SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		cv.put(PHONE_NUMBER, str);
		cv.put(DELETEINDEX, "0");
		db.insert(TABLE_NAME, null, cv);
	}
	
	public int selectLinkManList(){
		int count = 0;
		HDatabaseHelper dbOpenHelper = new HDatabaseHelper(context);
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		Cursor ct = db.rawQuery(SELECT_NUMBER,null);
		if(ct.moveToNext()){
			count = ct.getInt(0);
		}
		db.close();
		ct.close();
		return count;
	}

}
