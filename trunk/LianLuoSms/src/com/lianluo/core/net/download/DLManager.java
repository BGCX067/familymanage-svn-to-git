package com.lianluo.core.net.download;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.haolianluo.sms2.R;
import com.haolianluo.sms2.model.HResDatabaseHelper;
import com.haolianluo.sms2.model.HResProvider;
import com.lianluo.core.util.HLog;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class DLManager  {
	private static final String TAG = "DLManager";
	private List<DLData> mAllTasks = new ArrayList<DLData>();
	private List<DLData> mActiveTasks = new ArrayList<DLData>();
	private List<DLData> mToStartTasks = new ArrayList<DLData>();
	//private SQLiteDatabase mDatabase;
    private Handler mProgressHandler;
	private Download mDownload = null;
	public static String LOCAL_PATH;
	public static String INSTALL_PATH = Environment.getExternalStorageDirectory() + File.separator + "lianluosms2";
	private Context mContext;
	private static DLManager mDLManager;
	//the delete type 
	public static final int DELETE_BY_FILENAME = 0;
	public static final int DELETE_BY_PACKAGENAME = 1;
	
	public static final int ACTION_UPDATE = 0;
	public static final int ACTION_DELETE = 1;
	public static final int ACTION_ADD = 2;
	public static final int ACTION_SUCCESS = 3;
	
	//download status
	protected static final int DOWNLOAD_SUCCESS = 0;
	protected static final int DOWNLOAD_FAIL = 1;
	protected static final int DOWNLOAD_STOP = 2;
	
	/**
	 * waiting for downloading
	 */
	public final static int STATUS_READY    = 1 << 0;

	/**
	 * downlaoding
	 */
	public final static int STATUS_RUNNING    = 1 << 1;

	/**
	 * download stopped
	 */
	public final static int STATUS_PAUSE     = 1 << 2;

	/**
	 * download finished
	 */
	public final static int STATUS_SUCCESS = 1 << 3;
	
	/***
	 * Constructor of download manager
	 * @param context a context for it to use
	 */
	private DLManager(Context context) {
		mContext = context;
		IntentFilter filter = new IntentFilter(Intent.ACTION_SHUTDOWN);
		mContext.getApplicationContext().registerReceiver(mShutDownReceiver, filter);
		LOCAL_PATH = mContext.getApplicationContext().getFilesDir().getAbsolutePath();
		HLog.d(TAG, "local path:" + LOCAL_PATH);
		//mDatabase = new DLSQLiteHelper(mContext).getWritableDatabase();
		mProgressHandler = new Handler();
	}

	private BroadcastReceiver mShutDownReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equalsIgnoreCase(Intent.ACTION_SHUTDOWN))
			{
				HLog.d(TAG, "action:shutdown");
				stopAllDownload();
			}
		}};
	
	/***
	 * Singleton. Get an instance of download manager
	 * @param context a context for it to use
	 * @return an instance of download manager
	 */
	public static DLManager getInstance(Context context)
	{
		HLog.v(TAG, "getInstance");
		if(null == mDLManager)
		{
			mDLManager = new DLManager(context);
		}
		return mDLManager;
	}

	private Handler mDLStatusHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			HLog.v(TAG, "handleMessage");
			switch(msg.what)
			{
				case DOWNLOAD_SUCCESS:
				{
					HLog.d(TAG, "download success");
					//update list status
					DLData task = mActiveTasks.get(0);
					task.setStatus(STATUS_SUCCESS);
					PackageManager pm = mContext.getPackageManager(); 
					PackageInfo info = pm.getPackageArchiveInfo(LOCAL_PATH + File.separator + task.getFileName(), PackageManager.GET_ACTIVITIES); 
					if(null != info)
					{
						task.setPackagename(info.applicationInfo.packageName);
					}
					ContentValues values = new ContentValues();
					values.put(HResDatabaseHelper.TASK_STATUS, task.getStatus());
					values.put(HResDatabaseHelper.CURRENT_SIZE, task.getCurrentSize());
					values.put(HResDatabaseHelper.TOTAL_SIZE, task.getTotalSize());
					values.put(HResDatabaseHelper.PACKAGENAME, task.getPackagename());
					mContext.getContentResolver().update(HResProvider.CONTENT_URI_DOWNLOAD, values, "_id = '" + task.getId() + "'", null);
					Message m = new Message();
					m.what = ACTION_UPDATE;
					Bundle bundle = new Bundle();
					bundle.putSerializable("task", task);
					m.setData(bundle);
					mProgressHandler.sendMessage(m);
					Message m1 = new Message();
					m1.what = ACTION_SUCCESS;
					Bundle bundle1 = new Bundle();
					bundle1.putSerializable("task", task);
					m1.setData(bundle1);
					mProgressHandler.sendMessage(m1);
					mActiveTasks.remove(0);
					if(mToStartTasks.size() > 0)
					{
						mActiveTasks.add(mToStartTasks.remove(0));
						startDownload();
					}
					Toast.makeText(mContext, task.getDisplayName() + mContext.getString(R.string.download_success), Toast.LENGTH_SHORT).show();
				}
				break;
				case DOWNLOAD_FAIL:
				{
					HLog.v(TAG, "download fail");
					if(null != mDownload)
					{
						mDownload.stopDownload();
						mDownload = null;
					}
					DLData task = null;
					if(!mActiveTasks.isEmpty())
					{
						task = mActiveTasks.get(0);
						task.setStatus(DLManager.STATUS_PAUSE);
						mActiveTasks.remove(task);
						Toast.makeText(mContext, task.getDisplayName() + mContext.getString(R.string.download_fail), Toast.LENGTH_SHORT).show();
					}
					if(mToStartTasks.size() > 0)
					{
						mActiveTasks.add(mToStartTasks.remove(0));
						startDownload();
					}
					ContentValues values = new ContentValues();
					values.put(HResDatabaseHelper.TASK_STATUS, task.getStatus());
					values.put(HResDatabaseHelper.CURRENT_SIZE, task.getCurrentSize());
					values.put(HResDatabaseHelper.TOTAL_SIZE, task.getTotalSize());
					mContext.getContentResolver().update(HResProvider.CONTENT_URI_DOWNLOAD, values, "_id = '" + task.getId() + "'", null);
					Message m = new Message();
					m.what = ACTION_UPDATE;
					Bundle bundle = new Bundle();
					bundle.putSerializable("task", task);
					m.setData(bundle);
					mProgressHandler.sendMessage(m);
				}
				break;
				case DOWNLOAD_STOP:
				{
					HLog.v(TAG, "download stop");
				}
				break;
			}
		}
	};
	
	/***
	 * start to download the first task in the active list
	 */
	private void startDownload() {
		HLog.v(TAG, "startDownload");
		DLData task = mActiveTasks.get(0);
		task.setStatus(DLManager.STATUS_RUNNING);
		Message m = new Message();
		m.what = ACTION_UPDATE;
		Bundle bundle = new Bundle();
		bundle.putSerializable("task", task);
		m.setData(bundle);
		mProgressHandler.sendMessage(m);
		mDownload = new Download(mContext, task);
		mDownload.setDLStatusHandler(mDLStatusHandler);
		mDownload.setProgressHandler(mProgressHandler);
		mDownload.startDownload();
	}

	/***
	 * Add a new donwload task
	 * @param data the download info of the task to be download
	 * @return the id of this task
	 */
	public long addTask(DLData data) {
		HLog.v(TAG, "addTask");
		long id = -1;
		if(0 >= mAllTasks.size())
		{
			HLog.v(TAG, "all task empty");
			Cursor cursor = mContext.getContentResolver().query(HResProvider.CONTENT_URI_DOWNLOAD, null, null, null, null);
			if(null != cursor && cursor.getCount() > 0)
			{
				while(cursor.moveToNext())
				{
					DLData task = new DLData();
					task.setId(cursor.getLong(cursor.getColumnIndex("_id")));
					task.setDisplayName(cursor.getString(cursor.getColumnIndex(HResDatabaseHelper.DISPLAY_NAME)));
					task.setStatus(cursor.getInt(cursor.getColumnIndex(HResDatabaseHelper.TASK_STATUS)));
					task.setCurrentSize(cursor.getInt(cursor.getColumnIndex(HResDatabaseHelper.CURRENT_SIZE)));
					task.setTotalSize(cursor.getInt(cursor.getColumnIndex(HResDatabaseHelper.TOTAL_SIZE)));
					task.setPackagename(cursor.getString(cursor.getColumnIndex(HResDatabaseHelper.PACKAGENAME)));
					task.setFileName(cursor.getString(cursor.getColumnIndex(HResDatabaseHelper.FILE_NAME)));
					task.setCharge(cursor.getString(cursor.getColumnIndex(HResDatabaseHelper.CHARGE)));
					task.setChargeMsg(cursor.getString(cursor.getColumnIndex(HResDatabaseHelper.CHARGE_MSG)));
					task.setResID(cursor.getString(cursor.getColumnIndex(HResDatabaseHelper.RES_ID)));
					task.setIconUrl(cursor.getString(cursor.getColumnIndex(HResDatabaseHelper.ICON_URL)));
					mAllTasks.add(task);
					Message m = new Message();
					m.what = ACTION_ADD;
					mProgressHandler.sendMessage(m);
					if(0 >= mActiveTasks.size())
					{
						if(DLManager.STATUS_READY == task.getStatus())
						{
							mActiveTasks.add(task);
							startDownload();
						}
					}
					else
					{
						if(DLManager.STATUS_READY == task.getStatus())
						{
							mToStartTasks.add(task);
						}
					}
				}
			}
			cursor.close();
		}
		if(null != data)
		{
			HLog.e(TAG, "data:" + data);
			String res_id = data.getResID();
			if(!mAllTasks.isEmpty())
			{
				HLog.e(TAG, "!mAllTasks.isEmpty()");
				for(DLData d : mAllTasks)
				{
					if(res_id.equals(d.getResID()))
					{
						return id;
					}
				}
			}
			/*
			*task list is empty, compare with database,
			*if this task with the same url is already exist in database, just return -1
			*/
			Cursor cursor = mContext.getContentResolver().query(HResProvider.CONTENT_URI_DOWNLOAD, null, 
					HResDatabaseHelper.RES_ID + " = '" + res_id + "'", null, null);
			HLog.e(TAG, "DOWNLOAD cursor:" + cursor.getCount());
			if(cursor.getCount() > 0)
			{
				cursor.close();
				return id;
			}
			cursor = mContext.getContentResolver().query(HResProvider.CONTENT_URI_SKIN, null, 
					HResDatabaseHelper.RES_ID + " = '" + res_id + "'", null, null);
			HLog.e(TAG, "SKIN cursor:" + cursor.getCount());
			if(cursor.getCount() > 0)
			{
				cursor.close();
				return id;
			}
			cursor.close();
			ContentValues values = new ContentValues();
			values.put(HResDatabaseHelper.DISPLAY_NAME, data.getDisplayName());
			values.put(HResDatabaseHelper.FILE_NAME, data.getFileName());
			values.put(HResDatabaseHelper.TASK_STATUS, data.getStatus());
			values.put(HResDatabaseHelper.TOTAL_SIZE, data.getTotalSize());
			values.put(HResDatabaseHelper.CURRENT_SIZE, data.getCurrentSize());
			values.put(HResDatabaseHelper.CHARGE, data.getCharge());
			values.put(HResDatabaseHelper.CHARGE_MSG, data.getChargeMsg());
			values.put(HResDatabaseHelper.PACKAGENAME, data.getPackagename());
			values.put(HResDatabaseHelper.RES_ID, data.getResID());
			values.put(HResDatabaseHelper.ICON_URL, data.getIconUrl());
			mContext.getContentResolver().insert(HResProvider.CONTENT_URI_DOWNLOAD, values);
			Cursor cr= mContext.getContentResolver().query(HResProvider.CONTENT_URI_DOWNLOAD, 
					null, null, null, "_id DESC");
			HLog.e(TAG, "DOWNLOAD2 cursor:" + cursor.getCount());
			if(cr != null && cr.getCount() > 0)
			{
				cr.moveToNext();
				id = cr.getLong(cr.getColumnIndex("_id"));
				data.setId(id);
				//return id;
			}
			cr.close();
		}
		// The first time to run the download process
		//else
		{
			HLog.e(TAG, "else data:" + data);
			if(null != data)
			{
				mAllTasks.add(data);
				Message m = new Message();
				m.what = ACTION_ADD;
				mProgressHandler.sendMessage(m);
				if(DLManager.STATUS_READY == data.getStatus())
				{
					mToStartTasks.add(data);
				}
				if(mActiveTasks.isEmpty())
				{
					if(mToStartTasks.size() > 0)
					{
						mActiveTasks.add(mToStartTasks.remove(0));
						startDownload();
					}
				}
				if(null != mDownload)
				{
					mDownload.setProgressHandler(mProgressHandler);
				}
			}
		}HLog.e(TAG, "add id:" + id);
		return id;
	}

	/***
	 * Restart a download task with the specific id. If there's no active task, 
	 * start this task immediately, otherwise put it into the waiting list
	 * @param id the id of the task
	 */
	public void restartTask(long id)
	{
		HLog.v(TAG, "restartTask");
		DLData data = null;
		for(DLData d : mAllTasks)
		{
			if(id == d.getId())
			{
				data = d;
				break;
			}
		}
		if(null == data)
		{
			return;
		}
		if(DLManager.STATUS_SUCCESS == data.getStatus())
		{
			return;
		}
		if(mActiveTasks.isEmpty())
		{
			mActiveTasks.add(data);
			startDownload();
		}
		else
		{
			data.setStatus(DLManager.STATUS_READY);
			mToStartTasks.add(data);
			Message m = new Message();
			m.what = ACTION_UPDATE;
			Bundle bundle = new Bundle();
			bundle.putSerializable("task", data);
			m.setData(bundle);
			mProgressHandler.sendMessage(m);
		}
	}
	
	/***
	 * Pause a download task with the specific id
	 * @param id the id of the task
	 */
	public void pauseTask(long id) {
		HLog.v(TAG, "pauseTask");
		DLData task = null;
		for(DLData d : mAllTasks)
		{
			if(id == d.getId())
			{
				task = d;
				break;
			}
		}
		if(null == task)
		{
			return;
		}
		if(DLManager.STATUS_SUCCESS == task.getStatus())
		{
			return;
		}
		if(DLManager.STATUS_RUNNING == task.getStatus())
		{
			if(null != mDownload)
			{
				mDownload.stopDownload();
				mDownload = null;
			}
			task.setStatus(DLManager.STATUS_PAUSE);
			mActiveTasks.remove(task);
			if(mToStartTasks.size() > 0)
			{
				mActiveTasks.add(mToStartTasks.remove(0));
				startDownload();
			}
			Message m = new Message();
			m.what = ACTION_UPDATE;
			Bundle bundle = new Bundle();
			bundle.putSerializable("task", task);
			m.setData(bundle);
			mProgressHandler.sendMessage(m);
			Toast.makeText(mContext, task.getDisplayName() + mContext.getString(R.string.download_stop), Toast.LENGTH_SHORT).show();
			return;
		}
		if(DLManager.STATUS_READY == task.getStatus())
		{
			task.setStatus(DLManager.STATUS_PAUSE);
			mToStartTasks.remove(task);
			Message m = new Message();
			m.what = ACTION_UPDATE;
			Bundle bundle = new Bundle();
			bundle.putSerializable("task", task);
			m.setData(bundle);
			mProgressHandler.sendMessage(m);
			return;
		}
	}
	
	/***
	 * Delete a download task with the specific id
	 * @param id the id of the task
	 */
	public void deleteTask(long id)
	{
		HLog.v(TAG, "deleteTask");
		HLog.d(TAG, "id:" + id);
		DLData task = null;
		for(DLData d : mAllTasks)
		{
			if(id == d.getId())
			{
				task = d;
				break;
			}
		}
		if(null == task)
		{
			return;
		}
		Message m = new Message();
		m.what = ACTION_DELETE;
		Bundle bundle = new Bundle();
		bundle.putSerializable("task", task);
		m.setData(bundle);
		mProgressHandler.sendMessage(m);
		String local_path = LOCAL_PATH + File.separator + task.getFileName();
		File f = new File(local_path);
		if(f.exists())
		{
			f.delete();
		}
		String local_icon = LOCAL_PATH + File.separator + task.getIconUrl();
		f = new File(local_icon);
		if(f.exists())
		{
			f.delete();
		}
		//mDatabase.delete(DLSQLiteHelper.TABLE_DOWNLOAD, "_id = '" + id + "'", null);
		mContext.getContentResolver().delete(HResProvider.CONTENT_URI_DOWNLOAD, "_id = '" + id + "'", null);
		if(DLManager.STATUS_RUNNING == task.getStatus())
		{
			if(null != mDownload)
			{
				mDownload.stopDownload();
				mDownload = null;
			}
			mActiveTasks.remove(task);
			if(mToStartTasks.size() > 0)
			{
				mActiveTasks.add(mToStartTasks.remove(0));
				startDownload();
			}
		}
		else if(DLManager.STATUS_READY == task.getStatus())
		{
			mToStartTasks.remove(task);
		}
		mAllTasks.remove(task);
	}
	
	/***
	 * Delete a download task with the specific column name indicates by type 
	 * @param value the column value used to find the task
	 * @param type which column
	 * @return the task that been deleted or null if can't find this task
	 */
	public DLData deleteTask(String value)
	{
		DLData task = null;
		for(DLData d : mAllTasks)
		{
			if(value.equals(d.getPackagename()))
			{
				task = d;
				break;
			}
		}
		if(null == task)
		{
			return null;
		}
		mAllTasks.remove(task);
		Message m = new Message();
		m.what = ACTION_DELETE;
		Bundle bundle = new Bundle();
		bundle.putSerializable("task", task);
		m.setData(bundle);
		mProgressHandler.sendMessage(m);
		String local_path = LOCAL_PATH + File.separator + task.getFileName();
		File f = new File(local_path);
		if(f.exists())
		{
			f.delete();
		}
		String local_icon = LOCAL_PATH + File.separator + task.getIconUrl();
		f = new File(local_icon);
		if(f.exists())
		{
			f.delete();
		}
		mContext.getContentResolver().delete(HResProvider.CONTENT_URI_DOWNLOAD, "_id = '" + task.getId() + "'", null);
		if(DLManager.STATUS_RUNNING == task.getStatus())
		{
			if(null != mDownload)
			{
				mDownload.stopDownload();
				mDownload = null;
			}
			mActiveTasks.remove(task);
			if(mToStartTasks.size() > 0)
			{
				mActiveTasks.add(mToStartTasks.remove(0));
				startDownload();
			}
		}
		else if(DLManager.STATUS_READY == task.getStatus())
		{
			mToStartTasks.remove(task);
		}
		return task;
	}
	
	/***
	 * Stop the whole download process when exit the application
	 */
	public void stopAllDownload()
	{	
		HLog.v(TAG, "stopAllDownload");
		if(!mActiveTasks.isEmpty())
		{
			if(null != mDownload)
			{
				mDownload.stopDownload();
				mDownload = null;
			}
			mActiveTasks.get(0).setStatus(DLManager.STATUS_READY);
		}
		for(DLData task : mAllTasks)
		{
			ContentValues values = new ContentValues();
			values.put(HResDatabaseHelper.CURRENT_SIZE, task.getCurrentSize());
			values.put(HResDatabaseHelper.PACKAGENAME, task.getPackagename());
			values.put(HResDatabaseHelper.FILE_NAME, task.getFileName());
			values.put(HResDatabaseHelper.DISPLAY_NAME, task.getDisplayName());
			values.put(HResDatabaseHelper.TASK_STATUS, task.getStatus());
			values.put(HResDatabaseHelper.TOTAL_SIZE, task.getTotalSize());
			mContext.getContentResolver().update(HResProvider.CONTENT_URI_DOWNLOAD, values, "_id = '" + task.getId() + "'", null);
		}
		mContext.getApplicationContext().unregisterReceiver(mShutDownReceiver);
		//mDatabase.close();
		mDLManager = null;
	}
	
	/***
	 * Get a list of all tasks
	 * @return the list of all tasks
	 */
	public List<DLData> getAllTasks(){
		//HLog.v(TAG, "getAllTasks");
		if(0 >= mAllTasks.size())
		{
			HLog.v(TAG, "all task empty");
			Cursor cursor = mContext.getContentResolver().query(HResProvider.CONTENT_URI_DOWNLOAD, null, null, null, null);
			if(null != cursor && cursor.getCount() > 0)
			{
				while(cursor.moveToNext())
				{
					DLData task = new DLData();
					task.setId(cursor.getLong(cursor.getColumnIndex("_id")));
					task.setDisplayName(cursor.getString(cursor.getColumnIndex(HResDatabaseHelper.DISPLAY_NAME)));
					task.setStatus(cursor.getInt(cursor.getColumnIndex(HResDatabaseHelper.TASK_STATUS)));
					task.setCurrentSize(cursor.getInt(cursor.getColumnIndex(HResDatabaseHelper.CURRENT_SIZE)));
					task.setTotalSize(cursor.getInt(cursor.getColumnIndex(HResDatabaseHelper.TOTAL_SIZE)));
					task.setPackagename(cursor.getString(cursor.getColumnIndex(HResDatabaseHelper.PACKAGENAME)));
					task.setFileName(cursor.getString(cursor.getColumnIndex(HResDatabaseHelper.FILE_NAME)));
					task.setCharge(cursor.getString(cursor.getColumnIndex(HResDatabaseHelper.CHARGE)));
					task.setChargeMsg(cursor.getString(cursor.getColumnIndex(HResDatabaseHelper.CHARGE_MSG)));
					task.setResID(cursor.getString(cursor.getColumnIndex(HResDatabaseHelper.RES_ID)));
					task.setIconUrl(cursor.getString(cursor.getColumnIndex(HResDatabaseHelper.ICON_URL)));
					mAllTasks.add(task);
					Message m = new Message();
					m.what = ACTION_ADD;
					mProgressHandler.sendMessage(m);
					if(0 >= mActiveTasks.size())
					{
						if(DLManager.STATUS_READY == task.getStatus())
						{
							mActiveTasks.add(task);
							startDownload();
						}
					}
					else
					{
						if(DLManager.STATUS_READY == task.getStatus())
						{
							mToStartTasks.add(task);
						}
					}
				}
			}
			cursor.close();
		}
		return mAllTasks;
	}
	
	/***
	 * Set a handler to update the progress
	 * @param handler the handler to update the progress
	 */
	public void setProgressHandler(Handler handler)
	{
		HLog.v(TAG, "setProgressHandler");
		mProgressHandler = handler;
		if(null != mDownload)
		{
			mDownload.setProgressHandler(mProgressHandler);
		}
	}
	
	/***
	 * Get a list of tasks that completed or not completed
	 * @param completed the tasks are completed or not
	 * @return a list of tasks
	 */
	public List<DLData> getTasks(boolean completed)
	{
		HLog.i(TAG, "getTasks");
		List<DLData> tasks = new ArrayList<DLData>();
		if(completed)
		{
			for(DLData d : mAllTasks)
			{
				if(DLManager.STATUS_SUCCESS == d.getStatus())
				{
					tasks.add(d);
				}
			}
		}
		else
		{
			for(DLData d : mAllTasks)
			{
				if(DLManager.STATUS_SUCCESS != d.getStatus())
				{
					tasks.add(d);
				}
			}
		}
		return tasks;
	}
}

