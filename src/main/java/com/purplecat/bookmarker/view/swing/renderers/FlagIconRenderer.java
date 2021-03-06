package com.purplecat.bookmarker.view.swing.renderers;

import com.purplecat.bookmarker.Resources;
import com.purplecat.commons.swing.EnablableTableCellRenderer;
import com.purplecat.commons.swing.IImageRepository;

public class FlagIconRenderer extends EnablableTableCellRenderer {
	
	final IImageRepository _repository;
	
	public FlagIconRenderer(IImageRepository repository) { 
		_repository = repository;
	}
	
	@Override
	public void setValue(Object value) {
		if ( value != null && value instanceof Boolean && ((Boolean)value) ) {
			setIcon(_repository.getImage(Resources.image.imgFlag));
		}
		else {
			setIcon(null);
		}
	}
}
