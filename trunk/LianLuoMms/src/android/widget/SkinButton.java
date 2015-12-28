package android.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.haolianluo.sms2.ui.sms2.SkinActivity;

public class SkinButton extends Button {

	private Context mContext;

	public SkinButton(Context context) {
		super(context);
		this.mContext = context;
	}

	public SkinButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		int backgroundId = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "background", 0);
		int textColorId = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "textColor", 0);
		
		SkinActivity.skin_map.put(this.getId() + "_bg", backgroundId);
		SkinActivity.skin_map.put(this.getId() + "_tc", textColorId);
		
		SkinActivity.mSkinManage.setBackground(this, backgroundId);
		SkinActivity.mSkinManage.setTextColor(this, textColorId);
	}
	
	@Override
	public void setBackgroundResource(int resid) {
		SkinActivity.skin_map.put(this.getId() + "_bg", resid);
		SkinActivity.mSkinManage.setBackground(this, resid);
	}
	
	public void changeSkin() {
		int backgroundId = SkinActivity.skin_map.get(this.getId() + "_bg");
		int textColorId = SkinActivity.skin_map.get(this.getId() + "_tc");
		SkinActivity.mSkinManage.setBackground(this, backgroundId);
		SkinActivity.mSkinManage.setTextColor(this, textColorId);
	}
}
