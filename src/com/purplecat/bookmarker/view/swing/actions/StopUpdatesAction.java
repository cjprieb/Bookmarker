package com.purplecat.bookmarker.view.swing.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.purplecat.bookmarker.controller.Controller;

public class StopUpdatesAction implements ActionListener {
	Controller _controller;
	
	public StopUpdatesAction(Controller ctrl) {
		_controller = ctrl;
	}
	
	@Override 
	public void actionPerformed(ActionEvent e) {
		_controller.stopUpdates();		
	}
}