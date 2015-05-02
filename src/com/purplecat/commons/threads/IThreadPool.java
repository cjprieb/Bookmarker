package com.purplecat.commons.threads;

public interface IThreadPool {
	public void runOnUIThread(IThreadTask task);
	public void runOnUIThread(Runnable task);
	public void runOnWorkerThread(IThreadTask task);
	public void runOnWorkerThread(Runnable task);
	public boolean isUIThread();
}
