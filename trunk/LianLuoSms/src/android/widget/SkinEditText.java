package android.widget;

import com.haolianluo.sms2.SkinActivity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class SkinEditText extends EditText {

	private Context mContext;
	
	public SkinEditText(Context context) {
		super(context);
		this.mContext = context;
	}

	public SkinEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		int backgroundId = attrs.getAttributeResourceValue(
				"http://schemas.android.com/apk/res/android", "background", 0);
		int textColorId = attrs.getAttributeResourceValue(
				"http://schemas.android.com/apk/res/android", "textColor", 0);
		SkinActivity.mSkinManage.setBackground(this, backgroundId);
		SkinActivity.mSkinManage.setTextColor(this, textColorId);
	}
}
