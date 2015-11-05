package com.purplecat.bookmarker.view.swing.panels;

import com.google.inject.Inject;
import com.purplecat.bookmarker.Resources;
import com.purplecat.commons.IResourceService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Crystal on 9/20/15.
 */
public class EditSettingsDialog extends JDialog {

	@Inject
	IResourceService _resources;

	public EditSettingsDialog() {

	}

	private void init() {
		JButton okBtn = new JButton(_resources.getString(Resources.string.lblOkay));
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}

}
