package com.haolianluo.sms2.model;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.haolianluo.sms2.data.HConst;
import com.lianluo.core.cache.DataParser;
import com.lianluo.core.event.IEvent;
import com.lianluo.core.task.BaseTask;
import com.lianluo.core.task.TaskManagerFactory;
import com.lianluo.core.util.HChanger;
import com.lianluo.core.util.ToolsUtil;

public class HResLibParser extends DataParser {

	private Context mContext;
	private ArrayList<HResLibModel> mList;
	private HResLibModel mResLib;
	private String tagName;
	private String i;
	private String by;
	private String rc;
	private String rc1;
	private String rc2;
	private String p;
	private String mParams;
	private int f;

	public HResLibParser(Context context, String params, int f) {
		super(context);
		this.mContext = context;
		this.mParams = params;
		this.f = f;
	}

	//@SuppressWarnings("unchecked")
//	public ArrayList<HResLibModel> getResLibCacheList(final String key, final int page) {
//
//		ArrayList<HResLibModel> result = (ArrayList<HResLibModel>) getCache(key);
//		ArrayList<HResLibModel> result = new ArrayList<HResLibModel>();
//		return result;
//	}
	
	public void getResLibNetList(final String key,
			final Handler handler, final int page) {
			TaskManagerFactory.createParserTaskManager().addTask(
					new BaseTask(null) {

						@Override
						public void doTask(IEvent event) throws Exception {
							Object obj = getNet(key);
								Message msg = new Message();
								msg.obj = obj;
								if (page > 0) {
									Bundle data = new Bundle();
									data.putInt("page", page);
									msg.setData(data);
								}
								handler.sendMessage(msg);
							}
					});
	}
	@Override
	public String getUrl() {
		Log.i("TAG", "=========url url:" + HConst.RESLIB_HOST_URL);
		return HConst.RESLIB_HOST_URL;
	}

	@Override
	public String getBody() {
		Log.i("TAG", "=========url body:" + HConst.RESLIB_SHOP(mContext, mParams, f));
		return HConst.RESLIB_SHOP(mContext, mParams, f);
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if ("i".equals(tagName)) {
			i = new String(ch, start, length);
		}
		if("by".equals(tagName)) {
			by = new String(ch, start, length);
		}
		if ("p".equals(tagName)) {
			p = new String(ch, start, length);
		}
		if(ToolsUtil.MM_FLAG) {
			if ("rcm".equals(tagName)) {
				rc = new String(ch, start, length);
			}
		} else {
			if ("rc".equals(tagName)) {
				rc = new String(ch, start, length);
			}
		}
		if ("rc1".equals(tagName)) {
			rc1 = new String(ch, start, length);
		}
		if ("rc2".equals(tagName)) {
			rc2 = new String(ch, start, length);
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
			mList.add(mResLib);
		}
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		mList = new ArrayList<HResLibModel>();
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
				mResLib.setBy(by);
				mResLib.setP(p);
				mResLib.setRc(HChanger.convertJiemi(rc, ""));
				mResLib.setRc1(HChanger.convertJiemi(rc1, ""));
				mResLib.setRc2(HChanger.convertJiemi(rc2, ""));
				mResLib.setMi(attributes.getValue("mi"));
				mResLib.setMa(attributes.getValue("ma"));
				mResLib.setPn(attributes.getValue("pn"));
				mResLib.setPkn(attributes.getValue("pkn"));
				mResLib.setIn(attributes.getValue("in"));
				mResLib.setEt(attributes.getValue("et"));
				mResLib.setMd(attributes.getValue("md"));
				mResLib.setMv(attributes.getValue("mv"));
				mResLib.setPu(attributes.getValue("pu"));
				mResLib.setFs(attributes.getValue("fs"));
				mResLib.setMn(attributes.getValue("mn"));
				mResLib.setPl(attributes.getValue("pl"));
				mResLib.setPr(attributes.getValue("pr"));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public String getRc() {
		return rc;
	}

	public void setRc(String rc) {
		this.rc = rc;
	}

	public String getRc1() {
		return rc1;
	}

	public void setRc1(String rc1) {
		this.rc1 = rc1;
	}

	public String getRc2() {
		return rc2;
	}

	public void setRc2(String rc2) {
		this.rc2 = rc2;
	}

}
