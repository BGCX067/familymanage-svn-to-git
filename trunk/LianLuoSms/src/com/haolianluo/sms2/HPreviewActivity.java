package com.haolianluo.sms2;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.data.HSharedPreferences;
import com.haolianluo.sms2.model.HResDatabaseHelper;
import com.haolianluo.sms2.model.HResProvider;
import com.haolianluo.sms2.model.HSmsManage;
import com.haolianluo.sms2.model.HStatistics;
import com.haolianluo.swfp.GLSurfaceView;
import com.haolianluo.swfp.HGLSufaceViewRenderer;
import com.lianluo.core.cache.CacheManager;
import com.lianluo.core.skin.SkinManage;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SkinFrameLayout;
import android.widget.SkinRelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HPreviewActivity extends HActivity {
	
	private static final String TAG = "HPreviewActivity";
	
	private AlertDialog.Builder builder = null;
	private EditText et_input_box;
	private InputMethodManager imm;
	private ProgressDialog progressDialog;
	public static HGLSufaceViewRenderer mGLSufaceViewRenderer;
	private int mTalkPosion = 0;
	private HSmsManage mSmsManage;
	private String mAddress;
	private String mName;
	private int screenW ,screenH;
	private static boolean isActivityFirst = true;
	private SkinFrameLayout skinFrameLayout = null;
	public static boolean isSend = false;
	private HSharedPreferences sp;
	private HStatistics mStatistics;
	private FrameLayout  fl;
	private GLSurfaceView glview;
	private HSharedPreferences mHSharedPreferences;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("HPreviewActivity onCreate----->>>>");
		sp = new HSharedPreferences(HPreviewActivity.this);
		isSend = false;
		HConst.markActivity = 4;
		//初始化缓存
				CacheManager.newInstance().openCache(getApplicationContext());
				//init skin
				if(getContentResolver() != null)
				{
					Cursor cursor = getContentResolver().query(HResProvider.CONTENT_URI_SKIN, null, 
							HResDatabaseHelper.RES_USE + " = '1'", null, null);
					if(cursor.getCount() > 0)
					{
						cursor.moveToNext();
						SkinManage.mCurrentSkin = cursor.getString(cursor.getColumnIndex(HResDatabaseHelper.PACKAGENAME));
						SkinManage.mCurrentFile = cursor.getString(cursor
								.getColumnIndex(HResDatabaseHelper.FILE_NAME));
					}
					else
					{
						SkinManage.mCurrentSkin = HConst.DEFAULT_PACKAGE_NAME;
					}
					cursor.close();
				}
		mTalkPosion =  getIntent().getIntExtra("talkPosion", 0);
		mAddress =  getIntent().getStringExtra("address");
		mName = getIntent().getStringExtra("name");
		mSmsManage = new HSmsManage(this.getApplication());
		mStatistics = new HStatistics(this);
		mHSharedPreferences = new HSharedPreferences(this);
 	}
	
	@Override
	protected void onStart() {
		super.onStart();
//		if(!mHSharedPreferences.getIsReadBuffer()){
//			finish();
//			return;
//		}
		if(builder == null){
			getLayout();
			init();
		}
	}


	private void getLayout() {
		if(skinFrameLayout != null){
			skinFrameLayout.removeAllViews();
		}
		skinFrameLayout = (SkinFrameLayout)SkinRelativeLayout.inflate(this, R.layout.hpreview, null);
		setContentView(skinFrameLayout);
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.lr_bottom);
		if(HConst.iscollect){
			rl.setVisibility(View.GONE);
		}else{
			rl.setVisibility(View.VISIBLE);
		}
	}

	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(isActivityFirst){
			Display dis = getWindowManager().getDefaultDisplay();
			Rect frame = new Rect();
			getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
			int statusBarHeight = frame.top;
			screenW = dis.getWidth();
			screenH = dis.getHeight();
			sp.saveWidth((screenW - fl.getWidth()) / 2);
			sp.saveHeight((screenH - fl.getHeight() - statusBarHeight) / 2);
		}
		isActivityFirst = false;
	
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		fl.setPadding(sp.readWidth(), sp.readHeight(), 0, 0);
		fl.setLayoutParams(lp);
	}
	
	
	private void init(){
		fl = (FrameLayout) findViewById(R.id.fl_flash);
		glview = (GLSurfaceView) findViewById(R.id.play);
	    mSmsManage.loadTalkList(0,mAddress,mName,mSmsManage.getThreadIdForAndress(mAddress.split(",")),null,null,false);
	    
		Intent intent = getIntent();
		boolean is = intent.getBooleanExtra("dialog", false);
		String fileName = ToolsUtil.getFileName(HPreviewActivity.this);
		if(is){
			String str = intent.getStringExtra("body");
			mGLSufaceViewRenderer = new HGLSufaceViewRenderer(this,fileName,ToolsUtil.getTmpStr(str,HPreviewActivity.this)[0]);
		}else{
			if(mTalkPosion != mSmsManage.getAdapter().size()-1){
				mTalkPosion = mSmsManage.getAdapter().size()-1;
			}
			mGLSufaceViewRenderer = new HGLSufaceViewRenderer(this,fileName,ToolsUtil.getTmpStr(mSmsManage.getAdapter().get(mTalkPosion).body,HPreviewActivity.this)[0]);
		}
		glview.setRenderer(mGLSufaceViewRenderer);
		//统计短信字数
		final TextView tv_count = (TextView) findViewById(R.id.count);
		tv_count.setText(ToolsUtil.getCountString(""));
		et_input_box = (EditText) findViewById(R.id.et_body);
		 imm = (InputMethodManager) et_input_box.getContext().getSystemService(INPUT_METHOD_SERVICE);
		//发送短信--------
		final Button bt_send = (Button) findViewById(R.id.bt_send);
		bt_send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mStatistics.add(HStatistics.Z7_2, "", "", "");
				isLogin();
			}

		});
           et_input_box.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				tv_count.setText(ToolsUtil.getCountString(s.toString()));
				if(s.toString().length()>970){
					Toast.makeText(HPreviewActivity.this, R.string.textTooLong,Toast.LENGTH_LONG).show();
				}

			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		hasWindowFocus();
	}
	
	
	public void loginAfterSendSms() {
		if(!ToolsUtil.readSIMCard(HPreviewActivity.this)){
			 return;
		 }
		if(!et_input_box.getText().toString().equals("")){
			isSend = true;
			HConst.markActivity = 4;

		final String etbody = et_input_box.getText().toString();
		new Thread(){
			public void run(){
				mSmsManage.sendSms(etbody,mAddress,mName);
				handler.sendEmptyMessage(1);
			}
		}.start();
		}
		et_input_box.setText("");
		if(imm.isActive()){
			imm.hideSoftInputFromWindow(et_input_box.getWindowToken(), 0);
		}
	}
	
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == 1){
				Toast.makeText(HPreviewActivity.this, R.string.finishsend,Toast.LENGTH_LONG).show();				
				Intent intent = new Intent();
				intent.setAction(HConst.ACTION_UPDATA_TITLE_SMS_NUMBER);
				sendBroadcast(intent);
			}else if(msg.what == 2){
				if(progressDialog != null && progressDialog.isShowing()){
					progressDialog.hide();
					progressDialog.dismiss();
				}
			}
		};
	};
	
	@Override
	protected void onRestart() {
		super.onRestart();
		HLog.i(TAG, "onRestart-------------->>>");
		HConst.markActivity = 4;
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		glview.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		glview.onResume();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
}
