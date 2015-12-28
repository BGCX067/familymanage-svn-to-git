package com.haolianluo.sms2;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.data.HSmsApplication;
import com.haolianluo.sms2.model.HAddressBookManager;
import com.haolianluo.sms2.model.HDatabaseHelper;
import com.haolianluo.sms2.model.HHistoryLinkman;
import com.haolianluo.sms2.model.HSms;
import com.haolianluo.sms2.model.HSmsManage;
import com.haolianluo.sms2.model.HStatistics;
import com.lianluo.core.util.ToolsUtil;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SkinTextView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 编辑短信界面
 * 
 * @author fyq
 * 
 */
public class HEditSmsActivity extends HActivity {
	private HAddressBookManager mAddressBookManager;
	private HSmsManage mSmsManage ;
	private LinearLayout topGrid, bottomGrid;
	private EditText et_address;
	private EditText et_body;
	private TextView tv_count;
	private Button rl_add;
	private Button bt_send;
	private ImageView tv_linkman;
	private TextView tv_linkman1;
	private String address = null;
	private String body = null;
	private int mPosition = -1;
	/**信息存在于数据库中的id*/
	private String smsId = null;
	private List<String> numbers;
	private List<String> numbersRecord;
	private List<String> histroyLinkmanList;
	
	private static final int ITEM1 = Menu.FIRST;
	private static final int ITEM2 = Menu.FIRST + 1;
	private static final int ITEM3 = Menu.FIRST + 2;
	
