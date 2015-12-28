package com.haolianluo.sms2.model;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.haolianluo.sms2.data.HConst;
import com.lianluo.core.cache.DataParser;

public class HNewListParser extends DataParser {

	private Context mContext;
	private String tagName;
	private HNewListModel mNewList;
	private String i;
	private String t;
	private String s;

	public HNewListParser(Context context) {
		super(context);
		this.mContext = context;
	}

	public HNewListModel newlist() {
		Object obj = getNet(null);
		return (HNewListModel) obj;
	}

	@Override
	public String getUrl() {
		return HConst.RESLIB_HOST_URL;
	}

	@Override
	public String getBody() {
		return HConst.NEW_LIST(mContext);
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if ("i".equals(tagName)) {
			i = new String(ch, start, length);
			mNewList.setI(i);
		}
		if ("t".equals(tagName)) {
			t = new String(ch, start, length);
			mNewList.setT(t);
		}
		if ("s".equals(tagName)) {
			s = new String(ch, start, length);
			mNewList.setS(s);
		}
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		super.obj = mNewList;
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		mNewList = new HNewListModel();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		tagName = localName;
	}

}
