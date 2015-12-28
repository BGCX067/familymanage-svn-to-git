package com.ia;

import java.io.UnsupportedEncodingException;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

public class HistoryRecord {

	private String filePath;
	private int filePosition;
	
	private byte[] path = new byte[256];
	public static RecordStore openRecord()
	{
		try {
			return RecordStore.openRecordStore("BOOK_MARK", true);
		} catch (RecordStoreFullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void closeRecord(RecordStore record)
	{
		try {
			record.closeRecordStore();
		} catch (RecordStoreNotOpenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int getLastReadIndex(RecordStore record)
	{
		byte[] b = new byte[4];
		try {
			System.out.println(record.getRecord(1, b, 0));
			System.out.println("byteToInt(b):" + byteToInt(b));
			return byteToInt(b);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public static void setLastReadIndex(RecordStore record, int index)
	{System.out.println("index:" + index);
		byte[] b = intToByte(index);System.out.println("byteToInt(b)" + byteToInt(b));
		try {
			if(record.getNumRecords() > 0)
			{System.out.println("nums>:" + record.getNumRecords());
				record.setRecord(1, b, 0, 4);
			}
			else
			{System.out.println("nums<=:" + record.getNumRecords());
				System.out.println(record.addRecord(b, 0, 4));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int addRecord(RecordStore record, String path, int position)
	{
		byte[] pos = intToByte(position);
		byte[] pat = stringToByte(path);
		int l = pos.length + pat.length;
		byte[] data = new byte[l];
		for(int i = 0; i < l; i++)
		{
			if(i < pos.length)
			{
				data[i] = pos[i];
			}
			else
			{
				data[i] = pat[i - pos.length];
			}
		}
		try {
			return record.addRecord(data, 0, l);
		} catch (RecordStoreNotOpenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreFullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public static void setPosition(RecordStore record, int recordId, byte[] newData)
	{
		try {
			record.setRecord(recordId, newData, 0, 4);
		} catch (RecordStoreNotOpenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidRecordIDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreFullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int getPosition(RecordStore record, int recordId)
	{
		try {
			byte[] buffer = record.getRecord(recordId);
			byte[] b = new byte[4];
			for(int i = 0; i < 4; i++)
			{
				b[i] = buffer[i];
			}
			return byteToInt(b);
		} catch (RecordStoreNotOpenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidRecordIDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public static String getPath(RecordStore record, int recordId)
	{
		try {
			byte[] buffer = record.getRecord(recordId);
			int l = buffer.length - 4;
			byte[] b = new byte[l];
			for(int i = 0; i < l; i++)
			{
				b[i] = buffer[i + 4];
			}
			return byteToString(b);
		} catch (RecordStoreNotOpenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidRecordIDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] intToByte(int t)
	{
		byte[] b = new byte[4];
		for(int i = 0; i < 4; i++)
		{
			b[i] = (byte)t;
			t = t >> 8;
		}
		return b;
	}
	
	public static int byteToInt(byte[] b)
	{
		int t = 0;
		for(int i = 3; i >= 0; i--)
        {
                t = t << 8;
                t = b[i] < 0 ? t + b[i] + 256 : t + b[i];
        } 
		return t;
	}
	
	public static String byteToString(byte[] b)
	{
		try {
			return new String(b, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static byte[] stringToByte(String str)
	{
		try {
			return str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static void deleteRecordStore()
	{
		try {
			RecordStore.deleteRecordStore("BOOK_MARK");
		} catch (RecordStoreNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
