package com.haolianluo.sms2.model;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;

import com.haolianluo.sms2.data.HConst;
import com.lianluo.core.cache.DataParser;
import com.lianluo.core.util.HLog;

public class HUpdateParser extends DataParser {

	private Context mContext;
	private String tagName;
	private HUpdateModel mUpdate;
	private String p;

	public HUpdateParser(Context context) {
		super(context);
		this.mContext = context;
	}

	public HUpdateModel update() {
		Object obj = getNet(null);
		return (HUpdateModel) obj;
	}

	@Override
	public String getUrl() {
		return HConst.UPDATE_HOST_URL;
	}

	@Override
	public String getBody() {
		return HConst.update_request_xml(mContext);
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if ("p".equals(tagName)) {
			p = new String(ch, start, length);
		}
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		super.obj = mUpdate;
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		mUpdate = new HUpdateModel();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		tagName = localName;
		if ("v".equals(localName)) {
			try {
				mUpdate.setUpdate(true);
				mUpdate.setP(p);
				mUpdate.setN(attributes.getValue("n"));
				mUpdate.setVer(attributes.getValue("ver"));
				mUpdate.setDesc(attributes.getValue("desc"));
				mUpdate.setUrl(attributes.getValue("url"));
				mUpdate.setFs(attributes.getValue("fs"));
				mUpdate.setSt(attributes.getValue("st"));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if("re".equals(localName)) {
			try {
				mUpdate.setUpdate(false);
				mUpdate.setRe(attributes.getValue("b"));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if("bt".equals(localName)) {
			try {
				mUpdate.setBt(attributes.getValue("t"));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void onError(Exception ex) {
		HLog.d("HUpdateParserException", ex.toString());
	}

}
