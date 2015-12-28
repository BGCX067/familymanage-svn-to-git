package com.GetPosition;
import android.util.Log;

import com.unboundid.ldap.sdk.*;
import com.unboundid.*;

public class ldap {
	private LDAPConnection ldapConnection = null;
	public static final int SUCCESS = 0;
	public static final int NOUSER = 1;
	public static final int PWDERROR = 2;
	public static final int LOGIN_ERROR = 3;
	private static final String serviceUrl = "neuron.iasolution.net";
	private static final int servicePort = 389;
	private static final String defaule_service_bindId = 
		"CN=Facebook JBlend,OU=Misc,OU=iaSolution,DC=iaSolution,DC=net";
	private static final String default_service_Pwd = "iafb3727";

	public static LDAPConnection ldapConnect(String host,int port){
		LDAPConnection myconn = null;
		try {
			myconn = new LDAPConnection(host, port);
		} catch (LDAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return myconn;
	}

	public static int ldapLogin(LDAPConnection myconn,String BindId,String BindPwd){
		if(myconn == null ){
			return LOGIN_ERROR;
		}
		
		try {
			BindResult bindResult = myconn.bind(BindId,BindPwd);
			Log.d("LDAPTest", "bindResult = " + bindResult);
		} catch (LDAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return LOGIN_ERROR;
		}		
		return SUCCESS;
	}
	public static String GetbaseDN(LDAPConnection myconn,String LoginId){

		String baseDN = null;                
		Filter mFilter = null;
		Filter mFilter1;
		Filter mFilter2;
		int iRes = SUCCESS;

		if(myconn == null){
			return null;
		}else{
			if(SUCCESS != ldapLogin(myconn,defaule_service_bindId,default_service_Pwd)){
				return null;
				
			}
		}

		try {
			mFilter1 = Filter.create("(objectClass=user)");
			mFilter2 = Filter.createEqualityFilter("sAMAccountName", LoginId); 
			mFilter = Filter.createANDFilter(mFilter1, mFilter2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		SearchRequest searchRequest = new SearchRequest(
				"OU=iaSolution,DC=iaSolution,DC=net",
				SearchScope.SUB, mFilter, "distinguishedName");
		try {
			SearchResult searchResult = myconn.search(searchRequest);
			for (SearchResultEntry entry : searchResult.getSearchEntries()) {
				for (Attribute att : entry.getAttributes()) {
					//baseDN = att.getValueAsDN();
					String name = att.getBaseName();
					baseDN = att.getValue();
				}
			}			
		}catch(Exception e){
			System.err.println("The search was failure.");
		}
		return baseDN;		
	}
	
	public static SearchResult getWifiInfo(LDAPConnection myconn,String baseDN,String UserPwd){
		SearchResult wifiResult = null;
		Filter mFilter = null;
		int iRes = SUCCESS;

		if(myconn == null){
			return null;
		}else{
			if(SUCCESS != ldapLogin(myconn,baseDN,UserPwd)){
				return null;
			}
		}
		try {
			mFilter = Filter.create("(objectClass=computer)");
		} catch (LDAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		SearchRequest searchRequest = new SearchRequest(
				"OU=WLAN,OU=iaSolution,DC=iaSolution,DC=net",
				SearchScope.SUB, mFilter, "CN", "targetAddress");
		try {
			wifiResult = myconn.search(searchRequest);

		} catch (LDAPSearchException lse) {
			System.err.println("The search failed.");
		}
		return wifiResult;
	}
	public static SearchResult getBlueToothInfo(LDAPConnection myconn,String baseDN,String UserPwd){
		SearchResult BtResult = null;
		Filter mFilter = null;
		int iRes = SUCCESS;

		if(myconn == null){
			return null;
		}else{
			if(SUCCESS != ldapLogin(myconn,baseDN,UserPwd)){
				return null;
			}
		}
		try {
			mFilter = Filter.create("(objectClass=user)");
		} catch (LDAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

    	SearchRequest searchRequest = new SearchRequest(
		        "OU=Conference Room,OU=iaSolution,DC=iaSolution,DC=net",
	      		 SearchScope.SUB, mFilter, "CN", "ipPhone");	
		try {
			BtResult = myconn.search(searchRequest);

		} catch (LDAPSearchException lse) {
			System.err.println("The search failed.");
		}
		return BtResult;
	}
	
}
