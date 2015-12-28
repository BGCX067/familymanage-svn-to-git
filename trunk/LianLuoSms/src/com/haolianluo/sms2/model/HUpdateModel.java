package com.haolianluo.sms2.model;

public class HUpdateModel {

	private boolean isUpdate;// 是否需要更新(false时re有值，true时剩下的都有值)
	private String p;// 请求资源地址前缀
	private String n;// 产品名称
	private String ver;// 版本号
	private String desc;// 产品说明
	private String url;// 产品包路径
	private String fs;// 产品包大小
	private String st;// 是否强制更新 0为不强制 1为强制
	private String bt;// 几天更新一次
	private String re;// 返回原因

	public String getP() {
		return p;
	}

	public void setP(String p) {
		this.p = p;
	}

	public String getN() {
		return n;
	}

	public void setN(String n) {
		this.n = n;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFs() {
		return fs;
	}

	public void setFs(String fs) {
		this.fs = fs;
	}

	public String getBt() {
		return bt;
	}

	public void setBt(String bt) {
		this.bt = bt;
	}

	public String getRe() {
		return re;
	}

	public void setRe(String re) {
		this.re = re;
	}

	public boolean isUpdate() {
		return isUpdate;
	}

	public void setUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public String getSt() {
		return st;
	}

	public void setSt(String st) {
		this.st = st;
	}
}
