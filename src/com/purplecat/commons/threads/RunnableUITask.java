package com.purplecat.commons.threads;

public class RunnableUITask implements Runnable {
	IThreadPool _runner;
	IThreadTask _task;
	
	public RunnableUITask(IThreadPool runner, IThreadTask task) {
		_runner = runner;
		_task = task;
	}
	
	@Override
	public void run() {
		if ( !_runner.isUIThread() ) {
			throw new IllegalStateException("RunnableUITask must be run on UI thread.");
		}
		_task.uiTaskCompleted();
	}
}
