package com.haolianluo.sms2;

import java.util.List;
import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.data.HSharedPreferences;
import com.haolianluo.sms2.data.HSmsApplication;
import com.haolianluo.sms2.model.HAddressBookManager;
import com.haolianluo.sms2.model.HBufferList;
import com.haolianluo.sms2.model.HCollectTable;
import com.haolianluo.sms2.model.HSms;
import com.haolianluo.sms2.model.HSmsManage;
import com.haolianluo.sms2.model.HStatistics;
import com.haolianluo.sms2.model.HThreadManager;
import com.haolianluo.swfp.GLSurfaceView;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;

import android.app.ActivityGroup;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;


public class HActivityGroup extends ActivityGroup implements OnTouchListener,OnGestureListener,OnClickListener {
	
	private static final String TAG = "HActivityGroup";
	
	private HStatistics mStatistics;
	private FrameLayout mLinearLayout;
	private HSharedPreferences mHSharedPreferences;
	private int mThreadPosition = -1;//主界面过来的索引值
	private HThreadManager mThreadManager = null;
	private HSmsManage mSmsManage = null;
	private HAddressBookManager mAddressBookManager = null;
	private GestureDetector mGestureDetector;
	private int mCurIndex = 0;//当前读取Talk的索引值
	private int mTotalNumber = 0;//当前talk的总条数
	private int mPartIndex = 0;
	private int mDraftAndMms = -1;//0 --- 草稿     1----彩信
	private String mName,mAddress;
	private boolean mIsDialog;
	
