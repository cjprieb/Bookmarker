package com.purplecat.bookmarker.test.swing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.WebsiteInfo;
import com.purplecat.bookmarker.services.databases.IOnlineMediaRepository;
import com.purplecat.bookmarker.test.modules.TestBookmarkerModule;
import com.purplecat.bookmarker.view.swing.models.OnlineUpdateItemTableModel;
import com.purplecat.bookmarker.view.swing.renderers.DataFields;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.TTableColumn;
import com.purplecat.commons.tests.GetRandom;

public class OnlineTableViews {
	IOnlineMediaRepository _repository;
	IResourceService _resources;
	TTableColumn[] _columns;
	WebsiteInfo _site;
	
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
