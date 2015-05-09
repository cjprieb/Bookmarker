package com.purplecat.bookmarker.test.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.purplecat.bookmarker.test.DatabaseMangaServiceTests;
import com.purplecat.bookmarker.test.OnlineUpdateServiceTests;
import com.purplecat.bookmarker.test.UrlPatternServiceTests;

@RunWith(Suite.class)
@SuiteClasses({ DatabaseMangaServiceTests.class,
		OnlineUpdateServiceTests.class, UrlPatternServiceTests.class })
public class ServiceTestSuite {

}
