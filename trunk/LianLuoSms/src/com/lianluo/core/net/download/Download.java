package com.lianluo.core.net.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;

import org.apache.http.HttpStatus;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.haolianluo.sms2.data.HConst;
import com.lianluo.core.net.BaseHttp;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;
/*
 * A downloader
 */
public class Download extends BaseHttp implements Runnable 
{
	private static final String TAG = "Download";
	private Handler mProgressHandler;
    private Handler mDLStatusHandler;
	private static final int BUFFER_SIZE = 1024;
	private File mLocalFile;
	private Thread mDLThread;
	private DLData mCurrentTask;
	private boolean mIsRun = false;
	private Context mContext;
	private static final String RES_URL = "http://ws.lianluo.com/sms2/ModuleDown";

	/***
	 * Constructor of download
	 * @param context a context for it to use
	 * @param task a download task to be download
	 */
	public Download(Context context, DLData task)
	{
		if(ToolsUtil.isCMWAP(context)) {
			super.setCmwap(true);
		}
		mCurrentTask = task;
		mContext = context;
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
	
	/***
	 * start the download thread
	 */
	public void startDownload() {
		HLog.v(TAG, "startDownload");
		mIsRun = true;
		mDLThread = new Thread(this);
		mDLThread.start();
	}

	/***
	 * stop the download thread
	 */
	public void stopDownload()
	{
		HLog.v(TAG, "stopDownload");
		mIsRun = false;
		mDLThread.interrupt();
		mDLThread = null;
	}
	
	/**
	 * HTTP response
	 * @param httpStatusCode the response code
	 * @return succeed or not
	 */
	@Override
	protected boolean onResponse(int httpStatusCode) 
	{
		HLog.v(TAG, "response code:" + httpStatusCode);
		if(HttpStatus.SC_OK == httpStatusCode) 
		{
			return true;
		}
		if(HttpStatus.SC_PARTIAL_CONTENT == httpStatusCode)
		{
			return true;
		}
		Message msg = new Message();
		msg.what = DLManager.DOWNLOAD_FAIL;
		mDLStatusHandler.sendMessage(msg);
		return false;
	}

	/**
	 * receive data from server
	 * @param is the inputstream to transfer data
	 */
	@Override
	public void onReceive(InputStream is) {
		HLog.v(TAG, "onReceive");
		if (mLocalFile != null) 
		{
			if(mCurrentTask.getTotalSize() <= 0)
			{
				mCurrentTask.setTotalSize(Integer.parseInt(mHeaderMessageMap.get("content-length").trim()));
			}
			writeFile(is);
		}
	}

	/**
	 * write to local file
	 * @param is the inputstream to gain data
	 */
	private void writeFile(InputStream is) 
	{
		try 
		{
			RandomAccessFile rf = null;
			if(mLocalFile.exists() && mCurrentTask.getCurrentSize() > 0)
			{
				rf = new RandomAccessFile(mLocalFile, "rwd");
				rf.seek(mCurrentTask.getCurrentSize());
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
					msg.what = DLManager.DOWNLOAD_STOP;
					mDLStatusHandler.sendMessage(msg);
					return;
				}
				int size = is.read(buffer);
				if (size == -1) 
				{
					break;
				}
				int current = mCurrentTask.getCurrentSize() + size;
				if(current > mCurrentTask.getTotalSize())
				{
					current = mCurrentTask.getTotalSize();
				}
				mCurrentTask.setCurrentSize(current);
				
				rf.write(buffer, 0, size);
				Message m = new Message();
				m.what = DLManager.ACTION_UPDATE;
				Bundle bundle = new Bundle();
				bundle.putSerializable("task", mCurrentTask);
				m.setData(bundle);
				mProgressHandler.sendMessage(m);
			}
			if(mCurrentTask.getCurrentSize() == mCurrentTask.getTotalSize())
			{
				/*
				 * download success, update download manager
				 */
				Message msg = new Message();
				msg.what = DLManager.DOWNLOAD_SUCCESS;
				mDLStatusHandler.sendMessage(msg);
			}
			else
			{
				HLog.d(TAG, "cur != total");
				Message msg = new Message();
				msg.what = DLManager.DOWNLOAD_FAIL;
				mDLStatusHandler.sendMessage(msg);
			}
		}
		catch (Exception ex)
		{
			onError(ex);
		} 
		finally
		{
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * handle error when error occurred 
	 * @param ex exception object
	 */
	@Override
	protected void onError(Exception ex) {
		HLog.e(TAG, "onError");
		ex.printStackTrace();
		Message msg = new Message();
		msg.what = DLManager.DOWNLOAD_FAIL;
		mDLStatusHandler.sendMessage(msg);
	}

	@Override
	public void run() 
	{
		HLog.v(TAG, "run");
		mCurrentTask.setStatus(DLManager.STATUS_RUNNING);
		File f = new File(DLManager.LOCAL_PATH);
		if(!f.exists())
		{
			f.mkdir();
		}
		mLocalFile = new File(DLManager.LOCAL_PATH + File.separator + mCurrentTask.getFileName());
		HLog.d(TAG, "file:" + mLocalFile.toString());
		
		if(mCurrentTask.getCurrentSize() > 0)
		{
			if(mCurrentTask.getCurrentSize() < mCurrentTask.getTotalSize())
			{
				HashMap<String, String> headmap = new HashMap<String, String>();
				String range = "bytes=" + mCurrentTask.getCurrentSize() + "-";
				headmap.put("Range", range);
				HLog.v(TAG, "range:" + range);
				setRequestHeader(headmap);
			}
			else
			{
				return;
			}
		}
		connect(HConst.RESLIB_DOWNLOAD(mContext, mCurrentTask.getResID()));
	}

	/**
	 * Get the url of this download task
	 * @return the url
	 */
	@Override
	protected String getUrl() {
		// TODO Auto-generated method stub
		return RES_URL;
	}
}
