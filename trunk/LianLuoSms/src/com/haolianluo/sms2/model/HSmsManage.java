package com.haolianluo.sms2.model;


import java.util.ArrayList;
import java.util.List;

import com.haolianluo.sms2.HPlayMMSAdapter;
import com.haolianluo.sms2.HTalkAdapter;
import com.haolianluo.sms2.HThreadAdapter;
import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.data.HSmsApplication;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/***
 * 信息管理 
 * @author jianhua 2011年11月29日16:30:35
 */
public class HSmsManage {	
	
	private HSmsApplication mApplication;
	private HThread mMessageModel;
	private final int UPDATA_UI = 0;
	
	public static HTalkAdapter mAdapter;
	private static String mTitlsAddress;
	
	/**
	 * 构造函数
	 * @param context
	 */
	public HSmsManage(Application application){
		mApplication = (HSmsApplication) application;
		mMessageModel = new HThread(mApplication);
	}
	
	
	/**
	 * Talk界面
	 * @param position
	 */
	private String strName = null;
	private String strAddress = null;
	private String strThreadID = null;
	public HTalkAdapter loadTalkList(final int threadposition,String address,String name,String threadID,ListView lv,TextView tv,boolean run){
		mAdapter = new HTalkAdapter(LayoutInflater.from(mApplication),lv,tv,address);
		if(address == null){
			strName = getTalkTitleName(threadposition);
			strAddress = getTalkTitleAddress(threadposition);
			strThreadID = getStringThreadId(threadposition);
		}else{
			strName = name;
			strAddress = address;
			strThreadID = threadID;
		}
		mTitlsAddress = strAddress;
		if(!HConst.iscollect){
			load(threadposition,run);
		}else{
			loadCollectData(strAddress,run,mAdapter);
		}
		return mAdapter;
	}
	
	private void loadCollectData(final String address,boolean isrun,final HTalkAdapter adapter){
		final HCollectTable ct = new HCollectTable(mApplication);
		if(isrun){
			new Thread(){
				public void run() {
					ct.loadTalkSms(address,adapter);
				};
			}.start();
		}else{
			ct.loadTalkSms(address,adapter);
		}
	}
	
