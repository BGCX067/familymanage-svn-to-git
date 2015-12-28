package com.haolianluo.sms2.ui.sms2;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.widget.Toast;

import com.haolianluo.sms2.R;
import com.haolianluo.sms2.data.HConst;
import com.lianluo.core.util.ToolsUtil;

public class HAddContact {
	

	public HAddContact(final Activity context,final String []address){
		//添加去掉通讯录重复记录功能
		int length = address.length;
		ArrayList<String> result = new ArrayList<String>();
		for(int i = 0; i < length; i++) {
			String number = address[i];
			boolean flag = ToolsUtil.isContact(context, number);
			if(!flag) {
				result.add(number);
			}
		}
		int size = result.size();
		final String[] dialog_item = new String[size];
		for(int i = 0; i < size; i++) {
			dialog_item[i] = result.get(i);
		}
		
		if(size == 0) {
			Toast.makeText(context, context.getResources().getString(R.string.addcontact_exist), Toast.LENGTH_SHORT).show();
		} else {
			if(dialog_item.length == 1){
				 insertSysPhone(context,dialog_item[0]);
			}else{
				AlertDialog.Builder dialog = new AlertDialog.Builder(context);
				dialog.setTitle(R.string.addcontact).setItems(dialog_item, new DialogInterface.OnClickListener(){
					 @Override
					public void onClick(DialogInterface dialog, int which) {
						 insertSysPhone(context,dialog_item[which]);
					}
				 }).create().show();
			}
		}
	}
	
	/***
	 * 添加联系人
	 * @param address
	 */
	private void insertSysPhone(Activity context , String address){
//		Uri insertUri = android.provider.ContactsContract.Contacts.CONTENT_URI;
//		Intent intent = new Intent(Intent.ACTION_INSERT, insertUri);
//		intent.putExtra(android.provider.ContactsContract.Intents.Insert.PHONE,address);
//		context.startActivityForResult(intent, HConst.REQUEST_CONTACT);
		
		Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
        intent.setType(Contacts.CONTENT_ITEM_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, address);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        context.startActivityForResult(intent, HConst.REQUEST_CONTACT);
	}
}
