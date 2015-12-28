package com.exuan.texsttextview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class TestTextView extends TextView
{
	private Context mContext;
	public static final String LOGTAG = TestTextView.class.getSimpleName();
	public TestTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mContext = context;
		setDrawingCacheEnabled(true);
	}

	public TestTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		setDrawingCacheEnabled(true);
	}
	
	public TestTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		setDrawingCacheEnabled(true);
	}
	
	protected void onDraw(Canvas paramCanvas)
	{
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		Rect rect = paramCanvas.getClipBounds();
		RectF rt = new RectF(rect);
		RectF rt1 = new RectF();
		//paramCanvas.dr
		paramCanvas.drawRoundRect(rt, 15.2f, 15.2f, paint);
		//paramCanvas.drawRect(rect, paint);
		paint.setTextSize(20);
		paint.setColor(Color.BLACK);
		paramCanvas.drawText("111", 10, 20, paint);
		paramCanvas.drawText(mContext.getString(R.string.text), 10, 40, paint);
		
	}
}