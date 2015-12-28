package com.lianluo.core.cache;

import java.util.HashMap;


/**
 * 内存缓存类
 * 
 * @author ZhouJian
 * 
 */
public class MemoryCache implements BaseCache {
	
	private static HashMap<String, Object> mCacheMap;
	
	public MemoryCache() {
		if(mCacheMap == null) {
			mCacheMap = new HashMap<String, Object>();
		}
	}
	
	@Override
	public void putCache(String key, Object obj) {
		mCacheMap.put(key, obj);
	}

	@Override
	public Object getCache(String key) {
		return mCacheMap.get(key);
	}
	
	@Override
	public void clear() {
		mCacheMap.clear();
	}

}
