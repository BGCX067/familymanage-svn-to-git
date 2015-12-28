package com.haolianluo.sms2.ui.sms2;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.SkinButton;
import android.widget.SkinImageView;
import android.widget.SkinLinearLayout;
import android.widget.SkinListView;
import android.widget.SkinRelativeLayout;
import android.widget.SkinTextView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.haolianluo.sms2.R;
import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.mms.data.ContactList;
import com.haolianluo.sms2.mms.data.Conversation;
import com.haolianluo.sms2.model.HNewListModel;
import com.haolianluo.sms2.model.HNewListParser;
import com.haolianluo.sms2.model.HResDatabaseHelper;
import com.haolianluo.sms2.model.HResProvider;
import com.haolianluo.sms2.model.HStatistics;
import com.haolianluo.sms2.model.HTryUseModel;
import com.haolianluo.sms2.model.HTryUseParser;
import com.haolianluo.sms2.ui.ConversationList;
import com.haolianluo.sms2.ui.ConversationListItemData;
import com.haolianluo.sms2.util.DraftCache;
import com.lianluo.core.cache.CacheManager;
import com.lianluo.core.event.IEvent;
import com.lianluo.core.skin.SkinManage;
import com.lianluo.core.task.BaseTask;
import com.lianluo.core.task.TaskManagerFactory;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;

public class HThreadActivity extends ConversationList {

	private SkinButton mEditsms;//写短信
	private SkinButton mSearch; //search sms
	private SkinLinearLayout mLinearBottom;
	private int positionValue;	//会话索引值
	private HUpdateTools hUpdateTools;
//	private LinearLayout mMainLayout;
	private HStatistics mStatistics;
	private boolean isExit = false;
	
	//更新的新模版图标以及数量
	private FrameLayout thread_new_tem_layout;
	private TextView thread_new_tem_count;
	private Context mContext;
	private SkinLinearLayout thread_layout;
	private SkinRelativeLayout thread_title_layout;
	private SkinImageView iv_icon;
	private SkinTextView tv_appname;
	private SkinButton bt_shortcut;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		HConst.markActivity = 1;
		mContext = this;
		hUpdateTools = new HUpdateTools(this);
		mStatistics = new HStatistics(this);
		registerReceiver(killOneself, new IntentFilter(HConst.ACTION_KILL_ONESELF));
		registerReceiver(updateDialog, new IntentFilter(HConst.ACTION_UPDATE_DIALOG));
		
		//登录软件
		mHSharedPreferences.setLogout(false);
		
		//初始化缓存
		CacheManager.newInstance().openCache(getApplicationContext());
		//初始化试用模版过期
		TaskManagerFactory.createParserTaskManager().addTask(new BaseTask(null) {
			@Override
			public void doTask(IEvent event) throws Exception {
				HTryUseModel model = new HTryUseParser(HThreadActivity.this).tryuse();
				mHSharedPreferences.setTryOver(model.isPast());
			}
		});
		//初始化更新资源库数量
		TaskManagerFactory.createParserTaskManager().addTask(new BaseTask(null) {
			@Override
			public void doTask(IEvent event) throws Exception {
				HNewListModel model = new HNewListParser(HThreadActivity.this).newlist();
				mHSharedPreferences.setNewListCount(model.getS());
				mHSharedPreferences.setNewListTime(model.getT());
			}
		});
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

		HConst.setConfigAndCount(mContext, mHSharedPreferences.getNewListTime(), mHSharedPreferences.getNewListCount());
		
