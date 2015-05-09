package com.purplecat.bookmarker.test.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.purplecat.bookmarker.test.MangaDatabaseRepositoryTests;
import com.purplecat.bookmarker.test.NamedStatementTests;
import com.purplecat.bookmarker.test.OnlineDatabaseRepositoryTests;
import com.purplecat.bookmarker.test.UrlDatabaseRepositoryTests;

@RunWith(Suite.class)
@SuiteClasses({ NamedStatementTests.class, MangaDatabaseRepositoryTests.class,
		OnlineDatabaseRepositoryTests.class, UrlDatabaseRepositoryTests.class })
public class DatabaseTestSuite {

}
