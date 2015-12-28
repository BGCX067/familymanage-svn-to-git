package com.haolianluo.sms2.model;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.haolianluo.sms2.data.HConst;
import com.lianluo.core.cache.DataParser;

public class HUserUpdatePasswdParser extends DataParser {

	private Context mContext;
	private HResponseModel model;
	private String tagName;
	private String s3;
	private String s4;
	private String mPhoneNum;
	private String mOldPassword;
	private String mNewPassword;

	public HUserUpdatePasswdParser(Context context, String phoneNum, String oldPassword, String newPassword) {
		super(context);
		this.mContext = context;
		this.mPhoneNum = phoneNum;
		this.mOldPassword = oldPassword;
		this.mNewPassword = newPassword;
	}

	public HResponseModel getUserUpdatePasswd() {
		HResponseModel result = (HResponseModel) getNet(null, "text/xml; charset=UTF-8");
		return result;
	}

	@Override
	public String getUrl() {
		return HConst.USER_REGISTER_HOST_URL;
	}

	@Override
	public String getBody() {
		return HConst.USER_UPDATEPASSWD(mContext, mPhoneNum, mOldPassword, mNewPassword);
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
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		if("OK".equals(s3)) {
			model.setResponse(true);
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
		model = new HResponseModel();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		tagName = localName;
	}

}
