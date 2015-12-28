package com.haolianluo.sms2.model;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;

import com.lianluo.core.net.StringHttp;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ParseUtil;
import com.lianluo.core.util.ToolsUtil;

public class PayBussiness {

	private static final String TAG = "PayBussiness";
	private String mResId;
	private String url = "http://ws.haolianluo.com/wap/NewPayAddress";
	private String pay_success_url = "http://ws.haolianluo.com/wap/NewPayServices";
	private final String isPayUrl = "http://ws.lianluo.com/sms2/SmsModuleService";
	private boolean mIsPayMonth = false;

	private String getNetPayAddressXml(Context context, String phoneNum) {
		StringBuffer buf = new StringBuffer();
		buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		buf.append("<c>");
		buf.append("<h>");
		buf.append("<p1>9</p1>");// 产品名称
		buf.append("<r1>5</r1>");// 操作平台 5为android
		buf.append("<r2>" + ToolsUtil.getSDKLevel(context) + "</r2>");// 平台类型版本
		buf.append("<c2>" + ToolsUtil.getPhoneType() + "</c2>");// 机型
		buf.append("<e1>" + phoneNum + "</e1>");// 手机号
		buf.append("<a1>" + ToolsUtil.getImsi(context) + "</a1>");// IMSI
		buf.append("<b1>" + ToolsUtil.getImei(context) + "</b1>");// IMEI
		buf.append("<q1>" + ToolsUtil.getChannelNum(context) + "</q1>");// 渠道号
		buf.append("<v1>" + ToolsUtil.getVersion(context) + "</v1>");// 版本号
		buf.append("<y1>0003</y1>");//协议版本号
		buf.append("</h>");
		buf.append("</c>");
		return buf.toString();
	}

	private String getNewPayServicesRequestXml(Context context, String phoneNum) {
		StringBuffer buf = new StringBuffer();
		buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		buf.append("<c>");
		buf.append("<h>");
		buf.append("<p1>9</p1>");// 产品名称
		buf.append("<r1>5</r1>");// 操作平台 5为android
		buf.append("<r2>" + ToolsUtil.getSDKLevel(context) + "</r2>");// 平台类型版本
		buf.append("<c2>" + ToolsUtil.getPhoneType() + "</c2>");// 机型
		buf.append("<e1>" + phoneNum + "</e1>");// 手机号
		buf.append("<a1>" + ToolsUtil.getImsi(context) + "</a1>");// IMSI
		buf.append("<b1>" + ToolsUtil.getImei(context) + "</b1>");// IMEI
		buf.append("<q1>" + ToolsUtil.getChannelNum(context) + "</q1>");// 渠道号
		buf.append("<v1>" + ToolsUtil.getVersion(context) + "</v1>");// 版本号
		buf.append("<y1>0003</y1>");//协议版本号
		buf.append("</h>");
		buf.append("<k1>getsp</k1>");
		buf.append("<n1>2</n1>");// 计费节点 ：初步设定 1、为前置计费节点 2、为短信节点
		buf.append("<pt>10</pt>");
		buf.append("<id>" + this.mResId + "</id>");// 付费产品ID
		buf.append("</c>");
		return buf.toString();
	}

