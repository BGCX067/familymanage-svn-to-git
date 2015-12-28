package com.haolianluo.sms2.ui.sms2;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.model.HMoldPay;
import com.haolianluo.sms2.model.HResDatabaseHelper;
import com.haolianluo.sms2.model.HResLibInfoParser;
import com.haolianluo.sms2.model.HResLibModel;
import com.haolianluo.sms2.model.HResProvider;
import com.haolianluo.sms2.model.HStatistics;
import com.haolianluo.sms2.model.PayBussiness;
import com.lianluo.core.cache.CacheManager;
import com.lianluo.core.cache.ResourceFileCache;
import com.lianluo.core.net.download.DLData;
import com.lianluo.core.net.download.DLManager;
import com.lianluo.core.skin.SkinManage;
import com.lianluo.core.util.HLog;
import com.lianluo.core.util.ImageUtil;
import com.lianluo.core.util.ToolsUtil;
import com.haolianluo.sms2.R;

public class HResDetailActivity extends HActivity
{
	private static final String TAG = "HResDetailActivity";
	private static final int UPDATEUI = 9 ;
	private ImageView back;		//顶部返回按钮
	
	private TextView templateName;	//模板名称
	
	private ImageView templateIcon;		//模板图标
	private Handler iconHandler;	//下载图标handler
	
	private TextView templateName2;		//模板名称
	
	//private TextView templateSize;		//模板大小
	//private static final int UNIT = 1024 * 1024;
	public static final int ACTION_UPDATE = 0;
	public static final int ACTION_DELETE = 1;
	Dialog mConfirmDialog;
	protected Dialog waitDialog = null;
	/**状态
	 * 1.下载
	 * 2.点击下载后不可见状态
	 * 3.下载完毕，安装
	 * 4.安装完毕，已安装
	 * */
	private Button templateStatus;				
	
	/**下载进度条*/
	private ProgressBar mProgressBar;
	
	private List<DLData> mTasksDownloading = new ArrayList<DLData>();
	//更新UI
	
	/**下载进度百分比显示*/
	private TextView mProgressPercent;
	
	/**下载区域layout*/
	private LinearLayout mProgressLayout;

	/**取消按钮*/
	private ImageView mCancelButton;
	
	private ImageView mDetail_pre_1;
	private ImageView mDetail_pre_2;
	private ImageView mDetail_pre_3;
	
	private LinearLayout radio_index;
	
	//private boolean pre_isShowing = false;
	
	/**模板的三张预览图*/
//	private Bitmap[] bitmapPre = new Bitmap[3];
	private Bitmap[] bitmapPre;
	
	private String[] picPath = new String[3];
	private Handler zipHandler;
	
	/**下载次数，星级，更新时间*/
	private TextView downTimes;
	private RatingBar starLevel;
	private TextView showDate;
	private TextView detailCharge;
	
	/**模板说明简介*/
	private TextView templateDesc;
	
	/**用户还浏览了一下其他模板区域*/
	private LinearLayout othershow_layoutL;
	private ImageView otherIconL;
	private TextView otherNameL;
	//private TextView otherSizeL;
	private RatingBar otherstarLevelL;
	private TextView otherchargestatusL;
	@SuppressWarnings("unused")
	private String l_ID;
	
	
	private LinearLayout othershow_layoutR;
	private ImageView otherIconR;
	private TextView otherNameR;
	//private TextView otherSizeR;
	private RatingBar otherstarLevelR;
	private TextView otherchargestatusR;
	
	@SuppressWarnings("unused")
	private String r_ID;
	
	private Context mContext;
	
	/**保存进度条的进度值，达到最大的时候，进度条隐藏*/
	private String downTimesFromXml;
	
	private String iconFileName;
	private String tjIconFileNameL;
	private String tjIconFileNameR;
	
	
	/**传过来的模板ID*/
	private String template_id;
	private String templateAddress_P;
	/**模板详情*/
	private HResLibModel info = null;
	private int tjSizeInfo = 0;
//	private Handler infoHandler;
	private DLManager dlManager;
	private long taskId;
	private DLData task;
	
	private Handler barHandler;
	HResLibModel data = null;
	
	public static final int INFO_SUCC = 0;
	public static final int INFO_FAIL = 1;
	public static final int INFO_NONET = 2;
	public static final int INFO_NOREF = 3;
	
	private HStatistics mStatistics;
	
	private String infoType = "";
	//private String yuan = "";
	
	private HMoldPay hmdPay = null;
	
	private Gallery gallery;
	private LinearLayout waitBarLayout;
	
	/***
	 * 0.详情页等待
	 * 1.获取付费信息等待
	 */
	private int dialogCa = 0;
	
	private String yuan = "";
	
	private PayBussiness payBussiness;
	
	/**是否发生订购行为，如果订购过，则返回到列表进行刷新，如果无，则不刷新*/
	private boolean isPayED = false;
	/***
	 * 下载按钮事件/显示逻辑如下：
	 * 1.如果模版付费字段为：0，则按钮显示为“免费”； 如果模版付费字段为：1/-1，那么按钮显示的字段和模版金额字段相同（如：2元、限免）；
	 * 2.如果模版付费字段为：0/-1，则不会显示付费信息弹框提示；如果是1，则会显示付费信息框提示；
	 */
//	public void onCreate(Bundle savedInstanceState)
//	{
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.res_detail);
//		dlManager = DLManager.getInstance(HResDetailActivity.this);
//		dlManager.setProgressHandler(downHandler);
//		infoHandler = new Handler(){
//
//			@Override
//			public void handleMessage(Message msg) {
//				info = (HResLibModel) msg.obj;
//				if(info != null) {
//					if (mTasksDownloading.size() == 0) {
//						mTasksDownloading = dlManager.getAllTasks();
//					}
//					
//					initEvent();
//					initInfo();
//					//要把分散的几个缩略图放进下载任务中，缩略图+预览图zip文件
//					downPic();
//				}
//			}
//			
//		};
//	}
	
	private void initView()
	{
		
		back = (ImageView)findViewById(R.id.res_title_icon);
		
		back.setOnClickListener(pre_clickListener);
		
		templateName = (TextView)findViewById(R.id.res_title_text);
		templateName.setText(data.getPn());
		
		templateIcon = (ImageView) findViewById(R.id.res_detail_icon);
		templateName2 = (TextView) findViewById(R.id.res_detail_name);
		//templateSize = (TextView) findViewById(R.id.res_detail_size);
		
		templateName2.setText(data.getPn());
		//templateSize.setText(KBToMB(data.getFs()));
		
		detailCharge = (TextView) findViewById(R.id.res_detail_chargestatus);
		if(data.getPl().equals("0"))
		{
			detailCharge.setText(getString(R.string.res_text_free));
		}else
		{
			detailCharge.setText(data.getPr());
		}
		
		mProgressLayout = (LinearLayout) findViewById(R.id.res_detail_progress_layout);
		templateStatus = (Button) findViewById(R.id.res_detail_install);
		templateStatus.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				String text = templateStatus.getText().toString();
				if(text == null || text.equals(getString(R.string.skin_bt_useing)))
				{
					return false;
				}
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					templateStatus.setBackgroundResource(R.drawable.res_selected);
					break;
				default :
					if(text.equals(getString(R.string.res_text_free)) || text.equals(info.getPr()))
//					if(text.equals(getString(R.string.spopu_bt_dl)))
					{
						//免费和限免和金额
						templateStatus.setBackgroundResource(R.drawable.res_de_down);
					}
//					else if(text.equals(getString(R.string.install)))
//					{
//						//安装
//						templateStatus.setBackgroundResource(R.drawable.res_install_new);
//					}
					else if(text.equals(getString(R.string.res_check)))
					{
						//查看 
						templateStatus.setBackgroundResource(R.drawable.res_de_down);
					}
					else if(text.equals(getString(R.string.skin_bt_use)))
					{
						templateStatus.setBackgroundResource(R.drawable.res_install_new);
					}
					break;
				}
				return false;
			}
		});
		mProgressBar = (ProgressBar) findViewById(R.id.res_detail_progressbar_new);
		
		
		
		mProgressPercent = (TextView) findViewById(R.id.res_detail_progresspercent);
		mCancelButton = (ImageView) findViewById(R.id.res_detail_start_pause_down);
		mCancelButton.setBackgroundResource(R.drawable.res_de_cancel);
		
		
		mDetail_pre_1 = (ImageView) findViewById(R.id.res_detail_pre_1);
		mDetail_pre_2 = (ImageView) findViewById(R.id.res_detail_pre_2);
		mDetail_pre_3 = (ImageView) findViewById(R.id.res_detail_pre_3);
		
