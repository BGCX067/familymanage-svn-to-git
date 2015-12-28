package com.hammer.notes.utils;
 
import java.util.Calendar;
import java.util.Formatter;

import com.hammer.notes.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
 
public class ToolsUtil
{
	public static int loadTexture(final Context context, Bitmap bitmap)
    {
		final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);
         
        if(textureHandle[0] != 0)
        {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
        }
        /* 
        if(textureHandle[0] == 0)
        {
            throw new RuntimeException("failed to load texture");
        }*/
         
        return textureHandle[0];
    }
	
	public static Bitmap takeScreenshot(View view) {
 		assert view.getWidth() > 0 && view.getHeight() > 0;
 		Bitmap.Config config = Bitmap.Config.ARGB_8888;
 		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), config);
 		Canvas canvas = new Canvas(bitmap);
 		view.draw(canvas);
 		return Bitmap.createBitmap(bitmap, 0, 0, view.getWidth(), /*view.getHeight()*/682);
 	}
	
	public static String buildHourMinute(long time, Context context, Formatter formatter, StringBuilder builder) {
		builder.setLength(0);
        String date = DateUtils.formatDateRange(
        		context,
                formatter,
                time,
                time,
                DateUtils.FORMAT_SHOW_TIME |
                DateUtils.FORMAT_24HOUR,
                null).toString();
        return date;
    }
	
	public static String buildMonthDate(long time, Context context, Formatter formatter, StringBuilder builder) {
		builder.setLength(0);
        String date = DateUtils.formatDateRange(
        		context,
        		formatter,
                time,
                time,
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR,
                null).toString();
        return date;
    }
	
	public static String buildFullDate(long time, Context context, Formatter formatter, StringBuilder builder) {
		builder.setLength(0);
        String date = DateUtils.formatDateRange(
        		context,
        		formatter,
                time,
                time,
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR,
                null).toString();
        return date;
    }
	
	public static String buildTime(long time, Context context, Formatter formatter, StringBuilder builder)
	{
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		Calendar now = Calendar.getInstance();
		StringBuilder date = new StringBuilder(ToolsUtil.buildHourMinute(time, context, formatter, builder));
		if(c.get(Calendar.YEAR) == now.get(Calendar.YEAR))
		{
			date.append(" ").append(ToolsUtil.buildMonthDate(time, context, formatter, builder));
		}
		else
		{
			date.append(" ").append(ToolsUtil.buildFullDate(time, context, formatter, builder));
		}
		return date.toString();
	}
	
	public static String buildAgoText(Context context, long time)
	{
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		Calendar now = Calendar.getInstance();
		int ta = 1;
		String unit = "";
		if(time > now.getTimeInMillis())
		{
			return "";
		}
		else if(c.get(Calendar.YEAR) < now.get(Calendar.YEAR))
		{
			ta = now.get(Calendar.YEAR) - c.get(Calendar.YEAR);
			if(1 == ta)
			{
				unit = context.getString(R.string.year);
			}
			else
			{
				unit = context.getString(R.string.years);
			}
		}
		else if(c.get(Calendar.MONTH) < now.get(Calendar.MONTH))
		{
			ta = now.get(Calendar.MONTH) - c.get(Calendar.MONTH);
			if(1 == ta)
			{
				unit = context.getString(R.string.month);
			}
			else
			{
				unit = context.getString(R.string.months);
			}
		}
		else if(c.get(Calendar.DATE) < now.get(Calendar.DATE))
		{
			ta = now.get(Calendar.DATE) - c.get(Calendar.DATE);
			if(1 == ta)
			{
				unit = context.getString(R.string.day);
			}
			else
			{
				unit = context.getString(R.string.days);
			}
		}
		else if(c.get(Calendar.HOUR_OF_DAY) < now.get(Calendar.HOUR_OF_DAY))
		{
			ta = now.get(Calendar.HOUR_OF_DAY) - c.get(Calendar.HOUR_OF_DAY);
			if(1 == ta)
			{
				unit = context.getString(R.string.hour);
			}
			else
			{
				unit = context.getString(R.string.hours);
			}
		}
		else if(c.get(Calendar.MINUTE) < now.get(Calendar.MINUTE))
		{
			ta = now.get(Calendar.MINUTE) - c.get(Calendar.MINUTE);
			if(1 == ta)
			{
				unit = context.getString(R.string.minute);
			}
			else
			{
				unit = context.getString(R.string.minutes);
			}
		}
		else
		{
			unit = context.getString(R.string.minute);
		}
		return String.format(context.getString(R.string.ago), ta, unit);
	}
}