package com.exuan.ecalendar;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

public class EAboutActivity extends Activity
{
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		TextView authorTextView = (TextView)findViewById(R.id.textview_author);
		authorTextView.setTextColor(ECalendarActivity.mHolidayColor);
		TextView versionTextView = (TextView) findViewById(R.id.textview_version);
		versionTextView.setTextColor(ECalendarActivity.mHolidayColor);
		String version = "1.3";
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			version = packInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		versionTextView.setText(getString(R.string.version) + version);
	}
}