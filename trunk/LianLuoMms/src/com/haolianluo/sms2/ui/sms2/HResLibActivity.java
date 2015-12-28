package com.haolianluo.sms2.ui.sms2;

import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.AbsListView.OnScrollListener;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.data.HSharedPreferences;
import com.haolianluo.sms2.model.HMoldPay;
import com.haolianluo.sms2.model.HResDatabaseHelper;
import com.haolianluo.sms2.model.HResLibModel;
import com.haolianluo.sms2.model.HResLibParser;
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

public class HResLibActivity extends HActivity implements OnClickListener,
		OnGestureListener {
	/*付费**/
	final int mTYPEHOT = 0; 
	/*免费**/
	final int mTYPEFREE = 1;
	/*畅销*/
	final int mTYPENEW = 2;
	
	/*我的模版**/
	final int mTYPEMY = 3;
	
	TextView mHotTv, mFreeTv, mNewTv, mTilteTv, mMyTv;
	ImageView mTitleIv;// , mTitleBt;
	
	private Context mContext;
	
	
	boolean back = false;
	
	private LinearLayout mMenuLinearLayout;
	private boolean mIsDeleteMode = false;
	
	private ProgressDialog mDeleteDialog;

	//包月按钮
	private Button month_rate_bt;
	
	/**
	 * 移动MM包月状态
	 * -1：有包月记录过期；
	 * -2：未有包月记录；
	 *  0：包月中；
	 */
	private String month_rate_status = "";
	
	private String linshi_by = "";
	/**包月中*/
	private static final String MONTH_RATE_ING = "0";
	/**包月已过期*/
	private static final String MONTH_RATE_OUT = "-1";
	/**没有包月记录*/
	private static final String MONTH_RATE_NORECORDE = "-2";
	
	/**
	 * 移动MM包月提示语
	 */
	private String month_rate_RC1 = "";
	
	/**
	 * 移动MM包月快到期提醒
	 */
	private String month_rate_RC2 = "";
	
	private PayBussiness payBussi;
	
	private LinearLayout month_rate_layout;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		back = false;
		
		//进入资源库默认展示付费模版
		type = mTYPEHOT;
		setContentView(R.layout.res_list);
		mContext = this;
		
		mInflater = LayoutInflater.from(mContext);
		
		
		// IntentFilter filter = new
		// IntentFilter(HSkinReceiver.ACTION_INSTALLED);
		// registerReceiver(mBroadcastInstalled, filter);
		// new HUpdateTools(HResLibActivity.this).checkUpdate();
	}

	//获取支付是否成功
	private Handler month_PayHandler = new Handler()
	{
		public void handleMessage(Message msg) {
			int what = msg.what;
			//关闭dialog
			if(what == 200)
			{
				//包月成功
				mStatistics.add(HStatistics.Z8_6, "", "", "");
				
				//Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
				
				//支付成功，重新刷新列表（付费和畅销）
				//先清除付费和畅销的第一页的缓存
//				CacheManager cacheManager = new CacheManager();
//				cacheManager.removeResourceCache(ToolsUtil.getLanguage()
//						+ HConst.RESLIB_CXLST + 1);
//				
//				cacheManager.removeResourceCache(ToolsUtil.getLanguage()
//						+ HConst.RESLIB_PYLST + 1);
				
//				if(mFreeAdapter != null)
//				{	
//					mFreeAdapter.getResList(mTYPEHOT, 1, 1);
//					mFreeAdapter.getResList(mTYPENEW, 1, 1);
//					
//					initAdapter(mTYPEHOT);
//					initAdapter(mTYPENEW);
//				}
				
					mHotAdapter = null;
					mNewAdapter = null;
					mFreeAdapter = null;
					
					initAdapter(mTYPEHOT);
					
//				if (mHotAdapter != null) {
//					//mHotAdapter.getResList(mTYPEHOT, 1, 1);
//					mHotAdapter.notifyDataSetChanged();
//				}
//				if (mNewAdapter != null) {
//					//mNewAdapter.getResList(mTYPEHOT, 1, 1);
//					mNewAdapter.notifyDataSetChanged();
//				}
				HConst.setMonthSTATU(mContext, "0");
				if(month_rate_layout != null)
				{	
					linshi_by = "0";
					month_rate_layout.setVisibility(View.GONE);
				}
				
			}else if(what == 100)
			{
				//支付失败
				//Toast.makeText(mContext, "支付失败，请重试", Toast.LENGTH_SHORT).show();
			}
		};
	};
	
	/*
	 * private BroadcastReceiver mBroadcastInstalled = new BroadcastReceiver() {
	 * 
	 * @Override public void onReceive(Context context, Intent intent) { // TODO
	 * Auto-generated method stub if (intent != null && intent.getAction()
	 * .equals(HSkinReceiver.ACTION_INSTALLED)) {
	 * diaHandler.sendEmptyMessage(4); } } };
	 */
	public void onStart() {
		super.onStart();
		HConst.markActivity  = 8;
		mDLManager = DLManager.getInstance(mContext);
		mDLDatalist = mDLManager.getAllTasks();
		
		//为包月计费进行预先初始化
		payBussi = new PayBussiness();
		if(ToolsUtil.MM_FLAG)
		{	
			payBussi.applyPay(this, month_PayHandler);
		}
		System.out.println("mFreeAdapter : "+ mFreeAdapter + "  mHot: "+ mHotAdapter + "  mNew :"+ mNewAdapter);
	    
		//back 如果返回时需要刷新adapter列表或则有在详情页付过费的
		if (!back || ToolsUtil.mIDlist.size() > 0) {
//			mFreeAdapter = null;
//			mHotAdapter = null;
//			mNewAdapter = null;
			initView(type);
		}
//        if(mFreeAdapter == null||mHotAdapter == null||mNewAdapter == null){
//        	initView(type);
//        }
		
		if (mFreeAdapter != null) {
			mFreeAdapter.notifyDataSetChanged();
		}

		if (mHotAdapter != null) {
			mHotAdapter.notifyDataSetChanged();
		}
		if (mNewAdapter != null) {
			mNewAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}

	/**
	 * 广告区域的显示与隐藏
	 */
	private void setLayoutAndToastMonthRate() {
		if(!ToolsUtil.MM_FLAG)
		{
			return;
		}
		if(HConst.getMonthSTATU(mContext) != null && HConst.getMonthSTATU(mContext).equals(MONTH_RATE_ING))
		{
			//如果处于包月中就隐藏广告条
			if(month_rate_layout != null)
			{	
				month_rate_layout.setVisibility(View.GONE);
			}
		}
		else if(linshi_by != null && linshi_by.equals(MONTH_RATE_ING))
		{
			//如果处于包月中就隐藏广告条
			if(month_rate_layout != null)
			{
				month_rate_layout.setVisibility(View.GONE);
			}
		}
		else if(HConst.getMonthSTATU(mContext) != null && !HConst.getMonthSTATU(mContext).equals("") && !HConst.getMonthSTATU(mContext).equals(MONTH_RATE_ING))
		{
			//如果不是包月中就显示广告条(无包月记录和包月已过期)
			if(month_rate_layout != null)
			{
				month_rate_layout.setVisibility(View.VISIBLE);
				
				if(HConst.getMonthSTATU(mContext).equals(MONTH_RATE_OUT))
				{
					//包月已过期，进行包月过期提示
					Toast.makeText(mContext, getString(R.string.month_rate_out), Toast.LENGTH_SHORT).show();
				}
			}
		}
		
		SharedPreferences.Editor se = getSharedPreferences(HConst.ORDER_TOAST, Context.MODE_PRIVATE).edit();
		
		SharedPreferences sp = getSharedPreferences(HConst.ORDER_TOAST, Context.MODE_PRIVATE);
		
		SimpleDateFormat myFmt2=new SimpleDateFormat("yyyy-MM-dd");
		String formatTime = myFmt2.format(new Date());
		
		//如果接收到服务器端返回的包月第多少天提示，toast提醒用户，然后用当天的标识设置缓存标志位
		if(month_rate_RC2 != null && !month_rate_RC2.equals("") && sp.getBoolean(formatTime, true))
		{
			Toast.makeText(mContext, month_rate_RC2, Toast.LENGTH_LONG).show();
			
			se.putBoolean(formatTime, false);
			se.commit();
		}
	}
	
	
	/**
	 * 按键处理
	 * @param keyCode
	 * @param event
	 * @return
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//我的模版
		if (type == mTYPEMY) {
			if (KeyEvent.KEYCODE_MENU == event.getKeyCode()) {
				if (mMenuLinearLayout != null
						&& mMenuLinearLayout.getVisibility() == View.VISIBLE) {
					return true;
				} else {
					if (mMenuLinearLayout == null) {
						mMenuLinearLayout = (LinearLayout) mMyLinearLayout
								.findViewById(R.id.linearlayout_menu);
					}
					mMenuLinearLayout.setVisibility(View.VISIBLE);
					Button bt_select_all = (Button) mMenuLinearLayout
							.findViewById(R.id.button_select_all);
					Button bt_cancel_selected = (Button) mMenuLinearLayout
							.findViewById(R.id.button_cancel_selected);
					Button bt_delete_selected = (Button) mMenuLinearLayout
							.findViewById(R.id.button_delete_selected);
					Button bt_back = (Button) mMenuLinearLayout
							.findViewById(R.id.button_back);
					bt_select_all.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							mDeleteList.clear();
							mIsSelectAll = true;
							initMy();
							if (SkinManage.mCurrentSkin
									.equals(HConst.DEFAULT_PACKAGE_NAME)) {
								mInstalledCursor.moveToPosition(0);
							} else {
								if (mInstalledCursor.getCount() > 1) {
									mInstalledCursor.moveToPosition(1);
								}
							}
							while (mInstalledCursor.moveToNext()) {
								mDeleteList
										.add(mInstalledCursor
												.getString(mInstalledCursor
														.getColumnIndex(HResDatabaseHelper.FILE_NAME)));
							}
							mIsSelectAll = false;
						}
					});
					bt_cancel_selected
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									mDeleteList.clear();
									initMy();
								}
							});
					bt_delete_selected
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									if (0 == mDeleteList.size()) {
										Toast
												.makeText(
														mContext,
														getString(R.string.select_empty),
														Toast.LENGTH_SHORT)
												.show();
										return;
									} else {
										new AlertDialog.Builder(mContext)
												.setMessage(
														getString(R.string.delete_skin))
												.setPositiveButton(
														getString(R.string.yes),
														new DialogInterface.OnClickListener() {

															@Override
															public void onClick(
																	DialogInterface dialog,
																	int which) {
																// TODO
																// Auto-generated
																// method stub
																if (null == mDeleteDialog) {
																	mDeleteDialog = new ProgressDialog(
																			mContext);
																	mDeleteDialog
																			.setMessage(getString(R.string.deleting));
																	mDeleteDialog
																			.setCancelable(false);
																	mDeleteDialog
																			.setCanceledOnTouchOutside(false);
																	mDeleteDialog
																			.show();
																}
																new Thread() {
																	public void run() {
																		for (String filename : mDeleteList) {
																			deleteSkin(filename);
																		}
																		mDeleteHandler
																				.sendEmptyMessage(0);
																	}
																}.start();
															}
														})
												.setNegativeButton(
														getString(R.string.calcel),
														new DialogInterface.OnClickListener() {

															@Override
															public void onClick(
																	DialogInterface dialog,
																	int which) {
																// TODO
																// Auto-generated
																// method stub

															}
														}).create().show();
									}
								}
							});
					bt_back.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							mMenuLinearLayout.setVisibility(View.GONE);
							mIsDeleteMode = false;
							initMy();
						}
					});
					mIsDeleteMode = true;
					mDeleteList.clear();
					initMy();
					return true;
				}
			} else if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
				if (mIsDeleteMode) {
					mMenuLinearLayout.setVisibility(View.GONE);
					mIsDeleteMode = false;
					mDeleteList.clear();
					initMy();
					return true;
				} else {
					return super.onKeyDown(keyCode, event);
				}
			} else {
				return super.onKeyDown(keyCode, event);
			}
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	/**
	 * 
	 */
	private Handler mDeleteHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (null != mDeleteDialog) {
				mDeleteDialog.dismiss();
				mDeleteDialog = null;
				Toast.makeText(mContext, getString(R.string.skin_deleted),
						Toast.LENGTH_SHORT).show();
				mMenuLinearLayout.setVisibility(View.GONE);
				mIsDeleteMode = false;
				mDeleteList.clear();
				initMy();
			}
		}
	};
	
	boolean showDialog = false;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//详情页返回
		back = true;
		diaHandler.sendEmptyMessage(0);
		diaHandler.sendEmptyMessage(2);
		
