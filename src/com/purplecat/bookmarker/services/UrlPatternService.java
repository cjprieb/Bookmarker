package com.purplecat.bookmarker.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.purplecat.bookmarker.models.UrlPattern;
import com.purplecat.bookmarker.models.UrlPatternResult;
import com.purplecat.bookmarker.services.databases.IDatabaseConnector;

public class UrlPatternService extends BaseDatabaseItemService<UrlPattern> {
	
	public UrlPatternService(IDatabaseConnector<UrlPattern> database) {
		super(database);
	}
	
	public UrlPatternResult match(String url) throws ServiceException {
		UrlPatternResult result = new UrlPatternResult();
		result._success = false;
		if (url != null && url.length() > 0) {
			for( UrlPattern pattern : list() ) {
				if ( match(pattern, url, result) ) {
					result._success = true;
					break;
				}
			}
		}
		return result;
	}

	private boolean match(UrlPattern urlPattern, String url, UrlPatternResult result) {
		if ( urlPattern._pattern == null ) {
			urlPattern._pattern = Pattern.compile(urlPattern._patternString);
		}
		
		Matcher matcher = urlPattern._pattern.matcher(url);
		if ( matcher.find() ) {			
			result._title = getGroupMatch(matcher, urlPattern, "TITLE");
			if ( result._title != null ) {
				result._title = result._title.replaceAll("([_\\+-]|%20)", " ");				
			}
			
			result._volume = getGroupMatchInteger(matcher, urlPattern, "VOLUME");
			
			String chapterStr = getGroupMatch(matcher, urlPattern, "CHAPTER");
			if ( chapterStr != null && chapterStr.length() > 0 ) {
				parseChapter(chapterStr, result);
			}
			result._page = getGroupMatchInteger(matcher, urlPattern, "PAGE");
			
			return true;
		}
		
		return false;
	}
	
	private String getGroupMatch(Matcher matcher, UrlPattern pattern, String key) {		
		Integer groupIndex = pattern._map.get(key);
		if ( groupIndex != null && groupIndex > 0 && groupIndex <= matcher.groupCount() ) {
			return matcher.group(groupIndex);
		}
		return null;
	}
	
	private int getGroupMatchInteger(Matcher matcher, UrlPattern pattern, String key) {
		String groupValue = getGroupMatch(matcher, pattern, key);
		if ( groupValue != null && groupValue.length() > 0 ) {
			try {
				return Integer.parseInt(groupValue);
			} catch (NumberFormatException e) {	}
		}
		return 0;
	}
	
	private void parseChapter(String sChapterStr, UrlPatternResult result) {
		int iDecimal = sChapterStr.indexOf('.');
		try {
			result._chapter = Integer.parseInt(iDecimal > 0 ? sChapterStr.substring(0, iDecimal) : sChapterStr);
			String sSubChapter = iDecimal > 0 ? sChapterStr.substring(iDecimal+1).toLowerCase() : "";
			if ( sSubChapter.length() > 0 ) {
				if ( Character.isLetter(sSubChapter.charAt(0)) ) {
					//assume one character long
					result._subChapter = (int)(sSubChapter.charAt(0) - 'a') + 1;//'a' == 1
				}
				else if ( Character.isDigit(sSubChapter.charAt(0)) ) {
					//assume digit
					result._subChapter = Integer.parseInt(sSubChapter);
				}
			}
		} catch (NumberFormatException e) {}
	}
}