	private void load(final int threadposition,boolean isRun){
		try{
			if(isRun){
				new Thread(){
					public void run(){
						readTalkDB(threadposition);
					}

				}.start();
			}else{
				readTalkDB(threadposition);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	private void readTalkDB(final int threadposition) {
		//读取本talk界面所有的mms
		List<HSms> list_mms = loadMms(strThreadID);
		int size = list_mms.size();
		HLog.i("size =====" + size);
		
		int []position = new int[7];
		int cur_smsSize = 0;
		int all_smsSize = 0;
		if(strThreadID!=null && strThreadID.equals("") && strAddress != null&&strAddress.split(",").length > 1 && HConst.isHtc){
			strThreadID = getThreadIdForAndress(strAddress.split(","));
		}
		
		Cursor cursor = mMessageModel.getTalkCursor(strThreadID,strAddress);
		HLog.i("strAddress " + strAddress);
		HLog.i(strThreadID+ "cursor " + cursor.getCount());
		cursor.moveToLast();
		
		if(cursor.getCount() == 0){//全是mms
			int index = 0;
			for(int i = size-1;i >= 0;i--){
				index++;
				HSms sms = list_mms.get(i);
				sms.name = strName;
				sms.address = strAddress;
				sms.smsid = String.valueOf(index);
				addHSms(sms);
			}
			list_mms.clear();
			handler.sendEmptyMessage(UPDATA_UI);
		}
		while (!cursor.isBeforeFirst()) {
			HSms sms = new HSms();
			sms = sms.getTalkMold(cursor, position,strName,strAddress);
			//以date对sms和mms做个排序
			if(size > 0){
				int index = 0;
				for(int i = size -1;i >= 0;i--){//mms 中间
					if(Long.parseLong(list_mms.get(i).time) < Long.parseLong(sms.time)){
						index++;
						HSms sms1 = list_mms.get(i);
						sms1.address = strAddress;
						sms1.name = strName;
						sms1.smsid = String.valueOf(index);
						addHSms(sms1);
						list_mms.remove(i);
						size = list_mms.size();
						continue;
					}
				}
			}
			
			addHSms(sms);
			cursor.moveToPrevious();
			cur_smsSize++;
			all_smsSize++;
			if (cur_smsSize == 10) {
				handler.sendEmptyMessage(UPDATA_UI);
				cur_smsSize = 0;
			}
		}
		
		size = list_mms.size();
		if(size != 0){//mms 最后
			int index = 0;
			for(int i = size-1;i >= 0;i--){
				index++;
				HSms sms = list_mms.get(i);
				sms.name = strName;
				sms.address = strAddress;
				sms.smsid = String.valueOf(index);
				addHSms(sms);
			}
		}
		
		cursor.close();
		if(HConst.isHtc){
			HSms sms = new HSms();
			Cursor cur = sms.getDraftCursor(mApplication, mTitlsAddress);
			if(cur == null)return;
			cur.moveToFirst();
			while (!cur.isAfterLast()) {
				HSms sms1 = new HSms();
				sms1 = sms1.getTalkMold(cur, position,strName,strAddress);
//				sms1.threadid = mApplication.adapter.get(threadposition).sms.threadid;
				addHSms(sms1);
				cur.moveToNext();
			}
			cur.close();
			handler.sendEmptyMessage(UPDATA_UI);
		}
		if (all_smsSize % 10 != 0) {
			handler.sendEmptyMessage(UPDATA_UI);
		}
	}
	
	private void addHSms(HSms sms){
		if(mAdapter != null && !HConst.iscollect){
			mAdapter.add(sms);
		}else{
			return;
		}
	}
	
	
	public Adapter getMMSAdapter(int position){
		String mid = mAdapter.get(position)._id;
		List<HMmsModel> list = mMessageModel.getItemMMS(mid);
		return new HPlayMMSAdapter(LayoutInflater.from(mApplication), list);
	}
	
	
	/***
	 * 界面的刷新
	 */
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case UPDATA_UI:
				if(mApplication.adapter != null){
					mApplication.adapter.notifyDataSetChanged();
				}
				if(mAdapter != null){
					mAdapter.notifyDataSetChanged();
				}
				break;
			case 1:
				if(mApplication.adapter != null){
					mApplication.adapter.notifyDataSetChanged();
				}
				break;
			case 2:
				Bundle bd = msg.getData();
				String thread_id = getThreadFromId(bd.getString("id"));
				if(mApplication.adapter != null){
					for(int j=0;j<mApplication.adapter.size();j++){
						if(mApplication.adapter.get(j).sms.threadid.equals(thread_id)){
							HThread model = mApplication.adapter.get(j);
							model.type = bd.getString("type");
//							model.count = String.valueOf(mAdapter.size());
							mApplication.adapter.set(j, model);
						}
					}
					mApplication.adapter.notifyDataSetChanged();
				}
				break;
			}
		};
	};
	
	

	/**
	 * 短信的发送
	 * @param text
	 * @param number
	 * @param draftId  等于 -1 发的是新信息    否则发送的是草稿
	 * @param address talk界面上面的电话号码
	 */
	public void sendSms(String text,String address,String name){
		HSms sms = new HSms();
		sms.address = address;
		sms.body = text;
		sms.name = name;
		sms.time = String.valueOf(System.currentTimeMillis());
		sms.type = "2";
		sms.read = "1";
		sms.threadid = getThreadIdForAndress(sms.address.split(","));
		sms.ismms = "0";
		sendLianLuoSms(sms,false,false);
	}
	
