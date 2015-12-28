package com.haolianluo.sms2.data;


import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;

import com.haolianluo.sms2.R;
import com.haolianluo.sms2.model.HResDatabaseHelper;
import com.haolianluo.sms2.model.HResProvider;
import com.haolianluo.sms2.ui.sms2.HResLibActivity;
import com.lianluo.core.util.Base64Coder;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;

public class HConst {
	
	/**
	 * true : 在转发界面点击发送之后，转发界面被调用finish()之前;
	 * false: 默认值，转发界面被调用finish()之后；
	 */
	public static boolean isForwarding = false;
	
	public static long collect_id; 
	public static long collect_date; 
	private static final String TAG = "HConst";
	
	public static int width = 320;
	public static int height = 480;
	
	/**主界面界面*/
	public static final int resultTalk = 2000;
	/**flash界面*/
	public static final int resultFlash = 2001;
	/**设置界面*/
	public static final int resultSetting = 2002;
	/**请求弹出通讯录保存界面标志*/
	public static final int REQUEST_CONTACT = 2003;
	/**请求弹出皮肤商店*/
	public static final int REQUEST_SKIN = 2004;
	/**请求弹出资源库*/
	public static final int REQUEST_RESOURCELIB = 2005;
	public static final int REQUEST_RESOURCEACCOUNT = 2006;
	/**预览界面回来*/
	public static final int PREVIEW_RESULT = 1002;
	/**TALK界面回来*/
	public static final int TALK_RESULT = 1003;
	/**是不是在搜索界面*/
	public static boolean isSearchActivity = false;
	/**true 收藏*/
	public static boolean iscollect = false;
	
	
	/**界面跳转标记----1、主界面 2、写信界面(talk) 3、(会话列表项点击跳转而来)talk 4、flash界面 5、设置界面 6、提醒对话 7、设置 8、资源库列表 9.详情页 10.添加联系人页*/
	public static int markActivity = -1;
	/**notify*/
	public static int markNotify = -1;
	/**是不是Htc*/
	public static boolean isHtc = false;
	/**默认名字*/
	public static final String defaultName = "default";
	/**是否进行了标记操作*/
	public static boolean isMark = false;
	/**是否正在显示提醒动画*/
	public static boolean isShowAlertAnimation = false;
	/**是否为收到信息*/
	public static boolean isReceiverSms = false;
	/**是否删除彩信*/
	public static boolean isDeleteMMS = false;
	
	public static final String PREF_USER = "pref_user";
	public static final String USER_KEY_LOGIN = "login";
	public static final String USER_KEY_PHONE = "phone";
	public static final String AFTER_NO_SHOW = "show";
	
	public static final String ACTION_UPDATA_RECEIVER_DIALOG_UI = "com.haolianluo.sms.updata.receiver.dialog.ui";
	public static final String ACTION_KILL_ONESELF = "com.haolianluo.sms.kill.oneself";
	public static final String ACTION_UPDATE_DIALOG = "com.haolianluo.sms.alert_dialog";
	public static final String ACTION_UPDATA_TITLE_SMS_NUMBER = "com.haolianluo.sms.updata.title.sms.number";
	
	public static final String DEFAULT_PACKAGE_NAME = "com.haolianluo.sms2";
	public static String SKIN_PACKAGE_NAME = DEFAULT_PACKAGE_NAME;
	
