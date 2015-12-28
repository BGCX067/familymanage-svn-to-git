package android.widget;



import android.content.Context;
import android.util.AttributeSet;

import com.haolianluo.sms2.ui.sms2.SkinActivity;


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
		
		SkinActivity.skin_map.put(this.getId() + "_bg", backgroundId);
		SkinActivity.skin_map.put(this.getId() + "_tc", textColorId);
		
		SkinActivity.mSkinManage.setBackground(this, backgroundId);
		SkinActivity.mSkinManage.setTextColor(this, textColorId);
	}
	
	public void changeSkin() {
		int backgroundId = SkinActivity.skin_map.get(this.getId() + "_bg");
		int textColorId = SkinActivity.skin_map.get(this.getId() + "_tc");
		SkinActivity.mSkinManage.setBackground(this, backgroundId);
		SkinActivity.mSkinManage.setTextColor(this, textColorId);
	}
}
