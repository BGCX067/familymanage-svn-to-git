package com.lianluo.core.task;

import com.lianluo.core.event.IEvent;

/**
 * 任务基类
 * 
 * @author ZhouJian
 * 
 */
public abstract class BaseTask implements ITask {

	private IEvent mEvent;// 事件接口

	public BaseTask(IEvent event) {
		mEvent = event;
	}

	/**
	 * 任务执行方法(由线程池调用)
	 */
	@Override
	public final void execute() {
		try {
			if(mEvent != null) {
				mEvent.onStart();
			}
			doTask(mEvent);
			if(mEvent != null) {
				mEvent.onFinish();
			}
		} catch (Exception ex) {
			if(mEvent != null) {
				mEvent.onError(ex);
			}
		}
	}

	/**
	 * 耗时操作
	 * 
	 * @throws Exception
	 */
	public abstract void doTask(IEvent event) throws Exception;

	/**
	 * 任务位置改变时回调函数
	 * 
	 * @param taskNum
	 *            当前位置
	 */
	@Override
	public void onTaskNumChanged(int taskNum) {
	}
}
