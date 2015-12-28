package com.haolianluo.sms2.model;

public class HShopSkinModel extends HShopModel {

	private static final long serialVersionUID = -7781910379868192140L;

	private String mi;// 模版ID
	private String ma;// 模块唯一码
	private String pn;// 模版名称
	private String io;// 模版简介
	private String et;// 推荐级别
	private String in;// 缩略图地址
	private String in2;// 模板预览地址
	private String pu;// 模版地址
	private String fs;// 模版大小
	private String pay;// 付费标签
	private String p_rc;//提示信息

	public String getMi() {
		return mi;
	}

	public void setMi(String mi) {
		this.mi = mi;
	}

	public String getMa() {
		return ma;
	}

	public void setMa(String ma) {
		this.ma = ma;
	}

	public String getPn() {
		return pn;
	}

	public void setPn(String pn) {
		this.pn = pn;
	}

	public String getIo() {
		return io;
	}

	public void setIo(String io) {
		this.io = io;
	}

	public String getEt() {
		return et;
	}

	public void setEt(String et) {
		this.et = et;
	}

	public String getIn() {
		return in;
	}

	public void setIn(String in) {
		this.in = in;
	}

	public String getIn2() {
		return in2;
	}

	public void setIn2(String in2) {
		this.in2 = in2;
	}

	public String getPu() {
		return pu;
	}

	public void setPu(String pu) {
		this.pu = pu;
	}

	public String getFs() {
		return fs;
	}

	public void setFs(String fs) {
		this.fs = fs;
	}

	public String getPay() {
		return pay;
	}

	public void setPay(String pay) {
		this.pay = pay;
	}

	public String getP_rc() {
		return p_rc;
	}

	public void setP_rc(String p_rc) {
		this.p_rc = p_rc;
	}

}
