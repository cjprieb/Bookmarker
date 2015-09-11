package com.purplecat.bookmarker.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.purplecat.bookmarker.GenreDatabaseRepositoryTests;
import com.purplecat.bookmarker.MangaDatabaseRepositoryTests;
import com.purplecat.bookmarker.NamedStatementTests;
import com.purplecat.bookmarker.OnlineDatabaseRepositoryTests;
import com.purplecat.bookmarker.SqlDatabaseTests;
import com.purplecat.bookmarker.UrlDatabaseRepositoryTests;

@RunWith(Suite.class)
@SuiteClasses({ 
	NamedStatementTests.class, 
	SqlDatabaseTests.class,
	MangaDatabaseRepositoryTests.class,
	OnlineDatabaseRepositoryTests.class, 
	UrlDatabaseRepositoryTests.class,
	GenreDatabaseRepositoryTests.class,})
public class DatabaseTestSuite {

}
