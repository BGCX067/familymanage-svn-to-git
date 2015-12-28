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

public class HShopAnimationParser extends DataParser {

	private Context mContext;
	private ArrayList<HShopAnimationModel> mList;
	private HShopAnimationModel animation;
	private String tagName;
	private String p;
	private String t;
	private String p_rc;
	private String mTimeStamp = " ";

	public HShopAnimationParser(Context context) {
		super(context);
		this.mContext = context;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<HShopAnimationModel> getAnimation(final String key, final Handler handler) {
		ArrayList<HShopAnimationModel> result = (ArrayList<HShopAnimationModel>) getCache(key);
		if (result == null) {
			result = (ArrayList<HShopAnimationModel>) getNet(key);
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
		return HConst.SERVER_RSH_URL;
	}

	@Override
	public String getBody() {
		return HConst.server_request_xml(mContext, HConst.SERVER_RSH_PARAM, mTimeStamp);
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
			mList.add(animation);
		}
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		mList = new ArrayList<HShopAnimationModel>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		tagName = localName;
		if ("v".equals(localName)) {
			animation = null;
			try {
				animation = new HShopAnimationModel();
				animation.setT(t);
				animation.setP(p);
				if(p_rc != null) {
					animation.setP_rc(HChanger.convertJiemi(p_rc, ""));
				}
				animation.setRid(attributes.getValue("rid"));
				animation.setRme(attributes.getValue("rme"));
				animation.setRimg(attributes.getValue("rimg"));
				animation.setRth(attributes.getValue("rth"));
				animation.setRpu(attributes.getValue("rpu"));
				animation.setRfs(attributes.getValue("rfs"));
				animation.setRgs(attributes.getValue("rgs"));
				animation.setRps(attributes.getValue("rps"));
				if(ToolsUtil.DEBUG) {
					animation.setPay("0");
				} else {
					animation.setPay(attributes.getValue("P"));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
	}

}
