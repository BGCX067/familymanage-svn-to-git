package com.haolianluo.sms2.model;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.haolianluo.sms2.data.HConst;
import com.lianluo.core.cache.DataParser;
import com.lianluo.core.event.IEvent;
import com.lianluo.core.task.BaseTask;
import com.lianluo.core.task.TaskManagerFactory;

public class HResLibParser extends DataParser {

	private Context mContext;
	private ArrayList<HResLibModel> mList;
	private HResLibModel mResLib;
	private String tagName;
	private String i;
	private String p;
	private String mParams;
	private int f;

	public HResLibParser(Context context, String params, int f) {
		super(context);
		this.mContext = context;
		this.mParams = params;
		this.f = f;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<HResLibModel> getResLibCacheList(final String key, final int page) {
        try{
		ArrayList<HResLibModel> result = (ArrayList<HResLibModel>) getCache(key);
		return result;
        }catch(Exception e){e.printStackTrace();}
        return null;
		
	}
	public void getResLibNetList(final String key,
			final Handler handler, final int page) {
			TaskManagerFactory.createParserTaskManager().addTask(
					new BaseTask(null) {

						@Override
						public void doTask(IEvent event) throws Exception {
//							 try {
//									Thread.sleep(30000);
//								} catch (InterruptedException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
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
		return HConst.RESLIB_HOST_URL;
	}

	@Override
	public String getBody() {
		return HConst.RESLIB_SHOP(mContext, mParams, f);
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if ("i".equals(tagName)) {
			i = new String(ch, start, length);
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
				mResLib.setP(p);
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
				mResLib.setPl(attributes.getValue("pl"));
				mResLib.setPr(attributes.getValue("pr"));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
