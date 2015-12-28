package android.widget;



import com.haolianluo.sms2.SkinActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class SkinListView extends ListView {
	
	public SkinListView(Context context) {
		super(context);
	}

	public SkinListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		int backgroundID = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "background", 0);
		SkinActivity.mSkinManage.setBackground(this, backgroundID);
	}

}
