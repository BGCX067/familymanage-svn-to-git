package com.haolianluo.sms2;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SkinRelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.data.HSharedPreferences;
import com.haolianluo.sms2.model.HAddressBookManager;
import com.haolianluo.sms2.model.HCollectTable;
import com.haolianluo.sms2.model.HResDatabaseHelper;
import com.haolianluo.sms2.model.HResProvider;
import com.haolianluo.sms2.model.HSms;
import com.haolianluo.sms2.model.HStatistics;
import com.haolianluo.sms2.model.HThread;
import com.haolianluo.sms2.model.HThreadManager;
import com.lianluo.core.cache.CacheManager;
import com.lianluo.core.net.download.DLManager;
import com.lianluo.core.skin.SkinManage;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;

public class HThreadActivity extends SkinActivity {
	
	private static final String TAG = "HThreadActivity";
	
	/** 短信列表 */
	private ListView lv_thread = null;
	private HThreadManager mThreadManager = null;
	private SkinRelativeLayout skinLinearLayout = null;
	private int mPosition = -1;
	private RelativeLayout rl_title;
	private RelativeLayout rl_search;
	private EditText et_search;
	private StringBuffer sb = new StringBuffer();
	/**开始大小*/
	private int searchStartSize = 0;
	/**收藏*/
	private HCollectTable collectTable = null;
	private HStatistics mStatistics;
	
	private Button mEditsms;//写短信
	
