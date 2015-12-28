package com.haolianluo.sms2.model;

import java.util.HashMap;

import mm.vending.OnPurchaseListener;
import mm.vending.Purchase;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.haolianluo.sms2.R;

public class PayMM {

	private static final String TAG = "PayMM";
	
	// 计费信息(现网自测试)
	private static final String APPID = "300002514033";
	private static final String APPKEY = "A4EB1E5033373646";
	// 计费点信息
//	private static final String LEVEL_PAYCODE = "000000000000"; // 单次
	private static final String MONTH_PAYCODE = "30000251403301"; // 包月
	private static final String ITEMS_PAYCODE = "30000251403302"; // 单点多次

	// 是否缓存授权文件
	private Boolean cacheLicense = true;
	private HashMap<Object, String> msgMap = new HashMap<Object, String>();

	private Purchase purchase;
	private int currentReq = 0;

	public static final int MSG_EVENT_ENABLE_UNSUBSCRIBE = 1;
	public static final int MSG_EVENT_CHECK_FINISHED = 2;
	public static final int MSG_EVENT_ENABLE_UNSUBSCRIBEOK = 3;
	public static final int MSG_EVENT_BEFORE_APPLY = 4;
	public static final int MSG_EVENT_AFTER_APPLY = 5;
	public static final int MSG_EVENT_AFTER_DOWNLOAD = 6;
	public static final int MSG_EVENT_BEFORE_DOWNLOAD = 7;
	public static final int MSG_EVENT_INIT_FINISHED = 8;

	private final String nomalText = "请稍候...";
//	private final String initText = "正在初始化,请稍候...";
	private ProgressDialog mProgressDialog = null;
	private Activity mActivity;
	private Handler mHandler;
	
