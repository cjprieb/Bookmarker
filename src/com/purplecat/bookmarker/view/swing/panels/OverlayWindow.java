package com.purplecat.bookmarker.view.swing.panels;

import java.awt.Container;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JWindow;

public class OverlayWindow {	
	private JPanel _panel;
	private JTextArea _linkPanel;
	private JWindow _window;
	
	public OverlayWindow(JPanel panel) {
		_panel = panel;
	}
	
	public void create() {		
		_linkPanel = new JTextArea(1, 40);
		_linkPanel.setBackground(com.purplecat.commons.swing.Toolbox.COLOR_DUSKY_BLUE);
		_linkPanel.setLineWrap(true);
		_linkPanel.setEditable(false);
		_linkPanel.setBorder(BorderFactory.createEtchedBorder());
		
		_window	= new JWindow();
		_window.getContentPane().add(_linkPanel);		
	}
	
	public void showWindow() {
		_window.pack();
		
		if ( _panel != null ) {
			Container topLevelFrame = _panel.getTopLevelAncestor();
			Point p = topLevelFrame.getLocation();		
			
			p.x += 10;
			p.y += topLevelFrame.getSize().height - _window.getSize().height - 10;
			
			_window.setLocation(p);
			_window.setVisible(true);
		}
	}
	
	public void hideWindow() {
		_window.setVisible(false);
	}
	
	public void setText(String s) {
		_linkPanel.setText(s);
	}
}
