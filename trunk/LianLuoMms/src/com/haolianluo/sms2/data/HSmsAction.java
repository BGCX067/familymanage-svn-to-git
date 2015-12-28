package com.haolianluo.sms2.data;

public class HSmsAction {
	/**
	 * 更新收到短信的Dialog上面的内容
	 */
	public static final String ACTION_UPDATA_RECEIVERSMS_DIALOG = "com.haolianluo.sms.updata.receiversms.dialog";
	/**
	 * 更新UI
	 */
	public static final String ACTION_UPDATA_UI = "com.haolianluo.sms.updata.ui";
	/**
	 * Talk更新UI
	 */
	public static final String ACTION_UPDATA_TALK_UI = "com.haolianluo.sms.updata.talk.ui";
	/**
	 * 下载完action
	 */
	public static final String ACTION_BROADCAST = "com.haolianluo.sms.HHandleStencilACT";
	/**
	 * 短信变化广播action
	 */
	public static final String ACTION_SMS_CHANGED = "com.haolianluo.sms.smschanged";
	/**
	 * 模板包前缀action
	 */
	public static final String ACTION_SMS_MOLDS = "com.haolianluo.smsmold";
	/***
	 * 一个Action 常量
	 */
	public static final String MY_ACTION = "com.haolianluo.sms.installok";
	/***
	 * 通知更新flash界面模板
	 */
	public static final String ACTION_FLASH_UPMODL = "com.haolianluo.sms.flash_upmodl";
	/***
	 * 通知后台下载列表更新
	 */
	public static final String ACTION_BACK_DOWNLOAD = "com.haolianluo.sms.back_download";
	
	/**
	 * 全部安装完模板包的广播
	 */
	public static final String MY_ACTION_INSTALL_ALL_OK = "com.haolianluo.sms.installok.all";
	/**
	 * 启动模板requestcode
	 */
	public static final int TO_MOLD_REQUEST_CODE = 101;
	/**
	 * 渠道号
	 */
	public static final String QU_DAO_HAO = "qdh";
	/***
	 * 模板缩略图的名字
	 */
	public static final String BREVIARY_IMAGE = "breviary";  
	/**
	 * intent 参数 模板id
	 */
	public static final String EXTRA_MID = "moldid";
	/**
	 * intent 参数 是否是默认模板
	 */
	public static final String EXTRA_DEFMOLD = "isDef";
	/**
	 * intent 值  针对EXTRA_DEFMOLD 是默认模板
	 */
	public static final boolean VALUE_ISDEFMOLD = true;
	/**
	 * intent 值  针对EXTRA_DEFMOLD 不是默认模板
	 */
	public static final boolean VALUE_NOTDEFMOLD = false;
	
	/**
	 * intent 参数 是否安装模板成功
	 */
	public static final String EXTRA_INSTALL_MOLD_STATE = "moldinstall";
	/**
	 * intent 值 针对EXTRA_INSTALL_MOLD_STATE 安装成功
	 */
	public static final boolean VALUE_INSTALL_MOLD_OK = true;
	/**
	 * intent 值 针对EXTRA_INSTALL_MOLD_STATE 安装失败
	 */
	public static final boolean VALUE_INSTALL_MOLD_LOSS = false;
}
