package com.purplecat.bookmarker.view.swing.observers;

import java.awt.Container;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.purplecat.bookmarker.view.swing.MainPanel;
import com.purplecat.commons.swing.Toolbox;

@Singleton
public class SummaryHyperlinkObserver implements HyperlinkListener {
	
	@Inject MainPanel _hostPanel;
	@Inject Toolbox _toolbox;
	
	JTextArea _linkPanel;
	JWindow _window;
	
	public SummaryHyperlinkObserver() {
		create();
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
	
	private void showWindow() {
		_window.pack();
		
		Container topLevelFrame = _hostPanel._panel.getTopLevelAncestor();
		Point p = topLevelFrame.getLocation();		
		
		p.x += 10;
		p.y += topLevelFrame.getSize().height - _window.getSize().height - 10;
		
		_window.setLocation(p);
		_window.setVisible(true);
	}
	
	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if ( e.getEventType() == EventType.ACTIVATED ) {
			if ( e.getDescription().startsWith("KEY") ) {
				
			}
			else if ( e.getURL() != null ) {
				_toolbox.browse(e.getURL());
			}
		}
		else if ( e.getEventType() == EventType.ENTERED )  {
			_linkPanel.setText(e.getDescription());
			showWindow();
		}
		else if ( e.getEventType() == EventType.EXITED )  {
			_linkPanel.setText("");			
			_window.setVisible(false);
		}
	}
}
