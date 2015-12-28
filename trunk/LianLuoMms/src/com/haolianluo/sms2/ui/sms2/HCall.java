package com.haolianluo.sms2.ui.sms2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.haolianluo.sms2.R;


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
			 Intent phoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + groupName[0]));
			 context.startActivity(phoneIntent);
		}else{
			AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			dialog.setTitle(R.string.call);
			dialog.setItems(groupNumber, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				    Intent phoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + groupName[which]));
					context.startActivity(phoneIntent);
				}}).create().show();
		}
	}

}
