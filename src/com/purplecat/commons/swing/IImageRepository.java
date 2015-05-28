package com.purplecat.commons.swing;

import javax.swing.ImageIcon;

public interface IImageRepository {	
	ImageIcon getImage(String fileName);
	
	ImageIcon getImage(int imageKey);

	ImageIcon getTimerIcon(String fileName, int mFrameNumber);
}
