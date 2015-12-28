package com.lianluo.core.util;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;
import android.util.Log;

public class DrawableUtil {

	private static final String TAG = "BitmapUtil";
	private static final int hardCachedSize = 3 * 1024 * 1024;
	// hard cache
	private static LruCache<String, Drawable> sHardDrawableCache = new LruCache<String, Drawable>(
			hardCachedSize) {
		@Override
		public int sizeOf(String key, Drawable value) {
			if(value == null) {
				return 0;
			}
			try {
				Bitmap bp = ((BitmapDrawable)value).getBitmap();
				return bp.getRowBytes() * bp.getHeight();
			} catch(Exception ex) {
				return 0;
			}
		}

		@Override
		protected void entryRemoved(boolean evicted, String key,
				Drawable oldValue, Drawable newValue) {
			Log.v(TAG, "hard cache is full , push to soft cache");
			sSoftDrawableCache.put(key, new SoftReference<Drawable>(oldValue));
		}
	};

	// soft cache
	private static final int SOFT_CACHE_CAPACITY = 50;
	private static LinkedHashMap<String, SoftReference<Drawable>> sSoftDrawableCache = new LinkedHashMap<String, SoftReference<Drawable>>(
			SOFT_CACHE_CAPACITY, 0.75f, true) {

		private static final long serialVersionUID = -7432990073665477678L;

		@Override
		public SoftReference<Drawable> put(String key, SoftReference<Drawable> value) {
			return super.put(key, value);
		}

		protected boolean removeEldestEntry(
				Map.Entry<String, java.lang.ref.SoftReference<Drawable>> eldest) {
			if (size() > SOFT_CACHE_CAPACITY) {
				Log.v(TAG, "Soft Reference limit , purge one");
				return true;
			}
			return false;
		};
	};

	// 缓存Drawable
	public static boolean putDrawable(String key, Drawable Drawable) {
		if (Drawable != null) {
			synchronized (sHardDrawableCache) {
				sHardDrawableCache.put(key, Drawable);
			}
			return true;
		}
		return false;
	}

	// 从缓存中获取Drawable
	public static Drawable getDrawable(String key) {
		synchronized (sHardDrawableCache) {
			Drawable Drawable = sHardDrawableCache.get(key);
			if (Drawable != null) {
				return Drawable;
			}
		}
		synchronized (sSoftDrawableCache) {
			SoftReference<Drawable> DrawableReference = sSoftDrawableCache.get(key);
			if (DrawableReference != null) {
				Drawable Drawable2 = DrawableReference.get();
				if (Drawable2 != null)
					return Drawable2;
				else {
					Log.v(TAG, "soft reference recover");
					sSoftDrawableCache.remove(key);
				}
			}
		}
		return null;
	}
}