	/***
	 * 添加完联系人对主talk界面List的更新
	 */
	public void addContactsUpdataList(int position){
		if(mAdapter != null){
			HAddressBookManager abm = new HAddressBookManager(mApplication);
			String address = mAdapter.get(position).address;
			String name = abm.getNameByNumber(address);
			HSms model = mAdapter.get(position);
			model.name = name;
			mAdapter.set(position, model);
		}
	}
	
	/***
	 * 有没有名字
	 * @return true 有  false 没有
	 */
	public List<String> isGetName(String address,String name){
		List<String> list = new ArrayList<String>();
		String [] addressArrary = address.split(",");
		String [] nameArrary = name.split(",");
		for(int i=0;i<nameArrary.length;i++){
			if(nameArrary[i].equals(HConst.defaultName)){
				list.add(addressArrary[i]);
			}
		}
		return list;
	}
 
	/**
	 * 重发
	 * @param position
	 * @param threadPosition
	 */
	public void resend(int position) {
		HSms sms = mAdapter.get(position);
		sms.type = "2";
		sendLianLuoSms(sms,false,true);
	}
	 
	 /***
	  * 清空talkList
	  */
	 public void clearTalkList(){
		 if(mAdapter == null)return;
		 mAdapter.clear();
		 mAdapter = null;
		 mTitlsAddress = null;
	 }
	 
	 public void addDraft(String body,String address,String name){
		 HSms sms = getDraft();
		 if(sms == null){
			 sms = new HSms();
		 }
		 int id = getSmsMaxId()+1;
		 sms.smsid = null;
		 sms.address = address;
		 sms.name = name;
		 sms.body = body;
		 sms.type = "3";
		 sms.read = "1";
		 sms.time = String.valueOf(System.currentTimeMillis());
		 sms.ismms = "0";
//		 sms.threadid = getThreadIdForAndress(address.split(","));
		 if(!HConst.isHtc){
			 sms.threadid = getThreadIdForAndress(address.split(","));
		 }else{
			 sms.threadid = "";
		 }
		 updataList(sms,true,false,id,false);
	 }
	 
	 
	 private HSms getDraft(){
		if (mAdapter != null) {
			int size = mAdapter.size();
			for (int i = 0; i < size; i++) {
				if (mAdapter.get(i).type.equals("3")) {
					return mAdapter.get(i);
				}
			}
		}
		return null;
	 }
	 
	 
	 /**
	  * 读取mms
	  * @param position
	  */
	 private List<HSms> loadMms(String id){
		 return mMessageModel.getMMS(id);
	 }
	 
	 public HSms getMMS(String _id){
		 HSms sms = mMessageModel.getDownFinishSms(_id);
		 sms.address = getAddressForThreadId(sms.threadid).address;
		 sms.name = getAddressForThreadId(sms.threadid).name;
		 sms.ismms = "1";
		 sms.read = "0";
		 return sms;
	 }
	 
	
	 private HThread getAddressForThreadId(String threadId){
		 HThreadManager threadManager = new HThreadManager(mApplication);
		 HThread thread = threadManager.getThreadForThreadId(threadId);
		 return thread;
	 }

	 
	 /***
	  *是否跳到播放彩信界面
	  * @return
	  */
	 public boolean isGotoPlayMms(int position){
		 if("1".equals(mAdapter.get(position).ismms)){
			 return true;
		 }
		 return false;
	 }
	 
	 /***
	  * 得到彩信
	  * @return
	  */
	 public HSms getModelMMS(int position){
		 return mAdapter.get(position);
	 }
	 
	 /***
	  * 得到talk title 上面的头像
	  * @param threadPosition
	  * @return
	  */
	 public Bitmap getHeadBitmap(String address){
		 for(int i = 0;i < mApplication.adapter.size();i++){
			 if(mApplication.adapter.get(i).address.equals(address)){
				 return mApplication.adapter.get(i).headbm;
			 }
		 }
		 return null;
	 }
	 
