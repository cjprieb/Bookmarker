package com.purplecat.bookmarker.services.websites;

import java.util.List;

import com.google.inject.Inject;
import com.purplecat.commons.threads.IThreadPool;


/**
 * Worker thread class; calls UI Thread
 * @author Crystal
 *
 */
public class WebsiteThreadObserver {
	
	private final IThreadPool _threadPool;
	//private List<IWebsiteLoadObserver> _observers;
	
	@Inject
	public WebsiteThreadObserver(IThreadPool threadPool) {
		_threadPool = threadPool;
	}

}