//		mDetail_pre_1.setOnClickListener(pre_clickListener);
//		mDetail_pre_2.setOnClickListener(pre_clickListener);
//		mDetail_pre_3.setOnClickListener(pre_clickListener);
		
		waitBarLayout = (LinearLayout) findViewById(R.id.res_detail_waitbarlayout);
		gallery = (Gallery) findViewById(R.id.gallery);
		radio_index = (LinearLayout) findViewById(R.id.res_detail_radio_layout);
		
		downTimes = (TextView) findViewById(R.id.res_detail_downtime);
		downTimes.setText(downTimesFromXml + " " + data.getMd());
		
		starLevel = (RatingBar) findViewById(R.id.res_detail_starlevel);
		starLevel.setRating(Long.parseLong(data.getEt()));
		
		showDate = (TextView) findViewById(R.id.res_detail_showdate);
		
		templateDesc = (TextView) findViewById(R.id.res_detail_des);
		
		othershow_layoutL = (LinearLayout) findViewById(R.id.res_detail_othershow_layout);
		otherIconL = (ImageView) findViewById(R.id.res_detail_othericon);
		otherNameL = (TextView) findViewById(R.id.res_detail_othername);
		//otherSizeL = (TextView) findViewById(R.id.res_detail_othersize);
		otherstarLevelL = (RatingBar) findViewById(R.id.res_detail_otherstarlevel);
		otherchargestatusL = (TextView) findViewById(R.id.res_detail_otherchargestatus);
		
		
		othershow_layoutR = (LinearLayout) findViewById(R.id.res_detail_othershow_rigth_layout);
		otherIconR = (ImageView) findViewById(R.id.res_detail_right_othericon);
		
		otherNameR = (TextView) findViewById(R.id.res_detail_right_othername);
		//otherSizeR = (TextView) findViewById(R.id.res_detail_right_othersize);
		otherstarLevelR = (RatingBar) findViewById(R.id.res_detail_rightstarlevel);
		otherchargestatusR = (TextView) findViewById(R.id.res_detail_rightchargestatus);
		
	}

	private OnClickListener pre_clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
