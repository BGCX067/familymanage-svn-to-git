package com.haolianluo.sms2;

import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
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

public class HResLibActivity extends HActivity implements OnClickListener,
		OnGestureListener {
	final int mTYPEHOT = 0;
	final int mTYPEFREE = 1;
	final int mTYPENEW = 2;
	final int mTYPEMY = 3;
	TextView mHotTv, mFreeTv, mNewTv, mTilteTv, mMyTv;
	ImageView mTitleIv;// , mTitleBt;
	private Context mContext;
	boolean back = false;
	private boolean mIsDeleteMode = false;
	private LinearLayout mMenuLinearLayout;
//	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		back = false;
		type = mTYPEHOT;
		setContentView(R.layout.res_list);
		mContext = this;
		mInflater = LayoutInflater.from(mContext);
//		IntentFilter filter = new IntentFilter(HSkinReceiver.ACTION_INSTALLED);
		//registerReceiver(mBroadcastInstalled, filter);
		// new HUpdateTools(HResLibActivity.this).checkUpdate();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_SEARCH == event.getKeyCode()){
			return true;
		}
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
														getString(R.string.confirm),
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
	
	private ProgressDialog mDeleteDialog;
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
	
	/*
	private BroadcastReceiver mBroadcastInstalled = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null
					&& intent.getAction()
							.equals(HSkinReceiver.ACTION_INSTALLED)) {
				diaHandler.sendEmptyMessage(4);
			}
		}
	};
*/
	public void onStart() {
		System.out.println("onStrat");
		super.onStart();
		mDLManager = DLManager.getInstance(mContext);
		mDLDatalist = mDLManager.getAllTasks();
		if (!back) {
			mFreeAdapter = null;
			mHotAdapter = null;
			mNewAdapter = null;
			initView(type);
		}

		System.out.println("mNewAdapter.notifyDataSetChanged   " + type
				+ "    mtfreeadapter : " + mNewAdapter);
		if (mFreeAdapter != null) {
			mFreeAdapter.notifyDataSetChanged();
			System.out.println("mNewAdapter.notifyDataSetChanged");
		}

		if (mHotAdapter != null) {
			mHotAdapter.notifyDataSetChanged();
		}
		if (mNewAdapter != null) {
			mNewAdapter.notifyDataSetChanged();
		}

	}

	boolean showDialog = false;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		back = true;
		diaHandler.sendEmptyMessage(0);
		diaHandler.sendEmptyMessage(2);
		if (resultCode == RESULT_OK) {
			Bundle bd = data.getExtras();
			String tag = bd.getString("delete");
			if (tag.equals("delete")) {
				showDialog = true;
				back = false;
			}
		}
		count = type;

	}

	private void getLayout() {
		mHotTv = (TextView) findViewById(R.id.text_hot_res);
		mFreeTv = (TextView) findViewById(R.id.text_free_res);
		mNewTv = (TextView) findViewById(R.id.text_new_res);
		mMyTv = (TextView) findViewById(R.id.text_my_res);
		mHotTv.setOnClickListener(this);
		mFreeTv.setOnClickListener(this);
		mNewTv.setOnClickListener(this);
		mMyTv.setOnClickListener(this);
		mTilteTv = (TextView) findViewById(R.id.res_title_text);
		mTitleIv = (ImageView) findViewById(R.id.res_title_icon);
		mTitleIv.setOnClickListener(this);
		/*
		 * mTitleBt = (ImageView) findViewById(R.id.res_title_button);
		 * mTitleBt.setVisibility(View.VISIBLE);
		 * mTitleBt.setOnClickListener(this);
		 */
		mFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
		mDetector = new GestureDetector(this);

	}

	private static int type = 0;

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

	public void setViewFilpperChild(int flag, GridView view, HResLibAdapter hla) {
		System.out.println(" flag : " + flag);

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
			System.out.println("11111111111");
			if (count != 0) {
				setChoice(v.getId());
				mStatistics.add(HStatistics.Z8_2, "", "", "");
			}

			break;
		case R.id.text_free_res:
			System.out.println("22222222222");
			if (count != 1) {
				setChoice(v.getId());
				mStatistics.add(HStatistics.Z8_1, "", "", "");
			}

			break;
		case R.id.text_new_res:
			System.out.println("333333333333");
			if (count != 2) {
				setChoice(v.getId());
				mStatistics.add(HStatistics.Z8_3, "", "", "");

			}
			break;
		case R.id.text_my_res:
			System.out.println("444444444444");
			if (count != 3) {
				setChoice(v.getId());
				mStatistics.add(HStatistics.Z8_4, "", "", "");

			}
			break;
		case R.id.res_title_button: {
			HStatistics mStatistics = new HStatistics(HResLibActivity.this);
			mStatistics.add(HStatistics.Z8_4, "", "", "");
			Intent intent = new Intent(HResLibActivity.this,
					HResMineActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.res_title_icon:
			finish();
			break;
		}

	}

	HStatistics mStatistics = new HStatistics(this);

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
			textH.setBackgroundResource(R.drawable.res_pitch);
			mHotTv.setTextColor(getColor(R.color.res_choice));
			this.mFlipper.setDisplayedChild(0);
			this.count = 0;

			break;
		case R.id.text_free_res:
			initAdapter(mTYPEFREE);
			textF.setBackgroundResource(R.drawable.res_pitch);
			mFreeTv.setTextColor(getColor(R.color.res_choice));
			this.mFlipper.setDisplayedChild(1);
			this.count = 1;

			break;
		case R.id.text_new_res:
			initAdapter(mTYPENEW);
			textN.setBackgroundResource(R.drawable.res_pitch);
			mNewTv.setTextColor(getColor(R.color.res_choice));
			this.mFlipper.setDisplayedChild(2);
			this.count = 2;

			break;
		case R.id.text_my_res:
			initAdapter(mTYPEMY);
			textM.setBackgroundResource(R.drawable.res_pitch);
			mMyTv.setTextColor(getColor(R.color.res_choice));
			this.mFlipper.setDisplayedChild(3);
			this.count = 3;

			break;
		default:
			// initAdapter(mTYPEHOT);
			this.mFlipper.setDisplayedChild(0);
			textF.setBackgroundResource(R.drawable.res_pitch);
			mFreeTv.setTextColor(getColor(R.color.res_choice));
			break;
		}

	}

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
			String apkPath = DLManager.INSTALL_PATH
					+ File.separator
					+ cursor.getString(cursor
							.getColumnIndex(HResDatabaseHelper.FILE_NAME));
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
		if ("com.haolianluo.sms2".equalsIgnoreCase(packname)) {
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
			bt_apply.setText(getString(R.string.skin_bt_useing));
			bt_apply.setEnabled(false);
			fl.setVisibility(View.GONE);
		} else {
			bt_apply.setBackgroundResource(R.drawable.res_apply);
			bt_apply.setText(getString(R.string.skin_bt_use));
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
//		final String resid = res_id;
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
		 * packagename); Intent i = new Intent(Intent.ACTION_DELETE, uri);
		 * startActivity(i); }}) .create() .show(); }});
		 */
		// new Design

		// old design
		if (!(packagename.equals("com.haolianluo.sms2") || packagename
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

	private List<String> mDeleteList = new ArrayList<String>();
	private boolean mIsSelectAll = false;
	
	private void deleteSkin(String filename) {
		Cursor c = mContext.getContentResolver().query(HResProvider.CONTENT_URI_SKIN, new String[]{HResDatabaseHelper.RES_ID},
				HResDatabaseHelper.FILE_NAME + "= '" + filename + "'", null, null);
		if(null != c && c.getCount() > 0)
		{
			c.moveToNext();
			HStatistics mHStatistics = new HStatistics(
					HResLibActivity.this);
			mHStatistics.add(HStatistics.Z12_2, c.getString(c.getColumnIndex(HResDatabaseHelper.RES_ID)),
					"0", "0");
		}
		File file = new File(DLManager.LOCAL_PATH + File.separator + filename);
		if (file.exists()) {
			file.delete();
		}
		mContext.getContentResolver().delete(HResProvider.CONTENT_URI_SKIN,
				HResDatabaseHelper.FILE_NAME + "= '" + filename + "'", null);
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
						mHStatistics.add(HStatistics.Z12_3, resid,
								(charge == 0) ? "0" : "1", "1");
							
						SkinManage.mCurrentSkin = c
								.getString(c
										.getColumnIndex(HResDatabaseHelper.PACKAGENAME));
						SkinManage.mCurrentFile = c.getString(c
								.getColumnIndex(HResDatabaseHelper.FILE_NAME));
					}
					c.close();
					mChangeSkinHandler.sendEmptyMessage(0);
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
	
	private ProgressDialog mProgressDialog;

	private Handler mChangeSkinHandler = new Handler() {
		public void handleMessage(Message msg) {
			initInstalled();
			initMy();
			if (null != mProgressDialog) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			Toast.makeText(mContext, getString(R.string.skin_changed),
					Toast.LENGTH_SHORT).show();
		}
	};

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

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,

	float arg3) {
		Filing = false;
		System.out.println("Fling" + "Fling Happened!");
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
		if (Filing) {
			Filing = false;
			return false;
		}

		return super.dispatchTouchEvent(ev);
	}

	private void moveSetChoic(int count) {
		switch (count) {
		case 0:
			setChoice(R.id.text_hot_res);

			break;
		case 1:
			setChoice(R.id.text_free_res);

			break;
		case 2:
			setChoice(R.id.text_new_res);
			break;
		case 3:
			setChoice(R.id.text_my_res);
			break;
		}
	}

	ProgressDialog updataDialog = null;
	Handler diaHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (!ToolsUtil.MM_FLAG) {

				if (msg.what == 1) {
					showDialog();
				} else if (msg.what == 0) {
					closeDialog();
				} else if (msg.what == 3) {
					showDialogNoCancal();
				} else if (msg.what == 2) {
					closeNoCancel();
				}

			}
			if (msg.what == 4) {
				moveSetChoic(3);
			}
		};
	};
	Dialog mDialog = null;
	Dialog mDialog1 = null;
	String showPayDialog = "-1";

	private void showDialog() {
		if (mDialog == null) {
			View v = getLayoutInflater().inflate(R.layout.dialogview, null);
			View layout = v.findViewById(R.id.dialog_view);
			mDialog = new Dialog(this, R.style.MyFullHeightDialog);
			mDialog.setContentView(layout);
			mDialog.show();
		} else {
			mDialog.show();
		}
	}

	private void showDialogNoCancal() {
		if (mDialog1 == null) {
			View v = getLayoutInflater().inflate(R.layout.dialogview, null);
			View layout = v.findViewById(R.id.dialog_view);
			mDialog1 = new Dialog(this, R.style.MyFullHeightDialog);
			mDialog1.setContentView(layout);
			mDialog1.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					showPayDialog = "-1";
					System.out.println("onKeyDown : " + showPayDialog);
					if (mDialog1 != null && mDialog1.isShowing()) {
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
		if (mDialog != null) {
			mDialog.hide();
			mDialog = null;
		}
	}

	private void closeNoCancel() {
		if (mDialog1 != null) {
			mDialog1.dismiss();
			mDialog1 = null;
		}
	}

	@Override
	protected void onPause() {

		super.onPause();
		HLog.d("list", "onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		HLog.d("list", "onStop");
	}

	@Override
	protected void onDestroy() {
		back = false;
		super.onDestroy();
		HLog.d("list", "onDestroy");
		mDialog = null;
		mDialog1 = null;
		//unregisterReceiver(mBroadcastInstalled);
	}

	
	private DLManager mDLManager;

	List<DLData> mDLDatalist;

	public class HResLibAdapter extends BaseAdapter {
		final int mTYPEHOT = 1;
		final int mTYPEFREE = 0;
		final int mTYPEFNEW = 2;
		PayBussiness mPayBussiness;
		private int mFlag;
		private Context mContext;
		private ArrayList<HResLibModel> mResList = new ArrayList<HResLibModel>();
		private GridView mListView;
		Handler diaHandler;

		HStatistics mStatistics;
        Map<String ,String> mMap;
        
		public HResLibAdapter(Activity context, int flag, GridView view,
				boolean showDialog, Handler diaHandler) {
			
			this.diaHandler = diaHandler;
			this.mContext = context;
			this.mFlag = flag;
			this.mListView = view;
			mMap = new HashMap <String ,String>();
			if(ToolsUtil.mIDlist != null){
				ToolsUtil.mIDlist.clear();
			}
			mStatistics = new HStatistics(mContext);
			if (showDialog) {
				diaHandler.sendEmptyMessage(1);
			}
			getResList(flag, 1, 1);
			mCount = mResList.size();
			pageIndex = 1;
			// mDLManager = DLManager.getInstance(mContext);
			// mDLDatalist = mDLManager.getAllTasks();

			// true 即为MM商城，则提前进行初始化
			mPayBussiness = new PayBussiness();
			if (ToolsUtil.MM_FLAG) {
				mPayBussiness.applyPay(context, startPayHandler);
			}
			mDLManager.setProgressHandler(mDownLoadHandler);
		}

		private void setGridBg() {
			mCount = mResList.size();
			if (mCount == 0) {
				mListView.setBackgroundResource(R.drawable.no_net);
			} else {
				mListView.setBackgroundDrawable(null);
			}
			HResLibAdapter.this.notifyDataSetChanged();
		}

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
//		    HResLibModel info = null;
//		    new HResLibInfoParser(getApplicationContext(), resModel.getMi()).getResLibInfo();
			
			int id = 1;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.res_adapter_item, null);

				holder.res_view = convertView.findViewById(R.id.res);
				holder.titleIv = (ImageView) convertView
						.findViewById(R.id.res_title);
				holder.titleTv = (TextView) convertView
						.findViewById(R.id.res_title_text);
				holder.starBar = (RatingBar) convertView
						.findViewById(R.id.res_star);
				holder.qBuyButton = (Button) convertView
						.findViewById(R.id.res_quick_buy);
				holder.reviewTv = (TextView) convertView
						.findViewById(R.id.res_review);
				holder.payTv = (TextView) convertView
						.findViewById(R.id.res_title_pay);
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
				id = Integer.parseInt(resModel.getMi().trim());
				int isDown = getStatus(pageName);
				holder.qBuyButton.setBackgroundResource(R.drawable.res_de_down);
				holder.qBuyButton.setEnabled(true);
				switch (isDown) {
				case INSTALLED:
					holder.qBuyButton.setText(mContext
							.getText(R.string.res_check));
					holder.qBuyButton.setVisibility(View.VISIBLE);
					holder.proView.setVisibility(View.GONE);
					holder.qBuyButton
					.setBackgroundResource(R.drawable.res_de_down);
					break;
				case DOWNLOADED:
					holder.qBuyButton.setText(mContext
							.getText(R.string.install));
					holder.qBuyButton.setVisibility(View.VISIBLE);
					holder.proView.setVisibility(View.GONE);
					holder.qBuyButton
							.setBackgroundResource(R.drawable.res_install_new);
					break;
				case NO_DOWNLOAD:
					holder.qBuyButton.setText(mContext
							.getText(R.string.download_times));
					holder.qBuyButton.setVisibility(View.VISIBLE);
					holder.proView.setVisibility(View.GONE);
					holder.qBuyButton
							.setBackgroundResource(R.drawable.res_de_down);
					break;
				case DOWNLOADING:
					holder.qBuyButton.setVisibility(View.GONE);
					holder.proView.setVisibility(View.VISIBLE);
                    
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
										.getText(R.string.install));
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
										.setBackgroundResource(R.drawable.res_pause);
								break;
							case DLManager.STATUS_PAUSE:
								holder.stageIv
										.setBackgroundResource(R.drawable.res_start);
								break;
							}

						}

					}

					break;
				}
				holder.titleTv.setText(title);
				holder.payTv.setText(yuan);

				holder.qBuyButton.setId(position);
				// holder.qBuyButton.setOnClickListener(buttonListener);
				holder.qBuyButton
						.setOnClickListener(new DonwLoadButtonListener(isDown));
				holder.res_view.setId(position);
				holder.res_view.setOnClickListener(resViewItemListner);

				String review = mContext.getString(R.string.res_ml);
				review = review.replace("*", resModel.getMd());
				for(int i = 0;i < ToolsUtil.mIDNlist.size();i++){
					if(resModel.getMi().equals(ToolsUtil.mIDNlist.get(i))){
						if(mMap.get(ToolsUtil.mIDNlist.get(i)) != null){
							review = review.replace("*", mMap.get(ToolsUtil.mIDNlist.get(i)));
						}else{
							
						}
					}
				}
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
			convertView.setId(id);
			return convertView;
		}

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
				Button b = (Button)v;
				if(b.getText().toString().equals(mContext.getText(R.string.install))){
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
					// onMineActivity();
					diaHandler.sendEmptyMessage(4);
					break;
				case DOWNLOADED:
					b.setText(mContext
							.getText(R.string.res_check));
					b.setBackgroundResource(R.drawable.res_de_down);
					b.setEnabled(false);
					
					buttonEvent(resModel, 0);
					break;
				case NO_DOWNLOAD:
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
			HStatistics mStatistics = new HStatistics(mContext);
			mStatistics.add(HStatistics.Z8_4, "", "", "");
			Intent intent = new Intent(mContext, HResMineActivity.class);
			mContext.startActivity(intent);
		}

		/** 限制快速重复点击 ，出此下策情非得已 */
		boolean onClickButton = false;
		long time = 0;
		long startTime = 0;

		// OnClickListener buttonListener = new OnClickListener() {
		//
		// @Override
		// public void onClick(final View v) {
		// // time = System.currentTimeMillis();
		// // if(startTime !=0 && time < startTime + 500){
		// // return ;
		// // }
		// // startTime = System.currentTimeMillis();
		// if (onClickButton == false) {
		// HResLibModel resModel = mResList.get(v.getId());
		// onClickButton = true;
		// Button qbutton = (Button) v;
		// // 安装
		// String install = mContext.getText(R.string.install).toString();
		// String downLoading = mContext.getText(R.string.res_ding)
		// .toString();
		// String buttonText = qbutton.getText().toString();
		// String buttonUse = mContext.getText(R.string.skin_bt_use)
		// .toString();
		// System.out.println(resModel.getPn() + "buttonText:"
		// + buttonText);
		// if (buttonText.equals(install)) { // 已下载
		// mStatistics
		// .add(HStatistics.Z10_5, resModel.getMi(), "", "");
		// buttonEvent(qbutton, resModel, 0);
		// } else if (buttonText.equals(downLoading)
		// || buttonText.equals(mContext
		// .getText(R.string.skin_bt_useing))) { // 下载中
		// qbutton.setClickable(false);
		// // qbutton.setBackgroundResource(R.drawable.res_de_installed);
		// onClickButton = false;
		//
		// } else if (buttonText.equals(buttonUse)) { // 应用
		// // mStatistics.add(HStatistics.Z12_4, resModel.getMi(), "",
		// // "");
		// qbutton.setText(mContext.getText(R.string.skin_bt_useing));
		// qbutton.setClickable(false);
		// HResLibAdapter.this.notifyDataSetChanged();
		// ContentValues cvalues = new ContentValues();
		// cvalues.put(HResDatabaseHelper.RES_USE, 0);
		// mContext.getContentResolver().update(
		// HResProvider.CONTENT_URI_SKIN, cvalues, null, null);
		// ContentValues values = new ContentValues();
		// values.put(HResDatabaseHelper.RES_USE, 1);
		// mContext.getContentResolver().update(
		// HResProvider.CONTENT_URI_SKIN,
		// values,
		// HResDatabaseHelper.PACKAGENAME + " = '"
		// + resModel.getPkn() + "'", null);
		// Cursor c = mContext.getContentResolver().query(
		// HResProvider.CONTENT_URI_SKIN, null,
		// HResDatabaseHelper.RES_USE + " = '1'", null, null);
		// onClickButton = false;
		// if (c.getCount() > 0) {
		// c.moveToNext();
		// HStatistics mHStatistics = new HStatistics(mContext);
		// int charge = c.getInt(c
		// .getColumnIndex(HResDatabaseHelper.CHARGE));
		// String resid = c.getString(c
		// .getColumnIndex(HResDatabaseHelper.RES_ID));
		// resid = (resid == null || resid.trim().equals("")) ? "0"
		// : resid;
		// mHStatistics.add(HStatistics.Z12_1, resid,
		// (charge == 0) ? "0" : "1", "1");
		// SkinManage.mCurrentSkin = c
		// .getString(c
		// .getColumnIndex(HResDatabaseHelper.PACKAGENAME));
		// }
		// c.close();
		//
		// } else {// 未下载
		// String pay = resModel.getPl();
		// if (pay.equals("-1")) {
		// pay = "1";
		// }
		// mStatistics.add(HStatistics.Z10_1, resModel.getMi(), ""
		// + mFlag, pay);
		// mStatistics
		// .add(HStatistics.Z10_3, resModel.getMi(), "", "");
		// mStatistics
		// .add(HStatistics.Z10_4, resModel.getMi(), "", "");
		// buttonEvent(qbutton, resModel, 1);
		// }
		// HResLibAdapter.this.notifyDataSetChanged();
		// } else {
		// return;
		// }
		// }
		// };

		private void buttonEvent(final HResLibModel model, int flag) {
			if (!ToolsUtil.checkSDcard()) {
				Toast.makeText(mContext, mContext.getString(R.string.plugsd),
						Toast.LENGTH_LONG).show();
				return;
			}
			String pay = model.getPl();
            System.out.println("pay : "+ pay);
            if(pay .equals("1"))
            for(int i = 0;i< ToolsUtil.mIDlist.size();i ++){
            	if(model.getMi().equals(ToolsUtil.mIDlist.get(i).toString())){
            		pay = "-1";
            		model.setPl(pay);
            		break;
            	}
            }
			if (!pay.equals("0"))
				extracted(model, flag, pay);
			else {
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

		private void extracted(final HResLibModel model, int flag, String pay) {
			{

				SharedPreferences pref = mContext.getSharedPreferences(
						HConst.PREF_USER, Context.MODE_PRIVATE);
				boolean isLogin = pref.getBoolean(HConst.USER_KEY_LOGIN, false);
				HLog.d("isLogin", String.valueOf(isLogin));
				boolean Im = ToolsUtil.IM_FLAG;
				System.out
						.println("Im = " + Im + "   pay = " + pay.equals("0"));
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

							diaHandler.sendEmptyMessage(3);

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
										mPayBussiness.startPay(HResLibActivity.this,model.getMi(), ToolsUtil.getPhoneNum(HResLibActivity.this));

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

		// 获取支付是否成功
		private Handler startPayHandler = new Handler() {
			public void handleMessage(Message msg) {
				int what = msg.what;
				if (what == 200) {
//					Toast.makeText(HResLibActivity.this, "支付成功，模板已经开始下载!",
//							Toast.LENGTH_SHORT).show();
					// 支付成功开始正常下载
					DonwloadModel(mResModel);
					if(mResModel.getPl().equals("1")){
						mResModel.setPl("-1");
					}

				} else if (what == 100) {
					// 支付失败 提醒用户
//					Toast.makeText(HResLibActivity.this, "支付失败，请重试!",
//							Toast.LENGTH_SHORT).show();
				}
			};
		};
		// Button button;
		HResLibModel mResModel;
		private Handler payHandler = new Handler() {
			public void handleMessage(Message msg) {
				Bundle bundle = msg.getData();
				String id = bundle.getString("Mi");
				HMoldPay hmp = (HMoldPay) msg.obj;
				System.out.println("payHandler 1: " + showPayDialog);
				if ((mResModel != null && mResModel.getMi().equals(id))) {
					if (!showPayDialog.equals(id)) {
						return;
					}
					System.out.println("payHandler : " + showPayDialog);
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
				System.out.println("message : " + message);
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
											onClickButton = false;
											// diaHandler.sendEmptyMessage(1);
											// 进行付费动作
											HStatistics mHStatistics = new HStatistics(
													mContext);
											mHStatistics.add(HStatistics.Z11_1,
													model.getMi(), "", "");
											mPayBussiness.startPay(
													HResLibActivity.this,
													new Handler(), hmd,
													phoneNumber);
											// 把该模板的付费状态设置为已付费
											model.setPl("-1");
											
											DonwloadModel(model);
											HResLibAdapter.this
													.notifyDataSetChanged();
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

		Handler mDownLoadHandler = new Handler() {
			public void handleMessage(Message msg) {
				Bundle bundle = msg.getData();
				DLData dldata = (DLData) bundle.getSerializable("task");
				switch (msg.what) {
				case DLManager.ACTION_DELETE:
					HResLibAdapter.this.notifyDataSetChanged();
					break;
				case DLManager.ACTION_SUCCESS:
					HResLibAdapter.this.notifyDataSetChanged();
					break;
				case DLManager.ACTION_UPDATE:
					View view = null;
					if (dldata != null)
						view = mListView.findViewById(Integer.parseInt(dldata
								.getResID().trim()));
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
						}
						String p = df.format(progress);
						percentTv.setText(p + "%");
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
				// System.out.println("resViewItemListner :" +
				// HResLibActivity.Filing);

				if (onClickButton == false) {
					if (!ToolsUtil.checkNet(mContext)) {
						Toast.makeText(mContext,
								mContext.getString(R.string.no_connect),
								Toast.LENGTH_LONG).show();
					} else {
						HResLibModel hlm = mResList.get(v.getId());
						Intent intent = new Intent();
						System.out.println("model : " + hlm.getPn());
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
						intent.setClass(mContext, HResDetailActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
						((HResLibActivity) mContext).startActivityForResult(
								intent, 20);

					}
				}
			}
		};

		class MyHolder {
			View proView;
			TextView percentTv;
			ImageView stageIv;
			TextView payTv;
			View res_view;
			/* 缩略图 */
			ImageView titleIv;
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

		private void getResList(int flag, int start, int page) {

			ArrayList<HResLibModel> list = new ArrayList<HResLibModel>();
			switch (flag) {
			case mTYPEHOT:
				list = new HResLibParser(mContext, HConst.RESLIB_PYLST, start)
						.getResLibCacheList(ToolsUtil.getLanguage()
								+ HConst.RESLIB_PYLST + page, page);

				break;
			case mTYPEFREE:
				list = new HResLibParser(mContext, HConst.RESLIB_FRLST, start)
						.getResLibCacheList(ToolsUtil.getLanguage()
								+ HConst.RESLIB_FRLST + page, page);
				break;
			case mTYPEFNEW:
				list = new HResLibParser(mContext, HConst.RESLIB_CXLST, start)
						.getResLibCacheList(ToolsUtil.getLanguage()
								+ HConst.RESLIB_CXLST + page, page);
				break;
			}

			System.out.println("list : " + list);

			if (list != null) {
				int len = list.size();
				System.out.println("list : " + list.size());
				if (len < 10) {
					nextList = false;
				}
				for (int i = 0; i < len; i++) {
					mResList.add(list.get(i));
				}
				cacheListNull = false;
			} else {
				cacheListNull = true;
				diaHandler.sendEmptyMessage(1);
				// if (pageIndex > 1)
				// pageIndex--;
			}
			scrollLoad = false;
			getListPage = page;
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
				// System.out.println("handlemessage");
				ArrayList<HResLibModel> list = (ArrayList<HResLibModel>) msg.obj;
				int llen = 0;
				Bundle bundle = msg.getData();
				int index = bundle.getInt("page");
				// System.out.println("page : " + index);
				int page = (index - 1) * HConst.REQUEST_NUMBER;

				if (list != null) {
					int mlen = mResList.size();

					llen = list.size();
					if (llen < HConst.REQUEST_NUMBER && index == getListPage) {
						nextList = false;
					} else {
						nextList = true;

					}
					for (int i = 0; i < HConst.REQUEST_NUMBER; i++) {
						if (i >= llen) {
							if (page + i < mlen)
								mResList.remove((page + llen));

						} else {
							if (page + i < mlen) {
								mResList.set(page + i, list.get(i));
							} else {
								mResList.add(list.get(i));
							}
						}

					}
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
							Toast.makeText(mContext,
									mContext.getText(R.string.loading_more),
									Toast.LENGTH_LONG).show();
							return;
						}
						if (!scrollLoad && nextList) {
							scrollLoad = true;
							pageIndex++;
							// System.out.println("pageIndex :" + pageIndex);
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

		private Handler getListHandler = new Handler() {
			public void handleMessage(Message msg) {
				final int page = msg.getData().getInt("page");
				final int start = msg.getData().getInt("start");
				// System.out.println(" getlistHandlerpage : " + page);
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
						mConfirmDialog = new AlertDialog.Builder(mContext)
								.setTitle(
										mContext
												.getString(R.string.confirm_install))
								.setMessage(message).setPositiveButton(
										mContext.getString(R.string.confirm),
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// if (chareStr.equals("1")) {
												// mStatistics.add(
												// HStatistics.Z11_1, "",
												// "", "");
												// } else {
												mStatistics.add(
														HStatistics.Z10_5_1,
														info.getMi(), "", "");
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
														mContext, tas);
												HResLibAdapter.this
												.notifyDataSetChanged();
												if (null != mConfirmDialog) {
													mConfirmDialog.dismiss();
													mConfirmDialog = null;
												}
											}
										}).setNegativeButton(
										mContext.getString(R.string.cancel),
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// if (chareStr.equals("1")) {
												// mStatistics.add(
												// HStatistics.Z11_2, "",
												// "", "");
												// } else {
												HResLibAdapter.this
												.notifyDataSetChanged();
												mStatistics.add(
														HStatistics.Z10_5_2,
														info.getMi(), "", "");
												// }
												if (null != mConfirmDialog) {
													mConfirmDialog.dismiss();
													mConfirmDialog = null;
												}
											}
										}).create();
						mConfirmDialog.show();
					}
				}
				onClickButton = false;
				return;
			}

		}

		/***
		 * 0.已安装 0 灰色按钮显示“已安装” 1.已下载 1 按钮可点击显示“安装” 2.未下载 2 按钮可点击显示“下载” 3.下载中 3
		 * 按钮不可见
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