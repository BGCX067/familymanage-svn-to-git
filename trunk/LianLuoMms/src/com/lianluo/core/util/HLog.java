package com.lianluo.core.util;

import android.util.Log;

/***
 * 
 * log工具类
 * @author jianhua   2011年9月2日8:57:25
 *
 */
public class HLog {
	
	private static final String TAG = "lianluosms";

	/**是否打印Log*/
	private static boolean isLog = true;
	
	/**
	 * Send an INFO log message.
	 * @param tag
	 * @param msg
	 */
	public static void i(String tag,String msg){
		if(isLog){
			Log.i(tag, msg);
		}
	}
	/**
	 * Send a DEBUG log message.
	 * @param tag
	 * @param msg
	 */
	public static void d(String tag,String msg){
		if(isLog){
			Log.d(tag, msg);
		}
	}
	public static void w(String tag,String msg){
		if(isLog){
			Log.w(tag, msg);
		}
	}
	public static void v(String tag,String msg){
		if(isLog){
			Log.v(tag, msg);
		}
	}
	public static void e(String tag,String msg){
		if(isLog){
			Log.e(tag, msg);
		}
	}
	
	public static void i(String msg){
		if(isLog){
			i(TAG,msg);
		}
	}
	
	public static void d(String msg){
		if(isLog){
			d(TAG,msg);
		}
	}
	
	public static void w(String msg){
		if(isLog){
			w(TAG,msg);
		}
	}
	
	public static void e(String msg){
		if(isLog){
			e(TAG,msg);
		}
	}
	public static void v(String msg){
		if(isLog){
			v(TAG,msg);
		}
	}
	
}
