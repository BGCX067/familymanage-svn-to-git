package com.haolianluo.sms2.model;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.Adapter;
import android.widget.Toast;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.data.HSharedPreferences;
import com.haolianluo.sms2.data.HSmsApplication;
import com.lianluo.core.util.HLog;
import com.haolianluo.sms2.R;
import com.haolianluo.sms2.ui.sms2.HSearchAdapter;
import com.haolianluo.sms2.ui.sms2.HThreadAdapter;

/***
 * 对thread界面的所有功能进行管理<br>
 * （调用HMessageModel函数）
 * @author jianhua  2011年11月18日21:44:48
 */

public class HThreadManager {
	
	private HSmsApplication mApplication;
	private HThread mSmsModel = null;
	private Dialog mDialog = null;
	private HSharedPreferences mSharedPreferences;
	private long preTime = 0;//上次的时间
	private final int UPDATA_UI = 0;
	private final int UPDATA_UI_DELETEALLSMS = 1;
	private final int UPDATA_READ = 2;
	private final int UPDATA_NOTIFY = 3;
	/**搜索Adapter*/
	private HSearchAdapter searchAdapter;
	
	public HThreadManager(Application application){
		mApplication = (HSmsApplication) application;
		mSmsModel = new HThread(mApplication);
		mSharedPreferences = new HSharedPreferences(mApplication);
	}
	
	
	/**
	 * 读取主界面,且返回主界面的adapter
	 */
	public Adapter loadThreadList(){
		HBufferList bufferList = new HBufferList(mApplication);
		mApplication.adapter = new HThreadAdapter(LayoutInflater.from(mApplication));
		if(mSharedPreferences.getIsReadBuffer()){
			HLog.i("读取缓存------------>>>>");
			mApplication.adapter.clear();
			readBuffer();
		}else{
			HLog.i("读取数据库------------>>>>");
			mSharedPreferences.setIsReadBuffer(true);
			bufferList.deleteBufferList();
			readDB();
			
		}
		return mApplication.adapter;
	}
	
