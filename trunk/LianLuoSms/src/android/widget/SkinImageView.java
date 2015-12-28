package android.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.haolianluo.sms2.SkinActivity;

public class SkinImageView extends ImageView {

	private Context mContext;
	
	public SkinImageView(Context context) {
		super(context);
		this.mContext = context;
	}

	public SkinImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		int backgroundId = attrs.getAttributeResourceValue(
				"http://schemas.android.com/apk/res/android", "background", 0);
		SkinActivity.mSkinManage.setBackground(this, backgroundId);
	}
}
