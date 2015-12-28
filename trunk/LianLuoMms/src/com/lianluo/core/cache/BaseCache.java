package com.lianluo.core.cache;


public interface BaseCache {

	public void putCache(String key, Object obj);

	public Object getCache(String key);

	public void clear();
	
}
