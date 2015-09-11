package com.purplecat.bookmarker.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.purplecat.bookmarker.ExtensionTests;
import com.purplecat.bookmarker.SortingTest;

@RunWith(Suite.class)
@SuiteClasses({ ExtensionTests.class, SortingTest.class })
public class HelperTestSuite {

}