	 public Bitmap getHeadBitmap(int threadPosition){
		 if(mApplication.adapter == null){
			 return null;
		 }
		 if(!HConst.iscollect)
			 return mApplication.adapter.get(threadPosition).headbm;
		 else{
			 if(mApplication.collectAdapter == null){
				 return null;
			 }
			 return mApplication.collectAdapter.get(threadPosition).headbm;
		 }
	 }
	 /***
	  * 得到talk title 上面的地址
	  * @param threadPosition
	  * @return
	  */
	 public String getTalkTitleAddress(int threadPosition){
		 if(mApplication.adapter == null){
			 return "";
		 }
		 if(!HConst.iscollect)
			 return mApplication.adapter.get(threadPosition).address;
		 else{
			 if(mApplication.collectAdapter == null){
				 return "";
			 }
			 return mApplication.collectAdapter.get(threadPosition).address;
		 }
	 }
	 
	 
	 public String getStringThreadId(int threadPosition){
		 if(mApplication.adapter == null){
			 return "-1";
		 }
		 if(!HConst.iscollect)
		     return mApplication.adapter.get(threadPosition).sms.threadid;
		 else{
			 if(mApplication.collectAdapter == null){
				 return "-1";
			 }
			 return mApplication.collectAdapter.get(threadPosition).sms.threadid;
		 }
	 }
	 
	 /***
	  * 得到talk title 上面的名字
	  * @param threadPosition
	  * @return
	  */
	 public String getTalkTitleName(int threadPosition){
		 if(mApplication.adapter == null){
			 return "";
		 }
		 if(!HConst.iscollect)
			 return mApplication.adapter.get(threadPosition).name;
		 else{
			 if(mApplication.collectAdapter == null){
				 return "";
			 }
			 return mApplication.collectAdapter.get(threadPosition).name;
		 }
	 }
	 
	 
	 
	 
		/**
		 * 删除Talk界面中一条记录
		 * @param threadPosition
		 * @param talkPosition
		 * @return  本talk删除完了
		 */
		public boolean deleteTalkItem(String address,int talkPosition){
			String threadId = mAdapter.get(talkPosition).threadid;
			String id = mAdapter.get(talkPosition).smsid;
			String type = mAdapter.get(talkPosition).type;
			String isMMS = mAdapter.get(talkPosition).ismms;
			int caoGaoSize = getDraftCount(mAdapter.getList());//得到本talk草稿的数量
			if(type.equals("3")){
				caoGaoSize--;
			}
			if(isMMS.equals("1")){
				id = mAdapter.get(talkPosition)._id;
			}
			mAdapter.get(talkPosition).deleteTalkItemDB(mApplication,id,isMMS);
			//更新talk
			mAdapter.remove(talkPosition);
			handler.sendEmptyMessage(UPDATA_UI);
			
			
			if(mApplication.adapter == null){
				HBufferList updateListDB = new HBufferList(mApplication);
				boolean is = updateListDB.cxBuffeListIsNull();
				if(!is){
					if(mAdapter.size() != 0){
						updateListDB.updataSms(mAdapter.get(mAdapter.size() - 1).time,mAdapter.get(mAdapter.size() - 1).body,mAdapter.get(mAdapter.size() - 1).threadid,type);
					}else{
						updateListDB.deleteBufferItem(threadId);
						return true;
					}
				}
				handler.sendEmptyMessage(UPDATA_UI);
				return false;
			}
			
			int threadPosition = -1;
			for(int i = 0;i < mApplication.adapter.size();i++){
				if( mApplication.adapter.get(i).address.equals(address)){
					threadPosition = i;
					break;
				}
			}
			if(mAdapter.size() >= 1){
				HThread modle = mApplication.adapter.get(threadPosition);
				String body = mAdapter.get(mAdapter.size() - 1).body;
				String time =  mAdapter.get(mAdapter.size() - 1).time;
				modle.ismms = mAdapter.get(mAdapter.size() - 1).ismms;
				modle.sms.time = time;
//				String ism  = mAdapter.get(mAdapter.size() - 1).ismms;
//				if(ism.equals("1")){
//					modle.sms.body = "彩信";
//				}else{
					modle.sms.body = body;
//				}
				if (!type.equals("3")) {
				    modle.count = String.valueOf(Integer.parseInt(modle.count) - 1);
				}
				if(caoGaoSize != 0){
					modle.type = "3";
				}else{
//					modle.type = "1";
//					modle.isFailed(modle.sms.threadid);
					modle.type = isFailed(mAdapter.getList());
				}
				mApplication.adapter.set(threadPosition, modle);
//				mApplication.adapter.get(threadPosition).sms.deleteTalkItemDB(mApplication,id,isMMS);
				HThread modle1 =  mApplication.adapter.get(threadPosition);
				mApplication.adapter.remove(threadPosition);
				Long preTime = Long.parseLong(modle1.sms.time);
				boolean is = false;
				for(int i = 0;i < mApplication.adapter.size();i++){
					if(preTime >= Long.parseLong(mApplication.adapter.get(i).sms.time)){
						is = true;
						mApplication.adapter.add(i, modle1);
						break;
					}
				}
				if(!is){
					mApplication.adapter.add(mApplication.adapter.size(), modle1);
				}
				
				
				handler.sendEmptyMessage(UPDATA_UI);
			}else{
//				mApplication.adapter.get(threadPosition).sms.deleteTalkItemDB(mApplication,id,isMMS);
				mApplication.adapter.remove(threadPosition);
				handler.sendEmptyMessage(UPDATA_UI);
				return true;
			}
			return false;
			
		}
		
		
		/***
		 * 
		 * @param 查询是否为发送失败
		 * @return
		 */
		protected String isFailed(List<HSms> list) {
			int size = list.size();
//			int draftCount = 0;
			for(int i = 0;i < size;i++){
				if(list.get(i).type.equals("5")){
					return "5";
				}
			}
			return "2";
			
//			String isFailed = "2";
//			Cursor cursor = null;
//			cursor = mApplication.getContentResolver().query(Uri.parse("content://sms/failed"), null, "thread_id=" + threadId,null, null);
//			if(cursor.getCount() > 0){
//				isFailed = "5";
//			}
//			cursor.close();
//			return isFailed;
		}
		
		
		/***
		 * 得到草稿的个数
		 * @return
		 */
		private int getDraftCount(List<HSms> list){
			int size = list.size();
			int draftCount = 0;
			for(int i = 0;i < size;i++){
				if(list.get(i).type.equals("3")){
					draftCount++;
				}
			}
			return draftCount;
		}
		
