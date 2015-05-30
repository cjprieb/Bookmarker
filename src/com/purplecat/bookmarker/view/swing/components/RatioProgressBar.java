package com.purplecat.bookmarker.view.swing.components;

import javax.swing.JProgressBar;

import com.purplecat.bookmarker.Resources;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.utils.StringUtils;


public class RatioProgressBar extends JProgressBar {
	final IResourceService _resources;	
	
	public RatioProgressBar(IResourceService resources) {
		_resources = resources;
		setStringPainted(true);		
		setString("");		
	}
	
	@Override
	public String getString() {
		String s = "";
		if ( progressString != null && model != null && model.getMaximum() > 0 ) {
			if ( StringUtils.isNullOrEmpty(progressString) ) {
				if ( model.getValue() >= model.getMaximum() ) {
					s = String.format("%s - %s", progressString, _resources.getString(Resources.string.lblDone));
				}
				else {
					s = String.format("%s - %d / %d", progressString, model.getValue(), model.getMaximum());
				}
			}
			else {
				if ( model.getValue() >= model.getMaximum() ) {
					s = _resources.getString(Resources.string.lblDone);
				}
				else {
					s = String.format("%d / %d", model.getValue(), model.getMaximum());
				}
			}
		}
		else if ( progressString != null ) {
			s = progressString;			
		}
		return(s);
	}
}