	private int preMarkActivity = -1;
	private HStatistics mStatistics;
	private boolean mIsRun = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editsms);
		preMarkActivity = HConst.markActivity;
		HConst.markActivity = 2;
		mSmsManage = new HSmsManage((HSmsApplication)this.getApplication());
		numbers = new ArrayList<String>();
		mStatistics = new HStatistics(this);
		init();
		drawButton();
	}
	
    
	private void init(){
		tv_count = (TextView)findViewById(R.id.count);
		bt_send = (Button) findViewById(R.id.bt_send);
		et_address = (EditText) findViewById(R.id.et_address);
		et_body = (EditText) findViewById(R.id.et_body);
		tv_linkman = (ImageView) findViewById(R.id.linkmanImageView);
		tv_linkman1 = (TextView) findViewById(R.id.linkmanImageView1);
		tv_linkman.setVisibility(View.GONE);
		tv_linkman1.setVisibility(View.GONE);
		numbersRecord = new ArrayList<String>();
		mAddressBookManager = new HAddressBookManager(this);
//		tv_historyLinkman = (TextView) findViewById(R.id.tv_history);
		textChangeBodyView();
		textChangeNumberView();
		selectDB();
		caogao();
		onClickSend();
		onClickAdd();
		if(getIntent().getBooleanExtra("setting", false)){
			et_body.setText(getString(R.string.inviteBody));
		}
	}

	private void textChangeNumberView() {
		et_address.addTextChangedListener(new TextWatcher() {
			/**
			 * count 0 删除 1添加
			 * start  总数
			 * before 和cont相反
			 * 写  beforeTextChanged ---->>>onTextChanged------>>>afterTextChanged
			 */
			public void onTextChanged(CharSequence s, int start, int before,int count) {
				if(s.toString().length() > 970){
					Toast.makeText(HEditSmsActivity.this, R.string.textTooLong,Toast.LENGTH_LONG).show();
				}
				et_body.toString();
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

			private void isShowNumber() {
//				if (getSendNumbers().trim().length() == 0){
//					Toast.makeText(HEditSmsActivity.this, R.string.linkmanInvalid,Toast.LENGTH_LONG).show();
//					return;
//				} else if(et_body.getText().length() <= 0) {
//					Toast.makeText(HEditSmsActivity.this, R.string.textTooLong,Toast.LENGTH_LONG).show();
//					return;
//				}
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
			
		});
	}

	private void textChangeBodyView() {
		et_body.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,int count) {isEnabled(s);}
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {	}
			public void afterTextChanged(Editable s) {}
			private void isEnabled(CharSequence s) {
				if(s.toString().length()>970){
					Toast.makeText(HEditSmsActivity.this, R.string.textTooLong,Toast.LENGTH_LONG).show();
				}
				tv_count.setText(ToolsUtil.getCountString(s.toString()));
				
			}
		});
	}


	/**
	 * @param 添加按钮按键事件
	 */
	private void onClickAdd() {
		rl_add = (Button) findViewById(R.id.bt_add);
		rl_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {// 弹出通讯录列表
				if(mIsRun){
					mIsRun = false;
					mStatistics.add(HStatistics.Z4_2, "", "", "");
					Intent intent = getIntent();
					intent.setClass(HEditSmsActivity.this, HContactActivity.class);
					startActivityForResult(intent, 11);
					mIsRun = true;
				}
			}
		});
	}
	
	/**
	 * @param 发送按钮按键事件
	 */
	private void onClickSend() {
		
		bt_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mStatistics.add(HStatistics.Z4_3, "", "", "");
				isLogin();
			}
		});
	}


	
	public void loginAfterSendSms() {
		final String number = getSendNumbers();
		boolean isRight = numberIsRight(number);
		if(!isRight){
			Toast.makeText(HEditSmsActivity.this, R.string.linkmanInvalid,Toast.LENGTH_LONG).show();
			return;
		}
		if(et_body.getText()==null || et_body.getText().equals("") || et_body.getText().length()==0){
			Toast.makeText(HEditSmsActivity.this, R.string.smsbodynull,Toast.LENGTH_LONG).show();
			return;
		}
		if(!compareString(et_address.getText().toString())){
			et_address.setText("");
			Toast.makeText(HEditSmsActivity.this, R.string.linkmanInvalid,Toast.LENGTH_LONG).show();
			return;
		}
		if(et_address.getText() == null || getSendNumbers().equals("") ||getSendNumbers().length()==0){
			Toast.makeText(HEditSmsActivity.this, R.string.linkmanInvalid,Toast.LENGTH_LONG).show();
			return;
		}else{
			 if(!ToolsUtil.readSIMCard(HEditSmsActivity.this)){
				 return;
			 }
	       
//	        new Thread() {
//				public void run() {
					 if(preMarkActivity == 1 || preMarkActivity == 7){//主界面
						 	if(!HConst.iscollect){
						 		Intent intent = new Intent();
								HAddressBookManager abm = new HAddressBookManager(HEditSmsActivity.this);
								intent.putExtra("address", number);
								intent.putExtra("name",abm.getNameByNumber(number));                           
								intent.setClass(HEditSmsActivity.this, HActivityGroup.class);
								startActivity(intent);
						 	}
							finish();
						}else if(preMarkActivity == 3){//Talk界面
							HConst.markActivity = 3;
							finish();
						}else if(preMarkActivity == 4){//flash界面
							setResult(HConst.resultFlash);
							finish();
						}
//					 try {
//							Thread.sleep(1000);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
					sendSms();
//				}
//			}.start();
}
	}
	
	
	
	private  boolean compareString(String str){
		String []arrStr = str.split(",");
		for(int i = 0;i < arrStr.length;i++){
			for(int j = i+1;j < arrStr.length;j++){
				if(arrStr[i].equals(arrStr[j])){
					return false;
				}
			}
		}
		return true;
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
		}else{
//			tv_historyLinkman.setVisibility(View.GONE);
		}
		if(cursor.getCount() > 0){
			histroyLinkmanList.add("&^%$!@#");
		}
		db.close();
		cursor.close();
	}
	
	private void deletcDB(String number,boolean isdeleteAll,int index) {
		HDatabaseHelper dbOpenHelper = new HDatabaseHelper(this);
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if(isdeleteAll){
			db.delete(HHistoryLinkman.TABLE_NAME, null, null);
			histroyLinkmanList.clear();
			tv_linkman1.setVisibility(View.GONE);
//			tv_historyLinkman.setVisibility(View.GONE);
		}else {
			Cursor cursorId = db.query(HHistoryLinkman.TABLE_NAME, new String[]{HHistoryLinkman.ID},HHistoryLinkman.PHONE_NUMBER + " = '" + number + "'", null, null, null, null);
			int temp = index;
			cursorId.moveToNext();
			String id = cursorId.getString(cursorId.getColumnIndex(HHistoryLinkman.ID));
			db.delete(HHistoryLinkman.TABLE_NAME, HHistoryLinkman.ID + " = " + id, null);
			histroyLinkmanList.remove(temp);
			if(histroyLinkmanList.size() == 0){
//				tv_historyLinkman.setVisibility(View.GONE);
			}	
			if(histroyLinkmanList.size() > 0){
				tv_linkman1.setVisibility(View.VISIBLE);
			}else{
				tv_linkman1.setVisibility(View.GONE);
			}
			
			cursorId.close();
		}
		drawButton();
		db.close();
	}

	
	
	
	/**
	 * @return true为草稿
	 */
	private void caogao() {
		try{
			if (preMarkActivity != 1) {
				mPosition = getIntent().getIntExtra("position",mPosition);
				
				if(!getIntent().getBooleanExtra("isforward", false)){
					address = mSmsManage.getAdapter().get(mPosition).address;
					if(address != null){
						address = address.startsWith("+86") ? address.substring(3): address;
					}
					body = mSmsManage.getAdapter().get(mPosition).body;
					smsId = mSmsManage.getAdapter().get(mPosition).smsid;
				}else{
					body = mSmsManage.getAdapter().get(mPosition).body;
				}
			
				et_address.setText(address);
				numbersRecord.clear();
				for(int i=0;i<getSendNumbers().split(",").length;i++){
					if(!"".equals(getSendNumbers().split(",")[i])){
						numbersRecord.add(getSendNumbers().split(",")[i]);
					}
				}
				numbers.clear();
				numbers.addAll(numbersRecord);
				if(histroyLinkmanList.size() > 0){
					tv_linkman1.setVisibility(View.VISIBLE);
				}else{
					tv_linkman1.setVisibility(View.GONE);
				}
				drawButton();
				et_body.setText(body);
				tv_count.setText(ToolsUtil.getCountString(""));
				tv_count.setText(ToolsUtil.getCountString(et_body.getText().toString()));
				getEidtSelection();
			}else{//主界面进入的写信界面
//				tv_count.setText(ToolsUtil.getCountString(""));
//				et_body.setText(getIntent().getStringExtra("smsbody"));
				et_address.setText(getIntent().getStringExtra("address"));
				et_body.setText("");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	/**
	 * 发送短信
	 */
	private void sendSms() {
		String number = getSendNumbers();
		boolean is = false;
		if (address != null && number.equals(address)) {
			HSms sms1 = mSmsManage.getAdapter().get(mPosition);
			if(sms1.type.equals("3")){
				is = true;
			}
			HSms sms = oldSms(number);
			if(sms != null){
				mSmsManage.sendLianLuoSms(sms,is,false);
			}
		} else {
			HSms sms = newSms(number);
			if(sms != null){
				mSmsManage.sendLianLuoSms(sms,is,false);
			}
		}
	}
	
	
	/***
	 * 判断此号码是不是合法的电话号码
	 */
	private boolean  numberIsRight(String str){
		String []arrStr = str.split(",");
		boolean is = true;
		int len = arrStr.length;
		for(int i = 0;i < len;i++){
			is = arrStr[i].matches("\\d*");
			if(!is){
				break;
			}
		}
		return is;
	}


	
	
	private HSms newSms(String number){
		HAddressBookManager abm = new HAddressBookManager(this);
		HSms sms = new HSms();
		sms.address = number;
		sms.name = abm.getNameByNumber(number);
		sms.body = et_body.getText().toString();
		sms.time = String.valueOf(System.currentTimeMillis());
		sms.type = "2";
		sms.read = "1";
		sms.threadid = mSmsManage.getThreadIdForAndress(number.split(","));
		sms.ismms = "0";
		return sms;
	}
	
	private HSms oldSms(String number){
		HAddressBookManager abm = new HAddressBookManager(this);
		HSms sms = mSmsManage.getAdapter().get(mPosition);
		sms.smsid = smsId;
		sms.address = number;
		sms.name = abm.getNameByNumber(number);
		sms.body = et_body.getText().toString();
		sms.time = String.valueOf(System.currentTimeMillis());
		sms.type = "2";
		sms.read = "1";
		sms.threadid = mSmsManage.getThreadIdForAndress(number.split(","));
		return sms;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(!et_body.getText().toString().trim().equals("")){//有效的联系人
				if (getSendNumbers().trim().equals("")) {
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(HEditSmsActivity.this);
					alertDialog.setTitle(R.string.giveUp);
					alertDialog.setMessage(R.string.notify_phonenumbernull);
					alertDialog.setPositiveButton(R.string.confirm, new  DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}});
					alertDialog.setNegativeButton(R.string.calcel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					alertDialog.create();
					alertDialog.show();
					return true;
				} else {
					HAddressBookManager abm = new HAddressBookManager(HEditSmsActivity.this);
					String address = getSendNumbers();
					String name = abm.getNameByNumber(address);
					if(!et_body.getText().toString().trim().equals("")){
						mSmsManage.addDraft(et_body.getText().toString(),address,name);
					}
//				    Intent intent = new Intent();
//				    intent.putExtra("address", address);
//				    intent.putExtra("name", name);
//					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					intent.setClass(HEditSmsActivity.this, HTalkActivity.class);
//					startActivity(intent);
					finish();
					
//					String number = getSendNumbers();
//					if (address != null && number.equals(address)) {// 更新草稿
//						//HSms sms = oldSms(number);
//						if(sms != null){
//							
//							//sms.addDraft((HSmsApplication)getApplication(),et_body.getText().toString(),sms);
//							//mSmsManage.updataList(sms);
//						}
//						Intent in = new Intent();
//						in.putExtra("mssage", et_body.getText().toString());
//						setResult(50, in);
//						finish();
//					} else {// 保存为草稿
//						HSms sms = newSms(number);
//						if(sms != null){
//							//sms.addDraft((HSmsApplication)getApplication(),et_body.getText().toString(),sms);
//							//mSmsManage.addDraft(body, et_body.getText().toString(),sms.name);
//							//mSmsManage.updataList(sms);
//						}
//						finish();
//					}
					return super.onKeyDown(keyCode, event);
				}
			}else{
				String address = getSendNumbers();
				String preAddress = getIntent().getStringExtra("address");
				int index = getIntent().getIntExtra("position", -1);
				if(null == preAddress)
				{
					preAddress = "";
				}
				if(!address.trim().equals("") && preAddress.equals(address)){
					boolean is = mSmsManage.deleteTalkItem(address, index);
					if(is){
						setResult(100);
					}
				}
				finish();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}


	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == 10) {
			this.setResult(10);
			this.finish();
		}

		if (resultCode == 11 && data.getStringArrayListExtra("numbercontact") != null) {
			numbers.clear();
			numbers = data.getStringArrayListExtra("numbercontact");
			if(numbers.toString().equals("")){
				return;
			}
			for(int i=0;i<numbers.size();i++){
				for(int j=0;j<numbersRecord.size();j++){
					if(numbers.get(i).equals(numbersRecord.get(j))){
						numbersRecord.remove(j);
					}
				}
			}
			numbers.addAll(numbersRecord);
			String shows = getShows();
			et_address.setText(shows);
			getEidtSelection();
//			et_body.requestFocusFromTouch();
			Timer timer = new Timer();
	        timer.schedule(new TimerTask(){
	        @Override
	         public void run() {
	           InputMethodManager imm = (InputMethodManager)et_body.getContext().getSystemService(INPUT_METHOD_SERVICE);
	           imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
	         }
	        }, 1000);
		}
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
	  				tv.setTextColor(R.color.editsms_color);
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
	  				System.out.println("selectName(numbers.get(i)) ===" + selectName(numbers.get(i)));
	  				tv.setText(selectName(numbers.get(i)));
	  				tv.setTextSize(fontSize);
	  				tv.setTextColor(R.color.editsms_color);
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
						mStatistics.add(HStatistics.Z4_4, "", "", "");
						if(curIndex == histroyLinkmanList.size()-1){
							if(mIsRun){
								mIsRun = false;
								Intent intent = getIntent();
								intent.setClass(HEditSmsActivity.this, HContactActivity.class);
								startActivityForResult(intent, 11);
								mIsRun = true;
								return;
							}
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
						
						String shows = getShows();
						et_address.setText(shows);
						//获取光标位置
						
//						et_address.requestFocusFromTouch();
						getEidtSelection();
						
					}
				});
	        	tv.setPadding(3, 0, 3, 0);
	        	tv.setGravity(Gravity.CENTER);
	        	tv.setTextSize(fontSize);
	        	tv.setTextColor(R.color.editsms_color);
	        	tv.setHorizontalFadingEdgeEnabled(true);// 设置view在水平滚动时，水平边是否淡出。
	        	tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
	        	tv.setSingleLine(true);
	        	String name= selectName(histroyLinkmanList.get(i));
	        	if(!name.equals("&^%$!@#")){
	        		tv.setText(name);
	        		tv.setBackgroundResource(R.drawable.history);
	        	}else{
	        		tv.setBackgroundResource(R.drawable.add_history_contact);
	        	}
	        	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(with, heigh);  
				lp.gravity = Gravity.CENTER;  
				lp.setMargins(margin, 0, 0, 0);
				tv.setLayoutParams(lp);  
	        	layout1.addView(tv);
	        	
	        }
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
	
	
	private void butclic(final int position){
		final AlertDialog.Builder alertDialog = new AlertDialog.Builder(HEditSmsActivity.this);
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
						et_address.setText(str);
					}else{
						listRemove(position);
						String shows = getShows();
						et_address.setText(shows);
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
	
	
	
	

	private String selectName(String str){
		String s = "";
		if(str.contains("|")){
			for(int i = 0;i < str.split("\\|").length;i++){
				s = s + str.split("\\|")[i]+ ",";
			}
		}else{
			s  = str;
		}
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
	
    int index = 0;
	public void setIndex(int index){
		this.index = index;
	}
	
	public int getIndex(){
		return index;
	}
	
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
    	menu.add(0, ITEM1, 0, R.string.delete_history);
    	menu.add(0, ITEM2, 0, R.string.qkls);
    	menu.add(0, ITEM3, 0, R.string.calcel);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	switch(item.getItemId()){
    	case ITEM1:
			deletcDB(histroyLinkmanList.get(getIndex()),false,getIndex());
    		break;
    	case ITEM2:
    		deletcDB(histroyLinkmanList.get(getIndex()),true,getIndex());
    		break;
    	case ITEM3:
    		break;
    	}
    	return true;
    }
    
    /***
     * 用于显示电话和名字，追加输入号码
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
				showName = mAddressBookManager.getNameByNumber(numbers.get(i));
				if(showName.equals(HConst.defaultName)){
					showAddress = numbers.get(i);
				}else{
					showAddress = showName;
				}
				sff.append(showAddress) .append("<").append(numbers.get(i)).append(">");
				sff.append(",");
			}
		}
		if(sff.length() > 0){
			return sff.substring(0,sff.length()-1).toString();
		}
		return sff.toString();
	}
	
	/***
	 * 获取电话号码
	 * @return
	 */
	private String getSendNumbers(){
		String address = et_address.getText().toString();
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
		Editable ea= et_address.getText();  //etEdit为EditText
		Selection.setSelection(ea, ea.length()); 
		}catch(Exception e){
		}
	}
	
	

}
