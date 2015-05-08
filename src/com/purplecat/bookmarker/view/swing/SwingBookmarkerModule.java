package com.purplecat.bookmarker.view.swing;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.services.UrlPatternService;
import com.purplecat.bookmarker.services.databases.IMediaRepository;
import com.purplecat.bookmarker.services.databases.IUrlPatternDatabase;
import com.purplecat.bookmarker.services.databases.MediaDatabaseRepository;
import com.purplecat.bookmarker.services.databases.UrlPatternDatabase;
import com.purplecat.commons.logs.FileLog;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.swing.MyApplication;
import com.purplecat.commons.swing.SwingThreadPool;
import com.purplecat.commons.swing.Toolbox;
import com.purplecat.commons.threads.IThreadPool;

public class SwingBookmarkerModule extends AbstractModule {

	@Override
	protected void configure() {
		//Utility Items
		bind(ILoggingService.class).to(FileLog.class);
		
		//Database/Repository items
		bind(IUrlPatternDatabase.class).to(UrlPatternDatabase.class);
		bind(UrlPatternService.class);
		bind(IMediaRepository.class).to(MediaDatabaseRepository.class);
		bind(Controller.class);
		bind(String.class).annotatedWith(Names.named("JDBC URL")).toInstance("jdbc:sqlite:../BookmarkView/databases/bookmarker.db");
		
		//Swing Items
		bind(Toolbox.class);
		bind(IThreadPool.class).to(SwingThreadPool.class);
		
		//Main Class
		bind(MyApplication.class).to(BookmarkerStart.class);
		
	}
}