	/**服务端接口定义---开始*/
	//im
//	public static final String MSG_SERVER_HOST = "219.232.251.185";
//	public static final String MSG_SERVER_HOST = "192.168.1.167";
	public static final String MSG_SERVER_HOST = "message.himessenger.com";
	public static final int MSG_SERVER_PORT = 6000;
	//服务端地址
	public static String SERVER_HOST_URL = "http://ws.lianluo.com/sms2/";
	//皮肤模板请求
	public static String SERVER_LST_PARAM = "lst";
	public static String SERVER_LST_URL = SERVER_HOST_URL + "SmsModuleService";
	//提醒模板请求
	public static String SERVER_RSH_PARAM = "rsh";
	public static String SERVER_RSH_URL = SERVER_HOST_URL + "ReModuleService";
	//贺卡模板请求
	public static String SERVER_HCD_PARAM = "hcd";
	public static String SERVER_HCD_URL = SERVER_HOST_URL + "CaModuleService";
	//字库请求
	public static String SERVER_FLST_PARAM = "flst";
	public static String SERVER_FLST_URL = SERVER_HOST_URL + "SmsModuleService";
	//请求接口xml
	public  static String server_request_xml(Context context, String k, String t) {
		StringBuffer body = new StringBuffer();
		body.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		body.append("<c>");
		body.append("<srqh>");
		body.append("<r1>5</r1>");
		body.append("<r2>" + ToolsUtil.getSDKLevel(context) + "</r2>");
		body.append("<c2>" + ToolsUtil.getPhoneType() + "</c2>");
		body.append("<a1>" + ToolsUtil.getImsi(context) + "</a1>");
		body.append("<b1>" + ToolsUtil.getImei(context) + "</b1>");
		body.append("<q1>" + ToolsUtil.getChannelNum(context) + "</q1>");
		body.append("<v1>" + ToolsUtil.getVersion(context) + "</v1>");
		body.append("</srqh>");
		body.append("<k>" + k + "</k>");
		body.append("<f>0</f>");
		body.append("<g>30</g>");
		body.append("<pt>" + t + "</pt>");
		body.append("</c>");
		return body.toString();
	}
	//问题反馈
	public static String SUGGEST_HOST_URL = "http://ws.haolianluo.com/wap/FeedbackService";
	public  static String suggest_request_xml(Context context, String content) {
		StringBuffer body = new StringBuffer();
		body.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		body.append("<b>");
		body.append("<p>100005</p>");
		body.append("<m>" + ToolsUtil.getPhoneNum(context) + "</m>");
		body.append("<im>" + ToolsUtil.getImsi(context) + "</im>");
		body.append("<v>" + ToolsUtil.getVersion(context) + "</v>");
		body.append("<o>5</o>");
		body.append("<ua>" + ToolsUtil.getPhoneType() + "</ua>");
		body.append("<uid>SMS</uid>");
		body.append("<t>jy</t>");
		body.append("<c>" + content + "</c>");
		body.append("</b>");
		return body.toString();
	}
	//检查更新
	public static String UPDATE_HOST_URL = "http://ws.lianluo.com/sms2/ClientService";
	public  static String update_request_xml(Context context) {
		StringBuffer body = new StringBuffer();
		body.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		body.append("<c>");
		body.append("<p1>9</p1>");
		body.append("<r1>5</r1>");
		body.append("<r2>" + ToolsUtil.getSDKLevel(context) + "</r2>");
		body.append("<c2>" + ToolsUtil.getPhoneType() + "</c2>");
		body.append("<a1>" + ToolsUtil.getImsi(context) + "</a1>");
		body.append("<e1>" + ToolsUtil.getPhoneNum(context) + "</e1>");
		body.append("<b1>" + ToolsUtil.getImei(context) + "</b1>");
		body.append("<q1>" + ToolsUtil.getChannelNum(context) + "</q1>");
		body.append("<v>" + ToolsUtil.getVersion(context) + "</v>");
		body.append("</c>");
		return body.toString();
	}
	   /**新商店接口定义---结束*/
	
		public static String FIRST_INSTALL(Context context) {
			StringBuffer body = new StringBuffer();
			body.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
			body.append("<c>");
			body.append("<srqh>");
			body.append("<r1>5</r1>");
			body.append("<r2>" + ToolsUtil.getSDKLevel(context) + "</r2>");
			body.append("<c2>" + ToolsUtil.getPhoneType() + "</c2>");
			body.append("<a1>" + ToolsUtil.getImsi(context) + "</a1>");
			body.append("<b1>" + ToolsUtil.getImei(context) + "</b1>");
			body.append("<q1>" + ToolsUtil.getChannelNum(context) + "</q1>");
			body.append("<v1>" + ToolsUtil.getVersion(context) + "</v1>");
			body.append("</srqh>");
			body.append("<k>setup</k>");
			body.append("</c>");
			HLog.d(TAG, body.toString());
			return body.toString();
		}
	
	
	/**服务端接口定义---结束*/
	
