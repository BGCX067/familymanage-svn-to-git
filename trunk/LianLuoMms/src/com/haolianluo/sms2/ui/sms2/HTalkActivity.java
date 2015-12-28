package com.haolianluo.sms2.ui.sms2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.TelephonyMy.Mms;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SkinButton;
import android.widget.SkinFrameLayout;
import android.widget.SkinImageView;
import android.widget.SkinLinearLayout;
import android.widget.SkinRelativeLayout;
import android.widget.SkinTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.mms.pdu.EncodedStringValue;
import com.google.android.mms.pdu.PduPersister;
import com.haolianluo.sms2.R;
import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.data.HSharedPreferences;
import com.haolianluo.sms2.mms.data.ContactList;
import com.haolianluo.sms2.mms.data.Conversation;
import com.haolianluo.sms2.mms.data.WorkingMessage;
import com.haolianluo.sms2.model.HAddressBookManager;
import com.haolianluo.sms2.model.HDatabaseHelper;
import com.haolianluo.sms2.model.HHistoryLinkman;
import com.haolianluo.sms2.model.HResDatabaseHelper;
import com.haolianluo.sms2.model.HResProvider;
import com.haolianluo.sms2.model.HStatistics;
import com.haolianluo.sms2.ui.ComposeMessageActivity;
import com.haolianluo.sms2.ui.MessageItem;
import com.haolianluo.sms2.ui.MessageListAdapter;
import com.haolianluo.sms2.ui.MessageListAdapter.ColumnsMap;
import com.haolianluo.sms2.ui.MessageUtils;
import com.haolianluo.swfp.GLSurfaceView;
import com.haolianluo.swfp.HGLSufaceViewRenderer;
import com.haolianluo.swfp.PageWidget;
import com.lianluo.core.cache.CacheManager;
import com.lianluo.core.skin.SkinManage;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;

public class HTalkActivity extends ComposeMessageActivity implements OnTouchListener {

	private Context mContext;
	private boolean hideHTalkTitle;
	
	
	private boolean isMove = false;
	private boolean isPageMove = false;
	private boolean calcCornerFlag = false;
	private int moveLength = 100;
	
	/**姓名、Flash与气泡切换按钮、资源库*/
//	private View hTalkTitle;
	private ImageView mIvHead;		//左上角头像
	private TextView mTvName;		//号码
	
	private SkinLinearLayout talk_layout;
	private SkinRelativeLayout talk_title_layout;
	private Button bt_shortcut_key;	//右上角快捷菜单按钮
	
	private FrameLayout talk_new_tem_layout;	//新模版icon的layout
	private TextView talk_new_tem_count;	//新模版的数量
	
	private String addressList;		//拨打电话传入的电话集合参数
	private String phoneList;			//联系人名字
	
	private Button bt_add;//添加联系人
	
	private int threadPosition = 0;	//会话索引值
	private HUpdateTools hUpdateTools;
	
	
	private Button mMmsButton;
	
	
	public static boolean isDraft = false;
	
	private int mScreenSize = -1;//0--- 480 * 800   1----320 * 480	2----240 * 320
	
	private ImageView tv_linkman;
	private TextView tv_linkman1;
	
	private List<String> numbers;
	private List<String> numbersRecord;
	private List<String> histroyLinkmanList;
	
	private HAddressBookManager mAddressBookManager;
	private LinearLayout topGrid, bottomGrid, mid;
	private ScrollView mid_layout;
	
	private boolean mIsRun = true;
	private String shows = null;
	
	private int flash_width, flash_height;
	private SharedPreferences preference;
	
	private HStatistics mStatistics;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mStatistics = new HStatistics(this);
		
		//mHSharedPreferences.setReadSmsType(1);
		//初始化缓存
		CacheManager.newInstance().openCache(getApplicationContext());
		//init skin
		if(getContentResolver() != null)
		{
			if(mHSharedPreferences.getTryOver())
			{
				Toast.makeText(mContext, getString(R.string.try_over), Toast.LENGTH_LONG).show();
				getContentResolver().delete(HResProvider.CONTENT_URI_SKIN, HResDatabaseHelper.CHARGE + " = '-1'", null);
				String dir = mContext.getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "default";
				File defdir = new File(dir);
				if(defdir.exists())
				{
					File[] files = defdir.listFiles();
					for(File f : files)
					{
						if(f.exists())
						{
							f.delete();
						}
					}
				}
				mHSharedPreferences.setTryOver(false);
			}
			Cursor cursor = getContentResolver().query(HResProvider.CONTENT_URI_SKIN, null, 
					HResDatabaseHelper.RES_USE + " = '1'", null, null);
			if(cursor != null && cursor.getCount() > 0)
			{
				cursor.moveToNext();
				SkinManage.mCurrentSkin = cursor.getString(cursor.getColumnIndex(HResDatabaseHelper.PACKAGENAME));
				SkinManage.mCurrentFile = cursor.getString(cursor
						.getColumnIndex(HResDatabaseHelper.FILE_NAME));
			}
			else
			{
				ContentValues values = new ContentValues();
				values.put(HResDatabaseHelper.RES_USE, 1);
				getContentResolver().update(HResProvider.CONTENT_URI_SKIN, values, HResDatabaseHelper.PACKAGENAME + " = '" + HConst.DEFAULT_PACKAGE_NAME + "'", null);
				SkinManage.mCurrentSkin = HConst.DEFAULT_PACKAGE_NAME;
			}
			if(cursor != null)
			{
				cursor.close();
			}
		}
		hUpdateTools = new HUpdateTools(this);
		registerReceiver(updateDialog, new IntentFilter(HConst.ACTION_UPDATE_DIALOG));
		threadPosition = getIntent().getIntExtra("position", -1);
		
		//boolean isEditSms = getIntent().getBooleanExtra("editsms", false);
		//获取屏幕的分辨率
		splitStringSize();
		
		mColumnsMap = new ColumnsMap();
		
		listName = getFileName();
		
		//init flash width height
		preference = mContext.getSharedPreferences("flash_data", 0);
		flash_width = preference.getInt("flash_width", 0);
		flash_height = preference.getInt("flash_height", 0);
		
