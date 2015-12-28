package com.lianluo.core.stats;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.lianluo.core.util.HLog;

import android.util.Log;

public class StatsParser extends DefaultHandler
{
	private int mResult = Statistics.Failed;
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
	}
	
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
	}
	
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if(localName.equalsIgnoreCase("re"))
		{HLog.e("StatsParser", "re:" + attributes.getValue("b"));
			if(attributes.getValue("b").equalsIgnoreCase("RecodeOK"))
			{
				mResult = Statistics.RecodeOK;
			}
			else if(attributes.getValue("b").equalsIgnoreCase("RecodeFailture"))
			{
				mResult = Statistics.RecodeFailture;
			}
			else if(attributes.getValue("b").equalsIgnoreCase("NOThisRequest"))
			{
				mResult = Statistics.NOThisRequest;
			}
			else if(attributes.getValue("b").equalsIgnoreCase("Paid"))
			{
				mResult = Statistics.Paid;
			}
			else if(attributes.getValue("b").equalsIgnoreCase("NoPaid"))
			{
				mResult = Statistics.NoPaid;
			}
			else if(attributes.getValue("b").equalsIgnoreCase("Free"))
			{
				mResult = Statistics.Free;
			}
		}
	}
	
	public int getStatsResult()
	{
		return mResult;
	}
}