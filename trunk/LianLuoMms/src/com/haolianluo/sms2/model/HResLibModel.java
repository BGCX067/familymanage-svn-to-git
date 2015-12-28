package com.haolianluo.sms2.model;

import java.io.Serializable;
import java.util.ArrayList;

public class HResLibModel implements Serializable {

	private static final long serialVersionUID = -8714864897409438618L;
	private String i;// 反馈接受请求
	private String p;// 请求资源地址前缀
	private String rc;// 付费提示信息 下载此模板需支付5元。确定下载？(此内容加密；对应请求k值为pylst:移动mm计费 单条)
	private String by;// 移动mm包月状态：有包月记录过期返回-1,未有包月记录返回-2，包月中返回0
	private String rc1;// 移动mm包月提示语 您可在10元包月成功后，30天内无限量下载模板库中任意模板，让您真正体验到模板随心情而换！确定要包月？(此内容加密；对应请求k值为pylst:移动mm计费 包月)
	private String rc2;// 移动mm包月到期提示语  您的包月期限将在2012-06-30后结束，是否要继续包月？(此标签内容加密，且只在用户包月开始后的第20天、25天、30天返回；对应请求k值为pylst:移动mm计费 包月到期)
	private String mi;// 模版ID
	private String ma;// 模块唯一码
	private String pn;// 模版名称
	private String pkn;// 包名
	private String io;// io模板详情
	private String in;// 缩略图地址
	private String in2;// 预览图地址
	private String et;// 推荐级别
	private String md;// 下载次数
	private String mv;// 模版当前版本
	private String pu;// 模版地址
	private String fs;// 模版大小
	private String mt;// 模板上线日期
	private String mn;//新加mn标签--1表示新模板；0表示没有更新
	private String pl;// -1为已付费 0为免费 1为收费
	private String pr;// 资费金额单位为分 免费时无此标签
	private ArrayList<HResLibModel> tj;// 推荐模版

	public String getI() {
		return i;
	}

	public void setI(String i) {
		this.i = i;
	}

	public String getP() {
		return p;
	}

	public void setP(String p) {
		this.p = p;
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

	public String getIn2() {
		return in2;
	}

	public void setIn2(String in2) {
		this.in2 = in2;
	}

	public String getMt() {
		return mt;
	}

	public void setMt(String mt) {
		this.mt = mt;
	}

	public ArrayList<HResLibModel> getTj() {
		return tj;
	}

	public void setTj(ArrayList<HResLibModel> tj) {
		this.tj = tj;
	}

	public String getIo() {
		return io;
	}

	public void setIo(String io) {
		this.io = io;
	}

	public String getRc() {
		return rc;
	}

	public void setRc(String rc) {
		this.rc = rc;
	}

	public String getPkn() {
		return pkn;
	}

	public void setPkn(String pkn) {
		this.pkn = pkn;
	}

	public String getBy() {
		return by;
	}

	public void setBy(String by) {
		this.by = by;
	}

	public String getRc1() {
		return rc1;
	}

	public void setRc1(String rc1) {
		this.rc1 = rc1;
	}

	public String getRc2() {
		return rc2;
	}

	public void setRc2(String rc2) {
		this.rc2 = rc2;
	}

	public String getMn() {
		return mn;
	}

	public void setMn(String mn) {
		this.mn = mn;
	}

}
