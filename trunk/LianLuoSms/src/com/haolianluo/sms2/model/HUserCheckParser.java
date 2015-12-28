package com.haolianluo.sms2.model;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.haolianluo.sms2.data.HConst;
import com.lianluo.core.cache.DataParser;

public class HUserCheckParser extends DataParser {

	private Context mContext;
	private HResponseModel model;
	private String tagName;
	private String s4;
	private String mPhoneNum;

	public HUserCheckParser(Context context, String phoneNum) {
		super(context);
		this.mContext = context;
		this.mPhoneNum = phoneNum;
	}

	public HResponseModel getUserCheck() {
		HResponseModel result = (HResponseModel) getNet(null);
		return result;
	}

	@Override
	public String getUrl() {
		return HConst.USER_CKECK_HOST_URL;
	}

	@Override
	public String getBody() {
		return HConst.USER_CHECK(mContext, mPhoneNum);
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if("s4".equals(tagName)) {
			s4 = new String(ch, start, length);
		}
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		if("OK".equals(s4)) {
			model.setResponse(true);
		} else {
			model.setResponse(false);
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
