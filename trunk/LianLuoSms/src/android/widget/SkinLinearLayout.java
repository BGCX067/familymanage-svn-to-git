package android.widget;



import com.haolianluo.sms2.SkinActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;


public class SkinLinearLayout extends LinearLayout {
	
	public SkinLinearLayout(Context context) {
		super(context);
	}

	public SkinLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		int backgroundId = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "background", 0);
		SkinActivity.mSkinManage.setBackground(this, backgroundId);
	}
	
}
