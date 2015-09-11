package com.purplecat.bookmarker.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.purplecat.bookmarker.FileSummaryRepositoryTests;
import com.purplecat.bookmarker.MediaServiceTests;
import com.purplecat.bookmarker.UrlPatternServiceTests;

@RunWith(Suite.class)
@SuiteClasses({ 
	MediaServiceTests.class, 
	UrlPatternServiceTests.class,
	FileSummaryRepositoryTests.class })
public class ServiceTestSuite {

}
