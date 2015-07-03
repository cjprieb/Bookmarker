package com.purplecat.bookmarker.view.swing.observers;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

import com.google.inject.Singleton;
import com.purplecat.bookmarker.view.swing.panels.OverlayWindow;
import com.purplecat.commons.swing.Toolbox;

@Singleton
public class SummaryHyperlinkObserver implements HyperlinkListener {
	private final Toolbox _toolbox;	
	private final OverlayWindow _window;
	
	public SummaryHyperlinkObserver(Toolbox toolbox, OverlayWindow window) {
		_toolbox = toolbox;
		_window = window;
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
			_window.setText(e.getDescription());
			_window.showWindow();
		}
		else if ( e.getEventType() == EventType.EXITED )  {
			_window.setText("");			
			_window.hideWindow();
		}
	}
}
