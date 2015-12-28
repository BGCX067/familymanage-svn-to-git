package com.haolianluo.sms2.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.haolianluo.sms2.data.HConst;
import com.lianluo.core.net.download.DLManager;
import com.lianluo.core.skin.SkinManage;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

public class HResDatabaseHelper extends SQLiteOpenHelper{
	//table download columns
	public static final String DISPLAY_NAME = "name";
	public static final String ICON_URL = "icon";
	public static final String FILE_NAME = "filename";
	public static final String TASK_STATUS = "status";
	public static final String TOTAL_SIZE = "totalsize";
	public static final String CURRENT_SIZE = "currentsize";
	public static final String CHARGE = "charge";
	public static final String CHARGE_MSG = "chargemsg";
	public static final String RES_ID = "resid";
	public static final String PACKAGENAME = "packagename";
	
	//table skin columns
	public static final String RES_KEY = "reskey";
	public static final String RES_USE = "resuse";
	
	private Context mContext;
	
	public HResDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS " + HResProvider.TABLE_DOWNLOAD +
				 " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				 DISPLAY_NAME + " VARCHAR NOT NULL, " +
				 FILE_NAME + " VARCHAR, " +
				 TASK_STATUS + " INTEGER, " +
				 TOTAL_SIZE + " INTEGER, " +
				 CURRENT_SIZE + " INTEGER, " +
				 RES_ID + " VARCHAR, " +
				 CHARGE + " VARCHAR, " +
				 ICON_URL + " VARCHAR, " +
				 CHARGE_MSG + " VARCHAR, " +
				 PACKAGENAME + " VARCHAR);"
				 );
		
		db.execSQL("CREATE TABLE IF NOT EXISTS " + HResProvider.TABLE_SKIN +
				 " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				 FILE_NAME + " VARCHAR, " +
				 PACKAGENAME + " VARCHAR, " +
				 DISPLAY_NAME + " VARCHAR, " +
				 RES_KEY + " VARCHAR, " +
				 RES_USE + " INTEGER, " +
				 TOTAL_SIZE + " INTEGER, " +
				 CHARGE + " INTEGER, " +
				 RES_ID + " VARCHAR);"
				 );
		ContentValues values = new ContentValues();
		values.put(PACKAGENAME, HConst.DEFAULT_PACKAGE_NAME);
		values.put(RES_USE, 1);
		values.put(RES_ID, 0);
		db.insert(HResProvider.TABLE_SKIN, null, values);
		int rid = 0;
		try
		{
			Resources resa = mContext.getResources();
			String[] nms = resa.getAssets().list("skin");
			String[] names = new String[nms.length];
			if(nms[0].charAt(0) > nms[nms.length - 1].charAt(0))
			{
				for(int i = 0; i < nms.length; i++)
				{
					names[i] = nms[nms.length - 1 - i];
				}
			}
			else
			{
				names = nms;
			}
			for(String name : names)
			{
				InputStream in = resa.getAssets().open("skin" + File.separator + name);
				String dir = mContext.getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "default";
				File defdir = new File(dir);
				if(!defdir.exists())
				{
					defdir.mkdir();
				}
				defdir = new File(dir + File.separator + name);
				if(defdir.exists())
				{
					defdir.delete();
				}
				FileOutputStream out = new FileOutputStream(defdir);
				byte[] buf = new byte[256];
				while(-1 != in.read(buf))
				{
					out.write(buf);
				}
				
				Resources res = mContext.getResources();
			     //AppInfoData appInfoData;  
			     String PATH_PackageParser = "android.content.pm.PackageParser";  
			     String PATH_AssetManager = "android.content.res.AssetManager";  
				      //反射得到pkgParserCls对象并实例化,有参数  
				      Class<?> pkgParserCls = Class.forName(PATH_PackageParser);  
				      Class<?>[] typeArgs = {String.class};  
				      Constructor<?> pkgParserCt = pkgParserCls.getConstructor(typeArgs);  
				      Object[] valueArgs = {defdir.toString()};  
				      Object pkgParser = pkgParserCt.newInstance(valueArgs);  
				        
				      //从pkgParserCls类得到parsePackage方法  
				      DisplayMetrics metrics = new DisplayMetrics();  
				      metrics.setToDefaults();//这个是与显示有关的, 这边使用默认  
				      typeArgs = new Class<?>[]{File.class,String.class,  
				            DisplayMetrics.class,int.class};  
				      Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod(  
				        "parsePackage", typeArgs);  
				        
				      valueArgs=new Object[]{new File(defdir.toString()),defdir.toString(),metrics,0};  
				        
				      //执行pkgParser_parsePackageMtd方法并返回  
				      Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,  
				        valueArgs);  
			        
				      Field appInfoFld = pkgParserPkg.getClass().getDeclaredField(  
				    		  "applicationInfo");  
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
			      valueArgs[0] = defdir.toString();  
			      //执行assetMag_addAssetPathMtd方法  
			      assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);  
			        
			        
			      //得到Resources对象并实例化,有参数  
			      res = mContext.getResources();  
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
				
			    String na = (String) res.getText(info.labelRes);
				ContentValues val = new ContentValues();
				val.put(PACKAGENAME, info.packageName);
				val.put(FILE_NAME, name);
				val.put(DISPLAY_NAME, na);
				val.put(RES_ID, rid + 1);
				val.put(RES_USE, 0);
				val.put(CHARGE, HConst.CHARGE_TRY);//-2 for try
				db.insert(HResProvider.TABLE_SKIN, null, val);
			}
		}
		catch(Exception e)
		{}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + HResProvider.TABLE_SKIN);
		db.execSQL("DROP TABLE IF EXISTS " + HResProvider.TABLE_DOWNLOAD);
		onCreate(db);
	}
	
	private ApplicationInfo getInfo(Context ctx, String apkPath)
	{
		Resources res = ctx.getResources();

	     File apkFile = new File(apkPath);  
	     if (!apkFile.exists() || !apkPath.toLowerCase().endsWith(".apk")) 
	     {  
	    	 return null;  
	     }  
	     //AppInfoData appInfoData;  
	     String PATH_PackageParser = "android.content.pm.PackageParser";  
	     String PATH_AssetManager = "android.content.res.AssetManager";  
	     try
	     {  
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
		      if (pkgParserPkg==null)
		      {  
		    	  return null;  
		      }  
		      Field appInfoFld = pkgParserPkg.getClass().getDeclaredField(  
		    		  "applicationInfo");  
	        
		      //从对象"pkgParserPkg"得到字段"appInfoFld"的值  
		      if (appInfoFld.get(pkgParserPkg)==null) 
		      {  
		    	  return null;  
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
	      
	      return info;
	     }
	     catch(Exception e)
	     {
	    	 return null;
	     }
	    }
	     
}