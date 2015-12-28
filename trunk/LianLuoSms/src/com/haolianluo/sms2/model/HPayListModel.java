package com.haolianluo.sms2.model;

import java.io.Serializable;

public class HPayListModel implements Serializable {

	private static final long serialVersionUID = -6273468303918414247L;
	private String i;// 反馈接受请求
	private String t2;// 模板最近付费时间
	private String pm;// 用户所有付费模板

	public String getI() {
		return i;
	}

	public void setI(String i) {
		this.i = i;
	}

	public String getT2() {
		return t2;
	}

	public void setT2(String t2) {
		this.t2 = t2;
	}

	public String getPm() {
		return pm;
	}

	public void setPm(String pm) {
		this.pm = pm;
	}

}
