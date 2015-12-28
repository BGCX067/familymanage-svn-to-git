package com.haolianluo.sms2.data;


import java.util.ArrayList;
import java.util.List;

import com.haolianluo.sms2.HThreadAdapter;
import com.haolianluo.sms2.model.HSms;

import android.app.Application;

/**
 * 数据共享
 * @author jianhua 2011年11月15日18:36:32
 */

public class HSmsApplication extends Application {
	
	public HThreadAdapter adapter = null;
	
	public HThreadAdapter collectAdapter = null;
	
	/**
	 * 收到短信临时存储list
	 */
	public List<HSms> list = new ArrayList<HSms>();


}
