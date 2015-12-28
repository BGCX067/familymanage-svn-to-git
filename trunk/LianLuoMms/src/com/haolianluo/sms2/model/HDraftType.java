package com.haolianluo.sms2.model;

	public class HDraftType {
		
		String date;
		String body;
		String id;
		String address;
		String threadid;

	public HDraftType(String _id,String _date,String _body,String _address,String _threadid){
		id = _id;
		date = _date;
		body = _body;
		address = _address;
		threadid =_threadid;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getThreadid() {
		return threadid;
	}

	public void setThreadid(String threadid) {
		this.threadid = threadid;
	}
	
	

}
