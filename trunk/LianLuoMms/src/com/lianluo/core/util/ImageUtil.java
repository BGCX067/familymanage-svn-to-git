package com.lianluo.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.lianluo.core.cache.CacheManager;
import com.lianluo.core.cache.ResourceFileCache;
import com.lianluo.core.event.IEvent;
import com.lianluo.core.net.DownloadHttp;
import com.lianluo.core.task.BaseTask;
import com.lianluo.core.task.TaskManagerFactory;

public class ImageUtil {

	private static HashMap<String, BaseTask> downloadImg_task = new HashMap<String, BaseTask>();

	public static void addDownloadTask(final Context context,
			final String imgPath, final String imgName, final Handler handler) {
		BaseTask task = downloadImg_task.get(imgName);
		if (task == null) {
			final Handler myHandler = new Handler() {

				@Override
				public void handleMessage(Message msg) {
					downloadImg_task.remove(imgName);
					Message message = new Message();
					Bundle bundle = new Bundle();
					bundle.putString(ResourceFileCache.RESOURCE_NAME, msg.getData().getString(ResourceFileCache.RESOURCE_NAME));
					message.setData(bundle);
					message.what = msg.what;
					handler.sendMessage(message);
				}

			};
			task = new BaseTask(null) {

				@Override
				public void doTask(IEvent event) throws Exception {
					try {
						DownloadHttp download = new DownloadHttp(context, imgName, myHandler) {
							
							protected void onError(Exception ex) {
								CacheManager.newInstance().removeResourceCache(imgName);
								Message msg = new Message();
								Bundle bundle = new Bundle();
								bundle.putString(ResourceFileCache.RESOURCE_NAME, imgName);
								msg.setData(bundle);
								msg.what = ResourceFileCache.RESOURCE_ERROR;
								myHandler.sendMessage(msg);
								ex.printStackTrace();
							};
							
						};
						download.setUrl(imgPath + imgName);
						download.connect();
					} catch (Exception ex) {
						CacheManager.newInstance().removeResourceCache(imgName);
						Message msg = new Message();
						Bundle bundle = new Bundle();
						bundle.putString(ResourceFileCache.RESOURCE_NAME, imgName);
						msg.setData(bundle);
						msg.what = ResourceFileCache.RESOURCE_ERROR;
						myHandler.sendMessage(msg);
						ex.printStackTrace();
					}
				}
			};
			TaskManagerFactory.createImageTaskManager().addTask(task);
			downloadImg_task.put(imgName, task);
		}
	}
	
	public static boolean checkDownloadTask(String imgName) {
		if(downloadImg_task.get(imgName) == null) {
			return false;
		} else {
			return true;
		}
	}

	public static Bitmap getBitmap(int bitAdress, Context context) {
		Bitmap bitmap;
		InputStream is = null;
		try {
			is = context.getResources().openRawResource(bitAdress);
			bitmap = BitmapFactory.decodeStream(is);
			System.gc();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// Ignore.
			}
		}
		return bitmap;
	}
}
