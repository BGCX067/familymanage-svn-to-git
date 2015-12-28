/*
 * Data Item class for download listview item
 */

package com.lianluo.core.net.download;

import java.io.Serializable;

public class DLData implements Serializable
{
	private static final long serialVersionUID = 1753841602925235547L;
	
	/** total size */
	private int mTotalSize = 0;
	
	/** current size*/
	private int mCurrentSize = 0;

	/** download status */
	private int mTaskStatus = DLManager.STATUS_READY;

	/** store name */
	private String mFileName = "";
	
	/** icon url */
	private String mIconUrl = "";

	/** task ID */
	private long mTaskID = 0l;
	
	/** res ID */
	private String mResID = "";

	/** task display name */
	private String mDisplayName = "";

	/** package name if it is an application */
	private String mPackagename = "";

	/** resource charge */
	private String mCharge;
	
	/** resource charge message*/
	private String mChargeMsg;
	
	public void setIconUrl(String url) {
		mIconUrl = url;
	}

	public String getIconUrl() {
		return mIconUrl;
	}

	public void setFileName(String name) {
		mFileName = name;
	}

	public String getFileName() {
		return mFileName;
	}
	
	public void setId(Long id) {
		mTaskID = id;
	}

	public Long getId() {
		return mTaskID;
	}

	public void setStatus(int status) {
		mTaskStatus = status;
	}

	public int getStatus() {
		return mTaskStatus;
	}

	public void setPackagename(String packagename) {
		mPackagename = packagename;
	}

	public String getPackagename() {
		return mPackagename;
	}

	public void setDisplayName(String name) {
		mDisplayName = name;
	}

	public String getDisplayName() {
		return mDisplayName;
	}

	public void setCurrentSize(int size) {
		mCurrentSize = size;
	}

	public int getCurrentSize() {
		return mCurrentSize;
	}

	public void setTotalSize(int size) {
		mTotalSize = size;
	}

	public int getTotalSize() {
		return mTotalSize;
	}
	
	public void setCharge(String charge) {
		mCharge = charge;
	}

	public String getCharge() {
		return mCharge;
	}
	
	public void setResID(String id) {
		mResID = id;
	}

	public String getResID() {
		return mResID;
	}

	public String getChargeMsg() {
		return mChargeMsg;
	}

	public void setChargeMsg(String chargeMsg) {
		mChargeMsg = chargeMsg;
	}
}