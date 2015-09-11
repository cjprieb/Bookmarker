package com.purplecat.bookmarker.services.websites;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.inject.Inject;
import com.purplecat.bookmarker.models.Genre;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.bookmarker.services.databases.IGenreRepository;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.tests.GetRandom;

public class SampleBatotoWebsite extends BatotoWebsite {
	
	private int _fileIndex = 0;
	private int _loadItemCount = 0;
	
	@Inject
	public SampleBatotoWebsite(ILoggingService logging, IGenreRepository genres) {
		super(logging, genres);
	}
	
	@Override
	protected Document getDocument(int page) throws IOException {
		if ( page == 1 ) {
			String fileName = _fileIndex % 2 == 0 ? "sample_batoto.html" : "sample_batoto_3.html";
			_fileIndex++;
	//		String fileName = "sample_batoto.html";
			File in = new File(fileName);
			if ( !in.exists() ) {
				_logging.debug(0, TAG, "sample file not found: " + in.getAbsolutePath());
			}
	//		_fileIndex++;
			return Jsoup.parse(in, "UTF-8");
		}
		return null;
	}

	@Override
	public OnlineMediaItem loadItem(OnlineMediaItem item) throws ServiceException {
		item._summary = GetRandom.getPhraseString(300);
		
		Genre genre = new Genre();
		genre._name = GetRandom.getString(6);
		item._genres.add(genre);
		
		_loadItemCount++;
		return item;
	}

	public int getLoadItemCount() {
		return _loadItemCount;
	}
}
