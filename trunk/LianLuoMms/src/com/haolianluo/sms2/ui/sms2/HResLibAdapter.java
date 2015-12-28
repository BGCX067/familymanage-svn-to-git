package com.haolianluo.sms2.ui.sms2;
//package com.haolianluo.sms2.ui.sms2;
//
//import java.io.File;
//import java.io.InputStream;
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.List;
//import com.haolianluo.sms2.R;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.BitmapDrawable;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.View.OnClickListener;
//import android.widget.AbsListView;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.RatingBar;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.AbsListView.OnScrollListener;
//
//import com.haolianluo.sms2.data.HConst;
//import com.haolianluo.sms2.model.HMoldPay;
//import com.haolianluo.sms2.model.HResDatabaseHelper;
//import com.haolianluo.sms2.model.HResLibModel;
//import com.haolianluo.sms2.model.HResLibParser;
//import com.haolianluo.sms2.model.HResProvider;
//import com.haolianluo.sms2.model.HStatistics;
//import com.haolianluo.sms2.model.PayBussiness;
//import com.lianluo.core.cache.CacheManager;
//import com.lianluo.core.cache.ResourceFileCache;
//import com.lianluo.core.net.download.DLData;
//import com.lianluo.core.net.download.DLManager;
//import com.lianluo.core.util.HLog;
//import com.lianluo.core.util.ImageUtil;
//import com.lianluo.core.util.ToolsUtil;
//
//public class HResLibAdapter extends BaseAdapter {
//	final int mTYPEHOT = 1;
//	final int mTYPEFREE = 0;
//	final int mTYPEFNEW = 2;
//	private int mFlag;
//	private Context mContext;
//	private ArrayList<HResLibModel> mResList = new ArrayList<HResLibModel>();
//	private GridView mListView;
//	Handler diaHandler;
//
//	private DLManager mDLManager;
//	// private DLData task;
//	HStatistics mStatistics;
//	List<DLData> mDLDatalist;
//
//	public HResLibAdapter(Context context, int flag, GridView view,
//			boolean showDialog, Handler diaHandler) {
//		this.diaHandler = diaHandler;
//		this.mContext = context;
//		this.mFlag = flag;
//		this.mListView = view;
//		mStatistics = new HStatistics(mContext);
//		if (showDialog) {
//			diaHandler.sendEmptyMessage(1);
//		}
////		getResList(flag, 1, 1);
//		mCount = mResList.size();
//		pageIndex = 1;
//		mDLManager = DLManager.getInstance(mContext);
//		mDLDatalist = mDLManager.getAllTasks();
//		mDLManager.setProgressHandler(mDownLoadHandler);
//	}
//
//	private void setGridBg() {
//		mCount = mResList.size();
//		if (mCount == 0) {
//			mListView.setBackgroundResource(R.drawable.no_net);
//		} else {
//			mListView.setBackgroundDrawable(null);
//		}
//		HResLibAdapter.this.notifyDataSetChanged();
//	}
//
//	private Handler mHandler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			if (HResLibAdapter.this != null) {
//				if (msg.what == ResourceFileCache.RESOURCE_SUCCESS) {
//					HResLibAdapter.this.notifyDataSetChanged();
//				}
//			}
//		}
//
//	};
//	int mCount = 0;
//
//	@Override
//	public int getCount() {
//		return mCount;
//	}
//
//	@Override
//	public Object getItem(int position) {
//		return mResList.get(position);
//	}
//
//	@Override
//	public long getItemId(int position) {
//		return position;
//	}
//
//	DecimalFormat format = new DecimalFormat("0.0");
//
//	String yuan;
//
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		MyHolder holder = new MyHolder();
//		HResLibModel resModel = mResList.get(position);
//		int id = 1;
//		if (convertView == null) {
//			convertView = LayoutInflater.from(mContext).inflate(
//					R.layout.res_adapter_item, null);
//
//			holder.res_view = convertView.findViewById(R.id.res);
//			holder.titleIv = (ImageView) convertView
//					.findViewById(R.id.res_title);
//			holder.titleTv = (TextView) convertView
//					.findViewById(R.id.res_title_text);
//			holder.starBar = (RatingBar) convertView
//					.findViewById(R.id.res_star);
//			holder.qBuyButton = (Button) convertView
//					.findViewById(R.id.res_quick_buy);
//			holder.reviewTv = (TextView) convertView
//					.findViewById(R.id.res_review);
////			holder.payTv = (TextView) convertView
////					.findViewById(R.id.res_title_pay);
//			holder.progressBar = (ProgressBar) convertView
//					.findViewById(R.id.res_detail_progressbar_new);
//			holder.percentTv = (TextView) convertView
//					.findViewById(R.id.res_percent);
//			holder.stageIv = (ImageView) convertView
//					.findViewById(R.id.res_stage);
//			holder.proView = (View) convertView
//					.findViewById(R.id.res_progressBar);
//			convertView.setTag(holder);
//		} else {
//			holder = (MyHolder) convertView.getTag();
//		}
//		try {
//			String isPay = resModel.getPl();
//			String title = resModel.getPn();
//			String pageName = resModel.getPkn();
//			String yuan = resModel.getPr();
//			id = Integer.parseInt(resModel.getMi().trim());
//			int isDown = getStatus(pageName);
//			holder.qBuyButton.setBackgroundResource(R.drawable.res_de_down);
//			switch (isDown) {
//			case INSTALLED:
//				holder.qBuyButton.setText(mContext.getText(R.string.res_check));
//				holder.qBuyButton.setVisibility(View.VISIBLE);
//				holder.proView.setVisibility(View.GONE);
//				holder.qBuyButton.setText(mContext.getText(R.string.install));
//				break;
//			case DOWNLOADED:
//				holder.qBuyButton.setText(mContext.getText(R.string.install));
//				holder.qBuyButton.setVisibility(View.VISIBLE);
//				holder.proView.setVisibility(View.GONE);
//				holder.qBuyButton.setBackgroundResource(R.drawable.res_install_new);
//				break;
//			case NO_DOWNLOAD:
//				holder.qBuyButton.setText(mContext.getText(R.string.download_times));
//				holder.qBuyButton.setVisibility(View.VISIBLE);
//				holder.proView.setVisibility(View.GONE);
//				holder.qBuyButton.setBackgroundResource(R.drawable.res_de_down);
//				break;
//			case DOWNLOADING:
//				holder.qBuyButton.setVisibility(View.GONE);
//				holder.proView.setVisibility(View.VISIBLE);
//				
//				for (DLData dldata : mDLDatalist) {
//					if (dldata.getPackagename().equals(pageName)) {
//						holder.stageIv.setOnClickListener(new OnStateListener(dldata.getId()));
//						switch (dldata.getStatus()) {
//						case DLManager.STATUS_READY:
//							holder.stageIv
//									.setBackgroundResource(R.drawable.btn_wait);
//							break;
//						case DLManager.STATUS_RUNNING:
//							holder.stageIv
//									.setBackgroundResource(R.drawable.res_pause);
//							break;
//						case DLManager.STATUS_PAUSE:
//							holder.stageIv
//									.setBackgroundResource(R.drawable.res_start);
//							break;
//						}
//					}
//					DecimalFormat df = new DecimalFormat();
//					df.setMaximumFractionDigits(1);
//					df.setMinimumFractionDigits(1);
//					float progress = 0.0f;
//					if (dldata.getCurrentSize() > 0
//							&& dldata.getTotalSize() > 0) {
//						progress = (float) dldata.getCurrentSize() * 100.0f
//								/ dldata.getTotalSize();
//					}
//					String p = df.format(progress);
//					holder.percentTv.setText(p + "%");
//					holder.progressBar.setMax(dldata.getTotalSize());
//					holder.progressBar.setProgress(dldata.getCurrentSize());
//				}
//
//				break;
//			}
//			holder.titleTv.setText(title);
//			holder.payTv.setText(yuan);
//
//			holder.qBuyButton.setId(position);
//			// holder.qBuyButton.setOnClickListener(buttonListener);
//			holder.qBuyButton.setOnClickListener(new DonwLoadButtonListener(
//					isDown));
//			holder.res_view.setId(position);
//			holder.res_view.setOnClickListener(resViewItemListner);
//
//			String review = mContext.getString(R.string.res_ml);
//			review = review.replace("*", resModel.getMd());
//			holder.reviewTv.setText(review);
//
//			float len = Float.valueOf(resModel.getEt().trim());
//			holder.starBar.setRating(len);
//
//			String titleImgUrl = resModel.getIn();
//			holder.titleIv.setBackgroundResource(R.drawable.logo);
//			InputStream is = CacheManager.newInstance().getResourceCache(
//					titleImgUrl);
//
//			if (is == null) {
//				ImageUtil.addDownloadTask(mContext, resModel.getP(),
//						titleImgUrl, mHandler);
//			} else {
//				Bitmap bitmap = null;
//				bitmap = BitmapFactory.decodeStream(is);
//				System.gc();
//				if (bitmap != null) {
//					holder.titleIv.setBackgroundDrawable(new BitmapDrawable(
//							bitmap));
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		convertView.setId(id);
//		return convertView;
//	}
//
//	class DonwLoadButtonListener implements OnClickListener {
//		int isDown = 0;
//
//		public DonwLoadButtonListener(int isDown) {
//			this.isDown = isDown;
//		}
//
//		@Override
//		public void onClick(View v) {
//			HResLibModel resModel = mResList.get(v.getId());
////			if (!onClickButton) {
////				onClickButton = true;
//				switch (isDown) {
//				case DOWNLOADING:
//					
//					break;
//				case INSTALLED:
////					onMineActivity();
//					diaHandler.sendEmptyMessage(4);
//					break;
//				case DOWNLOADED:
//					buttonEvent(resModel, 0);
//					break;
//				case NO_DOWNLOAD:
//					buttonEvent(resModel, 1);
//					break;
//
//				}
////			}
//
//		}
//
//	}
//	private class OnStateListener implements OnClickListener {
//		private long mID;
//		
//		public OnStateListener(long id) {
//			mID = id;
//		}
//
//		@Override
//		public void onClick(View v) {
//			
//			if(mID <= 0)
//			{
//				return;
//			}
//			DLData task = null;
//			for(DLData d : mDLDatalist)
//			{
//				if(d.getId() == mID)
//				{
//					task = d;
//				}
//			}
//			if(null == task)
//			{
//				return;
//			}
//			if(DLManager.STATUS_RUNNING == task.getStatus())
//			{
//				mDLManager.pauseTask(task.getId());
//				HResLibAdapter.this.notifyDataSetChanged();
//				return;
//			}
//			if(DLManager.STATUS_PAUSE == task.getStatus())
//			{
//				mDLManager.restartTask(task.getId());
//				HResLibAdapter.this.notifyDataSetChanged();
//				return;
//			}
//			if(DLManager.STATUS_READY == task.getStatus())
//			{
//				mDLManager.pauseTask(task.getId());
//				HResLibAdapter.this.notifyDataSetChanged();
//				return;
//			}
//			if(DLManager.STATUS_SUCCESS == task.getStatus())
//			{   
//				HResLibAdapter.this.notifyDataSetChanged();
//				return;
//			}
//		}
//	}
//	/*
//	public void onMineActivity() {
//		HStatistics mStatistics = new HStatistics(mContext);
//		mStatistics.add(HStatistics.Z8_4, "", "", "");
//		Intent intent = new Intent(mContext, HResMineActivity.class);
//		mContext.startActivity(intent);
//	}
//    */
//	/** 限制快速重复点击 ，出此下策情非得已 */
//	boolean onClickButton = false;
//	long time = 0;
//	long startTime = 0;
//
//	// OnClickListener buttonListener = new OnClickListener() {
//	//
//	// @Override
//	// public void onClick(final View v) {
//	// // time = System.currentTimeMillis();
//	// // if(startTime !=0 && time < startTime + 500){
//	// // return ;
//	// // }
//	// // startTime = System.currentTimeMillis();
//	// if (onClickButton == false) {
//	// HResLibModel resModel = mResList.get(v.getId());
//	// onClickButton = true;
//	// Button qbutton = (Button) v;
//	// // 安装
//	// String install = mContext.getText(R.string.install).toString();
//	// String downLoading = mContext.getText(R.string.res_ding)
//	// .toString();
//	// String buttonText = qbutton.getText().toString();
//	// String buttonUse = mContext.getText(R.string.skin_bt_use)
//	// .toString();
//	// if (buttonText.equals(install)) { // 已下载
//	// mStatistics
//	// .add(HStatistics.Z10_5, resModel.getMi(), "", "");
//	// buttonEvent(qbutton, resModel, 0);
//	// } else if (buttonText.equals(downLoading)
//	// || buttonText.equals(mContext
//	// .getText(R.string.skin_bt_useing))) { // 下载中
//	// qbutton.setClickable(false);
//	// // qbutton.setBackgroundResource(R.drawable.res_de_installed);
//	// onClickButton = false;
//	//
//	// } else if (buttonText.equals(buttonUse)) { // 应用
//	// // mStatistics.add(HStatistics.Z12_4, resModel.getMi(), "",
//	// // "");
//	// qbutton.setText(mContext.getText(R.string.skin_bt_useing));
//	// qbutton.setClickable(false);
//	// HResLibAdapter.this.notifyDataSetChanged();
//	// ContentValues cvalues = new ContentValues();
//	// cvalues.put(HResDatabaseHelper.RES_USE, 0);
//	// mContext.getContentResolver().update(
//	// HResProvider.CONTENT_URI_SKIN, cvalues, null, null);
//	// ContentValues values = new ContentValues();
//	// values.put(HResDatabaseHelper.RES_USE, 1);
//	// mContext.getContentResolver().update(
//	// HResProvider.CONTENT_URI_SKIN,
//	// values,
//	// HResDatabaseHelper.PACKAGENAME + " = '"
//	// + resModel.getPkn() + "'", null);
//	// Cursor c = mContext.getContentResolver().query(
//	// HResProvider.CONTENT_URI_SKIN, null,
//	// HResDatabaseHelper.RES_USE + " = '1'", null, null);
//	// onClickButton = false;
//	// if (c.getCount() > 0) {
//	// c.moveToNext();
//	// HStatistics mHStatistics = new HStatistics(mContext);
//	// int charge = c.getInt(c
//	// .getColumnIndex(HResDatabaseHelper.CHARGE));
//	// String resid = c.getString(c
//	// .getColumnIndex(HResDatabaseHelper.RES_ID));
//	// resid = (resid == null || resid.trim().equals("")) ? "0"
//	// : resid;
//	// mHStatistics.add(HStatistics.Z12_1, resid,
//	// (charge == 0) ? "0" : "1", "1");
//	// SkinManage.mCurrentSkin = c
//	// .getString(c
//	// .getColumnIndex(HResDatabaseHelper.PACKAGENAME));
//	// }
//	// c.close();
//	//
//	// } else {// 未下载
//	// String pay = resModel.getPl();
//	// if (pay.equals("-1")) {
//	// pay = "1";
//	// }
//	// mStatistics.add(HStatistics.Z10_1, resModel.getMi(), ""
//	// + mFlag, pay);
//	// mStatistics
//	// .add(HStatistics.Z10_3, resModel.getMi(), "", "");
//	// mStatistics
//	// .add(HStatistics.Z10_4, resModel.getMi(), "", "");
//	// buttonEvent(qbutton, resModel, 1);
//	// }
//	// HResLibAdapter.this.notifyDataSetChanged();
//	// } else {
//	// return;
//	// }
//	// }
//	// };
//
//	private void buttonEvent(final HResLibModel model, int flag) {
//		String pay = model.getPl();
//
//		if (!pay.equals("0"))
//			extracted(model, flag, pay);
//		else {
//			switch (flag) {
//			case 0:// 安装
//				installClick(model);
//				break;
//			case 1:// 下载
//				DonwloadModel(model);
//				break;
//			}
//		}
//	}
//
//	private void extracted(final HResLibModel model, int flag, String pay) {
//		{
//
//			SharedPreferences pref = mContext.getSharedPreferences(
//					HConst.PREF_USER, Context.MODE_PRIVATE);
//			boolean isLogin = pref.getBoolean(HConst.USER_KEY_LOGIN, false);
//			HLog.d("isLogin", String.valueOf(isLogin));
//			boolean Im = ToolsUtil.IM_FLAG;
//			if (Im && !isLogin) {
//				if (flag == 1)// 统计模板未登录状态下点击下载数
//					mStatistics.add(HStatistics.Z10_3, model.getMi(), "", "");
//				Toast.makeText(mContext,
//						mContext.getString(R.string.login_toast),
//						Toast.LENGTH_SHORT).show();
//				mContext.startActivity(new Intent(mContext,
//						HResLoginActivity.class));
//				onClickButton = false;
//			} else {
//				switch (flag) {
//				case 0:// 安装
//					installClick(model);
//					break;
//				case 1:// 下载
//					if (pay.equals("1")) {
//						diaHandler.sendEmptyMessage(3);
//						new Thread() {
//							@Override
//							public void run() {
//								Object obj = new PayBussiness().applyPay(
//										mContext, model.getMi(), ToolsUtil
//												.getPhoneNum(mContext));
//								Message msg = new Message();
//								msg.obj = obj;
//								Bundle data = new Bundle();
//								data.putString("Mi", model.getMi());
//								msg.setData(data);
//								mResModel = model;
//								payHandler.sendMessage(msg);
//							}
//						}.start();
//
//					} else {
//
//						DonwloadModel(model);
//					}
//					break;
//				}
//			}
//		}
//	}
//
//	// Button button;
//	HResLibModel mResModel;
//	private Handler payHandler = new Handler() {
//		public void handleMessage(Message msg) {
//			Bundle bundle = msg.getData();
//			String id = bundle.getString("Mi");
//			HMoldPay hmp = (HMoldPay) msg.obj;
//			if (mResModel != null && mResModel.getMi().equals(id)) {
//				downloadClick(id, ToolsUtil.getPhoneNum(mContext), hmp,
//						mResModel);
//			}
//
//		};
//	};
//
//	/***
//	 * 收费模版点击下载的时候进行收费提示，点击确认付费按钮后才开始进行下载
//	 * 
//	 * @param resId
//	 *            模版ID
//	 * @param phoneNumber
//	 *            本机电话号码
//	 */
//	private boolean downloadClick(final String resId, final String phoneNumber,
//			final HMoldPay hmd, final HResLibModel model) {
//		String message = "";
//		// 得到付费提示信息
//		try {
//			message = hmd.getPayRc();
//			if (message == null) {
//				HResLibAdapter.this.notifyDataSetChanged();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			Toast.makeText(mContext,
//					mContext.getString(R.string.toast_info_new),
//					Toast.LENGTH_SHORT).show();
//			diaHandler.sendEmptyMessage(2);
//			return false;
//		}
//		diaHandler.sendEmptyMessage(2);
//		if (mConfirmDialog == null) {
//
//			if (!ToolsUtil.readSIMCard(mContext)) {
//				mConfirmDialog = new AlertDialog.Builder(mContext).setTitle(
//						mContext.getString(R.string.notice)).setMessage(
//						mContext.getString(R.string.simInvalid))
//						.setPositiveButton(
//								mContext.getString(R.string.confirm),
//								new DialogInterface.OnClickListener() {
//
//									@Override
//									public void onClick(DialogInterface dialog,
//											int which) {
//										if (null != mConfirmDialog) {
//											mConfirmDialog.dismiss();
//											mConfirmDialog = null;
//										}
//									}
//								}).create();
//				mConfirmDialog.show();
//				onClickButton = false;
//				return false;
//			} else if (!ToolsUtil.checkNet(mContext)) {
//				mConfirmDialog = new AlertDialog.Builder(mContext).setTitle(
//						mContext.getString(R.string.notice)).setMessage(
//						mContext.getString(R.string.no_connect))
//						.setPositiveButton(
//								mContext.getString(R.string.confirm),
//								new DialogInterface.OnClickListener() {
//
//									@Override
//									public void onClick(DialogInterface dialog,
//											int which) {
//										if (null != mConfirmDialog) {
//											mConfirmDialog.dismiss();
//											mConfirmDialog = null;
//										}
//										diaHandler.sendEmptyMessage(0);
//									}
//								}).create();
//				mConfirmDialog.show();
//				onClickButton = false;
//				return false;
//			}
//			{
//				mConfirmDialog = new AlertDialog.Builder(mContext).setTitle(
//						mContext.getString(R.string.spopu_bt_dl) + ":"
//								+ model.getPn()).setMessage(message)
//						.setCancelable(false).setPositiveButton(
//								mContext.getString(R.string.confirm),
//								new DialogInterface.OnClickListener() {
//
//									@Override
//									public void onClick(DialogInterface dialog,
//											int which) {
//										if (null != mConfirmDialog) {
//											mConfirmDialog.dismiss();
//											mConfirmDialog = null;
//										}
//										onClickButton = false;
//										// diaHandler.sendEmptyMessage(1);
//										// 进行付费动作
//										HStatistics mHStatistics = new HStatistics(
//												mContext);
//										mHStatistics.add(HStatistics.Z11_1,
//												model.getMi(), "", "");
//										new PayBussiness().startPay(mContext,
//												hmd, phoneNumber);
//										// 把该模板的付费状态设置为已付费
//										model.setPl("-1");
//										DonwloadModel(model);
//										HResLibAdapter.this
//												.notifyDataSetChanged();
//									}
//								}).setNegativeButton(
//								mContext.getString(R.string.cancel),
//								new DialogInterface.OnClickListener() {
//
//									@Override
//									public void onClick(DialogInterface dialog,
//											int which) {
//										if (null != mConfirmDialog) {
//											mConfirmDialog.dismiss();
//											mConfirmDialog = null;
//										}
//										onClickButton = false;
//										HStatistics mHStatistics = new HStatistics(
//												mContext);
//										mHStatistics.add(HStatistics.Z11_2,
//												model.getMi(), "", "");
//										HResLibAdapter.this
//												.notifyDataSetChanged();
//										diaHandler.sendEmptyMessage(0);
//									}
//								}).create();
//				mConfirmDialog.show();
//			}
//		}
//		return false;
//	}
//
//	Handler mDownLoadHandler = new Handler() {
//		public void handleMessage(Message msg) {
//			Bundle bundle = msg.getData();
//			DLData dldata = (DLData) bundle.getSerializable("task");
//			switch (msg.what) {
//			case DLManager.ACTION_DELETE:
//				HResLibAdapter.this.notifyDataSetChanged();
//				break;
//			case DLManager.ACTION_SUCCESS:
//				HResLibAdapter.this.notifyDataSetChanged();
//				break;
//			case DLManager.ACTION_UPDATE:
//				View view = null;
//				if (dldata != null)
//					view = mListView.findViewById(Integer.parseInt(dldata
//							.getResID().trim()));
//				if (view != null) {
//					ProgressBar progressBar = (ProgressBar) view
//							.findViewById(R.id.res_detail_progressbar_new);
//					TextView percentTv = (TextView) view
//							.findViewById(R.id.res_percent);
//					ImageView stageIv = (ImageView) view
//							.findViewById(R.id.res_stage);
//					DecimalFormat df = new DecimalFormat();
//					df.setMaximumFractionDigits(1);
//					df.setMinimumFractionDigits(1);
//					float progress = 0.0f;
//					if (dldata.getCurrentSize() > 0
//							&& dldata.getTotalSize() > 0) {
//						progress = (float) dldata.getCurrentSize() * 100.0f
//								/ dldata.getTotalSize();
//					}
//					String p = df.format(progress);
//					percentTv.setText(p + "%");
//					progressBar.setMax(dldata.getTotalSize());
//					progressBar.setProgress(dldata.getCurrentSize());
//
//				}
//				break;
//			}
//
//		};
//	};
//
//	private void DonwloadModel(HResLibModel resModel) {
//		mDLManager.setProgressHandler(mDownLoadHandler);
//		DLData dlData = new DLData();
//		dlData.setResID(resModel.getMi()); // 传入模板ID即可
//		dlData.setFileName(resModel.getPu());
//		dlData.setDisplayName(resModel.getPn());
//		dlData.setCharge(resModel.getPl());
//		dlData.setChargeMsg(resModel.getRc());
//
//		dlData.setIconUrl(resModel.getIn());
//		dlData.setPackagename(resModel.getPkn());
//		mDLManager.addTask(dlData);
//		/*
//		 * Intent i = new Intent(mContext, HService.class); Bundle bundle = new
//		 * Bundle(); bundle.putSerializable("task", dlData);
//		 * mContext.startService(i);
//		 */
//
//		onClickButton = false;
//		HResLibAdapter.this.notifyDataSetChanged();
//	}
//
//	OnClickListener resViewItemListner = new OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//
//			if (onClickButton == false) {
//				if (!ToolsUtil.checkNet(mContext)) {
//					Toast.makeText(mContext,
//							mContext.getString(R.string.no_connect),
//							Toast.LENGTH_LONG).show();
//				} else {
//					HResLibModel hlm = mResList.get(v.getId());
//					Intent intent = new Intent();
//					switch (mFlag) {
//					case mTYPEFNEW:
//						intent.putExtra("type", "2");
//						// mStatistics.add(HStatistics.Z8_4, "", "", "");
//						break;
//					case mTYPEFREE:
//						intent.putExtra("type", "0");
//						// mStatistics.add(HStatistics.Z8_5, "", "", "");
//						break;
//					case mTYPEHOT:
//						intent.putExtra("type", "1");
//						// mStatistics.add(HStatistics.Z8_6, "", "", "");
//						break;
//					}
//
//					intent.putExtra("HRes", hlm);
//					intent.putExtra("ID", hlm.getMi());
//					intent.setClass(mContext, HResDetailActivity.class);
//					intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
//					((HResLibActivity) mContext).startActivityForResult(intent,
//							20);
//
//				}
//			}
//		}
//	};
//
//	class MyHolder {
//		View proView;
//		TextView percentTv;
//		ImageView stageIv;
//		TextView payTv;
//		View res_view;
//		/* 缩略图 */
//		ImageView titleIv;
//		/* 模版标题 */
//		TextView titleTv;
//		/* 模版评星 */
//		RatingBar starBar;
//		/* 评论文字 */
//		TextView reviewTv;
//		Button qBuyButton;
//		ProgressBar progressBar;
//		/** 下载区域layout */
//		// LinearLayout mProgressLayout;
//	}
//
//	class ResModel {
//		String modelName = "";
//		String modelSize = "";
//		int starNum = 0;
//		String reviewNum = "";
//
//		public ResModel(String name, String size, int star, String review) {
//			this.modelName = name;
//			this.modelSize = size;
//			this.starNum = star;
//			this.reviewNum = review;
//		}
//
//	}
//
//	/** 是否在加载列表中 */
//	private boolean scrollLoad = false;
//
//	private void getResList(int flag, int start, int page) {
//
//		ArrayList<HResLibModel> list = new ArrayList<HResLibModel>();
//		switch (flag) {
//		case mTYPEHOT:
//			list = new HResLibParser(mContext, HConst.RESLIB_PYLST, start)
//					.getResLibCacheList(ToolsUtil.getLanguage()
//							+ HConst.RESLIB_PYLST + page, page);
//
//			break;
//		case mTYPEFREE:
//			list = new HResLibParser(mContext, HConst.RESLIB_FRLST, start)
//					.getResLibCacheList(ToolsUtil.getLanguage()
//							+ HConst.RESLIB_FRLST + page, page);
//			break;
//		case mTYPEFNEW:
//			list = new HResLibParser(mContext, HConst.RESLIB_CXLST, start)
//					.getResLibCacheList(ToolsUtil.getLanguage()
//							+ HConst.RESLIB_CXLST + page, page);
//			break;
//		}
//
//
//		if (list != null) {
//			int len = list.size();
//			if (len < 10) {
//				nextList = false;
//			}
//			for (int i = 0; i < len; i++) {
//				mResList.add(list.get(i));
//			}
//			cacheListNull = false;
//		} else {
//			cacheListNull = true;
//			diaHandler.sendEmptyMessage(1);
//			// if (pageIndex > 1)
//			// pageIndex--;
//		}
//		scrollLoad = false;
//		getListPage = page;
//		setGridBg();
//		Message msg = new Message();
//		Bundle data = new Bundle();
//		data.putInt("page", page);
//		data.putInt("start", start);
//		msg.setData(data);
//		msg.what = flag;
//		getListHandler.sendMessage(msg);
//
//	}
//
//	private boolean cacheListNull = false;
//	private int getListPage = 0;
//	private Handler mLoadHandler = new Handler() {
//		@SuppressWarnings("unchecked")
//		@Override
//		public void handleMessage(Message msg) {
//			ArrayList<HResLibModel> list = (ArrayList<HResLibModel>) msg.obj;
//			int llen = 0;
//			Bundle bundle = msg.getData();
//			int index = bundle.getInt("page");
//			int page = (index - 1) * HConst.REQUEST_NUMBER;
//
//			if (list != null) {
//				int mlen = mResList.size();
//
//				llen = list.size();
//				if (llen < HConst.REQUEST_NUMBER && index == getListPage) {
//					nextList = false;
//				} else {
//					nextList = true;
//
//				}
//				for (int i = 0; i < HConst.REQUEST_NUMBER; i++) {
//					if (i >= llen) {
//						if (page + i < mlen)
//							mResList.remove((page + llen));
//
//					} else {
//						if (page + i < mlen) {
//							mResList.set(page + i, list.get(i));
//						} else {
//							mResList.add(list.get(i));
//						}
//					}
//
//				}
//			} else {
//				if (!ToolsUtil.checkNet(mContext)) {
//					Toast.makeText(mContext,
//							mContext.getString(R.string.no_connect),
//							Toast.LENGTH_LONG).show();
//				} else {
//					if (cacheListNull) {// netList&& caheList == null ,没有列表
//						nextList = false;
//						if (mResList.size() != 0)
//							Toast.makeText(mContext,
//									mContext.getText(R.string.loading_more),
//									Toast.LENGTH_LONG).show();
//					}
//
//				}
//			}
//			scrollLoad = false;
//			diaHandler.sendEmptyMessage(0);
//			setGridBg();
//
//		}
//
//	};
//	/** 是否有下一页 */
//	private boolean nextList = true;
//	private int pageIndex = 1;
//	OnScrollListener osl = new OnScrollListener() {
//
//		@Override
//		public void onScrollStateChanged(AbsListView view, int scrollState) {
//			int start = view.getCount();
//			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
//				if (view.getLastVisiblePosition() == start - 1) {
//					if (nextList == false && mResList.size() != 0) {
//						Toast.makeText(mContext,
//								mContext.getText(R.string.loading_more),
//								Toast.LENGTH_LONG).show();
//						return;
//					}
//					if (!scrollLoad && nextList) {
//						scrollLoad = true;
//						pageIndex++;
//						getResList(mFlag, start + 1, pageIndex);
//						HResLibAdapter.this.notifyDataSetChanged();
//					}
//
//				}
//			}
//
//		}
//
//		@Override
//		public void onScroll(AbsListView view, int firstVisibleItem,
//				int visibleItemCount, int totalItemCount) {
//		}
//	};
//
//	private Handler getListHandler = new Handler() {
//		public void handleMessage(Message msg) {
//			final int page = msg.getData().getInt("page");
//			final int start = msg.getData().getInt("start");
//			switch (msg.what) {
//			case mTYPEHOT:
//				new HResLibParser(mContext, HConst.RESLIB_PYLST, start)
//						.getResLibNetList(ToolsUtil.getLanguage()
//								+ HConst.RESLIB_PYLST + page, mLoadHandler,
//								page);
//				break;
//			case mTYPEFREE:
//				new HResLibParser(mContext, HConst.RESLIB_FRLST, start)
//						.getResLibNetList(ToolsUtil.getLanguage()
//								+ HConst.RESLIB_FRLST + page, mLoadHandler,
//								page);
//
//				break;
//			case mTYPEFNEW:
//				new HResLibParser(mContext, HConst.RESLIB_CXLST, start)
//						.getResLibNetList(ToolsUtil.getLanguage()
//								+ HConst.RESLIB_CXLST + page, mLoadHandler,
//								page);
//
//				break;
//			}
//
//		};
//	};
//
//	public static final int ACTION_UPDATE = 0;
//	public static final int ACTION_DELETE = 1;
//	Dialog mConfirmDialog;
//
//	// private String chareStr = "";
//
//	private void installClick(final HResLibModel info) {
//		DLData task = new DLData();
//		if (info.getPu() == null) {
//			onClickButton = false;
//			return;
//		} else {
//
//			task.setStatus(DLManager.STATUS_SUCCESS);
//			task.setFileName(info.getPu());
//			task.setCharge(info.getPl());
//			task.setChargeMsg(info.getRc());
//			task.setDisplayName(info.getPn());
//		}
//
//		if (DLManager.STATUS_SUCCESS == task.getStatus()) {
//			if (null != mConfirmDialog) {
//				if (mConfirmDialog.isShowing()) {
//					onClickButton = false;
//					return;
//				} else {
//					onClickButton = false;
//					mConfirmDialog.show();
//				}
//			} else {
//				final DLData tas = task;
//				int charge = 0;
//				if (task.getCharge() != null && !"".equals(task.getCharge())) {
//					charge = Integer.valueOf(task.getCharge());
//					// chareStr = String.valueOf(charge);
//				}
//				String message = "";
//
//				if (charge == 1 || charge == -1) {
//					message = String.format(mContext
//							.getString(R.string.install_paid), task
//							.getDisplayName());
//				} else {
//					message = String.format(mContext
//							.getString(R.string.install_free), task
//							.getDisplayName());
//				}
//
//				if (charge == 1) {
//					if (!ToolsUtil.readSIMCard(mContext)) {
//						mConfirmDialog = new AlertDialog.Builder(mContext)
//								.setTitle(mContext.getString(R.string.notice))
//								.setMessage(
//										mContext.getString(R.string.simInvalid))
//								.setPositiveButton(
//										mContext.getString(R.string.confirm),
//										new DialogInterface.OnClickListener() {
//
//											@Override
//											public void onClick(
//													DialogInterface dialog,
//													int which) {
//												if (null != mConfirmDialog) {
//													mConfirmDialog.dismiss();
//													mConfirmDialog = null;
//												}
//											}
//										}).create();
//						mConfirmDialog.show();
//						onClickButton = false;
//						return;
//					} else if (!ToolsUtil.checkNet(mContext)) {
//						mConfirmDialog = new AlertDialog.Builder(mContext)
//								.setTitle(mContext.getString(R.string.notice))
//								.setMessage(
//										mContext.getString(R.string.no_connect))
//								.setPositiveButton(
//										mContext.getString(R.string.confirm),
//										new DialogInterface.OnClickListener() {
//
//											@Override
//											public void onClick(
//													DialogInterface dialog,
//													int which) {
//												if (null != mConfirmDialog) {
//													mConfirmDialog.dismiss();
//													mConfirmDialog = null;
//												}
//											}
//										}).create();
//						mConfirmDialog.show();
//						onClickButton = false;
//						return;
//					}
//				}
//				// else
//				{
//					mConfirmDialog = new AlertDialog.Builder(mContext)
//							.setTitle(
//									mContext
//											.getString(R.string.confirm_install))
//							.setMessage(message).setPositiveButton(
//									mContext.getString(R.string.confirm),
//									new DialogInterface.OnClickListener() {
//
//										@Override
//										public void onClick(
//												DialogInterface dialog,
//												int which) {
//											// if (chareStr.equals("1")) {
//											// mStatistics.add(
//											// HStatistics.Z11_1, "",
//											// "", "");
//											// } else {
//											mStatistics.add(
//													HStatistics.Z10_5_1, info
//															.getMi(), "", "");
//											// }
//
//											Intent intent = new Intent(
//													Intent.ACTION_VIEW);
//											intent
//													.setDataAndType(
//															Uri
//																	.fromFile(new File(
//																			DLManager.LOCAL_PATH
//																					+ File.separator
//																					+ tas
//																							.getFileName())),
//															"application/vnd.android.package-archive");
//											intent
//													.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//											intent
//													.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//											mContext.startActivity(intent);
//
//											if (null != mConfirmDialog) {
//												mConfirmDialog.dismiss();
//												mConfirmDialog = null;
//											}
//										}
//									}).setNegativeButton(
//									mContext.getString(R.string.cancel),
//									new DialogInterface.OnClickListener() {
//
//										@Override
//										public void onClick(
//												DialogInterface dialog,
//												int which) {
//											// if (chareStr.equals("1")) {
//											// mStatistics.add(
//											// HStatistics.Z11_2, "",
//											// "", "");
//											// } else {
//											mStatistics.add(
//													HStatistics.Z10_5_2, info
//															.getMi(), "", "");
//											// }
//											if (null != mConfirmDialog) {
//												mConfirmDialog.dismiss();
//												mConfirmDialog = null;
//											}
//										}
//									}).create();
//					mConfirmDialog.show();
//				}
//			}
//			onClickButton = false;
//			return;
//		}
//
//	}
//
//	/***
//	 * 0.已安装 0 灰色按钮显示“已安装” 1.已下载 1 按钮可点击显示“安装” 2.未下载 2 按钮可点击显示“下载” 3.下载中 3 按钮不可见
//	 * 
//	 * @param packageName
//	 * @param tv
//	 * @return
//	 */
//	private static final int INSTALLED = 0;
//	private static final int DOWNLOADED = 1;
//	private static final int NO_DOWNLOAD = 2;
//	private static final int DOWNLOADING = 3;
//
//	private int getStatus(String packageName) {
//		int dlStatus = 0;
//		/** 查询是否已经安装 */
//		Cursor insCor = mContext.getContentResolver().query(
//				HResProvider.CONTENT_URI_SKIN, null,
//				HResDatabaseHelper.PACKAGENAME + " = '" + packageName + "'",
//				null, null);
//		if (insCor != null && insCor.getCount() > 0) {
//			insCor.close();
//			return INSTALLED;
//		} else {
//			/** 从download表中查询后三种状态 */
//			Cursor dlCor = mContext.getContentResolver()
//					.query(
//							HResProvider.CONTENT_URI_DOWNLOAD,
//							null,
//							HResDatabaseHelper.PACKAGENAME + " = '"
//									+ packageName + "'", null, null);
//			if (dlCor == null) {
//				if (insCor != null) {
//					insCor.close();
//				}
//				return NO_DOWNLOAD;
//			}
//			while (dlCor.moveToNext()) {
//				if (dlCor.getCount() > 0) {
//
//					dlStatus = dlCor.getInt(dlCor
//							.getColumnIndex(HResDatabaseHelper.TASK_STATUS));
//					if (dlStatus == DLManager.STATUS_SUCCESS) {
//						HLog.d("dlCor.count", dlCor.getCount() + "DOWNLOADED");
//						if (insCor != null) {
//							insCor.close();
//						}
//						dlCor.close();
//						return DOWNLOADED;
//					} else {
//						HLog.d("dlCor.count", dlCor.getCount() + "DOWNLOADING");
//						if (insCor != null) {
//							insCor.close();
//						}
//						dlCor.close();
//						return DOWNLOADING;
//					}
//				} else {
//					HLog.d("dlCor.count", dlCor.getCount() + "NO_DOWNLOAD");
//					if (insCor != null) {
//						insCor.close();
//					}
//					dlCor.close();
//					return NO_DOWNLOAD;
//				}
//			}
//			if (insCor != null) {
//				insCor.close();
//			}
//			dlCor.close();
//		}
//		HLog.d("dlCor.count", "NO_DOWNLOAD");
//		return NO_DOWNLOAD;
//	}
//	
//}
