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
import com.lianluo.core.util.HChanger;
import com.lianluo.core.util.ToolsUtil;

public class HShopSkinParser extends DataParser {

	private Context mContext;
	private ArrayList<HShopSkinModel> mList;
	private HShopSkinModel skin;
	private String tagName;
	private String p;
	private String t;
	private String p_rc;
	private String mTimeStamp = " ";

	public HShopSkinParser(Context context) {
		super(context);
		this.mContext = context;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<HShopSkinModel> getSkin(final String key, final Handler handler) {
		ArrayList<HShopSkinModel> result = (ArrayList<HShopSkinModel>) getCache(key);
		if (result == null) {
			result = (ArrayList<HShopSkinModel>) getNet(key);
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
		return HConst.SERVER_LST_URL;
	}

	@Override
	public String getBody() {
//		return HConst.server_request_xml(mContext, HConst.SERVER_LST_PARAM, mTimeStamp);
		return HConst.server_request_xml(mContext, HConst.SERVER_LST_PARAM, " ");
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
		if("rc".equals(tagName)) {
			p_rc = new String(ch, start, length);
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
		if ("v".equals(localName)) {
			mList.add(skin);
		}
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		mList = new ArrayList<HShopSkinModel>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		tagName = localName;
		if ("v".equals(localName)) {
			skin = null;
			try {
				skin = new HShopSkinModel();
				skin.setT(t);
				skin.setP(p);
				if(p_rc != null) {
					skin.setP_rc(HChanger.convertJiemi(p_rc, ""));
				}
				skin.setMi(attributes.getValue("mi"));
				skin.setMa(attributes.getValue("ma"));
				skin.setPn(attributes.getValue("pn"));
				skin.setIo(attributes.getValue("io"));
				skin.setEt(attributes.getValue("et"));
				skin.setIn(attributes.getValue("in"));
				skin.setIn2(attributes.getValue("in2"));
				skin.setPu(attributes.getValue("pu"));
				skin.setFs(attributes.getValue("fs"));
				if(ToolsUtil.DEBUG) {
					skin.setPay("0");
				} else {
					skin.setPay(attributes.getValue("P"));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
