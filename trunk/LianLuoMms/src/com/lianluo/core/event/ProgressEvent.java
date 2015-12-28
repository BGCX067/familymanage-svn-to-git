package com.lianluo.core.event;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;


/**
 * 事件子类(android平台ProgressDialog实现)
 * 
 * @author ZhouJian
 *
 */
public class ProgressEvent implements IEvent{
	
	private ProgressDialog mDialog;
	private Context mContext;
	private String mTitle, mMessage;
	private int mMax;
	private boolean mCancelable;
	private OnCancelListener mListener;
	private Handler mHandler;
	private boolean mFinished;
	
	/**
	 * 初始化可取消的ProgressDialog
	 * @param context 上下文
	 * @param message 内容
	 */
	public ProgressEvent(Context context, String message) {
		this(context, null, message, 0, true, null);
	}
	
	/**
	 * 初始化可取消的ProgressDialog
	 * @param context 上下文
	 * @param message 内容
	 * @param max 最大进度值
	 */
	public ProgressEvent(Context context, String message, int max) {
		this(context, null, message, max, true, null);
	}
	
	/**
	 * 初始化可取消的ProgressDialog
	 * @param context 上下文
	 * @param title 标题
	 * @param message 内容
	 */
	public ProgressEvent(Context context, String title, String message) {
		this(context, title, message, 0, true, null);
		
	}
	
	/**
	 * 初始化可取消的ProgressDialog
	 * @param context 上下文
	 * @param title 标题
	 * @param message 内容
	 * @param max 最大进度值
	 */
	public ProgressEvent(Context context, String title, String message, int max) {
		this(context, title, message, max, true, null);
	}
	
	/**
	 * 初始化ProgressDialog
	 * @param context 上下文
	 * @param title 标题
	 * @param message 内容
	 * @param cancelable 是否可取消
	 */
	public ProgressEvent(Context context, String title, String message, boolean cancelable) {
		this(context, title, message, 0, cancelable, null);
	}
	
	/**
	 * 初始化ProgressDialog
	 * @param context 上下文
	 * @param title 标题
	 * @param message 内容
	 * @param max 最大进度值
	 * @param cancelable 是否可取消
	 */
	public ProgressEvent(Context context, String title, String message, int max, boolean cancelable) {
		this(context, title, message, max, cancelable, null);
	}
	
	/**
	 * 初始化ProgressDialog
	 * @param context 上下文
	 * @param title 标题
	 * @param message 内容
	 * @param cancelable 是否可取消
	 * @param listener 取消监听
	 */
	public ProgressEvent(Context context, String title, String message, boolean cancelable, OnCancelListener listener) {
		this(context, title, message, 0, cancelable, listener);
	}
	
	/**
	 * 初始化ProgressDialog
	 * @param context 上下文
	 * @param title 标题
	 * @param message 内容
	 * @param max 最大进度值
	 * @param cancelable 是否可取消
	 * @param listener 取消监听
	 */
	public ProgressEvent(Context context, String title, String message, int max, boolean cancelable, OnCancelListener listener) {
		mContext = context;
		mTitle = title;
		mMessage = message;
		mMax = max;
		mCancelable = cancelable;
		mListener = listener;
		mHandler = new Handler();
	}

	/**
	 * 耗时操作执行前执行
	 * 初始化ProgressDialog并显示
	 */
	@Override
	public void onStart() {
		mFinished = false;
		showProgressDialog();
	}

	/**
	 * 耗时操作执行完执行
	 * ProgressDialog隐藏并销毁
	 */
	@Override
	public void onFinish() {
		dismissProgressDialog();
		mFinished = true;
	}
	
	/**
	 * 获得ProgressDialog
	 * @return ProgressDialog
	 */
	public ProgressDialog getProgressDialog() {
		return mDialog;
	}
	
	/**
	 * 是否已经执行完成
	 * @return
	 */
	public boolean isFinished() {
		return mFinished;
	}
	
	/**
	 * 显示ProgressDialog
	 */
	public void showProgressDialog() {
		if(!mFinished) {
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					if(mDialog == null) {
						mDialog = createProgressDialog();
					}
					mDialog.show();
				}
			});
		}
	}
	
	/**
	 * 隐藏ProgressDialog
	 */
	public void hideProgressDialog() {
		if(!mFinished) {
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					if(mDialog != null) {
						mDialog.hide();
					}
				}
			});
		}
	}
	
	/**
	 * 销毁ProgressDialog
	 */
	public void dismissProgressDialog() {
		if(!mFinished) {
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					if(mDialog != null) {
						mDialog.dismiss();
					}
				}
			});
		}
	}
	
	
	/**
	 * 设置当前进度
	 * @param progress 当前进度值
	 */
	public void setProgress(int progress) {
		if(mDialog != null && mMax > 0) {
			mDialog.setProgress(progress);
		}
	}
	
	/**
	 * 创建ProgressDialog
	 * @return ProgressDialog
	 */
	private ProgressDialog createProgressDialog() {
		ProgressDialog dialog = new ProgressDialog(mContext);
		dialog.setTitle(mTitle);
		dialog.setMessage(mMessage);
		if(mMax > 0) {
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setMax(mMax);
		}
		dialog.setCancelable(mCancelable);
		if(mCancelable && mListener != null) {
			dialog.setOnCancelListener(mListener);
		}
		return dialog;
	}

	/**
	 * 耗时操作发生错误时执行, 需要子类处理异常
	 * @param ex 错误信息
	 */
	@Override
	public void onError(Exception ex) {
		ex.printStackTrace();
	}

}