		if(send)
		{	
			//当前界面是新建短信按钮跳转而来的
			HConst.markActivity = 2;
		}
	}
	
    BroadcastReceiver updateDialog = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			intent.setClass(context, HUpdateDialog.class);
			context.startActivity(intent);
		}
	};
	
	
	@Override
	protected void onStart() {
		//转发后键盘部分黑屏bug
		talk_layout.changeSkin();
		
		//564
		/*if(getIntent().getBooleanExtra("talk_activity", false)) {
			notifaSmsName = getIntent().getStringExtra("titleName");
			HConst.iscollect = false;
		}*/
		initFlash();
		
		
		
		String notifaAddress = getIntent().getStringExtra("address");
		if(!"".equals(notifaAddress) && null != notifaAddress)
		{
			mConversation = Conversation.get(this, ContactList.getByNumbers(notifaAddress,
                    false /* don't block */, true /* replace number */), false);
			mWorkingMessage.setConversation(mConversation);
		}
		
		super.onStart();
		Log.d("onStart", "onStart---------------------");
		
		initTitle();
		numbers = new ArrayList<String>();
		
		init();
		if(send) {
			HConst.markActivity = 2;
			isShowNumber();
		}
		if(mMsgListView != null && mMsgListView.getVisibility() == View.VISIBLE)
		{
			mMsgListView.requestFocus();
		}
	}
	
	@Override
	public void changeSkin() {
		try {
			if(talk_layout != null) {
				talk_layout.changeSkin();
			}
			if(talk_title_layout != null) {
				talk_title_layout.changeSkin();
			}
			if(bt_shortcut_key != null) {
				((SkinButton)bt_shortcut_key).changeSkin();
			}
			if(mChageReadSms != null) {
				((SkinButton)mChageReadSms).changeSkin();
			}
			if(mIvHead != null) {
				((SkinImageView)mIvHead).changeSkin();
			}
			if(mTvName != null) {
				((SkinTextView)mTvName).changeSkin();
			}
			if(tv_percent != null) {
				((SkinTextView)tv_percent).changeSkin();
			}
			
			if(mTopPanel != null) {
				((SkinRelativeLayout)mTopPanel).changeSkin();
			}
			if(bt_add != null) {
				((SkinButton)bt_add).changeSkin();
			}
			if(mid != null) {
				((SkinLinearLayout)mid).changeSkin();
			}
			if(topGrid != null) {
				((SkinLinearLayout)topGrid).changeSkin();
			}
			if(tv_linkman != null) {
				((SkinImageView)tv_linkman).changeSkin();
			}
			if(tv_linkman1 != null) {
				((SkinTextView)tv_linkman1).changeSkin();
			}
			if(bottomGrid != null) {
				((SkinLinearLayout)bottomGrid).changeSkin();
			}
			if(mFlashListView != null) {
				((SkinFrameLayout)mFlashListView).changeSkin();
			}
			if(mBottomPanel != null) {
				((SkinLinearLayout)mBottomPanel).changeSkin();
			}
			if(mSendButton != null) {
				((SkinButton)mSendButton).changeSkin();
			}
			if(mTextCounter != null) {
				((SkinTextView)mTextCounter).changeSkin();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		super.changeSkin();
	}
	
	
	@Override
	public void initView() {
//		hTalkTitle = findViewById(R.id.htalk_title);
		tv_percent = (TextView) findViewById(R.id.tv_percent);
		mFlashListView = findViewById(R.id.fl_flash);
		mMmsView = findViewById(R.id.playDraftAndMms);
		mMmsView.setOnTouchListener(this);
		mMmsView.setLongClickable(true);
		mMmsView.setOnCreateContextMenuListener(mMsgListMenuCreateListener);
		mPageWidget = (PageWidget) findViewById(R.id.page);
		mCurPageBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
		mNextPageBitmap = mCurPageBitmap;
		mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);
		if(ToolsUtil.PAGE_FLAG) {
			mPageWidget.setVisibility(View.VISIBLE);
		}
		
		glview = (GLSurfaceView) findViewById(R.id.play);
		glview.setOnTouchListener(this);
		glview.setLongClickable(true);
		glview.setOnCreateContextMenuListener(mMsgListMenuCreateListener);
		glview.setBackgroundColor(0x00000000);
		mMmsButton = (Button) findViewById(R.id.playDraftAndMmsButton);
		mMmsButton.setOnClickListener(mmsView);
		mMmsSubject = (TextView) findViewById(R.id.playDraftAndMmsSubject);
		
//		View view = findViewById(R.id.htalk_title);
		mIvHead = (ImageView) findViewById(R.id.iv_head);
		mTvName = (TextView) findViewById(R.id.tv_name);
		mChageReadSms = (SkinButton) findViewById(R.id.bt_call);
		if(mHSharedPreferences.getReadSmsType() == 0)
		{
			mChageReadSms.setBackgroundResource(R.drawable.bt_chage_readsms_falsh_xml_in);
		}
		else
		{
			mChageReadSms.setBackgroundResource(R.drawable.bt_chage_readsms_falsh_xml);
		}
		talk_title_layout = (SkinRelativeLayout) findViewById(R.id.talk_title_layout);
		bt_shortcut_key = (Button) findViewById(R.id.bt_shortcut_key);
		talk_new_tem_layout = (FrameLayout) findViewById(R.id.talk_new_tem_layout);
		talk_new_tem_count = (TextView) findViewById(R.id.talk_new_tem_count);
		
		talk_layout = (SkinLinearLayout) findViewById(R.id.talk_layout);
		
		bt_add = (Button) findViewById(R.id.bt_add);
		bt_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 弹出通讯录列表
				mStatistics.add(HStatistics.Z4_2,"","","");
				
				Intent intent = new Intent();
				intent.setClass(HTalkActivity.this, HContactActivity.class);
				startActivityForResult(intent, 11);
			}
		});
		