	/**Map key 皮肤*/
	public final static String KEY_SKIN = "skin";
	/**Map key 字体*/
	public final static String KEY_FONT = "font";
	/**Map key 动画*/
	public final static String KEY_ANIMATION= "animation";
	/**Map value(资源类型 皮肤)*/
	public final static int TYPE_SKIN = 1;
	/**Map value(资源类型 字体)*/
	public final static int TYPE_FONT = 2;
	/**Map value(资源类型 动画)*/
	public final static int TYPE_ANIMATION = 3;
	/** resource charge status free*/
	public final static int CHARGE_FREE = 0;
	/** resource charge status pending*/
	public final static int CHARGE_PENDING = 1;
	/** resource charge status charged*/
	public final static int CHARGE_CHARGED = -1;
	/** resource charge status charged*/
	public final static int CHARGE_TRY = -2;
    public static final String ACTION []= new String []{Intent.ACTION_PACKAGE_ADDED,Intent.ACTION_PACKAGE_REMOVED};	
    public static String APP_KEY = "com.haolianluo.sms";
    public static String str_back = null;
    
    
    /**新商店接口定义---开始*/
    //服务端地址
	public static String RESLIB_HOST_URL = "http://ws.lianluo.com/sms2/SmsModuleService";
	//用户注册检测
	public static String USER_CKECK_HOST_URL = "http://ws.lianluo.com/sms2/user";
	//用户注册
	public static String USER_REGISTER_HOST_URL = "http://q.lianluo.com/group/real/user";

	//免费排行
	public static String RESLIB_FRLST = "frlst";
	//收费排行
	public static String RESLIB_PYLST = "pylst";
	//畅销排行
	public static String RESLIB_CXLST = "cxlst";
	//模版详情
	public static String RESLIB_MDETAIL = "mdetail";
	//用户付费模版列表
	public static String RESLIB_PAIDLST = "Paidlst";
	//已安装模板确认是否需要更新
	public static String RESLIB_NUPDATE = "nupdate";
	//检查用户注册
	public static String USER_ISREG = "isreg";
	//用户注册发送验证码
	public static String USER_REGCODE = "regcode";
	//用户注册
	public static String USER_REGSMS = "regsms";
	//用户登录
	public static String USER_LOGON = "logon";
	//忘记密码请求验证码
	public static String USER_SENDCODE = "sendcode";
	//忘记密码
	public static String USER_FORPASS = "forpass";
	//修改密码
	public static String USER_MODIFY = "modify";
	//请求头
	private static String RESLIB_HEADER(Context context, String phoneNum) {
		StringBuffer body = new StringBuffer();
		body.append("<srqh>");
		body.append("<c1>" + ToolsUtil.getVersion(context) + "</c1>");
		body.append("<r1>5</r1>");
		body.append("<r2>" + ToolsUtil.getSDKLevel(context) + "</r2>");
		body.append("<c2>" + ToolsUtil.getPhoneType() + "</c2>");
		if(phoneNum == null || "".equals(phoneNum)) {
			body.append("<e1>" + ToolsUtil.getPhoneNum(context) + "</e1>");
		} else {
			body.append("<e1>" + phoneNum + "</e1>");
		}
		if(ToolsUtil.MM_FLAG) {
			body.append("<y1>v3.0</y1>");
		} else {
			body.append("<y1> </y1>");
		}
		body.append("<le>" + ToolsUtil.getLanguage() + "</le>");
		body.append("<pi>3</pi>");
		body.append("<a1>" + ToolsUtil.getImsi(context) + "</a1>");
		body.append("<b1>" + ToolsUtil.getImei(context) + "</b1>");
		body.append("<q1>" + ToolsUtil.getChannelNum(context) + "</q1>");
		body.append("</srqh>");
		return body.toString();
	}
	public static int REQUEST_NUMBER = 10;

