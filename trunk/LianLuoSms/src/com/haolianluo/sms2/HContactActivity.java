/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haolianluo.sms2;

/**
 * 联系人界面类
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.model.HAddressBookManager;
import com.haolianluo.sms2.model.HSms;
import com.haolianluo.sms2.model.HSmsManage;
import com.haolianluo.sms2.model.HStatistics;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HContactActivity extends HActivity implements OnClickListener {

	/* 定义类******************* */
	private MyAdapter adapter;
	private ListAdapter listAdapter;
	/* 定义XML中的控件************* */
	private ListView ls;
	private Cursor cursor;
	private TextView linkmanTextView;
	private Button bt_seeAll;
	private Button bt_ok;
	private Button bt_cel;
	private EditText et_edit;
	private RelativeLayout rl_edit;

	/* 定义menu状态********************** */
	/** 标记所有联系人 */
	private final static int CHECKALL = Menu.FIRST;
	/** 取消所有标记 */
	private final static int CHECKNULL = Menu.FIRST + 1;
	/** 当邀请时为发送 */
	private final static int SEND = Menu.FIRST + 2;

	/* 定义变量********************** */
	/** 联系人列表？ */
	// private List<String> recentContacts;
	/** 被标记的联系人 */
	private List<String> checkedPeople;
	/** 标记是否是近期联系人 */
	private boolean isRecentContact = false;
	private boolean isSee = true;
	private List<String> list = new ArrayList<String>();
	private HAddressBookManager abm = null ;
	private int preMarkActivity = -1;
	private HStatistics mStatistics = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hcontact);
		mStatistics = new HStatistics(this);
		preMarkActivity = HConst.markActivity;
		abm = new HAddressBookManager(this);
		bt_ok = (Button) findViewById(R.id.contactbtn_ok);
		bt_ok.setOnClickListener(this);
		bt_cel = (Button) findViewById(R.id.contactbtn_cel);
		bt_cel.setOnClickListener(this);
		linkmanTextView = (TextView) findViewById(R.id.tv_linkman);
		bt_seeAll = (Button) findViewById(R.id.recentContact);
		linkmanTextView.setOnClickListener(this);
		if (isRecentContact) {
			linkmanTextView.setText(R.string.lookselect);
		}else{
			linkmanTextView.setText(R.string.linkman);
		}
		bt_seeAll.setOnClickListener(this);
		
		cursor = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				new String[] { Phone.DISPLAY_NAME, Phone.NUMBER, Phone._ID },
				null, null, Phone.DISPLAY_NAME);
		ls = (ListView) findViewById(R.id.list);
		if ((checkedPeople = getIntent().getStringArrayListExtra("numbers")) == null) {
			checkedPeople = new ArrayList<String>();
		}
		adapter = new MyAdapter(this, cursor);
		ls.setAdapter(adapter);
		rl_edit = (RelativeLayout) findViewById(R.id.rl_edit);
		et_edit = (EditText) findViewById(R.id.et_edit);
		
//		Button bt_search = (Button) findViewById(R.id.bt_search);
//		bt_search.setOnClickListener(new OnClickListener() {
//			boolean isOk = true;
//			@Override
//			public void onClick(View v) {
//				if(isOk){
//					isOk = false;
//					String constraint = et_edit.getText().toString();
//					if (constraint == null) {
//						constraint = "*";
//					}
//					StringBuilder buffer = null;
//					String[] args = null;
//					if (constraint != null) {
//						buffer = new StringBuilder();
//						buffer.append("UPPER(");
//						buffer.append(Phone.DISPLAY_NAME);
//						buffer.append(") GLOB ?");
//						args = new String[] { "*" + constraint.toString().toUpperCase() + "*" };
//					}
//					if(cursor != null){
//						cursor.close();
//					}
//					cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,new String[] { Phone.DISPLAY_NAME, Phone.NUMBER,Phone._ID },buffer == null ? null : buffer.toString(), args,Phone.DISPLAY_NAME);
//					adapter.changeCursor(cursor);
//					isOk = true;
//				}
//			}
//		});
		et_edit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run() {
						String constraint = et_edit.getText().toString();
						if (constraint == null) {
							constraint = "*";
						}
						StringBuilder buffer = null;
						String[] args = null;
						if (constraint != null) {
							buffer = new StringBuilder();
							buffer.append("UPPER(");
							buffer.append(Phone.DISPLAY_NAME);
							buffer.append(") GLOB ? or ");
							buffer.append("UPPER(");
							buffer.append(Phone.NUMBER);
							buffer.append(") GLOB ?");
							args = new String[] { "*" + constraint.toString().toUpperCase() + "*" };
						}
						if(cursor != null){
							cursor.close();
						}
						cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,new String[] { Phone.DISPLAY_NAME, Phone.NUMBER,Phone._ID },buffer == null ? null : buffer.toString(), new String[]{args[0],args[0]},Phone.DISPLAY_NAME);
						adapter.changeCursor(cursor);
					}}, 200);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		
		
		Button bt_clear = (Button) findViewById(R.id.bt_clear);
		bt_clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				String str = et_edit.getText().toString();
