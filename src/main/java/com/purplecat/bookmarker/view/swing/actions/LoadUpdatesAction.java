package com.purplecat.bookmarker.view.swing.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.purplecat.bookmarker.Resources;
import com.purplecat.bookmarker.view.swing.panels.OnlineUpdateTab;
import com.purplecat.commons.IResourceService;

public class LoadUpdatesAction extends AbstractAction {
	private final OnlineUpdateTab _tab;
	
	public LoadUpdatesAction(IResourceService resources, OnlineUpdateTab tab) {
		_tab = tab;
		this.putValue(Action.NAME, resources.getString(Resources.string.lblRefresh));
	}
	
	@Override 
	public void actionPerformed(ActionEvent e) {
		_tab.callLoadUpdates();		
	}
}