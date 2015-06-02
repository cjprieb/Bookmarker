package com.purplecat.bookmarker.view.swing.panels;

import javax.swing.GroupLayout;
import javax.swing.JPanel;

import com.google.inject.Inject;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.view.swing.components.SavedMediaTableControl;
import com.purplecat.bookmarker.view.swing.observers.SavedMediaSummaryObserver;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.swing.renderer.ICellRendererFactory;

public class SavedMediaTab {
	private JPanel _panel;
	
	@Inject Controller _controller;
	@Inject ICellRendererFactory _rendererFactory;
	@Inject IResourceService _resources;
	@Inject SummarySidebar _summaryPanel;
	@Inject SavedMediaSummaryObserver _summaryObserver;
	
	public void create() {		
		SavedMediaTableControl savedMediaTableControl = new SavedMediaTableControl(_rendererFactory, _resources);
		_controller.observeSavedMediaLoading(savedMediaTableControl.getModel().getObserver());
		_controller.observeOnlineThreadLoading(savedMediaTableControl.getModel().getObserver());
		_controller.observeSavedMediaUpdate(savedMediaTableControl.getModel().getObserver());
		savedMediaTableControl.getTable().addRowSelectionListener(_summaryObserver);

		_panel = new JPanel();
		GroupLayout layout = new GroupLayout(_panel);
		_panel.setLayout(layout);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(savedMediaTableControl.getComponent(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
			.addContainerGap());
		
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(savedMediaTableControl.getComponent(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)				
			.addContainerGap());
	}
	
	public JPanel getPanel() {
		return _panel;
	}
	
	public void updateSummaryPanel() {
		_summaryPanel.setSummaryView(_summaryObserver.getSummaryPanel());
	}
}
