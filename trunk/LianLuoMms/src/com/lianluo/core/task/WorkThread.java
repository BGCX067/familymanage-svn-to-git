package com.lianluo.core.task;


/**
 * 工作线程
 * 
 * @author ZhouJian
 * 
 */
public class WorkThread extends Thread {

	private TaskManager mManager;
	private ITask mTask;

	public WorkThread(TaskManager manager, int id) {
		super(manager, id + "");
		mManager = manager;
	}

	public void run() {
		while (true) {
			try {
				mTask = mManager.getTask();
			} catch (IllegalStateException ex) {
				break;
			} catch (NullPointerException ex) {
				mManager.waitThread();
				continue;
			}
			mManager.taskStart();
			mTask.execute();
			mManager.taskFinish();
			if(mManager.isThreadFinish()) {
				break;
			}
		}
	}
}