	//1免费排行, 2收费排行, 3畅销排行
	public static String RESLIB_SHOP(Context context, String params, int f) {
		StringBuffer body = new StringBuffer();
		body.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		body.append("<c>");
		body.append(RESLIB_HEADER(context, null));
		body.append("<k>" + params + "</k>");
		body.append("<f>"+f+"</f>");
		body.append("<g>"+REQUEST_NUMBER+"</g>");
		body.append("<pt>" + new HSharedPreferences(context).getNewListReqTime() + "</pt>");
		body.append("</c>");
		return body.toString();
	}
	//请求更新数量
	public static String NEW_LIST(Context context) {
		StringBuffer body = new StringBuffer();
		body.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		body.append("<c>");
		body.append(RESLIB_HEADER(context, null));
		body.append("<k>nlst</k>");//新模板列表请求
		body.append("<f>1</f>");
		body.append("<g>30</g>");
		body.append("<pt>" + new HSharedPreferences(context).getNewListReqTime() + "</pt>");
		body.append("</c>");
		return body.toString();
	}
	//请求试用模版
	public static String TRY_USE(Context context) {
		StringBuffer body = new StringBuffer();
		body.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		body.append("<c>");
		body.append(RESLIB_HEADER(context, null));
		body.append("<k>tlst</k>");//试用模板列表请求
		body.append("<f>1</f>");
		body.append("<g>300</g>");
		body.append("</c>");
		return body.toString();
	}
	//模版详情
	public static String RESLIB_INFO(Context context, String id) {
		StringBuffer body = new StringBuffer();
		body.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		body.append("<c>");
		body.append(RESLIB_HEADER(context, null));
		body.append("<k>" + RESLIB_MDETAIL + "</k>");
		body.append("<id>" + id + "</id>");
		body.append("</c>");
		return body.toString();
	}
	//模版下载
	public static String RESLIB_DOWNLOAD(Context context, String id) {
		StringBuffer body = new StringBuffer();
		body.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		body.append("<c>");
		body.append(RESLIB_HEADER(context, null));
		body.append("<id>" + id + "</id>");
		body.append("</c>");
		return body.toString();
	}
	//4用户付费模版列表
	public static String RESLIB_PAYLIST(Context context, String pt2) {
		StringBuffer body = new StringBuffer();
		body.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		body.append("<c>");
		body.append(RESLIB_HEADER(context, null));
		body.append("<k>" + RESLIB_PAIDLST + "</k>");
		body.append("<pt2>" + pt2 + "</pt2>");
		body.append("</c>");
		return body.toString();
	}
	//5用户已安装模板确认是否需要更新
	public static String RESLIB_RESUPDATE(Context context, String r) {
		StringBuffer body = new StringBuffer();
		body.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		body.append("<c>");
		body.append(RESLIB_HEADER(context, null));
		body.append("<k>" + RESLIB_NUPDATE + "</k>");
		body.append("<r>" + r + "</r>");
		body.append("</c>");
		return body.toString();
	}
	//用户注册检测
	public static String USER_CHECK(Context context, String phoneNum) {
		StringBuffer body = new StringBuffer();
		body.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		body.append("<c>");
		body.append(RESLIB_HEADER(context, phoneNum));
		body.append("<k>" + USER_ISREG + "</k>");
		body.append("</c>");
		return body.toString();
	}
	//用户注册发送验证码
	public static String USER_REGISTER_CODE(Context context, String phoneNum) {
		StringBuffer body = new StringBuffer();
		body.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		body.append("<c>");
		body.append(RESLIB_HEADER(context, phoneNum));
		body.append("<k>" + USER_REGCODE + "</k>");
		body.append("<mo>" + phoneNum + "</mo>");
		body.append("</c>");
		return body.toString();
	}
	//用户注册
	public static String USER_REGISTER(Context context, String phoneNum, String nickname, String password, String code) {
		StringBuffer body = new StringBuffer();
		body.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		body.append("<c>");
		body.append(RESLIB_HEADER(context, phoneNum));
		body.append("<k>" + USER_REGSMS + "</k>");
		body.append("<ni>" + nickname + "</ni>");
		body.append("<p1>" + Base64Coder.encodeString(password) + "</p1>");
		body.append("<no>" + code + "</no>");
		body.append("</c>");
		return body.toString();
	}
	//用户登录
	public static String USER_LOGIN(Context context, String phoneNum, String password) {
		StringBuffer body = new StringBuffer();
		body.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		body.append("<c>");
		body.append(RESLIB_HEADER(context, phoneNum));
		body.append("<k>" + USER_LOGON + "</k>");
		body.append("<p1>" + Base64Coder.encodeString(password) + "</p1>");
		body.append("<z>0." + System.currentTimeMillis() + "|1.0|2.0|3.0|4.0|5.0|6.0|7.0|8.0|9.0|10.0|11.0|12.0|13.0|14.0</z>");
		body.append("</c>");
		return body.toString();
	}
	//忘记密码发送验证码
	public static String USER_SENDCODE(Context context, String phoneNum) {
		StringBuffer body = new StringBuffer();
		body.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		body.append("<c>");
		body.append(RESLIB_HEADER(context, phoneNum));
		body.append("<k>" + USER_SENDCODE + "</k>");
		body.append("<mo>" + phoneNum + "</mo>");
		body.append("</c>");
		return body.toString();
	}
	//忘记密码
	public static String USER_FORGET(Context context, String phoneNum, String code, String password) {
		StringBuffer body = new StringBuffer();
		body.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		body.append("<c>");
		body.append(RESLIB_HEADER(context, phoneNum));
		body.append("<k>" + USER_FORPASS + "</k>");
		body.append("<mo>" + phoneNum + "</mo>");
		body.append("<no>" + code + "</no>");
		body.append("<np>" + Base64Coder.encodeString(password) + "</np>");
		body.append("</c>");
		return body.toString();
	}
	//修改密码
	public static String USER_UPDATEPASSWD(Context context, String phoneNum, String oldPassword, String newPassword) {
		StringBuffer body = new StringBuffer();
		body.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		body.append("<c>");
		body.append(RESLIB_HEADER(context, phoneNum));
		body.append("<k>" + USER_MODIFY + "</k>");
		body.append("<op>" + Base64Coder.encodeString(oldPassword) + "</op>");
		body.append("<np>" + Base64Coder.encodeString(newPassword) + "</np>");
		body.append("</c>");
		return body.toString();
	}
    /**新商店接口定义---结束*/
		