	private String getNewPayServicesSaveXml(Context context, HMoldPay hmp, String phoneNum) {
		StringBuffer buf = new StringBuffer();
		buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		buf.append("<c>");
		buf.append("<h>");
		buf.append("<p1>9</p1>");// 产品名称
		buf.append("<r1>5</r1>");// 操作平台 5为android
		buf.append("<r2>" + ToolsUtil.getSDKLevel(context) + "</r2>");// 平台类型版本
		buf.append("<c2>" + ToolsUtil.getPhoneType() + "</c2>");// 机型
		buf.append("<e1>" + phoneNum + "</e1>");// 手机号
		buf.append("<a1>" + ToolsUtil.getImsi(context) + "</a1>");// IMSI
		buf.append("<b1>" + ToolsUtil.getImei(context) + "</b1>");// IMEI
		buf.append("<q1>" + ToolsUtil.getChannelNum(context) + "</q1>");// 渠道号
		buf.append("<v1>" + ToolsUtil.getVersion(context) + "</v1>");// 版本号
		buf.append("<y1>0003</y1>");//协议版本号
		buf.append("</h>");
		buf.append("<k1>record</k1>");// 记录record
		if(ToolsUtil.MM_FLAG) {
			if(mIsPayMonth) {
				buf.append("<n1>6</n1>");// 计费节点 ：6、AndroidMM 包月 7 AndroidMM 单条
			} else {
				buf.append("<n1>7</n1>");// 计费节点 ：6、AndroidMM 包月 7 AndroidMM 单条
			}
			buf.append("<f1>7</f1>");// 付费方式 1为短信，２为百宝箱　３为ｗａｐ  7、AndroidMM 内置计费
		} else {
			buf.append("<n1>2</n1>");// 计费节点 ：初步设定 1、为前置计费节点 2、为短信节点
			buf.append("<f1>1</f1>");// 付费方式 1为短信，２为百宝箱　３为ｗａｐ
		}
		if(hmp == null) {
			buf.append("<ad> </ad>");// 指令地址,无时用空格代替
			buf.append("<ct> </ct>");// 指令内容/地址
			buf.append("<st> </st>");// 付费次数
			buf.append("<is> </is>");// 是否隐藏
			buf.append("<ib> </ib>");// 是否拦截下发
		} else {
			buf.append("<ad>" + hmp.getPayAd() + "</ad>");// 指令地址,无时用空格代替
			buf.append("<ct>" + hmp.getPayCt() + "</ct>");// 指令内容/地址
			buf.append("<st>" + hmp.getPaySt() + "</st>");// 付费次数
			buf.append("<is>" + hmp.getPayIs() + "</is>");// 是否隐藏
			buf.append("<ib>" + hmp.getPayIb() + "</ib>");// 是否拦截下发
		}
		buf.append("<s1>" + this.mResId + "</s1>");// 付费产品ID
		buf.append("<pt>10</pt>");// 付费方式
		buf.append("</c>");
		return buf.toString();
	}

	private String getIsPayReresult(Context context, String resId, String phoneNum) {
		StringBuffer buf = new StringBuffer();
		buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		buf.append("<c>");
		buf.append("<srqh>");
		buf.append("<r1>5</r1>");
		buf.append("<r2>" + ToolsUtil.getSDKLevel(context) + "</r2>");
		buf.append("<c2>" + ToolsUtil.getPhoneType() + "</c2>");
		buf.append("<e1>" + phoneNum + "</e1>");// 手机号
		buf.append("<a1>" + ToolsUtil.getImsi(context) + "</a1>");// IMSI
		buf.append("<b1>" + ToolsUtil.getImei(context) + "</b1>");// IMEI
		buf.append("<q1>" + ToolsUtil.getChannelNum(context) + "</q1>");// 渠道号
		buf.append("<v1>" + ToolsUtil.getVersion(context) + "</v1>");// 版本号
		buf.append("</srqh>");
		buf.append("<k>checkpay</k>");
		buf.append("<id>" + resId + "</id>");
		buf.append("</c>");
		return buf.toString();
	}
	
