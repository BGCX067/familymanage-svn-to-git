package com.haolianluo.sms2.model;

import java.util.ArrayList;
import com.haolianluo.sms2.data.HSmsApplication;
import com.lianluo.core.util.HLog;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

/**
 *  缓存数据库
 * @author jianhua  2011年10月10日12:58:55
 */

public class HBufferList extends ArrayList<HThread>{

	private static final long serialVersionUID = 1L;
	private HDatabaseHelper mDbOpenHelper;
	public SQLiteDatabase mDatabase;
	private HSmsApplication mApplication;
	
	/**表的一些信息*/
	//表的名字
	private static final String TABLE_NAME = "bufferlist";
	//表的一些字段
	private static final String _ID = "_id";
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String ADDRESS = "address";
	private static final String BODY = "body";
	private static final String TIME = "time";
	private static final String TYPE = "type";
	private static final String READ = "read";
	private static final String COUNT = "count";
	private static final String HEADBM = "headbm";
	private static final String ISMMS = "ismms";//是否是彩信  0-no 1-yes
	private static final String NOREADCOUNT = "noreadcount";
	
	//创建表的SQL语句
	public static final String CREATE_TABLE = "create table IF NOT EXISTS " + TABLE_NAME+ " " + "(" + _ID + " integer primary key autoincrement, " + ID + " text,"+ 
			NAME + " text," + ADDRESS + " text," + COUNT + " text," + BODY + " text," + TIME + " integer," + TYPE + " text," + READ +" text," + HEADBM + " text,"  
			+ ISMMS + " text," + NOREADCOUNT + " text);"; 

	
	
	
	public HBufferList(Application application){
		mDbOpenHelper = new HDatabaseHelper(application);
		mApplication = (HSmsApplication) application;
	} 
	

	@Override
	public boolean add(HThread model) {
		mDatabase = mDbOpenHelper.getWritableDatabase();
		ContentValues cv = getValue(model);
		mDatabase.insert(TABLE_NAME, null, cv);
		mDatabase.close();
		return super.add(model);
	}
	
	

	@Override
	public void add(int location, HThread model) {
		mDatabase = mDbOpenHelper.getWritableDatabase();
		ContentValues cv = getValue(model);
		mDatabase.insert(TABLE_NAME, ID, cv);
		mDatabase.close();
		super.add(location, model);
	}

	@Override
	public HThread remove(int location) {
		mDatabase = mDbOpenHelper.getWritableDatabase();
		String id = null;
		String address = null;
		id = mApplication.adapter.get(location).sms.threadid;
		address = mApplication.adapter.get(location).sms.address;
		if(id != null && !id.equals("")){
			mDatabase.delete(TABLE_NAME, ID + " = " + id, null);
		}else{
			if(address == null){
				address = mApplication.adapter.get(location).address;
			}
			mDatabase.delete(TABLE_NAME, ADDRESS + " = '" + address+"'", null);
		}
		mDatabase.close();
		return super.remove(location);
	}
	
	public Cursor queryBufferList(){
		Cursor cursor = null;
		mDatabase = mDbOpenHelper.getWritableDatabase();
		cursor = mDatabase.query(TABLE_NAME, null, null, null, null, null, "time desc");
		return cursor;
	}
	
	public void deleteBufferList(){
		mDatabase = mDbOpenHelper.getWritableDatabase();
		mDatabase.delete(TABLE_NAME, null, null);
		mDatabase.close();
	}
	
	public boolean cxBuffeListIsNull(){
		mDatabase = mDbOpenHelper.getWritableDatabase();
		Cursor cur = mDatabase.query(TABLE_NAME, null, null, null, null, null, null);
		if(cur.getCount() != 0){
			cur.close();
			mDatabase.close();
			return false;
		}
		cur.close();
		mDatabase.close();
		return true;
	}
	
	@Override
	public HThread set(int index, HThread model) {
		if(mDatabase == null || !mDatabase.isOpen()){
			mDatabase = mDbOpenHelper.getWritableDatabase();
		}
		ContentValues cv = getValue(model);
		String id = model.sms.threadid;
		if(id == null || id.equals("")){
			mDatabase.update(TABLE_NAME, cv, ADDRESS + "='" + model.address+"'", null);
		}else{
			mDatabase.update(TABLE_NAME, cv, ID + "=" + id, null);
		}
		mDatabase.close();
		return super.set(index, model);
	}
	
	private ContentValues getValue(HThread model){
		ContentValues cv = new ContentValues();
		cv.put(ID, model.sms.threadid);
		cv.put(NAME, model.name);
		cv.put(ADDRESS, model.address);
		cv.put(COUNT, model.count);
		cv.put(BODY, model.sms.body);
		cv.put(TIME, Long.parseLong(model.sms.time));
		cv.put(TYPE, model.type);
		cv.put(READ, model.sms.read);
		if(model.headbm == null){
			cv.put(HEADBM, "0");//没有头像
		}else{
			cv.put(HEADBM, "1");//有头像
		}
		cv.put(ISMMS, model.ismms);
		cv.put(NOREADCOUNT, "0");
		return cv;
	}
	
	
	
//	/**读取list*/
//	public List<HThread> loadBufferList(){
//		Cursor cursor = queryBufferList();
//		List<HThread> list = new ArrayList<HThread>();
//		while(cursor.moveToNext()){
//			HThread model = getHThread(cursor);
//			list.add(model);
//		}
//		cursor.close();
//		mDatabase.close();
//		return list;
//	}
	
