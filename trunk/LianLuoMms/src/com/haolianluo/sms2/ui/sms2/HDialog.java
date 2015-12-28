package com.haolianluo.sms2.ui.sms2;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnKeyListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.data.HSharedPreferences;
import com.haolianluo.sms2.model.HAddressBookManager;
import com.haolianluo.sms2.model.HBufferList;
import com.haolianluo.sms2.model.HSms;
import com.haolianluo.sms2.model.HSmsManage;
import com.haolianluo.sms2.model.HStatistics;
import com.haolianluo.sms2.model.HThreadManager;
import com.lianluo.core.util.ToolsUtil;
import com.haolianluo.sms2.MmsApp;
import com.haolianluo.sms2.R;


public class HDialog extends SkinActivity implements OnTouchListener,OnGestureListener{
	
	private Dialog mDialog;
	private static HSmsManage mSmsManage;
	private int index = 0;
	private GestureDetector mGestureDetector;
	private TextView tv_date;
	private TextView tv_body;
	private TextView tv_percent;
	private TextView tv_address;
	private ImageView iv_head;
	private EditText et_editsms;
	private HStatistics mStatistics;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerReceiver(updataUi, new IntentFilter(HConst.ACTION_UPDATA_RECEIVER_DIALOG_UI));
		mSmsManage = new HSmsManage(HDialog.this.getApplication());
		mGestureDetector = new GestureDetector(this);
		//是否要关闭dialog
		HSharedPreferences spf = new HSharedPreferences(this);
		if(!spf.getShortcutSwitch()){
			close();
			return;
		}
		mStatistics = new HStatistics(this);
		mStatistics.add(HStatistics.Z1_1, "", "", "");
		TelephonyManager mTelephonyMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		mTelephonyMgr.listen(new TeleListener(),PhoneStateListener.LISTEN_CALL_STATE);
		HConst.markActivity = 6;
		mDialog = new Dialog(this, R.drawable.dialogsyle);
		mDialog.setContentView(R.layout.receive_dialog);
		tv_date = (TextView) mDialog.findViewById(R.id.tv_date);
		iv_head = (ImageView) mDialog.findViewById(R.id.iv_head);
		tv_body = (TextView)mDialog. findViewById(R.id.tv_body);
		tv_address = (TextView) mDialog.findViewById(R.id.address);
		
		tv_percent = (TextView)mDialog.findViewById(R.id.tv_percent);
		et_editsms = (EditText) mDialog.findViewById(R.id.et_editsms);
		setDialogText();
		tv_body.setOnTouchListener(this);
		tv_body.setLongClickable(true);
		
