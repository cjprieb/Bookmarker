package com.purplecat.bookmarker.view.swing.panels;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.purplecat.bookmarker.Resources;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.view.swing.actions.LoadUpdatesAction;
import com.purplecat.bookmarker.view.swing.actions.StopUpdatesAction;
import com.purplecat.bookmarker.view.swing.components.OnlineUpdateItemTableControl;
import com.purplecat.bookmarker.view.swing.observers.UpdateMediaObserverControl;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.swing.renderer.ICellRendererFactory;

public class OnlineUpdateTab {
	
	private JPanel _panel;
	
	public JPanel create(Controller controller, ICellRendererFactory rendererFactory, IResourceService resources) {		
		OnlineUpdateItemTableControl updateMediaTableControl = new OnlineUpdateItemTableControl(rendererFactory, controller, resources);
		controller.observeOnlineThreadLoading(updateMediaTableControl.getModel().getObserver());
		controller.observeSavedMediaUpdate(updateMediaTableControl.getModel().getObserver());
		
		UpdateMediaObserverControl updateObserver = new UpdateMediaObserverControl(resources);
		controller.observeOnlineThreadLoading(updateObserver);
		
		JButton loadUpdatesButton = new JButton(resources.getString(Resources.string.lblLoadUpdates));
		loadUpdatesButton.addActionListener(new LoadUpdatesAction(controller));
		
		JButton stopUpdatesButton = new JButton(resources.getString(Resources.string.lblStopUpdates));
		stopUpdatesButton.addActionListener(new StopUpdatesAction(controller));

		_panel = new JPanel();
		GroupLayout layout = new GroupLayout(_panel);
		_panel.setLayout(layout);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addContainerGap()
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
				.addContainerGap());
		
		return _panel;
	}
}
