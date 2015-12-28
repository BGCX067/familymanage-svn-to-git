package com.haolianluo.sms2.model;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.haolianluo.sms2.data.HConst;
import com.lianluo.core.cache.DataParser;
import com.lianluo.core.util.Base64Coder;

public class HUserLoginParser extends DataParser {

	private Context mContext;
	private HUserModel model;
	private String tagName;
	private String s3;
	private String s4;
	private String idn;// 用户介绍
	private String sex;// 用户性别
	private String pca;// 号码归属地
	private String mn;// 联络 id
	private String us;// 0-上传通讯录 1-通讯录已上传
	private String fs;// 0 标识首次登录 1 非首次登录
	private String py;// 0 允许上传通讯录 1 不允许上传通讯录
	private String mPhoneNum;
	private String mPassword;

	public HUserLoginParser(Context context, String phoneNum, String password) {
		super(context);
		this.mContext = context;
		this.mPhoneNum = phoneNum;
		this.mPassword = password;
	}

	public HUserModel getUserLogin() {
		HUserModel result = (HUserModel) getNet(null, "text/xml; charset=UTF-8");
		return result;
	}

	@Override
	public String getUrl() {
		return HConst.USER_REGISTER_HOST_URL;
	}

	@Override
	public String getBody() {
		return HConst.USER_LOGIN(mContext, mPhoneNum, mPassword);
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if("s3".equals(tagName)) {
			s3 = new String(ch, start, length);
		}
		if("s4".equals(tagName)) {
			s4 = new String(ch, start, length);
		}
		if("idn".equals(tagName)) {
			idn = new String(ch, start, length);
			if(idn != null && !"".equals(idn)) {
				idn = Base64Coder.decodeString(idn);
			}
		}
		if("sex".equals(tagName)) {
			sex = new String(ch, start, length);
		}
		if("pca".equals(tagName)) {
			pca = new String(ch, start, length);
		}
		if("mn".equals(tagName)) {
			mn = new String(ch, start, length);
		}
		if("us".equals(tagName)) {
			us = new String(ch, start, length);
		}
		if("fs".equals(tagName)) {
			fs = new String(ch, start, length);
		}
		if("py".equals(tagName)) {
			py = new String(ch, start, length);
		}
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		if("OK".equals(s3)) {
			model.setResponse(true);
			model.setIdn(idn);
			model.setSex(sex);
			model.setPca(pca);
			model.setMn(mn);
			model.setUs(us);
			model.setFs(fs);
			model.setPy(py);
		} else {
			model.setResponse(false);
			model.setMessage(s4);
		}
		
		super.obj = model;
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		model = new HUserModel();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		tagName = localName;
	}

}
