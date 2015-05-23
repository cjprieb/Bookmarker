package com.purplecat.bookmarker.services.websites;

import java.util.List;

public abstract class IWebsiteThreadTask implements Runnable {
	private final List<IWebsiteLoadObserver> _observers;
	
	public IWebsiteThreadTask(List<IWebsiteLoadObserver> observers) {
		_observers = observers;
	}

	@Override
	public void run() {
		// GUI Thread Task
		for ( IWebsiteLoadObserver obs : _observers ) {
			run(obs);
		}
	}
	
	abstract public void run(IWebsiteLoadObserver obs);

}