//		mGLSufaceViewRenderer = new HGLSufaceViewRenderer(this, fileName, null, ttfName);
//		glview.setRenderer(mGLSufaceViewRenderer);
		super.initView();
	}
	
	public void initFlash() {
		mGLSufaceViewRenderer = new HGLSufaceViewRenderer(this, fileName, null, ttfName);
		glview.setRenderer(mGLSufaceViewRenderer);
	}
	
	protected void onLoaded(Cursor cursor, long targetMsgId) {
		try {
			initTitle();
			
			splitStringSize();
			cursor.moveToNext();
			if(cursor.getCount() > 0) {
				if(talkIndex < (cursor.getCount() - 1)) {
					talkIndex = cursor.getCount() - 1;
					curIndex = talkIndex;
				} else {
					talkIndex = cursor.getCount() - 1;
					if(curIndex == -1) {
						curIndex = talkIndex;
					} else {
						if(curIndex > talkIndex) {
							curIndex = talkIndex;
						}
					}
				}
				
		        if (targetMsgId != -1) {
                    cursor.moveToPosition(-1);
                    while (cursor.moveToNext()) {
                        long msgId = cursor.getLong(MessageListAdapter.COLUMN_ID);
                        if (msgId == targetMsgId) {
                        	curIndex = cursor.getPosition();
                            break;
                        }
                    }
                }
				
				tv_percent.setText((curIndex + 1) + "/" + (talkIndex + 1));
				
				cursor.moveToPosition(curIndex);
				String type = cursor.getString(mColumnsMap.mColumnMsgType);
		        MessageItem msgItem = new MessageItem(mContext, type, cursor, mColumnsMap, null);
		        getRandom(msgItem.mMsgId, listName);
		        
		        if(mGLSufaceViewRenderer == null) {
		        	mGLSufaceViewRenderer = new HGLSufaceViewRenderer(this, fileName, getTmpStr(msgItem.mBody)[0], ttfName);
		    		glview.setRenderer(mGLSufaceViewRenderer);
		        } else {
		        	mGLSufaceViewRenderer.turning(fileName, ttfName, getTmpStr(msgItem.mBody)[0], false);
		        }
		        
		        int temp = mHSharedPreferences.getReadSmsType();
				if(temp == 0){
					mMsgListView.setVisibility(View.GONE);
					mFlashListView.setVisibility(View.VISIBLE);
					HConst.markActivity = 4;
					tv_percent.setVisibility(View.VISIBLE);
					
					if(flash_width <=0 || flash_height <= 0) {
						Editor editor = preference.edit();
						editor.putInt("flash_width", glview.getWidth());
						editor.putInt("flash_height", glview.getHeight());
						editor.commit();
					}
				}else{
					if(!mExitOnSent || !HConst.isForwarding)
					{	
						mMsgListView.setVisibility(View.VISIBLE);
						HConst.markActivity = 3;
					}
					mFlashListView.setVisibility(View.GONE);
					tv_percent.setVisibility(View.GONE);
				}
				
		        if("mms".equals(type)) {
		        	mMmsView.setVisibility(View.VISIBLE);
		        	String subject = cursor.getString(mColumnsMap.mColumnMmsSubject);
		        	if(null == subject)
		        	{
		        		subject = mContext.getString(R.string.no_subject);
		        		mMmsSubject.setText(subject);
		        	}
		        	else
		        	{
			        EncodedStringValue v = new EncodedStringValue(
			        		cursor.getInt(mColumnsMap.mColumnMmsSubjectCharset),
	                        PduPersister.getBytes(subject));
			        mMmsSubject.setText(v.getString());
		        	}
		        	glview.setVisibility(View.GONE);
		        } else {
		        	mMmsView.setVisibility(View.GONE);
		        	glview.setVisibility(View.VISIBLE);
		        }
			} else {
				int temp = mHSharedPreferences.getReadSmsType();
				if(temp == 0){
					mMsgListView.setVisibility(View.GONE);
					mFlashListView.setVisibility(View.GONE);
					tv_percent.setVisibility(View.GONE);
				}
			}
			
			if(hideHTalkTitle) {
				mMsgListView.setVisibility(View.GONE);
				mFlashListView.setVisibility(View.GONE);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void initTitle() {
		//接收通知栏传入的联系人姓名预先显示，显示之后清除intent里面的该值，然后去数据库里查询再更新显示
		if(!"".equals(notifaSmsName))
		{
			mTvName.setText(notifaSmsName);
			getIntent().removeExtra("titleName");
		}
		
		//取得该会话相关联系人
		ContactList contactList = mConversation.getRecipients();
		int size = contactList.size();	//联系人的个数
		StringBuffer phoneBuffer = new StringBuffer();
		StringBuffer call_address = new StringBuffer();
		
		for(int i = 0; i < size; i++) {
			if (!contactList.get(i).existsInDatabase()) {
				//不存在显示号码
				if (i== (size - 1)) {
					phoneBuffer.append(contactList.get(i).getNumber());
				}else {
					phoneBuffer.append(contactList.get(i).getNumber() + ",");
				}
				
			}else {
				//存在的话显示名称
				if (i== (size - 1)) {
					phoneBuffer.append(contactList.get(i).getName());
				}else {
					phoneBuffer.append(contactList.get(i).getName() + ",");
				}
			}
			
			call_address.append(contactList.get(i).getNumber() + ",");
		}
		phoneList = phoneBuffer.toString();
		//显示名字或则号码或则二者混合显示
		if(!"".equals(phoneList))
		{	
			mTvName.setText(phoneList);
		}
		addressList = call_address.toString();
		
		//左上角头像
		if (size > 1) {
			//多个联系人,默认头像
			mIvHead.setBackgroundResource(R.drawable.def_head_s);
		}else if (size == 1) {
			//单个联系人就取其头像
			HAddressBookManager abm = new HAddressBookManager(getApplication());
			Bitmap bp = abm.getContactPhoto(contactList.get(0).getName(), contactList.get(0).getNumber());
			if(bp == null)
			{
				mIvHead.setBackgroundResource(R.drawable.def_head_s);
			}else {
				mIvHead.setImageBitmap(bp);
			}
			
		}
		
		//快捷菜单
		bt_shortcut_key.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new HShortcutDialog(HTalkActivity.this);
				//设置标志位
				HConst.setShowEdNewTemCount(HTalkActivity.this);
				int temp = mHSharedPreferences.getReadSmsType();
				if(temp == 0) {
					//商店图标点击数
					mStatistics.add(HStatistics.Z6_1,"","","");
				}else
				{
					mStatistics.add(HStatistics.Z7_1,"","","");
				}
			}
		});
		
		HSharedPreferences hsp = new HSharedPreferences(mContext);
		HConst.setConfigAndCount(mContext, hsp.getNewListTime(), hsp.getNewListCount());
		
		//TODO 判断是否要显示新图标
		if(!HConst.isShowNewTemCount(HTalkActivity.this, ""))
		{
			talk_new_tem_layout.setVisibility(View.VISIBLE);
			talk_new_tem_count.setText(HConst.getNewCount(HTalkActivity.this));
		}else
		{
			talk_new_tem_layout.setVisibility(View.GONE);
		}
		
		//拨打电话按钮
		mChageReadSms.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mStatistics.add(HStatistics.Z7_9,"","","");
//				context_call(addressList);
				int temp = mHSharedPreferences.getReadSmsType();
				if(temp == 0) {
					if(!mExitOnSent || !HConst.isForwarding)
					{	
						mMsgListView.setVisibility(View.VISIBLE);
						HConst.markActivity = 3;
					}
					mFlashListView.setVisibility(View.GONE);
					tv_percent.setVisibility(View.GONE);
					mHSharedPreferences.setReadSmsType(1);
					mChageReadSms.setBackgroundResource(R.drawable.bt_chage_readsms_falsh_xml);
				} else {
					mMsgListView.setVisibility(View.GONE);
					mFlashListView.setVisibility(View.VISIBLE);
					HConst.markActivity = 4;
					tv_percent.setVisibility(View.VISIBLE);
					mHSharedPreferences.setReadSmsType(0);
					mChageReadSms.setBackgroundResource(R.drawable.bt_chage_readsms_falsh_xml_in);
				}
				
//				new AlertDialog.Builder(mContext).setSingleChoiceItems(R.array.selectReadSmsType, temp, new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						mHSharedPreferences.setReadSmsType(which);
//						dialog.dismiss();
//						if(which == 0){
//							mMsgListView.setVisibility(View.GONE);
//							mFlashListView.setVisibility(View.VISIBLE);
//							tv_percent.setVisibility(View.VISIBLE);
//						} else {
//							mMsgListView.setVisibility(View.VISIBLE);
//							mFlashListView.setVisibility(View.GONE);
//							tv_percent.setVisibility(View.GONE);
//						}
//					}
//				}).show();
			}
		});
		
		/**
		 * Talk界面列表项点击事件
		 *  1.如果是短信（非草稿），则跳转到flash预览界面；
		 *  2.如果是短信草稿，则跳转到编写信息的界面（当前为一个界面	，要刷新UI）；
		 *  3.如果是彩信，则跳转到播放彩信的界面
		 */
//		mMsgListView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
////				if(!hUpdateTools.getIsUpdate())
////				{
////					startPreviewActivity(position);
////				}else
////				{
////					Toast.makeText(HTalkActivity.this, getString(R.string.version_toast), Toast.LENGTH_SHORT).show();
////				}
//				
//		}});
		if(shows != null && !"".equals(shows)) {
			setContact();
		}
	}
	
	@Override
	protected String getAddress() {
		return phoneList;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 11 && data.getStringArrayListExtra("numbercontact") != null) {
//			ArrayList<String> numbers = data.getStringArrayListExtra("numbercontact");
			numbers = data.getStringArrayListExtra("numbercontact");
			Log.i("TAG", "==============numbers:" + numbers);
			if("".equals(numbers.toString())){
				shows = null;
				return;
			}
//			shows = getShows();
//			setContact();
			for(int i=0;i<numbers.size();i++){
				for(int j=0;j<numbersRecord.size();j++){
					if(numbers.get(i).equals(numbersRecord.get(j))){
						numbersRecord.remove(j);
					}
				}
			}
			numbers.addAll(numbersRecord);
			shows = getShows();
			mRecipientsEditor.setText(shows);
			getEidtSelection();
//			et_body.requestFocusFromTouch();
			Timer timer = new Timer();
	        timer.schedule(new TimerTask(){
	        @Override
	         public void run() {
	           InputMethodManager imm = (InputMethodManager)mTextEditor.getContext().getSystemService(INPUT_METHOD_SERVICE);
	           imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
	         }
	        }, 1000);
		}
		
	}
	private void setContact() {
		String old = mRecipientsEditor.getText().toString();
//		String phones = old + "," + shows;
//		mRecipientsEditor.setText(phones);
		String phones = old + "," + shows;
		mRecipientsEditor.setText(getPhones(phones));
		final InputMethodManager inputManager = (InputMethodManager)
        getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager == null || !inputManager.isFullscreenMode()) {
            mTextEditor.requestFocus();
        }
	}
	
	private String getPhones(String p) {
		StringBuffer result = new StringBuffer();
		HashMap<String, String> map = new HashMap<String, String>();
		String[] ps = p.split(",");
		int size = ps.length;
		for(int i = 0; i < size; i++) {
			String s = ps[i].trim();
			if(s == null || "".equals(s)) {
				continue;
			}
			String ph = getPhone(s);
			map.put(ph, ph);
		}
		int i = 0;
		for(Entry<String, String> entry : map.entrySet()) {
			if(i == 0) {
				result.append(entry.getValue());
			} else {
				result.append("," + entry.getValue());
			}
			i++;
		}
		return result.toString();
	}
	
	private String getPhone(String p) {
		if(p.contains("<")) {
			p = p.substring((p.indexOf("<") + 1));
			p = p.substring(0, p.lastIndexOf(">"));
			return getPhone(p);
		} else {
			return p;
		}
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		listName.clear();
		listName = getFileName();
		getRandom(-100, listName);
		//修改497错误
//		mGLSufaceViewRenderer = new HGLSufaceViewRenderer(this, fileName, null, ttfName);
//		glview.setRenderer(mGLSufaceViewRenderer);
	}
	
	
	/***
     * 用于显示电话和名字，追加输入号码
     * @return
     */
