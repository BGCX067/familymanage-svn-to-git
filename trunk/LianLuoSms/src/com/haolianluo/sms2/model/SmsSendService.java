package com.haolianluo.sms2.model;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.data.HSmsApplication;
import com.lianluo.core.util.Base64Coder;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;

public class SmsSendService extends Service {

	private static final String TAG = "SmsSendService";
	public static final String SMSID = "smsId";
	public static final String PHONENUM = "phoneNum";
	public static final String ADDRESS = "address";
	public static final String BODY = "body";

	private Context mContext;
	private HSmsManage smsManager;
	private HAddressBookManager abm;
	
	private String phoneNum;
	private boolean loginFlag;
	private Looper mLooper;
	private SanderHandler mHandler;
	private Handler uiHandler;
	private OutputStream mOStram;
//	private NetworkConnectivityListener mConnectivityListener;
	static final String ACTION_SMS_SEND = "com.llsms.send";
	static final String ACTION_SMS_DELIVERY = "com.llsms.delivery";
	static final String ACTION_SMS_RECEIVER = "android.provider.Telephony.SMS_RECEIVED";
	private static final int SEND_SMS_REQUEST = 1;
	private static final int SEND_MSG_REQUEST = 2;
	private static final int SEND_MSG_WAKUPNETWORK = 3;
	private static final int SEND_MSG_LOGIN = 4;
	private static final int SEND_MSG_LOGOUT = 5;
	
	@Override
	public void onCreate() {
		HLog.i("onCreate");
		HLog.i(TAG, "im start-----------------");
		mContext = this;
		
		HandlerThread thread = new HandlerThread("com.llsms.sender");
		thread.start();
		mLooper = thread.getLooper();
		mHandler = new SanderHandler(mLooper);
		SmsReceiver sendReceiver = new SmsReceiver();
		IntentFilter sendFilter = new IntentFilter(ACTION_SMS_SEND);
		registerReceiver(sendReceiver, sendFilter);

		SmsReceiver deliveryReceiver = new SmsReceiver();
		IntentFilter deliveryFilter = new IntentFilter(ACTION_SMS_DELIVERY);
		registerReceiver(deliveryReceiver, deliveryFilter);

		SmsReceiver smsReceiver = new SmsReceiver();
		IntentFilter receiverFilter = new IntentFilter(ACTION_SMS_RECEIVER);
		registerReceiver(smsReceiver, receiverFilter);

//		mConnectivityListener = new NetworkConnectivityListener();
//		mConnectivityListener.startListening(this);
		
		smsManager = new HSmsManage((HSmsApplication)getApplicationContext());
		abm = new HAddressBookManager(this);
		uiHandler = new Handler();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		HLog.i("onStartCommand");
		loginFlag = getSharedPreferences(HConst.PREF_USER, Context.MODE_PRIVATE).getBoolean(HConst.USER_KEY_LOGIN, false);
		if (intent == null || intent.getExtras() == null) {
			// wake up network
			phoneNum = ToolsUtil.getPhoneNum(mContext);
			
			mHandler.sendEmptyMessage(SEND_MSG_WAKUPNETWORK);
			return START_STICKY;
		}
		
		boolean logout = intent.getExtras().getBoolean("logout");
		if(logout) {
			mHandler.sendEmptyMessage(SEND_MSG_LOGOUT);
			return START_STICKY;
		}
		
		Message m = null;
		// 验证联络人是否在通讯录中且其是联络短信用户时使用sendMsg()发送数据流，否则使用sendSms()发送
		
		HLog.i(TAG, "send loginFlag:" + loginFlag + ", hasDataStram:" + hasDataStram(intent) + ", address:" + intent.getExtras().getString(ADDRESS));
		if (loginFlag && hasDataStram(intent) && mOStram != null) {
			HLog.d(TAG, "------------send msg data");
			m = mHandler.obtainMessage(SEND_MSG_REQUEST);
		} else {
			HLog.d(TAG, "------------send sms data");
			m = mHandler.obtainMessage(SEND_SMS_REQUEST);
		}
		m.setData(intent.getExtras());
		mHandler.sendMessage(m);
		return START_STICKY;
	}

