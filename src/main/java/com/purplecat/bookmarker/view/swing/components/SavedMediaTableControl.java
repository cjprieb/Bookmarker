package com.purplecat.bookmarker.view.swing.components;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableRowSorter;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.purplecat.bookmarker.Resources;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.extensions.DateTimeFormats.ReverseDateTimeComparor;
import com.purplecat.bookmarker.extensions.MediaItemExt;
import com.purplecat.bookmarker.models.EFavoriteState.FavoriteComparor;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.SavedMediaQuery;
import com.purplecat.bookmarker.view.swing.models.SavedMediaTableModel;
import com.purplecat.bookmarker.view.swing.renderers.DataFields;
import com.purplecat.bookmarker.view.swing.renderers.UpdatedMediaRowRenderer;
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

public class SavedMediaTableControl {
	private final SavedMediaTableModel _model;
	private final TTable<Media> _table;
	private final JScrollPane _scroll;
	private final TableRowSorter<SavedMediaTableModel> _sorter;
	private final TTableColumn[] _columns;
	private final Controller _controller;
	private final Toolbox _toolbox;
	private final IResourceService _resources;
	
	private SavedMediaQuery _lastQuery;
	
	@Inject
	public SavedMediaTableControl(
			ICellRendererFactory factory, 
			Controller ctrl, 
			IResourceService resources, 
			Toolbox toolbox,
			@Named("Manga Url") IDragDropAction mangaDropAction) {
		_resources = resources;
		_controller = ctrl;
		_toolbox = toolbox;
		_columns = new TTableColumn[] {
				//DataFields.FLAG_COL,
				DataFields.MEDIA_STATE_COL,
				DataFields.FAVORITE_COL,
				DataFields.TITLE_COL,
				DataFields.PLACE_COL,
				DataFields.DATE_COL
		};
		_model = new SavedMediaTableModel(_columns, ctrl, resources);		
		_table = new TTable<Media>(factory, new UpdatedMediaRowRenderer());
		_table.setTemplateModel(_model);
		_scroll = new JScrollPane(_table);
		_sorter = new SavedBookmarkSorter(_model);
		_table.setRowSorter(_sorter);
		_table.addMouseListener(new DoubleClickListener());
		
        new FileDrop(_scroll, true, mangaDropAction);
		
		setupPopupMenu();
	}
	
	private void setupPopupMenu() {
		JPopupMenu menu = new JPopupMenu();

		menu.add(new JMenuItem(new RefreshItemAction()));
		
		_table.addMouseListener(new TablePopupCreator(_table, menu));
	}	
	
	public TTable<Media> getTable() {
		return _table;
	}
	
	public SavedMediaTableModel getModel() {
		return _model;
	}
	
	public Component getComponent() {
		return _scroll;
	}
	
	public class SavedBookmarkSorter extends TableRowSorter<SavedMediaTableModel> {
		SavedBookmarkSorter(SavedMediaTableModel model) {
			super(model);
			
			List<SortKey> sortKeys = new LinkedList<SortKey>();

			int index = ListUtils.indexOf(_columns, DataFields.FAVORITE_COL);
			if ( index >= 0 ) { 
				this.setComparator(index, new FavoriteComparor());
			}

			index = ListUtils.indexOf(_columns, DataFields.DATE_COL);
			if ( index >= 0 ) { 
				this.setComparator(index, new ReverseDateTimeComparor());
			}

			index = ListUtils.indexOf(_columns, DataFields.TITLE_COL);
			if ( index >= 0 ) { 
				sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
			}
			
			setSortKeys(sortKeys);

			//index = ListUtils.indexOf(_columns, DataFields.MEDIA_STATE_COL);
			//if ( index >= 0 ) { this.setComparator(index, new MediaStoryStateComparor()); }
		}		
	}
	
	public class DoubleClickListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if ( e.getClickCount() == 2 ) {
				String url = MediaItemExt.getPreferredUrl(_table.getSelectedItem());
				if ( !StringUtils.isNullOrEmpty(url) ) {
					_toolbox.browse(url);
				}
			}
		}
	}
	
	public class RefreshItemAction extends AbstractAction {		
		public RefreshItemAction() {
			this.putValue(Action.NAME, _resources.getString(Resources.string.menuLoadSummary));
			this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_L);
		}
		
		@Override 
		public void actionPerformed(ActionEvent e) {
			Media item = _table.getSelectedItem();
			if ( item != null ) {
				_controller.loadMediaSummary(item._id, item._titleUrl);
			}
		}
	}
	
	public void query(SavedMediaQuery query) {
		System.out.println("querying: isUpdated=" + query._isUpdated);
		_lastQuery = query;
		_sorter.setRowFilter(new QueryFilter(_lastQuery));
	}
	
	
	public class QueryFilter extends RowFilter<SavedMediaTableModel, Integer> {
		SavedMediaQuery _currentQuery;
		
		public QueryFilter(SavedMediaQuery query) {
			_currentQuery = query;
		}
		
		@Override
		public boolean include(RowFilter.Entry<? extends SavedMediaTableModel, ? extends Integer> entry) {
			if ( _currentQuery != null ) {
				return( _currentQuery.matches(entry.getModel().getItemAt(entry.getIdentifier())) );
			}
			else {
				return(true);
			}
		}
	}
}