//				if(str.equals("") || str == null){
//					return;
//				}else{
//					et_edit.setText("");
//					if(cursor != null){
//						cursor.close();
//					}
//					cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,new String[] { Phone.DISPLAY_NAME, Phone.NUMBER, Phone._ID },
//							                            null, null, Phone.DISPLAY_NAME);
//					adapter.changeCursor(cursor);
//				}
				
				if(checkedPeople.size() == 0){
					Toast.makeText(HContactActivity.this, R.string.nochoiceContacts,  Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intentOk = new Intent();
				intentOk.putStringArrayListExtra("numbercontact",(ArrayList<String>) checkedPeople);
				setResult(11, intentOk);
				finish();
			}
		});
	}
	
	

	/***
	 * 创建选项菜单
	 */
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		if (isRecentContact == false) {
			menu.add(0, CHECKALL, 0, R.string.checkall);
			menu.add(0, CHECKNULL, 0, R.string.checknull);
			if (getIntent().getStringExtra("main") != null) {
				menu.add(0, SEND, 0, R.string.send);
			}
		}
		return super.onCreateOptionsMenu(menu);
	}

	/***
	 * 选项菜单点击响应
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case CHECKALL:

			checkedPeople.clear();
			if (!isRecentContact) {
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					checkedPeople.add(cursor.getString(1));
				}
				adapter.notifyDataSetChanged();
			} else {

				for (int i = 0; i < list.size(); i++) {
					cursor.moveToPosition(i);
					checkedPeople.add(cursor.getString(1));
				}
				listAdapter.notifyDataSetChanged();
			}
			break;
		case CHECKNULL:
			checkedPeople.clear();
			if (!isRecentContact) {
				adapter.notifyDataSetChanged();
			} else {
				for (int i = 0; i < list.size(); i++) {
					cursor.moveToPosition(i);
					checkedPeople.add(cursor.getString(1));
				}
				listAdapter.notifyDataSetChanged();
			}
			break;
		case SEND:
			shearSendSms();

			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public  List<String> removeDuplicateWithOrder(List<String> list) {
        Set<String> set = new HashSet<String>();
        List<String> newList = new ArrayList<String>();
        for (Iterator<String> iter = list.iterator(); iter.hasNext();) {
        	String element = iter.next();
            if (set.add(element))
                newList.add(element);
        }
        return newList;
    }


	private void shearSendSms() {
		mStatistics.add(HStatistics.Z5_13,""+checkedPeople.size(), "", "");
		HLog.i("checkedPeople.size() " +checkedPeople.size());
		 if(checkedPeople.size() == 0){
			Toast.makeText(this, R.string.linkmanInvalid,Toast.LENGTH_LONG).show();
		 }
		 if(!ToolsUtil.readSIMCard(this.getApplication())){
			 return;
		 }
		checkedPeople = removeDuplicateWithOrder(checkedPeople);
		HSmsManage hSmsManage = new HSmsManage(getApplication());
		String message = getString(R.string.inviteBody);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < checkedPeople.size(); i++) {
			if(!checkedPeople.get(i).trim().equals("")){
				String str = checkedPeople.get(i);
				if(str.startsWith("+86")){
					str = str.substring(3);
				}
				sb.append(str).append(",");
			}
		}
		HLog.i("sb.toString() " +sb.toString());
		if (sb.length() > 0 && !sb.toString().trim().equals("")) {
			/**发送邀请信息*/
			String phoneNumber = sb.substring(0, sb.length() - 1);
			int i = hSmsManage.getThreadPosition(phoneNumber);
			HSms sms = null;
			if(i == -1){//新信息
				sms = getNewSms(hSmsManage, message, phoneNumber);
			}else{//旧信息
				sms = getOldSms(hSmsManage, i, message, phoneNumber);
			}
