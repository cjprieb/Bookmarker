package com.purplecat.bookmarker.view.swing;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.purplecat.commons.swing.MyApplication;

public class DebugStart {

	public static void main(String[] args) {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			//if it gets here, there's a problem.
			throw new NullPointerException("No sqlite JDBC connector found! Aborting");
		}
		
		Injector injector = Guice.createInjector(new DebugSwingBookmarkerModule());		
		injector.getInstance(MyApplication.class).startApplication();
	}

}
