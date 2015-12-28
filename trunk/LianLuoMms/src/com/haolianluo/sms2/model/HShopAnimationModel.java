package com.haolianluo.sms2.model;


public class HShopAnimationModel extends HShopModel {

	private static final long serialVersionUID = -7979121540536181504L;
	
	private String rid;// 提醒模板ID
	private String rme;// 提醒模板名字
	private String rimg;// 提醒缩略图路径
	private String rth;//提醒预览flash路径(图片)
	private String rpu;// 提醒模版地址
	private String rfs;// 提醒模版大小
	private String rgs;// 提醒模板缩略图大小
	private String rps;// 提醒模板预览图大小
	private String pay;// 付费标签
	private String p_rc;

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getRme() {
		return rme;
	}

	public void setRme(String rme) {
		this.rme = rme;
	}

	public String getRimg() {
		return rimg;
	}

	public void setRimg(String rimg) {
		this.rimg = rimg;
	}

	public String getRpu() {
		return rpu;
	}

	public void setRpu(String rpu) {
		this.rpu = rpu;
	}

	public String getRfs() {
		return rfs;
	}

	public void setRfs(String rfs) {
		this.rfs = rfs;
	}

	public String getRgs() {
		return rgs;
	}

	public void setRgs(String rgs) {
		this.rgs = rgs;
	}

	public String getPay() {
		return pay;
	}

	public void setPay(String pay) {
		this.pay = pay;
	}

	public String getRth() {
		return rth;
	}

	public void setRth(String rth) {
		this.rth = rth;
	}

	public String getRps() {
		return rps;
	}

	public void setRps(String rps) {
		this.rps = rps;
	}

	public String getP_rc() {
		return p_rc;
	}

	public void setP_rc(String p_rc) {
		this.p_rc = p_rc;
	}

}
