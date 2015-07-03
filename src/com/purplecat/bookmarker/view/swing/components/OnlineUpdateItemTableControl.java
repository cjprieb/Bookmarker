package com.purplecat.bookmarker.view.swing.components;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SortOrder;
import javax.swing.table.TableRowSorter;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.extensions.OnlineMediaItemExt;
import com.purplecat.bookmarker.extensions.OnlineMediaItemExt.OnlineBookmarkComparator;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.view.swing.actions.UpdateMediaFromItemAction;
import com.purplecat.bookmarker.view.swing.models.OnlineUpdateItemTableModel;
import com.purplecat.bookmarker.view.swing.renderers.DataFields;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.TTableColumn;
import com.purplecat.commons.swing.AppUtils.IDragDropAction;
import com.purplecat.commons.swing.TTable;
import com.purplecat.commons.swing.TablePopupCreator;
import com.purplecat.commons.swing.Toolbox;
import com.purplecat.commons.swing.dragdrop.FileDrop;
import com.purplecat.commons.swing.renderer.ICellRendererFactory;
import com.purplecat.commons.utils.ListUtils;
import com.purplecat.commons.utils.StringUtils;

public class OnlineUpdateItemTableControl {
	private final OnlineUpdateItemTableModel _model;
	private final TTable<OnlineMediaItem> _table;
	private final JScrollPane _scroll;
	private final TableRowSorter<OnlineUpdateItemTableModel> _sorter;
	private final TTableColumn[] _columns;
	private final Controller _controller;
	private final Toolbox _toolbox;
	
	@Inject
	public OnlineUpdateItemTableControl(
			ICellRendererFactory factory, 
			Controller ctrl, 
			IResourceService resources, 
			Toolbox toolbox,
			@Named("Manga Url") IDragDropAction mangaDropAction) {
		_controller = ctrl;
		_toolbox = toolbox;
		_columns = new TTableColumn[] {
				DataFields.TIME_COL,
				DataFields.ONLINE_STATE_COL,
				DataFields.TITLE_COL,
				DataFields.PLACE_COL
		};
		_model = new OnlineUpdateItemTableModel(_columns, resources);		
		_table = new TTable<OnlineMediaItem>(factory);
		_table.setTemplateModel(_model);		
		_scroll = new JScrollPane(_table);
		_sorter = new OnlineBookmarkSorter(_model);
		_table.setRowSorter(_sorter);
		_table.addMouseListener(new DoubleClickListener());
		
        new FileDrop(_scroll, true, mangaDropAction);
		
		setupPopupMenu();
	}
	
	public TTable<OnlineMediaItem> getTable() {
		return _table;
	}
	
	public OnlineUpdateItemTableModel getModel() {
		return _model;
	}
	
	private void setupPopupMenu() {
		JPopupMenu menu = new JPopupMenu();

		menu.add(new JMenuItem(new UpdateMediaFromItemAction(_table, _controller)));
		
		_table.addMouseListener(new TablePopupCreator(_table, menu));
	}	
	
	public Component getComponent() {
		return _scroll;
	}
	
	public class OnlineBookmarkSorter extends TableRowSorter<OnlineUpdateItemTableModel> {
		OnlineBookmarkSorter(OnlineUpdateItemTableModel model) {
			super(model);

			List<SortKey> sortKeys = new LinkedList<SortKey>();
			
			int index = ListUtils.indexOf(_columns, DataFields.ONLINE_STATE_COL);
			if ( index >= 0 ) { 
				this.setComparator(index, new OnlineBookmarkComparator());
				sortKeys.add(new SortKey(index, SortOrder.ASCENDING)); 
			}
			
			setSortKeys(sortKeys);

			//index = ListUtils.indexOf(_columns, DataFields.DATE_COL);
			//if ( index >= 0 ) { this.setComparator(index, new ReverseDateComparor()); }

			//index = ListUtils.indexOf(_columns, DataFields.MEDIA_STATE_COL);
			//if ( index >= 0 ) { this.setComparator(index, new MediaStoryStateComparor()); }
		}		
	}
	
	public class DoubleClickListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if ( e.getClickCount() == 2 ) {
				String url = OnlineMediaItemExt.getPreferredUrl(_table.getSelectedItem());
				if ( !StringUtils.isNullOrEmpty(url) ) {
					_toolbox.browse(url);
				}
			}
		}
	}
}
