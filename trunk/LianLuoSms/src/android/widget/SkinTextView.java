package android.widget;



import android.content.Context;
import android.util.AttributeSet;

import com.haolianluo.sms2.SkinActivity;


public class SkinTextView extends TextView {
	
	private Context mContext;
	
	public SkinTextView(Context context) {
		super(context);
		this.mContext = context;
	}

	public SkinTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		/**背景ID*/
		int backgroundId = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "background", 0);
		int textColorId = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "textColor", 0);
		
		SkinActivity.mSkinManage.setBackground(this, backgroundId);
		SkinActivity.mSkinManage.setTextColor(this, textColorId);
	}
}
