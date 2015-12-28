package com.haolianluo.sms2.model;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import com.haolianluo.sms2.R;
import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.data.HSmsApplication;
import com.lianluo.core.util.ToolsUtil;


import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

/***
 * @author jianhua   2011年11月15日17:39:46
 */

public class HThread {
	public String address;
	public String name = HConst.defaultName;
	public String count ="0";
	public Bitmap headbm;
	public String mark = "0";//是否被标记  0 没有被标记      1标记
	public String ismms = "0";
	public String type;
//	public String noReadCount = "0";
	public HSms sms = new HSms();
	
	//URI--------------------
	private static final Uri SMS_URI = Uri.parse("content://sms/");
	private static final Uri SMS_URI_DRAFT = Uri.parse("content://sms/draft"); // 草稿
	private static final Uri SMS_URI_FAILED = Uri.parse("content://sms/failed"); // 失败
	private static final Uri Canonical_addresses_uri =Uri.parse("content://mms-sms/canonical-addresses");
	private static final Uri ThreadsUri = Uri.parse("content://mms-sms/conversations?simple=true");
	//彩信
	private static final Uri URI_MMS_PDU = Uri.parse("content://mms/");//pdu表
	private static final Uri URI_MMS_PATT = Uri.parse("content://mms/part");//part表
	//表内字段---------------
	private static final String ID = "_id";
	private static final String THREAD_ID = "thread_id";
	private static final String DATE = "date";
	private static final String ADDRESS = "address";
	private static final String READ = "read";
	private static final String TYPE = "type";
	private static final String BODY = "body";
	private static final String COMMA = ",";
	private static final String SNIPPET = "snippet";
	private static final String MESSAGE_COUNT = "message_count";
	private static final String SNIPPET_CS = "snippet_cs";
	private static final String RECIPIENT_IDS = "recipient_ids";
	private static final String HAS_ATTACHMENT = "has_attachment";
	
	private HSmsApplication mApplication;
	
	public HThread(){}
	
	public HThread(Application application) {
		mApplication = (HSmsApplication) application;
	}
	