		public void sendSms(HSms sms){
			sendLianLuoSms(sms,false,false);
		}
		
		
		/***
		 * 发送新信息
		 * @param sms
		 */
		public void sendLianLuoSms(final HSms sms,final boolean isDraft,final boolean isresend) {
			 if(!ToolsUtil.readSIMCard(mApplication)){
				 return;
			 }
			 if(sms.address == null || sms.address.equals("")){
				 return;
			 }

			int id = getSmsMaxId()+1;
			HLog.i("id " + id );
			insertSendData(sms, isDraft,id,isresend);
			sendSMS(sms.address, sms.body,id);
			
		}

		public void insertSendData(HSms sms,boolean isDraft,int id,boolean isresend){
			 boolean isupdb = true;
			 HHistoryLinkman hl = new HHistoryLinkman(mApplication);
			 hl.addDistoryLinkManList(sms.address);
//			 new HStatistics(mApplication).add(HStatistics.Z20, String.valueOf(sms.body.length()), String.valueOf(sms.address.split(",").length), System.currentTimeMillis() + "." + System.currentTimeMillis());
			 if (sms.smsid == null) {
				 sms.type = "2";
			 }else{
				 sms.updataSystem(mApplication,sms,id);
				 isupdb = false;
			 }
			 updataList(sms,isupdb,isDraft,id,isresend);
		}
		
		
		/***
		 * 发送
		 * @param phoneNumber
		 * @param message
		 */
		private void sendSMS(String phoneNumber,  String message,int id) {
		    Bundle bd = new Bundle();
			Intent intent = new Intent();
//			bd.putString("address", "111,18210061352");
//			bd.putString("address", "18210061352");
//			bd.putString("address", "15001233341,18210061352");
			bd.putString("address", phoneNumber);
			bd.putString("body", message);
			bd.putInt("smsId", id);
			intent.setClass(mApplication, SmsSendService.class);
			intent.putExtras(bd);
			mApplication.startService(intent);
			
			
//			new Thread() {
//				public void run() {
//					int sum = 0;
//					String[] aaressArray = phoneNumber.split(",");
//					SmsManager sms = SmsManager.getDefault();
//					while (sum < aaressArray.length) {
//						/** 大于七十字时按长短信发送 */
//						if (message.length() > 70) {
//							ArrayList<String> al = new ArrayList<String>();
//							al = sms.divideMessage(message);
//							if (!aaressArray[sum].equals("")) {
//								sms.sendMultipartTextMessage(aaressArray[sum],null, al, null, null);
//							}
//						} else {
//							if (!aaressArray[sum].equals("") && !message.equals("")) {
//								sms.sendTextMessage(aaressArray[sum], null,message, null, null);
//							}
//						}
//						sum = sum + 1;
//					}
//				}
//			}.start();
		}
		
