package com.haolianluo.sms2;

import java.util.List;


import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.data.HSharedPreferences;
import com.haolianluo.sms2.data.HSmsApplication;
import com.haolianluo.sms2.model.HAddressBookManager;
import com.haolianluo.sms2.model.HBufferList;
import com.haolianluo.sms2.model.HResDatabaseHelper;
import com.haolianluo.sms2.model.HResProvider;
import com.haolianluo.sms2.model.HSms;
import com.haolianluo.sms2.model.HSmsManage;
import com.haolianluo.sms2.model.HStatistics;
import com.haolianluo.sms2.model.HThread;
import com.haolianluo.sms2.model.HThreadManager;
import com.lianluo.core.cache.CacheManager;
import com.lianluo.core.skin.SkinManage;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HTalkActivity extends SkinActivity {
	
	private static final String TAG = "HTalkActivity";

	private InputMethodManager imm;
	/** 重发 */
	public boolean isResend = false;
	private HSmsManage mSmsManage = null;
	private String mName = null;
	private EditText et_input_box = null;
	private String mAddress = null;
	private int threadPosition = 0;
	private HThreadManager tm = null;
	private HStatistics mStatistics;
	private HUpdateTools hUpdateTools;
	private HSharedPreferences mHSharedPreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHSharedPreferences = new HSharedPreferences(this);
		mStatistics = new HStatistics(this);
		HConst.markActivity = 3;
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
		hUpdateTools = new HUpdateTools(this);
		mSmsManage = new HSmsManage(getApplication());
		threadPosition = getIntent().getIntExtra("position", -1);
		System.out.println("threadPosition ====" + threadPosition);
		if(getIntent().hasExtra("address")){
			mAddress = getIntent().getStringExtra("address");
			mName = getIntent().getStringExtra("name");
		}else{
			mAddress = mSmsManage.getTalkTitleAddress(threadPosition);
			mName = mSmsManage.getTalkTitleName(threadPosition);//如果没有保存好的名字则得到默认名字
		}
		HLog.i(TAG, "threadPosition---->>>" + threadPosition);
		setContentView(R.layout.htalk);
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.lr_bottom);
		if(HConst.iscollect){
			rl.setVisibility(View.GONE);
		}else{
			rl.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
//		if(!mHSharedPreferences.getIsReadBuffer()){
//			finish();
//			return;
//		}
		tm = new HThreadManager((HSmsApplication)HTalkActivity.this.getApplication());
		if(HPreviewActivity.isSend){
			HPreviewActivity.isSend = false;
			//threadPosition = 0;
			mAddress = mSmsManage.getTalkTitleAddress(threadPosition);
			mName = mSmsManage.getTalkTitleName(threadPosition);//如果没有保存好的名字则得到默认名字
		}
		if(threadPosition == -1){
			threadPosition = tm.getPosition(mAddress);
		}
		init();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		HLog.i(TAG, "onRestart-------------->>>");
		HConst.markActivity = 3;
	}
	
	private void init() {
		
//		tv_name = (TextView) findViewById(R.id.tv_name);
		
		Intent intent = getIntent();
		Uri uri = intent.getData();
		if(uri != null && !"".equals(uri)){//从系统直接进来的
			String threadId = uri.getLastPathSegment();
			HThreadManager tm = new HThreadManager(this.getApplication());
			HThread thread = tm.getThreadForThreadId(threadId);
			mAddress = thread.address;
			mName = thread.name;
		}
		
//		HAddressBookManager abk = new HAddressBookManager(this);
//		mName = abk.getAppendName(mName, mAddress);//如果没有保存好的名字则得到电话号码
//		tv_name.setText(mName);
		
//		iv_head = (ImageView) findViewById(R.id.iv_head);//左上角头像
//		if(threadPosition != -1){
//			Bitmap bp = mSmsManage.getHeadBitmap(threadPosition);
//			if(bp == null){
//				iv_head.setBackgroundResource(R.drawable.def_head_s);
//			}else{
//				iv_head.setImageBitmap(bp);
//			}
//		}else{
//			Bitmap bp = new HAddressBookManager(this).getContactPhoto(mName, mAddress);
//			if(bp == null){
//				iv_head.setBackgroundResource(R.drawable.def_head_s);
//			}else{
//				iv_head.setImageBitmap(bp);
//			}
//		}
//		Button bt_shortcut_key = (Button) findViewById(R.id.bt_shortcut_key);
//		bt_shortcut_key.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mStatistics.add(HStatistics.Z6_2, "", "", "");
//				new HShortcutDialog(HTalkActivity.this); 
//				
//			}
//		});
		/** 短信列表 */
		ListView lv_talk = (ListView) findViewById(R.id.lv_talk_list);
		if(HConst.iscollect){
			lv_talk.setPadding(0, 0, 0, 30);
		}
		lv_talk.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				 if(mSmsManage.getAdapter() == null){
					 return;
				 }
				if(!hUpdateTools.getIsUpdate())
				{
					//et_input_box.setText("");
					if(mSmsManage.isGotoPlayMms(position)){

						Intent intent = new Intent();
						intent.putExtra("position", position);
						intent.putExtra("address", mAddress);
						intent.putExtra("name",mName);
						intent.setClass(HTalkActivity.this,HPlayMMSActivity.class);
						startActivity(intent);

				}else{
					if(mSmsManage.getAdapter().get(position).type.equals("3")){
						startEditActivity(position);
					}else{
//						startPreviewActivity(position);
					}
					
				}
			}else
			{
				Toast.makeText(HTalkActivity.this, getString(R.string.version_toast), Toast.LENGTH_SHORT).show();
			}
			}
		});
		
		//统计短信字数
		final TextView tv_count = (TextView) findViewById(R.id.count);
		tv_count.setText(ToolsUtil.getCountString(""));
		
		imm  = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		//发送按钮
		final Button bt_send = (Button) findViewById(R.id.bt_send);
		bt_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mStatistics.add(HStatistics.Z6_2, "", "", "");
				isLogin();
			}

		});
		
		//写信的编辑框
		et_input_box = (EditText) findViewById(R.id.et_body);
		et_input_box.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				tv_count.setText(ToolsUtil.getCountString(s.toString()));
				if(s.toString().length()>970){
					Toast.makeText(HTalkActivity.this, R.string.textTooLong,Toast.LENGTH_LONG).show();
				}
				if(s == null || s.toString().equals("")){
					bt_send.setEnabled(false);
				}else{
					bt_send.setEnabled(true);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
//		Button bt_call = (Button) findViewById(R.id.bt_call);//右上角呼叫
//		bt_call.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				mStatistics.add(HStatistics.Z6_1, "", "", "");
//				context_call();				
//			}
//		});
		
		
		//registerForContextMenu(lv_talk);
		
		HTalkAdapter talkAdapter = null;
		mSmsManage.clearTalkList();
		System.out.println("-------------------------------------------");
		if(threadPosition == -1){
			talkAdapter =  mSmsManage.loadTalkList(0,mAddress,mName,mSmsManage.getThreadIdForAndress(mAddress.split(",")),lv_talk,null,true);
		}else{
			talkAdapter =  mSmsManage.loadTalkList(threadPosition,null,null,null,lv_talk,null,false);
		}
		lv_talk.setAdapter(talkAdapter);
		talkAdapter.notifyDataSetChanged();
		if(getIntent().getStringExtra("notification") != null){
			HSms.notification_click();
		} else {
			tm.noReadChageRead(tm.getPosition(mAddress));
		}
	}
	
	public void loginAfterSendSms() {
		if(!ToolsUtil.readSIMCard(HTalkActivity.this)){
			 return;
		 }
		 final String editbody = et_input_box.getText().toString();
		if(!editbody.equals("")){
			 new Thread(){
					public void run(){
						mSmsManage.sendSms(editbody,mAddress,mName);
					}
			}.start();
			et_input_box.setText("");
			if(imm.isActive()){
				imm.hideSoftInputFromWindow(et_input_box.getWindowToken(), 0);
			}
		}
	}
	
	
	
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == 1){
//				progressDialog.dismiss();
				et_input_box.setText("");
				if(imm.isActive()){
					imm.hideSoftInputFromWindow(et_input_box.getWindowToken(), 0);
				}
			}
		};
	};
	
