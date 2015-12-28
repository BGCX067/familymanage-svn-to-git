package com.haolianluo.sms2.model;

public class HUserModel {

	private boolean response;// 是否正确
	private String message;// 错误日志
	
	private String idn;// 用户介绍
	private String sex;// 用户性别
	private String pca;// 号码归属地
	private String mn;// 联络 id
	private String us;// 0-上传通讯录 1-通讯录已上传
	private String fs;// 0 标识首次登录 1 非首次登录
	private String py;// 0 允许上传通讯录 1 不允许上传通讯录

	public String getIdn() {
		return idn;
	}

	public void setIdn(String idn) {
		this.idn = idn;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPca() {
		return pca;
	}

	public void setPca(String pca) {
		this.pca = pca;
	}

	public String getMn() {
		return mn;
	}

	public void setMn(String mn) {
		this.mn = mn;
	}

	public String getUs() {
		return us;
	}

	public void setUs(String us) {
		this.us = us;
	}

	public String getFs() {
		return fs;
	}

	public void setFs(String fs) {
		this.fs = fs;
	}

	public String getPy() {
		return py;
	}

	public void setPy(String py) {
		this.py = py;
	}

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
