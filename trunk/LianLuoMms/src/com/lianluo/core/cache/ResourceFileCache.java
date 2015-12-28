package com.lianluo.core.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class ResourceFileCache implements BaseCache {
	
	public static final int RESOURCE_SUCCESS = 1;
	public static final int RESOURCE_ERROR = 2;
	public static final String RESOURCE_NAME = "resourceName";
	
	private Handler mHandler;
	private int mBufferSize = 1024;
	
	public void putCache(String key, Object obj, Handler handler) {
		this.mHandler = handler;
		putCache(key, obj);
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString(ResourceFileCache.RESOURCE_NAME, key);
		msg.setData(bundle);
		msg.what = ResourceFileCache.RESOURCE_SUCCESS;
		handler.sendMessage(msg);
	}

	@Override
	public void putCache(String key, Object obj) {
		InputStream is = (InputStream) obj;
		FileOutputStream fos = null;
		try {
			File cacheDir = new File(CacheManager.CACHE_RESOURCE);
			if(!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			File cacheFile = new File(cacheDir, key);
			if(!cacheFile.exists()) {
				cacheFile.createNewFile();
			}
			fos = new FileOutputStream(cacheFile);
			byte[] buffer = new byte[mBufferSize];
			while (true) {
				int size = is.read(buffer);
				if (size == -1) {
					break;
				}
				fos.write(buffer, 0, size);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			if(mHandler != null) {
				mHandler.sendEmptyMessage(RESOURCE_ERROR);
			}
		} finally {
			try {
				fos.close();
				is.close();
			} catch(Exception ex) {
				ex.printStackTrace();
				if(mHandler != null) {
					mHandler.sendEmptyMessage(RESOURCE_ERROR);
				}
			}
		}
	}

	@Override
	public Object getCache(String key) {
		FileInputStream fis = null;
		try {
			File cacheDir = new File(CacheManager.CACHE_RESOURCE);
			if(!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			File cacheFile = new File(cacheDir, key);
			if(cacheFile.exists()) {
				fis = new FileInputStream(cacheFile);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return fis;
	}
	
	@Override
	public void clear() {
		try {
			File cacheDir = new File(CacheManager.CACHE_RESOURCE);
			if(!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			String[] cacheFiles = cacheDir.list();
			int size = cacheFiles.length;
			for(int i = 0; i < size; i++) {
				File cacheFile = new File(cacheDir, cacheFiles[i]);
				cacheFile.delete();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void removeCache(String key) {
		try {
			File cacheDir = new File(CacheManager.CACHE_RESOURCE);
			if(!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			File cacheFile = new File(cacheDir, key);
			if(cacheFile.exists()) {
				cacheFile.delete();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
