package com.haolianluo.sms2.model;

import java.util.ArrayList;

public class ImModel {

	private String cmd;
	private String msgData;
	private ArrayList<String> receiverList;

	private String sender;
	private String sendDate;

	private String status;
	private String msg;

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getMsgData() {
		return msgData;
	}

	public void setMsgData(String msgData) {
		this.msgData = msgData;
	}

	public ArrayList<String> getReceiverList() {
		return receiverList;
	}

	public void setReceiverList(ArrayList<String> receiverList) {
		this.receiverList = receiverList;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getSendDate() {
		return sendDate;
	}

	public void setSendDate(String sendDate) {
		this.sendDate = sendDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