	public static Context getResourceContext(Context context) {
		Context resContext = context;
		Cursor cursor = context.getContentResolver().query(HResProvider.CONTENT_URI_SKIN, null, 
				HResDatabaseHelper.RES_USE + " = '1'", null, null);
		String packageName = null;
		if(cursor.getCount() > 0)
		{
			cursor.moveToNext();
			packageName = cursor.getString(cursor.getColumnIndex(HResDatabaseHelper.PACKAGENAME));
		}
		cursor.close();
		try {
			if (packageName != null) {
				resContext = context.createPackageContext(packageName,
						Context.CONTEXT_IGNORE_SECURITY);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resContext;
	}
	/**更新策略*/
	public static final String UPDATE_FLAG = "update_flag";
	/**是否出现了强制更新*/
	public static final String IS_UPDATE = "is_update";
	
	/**如果后台联网发现更新，该标志为true，就不再去后台联网检查*/
	public static final String UPDATE = "update";
	
	/**强制更新出现的次数
	 * 1.首次检查出强制更新，不打断用户操作，用户第二次点击进入
	 * 	 软件的时候此标识为2，则弹出强制更新提醒框（更新、退出）；
	 * */
	public static final String SHOW_TIMES = "showTimes";
	
	/**是否显示通知栏提醒普通更新*/
	public static final String ISSHOW_NOTIFA = "notification";
	
	/**用户点击首次强制更新取消按钮后，之后联网检查出来的首次提醒不再显示*/
	public static final String ISSHOW_FORCEUPDATE = "show_first_update_dialog";
	
	/**联网发现需要强制更新的程序相关地址参数*/
	public static final String P = "p";
	public static final String URL = "url";
	
	public static final String SETTING_DIALOG_SHOW = "setting_dialog_show";
	public static final String AUTO_DIALOGS_SHOW = "auto_dialog_show";
	
	/**
	 * 包月用户隔段提醒标识
	 */
	public static final String ORDER_TOAST = "oder_toast";
	
	/**记录每次更换模版的时间*/
	public static final String CHANGERES_TIME = "checkres_time";
	
	/**更换模版*/
	public static final String CHANGERES_TIMEINFO = "checkres_timeinfo";
	
	/**
	 * 更换模板时，服务器记录该更换模板时间，若用户在10天内未更换模板，提示用户“许多新模板上线啦！等你来换模板哦！”
	 * @param beforeTime	上次更换模版的时间	格式：2012/12/22
	 * @return	true就是大于十天，false就是小于十天 
	 */
	public static boolean checkChangeTemIsToast(Date beforeTime)
	{
		boolean flag = false;
		
		if(beforeTime == null || beforeTime.equals(""))
		{
			return flag;
		}
		
		Long resultDay = getDaysBetween(beforeTime, new java.util.Date());

		//如果大于等于10，代表间隔超过十天，设置标志位为true
		if(resultDay >= 10L)
		{
			flag = true;
		}	
		return flag;
	}
	
	/**
	 * 查询两个日期的间隔时间
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static Long getDaysBetween(Date startDate, Date endDate) {
		Calendar fromCalendar = Calendar.getInstance();
		fromCalendar.setTime(startDate);
		fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
		fromCalendar.set(Calendar.MINUTE, 0);
		fromCalendar.set(Calendar.SECOND, 0);
		fromCalendar.set(Calendar.MILLISECOND, 0);

		Calendar toCalendar = Calendar.getInstance();
		toCalendar.setTime(endDate);
		toCalendar.set(Calendar.HOUR_OF_DAY, 0);
		toCalendar.set(Calendar.MINUTE, 0);
		toCalendar.set(Calendar.SECOND, 0);
		toCalendar.set(Calendar.MILLISECOND, 0);

		return (toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24);
	}
	
	/**
	 * 设置更换模版的时间
	 * @param mContext
	 */
	public static void setTheCheckTemTime(Context mContext)
	{
		SharedPreferences.Editor se = mContext.getSharedPreferences(HConst.CHANGERES_TIME, Context.MODE_PRIVATE).edit();
		String time = ToolsUtil.getCurrentTime_Normal();
		se.putString(HConst.CHANGERES_TIMEINFO, time);
		se.commit();
	}
	
	/**
	 * 获取上次模版更换的时间
	 * @param mContext
	 * @return
	 */
	public static String getTheCheckTemTiem(Context mContext)
	{
		String result = "";
		SharedPreferences sp = mContext.getSharedPreferences(HConst.CHANGERES_TIME, Context.MODE_PRIVATE);
		result = sp.getString(HConst.CHANGERES_TIMEINFO, "");
		
		return result;
	}
	
	//模版库上面的新模版数量标识
	public static final String NEW_TEM = "new_tem";
	
	public static final String NEW_TEM_CONFIG = "new_tem_config";		//标识
	public static final String NEW_TEM_COUNT = "new_tem_count";		//新模版的数量
	
	/**
	 * 判断该图标是否需要显示；
	 * 1.被用户点击过，之后就不再显示；
	 * 2.没有被用户点击过；
	 * @param config
	 * @return
	 */
	public static boolean isShowNewTemCount(Context mContext, String config)
	{
		boolean flag = false;
		SharedPreferences sp = mContext.getSharedPreferences(HConst.NEW_TEM, Context.MODE_PRIVATE);
		//true 则是被用户点击过（不显示），false就是没有被用户点击过(显示，但是count必须大于1)
		flag = sp.getBoolean(sp.getString(HConst.NEW_TEM_CONFIG, ""), false);
		
		String count = sp.getString(HConst.NEW_TEM_COUNT, "");
		
		//1. 有新模版，但是没有被用户点击过，--显示
		
		if(flag == false && count != null && !count.equals("") && !count.equals("0") && !count.equals("0.0"))
		{
		}else
		{
			//2. 没有新模版，仍然没有被用户点击过	--不显示
			flag = true;
		}
		
		return flag;
	}
	
	/**
	 * 用户点击过之后就设置标识，下次不再显示
	 * @param mContext
	 */
	public static void setShowEdNewTemCount(Context mContext)
	{
		SharedPreferences sp = mContext.getSharedPreferences(HConst.NEW_TEM, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		
		editor.putBoolean(sp.getString(HConst.NEW_TEM_CONFIG, ""), true);
		editor.commit();
	}
	
	/**
	 * 如果服务器返回的有新模版数量，把他们存起来
	 * @param mContext
	 * @param config	标志位
	 * @param count		新模版的数量
	 */
	public static void setConfigAndCount(Context mContext, String config, String count)
	{
		SharedPreferences.Editor se = mContext.getSharedPreferences(HConst.NEW_TEM, Context.MODE_PRIVATE).edit();
		se.putString(HConst.NEW_TEM_CONFIG, config);
		se.putString(HConst.NEW_TEM_COUNT, count);
		se.commit();
	}
	
	/**
	 * 返回新模版的数量
	 * @param mContext
	 * @return
	 */
	public static String getNewCount(Context mContext)
	{
		String count = "";
		SharedPreferences sp = mContext.getSharedPreferences(HConst.NEW_TEM, Context.MODE_PRIVATE);
		count = sp.getString(HConst.NEW_TEM_COUNT, "");
		return count;
	}
	
	/**
	 * 弹出选择提示框“您已经很久没更换过模板了，去看看吧！”，
	 * 选择提示框两个按钮分别为“去模板库”“以后再说”。
	 * 点击“去模板库”，转至模版库页面；
	 * 点击“以后再说”，停留在提示弹出页面
	 * @param mContext
	 */
	public static void showChangeTemDialog(final Context mContext)
	{
		AlertDialog.Builder builder = new Builder(mContext)
			.setTitle(mContext.getString(R.string.change_tem_toast))
			.setPositiveButton(mContext.getString(R.string.go_reslib), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	dialog.dismiss();
                    mContext.startActivity(new Intent(mContext, HResLibActivity.class));
                    
					//设置更换模版的时间
					HConst.setTheCheckTemTime(mContext);
                }
            })
            .setNegativeButton(R.string.later_go, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	dialog.dismiss();
                	
					//设置更换模版的时间
					HConst.setTheCheckTemTime(mContext);
                }
            })
            .setCancelable(false)
            ;
            builder.create();
            builder.show();
            
