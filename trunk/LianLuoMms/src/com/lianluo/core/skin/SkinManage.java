package com.lianluo.core.skin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.model.HResDatabaseHelper;
import com.haolianluo.sms2.model.HResProvider;
import com.lianluo.core.net.download.DLData;
import com.lianluo.core.net.download.DLManager;
import com.lianluo.core.util.DrawableUtil;

public class SkinManage implements ISkinManage
{

	private static final String TAG = "TAG";
	private static final String noCacheImgName_base = "base";
	private static final String noCacheImgName_top = "top";
	public static String mCurrentSkin = HConst.DEFAULT_PACKAGE_NAME;
	public static String mCurrentFile;
	
	public static String skin_res_key;
	public static Resources skin_res_value;
	
	private static SkinManage mSkinManage;
	public static ISkinManage newInstance() {
		if(mSkinManage == null) {
			mSkinManage = new SkinManage();
		}
		return mSkinManage;
	}
	
	@Override
	public void setBackground(View v, int backId) {
		// TODO Auto-generated method stub
		if(backId == 0)
		{
			return;
		}
		try {
			//Context skinContext = v.getContext().createPackageContext(mCurrentSkin, Context.CONTEXT_IGNORE_SECURITY);
			Resources res = v.getContext().getResources();
			Resources resn = null;
			if(mCurrentSkin.equals(HConst.DEFAULT_PACKAGE_NAME))
			{
				resn = res;
			}
			else
			{
//				String apkPath = "";
//				if(mCurrentFile.contains("default"))
//				{
//					apkPath = v.getContext().getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "default" + File.separator + mCurrentFile;
//				}
//				else
//				{
//					apkPath = DLManager.INSTALL_PATH + File.separator + mCurrentFile;
//				}
//				resn = getRes(v.getContext(), apkPath);
				
				resn = getSkinResource(v.getContext());
			}
			String name = res.getResourceEntryName(backId);
			
			String defType = res.getResourceTypeName(backId);
			String defPackage = mCurrentSkin;
			int id = resn.getIdentifier(name, defType, defPackage);
			Drawable bg = null;
			if (id != 0) {
				if(DrawableUtil.getDrawable(mCurrentSkin + "_" + id) == null) {
//					Log.i(TAG, mCurrentSkin + "_" + id + "==================skin=null");
					bg = resn.getDrawable(id);
					if(!bg.isStateful() && !noCacheImgName_base.equals(name) && !noCacheImgName_top.equals(name)) {
						DrawableUtil.putDrawable(mCurrentSkin + "_" + id, bg);
					}
				} else {
//					Log.i(TAG, mCurrentSkin + "_" + id + "==================skin=cache");
					bg = DrawableUtil.getDrawable(mCurrentSkin + "_" + id);
				}
			} else {
				if(DrawableUtil.getDrawable(mCurrentSkin + "_" + backId) == null) {
//					Log.i(TAG, mCurrentSkin + "_" + backId + "==================default=null");
					bg = v.getContext().getResources().getDrawable(backId);
					if(!bg.isStateful() && !noCacheImgName_base.equals(name) && !noCacheImgName_top.equals(name)) {
						DrawableUtil.putDrawable(mCurrentSkin + "_" + backId, bg);
					}
				} else {
//					Log.i(TAG, mCurrentSkin + "_" + backId + "==================default=cache");
					bg = DrawableUtil.getDrawable(mCurrentSkin + "_" + backId);
				}
			}
			v.setBackgroundDrawable(bg);
		} catch (Exception e) {
			Drawable bg = v.getContext().getResources().getDrawable(backId);
			v.setBackgroundDrawable(bg);
		}
	}
	
	private Resources getSkinResource(Context context) {
		if(mCurrentSkin.equals(skin_res_key)) {
			return skin_res_value;
		} else {
			String apkPath = "";
			if(mCurrentFile.contains("default"))
			{
				apkPath = context.getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "default" + File.separator + mCurrentFile;
			}
			else
			{
				apkPath = DLManager.INSTALL_PATH + File.separator + mCurrentFile;
			}
			skin_res_value = getRes(context, apkPath);
			skin_res_key = mCurrentSkin;
			
			return skin_res_value;
		}
	}

	@Override
	public void setImage(View v, int id) {
		// TODO Auto-generated method stub
		if(id == 0)
		{
			return;
		}
	}

	@Override
	public void setTextColor(View v, int colorId) {
		// TODO Auto-generated method stub
		if(colorId == 0)
		{
			return;
		}
		try {
			
			//Context skinContext = v.getContext().createPackageContext(mCurrentSkin, Context.CONTEXT_IGNORE_SECURITY);
			Resources res = v.getContext().getResources();
			String apkPath = "";
			if(mCurrentFile.contains("default"))
			{
				apkPath = v.getContext().getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "default" + File.separator + mCurrentFile;
			}
			else
			{
				apkPath = DLManager.INSTALL_PATH + File.separator + mCurrentFile;
			}
			Resources resn = getRes(v.getContext(), apkPath);
			String name = res.getResourceEntryName(colorId);
			String defType = res.getResourceTypeName(colorId);
			String defPackage = mCurrentSkin;
			int id = resn.getIdentifier(name, defType, defPackage);
			if (id != 0) {
				((TextView) v).setTextColor(resn.getColor(id));
			} else {
				((TextView) v).setTextColor(v.getContext().getResources().getColor(colorId));
			}
		} catch (Exception ex) {
			((TextView) v).setTextColor(v.getContext().getResources().getColor(colorId));
		}
	}

