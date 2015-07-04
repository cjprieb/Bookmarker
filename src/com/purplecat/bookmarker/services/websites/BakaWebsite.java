package com.purplecat.bookmarker.services.websites;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.purplecat.bookmarker.extensions.PlaceExt;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.WebsiteInfo;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.bookmarker.services.databases.IGenreRepository;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.utils.StringUtils;

public class BakaWebsite implements IWebsiteParser {
	final String TAG = "BakaWebsite";
	
	protected final WebsiteInfo _info;
	protected final ILoggingService _logging;
	protected final IGenreRepository _genreDatabase;
	
	public BakaWebsite(ILoggingService logging, IGenreRepository genres) {
		_logging = logging;
		_genreDatabase = genres;
		_info = new WebsiteInfo("BakaUpdates", "https://www.mangaupdates.com/releases.html");
	}

	@Override
	public OnlineMediaItem loadItem(OnlineMediaItem item) throws ServiceException {
		if ( StringUtils.isNullOrEmpty(item._titleUrl) ) {
			return item;
		}
		
		try {
			Document doc = Jsoup.connect(item._titleUrl).get();
			Elements divs = doc.select(".sMember .sCat");
			for ( Element catDiv : divs ) {
				String title = catDiv.text().toLowerCase(Locale.ENGLISH);
				Element contentDiv = catDiv.nextElementSibling();
				if ( title.equals("description") ) {
					item._summary = contentDiv.html();
				}
				else if ( title.equals("genre") ) {
					for ( Element genreLink : contentDiv.select("a") ) {
						if ( !genreLink.text().equals("Search for series") ) {
							item._genres.add(_genreDatabase.find(genreLink.text()));
						}
					}					
				}
			}		
		} 
		catch (SocketTimeoutException e) {
			_logging.error(TAG, "Timeout loading item: " + item);			
		}
		catch (IOException e) {
			_logging.error(TAG, "Error loading item: " + item, e);
			throw new ServiceException("Error loading Batoto", ServiceException.WEBSITE_ERROR);
		}
		return item;
	}

	@Override
	public WebsiteInfo getInfo() {
		return _info;
	}
	
	protected Document getDocument() throws IOException {
		return Jsoup.connect(_info._website).get();
	}

	@Override
	public List<OnlineMediaItem> load() throws ServiceException {
		try {
			return loadDocument(getDocument());
		} catch (IOException e) {
			_logging.error(TAG, "Error getting document", e);
			throw new ServiceException("Error loading Baka Updates", ServiceException.WEBSITE_ERROR);
		}
	}
	
	public List<OnlineMediaItem>  loadDocument(Document doc) {
		List<OnlineMediaItem> items = new LinkedList<OnlineMediaItem>();
		DateTime now = DateTime.now();
		Element mainTable = doc.select("#main_content table.text").first();
		if ( mainTable != null ) {
			Elements rows = mainTable.select("tr");
			//Skip first row, as that is the header
			for ( int i = 1; i < rows.size(); i++ ) {
				Elements cells = rows.get(i).select("td");
				if ( cells.size() < 3 ) {
					continue; //not enough columns
				}

				Element ahref = cells.get(0).select("a").first();
				if ( ahref != null ) {
					String sChapterText = cells.get(1).text();
					OnlineMediaItem item = new OnlineMediaItem();
					item._websiteName = _info._name;
					item._displayTitle = cells.get(0).text();
					item._updatedPlace = PlaceExt.parseBakaPlace(sChapterText);
					item._updatedDate = now;
					item._titleUrl = ahref.attr("href");
					//only add item if it has a title URL
					items.add(item);

					ahref = cells.get(2).select("a").first();
					if ( ahref != null ) {
						item._chapterUrl = ahref.attr("href");
					}
					
					if ( sChapterText.contains("Oneshot") ) {
						item._chapterName = sChapterText;
					}
					
					if ( item._displayTitle.endsWith("*") ) {
						item._displayTitle = item._displayTitle.substring(0, item._displayTitle.length()-1);
					}
				}
				
				//updating date so that items are in correct order
				now = now.minusSeconds(1);
			}
		}
		return items;
	}

}