//			hSmsManage.sendSms(sms);
			sms.smsid = null;
			send(sms, hSmsManage);
		
		} else {
			Toast.makeText(this, R.string.linkmanInvalid, Toast.LENGTH_LONG).show();
		}
	}
	
	private void send(final HSms sms,final HSmsManage hSmsManage){
		
		new Thread(){
			public void run(){
				 if(preMarkActivity == 7){//主界面
						Intent intent = new Intent();
						intent.putExtra("address", sms.address);
						intent.putExtra("name",abm.getNameByNumber(sms.address));
						HConst.markActivity = 3;
						intent.setClass(HContactActivity.this, HTalkActivity.class);
						startActivity(intent);
						setResult(105);
						finish();
					}else if(preMarkActivity == 3){//Talk界面
						HConst.markActivity = 3;
						finish();
					}else if(preMarkActivity == 4){//flash界面
						setResult(HConst.resultFlash);
						finish();
					}
				 try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				hSmsManage.sendSms(sms);
			}
		}.start();
		
	}
	
	
	private HSms getOldSms(HSmsManage hSmsManage,int index,String body,String number){
		HSms sms = hSmsManage.getThreadAdapter().get(index).sms;
		sms.address = number;
		sms.body = body;
		sms.time = String.valueOf(System.currentTimeMillis());
		sms.type = "2";
		sms.read = "1";
		sms.threadid = hSmsManage.getThreadIdForAndress(sms.address.split(","));
		return sms;
	}
	
	private HSms getNewSms(HSmsManage hSmsManage,String body,String number){
		HSms sms = new HSms();
		sms.address = number;
		sms.name = abm.getNameByNumber(number);
		sms.body = body;
		sms.time = String.valueOf(System.currentTimeMillis());
		sms.type = "2";
		sms.read = "1";
		sms.threadid = hSmsManage.getThreadIdForAndress(number.split(","));
		return sms;
	}

	
	
	
	/***
	 * 用CursorAdapter显示内容
	 * 
	 * @author Administrator
	 * 
	 */
	public class MyAdapter extends CursorAdapter {
		Cursor cursor;
		Bitmap bitmap;
		ArrayList<ImageView> cbs = new ArrayList<ImageView>();

		public MyAdapter(Context context, Cursor c) {
			super(context, c);
			this.cursor = c;
		}

		@Override
		public void bindView(View view, Context context, final Cursor cursor) {
			final Holder holder;
			holder = (Holder) view.getTag();
			bitmap = abm.getContactPhoto(cursor.getString(0), cursor.getString(1));
			holder.iv_head.setImageBitmap(bitmap);
			holder.textView1.setText(cursor.getString(0));
			TextPaint tp = holder.textView1.getPaint(); 
			tp.setFakeBoldText(true);
			final String number = cursor.getString(1);
			holder.textView2.setText(number);
			holder.cbox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					CheckBox cb = (CheckBox) v;
					if (!cb.isChecked()) {
						cb.setChecked(false);
						checkedPeople.remove(number);
					} else {
						cb.setChecked(true);
						checkedPeople.add(number);
					}
				}
			});
			
			if (checkedPeople.indexOf(cursor.getString(1)) >= 0) {
				holder.cbox.setChecked(true);
			} else {
				holder.cbox.setChecked(false);
			}

		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			Holder holder;
			holder = new Holder();
			View view = ((Activity) context).getLayoutInflater().inflate(R.layout.hcontact_adapter, null);
			holder.cbox = (CheckBox) view.findViewById(R.id.box);
			holder.iv_head = (ImageView) view.findViewById(R.id.iv_thread_head);
			holder.textView1 = (TextView) view.findViewById(R.id.text1);
			holder.textView2 = (TextView) view.findViewById(R.id.text2);
			holder.iv_head.setBackgroundResource(R.drawable.def_head);
			holder.textView1.setText(cursor.getString(0));
			holder.textView2.setText(cursor.getString(1));
			view.setTag(holder);
			return view;
		}

		@Override
		public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
			return super.runQueryOnBackgroundThread(constraint);
		}
		
		// 当选则某项后输入的位置显示的东西。
	}

	public void onDestroy() {
		super.onDestroy();
		if(cursor != null){
			cursor.close();
		}
		System.gc();
		System.gc();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			backClear();
			
			if (!isRecentContact) {
				if(checkedPeople!=null){
					checkedPeople.clear();
				}
				adapter.notifyDataSetChanged();
				finish();
			} else {
				
				rl_edit.setVisibility(View.VISIBLE);
				isRecentContact = false;
				linkmanTextView.setText(R.string.linkman);
				adapter.notifyDataSetChanged();
				ls.setAdapter(adapter);
				listAdapter.notifyDataSetChanged();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private static class Holder {
		ImageView iv_head;
		TextView textView1;
		TextView textView2;
		CheckBox cbox;
	}

//	/***
//	 * 获得一条记录中的所有电话号码
//	 * 
//	 * @param IsPhone
//	 * @param id
//	 * @return
//	 */
//	public String[] findPhone(int IsPhone, String id) {
//		String[] phoneList = null;
//		if (IsPhone > 0) {
//			Cursor phoneNumber_c = getContentResolver()
//					.query(
//							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//							new String[] { Phone.DISPLAY_NAME, Phone.NUMBER,
//									Phone._ID }, null, null, Phone.DISPLAY_NAME);
//			if (phoneNumber_c != null && phoneNumber_c.getCount() != 0) {
//				phoneList = new String[phoneNumber_c.getCount()];
//				int i = 0;
//				while (phoneNumber_c.moveToNext()) {
//					phoneList[i] = phoneNumber_c.getString(0);
//					i++;
//				}
//			}
//			phoneNumber_c.close();
//
//		}
//		return phoneList;
//	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.contactbtn_ok:
			// 发送信息
			if (getIntent().getStringExtra("main") != null) {
				shearSendSms();
			} else {
				Intent intentOk = new Intent();
				intentOk.putStringArrayListExtra("numbercontact",(ArrayList<String>) checkedPeople);
				setResult(11, intentOk);
				finish();
			}
			break;
		case R.id.contactbtn_cel:
			//backClear();
			if (!isRecentContact) {
				checkedPeople.clear();
				adapter.notifyDataSetChanged();
				finish();
			} else {
				rl_edit.setVisibility(View.VISIBLE);
				isRecentContact = false;
				linkmanTextView.setText(R.string.linkman);
				adapter.notifyDataSetChanged();
				ls.setAdapter(adapter);
				listAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.tv_linkman:
			// isRecentContact = false;
			// // linkmanTextView.setTextColor(Color.WHITE);
			// // recentContactTextView.setTextColor(R.color.lxrColor);
			// adapter.notifyDataSetChanged();
			// ls.setAdapter(adapter);
			break;
		case R.id.recentContact:
			if (isRecentContact == false && isSee) {//查看选中
				isSee = false;
				System.gc();
				System.gc();
				isRecentContact = true;
				list.addAll(checkedPeople);
				linkmanTextView.setText(R.string.lookselect);
				// recentContactTextView.setTextColor(Color.WHITE);
				// linkmanTextView.setTextColor(R.color.lxrColor);
				// recentContacts.clear();
				// recentContacts = settingHelper.readRecentContact();
				listAdapter = null;
				listAdapter = new ListAdapter(this);
				ls = (ListView) findViewById(R.id.list);
				// checkedPeople.clear();
				rl_edit.setVisibility(View.GONE);
				ls.setAdapter(listAdapter);
				isSee = true;
			} else if(isRecentContact && isSee){
				isSee = false;
				System.gc();
				System.gc();
				rl_edit.setVisibility(View.VISIBLE);
				isRecentContact = false;
				linkmanTextView.setText(R.string.linkman);
				// linkmanTextView.setTextColor(Color.WHITE);
				// recentContactTextView.setTextColor(R.color.lxrColor);
				//adapter.notifyDataSetChanged();
				ls.setAdapter(adapter);
				isSee = true;
			}

			break;
		}
	}

//	private void backClear() {
//		checkedPeople.clear();
//		if (!isRecentContact) {
//			adapter.notifyDataSetChanged();
//		} else {
//			listAdapter.notifyDataSetChanged();
//		}
//		finish();
//	}

	String str = "";
	int size = 0;

	class ListAdapter extends BaseAdapter {

		Context context;

		public ListAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return checkedPeople.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder hold;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.hcontact_adapter, null);
				hold = new Holder();
				hold.cbox = (CheckBox) convertView.findViewById(R.id.box);
				hold.iv_head = (ImageView) convertView.findViewById(R.id.iv_thread_head);
				hold.textView1 = (TextView) convertView.findViewById(R.id.text1);
				hold.textView2 = (TextView) convertView.findViewById(R.id.text2);
				convertView.setTag(hold);
			} else {
				hold = (Holder) convertView.getTag();
			}
			size = checkedPeople.size() - 1;
			str = checkedPeople.get(size - position).toString();
			String showName = abm.getAppendName(abm.getNameByNumber(str), str);
			hold.textView1.setText(showName);
			hold.iv_head.setImageBitmap(abm.getContactPhoto(showName, str));
			hold.textView2.setText(str);
			hold.cbox.setChecked(true);
			hold.cbox.setClickable(false);
			return convertView;
		}
	}
	
	
}