	public Drawable getDrawable(Context context, int resId) {
		if (!HConst.DEFAULT_PACKAGE_NAME.equals(mCurrentSkin)) {
			//Context skinContext = context.createPackageContext(mCurrentSkin, Context.CONTEXT_IGNORE_SECURITY);
			Resources res = context.getResources();
			Resources resn = null;
			if(mCurrentSkin.equals(HConst.DEFAULT_PACKAGE_NAME))
			{
				resn = res;
			}
			else
			{
//				String apkPath = "";
//				if(mCurrentFile.contains("default"))
//				{
//					apkPath = context.getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "default" + File.separator + mCurrentFile;
//				}
//				else
//				{
//					apkPath = DLManager.INSTALL_PATH + File.separator + mCurrentFile;
//				}
//				resn = getRes(context, apkPath);
				
				resn = getSkinResource(context);
			}
			String name = res.getResourceEntryName(resId);
			String defType = res.getResourceTypeName(resId);
			String defPackage = mCurrentSkin;
			int id = resn.getIdentifier(name, defType, defPackage);
			Drawable bg = null;
			if (id != 0) {
				if(DrawableUtil.getDrawable(mCurrentSkin + "_" + id) == null) {
//					Log.i(TAG, mCurrentSkin + "_" + id + "=================d=skin=null");
					bg = resn.getDrawable(id);
					if(!bg.isStateful() && !noCacheImgName_base.equals(name) && !noCacheImgName_top.equals(name)) {
						DrawableUtil.putDrawable(mCurrentSkin + "_" + id, bg);
					}
				} else {
//					Log.i(TAG, mCurrentSkin + "_" + id + "=================d=skin=cache");
					bg = DrawableUtil.getDrawable(mCurrentSkin + "_" + id);
				}
			} else {
				if(DrawableUtil.getDrawable(mCurrentSkin + "_" + resId) == null) {
//					Log.i(TAG, mCurrentSkin + "_" + resId + "=================d=default=null");
					bg = context.getResources().getDrawable(resId);
					if(!bg.isStateful() && !noCacheImgName_base.equals(name) && !noCacheImgName_top.equals(name)) {
						DrawableUtil.putDrawable(mCurrentSkin + "_" + resId, bg);
					}
				} else {
//					Log.i(TAG, mCurrentSkin + "_" + resId + "=================d=default=cache");
					bg = DrawableUtil.getDrawable(mCurrentSkin + "_" + resId);
				}
			}
			return bg;
		} else {
			Drawable bg = context.getResources().getDrawable(resId);
			return bg;
		}
	}
	
	
	public static Resources getRes(Context ctx, String apkPath)
	{

		Resources res = ctx.getResources();

	     File apkFile = new File(apkPath);  
	     if (!apkFile.exists() || !apkPath.toLowerCase().endsWith(".apk")) {  
	      return res;  
	     }  
	     //AppInfoData appInfoData;  
	     String PATH_PackageParser = "android.content.pm.PackageParser";  
	     String PATH_AssetManager = "android.content.res.AssetManager";  
	     try {  
	      //反射得到pkgParserCls对象并实例化,有参数  
	      Class<?> pkgParserCls = Class.forName(PATH_PackageParser);  
	      Class<?>[] typeArgs = {String.class};  
	      Constructor<?> pkgParserCt = pkgParserCls.getConstructor(typeArgs);  
	      Object[] valueArgs = {apkPath};  
	      Object pkgParser = pkgParserCt.newInstance(valueArgs);  
	        
	      //从pkgParserCls类得到parsePackage方法  
	      DisplayMetrics metrics = new DisplayMetrics();  
	      metrics.setToDefaults();//这个是与显示有关的, 这边使用默认  
	      typeArgs = new Class<?>[]{File.class,String.class,  
	            DisplayMetrics.class,int.class};  
	      Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod(  
	        "parsePackage", typeArgs);  
	        
	      valueArgs=new Object[]{new File(apkPath),apkPath,metrics,0};  
	        
	      //执行pkgParser_parsePackageMtd方法并返回  
	      Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,  
	        valueArgs);  
	        
	      //从返回的对象得到名为"applicationInfo"的字段对象   
	      if (pkgParserPkg==null) {  
	       return res;  
	      }  
	      Field appInfoFld = pkgParserPkg.getClass().getDeclaredField(  
	        "applicationInfo");  
	        
	      //从对象"pkgParserPkg"得到字段"appInfoFld"的值  
	      if (appInfoFld.get(pkgParserPkg)==null) {  
	       return res;  
	      }  
	      ApplicationInfo info = (ApplicationInfo) appInfoFld  
	        .get(pkgParserPkg);     
	        
	      //反射得到assetMagCls对象并实例化,无参  
	      Class<?> assetMagCls = Class.forName(PATH_AssetManager);     
	      Object assetMag = assetMagCls.newInstance();  
	      //从assetMagCls类得到addAssetPath方法  
	      typeArgs = new Class[1];  
	      typeArgs[0] = String.class;  
	      Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod(  
	        "addAssetPath", typeArgs);  
	      valueArgs = new Object[1];  
	      valueArgs[0] = apkPath;  
	      //执行assetMag_addAssetPathMtd方法  
	      assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);  
	        
	        
	      //得到Resources对象并实例化,有参数  
	      res = ctx.getResources();  
	      typeArgs = new Class[3];  
	      typeArgs[0] = assetMag.getClass();  
	      typeArgs[1] = res.getDisplayMetrics().getClass();  
	      typeArgs[2] = res.getConfiguration().getClass();  
	      Constructor<Resources> resCt = Resources.class  
	        .getConstructor(typeArgs);  
	      valueArgs = new Object[3];  
	      valueArgs[0] = assetMag;  
	      valueArgs[1] = res.getDisplayMetrics();  
	      valueArgs[2] = res.getConfiguration();  
	      res = (Resources) resCt.newInstance(valueArgs);  
	      // 读取apk文件的信息  
	      //appInfoData = new AppInfoData();  
	      if (info!=null) {  
	       if (info.icon != 0) {// 图片存在，则读取相关信息  
	        Drawable icon = res.getDrawable(info.icon);// 图标  
	       // res.
	        //appInfoData.setAppicon(icon);  
	        }  
	       if (info.labelRes != 0) {  
	        String neme = (String) res.getText(info.labelRes);// 名字  
	       // appInfoData.setAppname(neme);  
	       }else {  
	        String apkName=apkFile.getName();  
	       // appInfoData.setAppname(apkName.substring(0,apkName.lastIndexOf(".")));  
	       }  
	       String pkgName = info.packageName;// 包名     
	      // appInfoData.setApppackage(pkgName);  
	      }else {  
	       return res;  
	      }     
	      PackageManager pm = ctx.getPackageManager();  
	      PackageInfo packageInfo = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);  
	      if (packageInfo != null) {  
	       //appInfoData.setAppversion(packageInfo.versionName);//版本号  
	       //appInfoData.setAppversionCode(packageInfo.versionCode+"");//版本码  
	      }  
	     // return appInfoData;  
	     } catch (Exception e) {   
	      e.printStackTrace();  
	     }  
	     return res;
	     
	}
	
	@Override
	public String getSkin() {
		// TODO Auto-generated method stub
		return mCurrentSkin;
	}
	
	public static void installSkin(Context con, DLData task, final Handler finish)
	{
		final Context context = con;
		final DLData tas = task;
		new Thread(){
			public void run()
			{
				try
				{
					ContentValues v = new ContentValues();
					v.put(HResDatabaseHelper.RES_USE, 0);
					context.getContentResolver().update(HResProvider.CONTENT_URI_SKIN, v, null, null);
					ContentValues values = new ContentValues();
					values.put(HResDatabaseHelper.FILE_NAME, tas.getFileName());
					values.put(HResDatabaseHelper.PACKAGENAME, tas.getPackagename());
					values.put(HResDatabaseHelper.DISPLAY_NAME, tas.getDisplayName());
					values.put(HResDatabaseHelper.RES_KEY, "");
					values.put(HResDatabaseHelper.RES_USE, 1);
					values.put(HResDatabaseHelper.TOTAL_SIZE, tas.getTotalSize());
					values.put(HResDatabaseHelper.CHARGE, tas.getCharge());
					values.put(HResDatabaseHelper.RES_ID, tas.getResID());
					context.getContentResolver().insert(HResProvider.CONTENT_URI_SKIN, values);
					//jayce add
					SkinManage.mCurrentSkin = tas.getPackagename();
					SkinManage.mCurrentFile = tas.getFileName();
					FileInputStream in = new FileInputStream(DLManager.LOCAL_PATH + File.separator + tas.getFileName());
					File p = new File(DLManager.INSTALL_PATH);
					if(!p.exists())
					{
						p.mkdir();
					}
					p = new File(DLManager.INSTALL_PATH + File.separator + tas.getFileName());
					if(p.exists())
					{
						p.delete();
					}
					FileOutputStream out = new FileOutputStream(DLManager.INSTALL_PATH + File.separator + tas.getFileName());
					byte[] buf = new byte[256];
					while(-1 != in.read(buf))
					{
						out.write(buf);
					}
					DLManager dlManager = DLManager.getInstance(context);
					dlManager.deleteTask(tas.getPackagename());
					Bundle bundle = new Bundle();
					bundle.putString("disname", tas.getDisplayName());
					Message msg = new Message();
					msg.setData(bundle);
					finish.sendMessage(msg);
				}
				catch(Exception e)
				{
				}

			}
		}.start();
	}
}
