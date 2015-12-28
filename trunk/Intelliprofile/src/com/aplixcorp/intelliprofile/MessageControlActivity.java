package com.aplixcorp.intelliprofile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MessageControlActivity extends Activity
{
	public static final String FORWARDCALL = "tocall";
	public static final String CANCELFORWARD = "tocall";
	public static final String FORWARNDSMS = "sendto";
	public static final String CANCELFORWARDSMS = "tocall";

	public static final String LOCK = "LOCK";
	public static final String UNLOCK = "UNLOCK";
	public static final String POWEROFF = "POWEROFF";

	public static final String KEYWORDS_FORWARD_CALL = "key_words_forward_call";
	public static final String KEYWORDS_FORWARD_SMS = "key_words_forward_SMS";
	public static final String SMS_NOTIFICATION_RECEIVER_NUMBER = "sms_notification_receiver_number";
	public static final String SIM_CARD_IMSI = "SIM_CARD_IMSI";

	private Button mSetForwardCall;
	private Button mResetForwardCall;
	private Button mSetForwardSMS;
	private Button mResetForwardSMS;
	private Button mSetReceivingPhoneNumber;
	private Button mResetRecevingPhoneNumber;

	private final static int SET_FORWARD_CALL_DIALOG_ID = 1;
	private final static int RESET_FORWARD_CALL_DIALOG_ID = 2;
	private final static int SET_FORWARD_SMS_DIALOG_ID = 3;
	private final static int RESET_FORWARD_SMS_DIALOG_ID = 4;
	private final static int SET_RECEIVING_PHONE_NUMBER_DIALOG_ID = 5;
	
	private final static int MESSAGE_SET_FORWARD_CALL_SUCCESSFULLY = 0x1000;
	private final static int MESSAGE_SET_FORWARD_SMS_SUCCESSFULLY = 0x1001;
	private final static int MESSAGE_SET_NOTIFICATION_RECEIVER_SUCCESSFULLY = 0x1010; 

	private Context mContext;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_message_control);
		mContext = this;
		
                mSetForwardCall = (Button)findViewById(R.id.set_forward_call);
                mResetForwardCall = (Button)findViewById(R.id.reset_forward_call);
                mSetForwardSMS = (Button)findViewById(R.id.set_forward_SMS);
                mResetForwardSMS = (Button)findViewById(R.id.reset_forward_SMS);
		mSetReceivingPhoneNumber = (Button)findViewById(R.id.set_notification_number);
                mResetRecevingPhoneNumber = (Button)findViewById(R.id.reset_notification_number);
				
                mSetForwardCall.setOnClickListener(new OnClickListener(){
            @Override                	
    		public void onClick(View v) {
    			showDialog(SET_FORWARD_CALL_DIALOG_ID);
    		}});	

                mResetForwardCall.setOnClickListener(new OnClickListener(){
    		@Override
    		public void onClick(View v) {
		        SharedPreferences prefs = mContext.getSharedPreferences("ContextAware", Activity.MODE_PRIVATE);
			prefs.edit().putString(KEYWORDS_FORWARD_CALL, "").commit();
			Toast.makeText(mContext, getString(R.string.reset_forward_call_ok), Toast.LENGTH_SHORT).show();
		        return;
    		}});	
			
                mSetForwardSMS.setOnClickListener(new OnClickListener(){
    		@Override
    		public void onClick(View v) {
    			showDialog(SET_FORWARD_SMS_DIALOG_ID);
    		}});	
			
                mResetForwardSMS.setOnClickListener(new OnClickListener(){
    		@Override
    		public void onClick(View v) {
		        SharedPreferences prefs = mContext.getSharedPreferences("ContextAware", Activity.MODE_PRIVATE);
			prefs.edit().putString(KEYWORDS_FORWARD_SMS, "").commit();
			Toast.makeText(mContext, getString(R.string.reset_forward_sms_ok), Toast.LENGTH_SHORT).show();		
		        return;
    		}});	

                mSetReceivingPhoneNumber.setOnClickListener(new OnClickListener(){
    		@Override
    		public void onClick(View v) {
    			showDialog(SET_RECEIVING_PHONE_NUMBER_DIALOG_ID);
    		}});
			
		mResetRecevingPhoneNumber.setOnClickListener(new OnClickListener(){
    		@Override
    		public void onClick(View v) {
			SharedPreferences prefs = mContext.getSharedPreferences("ContextAware", Activity.MODE_PRIVATE);
			prefs.edit().putString(SMS_NOTIFICATION_RECEIVER_NUMBER, "").commit();
			Toast.makeText(mContext, getString(R.string.reset_forward_sms_ok), Toast.LENGTH_SHORT).show();		

    		}});
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
    protected Dialog onCreateDialog(int id)
    {
    	switch(id)
    	{
    		case SET_FORWARD_CALL_DIALOG_ID:
    		{
    			final EditText text = new EditText(mContext);
    			text.setText(R.string.first_key_forward_call);
    			return new AlertDialog.Builder(mContext)
    			.setTitle(getString(R.string.set_forward_call_title))
    			.setMessage(getString(R.string.set_forward_call_key_words))    			
    			.setView(text)
				.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					String forwardCallKey = text.getEditableText().toString();
				        SharedPreferences prefs = mContext.getSharedPreferences("ContextAware", Activity.MODE_PRIVATE);
					prefs.edit().putString(KEYWORDS_FORWARD_CALL, forwardCallKey).commit();
				    mHandler.sendEmptyMessage(MESSAGE_SET_FORWARD_CALL_SUCCESSFULLY);
				}})
				.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				}})		
    			.create();
    		}
		case SET_FORWARD_SMS_DIALOG_ID:
    		{
    			final EditText eb = new EditText(mContext);
    			eb.setText(R.string.first_key_forward_SMS);    			
    			return new AlertDialog.Builder(mContext)
    			.setTitle(getString(R.string.set_forward_sms_title))
    			.setMessage(getString(R.string.set_forward_sms_key_words))    			
    			.setView(eb)
				.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub			
					String forwardSMSKey = eb.getEditableText().toString();
				        SharedPreferences prefs = mContext.getSharedPreferences("ContextAware", Activity.MODE_PRIVATE);
					prefs.edit().putString(KEYWORDS_FORWARD_SMS, forwardSMSKey).commit();
					mHandler.sendEmptyMessage(MESSAGE_SET_FORWARD_SMS_SUCCESSFULLY);
				}})
				.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				}})		
    			.create();
    		}
		case SET_RECEIVING_PHONE_NUMBER_DIALOG_ID:
		{
    			final EditText eb = new EditText(mContext);
    			eb.setText("");    			
    			return new AlertDialog.Builder(mContext)
    			.setTitle(getString(R.string.set_number_title))
    			.setMessage(getString(R.string.Set_receiver_Number_message))
    			.setView(eb)
			.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub			
					String receiverNum = eb.getEditableText().toString();
				        SharedPreferences prefs = mContext.getSharedPreferences("ContextAware", Activity.MODE_PRIVATE);
					prefs.edit().putString(SMS_NOTIFICATION_RECEIVER_NUMBER, receiverNum).commit();

				        //save current IMSI
				        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
					String imsi = tm.getSubscriberId();
					if (null == imsi){
					     imsi = " ";
					}
					prefs.edit().putString(SIM_CARD_IMSI, imsi).commit();
					Log.d("MessageControlActivity", "Succeed to set notfication receiver");
					mHandler.sendEmptyMessage(MESSAGE_SET_NOTIFICATION_RECEIVER_SUCCESSFULLY);
				}})
				.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				}})		
    			.create();
				
		}
		default:
			return null;
    	}
    }

    public Handler mHandler = new Handler() {
    	@Override
        public void handleMessage(Message msg) {
    		Log.d("MessageControlActivity", "msg.what =" + msg.what);
    		switch(msg.what){
    		case MESSAGE_SET_FORWARD_CALL_SUCCESSFULLY:
    			Log.d("MessageControlActivity", "set forward call");
    			Toast.makeText(mContext, getString(R.string.succeed_to_set_forward_call), Toast.LENGTH_SHORT).show();    			
    			break;
    		case MESSAGE_SET_FORWARD_SMS_SUCCESSFULLY:
    			Log.d("MessageControlActivity", "set forward SMS");    			
    			Toast.makeText(mContext, getString(R.string.succeed_to_set_forward_sms), Toast.LENGTH_SHORT).show();  			
    			break;
    		case MESSAGE_SET_NOTIFICATION_RECEIVER_SUCCESSFULLY:
    			Log.d("MessageControlActivity", "set notification receiver");
    			Toast.makeText(mContext, getString(R.string.succeed_to_set_nofication_receiver), Toast.LENGTH_SHORT).show();
				
    			break;
    		default:
    			break;
    		}
    	}
    };
}
