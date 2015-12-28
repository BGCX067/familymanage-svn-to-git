package com.haolianluo.sms2.model;

import java.io.Serializable;

public class HResUpdateModel implements Serializable {

	private static final long serialVersionUID = -3594823607207209582L;
	private String i;// 反馈接受请求
	private String r;// 模版情况反馈 下划线前为模版ID，下划线后为是会否需要更新状态，如果需要更新则为1，不需要则为0。
	private String p;// 请求资源地址前缀
	private String v;// 需要更新的模版模版标签
	private String mi;// 模版ID
	private String ma;// 模块唯一码
	private String pn;// 模版名称
	private String in;// 缩略图地址
	private String et;// 推荐级别
	private String md;// 下载次数
	private String mv;// 模版当前版本
	private String pu;// 模版地址
	private String fs;// 模版大小
	private String pl;// 资费状态 0 为免费 1为收费 -1为已收费
	private String pr;// 资费金额单位为分 免费时无此标签

	public String getI() {
		return i;
	}

	public void setI(String i) {
		this.i = i;
	}

	public String getR() {
		return r;
	}

	public void setR(String r) {
		this.r = r;
	}

	public String getP() {
		return p;
	}

	public void setP(String p) {
		this.p = p;
	}

	public String getV() {
		return v;
	}

	public void setV(String v) {
		this.v = v;
	}

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

	public String getIn() {
		return in;
	}

	public void setIn(String in) {
		this.in = in;
	}

	public String getEt() {
		return et;
	}

	public void setEt(String et) {
		this.et = et;
	}

	public String getMd() {
		return md;
	}

	public void setMd(String md) {
		this.md = md;
	}

	public String getMv() {
		return mv;
	}

	public void setMv(String mv) {
		this.mv = mv;
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

	public String getPl() {
		return pl;
	}

	public void setPl(String pl) {
		this.pl = pl;
	}

	public String getPr() {
		return pr;
	}

	public void setPr(String pr) {
		this.pr = pr;
	}

}
