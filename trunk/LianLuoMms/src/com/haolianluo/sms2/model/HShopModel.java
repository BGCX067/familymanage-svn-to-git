package com.haolianluo.sms2.model;

import java.io.Serializable;

public class HShopModel implements Serializable {

	private static final long serialVersionUID = -4033458626094802227L;
	public static final int DOWNLOAD_STATE_NEW = 0;
	public static final int DOWNLOAD_STATE_STARTING = 1;
	public static final int DOWNLOAD_STATE_FINISHED = 2;

	private String t;// 时间戳
	private String p;// 资源地址

	private long downloadId;// 下载器ID
	private int downloadState;// 下载状态

	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}

	public String getP() {
		return p;
	}

	public void setP(String p) {
		this.p = p;
	}

	public long getDownloadId() {
		return downloadId;
	}

	public void setDownloadId(long downloadId) {
		this.downloadId = downloadId;
	}

	public int getDownloadState() {
		return downloadState;
	}

	public void setDownloadState(int downloadState) {
		this.downloadState = downloadState;
	}

}