		/***
		 * 根据电话号码，获得threadid
		 * @param aaressArray
		 * @return
		 */
		public String getThreadIdForAndress(String[] aaressArray) {
			String threadID = "-1";
			Uri.Builder uriBuilder = Uri.parse("content://mms-sms/threadID").buildUpon();
			int sum = 0;
			while (sum < aaressArray.length) {
				if (aaressArray[sum] != null && !aaressArray[sum].equals("")) {
					uriBuilder.appendQueryParameter("recipient", aaressArray[sum]);
				}
				sum = sum + 1;
			}
			Uri uri = uriBuilder.build();
			Cursor cursor = mApplication.getContentResolver().query(uri,new String[] { "_id" }, null, null, null);

			if (cursor != null) {
				if (cursor.moveToFirst()) {
					threadID = cursor.getString(0);
				}
			}
			cursor.close();
			return threadID;
		}
		
		/**
		 * 对界面的更新
		 * @param sms
		 */
		public void updataList(HSms sms,boolean isupdb,boolean isDraft,int id,boolean isresend){
			HBufferList updateListDB = new HBufferList(mApplication);
			boolean is = updateListDB.cxBuffeListIsNull();
			if(!is && mApplication.adapter == null){
				updateListDB.updataDB(sms, mApplication);
			}
			HAddressBookManager abm = new HAddressBookManager(mApplication);
			int position = -1;
			int len = sms.address.split(",").length;
			//thread
			if(mApplication.adapter != null && !isresend){
				position = getThreadPosition(sms.address);
				HThread thread = null;
				if(position != -1){//旧信息
					thread = mApplication.adapter.get(position);
					thread.address = sms.address;
					if(!sms.type.equals("3") && !isresend){
						thread.count = String.valueOf(Integer.parseInt(thread.count) + len);
					}
					
					if(isDraft){//发的草稿
						int temp =  getDraftCount(mAdapter.getList());
						if(temp == 0){
							thread.type = sms.type;
						}else{
							thread.type = "3";
						}
					}else{
						if(thread.type.equals("3")){
							//thread.type = "3";
							thread.type = sms.type;
						}else{
							thread.type = sms.type;
						}
					}
//					if(sms.read.equals("0")){
//						if(thread.noReadCount.equals("-1"))
//						{
//							thread.noReadCount = "0";
//						}
//						thread.noReadCount = String.valueOf(Integer.parseInt(thread.noReadCount) + 1);
//					}
//					thread.noReadCount = thread.smsNoReadCount(thread.sms.threadid,mApplication);
					
					mApplication.adapter.remove(position);
				}else{//新信息
					thread = new HThread();
					if(sms.type.equals("3")){
						thread.count = "0";
					}else{
						thread.count = String.valueOf(len);
					}
					thread.address = sms.address;
					thread.name = sms.name;
					thread.type = sms.type;
//					thread.noReadCount = thread.smsNoReadCount(thread.sms.threadid,mApplication);
					//thread.noReadCount = "1";
					
				}
				thread.sms = sms;
				thread.sms.smsid = String.valueOf(id);
				thread.headbm = abm.getContactPhoto(thread.name, thread.address);
				thread.ismms = sms.ismms;
				mApplication.adapter.add(0,thread);
			}
			updataTalk(sms,abm,isupdb,updateListDB,id,isresend);
			handler.sendEmptyMessage(UPDATA_UI);
			HLog.i("updata list end~~~~~~~~~~~~~~~~~");
		}
		
