package com.lianluo.core.cache;

import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

import com.lianluo.core.net.ParserHttp;

public abstract class DataParser extends DefaultHandler  {
	
	private Context mContext;
	protected Object obj;
	
	public DataParser(Context context){
		this.mContext = context;
	}

	public Object getCache(String key) {
		CacheManager cache = CacheManager.newInstance();
		if(cache!=null)
		obj = cache.getDataCache(key);
		return obj;
	}
	
	public Object getNet(String key) {
		return this.getNet(key, null);
	}
	
	public Object getNet(String key, String content_type) {
		obj = null;
		ParserHttp http = new ParserHttp(mContext, this);
		http.setUrl(getUrl());
		if(content_type == null || "".equals(content_type)) {
			http.connect(getBody());
		} else {
			http.connect(getBody(), content_type, null);
		}
		if(obj != null && key != null) {
			CacheManager.newInstance().putDataCache(key, obj);
		}
		return obj;
	}
	
	public abstract String getUrl();
	
	public abstract String getBody();
	
}
