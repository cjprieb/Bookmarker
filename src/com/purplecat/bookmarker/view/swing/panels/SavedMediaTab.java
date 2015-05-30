package com.purplecat.bookmarker.view.swing.panels;

import javax.swing.GroupLayout;
import javax.swing.JPanel;

import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.view.swing.components.SavedMediaTableControl;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.swing.renderer.ICellRendererFactory;

public class SavedMediaTab {
	private JPanel _panel;
	
	public JPanel create(Controller controller, ICellRendererFactory rendererFactory, IResourceService resources) {
		
		SavedMediaTableControl savedMediaTableControl = new SavedMediaTableControl(rendererFactory, resources);
		controller.observeSavedMediaLoading(savedMediaTableControl.getModel().getObserver());
		controller.observeOnlineThreadLoading(savedMediaTableControl.getModel().getObserver());
		controller.observeSavedMediaUpdate(savedMediaTableControl.getModel().getObserver());

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
		
		return _panel;
	}
}
