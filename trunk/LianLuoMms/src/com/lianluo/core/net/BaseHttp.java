package com.lianluo.core.net;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import com.lianluo.core.util.ToolsUtil;

/**
 * HTTP基类
 * 
 * @author ZhouJian
 * 
 */
public abstract class BaseHttp {

	public static final int SHORT_TIMEOUT = 30000;// 超时30秒
	public static final int LONG_TIMEOUT = 60000;// 超时60秒

	private boolean mIsCmwap;// 是否为CMWAP
	private HashMap<String, String> mRequestHeaderMap;
	private Header[] mResponseHeaders;//响应头
    public  HashMap<String, String> mHeaderMessageMap;

	/**
	 * 请求连接网络
	 */
	public void connect() {
		String url = getUrl();
		if (ToolsUtil.isEmpty(url)) {
			return;
		}
		try {
			HttpRequestBase request = createGet(url, mIsCmwap);
			request(request, url, mIsCmwap);
		} catch (Exception ex) {
			onError(ex);
		}
	}

	/**
	 * 请求连接网络
	 * @param body 包体
	 */
	public void connect(String body) {
		connect(body, null, null);
	}

	/**
	 * 请求连接网络
	 * @param body 包体
	 * @param content_type
	 * @param range
	 */
	public void connect(String body, String content_type, String range) {
		String url = getUrl();
		if (ToolsUtil.isEmpty(url)) {
			return;
		}
		if (ToolsUtil.isEmpty(body)) {
			throw new NullPointerException("Body is null.");
		} else {
			try {
				HttpRequestBase request = createPost(url, body, mIsCmwap);
				if(content_type != null) {
					setHttpHeader(request, "content-type", content_type);
				}
				if(range != null) {
					setHttpHeader(request, "range", "bytes=" + range);
				}
				request(request, url, mIsCmwap);
			} catch (Exception ex) {
				onError(ex);
			}
		}
	}
	
	/**
	 * 请求连接网络
	 * @param params 参数
	 */
	public void connect(HashMap<String, String> params) {
		connect(params, null, null);
	}

	/**
	 * 请求连接网络
	 * @param params 参数
	 * @param content_type
	 * @param range
	 */
	public void connect(HashMap<String, String> params, String content_type, String range) {
		String url = getUrl();
		if (ToolsUtil.isEmpty(url)) {
			return;
		}
		if (ToolsUtil.isEmpty(params)) {
			connect();
		} else {
			ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
			for (Entry<String, String> entry : params.entrySet()) {
				list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			try {
				HttpRequestBase request = createPost(url, list, mIsCmwap);
				if(content_type != null) {
					setHttpHeader(request, "content-type", content_type);
				}
				if(range != null) {
					setHttpHeader(request, "range", "bytes=" + range);
				}
				request(request, url, mIsCmwap);
			} catch (Exception ex) {
				onError(ex);
			}
		}
	}
	
	/**
	 * 构建GET请求
	 * @param url
	 * @param isCmwap
	 * @return
	 */
	private HttpRequestBase createGet(String url, boolean isCmwap) {
		HttpGet request = null;
		request = new HttpGet(url);
		return request;
	}

	/**
	 * 构建POST请求
	 * @param url
	 * @param body
	 * @param encoding
	 * @param isCmwap
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private HttpRequestBase createPost(String url, String body, boolean isCmwap) throws UnsupportedEncodingException {
		HttpPost request = null;
		request = new HttpPost(url);
		request.setEntity(new StringEntity(body, HTTP.UTF_8));
		return request;
	}

	/**
	 * 构建POST请求
	 * @param url
	 * @param params
	 * @param encoding
	 * @param isCmwap
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private HttpRequestBase createPost(String url, ArrayList<NameValuePair> params, 
			boolean isCmwap) throws UnsupportedEncodingException {
		HttpPost request = null;
		request = new HttpPost(url);
		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		return request;
	}
	
	/**
	 * 请求HTTP并响应结果
	 * @param request
	 * @param url
	 * @param isCmwap
	 * @throws Exception
	 */
	private void request(HttpRequestBase request, String url, boolean isCmwap) throws Exception {
		// 设置请求参数
		if(isCmwap) {
			setTimeOut(request.getParams(), LONG_TIMEOUT);
		} else {
			setTimeOut(request.getParams(), SHORT_TIMEOUT);
		}
		
		if(mRequestHeaderMap != null && mRequestHeaderMap.size() > 0) {
			for(Entry<String, String> entry : mRequestHeaderMap.entrySet()) {
				request.setHeader(entry.getKey(), entry.getValue());
			}
			mRequestHeaderMap.clear();
		}
		HttpResponse response = null;
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		
		if (isCmwap) {
			HttpHost proxy = new HttpHost("10.0.0.172", 80, "http");
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			response = httpClient.execute(request);
		} else {
			response = httpClient.execute(request);
		}
		mResponseHeaders = response.getAllHeaders();
		
		mHeaderMessageMap = getResponseHeader(mResponseHeaders);
		if (onResponse(response.getStatusLine().getStatusCode())) {
			onReceive(response.getEntity().getContent());
		}
	}

	/**
	 * 设置超时时间
	 * @param context
	 * @param params
	 */
	private void setTimeOut(HttpParams params, int timeout) {
		HttpConnectionParams.setConnectionTimeout(params, timeout);
		HttpConnectionParams.setSoTimeout(params, timeout);
	}

	/**
	 * 设置CMWAP
	 * @param isCmwap
	 */
	protected void setCmwap(boolean isCmwap) {
		mIsCmwap = isCmwap;
	}

	/**
	 * 设置非标准头信息
	 * @param httpRequest
	 * @param name
	 * @param value
	 */
	private void setHttpHeader(HttpRequestBase httpRequest, String name, String value) {
		if (ToolsUtil.isEmpty(name) || ToolsUtil.isEmpty(value)) {
			return;
		}
		httpRequest.setHeader(name, value);
	}
	
	protected void setRequestHeader(HashMap<String, String> requestHeaderMap) {
		this.mRequestHeaderMap = requestHeaderMap;
	}

	/**
	 * 获得响应头(key均处理为小写)
	 * @return
	 */
	private HashMap<String, String> getResponseHeader(Header[] header) {
		HashMap<String, String> httpHeaderMap = new HashMap<String, String>();
		int length = header.length;
		for(int i = 0; i < length; i++) {
			Header h = header[i];
			httpHeaderMap.put(h.getName().toLowerCase(), h.getValue());
		}
		return httpHeaderMap;
	}
	
	protected HashMap<String, String> getResponseHeader() {
		return mHeaderMessageMap;
	}
	
	/**
	 * 返回URL
	 * @return
	 */
	protected abstract String getUrl();

	/**
	 * 返回HTTP响应码
	 * @param httpStatusCode
	 * @return
	 */
	protected abstract boolean onResponse(int httpStatusCode);

	/**
	 * 接收数据
	 * @param httpEntity HTTP实体
	 * @throws Exception
	 */
	protected abstract void onReceive(InputStream is) throws Exception;

	/**
	 * 联网操作发生错误时执行
	 * @param ex 错误信息
	 */
	protected abstract void onError(Exception ex);

}
