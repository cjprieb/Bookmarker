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

public class SampleBatotoWebsite extends BatotoWebsite {
	
	private int _fileIndex = 0;
	
	@Inject
	public SampleBatotoWebsite(ILoggingService logging, IGenreRepository genres) {
		super(logging, genres);
	}
	
	@Override
	protected Document getDocument() throws IOException {
		String fileName = _fileIndex % 2 == 0 ? "sample_batoto_2.html" : "sample_batoto.html";
		File in = new File(fileName);
		if ( !in.exists() ) {
			_logging.debug(0, TAG, "sample file not found: " + in.getAbsolutePath());
		}
		_fileIndex++;
		return Jsoup.parse(in, "UTF-8");
	}

	@Override
	public OnlineMediaItem loadItem(OnlineMediaItem item) throws ServiceException {
		return item;
	}
}
