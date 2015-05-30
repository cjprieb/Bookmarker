package com.purplecat.bookmarker.view.swing;

import java.awt.Dimension;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.google.inject.Inject;
import com.purplecat.bookmarker.Resources;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.view.swing.actions.LoadUpdatesAction;
import com.purplecat.bookmarker.view.swing.actions.StopUpdatesAction;
import com.purplecat.bookmarker.view.swing.observers.UpdateMediaObserverControl;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.swing.renderer.ICellRendererFactory;

public class MainPanel {
	
	@Inject Controller _controller;
	@Inject ICellRendererFactory _rendererFactory;
	@Inject IResourceService _resources;

	public JPanel create(GlassTimerPanel timerGlassPane) {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(1000, 600));
		
		MediaTableControl savedMediaTableControl = new MediaTableControl(_rendererFactory, _resources);
		_controller.observeSavedMediaLoading(savedMediaTableControl.getModel().getObserver());
		_controller.observeOnlineThreadLoading(savedMediaTableControl.getModel().getObserver());
		_controller.observeSavedMediaUpdate(savedMediaTableControl.getModel().getObserver());
		
		UpdateMediaTableControl updateMediaTableControl = new UpdateMediaTableControl(_rendererFactory, _controller, _resources);
		_controller.observeOnlineThreadLoading(updateMediaTableControl.getModel().getObserver());
		_controller.observeSavedMediaUpdate(updateMediaTableControl.getModel().getObserver());
		
		UpdateMediaObserverControl updateObserver = new UpdateMediaObserverControl(_resources);
		_controller.observeOnlineThreadLoading(updateObserver);
		
		JButton loadUpdatesButton = new JButton(_resources.getString(Resources.string.lblLoadUpdates));
		loadUpdatesButton.addActionListener(new LoadUpdatesAction(_controller));
		
		JButton stopUpdatesButton = new JButton(_resources.getString(Resources.string.lblStopUpdates));
		stopUpdatesButton.addActionListener(new StopUpdatesAction(_controller));

		timerGlassPane.setCoverPanel(panel);
		
		_controller.observeSavedMediaLoading(new GlassPanelListObserver(timerGlassPane));
		
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(savedMediaTableControl.getComponent(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addGroup(layout.createParallelGroup()
						.addComponent(updateMediaTableControl.getComponent(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addGroup(layout.createSequentialGroup()
							.addComponent(loadUpdatesButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(stopUpdatesButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						)
						.addComponent(updateObserver.getProgressBar(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addGroup(layout.createSequentialGroup()
							.addComponent(updateObserver.getCountLabel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(updateObserver.getTimeLabel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(0, 0, Short.MAX_VALUE)
						)
				)
				.addContainerGap());
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup()
					.addGroup(layout.createSequentialGroup()
							.addComponent(updateMediaTableControl.getComponent(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
							.addGroup(layout.createParallelGroup()
								.addComponent(loadUpdatesButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(stopUpdatesButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							)
							.addGroup(layout.createParallelGroup()
									.addComponent(updateObserver.getCountLabel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(updateObserver.getTimeLabel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(updateObserver.getProgressBar(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						)
					.addComponent(savedMediaTableControl.getComponent(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				)
				.addContainerGap());
		
		return panel;
	}
	
	public static class GlassPanelListObserver implements IListLoadedObserver<Media> {
		
		protected final GlassTimerPanel _glassPanel;
		
		public GlassPanelListObserver(GlassTimerPanel glassPanel) {
			_glassPanel = glassPanel;
		}

		@Override
		public void notifyListLoaded(List<Media> list) {
			_glassPanel.stopTimer();
			_glassPanel.setVisible(false);
		}		
	}
}