		//关闭
		Button bt_close = (Button)mDialog.findViewById(R.id.bt_close);
		bt_close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mStatistics.add(HStatistics.Z1_3, "", "", "");
				closeDialog();
				close();
			}
		});
		
		// 发送
		final Button smsButton = (Button) mDialog.findViewById(R.id.backSmsButton);
		smsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 mStatistics.add(HStatistics.Z1_4, "", "", "");
				if(et_editsms.getText().length() <= 0){
					Toast.makeText(HDialog.this,R.string.searchInputContent, Toast.LENGTH_SHORT).show();
				}else{
					closeDialog();
					HSms sms = mSmsManage.getReceiverSmsList().get(index);
					sms.body = et_editsms.getText().toString();
					sms.time = String.valueOf(System.currentTimeMillis());
					sms.type = "2";
					sms.read = "1";
					sms.smsid = null;
					sms.threadid = mSmsManage.getThreadIdForAndress(sms.address.split(","));
					mSmsManage.sendSms(sms);
					close();
				}
			}
		});

			
		
		// 返回
		mDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					dialog.dismiss();
					dialog.cancel();
					close();
					return true;
				}
				return onKeyDown(keyCode, event);
			}
		});
		
		// 呼叫
		Button bt_call = (Button) mDialog.findViewById(R.id.bt_call);
		bt_call.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mStatistics.add(HStatistics.Z1_5, "", "", "");
				String address = mSmsManage.getReceiverSmsList().get(index).address;
				Intent phoneIntent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + address));
				startActivity(phoneIntent);
			}
		});
		
		
		mDialog.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					closeDialog();
					close();
					return true;
				}
				return onKeyDown(keyCode, event);
			}
		});
		
		mDialog.show();

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {
		if(mSmsManage.getReceiverSmsList().size() <= 1){
			return false;
		}
		
		if (e1.getX() - e2.getX() > 80) {
			index = index + 1;
			if(index > mSmsManage.getReceiverSmsList().size()-1){
				index = mSmsManage.getReceiverSmsList().size() - 1;
			}
			setDialogText();
			return true ;
		}else if(e1.getX() - e2.getX() < -120){
			index = index - 1;
			if(index < 0){
				index = 0;
			}
			setDialogText();
			return true ;
		}
		return false;
	}
	
	
	
	private void setDialogText() {
		try {
			HAddressBookManager abm = new HAddressBookManager(this);
			tv_body.setText(mSmsManage.getReceiverSmsList().get(index).body);
			tv_date.setText(ToolsUtil.getCurrentTime_dg(Long.parseLong(mSmsManage.getReceiverSmsList().get(index).time)));
			String address = mSmsManage.getReceiverSmsList().get(index).address;
			String name = mSmsManage.getReceiverSmsList().get(index).name;
			if(name.equals(HConst.defaultName)){
				tv_address.setText(address);
			}else{
				tv_address.setText(name);
			}
			tv_percent.setText((index + 1) + "/"+mSmsManage.getReceiverSmsList().size());
			iv_head.setImageBitmap(abm.getContactPhoto(mSmsManage.getReceiverSmsList().get(index).name, mSmsManage.getReceiverSmsList().get(index).address));
			HThreadManager threadManager = new HThreadManager(this.getApplication());
			HBufferList updateListDB = new HBufferList(HDialog.this.getApplication());
			HSharedPreferences sharedPreferences = new HSharedPreferences((MmsApp)this.getApplication());
			HSms sms = mSmsManage.getReceiverSmsList().get(index); 
			if(threadManager.getThreadAdapter() != null){
				threadManager.noReadToRead_ID(sms.smsid);
				threadManager.ncThread();
			}
			if(sharedPreferences.getIsReadBuffer()){
				sms.read = "1";
			   // updateListDB.updataDB(sms,(HSmsApplication)this.getApplication());//更新缓存
				updateListDB.updataBufferList(sms);
			    threadManager.noReadToRead_ID(sms.smsid);
			}else{
				//暂时取消，在快捷短信左右滑动之后，短信还是未读状态
				//threadManager.noReadToRead_ID(sms.smsid);
			}
		}catch(Exception ex) {
			index = index - 1;
			setDialogText();
		}
	}

	public void close(){
		HConst.isShowAlertAnimation = false;
		HConst.markActivity = -1;
		finish();
	}
	
	@Override
	public void onLongPress(MotionEvent e) {}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		closeDialog();
		close();
		startFlashActivity(index);
		return false;
	}
	

	@Override
	protected void onRestart() {
		HConst.markActivity = 6;
		super.onRestart();
	}
	
	/***
	 * 来短信----
	 */
	
	private void startFlashActivity(int index){
		String address = mSmsManage.getReceiverSmsList().get(index).address;
		String name=  mSmsManage.getReceiverSmsList().get(index).name;
		String threadId = mSmsManage.getThreadIdForAndress(address.split(","));
		String body = mSmsManage.getReceiverSmsList().get(index).body;
		String ismms = mSmsManage.getReceiverSmsList().get(index).ismms;
		mSmsManage.loadTalkList(0,address,name,threadId,null,null,false);
		int posion = getTalkPosion(address);
		Intent intent = new Intent();
		
		intent.setAction(HConst.ACTION_KILL_ONESELF);
		sendBroadcast(intent);
		mStatistics.add(HStatistics.Z1_2, "", "", "");
		if(ismms != null && ismms.equals("1")){
			intent.putExtra("position", posion -1);
			intent.setClass(HDialog.this, HTalkActivity.class);
		}else{
			intent.setClass(HDialog.this, HTalkActivity.class);
		}
		intent.putExtra("talkPosion",posion - 1);
		intent.putExtra("address", address);
		intent.putExtra("name",name);
		intent.putExtra("dialog",true);
		intent.putExtra("body", body);
		startActivity(intent);
	}
	
	
	

	/***
	 * 得到talk的索引值
	 */
	private int getTalkPosion(String address){
		int allNumber = mSmsManage.getAdapter().size();
		int temp = 0;
		if(mSmsManage.getReceiverSmsList().size() == 1){
			return allNumber;
		}
		for(int i = index + 1;i < mSmsManage.getReceiverSmsList().size();i++){
			if(mSmsManage.getReceiverSmsList().get(i).address.equals(address)){
				temp++;
			}
		}
		return allNumber - temp;
	}
	
	
	
	public Handler handler =  new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 0:
				setDialogText();
				break;
			}
		};
	};
	
    /**更新UI的广播*/
    BroadcastReceiver updataUi = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
//			index = index + 1;
			index = mSmsManage.getReceiverSmsList().size();
			handler.sendEmptyMessage(0);
		}
	};
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(updataUi);
		super.onDestroy();
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_SEARCH){
			closeDialog();
			close();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	@Override
	protected void onStop() {
		//来电话和按下home键
		if(!mIsCallRinging){
			closeDialog();
			close();
		}else{
			mIsCallRinging = false;
		}
		super.onStop();
	}
	
	
	private void closeDialog(){
		if(mDialog != null && mDialog.isShowing()){
			mDialog.dismiss();
			mDialog.cancel();
			mDialog = null;
		}
	}
	
	
	private boolean mIsCallRinging = false;//是否来电
	class TeleListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: 
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK: 
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				mIsCallRinging = true;
				break;
			default:
				break;
			}
		}
	}
	
	

}
