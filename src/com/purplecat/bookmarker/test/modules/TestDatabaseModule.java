package com.purplecat.bookmarker.test.modules;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.services.UrlPatternService;
import com.purplecat.bookmarker.services.databases.IMangaDatabaseConnector;
import com.purplecat.bookmarker.services.databases.IOnlineMediaRepository;
import com.purplecat.bookmarker.services.databases.IUrlPatternDatabase;
import com.purplecat.bookmarker.services.databases.MangaDatabaseConnector;
import com.purplecat.bookmarker.services.databases.OnlineMediaDatabase;
import com.purplecat.bookmarker.services.databases.UrlPatternDatabase;
import com.purplecat.bookmarker.test.DatabaseConnectorTests;
import com.purplecat.commons.logs.ConsoleLog;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.swing.SwingThreadPool;
import com.purplecat.commons.swing.Toolbox;
import com.purplecat.commons.threads.IThreadPool;

public class TestDatabaseModule extends AbstractModule {

	@Override
	protected void configure() {
		//Utility Items
		bind(ILoggingService.class).to(ConsoleLog.class);
		
		//Database/Repository items
		bind(IUrlPatternDatabase.class).to(UrlPatternDatabase.class);
		bind(IOnlineMediaRepository.class).to(OnlineMediaDatabase.class);
		bind(UrlPatternService.class);
		bind(IMangaDatabaseConnector.class).to(MangaDatabaseConnector.class);
		bind(Controller.class);
		bind(String.class).annotatedWith(Names.named("JDBC URL")).toInstance("jdbc:sqlite:" + DatabaseConnectorTests.TEST_DATABASE_PATH);
		
		//Swing Items
		bind(Toolbox.class);
		bind(IThreadPool.class).to(SwingThreadPool.class);		
	}
}
