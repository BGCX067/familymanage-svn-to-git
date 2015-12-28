package com.haolianluo.sms2.ui.sms2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.model.HSmsChange;
import com.haolianluo.sms2.model.HStatistics;
import com.haolianluo.sms2.model.SmsSendService;
import com.haolianluo.sms2.transaction.PrivilegedSmsReceiver;
import com.haolianluo.sms2.transaction.PushReceiver;
import com.lianluo.core.net.download.DLManager;
import com.lianluo.core.stats.Statistics;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;

/**
 * 
 * @author jianhua 2011年11月19日14:09:38
 *
 */
public class HService extends Service{
	
	public static boolean htread_check_flag = false;
	public static HashMap<Long, Boolean> htread_map = new HashMap<Long, Boolean>();
	
	private HSmsChange mSmsChange = null;
	private Rc rc;
	private Context mContext;
	private static final String TAG = "HService";
	private DLManager mDLManager;
	private PrivilegedSmsReceiver mSmsReceiver;
	private PushReceiver mMmsReceiver;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		mContext = this;
		//scanSkin();
		//initSkin();
		mSmsChange = new HSmsChange(new Handler(), this.getApplication());
		getContentResolver().registerContentObserver(Uri.parse("content://mms-sms/conversations?simple=true"), true,mSmsChange);
		
		IntentFilter smsIntentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		smsIntentFilter.setPriority(2147483647);
		mSmsReceiver = new PrivilegedSmsReceiver();
        registerReceiver(mSmsReceiver, smsIntentFilter);
        
        IntentFilter mmsIntentFilter = new IntentFilter("android.provider.Telephony.WAP_PUSH_RECEIVED");
        mmsIntentFilter.setPriority(2147483647);
        mMmsReceiver = new PushReceiver();
        registerReceiver(mMmsReceiver, mmsIntentFilter);
        
        IntentFilter ift = new IntentFilter();
        ift.addAction(Intent.ACTION_TIME_TICK);
        rc = new Rc();
        registerReceiver(rc, ift); 
        
