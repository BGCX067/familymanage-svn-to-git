package com.lianluo.core.event;

/**
 * 事件接口(平台无关, 子类绑定平台)
 * 
 * @author ZhouJian
 *
 */
public interface IEvent {

	/**
	 * 耗时操作执行前执行
	 */
	public void onStart() throws Exception;
	
	/**
	 * 耗时操作执行完执行
	 */
	public void onFinish() throws Exception;
	
	/**
	 * 耗时操作发生错误时执行
	 * @param ex 错误信息
	 */
	public void onError(Exception ex);
}
