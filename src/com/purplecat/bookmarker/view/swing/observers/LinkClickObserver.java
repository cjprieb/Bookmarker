package com.purplecat.bookmarker.view.swing.observers;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.JTextComponent;

import com.google.inject.Inject;
import com.purplecat.bookmarker.Resources;
import com.purplecat.bookmarker.view.swing.DefaultColors;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.swing.Toolbox;
import com.purplecat.commons.utils.StringUtils;

public class LinkClickObserver extends MouseAdapter {
	static String TAG = "LinkClickObserver";
	
	public static class Factory {		
		@Inject ILoggingService _logger;
		@Inject IResourceService _resources;
		@Inject Toolbox _toolbox;
		@Inject SummaryHyperlinkObserver _hyperlinkObserver;
		
		public Factory() {}
		
		public LinkClickObserver create(JTextComponent text) {
			return new LinkClickObserver(_logger, _resources, _toolbox, _hyperlinkObserver, text);
		}
	}
	
	//Injected through factory
	SummaryHyperlinkObserver _hyperlinkObserver;	
	ILoggingService _logger;
	IResourceService _resources;
	Toolbox _toolbox;
	
	//Publicly accessible components
	public String _url;
	public JTextComponent _textControl;
	public JPopupMenu _popupMenu;
	
	//Private actions
	AbstractAction _copyLinkURLAction;
	AbstractAction _openLinkAction;
	
	private LinkClickObserver(ILoggingService logger, IResourceService resources, Toolbox toolbox, SummaryHyperlinkObserver hyperlinkObs, JTextComponent text) {
		_logger = logger;
		_resources = resources;
		_toolbox = toolbox;
		_hyperlinkObserver = hyperlinkObs;
		
		_textControl = text;
		_textControl.addMouseListener(this);
		_textControl.addMouseMotionListener(this);
		_textControl.setForeground(DefaultColors.LINK_DEFAULT_COLOR);
		_textControl.setBackground(DefaultColors.LINK_BACKGROUND_DEFAULT_COLOR);
		
		_openLinkAction = new OpenLinkAction();
		_copyLinkURLAction = new CopyLinkURLAction();
		
		_popupMenu = new JPopupMenu();		
		_popupMenu.add(new JMenuItem(_openLinkAction));			
		_popupMenu.add(new JMenuItem(_copyLinkURLAction));
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		_textControl.setForeground(DefaultColors.LINK_HIGHIGHT_COLOR);
		_textControl.setBackground(DefaultColors.LINK_BACKGROUND_HIGHLIGHT_COLOR);
		_textControl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//		_logger.log(0, TAG, "mouse entered: \"" + mUrl + "\"");
		fireHyperlinkAction(EventType.ENTERED);
	}
	
	@Override		
	public void mouseExited(MouseEvent e) {
		_textControl.setForeground(DefaultColors.LINK_DEFAULT_COLOR);			
		_textControl.setBackground(DefaultColors.LINK_BACKGROUND_DEFAULT_COLOR);
		_textControl.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//		_logger.log(0, TAG, "mouse exited: \"" + mUrl + "\"");
		fireHyperlinkAction(EventType.EXITED);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		_logger.log(0, TAG, "mouse pressed: \"" + _url + "\"");
		if ( e.isPopupTrigger() ) {
			loadPopup(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		_logger.log(0, TAG, "mouse released: \"" + _url + "\"");
		if ( e.isPopupTrigger() ) {
			loadPopup(e);	
		}
		else {
			fireHyperlinkAction(EventType.ACTIVATED);				
		}
	}

	protected void loadPopup(MouseEvent e) {
		_popupMenu.show(_textControl, e.getPoint().x, e.getPoint().y);
	}	
	
	protected void fireHyperlinkAction(EventType type) {
		if ( !StringUtils.isNullOrEmpty(_url) ) {
			try {
				URL url = new URL(_url);
				HyperlinkEvent event = new HyperlinkEvent(_textControl, type, url, _url);
				_hyperlinkObserver.hyperlinkUpdate(event);
			} catch (Exception e) {
				_logger.error(TAG, "Could not convert \"" + _url + "\" to URL", e);
			}
		}
		else {
			_logger.error(TAG, "No \"" + _url + "\" found");
		}
	}
	
	class OpenLinkAction extends AbstractAction {
		public OpenLinkAction() {
			putValue(NAME, _resources.getString(Resources.string.lblOpenUrl));
			putValue(MNEMONIC_KEY, KeyEvent.VK_O);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			_toolbox.browse(_url);
		}
	}
	
	class CopyLinkURLAction extends AbstractAction {
		public CopyLinkURLAction() {
			putValue(NAME, _resources.getString(Resources.string.lblCopyUrl));
			putValue(MNEMONIC_KEY, KeyEvent.VK_C);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			_toolbox.copyTextToClipboard(_url);
		}
	}
}
