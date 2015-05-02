package com.purplecat.bookmarker.models;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class UrlPattern extends BaseDatabaseItem {
	public Pattern _pattern;
	public String _patternString = "";
	public Map<String, Integer> _map = new HashMap<String, Integer>();
	
	public UrlPattern copy() {
		UrlPattern urlpattern = new UrlPattern();
		urlpattern._id = this._id;
		urlpattern._pattern = this._pattern;
		urlpattern._map.putAll(this._map);
		return urlpattern;
	}
}
