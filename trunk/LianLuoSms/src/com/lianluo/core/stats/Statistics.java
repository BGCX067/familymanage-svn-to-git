package com.lianluo.core.stats;

import android.content.Context;
import android.util.Log;

import com.haolianluo.sms2.model.HStatistics;
import com.lianluo.core.net.ParserHttp;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;

public class Statistics
{
	public static final int RecodeOK = 0;
	public static final int RecodeFailture = 1;
	public static final int NOThisRequest = 2;
	public static final int Paid = 3;
	public static final int NoPaid = 4;
	public static final int Free = 5;
	public static final int Failed = 6;
	public static final String STATS_SERVER = "http://ws.lianluo.com/sms2/TjService";
	public static int sendStats(Context context)
	{
		HLog.i("---------------------------------------------");
		StringBuffer body = new StringBuffer();
		body.append("<?xml version='1.0' encoding='UTF-8' ?>");
		body.append("<c>");
		body.append("<srqh>");
		body.append("<r1>5</r1>");     //平台类型:
		body.append("<r2>" + ToolsUtil.getSDKLevel(context) + "</r2>");     //客户端平台版本类型
		body.append("<c2>" + ToolsUtil.getPhoneType() + "</c2>");       //机型
		body.append("<e1>" + ToolsUtil.getPhoneNum(context) + "</e1>");       //手机号
		body.append("<a1>" + ToolsUtil.getImsi(context) + "</a1>"); 
		body.append("<b1>" + ToolsUtil.getImei(context) + "</b1>");
		body.append("<v1>" + ToolsUtil.getVersion(context) + "</v1>");  //客户端版本号
		body.append("<p1>9</p1>");        //产品ID.
		body.append("<u1>" + ToolsUtil.getChannelNum(context) + "</u1>");   //渠道号
		body.append("<k1> </k1>");   //预留字段
		body.append("<t1> </t1>");  //预留字段
		body.append("<ac> </ac>"); //预留字段
		body.append("</srqh>");
		body.append("<k>report</k>");
		body.append("<y1>" + ToolsUtil.getVersion(context) + "</y1>");
		body.append("<r>");
		HStatistics hss = new HStatistics(context);
		String[] s = hss.getDateTimeList();
		body.append("<t>" + hss.getDescribe(HStatistics.T, null) + "</t>");
		//body.append(hss.getLogData());
		for (String ss : s) {
			body.append("<z>");
			body.append(hss.getDescribe(HStatistics.Z0, ss));
			body.append("|");
			body.append(hss.getDescribe(HStatistics.Z1, ss));
			body.append("|");
			body.append(hss.getDescribe(HStatistics.Z2, ss));
			body.append("|");
			body.append(hss.getDescribe(HStatistics.Z3, ss));
			body.append("|");
			body.append(hss.getDescribe(HStatistics.Z4, ss));
			body.append("|");
			body.append(hss.getDescribe(HStatistics.Z5, ss));
			body.append("|");
			body.append(hss.getDescribe(HStatistics.Z6, ss));
			body.append("|");
			body.append(hss.getDescribe(HStatistics.Z7, ss));
			body.append("|");
			body.append(hss.getDescribe(HStatistics.Z8, ss));
//			body.append("|");
//			body.append(hss.getDescribe(HStatistics.Z9, ss));
			body.append("|");
			body.append(hss.getDescribe(HStatistics.Z10, ss));
			body.append("|");
			body.append(hss.getDescribe(HStatistics.Z11, ss));
			body.append("|");
			body.append(hss.getDescribe(HStatistics.Z12, ss));
			body.append("|");
//			body.append(hss.getDescribe(HStatistics.Z13, ss));
//			body.append("|");
//			body.append(hss.getDescribe(HStatistics.Z14, ss));
//			body.append("|");
			body.append("</z>");
		}
		body.append("</r>");
		body.append("</c>");
		HLog.i("uploadData-------","统计====" + body.toString());
		StatsParser handler = new StatsParser();
		ParserHttp http = new ParserHttp(context, handler);
		http.setUrl(STATS_SERVER);
		http.connect(body.toString());
		return handler.getStatsResult();
	}
	
	public static final String CHARGE_SERVER = "http://ws.lianluo.com/sms2/SmsModuleService";
	public static int sendCharge(Context context, String id)
	{
		StringBuffer body = new StringBuffer();
		body.append("<?xml version='1.0' encoding='UTF-8' ?>");
		body.append("<c>");
		body.append("<srqh>");
		body.append("<r1>5</r1>");     
		body.append("<r2>" + ToolsUtil.getSDKLevel(context) + "</r2>");     
		body.append("<c2>" + ToolsUtil.getPhoneType() + "</c2>"); 
		body.append("<e1>" + ToolsUtil.getPhoneNum(context) + "</e1>"); 
		body.append("<a1>" + ToolsUtil.getImsi(context) + "</a1>"); 
		body.append("<b1>" + ToolsUtil.getImei(context) + "</b1>");
		body.append("<q1>" + ToolsUtil.getChannelNum(context) + "</q1>");
		body.append("<v1>" + ToolsUtil.getVersion(context) + "</v1>");
		body.append("</srqh>");
		body.append("<k>checkpay</k>"); 
		body.append("<id>" + id + "</id>"); 
		body.append("</c>");
		
		
		StatsParser handler = new StatsParser();
		ParserHttp http = new ParserHttp(context, handler);
		http.setUrl(CHARGE_SERVER);
		http.connect(body.toString());
		return handler.getStatsResult();
	}
	
	public static final String INSTALL_SERVER = "http://ws.lianluo.com/sms2/SmsModuleService";
	public static int sendInstall(Context context)
	{
		StringBuffer body = new StringBuffer();
		body.append("<?xml version='1.0' encoding='UTF-8' ?>");
		body.append("<c>");
		body.append("<srqh>");
		body.append("<r1>5</r1>");     
		body.append("<r2>" + ToolsUtil.getSDKLevel(context) + "</r2>");     
		body.append("<c2>" + ToolsUtil.getPhoneType() + "</c2>");       
		body.append("<a1>" + ToolsUtil.getImsi(context) + "</a1>"); 
		body.append("<b1>" + ToolsUtil.getImei(context) + "</b1>");
		body.append("<q1>" + ToolsUtil.getChannelNum(context) + "</q1>");
		body.append("<v1>" + ToolsUtil.getVersion(context) + "</v1>");
		body.append("</srqh>");
		body.append("<k>" + "setup" + "</k>");
		body.append("</c>");
		
		StatsParser handler = new StatsParser();
		ParserHttp http = new ParserHttp(context, handler);
		http.setUrl(INSTALL_SERVER);
		http.connect(body.toString());
		return handler.getStatsResult();
	}
}