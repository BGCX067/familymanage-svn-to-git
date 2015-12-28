package com.exuan.edownloadservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class DownloadTask implements Runnable, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4473620559837679883L;
	private String mUrl;
	private int mCurrentSize;
	private int mTotalSize;
	private long mTaskId;
	private int mTaskStatus;
	private Thread mDownloadThread;
	private boolean mIsRun;
	private Downloader mDownloader;
	
	private Handler mProgressHandler;
    private Handler mDLStatusHandler;
	private static final int BUFFER_SIZE = 1024;
	private File mLocalFile;
	private Context mContext;
	
	public final static int TASK_STATUS_READY    = 1;
	/**
	 * downlaoding
	 */
	public final static int TASK_STATUS_RUNNING    = 2;

	/**
	 * download stopped
	 */
	public final static int TASK_STATUS_PAUSE     = 3;

	/**
	 * download finished
	 */
	public final static int TASK_STATUS_SUCCESS = 4;
	
	public DownloadTask()
	{
		mDownloader = new Downloader();
	}
	
	public void setUrl(String url)
	{
		mUrl = url;
		mDownloader.setUrl(mUrl);
	}
	
	public String getUrl()
	{
		return mUrl;
	}
	
	public void setCurrentSize(int size)
	{
		mCurrentSize = size;
	}
	
	public int getCurrentSize()
	{
		return mCurrentSize;
	}
	
	public void setTotalSize(int size)
	{
		mTotalSize = size;
	}
	
	public int getTotalSize()
	{
		return mTotalSize;
	}
	
	public void setTaskStatus(int status)
	{
		mTaskStatus = status;
	}
	
	public int getTaskStatus()
	{
		return mTaskStatus;
	}
	
	public void setTaskId(long id)
	{
		mTaskId = id;
	}
	
	public long getTaskId()
	{
		return mTaskId;
	}

	public void startTask()
	{
		mDownloadThread = new Thread(this);
		mIsRun = true;
		mDownloadThread.start();
	}
	
	public void stopTask()
	{
		mIsRun = false;
		mDownloadThread.interrupt();
		mDownloadThread = null;
	}
	
	String getFileName() {
		// TODO Auto-generated method stub
		return mUrl.substring(mUrl.lastIndexOf("/") + 1);
	}
	
	/***
	 * Set a download status hanlder to update download status
	 * @param handler a handler to update download status
	 */
	public void setDLStatusHandler(Handler handler)
	{
		mDLStatusHandler = handler;
	}
	
	/***
	 * Set a download progress hanlder to update download progress
	 * @param handler a handler to update download progress
	 */
	public void setProgressHandler(Handler handler)
	{
		mProgressHandler = handler;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		mTaskStatus = TASK_STATUS_RUNNING;
		File f = new File(DownloadService.LOCAL_PATH);
		if(!f.exists())
		{
			f.mkdir();
		}
		mLocalFile = new File(DownloadService.LOCAL_PATH + File.separator + getFileName());
		
		if(getCurrentSize() > 0)
		{
			if(getCurrentSize() < getTotalSize())
			{
				String range = "bytes=" + getCurrentSize() + "-";
				mDownloader.addHeader("Range", range);
			}
		}
		InputStream in = mDownloader.getHTTPClient();
		saveFile(in);
	}

	private void saveFile(InputStream in) {
		// TODO Auto-generated method stub
		try 
		{
			RandomAccessFile rf = null;
			if(mLocalFile.exists() && getCurrentSize() > 0)
			{
				rf = new RandomAccessFile(mLocalFile, "rwd");
				rf.seek(getCurrentSize());
			}
			else
			{
				if(mLocalFile.exists())
				{
					mLocalFile.delete();
				}
				mLocalFile.createNewFile();
				FileOutputStream fos = mContext.openFileOutput(mLocalFile.getName(), Context.MODE_WORLD_READABLE);
				fos.close();
				rf = new RandomAccessFile(mLocalFile, "rwd");
			}
			byte[] buffer = new byte[BUFFER_SIZE];
			while (true)
			{
				if(!mIsRun)
				{
					Message msg = new Message();
					msg.what = DownloadService.DOWNLOAD_STOP;
					msg.obj = mTaskId;
					mDLStatusHandler.sendMessage(msg);
					return;
				}
				int size = in.read(buffer);
				if (size == -1) 
				{
					break;
				}
				int current = getCurrentSize() + size;
				if(current > getTotalSize())
				{
					current = getTotalSize();
				}
				setCurrentSize(current);
				
				rf.write(buffer, 0, size);
				Message m = new Message();
				m.what = DownloadService.ACTION_UPDATE;
				Bundle bundle = new Bundle();
				bundle.putSerializable("task", this);
				m.setData(bundle);
				mProgressHandler.sendMessage(m);
			}
			if(getCurrentSize() == getTotalSize())
			{
				/*
				 * download success, update download manager
				 */
				Message msg = new Message();
				msg.what = DownloadService.DOWNLOAD_SUCCESS;
				msg.obj = mTaskId;
				mDLStatusHandler.sendMessage(msg);
			}
			else
			{
				Message msg = new Message();
				msg.what = DownloadService.DOWNLOAD_FAIL;
				msg.obj = mTaskId;
				mDLStatusHandler.sendMessage(msg);
			}
		}
		catch (Exception ex)
		{
			//onError(ex);
		} 
		finally
		{
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}