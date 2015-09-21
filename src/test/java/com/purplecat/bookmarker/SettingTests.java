package com.purplecat.bookmarker;

import com.purplecat.bookmarker.dummies.DummySettings;
import com.purplecat.bookmarker.services.ISettingsService;
import com.purplecat.bookmarker.services.websites.DefaultWebsiteList;
import com.purplecat.bookmarker.services.websites.IWebsiteList;
import com.purplecat.bookmarker.services.websites.IWebsiteParser;
import com.purplecat.commons.logs.ConsoleLog;
import com.purplecat.commons.logs.ILoggingService;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Crystal on 9/10/15.
 */
public class SettingTests {

	ILoggingService _logging;
	ISettingsService _settings;
	IWebsiteList _websites;

	List<String> _nameList;

	@Before
	public void Setup() {
		_logging = new ConsoleLog();
		_settings = new DummySettings();
		_websites = new DefaultWebsiteList(_logging, null, _settings);

		_nameList = new ArrayList<>();
		_nameList.add("Batoto");
		_nameList.add("BakaUpdates");
		_settings.setWebsiteOrder(_nameList);
	}

	@Test
	public void WebsiteSorting() {
		List<IWebsiteParser> sortedList = _websites.getSortedList();
		assertNotNull(sortedList);
		assertEquals(_nameList.size(), sortedList.size());
		for (int i = 0; i < _nameList.size(); i++) {
			assertEquals(sortedList.get(i).getName(), _nameList.get(i));
		}
	}

	@Test
	public void WebsiteOrdering() {
		_nameList.add(0, "EgScans");
		_settings.setWebsiteOrder(_nameList);

		List<String> input = Arrays.asList("Batoto", "EgScans", "EgScans", "BakaUpdates", "Batoto", "Batoto", "Batoto", "BakaUpdates", "EgScans");
		List<String> expected = Arrays.asList("EgScans", "EgScans", "EgScans", "Batoto", "Batoto", "Batoto", "Batoto", "BakaUpdates", "BakaUpdates");
		Collections.sort(input, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return _websites.compare(o1, o2);
			}
		});

		for (int i = 0; i < expected.size(); i++) {
			assertEquals("values don't match at " + i, expected.get(i), input.get(i));
		}
	}
}
