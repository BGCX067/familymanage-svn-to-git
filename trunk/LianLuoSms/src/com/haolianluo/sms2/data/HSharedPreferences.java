package com.haolianluo.sms2.data;

import android.content.Context;
import android.content.SharedPreferences;

/***
 * 
 * 将数据写入缓存文件
 * 
 * 该类后期需要把多余的代码去掉。
 * 
 * @author jianhua 2011年9月2日9:21:25
 * 
 */

public class HSharedPreferences {

	private SharedPreferences mSharedPreferences = null;

	/**
	 * 写入的文件是默认
	 * @param context
	 */
	public HSharedPreferences(Context context) {
		String default_fileName = "haolianluo_sms";// 共享数据文件名
		mSharedPreferences = context.getSharedPreferences(default_fileName, 0);
	}
	
	/**
	 * 写入的文件是bufferFileName
	 * @param context
	 * @param bufferFileName
	 */
	public HSharedPreferences(Context context,String bufferFileName){
		mSharedPreferences = context.getSharedPreferences(bufferFileName, 0);
	}

	/** boolean值类型，默认值是false */
	private void writeBoolean(String key, boolean value) {
		SharedPreferences.Editor ed = mSharedPreferences.edit();
		ed.putBoolean(key, value);
		ed.commit();
	}

	private boolean readBoolean(String key) {
		boolean b = mSharedPreferences.getBoolean(key, false);
		return b;
	}
	
	private void writeString(String key, String value) {
		SharedPreferences.Editor ed = mSharedPreferences.edit();
		ed.putString(key, value);
		ed.commit();
	}
	
	private String readString(String key) {
		String b = mSharedPreferences.getString(key, "0.0");
		return b;
	}
	
	
	private boolean readBooleanTrue(String key) {
		boolean b = mSharedPreferences.getBoolean(key, true);
		return b;
	}
	
	private void writeInt(String key,int value){
		SharedPreferences.Editor ed = mSharedPreferences.edit();
		ed.putInt(key, value);
		ed.commit();
	}
	
	private int readInt(String key){
		return mSharedPreferences.getInt(key, 0);
	}
	
	public void saveWidth(int value){
		writeInt("width", value);
	}
	public void saveHeight(int value){
		writeInt("height", value);
	}
	public int readWidth(){
		return readInt("width");
	}
	public int readHeight(){
		return readInt("height");
	}
	
	/**
	 * 得到是不是HTC
	 */
	public boolean getIsHtc(){
		return readBoolean("ishtc");
	}
	
	/**
	 * 设置是不是HTC
	 * @param isHtc
	 */
	public void setIsHtc(boolean isHtc){
		writeBoolean("ishtc",isHtc);
	}
	
	/***
	 * 能否读取缓存
	 */
	public boolean getIsReadBuffer(){
		return readBoolean("is_read_bufferlist");
	}
	
	/**
	 * 设置能否读取缓存  true可以   false不可以
	 * @param isReadBuffer
	 */
	public void setIsReadBuffer(boolean isReadBuffer){
		writeBoolean("is_read_bufferlist",isReadBuffer);
	}
	
	/***
	 * 消息栏提醒开关 true为开
	 * @param on
	 */
	public void setMessageSwitch(boolean on){
		writeBoolean("message",on);
	}

	public boolean getMessageSwitch(){
		return readBooleanTrue("message");
	}
	
	/***
	 * 动画短信提醒开关 true为开
	 * @param on
	 */
	public void setFlashSwitch(boolean on){
		writeBoolean("flash",on);
	}

	public boolean getFlashSwitch(){
		return readBooleanTrue("flash");
	}
	
	/***
	 * 短信震动提醒开关 true为开
	 * @param on
	 */
	public void setVibrationSwitch(boolean on){
		writeBoolean("vibration",on);
	}

	public boolean getVibrationSwitch(){
		return readBooleanTrue("vibration");
	}
	
	/***
	 * 快捷短信提醒开关 true为开
	 * @param on
	 */
	public void setShortcutSwitch(boolean on){
		writeBoolean("shortcut",on);
	}

	public boolean getShortcutSwitch(){
		return readBooleanTrue("shortcut");
	}
	
	public void setFirst(boolean is){
		writeBoolean("isFirst",is);
	}
	
	public  boolean getIsFirst(){
		return readBooleanTrue("isFirst");
	}
	
	public void setSkinScan(boolean is){
		writeBoolean("isSkin",is);
	}
	
	public  boolean getSkinScan(){
		return readBoolean("isSkin");
	}
	
	public void setHighDen(boolean is){
		writeBoolean("isHighDen",is);
	}
	
	public  boolean getIsHighDen(){
		return readBooleanTrue("isHighDen");
	}
	
	public void setPreVersion(String preVersion){
		writeString("preVersion",preVersion);
	}
	
	public String getPreVersion(){
		return readString("preVersion");
	}
	
	public void setReadSmsType(int readSmsType){
		writeInt("readSmsType", readSmsType);
	}
	
	public int getReadSmsType(){
		return readInt("readSmsType");
	}

}
