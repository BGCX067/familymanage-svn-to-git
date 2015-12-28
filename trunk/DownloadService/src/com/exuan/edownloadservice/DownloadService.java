package com.exuan.edownloadservice;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class DownloadService extends Service
{
	private static final String TAG	 = "DownloadService";
	private List<DownloadTask> mActiveTasks = new ArrayList<DownloadTask>();
	private Map<Long, DownloadTask> mAllTasks = new HashMap<Long, DownloadTask>();
	private List<DownloadTask> mToStartTasks = new ArrayList<DownloadTask>();
	private Context mContext;
	private static final int MAX_ACTIVE_TASKS = 3;
	private IBinder mBinder;
	
	private Handler mProgressHandler;
	public static String LOCAL_PATH;
	
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
	
	public void onCreate()
	{
		super.onCreate();
		mContext = this;
		mBinder = new DownloadBinder();
		initTask();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	private void initTask()
	{
		if(mContext.getContentResolver() == null)
		{
			return;
		}
		if(mAllTasks.size() <= 0)
		{
			Cursor cursor = mContext.getContentResolver().query(DataProvider.CONTENT_URI_DOWNLOAD, null, null, null, null);
			if(cursor != null)
			{
				if(cursor.getCount() > 0)
				{
					DownloadTask task = new DownloadTask();
					task.setTaskId(cursor.getLong(cursor.getColumnIndex("_id")));
					task.setCurrentSize(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CURRENT_SIZE)));
					task.setTotalSize(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TOTAL_SIZE)));
					task.setUrl(cursor.getString(cursor.getColumnIndex(DatabaseHelper.URL)));
					task.setTaskStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_STATUS)));
					mAllTasks.put(task.getTaskId(), task);
					if(task.getTaskStatus() == DownloadTask.TASK_STATUS_READY)
					{
						mToStartTasks.add(task);
					}
				}
				cursor.close();
			}
		}
	}
	
	class DownloadBinder extends Binder
	{
		public DownloadBinder()
		{}
		
		public DownloadService getService()
		{
			return DownloadService.this;
		}
	}
	
	/***
	 * Add a new donwload task
	 * @param data the download info of the task to be download
	 * @return the id of this task
	 */
	public long addTask(DownloadTask task) {
		Log.v(TAG, "addTask");
		long id = -1;
		if(null != task)
		{
			//url already in downloading list
			if(!mAllTasks.isEmpty())
			{
				if(mAllTasks.containsKey(task.getTaskId()))
				{
					return task.getTaskId();
				}
				Iterator<Entry<Long, DownloadTask>> iter = mAllTasks.entrySet().iterator();
				while(iter.hasNext())
				{
					Map.Entry<Long, DownloadTask> entry = (Map.Entry<Long, DownloadTask>)iter.next();
				    DownloadTask value = entry.getValue();
				    if(value.getUrl().equals(task.getUrl()))      
				    {
				    	return id;
				    }
				}
			}
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.TASK_STATUS, task.getTaskStatus());
			values.put(DatabaseHelper.TOTAL_SIZE, task.getTotalSize());
			values.put(DatabaseHelper.CURRENT_SIZE, task.getCurrentSize());
			values.put(DatabaseHelper.URL, task.getUrl());
			mContext.getContentResolver().insert(DataProvider.CONTENT_URI_DOWNLOAD, values);
			Cursor cr= mContext.getContentResolver().query(DataProvider.CONTENT_URI_DOWNLOAD, 
					null, null, null, "_id DESC");
			if(cr != null && cr.getCount() > 0)
			{
				cr.moveToNext();
				id = cr.getLong(cr.getColumnIndex("_id"));
				task.setTaskId(id);
				//return id;
			}
			cr.close();
		}
		// The first time to run the download process
		//else
		{
			Log.e(TAG, "else data:" + task);
			if(null != task)
			{
				mAllTasks.put(task.getTaskId(), task);
				Message m = new Message();
				m.what = ACTION_ADD;
				mProgressHandler.sendMessage(m);
				if(STATUS_READY == task.getTaskStatus())
				{
					mToStartTasks.add(task);
				}
				if(mActiveTasks.size() < MAX_ACTIVE_TASKS)
				{
					if(mToStartTasks.size() > 0)
					{
						DownloadTask dt = mToStartTasks.remove(0);
						mActiveTasks.add(dt);
						dt.setProgressHandler(mProgressHandler);
						dt.startTask();
					}
				}
			}
		}
		Log.e(TAG, "add id:" + id);
		return id;
	}

	/***
	 * start to download the first task in the active list
	 *//*
	private void startDownload() {
		DownloadTask task = mActiveTasks.get(0);
		task.setTaskStatus(STATUS_RUNNING);
		Message m = new Message();
		m.what = ACTION_UPDATE;
		Bundle bundle = new Bundle();
		bundle.putSerializable("task", task);
		m.setData(bundle);
		mProgressHandler.sendMessage(m);
		mDownloader = new Downloader(mContext, task);
		task.setDLStatusHandler(mDLStatusHandler);
		task.setProgressHandler(mProgressHandler);
		mDownloader.startDownload();
	}
*/
	/***
	 * Restart a download task with the specific id. If there's no active task, 
	 * start this task immediately, otherwise put it into the waiting list
	 * @param id the id of the task
	 */
	public void restartTask(long id)
	{
		Log.v(TAG, "restartTask");
		DownloadTask task = mAllTasks.get(id);
		if(null == task)
		{
			return;
		}
		if(STATUS_SUCCESS == task.getTaskStatus())
		{
			return;
		}
		if(mActiveTasks.size() < MAX_ACTIVE_TASKS)
		{
			mActiveTasks.add(task);
			task.startTask();
		}
		else
		{
			task.setTaskStatus(STATUS_READY);
			mToStartTasks.add(task);
			Message m = new Message();
			m.what = ACTION_UPDATE;
			Bundle bundle = new Bundle();
			bundle.putSerializable("task", task);
			m.setData(bundle);
			mProgressHandler.sendMessage(m);
		}
	}
	
	/***
	 * Pause a download task with the specific id
	 * @param id the id of the task
	 */
	public void pauseTask(long id) {
		Log.v(TAG, "pauseTask");
		DownloadTask task = mAllTasks.get(id);
		if(null == task)
		{
			return;
		}
		if(STATUS_SUCCESS == task.getTaskStatus())
		{
			return;
		}
		if(STATUS_RUNNING == task.getTaskStatus())
		{
			if(null != task)
			{
				task.stopTask();
			}
			task.setTaskStatus(STATUS_PAUSE);
			mActiveTasks.remove(task);
			if(mActiveTasks.size() < MAX_ACTIVE_TASKS)
			{
				if(mToStartTasks.size() > 0)
				{
					DownloadTask dt = mToStartTasks.remove(0);
					mActiveTasks.add(dt);
					dt.setProgressHandler(mProgressHandler);
					dt.startTask();
				}
			}
			Message m = new Message();
			m.what = ACTION_UPDATE;
			Bundle bundle = new Bundle();
			bundle.putSerializable("task", task);
			m.setData(bundle);
			mProgressHandler.sendMessage(m);
			return;
		}
		if(STATUS_READY == task.getTaskStatus())
		{
			task.setTaskStatus(STATUS_PAUSE);
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
		Log.v(TAG, "deleteTask");
		Log.d(TAG, "id:" + id);
		DownloadTask task = mAllTasks.get(id);
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
		
		//mDatabase.delete(DLSQLiteHelper.TABLE_DOWNLOAD, "_id = '" + id + "'", null);
		mContext.getContentResolver().delete(DataProvider.CONTENT_URI_DOWNLOAD, "_id = '" + id + "'", null);
		if(STATUS_RUNNING == task.getTaskStatus())
		{
			if(null != task)
			{
				task.stopTask();
			}
			mActiveTasks.remove(task);
			if(mActiveTasks.size() < MAX_ACTIVE_TASKS)
			{
				if(mToStartTasks.size() > 0)
				{
					DownloadTask dt = mToStartTasks.remove(0);
					mActiveTasks.add(dt);
					dt.setProgressHandler(mProgressHandler);
					dt.startTask();
				}
			}
		}
		else if(STATUS_READY == task.getTaskStatus())
		{
			mToStartTasks.remove(task);
		}
		mAllTasks.remove(task.getTaskId());
	}
	
	/***
	 * Stop the whole download process when exit the application
	 */
	public void stopAllDownload()
	{	
		Log.v(TAG, "stopAllDownload");
		if(!mActiveTasks.isEmpty())
		{
			for(int i = 0; i < mActiveTasks.size(); i++)
			{
				DownloadTask task = mActiveTasks.get(i);
				task.stopTask();
				task.setTaskStatus(STATUS_READY);
			}
		}
		Iterator<Entry<Long, DownloadTask>> iter = mAllTasks.entrySet().iterator();
		while(iter.hasNext())
		{
			Map.Entry<Long, DownloadTask> entry = (Map.Entry<Long, DownloadTask>)iter.next();
		    DownloadTask dt = entry.getValue();
		    ContentValues values = new ContentValues();
			values.put(DatabaseHelper.CURRENT_SIZE, dt.getCurrentSize());
			values.put(DatabaseHelper.TASK_STATUS, dt.getTaskStatus());
			values.put(DatabaseHelper.TOTAL_SIZE, dt.getTotalSize());
			mContext.getContentResolver().update(DataProvider.CONTENT_URI_DOWNLOAD, values, "_id = '" + dt.getTaskId() + "'", null);
		}
		mContext.getApplicationContext().unregisterReceiver(mShutDownReceiver);
	}
	
	private BroadcastReceiver mShutDownReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equalsIgnoreCase(Intent.ACTION_SHUTDOWN))
			{
				stopAllDownload();
			}
		}};
	
	private Handler mDLStatusHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			DownloadTask task = mAllTasks.get(msg.obj);
			switch(msg.what)
			{
				case DOWNLOAD_SUCCESS:
				{
					//update list status
					task.setTaskStatus(STATUS_SUCCESS);
					ContentValues values = new ContentValues();
					values.put(DatabaseHelper.TASK_STATUS, task.getTaskStatus());
					values.put(DatabaseHelper.CURRENT_SIZE, task.getCurrentSize());
					values.put(DatabaseHelper.TOTAL_SIZE, task.getTotalSize());
					mContext.getContentResolver().update(DataProvider.CONTENT_URI_DOWNLOAD, values, "_id = '" + task.getTaskId() + "'", null);
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
					mActiveTasks.remove(task);
					if(mActiveTasks.size() < MAX_ACTIVE_TASKS)
					{
						if(mToStartTasks.size() > 0)
						{
							DownloadTask dt = mToStartTasks.remove(0);
							mActiveTasks.add(dt);
							dt.setProgressHandler(mProgressHandler);
							dt.startTask();
						}
					}
					//Toast.makeText(mContext, task.getDisplayName() + mContext.getString(R.string.download_success), Toast.LENGTH_SHORT).show();
				}
				break;
				case DOWNLOAD_FAIL:
				{/*
					if(null != mDownload)
					{
						mDownload.stopDownload();
						mDownload = null;
					}*/
					if(mActiveTasks.size() < MAX_ACTIVE_TASKS)
					{
						task = mActiveTasks.get(0);
						task.setTaskStatus(STATUS_PAUSE);
						mActiveTasks.remove(task);
						//Toast.makeText(mContext, task.getDisplayName() + mContext.getString(R.string.download_fail), Toast.LENGTH_SHORT).show();
					}
					if(mToStartTasks.size() > 0)
					{
						mActiveTasks.add(mToStartTasks.remove(0));
						//startDownload();
					}
					ContentValues values = new ContentValues();
					values.put(DatabaseHelper.TASK_STATUS, task.getTaskStatus());
					values.put(DatabaseHelper.CURRENT_SIZE, task.getCurrentSize());
					values.put(DatabaseHelper.TOTAL_SIZE, task.getTotalSize());
					mContext.getContentResolver().update(DataProvider.CONTENT_URI_DOWNLOAD, values, "_id = '" + task.getTaskId() + "'", null);
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
					Log.v(TAG, "download stop");
				}
				break;
			}
		}
	};
	
	/***
	 * Get a list of all tasks
	 * @return the list of all tasks
	 */
	public Map<Long, DownloadTask> getAllTasks(){
		Log.v(TAG, "getAllTasks");
		if(0 >= mAllTasks.size())
		{
			Log.v(TAG, "all task empty");
			Cursor cursor = mContext.getContentResolver().query(DataProvider.CONTENT_URI_DOWNLOAD, null, null, null, null);
			if(null != cursor && cursor.getCount() > 0)
			{
				while(cursor.moveToNext())
				{
					DownloadTask task = new DownloadTask();
					task.setTaskId(cursor.getLong(cursor.getColumnIndex("_id")));
					task.setTaskStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_STATUS)));
					task.setCurrentSize(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CURRENT_SIZE)));
					task.setTotalSize(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TOTAL_SIZE)));
					mAllTasks.put(task.getTaskId(), task);
					Message m = new Message();
					m.what = ACTION_ADD;
					mProgressHandler.sendMessage(m);
					if(MAX_ACTIVE_TASKS > mActiveTasks.size())
					{
						if(STATUS_READY == task.getTaskStatus())
						{
							mActiveTasks.add(task);
							task.setProgressHandler(mProgressHandler);
							task.startTask();
						}
					}
					else
					{
						if(STATUS_READY == task.getTaskStatus())
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
		Log.v(TAG, "setProgressHandler");
		mProgressHandler = handler;
		for(int i = 0; i < mActiveTasks.size(); i++)
		{
			DownloadTask task = mActiveTasks.get(i);
			task.setProgressHandler(mProgressHandler);
		}
	}
	
	/***
	 * Get a list of tasks that completed or not completed
	 * @param completed the tasks are completed or not
	 * @return a list of tasks
	 */
	public List<DownloadTask> getTasks(boolean completed)
	{
		Log.v(TAG, "getTasks");
		List<DownloadTask> tasks = new ArrayList<DownloadTask>();
		Iterator<Entry<Long, DownloadTask>> iter = mAllTasks.entrySet().iterator();
		while(iter.hasNext())
		{
			Map.Entry<Long, DownloadTask> entry = (Map.Entry<Long, DownloadTask>)iter.next();
		    DownloadTask dt = entry.getValue();
		    if(completed)
		    {
		    	if(STATUS_SUCCESS == dt.getTaskStatus())
				{
					tasks.add(dt);
				}
		    }
		    else
		    {
		    	if(STATUS_SUCCESS != dt.getTaskStatus())
				{
					tasks.add(dt);
				}
		    }
		}
		return tasks;
	}
}