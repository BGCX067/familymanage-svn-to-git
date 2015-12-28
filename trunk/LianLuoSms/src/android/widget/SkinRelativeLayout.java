package android.widget;

import com.haolianluo.sms2.SkinActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;


public class SkinRelativeLayout extends RelativeLayout {
	
	public SkinRelativeLayout(Context context) {
		super(context);
	}

	public SkinRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		int backgroundId = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "background", 0);
		SkinActivity.mSkinManage.setBackground(this, backgroundId);
	}

}
