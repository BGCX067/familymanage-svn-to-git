package com.haolianluo.sms2.model;


import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.data.HSharedPreferences;
import com.haolianluo.sms2.data.HSmsApplication;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;
import com.haolianluo.sms2.R;
import com.haolianluo.sms2.ui.sms2.HTalkActivity;
import com.haolianluo.sms2.ui.sms2.HThreadActivity;

/***
 * 一个talk
 * @author jianhua 2011年11月29日16:33:48
 */
public class HSms {
	
	public String _id;
	public String threadid;
	public String smsid;
	public String body;
	public String time;
	public String type;
	public String address;
	public String read;
	public String name = HConst.defaultName;
	public String ismms = "0";
	
	public HSms(){}
	
	
	/**
	 * 读取talkList
	 * @param cursor
	 * @param position
	 * @param name  title 上面的
	 * @param address  title 上面的
	 * @return
	 */
	public HSms getTalkMold(Cursor cursor,int []position,String name,String address){
		HSms sms = new HSms();
//		if(cursor.getPosition() == cursor.getCount() - 1){
			position[0] = cursor.getColumnIndex("_id");
			position[1] = cursor.getColumnIndex("body");
			position[2] = cursor.getColumnIndex("date");
			position[3] = cursor.getColumnIndex("type");
			position[4] = cursor.getColumnIndex("read");
			position[5] = cursor.getColumnIndex("address");
			position[6] = cursor.getColumnIndex("thread_id");
//		}
		sms.threadid = cursor.getString(position[6]);
		sms.smsid = cursor.getString(position[0]);
		sms.body = cursor.getString(position[1]);
		sms.time = cursor.getString(position[2]);
		sms.type = cursor.getString(position[3]);
		sms.read = cursor.getString(position[4]);
		sms.address = cursor.getString(position[5]);
		String []nameTitleArrary = name.split(",");
		String []addressTitleArrary = address.split(",");
		if(sms.type.equals("3")){
			sms.address = address;
			sms.name = name;
		}else{
			int addressLenght = addressTitleArrary.length;
			for(int i=0;i<addressLenght;i++){
				if(sms.address.equals((addressTitleArrary[i]))){
					sms.name = nameTitleArrary[i];
				}
			}
		}
		return sms;
	}
	
	
	
	public Cursor getDraftCursor(HSmsApplication application,String address){
		if("".equals(address) || address == null){
			return null;
		}
		address = address.replace(",", ";");
		ArrayList<String> list = new ArrayList<String>();
		
		String[]str = address.split(";");
	    StringBuffer sbAdd = new StringBuffer();
	    if(str.length < 5){
	    	 ToolsUtil.perm(str,0,str.length-1,list);  
	    	 for(int i=0;i<list.size();i++){
	         	if(i == 0){
	     			sbAdd.append("type=3 and ");
	     			sbAdd.append(" address like '"+list.get(i).substring(0, list.get(i).length()-2)+"'");
	     		}else{
	     			sbAdd.append(" or address like '"+list.get(i).substring(0, list.get(i).length()-2)+"'");
	     		}
	         }
	    }else{
	    	sbAdd.append("type=3 and address='"+address+"'");
	    }
       
		String where = sbAdd.toString();
		String oderbyDate = "date" + " desc";
		Cursor cur = application.getContentResolver().query(Uri.parse("content://sms/draft"), null,where, null, oderbyDate);
		return cur;
	}
	
	
	
	/***
	 * 删除talk中的一条短信
	 * @param context
	 * @param id
	 * @param isMMS
	 */
	public void deleteTalkItemDB(final HSmsApplication application,final String id,final String isMMS){
		if (isMMS.equals("0")) {
			Uri mUri = Uri.parse("content://sms/" + id);
			application.getContentResolver().delete(mUri, null, null);
		} else {
			HConst.isDeleteMMS = true;
			Uri mUri = Uri.parse("content://mms/" + id);
			application.getContentResolver().delete(mUri, null, null);
		}
		notification_updata(application);
		
	}
	
