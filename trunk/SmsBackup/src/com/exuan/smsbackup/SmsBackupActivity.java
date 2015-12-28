package com.exuan.smsbackup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SmsBackupActivity extends Activity {
	private ProgressDialog mProgressDialog;
	private static final String[] COLUMN = {"address", "read", "status", "type", "service_center", "body", "date"};
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait...");
        Button bt_backup = (Button)findViewById(R.id.button_backup);
        Button bt_restore = (Button)findViewById(R.id.button_restore);
        bt_backup.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mProgressDialog.isShowing())
				{
					mProgressDialog.dismiss();
				}
				try {
					mProgressDialog.show();
					Cursor cursor = getContentResolver().query(Uri.parse("content://sms"), COLUMN, null, null, null);
					XmlSerializer serializer = Xml.newSerializer();
					StringWriter writer = new StringWriter();
					serializer.setOutput(writer);
					serializer.startDocument("UTF-8", true);
					serializer.startTag("", "smss");
					if(null != cursor && 0 < cursor.getCount())
					{
						while(cursor.moveToNext())
						{
							serializer.startTag("", "sms");
							for(int i = 0; i < cursor.getColumnCount(); i++)
							{
								Log.e("jayce", "va:" + cursor.getString(i));
								serializer.attribute("", COLUMN[i], cursor.getString(i) == null ? "" : cursor.getString(i));
							}
							serializer.endTag("", "sms");
						}
					}
					serializer.endTag("", "smss");
					serializer.endDocument();
					Log.e("jayce", "ser:" + serializer);
					Log.e("jayce", "wri:" + writer.toString());
					File f = new File(Environment.getExternalStorageDirectory() + File.separator + "smsback.xml");
					if(f.exists())
					{
						f.delete();
						f.createNewFile();
					}
					FileOutputStream out = new FileOutputStream(f);
					out.write(writer.toString().getBytes());
					cursor.close();
					out.close();
					if(mProgressDialog.isShowing())
					{
						mProgressDialog.dismiss();
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					if(mProgressDialog.isShowing())
					{
						mProgressDialog.dismiss();
					}
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					if(mProgressDialog.isShowing())
					{
						mProgressDialog.dismiss();
					}
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					if(mProgressDialog.isShowing())
					{
						mProgressDialog.dismiss();
					}
					e.printStackTrace();
				}
			}});
        
        bt_restore.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				XmlPullParser parser = Xml.newPullParser();
		         
		         try {
		        	 FileInputStream in = new FileInputStream(Environment.getExternalStorageDirectory() + File.separator + "smsback.xml");
		             parser.setInput(in, "UTF-8");
		             int eventType = parser.getEventType();
		             while(eventType != XmlPullParser.END_DOCUMENT)
		             {
		                 switch(eventType)
		                 {
		                     case XmlPullParser.START_TAG:
		                     {
		                         String tag = parser.getName();
		                         if(tag.equalsIgnoreCase("sms"))
		                         {
		                        	 ContentValues values = new ContentValues();
		                        	 for(int i = 0; i < COLUMN.length; i++)
		                         	 {
		                         		 Log.e("jayce", "va:" + parser.getAttributeValue("", COLUMN[i]));
		                         		 values.put(COLUMN[i], parser.getAttributeValue("", COLUMN[i]));
		                         	 }
		                         	 Cursor c = getContentResolver().query(Uri.parse("content://sms"), null, 
		                         			COLUMN[0] + " = '" + values.get(COLUMN[0]) + "'" + " AND " +
		                         			COLUMN[5] + " = '" + values.get(COLUMN[5]) + "'" + " AND " +
		                         			COLUMN[6] + " = '" + values.get(COLUMN[6]) + "'"
		                         			, null, null);
		                         	 if(null == c || 0 >= c.getCount())
		                         	 {
		                         		 getContentResolver().insert(Uri.parse("content://sms"), values);
		                         	 }
		                         }
		                     }
		                     break;
		                 }
		                 eventType = parser.next();
		             }
		             in.close();
		         } catch (XmlPullParserException e) {
		             // TODO Auto-generated catch block
		             e.printStackTrace();
		         } catch (IOException e) {
		             // TODO Auto-generated catch block
		             e.printStackTrace();
		         }
			}});
    }
    
}