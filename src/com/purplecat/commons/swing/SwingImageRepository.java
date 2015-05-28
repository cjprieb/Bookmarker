package com.purplecat.commons.swing;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.util.HashMap;

import javax.swing.ImageIcon;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.logs.ILoggingService;

@Singleton
public class SwingImageRepository implements IImageRepository {
	final static String TAG = "AbstractImageRepository";
	
	final ILoggingService _logger;
	final IResourceService _resources;
	final String _projectName;
	
	HashMap<String, ImageIcon> _map;
	ImageIcon[][] _timerIcons = new ImageIcon[8][4];
	
	@Inject
	public SwingImageRepository(ILoggingService logger, IResourceService resources, @Named("Project Path") String projectName) {
		_logger = logger;
		_resources = resources;
		_projectName = projectName;
	}

	@Override
	public ImageIcon getImage(int key) {
		return getImageResource(_resources.getImageFile(key));
	}

	/**
	 * 
	 * @param fileName - expected to have extension
	 * @return
	 */
	@Override
	public ImageIcon getImage(String fileName) {
		return getImageResource(fileName);
	}

	@Override
	public ImageIcon getTimerIcon(String fileName, int frameNumber) {
		int timer = (int)(frameNumber % 31) + 1;
		int x = (timer % 8);
		int y = (timer / 8);
		
		return(getTimerIcon(fileName, x, y));		
	}
	
	/**
	 * 
	 * @param fileName - expected to have extension
	 * @return
	 */
	protected ImageIcon getImageResource(String fileName) {
		ImageIcon image = null;
		
		if ( _map == null ) {
			_map = new HashMap<String, ImageIcon>();
		}
		
		if ( _map.containsKey(fileName) ) {
			image = _map.get(fileName);
		}
		
		if ( image == null ) {
			image = createImageIcon(fileName);
			if ( image != null ) {
				_map.put(fileName, image);
			} else if ( fileName.startsWith(AppIcons.SMALL_KEY_WORD) ) {
				String baseKey = fileName.substring(AppIcons.SMALL_KEY_WORD.length()); 
				image = getImageResource(baseKey);
				if ( image != null ) {
					image = scaleImageIcon(image, AppIcons.SMALL_DOCK_IMAGE_SCALE);
					_map.put(fileName, image);
				}
			} else if ( fileName.startsWith(AppIcons.LARGE_KEY_WORD) ) {
				String baseKey = fileName.substring(AppIcons.LARGE_KEY_WORD.length()); 
				image = getImageResource(baseKey);
				if ( image != null ) {
					image = scaleImageIcon(image, AppIcons.LARGE_DOCK_IMAGE_SCALE);
					_map.put(fileName, image);
				}		
			}
		}
		
		return(image);
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	private ImageIcon createImageIcon(String fileName) {	
		if ( _projectName != null ) {
			String fullPath = _projectName + "resources/icons/" + fileName;
			try {
				java.net.URL imgURL = getClass().getResource(fullPath);
				if (imgURL != null) {
					return (new ImageIcon(imgURL, fileName));
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
	
	private ImageIcon getTimerIcon(String fileName, int x, int y) {
		int width = 22;
		int height = 22;
		
		if ( _timerIcons[x][y] == null ) {
			//Initialize Timer Icons
			int adjx = x * width;
			int adjy = y * height;
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics graphics = image.getGraphics();			
						
			graphics.drawImage(getImage(fileName).getImage(), 
					0, 0, width, height,			//destination rectangle
					adjx, adjy, adjx+width, adjy+height,	//source rectangle
					null, null);	
			_timerIcons[x][y] = new ImageIcon(image);
		}
		
		return(_timerIcons[x][y]);
	}

}
