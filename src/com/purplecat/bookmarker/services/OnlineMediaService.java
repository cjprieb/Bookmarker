package com.purplecat.bookmarker.services;

import com.google.inject.Inject;
import com.purplecat.bookmarker.services.databases.IOnlineMediaRepository;

public class OnlineMediaService {
	
	public final IOnlineMediaRepository _database;
	
	@Inject
	public OnlineMediaService(IOnlineMediaRepository database) {
		_database = database;
	}

}
