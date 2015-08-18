package com.purplecat.bookmarker.services;

import java.io.File;
import java.io.IOException;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.purplecat.commons.io.FileUtils;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.utils.StringUtils;

@Singleton
public class FileSummaryRepository implements ISummaryRepository {
	private final String TAG = "FileSummaryRepository";
	
	private final ISettingsService _settings;
	private final ILoggingService _logging;
	
	@Inject
	FileSummaryRepository(ISettingsService settings, ILoggingService logging) {
		_settings = settings;
		_logging = logging;
	}

	@Override
	public String loadSummary(long mediaId, String websiteName) {
		String sSummary = null;
		File summaryFile = getFile(mediaId, websiteName);
		if ( summaryFile.exists() ) {
			try {
				sSummary = FileUtils.readAllText(summaryFile);
			} catch (IOException e) {
				_logging.error(TAG, "Error loading from " + summaryFile.getAbsolutePath(), e);
			}
		}
		return sSummary;
	}

	@Override
	public void saveSummary(long mediaId, String websiteName, String summary) {
		if ( !StringUtils.isNullOrEmpty(summary) ) {
			File summaryFile = getFile(mediaId, websiteName);
			try {
				if (!summaryFile.getParentFile().exists()) {
					summaryFile.getParentFile().mkdirs();
				}
				FileUtils.writeAllText(summaryFile, summary);
			} catch (IOException e) {
				_logging.error(TAG, "Error writing to " + summaryFile.getAbsolutePath(), e);
			}
		}
	}
	
	public File getFile(long mediaId, String websiteName) {
		File file = null;
		if ( !StringUtils.isNullOrEmpty(websiteName) ) {
			file = new File(_settings.getSummaryDirectory(), String.format("%s-%s.html", mediaId, websiteName));
		}
		else {
			File parent = new File(_settings.getSummaryDirectory());
			if ( parent.exists() ) {
				String prefix = String.format("%s-", mediaId);
				for ( String filename : parent.list() ) {
					if ( filename.startsWith(prefix) ) {
						file = new File(_settings.getSummaryDirectory(), filename);
						break;
					}
				}
			}
		}
		return file;
	}

}