		listView.setOnCreateContextMenuListener(mConvListOnCreateContextMenuListener);
		mStatistics.add(HStatistics.Z0, String.valueOf(System.currentTimeMillis()), "","");
		Intent intent1 = getIntent();
		boolean is1 =  intent1.getBooleanExtra("dialog", false);
		mStatistics.add(HStatistics.Z0, String.valueOf(System.currentTimeMillis()), "","");
		if(!is1){
			mStatistics.add(HStatistics.Z2_1, "", "", "");
		}
		boolean is = ToolsUtil.isServiceWork(this,"com.haolianluo.sms2.ui.sms2.HService");
		if(!is){
			Intent intent = new Intent();
			intent.setClass(this, HService.class);
			startService(intent);
		}
	}
	
	@Override
	public void changeSkin() {
		if(thread_layout != null) {
			thread_layout.changeSkin();
		}
		if(mLinearBottom != null) {
			mLinearBottom.changeSkin();
		}
		if(mEditsms != null) {
			mEditsms.changeSkin();
		}
		if(mSearch != null) {
			mSearch.changeSkin();
		}
		if(listView != null) {
			((SkinListView)listView).changeSkin();
		}
		if(thread_title_layout != null) {
			thread_title_layout.changeSkin();
		}
		if(iv_icon != null) {
			iv_icon.changeSkin();
		}
		if(tv_appname != null) {
			tv_appname.changeSkin();
		}
		if(bt_shortcut != null) {
			bt_shortcut.changeSkin();
		}
	};
	
	
    BroadcastReceiver killOneself = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};
	
    BroadcastReceiver updateDialog = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			intent.setClass(context, HUpdateDialog.class);
			context.startActivity(intent);
		}
	};

	
	protected void onRestart() {
		Intent intent = getIntent();
		boolean is =  intent.getBooleanExtra("dialog", false);
		if(isExit && !is){
			isExit = false;
			mStatistics.add(HStatistics.Z0, String.valueOf(System.currentTimeMillis()), "","");
			mStatistics.add(HStatistics.Z2, "", "", "");
		}
		super.onRestart();
	}
	
	@Override
	protected void onDestroy() {
		//关闭缓存
		CacheManager.newInstance().closeCache();
		unregisterReceiver(killOneself);
		unregisterReceiver(updateDialog);
		
		super.onDestroy();
		
		try {
			if(mListAdapter.getCursor() != null) {
				mListAdapter.getCursor().close();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void initView() {
		mLinearBottom = (SkinLinearLayout)findViewById(R.id.linear_bottom);
		//写信息
		mEditsms = (SkinButton) findViewById(R.id.bt_editsms);
		mEditsms.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//新建按钮点击数
				mEditsms.setEnabled(false);
				mStatistics.add(HStatistics.Z4_1,"","","");
				createNewMessage();
			}
		});
		mSearch = (SkinButton)findViewById(R.id.bt_searchsms);
		mSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//搜索按钮点击数
				mStatistics.add(HStatistics.Z5_2, "", "", "");
				//搜索
				//HThreadActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
				onSearchRequested();
				
			}
		});
		
		thread_new_tem_layout = (FrameLayout) findViewById(R.id.thread_new_tem_layout);
		thread_new_tem_count = (TextView) findViewById(R.id.thread_new_tem_count);
		
		//TODO 判断是否要显示新图标
		if(!HConst.isShowNewTemCount(HThreadActivity.this, ""))
		{
			thread_new_tem_layout.setVisibility(View.VISIBLE);
			thread_new_tem_count.setText(HConst.getNewCount(HThreadActivity.this));
		}else
		{
			thread_new_tem_layout.setVisibility(View.GONE);
		}
		
		thread_layout = (SkinLinearLayout) findViewById(R.id.thread_layout);
		thread_title_layout = (SkinRelativeLayout) findViewById(R.id.thread_title_layout);
		iv_icon = (SkinImageView) findViewById(R.id.iv_icon);
		tv_appname = (SkinTextView) findViewById(R.id.tv_appname);
		//快捷菜单
		bt_shortcut = (SkinButton) findViewById(R.id.bt_shortcut);
		bt_shortcut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new HShortcutDialog(HThreadActivity.this); 
				//设置标志位
				HConst.setShowEdNewTemCount(HThreadActivity.this);
				//商店图标点击数
				mStatistics.add(HStatistics.Z3_1,"","","");
			}
		});
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		boolean flag = intent.getBooleanExtra("notify_collect_close", false);
		Log.i("TAG", "-----a-----------a----------onStart:" + flag);
		if(flag) {
			HConst.iscollect = false;
		}
		/*HThreadActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);*/
		super.onNewIntent(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		new Thread()
		{
			public void run() {
				Looper.prepare();
				toast_change_tem();
				Looper.loop();
			};
		}.start();
	}
	@Override
	protected void onStart() {/*
		HThreadActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);*/
		//755bug 同样是键盘弹出状态突然跳转到某页面，背景有键盘遮罩
		thread_layout.changeSkin();
		
		super.onStart();
		HConst.markActivity = 1;
		
		//需要进行强制更新并且是第二次
		HLog.d("isUpdate", String.valueOf(hUpdateTools.getIsUpdate()));
		HLog.d("isFirst", String.valueOf(hUpdateTools.getIsFirstUpdate()));
		if(hUpdateTools.getIsUpdate() && hUpdateTools.getIsFirstUpdate() == 2)
		{
			hUpdateTools.alertUpdate_Exit();
		}
		if (HConst.iscollect || HService.htread_check_flag) {
			linear_bottom.setVisibility(View.GONE);
		}
		else
		{
			linear_bottom.setVisibility(View.VISIBLE);
		}
		
		//toast_change_tem();
		//TODO 判断是否要显示新图标
		if(!HConst.isShowNewTemCount(HThreadActivity.this, ""))
		{
			thread_new_tem_layout.setVisibility(View.VISIBLE);
			thread_new_tem_count.setText(HConst.getNewCount(HThreadActivity.this));
		}else
		{
			thread_new_tem_layout.setVisibility(View.GONE);
		}
		
		if(mEditsms != null)
		{
			mEditsms.setEnabled(true);
		}
	}
	
	/**
	 * 检查是否已经十天没有更换模版了
	 */
	private void toast_change_tem()
	{
		String beforeTime = HConst.getTheCheckTemTiem(HThreadActivity.this);
		
		if(beforeTime == null || beforeTime.equals(""))
		{
			HConst.setTheCheckTemTime(mContext);
			return;
		}
		
		if(HConst.checkChangeTemIsToast(new Date(beforeTime)))
		{
			//Toast.makeText(HThreadActivity.this, getString(R.string.change_tem_toast), Toast.LENGTH_LONG).show();
			HConst.showChangeTemDialog(HThreadActivity.this);
			
		}
	}
	
	@Override
	protected void openThread(long threadId) {
		startActivity(HTalkActivity.createIntent(this, threadId).putExtra("position", positionValue));
		
		Conversation conv = Conversation.from(getApplicationContext(), mListAdapter.getCursor());
        ConversationListItemData ch = new ConversationListItemData(getApplicationContext(), conv);
        if(ch.hasDraft()) {
        	HTalkActivity.setSendFlag(true);
        	HTalkActivity.isDraft = true;
        } else {
        	HTalkActivity.setSendFlag(false);
        	HTalkActivity.isDraft = false;
        }
	}

	
	private boolean isQQ = false;
	/**
	 * Menu菜单项
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		
		MenuInflater inflater = getMenuInflater();
		//Menu键点击数
		mStatistics.add(HStatistics.Z5_1, "", "", "");
		//非收藏
		if(!HConst.iscollect)
		{
			//非标记
			if(!HConst.isMark)
			{
				String str = ToolsUtil.getChannel(HThreadActivity.this).trim();
				if(str.equals("qq")){
					isQQ = true;
					inflater.inflate(R.menu.thread_menu_qq, menu);
				}else{
					isQQ = false;
//					if(ToolsUtil.COLLECT_FLAG) {
//						inflater.inflate(R.menu.thread_menu_collect, menu);
//					} else {
//						inflater.inflate(R.menu.thread_menu, menu);
//					}
					inflater.inflate(R.menu.thread_menu_collect, menu);
				}
			}else {
				//标记菜单
				inflater.inflate(R.menu.thread_checkbox_menu	, menu);
			}
		} else {
			//非标记
			if(!HConst.isMark)
			{
				String str = ToolsUtil.getChannel(HThreadActivity.this).trim();
				if(str.equals("qq")){
					isQQ = true;
					inflater.inflate(R.menu.thread_menu_qq, menu);
				}else{
					isQQ = false;
//					if(ToolsUtil.COLLECT_FLAG) {
//						inflater.inflate(R.menu.thread_menu_collect_cancel, menu);
//					} else {
//						inflater.inflate(R.menu.thread_menu, menu);
//					}
					inflater.inflate(R.menu.thread_menu_collect_cancel, menu);
				}
			}else {
				//标记菜单
				inflater.inflate(R.menu.thread_checkbox_menu	, menu);
			}
		}
		return true;
	}
	
    private void goHome()
    {
    	Intent homeIntent = new Intent();
        homeIntent.setAction(Intent.ACTION_MAIN);  
        homeIntent.addCategory(Intent.CATEGORY_HOME);     
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
        startActivity(homeIntent);

    }
    
	/**
	 * Menu菜单项点击事件
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_exit:
			new AlertDialog.Builder(HThreadActivity.this)
			.setTitle(getString(R.string.exit))
			.setMessage(getString(R.string.exit_confirm))
			.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					mStatistics.add(HStatistics.Z0, String.valueOf(System.currentTimeMillis()), "","");
					
					Intent serviceIntent = new Intent();
					serviceIntent.setAction("com.haolianluo.sms2.ui.sms2.HService");
					stopService(serviceIntent);
					
					//goHome();
					mHSharedPreferences.setLogout(true);
					
					System.exit(0);
				}
			})
			.setNegativeButton(getString(R.string.calcel), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			})
			.create()
			.show();
			
			break;
		case R.id.menu_collect:
			//收藏
			mStatistics .add(HStatistics.Z5_9, "", "", "");
			linear_bottom.setVisibility(View.GONE);
			HConst.iscollect = true;
			startAsyncQuery();
			break;
//		case R.id.menu_collect_cancel:
//			//信息
//			HConst.iscollect = false;
//			startAsyncQuery();
//			break;
		case R.id.menu_search:
			mStatistics.add(HStatistics.Z5_2, "", "", "");
			//搜索
			onSearchRequested();
			break;
		case R.id.menu_clear:
			//清空
			confirmDeleteThread(-1L, mQueryHandler);
			break;
		case R.id.menu_mark:
			mStatistics.add(HStatistics.Z5_4, "", "", "");
			//标记
			mLinearBottom.setVisibility(View.GONE);
			
			HService.htread_check_flag = true;
			HService.htread_map.clear();
			mListAdapter.notifyDataSetChanged();
			HConst.isMark = true;
			break;
		case R.id.menu_setting:
			mStatistics.add(HStatistics.Z5_5, "", "", "");
			//设置
			menu_setting();
			break;
		case R.id.menu_about:
			//关于
			//menu_about();
			//HConst.iscollect = true;
			collectAdapter();
			break;
		case R.id.menu_share:
			//分享
			menu_share();
			break;
		case R.id.menu_selectAllMark:
			mStatistics.add(HStatistics.Z5_4_1, "", "", "");
			//标记全部
			Cursor cursor = mListAdapter.getCursor();
            if (cursor == null || cursor.getPosition() < 0) {
                break;
            }
            boolean flag = cursor.moveToFirst();
            while(flag) {
            	long thread_id = cursor.getLong(0);
            	HService.htread_map.put(thread_id, true);
            	if(!cursor.moveToNext()) {
            		break;
            	}
            }
            mListAdapter.notifyDataSetChanged();
			break;
    	case R.id.menu_removeMark:
    		mStatistics.add(HStatistics.Z5_4_2, "", "", "");
    		//取消标记
    		HService.htread_map.clear();
    		mListAdapter.notifyDataSetChanged();
    		break;
    	case R.id.menu_deleteSelectedMark:
    		mStatistics.add(HStatistics.Z5_4_3, "", "", "");
    		if(HService.htread_map.isEmpty())
    		{
    			Toast.makeText(HThreadActivity.this, getString(R.string.mark_toast), Toast.LENGTH_SHORT).show();
    			break;
    		}
    		//删除选中
    		AlertDialog.Builder builder = new AlertDialog.Builder(this).setMessage(getString(R.string.deleteMark));
    		builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				//标记删除的短信数
    				mStatistics.add(HStatistics.Z5_4_5, HService.htread_map.size() + "", "", "");
    				for(Entry<Long, Boolean> entry : HService.htread_map.entrySet()) {
    	    			if(entry.getValue()) {
    	    				Conversation.startDelete(mQueryHandler, ConversationList.DELETE_CONVERSATION_TOKEN, false, entry.getKey());
    	    	            DraftCache.getInstance().setDraftState(entry.getKey(), false);
    	    			}
    	    		}
    	    		HService.htread_map.clear();
    	    		//mListAdapter.notifyDataSetChanged();
    			}
    		});
    		builder.setNegativeButton(R.string.calcel, new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {}
    		});
    		builder.create().show();
    		break;
    	case R.id.menu_cancel:
    		mStatistics.add(HStatistics.Z5_4_4, "", "", "");
    		//取消
    		calcleMark();
    		break;
        default:
            return true;
		}
		return false;
	}
	/***
	 * 设置
	 */
	private void menu_setting(){
		Intent intentSet = new Intent();
		intentSet.setClass(HThreadActivity.this, HSettingActivity.class);
		startActivityForResult(intentSet, HConst.REQUEST_RESOURCELIB); 
	}
	/***
	 * 分享
	 */
	private void menu_share(){
//		Intent intent = new Intent();
//		intent.putExtra("main", "main");
//		intent.setClass(this, HContactActivity.class);
//		startActivity(intent);
		if(!isQQ){
//			menu_share();
			//我说两句
			Intent intentSpeek  = new Intent();
			HConst.markActivity = 1;
			intentSpeek.setClass(HThreadActivity.this, HProblemActivity.class);
			startActivity(intentSpeek);
		}else{
			Uri uri = Uri.parse("http://app.qq.com/g/s?aid=index&g_f=990424");
			Intent it = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(it);
		}
	}
	/**
	 * 上下文菜单点击事件
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Cursor cursor = mListAdapter.getCursor();
		if(cursor == null)
		{
			return super.onContextItemSelected(item);
		}
		else
		{
			cursor.moveToPosition(mLongClickPosition);
		}
		if(cursor != null && cursor.getPosition() >= 0) {
			Conversation conv = Conversation.from(HThreadActivity.this, cursor);
			ContactList contactList = conv.getRecipients();
			
			StringBuffer phoneBuffer = new StringBuffer();
			StringBuffer phoneBufferAddress = new StringBuffer();
			String[] phoneArr = new String[contactList.size()];
			for (int i = 0; i < contactList.size(); i++) {
				phoneBuffer.append(contactList.get(i).getName() + ",");
				phoneBufferAddress.append(contactList.get(i).getNumber() + ",");
				phoneArr[i] = contactList.get(i).getNumber();
			}
			long threadId = conv.getThreadId();
//			if(!HConst.iscollect){
				switch (item.getItemId()) {
				case 1:
					//查看
					mStatistics.add(HStatistics.Z3_2,"","","");
					
					openThread(threadId);
					break;
				case 2:
					//回复
					mStatistics.add(HStatistics.Z3_3,"","","");
					openThread(threadId);
					break;
				case 3:
					//删除
					mStatistics.add(HStatistics.Z3_4,"","","");
					confirmDeleteThread(threadId, mQueryHandler);
					break;
				case 4:
					//呼叫
					mStatistics.add(HStatistics.Z3_5,"","","");
					context_call(phoneBuffer.toString(),phoneBufferAddress.toString());
					break;
				case 5:
					//添加联系人
					mStatistics.add(HStatistics.Z3_6,"","","");
					new HAddContact(HThreadActivity.this, phoneArr);
					break;
				default:
					break;
//				}		
//			else{
//				switch (item.getItemId()) {
//				case 1:
//					//转发
//					context_forward();
//					break;
//				case 2:
//					String []address = collectTable.isGetName((HThreadAdapter)lv_thread.getAdapter(),info.position).toArray(new String[]{});
//					new HAddContact(HThreadActivity.this, address);
//					break;
//				case 3:
//					collectTable.deleteNote(mPosition);
//					break;	
//			
			}
		}
		return super.onContextItemSelected(item);
	}
	
	private void collectAdapter(){
		Toast.makeText(HThreadActivity.this, "待实现", Toast.LENGTH_SHORT).show();
	}
	private int mLongClickPosition = 0;
	private final OnCreateContextMenuListener mConvListOnCreateContextMenuListener =
		        new OnCreateContextMenuListener() {
		        public void onCreateContextMenu(ContextMenu menu, View v,
		                ContextMenuInfo menuInfo) {
		        	Cursor cursor = mListAdapter.getCursor();
		        	if(cursor == null || cursor.getPosition() < 0)
		        	{
		        		return;
		        	}
//		        	if (!HConst.iscollect) 
//		        	{
			        	Conversation conv = Conversation.from(HThreadActivity.this, cursor);
			        	ContactList recipients = conv.getRecipients();
			        	menu.setHeaderTitle(recipients.formatNames(","));
			        	
			        	AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
			        	if (info.position >= 0) {
			        		mLongClickPosition = info.position;
							menu.add(0, 1, 0, R.string.lookUp);
							if(!HConst.iscollect){
								menu.add(0, 2, 0, R.string.reply);
							}
							menu.add(0, 3, 0, R.string.remove);
							menu.add(0, 4, 0, R.string.call);
							
							//判断是否有陌生人号码, 如果有,则添加联系人
							List<String> list = isGetName(recipients);
							if(list != null && list.size() > 0)
							{
								menu.add(0, 5, 0, R.string.addcontact);
							}
						}
//			        }else {
//			        	//收藏相关
//			        	
//					}
		        }
		    };
	
	/**
	 * 得到所有没有名字的电话号码
	 */
	public List<String> isGetName(ContactList recipients)
	{
		    	if (recipients == null || recipients.size() == 0) {
					return null;
				}
		    	List<String> list = new ArrayList<String>();
		    	for (int i = 0; i < recipients.size(); i++) 
		    	{
					if(!recipients.get(i).existsInDatabase())
					{
						//陌生号码
						list.add(recipients.get(i).getNumber());
					}
				}
		    	return list;
	}
	
	/***
	 * 上下文菜单按钮2，呼叫
	 */
	private void context_call(String addressList,String address) {
//		if(HConst.isSearchActivity){
//			address = mThreadManager.getSearchAdapter().get(mPosition).thread.address;
//		}else{
//			address = mThreadManager.getAddress(mPosition);
//		}
		new HCall(HThreadActivity.this, addressList, address);
	}
	
	/**
	 * 关于
	 */
