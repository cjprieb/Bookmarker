package com.purplecat.bookmarker.test.modules;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.purplecat.bookmarker.controller.Controller;
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
import com.purplecat.bookmarker.test.OnlineUpdateThreadTests.DummyThreadPool;
import com.purplecat.bookmarker.test.dummies.DummyConnectionManager;
import com.purplecat.bookmarker.test.dummies.SampleDatabaseService.SampleGenreDatabase;
import com.purplecat.bookmarker.test.dummies.SampleDatabaseService.SamplePatternDatabase;
import com.purplecat.bookmarker.test.dummies.SampleMangaDatabase;
import com.purplecat.bookmarker.test.dummies.SampleOnlineMangaDatabase;
import com.purplecat.bookmarker.test.dummies.SampleWebsiteList;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.logs.ConsoleLog;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.swing.IImageRepository;
import com.purplecat.commons.swing.SwingImageRepository;
import com.purplecat.commons.swing.SwingResourceService;
import com.purplecat.commons.swing.Toolbox;
import com.purplecat.commons.threads.IThreadPool;

public class TestBookmarkerModule extends AbstractModule {

	@Override
	protected void configure() {
		//Utility Items
		bind(ILoggingService.class).to(ConsoleLog.class);
		bind(String.class).annotatedWith(Names.named("Resource File")).toInstance("com.purplecat.bookmarker.Resources");
		bind(String.class).annotatedWith(Names.named("Project Path")).toInstance("/com/purplecat/bookmarker/");

		//Controller items
		bind(IWebsiteLoadObserver.class).to(WebsiteThreadObserver.class);
		
		//Database/Repository items
		bind(IConnectionManager.class).to(DummyConnectionManager.class);
		bind(IUrlPatternDatabase.class).to(SamplePatternDatabase.class);
		bind(UrlPatternService.class);
		bind(IMediaRepository.class).to(SampleMangaDatabase.class);
		bind(IGenreRepository.class).to(SampleGenreDatabase.class);
		bind(IOnlineMediaRepository.class).to(SampleOnlineMangaDatabase.class);
		bind(Controller.class);
		bind(WebsiteThreadObserver.class);
		bind(IWebsiteList.class).to(SampleWebsiteList.class);
		bind(String.class).annotatedWith(Names.named("JDBC URL")).toInstance("jdbc:sqlite:" + DatabaseConnectorTestBase.TEST_DATABASE_PATH);
		
		//Swing Items
		bind(Toolbox.class);
		bind(IThreadPool.class).to(DummyThreadPool.class);
		bind(IResourceService.class).to(SwingResourceService.class);
		bind(IImageRepository.class).to(SwingImageRepository.class);
	}
}
