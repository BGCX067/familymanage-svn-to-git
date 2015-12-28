package com.haolianluo.sms2.model;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.haolianluo.sms2.data.HConst;
import com.lianluo.core.cache.DataParser;

public class HTryUseParser extends DataParser {

	private Context mContext;
	private String tagName;
	private HTryUseModel mTryUse;
	private String i;
	private boolean past;

	public HTryUseParser(Context context) {
		super(context);
		this.mContext = context;
	}

	public HTryUseModel tryuse() {
		Object obj = getNet(null);
		return (HTryUseModel) obj;
	}

	@Override
	public String getUrl() {
		return HConst.RESLIB_HOST_URL;
	}

	@Override
	public String getBody() {
		return HConst.TRY_USE(mContext);
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if ("i".equals(tagName)) {
			i = new String(ch, start, length);
			mTryUse.setI(i);
		}
		if ("v".equals(tagName)) {
			mTryUse.setPast(past);
		}
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		super.obj = mTryUse;
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		mTryUse = new HTryUseModel();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		tagName = localName;
		if ("v".equals(localName)) {
			try {
				if("1".equals(attributes.getValue("pl"))) {
					past = true;
				} else {
					past = false;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
