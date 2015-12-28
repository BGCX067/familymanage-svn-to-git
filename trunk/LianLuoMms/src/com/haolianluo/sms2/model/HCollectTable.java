package com.haolianluo.sms2.model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.ui.sms2.HThreadAdapter;


public class HCollectTable extends ArrayList<HThread>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//表名
	private static final String TABLE_NAME = "collect_table";
	private static final String KEY_ID = "key_id";// HCollectDB数据库id
	private static final String KEY_SMS_ID = "key_smsId";// sms
	private static final String KEY_NAME = "key_name";
	private static final String KEY_ADDRESS = "key_address";
	private static final String KEY_BODY = "key_body";
	private static final String KEY_TIME = "key_time";
	private static final String KEY_TYPE = "key_type";
	private static final String KEY_READ = "key_read";
	private static final String KEY_CURRENT_TIME = "key_current_time";
	
	private Context mContext;
	private HThreadAdapter adapter;
	private List<HThread> list = new ArrayList<HThread>();
	//创建表的SQL语句
	public static final String CREATE_COLLECT_TABLE = "create table IF NOT EXISTS " + TABLE_NAME + " (" + KEY_ID + " integer primary key autoincrement, " + KEY_SMS_ID + " text,"+ 
			KEY_NAME + " text," + KEY_ADDRESS + " text,"+ KEY_BODY + " text," + KEY_TIME + " text," + KEY_TYPE + " text," + KEY_READ +" text," + KEY_CURRENT_TIME +" text);"; 
	public HCollectTable(Context context) {
		mContext = context;
	}
	
	
	/***
	 * 插入记录
	 * @param HThread
	 * @return
	 */
	public void insertNote(HSms sms) {
		HDatabaseHelper dbOpenHelper = new HDatabaseHelper(mContext);
		SQLiteDatabase sqldb = dbOpenHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(KEY_SMS_ID, sms.smsid);
		cv.put(KEY_NAME, sms.name);
		cv.put(KEY_ADDRESS, sms.address);
		cv.put(KEY_BODY, sms.body);
		cv.put(KEY_TIME, sms.time);
		cv.put(KEY_TYPE, sms.type);
		cv.put(KEY_READ, "1");
		cv.put(KEY_CURRENT_TIME, System.currentTimeMillis());
		sqldb.insert(TABLE_NAME, null, cv);
		dbOpenHelper.close();
	}
	
	public void updataNote(HSms sms){
		HDatabaseHelper dbOpenHelper = new HDatabaseHelper(mContext);
		SQLiteDatabase sqldb = dbOpenHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(KEY_SMS_ID, sms.smsid);
		cv.put(KEY_NAME, sms.name);
		cv.put(KEY_ADDRESS, sms.address);
		cv.put(KEY_BODY, sms.body);
		cv.put(KEY_TIME, sms.time);
		cv.put(KEY_TYPE, sms.type);
		cv.put(KEY_READ, "1");
		cv.put(KEY_CURRENT_TIME, System.currentTimeMillis());
		sqldb.update(TABLE_NAME, cv, KEY_SMS_ID + "=" + sms.smsid, null);
		dbOpenHelper.close();
	}
	
	/***
	 * 删除记录  
	 * @param id collectTable id
	 */
	public void deleteNote(int position) {
		HDatabaseHelper dbOpenHelper = new HDatabaseHelper(mContext);
		SQLiteDatabase sqldb = dbOpenHelper.getWritableDatabase();
		String id = list.get(position).sms.smsid;
		list.remove(position);
		adapter.notifyDataSetChanged();
		String str = null;
		Cursor cursor = sqldb.query(TABLE_NAME, null, KEY_SMS_ID +"="+id, null, null, null, null);
		while(cursor.moveToNext()){
			str = cursor.getString(cursor.getColumnIndex(KEY_ID));
		}
		sqldb.delete(TABLE_NAME, KEY_ID + "=" + str, null);
		dbOpenHelper.close();
		cursor.close();
	}

	/***
	 * 删除所有信息
	 */
	public void deleteNotes() {
		HDatabaseHelper dbOpenHelper = new HDatabaseHelper(mContext);
		SQLiteDatabase sqldb = dbOpenHelper.getWritableDatabase();
		list.clear();
		adapter.notifyDataSetChanged();
		sqldb.delete(TABLE_NAME, null, null);
		dbOpenHelper.close();
	}
	
	public boolean querydb(String id){
		HDatabaseHelper dbOpenHelper = new HDatabaseHelper(mContext);
		SQLiteDatabase sqldb = dbOpenHelper.getWritableDatabase();
		String where = KEY_SMS_ID+"="+id;
		Cursor cursor = sqldb.query(TABLE_NAME, null, where, null, null, null, null);
		while(cursor.moveToNext()){
			return true;
		}
		cursor.close();
		dbOpenHelper.close();
		return false;
	}
	
	/**
	 * 读取主界面,且返回主界面的adapter
	 */
	public HThreadAdapter loadCollectList(){
		HDatabaseHelper dbOpenHelper = new HDatabaseHelper(mContext);
		SQLiteDatabase sqldb = dbOpenHelper.getWritableDatabase();
		Cursor cursor = sqldb.query(TABLE_NAME, null, null, null, null, null, KEY_CURRENT_TIME + " desc");
		while(cursor.moveToNext()){
			HThread model = new HThread();
			model.sms.smsid = cursor.getString(cursor.getColumnIndex(KEY_SMS_ID));
			model.address = cursor.getString(cursor.getColumnIndex(KEY_ADDRESS));
			model.name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
			
//			if(model.name.equals(HConst.defaultName)){
//				model.name = model.address;
//			}
			model.name = new HAddressBookManager(mContext).getNameByNumber(model.address);
			model.sms.body = cursor.getString(cursor.getColumnIndex(KEY_BODY));
			model.sms.time = cursor.getString(cursor.getColumnIndex(KEY_TIME));
			model.sms.type = cursor.getString(cursor.getColumnIndex(KEY_TYPE));
			model.sms.read = "1";
			model.headbm = null;
			list.add(model);
		}
		cursor.close();
		dbOpenHelper.close();
		if(adapter == null){
			adapter = new HThreadAdapter(LayoutInflater.from(mContext),list);
		}else{
			adapter.setList(list);
		}
		return adapter;
	}
	
	/***
	 * 得到所有没名字的电话号码
	 * @param position
	 * @return
	 */
	public List<String> isGetName(HThreadAdapter threadAdapter ,int position){
		String name = threadAdapter.get(position).name;
		List<String>list = new ArrayList<String>();
		String []arr = name.split(",");
		int size = arr.length;
		for(int i = 0;i < size;i++){
			if(arr[i].equals(HConst.defaultName)){
				list.add(threadAdapter.get(position).address.split(",")[i]);
			}
		}
		return list;
	}
}