	/***
	 * 把信息插入到系统信息库中
	 * @param context
	 * @param sms
	 */
	public void insertSystem(HSmsApplication context,HSms sms,int id){
		int sumInsert = 0;
		 if(sms.type.equals("3")){
			 if(HConst.isHtc){
				 HSms s = sms;
				 if(s.smsid == null){
//					 s.smsid = String.valueOf(id);
				 }
//				 HLog.i("s.smsid " + s.smsid);
//					 sms.smsid = String.valueOf(id);
//					 context.getContentResolver().insert(Uri.parse("content://sms/draft/#"), addMessageToUri(context, sms, sms.address.replace(",",";")));
//				 }else{
//					 String where = "_id =" + "'" + sms.smsid + "'";
//					 context.getContentResolver().update(Uri.parse("content://sms/draft/#"), addMessageToUri(context, sms, sms.address.replace(",",";")), where, null);
//				 }
				 context.getContentResolver().insert(Uri.parse("content://sms/draft/#"), addMessageToUri(context, s, s.address.replace(",",";")));
			 }else{
				 context.getContentResolver().insert(Uri.parse("content://sms/draft/#"), addMessageToUri(context, sms, ""));
			 }
		 }else{
			 HSms s = sms;
			 String[] aaressArray = s.address.split(",");
//			 HLog.i(s.address +" aaressArray " + aaressArray.length);
			 while (sumInsert < aaressArray.length) {
					if (!aaressArray[sumInsert].equals("")) {
//						 s.smsid = String.valueOf(id+sumInsert);
//						 HLog.i("insert " + s.smsid);
						context.getContentResolver().insert(Uri.parse("content://sms/"), addMessageToUri(context, s, aaressArray[sumInsert]));
						 int count = 0;
						 if(sumInsert == 0){
							 count = smsNoReadCount(context);
						 }else{
							 count = count + 1;
						 }
						 String name = new HAddressBookManager(context).getNameByNumber(s.address);
						 if(name.equals(HConst.defaultName)){
							 name = s.address;
						 }
						 if(!s.type.equals("2")){
							 notification_show(context,R.drawable.icon,name+": "+s.body,count+context.getString(R.string.nNewSms),count);
							 HSmsManage hsm = new HSmsManage(context); 
							 if(hsm.getAdapter() != null ){
								 if(HConst.markActivity == 3 || HConst.markActivity == 4){
									 HThreadManager tm = new HThreadManager(context); 
									 int threadPosition = tm.getPosition(s.address);
									 String titleAddress = hsm.getTalkTitleAddress(threadPosition);
									 if(titleAddress != null && ToolsUtil.isEqualAddress(titleAddress, s.address)){
										 notification_updata(context);
									 }
								 }
							 }
						 }
					}
					sumInsert = sumInsert + 1;
				} 
		 }
		
	}
	
    /***
     * 将要插入的数据打包成ContentValues
     * @param context
     * @param sms
     * @param address 针对群发
     * @return
     */
	private ContentValues addMessageToUri(Context context, HSms sms,String address) {
		ContentValues values = new ContentValues();
		values.put("address", address);
		values.put("date", sms.time);
		values.put("status", -1);
		values.put("type", sms.type);
		values.put("body", sms.body);
		values.put("read", sms.read);
		if (sms.threadid == null || "".equals(sms.threadid)) {
			String str = null;
			values.put("thread_id", str);
		} else {
			values.put("thread_id", sms.threadid);
		}
		return values;
	}
	 
	 
	 
	 
	 /**
	  * 将草稿修改
	  * @param id
	  * @param address
	  * @param body
	  * @param date
	  * @param type
	  * @param isUpdatadaft
	  * TODO wyn talk
	  */

