package com.purplecat.bookmarker.services.websites;

import java.io.IOException;
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
	
	private final WebsiteInfo _info;
	private final ILoggingService _logging;
	private final IGenreRepository _genreDatabase;
	private final Pattern _chapterRegex;
	private final Pattern _daysAgoRegex;
	private final Pattern _dateRegex;
	
	@Inject
	public BatotoWebsite(ILoggingService logging, IGenreRepository genres) {
		_logging = logging;
		_genreDatabase = genres;
		_info = new WebsiteInfo("Batoto", "http://bato.to/");
		
		//	http://bato.to/read/_/320356/world-trigger_ch101_by_glorious-scanlations
		//	http://bato.to/read/_/320360/medarot_v1_ch4_by_heavenly-scans
		//	http://bato.to/read/_/320273/song-of-the-long-march_ch43.2_by_easy-going-scans
		//	http://bato.to/read/_/320272/kounodori-the-stork_v2_ch7.1_by_futari-wa-pretty-anon
		//	http://bato.to/read/_/320220/alice-in-borderland_v6_ch24--v2-_by_nest-traducciones
		_chapterRegex = Pattern.compile("\\d+/([^/_]+)_(?:v(\\d+)_)?(?:ch(\\d+)([^_]+)?_)?by_(.+)");

		//35 minutes ago 
		//An hour ago
		//4 hours ago 
		//A day ago
		//2 days ago
		_daysAgoRegex = Pattern.compile("(?:(\\d+)|(\\w+)) (\\w+)");
		
		//Today, 09:05 PM
		//Today, 08:50 PM
		//Today, 07:47 PM
		_dateRegex = Pattern.compile("(\\w+), (\\d+):(\\d+) (\\w+)");
	}

	private DateTime parseDate(DateTime now, String text) {
		_logging.debug(1, TAG, "Parsing date: " + text);
		
		DateTime result = null;
		
		Matcher matcher = _daysAgoRegex.matcher(text);
		if (matcher.find()){
			_logging.debug(2, TAG, "Group 1: " + matcher.group(1));
			_logging.debug(2, TAG, "Group 2: " + matcher.group(2));
			_logging.debug(2, TAG, "Group 3: " + matcher.group(3));
			
			int unitsAgo = 1;			
			if ( matcher.group(1) != null ) {
				unitsAgo = Numbers.parseInt(matcher.group(1),1);
			}
			
			if ( matcher.group(3).startsWith("minute") ) {
				result = now.minusMinutes(unitsAgo);
			}
			else if ( matcher.group(3).startsWith("hour") ) {
				result = now.minusHours(unitsAgo);
			}
			else if ( matcher.group(3).startsWith("day") ) {
				result = now.minusDays(unitsAgo);
			}
		}
		else {			
			matcher = _dateRegex.matcher(text);
			if (matcher.find()){
				_logging.debug(2, TAG, "Group 1: " + matcher.group(1));
				_logging.debug(2, TAG, "Group 2: " + matcher.group(2));
				_logging.debug(2, TAG, "Group 3: " + matcher.group(3));
				_logging.debug(2, TAG, "Group 4: " + matcher.group(4));
				
				int hours = Numbers.parseInt(matcher.group(2), 0);	
				int minutes = Numbers.parseInt(matcher.group(3), 0);		
				if ( matcher.group(4).equalsIgnoreCase("PM") ) {
					hours += 12;
				}
				
				if ( matcher.group(1).toLowerCase().equals("today") ) {
					result = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hours, minutes);
				}
			}			
		}
		
		if ( result != null ) {
			_logging.debug(2, TAG, "Result: " + result.toString("MM/dd HH:mm"));
		}
		return result;
	}

	private Place parsePlace(String chapterUrl) {
		_logging.debug(1, TAG, "Parsing place: " + chapterUrl);
		int volume = 0, chapter = 0, sub = 0;
		boolean extra = false;
		
		Matcher matcher = _chapterRegex.matcher(chapterUrl);
		if ( matcher.find() ) {
			_logging.debug(2, TAG, "Group 1: " + matcher.group(1));
			_logging.debug(2, TAG, "Group 2: " + matcher.group(2));
			_logging.debug(2, TAG, "Group 3: " + matcher.group(3));
			_logging.debug(2, TAG, "Group 4: " + matcher.group(4));
			
			volume = Numbers.parseInt(matcher.group(2), 0);
			chapter = Numbers.parseInt(matcher.group(3), 0);
			String moreDetail = matcher.group(4);
			if ( moreDetail != null && moreDetail.length() > 0 ) {
				if ( moreDetail.startsWith(".") ) {
					String subChapterString = "";
					for ( int i = 1; i < moreDetail.length(); i++ ) {
						if ( Character.isDigit(moreDetail.charAt(i) ) ) {
							subChapterString += moreDetail.charAt(i);
						}
						else {
							break;
						}
					}
					sub = Numbers.parseInt(subChapterString, 0);
				}
				else if ( moreDetail.contains("v2") ){
					extra = true; //?
				}
			}
		}
		
		Place p = new Place(volume, chapter, sub, 0, extra);
		_logging.debug(2, TAG, "Place: " + p);
		return p;
	}

	@Override
	public List<OnlineMediaItem> load() throws ServiceException {
		List<OnlineMediaItem> items = new LinkedList<OnlineMediaItem>();
		DateTime now = DateTime.now();
		try {
			Document doc = Jsoup.connect(_info._website).get();
			Elements rows = doc.select(".chapters_list .lang_English");
			OnlineMediaItem currentItem = null;
			for ( Element row : rows ) {
				//Each item takes up at least 2 rows
				if ( row.select("td").size() == 2 ) {
					//first row: thumbnail and title and link to title
					currentItem = new OnlineMediaItem();
					items.add(currentItem);
					
					Element titleLink = null;
					try {
						titleLink = row.select("td").get(1).select("a").get(1);
						
						currentItem._websiteName = _info._name;
						currentItem._displayTitle = titleLink.text();
						currentItem._titleUrl = titleLink.attr("href");
						_logging.debug(0, TAG, "Parsing: " + currentItem._displayTitle + " (" + currentItem._titleUrl + ")");
					} catch (NullPointerException e) {
						currentItem = null;
						_logging.error(TAG, "Row could not be parsed as expected: " + row.html());
					}
				}
				else if ( currentItem != null ) {
					//chapter rows: place, link to chapter, and date/time
					String chapterUrl = row.select("a").first().attr("href");
					String chapterName = row.select("a").first().text();
					String stringDate = row.select("td").last().text();
					Place place = parsePlace(chapterUrl);
					DateTime date = parseDate(now, stringDate);
					
					if ( currentItem._updatedPlace.compareTo(place) <= 0 ) {
						currentItem._chapterUrl = chapterUrl;
						currentItem._chapterName = chapterName;
						currentItem._updatedPlace = place;
						currentItem._updatedDate = date;
					}
				}
			}
			
		} catch (IOException e) {
			_logging.error(TAG, "Error loading items", e);
			throw new ServiceException("Error loading Batoto", ServiceException.WEBSITE_ERROR);
		}
		return items;
	}

	@Override
	public WebsiteInfo getInfo() {
		return _info;
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
			
		} catch (IOException e) {
			_logging.error(TAG, "Error loading item: " + item, e);
			throw new ServiceException("Error loading Batoto", ServiceException.WEBSITE_ERROR);
		}
		return item;
	}

}
