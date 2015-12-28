package android.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.haolianluo.sms2.ui.sms2.SkinActivity;

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
		SkinActivity.skin_map.put(this.getId() + "_bg", backgroundId);
		SkinActivity.mSkinManage.setBackground(this, backgroundId);
	}
	
	public void changeSkin() {
		int backgroundId = SkinActivity.skin_map.get(this.getId() + "_bg");
		SkinActivity.mSkinManage.setBackground(this, backgroundId);
	}
}
