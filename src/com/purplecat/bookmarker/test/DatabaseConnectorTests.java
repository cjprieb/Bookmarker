package com.purplecat.bookmarker.test;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;

public class DatabaseConnectorTests {
	public static final String TEST_DATABASE_PATH = "data/bookmarker.db";
	public static final String CONNECTION_PREFIX = "jdbc:sqlite:";	
	public static final String DATABASE_PATH = "../BookmarkView/databases/bookmarker.db";
	
	public static String TEST_DATABASE_CONNECTION;
	
	@BeforeClass
	public static void loadDatabase() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			//if it gets here, there's a problem.
			e.printStackTrace();
			throw new NullPointerException("No sqlite JDBC connector found! Aborting");
		}
		
		File dest = new File(TEST_DATABASE_PATH);
		TEST_DATABASE_CONNECTION = CONNECTION_PREFIX + dest.getAbsolutePath();
		
		/*
		File src = new File(DATABASE_PATH);
		System.out.println(String.format("Copying database from %s to %s", src.getAbsolutePath(), dest.getAbsolutePath()));
		
		try {
			Utils.copyFiles(src, dest);
		} catch (IOException e) {
			System.err.println("Database copy failed");
			e.printStackTrace();
			throw new NullPointerException("Database for tests could not be copied. Aborting.");
		}
		System.out.println("Databased copied");*/
	}
}
