package com.haolianluo.sms2.mms.model;

public class HResponseModel {

	private boolean response;// 结果
	private String message;// 提示信息

	public boolean isResponse() {
		return response;
	}

	public void setResponse(boolean response) {
		this.response = response;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
