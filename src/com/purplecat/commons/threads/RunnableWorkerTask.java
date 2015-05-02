package com.purplecat.commons.threads;

public class RunnableWorkerTask implements Runnable {
	private IThreadPool _runner;
	private IThreadTask _task;
	
	public RunnableWorkerTask(IThreadPool runner, IThreadTask task) {
		_runner = runner;
		_task = task;
	}
	
	@Override
	public void run() {
		//TODO: assert not on UI thread.
		if ( _runner.isUIThread() ) {
			throw new IllegalStateException("RunnableWorkerTask cannot be run on UI thread.");
		}
		_task.workerTaskStart();
		_runner.runOnUIThread(_task);
	}
}
