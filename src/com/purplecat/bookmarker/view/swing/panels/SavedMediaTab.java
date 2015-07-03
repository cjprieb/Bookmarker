package com.purplecat.bookmarker.view.swing.panels;

import javax.swing.GroupLayout;
import javax.swing.JPanel;

import com.google.inject.Inject;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.view.swing.components.SavedMediaTableControl;
import com.purplecat.bookmarker.view.swing.observers.SavedMediaSummaryObserver;

public class SavedMediaTab {
	private JPanel _panel;
	
	@Inject Controller _controller;
	@Inject SummarySidebar _summaryPanel;
	@Inject SavedMediaSummaryObserver _summaryObserver;
	@Inject SavedMediaTableControl _savedMediaTableControl;
	
	public void create() {		
		_controller.observeSavedMediaLoading(_savedMediaTableControl.getModel().getObserver());
		_controller.observeOnlineThreadLoading(_savedMediaTableControl.getModel().getObserver());
		_controller.observeSavedMediaUpdate(_savedMediaTableControl.getModel().getObserver());
		_savedMediaTableControl.getTable().addRowSelectionListener(_summaryObserver);

		_panel = new JPanel();
		GroupLayout layout = new GroupLayout(_panel);
		_panel.setLayout(layout);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(_savedMediaTableControl.getComponent(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
			.addContainerGap());
		
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(_savedMediaTableControl.getComponent(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)				
			.addContainerGap());
	}
	
	public JPanel getPanel() {
		return _panel;
	}
	
	public void updateSummaryPanel() {
		_summaryPanel.setSummaryView(_summaryObserver.getSummaryPanel());
	}
}
