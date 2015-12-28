/**
 * 
 */
package com.decarta.calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * @author Tony Shen
 *
 */
public class Main extends Activity {
	private final int CKECK_IN = 0;
	private final int CKECK_OUT = 1;
	
	private Button checkInButton;
	private Button checkOutButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		checkInButton = (Button) findViewById(R.id.checkInBtn);
		checkInButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(Main.this,DateWidget.class);
				Bundle bundle = new Bundle();
				bundle.putInt("type",CKECK_IN);
				i.putExtras(bundle);
				startActivity(i);
			}
		});
		checkInButton.setText(mPerferences.getString("check_in", "mm-dd-yy"));
		
		checkOutButton = (Button) findViewById(R.id.checkOutBtn);
		checkOutButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(Main.this,DateWidget.class);
				Bundle bundle = new Bundle();
				bundle.putInt("type",CKECK_OUT);
				i.putExtras(bundle);
				startActivity(i);
			}
		});
		checkOutButton.setText(mPerferences.getString("check_out", "mm-dd-yy"));
	}
}