//			if(!pre_isShowing)
//			{
//				return;
//			}
			switch (v.getId()) {
//			case R.id.res_detail_pre_1:
//				startActivity(new Intent(HResDetailActivity.this, ImagePreviewActivity.class)
//					.putExtra("path", picPath[0]).putExtra("path_pre", picPath[2]).putExtra("path_next", picPath[1]));
//				break;
//			case R.id.res_detail_pre_2:
//				startActivity(new Intent(HResDetailActivity.this, ImagePreviewActivity.class)
//					.putExtra("path", picPath[1]).putExtra("path_pre", picPath[0]).putExtra("path_next", picPath[2]));
//				break;
//			case R.id.res_detail_pre_3:
//				startActivity(new Intent(HResDetailActivity.this, ImagePreviewActivity.class)
//					.putExtra("path", picPath[2]).putExtra("path_pre", picPath[1]).putExtra("path_next", picPath[0]));
//				break;
			case R.id.res_title_icon:
				Intent intent = new Intent(HResDetailActivity.this, HResLibActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
				startActivity(intent);
				finish();
				break;
			}
		}
	}; 
	
	private Handler downHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			
			
			Bundle bundle = msg.getData();
			if(bundle == null)
			{
				return;
			}
			DLData resultData = (DLData) bundle.getSerializable("task");
			if(resultData == null)
			{
				return;
			}
			/**保证下载返回显示的是之前点击的模板*/
			if(!template_id.endsWith(resultData.getResID()))
			{
				return;
			}
			
			if(msg.what == ACTION_UPDATE)
			{	
				if(resultData.getTotalSize() == 0)
				{
					return;
				}
				
				switch(resultData.getStatus())
				{
					case DLManager.STATUS_RUNNING:
					{
						DecimalFormat df = new DecimalFormat();
				        df.setMaximumFractionDigits(1);
				        df.setMinimumFractionDigits(1);
				        float progress = 0.0f;
				        if(resultData.getCurrentSize() > 0 && resultData.getTotalSize() > 0)
				        {
				        	progress = (float)resultData.getCurrentSize() * 100.0f / resultData.getTotalSize();
				        }
				        String p = df.format(progress);
				        mProgressPercent.setText(p + "%");
						mProgressBar.setMax(resultData.getTotalSize());
						mProgressBar.setProgress(resultData.getCurrentSize());
						
						if(resultData.getCurrentSize() > 0 && resultData.getCurrentSize() == resultData.getTotalSize())
						{
							//下载完成,将按钮显示并且设置文字为：应用, 并且设置下载layout不可见
							mProgressLayout.setVisibility(View.GONE);
							//templateStatus.setText(getString(R.string.install));
							templateStatus.setText(getString(R.string.skin_bt_use));
							templateStatus.setBackgroundResource(R.drawable.res_install_new);
							templateStatus.setVisibility(View.VISIBLE);
						}
					}
					break;
					case DLManager.STATUS_SUCCESS:
					{
						//下载完成,将按钮显示并且设置文字为：安装, 并且设置下载layout不可见
						mProgressLayout.setVisibility(View.GONE);
						//templateStatus.setText(getString(R.string.install));
						templateStatus.setText(getString(R.string.skin_bt_use));
						templateStatus.setBackgroundResource(R.drawable.res_install_new);
						templateStatus.setVisibility(View.VISIBLE);
						templateStatus.setEnabled(true);
						
						//更新下载次数显示
						if(info != null)
						{	
							downTimes.setText(getString(R.string.download_times) + " " + (Integer.parseInt(info.getMd().trim()) + 1));
						}
						task = resultData;
					}
					break;
					case DLManager.STATUS_FAILED:
					{
						mProgressLayout.setVisibility(View.GONE);
						dlManager.deleteTask(taskId);
						cancelDownload();
					}
					break;
					
				}
			}else if(msg.what == ACTION_DELETE)
			{
				if(resultData.getStatus() == DLManager.STATUS_SUCCESS)
				{
//					//安装完成并且应用，设置按钮文字为：已应用
//					templateStatus.setVisibility(View.VISIBLE);
//					templateStatus.setBackgroundResource(R.drawable.res_installed);
//					
//					templateStatus.setText(mContext.getString(R.string.skin_bt_useing));
//					templateStatus.setEnabled(false);
					//templateStatus.setText(mContext.getString(R.string.res_ins));
					
					//查看
					templateStatus.setText(getText(R.string.res_check));
					templateStatus.setBackgroundResource(R.drawable.res_de_down);
				}
				else
				{
					mProgressLayout.setVisibility(View.GONE);
					dlManager.deleteTask(taskId);
					cancelDownload();
				}
			}
		}
	};
	
	private void cancelDownload()
	{
		//取消下载
		templateStatus.setVisibility(View.VISIBLE);
		templateStatus.setBackgroundResource(R.drawable.res_de_down);
		templateStatus.setText(mContext.getString(R.string.spopu_bt_dl));
		
		
		templateStatus.setEnabled(true);
//		templateStatus.setBackgroundResource(R.drawable.res_de_down);
		
		String isPay = info.getPl();
		if (isPay.equals("0")) {
			templateStatus.setText(mContext
					.getText(R.string.res_text_free));
		}

		String yuan = "";
		if (isPay.equals("1")) {
			yuan = info.getPr();
			templateStatus.setText(yuan);
		}
		
		if(isPay.equals("-1"))
		{
			templateStatus.setText(getString(R.string.res_payed));
		}
	}
	
	/**包月中*/
	private static final String MONTH_RATE_ING = "0";
	/**包月已过期*/
	private static final String MONTH_RATE_OUT = "-1";
	/**没有包月记录*/
	private static final String MONTH_RATE_NORECORDE = "-2";
	
	public void isLogin(String buttonText)
	{
			//如果按钮显示为免费，不论按钮显示什么，付费状态为何值，都直接进行下载
			String month_by = info.getBy();
			System.out.println("month_by " + month_by);
			if(info.getPr().equals(getString(R.string.res_text_free)))
			{
				addTaskToDownManager();
				return;
			}
			//免费,限免,n元
			boolean isFree = buttonText.equals(getString(R.string.res_text_free)) || buttonText.equals(info.getPr());
			//boolean isFree = buttonText.equals(getString(R.string.spopu_bt_dl));
			/**
			 * 如果是付费(或者已付费)模板，需要判断当前用户是否登录，
			 * 如果没有登录则，跳转到登录界面
			 * 如果已经登录，则继续进行下载动作，
			 * 
			 * 如果是免费模板，则进行下载动作。
			 */
			if(!info.getPl().equals("0"))
			{	
				/**toolUtil,IM_FLAG true是现有的正常流程，false为不需要用户部分*/
				if(ToolsUtil.IM_FLAG)
				{
					//模板为非免费的
					boolean isLogin = false;
					
					SharedPreferences pref = getSharedPreferences(HConst.PREF_USER, Context.MODE_PRIVATE);
					isLogin = pref.getBoolean(HConst.USER_KEY_LOGIN, false);
					HLog.d("isLogin", String.valueOf(isLogin));
					if(!isLogin)
					{
						if(isFree)
						{	
							mStatistics.add(HStatistics.Z10_3, info.getMi(), "", "");
						}
						//如果没有登录,需要跳转到登录界面
						Toast.makeText(mContext, getString(R.string.login_toast), Toast.LENGTH_SHORT).show();
						startActivity(new Intent(HResDetailActivity.this,HResLoginActivity.class));
						return;
					}else if(info.getPl().equals("1") && isFree)
					{
						if(dialog_isShowing()){return;};
						showDialog("1");
						//已登陆，进行付费动作
						new Thread()
						{
							@Override
							public void run() {
								Looper.prepare();
								if(!ToolsUtil.MM_FLAG)
								{	
									//原始正常流程
									hmdPay = payBussiness.applyPay(HResDetailActivity.this, info.getMi(), ToolsUtil.getPhoneNum(mContext));
									HLog.d("paidInfo", "ID: " + info.getMi() + ", phone: " + ToolsUtil.getPhoneNum(mContext));
									payHandler.sendEmptyMessage(0);
								}else if(ToolsUtil.MM_FLAG)
								{
									dimissDialog();
									//MM商城流程, 直接弹出支付框
									payBussiness.startPay(HResDetailActivity.this, info.getMi(), ToolsUtil.getPhoneNum(HResDetailActivity.this), false);
									
								}
								Looper.loop();
							}
						}.start();
					}else if(info.getPl().equals("-1") && isFree)
					{
						addTaskToDownManager();
					}
//					else if(buttonText.equals(getString(R.string.install)))
//					{
//						installClick(task);
//					}
					
					if(isFree)
					{
						mStatistics.add(HStatistics.Z10_3, info.getMi(), "", "");
					}
				}
				
				else if(info.getPl().equals("1") && buttonText.equals(info.getPr()))
//				else if(info.getPl().equals("1") && buttonText.equals(getString(R.string.spopu_bt_dl)))
				{
					if(dialog_isShowing()){return;};
					showDialog("1");
					//如果无用户部分，是付费模版就直接进行是否付费动作
					new Thread()
					{
						@Override
						public void run() {
							Looper.prepare();
							if(!ToolsUtil.MM_FLAG)
							{
								//原始正常流程
								hmdPay = payBussiness.applyPay(HResDetailActivity.this, info.getMi(), ToolsUtil.getPhoneNum(mContext));
								HLog.d("paidInfo", "ID: " + info.getMi() + ", phone: " + ToolsUtil.getPhoneNum(mContext));
								payHandler.sendEmptyMessage(0);
							}else if(ToolsUtil.MM_FLAG)
							{
								dimissDialog();
								//MM商城流程, 直接弹出支付框
								payBussiness.startPay(HResDetailActivity.this, info.getMi(), ToolsUtil.getPhoneNum(HResDetailActivity.this), false);
							}
							Looper.loop();
						}
					}.start();
				}
				else if(info.getPl().equals("-1"))
//				else if(info.getPl().equals("-1") && buttonText.equals(getString(R.string.spopu_bt_dl)))
				{
					addTaskToDownManager();
				}
//				else if(buttonText.equals(getString(R.string.install)))
//				{
//					installClick(task);
//				}
		}
			
		else if(buttonText.equals(getString(R.string.res_text_free)))
//		else if(buttonText.equals(getString(R.string.spopu_bt_dl)))
		{
			//免费下载
			addTaskToDownManager();
		}
		
//		else if(buttonText.equals(getString(R.string.install)))
//		{
//			installClick(task);
//		}
			
			if(isFree)
			{
				mStatistics.add(HStatistics.Z10_3, info.getMi(), "", "");
			}
	}
	
	/***
	 * 应用模版
	 */
	private void useskin()
	{
		ContentValues cvalues = new ContentValues();
		cvalues.put(HResDatabaseHelper.RES_USE, 0);
		mContext.getContentResolver().update(
				HResProvider.CONTENT_URI_SKIN, cvalues, null, null);
		ContentValues values = new ContentValues();
		values.put(HResDatabaseHelper.RES_USE, 1);
		mContext.getContentResolver().update(
				HResProvider.CONTENT_URI_SKIN,
				values,
				HResDatabaseHelper.PACKAGENAME + " = '"
						+ info.getPkn() + "'", null);
		Cursor c = mContext.getContentResolver().query(
				HResProvider.CONTENT_URI_SKIN, null,
				HResDatabaseHelper.RES_USE + " = '1'", null, null);
		if (c.getCount() > 0) {
			c.moveToNext();
//			HStatistics mHStatistics = new HStatistics(mContext);
//			int charge = c.getInt(c
//					.getColumnIndex(HResDatabaseHelper.CHARGE));
//			String resid = c.getString(c
//					.getColumnIndex(HResDatabaseHelper.RES_ID));
//			resid = (resid == null || resid.trim().equals("")) ? "0"
//					: resid;
//			mHStatistics.add(HStatistics.Z12_1, resid,(charge == 0) ? "0" : "1", "1");
//			HLog.e(TAG, "Apply, id:" + resid);
			SkinManage.mCurrentSkin = c.getString(c
					.getColumnIndex(HResDatabaseHelper.PACKAGENAME));
			SkinManage.mCurrentFile = c.getString(c
					.getColumnIndex(HResDatabaseHelper.FILE_NAME));
		}
		c.close();
		
		Toast.makeText(
				HResDetailActivity.this,
				info.getPn()
						+ getString(R.string.skin_bt_useing),
				Toast.LENGTH_LONG).show();
		
		//Toast.makeText(mContext, getString(R.string.skin_changed), Toast.LENGTH_SHORT).show();
	}
	
	private void initEvent()
	{
		//按钮点击
		templateStatus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try
					{
					Button btn = (Button) v;
					String btnContent = btn.getText().toString();
					//下载,免费和限免，都进行下载
					if(btnContent.equals(getString(R.string.res_text_free)) || btnContent.equals(info.getPr()))
//					if(btnContent.equals(getString(R.string.spopu_bt_dl)))
					{	
						//确认付费之后才进行下载动作
						isLogin(btn.getText().toString());
					}
//					else if(btnContent.equals(getString(R.string.install)))
//					{
//						//安装
//						isLogin(btn.getText().toString());
//						mStatistics.add(HStatistics.Z10_5, info.getMi(), "", "");
//					}
//					else if(btnContent.equals(getString(R.string.skin_bt_use)))
//					{
//						//应用
//						isLogin(btn.getText().toString());
//						mStatistics.add(HStatistics.Z10_5, info.getMi(), "", "");
//					}
					else if(btnContent.equals(getString(R.string.res_check)))
					{
						//模板应用查看数
						mStatistics.add(HStatistics.Z10_7, info.getMi(), "", "");
						
						//HResDetailActivity.this.setResult(RESULT_OK, new Intent().putExtra("check", "check"));
						HConst.DETAIL_BACK_TYPE = HConst.TYPE_CHECK;
						HResDetailActivity.this.finish();
					}
					else if(btnContent.equals(getString(R.string.skin_bt_use)))
					{
						//应用
//						templateStatus.setText(mContext.getText(R.string.skin_bt_useing));
//						templateStatus.setBackgroundResource(R.drawable.res_installed);
//						templateStatus.setEnabled(false);
						
						//模板应用点击数
						mStatistics.add(HStatistics.Z10_6, info.getMi(), "", "");
						
						installClick(task);
						//useskin();
						
						//查看
						templateStatus.setEnabled(true);
						templateStatus.setText(getText(R.string.res_check));
						templateStatus.setBackgroundResource(R.drawable.res_de_down);
						
						//设置更换模版的时间
						HConst.setTheCheckTemTime(mContext);
					}
				} catch (Exception e) {
				e.printStackTrace();
			}
			}
		});
		
		
		iconHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case ResourceFileCache.RESOURCE_ERROR:
					//获取缩略图失败
					break;
				case ResourceFileCache.RESOURCE_SUCCESS:
				{
					String iconFileNameNow = msg.getData().getString(ResourceFileCache.RESOURCE_NAME);
					HLog.d("HResDetailActivity icon name: ", "down name:" + iconFileName);
					initPic(iconFileNameNow);
				}
					break;
				default:
					break;
				}
			}
		};
		
		otherIconL.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(tjSizeInfo >= 1)
				{
					HResLibModel infoL = info.getTj().get(0);
					infoL.setMd("0");
					finish();
					startActivity(new Intent().setClass(HResDetailActivity.this, HResDetailActivity.class).putExtra("HRes", infoL).putExtra("type", infoL.getPl()));
				}
			}
		});
		
		otherIconR.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(tjSizeInfo >= 1)
				{
					
					HResLibModel infoR = info.getTj().get(1);
					infoR.setMd("0");
					finish();
					startActivity(new Intent().setClass(HResDetailActivity.this, HResDetailActivity.class).putExtra("HRes", infoR).putExtra("type", infoR.getPl()));
				}
			}
		});
	
		zipHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case ResourceFileCache.RESOURCE_ERROR:
					// 获取预览图失败
					//Toast.makeText(HResDetailActivity.this, "下载缩略图失败", Toast.LENGTH_SHORT).show();
					break;
				case ResourceFileCache.RESOURCE_SUCCESS:
					String mZipFileName = msg.getData().getString(ResourceFileCache.RESOURCE_NAME);
					HLog.d(TAG, "down name:" + mZipFileName);

					/**解压zip包，并且显示*/
					skinZipDownLoadSuccess(mZipFileName);
					break;
				case UPDATEUI:
					/**显示预览图*/
					if(bitmapPre.length != 0)
					{
						waitBarLayout.setVisibility(View.GONE);
						gallery.setVisibility(View.VISIBLE);
						gallery.setAdapter(new ImageAdapter(HResDetailActivity.this, bitmapPre));
						
						radio_index.setVisibility(View.VISIBLE);
					}	
					
					//dimissDialog();
					
					break;
				}

			}

			private void skinZipDownLoadSuccess(final String mZipFileName) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							try {
								ToolsUtil.unzip(CacheManager.CACHE_RESOURCE, mZipFileName);
								
								String dirName = mZipFileName.substring(0, mZipFileName.indexOf("."));
								File imgDir = new File(CacheManager.CACHE_RESOURCE + dirName);
								if (imgDir.exists()) {
									File[] files = imgDir.listFiles();
									for (int i = 0; i < 3; i++) {
										
										HLog.d("path" + i, files[i].getPath());
//										Bitmap bt = decodeFile(files[i].getPath());
										Bitmap bt = BitmapFactory.decodeFile(files[i].getPath());
										
										String pngName = files[i].getName();
										String pngNameWithoutJpg = pngName.substring(0, 1);
										int indexS = Integer.parseInt(pngNameWithoutJpg);
										int index = indexS - 1;
										//int index = Integer.parseInt(files[i].getName().split(".")[0]) - 1;
										
										if (bt != null) {
											bitmapPre[index] = null;
											bitmapPre[index] = bt;
											picPath[index] = files[i].getPath();
											HLog.d("pic_index", index + "_" + files[i].getName());
										}
										
//										if (bt != null) {
//											bitmapPre[i] = null;
//											bitmapPre[i] = bt;
//											picPath[i] = files[i].getPath();
//										}
									}
									Message msg = new Message();
									msg.what = UPDATEUI;
									zipHandler.sendMessage(msg);
								}
							} catch(Exception ex) {
								ex.printStackTrace();
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}).start();
			}

		};
	}
	
	private void initPic(String iconFileNameNow) {
		Drawable iconDra = new BitmapDrawable(BitmapFactory.decodeFile(CacheManager.CACHE_RESOURCE + iconFileNameNow));
		if(iconDra != null)
		{
			if(iconFileNameNow.equals(iconFileName))
			{
				templateIcon.setBackgroundDrawable(iconDra);
			}else if(iconFileNameNow.equals(tjIconFileNameL))
			{
				otherIconL.setBackgroundDrawable(iconDra);
			}else if(iconFileNameNow.equals(tjIconFileNameR))
			{
				otherIconR.setBackgroundDrawable(iconDra);
			}
			
		}
	}
	
	

	private void initInfo()
	{

		//地址前缀
		if(info != null) {
			templateAddress_P = info.getP();
			/**初始化下载layout*/
			initProgressLayout(info.getMi());
			//利用包名来查看模板的状态
			initInstallButton(info.getPkn());
		}
		
		if(info == null)
		{
			return;
		}
		
		
		mCancelButton.setOnClickListener(new OnCancelListener());
		
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				setRadioIndex(position);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		/**判断缩略图是否有缓存
		 * 多语言对应的缩略图zip文件名不一致需要
		 */
		try {
			String dirName = info.getIn2().substring(0, info.getIn2().indexOf("."));
			File imgDir = new File(CacheManager.CACHE_RESOURCE + dirName);
			if(imgDir.exists()) {
				File[] files = imgDir.listFiles();
				for (int i = 0; i < 3; i++) {
					HLog.d("path" + i, files[i].getPath());
					HLog.d("png_name" + i, files[i].getName());
					
					//Bitmap bt = decodeFile(files[i].getPath());
					Bitmap bt = BitmapFactory.decodeFile(files[i].getPath());
					
					//取得缩略图名字
					String pngName = files[i].getName();
					String pngNameWithoutJpg = pngName.substring(0, 1);
					int indexS = Integer.parseInt(pngNameWithoutJpg);
					int index = indexS - 1;
					
					if (bt != null) {
						bitmapPre[index] = null;
						bitmapPre[index] = bt;
						picPath[index] = files[i].getPath();
						HLog.d("pic_index", index + "_" + files[i].getName());
					}
					
//					if (bt != null) {
//						bitmapPre[i] = null;
//						bitmapPre[i] = bt;
//						picPath[i] = files[i].getPath();
//					}
				}
				Message msg = new Message();
				msg.what = UPDATEUI;
				zipHandler.sendMessage(msg);
			} else {
				ImageUtil.addDownloadTask(mContext, templateAddress_P, info.getIn2(),zipHandler);
			}
		} catch(Exception ex) {
			ImageUtil.addDownloadTask(mContext, templateAddress_P, info.getIn2(),zipHandler);
			ex.printStackTrace();
		}
		
		
		templateName.setText(info.getPn());
		iconFileName = info.getIn();
		templateName2.setText(info.getPn());
		//templateSize.setText(KBToMB(info.getFs()));
		if(info.getPl().equals("0"))
		{
			detailCharge.setText(getString(R.string.res_text_free));
		}else
		{
			detailCharge.setText(info.getPr());
		}
		
		downTimes.setText(downTimesFromXml + " " + info.getMd());
		starLevel.setRating(Float.parseFloat(info.getEt()));
		showDate.setText(getString(R.string.show_date) + " " + info.getMt());
		
		//模板说明简介
		templateDesc.setText(info.getIo());
		int tjSize = info.getTj().size();
		tjSizeInfo = tjSize;
		if(tjSize >= 1)
		{
			l_ID = info.getTj().get(0).getMi();
			othershow_layoutL.setVisibility(View.VISIBLE);
			otherNameL.setText(info.getTj().get(0).getPn());
			//otherSizeL.setText(KBToMB(info.getTj().get(0).getFs()));
			otherstarLevelL.setRating(Float.parseFloat(info.getTj().get(0).getEt()));
			
			if(info.getTj().get(0).getPl().equals("0"))
			{
				otherchargestatusL.setText(getString(R.string.res_text_free));
			}else
			{
				otherchargestatusL.setText(info.getTj().get(0).getPr());
			}
			tjIconFileNameL = info.getTj().get(0).getIn();
		}
		
		if(tjSize >= 2)
		{
			r_ID = info.getTj().get(1).getMi();
			othershow_layoutR.setVisibility(View.VISIBLE);
			otherNameR.setText(info.getTj().get(1).getPn());
			//otherSizeR.setText(KBToMB(info.getTj().get(1).getFs()));
			otherstarLevelR.setRating(Float.parseFloat(info.getTj().get(1).getEt()));
			if(info.getTj().get(1).getPl().equals("0"))
			{
				otherchargestatusR.setText(getString(R.string.res_text_free));
			}else
			{
				otherchargestatusR.setText(info.getTj().get(1).getPr());
			}
			tjIconFileNameR = info.getTj().get(1).getIn();
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		HConst.markActivity = 9;
	}
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.res_detail);
		HConst.markActivity = 9;
		dlManager = DLManager.getInstance(HResDetailActivity.this);
		dlManager.setProgressHandler(downHandler);
		
		payBussiness = new PayBussiness();
		if(ToolsUtil.MM_FLAG)
		{
			//true 即为MM商城，则提前进行初始化
			payBussiness.applyPay(HResDetailActivity.this, startPayHandler);
		}
		
		/**
		 * 加入等待框，如果服务器返回的值为null,则弹出确定dilaog提示用户
		 * 点击确定返回到模板列表，并回传标志
		 */
		mContext = this;
		mStatistics = new HStatistics(mContext);
		
        waitDialog = createDialog();
		showDialog("0");
		
		bitmapPre = null;
		bitmapPre= new Bitmap[3];
		
		mTasksDownloading = dlManager.getAllTasks();
		
		downTimesFromXml = getString(R.string.download_times);
		
		//取得传过来的info对象
		Intent intent = this.getIntent();
		
		infoType = intent.getStringExtra("type");
		if(infoType != null && infoType.equals("-1"))
		{
			infoType = "1";
		}
		data = (HResLibModel) intent.getSerializableExtra("HRes");
		template_id = data.getMi();
		initView();
		
		//隐藏下载layout
		mProgressLayout.setVisibility(View.GONE);
		othershow_layoutL.setVisibility(View.GONE);
		othershow_layoutR.setVisibility(View.GONE);
		
		new Thread(){
			@Override
			public void run() {
				try {
					if(info != null)
					{
						Message msgSucc = new Message();
						msgSucc.what = INFO_SUCC;
						barHandler.sendMessage(msgSucc);
					}	
					
					else if (!ToolsUtil.checkNet(mContext)) {
						Message msgFail = new Message();
						msgFail.what = INFO_NONET;
						barHandler.sendMessage(msgFail);
					}
					else
					{
						
						info = new HResLibInfoParser(getApplicationContext(), template_id).getResLibInfo();
						
						if(HResDetailActivity.this.isFinishing()){return;};
						
						if(info != null)
						{
							String payStatus = info.getPl();
							if(info.getPl().equals("-1"))
							{
								payStatus = "1";
							}
							mStatistics.add(HStatistics.Z10_1, info.getMi(), infoType, payStatus);
							mStatistics.add(HStatistics.Z10_2, info.getMi(), "", "");
							HLog.d("Z10_1", info.getMi() + "-" + infoType + "-" + payStatus);
							Message msgSucc = new Message();
							msgSucc.what = INFO_SUCC;
							barHandler.sendMessage(msgSucc);
						}else
						{
							Message msgFail = new Message();
							msgFail.what = INFO_FAIL;
							barHandler.sendMessage(msgFail);
						}
					
					}
				} catch (Exception e) {
					e.printStackTrace();
					Message msgFail = new Message();
					msgFail.what = INFO_FAIL;
					barHandler.sendMessage(msgFail);
				}
			}
			
		}.start();
	
		
		barHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				int result = msg.what;
				switch (result) {
				case INFO_SUCC:
					initEvent();
					initInfo();
					//要把分散的几个缩略图放进下载任务中，缩略图+预览图zip文件
					downPic();
					
					dimissDialog();
					
					break;
					
				case INFO_FAIL:
					dimissDialog();
					templateStatus.setVisibility(View.INVISIBLE);
					//该模板在服务器上不存在提示用户确定返回列表
					AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
					builder.setCancelable(false);
					builder.setTitle(getString(R.string.notice));
					builder.setMessage(getString(R.string.toast_info_new));
					builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								//setResult(RESULT_OK, new Intent().putExtra("delete", "delete"));
								HConst.DETAIL_BACK_TYPE = HConst.TYPE_DELETE;
								HResDetailActivity.this.finish();
							}
						});
					builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							//setResult(RESULT_OK, new Intent().putExtra("delete", "delete"));
							HConst.DETAIL_BACK_TYPE = HConst.TYPE_DELETE;
							HResDetailActivity.this.finish();
						}
					});
					builder.setOnKeyListener(new OnKeyListener() {
						
						@Override
						public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
							if(keyCode == KeyEvent.KEYCODE_SEARCH)
							{
								return true;
							}
							return false;
						}
					});
					
					builder.show();
					break;
				case INFO_NONET:
					dimissDialog();
					templateStatus.setVisibility(View.INVISIBLE);
					Toast.makeText(mContext,
							mContext.getString(R.string.no_connect),
							Toast.LENGTH_LONG).show();
					break;
				}
			}
		};
		
		if (mTasksDownloading.size() == 0) {
			mTasksDownloading = dlManager.getAllTasks();
		}
		

		
	}

	private Dialog createDialog() {
		View v = getLayoutInflater().inflate(R.layout.dialogview, null);
		View layout =  v.findViewById(R.id.dialog_view);
		Dialog wait = new Dialog(HResDetailActivity.this,R.style.MyFullHeightDialog);
		//wait.setCancelable(false);
		wait.setContentView(layout);
		
		wait.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_BACK)
				{
					dimissDialog();
					if(dialogCa == 0)
					{	
						HResDetailActivity.this.finish();
					}
					return true;
				}else if(keyCode == KeyEvent.KEYCODE_SEARCH)
				{
					return true;
				}
				return false;
			}
		});
		return wait;
	}
	
	private void showDialog(String category)
	{
		waitDialog = createDialog();
		waitDialog.show();
		dialogCa = Integer.parseInt(category);
	}
	
	private void dimissDialog()
	{
		if(waitDialog != null && waitDialog.isShowing())
		{
			waitDialog.dismiss();
			waitDialog = null;
		}
	}
	
	private boolean dialog_isShowing()
	{
		boolean flag = false;
		
		if(waitDialog != null && waitDialog.isShowing())
		{
			flag = true;
		}
		
		if(mConfirmDialog != null && mConfirmDialog.isShowing())
		{
			flag = true;
		}
		return flag;
	}
	private void downPic()
	{
		try {
			//主模板缩略图
			Bitmap icon = BitmapFactory.decodeFile(CacheManager.CACHE_RESOURCE + iconFileName);
			if(icon == null) {
				ImageUtil.addDownloadTask(mContext, templateAddress_P, iconFileName, iconHandler);
			} else {
				templateIcon.setBackgroundDrawable(new BitmapDrawable(icon));
			}
			
			//推荐模板缩略图左右
			Bitmap tjLIcon = BitmapFactory.decodeFile(CacheManager.CACHE_RESOURCE + tjIconFileNameL);
			if(tjLIcon == null) {
				ImageUtil.addDownloadTask(mContext, templateAddress_P, tjIconFileNameL, iconHandler);
			} else {
				otherIconL.setBackgroundDrawable(new BitmapDrawable(tjLIcon));
			}
			Bitmap tjRIcon = BitmapFactory.decodeFile(CacheManager.CACHE_RESOURCE + tjIconFileNameR);
			if(tjRIcon == null) {
				ImageUtil.addDownloadTask(mContext, templateAddress_P, tjIconFileNameR, iconHandler);
			} else {
				otherIconR.setBackgroundDrawable(new BitmapDrawable(tjRIcon));
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * 转换
	 * @param kb
	 * @return
	 */
//	private static String KBToMB(String kb)
//	{
//		DecimalFormat df = new DecimalFormat();
//        df.setMaximumFractionDigits(1);
//        df.setMinimumFractionDigits(1);
//		float size = (float)Float.parseFloat(kb) / UNIT;
//		String s = df.format(size) + "MB";
//		return s;
//	}
	
	private class OnCancelListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			mProgressLayout.setVisibility(View.GONE);
			if(dlManager == null)
			{
				dlManager = DLManager.getInstance(HResDetailActivity.this);
			}
			
			//取消下载
			dlManager.deleteTask(taskId);
			//隐藏下载框
			
			cancelDownload();
			//initInstallButton(info.getPkn());
		}
	}
	
	
//	private Bitmap decodeFile(String path){
//		System.gc();
//		//int IMAGE_MAX_SIZE = 400;
//
//	    Bitmap b = null;
//	    try {
//	    	
//	        //Decode image size
//	        BitmapFactory.Options o = new BitmapFactory.Options();
//	        o.inJustDecodeBounds = true;
//
//	        FileInputStream fis = new FileInputStream(path);
//	        BitmapFactory.decodeStream(fis, null, o);
//	        fis.close();
//
//
//	        BitmapFactory.Options o2 = new BitmapFactory.Options();
////	        o2.inSampleSize = 2;
//	        o2.inSampleSize = 1;
//	        fis = new FileInputStream(path);
//	        b = BitmapFactory.decodeStream(fis, null, o2);
//	        fis.close();
//	    } catch (Exception e) {
//	    	e.printStackTrace();
//	    	System.gc();
//	    }
//	    
//	    return b;
//	}

	@Override
	protected void onResume() {
		super.onResume();
		templateStatus.setVisibility(View.VISIBLE);
	}
	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
	}
	
	/***
	 * 收费模版点击下载的时候进行收费提示，点击确认付费按钮后才开始进行下载
	 * @param resId  模版ID
	 * @param phoneNumber 本机电话号码
	 */
	private void downloadClick(final String resId, final String phoneNumber, final HMoldPay hmd)
	{
		//判断该模板是否为限免
		String pr = info.getPr();
		if(null == pr)
		{
			pr = "";
		}
		final boolean isSomeTimeFree = pr.equals(getString(R.string.skin_sometime_free));
		
		
		String message = "";
					//得到付费提示信息
					try {
						message = hmd.getPayRc();
						HLog.d("payInfo", hmd.getPayRc());
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(mContext, getString(R.string.toast_info_new), Toast.LENGTH_SHORT).show();
						return ;
					}
//					dimissDialog();
					
					/**如果当前dialog正在显示*/
					if(mConfirmDialog != null && mConfirmDialog.isShowing())
					{
						return;
					}
					
					if(!ToolsUtil.readSIMCard(mContext))
					{
						mConfirmDialog = new AlertDialog.Builder(mContext)
						.setTitle(mContext.getString(R.string.notice))
						.setMessage(mContext.getString(R.string.simInvalid))
						.setPositiveButton(mContext.getString(R.string.confirm), new DialogInterface.OnClickListener() {
								
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if(null != mConfirmDialog)
								{
									mConfirmDialog.dismiss();
									mConfirmDialog = null;
								}
							}
						})
						.create();
						if(!HResDetailActivity.this.isFinishing() && !mConfirmDialog.isShowing())
						{	
							dimissDialog();
							mConfirmDialog.show();
						}
						return ;
					}
					else if(!ToolsUtil.checkNet(mContext))
					{
						mConfirmDialog = new AlertDialog.Builder(mContext)
						.setTitle(mContext.getString(R.string.notice))
						.setMessage(mContext.getString(R.string.no_connect))
						.setPositiveButton(mContext.getString(R.string.confirm), new DialogInterface.OnClickListener() {
								
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if(null != mConfirmDialog)
								{
									mConfirmDialog.dismiss();
									mConfirmDialog = null;
								}
							}
						})
						.create();
						if(!HResDetailActivity.this.isFinishing() && !mConfirmDialog.isShowing())
						{	
							dimissDialog();
							mConfirmDialog.show();
						}
						return ;
					}
				{
						
					mConfirmDialog = new AlertDialog.Builder(mContext)
					.setTitle(mContext.getString(R.string.spopu_bt_dl))
					.setMessage(message)
					.setPositiveButton(mContext.getString(R.string.confirm), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(null != mConfirmDialog)
							{
								mConfirmDialog.dismiss();
								mConfirmDialog = null;
							}
							if(isSomeTimeFree)
							{
								addTaskToDownManager();
							}else
							{	
								//点击确认支付后，弹出等待框进行等待同时进行支付，支付成功之后，进行成功提示并且开始下载
								showDialog("2");
								//进行付费动作
								new Thread(){
									public void run() {
										Looper.prepare();
										payBussiness.startPay(HResDetailActivity.this, startPayHandler, hmd, phoneNumber);
										Looper.loop();
									};
								}.start();
								
								//把该模板的付费状态设置为已付费
								
								//如果是付费的，点击确认之后就把付费变成已付费
	//							if(info.getPl().equals("1") && !info.getPr().equals(getString(R.string.skin_sometime_free)))
	//							{
	//								info.setPl("-1");
	//							}
								//addTaskToDownManager();
							}
							
							mStatistics.add(HStatistics.Z11_1, info.getMi(), "", "");
						}
					})
					.setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(null != mConfirmDialog)
							{
								mConfirmDialog.dismiss();
								mConfirmDialog = null;
							}
							mStatistics.add(HStatistics.Z11_2, info.getMi(), "", "");
						}
					})
					.create();
					mConfirmDialog.setOnKeyListener(new OnKeyListener() {
						
						@Override
						public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
							if(keyCode == KeyEvent.KEYCODE_SEARCH)
							{
								return true;
							}
							return false;
						}
					});
					
					if(!HResDetailActivity.this.isFinishing() && !mConfirmDialog.isShowing())
					{	
						dimissDialog();
						mConfirmDialog.show();
					}
				}
			return;
	}
	
	//获取支付是否成功
	private Handler startPayHandler = new Handler()
	{
		public void handleMessage(Message msg) {
			int what = msg.what;
			dimissDialog();
			if(what == 200)
			{
				//如果发生订购行为，把id放进list里面，回传给ResLibActivity
				ToolsUtil.mIDlist.add(info.getMi());
				//Toast.makeText(HResDetailActivity.this, "支付成功，模板已经开始下载!", Toast.LENGTH_SHORT).show();
				//如果是付费的，点击确认之后就把付费变成已付费
				//ToolsUtil.mIDlist.add(info.getMi());
				info.setPl("-1");
				info.setPr(getString(R.string.res_payed));
				//支付成功开始正常下载
				addTaskToDownManager();
				isPayED = true;
			}else if(what == 100)
			{
				//支付失败 提醒用户
				Toast.makeText(HResDetailActivity.this, "支付失败，请重试!", Toast.LENGTH_SHORT).show();
			}
		};
	};
	
	private Handler payHandler = new Handler()
	{
		public void handleMessage(Message msg) {
			if(hmdPay == null)
			{
				dimissDialog();
				Toast.makeText(HResDetailActivity.this, R.string.toast_info_new, Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(!dialog_isShowing())
			{return;};
			downloadClick(info.getMi(), ToolsUtil.getPhoneNum(mContext), hmdPay);
		};
	};
	
	private void installClick(DLData task)
	{
		
		if(task == null)
		{
			if(info.getPu() == null)
			{
				return;
			}else
			{
				task = new DLData();
				task.setStatus(DLManager.STATUS_SUCCESS);
				task.setFileName(info.getPu());
				task.setCharge(info.getPl());
				task.setChargeMsg(info.getRc());
				task.setDisplayName(info.getPn());
				task.setPackagename(info.getPkn());
				task.setResID(info.getMi());
			}
		}
		
		if(DLManager.STATUS_SUCCESS == task.getStatus())
		{	
			if(null != mConfirmDialog)
			{
				if(mConfirmDialog.isShowing())
				{
					return;
				}
				else
				{
					mConfirmDialog.show();
				}
			}
			else
			{
				final DLData tas = task;
				int charge = 0;
				if(task.getCharge() != null && !"".equals(task.getCharge())) {
					charge = Integer.valueOf(task.getCharge());
				}
				String message = "";
				if(charge == 1 || charge == -1)
				{
					message = String.format(mContext.getString(R.string.install_paid), task.getDisplayName());
				}
				else {
					message = String.format(mContext.getString(R.string.install_free), task.getDisplayName());
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
					templateStatus.setEnabled(false);
					SkinManage.installSkin(mContext, tas, finish);
				}
			}
			return;
		}
	
	}
	
//	private void installClick(DLData task)
//	{
//		
//		if(task == null)
//		{
//			if(info.getPu() == null)
//			{
//				return;
//			}else
//			{
//				task = new DLData();
//				task.setStatus(DLManager.STATUS_SUCCESS);
//				task.setFileName(info.getPu());
//				task.setCharge(info.getPl());
//				task.setChargeMsg(info.getRc());
//				task.setDisplayName(info.getPn());
//				task.setPackagename(info.getPkn());
//				task.setResID(info.getMi());
//			}
//		}
//		
//		if(DLManager.STATUS_SUCCESS == task.getStatus())
//		{	
//			if(null != mConfirmDialog)
//			{
//				if(mConfirmDialog.isShowing())
//				{
//					return;
//				}
//				else
//				{
//					mConfirmDialog.show();
//				}
//			}
//			else
//			{
//				final DLData tas = task;
//				int charge = 0;
//				if(task.getCharge() != null && !"".equals(task.getCharge())) {
//					charge = Integer.valueOf(task.getCharge());
//				}
//				String message = "";
//				if(charge == 1 || charge == -1)
//				{
//					message = String.format(mContext.getString(R.string.install_paid), task.getDisplayName());
//				}
//				else {
//					message = String.format(mContext.getString(R.string.install_free), task.getDisplayName());
//				}
//				
//				if(charge == 1)
//				{
//					if(!ToolsUtil.readSIMCard(mContext))
//					{
//						mConfirmDialog = new AlertDialog.Builder(mContext)
//						.setTitle(mContext.getString(R.string.notice))
//						.setMessage(mContext.getString(R.string.simInvalid))
//						.setPositiveButton(mContext.getString(R.string.confirm), new DialogInterface.OnClickListener() {
//								
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//								if(null != mConfirmDialog)
//								{
//									mConfirmDialog.dismiss();
//									mConfirmDialog = null;
//								}
//							}
//						})
//						.create();
//						mConfirmDialog.show();
//						return;
//					}
//					else if(!ToolsUtil.checkNet(mContext))
//					{
//						mConfirmDialog = new AlertDialog.Builder(mContext)
//						.setTitle(mContext.getString(R.string.notice))
//						.setMessage(mContext.getString(R.string.no_connect))
//						.setPositiveButton(mContext.getString(R.string.confirm), new DialogInterface.OnClickListener() {
//								
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//								if(null != mConfirmDialog)
//								{
//									mConfirmDialog.dismiss();
//									mConfirmDialog = null;
//								}
//							}
//						})
//						.create();
//						mConfirmDialog.show();
//						return;
//					}
//				}
//				//else
//				{
//					mConfirmDialog = new AlertDialog.Builder(mContext)
//					.setTitle(mContext.getString(R.string.confirm_install))
//					.setMessage(message)
//					.setPositiveButton(mContext.getString(R.string.confirm), new DialogInterface.OnClickListener() {
//						
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							mStatistics.add(HStatistics.Z10_5_1, info.getMi(), "", "");
//							/*jayce delete
//							Intent intent = new Intent(Intent.ACTION_VIEW);
//							intent.setDataAndType(Uri.fromFile(new File(DLManager.LOCAL_PATH + File.separator + tas.getFileName())),
//									"application/vnd.android.package-archive");
//							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//							intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//							mContext.startActivity(intent);
//							*/
//							//jayce add
//							
//							templateStatus.setEnabled(false);
//							SkinManage.installSkin(mContext, tas, finish);
//							if(null != mConfirmDialog)
//							{
//								mConfirmDialog.dismiss();
//								mConfirmDialog = null;
//							}
//						}
//					})
//					.setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
//						
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							mStatistics.add(HStatistics.Z10_5_2, info.getMi(), "", "");
//
//							if(null != mConfirmDialog)
//							{
//								mConfirmDialog.dismiss();
//								mConfirmDialog = null;
//							}
//						}
//					})
//					.create();
//					mConfirmDialog.show();
//				}
//			}
//			return;
//		}
//	
//	}
	
	private final Handler finish = new Handler(){
		public void handleMessage(Message msg)
		{
			//安装完成并且应用，设置按钮文字为：已应用
//			templateStatus.setVisibility(View.VISIBLE);
//			templateStatus.setBackgroundResource(R.drawable.res_installed);
//			
//			templateStatus.setText(mContext.getString(R.string.skin_bt_useing));
//			templateStatus.setEnabled(false);
//			
//			Toast.makeText(HResDetailActivity.this, msg.getData().getString("disname") + getString(R.string.skin_bt_useing), Toast.LENGTH_LONG).show();
			
			//useskin();
			Toast.makeText(
					HResDetailActivity.this,
					info.getPn()
							+ getString(R.string.skin_bt_useing),
					Toast.LENGTH_LONG).show();
			
			//查看
			templateStatus.setEnabled(true);
			templateStatus.setText(getText(R.string.res_check));
			templateStatus.setBackgroundResource(R.drawable.res_de_down);
		}
	};
	
	private void initProgressLayout(String template_id)
	{
		for(DLData data : mTasksDownloading)
		{
			if(data.getResID().equals(template_id))
			{	
				updateItem(mProgressLayout, data);
				
			}
		}
	}
	
	private void updateItem(LinearLayout progressLayout, DLData data)
	{
		taskId = data.getId();
		mCancelButton.setOnClickListener(new OnCancelListener());
		
		DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);
        df.setMinimumFractionDigits(1);
        float progress = 0.0f;
        if(data.getCurrentSize() > 0 && data.getTotalSize() > 0)
        {
        	progress = (float)data.getCurrentSize() * 100.0f / data.getTotalSize();
        }
        String p = df.format(progress);
        mProgressPercent.setText(p + "%");
		mProgressBar.setMax(data.getTotalSize());
		mProgressBar.setProgress(data.getCurrentSize());
		if(!p.equals("100.0"))
		{	
			mProgressLayout.setVisibility(View.VISIBLE);
		}
//		dlManager = DLManager.getInstance(mContext);
//		dlManager.setProgressHandler(downHandler);
	}
	
	/**根据模板的状态来初始化下载按钮
	 * 1.下载
	 * 2.安装
	 * 3.已安装
	 * */
	private void initInstallButton(String packageName)
	{
		HLog.d("packageName", packageName);
		//获取该模板的目前状态
		int status = getStatus(packageName);
		String isPay = info.getPl();
		
		templateStatus.setEnabled(true);
		templateStatus.setBackgroundResource(R.drawable.res_de_down);
		
		switch (status) {
		case INSTALLED:
			//已安装：应用
			HLog.i("status", "INSTALLED");
//			templateStatus.setBackgroundResource(R.drawable.res_installed);
//			templateStatus.setEnabled(false);
//			templateStatus.setText(mContext.getString(R.string.res_ins));
			
			Cursor c = mContext.getContentResolver().query(
					HResProvider.CONTENT_URI_SKIN,
					null,
					HResDatabaseHelper.PACKAGENAME + " = " + "'"
							+ packageName + "'", null, null);
			if (c != null && c.getCount() > 0) {

				while (c.moveToNext()) {
					int use = c
							.getInt(c
									.getColumnIndex(HResDatabaseHelper.RES_USE));
//					if (use == 1) {
//						templateStatus.setEnabled(false);
//						templateStatus.setText(getText(R.string.skin_bt_useing));
//						templateStatus.setBackgroundResource(R.drawable.res_installed);
//
//					} else {
//						templateStatus.setEnabled(true);
//						templateStatus.setText(getText(R.string.skin_bt_use));
//						templateStatus.setBackgroundResource(R.drawable.res_apply);
//					}
					
					//查看
					templateStatus.setText(getText(R.string.res_check));
					templateStatus.setBackgroundResource(R.drawable.res_de_down);
					
				}

			}
			c.close();
			
			mProgressLayout.setVisibility(View.GONE);
			break;
		case DOWNLOADED:
			//已下载：应用
			HLog.i("status", "DOWNLOADED");
			
			templateStatus.setBackgroundResource(R.drawable.res_install_new);
//			templateStatus.setText(mContext.getString(R.string.install));
			//改成应用
			templateStatus.setText(getText(R.string.skin_bt_use));
			break;
		case NO_DOWNLOAD:
			//未下载：免费/限免
			HLog.i("status", "NO_DOWNLOAD");
			templateStatus.setBackgroundResource(R.drawable.res_de_down);
			//templateStatus.setText(mContext.getString(R.string.spopu_bt_dl));
			
			//如果是0，显示为免费
			if (isPay.equals("0")) {
				templateStatus.setText(mContext
						.getText(R.string.res_text_free));
			}
			//如果1或则-1，则都显示为限免
			else if (isPay.equals("1") || isPay.equals("-1")) {
				yuan = info.getPr();
				templateStatus.setText(yuan);
			}
			
			break;
		case DOWNLOADING:
			//下载中：下载中
			HLog.i("status", "DOWNLOADING");
			//templateStatus.setVisibility(View.INVISIBLE);
			
			templateStatus.setText(mContext
					.getText(R.string.res_ding));
			templateStatus.setEnabled(false);
			templateStatus
					.setBackgroundResource(R.drawable.res_de_down_loading);
			
			mProgressLayout.setVisibility(View.VISIBLE);
			break;
		}
	}
	
	/***
	 * 0.已安装 0	灰色按钮显示“已安装”
	 * 1.已下载	1	按钮可点击显示“安装”
	 * 2.未下载 2   按钮可点击显示“下载”
	 * 3.下载中 3	按钮不可见
	 * @param packageName
	 * @param tv
	 * @return
	 */
	private static final int INSTALLED = 0;
	private static final int DOWNLOADED = 1;
	private static final int NO_DOWNLOAD = 2;
	private static final int DOWNLOADING = 3;
	private int getStatus(String packageName) {
		int dlStatus = 0;
		/**查询是否已经安装*/
		Cursor insCor = mContext.getContentResolver().query(
				HResProvider.CONTENT_URI_SKIN, null,
				HResDatabaseHelper.PACKAGENAME + " = '" + packageName + "'",
				null, null);
		if (insCor != null && insCor.getCount() > 0) {
			insCor.close();
			return INSTALLED;
		} else {
			/**从download表中查询后三种状态*/
			Cursor dlCor = mContext.getContentResolver().query(
					HResProvider.CONTENT_URI_DOWNLOAD,
					null,
					HResDatabaseHelper.PACKAGENAME + " = '" + packageName
							+ "'", null, null);
			if(dlCor == null)
			{
				if(insCor != null){insCor.close();}
				return NO_DOWNLOAD;
			}
			while(dlCor.moveToNext()) {
				if (dlCor.getCount() > 0) {
						
					dlStatus = dlCor.getInt(dlCor.getColumnIndex(HResDatabaseHelper.TASK_STATUS));
					if (dlStatus == DLManager.STATUS_SUCCESS) {
						HLog.d("dlCor.count", dlCor.getCount() + "DOWNLOADED");
						if(insCor != null){insCor.close();}
						dlCor.close();
						return DOWNLOADED;
					}else
					{
						HLog.d("dlCor.count", dlCor.getCount() + "DOWNLOADING");
						if(insCor != null){insCor.close();}
						dlCor.close();
						return DOWNLOADING;
					}
				}else
				{
					HLog.d("dlCor.count", dlCor.getCount() + "NO_DOWNLOAD");
					if(insCor != null){insCor.close();}
					dlCor.close();
					return NO_DOWNLOAD;
				}
			}
			if(insCor != null){insCor.close();}
			dlCor.close();
		}
		HLog.d("dlCor.count", "NO_DOWNLOAD");
		return NO_DOWNLOAD;
	}
	
	private void reBitmap()
	{
		CacheManager.newInstance().clearMemoryCache();
		if(bitmapPre.length == 3)
		{
			if(bitmapPre[0] != null && !bitmapPre[0].isRecycled())
			{	
				bitmapPre[0].recycle();
				bitmapPre[0] = null;
			}
			if(bitmapPre[1] != null && !bitmapPre[1].isRecycled())
			{	
				bitmapPre[1].recycle();
				bitmapPre[1] = null;
			}		
			if(bitmapPre[2] != null && !bitmapPre[2].isRecycled())
			{	
				bitmapPre[2].recycle();
				bitmapPre[2] = null;
			}
		}
		System.gc();
	}

	@Override
	protected void onPause() {
		HLog.d("Detail_onPause", "onPause");
		System.gc();
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		HLog.d("Detail_onStop", "onStop");
		System.gc();
		super.onStop();
		dimissDialog();
		if(mConfirmDialog!= null)
		{
			mConfirmDialog.dismiss();
		}
	}
	
	@Override
	protected void onRestart() {
		HLog.d("Detail_onRestart", "onRestart");
		super.onRestart();
		dimissDialog();
		if(mConfirmDialog!= null)
		{
			mConfirmDialog.show();
		}
	}
	
	@Override
	protected void onDestroy() {
		reBitmap();
		HLog.d("Detail_onDestroy", "onDestroy");
		super.onDestroy();
	}

	private void addTaskToDownManager() {
		if(!ToolsUtil.checkSDcard())
		{
			Toast.makeText(mContext, mContext.getString(R.string.plugsd), Toast.LENGTH_LONG).show();
			return;
		}
		
		mProgressLayout.setVisibility(View.VISIBLE);
		//templateStatus.setVisibility(View.INVISIBLE);
		templateStatus.setBackgroundResource(R.drawable.res_de_down_loading);
		templateStatus.setText(getString(R.string.res_ding));
		templateStatus.setEnabled(false);
		
		
		DLData dlData = new DLData();
		dlData.setResID(info.getMi());	//传入模板ID即可
		HLog.d("task Id", info.getMi());
		dlData.setFileName(info.getPu());
		dlData.setDisplayName(info.getPn());
		dlData.setCharge(info.getPl());
		dlData.setChargeMsg(info.getRc());
		dlData.setIconUrl(info.getIn());
		dlData.setPackagename(info.getPkn());
		HLog.e(TAG, "pkn:" + info.getPkn());
		taskId = dlManager.addTask(dlData);
		
		mProgressBar.setProgress(0);
		mProgressPercent.setText("0.0%");
		mCancelButton.setBackgroundResource(R.drawable.res_de_cancel);
	}
	
	private class ImageAdapter extends BaseAdapter
	{

		private Context mContext;
		private Bitmap[] imgPre;
		public ImageAdapter(Context c, Bitmap[] pre) { 
			mContext = c;  
			imgPre = new Bitmap[pre.length];
			for(int i = 0; i < pre.length; i ++)
			{
				imgPre[i] = pre[i];
			}
		}  
		
		@Override
		public int getCount() {
			return imgPre.length;
		}

		@Override
		public Object getItem(int position) {
			return imgPre[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = (ImageView) convertView;

			if (i == null) // 如果 view 为空
			{
				i = new ImageView(mContext); // 就新建一个
			}
			i.setImageBitmap(imgPre[position]);
			i.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			
			return i;
		}
		
	}
	
	private void setRadioIndex(int position)
	{
		switch (position) {
		case 0:
			mDetail_pre_1.setImageResource(R.drawable.radio_on);
			mDetail_pre_2.setImageResource(R.drawable.radio);
			mDetail_pre_3.setImageResource(R.drawable.radio);
			break;

		case 1:
			mDetail_pre_1.setImageResource(R.drawable.radio);
			mDetail_pre_2.setImageResource(R.drawable.radio_on);
			mDetail_pre_3.setImageResource(R.drawable.radio);
			break;
		case 2:
			mDetail_pre_1.setImageResource(R.drawable.radio);
			mDetail_pre_2.setImageResource(R.drawable.radio);
			mDetail_pre_3.setImageResource(R.drawable.radio_on);
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_SEARCH)
		{
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			if(isPayED)
			{	
				Intent intent = new Intent(HResDetailActivity.this, HResLibActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
				startActivity(intent);
			}
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
