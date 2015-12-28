package com.haolianluo.sms2;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.haolianluo.sms2.data.HConst;
public class HShortcutDialog{
	

	public HShortcutDialog(final Activity activity) {
		
		if(!new HUpdateTools(activity).getIsUpdate())
		{
			Intent intent = new Intent();
			intent.setClass(activity, HResLibActivity.class);
			activity.startActivityForResult(intent, HConst.REQUEST_RESOURCELIB); 
		}else
		{
			Toast.makeText(activity, activity.getString(R.string.version_toast), Toast.LENGTH_SHORT).show();
		}
			

		
//		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//		builder.setTitle(activity.getString(R.string.shortcutMenu));
//		String[] temp = activity.getResources().getStringArray(R.array.shortcutMenuItem);
//		builder.setItems(temp, new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				switch(which){
//				case 0://资源库
//					mStatistics.add(HStatistics.Z3, "", "", "");
//					Intent intent = new Intent();
//					intent.setClass(activity, HResLibActivity.class);
//					activity.startActivityForResult(intent, HConst.REQUEST_RESOURCELIB); 
//					break;
//				case 1://设置
//					mStatistics.add(HStatistics.Z10, "", "", "");
//					Intent intentSet = new Intent();
//					intentSet.setClass(activity, HSettingActivity.class);
//					activity.startActivityForResult(intentSet, HConst.REQUEST_RESOURCELIB); 
//					break;
//				case 2://分享
//					mStatistics.add(HStatistics.Z9, "", "", "");
//					Intent intentShare  = new Intent();
//					intentShare.putExtra("main", "main");
//					intentShare.setClass(activity, HContactActivity.class);
//					activity.startActivity(intentShare);
//					break;
//				case 3://我说两句
//					Intent intentSpeek  = new Intent();
//					intentSpeek.setClass(activity, HProblemActivity.class);
//					activity.startActivity(intentSpeek);
//					break;
//				}
//			}
//		});
//		builder.create().show();
	}
	
}
