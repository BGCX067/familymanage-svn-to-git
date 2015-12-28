package com.haolianluo.sms2.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.haolianluo.sms2.data.HSharedPreferences;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class HStatistics {
	/**
	 * t、用户登录退出时间戳
	 * <t>(1294827620000,1294914020000).(1294914025000,1295000420000)</t>
	 * 格式：标签<t></t> 内容（登录时间戳，退出时间戳）多次登录用“.“隔开
	 */
	public static final String T = "t";
	/**
	 * 非计数 格式：0.日志日期 记录某天的记录时间以当天最近的一次登录时间为准(2011-01-12
	 * 18:20:20-->1294827620000) 若该天一直为登录状态则以当天00：00：00为准（2011-01-12
	 * 00:00:00-->1294761600000)
	 */
	public static final String Z0 = "z0";
	/**
	 * 退出时使用
	 */
	public static final String Z0_1 = "z0_1";

	//飘窗
	//1.接到短信提示的小窗口次数.点击飘窗进入联络短信次数.飘窗点击X的次数.飘窗点击窗口直接回复短信数.呼叫按钮点击次数
	public static final String Z1 = "z1";
	/***
	 * 接到短信提示的小窗口次数
	 */
	public static final String Z1_1 = "z1_1";
	/**
	 * 点击飘窗进入联络短信次数
	 */
	public static final String Z1_2 = "z1_2";
	/***
	 * 飘窗点击X的次数.
	 */
	public static final String Z1_3 = "z1_3";
	/**
	 * 飘窗点击窗口直接回复短信数
	 */
	public static final String Z1_4 = "z1_4";
	/**
	 * 呼叫按钮点击次数
	 */
	public static final String Z1_5 = "z1_5";
	
	//2、桌面点击进入次数
	/**
	 * 2、桌面点击进入次数
	 */
	public static final String Z2 = "z2";
	
	//3、快捷菜单（右上）点击数
	/***
	 * 3、快捷菜单（右上）点击数
	 */
	public static final String Z3 = "z3";
	
	//4、撰写短信按钮点击数
	//4.添加收信人按钮点击数.发送按钮点击次数
	public static final String Z4 = "z4";
	/**
	 * 撰写短信按钮点击数
	 */
	public static final String Z4_1 = "z4_1";
	/**
	 * 添加收信人按钮点击数
	 */
	public static final String Z4_2 = "z4_2";
	/***
	 * 发送按钮点击次数
	 */
	public static final String Z4_3 = "z4_3";
	/***
	 * 最近联系人点击数按钮点击次数
	 */
	public static final String Z4_4 = "z4_4";
	
	//5、MENU菜单窗口
	public static final String Z5 = "z5";
	/***
	 * MENU菜单窗口点击次数
	 */
	public static final String Z5_1 = "z5_1";
	/***
	 * 搜索的点击数
	 */
	public static final String Z5_2 = "z5_2";

	/**
	 * 标记的点击次数
	 */
	public static final String Z5_4 = "z5_4";
	/**
	 * 标记全部的点击数
	 */
	public static final String Z5_4_1 = "z5_4_1";
	/**
	 * 取消标记的点击数
	 */
	public static final String Z5_4_2 = "z5_4_2";
	/**
	 * 删除选中的点击数
	 */
	public static final String Z5_4_3 = "z5_4_3";
	/**
	 * 返回的点击数
	 */
	public static final String Z5_4_4 = "z5_4_4";
	/**
	 * 标记删除的短信数
	 */
	public static final String Z5_4_5 = "z5_4_5";
	/**
	 * 设置点击次数
	 */
	public static final String Z5_5 = "z5_5";
	/**
	 * 关于点击次数
	 */
	public static final String Z5_6 = "z5_6";
	/***
	 * 分享的点击数
	 */
	public static final String Z5_7 = "z5_7";
	/***
	 * 账户管理点击次数
	 */
	public static final String Z5_8 = "z5_8";

	/**
	 * 下载检查更新的点击数
	 */
	public static final String Z5_11 = "z5_11";
	/**
	 * 分享点击数
	 */
	public static final String Z5_12 = "z5_12";
	/**
	 * 分享的人数
	 */
	public static final String Z5_13 = "z5_13";
	/**
	 * 关于点击数
	 */
	public static final String Z5_14 = "z5_14";
	/**
	 * 腾讯应用中心
	 */
	public static final String Z5_15 = "z5_15";
	/**
	 * 反馈的点击数
	 */
	public static final String Z5_16 = "z5_16";
	/**
	 * 反馈确定的点击数
	 */
	public static final String Z5_16_1 = "z5_16_1";
	/**
	 * 收藏点击次数
	 */
	public static final String Z5_9 = "z5_9";
	/**
	 * 长按回复数
	 */
	public static final String Z5_9_1 = "z5_9_1";
	/***
	 * 呼叫数
	 */
	public static final String Z5_9_2 = "z5_9_2";
	/**
	 * 取消收藏数
	 */
	public static final String Z5_9_3 = "z5_9_3";
	/***
	 * 保存联系人点击数
	 */
	public static final String Z5_9_4 = "z5_9_4";
	
	
	//6、短信泡泡界面
	//商店点击数.发送短信点击次数.添加收藏.转发.转发最近联系人数.添加联系人数.转发后的发送点击数.草稿发送点击数.呼叫.复制文本.删除.添加联系人
	public static final String Z6 = "z6";
	/**
	 * 商店点击数
	 */
	public static final String Z6_1 = "z6_1";
	/**
	 * 发送短信点击次数
	 */
	public static final String Z6_2 = "z6_2";
	/**
	 * 添加收藏
	 */
	public static final String Z6_3 = "z6_3";
	/**
	 * 转发
	 */
	public static final String Z6_4 = "z6_4";
	/**
	 * 呼叫
	 */
	public static final String Z6_9 = "z6_9";
	/**
	 * 复制文本.
	 */
	public static final String Z6_10 = "z6_10";
	/**
	 * 删除.
	 */
	public static final String Z6_11 = "z6_11";
	/**
	 * 添加联系人
	 */
	public static final String Z6_12 = "z6_12";
	
	//7、信息详细页面
	//7.阅读方式.商店点击数.发送短信点击次数.(长按转发数.转发后的发送点击数.呼叫数.删除数.复制文本数.添加收藏数.保存联系人点击数)   
	public static final String Z7 = "z7";
	/**
	 * 商店点击数
	 */
	public static final String Z7_1 = "z7_1";
	/**
	 * 发送短信点击次数
	 */
	public static final String Z7_2 = "z7_2";
	/**
	 * 转发数
	 */
	public static final String Z7_3 = "z7_3";
	/**
	 * 转发后的发送点击数
	 */
	//public static final String Z7_4 = "z7_4";
	/**
	 * 呼叫数.
	 */
	public static final String Z7_5 = "z7_5";
	/**
	 * 删除数.
	 */
	public static final String Z7_6 = "z7_6";
	/**
	 * 复制文本数.
	 */
	public static final String Z7_7 = "z7_7";
	/**
	 * 添加收藏数.
	 */
	public static final String Z7_8 = "z7_8";
	/**
	 * 保存联系人点击数
	 */
	public static final String Z7_9 = "z7_9";
	//8、资源库
	public static final String Z8 = "z8";
	/**
	 *付费排行点击次数 
	 */
	public static final String Z8_1 = "z8_1";
	/**
	 * 免费排行点击次数
	 */
	public static final String Z8_2 = "z8_2";
	/**
	 * 畅销排行点击次数
	 */
	public static final String Z8_3 = "z8_3";
	/**
	 * 我的模板点击次数
	 */
	public static final String Z8_4 = "z8_4";
	
	//9、登陆界面
	public static final String Z9 = "z9";
	/**
	 * 用户登陆数
	 */
	public static final String Z9_1 = "z9_1";
	/**
	 * 用户点击注册数
	 */
	public static final String Z9_2 = "z9_2";
	/**
	 * 用户点击注册取消数
	 */
	public static final String Z9_3 = "z9_3";
	/**
	 * 用户点击忘记密码数
	 */
	public static final String Z9_4 = "z9_4";
	
	//10、模板
	public static final String Z10 = "z10";
	/**
	 * 模板ID.来源.收费1/免费0.模版点击数
	 */
	public static final String Z10_1 = "z10_1";
	/**
	 * 模板详情点击数
	 */
	public static final String Z10_2 = "z10_2";
	/**
	 * 模板点击下载数
	 */
	public static final String Z10_3 = "z10_3";
	/**
	 * 收费免费
	 */
	public static final String Z10_4 = "z10_4";
	/***
	 * 模板点击安装数
	 */
	public static final String Z10_5 = "z10_5";
	/**
	 * 确认安装界面确认点击数
	 */
	public static final String Z10_5_1 = "z10_5_1";
	/**
	 * 确认安装界面取消点击数
	 */
	public static final String Z10_5_2 = "z10_5_2";
	/**
	 * 安装后点击数
	 */
	public static final String Z10_5_3 = "z10_5_3";
	
	//11、收费
	public static final String Z11 = "z11";
	/**
	 * 付费提示框确认点击数
	 */
	public static final String Z11_1 = "z11_1";
	/**
	 * 取消点击数
	 */
	public static final String Z11_2 = "z11_2";
	/**
	 * 计费短信的弹出数
	 */
	public static final String Z11_3 = "z11_3";
	
	//12、我的模板
	public static final String Z12 = "z12";
	/**
	 * [模板ID.来源.使用次数.当前使用状态]
	 */
	/**
	 * 安装数
	 */
	public static final String Z12_1 = "z12_1";
	
	/**
	 * 删除数
	 */
	public static final String Z12_2 = "Z12_2";
	/**
	 * 应用数
	 */
	public static final String Z12_3 = "Z12_3";
	
	
	//回复数.呼叫数.删除数.保存联系人点击数 
	/***
	 * 回复数.
	 */
	public static final String Z13_1 = "Z13_1";
	/**
	 * 呼叫数.
	 */
	public static final String Z13_2 = "Z13_2";
	/**
	 * 删除数.
	 */
	public static final String Z13_3 = "Z13_3";
	/**
	 * 保存联系人点击数 
	 */
	public static final String Z13_4 = "Z13_4";



	//-----------------------------------------

	public static final String TABLE_NAME = "stata";
	/**
	 * _ID
	 */
	public static final String DB_ID = "_id";
	/**
	 * 操作描述符
	 */
	public static final String DB_CZ = "cz";
	public static final String DB_DATETIME = "dt";
	/**
	 * 操作描述1
	 */
	public static final String DATA1 = "data1";
	/**
	 * 操作描述2
	 */
	public static final String DATA2 = "data2";
	/**
	 * 操作描述3
	 */
	public static final String DATA3 = "data3";
	/**
	 * 总的条数
	 */
	public static final String COUNT = "count";
	
	
	public static final String TABLE_CREATE = "create table IF NOT EXISTS  "
			+ TABLE_NAME + " " + "(" + DB_ID
			+ " integer primary key autoincrement, " + DB_CZ
			+ " text not null," + DB_DATETIME + " text," +COUNT + " integer,"+ DATA1 + " text,"
			+ DATA2 + " text," + DATA3 + " text);";
	SQLiteDatabase db;
	HDatabaseHelper helper;
	Context context;
	String today;

	public HStatistics(Context context) {
		this.context = context;
		helper = new HDatabaseHelper(context);
		Calendar td = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DATE));
		today = String.valueOf(td.getTimeInMillis());
	}

	private int getCount(String zc, String data) {
		int rv = 0;
		Cursor c = db.query(TABLE_NAME, new String[]{COUNT}, DB_CZ + " ='" + zc +"' and " + DB_DATETIME + " ='" + data + "'", null, null, null, null);
		if (c.moveToFirst()) {
			rv = c.getInt(0);
		}
		if(c.getCount() == 0){
			rv = 0;
		}
		c.close();
		return rv;
	}
	
	private int getCountForTen(String zc, String data, String d1) {
		int rv = 0;
		Cursor c = db.query(TABLE_NAME, new String[]{COUNT}, DB_CZ + " ='" + zc +"' and " + DB_DATETIME + " ='" + data + "'" + " and " + DATA1 + " ='" + d1 +"'", null, null, null, null);
		if (c.moveToFirst()) {
			rv = c.getInt(0);
		}
		if(c.getCount() == 0){
			rv = 0;
		}
		c.close();
		return rv;
	}

	/*public String getLogData()
	{
		StringBuilder sb = new StringBuilder("<t>");
		db = helper.getWritableDatabase();
		Cursor c = db.query(TABLE_NAME, new String[]{DB_DATETIME}, DB_CZ + " = '" + Z13_1_1 + "' OR " + 
		DB_CZ + " = '" + Z13_1_4 + "'", null, null, null, DB_DATETIME);
		int i = 0;
		if(c != null && c.getCount() > 0)
		{
			while(c.moveToNext())
			{
				if(i % 2 == 0)
				{
					if(i > 1)
					{
						sb.append(".");
					}
					sb.append("(");
					sb.append(c.getString(0));
				}
				else
				{
					sb.append(".");
					sb.append(c.getString(0));
					sb.append(")");
				}
			}
		}
		c.close();
		sb.append("</t>");
		return sb.toString();
	}*/
	
	public String getDescribe(String zc, String data) {
		db = helper.getWritableDatabase();
		String rvalue = "";
		if(zc.equals(Z1)){//飘窗
			rvalue = "1." + getCount(Z1_1, data) + "." + getCount(Z1_2, data) + "." + getCount(Z1_3, data) +  "." + getCount(Z1_4, data) + "." + getCount(Z1_5, data);
		}else if(zc.equals(Z2)){//桌面点击进入次数
			rvalue = "2." + getCount(Z2, data);
		}else if(zc.equals(Z3)){//快捷菜单（右上）点击数
			rvalue = "3." + getCount(Z3, data);
		}else if(zc.equals(Z4)){//撰写短信按钮点击数
			rvalue = "4." + getCount(Z4_1, data) + "." + getCount(Z4_2, data) + "." + getCount(Z4_3, data) /*+ "." + getCount(Z4_4, data)*/;
		}else if(zc.equals(Z5)){//MENU菜单窗口
			HSharedPreferences sp = new HSharedPreferences(context);
			boolean xx = sp.getMessageSwitch();//消息栏  开1  关 0
			boolean dxzd = sp.getVibrationSwitch();//短信震动提醒开关数
			boolean kjdx = sp.getShortcutSwitch();//快捷短信开关数.
			String strXX = "0",strDXZD = "0",strKJDX = "0";
			if(xx){
				strXX = "1";
			}
			if(dxzd){ 
				strDXZD = "1";
			}
			if(kjdx){
				strKJDX = "1";
			}
			//5.MENU键点击数.搜索按钮点击数.（通过联系人搜索次数．通过号码搜索次数．通过信息内容搜索次数）．清空按钮点击数.（确定的点击数．取消的点击数）
			//.标记按钮点击数．（标记全部的点击数.取消标记的点击数.删除选中的点击数.返回的点击数.标记删除的短信数）
			//.设置按钮点击数.消息栏提醒开关（开1关0）．短信振动提醒开关（开1关0）．
			 // 快捷短信开关（开1关0）.检查更新.分享.（分享人数）.关于.收藏按钮点击数.我要说2句点击数
			rvalue = "5." + getCount(Z5_1, data) + "." + getCount(Z5_2, data) + ".(0.0.0).0.(0.0)." + getCount(Z5_4, data) + ".(" 
			              + getCount(Z5_4_1, data)	+ "." + getCount(Z5_4_2, data) + "." + getCount(Z5_4_3, data) + "." + getCount(Z5_4_4, data) + "." + getCount(Z5_4_5, data) 
			              + ")." + getCount(Z5_5, data)
			               + "." + strXX + "." + strDXZD + "." + strKJDX + "." + getCount(Z5_11, data) 
			               + "." + getCount(Z5_12, data) + ".(" + getCount(Z5_13, data) + ")" + "." +  getCount(Z5_14,data) 
			               + "." + getCount(Z5_9, data) + "." + getCount(Z5_16_1, data);
//			rvalue = "5." + getCount(Z5_1, data) + "." + getCount(Z5_2, data) + ".(" + getCount(Z5_4_1, data)
//					  + "." + getCount(Z5_4_2, data) + "." + getCount(Z5_4_3, data) + "." + getCount(Z5_4_4, data) + ")." + getCount(Z5_5, data) 
//					  + "." + strXX + "." + strDXZD + "." + strKJDX + "." + getCount(Z5_11, data) 
//					  + "." + getCount(Z5_12, data) + ".(" + getCount(Z5_13, data) + ")" + "." +  getCount(Z5_14,data) 
//					  + "." + getCount(Z5_9, data) + "." + getCount(Z5_16_1, data);
		}else if(zc.equals(Z6)){//短信泡泡界面
//			//6.商店点击数.发送短信点击次数.添加收藏.转发.转发最近联系人数.添加联系人数.转发后的发送点击数.草稿发送点击数.呼叫.复制文本.删除.添加联系人
//			rvalue = "6." + getCount(Z6_1, data) + "." + getCount(Z6_2, data) + "." + getCount(Z6_3, data) + "." + getCount(Z6_4, data)
//					+ "." + "0" + "." + "0" + "." + "0" + "." + "0"
//					+ "." + getCount(Z6_9, data) + "." + getCount(Z6_10, data) + "." + getCount(Z6_11, data) + "." + getCount(Z6_12, data);
			
			//6.打电话按钮点击次数.商店点击数.发送短信点击次数.添加收藏.转发.呼叫.复制文本.删除.添加联系人
			rvalue = "6." + "0" + "." + getCount(Z6_1, data) + "." + getCount(Z6_2, data) + "." + getCount(Z6_3, data)
					+ "." + getCount(Z6_4, data) +  "." + getCount(Z6_9, data) + "." + getCount(Z6_10, data)
					+ "." + getCount(Z6_11, data) + "." + getCount(Z6_12, data);
 					 
		}else if(zc.equals(Z7)){//信息详细页面
			//7.阅读方式.商店点击数.发送短信点击次数.(长按转发数.转发后的发送点击数.呼叫数.删除数.复制文本数.添加收藏数.保存联系人点击数) 
			HSharedPreferences sp = new HSharedPreferences(context);
			int type = sp.getReadSmsType();//0 --flash 1---talk
			if(type == 1){
				type = 0;
			}else if(type == 0){
				type = 1;
			}
			rvalue = "7." +  "0" + "." + getCount(Z7_1, data) + "." + getCount(Z7_2, data) + "." + type;
//			rvalue = "7." + type + "." + getCount(Z7_1, data) + "." + getCount(Z7_2, data) + ".(" + getCount(Z7_3, data) + "." + "0"
//					  + "." + getCount(Z7_5, data) + "." + getCount(Z7_6, data) + "." + getCount(Z7_7, data) + "." + getCount(Z7_8, data) 
//					  + "." + getCount(Z7_9, data) + ")";
		}else if(zc.equals(Z8)){//资源库
			rvalue = "8." + getCount(Z8_1, data) + "." + getCount(Z8_2, data) + "." + getCount(Z8_3, data) + "." + getCount(Z8_4, data);
//			rvalue = "8." + getCount(Z8_1, data) + "." + getCount(Z8_2, data) + "." + getCount(Z8_3, data) + "." + getCount(Z8_4, data) 
//					+ "." + getCount(Z8_5, data) + "." + getCount(Z8_6, data) + "." + getCount(Z8_7, data) + "." + getCount(Z8_8, data);
		}else if(zc.equals(Z9)){//登陆界面
			if(ToolsUtil.IM_FLAG) {
				rvalue = "9." + getCount(Z9_1, data) + "." + getCount(Z9_2, data) + "." + getCount(Z9_3, data) + "." + getCount(Z9_4, data);
			}
		}else if(zc.equals(Z10)){//模板
//			rvalue = "10.[" + getCount(Z10_1, data) + "." + getCount(Z10_2, data) + "." + getCount(Z10_3, data) + "." + getCount(Z10_4, data)
//					+ "." + getCount(Z10_5, data) + ".(" + getCount(Z10_5_1, data) + "." + getCount(Z10_5_2, data) + ")." + getCount(Z10_5_3, data) + "]";
			//查出某一天的Z10_1的相关统计，一个ID的一个，cursor包含多个Id,多列
			Cursor c = db.query(TABLE_NAME, new String[]{DATA1, DATA2, DATA3, COUNT, "count(" + DATA1 + ")" } , DB_CZ + " = '" + Z10_1 + "'", null, DATA1, null, null);
			if(c != null && c.getCount() > 0)
			{
				//10.[214.1.1.3.2.1.1.3.(2.1).1].10.[216.1.1.3.2.1.1.3.(2.1).1]
				StringBuilder sbList = new StringBuilder("");
				int i = 0;
				while(c.moveToNext())
				{
					if(i != 0) {
						sbList.append(".");
					}
					i++;
					
					StringBuilder sb = new StringBuilder("10");
					sb.append(".[");
					sb.append(c.getString(0));
					sb.append(".");
					sb.append(c.getString(1));
					sb.append(".");
					sb.append(c.getString(2));
					sb.append(".");
					
					sb.append(c.getString(1));	//栏目和来源一致
					
					//以上为10_1: 模板ID.来源.收费1/免费0.栏目（免费．付费．畅销）.模版点击数
					sb.append(".");
					sb.append(getCountForTen(Z10_2, data, c.getString(0)));
					sb.append(".");
					sb.append(getCountForTen(Z10_3, data, c.getString(0)));
					sb.append(".");
					
					//sb.append(getCountForTen(Z10_4, data, c.getString(0)));
					//收费1/免费0
					sb.append(c.getString(2));
					
					sb.append(".");
					sb.append(getCountForTen(Z10_5, data, c.getString(0)));
					
					sb.append(".(");
					sb.append(getCountForTen(Z10_5_1, data, c.getString(0)));
					sb.append(".");
					sb.append(getCountForTen(Z10_5_2, data, c.getString(0)));
					
					sb.append(").");
					sb.append(getCountForTen(Z10_5_3, data, c.getString(0)));
					
					sb.append("]");
					
					sbList.append(sb.toString());
					HLog.i("TAG", "z_10: " + sb.toString());
				}
				rvalue = sbList.toString();
			}
			c.close();
			
		}else if(zc.equals(Z11)){//收费
			//rvalue = "11." + getCount(Z11_1, data) + "." + getCount(Z11_2, data) + "." + getCount(Z11_3, data);
			
			//11.[1(268).1(278)].11.[1(278).1(278)]
			//11.付费提示框确认点击数(模板ID).取消点击数(模板ID)
			Cursor c = db.query(TABLE_NAME, new String[]{COUNT, DATA1, "count(" + DATA1 + ")" } , DB_CZ + " = '" + Z11_1 + "'", null, DATA1, null, null);
			StringBuilder sb = new StringBuilder("11");
			if(c != null && c.getCount() > 0)
			{
				int i = 0;
				while(c.moveToNext())
				{
					if(i!= 0)
					{
						sb.append(".");
						sb.append("11");
					}
					i++;
					sb.append(".[");
					sb.append(c.getString(0));
					
					sb.append("(");
					sb.append(c.getString(1));
					sb.append(")");
					
					sb.append(".");
					sb.append(getCountForTen(Z11_2, data, c.getString(1)));
					
					sb.append("(");
					sb.append(c.getString(1));
					sb.append(")");
					
					sb.append("]");
				}
			}
			else
			{
				sb.append(".");
			}
			c.close();
			rvalue = sb.toString();
		
			
		}else if(zc.equals(Z12)){//我的模板
			Cursor c = db.query(TABLE_NAME, new String[]{DATA1} , 
		    DB_CZ + " = '" + Z12_1 + "'" + " OR " + DB_CZ + " = '" + Z12_2 + "'" + " OR " + DB_CZ + " = '" + Z12_3 + "'",
			null, DATA1, null, null);
			StringBuilder sb = new StringBuilder("");
			if(c != null && c.getCount() > 0)
			{
				while(c.moveToNext())
				{
					sb.append("12.[");
					String id = c.getString(0);
					sb.append(id);
					//install count
					sb.append(".");
					Cursor cu = db.query(TABLE_NAME, new String[]{COUNT} , 
						    DB_CZ + " = '" + Z12_1 + "'" + " AND " + DATA1 + " = '" + id + "'",
							null, null, null, null);
					if(cu != null && cu.getCount() > 0)
					{
						cu.moveToNext();
						sb.append(cu.getString(0));
					}
					else
					{
						sb.append("0");
					}
					//delete count
					sb.append(".");
					cu = db.query(TABLE_NAME, new String[]{COUNT} , 
						    DB_CZ + " = '" + Z12_2 + "'" + " AND " + DATA1 + " = '" + id + "'",
							null, null, null, null);
					if(cu != null && cu.getCount() > 0)
					{
						cu.moveToNext();
						sb.append(cu.getString(0));
					}
					else
					{
						sb.append("0");
					}
					//apply count
					sb.append(".");
					cu = db.query(TABLE_NAME, new String[]{COUNT} , 
						    DB_CZ + " = '" + Z12_3 + "'" + " AND " + DATA1 + " = '" + id + "'",
							null, null, null, null);
					if(cu != null && cu.getCount() > 0)
					{
						cu.moveToNext();
						sb.append(cu.getString(0));
					}
					else
					{
						sb.append("0");
					}
					sb.append("].");
					cu.close();
				}
				Cursor co  = context.getContentResolver().query(HResProvider.CONTENT_URI_SKIN, new String[]{HResDatabaseHelper.RES_ID}, HResDatabaseHelper.RES_USE + " = '1'", null, null);
				if(co != null && co.getCount() > 0)
				{
					co.moveToNext();
					String resid = co.getString(co.getColumnIndex(HResDatabaseHelper.RES_ID));
					resid = (resid == null || resid.trim().equals("")) ? "0" : resid;
					sb.append(resid);
				}
				else
				{
					sb.append("0");
				}
				co.close();
			}
			c.close();
			rvalue = sb.toString();
		}else if(zc.equals(T)){
			Cursor ct = db.rawQuery("select data1,data2,data3 from " + TABLE_NAME + " where " + DB_CZ + " = ? and " +  DB_DATETIME + " != ?", new String[]{Z0,today});
			String rct = "";
			while(ct.moveToNext()){
				String tmp1 = ct.getString(0);
				String tmp2 = ct.getString(1);
				if(tmp1 == null || tmp1.equals("")){
					tmp1 = today;
				}
				if(tmp2 == null || tmp2.equals("")){
					tmp2 = today;
				}
				rct += "(" + tmp1 + "." + tmp2 + ").";
			}
			ct.close();
			if(!rct.equals("")){
				rvalue = rct.substring(0,rct.length()-1);
			}
		}else if(zc.equals(Z0)){
			Cursor ct = db.rawQuery("select max(data1) from " + TABLE_NAME + " where " + DB_CZ + " = ? and " + DB_DATETIME + " = ?", new String[]{Z0,data});
			String rv = "";
			if(ct.moveToFirst()){
				rv = ct.getString(0);
			}
			if(rv == null){
				rv = today;
			}
			ct.close();
			rvalue = "0." + rv;
		}
//		else if(zc.equals(Z13)){//设置 
//			HSharedPreferences sp = new HSharedPreferences(context);
//			boolean xx = sp.getMessageSwitch();//消息栏  开1  关 0
//			boolean dxzd = sp.getVibrationSwitch();//短信震动提醒开关数
//			boolean kjdx = sp.getShortcutSwitch();//快捷短信开关数.
//			String strXX = "0",strDXZD = "0",strKJDX = "0";
//			if(xx){
//				strXX = "1";
//			}
//			if(dxzd){ 
//				strDXZD = "1";
//			}
//			if(kjdx){
//				strKJDX = "1";
//			}
//			rvalue = "13." + strXX + "." + strDXZD + "." + strKJDX + "." + getCount(Z13_1, data) + ".(" + getCount(Z13_1_1, data) + "." + getCount(Z13_1_2, data)
//					+ "." + getCount(Z13_1_3, data) + "." + getCount(Z13_1_4, data) + ")." +  getCount(Z13_2, data) + "." + getCount(Z13_3, data)
//					+ "." + getCount(Z13_4, data);
//		}else if(zc.equals(Z14)){
//			rvalue = "14." + getCount(Z14_1, data) + ".(" + getCount(Z14_2, data) + "." + getCount(Z14_3, data) + getCount(Z14_4, data) + ")"; 
//		}
		db.close();
		HLog.i("TAG", "data result: " + rvalue);
		return rvalue;
	}

	public String[] getDateTimeList() {
		db = helper.getWritableDatabase();
		Cursor dt = db.rawQuery("select " + DB_DATETIME + " from " + TABLE_NAME
				+ " group by " + DB_DATETIME + " having " + DB_DATETIME
				+ " != ?", new String[] { today });
		String[] s = new String[dt.getCount()];
		int i = 0;
		while (dt.moveToNext()) {
			s[i++] = dt.getString(0);
		}
		dt.close();
		db.close();
		return s;
	}

	public void add(String zc, String d1, String d2, String d3) {
		db = helper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		if (zc.equals(Z0_1)) {
			cv.put(DATA2, d2);
			db.update(TABLE_NAME, cv, " data1 in (select max(data1) from "+ TABLE_NAME + " where " + DB_CZ + " = ?)",new String[] { Z0 });
		} else {
			String count = "1";
			if(zc.equals(Z5_13) || zc.equals(Z5_4_5)){
				count = d1;
			}
			if(zc.equals(Z10_1) || zc.equals(Z10_2) || zc.equals(Z10_3) || zc.equals(Z10_5) ||zc.equals(Z10_5_1) ||  zc.equals(Z10_5_2) || zc.equals(Z10_5_3)
					|| zc.equals(Z11_1) ||  zc.equals(Z11_2) || zc.equals(Z12_1) || zc.equals(Z12_2) || zc.equals(Z12_3)// || zc.equals(Z12_4)
					 )
			{
				/***
				 * 插入统计数据的时候是针对某一个模板来插入的，规则如下：
				 * 1.先根据ZC、ID、DATETIME 三个参数来确定数据库中该条统计的count列的值;
				 * 2.如果该条数据存在：对该条数据进行更新，设置count值为count+1;
				 * 3.如果该条数据不存在，直接插入该条数据；
				 */
				
				//1.先根据ZC、ID、DATETIME 三个参数来确定数据库中该条统计的count列的值;
				Cursor cursor_10 = db.rawQuery("select " + DB_CZ + " from " + TABLE_NAME + " where " + DB_CZ + " = ? and " + DB_DATETIME + " = ? and " + DATA1 + " = ? ", 
						new String[]{zc, today, d1});
				if(cursor_10.getCount() == 0)
				{
					//无数据，直接插入即可
					cv.put(DB_CZ, zc);
					cv.put(COUNT, Integer.parseInt(count));
					cv.put(DATA1, d1);
					cv.put(DATA2, d2);
					cv.put(DATA3, d3);
					cv.put(DB_DATETIME, today);
					db.insert(TABLE_NAME, null, cv);
				}else
				{
					//有数据，更新之,count加1
					db.execSQL("update " + TABLE_NAME + " set " + COUNT + "=((select " + COUNT + " from " + TABLE_NAME + " where " + DB_CZ + " = '" + zc+ "' and " + DB_DATETIME +  " = '" + today + "' and " + DATA1 +  " = '" + d1 +"'"+ ") + " + count +")" + " where " + DB_CZ + " = ? and " + DB_DATETIME +  " = ? and " + DATA1 + " = ? ",new String[]{zc,today, d1});
				}
				
				cursor_10.close();
			}
			else
			{
				Cursor cursor = db.rawQuery("select " + DB_CZ + " from " + TABLE_NAME  + " where " + DB_CZ + " = ? and " + DB_DATETIME +  " = ? ", new String[]{ zc,today });
				if(cursor.getCount() == 0 || zc.equals(Z12_1) || /*zc.equals(Z13_1_1) || zc.equals(Z13_1_4) ||*/ zc.equals(Z0) || zc.equals(Z0_1)){
					cv.put(DB_CZ, zc);
					cv.put(COUNT, Integer.parseInt(count));
					cv.put(DATA1, d1);
					cv.put(DATA2, d2);
					cv.put(DATA3, d3);
					cv.put(DB_DATETIME, today);
					db.insert(TABLE_NAME, null, cv);
				}
				else{
					  db.execSQL("update " + TABLE_NAME + " set " + COUNT + "=((select " + COUNT + " from " + TABLE_NAME + " where " + DB_CZ + " = '" + zc+ "' and " + DB_DATETIME +  " = '" + today  +"'"+") + " + count +")" + " where " + DB_CZ + " = ? and " + DB_DATETIME +  " = ? ",new String[]{zc,today});
				}
				cursor.close();
			}
		}
		db.close();
	}

	public void deleteOld() {
		HLog.i("----------->>>deleteOld");
		db = helper.getWritableDatabase();
		db.delete(TABLE_NAME, DB_DATETIME + " != ? ", new String[] { today });
		db.close();
	}
}
