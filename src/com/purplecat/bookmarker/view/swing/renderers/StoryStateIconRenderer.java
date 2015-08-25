package com.purplecat.bookmarker.view.swing.renderers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import com.purplecat.bookmarker.Resources;
import com.purplecat.bookmarker.controller.FolderCache;
import com.purplecat.bookmarker.extensions.StoryStateExt;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.StoryStateModel;
import com.purplecat.bookmarker.view.swing.DefaultColors;
import com.purplecat.commons.swing.EnablableTableCellRenderer;
import com.purplecat.commons.swing.IImageRepository;

public class StoryStateIconRenderer extends EnablableTableCellRenderer {	
	
	final IImageRepository _repository;
	final FolderCache _folders;
	
	public StoryStateIconRenderer(IImageRepository repository, FolderCache folders) {
		_repository = repository;		
		_folders = folders;
	}
	
	@Override
	public void setValue(Object value) {
		StoryStateModel image = null;
		if ( value != null ) {		
			if ( value instanceof Media ) {
				Media media = (Media)value;
				image = StoryStateExt.getView(media, _folders.getById(media._folderId));
			}			
			else if ( value instanceof OnlineMediaItem ) {
				OnlineMediaItem onlineItem = (OnlineMediaItem)value;
				image = StoryStateExt.getView(onlineItem, _folders.getById(onlineItem._folderId));
			}	
			/*else if ( value instanceof ScheduledItem ) {
				image = StoryStateFormat.getStoryStateImage((ScheduledItem)value);
			}*/
		}
		
		if ( image != null && image._imageKey > 0 ) {
			setIcon(createUpdateImage(image));
		}
		else if ( value instanceof ImageIcon ) {
			this.setIcon((ImageIcon)value);
		}
		else {
			setIcon(null);
		}
	}
	
	/*public static class UpdateImage {
		StoryStateImage mKey;
		ImageIcon mIcon;
		
		public UpdateImage(StoryStateImage imageKey, ImageIcon icon) {
			mKey = imageKey;
			mIcon = icon;
		}
		
		public boolean matches(StoryStateImage imageKey) {
			return(mKey.equals(imageKey));
		}
	}
	
	private static List<UpdateImage> UPDATE_IMAGES = new LinkedList<UpdateImage>();

	public static ImageIcon getUpdateIcon(StoryStateImage storyState) {		
		ImageIcon image = null;
		for ( UpdateImage update : UPDATE_IMAGES ) {
			if ( update.matches(storyState) ) {
				image = update.mIcon;
				break;
			}
		}
		if ( image == null ) {
			image = createUpdateImage(storyState);
		}
		return(image);
	}*/
	
	private ImageIcon createUpdateImage(StoryStateModel storyState) {
		Color updateColor = null;
		//Color bgColor = null;
		
		switch (storyState._updateMode) {
			case StoryStateModel.FULL_UPDATE: 
				updateColor = DefaultColors.UPDATED_COLOR;
				break;
				
			case StoryStateModel.MUTED_UPDATE:
				updateColor = DefaultColors.MUTED_UPDATED_COLOR;
				break;
		}
		
		ImageIcon icon = _repository.getImage(storyState._imageKey);
		
		if ( storyState._imageKey == Resources.image.imgClosedBowRedId && updateColor != null ) {
			icon = _repository.getImage(Resources.image.imgClosedBowGreenId);
		}
		else if ( icon != null && updateColor != null ) {
			int width 	= icon.getIconWidth();
			int height	= icon.getIconHeight();
			int xOffset	= 1;
			int yOffset	= height-5;
			
			BufferedImage 	image 		= new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics 		graphics 	= image.getGraphics();
		
			graphics.drawImage(icon.getImage(), 0, 0, null);
			
			graphics.setColor(DefaultColors.getAlphaColor(updateColor, 255));
			xOffset = 2;
			graphics.drawLine(xOffset, yOffset, width-xOffset, yOffset);
			
			graphics.setColor(DefaultColors.getAlphaColor(updateColor, 200));
			graphics.drawLine(xOffset, yOffset+1, width-xOffset, yOffset+1);
			graphics.drawLine(xOffset, yOffset-1, width-xOffset, yOffset-1);
			xOffset = 1;
			graphics.drawLine(xOffset, yOffset, xOffset+1, yOffset);
			graphics.drawLine(width-xOffset, yOffset, width-xOffset-1, yOffset);
			
			graphics.setColor(DefaultColors.getAlphaColor(updateColor, 145));
			graphics.drawLine(xOffset, 			yOffset+1, 	xOffset+1, 			yOffset+1);
			graphics.drawLine(width-xOffset, 	yOffset+1, 	width-xOffset-1, 	yOffset+1);
			graphics.drawLine(width-xOffset, 	yOffset-1, 	width-xOffset-1, 	yOffset-1);
			graphics.drawLine(xOffset, 			yOffset-1, 	xOffset+1, 			yOffset-1);
			
			icon = new ImageIcon(image, "update_" + storyState._imageKey);
		}
		
		/*if ( icon != null && bgColor != null ) {
			icon = _repository.addBackground(icon, 0, bgColor); //0 == no type set, so a rectangular border 
		}*/
		
		return(icon);
	}
}
