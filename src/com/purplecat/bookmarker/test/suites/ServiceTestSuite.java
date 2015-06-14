package com.purplecat.bookmarker.test.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.purplecat.bookmarker.test.MediaServiceTests;
import com.purplecat.bookmarker.test.UrlPatternServiceTests;

@RunWith(Suite.class)
@SuiteClasses({ 
	MediaServiceTests.class, 
	UrlPatternServiceTests.class })
public class ServiceTestSuite {

}
