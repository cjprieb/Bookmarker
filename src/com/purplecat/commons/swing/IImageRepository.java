package com.purplecat.commons.swing;

import java.awt.Color;

import javax.swing.ImageIcon;

public interface IImageRepository {	
	ImageIcon getImage(String fileName);
	
	ImageIcon getImage(int imageKey);
	
	ImageIcon getScaledImage(int imageKey, double scale);
	
	ImageIcon getDockImage(int imageKey, Color c, String text); 

	ImageIcon getTimerIcon(String fileName, int mFrameNumber);
}
