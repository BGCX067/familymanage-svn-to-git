package com.exuan.ecallrecords;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

public class EAboutActivity extends Activity
{
	TextView mVersionTextView;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		mVersionTextView = (TextView) findViewById(R.id.textview_version);
		String version = "1.2";
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			version = packInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mVersionTextView.setText(getString(R.string.version) + version);
	}
}