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

public class HResUpdateParser extends DataParser {

	private Context mContext;
	private ArrayList<HResUpdateModel> mList;
	private HResUpdateModel mResUpdate;
	private String tagName;
	private String i;
	private String r;
	private String p;
	private String mR = " ";

	public HResUpdateParser(Context context, String r) {
		super(context);
		this.mContext = context;
		this.mR = r;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<HResUpdateModel> getResUpdate(final String key, final Handler handler) {
		ArrayList<HResUpdateModel> result = (ArrayList<HResUpdateModel>) getCache(key);
		if (result == null) {
			result = (ArrayList<HResUpdateModel>) getNet(key);
		} else {
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
		return HConst.RESLIB_RESUPDATE(mContext, mR);
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if("i".equals(tagName)) {
			i = new String(ch, start, length);
		}
		if ("r".equals(tagName)) {
			r = new String(ch, start, length);
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
			mList.add(mResUpdate);
		}
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		mList = new ArrayList<HResUpdateModel>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		tagName = localName;
		if ("v".equals(localName)) {
			mResUpdate = null;
			try {
				mResUpdate = new HResUpdateModel();
				mResUpdate.setI(i);
				mResUpdate.setR(r);
				mResUpdate.setP(p);
				mResUpdate.setMi(attributes.getValue("mi"));
				mResUpdate.setMa(attributes.getValue("ma"));
				mResUpdate.setPn(attributes.getValue("pn"));
				mResUpdate.setIn(attributes.getValue("in"));
				mResUpdate.setEt(attributes.getValue("et"));
				mResUpdate.setMd(attributes.getValue("md"));
				mResUpdate.setMv(attributes.getValue("mv"));
				mResUpdate.setPu(attributes.getValue("pu"));
				mResUpdate.setFs(attributes.getValue("fs"));
				mResUpdate.setPl(attributes.getValue("pl"));
				mResUpdate.setPr(attributes.getValue("pr"));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
