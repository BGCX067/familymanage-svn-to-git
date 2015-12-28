package com.lianluo.core.task;

public class TaskManagerFactory {
	
	private static TaskManager mParserTaskManager, mImageTaskManager;

	public static TaskManager createParserTaskManager() {
		if (mParserTaskManager == null) {
			mParserTaskManager = new TaskManager(0, 5);
		}
		return mParserTaskManager;
	}
	
	public static TaskManager createImageTaskManager() {
		if (mImageTaskManager == null) {
			mImageTaskManager = new TaskManager(0, 5);
		}
		return mImageTaskManager;
	}
	
}