//	private void startPreviewActivity(int position) {
//		Intent intent = new Intent();
//		intent.putExtra("talkPosion", position);
//		intent.putExtra("address", mAddress);
//		intent.putExtra("name", mName);
//		intent.setClass(HTalkActivity.this, HPreviewActivity.class);
//		startActivityForResult(intent, 0);
//	}

	private void startEditActivity(int position) {
		Intent intent = new Intent();
		intent.putExtra("position", position);
		intent.putExtra("address", mAddress);
		intent.setClass(HTalkActivity.this, HEditSmsActivity.class);
		startActivityForResult(intent, 0);
	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			if(getIntent().getStringExtra("notification") != null && mSmsManage.getThreadAdapter() != null){//notifition进来的
//				HBufferList updateListDB = new HBufferList(HTalkActivity.this.getApplication());
//				HThreadManager tm = new HThreadManager((HSmsApplication)this.getApplication()); 
//				HSharedPreferences sharedPreferences = new HSharedPreferences((HSmsApplication)this.getApplication());
//				if(sharedPreferences.getIsReadBuffer()){
//					HSms sms = mSmsManage.getAdapter().get(mSmsManage.getAdapter().size()-1); 
//					sms.read = "1";
//					updateListDB.updataBufferList(sms);
//				    tm.noReadChageRead(tm.getPosition(mAddress));
//					tm.ncThread();
//				}else{
//					tm.noReadChageRead(tm.getPosition(mAddress));
//					tm.ncThread();
//				}
//				Intent intent = new Intent();
//				intent.putExtra("is", "ok");
//				intent.setClass(this, HThreadActivity.class);
//				startActivity(intent);
//			}
//			if(getIntent().getBooleanExtra("dialog", false)){//反的进来的
//				HBufferList updateListDB = new HBufferList(HTalkActivity.this.getApplication());
//				HSharedPreferences sharedPreferences = new HSharedPreferences((HSmsApplication)this.getApplication());
//				if(sharedPreferences.getIsReadBuffer() && mSmsManage.getThreadAdapter() == null ){
//					if(mSmsManage.getAdapter().getCount() != 0){
//						HSms sms = mSmsManage.getAdapter().get(mSmsManage.getAdapter().size()-1); 
//						sms.read = "1";
//						sms.threadid = mSmsManage.getThreadIdForAndress(sms.address.split(","));
//						updateListDB.updataBufferList(sms);
//					}
//
//				    //updateListDB.updataDB(sms,(HSmsApplication)this.getApplication());
//				}
//				if(mSmsManage.getThreadAdapter() != null){
//					tm.noReadChageRead(tm.getPosition(mAddress));
//					tm.ncThread();
//				}
//				
//				Intent intent = new Intent();
//				intent.putExtra("back", mAddress);
//				intent.putExtra("is", "ok");
//				intent.putExtra("dialog", true);
//				intent.setClass(this, HThreadActivity.class);
//				startActivity(intent);
//			}
//			String body = et_input_box.getText().toString();
//			if(body != null && !body.equals("")){
//				mSmsManage.addDraft(body, mAddress,mName);
//				Toast.makeText(HTalkActivity.this, R.string.savedraft,Toast.LENGTH_LONG).show();
//			}
//			finish();
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}

//	/** 创建上下文菜单 */
//	@Override
//	public void onCreateContextMenu(ContextMenu contextMenu, View v,ContextMenuInfo contextMenuInfo) {
//		AdapterContextMenuInfo info = (AdapterContextMenuInfo) contextMenuInfo;
//		HSms sms = mSmsManage.getAdapter().getList().get(info.position);
//		HAddressBookManager abk = new HAddressBookManager(this);
//		contextMenu.setHeaderTitle(abk.getAppendName(sms.name, sms.address));
//		if(sms.ismms.equals("0")){
//			contextMenu.add(0, 9, 0, R.string.addCollect);//添加收藏
//		}
//		if (mAddress != null && !mAddress.equals("")) {
//			if(sms.ismms.equals("0")){
//				contextMenu.add(0, 4, 1, R.string.forward);//转发
//			}
//			contextMenu.add(0, 2, 2, R.string.call);//呼叫
//		} 
//		if (sms.type.equals("5") && sms.ismms.equals("0")) {
//			contextMenu.add(0, 3, 3, R.string.resend);// 重发
//		}
//		if(sms.ismms.equals("0")){
//			contextMenu.add(0, 6, 4, R.string.copysms);// 复制短信文本
//		}
//		contextMenu.add(0, 5, 5, R.string.delete);// 删除
//		 List<String> list = null;
//		if(threadPosition == -1){
//			list = mSmsManage.isGetName(mAddress,getIntent().getStringExtra("name"));
//		}else{
//			list = tm.isGetName(threadPosition);
//		}
//		 if (list.size() > 0) {
//			contextMenu.add(0, 8, 6, R.string.addcontact);// 添加通讯录
//		 }
//	}

//	/** 上下文菜单点击事件 */
//	@Override
//	public boolean onContextItemSelected(MenuItem item) {
//		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
//		mPosition = info.position;
//		HSms sms = mSmsManage.getAdapter().getList().get(info.position);
//		switch (item.getItemId()) {
//		case 1:
//			context_edit();
//			break;
//		case 2:
//			mStatistics.add(HStatistics.Z6_6, "", "", "");
//			context_call();
//			break;
//		case 3:
//			context_resend(mPosition);
//			break;
//		case 4:
//			context_forward();
//			break;
//		case 5:
//			context_a_delete(mPosition);
//			break;
//		case 6:
//			context_copytext(sms);
//			break;
//		case 7:
//			context_sms_details(sms);
//			break;
//		case 8:
//			context_add_contact(mPosition);
//			break;
//		case 9:
//			add_collect(sms);
//			break;
//		}
//		return super.onContextItemSelected(item);
//	}

//	/***
//	 * 上下文菜单按钮1，编辑
//	 */
//	private void context_edit() {
//	}

//	/***
//	 * 上下文菜单按钮2，呼叫
//	 */
//	private void context_call() {
//		new HCall(HTalkActivity.this, mAddress,mName);
//	}

//	/***
//	 * 上下文菜单按钮3，重发
//	 */
//	private void context_resend(int position) {
//		mSmsManage.resend(position);
//		Toast.makeText(HTalkActivity.this, R.string.hasSend,Toast.LENGTH_LONG).show();
//	}

//	/***
//	 * 上下文菜单按钮4，转发
//	 */
//	private void context_forward() {
//		 mStatistics.add(HStatistics.Z6_5, "", "", "");
//		 Intent intent = new Intent();
//		 intent.setClass(HTalkActivity.this, HEditSmsActivity.class);
//		 intent.putExtra("position", mPosition);
//		 intent.putExtra("smsbody", mSmsManage.getAdapter().get(mPosition).body);
//		 intent.putExtra("isforward", true);
//		 startActivity(intent);
//	}

//	/***
//	 * 上下文菜单按钮5，删除单条信息
//	 */
//	private void context_a_delete(final int talkPosition) {
//		mStatistics.add(HStatistics.Z6_8, "", "", "");
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setMessage(R.string.isdelete_a_sms);
//		builder.setCancelable(false);
//		builder.setPositiveButton(R.string.confirm,
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//						boolean is = mSmsManage.deleteTalkItem(mAddress, talkPosition);
//						if(mSmsManage.getThreadAdapter() == null){
//							Intent intent = new Intent();
//							intent.putExtra("back", mAddress);
//							intent.putExtra("is", "ok");
//							intent.setClass(HTalkActivity.this, HThreadActivity.class);
//							startActivity(intent);
//						}
//						if(is){
//							returnThreadActivity();						}
//					}
//				});
//		builder.setNegativeButton(R.string.cancel,
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//					}
//				});
//		builder.create().show();
//	}
//
//	/***
//	 * 上下文菜单按钮6， 复制短信文本
//	 */
//	private void context_copytext(HSms sms) {
//		mStatistics.add(HStatistics.Z6_7, "", "", "");
//		ClipboardManager cbm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//		cbm.setText(sms.body);
//	}

//	/***
//	 * 上下文菜单按钮7，查看详情
//	 */
//	private void context_sms_details(HSms sms) {
//		String str = sms.type.equals("1") ? getString(R.string.reciveTime) : getString(R.string.sendTime);
//		String stra = sms.type.equals("1") ? getString(R.string.sender) : getString(R.string.receiver);
//		CharSequence[] items = { getString(R.string.smsType),stra + sms.address, str,ToolsUtil.getCurrentTime(Long.valueOf(sms.time)) };
//		new AlertDialog.Builder(this).setTitle(R.string.sms_details).setItems(items, null)
//				.setPositiveButton(R.string.confirm,new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,int whichButton) {
//							}
//						}).create().show();
//	}

//	/***
//	 * 上下文菜单按钮8，添加联系人
//	 */
//	private void context_add_contact(int position) {
//		mStatistics.add(HStatistics.Z6_9, "", "", "");
//		String []address = null;
//		if(threadPosition == -1){
//			address = mSmsManage.isGetName(mAddress, getIntent().getStringExtra("name")).toArray(new String[]{});
//		}else{
//			address = tm.isGetName(threadPosition).toArray(new String[]{});
//		}
//		new HAddContact(HTalkActivity.this, address);
//	}

	
//	/***
//	 * 上下文菜单添加收藏
//	 * @param model
//	 */
//	private void add_collect(HSms sms){
//		mStatistics.add(HStatistics.Z6_4, "", "", "");
//		HCollectTable collectTable = new HCollectTable(HTalkActivity.this);
//		if(collectTable.querydb(sms.smsid)){
//			Toast.makeText(HTalkActivity.this, getString(R.string.bukechongfushouc), Toast.LENGTH_SHORT).show();
//		}else{
//			collectTable.insertNote(sms);
//		}
//		
//	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case HConst.REQUEST_CONTACT:
			HAddressBookManager abk = new HAddressBookManager(this);
			if(threadPosition == -1){
				mName = abk.getNameByNumber(mAddress);
				Bitmap bp = abk.getContactPhoto(mName, mAddress);
//				iv_head.setImageBitmap(bp);
				HBufferList updateListDB = new HBufferList(HTalkActivity.this.getApplication());
				if(mSmsManage.getThreadAdapter() != null){
					HThreadManager tm = new HThreadManager((HSmsApplication)this.getApplication()); 
					tm.updataNameAndBitmap(mName,bp,tm.getPosition(mAddress));
					tm.ncThread();
				}else{
					boolean is = updateListDB.cxBuffeListIsNull();
					if(!is){
						updateListDB.updataName(mName, mAddress);
					}
				}
			}else{
				tm.updataList(threadPosition);
				tm.ncThread();
				mName = abk.getAppendName(tm.getName(threadPosition), tm.getAddress(threadPosition));//如果没有保存好的名字则得到电话号码
//				iv_head.setImageBitmap(mSmsManage.getHeadBitmap(threadPosition));
			}
//			tv_name.setText(mName);
			break;
		}
		if(resultCode == HConst.PREVIEW_RESULT){
			returnThreadActivity();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void returnThreadActivity(){
		 if(haveThreadActivity()){
		    	setResult(HConst.TALK_RESULT);
				finish();
		    }else{
		    	Intent intent = new Intent();
		    	intent.putExtra("is", "ok");
				intent.setClass(this, HThreadActivity.class);
				startActivity(intent);
		    }
	}
	
	private boolean haveThreadActivity(){
		ActivityManager mActivityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		 List<ActivityManager.RunningTaskInfo> runningTasks = mActivityManager.getRunningTasks(40); 
	        for(ActivityManager.RunningTaskInfo taskInfo:runningTasks){  
	        	if(taskInfo.baseActivity.getClassName().equals("com.haolianluo.sms2.HThreadActivity")){
	        		return true;
	        	}
	        }
	   return false;
	}
}
