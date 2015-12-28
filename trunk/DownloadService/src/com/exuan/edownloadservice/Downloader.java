package com.exuan.edownloadservice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import android.util.Log;

public class Downloader
{
	private static final String TAG = "Downloader";
	private int mTimeout = 30000;
	private Map<String, String> mHeadersMap = new HashMap<String, String>();
	private String mUrl;
	private boolean mIsWap = false;
	
	public Downloader()
	{
		
	}
	
	public void addHeader(String name, String value)
	{
		if(checkString(name) && checkString(value))
		{
			mHeadersMap.put(name, value);
		}
	}
	
	public void setIsWap(boolean wap)
	{
		mIsWap = wap;
	}
	
	public void setTimeout(int time)
	{
		mTimeout = time;
	}
	
	public void setUrl(String url)
	{
		mUrl = url;
	}
	
	public InputStream getHTTPClient()
	{
		if(!checkString(mUrl))
		{
			return null;
		}
		HttpGet request = new HttpGet(mUrl);
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, mTimeout);
		HttpConnectionParams.setSoTimeout(httpParams, mTimeout);
		if(mHeadersMap.size() > 0)
		{
			Iterator<Entry<String, String>> iter = mHeadersMap.entrySet().iterator();
			while(iter.hasNext())
			{
				Map.Entry<String, String> entry = (Map.Entry<String, String>)iter.next();
				String name = entry.getKey();
				String value = entry.getValue();
				request.setHeader(name, value);
			}
		}
		HttpClient client = new DefaultHttpClient(httpParams);
		if (mIsWap) 
		{
			HttpHost proxy = new HttpHost("10.0.0.172", 80, "http");
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		HttpResponse response = null;
		InputStream inputStream = null;
		try {
			response = client.execute(request);
			int responsecode = response.getStatusLine().getStatusCode();
			Log.d(TAG, "response code:" + responsecode);
			inputStream = response.getEntity().getContent();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			inputStream = null;
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			inputStream = null;
			e.printStackTrace();
		}
		return inputStream;
	}
	
	public InputStream postHTTPClient(String body)
	{
		HttpPost request = null;
		request = new HttpPost(mUrl);
		InputStream inputStream = null;
		try 
		{
			request.setEntity(new StringEntity(body, HTTP.UTF_8));
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, mTimeout);
			HttpConnectionParams.setSoTimeout(httpParams, mTimeout);
			if(mHeadersMap.size() > 0)
			{
				Iterator<Entry<String, String>> iter = mHeadersMap.entrySet().iterator();
				while(iter.hasNext())
				{
					Map.Entry<String, String> entry = (Map.Entry<String, String>)iter.next();
					String name = entry.getKey();
					String value = entry.getValue();
					request.setHeader(name, value);
				}
			}
			HttpClient client = new DefaultHttpClient(httpParams);
			if (mIsWap) 
			{
				HttpHost proxy = new HttpHost("10.0.0.172", 80, "http");
				client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			}
			HttpResponse response = null;
			response = client.execute(request);
			int responsecode = response.getStatusLine().getStatusCode();
			Log.d(TAG, "response code:" + responsecode);
			inputStream = response.getEntity().getContent();
		}catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			inputStream = null;
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			inputStream = null;
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			inputStream = null;
			e.printStackTrace();
		}
		return inputStream;
	}
	
	public InputStream getHTTPUrl()
	{
		InputStream inputStream = null;
		URL url = null;
		URLConnection urlconn = null;
		HttpURLConnection conn = null;
		try
		{
			url = new URL(mUrl);
			if (mIsWap) 
			{
				Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80));
				urlconn =  url.openConnection(proxy);
			}else
			{
				urlconn = url.openConnection();
			}
			conn = (HttpURLConnection) urlconn;
			if(conn != null)
			{
				conn.setRequestMethod("GET");
				conn.setReadTimeout(mTimeout);
				conn.setConnectTimeout(mTimeout);
				inputStream = conn.getInputStream();
			}
		} 
		catch (Exception e)
		{
			inputStream = null;
			e.printStackTrace();
		}
		finally
		{
			if(conn != null)
			{
				conn.disconnect();
			}
		}
		return inputStream;
	}
	
	@SuppressWarnings("unchecked")
	public InputStream postHTTPUrl(Map params)
	{
		InputStream inputStream = null;
		URL url = null;
		URLConnection urlconn = null;
		HttpURLConnection conn = null;
		try
		{
			StringBuffer param = new StringBuffer();
			if(params.size() > 0)
			{
				Iterator iter = params.entrySet().iterator();
				while(iter.hasNext())
				{
					Map.Entry entry = (Map.Entry)iter.next();
					String key = entry.getKey().toString();
					String value = entry.getValue().toString();
					param.append(key);
					param.append("=");
					param.append(URLEncoder.encode(value, HTTP.UTF_8));
					param.append("&");
				}
			}
			url = new URL(mUrl);
			if (mIsWap) 
			{
				Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80));
				urlconn =  url.openConnection(proxy);
			}else
			{
				urlconn = url.openConnection();
			}
			conn = (HttpURLConnection) urlconn;
			if(conn != null)
			{
				conn.setRequestMethod("POST");
				conn.setReadTimeout(mTimeout);
				conn.setConnectTimeout(mTimeout);
				conn.setDoOutput(true);
	            byte[] b = param.toString().getBytes();
	            conn.getOutputStream().write(b, 0, b.length);
	            conn.getOutputStream().flush();
	            conn.getOutputStream().close();
	            inputStream = conn.getInputStream();
			}
		} 
		catch (Exception e)
		{
			inputStream = null;
			e.printStackTrace();
		}
		finally
		{
			if(conn != null)
			{
				conn.disconnect();
			}
		}
		return inputStream;
	}
	
	private boolean checkString(String string)
	{
		if(string == null || string.trim().length() == 0)
		{
			return false;
		}
		return true;
	}
}