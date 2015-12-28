package com.lianluo.core.task;

/**
 * 任务接口
 * 
 * @author ZhouJian
 * 
 */
public interface ITask {

	/**
	 * 任务执行方法(由线程池调用)
	 */
	public void execute();

	/**
	 * 任务位置改变时回调函数
	 * 
	 * @param taskNum
	 *            当前位置
	 */
	public void onTaskNumChanged(int taskNum);

}