//	private String getShows(ArrayList<String> numbers) {
//		if(numbers == null || numbers.size() <= 0 ){
//			return "";
//		}
//		StringBuffer sff = new StringBuffer();
//		int size = numbers.size();
//		for(int i = 0; i < size; i++){
//			if(i == (size - 1)) {
//				sff.append(numbers.get(i));
//			} else {
//				sff.append(numbers.get(i)).append(",");
//			}
//		}
//		return sff.toString();
//	}
	
    /***
     * 用于显示电话和名字，追加输入号码
     * 格式: 移动<10086>,
     * @return
     */
	private String getShows() {
		if(numbers == null || numbers.size() <= 0 ){
			return "";
		}
		StringBuffer sff = new StringBuffer();
		String showName = "";
		String showAddress = "";
		for(int i=0;i<numbers.size();i++){
			if(!numbers.get(i).trim().equals("")){
//				showName = mAddressBookManager.getNameByNumber(numbers.get(i));
//				if(showName.equals(HConst.defaultName)){
//					showAddress = numbers.get(i);
//				}else{
//					showAddress = showName;
//				}
//				sff.append(showAddress).append(" <").append(numbers.get(i)).append(">");
				sff.append(numbers.get(i));
				if(i != numbers.size() - 1) {
					sff.append(",");
				}
			}
		}
//		if(sff.length() > 0){
//			return sff.substring(0,sff.length()-1).toString();
//		}
		return sff.toString();
	}
	
	@Override
	protected void hideHTalkTitle(boolean flag) {
		Log.i("TAG", "==========================" + flag);
		
		if(flag) {
			talk_title_layout.setVisibility(View.GONE);
			mTopPanel.setVisibility(View.VISIBLE);
			
			//解决talk里面添加主题而不是发送页面添加主题
			if(mRecipientsEditor == null) {
				bt_add.setVisibility(View.GONE);
	        	return;
	        }
			//解决发送成功之后，添加主题之后显示添加联系人按钮
			if(mRecipientsEditor.getVisibility() == View.GONE) {
				bt_add.setVisibility(View.GONE);
				return;
			}
			
			hideHTalkTitle = flag;
			
			if(bt_add != null) {
				bt_add.setVisibility(View.VISIBLE);
			}
			
			if(mid_layout != null) {
				mid_layout.setVisibility(View.VISIBLE);
				mid.setVisibility(View.VISIBLE);
			}
			
			if(mMsgListView != null) {
				mMsgListView.setVisibility(View.GONE);
			}
		} else {
			talk_title_layout.setVisibility(View.VISIBLE);
			mTopPanel.setVisibility(View.GONE);
			if(bt_add != null) {
				bt_add.setVisibility(View.VISIBLE);
			}
			
			if(mid_layout != null) {
				mid_layout.setVisibility(View.GONE);
				mid.setVisibility(View.GONE);
			}
			
			hideHTalkTitle = flag;
			initTitle();
		}

	}
	
	/***
	 * 呼叫
	 */
	private void context_call(String address) {
		new HCall(HTalkActivity.this, address, "");
	}
	
