package com.haolianluo.sms2;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.model.HUpdateModel;
import com.haolianluo.sms2.model.HUpdateParser;
import com.lianluo.core.event.IEvent;
import com.lianluo.core.task.BaseTask;
import com.lianluo.core.task.TaskManagerFactory;
import com.lianluo.core.util.ToolsUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
/***
 * 检查程序版本是否有新版本
 * @author Administrator
 *
 */
public class HUpdateTools {
	private String p;
	private String url;
	private Context mContext;
	public HUpdateTools(Context context)
	{
		mContext = context;
	}
	
	
	private Handler handler = new Handler(){
		@Override
		public void dispatchMessage(Message msg) {
			SharedPreferences sp = mContext.getSharedPreferences(HConst.UPDATE_FLAG, 0);
			switch(msg.what){
			case 0:
				if(sp.getBoolean(HConst.ISSHOW_NOTIFA, true))
				{	
					Editor editor = sp.edit();
					editor.putString(HConst.P, p);
					editor.putString(HConst.URL, url);
					editor.commit();
					
					NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
					notificationManager.cancel(0);
					Notification notification = new Notification();
					notification.icon = R.drawable.icon;
					notification.defaults = Notification.DEFAULT_SOUND;
					notification.flags = Notification.FLAG_AUTO_CANCEL;
					
					Intent notificationIntent = new Intent(mContext, HUpdateDialog.class);
					notificationIntent.putExtra("showNotification", "show");
					//notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					
					sp.edit().putBoolean(HConst.ISSHOW_NOTIFA, false).commit();
					
					PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
					notification.setLatestEventInfo(mContext, mContext.getString(R.string.havenewTitle), 
							mContext.getString(R.string.normal_update_content), contentIntent);
					notificationManager.notify(0, notification);
				}
				break;
				
			case 1:
				//首次检查出强制更新，点击取消按钮之后，之后就不再提醒
				if(sp.getBoolean(HConst.ISSHOW_FORCEUPDATE, true))
				{	
					Intent intent = new Intent();
					intent.setAction(HConst.ACTION_UPDATE_DIALOG);
					mContext.sendBroadcast(intent);
				}
				break;
			case 2 :
				//Toast.makeText(mContext, mContext.getString(R.string.nonew), Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
	
	/**
	 * 如果是true则需要强制更新
	 * 是false不需要更新
	 * @return flag
	 */
	public boolean getIsUpdate()
	{
		boolean flag = false;
		SharedPreferences sp = mContext.getSharedPreferences(HConst.UPDATE_FLAG, 0);
		flag = sp.getBoolean(HConst.IS_UPDATE, false);
		return flag;
	}
	
	/**
	 * 检查强制更新是否首次出现，如果是首次出现，则不打断用户操作过程，但是预览和商店是不允许
	 * 进入的，等下次进入软件的时候再弹出强制更新提醒框
	 * @return
	 */
	public int getIsFirstUpdate()
	{
		int times = 0;
		SharedPreferences sp = mContext.getSharedPreferences(HConst.UPDATE_FLAG, 0);
		times = sp.getInt(HConst.SHOW_TIMES, 0);
		return times;
	}
	
	public void checkUpdate()
	{
		if(ToolsUtil.checkNet(mContext))
		{
	   			TaskManagerFactory.createParserTaskManager().addTask(new BaseTask(null) {
					@Override
					public void doTask(IEvent event) throws Exception {
						HUpdateModel model = new HUpdateParser(mContext).update();
						SharedPreferences sp = mContext.getSharedPreferences(HConst.UPDATE_FLAG, 0);
						Editor editor = sp.edit();
	   					
						if(model == null)
						{
							Toast.makeText(mContext, R.string.connect_fail, Toast.LENGTH_SHORT).show();
							return;
						}
						else if(model.isUpdate() && model.getSt().equals("1")){
							if(!ToolsUtil.FORCEUPDATE_FLAG)
							{
			   					editor.putBoolean(HConst.UPDATE, true);
			   					editor.commit();
			   					handler.sendEmptyMessage(2);
			   					return;
							}
		   					p = model.getP();
		   					url = model.getUrl();
		   					editor.putBoolean(HConst.UPDATE, true);
		   					editor.putBoolean(HConst.IS_UPDATE, true);	//已经发现有强制更新
		   					editor.putInt(HConst.SHOW_TIMES, 1);		//标识第一次，商店和预览界面无法接入
		   					//缓存更新地址
		   					editor.putString(HConst.P, p);
		   					editor.putString(HConst.URL, url);
		   					editor.commit();
		   					
		   					handler.sendEmptyMessage(1);
		   					
		   				}else if(model.isUpdate() && model.getSt().equals("0"))
		   				{
		   					editor.putBoolean(HConst.UPDATE, true);
		   					editor.commit();
		   					p = model.getP();
		   					url = model.getUrl();
		   					handler.sendEmptyMessage(0);
		   				}else if(!model.isUpdate()){
		   					editor.putBoolean(HConst.UPDATE, true);
		   					editor.commit();
		   					handler.sendEmptyMessage(2);
		   				}
					}
				});
		}else {
				Toast.makeText(mContext, R.string.connect_fail, Toast.LENGTH_SHORT).show();
			}
		
	}
	
	
	public void alertUpdate_Exit()
	{
		mContext.startActivity(new Intent(mContext, HUpdateDialog.class));
	}
}
