package com.purplecat.bookmarker.view.swing;

import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.JPanel;

import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.commons.swing.renderer.ICellRendererFactory;

public class MainPanel {

	public static JPanel create(Controller ctrl, ICellRendererFactory factory) {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(800, 600));
		
		MediaTableControl tableControl = new MediaTableControl(factory);
		ctrl.observeSavedMediaLoading(tableControl.getModel().getObserver());
		
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
	
}
