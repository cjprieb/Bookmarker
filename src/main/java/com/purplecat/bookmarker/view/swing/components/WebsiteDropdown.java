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

public class WebsiteDropdown extends JComboBox<WebsiteInfo> {
	
	@Inject
	public WebsiteDropdown(IResourceService resources, IWebsiteList websites) {
		DefaultComboBoxModel<WebsiteInfo> model = new DefaultComboBoxModel<WebsiteInfo>();
		this.setModel(model);
		
		for ( IWebsiteParser website : websites.getList() ) {
			model.addElement(website.getInfo());
		}
		
		WebsiteComboBoxRenderer renderer = new WebsiteComboBoxRenderer(); 
		this.setRenderer(renderer);
	}
	
	public class WebsiteComboBoxRenderer implements ListCellRenderer<WebsiteInfo> {
		BasicComboBoxRenderer mRenderer = new BasicComboBoxRenderer();
		
		@Override
		public Component getListCellRendererComponent(JList<? extends WebsiteInfo> list, WebsiteInfo info, int index, boolean isSelected, boolean hasFocus) {
			return(mRenderer.getListCellRendererComponent(list, info._name, index, isSelected, hasFocus));
		}
	}

}
