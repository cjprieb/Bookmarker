package com.purplecat.bookmarker.view.swing.renderers;

import com.purplecat.commons.swing.EnablableTableCellRenderer;
import com.purplecat.bookmarks.model.Place;

public class EpisodeRenderer extends EnablableTableCellRenderer {
	
	public EpisodeRenderer() { 
		super(); 
	}
	
	@Override
	public void setValue(Object value) {
		if ( value == null ) {
			setText("");			
		}
		else {
			Place p = (Place)value;
			StringBuilder s = new StringBuilder(32);
			if ( !p.isEmpty() ) {
				if  ( p.getVolume() > 0 ) {
					s.append("S").append(p.getVolume()).append(' ');
				}
				s.append(String.format("E%02d", p.getChapter()));
				if ( p.isExtra() ) {
					s.append('*');
				}
			}
			setText(s.toString());
		}
	}
}
