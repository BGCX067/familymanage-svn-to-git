package com.lianluo.core.skin;


import android.view.View;

public interface ISkinManage
{
	public void setBackground(View v, int id);
	public void setTextColor(View v, int id);
	public void setImage(View v, int id);
	public String getSkin();
	//public Drawable getDrawable(Context mContext, int resId);
}