package com.haolianluo.sms2.model;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;

import com.haolianluo.sms2.data.HConst;
import com.lianluo.core.cache.DataParser;
import com.lianluo.core.util.HLog;

public class HFirstInstallParser extends DataParser {
	private static final String TAG = "HFirstInstallParser";

	private Context mContext;

	public HFirstInstallParser(Context context) {
		super(context);
		this.mContext = context;
	}

	public Object getFirstInstall() {
		Object result = getNet(null, "text/xml; charset=UTF-8");
		return result;
	}

	@Override
	public String getUrl() {
		return HConst.RESLIB_HOST_URL;
	}

	@Override
	public String getBody() {
		return HConst.FIRST_INSTALL(mContext);
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
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
		if(localName.equals("re"))
		{
			HLog.e(TAG, "attr:" + attributes.getValue(0));
		}
	}

}
