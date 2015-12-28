package com.haolianluo.sms2.model;

public class HMoldPay {
//	<?xml version=1.0 encoding=UTF-8 ?>
//	<b>
//	<f1>1</f1>　--1为短信，２为百宝箱　３为ｗａｐ
//	<ad>1066666620</ad>
//	<ct>800083<ct>  
//	<rc>感谢您使用搜狐公司的娱乐互动业务，信息费1元/条(不含通讯费),确认点播请按“确认”发送短信开始享受服务，返回不扣费。客服电话：4008816666</rc>
//	<st>2</st>   --发送次数
//	<is>1</is>   --1为显示0为不显示 
//	<ib>0</ib>   --1为拦截下发 0为不拦截
//	</b>
	
	private String adrC2 = "";
	
	private String payF1 = "";
	private String payAd = "";
	private String payCt = "";
	private String payRc = "";
	private String payIs = "";
	private String paySt = "";
	private String payIb = "";
	private String payResId = "";
	public void setPayF1(String payF1) {
		this.payF1 = payF1;
	}
	public String getPayF1() {
		return payF1;
	}
	public void setPayAd(String payAd) {
		this.payAd = payAd;
	}
	public String getPayAd() {
		return payAd;
	}
	public void setPayCt(String payCt) {
		this.payCt = payCt;
	}
	public String getPayCt() {
		return payCt;
	}
	public void setPayRc(String payRc) {
		this.payRc = payRc;
	}
	public String getPayRc() {
		return payRc;
	}
	public void setPayIs(String payIS) {
		this.payIs = payIS;
	}
	public String getPayIs() {
		return payIs;
	}
	public void setPayIb(String payIb) {
		this.payIb = payIb;
	}
	public String getPayIb() {
		return payIb;
	}
	public void setPaySt(String paySt) {
		this.paySt = paySt;
	}
	public String getPaySt() {
		return paySt;
	}
	public void setPayResId(String payResId) {
		this.payResId = payResId;
	}
	public String getPayResId() {
		return payResId;
	}
	public String getAdrC2() {
		return adrC2;
	}
	public void setAdrC2(String adrC2) {
		this.adrC2 = adrC2;
	}
	
	
	
}
