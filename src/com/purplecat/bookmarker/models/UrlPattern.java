package com.purplecat.bookmarker.models;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class UrlPattern extends BaseDatabaseItem {
	public Pattern _pattern;
	public String _patternString = "";
	public Map<String, Integer> _map = new HashMap<String, Integer>();
	public UrlPatternType _type = UrlPatternType.MANGA;
	
	public UrlPattern copy() {
		UrlPattern urlpattern = new UrlPattern();
		urlpattern._id = this._id;
		urlpattern._patternString = this._patternString;
		urlpattern._pattern = Pattern.compile(_patternString);
		urlpattern._map.putAll(this._map);
		urlpattern._type = this._type;
		return urlpattern;
	}
}
