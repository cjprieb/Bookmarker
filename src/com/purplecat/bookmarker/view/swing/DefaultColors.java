package com.purplecat.bookmarker.view.swing;

import java.awt.Color;

public class DefaultColors {
	public static Color UPDATED_COLOR 					= new Color(0x66, 0xFF, 0x00);
	public static Color MUTED_UPDATED_COLOR 			= new Color(0x77, 0x99, 0x77);
	public static Color READ_COLOR 						= new Color(0x00, 0x33, 0x99);
	public static Color HIGHTLIGHT_COLOR 				= new Color(0xFF, 0xCC, 0x00);	
	public static Color UPDATE_FOREGROUND_COLOR			= Color.GREEN.darker(); 
	public static Color READ_FOREGROUND_COLOR			= Color.BLUE.darker();
	public static Color LINK_HIGHIGHT_COLOR 			= new Color(0x35, 0x86, 0xD4);
	public static Color LINK_BACKGROUND_HIGHLIGHT_COLOR = new Color(0xCC, 0xCC, 0xCC);
	public static Color LINK_DEFAULT_COLOR 				= new Color(0x00, 0x56, 0xA0);
	public static Color LINK_BACKGROUND_DEFAULT_COLOR 	= new Color(0xFF, 0xFF, 0xFF);
	public static Color TAG_LIST_COLOR					= new Color(0x60, 0x60, 0x60);
	public static Color LABEL_COLOR						= new Color(0x60, 0x60, 0x60);
	public static Color PANEL_BACKGROUND_COLOR			= new Color(0xFF, 0xFF, 0xFF);
	
	public static java.awt.Color getAlphaColor(Color color, int alpha) {
		return(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
	}
}
