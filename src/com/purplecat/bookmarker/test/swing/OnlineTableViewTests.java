package com.purplecat.bookmarker.test.swing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.WebsiteInfo;
import com.purplecat.bookmarker.services.databases.IOnlineMediaRepository;
import com.purplecat.bookmarker.test.dummies.DummyDragDrop;
import com.purplecat.bookmarker.test.modules.TestBookmarkerModule;
import com.purplecat.bookmarker.view.swing.components.OnlineUpdateItemTableControl;
import com.purplecat.bookmarker.view.swing.models.OnlineUpdateItemTableModel;
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
		_repository = injector.getInstance(IOnlineMediaRepository.class);
		_resources = injector.getInstance(IResourceService.class);
		_controller = injector.getInstance(Controller.class);
		_imageResources = injector.getInstance(IImageRepository.class);
		_toolbox = injector.getInstance(Toolbox.class);
	}
	
	@Test 
	public void onlineTableModel_SiteStarted() {
		OnlineUpdateItemTableModel model = new OnlineUpdateItemTableModel(_columns, _resources);
		
		model.getObserver().notifySiteStarted(_site);		
		assertEquals(1, model.getRowCount());
		assertEquals(_site._name, model.getItemAt(0)._displayTitle);
		assertEquals(_site._website, model.getItemAt(0)._titleUrl);
		assertNull(model.getItemAt(0)._updatedDate);
		
		//Don't duplicate
		model.getObserver().notifySiteStarted(_site);		
		assertEquals(1, model.getRowCount());
	}
	
	@Test 
	public void onlineTableModel_SiteParsed() {
		OnlineUpdateItemTableModel model = new OnlineUpdateItemTableModel(_columns, _resources);

		model.getObserver().notifySiteStarted(_site);
		model.getObserver().notifySiteParsed(_site, 10);
		assertEquals(1, model.getRowCount());
		assertEquals(_site._name, model.getItemAt(0)._displayTitle);
		assertEquals(_site._website, model.getItemAt(0)._titleUrl);
		assertNotNull(model.getItemAt(0)._updatedDate);
	}
	
	@Test 
	public void onlineTableModel_ItemParsed() {
		OnlineUpdateItemTableModel model = new OnlineUpdateItemTableModel(_columns, _resources);
		OnlineMediaItem item = getRandomItem();

		model.getObserver().notifySiteStarted(_site);
		model.getObserver().notifyItemParsed(item, 1, 1);
		
		assertEquals(2, model.getRowCount());
		assertEquals(item, model.getItemAt(1));
	}
	
	@Test 
	public void onlineTableModel_CorrectOrder() {
		BookmarkerRendererFactory factory = new BookmarkerRendererFactory(_imageResources, _resources);
		OnlineUpdateItemTableControl tableControl = new OnlineUpdateItemTableControl(factory, _controller, _resources, _toolbox, new DummyDragDrop());
		OnlineUpdateItemTableModel model = tableControl.getModel();
		_controller.observeOnlineThreadLoading(model.getObserver());
		_controller.loadUpdateMedia();
//		_controller.loadUpdateMedia();//trigger twice as using sample batoto which loads a different file the second time
		
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
		item._updatedPlace._chapter = GetRandom.getInteger(0, 200);
		item._updatedDate = new DateTime();
		return item;
	}
}
