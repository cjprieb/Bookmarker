package com.purplecat.bookmarker.services;

import com.google.inject.Inject;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.utils.JavaPropertiesRepository;
import com.purplecat.commons.utils.SimpleCache;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Crystal on 9/13/15.
 */
public class PropertiesSettingsService extends JavaPropertiesRepository implements ISettingsService {


	private static final String SUMMARY_DIRECTORY_KEY = "SUMMARY_DIRECTORY_KEY";
	private static final String WEBSITE_ORDER_KEY = "WebsiteOrderKey";

	private SimpleCache<List<String>> _websiteOrderCache;

	@Inject
	public PropertiesSettingsService(ILoggingService logging) {
		super(logging, "bookmarker.config");
		_websiteOrderCache = new SimpleCache<>(new Period(0, 15, 0, 0));
	}

	@Override
	public String getSummaryDirectory() {
		return getValue(SUMMARY_DIRECTORY_KEY, "data/summaries/");
	}

	@Override
	public void setSummaryDirectory(String directory) {
		setValue(SUMMARY_DIRECTORY_KEY, directory);
	}

	@Override
	public List<String> getWebsiteOrder() {
		List<String> value = _websiteOrderCache.get();
		if ( value != null ) return value;

		System.out.println("Getting Website Order Setting: " + DateTime.now());
		List<String> def = Arrays.asList("Batoto", "BakaUpdates");
		value = getValue(WEBSITE_ORDER_KEY, def);
		_websiteOrderCache.set(value);
		return value;
	}

	@Override
	public void setWebsiteOrder(List<String> nameOrder) {
		setValue(WEBSITE_ORDER_KEY, nameOrder);
		_websiteOrderCache.set(nameOrder);
	}
}
