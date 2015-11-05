package com.purplecat.bookmarker.services.websites;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.inject.Inject;
import com.purplecat.bookmarker.extensions.PlaceExt;
import com.purplecat.bookmarker.extensions.WebsiteDateExt;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.Place;
import com.purplecat.bookmarker.models.WebsiteInfo;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.bookmarker.services.databases.IGenreRepository;
import com.purplecat.commons.extensions.Numbers;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.utils.StringUtils;

public class BatotoWebsite implements IWebsiteParser {
	final String TAG = "BatotoWebsite";
	final String NAME = "Batoto";
	final String HOMEPAGE_URL = "http://bato.to";

	protected final ILoggingService _logging;
	protected final IGenreRepository _genreDatabase;
	
	//(4.62 - 128votes)
	protected final Pattern _ratingPattern = Pattern.compile("([\\d\\.]+)");
	protected final String _pageFormat = "http://bato.to/?p=%d";
	protected final int MAX_PAGE = 5;
	
	@Inject
	public BatotoWebsite(ILoggingService logging, IGenreRepository genres) {
		_logging = logging;
		_genreDatabase = genres;
	}

	@Override
	public String getName() { return NAME; }

	@Override
	public String getWebsiteUrl() { return HOMEPAGE_URL; }
	
	protected Document getDocument(int page) throws IOException {
		return Jsoup.connect(String.format(_pageFormat, page)).get();
	}

	@Override
	public List<OnlineMediaItem> load(DateTime minDateToLoad) throws ServiceException {
		try {
			List<OnlineMediaItem> allItems = new LinkedList<OnlineMediaItem>();
			for ( int i = 1; i <= MAX_PAGE; i++ ) {
				List<OnlineMediaItem> list = loadDocument(getDocument(i), minDateToLoad);
				if ( list == null || list.isEmpty() ) {
					break;
				}
				allItems.addAll(list);
			}
			return allItems;
		} catch (IOException e) {
			_logging.error(TAG, "Error getting document", e);
			throw new ServiceException("Error loading Batoto", ServiceException.WEBSITE_ERROR);
		}
	}
	
	public List<OnlineMediaItem>  loadDocument(Document doc, DateTime minDateToLoad) {
		if ( doc == null ) { return null; }
		
		List<OnlineMediaItem> items = new LinkedList<OnlineMediaItem>();
		DateTime now = DateTime.now();
		Elements rows = doc.select(".chapters_list .lang_English");
		OnlineMediaItem currentItem = null;
//		int rowCount = 0;
//		_logging.debug(0, TAG, rows.size() + " rows found");
		for ( Element row : rows ) {
//			rowCount++;
			//Each item takes up at least 2 rows
			if ( row.select("td").size() == 2 ) {
				//first row: thumbnail and title and link to title
				if ( currentItem != null ) {
					if ( currentItem._updatedDate.compareTo(minDateToLoad) >= 0 ) {
						items.add(currentItem);
					}
				}
				currentItem = new OnlineMediaItem();
				
				Element titleLink = null;
				try {
					titleLink = row.select("td").get(1).select("a").get(1);
					
					currentItem._websiteName = getName();
					currentItem._displayTitle = titleLink.text();
					currentItem._titleUrl = titleLink.attr("href");
//					_logging.debug(0, TAG, "Parsing: " + currentItem._displayTitle + " (" + currentItem._titleUrl + ")");
				} catch (NullPointerException e) {
					currentItem = null;
					_logging.error(TAG, "Row could not be parsed as expected: " + row.html());
				}
			}
			else if ( currentItem != null ) {
//				_logging.debug(1, TAG, rowCount + ") parsing date for: " + currentItem._displayTitle);
				//chapter rows: place, link to chapter, and date/time
				String chapterUrl = row.select("a").first().attr("href");
				String chapterName = row.select("a").first().text();
				String stringDate = row.select("td").last().text();
				Place place = PlaceExt.parseBatotoPlace(chapterName);
				DateTime date = WebsiteDateExt.parseBatotoDate(now, stringDate);
//				_logging.debug(2, TAG, "date: " + date);
//				_logging.debug(2, TAG, "place: " + place);
				
				if ( currentItem._updatedPlace.compareTo(place) <= 0 ) {
//					_logging.debug(2, TAG, "updating date and place of existing batoto item");
					currentItem._chapterUrl = HOMEPAGE_URL + chapterUrl;
					currentItem._chapterName = chapterName;
					currentItem._updatedPlace = place;
					currentItem._updatedDate = date;
				}
			}
		}
		return items;
	}

	@Override
	public OnlineMediaItem loadItem(OnlineMediaItem item) throws ServiceException {
		if ( StringUtils.isNullOrEmpty(item._titleUrl) ) {
			return item;
		}
		
		try {
			Document doc = Jsoup.connect(item._titleUrl).get();
			Elements rows = doc.select("table.ipb_table tr");
			for ( Element row : rows ) {
				//Each row has at least 2 columns
				if ( row.select("td").size() >= 2 ) {
					String label = row.select("td").get(0).text().toLowerCase(Locale.ENGLISH);
					
					if ( label.startsWith("genres") ) {
						for ( Element genreLink : row.select("a") ) {							
							item._genres.add(_genreDatabase.find(genreLink.text()));
						}
					}
					else if ( label.startsWith("description") ) {
						item._summary = row.select("td").html();
					}
				}
			}
			//(4.62 - 128votes)
			Element ratingElement = doc.select(".rating").first();
			if ( ratingElement != null ) {
				System.out.println("class: " + ratingElement.text());
				Matcher matcher = _ratingPattern.matcher(ratingElement.text());
				if ( matcher.find() ) {
					item._rating = Numbers.parseDouble(matcher.group(1), 0) / 5.0; //out of 5, make it out of 1
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
	public boolean urlMatches(String url) {
		return !StringUtils.isNullOrEmpty(url) && (url.contains("batoto") || url.contains("bato.to"));
	}

}
