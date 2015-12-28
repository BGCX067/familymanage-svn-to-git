package com.exuan.ephoneattribution;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EPhoneAttributionActivity extends Activity {
	
	private static final int connectTimeout = 5000;
	private static final int readTimeout = 5000;
	private Context mContext;
	TextView mNumberTextView;
	TextView mProvinceTextView;
	TextView mCityTextView;
	TextView mAreaTextView;
	TextView mZipTextView;
	TextView mCardTextView;
	Button mSearchButton;
	ImageView mClearImageView;
	LinearLayout mProgressLinearLayout;
	EditText mNumberEditText;
	private static final int SEARCH_SUCCESS = 0;
	private static final int SEARCH_FAIL = 1;
	private static final int FORMAT_ERROR = 2;
	String[] mInfo;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = this;
        mNumberTextView = (TextView)findViewById(R.id.textview_number);
        mProvinceTextView = (TextView)findViewById(R.id.textview_province);
        mCityTextView = (TextView)findViewById(R.id.textview_city);
        mAreaTextView = (TextView)findViewById(R.id.textview_area_code);
        mZipTextView = (TextView)findViewById(R.id.textview_zip_code);
        mCardTextView = (TextView)findViewById(R.id.textview_card);
        mSearchButton = (Button)findViewById(R.id.button_search);
        mProgressLinearLayout = (LinearLayout)findViewById(R.id.linearlayout_progress);
        mNumberEditText = (EditText)findViewById(R.id.edittext_number);
        mClearImageView = (ImageView)findViewById(R.id.imageview_clear);
        
        mClearImageView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mNumberEditText.setText("");
			}});
        
        mSearchButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mNumberTextView.setText(getString(R.string.number));
		        mProvinceTextView.setText(getString(R.string.province));
		        mCityTextView.setText(getString(R.string.city));
		        mAreaTextView.setText(getString(R.string.area_code));
		        mZipTextView.setText(getString(R.string.zip_code));
		        mCardTextView.setText(getString(R.string.card));
				if(mNumberEditText.getText().toString().length() > 0)
				{
					mSearchButton.setVisibility(View.GONE);
					mProgressLinearLayout.setVisibility(View.VISIBLE);
					
					new Thread(){
						public void run()
						{
							String net = String.format("http://api.showji.com/Locating/default.aspx?m=%s&output=json&callback=querycallback", mNumberEditText.getText().toString());
							try {
								URL url = new URL(net);
								HttpURLConnection ucon = (HttpURLConnection)(url.openConnection());
								ucon.setConnectTimeout(connectTimeout);
								ucon.setReadTimeout(readTimeout);
								InputStream xmlStream = ucon.getInputStream();
								byte[] by = new byte[600];
								xmlStream.read(by);
								String s = new String(by, "utf-8");
								
								s = s.substring(s.indexOf('"'));
								s = s.substring(0, s.lastIndexOf('"') + 1);
								mInfo = s.split(",");
								for(int i = 0; i < mInfo.length; i++)
								{
									mInfo[i] = mInfo[i].substring(0, mInfo[i].lastIndexOf('"'));
									mInfo[i] = mInfo[i].substring(mInfo[i].lastIndexOf('"') + 1);
								}
								if(mInfo[1].equals("True"))
								{
							        Message msg = new Message();
									msg.what = SEARCH_SUCCESS;
									mHandler.sendMessage(msg);
								}
								else
								{
									Message msg = new Message();
									msg.what = FORMAT_ERROR;
									mHandler.sendMessage(msg);
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								Message msg = new Message();
								msg.what = SEARCH_FAIL;
								mHandler.sendMessage(msg);
							}
						}
					}.start();
				}
				else
				{
					Toast.makeText(mContext, getString(R.string.input_number), Toast.LENGTH_LONG).show();
				}
			}});
    }
    
    Handler mHandler = new Handler()
    {
    	public void handleMessage(Message msg)
    	{
    		switch(msg.what)
    		{
	    		case SEARCH_SUCCESS:
	    		{
	    			mSearchButton.setVisibility(View.VISIBLE);
					mProgressLinearLayout.setVisibility(View.GONE);
					mNumberTextView.setText(getString(R.string.number) + " " + mInfo[0]);
			        mProvinceTextView.setText(getString(R.string.province) + " " + mInfo[2]);
			        mCityTextView.setText(getString(R.string.city) + " " + mInfo[3]);
			        mAreaTextView.setText(getString(R.string.area_code) + " " + mInfo[4]);
			        mZipTextView.setText(getString(R.string.zip_code) + " " + mInfo[5]);
			        mCardTextView.setText(getString(R.string.card) + " " + mInfo[6] + mInfo[7]);
	    		}
	    		break;
	    		case SEARCH_FAIL:
	    		{
	    			mSearchButton.setVisibility(View.VISIBLE);
					mProgressLinearLayout.setVisibility(View.GONE);
					Toast.makeText(mContext, getString(R.string.fail), Toast.LENGTH_LONG).show();
	    		}
	    		break;
	    		case FORMAT_ERROR:
	    		{
	    			mSearchButton.setVisibility(View.VISIBLE);
					mProgressLinearLayout.setVisibility(View.GONE);
					Toast.makeText(mContext, getString(R.string.wrong_number), Toast.LENGTH_LONG).show();
	    		}
	    		break;
    		}
    	}
    };
}