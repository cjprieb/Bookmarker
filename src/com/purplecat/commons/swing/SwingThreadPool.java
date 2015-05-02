package com.purplecat.commons.swing;

import java.awt.EventQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.purplecat.commons.threads.IThreadPool;
import com.purplecat.commons.threads.IThreadTask;
import com.purplecat.commons.threads.RunnableUITask;
import com.purplecat.commons.threads.RunnableWorkerTask;

public class SwingThreadPool implements IThreadPool {
	public static final int MAX_THREADS = 5;
	
	public Executor mExecutor = null;
	
	public SwingThreadPool() {
		mExecutor = Executors.newFixedThreadPool(MAX_THREADS);		
	}	

	@Override
	public void runOnUIThread(IThreadTask task) {
		runOnUIThread(new RunnableUITask(this, task));
	}

	@Override
	public void runOnUIThread(Runnable task) {		
		if ( EventQueue.isDispatchThread() ) {
			task.run();
		}
		else {
			EventQueue.invokeLater(task);
		}
	}

	@Override
	public void runOnWorkerThread(IThreadTask task) {
		runOnWorkerThread(new RunnableWorkerTask(this, task));		
	}

	@Override
	public void runOnWorkerThread(Runnable task) {
		mExecutor.execute(task);
	}

	@Override
	public boolean isUIThread() {
		return EventQueue.isDispatchThread();
	}

}