		/***
		 * 对talk的更新
		 * @param sms
		 */
		private void updataTalk(HSms sms,HAddressBookManager abm ,boolean isupdb,HBufferList updateListDB,int id,boolean isresend){
			//talk
			HLog.i(HConst.markActivity+ "mAdapter " + sms.address);
			if(mAdapter != null && (HConst.markActivity == 3 || HConst.markActivity == 4)){
				HLog.i(mTitlsAddress+ "mTitlsAddress " + ToolsUtil.isEqualAddress(mTitlsAddress, sms.address));
				if(mTitlsAddress != null && (isresend?(sms.threadid.equals(getThreadIdForAndress(mTitlsAddress.split(",")))):ToolsUtil.isEqualAddress(mTitlsAddress, sms.address))){
					updateListDB.updataBufferList(sms);
					String []arr = sms.address.split(",");
					int size = arr.length;
					if(sms.type.equals("3")){
						HSms s = sms;
						s.threadid = sms.threadid;
						s.body = sms.body;
						s.ismms = sms.ismms;
						s.read = sms.read;
						s.type = sms.type;
						s.time = sms.time;
						s.address = sms.address;
						s.name = abm.getNameByNumber(sms.address);
						mAdapter.remove(sms);
						addHSms(s);
						isInsertDB(s, isupdb,id);
					}else{
						sms.read = "1";
						if(sms.smsid == null){//新信息
							addSms(sms, abm, arr, size,isupdb,id);
						}else{//更新信息
							
							//----
							HThread temp = mApplication.adapter.get(0);
//							int count = Integer.parseInt(temp.noReadCount) - 1;
//							System.out.println("count ----" + count);
//							temp.noReadCount = count + "";
//							mApplication.adapter.remove(0);
//							mApplication.adapter.add(0,temp);
//							temp.noReadCount = temp.smsNoReadCount(temp.sms.threadid,mApplication);
							//-----
							HSms s = sms;
					    	mAdapter.remove(sms);
					    	addSms(s, abm, arr, size,isupdb,id);
						}
					}
				}else{
					isInsertDB(sms, isupdb,id);
				}
			}else{
				isInsertDB(sms, isupdb,id);
			}
			handler.sendEmptyMessage(UPDATA_UI);
		}


		private void addSms(HSms sms, HAddressBookManager abm, String[] arr,int size,boolean isupdb,int id) {
			for(int i = 0;i < size;i++){
				HSms s = new HSms();
//				s._id = sms._id;
				s._id = String.valueOf(id+i);
				s.smsid = String.valueOf(id+i);
				s.body = sms.body;
				s.ismms = sms.ismms;
				s.read = sms.read;
				s.type = sms.type;
				s.time = sms.time;
				s.address = arr[i];
				s.name = abm.getNameByNumber(arr[i]);
				s.threadid = sms.threadid;
				addHSms(s);
				isInsertDB(s, isupdb,id);
				if(i%10==0){
					handler.sendEmptyMessage(UPDATA_UI);
				}
				if(i == size-1){
					handler.sendEmptyMessage(UPDATA_UI);
				}
			}
		}
		
