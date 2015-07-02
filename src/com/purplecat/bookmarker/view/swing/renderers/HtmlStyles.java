package com.purplecat.bookmarker.view.swing.renderers;

import java.util.HashMap;

import com.purplecat.commons.swing.AppUtils;
import com.purplecat.commons.styles.HtmlStyle;

public class HtmlStyles {
	public static final String PARAGRAPH_STYLE 	= "paragraph";
	public static final String TITLE_STYLE 		= "title";
	public static final String GENRE_LIST_STYLE = "genre-list";
	public static final String TIME_STYLE 		= "time";
	public static final String SUMMARY_STYLE 	= "summary";
	public static final String INDENT_STYLE 	= "indent";
	public static final String GENRE_STYLE 		= "genre";
	public static final String UPDATED_STYLE 	= "updated";
	public static final String READ_STYLE 	= "read";
	public static final String LINK_STYLE 		= "link";
	
	private static HashMap<String, HtmlStyle> mStyles = null;
	
	public static HtmlStyle get(String key) {
		if ( mStyles == null ) {
			mStyles = new HashMap<String, HtmlStyle>();
			createDefaultStyles();
		}
		return(mStyles.get(key));
	}
	
	private static void createDefaultStyles() {				
		HtmlStyle paragraph = new HtmlStyle();
		paragraph.put("margin", "0px 0px 6px 3px");
		if ( AppUtils.isMacOS() ) {
			paragraph.put("font-family", "sans-serif");
			paragraph.put("font-size", "1.1em");		
		}
		else {
			paragraph.put("font-family", "sans-serif");
			paragraph.put("font-size", "1em");
		}
		mStyles.put(PARAGRAPH_STYLE, paragraph);
			
		HtmlStyle style = new HtmlStyle();
		style.putAll(paragraph);
		style.put("font-size", "1.2em");
		style.put("font-weight", "bold");
		mStyles.put(TITLE_STYLE, style);

		style = new HtmlStyle();
		style.putAll(paragraph);
		mStyles.put(GENRE_LIST_STYLE, style);

		style = new HtmlStyle();
		style.putAll(paragraph);
		style.put("font-style", "italic");
		mStyles.put(TIME_STYLE, style);

		style = new HtmlStyle();
		style.putAll(paragraph);
		style.put("text-indent", "1em");			
		mStyles.put(SUMMARY_STYLE, style);

		style = new HtmlStyle();
		style.putAll(paragraph);
		style.put("margin", "0px 0px 6px 18px");	
		mStyles.put(INDENT_STYLE, style);
		
		style = new HtmlStyle();
		style.put("background-color", "#3399CC");	
		style.put("color", "#ffffff");	
		mStyles.put(GENRE_STYLE, style);

		style = new HtmlStyle();
		style.put("color", "green");
		mStyles.put(UPDATED_STYLE, style);

		style = new HtmlStyle();
		style.put("color", "navy");
		mStyles.put(READ_STYLE, style);
		
		style = new HtmlStyle();
		style.putAll(paragraph);
		mStyles.put(LINK_STYLE, style);
	}
}
