package com.purplecat.bookmarker.view.swing.html;

import java.awt.Component;

import javax.swing.text.JTextComponent;

import com.purplecat.bookmarker.view.swing.components.HtmlEditorPane;

public class SummaryTextRow extends SummaryRow {
	public JTextComponent _textArea;
	
	public SummaryTextRow(String label) {
		super(label);
		_textArea = new SummaryTextArea();
	}
	
	public SummaryTextRow(String label, boolean useHtmlEditor) {
		super(label);
		if ( useHtmlEditor ) {
			_textArea = new HtmlEditorPane();//background == false
			_textArea.setFocusable(true);
		}
		else {
			_textArea = new SummaryTextArea();
		}
	}
	
	@Override
	public Component getDataComponent() {
		return _textArea;
	}

	public void setText(String text) {
		_textArea.setText(text);
	}
}
