package com.GetPosition;

import android.util.Log;

import com.unboundid.ldap.sdk.*;
import com.unboundid.*;

public class Tools {

	private static final String LOGTAG = "Tools";
	public static SearchResult getDevices()
	{
		Filter filter = null;
		LDAPConnection c = null;
		SearchResult searchResult = null;
		try {
			c = new LDAPConnection("neuron.iasolution.net", 389);
			BindResult bindResult = c.bind("CN=Jack.Liu,OU=R&D Task Force 7,OU=iaSolution,DC=iaSolution,DC=net",
					"laxMissth22");	
		} catch (LDAPException e) {
			// TODO Auto-generated catch block
			Log.e(LOGTAG, "bind error");
			e.printStackTrace();
		}

		try {
			filter = Filter.create("(objectClass=computer)");
		} catch (LDAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(LOGTAG, "Filter.create error");
		}
		try 
		{
		SearchRequest searchRequest = new SearchRequest(
				"OU=WLAN,OU=iaSolution,DC=iaSolution,DC=net",
				SearchScope.SUB, filter, "CN", "targetAddress");
		
			searchResult = c.search(searchRequest);
		} catch (LDAPSearchException lse) {
			Log.e(LOGTAG, "LDAPSearchException error");
		} 
		return searchResult;
	}
	
	public static boolean loginRequest()
	{
		return false;
	}
}
