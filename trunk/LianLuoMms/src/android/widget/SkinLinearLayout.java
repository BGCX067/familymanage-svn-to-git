package android.widget;



import com.haolianluo.sms2.ui.sms2.SkinActivity;

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
		SkinActivity.skin_map.put(this.getId() + "_bg", backgroundId);
		SkinActivity.mSkinManage.setBackground(this, backgroundId);
	}
	
	public void changeSkin() {
		int backgroundId = SkinActivity.skin_map.get(this.getId() + "_bg");
		SkinActivity.mSkinManage.setBackground(this, backgroundId);
	}
	
}
