package com.purplecat.bookmarker.view.swing.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.purplecat.bookmarker.Resources;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.commons.IResourceService;

public class StopUpdatesAction extends AbstractAction {
	Controller _controller;
	
	public StopUpdatesAction(Controller ctrl, IResourceService resources) {
		_controller = ctrl;
		this.putValue(Action.NAME, resources.getString(Resources.string.lblStop));
	}
	
	@Override 
	public void actionPerformed(ActionEvent e) {
		_controller.stopUpdates();		
	}
}