	public PayMM(Activity activity, Handler handler) {
		mActivity = activity;
		mHandler = handler;
		init();
	}
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Log.i("TAG", msg.what+"-----------------------------"+msg.obj);
			switch (msg.what) {
			case MSG_EVENT_ENABLE_UNSUBSCRIBE:
				showDialog(mActivity, (String) msg.obj);
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}
				break;
			case MSG_EVENT_CHECK_FINISHED:
				showDialog(mActivity, (String) msg.obj);
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}

				break;
			case MSG_EVENT_ENABLE_UNSUBSCRIBEOK:
				showDialog(mActivity, (String) msg.obj);
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}
				break;
			case MSG_EVENT_BEFORE_APPLY:
				showProgressDialog(nomalText);
				break;
			case MSG_EVENT_AFTER_APPLY:
				showProgressDialog(nomalText);
				break;
			case MSG_EVENT_BEFORE_DOWNLOAD:
				showProgressDialog(nomalText);
				break;
			case MSG_EVENT_AFTER_DOWNLOAD:
				showProgressDialog(nomalText);
				break;
			case MSG_EVENT_INIT_FINISHED:

				break;
			}
			Message m_msg = new Message();
			m_msg.what = msg.what;
			m_msg.obj = msg.obj;
			mHandler.sendMessage(m_msg);
		}

	};

	OnPurchaseListener listener = new OnPurchaseListener() {

		public void onBeforeApply() {
			handler.obtainMessage(MSG_EVENT_BEFORE_APPLY).sendToTarget();
		}

		public void onAfterApply() {
			handler.obtainMessage(MSG_EVENT_AFTER_APPLY).sendToTarget();
		}

		@Override
		public void onAfterDownload() {
			handler.obtainMessage(MSG_EVENT_AFTER_DOWNLOAD).sendToTarget();
		}

		@Override
		public void onBeforeDownload() {
			handler.obtainMessage(MSG_EVENT_BEFORE_DOWNLOAD).sendToTarget();
		}

		@Override
		public void onQueryFinish(StatusCode code, HashMap map) {

			Log.d(TAG, "license finish, status code = " + code.name());
//			if (mActivity.isFinishing()) {
//				return;
//			}
			if (code.equals(OnPurchaseListener.StatusCode.QUERY_SUCCEED)) {
				String orderId = (String) map.get(OnPurchaseListener.ORDERID);
				if (currentReq == 1) {
					Message msg = handler
							.obtainMessage(MSG_EVENT_ENABLE_UNSUBSCRIBE);
					msg.obj = new String("查询成功(业务已订购)。");
					msg.sendToTarget();
					return;
				} else {
					Message msg = handler
							.obtainMessage(MSG_EVENT_CHECK_FINISHED);
					msg.obj = new String("查询成功(业务已订购)。");
					msg.sendToTarget();
					return;
				}
			} else {
				Message msg = handler.obtainMessage(MSG_EVENT_CHECK_FINISHED);
				msg.obj = new String("查询结果：" + msgMap.get(code));
				msg.sendToTarget();
			}
		}

		@Override
		public void onBillingFinish(StatusCode code, HashMap orderMap) {
			// 付费失败，建议重试，视失败的次数决定是否继续运行
			Log.d(TAG, "billing finish, status code = " + code.name());
//			if (mActivity.isFinishing()) {
//				return;
//			}
			if (code.equals(OnPurchaseListener.StatusCode.BILL_SUCCEED)) {
				if (currentReq == 1) {
					Message msg = handler
							.obtainMessage(MSG_EVENT_ENABLE_UNSUBSCRIBE);
					msg.obj = new String("订购结果：" + "订购成功");
					msg.sendToTarget();
					return;
				} else {
					Message msg = handler
							.obtainMessage(MSG_EVENT_CHECK_FINISHED);
					msg.obj = new String("订购结果：" + "订购成功");
					msg.sendToTarget();
					return;
				}
			} else {
				if (orderMap != null
						&& code.equals(OnPurchaseListener.StatusCode.AUTH_SUCCEED)) {
					if (currentReq == 1) {
						Message msg = handler
								.obtainMessage(MSG_EVENT_ENABLE_UNSUBSCRIBE);
						msg.obj = new String("订购结果：" + "订购成功");
						msg.sendToTarget();
						return;
					} else {
						Message msg = handler
								.obtainMessage(MSG_EVENT_CHECK_FINISHED);
						msg.obj = new String("订购结果：" + "订购成功");
						msg.sendToTarget();
						return;
					}
				} else {
					Message msg = handler
							.obtainMessage(MSG_EVENT_CHECK_FINISHED);
					msg.obj = new String("订购结果：" + msgMap.get(code));
					msg.sendToTarget();
				}
			}
		}

		@Override
		public void onUnsubscribeFinish(StatusCode code) {
			Log.d(TAG, "unsubscribe finish, status code = " + code.name());
//			if (mActivity.isFinishing()) {
//				return;
//			}
			if (code.equals(OnPurchaseListener.StatusCode.UNSUB_SUCCEED)) {
				if (currentReq == 4) {
					Message msg = handler
							.obtainMessage(MSG_EVENT_ENABLE_UNSUBSCRIBEOK);
					msg.obj = new String("退订结果：" + msgMap.get(code));
					msg.sendToTarget();
					return;
				}
			} else if (code
					.equals(OnPurchaseListener.StatusCode.UNSUB_NOT_FOUND)) {
				if (currentReq == 4) {
					Message msg = handler
							.obtainMessage(MSG_EVENT_ENABLE_UNSUBSCRIBE);
					msg.obj = new String("退订结果：" + msgMap.get(code));
					msg.sendToTarget();
					return;
				}
			} else {
				Message msg = handler.obtainMessage(MSG_EVENT_CHECK_FINISHED);
				msg.obj = new String("退订结果：" + msgMap.get(code));
				msg.sendToTarget();
			}

		}

		@Override
		public void onInitFinish(StatusCode statusCode) {
			Log.d(TAG, "Init finish, status code = " + statusCode.name());
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
			}
		}

	};

	public void init() {
		try {
			ApplicationInfo applicationInfo = mActivity.getApplicationInfo();
			System.out.println("path=" + applicationInfo.dataDir);
			initMsgs();
			// 初始化SDK
			purchase = new Purchase(mActivity, APPID, APPKEY, cacheLicense);
			// 设置网络超时
			purchase.setTimeout(10000, 10000);
			// 调用sdk初始化接口
//			showProgressDialog(initText);
			purchase.init(listener);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private void showProgressDialog(String text) {
		try {
			if (mProgressDialog == null) {
				mProgressDialog = new ProgressDialog(mActivity);
				mProgressDialog.setIndeterminate(true);
				LayoutInflater inflater = mActivity.getLayoutInflater();
				View view = inflater.inflate(R.layout.mm_layout, null);
				mProgressDialog.setView(view);
				mProgressDialog.setMessage("请稍候.....");
				mProgressDialog.setCancelable(false);
			}
			if (!mProgressDialog.isShowing()) {
				mProgressDialog.show();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}

	// 包月订购类型业务查询模块
	public void checkMonth() {
//		if (monthButton.getText().equals(getString(R.string.unsubscribe))) {
//			// 如果包月订购button上的文字为“退订”，则将currentReq设置为4。
//			showProgressDialog(nomalText);
//			purchase.checkLicense(MONTH_PAYCODE, listener);
//			currentReq = 4;
//		} else {
//			// 如果包月订购button上的文字为“订购”，则将currentReq设置为1。
//			showProgressDialog(nomalText);
//			currentReq = 1;
//			purchase.checkLicense(MONTH_PAYCODE, listener);
//		}
	}

	// 单次订购类型业务查询模块
//	public void checkLevel() {
//		showProgressDialog(nomalText);
//		purchase.checkLicense(LEVEL_PAYCODE, listener);
//		currentReq = 6;
//	}

	// 多次订购类型业务查询模块
//	public void checkItems() {
//		showProgressDialog(nomalText);
//		purchase.checkLicense(ITEMS_PAYCODE, listener);
//		currentReq = 6;
//	}

	// 包月订购类型查询业务订购和退订模块。注：仅包月业务可能存在退订功能。
	public void purchaseMonth() {
		try {
			// 执行包月订购功能
			showProgressDialog(nomalText);
			currentReq = 1;
			purchase.checkAndOrder(MONTH_PAYCODE, 1, listener);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}

	// 单次订购类型业务订购模块.
//	public void purchaseLevel() {
//		showProgressDialog(nomalText);
//		purchase.checkAndOrder(LEVEL_PAYCODE, 1, listener);
//		currentReq = 2;
//	}

	// 多次订购类型业务订购模块.
	public void purchaseItems() {
		try {
			int count = 1;
			showProgressDialog(nomalText);
			purchase.checkAndOrder(ITEMS_PAYCODE, count, listener);
			currentReq = 3;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}

	private void showDialog(Activity mActivity, String msg) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
			builder.setIcon(R.drawable.icon);
			builder.setTitle("信息");
			builder.setMessage((msg == null) ? "Undefined error" : msg);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
				}
			});
			builder.create().show();
		} catch(Exception ex) {
		}
	}

	private void initMsgs() {
		// 下载版权声明文件提示信息
		msgMap.put(OnPurchaseListener.StatusCode.COPYRIGHT_VALIDATE_FAIL,
				new String("未能获得版权声明，请重新安装应用!"));
		msgMap.put(OnPurchaseListener.StatusCode.COPYRIGHT_PARSE_FAIL,
				new String("未能获得版权声明，请重新安装应用!"));
		msgMap.put(OnPurchaseListener.StatusCode.COPYRIGHT_NETWORK_FAIL,
				new String("网络错误，请检查网络!"));
		msgMap.put(OnPurchaseListener.StatusCode.COPYRIGHT_NOT_FOUND,
				new String("未能获得版权声明，请稍后再试!"));
		msgMap.put(OnPurchaseListener.StatusCode.COPYRIGHT_LOCAL_LOADFAILED,
				new String("未能获得版权声明，请重新安装应用!"));
		msgMap.put(OnPurchaseListener.StatusCode.COPYRIGHT_LOCAL_VALIDATE,
				new String("未能获得版权声明，请重新安装应用!"));
		// 鉴权提示信息
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_SUCCEED, new String(
				"授权验证通过(该业务已订购)"));
		msgMap.put(
				OnPurchaseListener.StatusCode.AUTH_NOT_DOWNLOAD,
				new String(
						"您使用的程序不是移动应用商场下载的正式版本，请登录移动应用商场http://mm.10086.cn/，或使用MM客户端下载"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_VALIDATE_FAIL,
				new String("订购失败，请稍后再试！"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_PARSE_FAIL, new String(
				"订购失败，请稍后再试！"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_NETWORK_FAIL, new String(
				"网络错误，请检查网络!"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_NOT_FOUND, new String(
				"商品未订购！"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_FORBIDDEN, new String(
				"商品已下架！"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_FROZEN, new String(
				"商品缺货！"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_PAYCODE_ERROR,
				new String("商品不存在!"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_NO_AUTHORIZATION,
				new String("订购失败，请稍后再试！"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_CSSP_BUSY, new String(
				"订购失败，请稍后再试！"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_OTHER_ERROR, new String(
				"订购失败，请稍后再试！"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_INVALID_USER, new String(
				"订购失败，请稍后再试！"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_INVALID_APP, new String(
				"订购失败，请稍后再试！"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_LICENSE_ERROR,
				new String("订购失败，请稍后再试！"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_INVALID_SIGN, new String(
				"订购失败，请稍后再试！"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_NO_ABILITY, new String(
				"订购失败，请稍后再试！"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_NO_APP, new String(
				"订购失败，请稍后再试！"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_TIME_LIMIT, new String(
				"订购失败，请稍后再试！"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_UNDEFINED_ERROR,
				new String("订购失败，请稍后再试！"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_SDK_ERROR, new String(
				"订购失败，请稍后再试！"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_NO_BUSINESS, new String(
				"订购失败，请稍后再试！"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_INVALID_ORDERCOUNT,
				new String("订购失败，订购数量超出限制！"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_FORBID_ORDER, new String(
				"订购失败，商品目前不能订购！"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_INVALID_SIDSIGN,
				new String("订购失败，请稍后再试！"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_STATICMARK_VERIFY_FAILED,
				new String("订购失败，您的程序来源可能有问题，请重新下载！"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_NO_DYQUESTION,
				new String("订购失败，请稍后再试！"));

		// app运行环境检测提示信息
		msgMap.put(OnPurchaseListener.StatusCode.ENV_NOT_CMCC, new String(
				"您不是中国移动用户，不能使用该业务"));
		msgMap.put(OnPurchaseListener.StatusCode.ENV_NO_SIM, new String(
				"初始化失败，您的设备可能没有SIM卡"));
		msgMap.put(OnPurchaseListener.StatusCode.ENV_NETWORK_FAIL, new String(
				"您的设备网络无连接，请检查网络"));
		msgMap.put(OnPurchaseListener.StatusCode.ENV_INIT_RUNNING, new String(
				"正在初始化 ,请稍后再试"));
		msgMap.put(OnPurchaseListener.StatusCode.ENV_NOTSUPPORT_PAD,
				new String("非常抱歉 ,暂不支持无3G模组的平板电脑"));

		msgMap.put(OnPurchaseListener.StatusCode.SUBSCRIBER_IDENTIFY_TIMEOUT,
				new String("网络超时，请检查网络连接！"));
		// 订购提示信息
		msgMap.put(OnPurchaseListener.StatusCode.BILL_SUCCEED, new String(
				"订购成功"));
		msgMap.put(OnPurchaseListener.StatusCode.BILL_PARSE_FAIL, new String(
				"订购失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.BILL_NETWORK_FAIL, new String(
				"网络错误，订购失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.BILL_INTERNAL_FAIL,
				new String("订购失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.BILL_PW_FAIL, new String(
				"支付密码错误，订购失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.BILL_INVALID_SESSION,
				new String("重试次数太多，订购失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.BILL_CSSP_BUSY, new String(
				"订购失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.BILL_INVALID_APP, new String(
				"订购失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.BILL_LICENSE_ERROR,
				new String("订购失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.BILL_INVALID_SIGN, new String(
				"订购失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.BILL_NO_ABILITY, new String(
				"订购失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.BILL_NO_APP, new String(
				"订购失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.BILL_NO_BUSINESS, new String(
				"订购失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.BILL_UNDEFINED_ERROR,
				new String("订购失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.BILL_SDK_ERROR, new String(
				"订购失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.BILL_INVALID_USER, new String(
				"订购失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.BILL_INVALID_LICENSE,
				new String("订购失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.BILL_CANCEL_FAIL, new String(
				"该商品已取消订购"));
		msgMap.put(OnPurchaseListener.StatusCode.BILL_INVALID_SIDSIGN,
				new String("订购失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.BILL_DYMARK_CREATE_ERROR,
				new String("校验错误，订购失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.BILL_SMSCODE_ERROR,
				new String("短信验证码错误，订购失败！"));

		msgMap.put(OnPurchaseListener.StatusCode.BILL_DYMARK_ERROR, new String(
				"校验错误，订购失败！"));
		// 退订提示信息
		msgMap.put(OnPurchaseListener.StatusCode.UNSUB_SUCCEED, new String(
				"退订成功"));
		msgMap.put(OnPurchaseListener.StatusCode.UNSUB_NOT_FOUND, new String(
				"退订失败,该业务未订购！"));
		msgMap.put(OnPurchaseListener.StatusCode.UNSUB_INTERNAL_ERROR,
				new String("退订失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.UNSUB_PARSE_FAIL, new String(
				"退订失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.UNSUB_SDK_ERROR, new String(
				"退订失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.UNSUB_NETWORK_FAIL,
				new String("退订失败,请检查网络连接！"));
		msgMap.put(OnPurchaseListener.StatusCode.UNSUB_NO_ABILITY, new String(
				"退订失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.UNSUB_NO_APP, new String(
				"退订失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.UNSUB_NO_AUTHORIZATION,
				new String("退订失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.UNSUB_FORBIDDEN, new String(
				"退订失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.UNSUB_CSSP_BUSY, new String(
				"退订失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.UNSUB_FROZEN, new String(
				"退订失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.UNSUB_PAYCODE_ERROR,
				new String("退订失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.UNSUB_LICENSE_ERROR,
				new String("退订失败！"));
		msgMap.put(OnPurchaseListener.StatusCode.UNSUB_INVALID_USER,
				new String("退订失败！"));

		// 查询信息
		msgMap.put(OnPurchaseListener.StatusCode.QUERY_CSSP_BUSY, new String(
				"没有符合条件的查询结果"));
		msgMap.put(OnPurchaseListener.StatusCode.QUERY_FROZEN, new String(
				"没有符合条件的查询结果"));
		msgMap.put(OnPurchaseListener.StatusCode.QUERY_INVALID_SIGN,
				new String("没有符合条件的查询结果"));
		msgMap.put(OnPurchaseListener.StatusCode.QUERY_INVALID_USER,
				new String("没有符合条件的查询结果"));
		msgMap.put(OnPurchaseListener.StatusCode.QUERY_LICENSE_ERROR,
				new String("没有符合条件的查询结果"));
		msgMap.put(OnPurchaseListener.StatusCode.QUERY_NO_ABILITY, new String(
				"没有符合条件的查询结果"));
		msgMap.put(OnPurchaseListener.StatusCode.QUERY_NO_APP, new String(
				"没有符合条件的查询结果"));
		msgMap.put(OnPurchaseListener.StatusCode.QUERY_NO_AUTHORIZATION,
				new String("没有符合条件的查询结果"));
		msgMap.put(OnPurchaseListener.StatusCode.QUERY_OTHER_ERROR, new String(
				"没有符合条件的查询结果"));
		msgMap.put(OnPurchaseListener.StatusCode.QUERY_PAYCODE_ERROR,
				new String("商品不存在"));
		msgMap.put(OnPurchaseListener.StatusCode.QUERY_NOT_FOUND, new String(
				"商品未订购"));
		msgMap.put(OnPurchaseListener.StatusCode.AUTH_FORBID_CHECK_CERT,
				new String("软件错误,系统禁止客户端多个线程同时申请安全凭证！！！"));
		msgMap.put(OnPurchaseListener.StatusCode.QUERY_NETWORK_FAIL,
				new String("查询失败,请检查网络连接！"));
		msgMap.put(OnPurchaseListener.StatusCode.QUERY_INVALID_SIDSIGN,
				new String("没有符合条件的查询结果"));
		// 安全凭证
		msgMap.put(OnPurchaseListener.StatusCode.CERT_IMSI_ERR, new String(
				"用户身份数字证书申请失败"));
		msgMap.put(OnPurchaseListener.StatusCode.CERT_NETWORK_FAIL, new String(
				"用户身份数字证书申请失败"));
		msgMap.put(OnPurchaseListener.StatusCode.CERT_PKI_ERR, new String(
				"用户身份数字证书申请失败"));
		msgMap.put(OnPurchaseListener.StatusCode.CERT_PUBKEY_ERR, new String(
				"用户身份数字证书申请失败"));
		msgMap.put(OnPurchaseListener.StatusCode.CERT_SMS_ERR, new String(
				"用户身份数字证书申请失败"));
		msgMap.put(OnPurchaseListener.StatusCode.CETRT_SID_ERR, new String(
				"用户身份数字证书申请失败"));

		msgMap.put(OnPurchaseListener.StatusCode.CERT_REQUEST_CANCEL,
				new String("用户身份数字证书申请取消，请下次再申请"));
		// casdk初始化
		msgMap.put(OnPurchaseListener.StatusCode.CERT_IMEI_ERR, new String(
				"初始化失败，请稍后再试！"));
		msgMap.put(OnPurchaseListener.StatusCode.CERT_APP_ERR, new String(
				"初始化失败，请稍后再试！"));
		msgMap.put(OnPurchaseListener.StatusCode.CERT_CONFIG_ERR, new String(
				"初始化失败，请稍后再试！"));
		msgMap.put(OnPurchaseListener.StatusCode.CERT_VALUE_ERR, new String(
				"初始化失败，请稍后再试！"));
		msgMap.put(OnPurchaseListener.StatusCode.CERT_ORTHER_ERR, new String(
				"初始化失败，请稍后再试！"));
		msgMap.put(OnPurchaseListener.StatusCode.CERT_EXCEPTION, new String(
				"初始化失败，请稍后再试！"));
		msgMap.put(OnPurchaseListener.StatusCode.CERT_SUCCEED, new String(
				"安全凭证申请成功!"));
		msgMap.put(OnPurchaseListener.StatusCode.CERT_INIT_SUCCEED, new String(
				"SDK初始化成功!"));

		// 渠道信息读取提示
		msgMap.put(OnPurchaseListener.StatusCode.CHANNEL_ERROR, new String(
				"程序未被认证，请确认您所下载程序来源合法的应用商场！"));
	}

}