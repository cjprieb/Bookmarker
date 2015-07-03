package com.purplecat.bookmarker.test.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.purplecat.bookmarker.test.ExtensionTests;
import com.purplecat.bookmarker.test.SortingTest;

@RunWith(Suite.class)
@SuiteClasses({ ExtensionTests.class, SortingTest.class })
public class HelperTestSuite {

}
