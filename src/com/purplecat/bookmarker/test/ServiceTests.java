package com.purplecat.bookmarker.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ DatabaseMangaServiceTests.class,
		DatabaseUrlPatternServiceTests.class })
public class ServiceTests {

}
