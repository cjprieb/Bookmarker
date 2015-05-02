package com.purplecat.bookmarker.view.swing;

import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.commons.swing.TTable;

public class MainPanel {

	public static JPanel create(Controller ctrl) {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(500, 600));
		
		MediaTableModel tableModel = new MediaTableModel();
		ctrl.observeSavedMediaLoading(tableModel.getObserver());
		
		TTable<Media> table = new TTable<Media>();
		table.setTemplateModel(tableModel);
		
		JScrollPane scroll = new JScrollPane(table);
		
		GroupLayout layout = new GroupLayout(panel);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(scroll, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addContainerGap());
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(scroll, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addContainerGap());
		
		return panel;
	}
	
}
