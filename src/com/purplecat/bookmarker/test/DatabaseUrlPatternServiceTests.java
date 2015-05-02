package com.purplecat.bookmarker.test;

import static org.junit.Assert.fail;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.purplecat.bookmarker.models.UrlPatternResult;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.bookmarker.services.UrlPatternService;
import com.purplecat.bookmarker.test.modules.TestBookmarkerModule;
import com.purplecat.commons.tests.GetRandom;
import com.purplecat.commons.tests.Utils;

public class DatabaseUrlPatternServiceTests {

	private UrlPatternService _service;

	private List<UrlPatternResult> _validUrls;

	private String[] _invalidUrls = { "" };

	@Before
	public void setUpBeforeTest() throws Exception {
		Injector injector = Guice.createInjector(new TestBookmarkerModule());		
		_service = injector.getInstance(UrlPatternService.class);

		_validUrls = new LinkedList<UrlPatternResult>();
		List<String> lines = Utils.getFile(getClass(), "/resources/SampleUrlPatternResults.txt");
		for (String line : lines) {
			String[] tokens = line.split("\t");
			UrlPatternResult result = new UrlPatternResult();
			result._chapterUrl = tokens[0];
			result._title = tokens[1];
			result._volume = Utils.parseInt(tokens[2], 0);
			result._chapter = Utils.parseInt(tokens[3], 0);
			result._subChapter = Utils.parseInt(tokens[4], 0);
			result._page = Utils.parseInt(tokens[5], 0);
			_validUrls.add(result);
		}
		
		Assert.assertTrue("no items in list", _service.list().size() > 0);
	}

	@Test
	public void testUrlPatternMatch() {
		try {
			//UrlPatternResult expected = Random.getItem(_validUrls);
			for ( UrlPatternResult expected : _validUrls ) {
				System.out.println("Parsing: " + expected._chapterUrl);
				UrlPatternResult result = _service.match(expected._chapterUrl);
				Assert.assertNotNull("Result is null", result);
				Assert.assertTrue("Result wasn't successful", result._success);
				Assert.assertEquals("title mismatch", expected._title, result._title);
				Assert.assertEquals("volume mismatch", expected._volume, result._volume);
				Assert.assertEquals("chapter mismatch", expected._chapter, result._chapter);
				Assert.assertEquals("sub chapter mismatch", expected._subChapter, result._subChapter);
				Assert.assertEquals("page mismatch", expected._page, result._page);
				System.out.println("\tSuccess.");
			}
		} catch (ServiceException e) {
			fail("Exception thrown: " + e.getMessage());
		}
	}

	@Test
	public void testInvalidUrlPatternMatch() {
		try {
			String url = GetRandom.getItem(_invalidUrls);
			;
			UrlPatternResult result = _service.match(url);
			Assert.assertNotNull("Result is null", result);
			Assert.assertTrue(
					"Result was successful when it should have failed",
					!result._success);
		} catch (ServiceException e) {
			fail("Exception thrown: " + e.getMessage());
		}
	}
}
