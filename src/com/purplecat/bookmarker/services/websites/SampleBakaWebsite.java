package com.purplecat.bookmarker.services.websites;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.inject.Inject;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.bookmarker.services.databases.IGenreRepository;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.tests.GetRandom;

public class SampleBakaWebsite extends BakaWebsite {
	
	@Inject
	public SampleBakaWebsite(ILoggingService logging, IGenreRepository genres) {
		super(logging, genres);
	}
	
	@Override
	protected Document getDocument() throws IOException {
		String fileName = "sample_batoto.html";
		File in = new File(fileName);
		if ( !in.exists() ) {
			_logging.debug(0, TAG, "sample file not found: " + in.getAbsolutePath());
		}
		return Jsoup.parse(in, "UTF-8");
	}

	@Override
	public OnlineMediaItem loadItem(OnlineMediaItem item) throws ServiceException {
		item._summary = GetRandom.getPhraseString(300);
		return item;
	}
}
