package com.haolianluo.sms2.model;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.haolianluo.sms2.data.HConst;
import com.lianluo.core.cache.DataParser;
import com.lianluo.core.util.HChanger;

public class HResLibInfoParser extends DataParser {

	private Context mContext;
	private HResLibModel mResLib, tj;
	private ArrayList<HResLibModel> tj_list;
	private String tagName;
	private String i;
	private String p;
	private String rc;
	private String mId;

	public HResLibInfoParser(Context context, String id) {
		super(context);
		this.mContext = context;
		this.mId = id;
	}

//	public HResLibModel getResLibInfo(final String key, final Handler handler) {
//		HResLibModel result = (HResLibModel) getCache(key);
//		if (result == null) {
//			result = (HResLibModel) getNet(key);
//		} else {
//			TaskManagerFactory.createParserTaskManager().addTask(
//					new BaseTask(null) {
//
//						@Override
//						public void doTask(IEvent event) throws Exception {
//							Object obj = getNet(key);
//							if (obj != null) {
//								Message msg = new Message();
//								msg.obj = obj;
//								handler.sendMessage(msg);
//							}
//						}
//					});
//		}
	public HResLibModel getResLibInfo() {
		HResLibModel result = (HResLibModel) getNet(null);
		return result;
	}

	@Override
	public String getUrl() {
		return HConst.RESLIB_HOST_URL;
	}

	@Override
	public String getBody() {
		return HConst.RESLIB_INFO(mContext, mId);
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if("i".equals(tagName)) {
			i = new String(ch, start, length);
		}
		if ("p".equals(tagName)) {
			p = new String(ch, start, length);
		}
		if("rc".equals(tagName)) {
			rc = new String(ch, start, length);
		}
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		mResLib.setTj(tj_list);
		super.obj = mResLib;
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		if ("tj".equals(localName)) {
			tj_list.add(tj);
		}
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		tj_list = new ArrayList<HResLibModel>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		tagName = localName;
		if ("v".equals(localName)) {
			mResLib = null;
			try {
				mResLib = new HResLibModel();
				mResLib.setI(i);
				mResLib.setP(p);
				mResLib.setRc(HChanger.convertJiemi(rc, ""));
				mResLib.setMi(attributes.getValue("mi"));
				mResLib.setMa(attributes.getValue("ma"));
				mResLib.setPn(attributes.getValue("pn"));
				mResLib.setPkn(attributes.getValue("pkn"));
				mResLib.setIo(attributes.getValue("io"));
				mResLib.setIn(attributes.getValue("in"));
				mResLib.setIn2(attributes.getValue("in2"));
				mResLib.setEt(attributes.getValue("et"));
				mResLib.setMd(attributes.getValue("md"));
				mResLib.setMv(attributes.getValue("mv"));
				mResLib.setPu(attributes.getValue("pu"));
				mResLib.setFs(attributes.getValue("fs"));
				mResLib.setMt(attributes.getValue("mt"));
				mResLib.setPl(attributes.getValue("pl"));
				mResLib.setPr(attributes.getValue("pr"));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		if ("tj".equals(localName)) {
			tj = null;
			try {
				tj = new HResLibModel();
				tj.setMi(attributes.getValue("mi"));
				tj.setMa(attributes.getValue("ma"));
				tj.setPn(attributes.getValue("pn"));
				tj.setIn(attributes.getValue("in"));
				tj.setFs(attributes.getValue("fs"));
				tj.setEt(attributes.getValue("et"));
				tj.setPl(attributes.getValue("pl"));
				tj.setPr(attributes.getValue("pr"));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
