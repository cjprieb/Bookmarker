package com.purplecat.commons.swing;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.util.HashMap;

import javax.swing.ImageIcon;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.purplecat.commons.logs.ILoggingService;

@Singleton
public abstract class AbstractImageRepository implements IImageRepository {
	final static String TAG = "AbstractImageRepository";
	
	final ILoggingService _logger;
	
	HashMap<String, ImageIcon> _map;
	ImageIcon[][] _timerIcons = new ImageIcon[8][4];
	
	@Inject
	public AbstractImageRepository(ILoggingService logger) {
		_logger = logger;
	}
	
	@Override
	public ImageIcon getAppImage(String name) {
		return getImageResource("commons", name, "png");
	}

	@Override
	public ImageIcon getTimerIcon(int frameNumber) {
		int timer = (int)(frameNumber % 31) + 1;
		int x = (timer % 8);
		int y = (timer / 8);
		
		return(getTimerIcon(x, y));		
	}
	
	protected ImageIcon getImageResource(String basePath, String key, String ext) {
		ImageIcon image = null;
		
		if ( _map == null ) {
			_map = new HashMap<String, ImageIcon>();
		}
		
		if ( _map.containsKey(key) ) {
			image = _map.get(key);
		}
		
		if ( image == null ) {
			image = createImageIcon(basePath, String.format("%s.%s", key, ext), key);
			if ( image != null ) {
				_map.put(key, image);
			} else if ( key.startsWith(AppIcons.SMALL_KEY_WORD) ) {
				String baseKey = key.substring(AppIcons.SMALL_KEY_WORD.length()); 
				image = getImageResource(basePath, baseKey, ext);
				if ( image != null ) {
					image = scaleImageIcon(image, AppIcons.SMALL_DOCK_IMAGE_SCALE);
					_map.put(key, image);
				}
			} else if ( key.startsWith(AppIcons.LARGE_KEY_WORD) ) {
				String baseKey = key.substring(AppIcons.LARGE_KEY_WORD.length()); 
				image = getImageResource(basePath, baseKey, ext);
				if ( image != null ) {
					image = scaleImageIcon(image, AppIcons.LARGE_DOCK_IMAGE_SCALE);
					_map.put(key, image);
				}		
			}
		}
		
		return(image);
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	private ImageIcon createImageIcon(String appName, String path, String description) {	
		if ( path != null ) {
			String fullPath = String.format("/com/purplecat/%s/resources/icons/%s", appName, path);
			try {
				java.net.URL imgURL = getClass().getResource(fullPath);
				if (imgURL != null) {
					return (new ImageIcon(imgURL, description));
				} else {
					throw (new MalformedURLException());
				}
			} catch (MalformedURLException e) {
				_logger.error(TAG, "MalformedURLException: Couldn't find image \"" + fullPath + "\"", e);
			}
		}
		return null;
	}
	
	private ImageIcon scaleImageIcon(ImageIcon icon, double scale) {
		double width = icon.getIconWidth() * scale;
		double height = icon.getIconHeight() * scale;
		Image image = icon.getImage().getScaledInstance((int)width, (int)height, Image.SCALE_SMOOTH);
		return(new ImageIcon(image));
	}
	
	private ImageIcon getTimerIcon(int x, int y) {
		int width = 22;
		int height = 22;
		
		if ( _timerIcons[x][y] == null ) {
			//Initialize Timer Icons
			int adjx = x * width;
			int adjy = y * height;
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics graphics = image.getGraphics();			
						
			graphics.drawImage(getAppImage(AppIcons.appProcessWorkingId).getImage(), 
					0, 0, width, height,			//destination rectangle
					adjx, adjy, adjx+width, adjy+height,	//source rectangle
					null, null);	
			_timerIcons[x][y] = new ImageIcon(image);
		}
		
		return(_timerIcons[x][y]);
	}

}
