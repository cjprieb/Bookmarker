package com.purplecat.bookmarker.view.swing;

import java.awt.Dimension;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JPanel;

import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.commons.swing.renderer.ICellRendererFactory;

public class MainPanel {

	public static JPanel create(Controller ctrl, ICellRendererFactory rendererFactory, GlassTimerPanel timerGlassPane) {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(800, 600));
		
		MediaTableControl tableControl = new MediaTableControl(rendererFactory);
		ctrl.observeSavedMediaLoading(tableControl.getModel().getObserver());

		timerGlassPane.setCoverPanel(panel);
		
		ctrl.observeSavedMediaLoading(new GlassPanelListObserver(timerGlassPane));
		
		GroupLayout layout = new GroupLayout(panel);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(tableControl.getComponent(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addContainerGap());
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(tableControl.getComponent(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
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
