package com.haolianluo.sms2.model;


public class HShopFontModel extends HShopModel {

	private static final long serialVersionUID = -8013851725895906498L;
	
	private String id;// 字库ID
	private String n1;// 字库名称
	private String de;// 字库描述
	private String ic;// 字库图片
	private String fs;// 字库大小
	private String fp;// 字库文件名
	private String pay;// 付费标签

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getN1() {
		return n1;
	}

	public void setN1(String n1) {
		this.n1 = n1;
	}

	public String getDe() {
		return de;
	}

	public void setDe(String de) {
		this.de = de;
	}

	public String getIc() {
		return ic;
	}

	public void setIc(String ic) {
		this.ic = ic;
	}

	public String getFs() {
		return fs;
	}

	public void setFs(String fs) {
		this.fs = fs;
	}

	public String getFp() {
		return fp;
	}

	public void setFp(String fp) {
		this.fp = fp;
	}

	public String getPay() {
		return pay;
	}

	public void setPay(String pay) {
		this.pay = pay;
	}

}
