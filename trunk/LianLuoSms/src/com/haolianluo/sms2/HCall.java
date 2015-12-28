package com.haolianluo.sms2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;


public class HCall {

	/***
	 * 呼叫
	 * @param context
	 * @param address
	 */
	public HCall(final Context context ,String address,String name) {
		final CharSequence[] groupNumber = address.split(",");
		final CharSequence[] groupName = name.split(",");
		if(groupNumber.length == 1){
			 Intent phoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + groupNumber[0]));
			 context.startActivity(phoneIntent);
		}else{
			AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			dialog.setTitle(R.string.call);
			dialog.setItems(groupName, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				    Intent phoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + groupNumber[which]));
					context.startActivity(phoneIntent);
				}}).create().show();
		}
	}

}