	private void wakeupNetwork() {
		new Thread() {
			public void run() {
				HLog.i(TAG, "start --------------- wakeup ---------------------- net");
				BufferedReader br = null;
				try {
					Socket k = new Socket(InetAddress.getByName(HConst.MSG_SERVER_HOST), HConst.MSG_SERVER_PORT);
					mOStram = k.getOutputStream();
					InputStream is = k.getInputStream();
					br = new BufferedReader(new InputStreamReader(is));
					int len = 0;
					String s, xml;
					if(ToolsUtil.isEmpty(phoneNum)) {
						return;
					}
					mHandler.sendEmptyMessage(SEND_MSG_LOGIN);
					while (true) {
						s = br.readLine();// content-type: xml\r\n
						HLog.i("Paser", "content-type:" + s);
						s = br.readLine();// content-length: xxx\r\n
						HLog.i("Paser", "content-length:" + s);
						if(s == null) {
							break;
						}
						len = s.indexOf(':');
						len = Integer.parseInt(s.substring(len + 1).trim());
						HLog.i("Paser", "length:" + len);

						s = br.readLine();
						HLog.i("Paser", "rn:" + s);

						char[] buffer = new char[len];
						br.read(buffer, 0, len);// xml protocol
						xml = new String(buffer);
						HLog.i("Paser", "body:" + xml);
						paser(xml);
						buffer = null;
					}
				} catch (UnknownHostException e) {
//					e.printStackTrace();
				} catch (IOException e) {
//					e.printStackTrace();
				} finally {
					if (br != null) {
						try {
							br.close();
						} catch (IOException e) {
						}
					}
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mHandler.sendEmptyMessage(SEND_MSG_WAKUPNETWORK);
				}
			}
		}.start();
	}