//	private void menu_about(){
//		 Dialog dialog = new Dialog(this,R.style.about_style);
//			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//			dialog.setContentView(R.layout.about_dialog);
//			dialog.show();
//			TextView smsBody = (TextView) dialog.findViewById(R.id.body);
//			smsBody.setAutoLinkMask(Linkify.EMAIL_ADDRESSES|Linkify.WEB_URLS);
//			// 正式版本号
//			String version = getString(R.string.about_body).replaceAll("\\#",ToolsUtil.getVersion(this));
//			smsBody.setText(version);
//			smsBody.setMovementMethod(LinkMovementMethod.getInstance());
//	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(HService.htread_check_flag) {
				calcleMark();
				return true;
			}
			
			if(HConst.iscollect){
				linear_bottom.setVisibility(View.VISIBLE);
				HConst.iscollect = false;
				startAsyncQuery();
				return true;
			}
			
			isExit = true;
			Intent homeIntent = new Intent();
            homeIntent.setAction(Intent.ACTION_MAIN);  
            homeIntent.addCategory(Intent.CATEGORY_HOME);     
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
            startActivity(homeIntent);
            
            SharedPreferences spU = HThreadActivity.this.getSharedPreferences(HConst.UPDATE_FLAG, 0);
            if(spU.getInt(HConst.SHOW_TIMES, 0) == 1)
            {
            	Editor et = spU.edit();
            	et.putInt(HConst.SHOW_TIMES, 2);
            	et.commit();
            }
            
			return true;
		} else if(keyCode == KeyEvent.KEYCODE_SEARCH) {
			if(HConst.iscollect) {
    			return true;
    		}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void calcleMark() {
		if(!HConst.iscollect)
		{	
			mLinearBottom.setVisibility(View.VISIBLE);
		}
		HService.htread_check_flag = false;
		HService.htread_map.clear();
		mListAdapter.notifyDataSetChanged();
		
		HConst.isMark = false;
	}

}
