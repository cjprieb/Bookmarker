package com.purplecat.bookmarker.test.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.purplecat.bookmarker.test.OnlineUpdateThreadTests;
import com.purplecat.bookmarker.test.WebsiteLoadingTests;
import com.purplecat.bookmarker.test.WebsiteParsingTests;

@RunWith(Suite.class)
@SuiteClasses({ 
	WebsiteParsingTests.class,
	WebsiteLoadingTests.class, 
	OnlineUpdateThreadTests.class })
public class WebsiteTestSuite {

}
