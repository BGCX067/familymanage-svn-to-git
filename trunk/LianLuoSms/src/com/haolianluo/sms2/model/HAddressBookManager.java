package com.haolianluo.sms2.model;

import java.io.InputStream;
import com.haolianluo.sms2.data.HConst;
import com.lianluo.core.util.HLog;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;


/***
 *   通讯录管理类
 * 
 * 2011年9月6日13:16:21
 *
 */

public class HAddressBookManager {
	
	private Context mContext;
	
	public HAddressBookManager(Context context){
		mContext = context;
	}
	/**
	 * 根据电话号码查询姓名
	 * @param number 一个号码、或者多个号码用","分割
	 * @return 一个号码、或者多个号码用","分割
	 */
	public String getNameByNumber(String number){
		if(number == null || number.equals("")){
			return "";
		}
		StringBuffer sb = new StringBuffer();
		String []arrayNum = number.split(",");
		try{
			Uri uri = null;
			Cursor c =null;
			for(int i=0;i<arrayNum.length;i++){
				if(!arrayNum[i].equals("")){
				 uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(arrayNum[i]));
				  c = mContext.getContentResolver().query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
					if(c.moveToNext()){
						if(arrayNum.length == 1){
							sb.delete(0, arrayNum[0].length());
						}
						sb.append(c.getString(0));
						sb.append(",");
					}else{
						sb.append(HConst.defaultName);
						sb.append(",");
					}
					c.close();
				}
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		if(sb.toString().equals("")){
			return "";
		}
		return sb.toString().substring(0, sb.length() - 1);
	}

	
	/**
	 * 根据电话号码查询姓名
	 * @param number 一个号码、或者多个号码用","分割
	 * @return 一个号码、或者多个号码用","分割 例如 ：王五<1231321313>
	 */
	public String NameToNumber(String number){
		if(number == null || number.equals("")){
			return "";
		}
		StringBuffer sb = new StringBuffer();
		String []arrayNum = number.split(",");
		try{
			Uri uri = null;
			Cursor c =null;
			for(int i=0;i<arrayNum.length;i++){
				if(!arrayNum[i].equals("")){
				 uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(arrayNum[i]));
				  c = mContext.getContentResolver().query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
					if(c.moveToNext()){
						if(arrayNum.length == 1){
							sb.delete(0, arrayNum[0].length());
						}
						sb.append(c.getString(0)).append("<").append(arrayNum[i]).append(">");
						sb.append(",");
					}else{
						sb.append(HConst.defaultName);
						sb.append(",");
					}
					c.close();
				}
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		if(sb.toString().equals("")){
			return "";
		}
		return sb.toString().substring(0, sb.length() - 1);
	}

	/***
	 * 如果没有保存好的名字则得到电话号码 
	 * @param _name
	 * @param _address
	 * @return
	 */
	public String getAppendName(String _name ,String _address){
//		HLog.i(_name + " _address " + _address);
		StringBuffer sbf = new StringBuffer();
		if(_name != null){
			String []name = _name.split(",");
			String []address = _address.split(",");
			for(int i=0;i<name.length;i++){
				if (name[i].equals(HConst.defaultName)) {
					sbf.append(address[i]);
				} else {
					sbf.append(name[i]);
				}
				if(i != name.length - 1){
					sbf.append(",");
				}
			}
		}
		return sbf.toString();
	}
	
	
	/***
	 * 根据名字获得联系人在通讯录中的头像
	 * @param address
	 * @return
	 */
	public Bitmap getContactPhoto(String name, String number){ 
		try{
			Bitmap contactPhoto = null;
			
			if(name == null || name.split(",").length > 1){
				return contactPhoto;
			}
			number = number.replace("+86", "");
			ContentResolver cr = mContext.getContentResolver();
			Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,"DISPLAY_NAME = '" + name.replace("'", "''") + "'", null, ContactsContract.Contacts._ID + " DESC");
			if(cursor.getCount() == 0){
				cursor.close();
				return contactPhoto;
			}
			while (cursor.moveToNext()) {
				String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
				Cursor cu = mContext.getContentResolver().query(  
		                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,  
		                new String[]{ContactsContract.CommonDataKinds.Phone._ID},   
		                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = '" + contactId + "' AND " +
		                ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + number + "'",  
		                null, null); 
				if(cu!=null && cu.getCount() > 0) {
					Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
					InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
					contactPhoto = BitmapFactory.decodeStream(input);
					cu.close();
					cursor.close();
					return contactPhoto;
				}
				cu.close();
			}
			cursor.close();
		    return contactPhoto;
		}catch(Exception ex){
			HLog.i("内存溢出----------------------->>>>");
			return null;
		}
    }
	
}
