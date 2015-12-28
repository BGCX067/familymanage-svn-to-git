package com.haolianluo.sms2.util;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.model.HUpdateModel;
import com.haolianluo.sms2.model.HUpdateParser;
import com.lianluo.core.event.IEvent;
import com.lianluo.core.task.BaseTask;
import com.lianluo.core.task.TaskManagerFactory;
import com.lianluo.core.util.ToolsUtil;
import com.haolianluo.sms2.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
/***
 * 检查程序版本是否有新版本
 * @author Administrator
 *
 */
public class HUpdateTools {
	private String p;
	private String url;
	//private Activity mActivity;
	//private ProgressDialog mDialog;
	private Context mContext;
	public HUpdateTools(Context context)
	{
		//this.mActivity = activity;
		mContext = context;
	}
	
	
	private Handler handler = new Handler(){
		@Override
		public void dispatchMessage(Message msg) {
			switch(msg.what){
			case 0:
				//Toast.makeText(mActivity, R.string.nonew, Toast.LENGTH_SHORT).show();
				break;
			case 1:
//				AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
//				builder.setTitle(R.string.havenewTitle);
//				builder.setMessage(R.string.havenew);
//				builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//						Uri apk = Uri.parse(p + url); 
//						Intent intent = new Intent(Intent.ACTION_VIEW, apk); 
//						mActivity.startActivity(intent);
//					}
//				});
//				builder.setNegativeButton(R.string.calcel, new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//					}
//				});
//				if(!mActivity.isFinishing())
//				{
//					builder.show();
//				}
//				break;
				//设置标志位，下次进入软件的时候提示更新
				SharedPreferences sp = mContext.getSharedPreferences(HConst.UPDATE_FLAG, 0);
				Editor editor = sp.edit();
				editor.putBoolean(HConst.IS_UPDATE, true);
				//editor.putString(HConst.SHOW_TIMES, "0");	//0 首次出现，不进行强制更新，等下次出现的时候进行更新
				editor.putString(HConst.P, p);
				editor.putString(HConst.URL, url);
				editor.commit();
				//Toast.makeText(mContext, "发现强制更新", Toast.LENGTH_SHORT).show();
				
			}
		}
	};
	
	/**
	 * get the update_flag
	 * @return flag
	 */
	public boolean getIsUpdate()
	{
		boolean flag = false;
		SharedPreferences sp = mContext.getSharedPreferences(HConst.UPDATE_FLAG, 0);
		flag = sp.getBoolean(HConst.IS_UPDATE, false);
//		if(sp.getString(HConst.SHOW_TIMES, "0").equals("0"))
//		{
//			flag = false;
//		}
		return flag;
	}
	public void checkUpdate()
	{
		if(ToolsUtil.checkNet(mContext))
		{
			//showDialog();
	   			TaskManagerFactory.createParserTaskManager().addTask(new BaseTask(null) {
					@Override
					public void doTask(IEvent event) throws Exception {
						HUpdateModel model = new HUpdateParser(mContext).update();
						
						//dismissDialog();
		   				if(model != null && model.isUpdate()){
		   					p = model.getP();
		   					url = model.getUrl();
		   					handler.sendEmptyMessage(1);
		   				}else{
		   					handler.sendEmptyMessage(0);
		   				}
					}
				});
		}else {
				//Toast.makeText(mActivity, R.string.connect_fail, Toast.LENGTH_SHORT).show();
			}
		
	}
	
//	protected Dialog onCreateDialog() {
//		mDialog = new ProgressDialog(mActivity);
//			mDialog.setCancelable(false);
//			mDialog.setMessage(mActivity.getString(R.string.checkUpdate));
//			mDialog.setOnKeyListener(new OnKeyListener() {
//				public boolean onKey(DialogInterface dialog, int keyCode,KeyEvent event) {
//					if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_SEARCH) {
//						return true;
//					}
//					return false;
//				}
//			});
//			return mDialog;
//	}
//	
//	private void showDialog()
//	{
//		if(mDialog == null)
//		{
//			mDialog = (ProgressDialog) onCreateDialog();
//		}
//		mDialog.show();
//	}
//	
//	private void dismissDialog()
//	{
//		if(mDialog == null || !mDialog.isShowing())
//		{
//			return;
//		}
//		mDialog.dismiss();
//	}
	
	private void alertDialogForUpdate()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(R.string.havenewTitle);
		builder.setMessage(R.string.havenew);
		builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//get the p and url
				SharedPreferences sp = mContext.getSharedPreferences(HConst.UPDATE_FLAG, 0);
				p = sp.getString(HConst.P, "");
				url = sp.getString(HConst.URL, "");
				
				Uri apk = Uri.parse(p + url); 
				Intent intent = new Intent(Intent.ACTION_VIEW, apk); 
				mContext.startActivity(intent);
			}
		});
		builder.setNegativeButton(R.string.calcel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}
	
	public void alertUpdate_Exit()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(R.string.havenewTitle);
		builder.setMessage(R.string.havenew);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//get the p and url
				SharedPreferences sp = mContext.getSharedPreferences(HConst.UPDATE_FLAG, 0);
				p = sp.getString(HConst.P, "");
				url = sp.getString(HConst.URL, "");
				

				
				Uri apk = Uri.parse(p + url); 
				Intent intent = new Intent(Intent.ACTION_VIEW, apk); 
				mContext.startActivity(intent);
				
				Intent intentK = new Intent();
				intentK.setAction(HConst.ACTION_KILL_ONESELF);
				mContext.sendBroadcast(intentK);
			}
		});
		builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//exit the sms
				Activity activity = (Activity) mContext;
				activity.finish();
				Intent intent = new Intent();
				intent.setAction(HConst.ACTION_KILL_ONESELF);
				mContext.sendBroadcast(intent);
			}
		});
		builder.create();
		builder.show();
	}
}
