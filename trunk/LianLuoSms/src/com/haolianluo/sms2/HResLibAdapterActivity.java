package com.haolianluo.sms2;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.model.HResLibModel;
import com.haolianluo.sms2.model.HResLibParser;
import com.lianluo.core.cache.CacheManager;
import com.lianluo.core.cache.ResourceFileCache;
import com.lianluo.core.util.ImageUtil;
import com.lianluo.core.util.ToolsUtil;

public class HResLibAdapterActivity {
	final int mTYPEHOT = 1;
	final int mTYPEFREE = 0;
	final int mTYPEPAY = 2;
	private int mFlag;
	private Context mContext;
	LinearLayout mLinearLayou;
	LayoutInflater mInflater;
	private ArrayList<HResLibModel> mResList = new ArrayList<HResLibModel>();
	Handler diaHandler;

	public HResLibAdapterActivity(Context context, int type,
			LinearLayout layout, boolean showDialog, Handler diaHandler) {
		mLinearLayou = layout;
		this.mFlag = type;
		this.mContext = context;
		mInflater = LayoutInflater.from(mContext);
		this.diaHandler = diaHandler;
		getResList(mFlag, 1, 1);
	}
    int page = 0;
	public LinearLayout getLinearLayout() {
		
		mLinearLayou.removeAllViews();
		mLinearLayou.setScrollbarFadingEnabled(true);
		for (int i = 0; i < mResList.size(); i++) {
			mLinearLayou.addView(getView(i), i);
		}
		int count = mResList.size();
		if (count != 0 && count % 10 == 0) {
			Button button = new Button(mContext);
			button.setText("更多");
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					page ++;
					getResList(mFlag,page*10, page);
					getLinearLayout();
                    
				}
			});
			mLinearLayou.addView(button);
			
		}
		return mLinearLayou;
	}

	/** 是否在加载列表中 */
//	private boolean scrollLoad = false;

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
		case mTYPEPAY:
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
		}
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

	int mCount = 0;

	private void setGridBg() {
		mCount = mResList.size();
		if (mCount == 0) {
			mLinearLayou.setBackgroundResource(R.drawable.no_net);
		} else {
			mLinearLayou.setBackgroundDrawable(null);
		}

	}

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
			case mTYPEPAY:
				new HResLibParser(mContext, HConst.RESLIB_CXLST, start)
						.getResLibNetList(ToolsUtil.getLanguage()
								+ HConst.RESLIB_CXLST + page, mLoadHandler,
								page);

				break;
			}

		};
	};
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
							Toast.makeText(mContext,
									mContext.getText(R.string.loading_more),
									Toast.LENGTH_LONG).show();
					}

				}
			}
			diaHandler.sendEmptyMessage(0);
			setGridBg();

		}

	};
	/** 是否有下一页 */
	private boolean nextList = true;
	private int pageIndex = 1;

	public View getView(int id) {
		HResLibModel resModel = mResList.get(id);
		String yuan = resModel.getPr();
		String name = resModel.getPn();
		
		View view = mInflater.inflate(R.layout.res_adapter_item, null);
        ImageView titleImg = (ImageView)view.findViewById(R.id.res_title) ;
        TextView titleText = (TextView)view.findViewById(R.id.res_title_text);
        titleText.setText(name);
        TextView payText = (TextView)view.findViewById(R.id.res_title_pay);
        payText.setText(yuan);
		view.setId(id);
		String titleImgUrl = resModel.getIn();
		titleImg.setBackgroundResource(R.drawable.logo);
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
				titleImg.setBackgroundDrawable(new BitmapDrawable(
						bitmap));
			}
		}
		view.setOnClickListener(resViewItemListner);
		return view;
	}
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			
				if (msg.what == ResourceFileCache.RESOURCE_SUCCESS) {
					
				}
			
		}

	};
	boolean onClickButton;
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
					case mTYPEPAY:
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
					((HResLibActivity) mContext).startActivityForResult(intent,
							20);

				}
			}
		}
	};
}
