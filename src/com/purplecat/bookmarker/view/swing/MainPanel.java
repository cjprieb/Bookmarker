package com.purplecat.bookmarker.view.swing;

import java.awt.Dimension;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.google.inject.Inject;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.view.swing.panels.GlassTimerPanel;
import com.purplecat.bookmarker.view.swing.panels.OnlineUpdateTab;
import com.purplecat.bookmarker.view.swing.panels.SavedMediaTab;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.swing.renderer.ICellRendererFactory;

public class MainPanel {
	
	@Inject Controller _controller;
	@Inject ICellRendererFactory _rendererFactory;
	@Inject IResourceService _resources;

	public JPanel create(GlassTimerPanel timerGlassPane) {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(1000, 600));

		timerGlassPane.setCoverPanel(panel);		
		_controller.observeSavedMediaLoading(new GlassPanelListObserver(timerGlassPane));
		
		JPanel savedMediaTab = new SavedMediaTab().create(_controller, _rendererFactory, _resources);
		JPanel onlineUpdateTab = new OnlineUpdateTab().create(_controller, _rendererFactory, _resources);
		
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(savedMediaTab, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addComponent(onlineUpdateTab, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addContainerGap());
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup()
					.addComponent(savedMediaTab, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addComponent(onlineUpdateTab, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				)
				.addContainerGap());
		
		return panel;
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