	private void readBuffer(){
//		new Thread(){
//			public void run(){
				HBufferList bufferList = new HBufferList(mApplication);
				Cursor cursor = bufferList.queryBufferList();
				List<HThread> list = new ArrayList<HThread>();
				while(cursor.moveToNext()){
					HThread model = bufferList.getHThread(cursor);
					if(model.sms.threadid != null && model.sms.threadid != ""){
						list.add(model);
					}
					if(list.size() == 10 && mApplication.adapter != null){
						mApplication.adapter.addAll(list);
						list.clear();
						handler.sendEmptyMessage(UPDATA_UI);
					}
				}
				if(HConst.str_back != null && mApplication.adapter != null){
					handler.sendEmptyMessage(UPDATA_READ);
				}
				if(list.size() != 0 && mApplication.adapter != null){
					mApplication.adapter.addAll(list);
					list.clear();
					handler.sendEmptyMessage(UPDATA_UI);
				}
				cursor.close();
				bufferList.mDatabase.close();
//			}
//		}.start();
	}
	
	
	/***
	 * 读取数据库
	 */
	private void readDB() {
//		new Thread() {
//			public void run() {
				int[] position = new int[14];
				int readSmsNumber = 0;
				int readAllSmsNumber = 0;
				if (mApplication.adapter.size() > 0) {
					mApplication.adapter.clear();
				}

				Cursor cursor = mApplication.getContentResolver().query(Uri.parse("content://mms-sms/conversations?simple=true"), null, "date is not null", null,"date desc");
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					HThread model = getModel(cursor,position);
					if (model != null) {
						addThread(model);
					}
					cursor.moveToNext();

					readSmsNumber++;
					readAllSmsNumber++;
					if (readSmsNumber == 10) {
						readSmsNumber = 0;
						handler.sendEmptyMessage(UPDATA_UI);
					}
				}
				cursor.close();
				if (HConst.isHtc) {
					if(mApplication.adapter != null){
						mSmsModel.readDraftHtc();
					}else{
						mSharedPreferences.setIsReadBuffer(false); 
					}
					handler.sendEmptyMessage(UPDATA_UI);
				}
				if(HConst.str_back != null && mApplication.adapter != null){
					handler.sendEmptyMessage(UPDATA_READ);
				}
				if (readAllSmsNumber % 10 != 0) {
					handler.sendEmptyMessage(UPDATA_UI);
				}
//			}
//		}.start();
	}
	
	
	private HThread getModel(Cursor cursor,int[] position){
		HThread model = null;
		if(!HConst.isHtc){
			model =  mSmsModel.getModel(cursor, position);
		}else{
			model =  mSmsModel.getHtcModel(cursor, position);
		}
		return model;
	}
	
	
	public HThread getThreadForThreadId(String threadId){
		String where = "_id" + "=" + "'" +threadId +"'";
		Cursor cursor = mApplication.getContentResolver().query(Uri.parse("content://mms-sms/conversations?simple=true"), null, where, null,null);
		cursor.moveToFirst();
		int []position = new int[14];
		HThread model = getModel(cursor,position);
		cursor.close();
		return model;
	}
	
	
	/***
	 * 界面的刷新
	 */
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case UPDATA_UI:
				ncThread();
				break;
			case UPDATA_UI_DELETEALLSMS:
				if(mDialog != null && mDialog.isShowing()){
					mDialog.hide();
					mDialog.cancel();
				}
				ncThread();
				break;
			case UPDATA_READ:
				int p = getPosition(HConst.str_back);
				HLog.e( p+ "HConst.str_back" + HConst.str_back);
				noReadChageRead(p);
				ncThread();
				break;
			case UPDATA_NOTIFY:
				updataNotify();
				break;
			}
		};
	};
	
	
	/***
	 * 得到所有没名字的电话号码
	 * @param position
	 * @return
	 */
	public List<String> isGetName(int position){
		String name = mApplication.adapter.get(position).name;
		List<String>list = new ArrayList<String>();
		String []arr = name.split(",");
		int size = arr.length;
		for(int i = 0;i < size;i++){
			if(arr[i].equals(HConst.defaultName)){
				list.add(mApplication.adapter.get(position).address.split(",")[i]);
			}
		}
		return list;
	}
	
	/***
	 * 得到名字
	 */
	public String getName(int position){
		if(mApplication.adapter == null){
			return "";
		}
		String name = mApplication.adapter.get(position).name;
		return name;
	}
	
	/***
	 * 得到电话号码
	 */
	public String getAddress(int position){
		if(mApplication.adapter == null){
			return "";
		}
		String name = mApplication.adapter.get(position).address;
		return name;
	}
	
	
	/***
	 * 删除一个thread
	 * @param position
	 */
	public void deleteThreadItem(int position,int index){
		if(HConst.isSearchActivity && index != -1){//搜索
			String id = searchAdapter.get(position).thread.sms.threadid;
			String address = searchAdapter.get(position).thread.address;
			searchAdapter.remove(position);//删除搜索list
			handler.sendEmptyMessage(UPDATA_UI);
			mApplication.adapter.remove(index);//删除主页面list
			deleteThreadItemDB(id,address);//删除数据库
		}else{
			String id = mApplication.adapter.get(position).sms.threadid;
			String address = mApplication.adapter.get(position).address;
			mApplication.adapter.remove(position);
			handler.sendEmptyMessage(UPDATA_UI);
			deleteThreadItemDB(id,address);
		}
	}
	
	
	/***
	 * 删除一个thread
	 * @param position
	 */
	public void deleteMark(int position,int index){
		HLog.i("deleteMark " + index);
		if(HConst.isSearchActivity && index != -1){//搜索
			String id = searchAdapter.get(position).thread.sms.threadid;
			String address = searchAdapter.get(position).thread.address;
			searchAdapter.remove(position);//删除搜索list
			mApplication.adapter.remove(index);//删除主页面list
			deleteThreadItemDB(id,address);//删除数据库
		}else{
			String id = mApplication.adapter.get(position).sms.threadid;
			String address = mApplication.adapter.get(position).address;
			mApplication.adapter.remove(position);
			deleteThreadItemDB(id,address);
		}
		
	}
	
	private void deleteThreadItemDB(final String id,final String address){
		new Thread(){
			public void run(){
				mSmsModel.deleteThreadItem(id,address);
			}
		}.start();
	}
	
	/***
	 * 将未读改为已读
	 * @param position
	 */
	public void noReadChageRead(int position){
		HLog.e("noReadChageRead ===" + position);
		if( mApplication.adapter == null || position == -1){
			return;
		}
		String read = mApplication.adapter.get(position).sms.read;
		String id = mApplication.adapter.get(position).sms.threadid;
		String issms = mApplication.adapter.get(position).ismms;
		if(read.equals("0")){
			HThread model = mApplication.adapter.get(position);
			model.sms.read = "1";
			mApplication.adapter.set(position, model);
			noReadToRead(id);
		}
		if(issms.equals("1")){
			noReadToReadForMms(id);
		}
	}
	
	public void updataNameAndBitmap(String name,Bitmap bp,int position){
		HThread thread = mApplication.adapter.get(position);
		thread.name = name;
		thread.headbm = bp;
		mApplication.adapter.set(position, thread);
	}
	
	
	/***
	 * 将一组thread记录改为已读
	 * @param numread =0 and
	 */
	public void noReadToRead(final String tId) {
		new Thread(){
			public void run(){
				if(tId != null && !tId.equals("")){
					String where = "thread_id =" + tId;
					ContentValues values = new ContentValues();
					values.put("read", "1"); // 修改短信为已读模式
					mApplication.getContentResolver().update(Uri.parse("content://sms/"),values, where,null);
					handler.sendEmptyMessage(UPDATA_NOTIFY);
				}
			}
		}.start();
	}
	
	private void noReadToReadForMms(final String tId){
		new Thread(){
			public void run(){
				if(tId != null && !tId.equals("")){
					String where = "thread_id =" + tId;
					ContentValues values = new ContentValues();
					values.put("read", "1"); // 修改短信为已读模式
					mApplication.getContentResolver().update(Uri.parse("content://mms/"),values, where,null);
				}
			}
		}.start();
	}
	
	
	/***
	 * 根据id改为已读
	 * @param id
	 */
	public void noReadToRead_ID(final String id){
		new Thread() {
			public void run() {
				ContentValues values = new ContentValues();
				values.put("read", 1);// 已读				
				mApplication.getContentResolver().update(Uri.parse("content://sms/"),values, "_id" + "=" + id, null);
				handler.sendEmptyMessage(UPDATA_NOTIFY);
				
			}
		}.start();
	}
	
	
	/**
	 * 删除所有短信
	 * @param isHaveDialog 是否在删除的时候有dialog做一个等待
	 * @param title
	 * @param message
	 */
	public void deleteAllSms(boolean isHaveDialog,Context context,String title,String message){
		int size = mApplication.adapter.size();
		if(size == 0){
			Toast.makeText(context, R.string.nosms,Toast.LENGTH_SHORT).show();
			return;
		}
		if(isHaveDialog){
			mDialog = createProgressDialog(context,null,message);
			mDialog.show();
		}
		if(HConst.isSearchActivity){//搜索
			searchAdapter.clear();
		}
		new Thread(){
			public void run(){
				mSmsModel.deleteAllSms();
				mApplication.adapter.clear();
				HSharedPreferences spf = new HSharedPreferences(mApplication);
				spf.setIsReadBuffer(false);
				HBufferList buffer = new HBufferList(mApplication);
				buffer.deleteBufferList();
				handler.sendEmptyMessage(UPDATA_NOTIFY);
				handler.sendEmptyMessage(UPDATA_UI_DELETEALLSMS);
			}
		}.start();
	}
	
	/***
	 * ProgressDialog的创建
	 * @param title  没有title的时候传入null
	 * @param message
	 * @param 屏蔽掉了返回键、搜索键、menu键
	 * @return
	 */
	private ProgressDialog createProgressDialog(Context context,String title,String message){
		ProgressDialog pd = new ProgressDialog(context);
		pd.setCancelable(false);
		if(title != null){
			pd.setTitle(title);
		}
		pd.setMessage(message);
		pd.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_MENU) {
					return true;
				}
				return false;
			}
		});
		return pd;
	}
	
	/***
	 * 更新threadList
	 * @param application
	 * @param threadposition
	 */
    public void updataList(int threadposition){
    	mSmsModel.addContactsUpdataList(threadposition);
    }
    
    public void updataListSearch(int position){
    	mSmsModel.addContactsUpdataListSearch(position);
    }
    
    
    public void search(final String s,final int which,int before,int searchStartSize){
		preTime = System.currentTimeMillis();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				long curTime = System.currentTimeMillis();
				
            	if(curTime - preTime >= 200){
				if(s.length() > 0){//搜索框不为空
					if(searchAdapter.size() != 0){
						searchAdapter.clear();
					}
					for(int i = 0; i < mApplication.adapter.size(); i++){
						switch(which){
						case 0://通过联系人姓名 
							try{
								if (mApplication.adapter.get(i).name.matches(".*"+retConstraintStr(s)+".*")) {
									HSearch s = new HSearch();
									s.index = i;
									s.thread = mApplication.adapter.get(i);
									searchAdapter.add(s);
		  						}
							}catch(Exception ex){
								break;
							}
							break;
						case 1://通过电话号码
							try{
								if (mApplication.adapter.get(i).address.matches(".*"+retConstraintStr(s)+".*")) {
									HSearch s = new HSearch();
									s.index = i;
									s.thread = mApplication.adapter.get(i);
									searchAdapter.add(s);
	      						}
							}catch(Exception ex){
								break;
							}
							break;
						case 2://通过信息内容
							StringBuffer sb = mApplication.adapter.get(0).sms.doSearchList(s.toString(),mApplication);
							String []arrThreadID = array_unique(sb.toString().split(","));
							for(int j = 0;j < arrThreadID.length;j++){
								if(mApplication.adapter.get(i).sms.threadid.equals(arrThreadID[j])){
									HSearch s = new HSearch();
									s.index = i;
									s.thread = mApplication.adapter.get(i);
									searchAdapter.add(s);
								}
							}
							break;
						}
					}
					searchAdapter.notifyDataSetChanged();
				}
            }
			}
		},200);
		
		if(s.length() == 0){//搜索框为空
			searchStartSize = toSearchAdapter(searchAdapter);
			handler.sendEmptyMessage(UPDATA_UI);
		}
    }
    
    public void clearSearchAdapter(){
    	searchAdapter.clear();
    }
    
    /***
     * 将所有threadAdapter 转为HSearchAdapter
     * @param searchAdapter
     * @return
     */
    public int toSearchAdapter(HSearchAdapter searchAdapter){
    	if(searchAdapter != null){
    		searchAdapter.clear();
    		for(int i=0;i<mApplication.adapter.size();i++){
    			HSearch se = new HSearch();
    			se.index = -1;
    			se.thread = mApplication.adapter.get(i);
    			searchAdapter.add(se);
    		}
    	}
    	return searchAdapter.size();
    }
    
    /***
     * 使用Match时对特殊字符进行处理
     * @param constraint
     * @return
     */
    private String retConstraintStr(final CharSequence constraint) {
		String str;
		str = constraint.toString();
		if(str != null){
			str = str.replace("+", "\\+");
			str = str.replace("*", "\\*");
			str = str.replace(".", "\\.");
		}
		return str;
	}   
    
    /***
     * 去掉重复项
     * @param a
     * @return
     */
    public static String[] array_unique(String[] a) {   
  	  
	    List<String> list = new LinkedList<String>();   
	    for(int i = 0; i < a.length; i++) {   
	        if(!list.contains(a[i])) {   
	            list.add(a[i]);   
	        }   
	    }   
	    return (String[])list.toArray(new String[list.size()]);   
	}  
    
    /***
     * 退出搜索完后刷新屏幕
     */