//	private void startPreviewActivity(int position) {
//		Intent intent = new Intent();
//		intent.putExtra("talkPosion", position);
//		intent.putExtra("address", addressList);
//		intent.putExtra("name", phoneList);
//		intent.setClass(HTalkActivity.this, HPreviewActivity.class);
//		startActivityForResult(intent, 0);
//	}
	
	private void splitStringSize(){
		Display dis = getWindowManager().getDefaultDisplay();
		if(dis.getWidth() >= 480){
			mScreenSize = 0;
			mNumber = 100;
		} else if(dis.getWidth() >= 320 && dis.getWidth() < 480){
			mScreenSize = 1;
			mNumber = 80;
		} else {
			mScreenSize = 2;
			mNumber = 70;
		}
	}
	
	/***
	 * 随机得到模板的名字
	 */
	private List<String> getFileName(){
		String path = null;
		if(SkinManage.mCurrentSkin.equals(HConst.DEFAULT_PACKAGE_NAME)){
			//默认皮肤
			if(mScreenSize == 1){
				path = "mold-m";
			} else if(mScreenSize == 2){
				path = "mold-l";
			} else {
				path = "mold";
			}
		}else{
			path = "mold";
		}
		List<String> list = new ArrayList<String>();
		String[] arrFile = null;
		try {
			arrFile = mContext.getAssets().list(path);
		} catch (IOException e) {
			e.printStackTrace();
			try {
				path = "mold";
				arrFile = mContext.getAssets().list(path);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		int size = arrFile.length;
		for (int i = 0; i < size; i++) {
			list.add(path + "/" + arrFile[i]);
		}
		return list;
	}
	
	private long msdIdFlag = -1;
	public void getRandom(long msdId, List<String> list){
		if(msdIdFlag != msdId) {
			msdIdFlag = msdId;
			//随机取一个
			Random random = new Random();
			int result = random.nextInt(list.size());
			fileName = list.get(result);
		}
	}
	
	/***
	 * @param str  字符串
	 * @param len  要切的每条的长度
	 * @return
	 */
	public String[] getTmpStr(String str){
		String[] tmp;
		if(str == null || "".equals(str))
		{
			tmp = new String[1];
			tmp[0] = "";
			return tmp;
		}
		int len = mNumber;
		int count = str.length() / len;
		if(str.length() % len == 0 ){
			if(count == 0){
				count = 1;
			}
			tmp = new String[count];
		}else{
			tmp = new String[count + 1];
		}
		for(int i = 0;i<tmp.length;i++){
			if(i == tmp.length -1){

				tmp[i] = str.substring(i * len  ,str.length());
			}else if(tmp.length == 0){
				tmp[i] = str;
			}else{
				tmp[i] = str.substring(i * len , (i + 1) * len);
			}
		}
		return tmp;
	}
	
	private void refreshUI() {
		try {
			if(talk_layout != null) {
				talk_layout.invalidate();
			}
			if(talk_title_layout != null) {
				talk_title_layout.invalidate();
			}
			if(bt_shortcut_key != null) {
				((SkinButton)bt_shortcut_key).invalidate();
			}
			if(mChageReadSms != null) {
				((SkinButton)mChageReadSms).invalidate();
			}
			if(mIvHead != null) {
				((SkinImageView)mIvHead).invalidate();
			}
			if(mTvName != null) {
				((SkinTextView)mTvName).invalidate();
			}
			if(tv_percent != null) {
				((SkinTextView)tv_percent).invalidate();
			}
			
			if(mTopPanel != null) {
				((SkinRelativeLayout)mTopPanel).invalidate();
			}
			if(bt_add != null) {
				((SkinButton)bt_add).invalidate();
			}
			if(mid != null) {
				((SkinLinearLayout)mid).invalidate();
			}
			if(topGrid != null) {
				((SkinLinearLayout)topGrid).invalidate();
			}
			if(tv_linkman != null) {
				((SkinImageView)tv_linkman).invalidate();
			}
			if(tv_linkman1 != null) {
				((SkinTextView)tv_linkman1).invalidate();
			}
			if(bottomGrid != null) {
				((SkinLinearLayout)bottomGrid).invalidate();
			}
			if(mFlashListView != null) {
				((SkinFrameLayout)mFlashListView).invalidate();
			}
			if(mBottomPanel != null) {
				((SkinLinearLayout)mBottomPanel).invalidate();
			}
			if(mSendButton != null) {
				((SkinButton)mSendButton).invalidate();
			}
			if(mTextCounter != null) {
				((SkinTextView)mTextCounter).invalidate();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	private Handler refreshHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			refreshUI();
		}
	};
	private float xx1, yy1, xx2;
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		changePage(v, event);
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xx1 = event.getX();
			yy1 = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			if(isMove) {
				return true;
			}
			xx2 = event.getX();
			try {
				Cursor cursor = mMsgListAdapter.getCursor();
				if (xx1 > xx2) {
					cursor.moveToPosition(curIndex);
					String type = cursor.getString(mColumnsMap.mColumnMsgType);
			        MessageItem msgItem = new MessageItem(mContext, type, cursor, mColumnsMap, null);
					String[] tmp = getTmpStr(msgItem.mBody);
					
					if(tmp.length - 1 > partIndex) {
						isPageMove = true;
						if (xx1 - xx2 < moveLength) {
							return false;
						}
						isMove = true;
						
						partIndex++;
						Log.i("TAG", "=============================1=if" + tmp[partIndex]);
						mGLSufaceViewRenderer.turning(fileName, ttfName, tmp[partIndex], true);
					} else {
						partIndex = 0;
						if(curIndex < talkIndex){//当前条数不是最后一条
							isPageMove = true;
							if (xx1 - xx2 < moveLength) {
								return false;
							}
							isMove = true;
							
							curIndex++;
							tv_percent.setText((curIndex + 1) + "/" + (talkIndex + 1));
							
							cursor.moveToPosition(curIndex);
							type = cursor.getString(mColumnsMap.mColumnMsgType);
					        msgItem = new MessageItem(mContext, type, cursor, mColumnsMap, null);
					        getRandom(msgItem.mMsgId, listName);
					        if("mms".equals(type)) {
					        	mMmsView.setVisibility(View.VISIBLE);
					        	String subject = cursor.getString(mColumnsMap.mColumnMmsSubject);
					        	if(null == subject)
					        	{
					        		subject = mContext.getString(R.string.no_subject);
					        		mMmsSubject.setText(subject);
					        	}
					        	else
					        	{
						        EncodedStringValue vv = new EncodedStringValue(
						        		cursor.getInt(mColumnsMap.mColumnMmsSubjectCharset),
				                        PduPersister.getBytes(subject));
						        mMmsSubject.setText(vv.getString());
					        	}
					        	glview.setVisibility(View.GONE);
					        } else {
					        	mMmsView.setVisibility(View.GONE);
					        	glview.setVisibility(View.VISIBLE);
					        	Log.i("TAG", "=============================1=else" + tmp[partIndex]);
					        	mGLSufaceViewRenderer.turning(fileName, ttfName, getTmpStr(msgItem.mBody)[0], true);
					        }
						}else{
							Toast.makeText(mContext, R.string.lastone, Toast.LENGTH_SHORT).show();
						}
					}
					return true;
				
				} else if(xx1 < xx2){
					cursor.moveToPosition(curIndex);
					String type = cursor.getString(mColumnsMap.mColumnMsgType);
			        MessageItem msgItem = new MessageItem(mContext, type, cursor, mColumnsMap, null);
					String[] tmp = getTmpStr(msgItem.mBody);
					
					if(partIndex > 0) {
						isPageMove = true;
						if (xx2 - xx1 < moveLength) {
							return false;
						}
						isMove = true;
						
						partIndex--;
						Log.i("TAG", "=============================2=if" + tmp[partIndex]);
						mGLSufaceViewRenderer.turning(fileName, ttfName, tmp[partIndex], true);
					} else {
						partIndex = 0;
						if(curIndex > 0){
							isPageMove = true;
							if (xx2 - xx1 < moveLength) {
								return false;
							}
							isMove = true;
							
							curIndex--;
							tv_percent.setText((curIndex + 1) + "/" + (talkIndex + 1));
							
							cursor.moveToPosition(curIndex);
							type = cursor.getString(mColumnsMap.mColumnMsgType);
					        msgItem = new MessageItem(mContext, type, cursor, mColumnsMap, null);
					        getRandom(msgItem.mMsgId, listName);
					        if("mms".equals(type)) {
					        	mMmsView.setVisibility(View.VISIBLE);
					        	String subject = cursor.getString(mColumnsMap.mColumnMmsSubject);
					        	if(null == subject)
					        	{
					        		subject = mContext.getString(R.string.no_subject);
					        		mMmsSubject.setText(subject);
					        	}
					        	else
					        	{
						        EncodedStringValue vv = new EncodedStringValue(
						        		cursor.getInt(mColumnsMap.mColumnMmsSubjectCharset),
				                        PduPersister.getBytes(subject));
						        mMmsSubject.setText(vv.getString());
					        	}
					        	glview.setVisibility(View.GONE);
					        } else {
					        	mMmsView.setVisibility(View.GONE);
					        	glview.setVisibility(View.VISIBLE);
					        	Log.i("TAG", "=============================2=else" + tmp[partIndex]);
					        	mGLSufaceViewRenderer.turning(fileName, ttfName, getTmpStr(msgItem.mBody)[0], true);
					        }
						}else{
							Toast.makeText(mContext, R.string.firstone, Toast.LENGTH_SHORT).show();
						}
					}
					return true;
				
				}
			} catch(Exception ex) {
				ex.printStackTrace();
				return false;
			}
			break;
		case MotionEvent.ACTION_UP:
			isMove = false;
			isPageMove = false;
			calcCornerFlag = false;
			break;
		}
		return false;
	}
	private void changePage(View v, MotionEvent event) {
		if(!isPageMove) {
			return;
		}
		int pageW = mPageWidget.getWidth();
		int pageH = mPageWidget.getHeight();
		if (v == glview) {
			if(!calcCornerFlag) {
				Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.page_bg);
				mCurPageBitmap = Bitmap.createScaledBitmap(temp, pageW, pageH, true);
				
				mPageWidget.abortAnimation();
				mPageWidget.calcCornerXY(xx1, yy1, pageW, pageH);
				
				mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
				
				calcCornerFlag = true;
			}
			if(event.getAction() == MotionEvent.ACTION_UP) {
				try {
					refreshUI();
					refreshHandler.sendMessageDelayed(refreshHandler.obtainMessage(), 300);
					refreshHandler.sendMessageDelayed(refreshHandler.obtainMessage(), 600);
					refreshHandler.sendMessageDelayed(refreshHandler.obtainMessage(), 900);
					refreshHandler.sendMessageDelayed(refreshHandler.obtainMessage(), 1200);
					refreshHandler.sendMessageDelayed(refreshHandler.obtainMessage(), 1500);
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			mPageWidget.doTouchEvent(event, pageW, pageH);
		}
	}
	
	private OnClickListener mmsView = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			try {
				Cursor cursor = mMsgListAdapter.getCursor();
				cursor.moveToPosition(curIndex);
				String type = cursor.getString(mColumnsMap.mColumnMsgType);
		        MessageItem msgItem = new MessageItem(mContext, type, cursor, mColumnsMap, null);
				
				MessageUtils.viewMmsMessageAttachment(mContext, ContentUris.withAppendedId(Mms.CONTENT_URI, msgItem.mMsgId), null);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	};
	
	@Override
	protected void onPause() {
		super.onPause();
		shows = getShows();
		glview.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//点击新建按钮跳转到talk页面；点击会话列表项进来没有历史会话的talk页面；转发界面；
		 if(send  &&  mRecipientsEditor != null && mRecipientsEditor.getVisibility() == View.VISIBLE)
		{
			mid_layout.setVisibility(View.VISIBLE);
			mid.setVisibility(View.VISIBLE);
		}
		//有会话历史的联系人talk页
		else if(mRecipientsEditor == null || mRecipientsEditor.getVisibility() == View.GONE)
		{
			mid_layout.setVisibility(View.GONE);
			mid.setVisibility(View.GONE);
		}



		glview.onResume();
		
		new Thread()
		{
			public void run() {
				Looper.prepare();
				toast_change_tem();
				Looper.loop();
			};
		}.start();
	}
	/**
	 * 检查是否已经十天没有更换模版了
	 */
	private void toast_change_tem()
	{
		String beforeTime = HConst.getTheCheckTemTiem(HTalkActivity.this);
		
		if(beforeTime == null || beforeTime.equals(""))
		{
			return;
		}
		
		if(HConst.checkChangeTemIsToast(new Date(beforeTime)))
		{
			//Toast.makeText(HThreadActivity.this, getString(R.string.change_tem_toast), Toast.LENGTH_LONG).show();
			HConst.showChangeTemDialog(HTalkActivity.this);
			
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(updateDialog);
		
		//653 转发问题
//		glview.destroyDrawingCache();
//		mGLSufaceViewRenderer = null;
		isDraft = false;
		
		try {
			if(mMsgListAdapter.getCursor() != null) {
				mMsgListAdapter.getCursor().close();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	protected void  ensureCorrectButtonHeight(){
		
	}
	protected void resetCounter() {
		mTextCounter.setVisibility(View.VISIBLE);
    }
	private boolean mIsLongsms = false;
	protected void textChangeBodyView() {
		
		mTextEditor.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,int count) 
			{
				if(s.toString().length() >= 480)
				{
					if(!mIsLongsms)
					{
						mIsLongsms = true;
					}
				}
					isEnabled(s);
			}
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {	}
			public void afterTextChanged(Editable s) {}
			private void isEnabled(CharSequence s) {
				if(s.toString().length()>970){
					Toast.makeText(HTalkActivity.this, R.string.textTooLong,Toast.LENGTH_LONG).show();
				}
				mTextCounter.setText(ToolsUtil.getCountString(mWorkingMessage, s.toString()));
			}
		});
	}
	protected void updateCounter(CharSequence text, int start, int before, int count) {
        WorkingMessage workingMessage = mWorkingMessage;
        if (workingMessage.requiresMms()) {
            // If we're not removing text (i.e. no chance of converting back to SMS
            // because of this change) and we're in MMS mode, just bail out since we
            // then won't have to calculate the length unnecessarily.
            final boolean textRemoved = (before > count);
            if (!textRemoved) {
                setSendButtonText(workingMessage.requiresMms());
                return;
            }
        }

        int[] params = SmsMessage.calculateLength(text, false);
            /* SmsMessage.calculateLength returns an int[4] with:
             *   int[0] being the number of SMS's required,
             *   int[1] the number of code units used,
             *   int[2] is the number of code units remaining until the next message.
             *   int[3] is the encoding type that should be used for the message.
             */
        int msgCount = params[0];
        int remainingInCurrentMessage = params[2];

        // Show the counter only if:
        // - We are not in MMS mode
        // - We are going to send more than one message OR we are getting close
        boolean showCounter = false;
        if (!workingMessage.requiresMms() &&
                (msgCount > 1 ||
                 remainingInCurrentMessage <= CHARS_REMAINING_BEFORE_COUNTER_SHOWN)) {
            showCounter = true;
        }

        setSendButtonText(workingMessage.requiresMms());

//        if (showCounter) {
//            // Update the remaining characters and number of messages required.
//            String counterText = msgCount > 1 ? remainingInCurrentMessage + " / " + msgCount
//                    : String.valueOf(remainingInCurrentMessage);
//            mTextCounter.setText(counterText);
//            mTextCounter.setVisibility(View.VISIBLE);
//        } else {
//            mTextCounter.setVisibility(View.GONE);
//        }
    }
	
	private void init(){
		mid_layout = (ScrollView) findViewById(R.id.mid_layout);
		mid = (LinearLayout) findViewById(R.id.mid);
		tv_linkman = (ImageView) findViewById(R.id.linkmanImageView);
		tv_linkman1 = (TextView) findViewById(R.id.linkmanImageView1);
		tv_linkman.setVisibility(View.GONE);
		tv_linkman1.setVisibility(View.GONE);
		numbersRecord = new ArrayList<String>();
		mAddressBookManager = new HAddressBookManager(this);
//		tv_historyLinkman = (TextView) findViewById(R.id.tv_history);
		//textChangeBodyView();
		textChangeNumberView();
		selectDB();
		//caogao();
		//onClickSend();
		//onClickAdd();
//		if(getIntent().getBooleanExtra("setting", false)){
//			et_body.setText(getString(R.string.inviteBody));
//		}
	}
	
    /**
     * @param 查询历史记录
     */
	private void selectDB() {
		HDatabaseHelper dbOpenHelper = new HDatabaseHelper(this);
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		histroyLinkmanList = new ArrayList<String>();
		
		Cursor cursor = db.query(HHistoryLinkman.TABLE_NAME, new String[]{HHistoryLinkman.ID+ " AS _id ",HHistoryLinkman.PHONE_NUMBER},null,null,null,null,null);
		while(cursor.moveToNext()){
			String str = cursor.getString(cursor.getColumnIndex(HHistoryLinkman.PHONE_NUMBER));
			histroyLinkmanList.add(str);
		}
		if(histroyLinkmanList.size() > 0){//隐藏分割线
//			tv_historyLinkman.setVisibility(View.VISIBLE);
			tv_linkman1.setVisibility(View.VISIBLE);
		}else{
//			tv_historyLinkman.setVisibility(View.GONE);
			tv_linkman1.setVisibility(View.GONE);
		}
//		if(cursor.getCount() > 0){
//			histroyLinkmanList.add("&^%$!@#");
//		}
		histroyLinkmanList.add("&^%$!@#");
		cursor.close();
		db.close();
	}
	
	/***
	 * 
	 * @param context
	 * @param dipValue
	 * @return 由px转为dip
	 */
	private int dip2px(Context context, float dipValue){    
		final float scale = context.getResources().getDisplayMetrics().density;    
		return (int)(dipValue * scale + 0.5f);    
	} 
	
	private String selectName(String str){
		String s = "";
		if(str.contains("|")){
			for(int i = 0;i < str.split("\\|").length;i++){
				s = s + str.split("\\|")[i]+ ",";
			}
		}else{
			s  = str;
		}
		if(mAddressBookManager == null)
		{
			Log.d("Tag","mAddressBokkManager ========================== null");
		}else
		{
			Log.d("Tag","mAddressBokkManager !!!!!!!!!!!!!!========================= null");
		}
		Log.d("selectName  ", s);
		//&^%$!@#
		String name = mAddressBookManager.getNameByNumber(s);
		String []nameArray = name.split(",");
		String []sArray = s.split(",");
		StringBuffer sbf = new StringBuffer();
		for(int i=0;i<nameArray.length;i++){
			if(!nameArray[i].equals(HConst.defaultName)){
				sbf.append(nameArray[i]).append(",");
			}else{
				sbf.append(sArray[i]).append(",");
			}
		}
		String showName = "";
		if(sbf.length()>0){
			showName = sbf.substring(0,sbf.length()-1);
		}
		return showName;
	}
	
	/***
	 * 绘制历史记录以及当前输入的联系人
	 */
	private void drawButton(){
	        LinearLayout layout1 = null;
	        int lineNum = 4;
	        int fontSize = 13;
	        int heigh = dip2px(this, 30);
	        int with = dip2px(this, 75);
	        int margin = dip2px(this, 3);
	        int marginTop = dip2px(this, 5);
	        
	        if(topGrid != null){
	        	topGrid.removeAllViews();
	        }
	        if(bottomGrid != null){
	        	bottomGrid.removeAllViews();
	        }
	        topGrid = (LinearLayout) findViewById(R.id.topGrid);
	        bottomGrid = (LinearLayout) findViewById(R.id.bottomGrid);
	        if(numbers.isEmpty()){
	        	  for (int i = 0; i < numbersRecord.size(); i++) {
	        		 final int curIndex = i;
	  				if (i % lineNum == 0) {
	  					layout1 = new LinearLayout(this);
	  					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);  
	  					lp.setMargins(0, marginTop, 0, 10);
	  					layout1.setLayoutParams(lp);
	  					layout1.setOrientation(LinearLayout.HORIZONTAL);
	  					topGrid.addView(layout1);
	  				}
	  				
	  				SkinTextView tv = new SkinTextView(this);
	  				tv.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							butclic(curIndex);
							
						}
					});
	  				tv.setPadding(3, 0, 3, 0);
	  				tv.setGravity(Gravity.CENTER);
	  				tv.setText(selectName(numbersRecord.get(i)));
	  				tv.setTextSize(fontSize);
	  				tv.setTextColor(getResources().getColor(R.color.editsms_color));
	  				tv.setSingleLine();
	  				tv.setHorizontalFadingEdgeEnabled(true);// 设置view在水平滚动时，水平边是否淡出。
	  				tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
	  				tv.setBackgroundResource(R.drawable.history);
	  				layout1.addView(tv);
	  				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(with, heigh);  
	  				lp.gravity = Gravity.CENTER;  
	  				lp.setMargins(margin, 0, 0, 0);
	  				tv.setLayoutParams(lp);  

	  			}
	        }else{
	        	  for (int i = 0; i < numbers.size(); i++) {
	        		  final int curIndex = i;
	  				if (i % lineNum == 0) {
	  					layout1 = new LinearLayout(this);
	  					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);  
	  					lp.setMargins(0, marginTop, 0, 10);
	  					layout1.setLayoutParams(lp);
	  					layout1.setOrientation(LinearLayout.HORIZONTAL);
	  					topGrid.addView(layout1);
	  				}
	  				SkinTextView tv = new SkinTextView(this);
	  				tv.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							butclic(curIndex);
							
						}
					});
	  				tv.setPadding(3, 0, 3, 0);
	  				tv.setGravity(Gravity.CENTER);
	  				Log.d("tv_value--------------非历史记录", numbers.get(i));
	  				tv.setText(selectName(numbers.get(i)));
	  				tv.setTextSize(fontSize);
	  				tv.setTextColor(getResources().getColor(R.color.editsms_color));
	  				tv.setSingleLine();
	  				tv.setHorizontalFadingEdgeEnabled(true);// 设置view在水平滚动时，水平边是否淡出。
	  				tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
	  				tv.setBackgroundResource(R.drawable.history);
	  				layout1.addView(tv);
	  				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(with, heigh);  
	  				lp.gravity = Gravity.CENTER;  
	  				lp.setMargins(margin, 0, 0, 0);
	  				tv.setLayoutParams(lp);  

	  			}
	        	
	        }
	        //历史记录
	        for ( int i = 0; i < histroyLinkmanList.size(); i++) {
	        	final int curIndex = i;
	        	if (i % lineNum == 0) {
	        		layout1 = new LinearLayout(this);
	        		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);  
					lp.setMargins(0, marginTop, 0, marginTop);
					layout1.setLayoutParams(lp);
	        		layout1.setOrientation(LinearLayout.HORIZONTAL);
	        		bottomGrid.addView(layout1);
	        	}
	        	SkinTextView tv = new SkinTextView(this);
	        	registerForContextMenu(tv);
	        	tv.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						if(curIndex == histroyLinkmanList.size()-1){
							return true;
						}
						setIndex(curIndex);
						return false;
					}
				});
	        	tv.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//mStatistics.add(HStatistics.Z4_4, "", "", "");
						if(curIndex == histroyLinkmanList.size()-1){
//							if(mIsRun){
//								mIsRun = false;
//								Intent intent = getIntent();
//								intent.setClass(HTalkActivity.this, HContactActivity.class);
//								startActivityForResult(intent, 11);
//								mIsRun = true;
//								return;
//							}
							// 弹出通讯录列表
							Intent intent = new Intent();
							intent.setClass(HTalkActivity.this, HContactActivity.class);
							startActivityForResult(intent, 11);
							return;
						}
						numbers.clear();
						String [] str = (histroyLinkmanList.get(curIndex).toString()).split(",");
						for(int i=0;i<str.length;i++){
							numbers.add(str[i]);
						}
						
						for(int i=0;i<numbers.size();i++){
							for(int j=0;j<numbersRecord.size();j++){
								if(numbers.get(i).equals(numbersRecord.get(j))){
									numbersRecord.remove(j);
								}
							}
						}
						for(int i=0;i<numbersRecord.size();i++){
							numbers.add(0,numbersRecord.get(i));
						}
						
						shows = getShows();
						mRecipientsEditor.setText(shows);
						//获取光标位置
