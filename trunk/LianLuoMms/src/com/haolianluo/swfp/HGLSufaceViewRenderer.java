package com.haolianluo.swfp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import com.haolianluo.sms2.data.HConst;
import com.lianluo.core.net.download.DLManager;
import com.lianluo.core.skin.SkinManage;
import com.lianluo.core.util.HLog;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
public class HGLSufaceViewRenderer implements GLSurfaceView.Renderer {

	/**渲染flash*/
	private HBitmapTexture mBmpTex;
    /**信息内容*/
	private String sms = null;
	/**字库名*/
	private String ttffile;
	/**文件路径*/
	private String filename;
	/**图像位深度*/
	private final int DEPTH = 16;
	/**flash编号*/
	private int mID;
//	private final int FRAMES = 50;
	
	private Resources mResources;
	
	//是否是手动翻页
	private boolean isFling = false;
	
	static {
    	System.loadLibrary("flash");
        System.loadLibrary("swfengine");
    }

	 /**
	  * 模板
	  * @param context
	  * @param fileName
	  * @param sms
	  * @param ttf
	  */
	public HGLSufaceViewRenderer(Context context,String fileName,String sms,String ttf){
		if(sms == null || "".equals(sms)){
			sms = "  ";
		}
		this.filename = fileName;
		this.ttffile = ttf;
		this.sms = sms;
		getResource(context);
	}
	
	public HGLSufaceViewRenderer(Context context,String fileName,String sms){
		if(sms == null || "".equals(sms)){
			sms = "  ";
		}
		this.filename = fileName;
		this.ttffile = "system/fonts/DroidSansFallback.ttf";
		this.sms = sms;
		getResource(context);
	}
	
	
	/**
	 * 提醒动画
	 * @param context
	 */
	public HGLSufaceViewRenderer(Context context){
		//在提醒动画中不需要写文字，所以这块都传默认的就可以
		if(sms == null || "".equals(sms)){
			sms = "  ";
		}
		this.sms = "";
		this.ttffile = "system/fonts/DroidSansFallback.ttf";
		getResource(context);
		this.filename = gettxdhName();
	}
	
