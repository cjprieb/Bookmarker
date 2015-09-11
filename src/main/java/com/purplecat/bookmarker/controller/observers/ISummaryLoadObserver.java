package com.purplecat.bookmarker.controller.observers;

import com.purplecat.bookmarker.models.OnlineMediaItem;

public interface ISummaryLoadObserver {
	public void notifySummaryLoadStarted(long mediaId);
	public void notifySummaryLoadFinished(OnlineMediaItem item);
}
