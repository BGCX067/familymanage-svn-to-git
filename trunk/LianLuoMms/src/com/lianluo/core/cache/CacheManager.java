package com.lianluo.core.cache;

import java.io.InputStream;

import android.content.Context;
import android.os.Handler;

public class CacheManager {

	public static String CACHE_DATA;
	public static String CACHE_RESOURCE;
	
	private static CacheManager mCacheManager;
	private static MemoryCache mMemoryCache;
	private static DataFileCache mDataFileCache;
	private static ResourceFileCache mResourceFileCache;
	
	public static CacheManager newInstance() {
		if(mCacheManager == null) {
			mCacheManager = new CacheManager();
		}
		return mCacheManager;
	}
	
	public void openCache(Context context) {
		CACHE_DATA = context.getCacheDir() + "/data/";
		CACHE_RESOURCE = context.getCacheDir() + "/res/";
		
		if(mMemoryCache == null) {
			mMemoryCache = new MemoryCache();
		}
		if(mDataFileCache == null) {
			mDataFileCache = new DataFileCache();
		}
		if(mResourceFileCache == null) {
			mResourceFileCache = new ResourceFileCache();
		}
	}
	
	public void closeCache() {
		if(mMemoryCache != null) {
			mMemoryCache.clear();
		}
	}
	
	public void putDataCache(String key, Object obj) {
		mMemoryCache.putCache(key, obj);
		mDataFileCache.putCache(key, obj);
	}

	public Object getDataCache(String key) {
		Object obj = mMemoryCache.getCache(key);
		if(obj == null) {
			obj = mDataFileCache.getCache(key);
		}
		return obj;
	}
	
	public void clearMemoryCache() {
		mMemoryCache.clear();
	}
	
	public void putResourceCache(String name, InputStream is, Handler handler) {
		mResourceFileCache.putCache(name, is, handler);
	}
	
	public InputStream getResourceCache(String name) {
		Object obj = mResourceFileCache.getCache(name);
		if(obj == null) {
			return null;
		} else {
			return (InputStream) obj;
		}
	}
	
	public void removeResourceCache(String name) {
		mResourceFileCache.removeCache(name);
	}
	
}
