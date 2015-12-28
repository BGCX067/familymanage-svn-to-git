package com.lianluo.core.util;

import java.io.ByteArrayInputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.haolianluo.sms2.model.HMoldAdr;
import com.haolianluo.sms2.model.HMoldPay;


public class ParseUtil {

//	<?xml version=1.0 encoding=UTF-8 ?>
//	<b>
//	<c1>http://wapmail.10086.cn:9090/wp/w2/setsendacnt.htm?rnum=124</c1>   --请求手机号地址
//	<c2>http://211.143.108.6/wap/NewPayServices</c2>              --请求计费地址
//	</b>
	public static HMoldAdr parseAdrXml(String xml) {
		HMoldAdr ha =  new HMoldAdr();;
		try {
			XmlPullParser pullParser = Xml.newPullParser();
			pullParser.setInput(new ByteArrayInputStream(xml.getBytes()), "UTF-8");
			int eventType = pullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				 
				switch (eventType) {
				case XmlPullParser.START_TAG:
					String nodeName = pullParser.getName();
					if (nodeName.equals("c1")) {
						ha.setAdrC1(pullParser.nextText());
					}
					if (nodeName.equals("c2")) {
						ha.setAdrC2(pullParser.nextText());
					}
				}
				eventType = pullParser.next();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return ha;
	}
	public static boolean parseIsPayXml(String xml) {
		boolean flag = false;
		try {
			XmlPullParser pullParser = Xml.newPullParser();
			pullParser.setInput(new ByteArrayInputStream(xml.getBytes()), "UTF-8");
			int eventType = pullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				 
				switch (eventType) {
				case XmlPullParser.START_TAG:
					String nodeName = pullParser.getName();
					if (nodeName.equals("re")) {
						String result = pullParser.getAttributeValue(null, "b");
						if(result != null && "NoPaid".equals(result)) {
							flag = true;
						}
					}
				}
				eventType = pullParser.next();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return flag;
	}
//	<?xml version=1.0 encoding=UTF-8 ?>
//	<b>
//	<f1>1</f1>　--1为短信，２为百宝箱　３为ｗａｐ
//	<ad>1066666620</ad>
//	<ct>800083<ct>  
//	<rc>感谢您使用搜狐公司的娱乐互动业务，信息费1元/条(不含通讯费),确认点播请按“确认”发送短信开始享受服务，返回不扣费。客服电话：4008816666</rc>
//	<st>2</st>   --发送次数
//	<is>1</is>   --1为显示0为不显示 
//	<ib>0</ib>   --1为拦截下发 0为不拦截
//	</b>
	public static HMoldPay parseMoldPayXml(String xml) {
		HMoldPay hp = new HMoldPay();
		try {
			XmlPullParser pullParser = Xml.newPullParser();
			pullParser.setInput(new ByteArrayInputStream(xml.getBytes()), "UTF-8");
			int eventType = pullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				
				switch (eventType) {
				case XmlPullParser.START_TAG:
					String nodeName = pullParser.getName();
					if (nodeName.equals("f1")) {
						hp.setPayF1(pullParser.nextText());
					}else if(nodeName.equals("ad")){
						hp.setPayAd(pullParser.nextText());
					}else if(nodeName.equals("ct")){
						hp.setPayCt(pullParser.nextText());
					}else if(nodeName.equals("rc")){
						hp.setPayRc(HChanger.convertJiemi(pullParser.nextText(), ""));
					}else if(nodeName.equals("st")){
						hp.setPaySt(pullParser.nextText());
					}else if(nodeName.equals("is")){
						hp.setPayIs(pullParser.nextText());
					}else if(nodeName.equals("ib")){
						hp.setPayIb(pullParser.nextText());
					}
				}
				eventType = pullParser.next();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return hp;
	}
	
//	<b>
//	<re>SUCCESS</re>
//	</b>
	public static boolean parseMoldSaveXml(String xml) {
		boolean flag = false;
		try {
//			XmlPullParser pullParser = Xml.newPullParser();
//			pullParser.setInput(new ByteArrayInputStream(xml.getBytes()), "UTF-8");
//			int eventType = pullParser.getEventType();
//			while (eventType != XmlPullParser.END_DOCUMENT) {
//				
//				switch (eventType) {
//				case XmlPullParser.START_TAG:
//					String nodeName = pullParser.getName();
//					if (nodeName.equals("re") || nodeName.equals("R")) {
//						String re = pullParser.nextText();
//						if("SUCCESS".equals(re)) {
//							flag = true;
//						}
//					}
//				}
//				eventType = pullParser.next();
//			}
			if(xml != null && !"".equals(xml)) {
				return xml.contains("SUCCESS");
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return flag;
	}
	
	
}
