package com.purplecat.bookmarker.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.purplecat.bookmarker.services.ISettingsService;
import com.purplecat.bookmarker.services.ISummaryRepository;
import com.purplecat.bookmarker.test.modules.TestDatabaseModule;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.tests.GetRandom;

public class FileSummaryRepositoryTests {
	
	static ISummaryRepository _summaryRepository;
	static File _summaryDirectory;
	static ILoggingService _logging;

	@BeforeClass
	public static void setUpBeforeTest() throws Exception {
		Injector injector = Guice.createInjector(new TestDatabaseModule());	
		_summaryRepository = injector.getInstance(ISummaryRepository.class);
		_logging = injector.getInstance(ILoggingService.class);
		ISettingsService settings = injector.getInstance(ISettingsService.class);
		
		_summaryDirectory = new File(settings.getSummaryDirectory());
		System.out.println("Summary directory: " + _summaryDirectory.getAbsolutePath());
		
	}

	@Test
	public void saveAndLoadSummary() {
		String summary = GetRandom.getPhraseString(100);
		long mediaId = 100;
		String websiteName = "batoto";
		
		_summaryRepository.saveSummary(mediaId, websiteName, summary);
		
		String result = _summaryRepository.loadSummary(mediaId, websiteName);
		assertEquals(summary, result);
		
		File file = new File(_summaryDirectory, String.format("%s-%s.html", mediaId, websiteName));
		assertTrue(file.exists());
	}

	@Test
	public void saveAndLoadSummaryWithoutWebsite() {
		String summary = GetRandom.getPhraseString(100);
		long mediaId = 200;
		String websiteName = "batoto";
		
		_summaryRepository.saveSummary(mediaId, websiteName, summary);
		
		String result = _summaryRepository.loadSummary(mediaId, null);
		assertEquals(summary, result);
		
		File file = new File(_summaryDirectory, String.format("%s-%s.html", mediaId, websiteName));
		assertTrue(file.exists());
	}

}
