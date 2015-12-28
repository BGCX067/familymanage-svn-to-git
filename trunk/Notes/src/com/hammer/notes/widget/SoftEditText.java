package com.hammer.notes.widget;

import com.hammer.notes.NotesActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;

public class SoftEditText extends EditText
{
	private NotesActivity mActivity;
	public SoftEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mActivity = (NotesActivity)context;
	}

	public SoftEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mActivity = (NotesActivity)context;
	}

	public SoftEditText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mActivity = (NotesActivity)context;
	}
	
	public boolean dispatchKeyEventPreIme(KeyEvent event) 
	{
		if(KeyEvent.KEYCODE_BACK == event.getKeyCode())
		{
			if(null != mActivity && mActivity.mIsInputMethodShow)
			{
				mActivity.mIsInputMethodShow = false;
			}
		}
		return super.dispatchKeyEventPreIme(event);
	}

	
}