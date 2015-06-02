package com.purplecat.bookmarker.view.swing;

import java.awt.Dimension;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JPanel;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.view.swing.panels.GlassTimerPanel;
import com.purplecat.bookmarker.view.swing.panels.OnlineUpdateTab;
import com.purplecat.bookmarker.view.swing.panels.SavedMediaTab;
import com.purplecat.bookmarker.view.swing.panels.SummarySidebar;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.swing.renderer.ICellRendererFactory;

@Singleton
public class MainPanel {
	
	@Inject Controller _controller;
	@Inject ICellRendererFactory _rendererFactory;
	@Inject IResourceService _resources;
	
	@Inject SavedMediaTab _savedMediaTab;
	@Inject OnlineUpdateTab _onlineUpdateTab;	
	@Inject SummarySidebar _summaryPanel;
	
	public JPanel _panel;

	public JPanel create(GlassTimerPanel timerGlassPane) {
		_panel = new JPanel();
		_panel.setPreferredSize(new Dimension(1300, 600));

		timerGlassPane.setCoverPanel(_panel);		
		_controller.observeSavedMediaLoading(new GlassPanelListObserver(timerGlassPane));
		
		JPanel summaryPanel = _summaryPanel.create();		
		JPanel savedMediaPanel = _savedMediaTab.create();
		JPanel onlineUpdatePanel = _onlineUpdateTab.create();
		
		
		GroupLayout layout = new GroupLayout(_panel);
		_panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(savedMediaPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addComponent(onlineUpdatePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addComponent(summaryPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addContainerGap());
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup()
					.addComponent(savedMediaPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addComponent(onlineUpdatePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addComponent(summaryPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				)
				.addContainerGap());
		
		return _panel;
	}
	
	public static class GlassPanelListObserver implements IListLoadedObserver<Media> {
		
		protected final GlassTimerPanel _glassPanel;
		
		public GlassPanelListObserver(GlassTimerPanel glassPanel) {
			_glassPanel = glassPanel;
			_glassPanel.setProgress(0, 0);
		}

		@Override
		public void notifyItemLoaded(Media item, int index, int total) {
			_glassPanel.setProgress(index, total);
		}

		@Override
		public void notifyListLoaded(List<Media> list) {
			_glassPanel.stopTimer();
			_glassPanel.setProgress(0, 0);
			_glassPanel.setVisible(false);
		}		
	}
}
