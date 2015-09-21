package com.purplecat.bookmarker.swing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.purplecat.bookmarker.services.websites.IWebsiteList;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.controller.FolderCache;
import com.purplecat.bookmarker.models.EStoryState;
import com.purplecat.bookmarker.models.Folder;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.WebsiteInfo;
import com.purplecat.bookmarker.services.databases.IOnlineMediaRepository;
import com.purplecat.bookmarker.dummies.DummyDragDrop;
import com.purplecat.bookmarker.modules.TestBookmarkerModule;
import com.purplecat.bookmarker.view.swing.components.OnlineUpdateItemTableControl;
import com.purplecat.bookmarker.view.swing.models.OnlineUpdateItemTableModel;
import com.purplecat.bookmarker.view.swing.models.OnlineUpdateItemTableModel.OnlineMediaListObserver;
import com.purplecat.bookmarker.view.swing.renderers.BookmarkerRendererFactory;
import com.purplecat.bookmarker.view.swing.renderers.DataFields;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.TTableColumn;
import com.purplecat.commons.swing.IImageRepository;
import com.purplecat.commons.swing.Toolbox;
import com.purplecat.commons.tests.GetRandom;

public class OnlineTableViewTests {
	IOnlineMediaRepository _repository;
	IResourceService _resources;
	IImageRepository _imageResources;
	TTableColumn[] _columns;
	WebsiteInfo _site;
	Controller _controller;
	Toolbox _toolbox;
	OnlineUpdateItemTableModel _model;
	OnlineMediaListObserver _observer;
	FolderCache _folderCache;
	Folder _ignoreFolder;
	IWebsiteList _websiteList;
	
	@Before
	public void setup() {
		_columns = new TTableColumn[] {
				DataFields.MEDIA_STATE_COL,
				DataFields.TITLE_COL,
				DataFields.PLACE_COL,
				DataFields.TIME_COL
		};
		
		_site = new WebsiteInfo("Batoto", "www.batoto.net");
		
		Injector injector = Guice.createInjector(new TestBookmarkerModule());
		_folderCache = injector.getInstance(FolderCache.class);
		_repository = injector.getInstance(IOnlineMediaRepository.class);
		_resources = injector.getInstance(IResourceService.class);
		_controller = injector.getInstance(Controller.class);
		_imageResources = injector.getInstance(IImageRepository.class);
		_toolbox = injector.getInstance(Toolbox.class);
		_model = new OnlineUpdateItemTableModel(_controller, _resources, _folderCache);
		_model.setColumns(_columns);
		_observer = _model.new OnlineMediaListObserver();
		_websiteList = injector.getInstance(IWebsiteList.class);
		
		_ignoreFolder = new Folder();
		_ignoreFolder._id = 1;
		_ignoreFolder._storyState = EStoryState.MIDDLE_CHAPTER_BORED;
		_folderCache.notifyItemUpdated(_ignoreFolder);
	}
	
	@Test 
	public void onlineTableModel_SiteStarted() {		
		_observer.notifySiteStarted(_site._name, _site._website);
		assertEquals(1, _model.getRowCount());
		assertEquals(_site._name, _model.getItemAt(0)._displayTitle);
		assertEquals(_site._website, _model.getItemAt(0)._titleUrl);
		assertNull(_model.getItemAt(0)._updatedDate);
		
		//Don't duplicate
		_observer.notifySiteStarted(_site._name, _site._website);
		assertEquals(1, _model.getRowCount());
	}
	
	@Test 
	public void onlineTableModel_SiteParsed() {
		_observer.notifySiteStarted(_site._name, _site._website);
		_observer.notifySiteParsed(_site._name, 10);
		assertEquals(1, _model.getRowCount());
		assertEquals(_site._name, _model.getItemAt(0)._displayTitle);
		assertEquals(_site._website, _model.getItemAt(0)._titleUrl);
		assertNotNull(_model.getItemAt(0)._updatedDate);
	}
	