		private void isInsertDB(HSms s,boolean isupdb,int id){
//			HLog.i("isInsertDB " + isupdb);
			if(isupdb){
				s.insertSystem(mApplication, s,id);
			}
		}
		
		
		public int getThreadPosition(String address){
			return mMessageModel.getPosition(address);
		}
		
		/**
		 * 得到talk需要的adapter
		 * @return
		 */
		public HTalkAdapter getAdapter(){
			return mAdapter;
		}
		
		public HThreadAdapter getThreadAdapter(){
			return mApplication.adapter;
		}
		
		public int getSmsMaxId(){
			int count = 0;
			Cursor cursor = mApplication.getContentResolver().query(Uri.parse("content://sms/"), new String[]{"max(_id)"}, null, null, null);
	 		cursor.moveToFirst();
	 		while(!cursor.isAfterLast()){
	 			count = cursor.getInt(0);
	 			cursor.moveToNext();
	 		}
	 		cursor.close();
	 		if(HConst.isHtc){
	 		//草稿
	 		int count1 = 0;
			Cursor cursor1 = mApplication.getContentResolver().query(Uri.parse("content://sms/draft"), new String[]{"max(_id)"}, null, null, null);
	 		cursor1.moveToFirst();
	 		while(!cursor1.isAfterLast()){
	 			count1 = cursor1.getInt(0);
	 			cursor1.moveToNext();
	 		}
	 		cursor.close();
	 		cursor1.close();
	 		if(count >= count1){
	 			return count;
	 		}else{
	 			return count1;
	 		}
	 		}else{
	 			return count;
	 		}
		}
		
		
		public List<HSms> getReceiverSmsList(){
			return mApplication.list;
		}
		
		
		public Cursor getTalkCursor(String threadID,String address){
			return mMessageModel.getTalkCursor(threadID,address);
		}
		
		public void updataSystemDB(Context context,final String id,final String type){
			
			new HBufferList(mApplication);
			new Thread(){
				public void run(){
					try{
					updataAdapter(id, type);
					}catch(Exception e){
					}
				}
			}.start();
			new Thread(){
				public void run(){
					Bundle b= new Bundle();
					b.putString("id", id);
					b.putString("type",type);
					Message m = handler.obtainMessage(2);
					m.setData(b);
					handler.sendMessage(m);
				}
			}.start();
			new Thread(){
				public void run(){
					ContentValues values = new ContentValues();
					values.put("type", type);
					HLog.i(" type " + type);
					HLog.i( getSmsMaxId()+" getSmsMaxId " + id);
					mApplication.getContentResolver().update(Uri.parse("content://sms/"),values , "_id =" + id, null);
				}
			}.start();
		
		}
		
	
		private void updataAdapter(String id,String type){
			if(mAdapter != null && mAdapter.size() != 0 ){
				for(int i=mAdapter.size()-1;i>=0;i--){
//				for(int i=0;i<mAdapter.size();i++){
					if(mAdapter.get(i).smsid!=null&&mAdapter.get(i).smsid.equals(id)){
						HSms sms = mAdapter.get(i);
						sms.type = type;
						mAdapter.set(i, sms);
//						
					}
				}
				handler.sendEmptyMessage(0);
			}
		}
		
		/***
		 * 根据id获得thread
		 * @param id
		 * @return
		 */
		public String getThreadFromId(String id){
			
			String address = null;
			String where = "_id=" + id; 
			if(!id.equals("0")){
			Cursor cursor = mApplication.getContentResolver().query( Uri.parse("content://sms/"),new String[]{"thread_id"}, where, null, null);
			cursor.moveToFirst();
	 		while(!cursor.isAfterLast()){
	 			address = cursor.getString(0);
	 			cursor.moveToNext();
	 		}
	 		cursor.close();
			}else{
				address = null;	
			}
	 		return address;
		}
}
