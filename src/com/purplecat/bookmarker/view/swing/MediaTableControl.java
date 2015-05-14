package com.purplecat.bookmarker.view.swing;

import java.awt.Component;

import javax.swing.JScrollPane;

import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.view.swing.renderers.DataFields;
import com.purplecat.commons.TTableColumn;
import com.purplecat.commons.swing.TTable;
import com.purplecat.commons.swing.renderer.ICellRendererFactory;

public class MediaTableControl {
	private final MediaTableModel _model;
	private final TTable<Media> _table;
	private final JScrollPane _scroll;
	
	public MediaTableControl(ICellRendererFactory factory) {
		_model = new MediaTableModel(new TTableColumn[] {
				DataFields.TITLE_COL,
				DataFields.PLACE_COL,
				DataFields.DATE_COL
		});		
		_table = new TTable<Media>(factory);
		_table.setTemplateModel(_model);		
		_scroll = new JScrollPane(_table);
	}
	
	public MediaTableModel getModel() {
		return _model;
	}
	
	public Component getComponent() {
		return _scroll;
	}

}
