package com.GetPosition;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DataBaseHelper extends SQLiteOpenHelper {
	public static final String Table_User = "User";
	public static final String Table_Wlan ="WLAN";
	public static final String Table_BlueTooth = "BlutTooth";
	
	public static final String User_ID = "ID";
	public static final String User_pwd = "pwd";
	public static final String User_Grp = "user_group";
	public static final String Company_Info = "Company";
	
	public static final String Wlan_Name = "Wlan_name";
	public static final String Wlan_Mac = "Wlan_Mac";
	public static final String Wlan_Rssi = "Wlan_Rssi";
	
	public static final String BlueTooth_Name = "BlueTooth_Name";
	public static final String BlueTooth_Mac ="BlueTooth_Mac";
	public static final String BlueTooth_Rssi= "BlueTooth_Rssi";

	public DataBaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		db.execSQL("CREATE TABLE IF NOT EXISTS " + Table_Wlan +
				 "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				 Wlan_Name + " VARCHAR, " +
				 Wlan_Mac + " VARCHAR NOT NULL, " +
				 Wlan_Rssi + " VARCHAR)"
			 );		
		
		db.execSQL("CREATE TABLE IF NOT EXISTS " + Table_BlueTooth +
				 "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				 BlueTooth_Name + " VARCHAR, " +
				 BlueTooth_Mac + " VARCHAR NOT NULL, " +
				 BlueTooth_Rssi + " VARCHAR)"
			 );	
		db.execSQL("CREATE TABLE IF NOT EXISTS " + Table_User +
				 "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				 User_ID + " VARCHAR NOT NULL, " +
				 User_pwd + " VARCHAR NOT NULL)"
			 );			
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + Table_User);
		db.execSQL("DROP TABLE IF EXISTS " + Table_Wlan);
		db.execSQL("DROP TABLE IF EXISTS " + Table_BlueTooth);
		onCreate(db);		
	}

}
