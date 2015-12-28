package com.haolianluo.sms2.model;

import android.content.Context;

import com.lianluo.core.util.Base64Coder;
import com.lianluo.core.util.ToolsUtil;

public class MsgProtocol {
	static final String Base = 
		"<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
	    "<MQMessage ver=\"1.00\">" +
			"<body cmd=\"%s\" UserName=\"%s\" DeviceID=\"%s\" OSType=\"5\" OSVer =\"%s\">%s</body>" +
		"</MQMessage>";

	static final String Logout = "<logout>0</logout>";
	
	static final Character SEPARATOR = ',';
	
	static String biudLogin(Context context, String phoneNum){
		return packet(String.format(Base, "1001", phoneNum, ToolsUtil.getImsi(context), "2", ""));
	}
	
	static String biudLogout(Context context, String phoneNum){
		return packet(String.format(Base, "1001", phoneNum, ToolsUtil.getImsi(context), "2", Logout));
	}
	
	static String biudMessag(Context context, String phoneNum, String contacts, String messag){
		String[] c = contacts.split(SEPARATOR.toString());
		StringBuffer str = new StringBuffer();
		str.append(String.format("<msgData>%s</msgData>", Base64Coder.encodeString(messag)));
		str.append("<receiver_list>");
		for(int i = 0; i < c.length; i++){
			str.append(String.format("<receiver>%s</receiver>", c[i]));
		}
		str.append("</receiver_list>");
		return packet(String.format(Base, "1005", phoneNum, ToolsUtil.getImsi(context), "2", str.toString()));
	}
	
	static String packet(String data) {
		StringBuffer result = new StringBuffer();
		result.append("Content-Type:xml\r\n");
		result.append("Content-Length:" + data.length() + "\r\n\r\n");
		result.append(data);
		return result.toString();
	}
}