	private TextView mNameTextView;
	private ImageView mHeadImageView;
	private TextView mProgessTextView;
	private GLSurfaceView mGlSurfaceView;
	private ImageView mPlaydraftMmsImageView;
	private Button mPlaydraftMmsButton;
	private RelativeLayout mRelativeLayout;
	private TextView mPlayDraftAndMmsTextView;
	private Button mChageReadSms;
	private EditText mEditText;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getIntent().getStringExtra("notification") != null){
			HConst.iscollect = false;
		}
		HLog.i(TAG, "onCreate ---------------->>>");
		registerReceiver(br, new IntentFilter(HConst.ACTION_UPDATA_TITLE_SMS_NUMBER));
		getInstance();
		mThreadPosition = getIntent().getIntExtra("position", -1);
		System.out.println("mThreadPosition -------------" + mThreadPosition);
		if(mThreadPosition == -1){
			mName = getIntent().getStringExtra("name");
			mAddress = getIntent().getStringExtra("address");
			mThreadPosition = mThreadManager.getPosition(mAddress);
		}
		
		if(mGlSurfaceView != null){
			mGlSurfaceView.setLongClickable(true);
			mGlSurfaceView.setOnTouchListener(this);
			registerForContextMenu(mGlSurfaceView);
		}
	}
	
	
	/***
	 * 设置名字
	 */
	private void setName(){
		mNameTextView = (TextView) findViewById(R.id.tv_name);
		if(mThreadPosition != -1){
			mAddress =  mSmsManage.getTalkTitleAddress(mThreadPosition);
			mName = mAddressBookManager.getAppendName(mSmsManage.getTalkTitleName(mThreadPosition), mAddress);
		}
		mNameTextView.setText(mName);
	}
	
	/***
	 * 设置头像
	 */
	private void setHead(){
		mHeadImageView = (ImageView) findViewById(R.id.iv_head);
		
		if(mThreadPosition != -1){
			Bitmap bp = mSmsManage.getHeadBitmap(mThreadPosition);
			if(bp == null){
				mHeadImageView.setBackgroundResource(R.drawable.def_head_s);
			}else{
				mHeadImageView.setImageBitmap(bp);
			}
		}else{
			Bitmap bp = new HAddressBookManager(this).getContactPhoto(mName, mAddress);
			if(bp == null){
				mHeadImageView.setBackgroundResource(R.drawable.def_head_s);
			}else{
				mHeadImageView.setImageBitmap(bp);
			}
		}
	}
	
	private void setmProgessTextView(int count){
		mTotalNumber = mSmsManage.getAdapter().size();
		//TODO 暂时这样修改
		//if(mProgessTextView == null){
			mProgessTextView = (TextView) findViewById(R.id.tv_percent);
		//}
		int temp = mHSharedPreferences.getReadSmsType();
		if(temp == 0){
			mProgessTextView.setVisibility(View.VISIBLE);
			if(count != 0){
				mProgessTextView.setText((mCurIndex + 1) + "(" + (count + 1) + ")" +"/" + mTotalNumber);
			}else{
				mProgessTextView.setText((mCurIndex + 1) + "/" + mTotalNumber);
			}
		}else{
			mProgessTextView.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 获取实例
	 */
	private void getInstance(){
		mHSharedPreferences = new HSharedPreferences(this);
		mThreadManager = new HThreadManager(HActivityGroup.this.getApplication());
		mSmsManage = new HSmsManage(this.getApplication());
		mAddressBookManager = new HAddressBookManager(this);
		mGestureDetector = new GestureDetector(this);
		mStatistics = new HStatistics(this);
	}
	
	/***
	 * 改成已读
	 * @param position
	 */
	private void chageAlreadyRead(int position){
		if(position == -1){
			return;
		}
		mThreadManager.noReadChageRead(position);
	}
	
	private void selectActivity(){


		if(!HConst.iscollect){
			mThreadPosition = mThreadManager.getPosition(mAddress);
		}else{
			mThreadPosition = mThreadManager.getCollectPosition(mAddress);
		}
		String editCaoGao = "";
		if(mEditText != null)
		{
			editCaoGao = mEditText.getEditableText().toString().trim();
		}
		
//		if(mLinearLayout == null){
		mLinearLayout = null;
		mLinearLayout = (FrameLayout) findViewById(R.id.linearLayout);

//		mLinearLayout.removeAllViews();
		String address = null,name = null;
		mIsDialog = getIntent().getBooleanExtra("dialog", false);
		HLog.i(TAG, "mIsDialog ----->>>" + mIsDialog);
		if(mIsDialog){
			address = getIntent().getStringExtra("address");
			name = getIntent().getStringExtra("name");
			mCurIndex = getIntent().getIntExtra("talkPosion",0);
		}
		else if(getIntent().getStringExtra("notification") != null)
		{
			address = getIntent().getStringExtra("address");
			name = getIntent().getStringExtra("name");
		}
		else
		{
		if(!HConst.iscollect){
			address = mThreadManager.getThreadAdapter().get(mThreadPosition).address;
			name = mThreadManager.getThreadAdapter().get(mThreadPosition).name;
		}else{
			address = mThreadManager.getCollectAdapter().get(mThreadPosition).address;
			name = mThreadManager.getCollectAdapter().get(mThreadPosition).name;
		}
		}
		
		if(mHSharedPreferences.getReadSmsType() == 0){
			mLinearLayout.removeAllViews();
			mChageReadSms.setBackgroundResource(R.drawable.bt_chage_readsms_falsh_xml);
			Intent intent = new Intent();
			intent.putExtra("address", address);
			intent.putExtra("name",name);
			intent.putExtra("talkPosion", mCurIndex);
			View view = getLocalActivityManager().startActivity("flash",intent.setClass(HActivityGroup.this, HPreviewActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
			mGlSurfaceView = (GLSurfaceView) view.findViewById(R.id.play);
			mPlaydraftMmsImageView = (ImageView) view.findViewById(R.id.playDraftAndMmsImageView);
			mPlaydraftMmsButton = (Button) view.findViewById(R.id.playDraftAndMmsButton);
			mRelativeLayout = (RelativeLayout) view.findViewById(R.id.playDraftAndMms);
			mPlayDraftAndMmsTextView = (TextView) view.findViewById(R.id.playDraftAndMmsTextView);
			mEditText = (EditText)view.findViewById(R.id.et_body);
			if(!editCaoGao.equals("") || editCaoGao != null){
				mEditText.setText(editCaoGao);
			}
			mLinearLayout.addView(view);
			mCurIndex =  mSmsManage.getAdapter().size() - 1;
			String ismmsType = mSmsManage.getAdapter().get(mCurIndex).ismms;//1 就是彩信
			String type = mSmsManage.getAdapter().get(mCurIndex).type;
			if(ismmsType.equals("1")){
				mDraftAndMms = 1;
				showPlayMmsAndDraft(1);
			}else if(type.equals("3")){
				mDraftAndMms = 0;
				showPlayMmsAndDraft(0);
			}
			mGlSurfaceView.setLongClickable(true);
			mGlSurfaceView.setOnTouchListener(this);
			mPlaydraftMmsImageView.setLongClickable(true);
			mPlaydraftMmsImageView.setOnTouchListener(this);
			setmProgessTextView(0);
			mPlaydraftMmsButton.setOnClickListener(this);
		}else if(mHSharedPreferences.getReadSmsType() == 1){
			mChageReadSms.setBackgroundResource(R.drawable.bt_chage_readsms_talk_xml);
			//if(mThreadPosition == -1){
			//	mThreadPosition = -1;
			//}
			Intent intent = new Intent();
			intent.putExtra("name", mName);
			intent.putExtra("address", mAddress);
			intent.putExtra("position", mThreadPosition);
			System.out.println("mThreadPosition -----=====>>>" + mThreadPosition);
			View view = getLocalActivityManager().startActivity("talk",intent.setClass(HActivityGroup.this, HTalkActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
			mEditText = (EditText)view.findViewById(R.id.et_body);
			mEditText.setText(editCaoGao);
			mLinearLayout.addView(view);
			setmProgessTextView(0);
			ListView listView = (ListView) view.findViewById(R.id.lv_talk_list);
			registerForContextMenu(listView);
		}
	}
	
	/**
	 * 更改读取信息的方式
	 */
	private void selectReadSmsType(){
		Button button = (Button) findViewById(R.id.bt_shortcut_key);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mHSharedPreferences.getReadSmsType() == 0){
					mStatistics.add(HStatistics.Z7_1, "", "", "");
				}else if(mHSharedPreferences.getReadSmsType() == 1){
					mStatistics.add(HStatistics.Z6_1, "", "", "");
				}
				new HShortcutDialog(HActivityGroup.this); 
			}
		});
		mChageReadSms.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int temp = mHSharedPreferences.getReadSmsType();
				new AlertDialog.Builder(HActivityGroup.this).setSingleChoiceItems(R.array.selectReadSmsType, temp, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mHSharedPreferences.setReadSmsType(which);
						dialog.dismiss();
						selectActivity();
					}
				}).show();
			}
		});
	}


	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}


	@Override
	public void onShowPress(MotionEvent e) {
	}


	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return true;
	}


	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,float distanceY) {
		return false;
	}


	@Override
	public void onLongPress(MotionEvent e) {
		registerForContextMenu(mGlSurfaceView);
		registerForContextMenu(mPlaydraftMmsImageView);
	}
	
	

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {
		if(mGlSurfaceView != null){
			unregisterForContextMenu(mGlSurfaceView);
			unregisterForContextMenu(mPlaydraftMmsImageView);
		}
		
		HLog.e(TAG, "onFling --------------->>>");
		String curBody = mSmsManage.getAdapter().get(mCurIndex).body;
		String ismmsType = mSmsManage.getAdapter().get(mCurIndex).ismms;//1 就是彩信
		String type = mSmsManage.getAdapter().get(mCurIndex).type;//1 就是彩信
		String[] tmp = ToolsUtil.getTmpStr(curBody,HActivityGroup.this);
		if(e1.getX() - e2.getX() > 50 && Math.abs(velocityX) > 0){
			if(mPartIndex < tmp.length - 1){//当前条数不是最后一条
				HPreviewActivity.mGLSufaceViewRenderer.turning(ToolsUtil.getFileName(HActivityGroup.this),tmp[mPartIndex += 1]);
				setmProgessTextView(mPartIndex);
			}else{
				if(mCurIndex < mTotalNumber - 1){
					mPartIndex = 0;
					mCurIndex++;
					curBody = mSmsManage.getAdapter().get(mCurIndex).body;
					ismmsType = mSmsManage.getAdapter().get(mCurIndex).ismms;//1 就是彩信
					type = mSmsManage.getAdapter().get(mCurIndex).type;//1 就是彩信
					setmProgessTextView(0);
					if(ismmsType.equals("1")){
						mDraftAndMms = 1;
						showPlayMmsAndDraft(1);
						return true;
					}else if(type.equals("3")){
						mDraftAndMms = 0;
						showPlayMmsAndDraft(0);
						return true;
					}
					showSms();
					tmp = ToolsUtil.getTmpStr(curBody,HActivityGroup.this);
					mGlSurfaceView.setVisibility(View.VISIBLE);
					mRelativeLayout.setVisibility(View.GONE);
					HPreviewActivity.mGLSufaceViewRenderer.turning(ToolsUtil.getFileName(HActivityGroup.this),tmp[mPartIndex]);
				}else{
					Toast.makeText(HActivityGroup.this, R.string.lastone,Toast.LENGTH_SHORT).show();
				}
			}
			return true;
		}else if(e2.getX()-e1.getX() > 50  && Math.abs(velocityX) > 0){
			if(mPartIndex > 0){
				HPreviewActivity.mGLSufaceViewRenderer.turning(ToolsUtil.getFileName(HActivityGroup.this),tmp[mPartIndex -= 1]);
				setmProgessTextView(mPartIndex);
			}else{
				if(mCurIndex != 0){
					mPartIndex = 0;
					mCurIndex--;
					ismmsType = mSmsManage.getAdapter().get(mCurIndex).ismms;//1 就是彩信
					type = mSmsManage.getAdapter().get(mCurIndex).type;//1 就是彩信
					setmProgessTextView(0);
					if(ismmsType.equals("1")){
						mDraftAndMms = 1;
						showPlayMmsAndDraft(1);
						return true;
					}else if(type.equals("3")){
						mDraftAndMms = 0;
						showPlayMmsAndDraft(0);
						return true;
					}
					curBody = mSmsManage.getAdapter().get(mCurIndex).body;
					tmp = ToolsUtil.getTmpStr(curBody,HActivityGroup.this);
					showSms();
					HPreviewActivity.mGLSufaceViewRenderer.turning(ToolsUtil.getFileName(HActivityGroup.this),tmp[mPartIndex]);
				}else{
					Toast.makeText(HActivityGroup.this, R.string.firstone,Toast.LENGTH_SHORT).show();
				}
			}
			return true;
		}
		
		return false;
	}

	/**
	 * @param i  0 --draft 1--mms
	 */
	private void showPlayMmsAndDraft(int i){
		mGlSurfaceView.setVisibility(View.GONE);
		mRelativeLayout.setVisibility(View.VISIBLE);
		if(i == 0){
			mPlayDraftAndMmsTextView.setText(R.string.seeDraft);
		}else{
			mPlayDraftAndMmsTextView.setText(R.string.seeMms);
		}
	}
	
	private void showSms(){
		mGlSurfaceView.setVisibility(View.VISIBLE);
		mRelativeLayout.setVisibility(View.GONE);
	}
	

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	
	@Override
	protected void onDestroy() {
		HLog.i(TAG, "onDestroy ---------------->>>");
		//mSmsManage.getAdapter().clear();
		unregisterReceiver(br);
		super.onDestroy();
	}
	

	@Override
	public void onClick(View v) {
		if(v == mPlaydraftMmsButton){
			if(mDraftAndMms != -1){
				if(mDraftAndMms == 0){
					Intent intent = new Intent();
					intent.putExtra("position", mCurIndex);
					intent.putExtra("address", mSmsManage.getTalkTitleAddress(mThreadPosition));
					intent.setClass(HActivityGroup.this, HEditSmsActivity.class);
					startActivityForResult(intent, 0);
					mCurIndex++;
					if(mCurIndex == mTotalNumber){
						mCurIndex = 0;
					}
				}else if(mDraftAndMms == 1){
					Intent intent = new Intent();
					intent.putExtra("position", mCurIndex);
					intent.putExtra("address", mSmsManage.getTalkTitleAddress(mThreadPosition));
					intent.putExtra("name",mAddressBookManager.getAppendName(mSmsManage.getTalkTitleName(mThreadPosition), mSmsManage.getTalkTitleAddress(mThreadPosition)));
					intent.setClass(HActivityGroup.this,HPlayMMSActivity.class);
					startActivityForResult(intent, 0);
					mCurIndex++;
					if(mCurIndex == mTotalNumber){
						mCurIndex = 0;
					}
				}
				mDraftAndMms = -1;
			}
		}
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		HLog.i(TAG, "onRestart-------------->>>");
//		if(!mHSharedPreferences.getIsReadBuffer()){
//			finish();
//			return;
//		}
		if(!HConst.iscollect){
			mThreadPosition = mThreadManager.getPosition(mAddress);
		}else{
			mThreadPosition = mThreadManager.getCollectPosition(mAddress);
		}
		HLog.i(TAG, "mThreadPosition----" + mThreadPosition);
	}
	
	
	@Override
	protected void onStart() {
		super.onStart();
		setContentView(R.layout.activity_group);
		mChageReadSms = (Button) findViewById(R.id.changeReadSmsWayButton);
		setHead();
		setName();
		chageAlreadyRead(mThreadPosition);
		selectReadSmsType();
//		if(!mHSharedPreferences.getIsReadBuffer()){
//			return;
//		}
		HLog.i(TAG, "onStart ---------------->>>");
		selectActivity();
		setmProgessTextView(0);
	}
	
	BroadcastReceiver br = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			setmProgessTextView(0);
		}
	};
	
	
	
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == 0){
			if(mIsDialog){
				HBufferList updateListDB = new HBufferList(HActivityGroup.this.getApplication());
				HSharedPreferences sharedPreferences = new HSharedPreferences((HSmsApplication)this.getApplication());
				if(sharedPreferences.getIsReadBuffer() && mSmsManage.getThreadAdapter() == null ){
					if(mSmsManage.getAdapter().getCount() != 0){
						HSms sms = mSmsManage.getAdapter().get(mSmsManage.getAdapter().size()-1); 
						sms.read = "1";
						sms.threadid = mSmsManage.getThreadIdForAndress(sms.address.split(","));
						updateListDB.updataBufferList(sms);
					}
				}
				if(mSmsManage.getThreadAdapter() != null){
					mThreadManager.noReadChageRead(mThreadManager.getPosition(mAddress));
					mThreadManager.ncThread();
				}
				finish();
				Intent intent = new Intent();
				intent.putExtra("back", mAddress);
				intent.putExtra("is", "ok");
				intent.putExtra("dialog", true);
				intent.setClass(this, HThreadActivity.class);
				startActivity(intent);
			}
			if(!mEditText.getText().toString().trim().equals("")){
				mSmsManage.addDraft(mEditText.getText().toString(),mAddress,mName);
			}
			finish();
			return true;
		}
		return super.dispatchKeyEvent(event);  
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case HConst.REQUEST_CONTACT:
			HAddressBookManager abk = new HAddressBookManager(this);
			if(mThreadPosition == -1){
				mName = abk.getNameByNumber(mAddress);
				Bitmap bp = abk.getContactPhoto(mName, mAddress);
				mHeadImageView.setImageBitmap(bp);
				HBufferList updateListDB = new HBufferList(HActivityGroup.this.getApplication());
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
				mThreadManager.updataList(mThreadPosition);
				mThreadManager.ncThread();
				mName = abk.getAppendName(mThreadManager.getName(mThreadPosition), mThreadManager.getAddress(mThreadPosition));//如果没有保存好的名字则得到电话号码
				mHeadImageView.setImageBitmap(mSmsManage.getHeadBitmap(mThreadPosition));
			}
