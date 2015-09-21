package com.purplecat.bookmarker.view.swing.components;

import java.awt.Component;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import com.google.inject.Inject;
import com.purplecat.bookmarker.models.WebsiteInfo;
import com.purplecat.bookmarker.services.websites.IWebsiteList;
import com.purplecat.bookmarker.services.websites.IWebsiteParser;
import com.purplecat.commons.IResourceService;

public class WebsiteDropdown extends JComboBox<String> {
	
	@Inject
	public WebsiteDropdown(IWebsiteList websites) {
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
		this.setModel(model);
		
		for ( IWebsiteParser website : websites.getList() ) {
			model.addElement(website.getName());
		}
	}

}