	private static int mIndex = 0;
	public static boolean isGo = false;
	private boolean isExit = false;
	private HUpdateTools hUpdateTools;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		HLog.i("HThreadActivity onCreate");
		mStatistics = new HStatistics(this);
		hUpdateTools = new HUpdateTools(this);
		//初始化缓存
		CacheManager.newInstance().openCache(getApplicationContext());
		// 初始化当前使用皮肤包名
		//new SkinBussiness(this ,HConst.ACTION,new HSkinReceiver(this,HConst.APP_KEY)).initSkinPackageName(HConst.TYPE_SKIN);
		HConst.markActivity = 1;
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
		boolean is = ToolsUtil.isServiceWork(this,"com.haolianluo.sms2.HService");
		if(!is){
			Intent intent = new Intent();
			intent.setClass(this, HService.class);
			startService(intent);
		}
		Intent intent = getIntent();
		boolean is1 =  intent.getBooleanExtra("dialog", false);
		mStatistics.add(HStatistics.Z0, String.valueOf(System.currentTimeMillis()), "","");
		if(!is1){
			mStatistics.add(HStatistics.Z2, "", "", "");
		}
		setContentView(R.layout.hthread);

	}
	
	@Override
	protected void onStart() {
		//getLayout();
		super.onStart();
		//需要进行强制更新并且是第二次
		HLog.d("isUpdate", String.valueOf(hUpdateTools.getIsUpdate()));
		HLog.d("isFirst", String.valueOf(hUpdateTools.getIsFirstUpdate()));
		if(hUpdateTools.getIsUpdate() && hUpdateTools.getIsFirstUpdate() == 2)
		{
			hUpdateTools.alertUpdate_Exit();
		}
		
		init();
		if(HConst.isSearchActivity){
			et_search.setText(sb.toString());
		}
		lv_thread.setSelection(mIndex);
	}
   
	private void getLayout() {
		if(skinLinearLayout != null){
			skinLinearLayout.removeAllViews();
		}
		skinLinearLayout = (SkinRelativeLayout)SkinRelativeLayout.inflate(this, R.layout.hthread, null);
		setContentView(skinLinearLayout);
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		Intent intent = getIntent();
		boolean is =  intent.getBooleanExtra("dialog", false);
		HLog.i("isExit ==" + isExit + "is" + is);
		if(isExit && !is){
			isExit = false;
			mStatistics.add(HStatistics.Z0, String.valueOf(System.currentTimeMillis()), "","");
			mStatistics.add(HStatistics.Z2, "", "", "");
		}
		HConst.markActivity = 1;
	}

	public void onDestroy() {
		super.onDestroy();
		HLog.i("HThreadActivity onDestroy");
		
		//mThreadManager.clearThreadAdapter();
//		if(mStatistics != null){
//			mStatistics.add(HStatistics.Z0_1, "", String.valueOf(System.currentTimeMillis()), "");
//		}
		//HConst.markActivity = -1;
		CacheManager.newInstance().closeCache();
		//SharedPreferences pref = getSharedPreferences(HConst.PREF_USER, Context.MODE_PRIVATE);
		//pref.edit().putBoolean(HConst.USER_KEY_LOGIN, false).commit();
		//pref.edit().putString(HConst.USER_KEY_PHONE, "").commit();
	}
	
	private void init() {
		HConst.str_back = getIntent().getStringExtra("back");
		if(mThreadManager == null){
			mThreadManager = new HThreadManager(this.getApplication());
			HConst.isHtc = mThreadManager.IsHTC();
		}
		//显示列表
		lv_thread = (ListView) findViewById(R.id.lv_thread_list);
		lv_thread.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				startActivity(position);
			}
		});
		//写信息
		mEditsms = (Button) findViewById(R.id.bt_editsms);
		lv_thread.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
				return false;
			}
		});
		registerForContextMenu(lv_thread);
		
		// 当前短信
		final Button bt_threadSms = (Button) findViewById(R.id.bt_threadsms);
		// 收藏短信
		final Button bt_collection = (Button) findViewById(R.id.bt_collection);
		
		bt_threadSms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!HConst.iscollect){
					return;
				}
				HConst.iscollect = false; 
				bt_threadSms.setBackgroundResource(R.drawable.sms_on);
				bt_collection.setBackgroundResource(R.drawable.collect);
				//flipperAnimate(0);
				smsAdapter();
			}
		});

		// 收藏短信按键
		if(!HConst.iscollect){
			bt_threadSms.setBackgroundResource(R.drawable.sms_on);
			bt_collection.setBackgroundResource(R.drawable.collect);
			smsAdapter();
		}else{
			bt_threadSms.setBackgroundResource(R.drawable.sms);
			bt_collection.setBackgroundResource(R.drawable.collect_on);
			collectAdapter();
		}
		bt_collection.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(HConst.iscollect){
					return;
				}
				HConst.iscollect = true;
				if(HConst.isMark == true){
					mThreadManager.cancel_mark();
					HConst.isMark = false;
					mEditsms.setVisibility(View.VISIBLE);
				}
				if(HConst.isSearchActivity == true){
					HConst.isSearchActivity = false;
					rl_search.setVisibility(View.GONE);
					rl_title.setVisibility(View.VISIBLE);
				}
				bt_threadSms.setBackgroundResource(R.drawable.sms);
				bt_collection.setBackgroundResource(R.drawable.collect_on);
				//flipperAnimate(1);
				collectAdapter();
			}
		});
		
		//快捷菜单
		Button shortcut = (Button) findViewById(R.id.bt_shortcut);
		shortcut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mStatistics.add(HStatistics.Z3,"","","");
				new HShortcutDialog(HThreadActivity.this); 
			}
		});
		if(HConst.iscollect || HConst.isSearchActivity || HConst.isMark){
			mEditsms.setVisibility(View.GONE);
		}else{
			mEditsms.setVisibility(View.VISIBLE);
		}
		
		mEditsms.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mStatistics.add(HStatistics.Z4_1, "", "", "");
				Intent intent = new Intent();
				HConst.markActivity = 1;
				intent.setClass(HThreadActivity.this, HEditSmsActivity.class);
				startActivity(intent);