//		if (resultCode == RESULT_OK) {
//			
//			Bundle bd = data.getExtras();
//			
//			//详情页返回：1.没有拉取到该模版信息（delete）；2.点击查看按钮返回到资源库进入我的模版页（check）
//			String tag = bd.getString("delete");
//			String tag1= bd.getString("check");
//			if (tag!=null&&tag.equals("delete")) {
//				showDialog = true;
//				back = false;
//			}
//			
//			if (tag1!=null&&tag1.equals("check")) {
//				showDialog = true;
//				back = false;
//				type = mTYPEMY;
//			}
//		}
		
		if(!HConst.DETAIL_BACK_TYPE.equals(""))
		{
			String back_tag = HConst.DETAIL_BACK_TYPE;
			if(back_tag.equals(HConst.TYPE_DELETE))
			{
				showDialog = true;
				back = false;
				HConst.DETAIL_BACK_TYPE = HConst.TYPE_EMPTY;
			}
			
			if(back_tag.equals(HConst.TYPE_CHECK))
			{
				showDialog = true;
				back = false;
				type = mTYPEMY;
				HConst.DETAIL_BACK_TYPE = HConst.TYPE_EMPTY;
			}
		}
		// mFreeAdapte.
		// mHotAdapter = null;
		// mNewAdapter = null;

		count = type;
	}

	/**
	 * 初始化相关布局控件事件
	 */
	private void getLayout() {
		mHotTv = (TextView) findViewById(R.id.text_hot_res);
		mFreeTv = (TextView) findViewById(R.id.text_free_res);
		mNewTv = (TextView) findViewById(R.id.text_new_res);
		mMyTv = (TextView) findViewById(R.id.text_my_res);
		
		//包月按钮
		month_rate_bt = (Button) findViewById(R.id.month_rate);
		month_rate_layout = (LinearLayout) findViewById(R.id.month_rate_layout);
		
		mHotTv.setOnClickListener(this);
		mFreeTv.setOnClickListener(this);
		mNewTv.setOnClickListener(this);
		mMyTv.setOnClickListener(this);
		
		month_rate_layout.setOnClickListener(this);
		month_rate_bt.setOnClickListener(this);
		
		
		mTilteTv = (TextView) findViewById(R.id.res_title_text);
		mTitleIv = (ImageView) findViewById(R.id.res_title_icon);
		mTitleIv.setOnClickListener(this);
		
		//左右滑动，切换标签页
		mFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
		//手势识别
		mDetector = new GestureDetector(this);

	}
	
     /*保存进入详情时的 list列表索引**/
	private static int type = 0;

	/**
	 * 初始化某个标签页
	 * @param flag	标签页类型
	 */
	private void initView(int flag) {
		getLayout();
		moveSetChoic(flag);

		if (mTYPEMY != flag) {
			if (!ToolsUtil.checkNet(this)) {
				Toast.makeText(this, getString(R.string.no_connect),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	MyOnTouchListener myListener = null;
	GridView mHotG, mFreeG, mNewG;
	HResLibAdapter mFreeAdapter, mHotAdapter, mNewAdapter;

	// my skin related
	private Cursor mInstalledCursor;
	LinearLayout mMyLinearLayout;
	LinearLayout mListInstalled;
	Map<String, View> mViewMapInstalled = new HashMap<String, View>();
	LayoutInflater mInflater;
	Dialog mConfirmDialog;
	private TextView mInstalledCount;
	Map<String, Bitmap> mIconMap = new HashMap<String, Bitmap>();

	/**
	 * 初始化布局以及apdate
	 * @param flag
	 */
	private void initAdapter(int flag) {
		type = flag;
		myListener = new MyOnTouchListener();
		if (mFreeG == null)
			mFreeG = (GridView) findViewById(R.id.free_g);
		if (mHotG == null)
			mHotG = (GridView) findViewById(R.id.hot_g);
		if (mNewG == null)
			mNewG = (GridView) findViewById(R.id.new_g);
		if (mMyLinearLayout == null) {
			mMyLinearLayout = (LinearLayout) findViewById(R.id.my_g);

		}

		if (mFreeAdapter == null) {
			mFreeAdapter = new HResLibAdapter(this, mTYPEFREE, mFreeG,
					showDialog, diaHandler);
		}
		if (mHotAdapter == null) {
			mHotAdapter = new HResLibAdapter(this, mTYPEHOT, mHotG, showDialog,
					diaHandler);
		}
		
		if (mNewAdapter == null) {
			mNewAdapter = new HResLibAdapter(this, mTYPENEW, mNewG, showDialog,
					diaHandler);
		}
		
		setViewFilpperChild(mTYPEHOT, mHotG, mHotAdapter);
		setViewFilpperChild(mTYPEFREE, mFreeG, mFreeAdapter);
		setViewFilpperChild(mTYPENEW, mNewG, mNewAdapter);
		
		initMy();
		mFlipper.setOnTouchListener(myListener);
		
		// 允许长按住ViewFlipper,这样才能识别拖动等手势
		mFlipper.setLongClickable(true);

	}

	/**
	 * 设定ViewFilpperChild
	 * @param flag	标签页类型
	 * @param view	控件
	 * @param hla	适配器
	 */
	public void setViewFilpperChild(int flag, GridView view, HResLibAdapter hla) {

		view.setAdapter(hla);
		view.setOnScrollListener(hla.osl);
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				myListener.onTouch(v, event);
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.text_hot_res:
			if (count != 0) {
				setChoice(v.getId());
				setLayoutAndToastMonthRate();
				mStatistics.add(HStatistics.Z8_1, "", "", "");
			}

			break;
		case R.id.text_free_res:
			if (count != 1) {
				setChoice(v.getId());
				setLayoutAndToastMonthRate();
				mStatistics.add(HStatistics.Z8_2, "", "", "");
			}

			break;
		case R.id.text_new_res:
			if (count != 2) {
				setChoice(v.getId());
				setLayoutAndToastMonthRate();
				mStatistics.add(HStatistics.Z8_3, "", "", "");

			}
			break;
		case R.id.text_my_res:
			if (count != 3) {
				if(month_rate_layout != null)
				{
					month_rate_layout.setVisibility(View.GONE);
				}
				setChoice(v.getId());
				mStatistics.add(HStatistics.Z8_4, "", "", "");

			}
			break;
		case R.id.res_title_button: {
			// HStatistics mStatistics = new HStatistics(HResLibActivity.this);
			// mStatistics.add(HStatistics.Z8_4, "", "", "");
			// Intent intent = new Intent(HResLibActivity.this,
			// HResMineActivity.class);
			// startActivity(intent);
		}
			break;
		case R.id.res_title_icon:
			finish();
			break;
			
		case R.id.month_rate:
		{
			clickMonthRateButton();
			break;
		}
		
		case R.id.month_rate_layout:
		{
			clickMonthRateButton();
			break;
		}
		}

	}

	/**
	 * 广告条点击
	 */
	public void clickMonthRateButton()
	{
		final String rc1 = HConst.getMonthRC1(mContext);
		
		if(rc1!= null && month_rate_status.equals(MONTH_RATE_ING))
		{
			Toast.makeText(mContext, getString(R.string.pl_re_order), Toast.LENGTH_SHORT).show();
			return;
		}
		
		mStatistics.add(HStatistics.Z8_5, "", "", "");
		
		if(rc1.equals(""))
		{
			Toast.makeText(mContext, getString(R.string.pl_wait), Toast.LENGTH_SHORT).show();
			return;
		}
		
		//包月计费
		AlertDialog.Builder month_rate_dialog = new Builder(mContext);
		month_rate_dialog.setCancelable(true);
		month_rate_dialog.setTitle(getString(R.string.notice));	//提示
		month_rate_dialog.setMessage(rc1);
		
		month_rate_dialog.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				payBussi.startPay(
						HResLibActivity.this,
						"0",
						ToolsUtil.getPhoneNum(HResLibActivity.this),true);
			}
		});
		
		month_rate_dialog.setNegativeButton(getString(R.string.calcel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//month_rate_bt.setVisibility(View.GONE);
			}
		});
		month_rate_dialog.show();
	}
	
	HStatistics mStatistics = new HStatistics(this);
	
    /**
     * 设置top选项
     * @param id  textView ID
     */
	private void setChoice(int id) {
		TextView textH = (TextView) findViewById(R.id.res_hot_img);
		TextView textF = (TextView) findViewById(R.id.res_free_img);
		TextView textN = (TextView) findViewById(R.id.res_new_img);
		TextView textM = (TextView) findViewById(R.id.res_my_img);
		
		textH.setBackgroundDrawable(null);
		textF.setBackgroundDrawable(null);
		textN.setBackgroundDrawable(null);
		textM.setBackgroundDrawable(null);
		
		mHotTv.setTextColor(getColor(R.color.res_def));
		mFreeTv.setTextColor(getColor(R.color.res_def));
		mNewTv.setTextColor(getColor(R.color.res_def));
		mMyTv.setTextColor(getColor(R.color.res_def));

		switch (id) {
		case R.id.text_hot_res:
			initAdapter(mTYPEHOT);
			textH.setBackgroundResource(R.drawable.tab_on);
			mHotTv.setTextColor(getColor(R.color.res_choice));
			
			this.mFlipper.setDisplayedChild(0);
			this.count = 0;
			break;
		case R.id.text_free_res:
			initAdapter(mTYPEFREE);
			textF.setBackgroundResource(R.drawable.tab_on);
			mFreeTv.setTextColor(getColor(R.color.res_choice));
			
			this.mFlipper.setDisplayedChild(1);
			this.count = 1;
			break;
		case R.id.text_new_res:
			initAdapter(mTYPENEW);
			textN.setBackgroundResource(R.drawable.tab_on);
			mNewTv.setTextColor(getColor(R.color.res_choice));
			
			this.mFlipper.setDisplayedChild(2);
			this.count = 2;
			break;
		case R.id.text_my_res:
			initAdapter(mTYPEMY);
			textM.setBackgroundResource(R.drawable.tab_on);
			mMyTv.setTextColor(getColor(R.color.res_choice));
			
			this.mFlipper.setDisplayedChild(3);
			this.count = 3;

			break;
		default:
			// initAdapter(mTYPEHOT);
			this.mFlipper.setDisplayedChild(0);
			textF.setBackgroundResource(R.drawable.tab_on);
			mFreeTv.setTextColor(getColor(R.color.res_choice));
			break;
		}

	}

	//------------------------------------MY
	private void initMy() {
		mViewMapInstalled.clear();
		if (mListInstalled != null) {
			mListInstalled.removeAllViews();
		}
		mListInstalled = (LinearLayout) mMyLinearLayout
				.findViewById(R.id.list_installed);
		mInstalledCount = (TextView) mMyLinearLayout
				.findViewById(R.id.res_installed_count);
		initInstalled();
		updateCount();
	}

	private void initInstalled() {
		mInstalledCursor = mContext.getContentResolver().query(
				HResProvider.CONTENT_URI_SKIN, null, null, null,
				HResDatabaseHelper.RES_USE + " DESC");
		mListInstalled.removeAllViews();
		if (mInstalledCursor.getCount() > 0) {
			while (mInstalledCursor.moveToNext()) {
				View view = mInflater
						.inflate(R.layout.res_installed_item, null);
				updateInstalled(view, mInstalledCursor);
				mViewMapInstalled.put(String.valueOf(mInstalledCursor
						.getInt(mInstalledCursor.getColumnIndex("_id"))), view);
				mListInstalled.addView(view);
			}
		}
	}

	private void updateCount() {
		/*
		 * new mDownloadingCount.setText("(" + mTasksDownloading.size() + ")");
		 * mDownloadedCount.setText("(" + mTasksDownloaded.size() + ")");
		 */
		mInstalledCount.setText("(" + mInstalledCursor.getCount() + ")");
	}

	private void updateInstalled(View view, Cursor cursor) {
		ImageView iv_icon = (ImageView) view.findViewById(R.id.res_icon);
		try {
			String filename = cursor.getString(cursor.getColumnIndex(HResDatabaseHelper.FILE_NAME));
			String apkPath = "";
			if(filename != null && filename.contains("default"))
			{
				apkPath = mContext.getApplicationContext().getFilesDir().getAbsolutePath()
						+ File.separator + "default"+ File.separator + 
						cursor.getString(cursor
								.getColumnIndex(HResDatabaseHelper.FILE_NAME));
			}
			else
			{
				apkPath = DLManager.INSTALL_PATH
					+ File.separator
					+ cursor.getString(cursor
							.getColumnIndex(HResDatabaseHelper.FILE_NAME));
			}
			Resources res = SkinManage.getRes(mContext, apkPath);
			Drawable d = new BitmapDrawable(BitmapFactory.decodeStream(res
					.getAssets().open(
							"titleS" + File.separator
									+ res.getAssets().list("titleS")[0])));
			iv_icon.setBackgroundDrawable(d);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			iv_icon.setBackgroundResource(R.drawable.shop_skin);
		}
		TextView tv_name = (TextView) view.findViewById(R.id.res_name);
		// TextView tv_size = (TextView)view.findViewById(R.id.res_size);

		// old design
		FrameLayout fl = (FrameLayout) view.findViewById(R.id.res_frame_apply);
		ImageView iv_check = (ImageView) fl.findViewById(R.id.res_apply);
		Button bt_apply = (Button) view.findViewById(R.id.button_apply);
		if (mIsDeleteMode) {
			fl.setVisibility(View.VISIBLE);
			bt_apply.setVisibility(View.GONE);
			if (mIsSelectAll) {
				iv_check.setVisibility(View.VISIBLE);
			}
		} else {
			fl.setVisibility(View.GONE);
			bt_apply.setVisibility(View.VISIBLE);
		}
		// old design
		// new design
		/*
		 * Button bt_delete = (Button) view.findViewById(R.id.button_delete);
		 * Button bt_apply = (Button)view.findViewById(R.id.button_apply);
		 * TextView tv_applied =
		 * (TextView)view.findViewById(R.id.textview_applied);
		 */
		// new design

		// new design
		/*
		 * if((1 ==
		 * cursor.getInt(cursor.getColumnIndex(HResDatabaseHelper.RES_USE)))) {
		 * tv_applied.setVisibility(View.VISIBLE);
		 * bt_delete.setVisibility(View.GONE);
		 * bt_apply.setVisibility(View.GONE); } else {
		 * tv_applied.setVisibility(View.GONE);
		 * bt_delete.setVisibility(View.VISIBLE);
		 * bt_apply.setVisibility(View.VISIBLE); }
		 * bt_apply.setOnClickListener(new
		 * onCheckListener(cursor.getInt(cursor.getColumnIndex("_id"))));
		 */
		// new design

		String packname = cursor.getString(cursor
				.getColumnIndex(HResDatabaseHelper.PACKAGENAME));
		String disname = cursor.getString(cursor
				.getColumnIndex(HResDatabaseHelper.DISPLAY_NAME));
		if (HConst.DEFAULT_PACKAGE_NAME.equalsIgnoreCase(packname)) {
			// new design
			// bt_delete.setVisibility(View.GONE);
			// new design

			tv_name.setText(getString(R.string.default_skin));
			fl.setVisibility(View.GONE);
			// tv_size.setText("0.0MB");
		} else {/*
				 * PackageManager pm = mContext.getPackageManager();
				 * ApplicationInfo appinfo; try { appinfo =
				 * pm.getApplicationInfo(packname,
				 * PackageManager.GET_META_DATA); disname =
				 * appinfo.loadLabel(pm).toString(); } catch
				 * (NameNotFoundException e) { // TODO Auto-generated catch
				 * block e.printStackTrace(); }
				 */
			tv_name.setText(disname);
			/*
			 * DecimalFormat df = new DecimalFormat();
			 * df.setMaximumFractionDigits(1); df.setMinimumFractionDigits(1);
			 * float size =
			 * (float)cursor.getInt(cursor.getColumnIndex(HResDatabaseHelper
			 * .TOTAL_SIZE)) / UNIT; String s = df.format(size);
			 * tv_size.setText(s + "MB");
			 */
		}
		// old design
		if ((1 == cursor.getInt(cursor
				.getColumnIndex(HResDatabaseHelper.RES_USE)))) {
			bt_apply.setBackgroundResource(R.drawable.res_selected);
			if(HConst.CHARGE_TRY == cursor.getInt(cursor.getColumnIndex(HResDatabaseHelper.CHARGE)))
			{
				bt_apply.setText(getString(R.string.tried_skin));
			}
			else
			{
				bt_apply.setText(getString(R.string.skin_bt_useing));
			}
			bt_apply.setEnabled(false);
			fl.setVisibility(View.GONE);
		} else {
			bt_apply.setBackgroundResource(R.drawable.res_apply);
			if(HConst.CHARGE_TRY == cursor.getInt(cursor.getColumnIndex(HResDatabaseHelper.CHARGE)))
			{
				bt_apply.setText(getString(R.string.try_skin));
			}
			else
			{
				bt_apply.setText(getString(R.string.skin_bt_use));
			}
			bt_apply.setEnabled(true);
		}
		bt_apply.setOnClickListener(new onApplyListener(cursor.getInt(cursor
				.getColumnIndex("_id"))));
		// old design

		final String packagename = mInstalledCursor.getString(mInstalledCursor
				.getColumnIndex(HResDatabaseHelper.PACKAGENAME));
		final String name = disname;
		String res_id = mInstalledCursor.getString(mInstalledCursor
				.getColumnIndex(HResDatabaseHelper.RES_ID));
		res_id = (res_id == null || res_id.trim().equals("")) ? "0" : res_id;
		//final String resid = res_id;
		final String filename = cursor.getString(cursor
				.getColumnIndex(HResDatabaseHelper.FILE_NAME));
		fl.setOnClickListener(new onCheckListener(filename));
		// new Design
		/*
		 * bt_delete.setOnClickListener(new OnClickListener(){
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub new AlertDialog.Builder(mContext) .setTitle(name) .setItems(new
		 * String[]{getString(R.string.delete)}, new
		 * DialogInterface.OnClickListener(){
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) { //
		 * TODO Auto-generated method stub Uri uri = Uri.parse("package:" +
		 * packagename); Intent i = new Intent(Intent.ACTION_DELETE, uri);
		 * startActivity(i); }}) .create() .show(); }});
		 */
		// new Design

		// old design
		if (!(packagename.equals(HConst.DEFAULT_PACKAGE_NAME) || packagename
				.equals(SkinManage.mCurrentSkin))) {
			view.setLongClickable(true);
			view.setOnLongClickListener(new View.OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					new AlertDialog.Builder(mContext).setTitle(name).setItems(
							new String[] { getString(R.string.delete) },
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									// 卸载
									/*
									 * jayce delete HStatistics mStatistics =
									 * new HStatistics( HResLibActivity.this);
									 * mStatistics.add(HStatistics.Z12_3, resid,
									 * "", "");
									 */
									/*
									 * jayce delete Uri uri =
									 * Uri.parse("package:" + packagename);
									 * Intent i = new
									 * Intent(Intent.ACTION_DELETE, uri);
									 * startActivity(i);
									 */
									deleteSkin(filename);
									initMy();
								}
							}).create().show();
					return false;
				}
			});
		}
		// old design
	}

	private void deleteSkin(String filename) {
		File file = null;
		if(filename.contains("default"))
		{
			file = new File(mContext.getApplicationContext().getFilesDir().getAbsolutePath() + 
					File.separator + "default"+ File.separator + filename);
		}
		else
		{
			file = new File(DLManager.LOCAL_PATH + File.separator + filename);
		}
		if (file.exists()) {
			file.delete();
		}
		mContext.getContentResolver().delete(HResProvider.CONTENT_URI_SKIN,
				HResDatabaseHelper.FILE_NAME + "= '" + filename + "'", null);
	}

	private ProgressDialog mProgressDialog;

	private Handler mChangeSkinHandler = new Handler() {
		public void handleMessage(Message msg) {
			initMy();
			if (null != mProgressDialog) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			Toast.makeText(mContext, getString(R.string.skin_changed),
					Toast.LENGTH_SHORT).show();
		}
	};

	private List<String> mDeleteList = new ArrayList<String>();
	private boolean mIsSelectAll = false;

	private class onCheckListener implements OnClickListener {
		private String mFilename;

		public onCheckListener(String filename) {
			mFilename = filename;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ImageView iv_check = (ImageView) v.findViewById(R.id.res_apply);
			if (View.VISIBLE == iv_check.getVisibility()) {
				mDeleteList.remove(mFilename);
				iv_check.setVisibility(View.GONE);
			} else {
				mDeleteList.add(mFilename);
				iv_check.setVisibility(View.VISIBLE);
			}
		}

	}

	private class onApplyListener implements OnClickListener {
		private int mId;

		public onApplyListener(int id) {
			mId = id;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (null == mProgressDialog) {
				mProgressDialog = new ProgressDialog(mContext);
				mProgressDialog.setMessage(mContext
						.getString(R.string.skin_changing));
				mProgressDialog.show();
			}
			new Thread() {
				public void run() {
					ContentValues cvalues = new ContentValues();
					cvalues.put(HResDatabaseHelper.RES_USE, 0);
					mContext.getContentResolver().update(
							HResProvider.CONTENT_URI_SKIN, cvalues, null, null);
					ContentValues values = new ContentValues();
					values.put(HResDatabaseHelper.RES_USE, 1);
					mContext.getContentResolver().update(
							HResProvider.CONTENT_URI_SKIN, values,
							"_id = '" + mId + "'", null);
					Cursor c = mContext.getContentResolver().query(
							HResProvider.CONTENT_URI_SKIN, null,
							HResDatabaseHelper.RES_USE + " = '1'", null, null);
					if (c.getCount() > 0) {
						c.moveToNext();
						HStatistics mHStatistics = new HStatistics(
								HResLibActivity.this);
						int charge = c.getInt(c
								.getColumnIndex(HResDatabaseHelper.CHARGE));
						String resid = c.getString(c
								.getColumnIndex(HResDatabaseHelper.RES_ID));
						resid = (resid == null || resid.trim().equals("")) ? "0"
								: resid;
						// 应用
						// mStatistics.add(HStatistics.Z12_4, resid, "", "");
						mHStatistics.add(HStatistics.Z12_1, resid,
								(charge == 0) ? "0" : "1", "1");
						SkinManage.mCurrentSkin = c
								.getString(c
										.getColumnIndex(HResDatabaseHelper.PACKAGENAME));
						SkinManage.mCurrentFile = c.getString(c
								.getColumnIndex(HResDatabaseHelper.FILE_NAME));
					}
					c.close();
					mChangeSkinHandler.sendEmptyMessage(0);
					
					//设置更换模版的时间
					HConst.setTheCheckTemTime(mContext);
					
				}
			}.start();
			/*
			 * if(mView != null) { mView.removeAllViews(); } mView =
			 * (SkinLinearLayout)SkinLinearLayout.inflate(mContext,
			 * R.layout.res_mine, null); setContentView(mView);
			 */
			// initView();
		}
	}

	//----------------------------------------------MY
	//**********************************************资源库
	private GestureDetector mDetector;

	private ViewFlipper mFlipper;

	int count = 0;

	private int getColor(int id) {
		return getResources().getColor(id);
	}

	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	private static final int FLING_MIN_DISTANCE = 100;
	private static final int FLING_MIN_VELOCITY = 200;
	protected static boolean Filing = false;
    /*
     * (non-Javadoc)
     *滑动
     */
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,

	float arg3) {
		Filing = false;
		if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY() - e2.getY())) {

			if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
				this.mFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
						R.anim.push_left_in));
				this.mFlipper.setOutAnimation(AnimationUtils.loadAnimation(
						this, R.anim.push_left_out));

				if (count < 3) {
					Filing = true;
					
					this.mFlipper.showNext();
					count++;
						switch (count) {
						case 0:
							mStatistics.add(HStatistics.Z8_1, "", "", "");
							break;
						case 1:
							mStatistics.add(HStatistics.Z8_2, "", "", "");
							break;
						case 2:
							mStatistics.add(HStatistics.Z8_3, "", "", "");
							break;
						case 3:
							mStatistics.add(HStatistics.Z8_4, "", "", "");
							break;
						}
						moveSetChoic(count);
					}

				return true;

			} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {

				this.mFlipper.setInAnimation(AnimationUtils.loadAnimation(this,

				R.anim.push_right_in));

				this.mFlipper.setOutAnimation(AnimationUtils.loadAnimation(
						this,

						R.anim.push_right_out));

				if (count > 0) {
					Filing = true;
					this.mFlipper.showPrevious();
					count--;
					switch (count) {
					case 0:
						mStatistics.add(HStatistics.Z8_1, "", "", "");
						break;
					case 1:
						mStatistics.add(HStatistics.Z8_2, "", "", "");
						break;
					case 2:
						mStatistics.add(HStatistics.Z8_3, "", "", "");
						break;
					case 3:
						mStatistics.add(HStatistics.Z8_4, "", "", "");
						break;
					}
					moveSetChoic(count);
				}

				return true;

			}
		}
		return true;

	}

	public void onLongPress(MotionEvent arg0) {

	}

	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,

	float arg3) {

		return false;

	}

	public void onShowPress(MotionEvent arg0) {

	}

	public boolean onSingleTapUp(MotionEvent arg0) {

		// TODO Auto-generated method stub

		return false;

	}

	class MyOnTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			return mDetector.onTouchEvent(event);
		}

	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		this.mDetector.onTouchEvent(ev);
		//滑动 不触发点击事件
		if (Filing) {
			Filing = false;
			return false;
		}

		return super.dispatchTouchEvent(ev);
	}
   /*
    * 滑动设置list列表显示
    */
	private void moveSetChoic(int count) {
		switch (count) {
		case 0:
			setChoice(R.id.text_hot_res);
			setLayoutAndToastMonthRate();
			break;
		case 1:
			setChoice(R.id.text_free_res);
			setLayoutAndToastMonthRate();
			break;
		case 2:
			setChoice(R.id.text_new_res);
			setLayoutAndToastMonthRate();
			break;
		case 3:
			if(month_rate_layout != null)
			{
				month_rate_layout.setVisibility(View.GONE);
			}
			setChoice(R.id.text_my_res);
			break;
		}
	}

	ProgressDialog updataDialog = null;
	Handler diaHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				showDialog();
			} else if (msg.what == 0) {
				closeDialog();
			} else if (msg.what == 3) {
				showDialogNoCancal();
			} else if (msg.what == 2) {
				closeNoCancel();
			} else if (msg.what == 4) {
				moveSetChoic(3);
			}

		};
	};
	Dialog mDialog = null;
	Dialog mDialog1 = null;
	String showPayDialog = "-1";

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// // TODO Auto-generated method stub
	// if (keyCode == KeyEvent.KEYCODE_BACK){
	// showPayDialog = true;
	// if(mDialog1!=null&&mDialog1.isShowing()){
	// diaHandler.sendEmptyMessage(2);
	// }
	//    		
	// if(mConfirmDialog != null&&mConfirmDialog.isShowing()){
	//    			
	// mConfirmDialog.dismiss();
	// mConfirmDialog = null;
	// }
	// }
	// return super.onKeyDown(keyCode, event);
	// }
	private void showDialog() {
		if (mDialog == null) {
			View v = getLayoutInflater().inflate(R.layout.dialogview, null);
			View layout = v.findViewById(R.id.dialog_view);
			mDialog = new Dialog(this, R.style.MyFullHeightDialog);
			mDialog.setContentView(layout);
			// mDialog.setCancelable(false);
			if(!isDestroy)
			{	
				mDialog.show();
			}
		} else {
			if(!isDestroy)
			{
				mDialog.show();
			}
		}
	}

	private void showDialogNoCancal() {
		if (mDialog1 == null) {
			View v = getLayoutInflater().inflate(R.layout.dialogview, null);
			View layout = v.findViewById(R.id.dialog_view);
			mDialog1 = new Dialog(this, R.style.MyFullHeightDialog);
			mDialog1.setContentView(layout);
			mDialog1.setCancelable(false);
			mDialog1.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					showPayDialog = "-1";
					if (mDialog1 != null && mDialog1.isShowing()) {
						// diaHandler.sendEmptyMessage(2);
						mDialog1.dismiss();
						mDialog1 = null;
					}

				}
			});
			mDialog1.show();
		} else {
			mDialog1.show();
		}
	}

	private void closeDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
			mDialog = null;
		}
	}

	private void dimisDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
		if(mDialog1 != null && mDialog1.isShowing())
		{
			mDialog1.dismiss();
		}
	}
	
	private void closeNoCancel() {
		if (mDialog1 != null && mDialog1.isShowing()) {
			mDialog1.dismiss();
			mDialog1 = null;
		}
	}

	@Override
	protected void onPause() {
		dimisDialog();
		super.onPause();
		HLog.d("list", "onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		mDialog = null;
		mDialog1 = null;
	}

	private boolean isDestroy = false;
	@Override
	protected void onDestroy() {
		isDestroy = true;
		back = false;
		//更新新模版时间戳
		HSharedPreferences hsp = new HSharedPreferences(HResLibActivity.this);
		hsp.setNewListReqTime(hsp.getNewListTime());
		super.onDestroy();
		// unregisterReceiver(mBroadcastInstalled);
	}

	List<DLData> mDLDatalist;
	private DLManager mDLManager;

	public class HResLibAdapter extends BaseAdapter {
		/*免费**/
		final int mTYPEHOT = 1;
		/*付费**/
		final int mTYPEFREE = 0;
		/*畅销**/
		final int mTYPEFNEW = 2;
		
		private int mFlag;
		private Context mContext;
		
		private ArrayList<HResLibModel> mResList = new ArrayList<HResLibModel>();
		
		private GridView mListView;
		Handler diaHandler;
		PayBussiness mPayBussiness;
		
		// private DLData task;
		HStatistics mStatistics;

		public HResLibAdapter(Activity context, int flag, GridView view,
				boolean showDialog, Handler diaHandler) {
			this.diaHandler = diaHandler;
			this.mContext = context;
			this.mFlag = flag;
			this.mListView = view;
			
			mStatistics = new HStatistics(mContext);
			if (ToolsUtil.mIDlist != null) {
				ToolsUtil.mIDlist.clear();
			}
			if (!showDialog) {
				diaHandler.sendEmptyMessage(1);
			}
			getResList(flag, 1, 1);
			mCount = mResList.size();
			pageIndex = 1;
			
			// true 即为MM商城，则提前进行初始化
			mPayBussiness = new PayBussiness();
			
			if (ToolsUtil.MM_FLAG) {
				mPayBussiness.applyPay(context, startPayHandler);
			}
			mDLManager.setProgressHandler(mDownLoadHandler);
		}
         /**
          * 设置列表背景
          */
		private void setGridBg() {
			mCount = mResList.size();
			if (mCount == 0) {
				mListView.setBackgroundResource(R.drawable.no_net);
			} else {
				mListView.setBackgroundDrawable(null);
			}
			HResLibAdapter.this.notifyDataSetChanged();
		}
		
		// 获取支付是否成功
		private Handler startPayHandler = new Handler() {
			public void handleMessage(Message msg) {
				diaHandler.sendEmptyMessage(2);
				int what = msg.what;
				if (what == 200) {
					// 支付成功开始正常下载
					DonwloadModel(mResModel);
					if(mResModel.getPl().equals("1")){
						mResModel.setPl("-1");
					}
					HResLibAdapter.this.notifyDataSetChanged();

				} else if (what == 100) {
					// 支付失败 提醒用户
				}
			};
		};
		
		/**
		 * 获得缩略图成功刷新 adapter
		 */
		private Handler mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (HResLibAdapter.this != null) {
					if (msg.what == ResourceFileCache.RESOURCE_SUCCESS) {
						HResLibAdapter.this.notifyDataSetChanged();
					}
				}
			}

		};
		//TODO 
		int mCount = 0;

		@Override
		public int getCount() {
			return mCount;
		}

		@Override
		public Object getItem(int position) {
			return mResList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		DecimalFormat format = new DecimalFormat("0.0");

		String yuan;

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MyHolder holder = new MyHolder();
			HResLibModel resModel = mResList.get(position);
			
			int id = 1;
			
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.res_adapter_item, null);

				holder.res_view = convertView.findViewById(R.id.res);
				holder.titleIv = (ImageView) convertView
						.findViewById(R.id.res_title);
				holder.new_tem_icon = (ImageView) convertView.findViewById(R.id.res_new_tem_icon);
				
				holder.titleTv = (TextView) convertView
						.findViewById(R.id.res_title_text);
				holder.starBar = (RatingBar) convertView
						.findViewById(R.id.res_star);
				holder.qBuyButton = (Button) convertView
						.findViewById(R.id.res_quick_buy);
				holder.reviewTv = (TextView) convertView
						.findViewById(R.id.res_review);
				// holder.payTv = (TextView) convertView
				// .findViewById(R.id.res_title_pay);
				
				holder.progressBar = (ProgressBar) convertView
						.findViewById(R.id.res_detail_progressbar_new);
				holder.percentTv = (TextView) convertView
						.findViewById(R.id.res_percent);
				holder.stageIv = (ImageView) convertView
						.findViewById(R.id.res_stage);
				holder.proView = (View) convertView
						.findViewById(R.id.res_progressBar);
				convertView.setTag(holder);
			} else {
				holder = (MyHolder) convertView.getTag();
			}
			
			
			try {
				// String isPay = resModel.getPl();
				String title = resModel.getPn();
				String pageName = resModel.getPkn();
				String yuan = resModel.getPr();
				if (resModel.getPl().equals("0"))
				yuan = mContext.getString(R.string.res_text_free);
				id = Integer.parseInt(resModel.getMi().trim());
				
				if(ToolsUtil.mIDlist.contains(resModel.getMi()))
				{
					//如果该模版之前在详情里面下载过，就把其付费状态设置为 -1, 而且按钮文字设置为“已付费"
					resModel.setPl("-1");
				}
				
				//判断该模版是否是新模版
				if(resModel.getMn().equals("1"))
				{
					holder.new_tem_icon.setVisibility(View.VISIBLE);
				}else if(resModel.getMn().equals("0"))
				{
					holder.new_tem_icon.setVisibility(View.GONE);
				}
				
				if(resModel.getPl().equals("-1"))
				{
					yuan = mContext.getString(R.string.res_payed);
				}
				
				holder.qBuyButton.setBackgroundResource(R.drawable.res_de_down);
				holder.qBuyButton.setEnabled(true);
				//获得任务状态
				int isDown = getStatus(pageName);
				switch (isDown) {
				case INSTALLED:
					holder.qBuyButton.setText(mContext
							.getText(R.string.res_check));
					holder.qBuyButton.setVisibility(View.VISIBLE);
					holder.proView.setVisibility(View.GONE);
					break;
				case DOWNLOADED:
					holder.qBuyButton.setText(mContext
							.getText(R.string.skin_bt_use));
					holder.qBuyButton.setVisibility(View.VISIBLE);
					holder.proView.setVisibility(View.GONE);
					holder.qBuyButton
							.setBackgroundResource(R.drawable.res_install_new);
					break;
				case NO_DOWNLOAD:
					holder.qBuyButton.setText(yuan);
					holder.qBuyButton.setVisibility(View.VISIBLE);
					holder.proView.setVisibility(View.GONE);
					holder.qBuyButton
							.setBackgroundResource(R.drawable.res_de_down);
					break;
				case DOWNLOADING:
					holder.qBuyButton.setVisibility(View.GONE);
					holder.proView.setVisibility(View.VISIBLE);
                   //及时刷新进度
					for (DLData dldata : mDLDatalist) {
						if (dldata.getPackagename().equals(pageName)) {
							DecimalFormat df = new DecimalFormat();
							df.setMaximumFractionDigits(1);
							df.setMinimumFractionDigits(1);
							float progress = 0.0f;
							if (dldata.getCurrentSize() > 0
									&& dldata.getTotalSize() > 0) {
								progress = (float) dldata.getCurrentSize()
										* 100.0f / dldata.getTotalSize();
							}
							holder.stageIv.setEnabled(true);
							if ((int) progress >= 100) {
								holder.stageIv.setEnabled(false);
								holder.qBuyButton.setText(mContext
										.getText(R.string.skin_bt_use));
								holder.qBuyButton.setVisibility(View.VISIBLE);
								holder.proView.setVisibility(View.GONE);
								holder.qBuyButton
										.setBackgroundResource(R.drawable.res_install_new);
							} else {
								String p = df.format(progress);
								holder.percentTv.setText(p + "%");
								holder.progressBar
										.setMax(dldata.getTotalSize());
								holder.progressBar.setProgress(dldata
										.getCurrentSize());
								if(HConst.markActivity == 8)
								mDLManager.setProgressHandler(mDownLoadHandler);
							}
							holder.stageIv
									.setOnClickListener(new OnStateListener(
											dldata.getId()));
							switch (dldata.getStatus()) {
							case DLManager.STATUS_READY:
								holder.stageIv
										.setBackgroundResource(R.drawable.btn_wait);
								break;
							case DLManager.STATUS_RUNNING:
								holder.stageIv
										.setBackgroundResource(R.drawable.btn_pause);
								break;
							case DLManager.STATUS_PAUSE:
								holder.stageIv
										.setBackgroundResource(R.drawable.btn_start);
								break;
							}

						}

					}

					break;
				}
				holder.titleTv.setText(title);
				// holder.payTv.setText(yuan);

				holder.qBuyButton.setId(position);
				// holder.qBuyButton.setOnClickListener(buttonListener);
				holder.qBuyButton
						.setOnClickListener(new DonwLoadButtonListener(isDown));
				holder.res_view.setId(position);
				holder.res_view.setOnClickListener(resViewItemListner);

				// String review = mContext.getString(R.string.res_ml);
				String review = mContext.getString(R.string.download_times)
						+ resModel.getMd();

				// review = review.replace("*", resModel.getMd());
				holder.reviewTv.setText(review);

				float len = Float.valueOf(resModel.getEt().trim());
				holder.starBar.setRating(len);

				String titleImgUrl = resModel.getIn();
				holder.titleIv.setBackgroundResource(R.drawable.logo);
				InputStream is = CacheManager.newInstance().getResourceCache(
						titleImgUrl);

				if (is == null) {
					ImageUtil.addDownloadTask(mContext, resModel.getP(),
							titleImgUrl, mHandler);
				} else {
					Bitmap bitmap = null;
					bitmap = BitmapFactory.decodeStream(is);
					System.gc();
					if (bitmap != null) {
						holder.titleIv
								.setBackgroundDrawable(new BitmapDrawable(
										bitmap));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			convertView.setId(id + 100000);
			System.out.println(resModel.getPn() + "id : "+ id );
			return convertView;
		}
		
        /**
         * 列表中 下载button
         * @author Administrator
         *
         */
		class DonwLoadButtonListener implements OnClickListener {
			int isDown = 0;

			public DonwLoadButtonListener(int isDown) {
				this.isDown = isDown;
			}

			@Override
			public void onClick(View v) {
				HResLibModel resModel = mResList.get(v.getId());
				// if (!onClickButton) {
				// onClickButton = true;
				Button b = (Button) v;
				if(b.getText().toString().equals(mContext.getText(R.string.skin_bt_use))){
					v.setEnabled(false);
					b.setText(mContext
							.getText(R.string.res_check));
					b.setBackgroundResource(R.drawable.res_de_down);
					buttonEvent(resModel, 0);
					return;
				}
				switch (isDown) {
				case DOWNLOADING:

					break;
				case INSTALLED:
					//模板查看数
					mStatistics.add(HStatistics.Z10_7, resModel.getMi(), "", "");
					
					// onMineActivity();
					diaHandler.sendEmptyMessage(4);
					break;
				case DOWNLOADED:
					v.setEnabled(false);
					buttonEvent(resModel, 0);
					break;
				case NO_DOWNLOAD:
					showPayDialog = resModel.getMi();
					String payStatus = resModel.getPl();
					if(resModel.getPl().equals("-1"))
					{
						payStatus = "1";
					}
					showPayDialog = resModel.getMi();
					mStatistics.add(HStatistics.Z10_1, resModel.getMi(), ""+type, payStatus);
					mStatistics.add(HStatistics.Z10_3, resModel.getMi(), "", "");
					buttonEvent(resModel, 1);
					break;

				}
				// }

			}

		}
         /**
          * 进度条 暂停button
          * @author Administrator
          *
          */
		private class OnStateListener implements OnClickListener {
			private long mID;

			public OnStateListener(long id) {
				mID = id;
			}

			@Override
			public void onClick(View v) {

				if (mID <= 0) {
					return;
				}
				DLData task = null;
				for (DLData d : mDLDatalist) {
					if (d.getId() == mID) {
						task = d;
					}
				}
				if (null == task) {
					return;
				}
				if (DLManager.STATUS_RUNNING == task.getStatus()) {
					mDLManager.pauseTask(task.getId());
					HResLibAdapter.this.notifyDataSetChanged();
					return;
				}
				if (DLManager.STATUS_PAUSE == task.getStatus()) {
					mDLManager.restartTask(task.getId());
					HResLibAdapter.this.notifyDataSetChanged();
					return;
				}
				if (DLManager.STATUS_READY == task.getStatus()) {
					mDLManager.pauseTask(task.getId());
					HResLibAdapter.this.notifyDataSetChanged();
					return;
				}
				if (DLManager.STATUS_SUCCESS == task.getStatus()) {
					HResLibAdapter.this.notifyDataSetChanged();
					return;
				}
			}
		}

		public void onMineActivity() {
			// HStatistics mStatistics = new HStatistics(mContext);
			// mStatistics.add(HStatistics.Z8_4, "", "", "");
			// Intent intent = new Intent(mContext, HResMineActivity.class);
			// mContext.startActivity(intent);
		}

		/** 限制快速重复点击 ，出此下策情非得已 */
		boolean onClickButton = false;
		long time = 0;
		long startTime = 0;
        /**
         * 下载过程
         * @param model
         * @param flag
         */
		private void buttonEvent(final HResLibModel model, int flag) {
			if (!ToolsUtil.checkSDcard()) {
				Toast.makeText(mContext, mContext.getString(R.string.plugsd),
						Toast.LENGTH_LONG).show();
				return;
			}
			String pay = model.getPl();
			//如果是付费模版
			if (pay.equals("1"))
				//检测是否在详情中下载了
				for (int i = 0; i < ToolsUtil.mIDlist.size(); i++) {
					if (model.getMi().equals(
							ToolsUtil.mIDlist.get(i).toString())) {
						pay = "-1";
						if(!model.getPr().equals(mContext.getString(R.string.skin_sometime_free)) ){
							model.setPl(pay);
						}
						
						break;
					}
				}
			
			//如果不是免费
			if (!pay.equals("0") && !model.getPr().equals(getString(R.string.res_text_free)))
				extracted(model, flag, pay);
			else {  //免费
				switch (flag) {
				case 0:// 安装
					installClick(model);
					break;
				case 1:// 下载
					DonwloadModel(model);
					break;
				}
			}
		}
        //付费模版下载
		private void extracted(final HResLibModel model, int flag, String pay) {
			{

				SharedPreferences pref = mContext.getSharedPreferences(
						HConst.PREF_USER, Context.MODE_PRIVATE);
				boolean isLogin = pref.getBoolean(HConst.USER_KEY_LOGIN, false);
				HLog.d("isLogin", String.valueOf(isLogin));
				boolean Im = ToolsUtil.IM_FLAG;
				 //如果是IM模式&&未登录状态下 先登录 否则直接下载
				if (Im && !isLogin) { 
					if (flag == 1)// 统计模板未登录状态下点击下载数
						mStatistics.add(HStatistics.Z10_3, model.getMi(), "",
								"");
					Toast.makeText(mContext,
							mContext.getString(R.string.login_toast),
							Toast.LENGTH_SHORT).show();
					mContext.startActivity(new Intent(mContext,
							HResLoginActivity.class));
					onClickButton = false;
				} else {
					switch (flag) {
					case 0:// 安装
						installClick(model);
						break;
					case 1:// 下载
						if (pay.equals("1")) {
							if (!ToolsUtil.MM_FLAG) {
								diaHandler.sendEmptyMessage(3);
							}
							new Thread() {
								@Override
								public void run() {
									Looper.prepare();
									if (!ToolsUtil.MM_FLAG) {
										Object obj = mPayBussiness.applyPay(
												HResLibActivity.this, model
														.getMi(), ToolsUtil
														.getPhoneNum(mContext));
										Message msg = new Message();
										msg.obj = obj;
										Bundle data = new Bundle();
										data.putString("Mi", model.getMi());
										msg.setData(data);
										mResModel = model;
										payHandler.sendMessage(msg);
									} else {
										// MM商城流程, 直接弹出支付框
										mResModel = model;
										mPayBussiness
												.startPay(
														HResLibActivity.this,
														model.getMi(),
														ToolsUtil
																.getPhoneNum(HResLibActivity.this),
																false
																);

									}
									Looper.loop();
								}
							}.start();

						} else {

							DonwloadModel(model);
						}
						break;
					}
				}
			}
		}

		// Button button;
		HResLibModel mResModel;
		private Handler payHandler = new Handler() {
			public void handleMessage(Message msg) {
				Bundle bundle = msg.getData();
				String id = bundle.getString("Mi");
				HMoldPay hmp = (HMoldPay) msg.obj;
				if ((mResModel != null && mResModel.getMi().equals(id))) {
					if (!showPayDialog.equals(id)) {
						return;
					}
					downloadClick(id, ToolsUtil.getPhoneNum(mContext), hmp,
							mResModel);
				}

			};
		};

		/***
		 * 收费模版点击下载的时候进行收费提示，点击确认付费按钮后才开始进行下载
		 * 
		 * @param resId
		 *            模版ID
		 * @param phoneNumber
		 *            本机电话号码
		 */
		private boolean downloadClick(final String resId,
				final String phoneNumber, final HMoldPay hmd,
				final HResLibModel model) {
			String message = "";
			// 得到付费提示信息
			try {
				message = hmd.getPayRc();
				if (message == null) {
					HResLibAdapter.this.notifyDataSetChanged();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(mContext,
						mContext.getString(R.string.toast_info_new),
						Toast.LENGTH_SHORT).show();
				diaHandler.sendEmptyMessage(2);
				return false;
			}
			diaHandler.sendEmptyMessage(2);
			if (mConfirmDialog == null) {

				if (!ToolsUtil.readSIMCard(mContext)) {
					mConfirmDialog = new AlertDialog.Builder(mContext)
							.setTitle(mContext.getString(R.string.notice))
							.setMessage(mContext.getString(R.string.simInvalid))
							.setPositiveButton(
									mContext.getString(R.string.confirm),
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											if (null != mConfirmDialog) {
												mConfirmDialog.dismiss();
												mConfirmDialog = null;
											}
										}
									}).create();
					mConfirmDialog.show();
					onClickButton = false;
					return false;
				} else if (!ToolsUtil.checkNet(mContext)) {
					mConfirmDialog = new AlertDialog.Builder(mContext)
							.setTitle(mContext.getString(R.string.notice))
							.setMessage(mContext.getString(R.string.no_connect))
							.setPositiveButton(
									mContext.getString(R.string.confirm),
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											if (null != mConfirmDialog) {
												mConfirmDialog.dismiss();
												mConfirmDialog = null;
											}
											diaHandler.sendEmptyMessage(0);
										}
									}).create();
					mConfirmDialog.show();
					onClickButton = false;
					return false;
				}
				{
					mConfirmDialog = new AlertDialog.Builder(mContext)
							.setTitle(
									mContext.getString(R.string.spopu_bt_dl)
											+ ":" + model.getPn()).setMessage(
									message)
							/* .setCancelable(false) */.setPositiveButton(
									mContext.getString(R.string.confirm),
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											if (null != mConfirmDialog) {
												mConfirmDialog.dismiss();
												mConfirmDialog = null;
											}
											
											//等待
											diaHandler.sendEmptyMessage(3);
											
											onClickButton = false;
											// diaHandler.sendEmptyMessage(1);
											// 进行付费动作
											HStatistics mHStatistics = new HStatistics(
													mContext);
											mHStatistics.add(HStatistics.Z11_1,
													model.getMi(), "", "");
//											mPayBussiness.startPay(
//													HResLibActivity.this,
//													startPayHandler, hmd,
//													phoneNumber);
											
											new Thread(){
												public void run() {
													Looper.prepare();
													mPayBussiness.startPay(
															HResLibActivity.this,
															startPayHandler, hmd,
															phoneNumber);
													Looper.loop();
												};
											}.start();
											
											// 把该模板的付费状态设置为已付费
//											if(!model.getPr().equals(mContext.getString(R.string.skin_sometime_free)) ){
//												model.setPl("-1");
//												
//											}
//											DonwloadModel(model);
//											HResLibAdapter.this
//													.notifyDataSetChanged();
										}
									}).setNegativeButton(
									mContext.getString(R.string.cancel),
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											if (null != mConfirmDialog) {
												mConfirmDialog.dismiss();
												mConfirmDialog = null;
											}
											onClickButton = false;
											HStatistics mHStatistics = new HStatistics(
													mContext);
											mHStatistics.add(HStatistics.Z11_2,
													model.getMi(), "", "");
											HResLibAdapter.this
													.notifyDataSetChanged();
											diaHandler.sendEmptyMessage(0);
										}
									}).create();
					mConfirmDialog.show();
				}
			}
			return false;
		}
		
		View view = null;
		//下载handler，getView中设置Item的id ，通过hanler返回的 ID findView
		Handler mDownLoadHandler = new Handler() {
			public void handleMessage(Message msg) {
				Bundle bundle = msg.getData();
				DLData dldata = (DLData) bundle.getSerializable("task");
				System.out.println("msg : "+ msg.what);
				switch (msg.what) {
				case DLManager.ACTION_DELETE:
					HResLibAdapter.this.notifyDataSetChanged();
					break;
				case DLManager.ACTION_SUCCESS:
					HResLibAdapter.this.notifyDataSetChanged();
					break;
				case DLManager.ACTION_UPDATE:
					
					if(dldata.getStatus() == DLManager.STATUS_FAILED)
					{
						mDLManager.deleteTask(dldata.getId());
						HResLibAdapter.this.notifyDataSetChanged();
						break;
					}
					if (dldata != null){
						if(view == null){
							view = mListView.findViewById(Integer.parseInt(dldata
									.getResID().trim()) +100000);
							System.out.println(" view = nullllll");
							
						}else{
							if(view.getId() != Integer.parseInt(dldata
									.getResID().trim())){
								view = mListView.findViewById(Integer.parseInt(dldata
										.getResID().trim())+100000);
								System.out.println(" view getId ");
							}
						}
					}
					System.out.println("dldata : "+ dldata + "  view : "+ view + "  id"+dldata
							.getResID().trim());
					if (view != null) {
						ProgressBar progressBar = (ProgressBar) view
								.findViewById(R.id.res_detail_progressbar_new);
						TextView percentTv = (TextView) view
								.findViewById(R.id.res_percent);
						ImageView stageIv = (ImageView) view
								.findViewById(R.id.res_stage);
						DecimalFormat df = new DecimalFormat();
						df.setMaximumFractionDigits(1);
						df.setMinimumFractionDigits(1);
						float progress = 0.0f;
						if (dldata.getCurrentSize() > 0
								&& dldata.getTotalSize() > 0) {
							progress = (float) dldata.getCurrentSize() * 100.0f
									/ dldata.getTotalSize();
						}
						stageIv.setEnabled(true);
						
						if (progress >= 100) {
							stageIv.setEnabled(false);
							HResLibAdapter.this.notifyDataSetChanged();
							//Toast.makeText(mContext, "测试： "+ dldata.getDisplayName() + "下载成功", Toast.LENGTH_SHORT);
						}
						String p = df.format(progress);
						percentTv.setText(p + "%");
						System.out.println(" 进度："+ p);
						progressBar.setMax(dldata.getTotalSize());
						progressBar.setProgress(dldata.getCurrentSize());

					}
					break;
				}

			};
		};

		private void DonwloadModel(HResLibModel resModel) {
			mDLManager.setProgressHandler(mDownLoadHandler);
			DLData dlData = new DLData();
			dlData.setResID(resModel.getMi()); // 传入模板ID即可
			dlData.setFileName(resModel.getPu());
			dlData.setDisplayName(resModel.getPn());
			dlData.setCharge(resModel.getPl());
			dlData.setChargeMsg(resModel.getRc());

			dlData.setIconUrl(resModel.getIn());
			dlData.setPackagename(resModel.getPkn());
			mDLManager.addTask(dlData);
			/*
			 * Intent i = new Intent(mContext, HService.class); Bundle bundle =
			 * new Bundle(); bundle.putSerializable("task", dlData);
			 * mContext.startService(i);
			 */

			onClickButton = false;
			HResLibAdapter.this.notifyDataSetChanged();
		}

		OnClickListener resViewItemListner = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onClickButton == false) {
					if (!ToolsUtil.checkNet(mContext)) {
						Toast.makeText(mContext,
								mContext.getString(R.string.no_connect),
								Toast.LENGTH_LONG).show();
					} else {
						HResLibModel hlm = mResList.get(v.getId());
						Intent intent = new Intent();
						switch (mFlag) {
						case mTYPEFNEW:
							intent.putExtra("type", "2");
							// mStatistics.add(HStatistics.Z8_4, "", "", "");
							break;
						case mTYPEFREE:
							intent.putExtra("type", "0");
							// mStatistics.add(HStatistics.Z8_5, "", "", "");
							break;
						case mTYPEHOT:
							intent.putExtra("type", "1");
							// mStatistics.add(HStatistics.Z8_6, "", "", "");
							break;
						}

						intent.putExtra("HRes", hlm);
						intent.putExtra("ID", hlm.getMi());
						intent.setClass(HResLibActivity.this, HResDetailActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
						HResLibActivity.this.startActivityForResult(
								intent, 20);

					}
				}
			}
		};

		class MyHolder {
			View proView;
			TextView percentTv;
			ImageView stageIv;
			// TextView payTv;
			View res_view;
			/* 缩略图 */
			ImageView titleIv;
			
			/**新模版图标*/
			ImageView new_tem_icon;
			
			/* 模版标题 */
			TextView titleTv;
			/* 模版评星 */
			RatingBar starBar;
			/* 评论文字 */
			TextView reviewTv;
			Button qBuyButton;
			ProgressBar progressBar;
			/** 下载区域layout */
			// LinearLayout mProgressLayout;
		}

		class ResModel {
			String modelName = "";
			String modelSize = "";
			int starNum = 0;
			String reviewNum = "";

			public ResModel(String name, String size, int star, String review) {
				this.modelName = name;
				this.modelSize = size;
				this.starNum = star;
				this.reviewNum = review;
			}

		}

		
		
		/** 是否在加载列表中 */
		private boolean scrollLoad = false;
      
		
		/**
         * 下拉获得列表信息    10条请求一下
         * @param flag   付费，免费 ，畅销
         * @param start  第几条开始请求
         * @param page   第几页 （每10条一页）
         */
		private void getResList(int flag, int start, int page) {
            //获得缓存中列表信息
			ArrayList<HResLibModel> list = new ArrayList<HResLibModel>();
//			switch (flag) {
//			case mTYPEHOT:
//				list = new HResLibParser(mContext, HConst.RESLIB_PYLST, start)
//						.getResLibCacheList(ToolsUtil.getLanguage()
//								+ HConst.RESLIB_PYLST + page, page);
//
//				break;
//			case mTYPEFREE:
//				list = new HResLibParser(mContext, HConst.RESLIB_FRLST, start)
//						.getResLibCacheList(ToolsUtil.getLanguage()
//								+ HConst.RESLIB_FRLST + page, page);
//				break;
//			case mTYPEFNEW:
//				list = new HResLibParser(mContext, HConst.RESLIB_CXLST, start)
//						.getResLibCacheList(ToolsUtil.getLanguage()
//								+ HConst.RESLIB_CXLST + page, page);
//				break;
//			}
            
			if (list != null) {
				int len = list.size();
				if (len < 10) {
					nextList = false;
				}
				for (int i = 0; i < len; i++) {
					mResList.add(list.get(i));
					
//					if(month_rate_status == null){month_rate_status = "";}
//					if(month_rate_RC1 == null){month_rate_RC1 = "";};
//					if(month_rate_status.equals("") || month_rate_RC1.equals(""))
//					{	
//						//获取最新的资源库列表后获取包月状态和包月提示语
//						month_rate_status = list.get(i).getBy();
//						month_rate_RC1 = list.get(i).getRc1();
//						month_rate_RC2 = list.get(i).getRc2();
//						System.out.println("by : " + month_rate_status + " RC1 : " + month_rate_RC1 + " RC2 : " + month_rate_RC2);
//					}
				}
				cacheListNull = false;
				
				//setLayoutAndToastMonthRate();
				
			} else {
				cacheListNull = true;
				diaHandler.sendEmptyMessage(1);
				// if (pageIndex > 1)
				// pageIndex--;
			}
			scrollLoad = false;
			getListPage = page;
			
			//联网获取
			setGridBg();
			Message msg = new Message();
			Bundle data = new Bundle();
			data.putInt("page", page);
			data.putInt("start", start);
			msg.setData(data);
			msg.what = flag;
			getListHandler.sendMessage(msg);

		}

		private boolean cacheListNull = false;
		private int getListPage = 0;
		private Handler mLoadHandler = new Handler() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				ArrayList<HResLibModel> list = (ArrayList<HResLibModel>) msg.obj;
				int llen = 0;
				Bundle bundle = msg.getData();
				int index = bundle.getInt("page");
				int page = (index - 1) * HConst.REQUEST_NUMBER;
				String tabType = "";
				if (list != null) {
					int mlen = mResList.size();

					llen = list.size();
					if (llen < HConst.REQUEST_NUMBER && index == getListPage) {
						nextList = false;
					} else {
						nextList = true;
					}
					
					for (int i = 0; i < HConst.REQUEST_NUMBER; i++) {
						if(i == 0)
						{
							tabType = list.get(i).getI();
						}
						
						if (i >= llen) {
							if (page + i < mlen)
								mResList.remove((page + llen));

						} else {
							if (page + i < mlen) {
								mResList.set(page + i, list.get(i));
							} else {
								mResList.add(list.get(i));
							}
							
							//获取包月状态、包月是否过期提示语、20 25 30 隔断提醒
							if(month_rate_status.equals(""))
							{	
								//只有在付费里面的时候才会有这三项东西
								//获取最新的资源库列表后获取包月状态和包月提示语
								month_rate_status = list.get(i).getBy();
								if(month_rate_status == null){month_rate_status = "";}
								HConst.setMonthSTATU(mContext, month_rate_status);
							}
							if(month_rate_RC1.equals(""))
							{
								month_rate_RC1 = list.get(i).getRc1();
								if(month_rate_RC1 == null)
								{
									month_rate_RC1 = "";
								};
								HConst.setMonthRC1(mContext, month_rate_RC1);
							}
							if(month_rate_RC2.equals(""))
							{	
								month_rate_RC2 = list.get(i).getRc2();
								if(month_rate_RC2 == null){month_rate_RC2 = "";}
							}
						}

					}
					
					if(tabType != null && tabType.equals(HConst.RESLIB_PYLST))
					{	
						//包月固定第几天提醒用户还有几天到期
						setLayoutAndToastMonthRate();
					}
					
					//HResLibAdapter.this.notifyDataSetChanged();
				} else {
					if (!ToolsUtil.checkNet(mContext)) {
						Toast.makeText(mContext,
								mContext.getString(R.string.no_connect),
								Toast.LENGTH_LONG).show();
					} else {
						if (cacheListNull) {// netList&& caheList == null ,没有列表
							nextList = false;
							if (mResList.size() != 0)
								Toast
										.makeText(
												mContext,
												mContext
														.getText(R.string.loading_more),
												Toast.LENGTH_LONG).show();
						}

					}
				}
				scrollLoad = false;
				diaHandler.sendEmptyMessage(0);
				setGridBg();

			}

		};
		/** 是否有下一页 */
		private boolean nextList = true;
		private int pageIndex = 1;
		OnScrollListener osl = new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				int start = view.getCount();
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == start - 1) {

						if (nextList == false && mResList.size() != 0) {
//							Toast.makeText(mContext,
//									mContext.getText(R.string.loading_more),
//									Toast.LENGTH_LONG).show();
							return;
						}
						
						if (!scrollLoad && nextList) {
							scrollLoad = true;
							pageIndex++;
							getResList(mFlag, start + 1, pageIndex);
							HResLibAdapter.this.notifyDataSetChanged();
						}
					}
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		};
		
       /**
        * 获得列表信息
        */
		private Handler getListHandler = new Handler() {
			public void handleMessage(Message msg) {
				final int page = msg.getData().getInt("page");
				final int start = msg.getData().getInt("start");
				switch (msg.what) {
				case mTYPEHOT:
					new HResLibParser(mContext, HConst.RESLIB_PYLST, start)
							.getResLibNetList(ToolsUtil.getLanguage()
									+ HConst.RESLIB_PYLST + page, mLoadHandler,
									page);
					break;
				case mTYPEFREE:
					new HResLibParser(mContext, HConst.RESLIB_FRLST, start)
							.getResLibNetList(ToolsUtil.getLanguage()
									+ HConst.RESLIB_FRLST + page, mLoadHandler,
									page);

					break;
				case mTYPEFNEW:
					new HResLibParser(mContext, HConst.RESLIB_CXLST, start)
							.getResLibNetList(ToolsUtil.getLanguage()
									+ HConst.RESLIB_CXLST + page, mLoadHandler,
									page);

					break;
				}

			};
		};

		private final Handler finish = new Handler() {
			public void handleMessage(Message msg) {
				Toast.makeText(
						HResLibActivity.this,
						msg.getData().getString("disname")
								+ getString(R.string.skin_bt_useing),
						Toast.LENGTH_LONG).show();
				
				HResLibAdapter.this.notifyDataSetChanged();
			}
		};

		public static final int ACTION_UPDATE = 0;
		public static final int ACTION_DELETE = 1;
		Dialog mConfirmDialog;

		// private String chareStr = "";

		private void installClick(final HResLibModel info) {
			DLData task = new DLData();
			if (info.getPu() == null) {
				onClickButton = false;
				return;
			} else {

				task.setStatus(DLManager.STATUS_SUCCESS);
				task.setFileName(info.getPu());
				task.setCharge(info.getPl());
				task.setChargeMsg(info.getRc());
				task.setDisplayName(info.getPn());
				task.setPackagename(info.getPkn());
				task.setResID(info.getMi());
			}

			if (DLManager.STATUS_SUCCESS == task.getStatus()) {
				if (null != mConfirmDialog) {
					if (mConfirmDialog.isShowing()) {
						onClickButton = false;
						return;
					} else {
						onClickButton = false;
						mConfirmDialog.show();
					}
				} else {
					final DLData tas = task;
					int charge = 0;
					if (task.getCharge() != null
							&& !"".equals(task.getCharge())) {
						charge = Integer.valueOf(task.getCharge());
						// chareStr = String.valueOf(charge);
					}
					String message = "";

					if (charge == 1 || charge == -1) {
						message = String.format(mContext
								.getString(R.string.install_paid), task
								.getDisplayName());
					} else {
						message = String.format(mContext
								.getString(R.string.install_free), task
								.getDisplayName());
					}

					if (charge == 1) {
						if (!ToolsUtil.readSIMCard(mContext)) {
							mConfirmDialog = new AlertDialog.Builder(mContext)
									.setTitle(
											mContext.getString(R.string.notice))
									.setMessage(
											mContext
													.getString(R.string.simInvalid))
									.setPositiveButton(
											mContext
													.getString(R.string.confirm),
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													if (null != mConfirmDialog) {
														mConfirmDialog
																.dismiss();
														mConfirmDialog = null;
													}
												}
											}).create();
							mConfirmDialog.show();
							onClickButton = false;
							return;
						} else if (!ToolsUtil.checkNet(mContext)) {
							mConfirmDialog = new AlertDialog.Builder(mContext)
									.setTitle(
											mContext.getString(R.string.notice))
									.setMessage(
											mContext
													.getString(R.string.no_connect))
									.setPositiveButton(
											mContext
													.getString(R.string.confirm),
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													if (null != mConfirmDialog) {
														mConfirmDialog
																.dismiss();
														mConfirmDialog = null;
													}
												}
											}).create();
							mConfirmDialog.show();
							onClickButton = false;
							return;
						}
					}
					// else
					{
//						mConfirmDialog = new AlertDialog.Builder(mContext)
//								.setTitle(
//										mContext
//												.getString(R.string.confirm_install))
//								.setMessage(message).setPositiveButton(
//										mContext.getString(R.string.confirm),
//										new DialogInterface.OnClickListener() {
//
//											@Override
//											public void onClick(
//													DialogInterface dialog,
//													int which) {
//												mStatistics.add(
//														HStatistics.Z10_5_1,
//														info.getMi(), "", "");
												// }
												/*
												 * jayce delete Intent intent =
												 * new Intent(
												 * Intent.ACTION_VIEW); intent
												 * .setDataAndType( Uri
												 * .fromFile(new File(
												 * DLManager.LOCAL_PATH +
												 * File.separator + tas
												 * .getFileName())),
												 * "application/vnd.android.package-archive"
												 * ); intent.addFlags(Intent.
												 * FLAG_ACTIVITY_NEW_TASK);
												 * intent.addFlags(Intent.
												 * FLAG_ACTIVITY_MULTIPLE_TASK);
												 * mContext
												 * .startActivity(intent);
												 */
												// jayce add
												SkinManage.installSkin(
														mContext, tas, finish);
												
												//设置更换模版的时间
												HConst.setTheCheckTemTime(mContext);
												
												//模板应用点击数
												mStatistics.add(HStatistics.Z10_6, info.getMi(), "", "");
												
												if (null != mConfirmDialog) {
													mConfirmDialog.dismiss();
													mConfirmDialog = null;
												}
//											}
//										}).setNegativeButton(
//										mContext.getString(R.string.cancel),
//										new DialogInterface.OnClickListener() {
//
//											@Override
//											public void onClick(
//													DialogInterface dialog,
//													int which) {
//												// if (chareStr.equals("1")) {
//												// mStatistics.add(
//												// HStatistics.Z11_2, "",
//												// "", "");
//												// } else {
//												HResLibAdapter.this
//														.notifyDataSetChanged();
//												mStatistics.add(
//														HStatistics.Z10_5_2,
//														info.getMi(), "", "");
//												// }
//												if (null != mConfirmDialog) {
//													mConfirmDialog.dismiss();
//													mConfirmDialog = null;
//												}
//											}
//										}).create();
//						mConfirmDialog.show();
					}
				}
				onClickButton = false;
				return;
			}

		}

		/***
		 * 0.已安装 0 灰色按钮显示“已安装” 
		 * 1.已下载 1 按钮可点击显示“安装” 
		 * 2.未下载 2 按钮可点击显示“下载” 
		 * 3.下载中 3 按钮不可见
		 * 
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
			/** 查询是否已经安装 */
			Cursor insCor = mContext.getContentResolver()
					.query(
							HResProvider.CONTENT_URI_SKIN,
							null,
							HResDatabaseHelper.PACKAGENAME + " = '"
									+ packageName + "'", null, null);
			if (insCor != null && insCor.getCount() > 0) {
				insCor.close();
				return INSTALLED;
			} else {
				/** 从download表中查询后三种状态 */
				Cursor dlCor = mContext.getContentResolver().query(
						HResProvider.CONTENT_URI_DOWNLOAD,
						null,
						HResDatabaseHelper.PACKAGENAME + " = '" + packageName
								+ "'", null, null);
				if (dlCor == null) {
					if (insCor != null) {
						insCor.close();
					}
					return NO_DOWNLOAD;
				}
				while (dlCor.moveToNext()) {
					if (dlCor.getCount() > 0) {

						dlStatus = dlCor
								.getInt(dlCor
										.getColumnIndex(HResDatabaseHelper.TASK_STATUS));
						if (dlStatus == DLManager.STATUS_SUCCESS) {
							HLog.d("dlCor.count", dlCor.getCount()
									+ "DOWNLOADED");
							if (insCor != null) {
								insCor.close();
							}
							dlCor.close();
							return DOWNLOADED;
						} else {
							HLog.d("dlCor.count", dlCor.getCount()
									+ "DOWNLOADING");
							if (insCor != null) {
								insCor.close();
							}
							dlCor.close();
							return DOWNLOADING;
						}
					} else {
						HLog.d("dlCor.count", dlCor.getCount() + "NO_DOWNLOAD");
						if (insCor != null) {
							insCor.close();
						}
						dlCor.close();
						return NO_DOWNLOAD;
					}
				}
				if (insCor != null) {
					insCor.close();
				}
				dlCor.close();
			}
			HLog.d("dlCor.count", "NO_DOWNLOAD");
			return NO_DOWNLOAD;
		}

	}

}