	private String gettxdhName(){
		String[] arrFile = null;
		try {
			arrFile = mResources.getAssets().list("txdh");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "txdh/" + arrFile[0];
	}
	
	private void getResource(Context context)
	{
		if(SkinManage.mCurrentSkin.equals(HConst.DEFAULT_PACKAGE_NAME))
		{
			this.mResources = context.getResources();
		}
		else
		{
			String apkPath = "";
			if(SkinManage.mCurrentFile.contains("default"))
			{
				apkPath = context.getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "default" + File.separator + SkinManage.mCurrentFile;
			}
			else
			{
				apkPath = DLManager.INSTALL_PATH + File.separator + SkinManage.mCurrentFile;
			}
			this.mResources  = SkinManage.getRes(context, apkPath);
		}
	}
	
	boolean turn = false;
	boolean isFirst = true;
	public void onDrawFrame(GL10 gl) {
		if(turn){
			turn = false;
//			if(isFling)
//			{	
				//带有书籍翻页效果
//				SwfEngine.flips(mID, load(filename), "aaa", SwfEngine.getUniCodes(sms),15);
//			}else
//			{	
				//Log.d("MID--------------------------", mID + "");
				//File f1 = new File(ttffile);
				//SwfEngine.setDefaultFont(mID, f1.getParent(), f1.getName());
				SwfEngine.close(mID);
				mID = SwfEngine.open(load(filename));
				File f1 = new File(ttffile);
				SwfEngine.setDefaultFont(mID, f1.getParent(), f1.getName());
				SwfEngine.addDynamicTexts(mID, "aaa", SwfEngine.getUniCodes(this.sms));
				SwfEngine.bind(mID, mBmpTex.getWidth(), mBmpTex.getHeight(), mBmpTex.getByteArray(), mBmpTex.getDepth());
			//}
		}
		
//		if(isFirst){
//			isFirst = false;
//			SwfEngine.exec(mID);
//		}
		
		//翻页效果----
		SwfEngine.exec(mID);
		mBmpTex.draw(gl);
	}

	public void close(){
		SwfEngine.close(mID);
		Runtime.getRuntime().gc();
	}
	
	
	/***
	 * 翻页
	 * @param moBanFileName 模板的名字
	 * @param ttfName  字库的名字
	 * @param sms  信息
	 * @param index ？？？
	 */
	public void turning(String moBanFileName,String ttfName,String sms, boolean isFling){
		HLog.i("page turning!!!");
		this.filename = moBanFileName;
		this.ttffile = ttfName;
		this.sms = sms;
		this.isFling = isFling;
		HLog.i("lock 2");
		turn = true;
	}
	
	public void turning(String moBanFileName,String sms, boolean isFling){
		this.turning(moBanFileName, "system/fonts/DroidSansFallback.ttf", sms, isFling);
	}
	
	public void modifRead(int index){
		if(index != -1){
//			hsmsManage.queryIsRead(application.talkSmsList.get(index).getId());
		}
	}
	
	private byte[] load(String file) {
		try {
//			File f = new File("/sdcard/LIANLUOSMS/00001/p20/6.swf");
			if(file == null){
				return null;
			}
//			File f = new File(file);
			InputStream is = mResources.getAssets().open(file);
//			FileInputStream fis = new FileInputStream(new FILEi);
			int len = is.available();
			byte[] data = new byte[len];
			is.read(data);
			is.close();
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		/**设定窗口大小*/
        gl.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        /**指定投影矩阵为当前矩阵*/
        gl.glMatrixMode(GL10.GL_PROJECTION);
        /**重置当前矩阵为单位矩阵，以屏幕中心为原点的矩阵*/
        gl.glLoadIdentity();
        /**初始化一个矩阵*/
        gl.glFrustumf(-ratio, ratio, -1, 1, 0, 10);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		 /**对图像质量和绘制速度之间的权衡作一些控制*/
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,GL10.GL_FASTEST);
        /**设置背景颜色*/
        gl.glClearColor(.5f, .5f, .5f, 1);
        /** 设定深度测试*/
        gl.glEnable(GL10.GL_DEPTH_TEST);
        /** 设置2D的纹理映射*/
        gl.glEnable(GL10.GL_TEXTURE_2D);
        /**设定观察点*/
        gl.glTranslatef(0, 0, -1);
        /**启用顶点数组*/
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        /**激活纹理坐标数组绘图*/
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);

//        HLog.i("libswfengine", mID+ " +++++"+id+"++++++ " + sms);
//        mID = SwfEngine.SwfOpen(mContext.getAssets(), id + ".swf");
        mID = SwfEngine.open(load(filename));
        File f1 = new File(ttffile);
        SwfEngine.setDefaultFont(mID, f1.getParent(), f1.getName());
//        HLog.i("libswfengine", mID+ " +++++++++++ " + sms);
        SwfEngine.addDynamicTexts(mID, "aaa", SwfEngine.getUniCodes(sms));
        /**G3(w256 h341); G6(w240 h320) G7(256 256)*/
        //TODO -------???
//        if(!new HSharedPreferences(context).readIsRuning()){
//        	  mBmpTex = new HBitmapTexture(120, 200, DEPTH);
//        }else{
        	  //mBmpTex = new HBitmapTexture(240, 320, DEPTH);
        	mBmpTex = new HBitmapTexture(120, 160, DEPTH);
//        }
      

		SwfEngine.bind(mID, mBmpTex.getWidth(), mBmpTex.getHeight(), mBmpTex.getByteArray(), mBmpTex.getDepth());
	}
}

class SwfEngine
{
	public class State
	{
		public int wakeup;
		public int clipx;
		public int clipy;
		public int width;
		public int height;
	}
	static State state;
	public static native int open(byte[]data);
	public static native int addDynamicTexts(int id, String name, long []unicodes);
	public static native int setDefaultFont(int id, String dir, String font);
	public static native int bind(int id, int w, int h, byte[]pixels, int depth);
	public static native int exec(int id/*, short[]pixels*/);
	public static native int flips(int id, byte[] data, String name, long []unicodes, int frames);
	public static native void close(int id);

	public static long[] getUniCodes(String s) {
		if (s == null) {
			return null;
		}
		char[] cs = s.toCharArray();
		long[] ls = new long[cs.length];
		for (int i = 0; i < cs.length; i++) {
			ls[i] = (long) cs[i];
		}
		return ls;
	}

	public static int SwfOpen(String file) {
		try {
//			File f = new File("/sdcard/LIANLUOSMS/00001/p20/6.swf");
			if(file == null){
				return 0;
			}
			File f = new File(file);
			FileInputStream is = new FileInputStream(f);
			int len = is.available();
			byte[] data = new byte[len];
			is.read(data);
			
			int id = SwfEngine.open(data);
			if(id == 0){
//				HLog.i("SwfPlayer", "Swf Engine open erron...");
			}
			is.close();
			return id;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
}
