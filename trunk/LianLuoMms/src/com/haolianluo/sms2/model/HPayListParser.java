package com.haolianluo.sms2.model;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.haolianluo.sms2.data.HConst;
import com.lianluo.core.cache.DataParser;
import com.lianluo.core.event.IEvent;
import com.lianluo.core.task.BaseTask;
import com.lianluo.core.task.TaskManagerFactory;

public class HPayListParser extends DataParser {

	private Context mContext;
	private HPayListModel mPayList;
	private String tagName;
	private String mT2 = " ";

	public HPayListParser(Context context) {
		super(context);
		this.mContext = context;
	}

	public HPayListModel getPayList(final String key, final Handler handler) {
		HPayListModel result = (HPayListModel) getCache(key);
		if (result == null) {
			result = (HPayListModel) getNet(key);
		} else {
			mT2 = result.getT2();
			TaskManagerFactory.createParserTaskManager().addTask(
					new BaseTask(null) {

						@Override
						public void doTask(IEvent event) throws Exception {
							Object obj = getNet(key);
							if (obj != null) {
								Message msg = new Message();
								msg.obj = obj;
								handler.sendMessage(msg);
							}
						}
					});
		}
		return result;
	}

	@Override
	public String getUrl() {
		return HConst.RESLIB_HOST_URL;
	}

	@Override
	public String getBody() {
		return HConst.RESLIB_PAYLIST(mContext, mT2);
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if("i".equals(tagName)) {
			String i = new String(ch, start, length);
			mPayList.setI(i);
		}
		if ("t2".equals(tagName)) {
			String t2 = new String(ch, start, length);
			mPayList.setT2(t2);
		}
		if ("pm".equals(tagName)) {
			String pm = new String(ch, start, length);
			mPayList.setPm(pm);
		}
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		super.obj = mPayList;
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		mPayList = new HPayListModel();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		tagName = localName;
	}

}
