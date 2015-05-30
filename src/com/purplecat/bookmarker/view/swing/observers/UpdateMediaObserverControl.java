package com.purplecat.bookmarker.view.swing.observers;

import java.awt.Color;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.purplecat.bookmarker.Resources;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.WebsiteInfo;
import com.purplecat.bookmarker.services.websites.IWebsiteLoadObserver;
import com.purplecat.bookmarker.view.swing.components.LabelBadge;
import com.purplecat.bookmarker.view.swing.components.RatioProgressBar;
import com.purplecat.commons.IResourceService;

public class UpdateMediaObserverControl implements IWebsiteLoadObserver {
	IResourceService _resources;
	JProgressBar _progressBar;
	JLabel _timeLabel;
	LabelBadge _countLabel;
	int _updateCount;
	
	public UpdateMediaObserverControl(IResourceService resources) {
		_resources = resources;
		_progressBar = new RatioProgressBar(resources);
		_timeLabel = new JLabel();
		_countLabel = new LabelBadge(2);
		_countLabel.setBackground(Color.GREEN);
		_progressBar.setMaximum(0);
	}
	
	public JProgressBar getProgressBar() {
		return _progressBar;
	}
	
	public JLabel getTimeLabel() {
		return _timeLabel;
	}
	
	public LabelBadge getCountLabel() {
		return _countLabel;
	}

	@Override
	public void notifyLoadStarted() {
		markStarted();
		_progressBar.setString(_resources.getString(Resources.string.notifyUpdatesStarted));
		_timeLabel.setText(String.format(
				"%s:%s",
				_resources.getString(Resources.string.lblUpdated),
				DateTime.now().toString(DateTimeFormat.shortTime())
		));
		_updateCount = 0;
		updateIcons(Color.yellow, Color.black, 0);
	}

	@Override
	public void notifySiteStarted(WebsiteInfo site) {
		markStarted();
		_progressBar.setString(
			String.format(_resources.getString(Resources.string.notifyLoadingSite), site._name)
		);
	}

	@Override
	public void notifySiteParsed(WebsiteInfo site, int maxFound) {
		_progressBar.setMaximum(maxFound);
		_progressBar.setString(
			String.format(_resources.getString(Resources.string.notifyLoadingGenres), site._name)
		);
	}

	@Override
	public void notifyItemParsed(OnlineMediaItem item, int itemsParsed, int totalUpdateCount) {
		_progressBar.setValue(itemsParsed);
		if ( _updateCount != totalUpdateCount ) {
			_updateCount = totalUpdateCount;
			updateIcons(Color.yellow, Color.black, _updateCount);
		}
	}

	@Override
	public void notifySiteFinished(WebsiteInfo site) {
		markDone();
	}

	@Override
	public void notifyLoadFinished(List<OnlineMediaItem> list) {
		markDone();
	}
	
	private void markStarted() {
		_progressBar.setValue(0);
		_progressBar.setMaximum(0);
	}
	
	private void markDone() {
		_progressBar.setValue(_progressBar.getMaximum());
		updateIcons(Color.green, Color.black, _updateCount);
	}
	
	private void updateIcons(Color background, Color foreground, int updateCount) {
		_countLabel.setBackground(background);
		_countLabel.setForeground(foreground);
		_countLabel.setText(String.valueOf(updateCount));
	}

}