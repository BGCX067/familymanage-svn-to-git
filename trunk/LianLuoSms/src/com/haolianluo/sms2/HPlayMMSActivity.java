package com.haolianluo.sms2;

import com.haolianluo.sms2.model.HSmsManage;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 播放mms界面
 * @author jianhua
 * @author 2011年11月25日17:08:02
 */
public class HPlayMMSActivity extends HActivity{
	
	private int mPosition = -1;
	private HSmsManage mSmsManage;
	private EditText et_input_box;
	private InputMethodManager imm;
	private String mAddress,mName;
	private TextView tv_count;
	private boolean isThread = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_mms);
		mPosition = getIntent().getIntExtra("position", -1);
		mAddress = getIntent().getStringExtra("address");
		mName = getIntent().getStringExtra("name");
		mSmsManage = new HSmsManage(this.getApplication());
		init();
	}
	
	private void init() {
		HSmsManage talkManager = new HSmsManage(this.getApplication());
		ListView listview = (ListView) findViewById(R.id.listview);
		try{
			listview.setAdapter((HPlayMMSAdapter) talkManager.getMMSAdapter(mPosition));
		}catch(Exception ex){
			isThread = true;
		}
		et_input_box = (EditText) findViewById(R.id.et_body);
		tv_count = (TextView)findViewById(R.id.count);
		tv_count.setText(ToolsUtil.getCountString(et_input_box.getText().toString()));
		textChangeBodyView();
		imm = (InputMethodManager) et_input_box.getContext().getSystemService(INPUT_METHOD_SERVICE);
		// 发送短信--------
		final Button bt_send = (Button) findViewById(R.id.bt_send);
		bt_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isLogin();
			}

		});
	}
	
	private void textChangeBodyView() {
		et_input_box.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,int count) {isEnabled(s);}
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {	}
			public void afterTextChanged(Editable s) {}
			private void isEnabled(CharSequence s) {
				if(s.toString().length()>970){
					Toast.makeText(HPlayMMSActivity.this, R.string.textTooLong,Toast.LENGTH_LONG).show();
				}
				tv_count.setText(ToolsUtil.getCountString(s.toString()));
				
			}
		});
	}
	
	
	public void loginAfterSendSms() {
		if(!ToolsUtil.readSIMCard(HPlayMMSActivity.this)){
			 return;
		 }
		final String etbody = et_input_box.getText().toString();
		if(!etbody.equals("")){
		new Thread(){
			public void run(){
				HLog.i("etbody =" + etbody + "mAddress == " + mAddress + "mName ==" + mName);
				mSmsManage.sendSms(etbody,mAddress,mName);
			}
		}.start();
		}
		et_input_box.setText("");
		if(imm.isActive()){
			imm.hideSoftInputFromWindow(et_input_box.getWindowToken(), 0);
		}
		Toast.makeText(HPlayMMSActivity.this, R.string.finishsend,Toast.LENGTH_LONG).show();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			boolean is = getIntent().getBooleanExtra("dialog", false);
			if(is && !isThread){
				finish();
				Intent intent = new Intent();
				int size = mSmsManage.getAdapter().size();
				String threadId = mSmsManage.getAdapter().get(size-1).smsid;
				intent.putExtra("threadId", threadId);
				intent.putExtra("address", mSmsManage.getAdapter().get(size-1).address);
				intent.putExtra("name", mSmsManage.getAdapter().get(size-1).name);
				intent.putExtra("dialog",true);
				intent.setClass(this, HActivityGroup.class);
				startActivity(intent);
				return true;
			}else if(isThread){
				Intent intent = new Intent();
				intent.putExtra("is", "ok");
				intent.setClass(this, HThreadActivity.class);
				startActivity(intent);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
