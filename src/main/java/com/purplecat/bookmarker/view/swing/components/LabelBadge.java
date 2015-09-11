package com.purplecat.bookmarker.view.swing.components;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JLabel;

public class LabelBadge extends JLabel {
	
//	private static boolean sgPrinted = false;
	
	private int mColumns = 1;
	private int mMargin = 0;
	
	public LabelBadge(int columns) {
		mColumns = columns;
		this.setHorizontalAlignment(CENTER);
		this.setVerticalAlignment(CENTER);
	}
	
	public LabelBadge() {
		this(1);
	}
	
	public void setColumns(int columns) {
		mColumns = columns;
	}
	
	public int getColumns() {
		return(mColumns);
	}
	
	public void setMargin(int margin) {
		mMargin = margin;
	}
	
	public int getMargin() {
		return(mMargin);
	}
	
	@Override
	public Dimension getPreferredSize() {		
		Dimension d = super.getPreferredSize();		
		
		if ( getGraphics() != null ) {
			FontMetrics metrics = getGraphics().getFontMetrics(getFont());			
			if ( metrics != null ) {
				int colWidth = metrics.charWidth('m');
				d.width = (colWidth * mColumns) + (2 * mMargin);
				d.height = d.width;
//				if ( !sgPrinted ) {
//					Log.logMessage(0, "updatePreferredSize - " + getText());
//					Log.logMessage(1, "'m' width - " + metrics.charWidth('m'));
//					Log.logMessage(1, "'0' width - " + metrics.charWidth('0'));
//					Log.logMessage(1, "\u304E width - " + metrics.charWidth('\u304E'));
//					Log.logMessage(1, "猫 width - " + metrics.charWidth('猫'));
//					Log.logMessage(1, "\u067B width - " + metrics.charWidth('\u067B'));
//					sgPrinted = true;
//				}
			}			
		}
		
		return(d);
	}
	
	@Override
	public void paint(Graphics g) {
		Dimension size = getSize();
		g.setColor(getBackground());
		g.fillOval(0, 0, size.width, size.height);		
		super.paint(g);		
	}

}