		;
	}
	
	/**
	 * 包月提示语
	 */
	private static final String MONTH_RATE = "month_rate";
	private static final String MONTH_RATE_RC1 = "month_rate_rc1";
	private static final String MONTH_RATE_STATU = "month_rate_statu";
	public static void setMonthRC1(Context mContext, String rc1)
	{
		if(rc1 == null)
		{
			return;
		}
		//如果两次的提示语一致，则不需要变更
		if(rc1.equals(getMonthRC1(mContext)))
		{
			return;
		}
		SharedPreferences.Editor se = mContext.getSharedPreferences(HConst.MONTH_RATE, Context.MODE_PRIVATE).edit();
		se.putString(HConst.MONTH_RATE_RC1, rc1);
		se.commit();
	}
	
	public static String getMonthRC1(Context mContext)
	{
		String rc1 = "";
		SharedPreferences sp = mContext.getSharedPreferences(HConst.MONTH_RATE, Context.MODE_PRIVATE);
		rc1 = sp.getString(HConst.MONTH_RATE_RC1, "");
		return rc1;
	}
	
	//包月之后把付费状态存储在本地
	public static void setMonthSTATU(Context mContext, String statu)
	{
		if(statu == null)
		{
			return;
		}
		
		if(statu.equals(getMonthSTATU(mContext)))
		{
			return;
		}
		
		SharedPreferences.Editor se = mContext.getSharedPreferences(HConst.MONTH_RATE, Context.MODE_PRIVATE).edit();
		se.putString(HConst.MONTH_RATE_STATU, statu);
		se.commit();
	}
	
	public static String getMonthSTATU(Context mContext)
	{
		String statu = "";
		SharedPreferences sp = mContext.getSharedPreferences(HConst.MONTH_RATE, Context.MODE_PRIVATE);
		statu = sp.getString(HConst.MONTH_RATE_STATU, "");
		return statu;
	}
	
	public static String DETAIL_BACK_TYPE = "";
	//服务器无该模版信息
	public static final String TYPE_DELETE = "delete";
	//查看
	public static final String TYPE_CHECK = "check";
	
	//空
	public static final String TYPE_EMPTY = "";
}

