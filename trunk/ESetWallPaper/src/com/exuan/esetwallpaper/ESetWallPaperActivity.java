package com.exuan.esetwallpaper;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

public class ESetWallPaperActivity extends Activity {
	private static final int REQUEST_CODE = 3000;
	private Context mContext;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        Intent intent = new Intent(Intent.ACTION_PICK, null);  
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");  
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_CODE); 
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(Activity.RESULT_OK == resultCode)
		{
			if(REQUEST_CODE == requestCode)
			{
				try
				{
					Uri uri = data.getData();
					Bitmap photo = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
					WallpaperManager wm = WallpaperManager.getInstance(mContext);
					wm.setBitmap(photo);
					Toast.makeText(mContext, getString(R.string.success), Toast.LENGTH_LONG).show();
				}
				catch(Exception e)
				{
					e.printStackTrace();
					Toast.makeText(mContext, getString(R.string.fail), Toast.LENGTH_LONG).show();
				}
			}
		}
		finish();
	}
}