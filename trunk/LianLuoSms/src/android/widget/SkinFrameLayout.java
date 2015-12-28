package android.widget;



import android.content.Context;
import android.util.AttributeSet;

import com.haolianluo.sms2.SkinActivity;


public class SkinFrameLayout extends FrameLayout {
	
	public SkinFrameLayout(Context context) {
		super(context);
	}

	public SkinFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		int backgroundId = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "background", 0);
		SkinActivity.mSkinManage.setBackground(this, backgroundId);
	}
	
}
