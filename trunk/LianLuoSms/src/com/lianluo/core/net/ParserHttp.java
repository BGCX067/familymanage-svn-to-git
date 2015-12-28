package com.lianluo.core.net;

import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpStatus;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.util.Log;

import com.haolianluo.sms2.model.HUpdateParser;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;

public class ParserHttp extends BaseHttp {

	private static final String TAG = "ParserHttp";
	private String mUrl;
	private DefaultHandler mHandler;

	public ParserHttp(Context context, DefaultHandler handler) {
		if (ToolsUtil.isCMWAP(context)) {
			super.setCmwap(true);
		}
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
		
		String xml = ToolsUtil.InputStreamToString(is);
		HLog.d(TAG, "parser=" + xml);
		is = ToolsUtil.StringToInputStream(xml);
		
		if(value != null && Integer.valueOf(value) > 2) {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = parserFactory.newSAXParser();
			saxParser.parse(is, this.mHandler);
		}
	}

	@Override
	protected void onError(Exception ex) {
		ex.printStackTrace();
		if(mHandler instanceof HUpdateParser) {
			((HUpdateParser)mHandler).onError(ex);
		}
		
	}
}