//						et_address.requestFocusFromTouch();
						getEidtSelection();
						
					}
				});
	        	tv.setPadding(3, 0, 3, 0);
	        	tv.setGravity(Gravity.CENTER);
	        	tv.setTextSize(fontSize);
	        	tv.setTextColor(getResources().getColor(R.color.editsms_color));
	        	tv.setHorizontalFadingEdgeEnabled(true);// 设置view在水平滚动时，水平边是否淡出。
	        	tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
	        	tv.setSingleLine(true);
	        	String name= selectName(histroyLinkmanList.get(i));
	        	HLog.d("历史记录", "==============================================");
	        	if(!name.equals("&^%$!@#")){
	        		tv.setText(name);
	        		tv.setBackgroundResource(R.drawable.history);
	        		System.out.println("tv.setBackgroundResource(R.drawable.history) + name: " + name);
	        		//tv_linkman.setVisibility(View.VISIBLE);
	        	}else{
	        		System.out.println("tv.setBackgroundResource(R.drawable.add_contact)");
	        		tv.setBackgroundResource(R.drawable.add_contact);
//	        		tv.setVisibility(View.GONE);
//	        		tv.setEnabled(false);
	        		
	        	}
	        	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(with, heigh);  
				lp.gravity = Gravity.CENTER;  
				lp.setMargins(margin, 0, 0, 0);
				tv.setLayoutParams(lp);  
	        	layout1.addView(tv);
	        	
	        }
	}
	
    int index = 0;
	public void setIndex(int index){
		this.index = index;
	}
	
	public int getIndex(){
		return index;
	}
	
	private void butclic(final int position){
		final AlertDialog.Builder alertDialog = new AlertDialog.Builder(HTalkActivity.this);
		alertDialog.setItems(R.array.linkManString, new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			try{
				if(arg1 == 0){	
					if(numbers.size() == 0){
						String str = "";
						numbersRecord.remove(position);
						int size = numbersRecord.size();
						for(int i = 0;i < size;i++){
							str += (numbersRecord.get(i) + ",");
						}
						mRecipientsEditor.setText(str);
					}else{
						listRemove(position);
						shows = getShows();
						mRecipientsEditor.setText(shows);
					}
				}
				arg0.cancel();
			}catch(Exception ex){
			}
		}
	});
	AlertDialog ad = alertDialog.create();
	ad.show();
	}
	
	private void listRemove(int position){
		if(numbers.size() > position){
			numbers.remove(position);
		}
	}
	
	private void textChangeNumberView() {
		if(mRecipientsEditor == null){return;}
		mRecipientsEditor.addTextChangedListener(new TextWatcher() {
			/**
			 * count 0 删除 1添加
			 * start  总数
			 * before 和cont相反
			 * 写  beforeTextChanged ---->>>onTextChanged------>>>afterTextChanged
			 */
			public void onTextChanged(CharSequence s, int start, int before,int count) {
				if(s.toString().length() > 970){
					Toast.makeText(HTalkActivity.this, R.string.textTooLong,Toast.LENGTH_LONG).show();
				}
				//et_body.toString();
				if(count == 0){//删除的时候走
					numbersRecord.clear();
					numbers.clear();
				}
					isShowNumber();
				}
			
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}
			public void afterTextChanged(Editable s) {
			}

		});
	}
	
	private void isShowNumber() {
//		if (getSendNumbers().trim().length() == 0){
//			Toast.makeText(HEditSmsActivity.this, R.string.linkmanInvalid,Toast.LENGTH_LONG).show();
//			return;
//		} else if(et_body.getText().length() <= 0) {
//			Toast.makeText(HEditSmsActivity.this, R.string.textTooLong,Toast.LENGTH_LONG).show();
//			return;
//		}
//		if(getSendNumbers() == null || "".equals(getSendNumbers())) {
//			return;
//		}
		if(histroyLinkmanList.size() > 0){
			tv_linkman1.setVisibility(View.VISIBLE);
		}
	    numbersRecord.clear();
		for(int i=0;i<getSendNumbers().split(",").length;i++){
			if(!"".equals(getSendNumbers().split(",")[i])){
				numbersRecord.add(getSendNumbers().split(",")[i]);
			}
		}
		numbers.clear();
		numbers.addAll(numbersRecord);
		drawButton();
	}
	
	/***
	 * 获取电话号码
	 * @return
	 */
	private String getSendNumbers(){
		if(mRecipientsEditor == null) {
			return "";
		}
		String address = mRecipientsEditor.getText().toString();
		if(address == null || address.trim().length() == 0){
			return "";
		}
		StringBuffer sff = new StringBuffer();
		String[] shows = address.replace(" ", "").split(",");
		for(String s : shows){
			try{
				if(!s.equals("")){
					sff.append(jixi(s));
					sff.append(",");
				}
				
			}catch(Exception ex){
				ex.printStackTrace();
				continue;
			}
		}
		if(sff.length() != 0){
			return sff.substring(0,sff.length()-1);
		}else{
			return "";
		}
	}
	
	/***
	 * 解析
	 * @param s
	 * @return
	 */
	private String jixi(String s){
		int start = s.lastIndexOf('<');
		int end = s.lastIndexOf('>');
		String rvl = "";
		if(end >= 0){
			if(start < end){
				//如果< 在 >前
				rvl = s.substring(start + 1, end).replace(" ", "").replace("-", "");
			}
		}else if(start >= 0){
			rvl = s.substring(start + 1);
		}else {
			rvl = s;
		}
		if(rvl.startsWith("+86")){
			rvl = rvl.substring(3);
		}
//		if(!rvl.matches("\\d*")){//0-9
//			return "";
//		}
		return rvl;
	}
    
	/***
	 * @param 获取光标位置
	 */
	private void getEidtSelection() {
		try{
		Editable ea= mRecipientsEditor.getText();  //etEdit为EditText
		Selection.setSelection(ea, ea.length()); 
		}catch(Exception e){
		}
	}
	
	/***
	 * 保存发送联系人历史记录
	 */
	@Override
	public void insertDataToLinkMan() {
		super.insertDataToLinkMan();
		//转发界面点击发送按钮之后不隐藏历史联系人区域
		if(mExitOnSent)
		{	
			mid_layout.setVisibility(View.VISIBLE);
			mid.setVisibility(View.VISIBLE);
		}else if(send)
		{
			mid_layout.setVisibility(View.GONE);
			mid.setVisibility(View.GONE);
		}
		StringBuffer address = new StringBuffer("");
		if(numbers == null || numbers.size() == 0)
		{
			return;
		}
		final int length = numbers.size();
		for(int i = 0; i < length; i ++ )
		{
			address.append(numbers.get(i));
			if((i + 1) < length)
			{
				address.append(",");
			}
		}
		
		HHistoryLinkman hl = new HHistoryLinkman(getApplication());
		hl.addDistoryLinkManList(address.toString());
	}
	