//	public void updataThreadList(){
//		mApplication.adapter.setList(mApplication.threadSmsList);
//		notifyChangeThread();
//	}
	
	/***
	 * 取消标记
	 */
	public void cancel_mark(){
		for(int i = 0;i < mApplication.adapter.size();i++){
			mApplication.adapter.get(i).mark = "0";
	    }
		ncThread();
	}
	
	/***
	 * 标记全部
	 */
	public void all_mark(){
		if(HConst.isMark && HConst.isSearchActivity){
			for(int i = 0; i < searchAdapter.size();i++){
				searchAdapter.get(i).thread.mark = "1";
			}
		}else{
			for(int i = 0;i < mApplication.adapter.size();i++){
				mApplication.adapter.get(i).mark = "1";
			}
		}
		ncThread();
	}
	
	/***
	 * 删除标记
	 */
	public void delete_mark(Context context,String message) {
		mDialog = createProgressDialog(context,null,message);
		mDialog.show();
		new Thread(){
			public void run(){
				for (int i = 0; i < mApplication.adapter.size();){
					if (mApplication.adapter.get(i).mark.equals("1")) {
						deleteMark(i,-1);
					    handler.sendEmptyMessage(UPDATA_NOTIFY);
					}else{
						i++;
					}
				}
				if(HConst.isMark && HConst.isSearchActivity){
					for(int i = 0; i < searchAdapter.size();){
						if (searchAdapter.get(i).thread.mark.equals("1")) {
							searchAdapter.remove(i);
						}else{
							i++;
						}
					}
				}
				handler.sendEmptyMessage(UPDATA_UI_DELETEALLSMS);
			}
		}.start();
	}
	
	/***
	 * 刷新ThreadAdapter
	 */
	public void ncThread(){
		if(HConst.isSearchActivity){
			searchAdapter.notifyDataSetChanged();
		}else{
			if(mApplication.adapter != null){
				mApplication.adapter.notifyDataSetChanged();
			}
		}
	}
	
	public int applicationAdapterSize(){
		return mApplication.adapter.size();
	}
	
	public HSearchAdapter getSearchAdapter(){
		return searchAdapter;
	}
	public void setSearchAdapter(HSearchAdapter adapter){
		searchAdapter = adapter;
	}
	
	public HThreadAdapter getThreadAdapter(){
		return mApplication.adapter;
	}
	public void setThreadAdapter(HThreadAdapter adapter){
		mApplication.adapter = adapter;
	}
	
	 public void clearThreadAdapter(){
		 if(mApplication.adapter == null){
			 HLog.i("mApplication.adapter is null");
			 return;
		 }
		 mApplication.adapter.clear();
		 mApplication.adapter = null;
	 }
	 
	 /***
		 * 查询数据库判断是否为HTC系统，并将boolean保存
		 */
	 public boolean IsHTC() {
		boolean b = mSmsModel.queryIsHTC();
		new HSharedPreferences(mApplication).setIsHtc(b);
		return b;
	}
	 
	public void updataNotify(){
		mSmsModel.sms.notification_updata(mApplication);
	}
	
	public int getPosition(String address){
		if(address == null){
			return -1;
		}
		return mSmsModel.getPosition(address);
	}
	
	public void addThread(HThread thread){
		if(mApplication.adapter != null){
			if(thread.sms.threadid != null && thread.sms.threadid != ""){
				mApplication.adapter.add(thread);
			}
		}else{
			mSharedPreferences.setIsReadBuffer(false); 
		}
	}

	public int getPositionSearch(String address){
		if(searchAdapter == null){
			return -1;
		}
		String findstr = mSmsModel.getString(address);
		for(int i = 0;i < searchAdapter.size();i++){
			String adapterAddress = searchAdapter.get(i).thread.address;
			String finds = mSmsModel.getString(adapterAddress);
			if(findstr.equals(finds)){
				return i;
			}
		}
		return -1;
	}
}