	/**
	 * 读取thread的一条记录
	 * @param cursor
	 * @param position
	 * @return
	 */
	public HThread getModel(Cursor cursor,int []position){
		if (cursor.getPosition() == 0) {
			position[0] = cursor.getColumnIndex(SNIPPET);
			position[1] = cursor.getColumnIndex(DATE);
			position[2] = cursor.getColumnIndex(READ);
			position[3] = cursor.getColumnIndex(MESSAGE_COUNT);
			position[4] = cursor.getColumnIndex(HAS_ATTACHMENT);
			position[5] = cursor.getColumnIndex(SNIPPET_CS);
			position[6] = cursor.getColumnIndex(RECIPIENT_IDS);
			position[7] = cursor.getColumnIndex(ID);
		}
		HThread model = null;
		String recipientsId = cursor.getString(position[6]).trim().replace(" ", COMMA);// 电话号码id
    	String id = cursor.getString(position[7]);
    	 //根据recipientsId读取电话号码
		StringBuffer addressSb = new StringBuffer();
		Cursor cur = mApplication.getContentResolver().query(Canonical_addresses_uri, null,ID +" in (" + recipientsId + ")", null, null);
		cur.moveToFirst();
		while(!cur.isAfterLast()){
			position[8] = cur.getColumnIndex(ADDRESS);
			addressSb.append(cur.getString(position[8]).trim()).append(",");
			cur.moveToNext();
		}
		cur.close();
		//在LG P970上可以保存无号码的草稿
		String address = null;
		if(addressSb.toString().length() == 0){
			address = "";
		}else{
			address = ToolsUtil.sortNumber(addressSb.toString().substring(0,addressSb.length() -1));
		}
		
		HAddressBookManager abm = new HAddressBookManager(mApplication);
		//获得名字
		String name = abm.getNameByNumber(address);
		//判断是否有草稿
		String type = isDrart(address,id);
		if(type.equals("0")){
			type = isFailed(id);
		}
		String noReadCount = smsNoReadCount(id,mApplication);
		//获取头像
		Bitmap headBp = abm.getContactPhoto(name, address);
		String body = cursor.getString(position[0]);
    	/**判断是否有彩信，如果有则查询sms表,等于1说明有彩信，应给查询sms*/
		if (!cursor.getString(position[4]).equals("0") || !"0".equals(cursor.getString(position[5]))) {
			boolean is = getMmsType(id);
			if(is){
				type = "3";
			}
			
			String date = cursor.getString(position[1]);
		//	boolean is1 = getSmsDataForThreadID(date);
			String str1 = cursor.getString(position[5]);
			String ismms = "0";
			if(str1 == null || "106".equals(str1)){//thraed表最后一条是彩信
				try {
					ismms = "1";
					String str = null;
					if(body != null && cursor.getString(position[5]) != null &&  !cursor.getString(position[5]).equals("0")){
						str = new String(body.getBytes("iso-8859-1"), "UTF-8");
					}
					if (str == null || "".equals(str)) {
						body = "(" + mApplication.getString(R.string.wzt) +")";
					} else {
						body = mApplication.getString(R.string.zt) + str;
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			
			String []arr = {id,name,address,cursor.getString(position[3]),body,date,type,cursor.getString(position[2]),ismms,noReadCount};
			model = setSmsModel(arr,headBp);
		 }else{
			String []arr = {id,name,address,cursor.getString(position[3]),body,cursor.getString(position[1]),type,cursor.getString(position[2]),"0",noReadCount};
			model = setSmsModel(arr,headBp);
		}
		return model;
	}
	
	
	public HThread getHtcModel(Cursor cursor, int[] position) {
		if (cursor.getPosition() == 0) {
			position[0] = cursor.getColumnIndex(SNIPPET);
			position[1] = cursor.getColumnIndex(DATE);
			position[2] = cursor.getColumnIndex(READ);
			position[3] = cursor.getColumnIndex(MESSAGE_COUNT);
			position[4] = cursor.getColumnIndex(HAS_ATTACHMENT);
			position[5] = cursor.getColumnIndex(SNIPPET_CS);
			position[6] = cursor.getColumnIndex(RECIPIENT_IDS);
			position[7] = cursor.getColumnIndex(ID);
		}
		HThread model = null;
		String thread_id = cursor.getString(position[7]);
		String recipient_address = cursor.getString(cursor.getColumnIndex("recipient_address"));
		String[] addressArrary = recipient_address.split(";");
		StringBuffer addressSb = new StringBuffer();
		for (int i = addressArrary.length - 1; i >= 0; i--) {
			addressSb.append(addressArrary[i]).append(",");
		}
		String address = ToolsUtil.sortNumber(addressSb.toString().substring(0,
				addressSb.length() - 1));
		// 获得名字
		String name = new HAddressBookManager(mApplication)
				.getNameByNumber(address);
		// 判断是否有草稿
		String type = isDrart(address, thread_id);
		String noReadCount = smsNoReadCount(thread_id,mApplication);
//		if (type.equals("2")) {
//			type = isFailed(thread_id);
//		}
		
		if(type.equals("0")){
			type = isFailed(thread_id);
		}
		HAddressBookManager abm = new HAddressBookManager(mApplication);
		// 获取头像
		Bitmap headBp = abm.getContactPhoto(name, address);
		String body = cursor.getString(position[0]);
		/** 判断是否有彩信，如果有则查询sms表 */
		if (!cursor.getString(position[4]).equals("0") || !"0".equals(cursor.getString(position[5]))) {

			boolean is = getMmsType(thread_id);
			if(is){
				type = "3";
			}
			String date = cursor.getString(cursor.getColumnIndex(DATE));
//			boolean is1 = getSmsDataForThreadID(date);
			String str1 = cursor.getString(position[5]);
			String ismms = "0";
			if(str1 == null || "106".equals(str1)){//thraed表最后一条是彩信
				try {
					ismms = "1";
					String str = null;
					if(body != null && cursor.getString(position[5]) != null &&  !cursor.getString(position[5]).equals("0")){
						str = new String(body.getBytes("iso-8859-1"), "UTF-8");
					}
					if (str == null || "".equals(str)) {
						body = "(" + mApplication.getString(R.string.wzt) +")";
					} else {
						body = mApplication.getString(R.string.zt) + str;
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			String []arr = {thread_id,name,address,cursor.getString(position[3]),body,date, type, cursor.getString(cursor.getColumnIndex(READ)), ismms,noReadCount};
			model = setSmsModel(arr, headBp);
		} else {
			String[] arr = { thread_id, name, address,cursor.getString(position[3]), cursor.getString(position[0]),
					cursor.getString(position[1]), type,cursor.getString(position[2]), "0",noReadCount };
			model = setSmsModel(arr, headBp);
		}
		return model;
	}
	
	
	public Cursor getTalkCursor(String id,String address){
		String str_where = null;
		if (HConst.isHtc) {
			if(!id.equals("")){
				str_where = THREAD_ID + "=" + id +" and " + TYPE+" != 3";
        	}else{
        		str_where = ADDRESS + " = '"+address+"' and "+TYPE+" != 3";
        	}
		} else {
			str_where = THREAD_ID + "=" + id;
		}
		Cursor cursor = mApplication.getContentResolver().query(SMS_URI, null, str_where, null, null);
		return cursor;
	}
	
	
	
	
	
	/**
	 * 删除主界面的每一条
	 * @param position
	 */
	public void deleteThreadItem(String id,String address){
		if(id != null && !id.equals("")){
			String WHERE = THREAD_ID + "=" + id;
			Cursor cur = mApplication.getContentResolver().query(SMS_URI, new String[] { ID },WHERE, null, null);
			cur.moveToFirst();
			while (!cur.isAfterLast()) {
				mApplication.getContentResolver().delete(Uri.parse(SMS_URI + String.valueOf(cur.getLong(0))), null, null);
				cur.moveToNext();
			}
			cur.close();
			
			String WHERE1 = THREAD_ID + "=" + id;
			Cursor cur1 = mApplication.getContentResolver().query(Uri.parse("content://mms/"), new String[] { ID },WHERE1, null, null);
			cur1.moveToFirst();
			while (!cur1.isAfterLast()) {
				HConst.isDeleteMMS = true;
				mApplication.getContentResolver().delete(Uri.parse("content://mms/" + String.valueOf(cur1.getLong(0))), null, null);
				cur1.moveToNext();
			}
			cur1.close();
		}
		if(HConst.isHtc){
			String strAddress = address.replace(",", ";");
			ArrayList<String> list = new ArrayList<String>();
			
			String[]str = strAddress.split(";");
		    StringBuffer sbAdd = new StringBuffer();
		    if(str.length < 5){
		    	  ToolsUtil.perm(str,0,str.length-1,list);  
		    	 for(int i=0;i<list.size();i++){
		         	if(i == 0){
		     			sbAdd.append("type=3 and ");
		     			sbAdd.append(" address like '"+list.get(i).substring(0, list.get(i).length()-2)+"'");
		     		}else{
		     			sbAdd.append(" or address like '"+list.get(i).substring(0, list.get(i).length()-2)+"'");
		     		}
		         }
		    }else{
		    	sbAdd.append("type=3 and address='"+strAddress+"'");
		    }
	       
			String where = sbAdd.toString();
             mApplication.getContentResolver().delete(SMS_URI, where, null);
		}
		sms.notification_updata(mApplication);
	}
	
	/***
	 * 删除主界面所有的信息
	 */
	public void deleteAllSms(){
		mApplication.getContentResolver().delete(Uri.parse("content://mms-sms/conversations"), null, null);
	}
	
   
	/**
	 * 
	 * @return 获得未读短信数量
	 */
	public int smsNoReadCount(){
		Cursor cursor = null;
		int count = 0;
		String selection = READ + "= 0";
 		cursor = mApplication.getContentResolver().query(SMS_URI, new String[] { "count(*)" }, selection, null, null);
 		cursor.moveToFirst();
 		while(!cursor.isAfterLast()){
 			count = cursor.getInt(0);
 			cursor.moveToNext();
 		}
 		cursor.close();
 		return count;
	}
	
	
	public String smsNoReadCount(String ThreadID,Context context){
		Cursor cursor = null;
		int count = 0;
		//sms
		String selection = READ + "= 0 and " + THREAD_ID + "=" + ThreadID;
 		cursor = context.getContentResolver().query(SMS_URI, new String[] { "count(*)" }, selection, null, null);
 		cursor.moveToFirst();
 		while(!cursor.isAfterLast()){
 			count += cursor.getInt(0);
 			cursor.moveToNext();
 		}
 		cursor.close();
 		
 		//mms
 		selection = READ + "= 0 and " + THREAD_ID + "=" + ThreadID;
 		cursor = context.getContentResolver().query(URI_MMS_PDU, new String[] { "count(*)" }, selection, null, null);
 		cursor.moveToFirst();
 		while(!cursor.isAfterLast()){
 			count += cursor.getInt(0);
 			cursor.moveToNext();
 		}
 		cursor.close();
 		return String.valueOf(count);
	}
	
	
	
	/**
	 * 给model设置相应的值
	 * @param model
	 * @param arr[0]threadId
	 * @param arr[1]name
	 * @param arr[2]address
	 * @param arr[3]count
	 * @param arr[4]body
	 * @param arr[5]time
	 * @param arr[6]type
	 * @param arr[7]read
	 * @param arr[8]ismms
	 * @param Bitmap
	 */
	private HThread setSmsModel(String []arr,Bitmap Bitmap){
		HThread model = new HThread();
		model.sms.threadid = arr[0];
		model.name = arr[1];
		model.address = arr[2];
		model.count = arr[3];
		model.sms.body = arr[4];
		model.sms.time = arr[5];
		model.type = arr[6];
		model.sms.read = arr[7];
		model.ismms = arr[8];
//		model.noReadCount = arr[9];
		model.headbm = Bitmap;
		return model;
	}
	
	
	/***
	 * 
	 * @param thread_id
	 * @return 得到分组后，最近一条记录的id
	 */
//	private String groupByMaxId(String thread_id) {
//		Cursor cur = null;
//		String id = null;
//		String where = null;
//		where = THREAD_ID + "=" + thread_id;
//		cur = mApplication.getContentResolver().query(SMS_URI,new String[] { "max(" + ID + ")" }, where, null, null);
//		cur.moveToFirst();
//		while (!cur.isAfterLast()) {
//			id = cur.getString(0);
//			cur.moveToNext();
//		}
//		cur.close();
//		return id;
//	}
	
	/***
	 * 
	 * @param smsCount
	 *            用于存放当前id的条数
	 * @param id
	 *            thread_id
	 * @return 查询sms表返回某条记录的个数
	 */
//	private String querySmsCount(String thread_id) {
//		String smsCount = null;
//		Cursor cursor = null;
//		String whereAddressNum = null;
//		whereAddressNum = THREAD_ID + "=" + thread_id + " and " + TYPE + " != 3";
//		cursor = mApplication.getContentResolver().query(SMS_URI,new String[] { "count(*)" }, whereAddressNum, null, null);
//		cursor.moveToFirst();
//		while (!cursor.isAfterLast()) {
//			smsCount = cursor.getString(0);
//			cursor.moveToNext();
//		}
//		cursor.close();
//		return smsCount;
//	}
	
		
	public String isDrart(String addressArrary, String thread_id) {
		// 查询是不是草稿
		String isCaoGao = "0";
		Cursor cursor_caogao = null;
		if (HConst.isHtc) {
			String whereAddress = ADDRESS + "= '" + addressArrary +"'";
			cursor_caogao = mApplication.getContentResolver().query(SMS_URI, null, whereAddress, null, null);
		} else {
			cursor_caogao = mApplication.getContentResolver().query(SMS_URI_DRAFT, null, THREAD_ID + "=" + thread_id,null, null);
		}
		cursor_caogao.moveToFirst();
		if (cursor_caogao.getCount() != 0) {
			isCaoGao = cursor_caogao.getString(cursor_caogao.getColumnIndex(TYPE));
		}
		cursor_caogao.close();
		return isCaoGao;
	}
	
	
	
	
	/***
	 * 
	 * @param 查询是否为发送失败
	 * @return
	 */
	protected String isFailed(String threadId) {
		String isFailed = "2";
		Cursor cursor = null;
		cursor = mApplication.getContentResolver().query(SMS_URI_FAILED, null, THREAD_ID + "=" + threadId,null, null);
		if(cursor.getCount() > 0){
			isFailed = "5";
		}
		cursor.close();
		return isFailed;
	}
	
	
	
	/**
	 * htc专用读取草稿
	 */
	public void readDraftHtc(){
		/** 是否与threads表中的记录相同，true为相同 */
		/** 从sms表中读取草稿数据HTC信息的读取 */
		ArrayList<HDraftType> draftList = new ArrayList<HDraftType>();
		draftList = readDraftThread();
		HAddressBookManager abm = new HAddressBookManager(mApplication);
		boolean isSame = false;
		int size = mApplication.adapter.size();
		if (size > 0) {
			for (int i = 0; i < draftList.size(); i++) {
				isSame = false;
				for (int j = 0; j < size; j++) {
					if(draftList.get(i).getAddress() != null && !draftList.get(i).getAddress().equals("")){
					String[] listnum = mApplication.adapter.get(j).address.split(",");
					String[] draftnum = draftList.get(i).getAddress().split(";");
					if (isNumSame(listnum, draftnum)) {
						isSame = true;
						mApplication.adapter.get(j).sms.body = draftList.get(i).getBody();
						mApplication.adapter.get(j).sms.read = "1";
						mApplication.adapter.get(j).type = "3";
						mApplication.adapter.set(j, mApplication.adapter.get(j));
					} else {
						if (j == size - 1 && isSame == false) {
							HThread model = new HThread(mApplication);
							String address = ToolsUtil.sortNumber(getAppendString(draftList.get(i).getAddress().split(";")));
							String name = abm.getNameByNumber(address);
							String id = draftList.get(i).getThreadid();
							if (id == null) {
								id = "";
							}
							model.sms.threadid = id;
							model.name = name;
							model.address = address;
							model.type = "3";
							model.headbm = abm.getContactPhoto(name, address);
							model.sms.body = draftList.get(i).getBody();
							model.sms.time = draftList.get(i).getDate();
							model.sms.read = "1";
							model.count = "0";
							mApplication.adapter.add(model);
						}
					}
				}
				}
			}

		} else {
			for (int i = 0; i < draftList.size(); i++) {
				HThread model = new HThread();
				String address = ToolsUtil.sortNumber(getAppendString(draftList.get(i).getAddress().split(";")));
				String name = abm.getNameByNumber(address);
				String str = draftList.get(i).getThreadid();
				if (str == null) {
					str = "";
				}
				model.sms.threadid = "";
				model.name = name;
				model.address = address;
				model.type = "3";
				model.headbm = abm.getContactPhoto(name, address);
				model.sms.body = draftList.get(i).getBody();
				model.sms.time = draftList.get(i).getDate();
				model.sms.read = "1";
				model.count = "0";
				mApplication.adapter.add(model);
			}
		}
	}
	
	
	   /***
     * 比较电话号码是否一样
     * @param listnum
     * @param draftnum
     * @return
     */
	private boolean isNumSame(String[] listnum, String[] draftnum) {
		if (draftnum[0] == null || draftnum[0].equals("")) {
			return false;
		}
		ArrayList<String> list_listnum = new ArrayList<String>();
		ArrayList<String> list_draftnum = new ArrayList<String>();
		int list_listnum_size =  listnum.length;
		int list_draftnum_size =  draftnum.length;
		if(list_listnum_size != list_draftnum_size){
			return false;
		}
		for (int i = 0; i < list_listnum_size; i++) {
			if (listnum[i].startsWith("+86")) {
				listnum[i] = listnum[i].substring(3, listnum[i].length());
			}
			list_listnum.add(listnum[i].trim());
		}
		for (int i = 0; i < list_draftnum_size; i++) {
			if (draftnum[i].startsWith("+86")) {
				draftnum[i] = draftnum[i].substring(3, draftnum[i].length());
			}
			list_draftnum.add(draftnum[i].trim());
		}
		Collections.sort(list_listnum);
		Collections.sort(list_draftnum);
		boolean b = list_listnum.containsAll(list_draftnum);
		return b;
	}
	
	private String getAppendString(String[] arr) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			sb.append(arr[i]);
			sb.append(',');
		}
		return sb.toString().substring(0, sb.length() - 1);
	}
	
	
	/***
	 * 读取草稿
	 * @param draftType
	 * @param 主界面需要分组后再得到草稿
	 * @return
	 */
    private ArrayList<HDraftType> readDraftThread(){
    	ArrayList<HDraftType> draftList = new ArrayList<HDraftType>();
    	 String group = "0==0) GROUP BY (" + " replace("+ADDRESS+",' ','')";
    	 Cursor cur = mApplication.getContentResolver().query(SMS_URI_DRAFT, null, group, null, null);
    	cur.moveToFirst();
		while (!cur.isAfterLast()) {
			String m_body = cur.getString(cur.getColumnIndex(BODY));// 短信内容
			String m_date = cur.getString(cur.getColumnIndex(DATE));// 发送时间
			String m_id = cur.getString(cur.getColumnIndex(ID));
			String m_address = cur.getString(cur.getColumnIndex(ADDRESS));//号码
			String m_threadID = cur.getString(cur.getColumnIndex(THREAD_ID));//号码
			HDraftType draftType = new HDraftType(m_id, m_date, m_body,m_address,m_threadID);
			draftList.add(draftType);
			cur.moveToNext();
		}
		cur.close();
		return draftList;
    }	
    
    
   
	
	
	/***
	 * 添加完联系人对主界面List的更新
	 */
	public void addContactsUpdataList(int threadposition){
		if(mApplication.adapter != null){
			HAddressBookManager abm = new HAddressBookManager(mApplication);
			String address = mApplication.adapter.get(threadposition).address;
			String name = abm.getNameByNumber(address);
			HThread model = mApplication.adapter.get(threadposition);
			model.name = name;
			model.headbm = abm.getContactPhoto(name, address);
			System.out.println("model.name ===" + model.name);
			mApplication.adapter.set(threadposition, model);
		}
	}
	
	/***
	 * 添加完联系人对主界面List的更新
	 */
	public void addContactsUpdataListSearch(int position){
		HThreadManager tm = new HThreadManager(mApplication);
		if(tm.getSearchAdapter() != null){
			HAddressBookManager abm = new HAddressBookManager(mApplication);
			String address = tm.getSearchAdapter().get(position).thread.address;
			String name = abm.getNameByNumber(address);
			HSearch model = tm.getSearchAdapter().get(position);
			model.thread.name = name;
			model.thread.headbm = abm.getContactPhoto(name, address);
			tm.getSearchAdapter().set(position, model);
		}
	}
	
	

	/**
	 * 得到每一条彩信的详细信息
	 * @return
	 */
	public List<HMmsModel> getItemMMS(String mid){
		List<HMmsModel> list = new ArrayList<HMmsModel>();
		String strWhere = " mid = '" + mid + "'";//part表中的mid是pdu中的_id对应的
		Cursor partCursor = mApplication.getContentResolver().query(URI_MMS_PATT, null, strWhere, null, null);
		partCursor.moveToFirst();
		while(!partCursor.isAfterLast()){
			HMmsModel mmsModel = new HMmsModel();
			mmsModel._id = partCursor.getString(partCursor.getColumnIndex("_id"));
			mmsModel.mid = partCursor.getString(partCursor.getColumnIndex("mid"));
			mmsModel.ct = partCursor.getString(partCursor.getColumnIndex("ct"));
			mmsModel.cid = partCursor.getString(partCursor.getColumnIndex("cid"));
			mmsModel.cl = partCursor.getString(partCursor.getColumnIndex("cl"));
			mmsModel._data = partCursor.getString(partCursor.getColumnIndex("_data"));
			mmsModel.text = partCursor.getString(partCursor.getColumnIndex("text"));
			partCursor.moveToNext();
			list.add(mmsModel);
		}
		partCursor.close();
		return list;
	}
	
	/***
	 * 得到本 talk所有的彩信
	 * @param position
	 * @return
	 */
	public List<HSms> getMMS(String id){
		List<HSms> list = new ArrayList<HSms>();
		String where = THREAD_ID + " = " + "'" + id + "'";
		Cursor cursorPdu =  mApplication.getContentResolver().query(URI_MMS_PDU, null,where,null,null);
		cursorPdu.moveToFirst();
		while(!cursorPdu.isAfterLast()){
			HSms model = new HSms();
			String strDate = cursorPdu.getString(cursorPdu.getColumnIndex(DATE)) + "000";
			String type = cursorPdu.getString(cursorPdu.getColumnIndex("msg_box"));
			model.time = strDate;//时间
			model._id = cursorPdu.getString(cursorPdu.getColumnIndex(ID));//pdu表的ID
			model.ismms = "1";//彩信的标志
			model.type = type;
			sms.body = cursorPdu.getString(cursorPdu.getColumnIndex("sub"));
			String str = null;
			try {
				if(sms.body != null){
					str = new String(sms.body.getBytes("iso-8859-1"), "UTF-8");
				}
				if(str == null || "".equals(str)){
					model.body = "(无主题)";
				}else{
					model.body = "主题:" + str;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String ct_t = cursorPdu.getString(cursorPdu.getColumnIndex("ct_t"));//ME_811 手机上会插入一条空短信
			String ct_l = cursorPdu.getString(cursorPdu.getColumnIndex("ct_l"));
			if(ct_t != null || ct_l != null){
				list.add(model);
			}
			cursorPdu.moveToNext();
		}
		cursorPdu.close();
		return list;
	}
	
	
	/**
	 * 得到新的彩信
	 * @return
	 */
	public HSms getDownFinishSms(String _id){
		String where = "_id" + " = " + "'" + _id + "'";
		Cursor cursorPdu = mApplication.getContentResolver().query(URI_MMS_PDU, null,where,null,null);
		cursorPdu.moveToFirst();
		HSms sms = new HSms();
		String strDate = cursorPdu.getString(cursorPdu.getColumnIndex(DATE)) + "000";
		String type = cursorPdu.getString(cursorPdu.getColumnIndex("msg_box"));
		sms.time = strDate;//时间
		sms.threadid = cursorPdu.getString(cursorPdu.getColumnIndex("thread_id"));
		sms._id = cursorPdu.getString(cursorPdu.getColumnIndex("_id"));
		sms.ismms = "1";//彩信的标志
		sms.type = type;
		sms.body = cursorPdu.getString(cursorPdu.getColumnIndex("sub"));
		try {
			if(sms.body != null && !sms.body.equals("")){
				String str = new String(sms.body.getBytes("ISO8859_1"),"utf-8");
				sms.body = "主题:" + str;
			}else{
				sms.body = "无主题";
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		cursorPdu.close();
		return sms;
	}
	
	/**
	 * 得到本talk界面中的彩信有没有是草稿的
	 * @return
	 */
	private boolean getMmsType(String id){
		String where = THREAD_ID + " = " + "'" + id + "'";
		Cursor cursorPdu =  mApplication.getContentResolver().query(URI_MMS_PDU, null,where,null,null);
		cursorPdu.moveToFirst();
		while(!cursorPdu.isAfterLast()){
			String msg_box = cursorPdu.getString(cursorPdu.getColumnIndex("msg_box"));
			if(msg_box.equals("3")){
				cursorPdu.close();
				return true;
			}
			cursorPdu.moveToNext();
		}
		cursorPdu.close();
		return false;
	}
	
//	/**
//	 * 得到本talk最新的一条记录的时间
//	 * */
//	private String getDate(){
//		Cursor cursorPdu =  mApplication.getContentResolver().query(URI_MMS_PDU, null,null,null,null);
//		cursorPdu.moveToLast();
//		String str =  cursorPdu.getString(cursorPdu.getColumnIndex("date")) + "000";
//		cursorPdu.close();
//		return str;
//	}
	
	/***
	 * 判断是否为htc手机，如果有异常抛出说明不是htc手机，并将返回false;
	 * @return
	 */
	public boolean queryIsHTC() {
		boolean isHTC = true;
		Cursor cursor = null;
		cursor = mApplication.getContentResolver().query(ThreadsUri, null, null, null, null);
		if(cursor.getColumnIndex("priority") == -1){
			isHTC = false;
		}
		cursor.close();
		return isHTC;
	}
	
	public int getPosition(String address){
		if(mApplication.adapter == null || address == null){
			return -1;
		}
		String findstr = getString(address);
		for(int i = 0;i < mApplication.adapter.size();i++){
			String adapterAddress = mApplication.adapter.get(i).address;
			String finds = getString(adapterAddress);
			if(findstr.equals(finds)){
				return i;
			}
		}
		return -1;
	}
	
	
	public int getCollectPosition(String address){
		if(mApplication.collectAdapter == null || address == null){
			return -1;
		}
		String findstr = getString(address);
		for(int i = 0;i < mApplication.collectAdapter.size();i++){
			String adapterAddress = mApplication.collectAdapter.get(i).address;
			String finds = getString(adapterAddress);
			if(findstr.equals(finds)){
				return i;
			}
		}
		return -1;
	}
	
	
	
	
	protected String getString(String address){
		 address = address.replace("12520", "").replace("+86", "").replace("12520020", "");
		 return isSame(address);
	}
	
	/**将字符串根据逗号分割为有序数组并返回用逗号分割的字符串**/
	public String isSame(String str) {
		String[] listnum = str.split(",");
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < listnum.length; i++) {
			if (listnum[i].startsWith("+86")) {
				listnum[i] = listnum[i].substring(3, listnum[i].length());
			}
			list.add(listnum[i]);
		}
		Collections.sort(list);
		StringBuffer sbstr = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			sbstr.append(list.get(i)).append(",");
		}
		if (sbstr.length() > 0) {
			str = sbstr.substring(0, sbstr.toString().trim().length() - 1);
		}
		return str;
	}
	
	public boolean getSmsDataForThreadID(String date){
		String where = "date" + "=" + "'" + date  + "'";
		//String order = "date desc";
		Cursor cursor = mApplication.getContentResolver().query(Uri.parse("content://sms/"), null, where, null, null);
		boolean is = false;
		if(cursor.getCount() > 0){
			is = true;
		}
		cursor.close();
		return is;
	}
	
}
