package com.haolianluo.sms2.model;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class IMDataParser extends DefaultHandler {
	
	private ImModel model;
	private String tagName;
	
	private String cmd;
	private String msgData;
	private ArrayList<String> receiverList;
	private String sender;
	private String sendDate;
	private String status;
	private String msg;
	
	public ImModel getImModel() {
		return model;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		String data = new String(ch, start, length).trim();
		if(data == null || "".equals(data)) {
			return;
		}
		
		if("1003".equals(cmd)) {
			//发送失败,请转发
			if("msgData".equals(tagName)) {
				msgData = new String(ch, start, length);
				receiverList = new ArrayList<String>();
			}
			if("receiver".equals(tagName)) {
				String receiver = new String(ch, start, length);
				receiverList.add(receiver);
			}
		} else if("1004".equals(cmd)) {
			//收到的消息
			if("sender".equals(tagName)) {
				sender = new String(ch, start, length);
			}
			if("msgData".equals(tagName)) {
				msgData = new String(ch, start, length);
			}
			if("sendDate".equals(tagName)) {
				sendDate = new String(ch, start, length);
			}
		} else if("8001".equals(cmd) || "8003".equals(cmd) || "8005".equals(cmd)) {
			//错误信息
			if("status".equals(tagName)) {
				status = new String(ch, start, length);
			}
			if("msg".equals(tagName)) {
				msg = new String(ch, start, length);
			}
		}
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		model.setCmd(cmd);
		model.setMsgData(msgData);
		model.setReceiverList(receiverList);
		model.setSender(sender);
		model.setSendDate(sendDate);
		model.setStatus(status);
		model.setMsg(msg);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		model = new ImModel();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		tagName = localName;
		if ("body".equals(localName)) {
			try {
				cmd = attributes.getValue("cmd");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
