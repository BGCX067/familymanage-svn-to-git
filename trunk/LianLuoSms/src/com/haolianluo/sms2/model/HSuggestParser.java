package com.haolianluo.sms2.model;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.haolianluo.sms2.data.HConst;
import com.lianluo.core.cache.DataParser;
import com.lianluo.core.util.HChanger;

public class HSuggestParser extends DataParser {

	private String re;
	private Context mContext;
	private String tagName;
	private String mContent;

	public HSuggestParser(Context context) {
		super(context);
		this.mContext = context;
	}

	public boolean suggest(String content) {
		this.mContent = HChanger.ConvertJiaMi(content);
		Object obj = getNet(null);
		return (Boolean) obj;
	}

	@Override
	public String getUrl() {
		return HConst.SUGGEST_HOST_URL;
	}

	@Override
	public String getBody() {
		return HConst.suggest_request_xml(mContext, mContent);
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if ("re".equals(tagName)) {
			re = new String(ch, start, length);
		}
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		boolean result = false;
		if(re != null && "OK".equalsIgnoreCase(re)) {
			result = true;
		}
		super.obj = result;
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		tagName = localName;
	}

}
