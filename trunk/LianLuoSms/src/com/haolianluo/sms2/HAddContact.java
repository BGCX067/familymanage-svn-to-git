package com.haolianluo.sms2;

import com.haolianluo.sms2.data.HConst;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

public class HAddContact {
	

	public HAddContact(final Activity context,final String []address){
		if(address.length == 1){
			 insertSysPhone(context,address[0]);
		}else{
			AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			dialog.setTitle(R.string.addcontact).setItems(address, new DialogInterface.OnClickListener(){
				 @Override
				public void onClick(DialogInterface dialog, int which) {
					 insertSysPhone(context,address[which]);
				}
			 }).create().show();
		}
	}
	
	/***
	 * 添加联系人
	 * @param address
	 */
	private void insertSysPhone(Activity context , String address){
		Uri insertUri = android.provider.ContactsContract.Contacts.CONTENT_URI;
		Intent intent = new Intent(Intent.ACTION_INSERT, insertUri);
		intent.putExtra(android.provider.ContactsContract.Intents.Insert.PHONE,address);
		context.startActivityForResult(intent, HConst.REQUEST_CONTACT);
	}
}
