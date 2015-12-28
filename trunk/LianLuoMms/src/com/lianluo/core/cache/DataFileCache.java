package com.lianluo.core.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DataFileCache implements BaseCache {

	@Override
	public void putCache(String key, Object obj) {
		try {
			File cacheDir = new File(CacheManager.CACHE_DATA);
			if(!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			File cacheFile = new File(cacheDir, key);
			if(!cacheFile.exists()) {
				cacheFile.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(cacheFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.flush();
			oos.close();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public Object getCache(String key) {
		Object obj = null;
		try {
			File cacheDir = new File(CacheManager.CACHE_DATA);
			if(!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			File cacheFile = new File(cacheDir, key);
			if(cacheFile.exists()) {
				FileInputStream fis = new FileInputStream(cacheFile);
				ObjectInputStream ois = new ObjectInputStream(fis);
				obj = ois.readObject();
				ois.close();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return obj;
	}

	@Override
	public void clear() {
		try {
			File cacheDir = new File(CacheManager.CACHE_DATA);
			if(!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			String[] cacheFiles = cacheDir.list();
			int size = cacheFiles.length;
			for(int i = 0; i < size; i++) {
				File cacheFile = new File(cacheDir, cacheFiles[i]);
				cacheFile.delete();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
