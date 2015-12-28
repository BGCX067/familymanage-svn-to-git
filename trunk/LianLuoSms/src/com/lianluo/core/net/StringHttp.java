package com.lianluo.core.net;

import java.io.InputStream;
import java.util.HashMap;

import org.apache.http.HttpStatus;

import android.content.Context;
import android.util.Log;

import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;

public class StringHttp extends BaseHttp {

	private static final String TAG = "StringHttp";
	private String mUrl;
	private String mResponse;

	public StringHttp(Context context) {
		if (ToolsUtil.isCMWAP(context)) {
			super.setCmwap(true);
		}
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
		if (httpStatusCode == HttpStatus.SC_OK) {
			return true;
		}
		return false;
	}

	@Override
	protected void onReceive(InputStream is) throws Exception {
		HashMap<String, String> header = getResponseHeader();
		String value = header.get("content-length");
		HLog.d(TAG, "value.length()=" + Integer.valueOf(value));
		
		mResponse = ToolsUtil.InputStreamToString(is);
		HLog.d(TAG, "parser=" + mResponse);
	}
	
	public String getResponse() {
		return mResponse;
	}

	@Override
	protected void onError(Exception ex) {
		ex.printStackTrace();
	}
}
