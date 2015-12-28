package com.lianluo.core.skin;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.model.HResDatabaseHelper;
import com.haolianluo.sms2.model.HResProvider;
import com.lianluo.core.net.download.DLData;
import com.lianluo.core.net.download.DLManager;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class SkinManage implements ISkinManage
{

	public static String mCurrentSkin = HConst.DEFAULT_PACKAGE_NAME;
	public static String mCurrentFile;
	
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
				String apkPath = DLManager.INSTALL_PATH + File.separator + mCurrentFile;
				resn = getRes(v.getContext(), apkPath);
			}
			String name = res.getResourceEntryName(backId);
			
			String defType = res.getResourceTypeName(backId);
			String defPackage = mCurrentSkin;
			int id = resn.getIdentifier(name, defType, defPackage);
			if (id != 0) {
				Drawable bg = resn.getDrawable(id);
				v.setBackgroundDrawable(bg);
			} else {
				Drawable bg = v.getContext().getResources().getDrawable(backId);
				v.setBackgroundDrawable(bg);
			}
		} catch (Exception e) {
			Drawable bg = v.getContext().getResources().getDrawable(backId);
			v.setBackgroundDrawable(bg);
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
			String apkPath = DLManager.INSTALL_PATH + File.separator + mCurrentFile;
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
/*
	public Drawable getDrawable(Context context, int resId) {
		if (!HConst.DEFAULT_PACKAGE_NAME.equals(mCurrentSkin)) {
			try {
				Context skinContext = context.createPackageContext(mCurrentSkin, Context.CONTEXT_IGNORE_SECURITY);
				Resources res = context.getResources();
				String name = res.getResourceEntryName(resId);
				String defType = res.getResourceTypeName(resId);
				String defPackage = mCurrentSkin;
				int id = skinContext.getResources().getIdentifier(name, defType, defPackage);
				if (id != 0) {
					Drawable bg = null;
					bg = skinContext.getResources().getDrawable(id);
					return bg;
				} else {
					Drawable bg = context.getResources().getDrawable(resId);
					return bg;
				}
			} catch (NameNotFoundException ex) {
				Drawable bg = context.getResources().getDrawable(resId);
				return bg;
			}
		} else {
			Drawable bg = context.getResources().getDrawable(resId);
			return bg;
		}
	}
*/	
	
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
	
	public static void installSkin(Context con, DLData task)
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
				}
				catch(Exception e)
				{
				}

			}
		}.start();
	}
}