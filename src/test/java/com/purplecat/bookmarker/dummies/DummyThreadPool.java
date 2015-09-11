package com.purplecat.bookmarker.dummies;

import com.purplecat.commons.threads.IThreadPool;
import com.purplecat.commons.threads.IThreadTask;

public class DummyThreadPool implements IThreadPool {

	@Override
	public void runOnUIThread(IThreadTask task) {
		task.uiTaskCompleted();
	}

	@Override
	public void runOnUIThread(Runnable task) {
		task.run();
	}

	@Override
	public void runOnWorkerThread(IThreadTask task) {
		task.workerTaskStart();
		runOnUIThread(task);
	}

	@Override
	public void runOnWorkerThread(Runnable task) {
		task.run();
	}

	@Override
	public boolean isUIThread() {
		return false;
	}
	
}