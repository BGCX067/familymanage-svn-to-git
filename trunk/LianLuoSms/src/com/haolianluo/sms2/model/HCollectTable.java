package com.haolianluo.sms2.model;

import java.util.ArrayList;
import java.util.List;

import com.haolianluo.sms2.HTalkAdapter;
import com.haolianluo.sms2.HThreadAdapter;
import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.data.HSmsApplication;


import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;


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
	private HThreadManager mThreadManager;
	private HSmsManage mSmsManage;
	//创建表的SQL语句
	public static final String CREATE_COLLECT_TABLE = "create table IF NOT EXISTS " + TABLE_NAME + " (" + KEY_ID + " integer primary key autoincrement, " + KEY_SMS_ID + " text,"+ 
			KEY_NAME + " text," + KEY_ADDRESS + " text,"+ KEY_BODY + " text," + KEY_TIME + " text," + KEY_TYPE + " text," + KEY_READ +" text," + KEY_CURRENT_TIME +" integer);"; 
	public HCollectTable(Context context) {
		mContext = context;
		mThreadManager = new HThreadManager((Application)context.getApplicationContext());
		mSmsManage = new HSmsManage((Application)context.getApplicationContext());
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
	public boolean deleteNote(int position) {
		HDatabaseHelper dbOpenHelper = new HDatabaseHelper(mContext);
		SQLiteDatabase sqldb = dbOpenHelper.getWritableDatabase();
		String address = mThreadManager.getCollectAdapter().get(position).address;
		sqldb.delete(TABLE_NAME, KEY_ADDRESS + "=" + "'" +address + "'",null);
		dbOpenHelper.close();
		mThreadManager.getCollectAdapter().remove(position);
		int size = mThreadManager.getCollectAdapter().size();
		if(size > 0){
			return false;
		}
		return true;
	}
	
	
	/***
	 * 删除记录  
	 * @param id collectTable id
	 * @param threadPosition list id
	 */
	public boolean deleteNoteForSmsID(int position, int threadPosition) {
		HDatabaseHelper dbOpenHelper = new HDatabaseHelper(mContext);
		SQLiteDatabase sqldb = dbOpenHelper.getWritableDatabase();
		String smsId = mSmsManage.getAdapter().get(position).smsid;
		String address = mSmsManage.getAdapter().get(position).address;
		sqldb.delete(TABLE_NAME, KEY_SMS_ID + "=" + "'" +smsId + "'",null);
		dbOpenHelper.close();
		mSmsManage.getAdapter().remove(position);
		int size = mSmsManage.getAdapter().size();
		if(size > 0){
			String body = mSmsManage.getAdapter().get(size - 1).body;
			String _id = mSmsManage.getAdapter().get(size - 1)._id;
			String ismms = mSmsManage.getAdapter().get(size - 1).ismms;
			String name = mSmsManage.getAdapter().get(size - 1).name;
			String read = mSmsManage.getAdapter().get(size - 1).read;
			String smsid = mSmsManage.getAdapter().get(size - 1).smsid;
			String time = mSmsManage.getAdapter().get(size - 1).time;
			String threadid = mSmsManage.getAdapter().get(size - 1).threadid;
			
			for(int i = 0;i < mThreadManager.getCollectAdapter().size();i++){
				if(mThreadManager.getCollectAdapter().get(i).address.equals(address)){
					mThreadManager.getCollectAdapter().get(i).sms.address = address;
					mThreadManager.getCollectAdapter().get(i).sms.body = body;
					mThreadManager.getCollectAdapter().get(i).sms._id = _id;
					mThreadManager.getCollectAdapter().get(i).ismms = ismms;
					mThreadManager.getCollectAdapter().get(i).sms.name = name;
					mThreadManager.getCollectAdapter().get(i).sms.read = read;
					mThreadManager.getCollectAdapter().get(i).sms.smsid = smsid;
					mThreadManager.getCollectAdapter().get(i).sms.time = time;
					mThreadManager.getCollectAdapter().get(i).sms.threadid = threadid;
					mThreadManager.getCollectAdapter().get(i).count = String.valueOf(size);
					mThreadManager.getCollectAdapter().notifyDataSetChanged();
					break;
				}
			}
			mSmsManage.getAdapter().notifyDataSetChanged();
			return false;
		}else{
			mThreadManager.getCollectAdapter().remove(threadPosition);
			mThreadManager.getCollectAdapter().notifyDataSetChanged();
		}
		return true;
	}

	/***
	 * 删除所有信息
	 */
	public void deleteNotes() {
		HDatabaseHelper dbOpenHelper = new HDatabaseHelper(mContext);
		SQLiteDatabase sqldb = dbOpenHelper.getWritableDatabase();
		mThreadManager.getCollectAdapter().clear();
		mThreadManager.getCollectAdapter().notifyDataSetChanged();
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
		sqldb.close();
		return false;
	}
	
	
	public void loadCollectList(){
		HAddressBookManager abm = new HAddressBookManager(mContext);
		HDatabaseHelper dbOpenHelper = new HDatabaseHelper(mContext);
		SQLiteDatabase sqldb = dbOpenHelper.getWritableDatabase();
		String sql = "select * from "+ TABLE_NAME +" group by " + KEY_ADDRESS+ " order by " + KEY_CURRENT_TIME + " desc ";
		Cursor cursor = sqldb.rawQuery(sql, null);
		List<HThread> list = new ArrayList<HThread>();
		while(cursor.moveToNext()){
			HThread model = new HThread();
			model.sms.smsid = cursor.getString(cursor.getColumnIndex(KEY_SMS_ID));
			model.address = cursor.getString(cursor.getColumnIndex(KEY_ADDRESS));
			model.name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
			model.name = new HAddressBookManager(mContext).getNameByNumber(model.address);
			model.sms.body = cursor.getString(cursor.getColumnIndex(KEY_BODY));
			model.sms.time = cursor.getString(cursor.getColumnIndex(KEY_TIME));
			model.sms.type = cursor.getString(cursor.getColumnIndex(KEY_TYPE));
			model.type = cursor.getString(cursor.getColumnIndex(KEY_TYPE));
			model.sms.read = "1";
			model.headbm = null;
			Bitmap bitmap  = abm.getContactPhoto(model.name, model.address);
			model.headbm = bitmap;
			String where = KEY_ADDRESS + "='" + model.address +"'";
			Cursor cur = sqldb.query(TABLE_NAME, null, where, null, null, null, null);
			model.count = String.valueOf(cur.getCount());
			cur.close();
			list.add(model);
		}
		cursor.close();
		dbOpenHelper.close();
		if(mThreadManager.getCollectAdapter() != null && mThreadManager.getCollectAdapter().size() == 0){
			mThreadManager.getCollectAdapter().setList(list);
		}
	}
	
	
	public void loadTalkSms(String address,HTalkAdapter adapter){
		HDatabaseHelper dbOpenHelper = new HDatabaseHelper(mContext);
		SQLiteDatabase sqldb = dbOpenHelper.getWritableDatabase();
		String where = KEY_ADDRESS + "='" + address +"'";
		Cursor cursor = sqldb.query(TABLE_NAME, null, where, null, null, null, null);
		System.out.println("cursor ---------------" + cursor.getCount() + "address ---" + address);
		while(cursor.moveToNext()){
			HSms sms = new HSms();
			//sms._id = cursor.getString(cursor.getColumnIndex(KEY_SMS_ID));
			sms.address = cursor.getString(cursor.getColumnIndex(KEY_ADDRESS));
			sms.body = cursor.getString(cursor.getColumnIndex(KEY_BODY));
			sms.ismms = "0";
			sms.name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
			sms.read = "1";
			sms.smsid = cursor.getString(cursor.getColumnIndex(KEY_SMS_ID));
			sms.time = cursor.getString(cursor.getColumnIndex(KEY_TIME));
			sms.type = cursor.getString(cursor.getColumnIndex(KEY_TYPE));
			adapter.add(sms);
		}
		cursor.close();
		sqldb.close();
	}
	
	
//	/**
//	 * 读取主界面,且返回主界面的adapter
//	 */
//	public HThreadAdapter loadCollectList(){
//		HDatabaseHelper dbOpenHelper = new HDatabaseHelper(mContext);
//		SQLiteDatabase sqldb = dbOpenHelper.getWritableDatabase();
//		Cursor cursor = sqldb.query(TABLE_NAME, null, null, null, null, null, KEY_CURRENT_TIME + " desc");
//		while(cursor.moveToNext()){
//			HThread model = new HThread();
//			model.sms.smsid = cursor.getString(cursor.getColumnIndex(KEY_SMS_ID));
//			model.address = cursor.getString(cursor.getColumnIndex(KEY_ADDRESS));
//			model.name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
//			
////			if(model.name.equals(HConst.defaultName)){
////				model.name = model.address;
////			}
//			model.name = new HAddressBookManager(mContext).getNameByNumber(model.address);
//			model.sms.body = cursor.getString(cursor.getColumnIndex(KEY_BODY));
//			model.sms.time = cursor.getString(cursor.getColumnIndex(KEY_TIME));
//			model.sms.type = cursor.getString(cursor.getColumnIndex(KEY_TYPE));
//			model.sms.read = "1";
//			model.headbm = null;
//			list.add(model);
//		}
//		cursor.close();
//		dbOpenHelper.close();
//		if(adapter == null){
//			adapter = new HThreadAdapter(LayoutInflater.from(mContext),list);
//		}else{
//			adapter.setList(list);
//		}
//		return adapter;
//	}
	
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
	
	/**根据内容搜索*/
	public StringBuffer doSearchList(String str,HSmsApplication mApplication) {
		HDatabaseHelper dbOpenHelper = new HDatabaseHelper(mContext);
		SQLiteDatabase sqldb = dbOpenHelper.getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		String where = "select " + KEY_ADDRESS + " from "+ TABLE_NAME + " group  by "  + "KEY_ADDRESS having " +  KEY_BODY + " like " + "'%"  + str + "%'";
		Cursor cur = sqldb.rawQuery(where, null);
		cur.moveToLast();
		while (!cur.isBeforeFirst()) {
			String address = cur.getString(cur.getColumnIndex(KEY_ADDRESS));
			if (address != null && address.length() > 0) {
				sb.append(address).append(",");
			}
			cur.moveToPrevious();
		}
		cur.close();
		sqldb.close();
		return sb;
	}	
	
	
}
