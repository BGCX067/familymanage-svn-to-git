package com.lianluo.core.net;

import java.io.InputStream;
import java.util.HashMap;

import org.apache.http.HttpStatus;

import android.content.Context;
import android.os.Handler;

import com.lianluo.core.cache.CacheManager;
import com.lianluo.core.util.ToolsUtil;

public class DownloadHttp extends BaseHttp {
	
	private String mUrl;
	private String mName;
	private Handler mHandler;
	
	public DownloadHttp(Context context, String name, Handler handler) {
		if(ToolsUtil.isCMWAP(context)) {
			super.setCmwap(true);
		}
		this.mName = name;
		this.mHandler = handler;
	}
	
	public void setUrl(String url) {
		this.mUrl = url;
	}

	@Override
	protected String getUrl() {
		return this.mUrl;
	}

	@Override
	protected boolean onResponse(int httpStatusCode) {
		if(httpStatusCode == HttpStatus.SC_OK) {
			return true;
		}
		return false;
	}

	@Override
	protected void onReceive(InputStream is) throws Exception {
		onResponse(getResponseHeader());
		CacheManager.newInstance().putResourceCache(mName, is, mHandler);
	}
	
	protected void onResponse(HashMap<String, String> responseHeader) {
	}

	@Override
	protected void onError(Exception ex) {
		ex.printStackTrace();
	}
}