//				testServer();
			}
		});
		
		et_search = (EditText) findViewById(R.id.et_search);
		rl_title = (RelativeLayout) findViewById(R.id.rl_title);
		rl_search = (RelativeLayout) findViewById(R.id.rl_search);
		if(HConst.isSearchActivity){
			rl_search.setVisibility(View.VISIBLE);
			rl_title.setVisibility(View.GONE);
		}else{
			rl_search.setVisibility(View.GONE);
			rl_title.setVisibility(View.VISIBLE);
		}
		Button bt_clear = (Button) findViewById(R.id.bt_clear);
		bt_clear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				et_search.setText("");
				sb.delete(0, sb.length());
				mThreadManager.getSearchAdapter().clear();
				searchStartSize = mThreadManager.toSearchAdapter( mThreadManager.getSearchAdapter());
				mThreadManager.ncThread();
			}
		});
		
		if(getIntent().getStringExtra("notification") != null){
			HSms.notification_click();
		}
	}
	
	

	private void collectAdapter(){
		collectTable = new HCollectTable(HThreadActivity.this);
	    collectTable.loadCollectList();
		lv_thread.setAdapter(mThreadManager.getCollectAdapter());
		mThreadManager.getCollectAdapter().notifyDataSetChanged();
	}
	
	private void smsAdapter(){
		if(HConst.isSearchActivity){
			lv_thread.setAdapter( mThreadManager.getSearchAdapter());
		}else{
			 HSharedPreferences sharedPreferences = new HSharedPreferences(getApplication());
			if(mThreadManager.getThreadAdapter() != null && sharedPreferences.getIsReadBuffer()){
				HLog.i("不用读取数据库");
				lv_thread.setAdapter(mThreadManager.getThreadAdapter());
				return;
			}
			mThreadManager.setThreadAdapter((HThreadAdapter) mThreadManager.loadThreadList());
			lv_thread.setAdapter(mThreadManager.getThreadAdapter());
		}
	}
	
    
	/**创建上下文菜单*/
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		HAddressBookManager abk = new HAddressBookManager(this);
		if(!HConst.iscollect){
			int getIndex = getSearchIndex(info.position);
			String appandName = abk.getAppendName(mThreadManager.getName(getIndex), mThreadManager.getAddress(getIndex));
			menu.setHeaderTitle(appandName);
			//menu.add(0, 1, 0, R.string.lookUp);
			menu.add(0, 2, 0, R.string.reply);
			menu.add(0, 4, 0, R.string.call);
			menu.add(0, 3, 0, R.string.delete);
			List<String> list = mThreadManager.isGetName(getIndex);
			if(list.size() > 0){
				menu.add(0, 6, 0, R.string.addcontact);
			}
		}else{
			menu.add(0, 1, 1, R.string.reply);//回复
			menu.add(0, 2, 2, R.string.call);//呼叫
			menu.add(0, 3, 3, R.string.cancelCollect);//取消收藏
			List<String> list = collectTable.isGetName((HThreadAdapter)lv_thread.getAdapter(),info.position);
			if(list.size() > 0){
				menu.add(0, 4, 4, R.string.addcontact);//保存联系人
			}
		}
		
	}
	
	private int getSearchIndex(int position){
		if(HConst.isSearchActivity){
			int gindex = -1;
			if(!HConst.iscollect){
				gindex = mThreadManager.getPosition(mThreadManager.getSearchAdapter().get(position).thread.address);
			}else{
				gindex = mThreadManager.getCollectPosition(mThreadManager.getSearchAdapter().get(position).thread.address);
			}
			if(gindex != -1){
				return gindex;
			}else{
				return 0;
			}
		}else{
			return position;
		}
	}
	
	/**上下文菜单点击事件*/
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		mPosition = info.position;
		if(!HConst.iscollect){
			switch (item.getItemId()) {
			case 1:
				//mStatistics.add(HStatistics.Z6, "", "", "");
				//startActivity(mPosition);
				break;
			case 2:
				startActivity(mPosition);
				break;
			case 3:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.isdelete_a_sms);
				builder.setCancelable(false);
				builder.setPositiveButton(R.string.confirm,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								if(HConst.isSearchActivity){
									mThreadManager.deleteThreadItem(mPosition,getSearchIndex(mPosition));
								}else{
									mThreadManager.deleteThreadItem(mPosition,-1);
								}
								Toast.makeText(HThreadActivity.this, R.string.deleteSmsOk,  Toast.LENGTH_SHORT).show();
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
				break;
			case 4:
				//mStatistics.add(HStatistics.Z7_4, "", "", "");
				context_call();
				break;
			case 6:
				int getIndex = getSearchIndex(mPosition);
				String []address = mThreadManager.isGetName(getIndex).toArray(new String[]{});
				new HAddContact(HThreadActivity.this, address);
				break;
			}
			
		}else{
			switch (item.getItemId()) {
			//回复、呼叫、取消收藏、保存联系人
			case 1:
				mStatistics.add(HStatistics.Z5_9_1, "", "", "");
				context_forward(mPosition);
				break;
			case 2:
				mStatistics.add(HStatistics.Z5_9_2, "", "", "");
				context_call();
				break;
			case 3:
				mStatistics.add(HStatistics.Z5_9_3, "", "", "");
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.celCollect);
				builder.setCancelable(false);
				builder.setPositiveButton(R.string.confirm,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								collectTable.deleteNote(mPosition);
								mThreadManager.getCollectAdapter().notifyDataSetChanged();
								Toast.makeText(HThreadActivity.this, R.string.deleteSmsOk,  Toast.LENGTH_SHORT).show();
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
				break;
			case 4:
				mStatistics.add(HStatistics.Z5_9_4, "", "", "");
				String []address = collectTable.isGetName((HThreadAdapter)lv_thread.getAdapter(),info.position).toArray(new String[]{});
				new HAddContact(HThreadActivity.this, address);
				break;
			}
		}
		return true;
	}
	
	/***
	 * 上下文菜单按钮4，回复
	 */
	private void context_forward(int position) {
		 Intent intent = new Intent();
		 HConst.markActivity = 1;
		 intent.setClass(HThreadActivity.this, HEditSmsActivity.class);
		 intent.putExtra("address",mThreadManager.getCollectAdapter().get(position).address);
		 startActivity(intent);
	}
	
	/***
	 * 上下文菜单按钮2，呼叫
	 */
	private void context_call() {
		String address = null;
		String name = null;
		if(HConst.isSearchActivity){
			address = mThreadManager.getSearchAdapter().get(mPosition).thread.address;
			name = mThreadManager.getSearchAdapter().get(mPosition).thread.name;
		}else{
			address = mThreadManager.getAddress(mPosition);
			name = mThreadManager.getName(mPosition);
		}
		new HCall(HThreadActivity.this, address,name);
	}

	
	
	
    /**跳转到Talk界面*/
	private void startActivity(int position) {
		mIndex = position;
//		if(!HConst.iscollect){
			if(HConst.isSearchActivity){
				startTalk(getSearchIndex(position));
				HConst.isSearchActivity = false;
				mThreadManager.clearSearchAdapter();
			}else{
				startTalk(position);
			}
//		}else{
//			Intent i = new Intent();
//			HConst.markActivity = 1;
//			i.setClass(this, HSeeCollectSmsActivity.class);
//			String str  = ((HThreadAdapter)lv_thread.getAdapter()).get(position).sms.body;
//			i.putExtra("body", str);
//			startActivity(i);
//		}
	}
	
	private void startTalk(int position){
//		Intent intent = new Intent();
//		String count = mThreadManager.getThreadAdapter().get(position).count;
//		String isdraft = mThreadManager.getThreadAdapter().get(position).type;
//		String ismms = mThreadManager.getThreadAdapter().get(position).ismms;
//		String address =  mThreadManager.getThreadAdapter().get(position).address;
//		String name =  mThreadManager.getThreadAdapter().get(position).name;
//		String threadId = mThreadManager.getThreadAdapter().get(position).sms.threadid;
//		mThreadManager.noReadChageRead(position);
//		if(!isdraft.equals("3")){//不是草稿---
//			if(count.equals("0") || count.equals("1")){
//				if(!hUpdateTools.getIsUpdate())
//				{
//					if(ismms.equals("1")){//mms
//						HSmsManage smsManage = new HSmsManage(this.getApplication());
//						smsManage.loadTalkList(0,address,name,threadId,null,null,false);
//						intent.setAction(HConst.ACTION_KILL_ONESELF);
//						sendBroadcast(intent);
//						intent.putExtra("position", 0);
//						intent.putExtra("dialog",true);
//						intent.putExtra("address", address);
//						intent.putExtra("name",name);
//						HConst.markActivity = 1;
//						intent.setClass(HThreadActivity.this, HPlayMMSActivity.class);
//						startActivity(intent);
//						return;
//					}else{//sms
//	
//							String body = mThreadManager.getThreadAdapter().get(position).sms.body;
//							HSmsManage smsManage = new HSmsManage(this.getApplication());
//							smsManage.loadTalkList(0,address,name,threadId,null,null,false);
//							intent.setAction(HConst.ACTION_KILL_ONESELF);
//							sendBroadcast(intent);
//							intent.putExtra("position", 0);
//							intent.putExtra("dialog",true);
//							intent.putExtra("address", address);
//							intent.putExtra("name",name);
//		//					intent.putExtra("dialog",true);
//							intent.putExtra("body", body);
//							intent.putExtra("dialog1",true);
//							HConst.markActivity = 1;
//		
//							intent.setClass(HThreadActivity.this, HPreviewActivity.class);//FLAG_ACTIVITY_NO_HISTORY
//			//				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//							startActivity(intent);
//							return;
//					}					
//				}
//			}
//		}
//		HConst.markActivity = 1;
//		intent.setClass(HThreadActivity.this, HTalkActivity.class);
//		intent.putExtra("position", position);
//		startActivityForResult(intent, 0);
		
		Intent intent = new Intent();
		HConst.markActivity = 1;
		mThreadManager.noReadChageRead(position);
		intent.setClass(HThreadActivity.this, HActivityGroup.class);
		intent.putExtra("position", position);
		HLog.i(TAG, "position --->" + position);
		startActivity(intent);
		
	}

	/**ActivityResult返回处理*/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch (resultCode) {
		case HConst.resultTalk:
			break;
		case HConst.TALK_RESULT:
			mThreadManager.getThreadAdapter().notifyDataSetChanged();
			break;
		}
		switch(requestCode){
		case HConst.REQUEST_RESOURCELIB:
//			getLayout();
//			init();
			break;
		case HConst.REQUEST_SKIN:
			getLayout();
			init();
			break;
		case HConst.REQUEST_CONTACT:
			if(HConst.iscollect){
				HThread thread = ((HThreadAdapter)lv_thread.getAdapter()).get(mPosition);
				HSms sms = new HSms();
				sms.smsid = thread.sms.smsid;
				sms.address= thread.address;
				sms.name = new HAddressBookManager(HThreadActivity.this).getNameByNumber(sms.address);
				sms.body = thread.sms.body;
				sms.type = thread.sms.type;
				sms.time = thread.sms.time;
				sms.read = thread.sms.read;
				collectTable.updataNote(sms);
				int position = mThreadManager.getPosition(sms.address);
				if(position != -1){
					mThreadManager.updataList(position);
					mThreadManager.ncThread();
				}
			}else{
				if(HConst.isSearchActivity){
					mThreadManager.updataListSearch(mPosition);
					mThreadManager.updataList(getSearchIndex(mPosition));
					mThreadManager.ncThread();
				}else{
					mThreadManager.updataList(mPosition);
					mThreadManager.ncThread();
				}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	 
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(HConst.iscollect && !HConst.isMark && !HConst.isSearchActivity){
				mEditsms.setVisibility(View.VISIBLE);
				HConst.iscollect = false;
				smsAdapter();
				mThreadManager.getCollectAdapter().clear();
				return true;
			}else if(HConst.iscollect && HConst.isMark && !HConst.isSearchActivity){
				HConst.isMark = false;
				mThreadManager.getCollectAdapter().notifyDataSetChanged();
				return true;
			}
			
			if(HConst.isSearchActivity && !HConst.iscollect){
				mEditsms.setVisibility(View.VISIBLE);
				mThreadManager.getSearchAdapter().clear();
				mThreadManager.setSearchAdapter(null);
				HConst.isSearchActivity = false;
				rl_search.setVisibility(View.GONE);
				rl_title.setVisibility(View.VISIBLE);
                lv_thread.setAdapter(mThreadManager.getThreadAdapter());
                mThreadManager.ncThread();
				return true;
			}else if(HConst.isSearchActivity && HConst.iscollect){
				mThreadManager.getSearchAdapter().clear();
				mThreadManager.setSearchAdapter(null);
				HConst.isSearchActivity = false;
				rl_search.setVisibility(View.GONE);
				rl_title.setVisibility(View.VISIBLE);
                lv_thread.setAdapter(mThreadManager.getCollectAdapter());
                mThreadManager.getCollectAdapter().notifyDataSetChanged();
                rl_title.requestFocus();
				return true;
			}
			
			if(HConst.isMark){
			    mThreadManager.cancel_mark();
				HConst.isMark = false;
				mEditsms.setVisibility(View.VISIBLE);
				return true;
			}
			isExit = true;
			mIndex = 0;
			Intent intent = getIntent();
			if(intent.getBooleanExtra("dialog", false)){
				intent.putExtra("dialog", false);
			}
			DLManager manager = DLManager.getInstance(this);
			manager.stopAllDownload();
			
			mStatistics.add(HStatistics.Z0_1, "", String.valueOf(System.currentTimeMillis()), "");
			
			Intent homeIntent = new Intent();
            homeIntent.setAction(Intent.ACTION_MAIN);  
            homeIntent.addCategory(Intent.CATEGORY_HOME);     
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
            startActivity(homeIntent);
            
            //发现强制更新之后，用户退回到桌面的时候注入标志位
            SharedPreferences spU = HThreadActivity.this.getSharedPreferences(HConst.UPDATE_FLAG, 0);
            if(spU.getInt(HConst.SHOW_TIMES, 0) == 1)
            {
            	Editor et = spU.edit();
            	et.putInt(HConst.SHOW_TIMES, 2);
            	et.commit();
            }
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	private boolean isQQ = false;
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		if(HConst.isSearchActivity){
			return false;
		}
		MenuInflater inflater = getMenuInflater();
		mStatistics.add(HStatistics.Z5_1, "", "", "");
		if(!HConst.iscollect){
			if (!HConst.isMark) {
				String str = ToolsUtil.getChannel(HThreadActivity.this).trim();
				if(str.equals("qq")){
					isQQ = true;
					inflater.inflate(R.menu.thread_menu_qq, menu);
				}else{
					isQQ = false;
					inflater.inflate(R.menu.thread_menu, menu);
				}
			} else {
				inflater.inflate(R.menu.thread_checkbox_menu, menu);
			}
		}else{
			if(!HConst.isMark) {
				inflater.inflate(R.menu.collect_checkbox_menu, menu);
			}else{
				inflater.inflate(R.menu.thread_checkbox_menu, menu);
			}
		}
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.menu_search:
			mStatistics.add(HStatistics.Z5_2, "", "", "");
			menu_search();
			break;
		case R.id.menu_clear:
			//mStatistics.add(HStatistics.Z5_3, "", "", "");
			menu_clear();
			break;
		case R.id.menu_mark:
			mStatistics.add(HStatistics.Z5_4, "", "", "");
			menu_mark();
			break;
		case R.id.menu_setting:
			mStatistics.add(HStatistics.Z5_5, "", "", "");
			menu_setting();
			break;
		case R.id.menu_about:
			mStatistics.add(HStatistics.Z5_9, "", "", "");
//			menu_about();
			//收藏
			// 收藏短信按键
			HConst.iscollect = true;
			mEditsms.setVisibility(View.GONE);
			collectAdapter();
			break;
		case R.id.menu_share:
			if(!isQQ){
//				mStatistics.add(HStatistics.Z5_10, "", "", "");
//				menu_share();
				//我说两句
				Intent intentSpeek  = new Intent();
				HConst.markActivity = 1;
				intentSpeek.setClass(HThreadActivity.this, HProblemActivity.class);
				startActivity(intentSpeek);
			}else{
				mStatistics.add(HStatistics.Z5_15, "", "", "");
				Uri uri = Uri.parse("http://app.qq.com/g/s?aid=index&g_f=990424");
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(it);
			}
			break;
		case R.id.menu_selectAllMark://标记全部
			mStatistics.add(HStatistics.Z5_4_1, "", "", "");
			menu_allmark();
    		break;
    	case R.id.menu_removeMark://取消标记
    		mStatistics.add(HStatistics.Z5_4_2, "", "", "");
    		menu_cancelmark();
    		break;
    	case R.id.menu_deleteSelectedMark://删除选中
    		mStatistics.add(HStatistics.Z5_4_3, "", "", "");
    		menu_deletemark();
    		break;
    	case R.id.menu_cancel://取消
    		mStatistics.add(HStatistics.Z5_4_4, "", "", "");
    		menu_cancel();
    		break;
    	case R.id.menu_collect_mark:
    		HConst.isMark = true;
    		mThreadManager.getCollectAdapter().notifyDataSetChanged();
    		break;
    	case R.id.menu_collect_search:
    		menu_search();
    		break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/***
	 * 搜索
	 */
	private void menu_search(){
		if( mThreadManager.getSearchAdapter() == null){
			 mThreadManager.setSearchAdapter(new HSearchAdapter(LayoutInflater.from(HThreadActivity.this)));
		}
		if( mThreadManager.getSearchAdapter().size() != 0){
    		 mThreadManager.getSearchAdapter().clear();
		}
		mEditsms.setVisibility(View.GONE);
		searchStartSize = mThreadManager.toSearchAdapter( mThreadManager.getSearchAdapter());
		lv_thread.setAdapter( mThreadManager.getSearchAdapter());
		mThreadManager.getSearchAdapter().notifyDataSetChanged();
		rl_title.setVisibility(RelativeLayout.GONE);
		rl_search.setVisibility(View.VISIBLE);
		et_search.requestFocus();
		HConst.isSearchActivity = true;
		setingSearch();
		
//		CharSequence[] items = { getString(R.string.searchName),getString(R.string.searchNumber),getString(R.string.searchContent)};
//    	new AlertDialog.Builder(this).setItems(items, new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				mEditsms.setVisibility(View.GONE);
//				if( mThreadManager.getSearchAdapter() == null){
//					 mThreadManager.setSearchAdapter(new HSearchAdapter(LayoutInflater.from(HThreadActivity.this)));
//				}
//				if( mThreadManager.getSearchAdapter().size() != 0){
//		    		 mThreadManager.getSearchAdapter().clear();
//				}
//				searchStartSize = mThreadManager.toSearchAdapter( mThreadManager.getSearchAdapter());
//				lv_thread.setAdapter( mThreadManager.getSearchAdapter());
//				 mThreadManager.getSearchAdapter().notifyDataSetChanged();
//				rl_title.setVisibility(RelativeLayout.GONE);
//				rl_search.setVisibility(View.VISIBLE);
//				et_search.requestFocusFromTouch();
//				HConst.isSearchActivity = true;
//				switch (which) {
//				case 0:
//					mStatistics.add(HStatistics.Z5_2_1, "", "", "");
//					setingSearch(which,R.string.searchInputName);
//					break;
//				case 1:
//					mStatistics.add(HStatistics.Z5_2_2, "", "", "");
//					setingSearch(which,R.string.searchInputNumber);
//					break;
//				case 2:
//					mStatistics.add(HStatistics.Z5_2_3, "", "", "");
//					setingSearch(which,R.string.searchInputContent);
//					break;
//				}
//			}
//		}).create().show();
	}
	
	 
	 private void setingSearch(){
	    	et_search.setText("");
	    	sb.delete(0, sb.length());
	    	//et_search.setHint(getString(stringId));
		    et_search.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					mThreadManager.search(s.toString()/*, which*/, before,searchStartSize);
					sb.delete(0, sb.length());
					sb.append(s.toString());
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,int after) {
				}
				
				@Override
				public void afterTextChanged(Editable s) {
				}
			});
		 
		   
	    }
	
	/***
	 * 清空所有信息
	 */
	private void menu_clear(){
		AlertDialog.Builder builder = getBuilder(getString(R.string.empty),getString(R.string.delete_all_sms));
		builder.setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//mStatistics.add(HStatistics.Z5_3_1, "", "", "");
				mThreadManager.deleteAllSms(true, HThreadActivity.this, null, getString(R.string.deleteSmsing));
			}
		});
		builder.setNegativeButton(R.string.calcel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//mStatistics.add(HStatistics.Z5_3_2, "", "", "");
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	
	/***
	 * 标记
	 */
	private void menu_mark(){
		HConst.isMark = true;
		mEditsms.setVisibility(View.GONE);
		mThreadManager.ncThread();
	}
	
	/***
	 * 设置
	 */
	private void menu_setting(){
		Intent intentSet = new Intent();
		HConst.markActivity = 1;
		intentSet.setClass(HThreadActivity.this, HSettingActivity.class);
		startActivityForResult(intentSet, HConst.REQUEST_RESOURCELIB); 
	}
	
	/**
	 * 关于
	 */
	@SuppressWarnings("unused")
	private void menu_about(){
		 Dialog dialog = new Dialog(this,R.style.about_style);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.about_dialog);
			dialog.show();
			TextView smsBody = (TextView) dialog.findViewById(R.id.body);
			smsBody.setAutoLinkMask(Linkify.EMAIL_ADDRESSES|Linkify.WEB_URLS|Linkify.PHONE_NUMBERS);
			// 正式版本号
			String version = getString(R.string.about_body).replaceAll("\\#",ToolsUtil.getVersion(this));
			smsBody.setText(version);
			smsBody.setMovementMethod(LinkMovementMethod.getInstance());
	}
	
	/***
	 * 标记全部
	 */
	private void menu_allmark(){
	  mThreadManager.all_mark();
	}
	
	/***
	 * 取消标记
	 */
	private void menu_cancelmark(){
      mThreadManager.cancel_mark();
	}
	
	/***
	 * 删除选中标记
	 */
	private void menu_deletemark(){
		boolean is = mThreadManager.isHaveMark();
		if(!is){
			Toast.makeText(HThreadActivity.this, R.string.nomarksms,  Toast.LENGTH_SHORT).show();
			return;
		}
		AlertDialog.Builder builder = getBuilder(getString(R.string.deleteMark),null);
		builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				boolean is = mThreadManager.isHaveMark();
				if(!is){
					Toast.makeText(HThreadActivity.this, R.string.nomarksms,  Toast.LENGTH_SHORT).show();
				}else{
					mThreadManager.delete_mark(HThreadActivity.this,getString(R.string.delete_smsing)); 
				}
			}
		});
		builder.setNegativeButton(R.string.calcel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
		builder.create().show();
	}
	
	/***
	 * 取消
	 */
	private void menu_cancel(){
		mThreadManager.cancel_mark();
    	HConst.isMark = false;
    	if(!HConst.iscollect){
    		mEditsms.setVisibility(View.VISIBLE);
    	}
	}
	
	public ProgressDialog createProgressDialog(String message) {
		ProgressDialog dialog = new ProgressDialog(HThreadActivity.this);
		dialog.setCancelable(false);
		dialog.setMessage(message);
		dialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_SEARCH) {
					return true;
				}
				return false;
			}
		});
		return dialog;
	}
	
	
	/***
	 * AlertDialog
	 * @param title  dialog的title
	 * @param message  dialog的message
	 * @return
	 */
	private Builder getBuilder(String title,String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
		return builder;
	}
	
//	private void flipperAnimate(int display){
//		ViewFlipper mFlipper = (ViewFlipper)findViewById(R.id.flipper);
//		switch(display){
//		case 0:
//			mFlipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.push_right_in));
//			mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_right_out));
//			break;
//		case 1:
//			mFlipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_in));
//			mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_out));
//			break;
//		}
//		mFlipper.setDisplayedChild(display);
//	}
	
}
