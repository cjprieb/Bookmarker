package com.purplecat.bookmarker.services;

public interface ISummaryRepository {

	String loadSummary(long _mediaId, String _websiteName);

	void saveSummary(long _mediaId, String _websiteName, String _summary);

}
