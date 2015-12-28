package com.haolianluo.sms2;

import android.os.Bundle;
import android.widget.TextView;

public class HSeeCollectSmsActivity extends SkinActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.see_collectsms);
	}
	
	
	@Override
	protected void onStart() {
		//setContentView(R.layout.see_collectsms);
		super.onStart();
		init();
	}
	
	
	private void init(){
		TextView textView = (TextView) findViewById(R.id.textView);
		textView.setText(getIntent().getStringExtra("body"));
	}
}
