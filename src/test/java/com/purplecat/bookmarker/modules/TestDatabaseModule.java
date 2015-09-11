package com.purplecat.bookmarker.modules;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.services.DefaultSettingsService;
import com.purplecat.bookmarker.services.FileSummaryRepository;
import com.purplecat.bookmarker.services.IFolderRepository;
import com.purplecat.bookmarker.services.ISettingsService;
import com.purplecat.bookmarker.services.ISummaryRepository;
import com.purplecat.bookmarker.services.UrlPatternService;
import com.purplecat.bookmarker.services.databases.GenreDatabaseRepository;
import com.purplecat.bookmarker.services.databases.IGenreRepository;
import com.purplecat.bookmarker.services.databases.IMediaRepository;
import com.purplecat.bookmarker.services.databases.IOnlineMediaRepository;
import com.purplecat.bookmarker.services.databases.IUrlPatternDatabase;
import com.purplecat.bookmarker.services.databases.MediaDatabaseRepository;
import com.purplecat.bookmarker.services.databases.OnlineMediaDatabase;
import com.purplecat.bookmarker.services.databases.UrlPatternDatabase;
import com.purplecat.bookmarker.services.websites.DefaultWebsiteList;
import com.purplecat.bookmarker.services.websites.IWebsiteList;
import com.purplecat.bookmarker.services.websites.IWebsiteLoadObserver;
import com.purplecat.bookmarker.services.websites.WebsiteThreadObserver;
import com.purplecat.bookmarker.sql.ConnectionManager;
import com.purplecat.bookmarker.sql.IConnectionManager;
import com.purplecat.bookmarker.DatabaseConnectorTestBase;
import com.purplecat.bookmarker.dummies.SampleDatabaseService.SampleFolderDatabase;
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
		bind(IConnectionManager.class).to(ConnectionManager.class);
		bind(IUrlPatternDatabase.class).to(UrlPatternDatabase.class);
		bind(IOnlineMediaRepository.class).to(OnlineMediaDatabase.class);
		bind(IFolderRepository.class).to(SampleFolderDatabase.class);
		bind(ISummaryRepository.class).to(FileSummaryRepository.class);
		bind(ISettingsService.class).to(DefaultSettingsService.class);
		bind(UrlPatternService.class);
		bind(IMediaRepository.class).to(MediaDatabaseRepository.class);
		bind(IGenreRepository.class).to(GenreDatabaseRepository.class);
		bind(Controller.class);
		bind(IWebsiteLoadObserver.class).to(WebsiteThreadObserver.class);
		bind(IWebsiteList.class).to(DefaultWebsiteList.class);
		bind(String.class).annotatedWith(Names.named("JDBC URL")).toInstance("jdbc:sqlite:" + DatabaseConnectorTestBase.TEST_DATABASE_PATH);
		
		//Swing Items
		bind(Toolbox.class);
		bind(IThreadPool.class).to(SwingThreadPool.class);		
	}
}
