package com.purplecat.bookmarker.view.swing.renderers;

import com.purplecat.bookmarks.model.Genre;
import com.purplecat.bookmarks.model.GenreList;
import com.purplecat.commons.swing.EnablableTableCellRenderer;

public class GenreRenderer extends EnablableTableCellRenderer {
	
//	public GenreRenderer() { 
//		super(); 
//		Font font = Font.decode("webdings");
//		if ( font != null ) {
//			this.setFont(font);
//			Log.logMessage(0, "genre renderer font - " + this.getFont().getName());
//		}
//		else {
//			Log.logMessage(0, "genre renderer font - could not create - " + this.getFont().getName());
//		}
//	}
	
	@Override
	public void setValue(Object value) {
		String s = "";
		if ( value != null ) {
			if ( value instanceof GenreList ) {
				for ( Genre genre : (GenreList)value ) {
					if ( s.length() > 0 ) { s += ", "; }
					s += genre.getName();				
				}
			}
//			else {
//				s = getGenreString((String)value);
//			}
		}
		setText(s);
	}
	
//	public static String getGenreString(String genreName) {
//		String s = "";
//		if ( genreName.equals("Shoujo") ) {
//			s = "女 ";
//		}
//		else if ( genreName.equals("Shounen") ) {
//			s = "男 ";
//		}
//		else if ( genreName.equals("Josei") ) {
//			s = "大女 ";
//		}
//		else if ( genreName.equals("Romance") ) {
//			s = "\u2764 ";
//		}
//		else if ( genreName.equals("School Life") ) {
//			s = "学 ";
//		}
//		else if ( genreName.equals("Action") || genreName.equals("Adventure") ) {
//			s = "\u2694 ";	//FAILED: \u2694
//		}
//		else if ( genreName.equals("Fantasy") ) {
//			s = "\u265E ";
//		}
//		else if ( genreName.equals("Historical") ) {
//			s = "\u265A ";
//		}
//		else if ( genreName.equals("Sci-fi") ) {
//			s = "\u2728 ";
//		}
//		else if ( genreName.equals("Sports") ) {
//			s = "\u26BD ";	//FAILED: \u26BD 
//		}
//		else if ( genreName.equals("Drama") ) {
//			s = "\u0001F3Ad ";	//FAILED: \u26BD 
//		}
//		return(s);
//	}

}
