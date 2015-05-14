package com.purplecat.commons.swing;

import javax.swing.ImageIcon;

public interface IImageRepository {
	ImageIcon getAppImage(String name);
	
	ImageIcon getImage(String name);

	ImageIcon getTimerIcon(int mFrameNumber);
}