	private void paser(String xml) {
		// 如果是回执表明已经送达对方，更新点亮状态的时间戳
		// 如果是点亮请求表明对方已成为用户？？？----
		// 如果是路由请求失败表明对方无网络连接（或非用户），使用sms转发。并且
		// 如果对方是点亮状态，检测是否过期（周期为X）。过期则注销。？？？软件被卸载？？？
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes());
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = parserFactory.newSAXParser();
			IMDataParser imParser = new IMDataParser();
			saxParser.parse(is, imParser);
			ImModel imModel = imParser.getImModel();
			String cmd = imModel.getCmd();
			if("1003".equals(cmd)) {
				HLog.d(TAG, "1003");
				//发送失败,请转发
				StringBuffer address = new StringBuffer();
				ArrayList<String> phones = imModel.getReceiverList();
				int size = phones.size();
				for(int i = 0; i < size; i++) {
					address.append(phones.get(i));
					if(i != (size - 1)) {
						address.append(",");
					}
				}
				Bundle bd = new Bundle();
				bd.putString("address", address.toString());
				bd.putString("body", Base64Coder.decodeString(imModel.getMsgData()));
				int id = smsManager.getSmsMaxId()+1;
				bd.putInt("smsId", id);
				sendSms(bd);
			} else if("1004".equals(cmd)) {
				HLog.d(TAG, "1004");
				final String sender = imModel.getSender();
				final String msgData = imModel.getMsgData();
				uiHandler.post(new Runnable() {
					
					@Override
					public void run() {
						//收到的消息
						HSms sms = new HSms();
			   			sms.address = sender;
			   			sms.body = Base64Coder.decodeString(msgData);
			   			sms.ismms = "0";
			   			sms.read =  "0";//未读
//			   			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			   			Date date =(Date)sdf.parse(imModel.getSendDate());
//			   			sms.time = date.getTime() + "";
			   			sms.time = System.currentTimeMillis() + "";
			   			sms.type = "1";//收到的 
			   			sms.threadid = smsManager.getThreadIdForAndress(new String[]{sender});
			   			sms.name = abm.getNameByNumber(sms.address);
			   			sms.smsid = String.valueOf(smsManager.getSmsMaxId() + 1);
						smsManager.updataList(sms, true, false, smsManager.getSmsMaxId()+1, false);
					}
				});
			} else if("8001".equals(cmd) || "8003".equals(cmd) || "8005".equals(cmd)) {
				//错误信息
				HLog.d(TAG, "8001 - 8003 - 8005:" + cmd);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getPhoneNum(String number) {
		try {
			if(number.contains("<")) {
				int start = number.indexOf("<") + 1;
				int end = number.lastIndexOf(">");
				number = number.substring(start, end);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return number;
	}

	private boolean isContact(String number) {
		number = getPhoneNum(number);
		
		ContentResolver contentResolver = getContentResolver();
		String projections[] = new String[] { Phone.CONTACT_ID, Phone.NUMBER };
		number = PhoneNumberUtils.formatNumber(number);
		Cursor cursor = contentResolver.query(Phone.CONTENT_URI, projections, // select
				ContactsContract.CommonDataKinds.Phone.TYPE + "=" + ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE + 
				" or " + ContactsContract.CommonDataKinds.Phone.TYPE + "=" + ContactsContract.CommonDataKinds.Phone.TYPE_HOME + 
				" or " + ContactsContract.CommonDataKinds.Phone.TYPE + "=" + ContactsContract.CommonDataKinds.Phone.TYPE_WORK + 
				" and " + Phone.NUMBER + " = ?", // where sentence
				new String[] { number }, // where values
				null); // order by
		boolean b = cursor.getCount() > 0 ? true : false;
		cursor.close();
		return b;
	}

	private boolean hasDataStram(Intent intent) {
		//是否允许使用数据流
		if(!ToolsUtil.IM_FLAG) {
			HLog.i(TAG, "hasDataStram im flag: false");
			return false;
		}
		
		
		// 验证自身网络可用
//		if(mConnectivityListener.getState() != State.CONNECTED){
//			HLog.i(TAG, "hasDataStream state:" + mConnectivityListener.getState());
//			return false;
//		}
//		HLog.i("SmsSendService", "NetWork Type: " + mConnectivityListener.getNetworkInfo().getTypeName());
		if(!ToolsUtil.checkNet(mContext)) {
			HLog.i(TAG, "hasDataStream state: false --- no net");
			return false;
		}
		
		// 验证联系人在通讯录中，先检索数据库是因为简化同步
		String address = intent.getExtras().getString(ADDRESS);
		if(address.contains(",")) {
			String[] addresses = address.split(",");
			int len = addresses.length;
			for(int i = 0; i < len; i++) {
				if(!isContact(addresses[i])){
					HLog.i(TAG, addresses[i] + "is contact:" + false);
					return false;
				}
			}
		} else {
			if(!isContact(address)){
				HLog.i(TAG, address + "is contact:" + false);
				return false;
			}
		}
		// 验证联系人点亮状态，如果是未点亮状态且已经过期（包括初次与其联系）或者已点亮
		//	检索在自身数据库是否存在，不存在时视为次与其联系
		return true;
	}

	private void sendSms(String address, String body,int id,int addressCount) {
		SmsManager smsMag = SmsManager.getDefault();
		Intent sendIntent = new Intent(ACTION_SMS_SEND);
		Intent deliveryIntent = new Intent(ACTION_SMS_DELIVERY);
		String smsId = String.valueOf(id);
		sendIntent.putExtra("smsId",smsId);
		deliveryIntent.putExtra("smsId",smsId);
		PendingIntent sendPI = PendingIntent.getBroadcast(this, id, sendIntent,PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent deliveryPI = PendingIntent.getBroadcast(this, id,deliveryIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		smsMag.sendTextMessage(address, null, body, sendPI, deliveryPI);
	}

	private void sendSms(Bundle bd){
		String address = bd.getString("address");
		String body = bd.getString("body");
		int id = bd.getInt("smsId");
		String []arrayAddress = address.split(",");
		ArrayList<String> bodyArray = SmsManager.getDefault().divideMessage(body);
		for(int i=0;i<arrayAddress.length;i++){
			for(int j=0;j<bodyArray.size();j++){
				sendSms(arrayAddress[i], bodyArray.get(j), id+i,arrayAddress.length);
			}
		}
	}

	private void write(String xml) {
		try {
			mOStram.write(xml.getBytes("UTF-8"));
			mOStram.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	class SanderHandler extends Handler {
		public SanderHandler(Looper l) {
			super(l);
		}

		@Override
		public void handleMessage(Message m) {
			switch (m.what) {
			case SEND_SMS_REQUEST:
				sendSms(m.getData());
				break;
			case SEND_MSG_REQUEST:
				Bundle bd = m.getData();
				String address = getPhoneNum(bd.getString("address"));
				String body = bd.getString("body");
				String xml = MsgProtocol.biudMessag(mContext, phoneNum, address, body);
				HLog.i(TAG, "send:" + xml);
				write(xml);
				break;
			case SEND_MSG_WAKUPNETWORK:
				new Thread() {
					@Override
					public void run() {
						loginFlag = getSharedPreferences(HConst.PREF_USER, Context.MODE_PRIVATE).getBoolean(HConst.USER_KEY_LOGIN, false);
						if(loginFlag) {
							wakeupNetwork();
						}
					}
				}.start();
				break;
			case SEND_MSG_LOGIN:
				write(MsgProtocol.biudLogin(mContext, phoneNum));
				HLog.i(TAG, "login:" + MsgProtocol.biudLogin(mContext, phoneNum));
				break;
			case SEND_MSG_LOGOUT:
				write(MsgProtocol.biudLogout(mContext, phoneNum));
				HLog.i(TAG, "logout:" + MsgProtocol.biudLogout(mContext, phoneNum));
				break;
			default:
				// write(MsgProtocol.biudLogout());//Never
				mLooper.quit();
			}
		}
	}
}
