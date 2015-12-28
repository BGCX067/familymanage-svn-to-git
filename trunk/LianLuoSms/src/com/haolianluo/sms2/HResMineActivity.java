package com.haolianluo.sms2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.model.HResDatabaseHelper;
import com.haolianluo.sms2.model.HResProvider;
import com.haolianluo.sms2.model.HStatistics;
import com.lianluo.core.net.download.DLData;
import com.lianluo.core.net.download.DLManager;
import com.lianluo.core.skin.SkinManage;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ToolsUtil;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class HResMineActivity extends HActivity
{
	private static final String TAG = "HResMineActivity";
	private Context mContext;
	Cursor mInstalledCursor;
	/* new
	LinearLayout mListDownloading;
	LinearLayout mListDownloaded;
	*/
	LinearLayout mListInstalled;
	DLManager mDLManager;
	/* new
	Map<String, View> mViewMapDownloading = new HashMap<String, View>();
	Map<String, View> mViewMapDownloaded = new HashMap<String, View>();*/
	Map<String, View> mViewMapInstalled = new HashMap<String, View>();
	/* new
	List<DLData> mTasksDownloading = new ArrayList<DLData>();
	List<DLData> mTasksDownloaded = new ArrayList<DLData>();*/
	LayoutInflater mInflater;
	Dialog mConfirmDialog;
	/*
	private TextView mDownloadingCount;
	private TextView mDownloadedCount;*/
	private TextView mInstalledCount;
	private ImageView mTitleBack;
	private static final int UNIT = 1024 * 1024;
	//private SkinLinearLayout mSkinlo;
	Map<String, Bitmap> mIconMap = new HashMap<String, Bitmap>();
	
	private BroadcastReceiver mBroadcastInstalled = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent != null && intent.getAction().equals(HSkinReceiver.ACTION_INSTALLED))
			{
				HLog.e(TAG, "ACTION_INSTALLED");
				initView();
			}
		}};
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext = this;
		mInflater = LayoutInflater.from(mContext);
		setContentView(R.layout.res_mine);
		IntentFilter filter = new IntentFilter(HSkinReceiver.ACTION_INSTALLED);
		registerReceiver(mBroadcastInstalled, filter);
	}
	
	public void onStart()
	{
		HLog.e(TAG, "onStart");
		super.onStart();
		initView();
	}
	
	public void onResume()
	{
		super.onResume();
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		mInstalledCursor.close();
		@SuppressWarnings("rawtypes")
		Iterator iter = mIconMap.entrySet().iterator();
		while(iter.hasNext())
		{
			@SuppressWarnings("unchecked")
			Map.Entry<String , Bitmap> entry = (Entry<String, Bitmap>) iter.next();
			Bitmap bm = entry.getValue();
			bm.recycle();
		}
		mIconMap.clear();
		unregisterReceiver(mBroadcastInstalled);
	}
	
	protected void initView()
	{
		//if(mSkinlo != null)
		//{
		//	mSkinlo.removeAllViews();
		//}
		/* new
		mViewMapDownloading.clear();
		mViewMapDownloaded.clear();
		*/
		mViewMapInstalled.clear();
		/* new
		mTasksDownloading.clear();
		mTasksDownloaded.clear();
		*/
		//mSkinlo = (SkinLinearLayout)SkinLinearLayout.inflate(mContext, R.layout.res_mine, null);
		//setContentView(mSkinlo);
		View title = findViewById(R.id.top);
		mTitleBack = (ImageView) title.findViewById(R.id.res_title_icon);
		mTitleBack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		TextView tv_title = (TextView) title.findViewById(R.id.res_title_text);
		tv_title.setText(R.string.shop_tab1);
		ImageView sc = (ImageView)title.findViewById(R.id.res_title_button);
		sc.setVisibility(View.GONE);
		/* new
		if(mListDownloading != null)
		{
			mListDownloading.removeAllViews();
		}
		if(mListDownloaded != null)
		{
			mListDownloaded.removeAllViews();
		}
		*/
		if(mListInstalled != null)
		{
			mListInstalled.removeAllViews();
		}
		/* new
		mListDownloading = (LinearLayout) findViewById(R.id.list_downloading);
		mListDownloaded = (LinearLayout) findViewById(R.id.list_downloaded);
		*/
		mListInstalled= (LinearLayout) findViewById(R.id.list_installed);
		/* new
		mDownloadingCount = (TextView)findViewById(R.id.res_downloading_count);
		mDownloadedCount = (TextView)findViewById(R.id.res_downloaded_count);
		*/
		mInstalledCount = (TextView)findViewById(R.id.res_installed_count);
		/* new
		initDownload();
		*/
		initInstalled();
		updateCount();
	}
	/* new
	private void initDownload()
	{
		mDLManager = DLManager.getInstance(mContext);
		List<DLData> list = mDLManager.getAllTasks();
		mDLManager.setProgressHandler(mProgressHandler);
		for(DLData d : list)
		{
			final Long id = d.getId();
			final String sid = String.valueOf(id);
			if(d.getStatus() == DLManager.STATUS_SUCCESS)
			{
				mTasksDownloaded.add(d);
				View view = mInflater.inflate(R.layout.res_downloaded_item, null);
				updateDownloaded(view, d);
				mViewMapDownloaded.put(sid, view);
				mListDownloaded.addView(view);
			}
			else
			{
				mTasksDownloading.add(d);
				View view = mInflater.inflate(R.layout.res_downloading_item, null);
				updateDownloading(view, d);
				mViewMapDownloading.put(String.valueOf(d.getId()), view);
				mListDownloading.addView(view);
			}
		}
	}
	*/
	private void initInstalled()
	{
		mInstalledCursor = mContext.getContentResolver().query(HResProvider.CONTENT_URI_SKIN, null, null, null, HResDatabaseHelper.RES_USE + " DESC");
		mListInstalled.removeAllViews();
		if(mInstalledCursor.getCount() > 0)
		{
			while(mInstalledCursor.moveToNext())
			{
				View view = mInflater.inflate(R.layout.res_installed_item, null);
				//updateInstalled(view, mInstalledCursor);
				mViewMapInstalled.put(String.valueOf(mInstalledCursor.getInt(mInstalledCursor.getColumnIndex("_id"))), view);
				mListInstalled.addView(view);
			}
		}
	}
	
	private void updateCount()
	{
		/* new
		mDownloadingCount.setText("(" + mTasksDownloading.size() + ")");
		mDownloadedCount.setText("(" + mTasksDownloaded.size() + ")");
		*/
		mInstalledCount.setText("(" + mInstalledCursor.getCount() + ")");
	}
	/* new
	private void updateDownloading(View view, DLData data)
	{
		ImageView iv_icon = (ImageView) view.findViewById(R.id.res_icon);
		ImageView iv_status = (ImageView)view.findViewById(R.id.res_status);
		TextView tv_name = (TextView)view.findViewById(R.id.res_name);
		TextView tv_size = (TextView)view.findViewById(R.id.res_size);
		TextView tv_prog = (TextView)view.findViewById(R.id.res_progress);
		ProgressBar pro_bar = (ProgressBar)view.findViewById(R.id.progressbar);
		
		iv_status.setOnClickListener(new OnStateListener(data.getId()));
		
		switch(data.getStatus())
		{
			case DLManager.STATUS_READY:
			{
				iv_status.setBackgroundResource(R.drawable.btn_wait);
				//pro_bar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_style));
			}
			break;
			case DLManager.STATUS_RUNNING:
			{
				iv_status.setBackgroundResource(R.drawable.res_pause);
				//pro_bar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_style));
			}
			break;
			case DLManager.STATUS_PAUSE:
			{
				iv_status.setBackgroundResource(R.drawable.res_start);
				//pro_bar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_style));
			}
			break;
		}
		tv_name.setText(data.getDisplayName());
		setIcon(iv_icon, data);
		
		DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);
        df.setMinimumFractionDigits(1);
        float progress = 0.0f;
        if(data.getCurrentSize() > 0 && data.getTotalSize() > 0)
        {
        	progress = (float)data.getCurrentSize() * 100.0f / data.getTotalSize();
        }
        String p = df.format(progress);
        tv_prog.setText(p + "%");
		float size = (float)data.getTotalSize() / UNIT;
		String s = df.format(size);
		tv_size.setText(s + "MB");
		pro_bar.setMax(data.getTotalSize());
		pro_bar.setProgress(data.getCurrentSize());
		final Long id = data.getId();
		final String sid = String.valueOf(id);
		final DLData da = data;
		view.setLongClickable(true);
		view.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(mContext)
				.setTitle(da.getDisplayName())
				.setItems(new String[]{getString(R.string.delete)}, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						View v = mViewMapDownloading.get(String.valueOf(id));
						if(v != null)
						{
							mViewMapDownloading.remove(sid);
							mListDownloading.removeView(v);
							mTasksDownloading.remove(da);
							mDLManager.deleteTask(id);
						}
					}})
				.create()
				.show();
				return false;
			}
		});
	}
	*/
	/* new
	private void updateDownloaded(View view, DLData data)
	{
		ImageView iv_icon = (ImageView) view.findViewById(R.id.res_icon);
		setIcon(iv_icon, data);
		
		TextView tv_name = (TextView)view.findViewById(R.id.res_name);
		TextView tv_size = (TextView)view.findViewById(R.id.res_size);
		TextView tv_install = (TextView)view.findViewById(R.id.res_install);
		tv_install.setOnClickListener(new OnInstallListener(data.getId()));
		tv_name.setText(data.getDisplayName());
		DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);
        df.setMinimumFractionDigits(1);
		float size = (float)data.getTotalSize() / UNIT;
		String s = df.format(size);
		tv_size.setText(s + "MB");
		final Long id = data.getId();
		final String sid = String.valueOf(id);
		final DLData da = data;
		view.setLongClickable(true);
		view.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(mContext)
				.setTitle(da.getDisplayName())
				.setItems(new String[]{getString(R.string.delete)}, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						View v = mViewMapDownloaded.get(String.valueOf(id));
						if(v != null)
						{
							mViewMapDownloaded.remove(sid);
							mListDownloaded.removeView(v);
							mTasksDownloaded.remove(da);
							mDLManager.deleteTask(id);
						}
					}})
				.create()
				.show();
				return false;
			}
		});
	}
	*/
	/*private void updateInstalled(View view, Cursor cursor)
	{
		
		ImageView iv_icon = (ImageView) view.findViewById(R.id.res_icon);
		try {
			Context context = mContext.createPackageContext(cursor.getString(cursor.getColumnIndex(HResDatabaseHelper.PACKAGENAME)), Context.CONTEXT_IGNORE_SECURITY);
			Drawable d = new BitmapDrawable(BitmapFactory.decodeStream(context.getAssets().open("titleS" + File.separator + context.getAssets().list("titleS")[0])));
			iv_icon.setBackgroundDrawable(d);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			iv_icon.setBackgroundResource(R.drawable.shop_skin);
		}
		TextView tv_name = (TextView) view.findViewById(R.id.res_name);
		//TextView tv_size = (TextView)view.findViewById(R.id.res_size);
		
		//old design
		FrameLayout fl = (FrameLayout)view.findViewById(R.id.res_frame_apply);
		ImageView iv_apply = (ImageView)view.findViewById(R.id.res_apply);
		
		//old design
		//new design
		
		Button bt_delete = (Button) view.findViewById(R.id.button_delete);
		Button bt_apply = (Button)view.findViewById(R.id.button_apply);
		TextView tv_applied = (TextView)view.findViewById(R.id.textview_applied);
		
		//new design
		
		//new design
		
		if((1 == cursor.getInt(cursor.getColumnIndex(HResDatabaseHelper.RES_USE))))
		{
			tv_applied.setVisibility(View.VISIBLE);
			bt_delete.setVisibility(View.GONE);
			bt_apply.setVisibility(View.GONE);
		}
		else
		{
			tv_applied.setVisibility(View.GONE);
			bt_delete.setVisibility(View.VISIBLE);
			bt_apply.setVisibility(View.VISIBLE);
		}
		bt_apply.setOnClickListener(new onCheckListener(cursor.getInt(cursor.getColumnIndex("_id"))));
		
		//new design
		
		String packname = cursor.getString(cursor.getColumnIndex(HResDatabaseHelper.PACKAGENAME));
		String disname = cursor.getString(cursor.getColumnIndex(HResDatabaseHelper.DISPLAY_NAME));
		if("com.haolianluo.sms2".equalsIgnoreCase(packname))
		{
			//new design
			//bt_delete.setVisibility(View.GONE);
			//new design
			
			tv_name.setText(getString(R.string.default_skin));
			//tv_size.setText("0.0MB");
		}
		else
		{
			PackageManager pm = mContext.getPackageManager();
			ApplicationInfo appinfo;
			try {
				appinfo = pm.getApplicationInfo(packname,
						PackageManager.GET_META_DATA);
				disname = appinfo.loadLabel(pm).toString();
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tv_name.setText(disname);
			
			DecimalFormat df = new DecimalFormat();
	        df.setMaximumFractionDigits(1);
	        df.setMinimumFractionDigits(1);
			float size = (float)cursor.getInt(cursor.getColumnIndex(HResDatabaseHelper.TOTAL_SIZE)) / UNIT;
			String s = df.format(size);
			tv_size.setText(s + "MB");
			
		}
		//old design
		if((1 == cursor.getInt(cursor.getColumnIndex(HResDatabaseHelper.RES_USE))))
		{
			iv_apply.setVisibility(View.VISIBLE);
		}
		else
		{
			iv_apply.setVisibility(View.GONE);
		}
		fl.setOnClickListener(new onCheckListener(cursor.getInt(cursor.getColumnIndex("_id"))));
		//old design
		
		final String packagename = mInstalledCursor.getString(mInstalledCursor.getColumnIndex(HResDatabaseHelper.PACKAGENAME));
		final String name = disname;
		String res_id = mInstalledCursor.getString(mInstalledCursor.getColumnIndex(HResDatabaseHelper.RES_ID));
		res_id = (res_id == null || res_id.trim().equals("")) ? "0" : res_id;
		final String resid = res_id;
		//new Design
		
		bt_delete.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(mContext)
				.setTitle(name)
				.setItems(new String[]{getString(R.string.delete)}, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Uri uri = Uri.parse("package:" + packagename);
						Intent i = new Intent(Intent.ACTION_DELETE, uri);
						startActivity(i);
					}})
				.create()
				.show();
			}});
			
		//new Design
		
		//old design
		if(!packagename.equals("com.haolianluo.sms2"))
		{
			view.setLongClickable(true);
			view.setOnLongClickListener(new View.OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					new AlertDialog.Builder(mContext)
					.setTitle(name)
					.setItems(new String[]{getString(R.string.delete)}, new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							//卸载
							HStatistics mStatistics = new HStatistics(HResMineActivity.this);
							mStatistics.add(HStatistics.Z12_3, resid, "", "");
							Uri uri = Uri.parse("package:" + packagename);
							Intent i = new Intent(Intent.ACTION_DELETE, uri);
							startActivity(i);
						}})
					.create()
					.show();
					return false;
				}
			});
		}
		//old design
	}*/
	
	private class onCheckListener implements OnClickListener
	{
		private int mId;
		public onCheckListener(int id)
		{
			mId = id;
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(null == mProgressDialog)
			{
				mProgressDialog = new ProgressDialog(mContext);
				mProgressDialog.setMessage(mContext.getString(R.string.skin_changing));
				mProgressDialog.show();
			}
			new Thread(){
				public void run()
				{
					ContentValues cvalues = new ContentValues();
					cvalues.put(HResDatabaseHelper.RES_USE, 0);
					mContext.getContentResolver().update(HResProvider.CONTENT_URI_SKIN, cvalues, null, null);
					ContentValues values = new ContentValues();
					values.put(HResDatabaseHelper.RES_USE, 1);
					mContext.getContentResolver().update(HResProvider.CONTENT_URI_SKIN, values, "_id = '" + mId + "'", null);
					Cursor c = mContext.getContentResolver().query(HResProvider.CONTENT_URI_SKIN, null, 
							HResDatabaseHelper.RES_USE + " = '1'", null, null); 
					if(c.getCount() > 0)
					{
						c.moveToNext();
						HStatistics mHStatistics = new HStatistics(HResMineActivity.this);
						int charge = c.getInt(c.getColumnIndex(HResDatabaseHelper.CHARGE));
						String resid = c.getString(c.getColumnIndex(HResDatabaseHelper.RES_ID));
						resid = (resid == null || resid.trim().equals("")) ? "0" : resid;
						//应用
						//mStatistics.add(HStatistics.Z12_4, resid, "", "");
						mHStatistics.add(HStatistics.Z12_1, resid, (charge == 0) ? "0" : "1", "1");
						HLog.e(TAG, "Apply, id:" + resid);
						SkinManage.mCurrentSkin = c.getString(c.getColumnIndex(HResDatabaseHelper.PACKAGENAME));
					}
					c.close();
					mChangeSkinHandler.sendEmptyMessage(0);
				}
			}.start();
			/*
			if(mView != null)
			{
				mView.removeAllViews();
			}
			mView = (SkinLinearLayout)SkinLinearLayout.inflate(mContext, R.layout.res_mine, null);
			setContentView(mView);*/
			//initView();
		}
	}
	
	private ProgressDialog mProgressDialog;
	
	private Handler mChangeSkinHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			initInstalled();
			initView();
			if(null != mProgressDialog)
			{
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			Toast.makeText(mContext, getString(R.string.skin_changed), Toast.LENGTH_SHORT).show();
		}
	};
	
	/* new
	private class OnInstallListener implements OnClickListener
	{
		private long mID;
		
		public OnInstallListener(long id) {
			mID = id;
		}
		@Override
		public void onClick(View v) {
			//安装
			// TODO Auto-generated method stub
			HStatistics mHStatistics = new HStatistics(HResMineActivity.this);
			mHStatistics.add(HStatistics.Z10_5, "", "", "");
			if(mID <= 0)
			{
				return;
			}
			DLData task = null;
			for(DLData d : mTasksDownloaded)
			{
				if(d.getId() == mID)
				{
					task = d;
				}
			}
			if(null == task)
			{
				return;
			}
			HStatistics mStatistics = new HStatistics(HResMineActivity.this);
			String resid = (task.getResID() == null || task.getResID().trim().equals("")) ? "0" : task.getResID();
			mStatistics.add(HStatistics.Z12_2, resid, "", "");
			final DLData tas = task;
			int charge = 0;
			if(task.getCharge() != null && !"".equals(task.getCharge())) {
				charge = Integer.valueOf(task.getCharge());
			}
			String message = "";
//			if(charge == 1 || charge == -1)
//			{	
//				message = String.format(mContext.getString(R.string.install_charge), task.getChargeMsg());
//			} else
			if(charge == 1 || charge == -1)
			{
				message = String.format(mContext.getString(R.string.install_paid), task.getDisplayName());
			}
			else
			{
				message = String.format(mContext.getString(R.string.install_free), task.getDisplayName());
			}
			if(charge != 0)
			{
				if(ToolsUtil.IM_FLAG)
				{
					SharedPreferences pref = mContext.getSharedPreferences(HConst.PREF_USER, Context.MODE_PRIVATE);
					boolean login = pref.getBoolean(HConst.USER_KEY_LOGIN, false);
					if(!login)
					{
						Toast.makeText(mContext, getString(R.string.login_toast), Toast.LENGTH_LONG).show();
						Intent i = new Intent(mContext, HResLoginActivity.class);
						startActivity(i);
						return;
					}
				}
			}
			if(charge == 1)
			{
				if(!ToolsUtil.readSIMCard(mContext))
				{
					mConfirmDialog = new AlertDialog.Builder(mContext)
					.setTitle(mContext.getString(R.string.notice))
					.setMessage(mContext.getString(R.string.simInvalid))
					.setPositiveButton(mContext.getString(R.string.confirm), new DialogInterface.OnClickListener() {
							
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if(null != mConfirmDialog)
							{
								mConfirmDialog.dismiss();
								mConfirmDialog = null;
							}
						}
					})
					.create();
					mConfirmDialog.show();
					return;
				}
				else if(!ToolsUtil.checkNet(mContext))
				{
					mConfirmDialog = new AlertDialog.Builder(mContext)
					.setTitle(mContext.getString(R.string.notice))
					.setMessage(mContext.getString(R.string.no_connect))
					.setPositiveButton(mContext.getString(R.string.confirm), new DialogInterface.OnClickListener() {
							
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if(null != mConfirmDialog)
							{
								mConfirmDialog.dismiss();
								mConfirmDialog = null;
							}
						}
					})
					.create();
					mConfirmDialog.show();
					return;
				}
			}
			//else
			{
				final int cha = charge;
				mConfirmDialog = new AlertDialog.Builder(mContext)
				.setTitle(mContext.getString(R.string.confirm_install))
				.setMessage(message)
				.setPositiveButton(mContext.getString(R.string.confirm), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if(cha == 1)
						{
//							HStatistics mHStatistics = new HStatistics(HResMineActivity.this);
//							mHStatistics.add(HStatistics.Z11_1, "", "", "");
						}
						else
						{
							HStatistics mHStatistics = new HStatistics(HResMineActivity.this);
							mHStatistics.add(HStatistics.Z10_5_1, "", "", "");
						}
						
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setDataAndType(Uri.fromFile(new File(DLManager.LOCAL_PATH + File.separator + tas.getFileName())),
								"application/vnd.android.package-archive");
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
						mContext.startActivity(intent);
						if(null != mConfirmDialog)
						{
							mConfirmDialog.dismiss();
							mConfirmDialog = null;
						}
					}
				})
				.setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if(cha == 1)
						{
							HStatistics mHStatistics = new HStatistics(HResMineActivity.this);
//							mHStatistics.add(HStatistics.Z11_2, "", "", "");
						}
						else
						{
							HStatistics mHStatistics = new HStatistics(HResMineActivity.this);
							mHStatistics.add(HStatistics.Z10_5_2, "", "", "");
						}
						
						if(null != mConfirmDialog)
						{
							mConfirmDialog.dismiss();
							mConfirmDialog = null;
						}
					}
				})
				.create();
				mConfirmDialog.show();
			}
		}
		
	}
	*/
	/* new
	private class OnStateListener implements OnClickListener {
		private long mID;
		
		public OnStateListener(long id) {
			mID = id;
		}

		@Override
		public void onClick(View v) {
			
			if(mID <= 0)
			{
				return;
			}
			DLData task = null;
			for(DLData d : mTasksDownloading)
			{
				if(d.getId() == mID)
				{
					task = d;
				}
			}
			if(null == task)
			{
				return;
			}
			if(DLManager.STATUS_RUNNING == task.getStatus())
			{
				mDLManager.pauseTask(task.getId());
				return;
			}
			if(DLManager.STATUS_PAUSE == task.getStatus())
			{
				mDLManager.restartTask(task.getId());
				return;
			}
			if(DLManager.STATUS_READY == task.getStatus())
			{
				mDLManager.pauseTask(task.getId());
				return;
			}
			if(DLManager.STATUS_SUCCESS == task.getStatus())
			{
				return;
			}
		}
	}
	*/
	/* new
	private Handler mProgressHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			DLData task = (DLData) bundle.getSerializable("task");
			switch(msg.what)
			{
				case DLManager.ACTION_UPDATE:
				{
					if(task != null)
					{
						//if(task.getStatus() == DLManager.STATUS_SUCCESS)
						//{
						//	View view = mViewMapDownloading.remove(String.valueOf(task.getId()));
						//	mListDownloading.removeView(view);
						//	mTasksDownloading.remove(task);
						//	mTasksDownloaded.add(task);
						//	View vi = mInflater.inflate(R.layout.res_downloaded_item, null);
						//	updateDownloaded(vi, task);
						//	mViewMapDownloaded.put(String.valueOf(task.getId()), vi);
						//	mListDownloaded.addView(vi);
						//	updateCount();
						//}
						if(task.getStatus() != DLManager.STATUS_SUCCESS)
						{
						View view = mViewMapDownloading.get(String.valueOf(task.getId()));
						if(view != null)
						{
							updateDownloading(view, task);
						}}
					}
				}
				break;
				case DLManager.ACTION_SUCCESS:
				{
					if(task != null)
					{
							View view = mViewMapDownloading.remove(String.valueOf(task.getId()));
							mListDownloading.removeView(view);
							mTasksDownloading.remove(task);
							mTasksDownloaded.add(task);
							View vi = mInflater.inflate(R.layout.res_downloaded_item, null);
							updateDownloaded(vi, task);
							mViewMapDownloaded.put(String.valueOf(task.getId()), vi);
							mListDownloaded.addView(vi);
							updateCount();
					}
				}
				break;
				case DLManager.ACTION_DELETE:
				{
					if(task != null)
					{
						if(task.getStatus() == DLManager.STATUS_SUCCESS)
						{
							View view = mViewMapDownloaded.remove(String.valueOf(task.getId()));
							mListDownloaded.removeView(view);
							mTasksDownloaded.remove(task);
						}
						else
						{
							View view = mViewMapDownloading.remove(String.valueOf(task.getId()));
							mListDownloading.removeView(view);
							mTasksDownloading.remove(task);
						}
					}
					updateCount();
				}
				break;
			}
		}
	};
	*/
	public void setIcon(View iv_icon, DLData data)
	{
		Bitmap bitmap = mIconMap.get(String.valueOf(data.getId()));
		if(bitmap != null)
		{
			iv_icon.setBackgroundDrawable(new BitmapDrawable(
					bitmap));
		}
		else
		{
			String iconpath = mContext.getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + data.getIconUrl();
			File f = new File(iconpath);
			if(f.exists())
			{
				bitmap = BitmapFactory.decodeFile(iconpath);
				System.gc();
				if (bitmap != null) {
					iv_icon.setBackgroundDrawable(new BitmapDrawable(
							bitmap));
					mIconMap.put(String.valueOf(data.getId()), bitmap);
				}
				else
				{
					iv_icon.setBackgroundResource(R.drawable.shop_skin);
				}
			}
			else
			{
				try
				{
					f.createNewFile();
					String cache = mContext.getCacheDir() + "/res/" + data.getIconUrl();
					File fca = new File(cache);
					if(fca.exists())
					{
						InputStream is = new FileInputStream(cache);
						FileOutputStream os = new FileOutputStream(iconpath);
						byte[] buf = new byte[1024];
						int read = -1;
						while((read = is.read(buf)) > 0)
						{
							os.write(buf, 0, read);
						}
						is.close();
						os.close();
						bitmap = BitmapFactory.decodeFile(iconpath);
						System.gc();
						if (bitmap != null) {
							iv_icon.setBackgroundDrawable(new BitmapDrawable(
									bitmap));
						}
						else
						{
							iv_icon.setBackgroundResource(R.drawable.shop_skin);
						}
					}
					else
					{
						iv_icon.setBackgroundResource(R.drawable.shop_skin);
					}
				}
				catch(Exception e)
				{
					iv_icon.setBackgroundResource(R.drawable.shop_skin);
				}
			}
		}
	}
}