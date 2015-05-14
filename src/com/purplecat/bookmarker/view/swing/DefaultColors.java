package com.purplecat.bookmarker.view.swing;

import java.awt.Color;

public class DefaultColors {
	public static Color UPDATED_COLOR 		= new Color(0xff66ff00);
	public static Color MUTED_UPDATED_COLOR = new Color(0xff779977);
	public static Color READ_COLOR 			= new Color(0xff003399);
	public static Color HIGHTLIGHT_COLOR 	= new Color(0xffffcc00);
	
	public static java.awt.Color getAlphaColor(Color color, int alpha) {
		return(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
	}
}
