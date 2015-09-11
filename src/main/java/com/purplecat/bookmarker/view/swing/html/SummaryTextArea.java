package com.purplecat.bookmarker.view.swing.html;

import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.text.DefaultCaret;

public class SummaryTextArea extends JTextArea {	
	public SummaryTextArea(String s) {		
		setup();
		this.setText(s);
	}
	
	public SummaryTextArea() {		
		setup();
	}
	
	private void setup() {
		DefaultCaret caret = (DefaultCaret)getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		this.setEditable(false);
		this.setFocusable(true);
		this.setWrapStyleWord(true);
		this.setLineWrap(true);
		this.setOpaque(false);
	}
	
	@Override
	public void setBorder(Border border) {}
}