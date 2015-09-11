package com.purplecat.bookmarker.view.swing.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.google.inject.Inject;
import com.purplecat.bookmarker.Resources;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.WebsiteInfo;
import com.purplecat.bookmarker.services.websites.IWebsiteLoadObserver;
import com.purplecat.bookmarker.view.swing.actions.LoadUpdatesAction;
import com.purplecat.bookmarker.view.swing.actions.StopUpdatesAction;
import com.purplecat.bookmarker.view.swing.components.HourSpinner;
import com.purplecat.bookmarker.view.swing.components.OnlineUpdateItemTableControl;
import com.purplecat.bookmarker.view.swing.components.WebsiteDropdown;
import com.purplecat.bookmarker.view.swing.observers.OnlineMediaSummaryObserver;
import com.purplecat.bookmarker.view.swing.observers.UpdateMediaObserverControl;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.swing.Toolbox;

public class OnlineUpdateTab {
	private static final String TAG = "MainPanel";
	
	@Inject Controller _controller;
	@Inject ILoggingService _logging;
	@Inject IResourceService _resources;
	@Inject Toolbox _toolbox;
	@Inject SummarySidebar _summaryPanel;
	@Inject OnlineMediaSummaryObserver _summaryObserver;
	@Inject OnlineUpdateItemTableControl _updateMediaTableControl;
	@Inject WebsiteDropdown _websiteDropdown;
	
	private JPanel _panel;
	private HourSpinner _spinner;
	private JCheckBox _chkLoadGenres;
	private JCheckBox _chkAllWebsites;
	
	public void create() {
		UpdateMediaObserverControl updateObserver = new UpdateMediaObserverControl(_resources);
		
		{
			_controller.observeOnlineThreadLoading(updateObserver);
			_controller.observeOnlineThreadLoading(new OnlineUpdateTabObserver());
			_controller.observeSummaryLoading(_summaryObserver);
			_updateMediaTableControl.getTable().addRowSelectionListener(_summaryObserver);
		}			
		
		JButton loadUpdatesButton = new JButton(new LoadUpdatesAction(_resources, this));		
		JButton stopUpdatesButton = new JButton(new StopUpdatesAction(_controller, _resources));		
		
		{
			_spinner = new HourSpinner(_resources);
			_spinner.setValue(8);
			
			_chkLoadGenres = new JCheckBox(_resources.getString(Resources.string.lblLoadGenres));
			_chkAllWebsites = new JCheckBox(_resources.getString(Resources.string.lblAllSites));
			
			_chkAllWebsites.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					_websiteDropdown.setEnabled(!_chkAllWebsites.isSelected());
				}				
			});
		}

		_panel = new JPanel();
		GroupLayout layout = new GroupLayout(_panel);
		_panel.setLayout(layout);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
							.addComponent(_chkAllWebsites, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(_websiteDropdown, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(_spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(_chkLoadGenres, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(0, 0, Short.MAX_VALUE)
							.addComponent(loadUpdatesButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(stopUpdatesButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						)
						.addComponent(_updateMediaTableControl.getComponent(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addGroup(layout.createSequentialGroup()
							.addComponent(updateObserver.getCountLabel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(updateObserver.getTimeLabel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(0, 0, Short.MAX_VALUE)
						)
						.addComponent(updateObserver.getProgressBar(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				)
				.addContainerGap());
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup()
					.addComponent(_chkAllWebsites, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(_websiteDropdown, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(_spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(_chkLoadGenres, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(loadUpdatesButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(stopUpdatesButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addComponent(_updateMediaTableControl.getComponent(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(updateObserver.getCountLabel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(updateObserver.getTimeLabel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(updateObserver.getProgressBar(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)				
				.addContainerGap());
		
		loadPreferences();
	}
	
	public JPanel getPanel() {
		return _panel;
	}
	
	public void updateSummaryPanel() {
		_summaryPanel.setSummaryView(_summaryObserver.getSummaryPanel());
	}

	public int getHoursAgo() {
		return (int)_spinner.getHourValue();
	}

	public boolean getLoadGenresSetting() {
		return _chkLoadGenres.isSelected();
	}
	
	public boolean getLoadAllWebsitesSetting() {
		return _chkAllWebsites.isSelected();
	}
	
	public WebsiteInfo getSelectedWebsiteInfo() {
		return (WebsiteInfo)_websiteDropdown.getSelectedItem();
	}
	
	public void callLoadUpdates() {
		_controller.loadUpdateMedia(getHoursAgo(), getLoadGenresSetting(), getLoadAllWebsitesSetting(), getSelectedWebsiteInfo());		
	}

	public OnlineMediaItem getSelectedItem() {
		return _updateMediaTableControl.getTable().getSelectedItem();
	}
	
	public void loadPreferences() {
		_logging.debug(1, TAG, "Loading OnlineUpdateTab preferences");
		Preferences prefs = Preferences.userNodeForPackage(getClass());
		_logging.debug(3, TAG, "hours ago: " + prefs.getInt("update-hoursAgo", 8));
		_spinner.setValue(prefs.getInt("update-hoursAgo", 8));
		_chkLoadGenres.setSelected(prefs.getBoolean("update-loadGenres", true));
		_chkAllWebsites.setSelected(prefs.getBoolean("update-allWebsites", false));
		
		String websiteName = prefs.get("update-selectedWebsite", "");
		for ( int i = 0; i < _websiteDropdown.getItemCount(); i++ ) {
			WebsiteInfo info = _websiteDropdown.getItemAt(i);
			if ( info._name.equals(websiteName) ) {
				_websiteDropdown.setSelectedIndex(i);
				break;
			}
		}
		
		_websiteDropdown.setEnabled(!_chkAllWebsites.isSelected());
	}
	
	public void savePreferences() {
		_logging.debug(2, TAG, "Saving OnlineUpdateTab preferences");
		Preferences prefs = Preferences.userNodeForPackage(getClass());
		_logging.debug(3, TAG, "hours ago: " + (int)_spinner.getHourValue());
		
		prefs.putInt("update-hoursAgo", (int)_spinner.getHourValue());
		prefs.putBoolean("update-allWebsites", _chkAllWebsites.isSelected());
		prefs.putBoolean("update-loadGenres", _chkLoadGenres.isSelected());
		prefs.put("update-selectedWebsite", ((WebsiteInfo)_websiteDropdown.getSelectedItem())._name);
	}

	class OnlineUpdateTabObserver implements IWebsiteLoadObserver {
		@Override
		public void notifyLoadStarted() {}
	
		@Override
		public void notifySiteStarted(WebsiteInfo site) {}
	
		@Override
		public void notifySiteParsed(WebsiteInfo site, int itemsFound) {}
	
		@Override
		public void notifyItemParsed(OnlineMediaItem item, int itemsParsed, int totalUpdateCount) {}
		
		@Override
		public void notifyItemRemoved(OnlineMediaItem newItem, int itemsParsed, int size) {}
	
		@Override
		public void notifySiteFinished(WebsiteInfo site) {
			_updateMediaTableControl.getModel().removeItemsOlderThan(getHoursAgo(), site._name);
		}
	
		@Override
		public void notifyLoadFinished(List<OnlineMediaItem> list) {}
	}
}
