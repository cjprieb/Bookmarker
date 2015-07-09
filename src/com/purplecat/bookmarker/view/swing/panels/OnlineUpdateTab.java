package com.purplecat.bookmarker.view.swing.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.google.inject.Inject;
import com.purplecat.bookmarker.Resources;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.models.WebsiteInfo;
import com.purplecat.bookmarker.view.swing.actions.LoadUpdatesAction;
import com.purplecat.bookmarker.view.swing.actions.StopUpdatesAction;
import com.purplecat.bookmarker.view.swing.components.HourSpinner;
import com.purplecat.bookmarker.view.swing.components.OnlineUpdateItemTableControl;
import com.purplecat.bookmarker.view.swing.components.WebsiteDropdown;
import com.purplecat.bookmarker.view.swing.observers.OnlineMediaSummaryObserver;
import com.purplecat.bookmarker.view.swing.observers.UpdateMediaObserverControl;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.swing.Toolbox;

public class OnlineUpdateTab {
	
	@Inject Controller _controller;
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
			_controller.observeOnlineThreadLoading(_updateMediaTableControl.getModel().getObserver());
			_controller.observeSavedMediaUpdate(_updateMediaTableControl.getModel().getObserver());
			_controller.observeOnlineThreadLoading(updateObserver);
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
}
