package com.purplecat.bookmarker.controller.tasks;

import com.purplecat.bookmarker.controller.observers.SampleTaskObserver;
import com.purplecat.commons.threads.IThreadTask;

public class SampleTask implements IThreadTask {
	Object _data;
	Object _result;
	Iterable<SampleTaskObserver> _observers;
	
	public SampleTask(Object data, Iterable<SampleTaskObserver> obs) {
		_data = data;
		_observers = obs;
	}
	
	@Override
	public void uiTaskCompleted() {
		for ( SampleTaskObserver obs : _observers ) {
			obs.notifyTaskComplete(_result);
		}
	}

	@Override
	public void workerTaskStart() {
		_result = doSomething(_data);
	}
	
	public Object doSomething(Object data) {
		return data;
	}
}