	public void updataSystem(HSmsApplication context,HSms sms,int id) {
		int sum = 0;
		if (type.equals("3") && HConst.isHtc) {
			context.getContentResolver().update(Uri.parse("content://sms/"), addMessageToUri(context, sms,address),  "_id =" + sms.smsid, null);
		} else if (type.equals("2") && HConst.isHtc) {
			String[] StringArray = address.split(",");
			while (sum < StringArray.length) {
				if (!StringArray[sum].equals("")) {
					
//					sms.smsid =  String.valueOf(id+sum);
					context.getContentResolver().insert(Uri.parse("content://sms/"),addMessageToUri(context, sms, StringArray[sum]));
					if (sum == 0) {
						Uri mUri = Uri.parse("content://sms/" + sms.smsid);
						context.getContentResolver().delete(mUri, null,null);
					}
				}
				sum = sum + 1;
			}
		} else {
			String[] StringArray = address.split(",");
			while (sum < StringArray.length) {
				if (!StringArray[sum].equals("")) {
					
//					HLog.i( id +" sms.smsid " + sms.smsid);
//					HLog.i( id +" typetype " + type);
//					sms.smsid =  String.valueOf(id+sum);
					context.getContentResolver().insert(Uri.parse("content://sms/"),addMessageToUri(context, sms, StringArray[sum]));
					if (sum == 0) {
						Uri mUri = Uri.parse("content://sms/" + sms.smsid);
						context.getContentResolver().delete(mUri, null,null);
					}
				}
				sum = sum + 1;
			}
		}
	}
	
	/**根据内容搜索*/
	public StringBuffer doSearchList(String str,HSmsApplication mApplication) {
		StringBuffer sb = new StringBuffer();
		String where = "body like '%" + str + "%'";
		Cursor cur = mApplication.getContentResolver().query(Uri.parse("content://sms/"),null, where, null, null);
		cur.moveToLast();
		while (!cur.isBeforeFirst()) {
			String thread_id = cur.getString(cur.getColumnIndex("thread_id"));
			if (thread_id != null && thread_id.length() > 0) {
				sb.append(thread_id).append(",");
			}
			cur.moveToPrevious();
		}
		cur.close();
		return sb;
	}	
	