//	private void deletcDB(String number,boolean isdeleteAll,int index) {
//		HDatabaseHelper dbOpenHelper = new HDatabaseHelper(this);
//		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
//		if(isdeleteAll){
//			db.delete(HHistoryLinkman.TABLE_NAME, null, null);
//			histroyLinkmanList.clear();
//			tv_linkman1.setVisibility(View.GONE);
////			tv_historyLinkman.setVisibility(View.GONE);
//		}else {
//			Cursor cursorId = db.query(HHistoryLinkman.TABLE_NAME, new String[]{HHistoryLinkman.ID},HHistoryLinkman.PHONE_NUMBER + " = '" + number + "'", null, null, null, null);
//			int temp = index;
//			cursorId.moveToNext();
//			String id = cursorId.getString(cursorId.getColumnIndex(HHistoryLinkman.ID));
//			db.delete(HHistoryLinkman.TABLE_NAME, HHistoryLinkman.ID + " = " + id, null);
//			histroyLinkmanList.remove(temp);
//			if(histroyLinkmanList.size() == 0){
////				tv_historyLinkman.setVisibility(View.GONE);
//			}	
//			if(histroyLinkmanList.size() > 0){
//				tv_linkman1.setVisibility(View.VISIBLE);
//			}else{
//				tv_linkman1.setVisibility(View.GONE);
//			}
//			
//			cursorId.close();
//		}
//		drawButton();
//		db.close();
//	}
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if(!HConst.iscollect) {
			if(flash_width <=0 || flash_height <= 0) {
				int temp = mHSharedPreferences.getReadSmsType();
				if(temp == 0 && !mHSharedPreferences.getFlashSet()){
					Editor editor = preference.edit();
					editor.putInt("flash_width", glview.getWidth());
					editor.putInt("flash_height", glview.getHeight());
					editor.commit();
					mHSharedPreferences.setFlashSet();
				}
			}
			if(flash_width <= 0 || flash_height <= 0) {
				if(glview.getWidth() <= 0 || glview.getHeight() <=0) {
					return;
				}
				super.onWindowFocusChanged(hasFocus);
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(glview.getWidth(), glview.getHeight());
				glview.setPadding(0, 0, 0, 0);
				glview.setLayoutParams(lp);
			} else {
				super.onWindowFocusChanged(hasFocus);
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(flash_width, flash_height);
				glview.setPadding(0, 0, 0, 0);
				glview.setLayoutParams(lp);
			}
		}
	}
	
	
}
