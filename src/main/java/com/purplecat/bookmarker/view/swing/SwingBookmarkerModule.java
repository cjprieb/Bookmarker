package com.purplecat.bookmarker.view.swing;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.purplecat.bookmarker.services.*;
import com.purplecat.bookmarker.services.databases.FolderDatabaseRepository;
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
import com.purplecat.bookmarker.view.swing.actions.UrlDragDropAction.AddMangaUrlDropAction;
import com.purplecat.bookmarker.view.swing.renderers.BookmarkerRendererFactory;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.logs.FileLog;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.swing.AppUtils.IDragDropAction;
import com.purplecat.commons.swing.IImageRepository;
import com.purplecat.commons.swing.MyApplication;
import com.purplecat.commons.swing.SwingImageRepository;
import com.purplecat.commons.swing.SwingResourceService;
import com.purplecat.commons.swing.SwingThreadPool;
import com.purplecat.commons.swing.Toolbox;
import com.purplecat.commons.swing.renderer.ICellRendererFactory;
import com.purplecat.commons.threads.IThreadPool;

public class SwingBookmarkerModule extends AbstractModule {

	@Override
	protected void configure() {
		//Utility Items
		bind(ILoggingService.class).to(FileLog.class);
//		bind(ILoggingService.class).to(ConsoleLog.class);
		bind(String.class).annotatedWith(Names.named("Resource File")).toInstance("com.purplecat.bookmarker.Resources");
		bind(String.class).annotatedWith(Names.named("Project Path")).toInstance("/com/purplecat/bookmarker/");
		bind(ISettingsService.class).to(PropertiesSettingsService.class);
		
		//Database/Repository items
		bind(IConnectionManager.class).to(ConnectionManager.class);
		bind(IUrlPatternDatabase.class).to(UrlPatternDatabase.class);
		bind(IOnlineMediaRepository.class).to(OnlineMediaDatabase.class);
		bind(IMediaRepository.class).to(MediaDatabaseRepository.class);
		bind(IGenreRepository.class).to(GenreDatabaseRepository.class);
		bind(ISummaryRepository.class).to(FileSummaryRepository.class);
		bind(IFolderRepository.class).to(FolderDatabaseRepository.class);
		bind(UrlPatternService.class);
		bind(IWebsiteLoadObserver.class).to(WebsiteThreadObserver.class);
		bind(IWebsiteList.class).to(DefaultWebsiteList.class);
		bind(String.class).annotatedWith(Names.named("JDBC URL")).toInstance("jdbc:sqlite:databases/bookmarker.db");
		
		//Swing Interface Items
		bind(IThreadPool.class).to(SwingThreadPool.class);
		bind(ICellRendererFactory.class).to(BookmarkerRendererFactory.class);
		bind(IImageRepository.class).to(SwingImageRepository.class);
		bind(IResourceService.class).to(SwingResourceService.class);
		bind(IDragDropAction.class).annotatedWith(Names.named("Manga Url")).to(AddMangaUrlDropAction.class);
		
		//Swing GUI Items
		bind(Toolbox.class);		
		
		//Main Class
		bind(MyApplication.class).to(BookmarkerStart.class);
		
	}
}
