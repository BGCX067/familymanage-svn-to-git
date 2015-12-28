package com.haolianluo.sms2.model;

import java.util.ArrayList;

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
import com.lianluo.core.util.ToolsUtil;

public class HShopFontParser extends DataParser {

	private Context mContext;
	private ArrayList<HShopFontModel> mList;
	private HShopFontModel font;
	private String tagName;
	private String p;
	private String t;
	private String mTimeStamp = " ";

	public HShopFontParser(Context context) {
		super(context);
		this.mContext = context;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<HShopFontModel> getFont(final String key, final Handler handler) {
		ArrayList<HShopFontModel> result = (ArrayList<HShopFontModel>) getCache(key);
		if (result == null) {
			result = (ArrayList<HShopFontModel>) getNet(key);
		} else {
			if(result.size() > 0) {
				mTimeStamp = result.get(0).getT();
			}
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
		return HConst.SERVER_FLST_URL;
	}

	@Override
	public String getBody() {
		return HConst.server_request_xml(mContext, HConst.SERVER_FLST_PARAM, mTimeStamp);
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if ("t".equals(tagName)) {
			t = new String(ch, start, length);
		}
		if ("p".equals(tagName)) {
			p = new String(ch, start, length);
		}
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		super.obj = mList;
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		if ("f".equals(localName)) {
			mList.add(font);
		}
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		mList = new ArrayList<HShopFontModel>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		tagName = localName;
		if ("f".equals(localName)) {
			font = null;
			try {
				font = new HShopFontModel();
				font.setT(t);
				font.setP(p);
				font.setId(attributes.getValue("id"));
				font.setN1(attributes.getValue("n1"));
				font.setDe(attributes.getValue("de"));
				font.setIc(attributes.getValue("ic"));
				font.setFs(attributes.getValue("fs"));
				font.setFp(attributes.getValue("fp"));
				if(ToolsUtil.DEBUG) {
					font.setPay("0");
				} else {
					font.setPay(attributes.getValue("P"));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
	}

}