        SharedPreferences pref = getSharedPreferences(HConst.PREF_USER, Context.MODE_PRIVATE);
        boolean loginFlag = pref.getBoolean(HConst.USER_KEY_LOGIN, false);
        if(loginFlag) {
        	//初始化数据流
			Intent service = new Intent();
	        service.setClass(HService.this, SmsSendService.class);
	        startService(service);
        }
        mDLManager = DLManager.getInstance(mContext);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);  
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);  
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);  
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        registerReceiver(mSDcardReceiver, filter);
		super.onCreate();
	}
	
	private BroadcastReceiver mSDcardReceiver = new  BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(Intent.ACTION_MEDIA_BAD_REMOVAL)
					|| intent.getAction().equals(Intent.ACTION_MEDIA_REMOVED)
					|| intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED))
			{
				mDLManager.stopAllDownload();
			}
			else if(intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED))
			{
				if(!ToolsUtil.checkSDcard())
				{
					return;
				}
				mDLManager.addTask(null);
			}
		}};
	
	@Override
	public void onDestroy() {
		getContentResolver().unregisterContentObserver(mSmsChange);
//		unregisterReceiver(mSMSReceiver);
		unregisterReceiver(rc);
//		unregisterReceiver(mSkinReceiver);
		unregisterReceiver(mSDcardReceiver);
		//SharedPreferences pref = getSharedPreferences(HConst.PREF_USER, Context.MODE_PRIVATE);
		//pref.edit().putBoolean(HConst.USER_KEY_LOGIN, false).commit();
		//pref.edit().putString(HConst.USER_KEY_PHONE, "").commit();
		
        unregisterReceiver(mSmsReceiver);
        unregisterReceiver(mMmsReceiver);
		
		
		super.onDestroy();
	}
	
	
	public static int dayForWeek()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int x = calendar.get(Calendar.DAY_OF_WEEK);
		return x;
	} 
	
	public static class Rc extends BroadcastReceiver {
		private HStatistics hss;
		@Override
		public void onReceive(Context context, Intent intent) {
			hss = new HStatistics(context);
			if(intent.getAction().toString().equals(Intent.ACTION_TIME_TICK)){
				final Context con = context;
				Date now = new Date(); 
				//String s = DateFormat.getTimeFormat(context).format(now);
				SimpleDateFormat myFmt2=new SimpleDateFormat("HH:mm");
				String s = myFmt2.format(now);
					if(s.equals("10:00")){
						new Thread(){
							@Override
							public void run() {
								try {
										int result = Statistics.sendStats(con);
										if(result == Statistics.RecodeOK)
										{
											hss.deleteOld();
										}
									//if(po.getB().equals("RecodeOK") || po.getB().equals("RecodeFailture")){
									//	hss.deleteOld();
									//}
								} catch (Exception e) {
									e.printStackTrace();
								}
								super.run();
							}
						}.start();
					}
					
					/**每周一进行更新*/
   					SharedPreferences sp = con.getSharedPreferences(HConst.UPDATE_FLAG, 0);
   					boolean is_update = sp.getBoolean(HConst.UPDATE, false);
   					
   					HLog.d(TAG, "Date: " + String.valueOf(dayForWeek()) + ", UpdateFlag: " + String.valueOf(is_update));
   					
					if(dayForWeek() == 2 && !is_update)
					{
						new Thread(){
							@Override
							public void run() {
								Looper.prepare();
								new HUpdateTools(con).checkUpdate();
								Looper.loop();
							}
						}.start();
					}
					/*
					final Handler handler = new Handler();
					if(s.substring(s.lastIndexOf(":") + 1).equalsIgnoreCase("00"))
					{
						final String skin_id = SkinBussiness.selectID(HConst.TYPE_SKIN);
						if(skin_id != null)
						{
							new Thread()
							{
								@Override
								public void run()
								{
									try 
									{
										int result = Statistics.sendCharge(con, skin_id);
										switch(result)
										{
											case Statistics.Paid:
											{
												SkinBussiness.setPaid(HConst.TYPE_SKIN, skin_id);
											}
											break;
											case Statistics.NoPaid:
											{
												handler.post(new Runnable()
												{
													@Override
													public void run() 
													{
														// TODO Auto-generated method stub
														try 
														{
															Intent i = new Intent(HSkinReceiver.ACTION_CHARGE);
															String packagename = SkinBussiness.selectPackageName(HConst.TYPE_SKIN);
															i.putExtra("packageName", packagename);
															i.putExtra("redId", skin_id);
															i.putExtra("sdkLevel", ToolsUtil.getSDKLevel(con));
															i.putExtra("channelNum", ToolsUtil.getChannelNum(con));
															i.putExtra("version", ToolsUtil.getVersion(con));
															con.sendBroadcast(i);
														} catch (Exception e)
														{
															e.printStackTrace();
														}
													}
												});
											}
											break;
											case Statistics.Free:
											{
												SkinBussiness.setPaid(HConst.TYPE_SKIN, skin_id);
											}
											break;
										}
									} catch (Exception e) 
									{
										e.printStackTrace();
									}
									super.run();
								}
							}.start();
						}
						final String anim_id = SkinBussiness.selectID(HConst.TYPE_ANIMATION);
						if(anim_id != null)
						{
							new Thread()
							{
								@Override
								public void run() 
								{
									try 
									{
										int result = Statistics.sendCharge(con, anim_id);
										switch(result)
										{
											case Statistics.Paid:
											{
												SkinBussiness.setPaid(HConst.TYPE_ANIMATION, anim_id);
											}
											break;
											case Statistics.NoPaid:
											{
												handler.post(new Runnable()
												{

													@Override
													public void run() 
													{
														// TODO Auto-generated method stub
														try 
														{
															Intent i = new Intent(HSkinReceiver.ACTION_CHARGE);
															String packagename = SkinBussiness.selectPackageName(HConst.TYPE_ANIMATION);
															i.putExtra("packageName", packagename);
															i.putExtra("redId", anim_id);
															i.putExtra("channelNum", ToolsUtil.getChannelNum(con));
															i.putExtra("sdkLevel", ToolsUtil.getSDKLevel(con));
															i.putExtra("version", ToolsUtil.getVersion(con));
															con.sendBroadcast(i);
														} catch (Exception e)
														{
															e.printStackTrace();
														}
													}
												});
											}
											break;
											case Statistics.Free:
											{
												SkinBussiness.setPaid(HConst.TYPE_ANIMATION, anim_id);
											}
											break;
										}
									} catch (Exception e) 
									{
										e.printStackTrace();
									}
									super.run();
								}
							}.start();
						}
					}*/
			}
		}

	}

}