	public HThread getHThread(Cursor cursor){
		HAddressBookManager abm = new HAddressBookManager(mApplication);
		HThread model = new HThread();
		model.sms.threadid = cursor.getString(cursor.getColumnIndex(HBufferList.ID));
		model.name = cursor.getString(cursor.getColumnIndex(HBufferList.NAME));
		model.address = cursor.getString(cursor.getColumnIndex(HBufferList.ADDRESS));
		model.count = cursor.getString(cursor.getColumnIndex(HBufferList.COUNT));
		model.sms.body = cursor.getString(cursor.getColumnIndex(HBufferList.BODY));
		model.sms.time = String.valueOf(cursor.getLong(cursor.getColumnIndex(HBufferList.TIME)));
		model.type = cursor.getString(cursor.getColumnIndex(HBufferList.TYPE));
		model.sms.read = cursor.getString(cursor.getColumnIndex(HBufferList.READ));
		String strHead =  cursor.getString(cursor.getColumnIndex(HBufferList.HEADBM));
		model.ismms = cursor.getString(cursor.getColumnIndex(HBufferList.ISMMS));
		//model.noReadCount = cursor.getString(cursor.getColumnIndex(HBufferList.NOREADCOUNT));
		Bitmap bitmap = null;
		if(strHead.equals("1")){
			bitmap = abm.getContactPhoto(model.name, model.address);
		}
		model.headbm = bitmap;
		return model;
	}

	
	public void  updataDB(HSms sms,HSmsApplication application){
		HLog.i("updata buffer list--------------------->>>");
		mDatabase = mDbOpenHelper.getWritableDatabase();
		//String where = ADDRESS + "=" + sms.address;
		String where = ID + "=" + sms.threadid;
		Cursor cursor = mDatabase.query(TABLE_NAME, null, where, null, null, null, null);
		int len = sms.address.split(",").length;
		if(cursor.getCount() > 0){//旧信息
			cursor.moveToFirst();
			String where1 = ID + "=" + sms.threadid;
			HThread model = getHThread(cursor);
			String count = cursor.getString(cursor.getColumnIndex(COUNT));
			String noReadSmsCount = cursor.getString(cursor.getColumnIndex(NOREADCOUNT));
			String type = model.type;
			mDatabase.delete(TABLE_NAME, where1, null);
			ContentValues values = getValue(model);
			
			if(!sms.type.equals("3")){
				values.put(TYPE, type); 
			}else{
				values.put(TYPE, sms.type); 
			}	
			
			values.put(ADDRESS, sms.address); 
			values.put(READ, sms.read); 
			values.put(BODY, sms.body);
			values.put(NOREADCOUNT, String.valueOf(Integer.parseInt(noReadSmsCount) + 1));
			if(!sms.type.equals("3")){
				values.put(COUNT, String.valueOf(Integer.parseInt(count) + len));
			}
			values.put(TIME, Long.parseLong(sms.time));
			mDatabase.insert(TABLE_NAME, null, values);
		}else{//新信息
			HAddressBookManager abm = new HAddressBookManager(application);
			HThread thread = new HThread(application);
			thread.address = sms.address;
			thread.sms.body = sms.body;
			thread.count = "1";
			thread.sms.threadid = sms.threadid;
			thread.name = abm.getNameByNumber(sms.address);
			thread.type =  sms.type;
			thread.sms.read = sms.read;
			if(!thread.type.equals("3")){
				thread.count = String.valueOf(len);
			}
			Bitmap headBp = null;
			if(thread.name.split(",").length == 1){
				headBp = abm.getContactPhoto(thread.name, thread.address);
			}
			thread.headbm = headBp;
			thread.mark = "0";
			thread.sms.time = sms.time;
			ContentValues cv = getValue(thread);
			mDatabase.insert(TABLE_NAME, null, cv);
		}
		cursor.close();
		mDatabase.close();
		
	}
	
	
	
	/**未读改成已读*/
	public void updataBufferList(HSms sms){
		if(sms.threadid.equals("")){
			return;
		}
		mDatabase = mDbOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(READ, "1"); // 修改短信为已读模式
		String where = ID + "=" + sms.threadid;
		mDatabase.update(TABLE_NAME, values, where, null);
		mDatabase.close();
	}
	
	/***
	 * 反的进来需要单独更新
	 */
	public void updataName(String name,String address){
		mDatabase = mDbOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(NAME, name); 
		values.put(HEADBM, "1"); 
		String where = ADDRESS + "=" + address;
		mDatabase.update(TABLE_NAME, values, where, null);
		mDatabase.close();
	}
	
	
	public void updataSms(String time,String nr,String threadID,String type){
		mDatabase = mDbOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		String where = ID + "=" + threadID;
		Cursor cursor = mDatabase.query(TABLE_NAME, null, where, null, null, null, null);
		cursor.moveToFirst();
		String count = cursor.getString(cursor.getColumnIndex(COUNT));
		
		values.put(BODY, nr); 
		values.put(TIME, Long.parseLong(time));
		if(!type.equals("3")){
			values.put(COUNT, String.valueOf(Integer.parseInt(count) - 1));
		}
		mDatabase.update(TABLE_NAME, values, where, null);
		mDatabase.close();
		cursor.close();
	}
	
	public void deleteBufferItem(String threadId){
		mDatabase = mDbOpenHelper.getWritableDatabase();
		String where = ID + "=" + threadId;
		mDatabase.delete(TABLE_NAME, where, null);
		mDatabase.close();
	}
}
