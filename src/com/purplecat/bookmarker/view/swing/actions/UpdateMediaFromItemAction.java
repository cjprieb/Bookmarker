package com.purplecat.bookmarker.view.swing.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.commons.swing.TTable;

public class UpdateMediaFromItemAction extends AbstractAction {
	Controller _controller;
	TTable<OnlineMediaItem> _table;
	
	public UpdateMediaFromItemAction(TTable<OnlineMediaItem> table, Controller ctrl) {
		_table = table;
		_controller = ctrl;
		this.putValue(NAME, "Save bookmark");
	}
	
	@Override 
	public void actionPerformed(ActionEvent e) {
		if ( _table.getSelectedItem() != null && _table.getSelectedItem()._id > 0 ) {
			_controller.updateMediaFrom(_table.getSelectedItem());
		}
	}
}