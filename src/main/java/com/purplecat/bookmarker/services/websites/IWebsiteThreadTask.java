package com.purplecat.bookmarker.services.websites;


public abstract class IWebsiteThreadTask implements Runnable {
	private final Iterable<IWebsiteLoadObserver> _observers;
	
	public IWebsiteThreadTask(Iterable<IWebsiteLoadObserver> observers) {
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