	/***
	 * 原始流程
	 * @param activity
	 * @param resId
	 * @param phoneNum
	 * @return
	 */
	public HMoldPay applyPay(Activity activity, String resId, String phoneNum) {
		HMoldPay result = null;
		try {
			if (ToolsUtil.checkNet(activity)) {
				mResId = resId;
				StringHttp ispay_net = new StringHttp(activity);
				ispay_net.setUrl(isPayUrl);
				ispay_net.connect(getIsPayReresult(activity, resId, phoneNum));
				HLog.i(TAG, "isPay_xml request: " + getIsPayReresult(activity, resId, phoneNum));
				String isPay_xml = ispay_net.getResponse();
				HLog.i(TAG, "isPay_xml: " + isPay_xml);
				if (ParseUtil.parseIsPayXml(isPay_xml)) {
					StringHttp add_net = new StringHttp(activity);
					add_net.setUrl(url);
					add_net.connect(getNetPayAddressXml(activity, phoneNum));
					HLog.i(TAG, "add_xml request: " + getNetPayAddressXml(activity, phoneNum));
					String add_xml = add_net.getResponse();
					HLog.i(TAG, "add_xml: " + add_xml);
					HMoldAdr hma = ParseUtil.parseAdrXml(add_xml);

					StringHttp sms_net = new StringHttp(activity);
					sms_net.setUrl(hma.getAdrC2());
					sms_net.connect(getNewPayServicesRequestXml(activity, phoneNum));
					HLog.i(TAG, "sms_xml request: " + getNewPayServicesRequestXml(activity, phoneNum));
					String sms_xml = sms_net.getResponse();
					HLog.i(TAG, "sms_xml: " + sms_xml);
					result = ParseUtil.parseMoldPayXml(sms_xml);
					result.setAdrC2(hma.getAdrC2());
				} else {
					HLog.e(TAG, "is p flag: false");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	private PayMM payMM;
	
	/**
	 *  MM商城初始化
	 * @param activity
	 */
	public void applyPay(Activity activity, Handler handler) {
		if(!ToolsUtil.MM_FLAG) {
			return;
		}
		a_handler = handler;
		payMM = new PayMM(activity, mHandler);
	}
	
	private Handler a_handler;
	Handler mHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			if("订购结果：订购成功".equals(msg.obj)) {
				a_handler.sendEmptyMessage(200);
				
				Log.i("TAG", "pay success---------------------");
				StringHttp save_net = new StringHttp(activity);
				save_net.setUrl(pay_success_url);
				Log.i("TAG", phoneNum+"pay success---------------------"+getNewPayServicesSaveXml(activity, null, phoneNum));
				save_net.connect(getNewPayServicesSaveXml(activity, null, phoneNum));
				HLog.i(TAG, "save_xml request: " + getNewPayServicesSaveXml(activity, null, phoneNum));
				String save_xml = save_net.getResponse();
				
				Log.i("TAG", "pay success---------------------"+save_xml);
				HLog.i(TAG, "save_xml: " + save_xml);
				boolean flag = ParseUtil.parseMoldSaveXml(save_xml);
				
				Log.i("TAG", "pay success---------------------"+flag);
				HLog.e(TAG, "save flag:" + flag);
			} else {
				a_handler.sendEmptyMessage(100);
			}
		}

	};
	
	private Activity activity;
	private String phoneNum;
	/**
	 * MM商城，点击下载按钮，调用该方法，弹出付费框，接收handler的回传结果付费是否成功的标志
	 * @param activity
	 * @param handler
	 */
	public void startPay(Activity activity, String resId, String phoneNum, boolean isPayMonth) {
		if(!ToolsUtil.MM_FLAG) {
			return;
		}
		try {
			mResId = resId;
			this.activity = activity;
			this.phoneNum = phoneNum;
			
			if(isPayMonth) {
				mIsPayMonth = true;
				payMM.purchaseMonth();
			} else {
				mIsPayMonth = false;
				payMM.purchaseItems();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 原始正常流程
	 * 200 付费成功
	 * 100 付费失败
	 * @param activity
	 * @param handler
	 * @param hmd
	 * @param phoneNum 电话号码
	 */
	public void startPay(Activity activity, Handler handler, HMoldPay hmd, String phoneNum) {
		try {
			String ad = hmd.getPayAd();
			String ct = hmd.getPayCt();
			HLog.i(TAG, "startPay ad:" + ad + ", ct:" + ct);
			if(ToolsUtil.isEmpty(ad) || ToolsUtil.isEmpty(ct)) {
				HLog.i(TAG, "startPay no pay");
				
				handler.sendEmptyMessage(100);
				return;
			} else {
				HLog.i(TAG, "startPay start pay");
				int len = Integer.parseInt(hmd.getPaySt().trim());
				SmsManager smsManager = SmsManager.getDefault();
				for (int i = 0; i < len; i++) {
					smsManager.sendTextMessage(ad, null, ct, null, null);
					Log.i("TAG", i + "pay ad:" + ad + ", ct:" + ct);
					HLog.i(TAG, "ad:" + ad + ", ct:" + ct);
					try {
						Thread.sleep(1000);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}

				StringHttp save_net = new StringHttp(activity);
				save_net.setUrl(hmd.getAdrC2());
				save_net.connect(getNewPayServicesSaveXml(activity, hmd, phoneNum));
				HLog.i(TAG, "save_xml request: " + getNewPayServicesSaveXml(activity, hmd, phoneNum));
				String save_xml = save_net.getResponse();
				HLog.i(TAG, "save_xml: " + save_xml);
				boolean flag = ParseUtil.parseMoldSaveXml(save_xml);
				HLog.e(TAG, "save flag:" + flag);
				
				if(flag) {
					handler.sendEmptyMessage(200);
					return;
				} else {
					handler.sendEmptyMessage(100);
					return;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			handler.sendEmptyMessage(100);
			return;
		}
	}

}