	public static NotificationManager mNotificationManager;
	public static final int mineSms = 1400;
	/***
	 * 通知
	 * @param context
	 * @param id
	 * @param ticker
	 * @param body
	 * @param count
	 */
    public void notification_show(HSmsApplication context ,int id,String ticker,String body,int count) { 
    	HSharedPreferences spf = new HSharedPreferences(context);
    	if(!spf.getMessageSwitch()){
    		return;
    	}
//    	HThreadActivity.isGo = true;
    	if(mNotificationManager == null){
    		mNotificationManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
    	}
		CharSequence from = ticker;       
		CharSequence message = body;
		
		HLog.d("TAG", "from : " + from + " message : " + message);
		Intent intent = new Intent();
		intent.putExtra("notification", "notification");
		
		if(count == 1){
			String []str = getNearData(context);
			Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/"), null, "address = '" + str[1] + "'", null, null);
			String threadId = null;
			if(cursor != null && cursor.getCount() > 0)
			{
				cursor.moveToNext();
				threadId = cursor.getString(cursor.getColumnIndex("thread_id"));
			}
			if(threadId != null)
			{
				intent.putExtra("threadId", threadId);
			}
			intent.putExtra("address", str[1]);
			intent.putExtra("name", new HAddressBookManager(context).getNameByNumber(str[1]));
			
			intent.setClass(context, HTalkActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setType("vnd.android-dir/mms-sms");  
		}else{
			intent.setClass(context, HThreadActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setType("vnd.android-dir/mms-sms");  
		}
		Log.i("TAG", "-----a-----------a----------notification_show");
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,  intent,PendingIntent.FLAG_CANCEL_CURRENT);   
	    Notification notif = null;
	    if(notif == null){
	    	 notif = new Notification(); 
	    }
	    LED(context,notif); 
	    if(!ticker.equals("")){
	    	current(context, notif);
	    	LED(context,notif); 
	    }
	  //设置自动清除
	    if(id != 0){
	    	 notif.icon = id; //context.getString(R.string.newSms)
	    }
		notif.tickerText = ticker;
		notif.setLatestEventInfo(context, from, message, contentIntent);
		mNotificationManager.notify(mineSms, notif);
		if(count == 0){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mNotificationManager.cancel(mineSms);
			mNotificationManager = null;
		}
    }
    
    /***
     * 更新通知
     * @param mApplication
     */
    public void notification_updata(HSmsApplication mApplication) {
		int count = smsNoReadCount(mApplication);
		if (count < 1) {
			notification_click();
		} else {
			if (mNotificationManager != null) {
				notification_show(mApplication, R.drawable.icon, "", count + mApplication.getString(R.string.nNewSms), count);
			}
		}
	}
    
    /***
     * 点击后取消通知
     */
    public static void notification_click(){
    	if(mNotificationManager != null)
		{
		   mNotificationManager.cancel(mineSms);
		   mNotificationManager = null;
		}	
    }
    
    /****
     * 获得通知声音
     * @param context
     * @param notif
     */
    public void current(HSmsApplication mApplication,Notification notif) {
        AudioManager audio = (AudioManager)mApplication.getSystemService(Context.AUDIO_SERVICE);
        switch (audio.getRingerMode()) {
                case AudioManager.RINGER_MODE_SILENT: 
                	 LED(mApplication,notif); 
                	break;
                case AudioManager.RINGER_MODE_VIBRATE: 
                	 vibrate(mApplication,notif);
    	        	 break;
        }
       
        if (audio.shouldVibrate(AudioManager.VIBRATE_TYPE_RINGER)){
        	 sound(mApplication, notif);
 	    	 vibrate(mApplication,notif);
 	    	 return;
        }
        sound(mApplication, notif);
   }
    
    private void vibrate(HSmsApplication mApplication,Notification notif) {
		//震动
    	HSharedPreferences spf = new HSharedPreferences(mApplication);
    	if(spf.getVibrationSwitch()){
    		 long[] vibrate = new long[] { 1000, 1000, 1000, 1000, 1000 }; 
    		 notif.vibrate = vibrate; 
    		 notif.defaults = Notification.DEFAULT_VIBRATE;
    	}
	}
	
	private void LED(HSmsApplication mApplication,Notification notif) {
		//LED闪光灯
	    notif.ledARGB =Color.YELLOW;  //这里是颜色，我们可以尝试改变，理论上0xFF0000是红色，0x00FF00是绿色
	    notif.ledOnMS = 100; 
	    notif.ledOffMS = 100; 
	    notif.flags = Notification.FLAG_SHOW_LIGHTS;
	}
	
	private  void sound(HSmsApplication mApplication, Notification notif) {
		//声音
	    notif.sound = getUri(mApplication);
	}
	
    public  Uri getUri(HSmsApplication mApplication){
    	Uri sDefautRingUri = RingtoneManager.getActualDefaultRingtoneUri(mApplication, RingtoneManager.TYPE_NOTIFICATION);
        return sDefautRingUri;
    } 
	
	
	/**
	 * 
	 * @return 未读短信数量
	 */
	public int smsNoReadCount(HSmsApplication mApplication){
		Cursor cursor = null;
		int count = 0;
		String selection = "read = 0";
 		cursor = mApplication.getContentResolver().query(Uri.parse("content://sms/"), new String[] { "count(*)" }, selection, null, null);
 		if(cursor == null)
 		{
 			return count;
 		}
 		cursor.moveToFirst();
 		while(!cursor.isAfterLast()){
 			count = cursor.getInt(0);
 			cursor.moveToNext();
 		}
 		cursor.close();
 		return count;
	}
	
	 /***
	 * 获得最近一条未读信息的thread_id 和address
	 * @return
	 */
	public String[] getNearData(HSmsApplication mApplication){
		String []str = new String[2];
		String where = "read=0";
		String order = "_id desc limit 1";
		Cursor cur = null;
		cur = mApplication.getContentResolver().query(Uri.parse("content://sms/"), null, where, null, order);
		cur.moveToFirst();
		while(!cur.isAfterLast()){
			str[0] = cur.getString(cur.getColumnIndex("thread_id"));
			str[1] = cur.getString(cur.getColumnIndex("address"));
			cur.moveToNext();
		}
		cur.close();
		return str;
	}
}
