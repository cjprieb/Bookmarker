package com.purplecat.bookmarker.view.swing.renderers;

import com.purplecat.bookmarker.extensions.FavoriteStateExt;
import com.purplecat.bookmarker.models.EFavoriteState;
import com.purplecat.commons.swing.EnablableTableCellRenderer;
import com.purplecat.commons.swing.IImageRepository;

public class FavoriteStateIconRenderer extends EnablableTableCellRenderer {
	
	final IImageRepository _repository;
	
	public FavoriteStateIconRenderer(IImageRepository repository) { 
		_repository = repository;
	}
	
	@Override
	public void setValue(Object value) {
		int key = 0;
		if ( value != null && value instanceof EFavoriteState ) {
			key = FavoriteStateExt.getIconKey((EFavoriteState)value);
		}
		
		if ( key > 0 ) {
			setIcon(_repository.getImage(key));
		}
		else {
			setIcon(null);
		}
	}
}
