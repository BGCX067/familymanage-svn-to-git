package android.widget;

import com.haolianluo.sms2.SkinActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class SkinGridView extends GridView{

	public SkinGridView(Context context) {
		super(context);
	}
	
	public SkinGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		int backgroundId = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "background", 0);
		SkinActivity.mSkinManage.setBackground(this, backgroundId);
	}
   
}
