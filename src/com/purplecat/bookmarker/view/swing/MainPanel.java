package com.purplecat.bookmarker.view.swing;

import java.awt.Dimension;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.view.swing.actions.LoadUpdatesAction;
import com.purplecat.bookmarker.view.swing.actions.StopUpdatesAction;
import com.purplecat.commons.swing.renderer.ICellRendererFactory;

public class MainPanel {

	public static JPanel create(Controller ctrl, ICellRendererFactory rendererFactory, GlassTimerPanel timerGlassPane) {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(1000, 600));
		
		MediaTableControl savedMediaTableControl = new MediaTableControl(rendererFactory);
		ctrl.observeSavedMediaLoading(savedMediaTableControl.getModel().getObserver());
		ctrl.observeOnlineThreadLoading(savedMediaTableControl.getModel().getObserver());
		ctrl.observeSavedMediaUpdate(savedMediaTableControl.getModel().getObserver());
		
		UpdateMediaTableControl updateMediaTableControl = new UpdateMediaTableControl(rendererFactory, ctrl);
		ctrl.observeOnlineThreadLoading(updateMediaTableControl.getModel().getObserver());
		ctrl.observeSavedMediaUpdate(updateMediaTableControl.getModel().getObserver());
		
		JButton loadUpdatesButton = new JButton("Load Updates");
		loadUpdatesButton.addActionListener(new LoadUpdatesAction(ctrl));
		
		JButton stopUpdatesButton = new JButton("Stop Updates");
		stopUpdatesButton.addActionListener(new StopUpdatesAction(ctrl));

		timerGlassPane.setCoverPanel(panel);
		
		ctrl.observeSavedMediaLoading(new GlassPanelListObserver(timerGlassPane));
		
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
							.addGap(0, 0, Short.MAX_VALUE)
						)
				)
				.addContainerGap());
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup()
					.addComponent(savedMediaTableControl.getComponent(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addGroup(layout.createSequentialGroup()
						.addComponent(updateMediaTableControl.getComponent(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addGroup(layout.createParallelGroup()
							.addComponent(loadUpdatesButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(stopUpdatesButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						)
					)
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