//			tv_name.setText(mName);
			break;
		}
		if(resultCode == 100){
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	/** 创建上下文菜单 */
	@Override
	public void onCreateContextMenu(ContextMenu contextMenu, View v,ContextMenuInfo contextMenuInfo) {
		if(mGlSurfaceView != null){
			unregisterForContextMenu(mGlSurfaceView);
			unregisterForContextMenu(mPlaydraftMmsImageView);
		}
		
		int index = -1;
		if(mHSharedPreferences.getReadSmsType() == 0){
			index = mCurIndex;
		}else{
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) contextMenuInfo;
			index = info.position; 
		}
		
		HSms sms = mSmsManage.getAdapter().getList().get(index);
		HAddressBookManager abk = new HAddressBookManager(this);
		contextMenu.setHeaderTitle(abk.getAppendName(sms.name, sms.address));
		if(sms.ismms.equals("0") && !HConst.iscollect && !sms.type.equals("3")){
			contextMenu.add(0, 9, 4, R.string.addCollect);//添加收藏-----------
		}else if(HConst.iscollect){
			contextMenu.add(0, 9, 4, R.string.cancelCollect);//取消收藏-----------
		}
		if (mAddress != null && !mAddress.equals("")) {
			if(sms.ismms.equals("0")){
				contextMenu.add(0, 4, 0, R.string.forward);//转发-----------
			}
			contextMenu.add(0, 2, 1, R.string.call);//呼叫------------
		} 
//		if (sms.type.equals("5") && sms.ismms.equals("0")) {
//			contextMenu.add(0, 3, 3, R.string.resend);// 重发
//		}
		if(sms.ismms.equals("0")){
			contextMenu.add(0, 6, 3, R.string.copysms);// 复制短信文本---------------
		}
		if(!HConst.iscollect){
			contextMenu.add(0, 5, 2, R.string.delete);// 删除---------
		}
		 List<String> list = null;
		if(mThreadPosition == -1){
			String name = getIntent().getStringExtra("name");
			if(name == null){
				name = mName;
			}
			list = mSmsManage.isGetName(mAddress,name);
		}else{
			list = mThreadManager.isGetName(mThreadPosition);
		}
		 if (list.size() > 0) {
			contextMenu.add(0, 8, 5, R.string.addcontact);// 添加通讯录------------------
		 }
	}
	
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(mGlSurfaceView != null){
			registerForContextMenu(mGlSurfaceView);
			registerForContextMenu(mPlaydraftMmsImageView);
		}
		int index = -1;
		if(mHSharedPreferences.getReadSmsType() == 0){
			index = mCurIndex;
		}else{
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			index = info.position; 
		}
		HSms sms = mSmsManage.getAdapter().getList().get(index);
		switch (item.getItemId()) {
		case 1:
			break;
		case 2:
			context_call();
			break;
		case 3:
			break;
		case 4:
			context_forward(index);
			break;
		case 5:
			context_a_delete(index);
			break;
		case 6:
			context_copytext(sms);
			break;
		case 7:
			break;
		case 8:
			context_add_contact();
			break;
		case 9:
			add_collect(sms,index);
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	
	
	private void context_add_contact() {
		if(mHSharedPreferences.getReadSmsType() == 0){
			mStatistics.add(HStatistics.Z7_9, "", "", "");
		}else if(mHSharedPreferences.getReadSmsType() == 1){
			mStatistics.add(HStatistics.Z6_12, "", "", "");
		}
		String []address = null;
		if(mThreadPosition == -1){
			address = mSmsManage.isGetName(mAddress, getIntent().getStringExtra("name")).toArray(new String[]{});
		}else{
			address = mThreadManager.isGetName(mThreadPosition).toArray(new String[]{});
		}
		new HAddContact(HActivityGroup.this, address);
	}
	
	private void add_collect(HSms sms,final int index){
		final HCollectTable collectTable = new HCollectTable(HActivityGroup.this);
		if(!HConst.iscollect){
			if(mHSharedPreferences.getReadSmsType() == 0){
				mStatistics.add(HStatistics.Z7_8, "", "", "");
			}else if(mHSharedPreferences.getReadSmsType() == 1){
				mStatistics.add(HStatistics.Z6_3, "", "", "");
			}
			if(collectTable.querydb(sms.smsid)){
				Toast.makeText(HActivityGroup.this, getString(R.string.bukechongfushouc), Toast.LENGTH_SHORT).show();
			}else{
				collectTable.insertNote(sms);
				Toast.makeText(HActivityGroup.this, getString(R.string.collectSuccess), Toast.LENGTH_SHORT).show();
			}
		}else{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.celCollect);
			builder.setCancelable(false);
			builder.setPositiveButton(R.string.confirm,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							boolean is = collectTable.deleteNoteForSmsID(index, mThreadPosition);
							if(is){
								finish();
							}else{
								if(mHSharedPreferences.getReadSmsType() == 0){
									mTotalNumber = mSmsManage.getAdapter().size();
									if(mCurIndex == mTotalNumber){
										if(!is){
											mCurIndex = 0;
											HPreviewActivity.mGLSufaceViewRenderer.turning(ToolsUtil.getFileName(HActivityGroup.this),mSmsManage.getAdapter().get(mCurIndex).body);
											setmProgessTextView(0);
										}
									}else{
										HPreviewActivity.mGLSufaceViewRenderer.turning(ToolsUtil.getFileName(HActivityGroup.this),mSmsManage.getAdapter().get(mCurIndex).body);
										setmProgessTextView(0);
									}

								}
								Toast.makeText(HActivityGroup.this, R.string.deleteSmsOk,  Toast.LENGTH_SHORT).show();
							}
						}
					});
			builder.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.create().show();
		}
	}
	
	
	private void context_copytext(HSms sms) {
		if(mHSharedPreferences.getReadSmsType() == 0){
			mStatistics.add(HStatistics.Z7_7, "", "", "");
		}else if(mHSharedPreferences.getReadSmsType() == 1){
			mStatistics.add(HStatistics.Z6_10, "", "", "");
		}
		ClipboardManager cbm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		cbm.setText(sms.body);
		Toast.makeText(HActivityGroup.this, R.string.copyok,  Toast.LENGTH_SHORT).show();
	}


	private void context_a_delete(final int talkPosition) {
		if(mHSharedPreferences.getReadSmsType() == 0){
			mStatistics.add(HStatistics.Z7_6, "", "", "");
		}else if(mHSharedPreferences.getReadSmsType() == 1){
			mStatistics.add(HStatistics.Z6_11, "", "", "");
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.isdelete_a_sms);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Toast.makeText(HActivityGroup.this, R.string.deleteSmsOk,  Toast.LENGTH_SHORT).show();
						boolean is = mSmsManage.deleteTalkItem(mAddress, talkPosition);
						
						if(mHSharedPreferences.getReadSmsType() == 0){
							mTotalNumber = mSmsManage.getAdapter().size();
							if(mCurIndex == mTotalNumber){
								if(!is){
									mCurIndex = 0;
									String ismmsType = mSmsManage.getAdapter().get(mCurIndex).ismms;//1 就是彩信
									String type = mSmsManage.getAdapter().get(mCurIndex).type;//1 就是彩信
									if(ismmsType.equals("1")){
										mDraftAndMms = 1;
										showPlayMmsAndDraft(1);
									}else if(type.equals("3")){
										mDraftAndMms = 0;
										showPlayMmsAndDraft(0);
									}else{
										mGlSurfaceView.setVisibility(View.VISIBLE);
										mRelativeLayout.setVisibility(View.GONE);
										HPreviewActivity.mGLSufaceViewRenderer.turning(ToolsUtil.getFileName(HActivityGroup.this),mSmsManage.getAdapter().get(mCurIndex).body);
									}
									setmProgessTextView(0);
								}else{
									finish();
								}
							}else{
								String ismmsType = mSmsManage.getAdapter().get(mCurIndex).ismms;//1 就是彩信
								String type = mSmsManage.getAdapter().get(mCurIndex).type;//1 就是彩信
								if(ismmsType.equals("1")){
									mDraftAndMms = 1;
									showPlayMmsAndDraft(1);
								}else if(type.equals("3")){
									mDraftAndMms = 0;
									showPlayMmsAndDraft(0);
								}else{
									HPreviewActivity.mGLSufaceViewRenderer.turning(ToolsUtil.getFileName(HActivityGroup.this),mSmsManage.getAdapter().get(mCurIndex).body);
								}
								setmProgessTextView(0);
							}

						}
						if(mSmsManage.getThreadAdapter() == null){
							Intent intent = new Intent();
							intent.putExtra("back", mAddress);
							intent.putExtra("is", "ok");
							intent.setClass(HActivityGroup.this, HThreadActivity.class);
							startActivity(intent);
						}
						if(is){
							returnThreadActivity();						}
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
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
	
	
	private void context_call() {
		if(mHSharedPreferences.getReadSmsType() == 0){
			mStatistics.add(HStatistics.Z7_5, "", "", "");
		}else if(mHSharedPreferences.getReadSmsType() == 1){
			mStatistics.add(HStatistics.Z6_9, "", "", "");
		}
		new HCall(HActivityGroup.this, mAddress,mName);
	}
	
	
	private void context_forward(int index) {
		if(mHSharedPreferences.getReadSmsType() == 0){
			mStatistics.add(HStatistics.Z7_3, "", "", "");
		}else if(mHSharedPreferences.getReadSmsType() == 1){
			mStatistics.add(HStatistics.Z6_4, "", "", "");
		}
		 Intent intent = new Intent();
		 intent.setClass(HActivityGroup.this, HEditSmsActivity.class);
		 intent.putExtra("position", index);
		 intent.putExtra("smsbody", mSmsManage.getAdapter().get(mCurIndex).body);
		 intent.putExtra("isforward", true);
		 startActivity(intent);
	}

	
}
