package com.purplecat.bookmarker.test.modules;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.purplecat.bookmarker.controller.tasks.OnlineUpdateTask;
import com.purplecat.bookmarker.services.IFolderRepository;
import com.purplecat.bookmarker.services.ISummaryRepository;
import com.purplecat.bookmarker.services.UrlPatternService;
import com.purplecat.bookmarker.services.databases.IGenreRepository;
import com.purplecat.bookmarker.services.databases.IMediaRepository;
import com.purplecat.bookmarker.services.databases.IOnlineMediaRepository;
import com.purplecat.bookmarker.services.databases.IUrlPatternDatabase;
import com.purplecat.bookmarker.services.websites.IWebsiteList;
import com.purplecat.bookmarker.services.websites.IWebsiteLoadObserver;
import com.purplecat.bookmarker.services.websites.WebsiteThreadObserver;
import com.purplecat.bookmarker.sql.IConnectionManager;
import com.purplecat.bookmarker.test.DatabaseConnectorTestBase;
import com.purplecat.bookmarker.test.dummies.DummyConnectionManager;
import com.purplecat.bookmarker.test.dummies.DummyOnlineItemRepository;
import com.purplecat.bookmarker.test.dummies.DummySummaryRepository;
import com.purplecat.bookmarker.test.dummies.DummyThreadObserver;
import com.purplecat.bookmarker.test.dummies.DummyThreadPool;
import com.purplecat.bookmarker.test.dummies.DummyWebsiteList;
import com.purplecat.bookmarker.test.dummies.SampleDatabaseService.SampleFolderDatabase;
import com.purplecat.bookmarker.test.dummies.SampleDatabaseService.SampleGenreDatabase;
import com.purplecat.bookmarker.test.dummies.SampleDatabaseService.SamplePatternDatabase;
import com.purplecat.bookmarker.test.dummies.SampleMangaDatabase;
import com.purplecat.commons.logs.ConsoleLog;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.threads.IThreadPool;

public class ThreadTestingModule extends AbstractModule {

	@Override
	protected void configure() {
		//Utility Items
		bind(ILoggingService.class).to(ConsoleLog.class);
		bind(IThreadPool.class).to(DummyThreadPool.class);
		
		//Website Items
		bind(IWebsiteLoadObserver.class).to(DummyThreadObserver.class);
		bind(OnlineUpdateTask.class);
		
		//Database/Repository items
		bind(IConnectionManager.class).to(DummyConnectionManager.class);
		bind(IUrlPatternDatabase.class).to(SamplePatternDatabase.class);
		bind(IOnlineMediaRepository.class).to(DummyOnlineItemRepository.class);
		bind(IGenreRepository.class).to(SampleGenreDatabase.class);
		bind(IFolderRepository.class).to(SampleFolderDatabase.class);
		bind(ISummaryRepository.class).to(DummySummaryRepository.class);
		bind(UrlPatternService.class);
		bind(WebsiteThreadObserver.class);
		bind(IWebsiteList.class).to(DummyWebsiteList.class);
		bind(IMediaRepository.class).to(SampleMangaDatabase.class);
		bind(String.class).annotatedWith(Names.named("JDBC URL")).toInstance("jdbc:sqlite:" + DatabaseConnectorTestBase.TEST_DATABASE_PATH);	
	}

}
