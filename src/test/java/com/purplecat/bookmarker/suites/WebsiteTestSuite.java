package com.purplecat.bookmarker.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.purplecat.bookmarker.OnlineUpdateThreadTests;
import com.purplecat.bookmarker.WebsiteLoadingTests;
import com.purplecat.bookmarker.WebsiteParsingTests;

@RunWith(Suite.class)
@SuiteClasses({ 
	WebsiteParsingTests.class,
	WebsiteLoadingTests.class, 
	OnlineUpdateThreadTests.class })
public class WebsiteTestSuite {

}