	@Test 
	public void onlineTableModel_SiteLoaded() {
		_observer.notifySiteStarted(_site._name, _site._website);
		_observer.notifySiteParsed(_site._name, 10);
		
		OnlineMediaItem item = getRandomItem();
		System.out.println("item: " + item);
		_observer.notifyItemParsed(item, 1, 5);
		
		OnlineMediaItem item2 = getRandomItem();
		item2._updatedDate = item2._updatedDate.minusHours(12);
		System.out.println("item2: " + item2);
		_observer.notifyItemParsed(item2, 2, 5);
		
		OnlineMediaItem item3 = getRandomItem();
		item3._updatedDate = item3._updatedDate.minusHours(12);
		item3._folderId = _ignoreFolder._id;
		System.out.println("item3: " + item3);
		_observer.notifyItemParsed(item3, 3, 5);
		
		OnlineMediaItem item4 = getRandomItem();
		item4._updatedDate = item4._updatedDate.minusHours(12);
		item4._isSaved = true;
		item4._lastReadPlace = item4._updatedPlace.copy();
		item4._lastReadPlace._chapter--;
		System.out.println("item4: " + item4);
		_observer.notifyItemParsed(item4, 4, 5);
		
		OnlineMediaItem item5 = getRandomItem();
		item5._updatedDate = item5._updatedDate.minusHours(12);
		item5._isSaved = true;
		item5._lastReadPlace = item5._updatedPlace.copy();
		System.out.println("item5: " + item5);
		_observer.notifyItemParsed(item5, 5, 5);
		
		assertEquals(6, _model.getRowCount());
		
		//_observer.notifySiteFinished(_site);
		int hoursAgo = 8;
		_model.removeItemsOlderThan(hoursAgo, _site._name);
		
		for ( int i = 0; i < _model.getRowCount(); i++ ) {
			System.out.println("  " + _model.getItemAt(i));
		}
		assertEquals(3, _model.getRowCount());
		assertEquals(item._displayTitle, _model.getItemAt(1)._displayTitle);
		assertEquals(item4._displayTitle, _model.getItemAt(2)._displayTitle);
	}
	
	@Test 
	public void onlineTableModel_ItemParsed() {
		OnlineMediaItem item = getRandomItem();

		_observer.notifySiteStarted(_site._name, _site._website);
		_observer.notifyItemParsed(item, 1, 1);
		
		assertEquals(2, _model.getRowCount());
		assertEquals(item, _model.getItemAt(1));
	}
	
	@Test 
	public void onlineTableModel_CorrectOrder() {
		BookmarkerRendererFactory factory = new BookmarkerRendererFactory(_imageResources, _resources, _folderCache);
		OnlineUpdateItemTableControl tableControl = new OnlineUpdateItemTableControl(factory, _controller, _resources, _toolbox, new DummyDragDrop(), _model, _websiteList);
		OnlineUpdateItemTableModel model = tableControl.getModel();
		OnlineMediaListObserver observer = _model.new OnlineMediaListObserver();
		_controller.observeOnlineThreadLoading(observer);
		_controller.loadUpdateMedia(8, true, false, _site._name);
		
		assertTrue("list has no elements", model.getRowCount() > 1);
		assertTrue("table has no elements", tableControl.getTable().getRowCount() > 1);

		int row = tableControl.getTable().convertRowIndexToModel(0);
		assertEquals(_site._name, model.getItemAt(row)._displayTitle);
		assertEquals("http://bato.to/", model.getItemAt(row)._titleUrl);

		boolean bUpdated = true;
		int updateCount = 0;
		for ( int i = 1; i < tableControl.getTable().getRowCount(); i++ ) {
			row = tableControl.getTable().convertRowIndexToModel(i);
			OnlineMediaItem item = model.getItemAt(row);
			System.out.println("Looking at: " + item);
			if ( !item.isUpdated() ) {
				bUpdated = false;
			}
			else if ( !bUpdated && item.isUpdated() ) {
				Assert.fail("Updated item in wrong order");
			}
			if ( item._summary.length() == 0 ) {
				Assert.fail("No summary loaded");
			}
			else {
				System.out.println("    has summary: " + item._summary);
			}
			if ( item.isUpdated() ) {
				updateCount++;
			}
		}
		Assert.assertTrue("no updates found", updateCount > 0);
	}
	
	private OnlineMediaItem getRandomItem() {
		OnlineMediaItem item = new OnlineMediaItem();
		item._id = GetRandom.getInteger(100, 1000);
		item._displayTitle = GetRandom.getString(10);
		item._chapterUrl = GetRandom.getString(20);
		item._updatedPlace._volume = GetRandom.getInteger(0, 10);
		item._updatedPlace._chapter = GetRandom.getInteger(10, 200);
		item._updatedDate = new DateTime();
		item._websiteName = _site._name;
		return item;
	}
}
