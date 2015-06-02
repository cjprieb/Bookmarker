package com.purplecat.bookmarker.view.swing.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.purplecat.commons.swing.Toolbox;

@Singleton
public class SummarySidebar {
	private JPanel _panel;
	private JScrollPane _scrollPane;
	
	@Inject Toolbox _toolbox;
	
	public JPanel create() {
		_panel = new JPanel();
		_panel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
		_panel.setPreferredSize(new Dimension(300, 400));	

		_scrollPane = new JScrollPane();
		_scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		_scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		JPanel editPanel = new JPanel();
		editPanel.setLayout(new BorderLayout());		
		{
			GroupLayout layout = new GroupLayout(_panel);
			_panel.setLayout(layout);
			layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(_scrollPane, GroupLayout.PREFERRED_SIZE, 100, Short.MAX_VALUE)
				.addComponent(editPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE));
			layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(_scrollPane, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 100, Short.MAX_VALUE)
				.addComponent(editPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE));
		}
		return _panel;
	}
	
	public void setSummaryView(JPanel panel) {
		if ( panel != null ) {
			_scrollPane.setViewportView(panel);
		}
	}
}
