package com.lianluo.core.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.widget.Toast;

import com.haolianluo.sms2.R;
import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.data.HSharedPreferences;
import com.haolianluo.sms2.mms.data.WorkingMessage;
import com.haolianluo.sms2.model.HResDatabaseHelper;
import com.haolianluo.sms2.model.HResProvider;

/**
 * 工具类
 * 
 * @author ZhouJian
 * 
 */
public class ToolsUtil {
	public static List<String>mIDlist = new ArrayList<String>();
	public static final boolean IM_FLAG = false;
	public static final boolean DEBUG = false;
	private static final boolean NO_SIM_SENDSMS = false;

	public static final boolean FORCEUPDATE_FLAG = false;
	
	public static final boolean RECEIVER_SMS = true;
	public static final boolean RECEIVER_MMS = true;
	public static final boolean MM_FLAG = true;
	public static final boolean PAGE_FLAG = true;
	
	/**
	 * 判断是否为空
	 * 
	 * @param msg
	 * @return
	 */
	public static boolean isEmpty(String msg) {
		if (msg == null || "".equals(msg)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断是否为空
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isEmpty(List<?> list) {
		if (list == null || list.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断是否为空
	 * 
	 * @param map
	 * @return
	 */
	public static boolean isEmpty(Map<?, ?> map) {
		if (map == null || map.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 输入流转成字符串
	 * 
	 * @param is
	 * @return
	 */
	public static String InputStreamToString(InputStream is) {
		if (is == null) {
			return null;
		}
		StringBuilder result = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String line = null;
			while ((line = reader.readLine()) != null) {
				result.append(line + "\n");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result.toString();
	}

	/**
	 * 字符串转成输入流
	 * 
	 * @param str
	 * @return
	 */
	public static InputStream StringToInputStream(String str) {
		if (str == null) {
			return null;
		}
		ByteArrayInputStream result = null;
		try {
			result = new ByteArrayInputStream(str.getBytes());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * 检查是否使用CMWAP
	 * 
	 * @param context
	 *            上下文
	 * @return 是CMWAP返回true 否则返回false
	 */
	public static boolean isCMWAP(Context context) {
		boolean isCMWAP = false;
		ConnectivityManager con = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = con.getActiveNetworkInfo();
		if (networkInfo != null
				&& "WIFI".equals(networkInfo.getTypeName().toUpperCase())) {
			return isCMWAP;
		} else {
			Cursor cursor = context.getContentResolver().query(
					Uri.parse("content://telephony/carriers/preferapn"),
					new String[] { "apn" }, null, null, null);
			cursor.moveToFirst();
			if (cursor.isAfterLast()) {
				isCMWAP = false;
			}
			try {
				if("cmwap".equals(cursor.getString(0)) || "uniwap".equals(cursor.getString(0))) {
					isCMWAP = true;
				} else {
					isCMWAP = false;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
			return isCMWAP;
		}
	}

	/**
	 * 检查网络 检查手机网络
	 * 
	 * @param activity
	 * @return
	 */
	public static boolean checkNet(Context context) {
		ConnectivityManager con = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = con.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isAvailable()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 设置网络
	 * 
	 * @param context
	 */
	public static void setNet(Context context) {
		try {
			Intent intent = new Intent();
			intent.setClassName("com.android.settings",
					"com.android.settings.WirelessSettings");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static boolean checkSDcard()
	{
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * 删除文件
	 * 
	 * @param filepath
	 * @throws IOException
	 */
	public static void deleteFile(String filepath) throws IOException {
		File f = new File(filepath);// 定义文件路径
		if (f != null && f.exists() && f.isDirectory()) {// 判断是文件还是目录
			if (f.listFiles().length == 0) {// 若目录下没有文件则直接删除
				f.delete();
			} else {// 若有则把文件放进数组，并判断是否有下级目录
				File delFile[] = f.listFiles();
				if (delFile != null && delFile.length > 0) {
					int i = delFile.length;
					for (int j = 0; j < i; j++) {
						if (delFile[j].isDirectory()) {
							// del(delFile[j].getAbsolutePath());
						} else {
							delFile[j].delete();// 删除文件
						}
					}
					if (f.listFiles().length == 0) {
						f.delete();
					}
				}
			}
		}
	}


	public static boolean readSIMCard(Context context) {
		if(!NO_SIM_SENDSMS){
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);// 取得相关系统服务
			switch (tm.getSimState()) { // getSimState()取得sim的状态 有下面6中状态
			case TelephonyManager.SIM_STATE_ABSENT:// sb.append("无卡");break;
				Toast.makeText(context, R.string.simInvalid, Toast.LENGTH_LONG).show();
				return false;
			case TelephonyManager.SIM_STATE_UNKNOWN:// sb.append("未知状态");break;
				Toast.makeText(context, R.string.simInvalid, Toast.LENGTH_LONG).show();
				return false;
			case TelephonyManager.SIM_STATE_NETWORK_LOCKED:// sb.append("需要NetworkPIN解锁");break;
			case TelephonyManager.SIM_STATE_PIN_REQUIRED:// sb.append("需要PIN解锁");break;
			case TelephonyManager.SIM_STATE_PUK_REQUIRED:// sb.append("需要PUK解锁");break;
				Toast.makeText(context, R.string.simInvalid, Toast.LENGTH_LONG).show();
				return false;
			case TelephonyManager.SIM_STATE_READY:// sb.append("良好");
				return true;
			}
			Toast.makeText(context, R.string.simInvalid, Toast.LENGTH_LONG).show();
			return false;
		}else{
			return true;
		}
	}

	/***
	 * 获取当前时间返回yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		return str;
	}

	/***
	 * 获取当前时间返回yyyy/MM/dd
	 * 
	 * @return
	 */
	public static String getCurrentTime_Normal() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		return str;
	}
	
	/***
	 * 将时间格式化为yyyy-MM-dd HH:mm:ss并返回
	 * 
	 * @param time
	 * @return
	 */
	public static String getCurrentTime(Long time) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(time);// 获取当前时间
		String str = formatter.format(curDate);
		return str;
	}

	/***
	 * 将时间格式化为yyyy-MM-dd HH:mm:ss并返回
	 * 
	 * @param time
	 * @return
	 */
	public static String getCurrentTime_dg(Long time) {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		Date curDate = new Date(time);// 获取当前时间
		String str = formatter.format(curDate);
		return str;
	}
	
	/**
	 * 检测名字是serviceName的service是否处于启动状态
	 * 
	 * @param conetext
	 * @param serviceName
	 * @return
	 */
	public static boolean isServiceWork(Context conetext, String serviceName) {
		ActivityManager am = (ActivityManager) conetext
				.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) am
				.getRunningServices(30);
		for (int i = 0; i < runningService.size(); i++) {
			if (runningService.get(i).service.getClassName().toString().equals(
					serviceName)) {
				return true;
			}
		}
		return false;
	}

	/***
	 * 统计个数
	 * 
	 * @param input
	 * @return
	 */
	public static String getCountString(WorkingMessage mWorkingMessage, String input) {
		
		int en = 160;
		int zn = 70;
		int page = 1;
		if (input == null) {
			return en + " / " + page;
		}
		int showCount = 0;
		if (isZNString(input)) {
			int count = input.length() % zn;
			page = input.length() / zn + 1;
			
			if(count == 0 && input.length() != 0){
				showCount = 70;
			}else{
				showCount = count;
			}
			
			if(page <= 3) {
				if(mWorkingMessage.mMmsState != 0) {
					mWorkingMessage.setLengthRequiresMms(false, true);
				}
			} else if(page >= 4) {
				if(mWorkingMessage.mMmsState == 0) {
					mWorkingMessage.setLengthRequiresMms(true, true);
				}
			}
			
			return showCount + " / " + (zn - input.length() % zn)
					+ " (" + ((input.length()-1) / zn + 1) + ")";
		} else {
			int count = input.length() % en;
			if(count == 0 && input.length() != 0){
				showCount = 160;
			}else{
				showCount = count;
			}
			System.out.println("字数："+showCount + " / " + (en - input.length() % en)
					+ " (" + ((input.length()-1) / en + 1) + ")");
			
			if(page <= 3) {
				if(mWorkingMessage.mMmsState != 0) {
					mWorkingMessage.setLengthRequiresMms(false, true);
				}
			} else if(page >= 4) {
				if(mWorkingMessage.mMmsState == 0) {
					mWorkingMessage.setLengthRequiresMms(true, true);
				}
			}
			
			return showCount + " / " + (en - input.length() % en)
					+ " (" + ((input.length()-1) / en + 1) + ")";
		}
	}

	/**
	 * 判断是否包含中文
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isZNString(String input) {
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) > 127)
				return true;
		}
		return false;
	}

	/**
	 * 解压zip包
	 * 
	 * @param zipFileName
	 * @param outputDirectory
	 * @throws Exception
	 */
	public static void unzip(String outputDirectory, String fileName)
			throws Exception {
		String unfileName = outputDirectory + File.separator + fileName;
		fileName = fileName.substring(0, fileName.indexOf("."));
		File dir = new File(outputDirectory + File.separator + fileName);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		ZipInputStream in = new ZipInputStream(new FileInputStream(unfileName));
		ZipEntry z;
		byte[] buffer = new byte[1024];
		while ((z = in.getNextEntry()) != null) {
			if (z.isDirectory()) {
				String name = z.getName();
				name = name.substring(0, name.length() - 1);
				File f = new File(dir, name);
				f.mkdir();

			} else {
				File f = new File(dir, z.getName());
				if (!f.exists()) {
					f.createNewFile();
					FileOutputStream out = new FileOutputStream(f);
					while (true) {
						int size = in.read(buffer);
						if (size == -1) {
							break;
						}
						out.write(buffer, 0, size);
					}
					out.close();
				}
			}
		}
		new File(unfileName).delete();
		in.close();
	}
	
	/***
	 * @param context
	 * @return 联网类型 cmnet 、cmwap 、uninet、uniwap
 	 */
	public static String getNetString(Context context){
		Cursor cursor = context.getContentResolver().query(Uri.parse("content://telephony/carriers/preferapn"), new String[]{"apn"},null, null, null );
        cursor.moveToFirst();
        return  cursor.getString(0);
	}

	public static String getImei(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		return imei;
	}

	public static String getImsi(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		HLog.e("TAG", "imsi:" + imsi);
		return imsi;
	}
	
	public static String getPhoneType() {
		return Build.MODEL == null ? "" : Build.MODEL;
	}
	
	public static String getSDKLevel(Context context) {
//		return Build.VERSION.SDK == null ? "" : Build.VERSION.SDK;
		HSharedPreferences sp = new HSharedPreferences(context);
		if(sp.getIsHighDen())
		{
			return "7";
		}
		else
		{
			return "6";
		}
	}
	
	public static String getVersion(Context context) {
		String result = "";
		InputStream is = context.getResources().openRawResource(R.raw.version);
		byte b;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			while ((b = (byte) is.read()) != -1) {
				baos.write(b);
			}
			result = baos.toString();
			baos.close();
			is.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * 获得运营的各种版本信息（比如QQ,移动MM）
	 * @param context
	 * @return
	 */
	public static String getChannel(Context context) {
		String result = "";
		InputStream is = context.getResources().openRawResource(R.raw.channel);
		byte b;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			while ((b = (byte) is.read()) != -1) {
				baos.write(b);
			}
			result = baos.toString();
			baos.close();
			is.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 获得渠道号信息
	 * 
	 * @param context
	 * @return
	 */
	public static String getChannelNum(Context context) {
		String result = "";
		InputStream is = context.getResources().openRawResource(R.raw.hll);
		byte b;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			while ((b = (byte) is.read()) != -1) {
				baos.write(b);
			}
			result = baos.toString();
			baos.close();
			is.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	public static String getPhoneNum(Context context) {
		String phone = "13000000000";
		SharedPreferences pref = context.getSharedPreferences(HConst.PREF_USER, Context.MODE_PRIVATE);
		boolean flag = pref.getBoolean(HConst.USER_KEY_LOGIN, false);
		if(flag) {
			phone = pref.getString(HConst.USER_KEY_PHONE, phone);
		} else {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if(tm.getLine1Number() != null) {
				phone = tm.getLine1Number();
			}
		}
		return phone;
	}
	
	public static boolean checkPhoneNumber(String phone)
	{
		String ph = phone.substring(0, 2);
		if(ph.equals("86"))
		{
			phone = phone.substring(2);
		}
		ph = phone.substring(0, 3);
		if(ph.equals("+86"))
		{
			phone = phone.substring(3);
		}
		Pattern pp = Pattern.compile("\\d{10}");
		if(!pp.matcher(phone).matches())
		{
			pp = Pattern.compile("\\d{11}");
		}
		return pp.matcher(phone).matches();	
	}
	
	public static boolean checkPassword(String password)
	{
		Pattern pp = Pattern.compile("[0-9a-zA-Z]{6,20}");
		return pp.matcher(password).matches();	
	}
	
	public static String getLanguage() {
//		String country = Locale.getDefault().getCountry();
//		if(isEmpty(country)) {
//			return Locale.getDefault().getLanguage();
//		} else {
//			return Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry();
//		}
		String language = Locale.getDefault().getLanguage();
		if("zh".equals(language)) {
			return "zh-cn";
		} else if("en".equals(language)) {
			return "en-ww";
		} else if("es".equals(language)) {
			return "es-es";
		} else {
			return Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry();
		}
	}
	
	  public static String perm(String[] buf,int start,int end,ArrayList<String> list){  
	    	StringBuffer sb = new StringBuffer();
	        if(start==end){//当只要求对数组中一个字母进行全排列时，只要就按该数组输出即可  
	            for(int i=0;i<=end;i++){
	            		sb.append(buf[i]).append(";%");
	            		if(i==end){
	            			list.add(sb.toString());
	            		}
	            	
	            }  
	        }  
	        else{
	            for(int i=start;i<=end;i++){  
	                String temp=buf[start];//交换数组第一个元素与后续的元素 
	                buf[start]=buf[i];  
	                buf[i]=temp;
	                perm(buf,start+1,end,list);//后续元素递归全排列  
	                temp=buf[start];//将交换后的数组还原  
	                buf[start]=buf[i];  
	                buf[i]=temp;  
	            }  
	        }  
	        return null;
	    }  
	  
		public static boolean isEqualAddress(String titleAddress, String address) {
			String fa = getSubAddress(address);
			String ta = getSubAddress(titleAddress);
			if (ta.equals(fa)) {
				return true;
			}
			return false;
		}
		
		 /**对电话号码进行排序*/
	public static String sortNumber(String str) {
		if (str == null || str.equals("")) {
			return "";
		}
		String[] listnum = str.split(",");
		ArrayList<String> list = new ArrayList<String>();
		int size = listnum.length;
		for (int i = 0; i < size; i++) {
			list.add(listnum[i]);
		}
		Collections.sort(list);
		StringBuffer sbstr = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			sbstr.append(list.get(i)).append(",");
		}
		if (sbstr.length() > 0) {
			str = sbstr.substring(0, sbstr.toString().trim().length() - 1);
		}
		return str;
	}
		
		private static String getSubAddress(String address){
			int len = address.length();
			if(len > 11){
				if(address.startsWith("12520") || address.startsWith("+86")){
					address = address.substring(len - 11);
				}
			}
			return sortNumber(address); 
		}
		
		public static void scanSkins(Context context){
			HLog.e("ToolsUtil", "initSkns");
				PackageManager pm = context.getPackageManager();
				List<PackageInfo> packages = pm.getInstalledPackages(0);
				for(PackageInfo pack : packages)
				{
					try
					{
						String packagename = pack.packageName;
						ApplicationInfo appinfo = pm.getApplicationInfo(packagename,
								PackageManager.GET_META_DATA);
						if(appinfo == null)
						{
							continue;
						}
						else if(appinfo.metaData == null)
						{
							continue;
						}
						String appkey = appinfo.metaData.get(HConst.APP_KEY).toString();
						if(appkey == null)
						{
							continue;
						}
						String appname = pack.applicationInfo.loadLabel(pm).toString();
						ContentValues values = new ContentValues();
						values.put(HResDatabaseHelper.RES_KEY, appkey);
						values.put(HResDatabaseHelper.PACKAGENAME, packagename);
						values.put(HResDatabaseHelper.DISPLAY_NAME, appname);
						context.getContentResolver().insert(HResProvider.CONTENT_URI_SKIN, values);
					}
					catch(Exception e)
					{
						
					}
				}
		}
		
		
	/***
	 * 随机得到模板的名字
	 */
	public static String getFileName(Activity activity) {
		Display dis = activity.getWindowManager().getDefaultDisplay();
		Context context = HConst.getResourceContext(activity);
		String str = context.getPackageName();
		String str1 = str.substring(0, str.length() - 1);
		String path = null;
		if (!str1.equals("com.haolianluo.sms")) {
			path = "mold";
		} else {// 默认皮肤
			if (dis.getWidth() >= 480) {
				path = "mold";
			} else {
				path = "mold-m";
			}
		}
		List<String> list = new ArrayList<String>();
		String[] arrFile = null;
		try {
			arrFile = context.getAssets().list(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int size = arrFile.length;
		for (int i = 0; i < size; i++) {
			list.add(path + "/" + arrFile[i]);
		}
		Random random = new Random();
		int result = random.nextInt(list.size());
		return list.get(result);
	}

	public static String[] getTmpStr(String str,Activity activity) {
		String[] tmp;
		int len = 0;
		Display dis = activity.getWindowManager().getDefaultDisplay();
		if (dis.getWidth() >= 480) {
			len = 80;
		} else {
			len = 70;
		}
		int count = str.length() / len;
		if (str.length() % len == 0) {
			if (count == 0) {
				count = 1;
			}
			tmp = new String[count];
		} else {
			tmp = new String[count + 1];
		}
		for (int i = 0; i < tmp.length; i++) {
			if (i == tmp.length - 1) {

				tmp[i] = str.substring(i * len, str.length());
			} else if (tmp.length == 0) {
				tmp[i] = str;
			} else {
				tmp[i] = str.substring(i * len, (i + 1) * len);
			}
		}
		return tmp;
	}
	
	/***
	 * 从短信内容中提取可以触发事件的一些链接
	 * @param body
	 * @return
	 */
	public static ArrayList<String> getUrls(String body)
	{
		ArrayList<String> list = new ArrayList<String>();
		
		ArrayList<String> phone_list = new ArrayList<String>();
		ArrayList<String> email_list = new ArrayList<String>();
		ArrayList<String> url_list = new ArrayList<String>();
		
		phone_list = pickUp(PHONE_REGEX, body);
		email_list = pickUp(EMAIL_REGEX, body);
		url_list = pickUp(URL_REGEX, body);
		
		if(phone_list.size() > 0)
		{
			for(Object obj : phone_list)
			{
				String str = (String) obj;
				list.add(str);
			}
		}
		
		if(email_list.size() > 0)
		{
			for(Object obj : email_list)
			{
				String str = (String) obj;
				list.add(str);
			}
		}
		
		if(url_list.size() > 0)
		{
			for(Object obj : url_list)
			{
				String str = (String) obj;
				list.add(str);
			}
		}
		return list;
	}
	
	/***
	 * type:
	 * 0---phone
	 * 1---email
	 * 2---url
	 * @param type
	 * @param text
	 * @return
	 */
	public static final int PHONE_REGEX = 0;
	public static final int EMAIL_REGEX = 1;
	public static final int URL_REGEX = 2;
	
	public static ArrayList<String> pickUp(int type, String text) {
			ArrayList<String> result = new ArrayList<String>();
		    Matcher matcher = null;
		    if(type == PHONE_REGEX)
		    {
		    	matcher = PHONE_PATTERN.matcher(text);
		    }else if(type == EMAIL_REGEX)
		    {
		    	matcher = EMAIL_PATTERN.matcher(text);
		    }else if(type == URL_REGEX)
		    {
		    	matcher = Patterns.WEB_URL.matcher(text);
		    }
		    final String telPrefix = "tel:";
		    final String mailtoPrefix = "mailto:";
		    final String urlPrefix = "http://";
		    while (matcher.find()) {
		    	String content = matcher.group();
		    	if(type == PHONE_REGEX)
		    	{
		    		result.add(telPrefix + content);
		    	}else if(type == EMAIL_REGEX)
		    	{
		    		result.add(mailtoPrefix + content);
		    	}
		    	else if(type == URL_REGEX)
		    	{
		    		if(!content.startsWith("http:"))
		    		{
		    			result.add(urlPrefix + content);
		    		}else
		    		{	
		    			result.add(matcher.group());
		    		}
		    	}
		    }
		    return result;
		  }
	  
	/***
	 * 提取电话
	 */
	public static final Pattern PHONE_PATTERN = Pattern.compile("(?<!\\d)(?:(?:1[35]\\d{9})|(?:0[1-9]\\d{1,2}-?\\d{7,8}))(?!\\d)");
	
	/***
	 * 提取邮箱
	 */
	public static final Pattern EMAIL_PATTERN = Pattern
			.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");
	
	
	public static boolean isContact(Context context, String number) {
		if(number.contains("<")) {
			number = number.substring(number.indexOf("<") + 1, number.indexOf(">"));
		}
		
		ContentResolver contentResolver = context.getContentResolver();
		String projections[] = new String[] { Phone.CONTACT_ID, Phone.NUMBER };
		number = PhoneNumberUtils.formatNumber(number);
		Cursor cursor = contentResolver.query(Phone.CONTENT_URI, projections, // select
				ContactsContract.CommonDataKinds.Phone.TYPE + "=" + ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE + 
				" and " + Phone.NUMBER + "=?", // where sentence
				new String[] { number }, // where values
				null); // order by
		boolean b = cursor.getCount() > 0 ? true : false;
		
		while(cursor.moveToNext()) {
			Log.i("TAG", number + "=================" + cursor.getString(cursor.getColumnIndex(Phone.NUMBER)));
		}
		cursor.close();
		return b;
	}
	
}
