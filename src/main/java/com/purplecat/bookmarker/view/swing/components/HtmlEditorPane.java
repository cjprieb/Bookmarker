package com.purplecat.bookmarker.view.swing.components;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SizeRequirements;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Element;
import javax.swing.text.ParagraphView;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLEditorKit.HTMLFactory;

import com.google.inject.Inject;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.swing.Toolbox;

public class HtmlEditorPane extends JEditorPane {
	static String TAG = "HtmlEditorPane";
	
	private CopyEditorTextAction mCopyLinkURLAction 	= new CopyEditorTextAction();
	private CopyEditorTextAction mCopyHTMLAction 		= new CopyEditorTextAction();
	
	@Inject ILoggingService _logger;
	@Inject Toolbox _toolbox;
	
	public HtmlEditorPane() {		
		this.setEditable(false);
		this.setFocusable(true);
		
		//MyCaret() is to prevent a JScrollPane of 'bouncing' to the EditorPane when 
		// setText() is called; 
		// see: http://stackoverflow.com/questions/11291353/jscrollpane-scrollbar-jumps-down-on-jeditorpane-settext?rq=1
		this.setCaret(new MyCaret());

//		if ( setBackground ) {
//			Color background = com.purplecat.commons.swing.Toolbox.COLOR_PANEL_BACKGROUND;
//			UIDefaults defaults = new UIDefaults();
//			defaults.put("EditorPane[Enabled].backgroundPainter", background);//this.getBackground()
//			this.putClientProperty("Nimbus.Overrides", defaults);
//			this.putClientProperty("Nimbus.Overrides.InheritDefaults", true);
//			this.setBackground(background);
//		}
		
		this.setContentType("text/html");
		this.setEditorKit(new WrapEditorKit());
		
		this.setText("<html>");
		
		PopupEnabler popup = new PopupEnabler();
		this.addMouseListener(popup);
		this.addHyperlinkListener(popup);
	}
	
	@Override
	public void setText(String text) {
		StringBuffer buf = new StringBuffer(500);
		for ( int i = 0; i < text.length(); i++ ) {
			char c = text.charAt(i);
			if ( c > 128 ) { 
				buf.append("&#").append((int)c).append(";");
			}
			else {
				buf.append(c);
			}
		}
		super.setText(buf.toString());
	}
	
	class MyCaret extends DefaultCaret {
		@Override
		protected void adjustVisibility(Rectangle nloc) {}
	}
	
	//WrapEditorKit (and following) allow for 
	// better wrapping with <p> tags when displaying HTML in an EditorView.
	class WrapEditorKit extends HTMLEditorKit {
		ViewFactory mDefaultFactory = new WrapColumnFactory();
		@Override
		public ViewFactory getViewFactory() {
			return(mDefaultFactory);
		}
	}

	class WrapColumnFactory extends HTMLFactory {
		@Override
		public View create(Element elem) {
			String kind = elem.getName();
			if ( kind != null && kind.equalsIgnoreCase("p") ) {
				return(new WrapLineParagraphView(elem));
			}			
			return( super.create(elem) );
		}
	}
	
	class WrapLineParagraphView extends ParagraphView {
		public WrapLineParagraphView(Element elem) { super(elem); }

		/*
		 * Code adapted from  http://java-sl.com/tip_html_letter_wrap.html
		 * 	which was a link from 
		 * 	http://stackoverflow.com/questions/7811666/enabling-word-wrap-in-a-jtextpane-with-htmldocument
		 */		
		@Override
        protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) { 
            if (r == null) { 
                  r = new SizeRequirements(); 
            } 
            float pref = layoutPool.getPreferredSpan(axis); 
            float min = layoutPool.getMinimumSpan(axis); 
            // Don't include insets, Box.getXXXSpan will include them. 
              r.minimum = (int)min; 
              r.preferred = Math.max(r.minimum, (int) pref); 
              r.maximum = Integer.MAX_VALUE; 
              r.alignment = 0.5f; 
            return r; 
       } 
	}
	
	class PopupEnabler extends MouseAdapter implements HyperlinkListener {
		
		URL			mURL = null;
		JPopupMenu 	mPopup;

		@Override
		public void mousePressed(MouseEvent e) {
			if ( e.isPopupTrigger() ) {
				loadPopup(e);
			}			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if ( e.isPopupTrigger() ) {
				loadPopup(e);
			}
		}

		@Override
		public void hyperlinkUpdate(HyperlinkEvent e) {
			if ( e.getEventType() == EventType.ACTIVATED ) {
			}
			else if ( e.getEventType() == EventType.ENTERED )  {
				mURL = e.getURL();
			}
			else if ( e.getEventType() == EventType.EXITED )  {
				mURL = null;
			}
		}
		
		private void loadPopup(MouseEvent e) {
			if ( mPopup == null ) {
				mPopup = new JPopupMenu();
						
				mPopup.add(new JMenuItem(LogHTMLAction));

				mCopyHTMLAction.putValue(Action.NAME, "Copy HTML");
				mCopyHTMLAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);				
				mPopup.add(new JMenuItem(mCopyHTMLAction));

				mCopyLinkURLAction.putValue(Action.NAME, "Copy link url");
				mCopyLinkURLAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_P);				
				mPopup.add(new JMenuItem(mCopyLinkURLAction));
			}
			mCopyLinkURLAction.setText( mURL != null ? mURL.toString() : null );
			mCopyLinkURLAction.setEnabled(mURL != null);
			
			mCopyHTMLAction.setText(getText());
			
			mPopup.show(HtmlEditorPane.this, e.getPoint().x, e.getPoint().y);
		}	
	}
	
	private Action LogHTMLAction = new AbstractAction() {
		{
			this.putValue(Action.NAME, "Log HTML");
			this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_L);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			_logger.log(0, TAG, getText());
		}			
	};
	
	private class CopyEditorTextAction extends AbstractAction {
		private String mText;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			_toolbox.copyTextToClipboard(mText);
		}
		
		public void setText(String s) {
			mText = s;
		}
	}
}
