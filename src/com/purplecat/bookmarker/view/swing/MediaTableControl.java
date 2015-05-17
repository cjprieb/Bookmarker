package com.purplecat.bookmarker.view.swing;

import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.table.TableRowSorter;

import com.purplecat.bookmarker.models.EFavoriteState.FavoriteComparor;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.view.swing.renderers.DataFields;
import com.purplecat.commons.TTableColumn;
import com.purplecat.commons.extensions.DateTimeFormats.ReverseDateComparor;
import com.purplecat.commons.swing.TTable;
import com.purplecat.commons.swing.renderer.ICellRendererFactory;
import com.purplecat.commons.utils.ListUtils;

public class MediaTableControl {
	private final MediaTableModel _model;
	private final TTable<Media> _table;
	private final JScrollPane _scroll;
	private final TableRowSorter<MediaTableModel> _sorter;
	private final TTableColumn[] _columns;
	
	public MediaTableControl(ICellRendererFactory factory) {
		_columns = new TTableColumn[] {
				//DataFields.FLAG_COL,
				DataFields.MEDIA_STATE_COL,
				DataFields.FAVORITE_COL,
				DataFields.TITLE_COL,
				DataFields.PLACE_COL,
				DataFields.DATE_COL
		};
		_model = new MediaTableModel(_columns);		
		_table = new TTable<Media>(factory);
		_table.setTemplateModel(_model);		
		_scroll = new JScrollPane(_table);
		_sorter = new SavedBookmarkSorter(_model);
		_table.setRowSorter(_sorter);
	}
	
	public MediaTableModel getModel() {
		return _model;
	}
	
	public Component getComponent() {
		return _scroll;
	}
	
	public class SavedBookmarkSorter extends TableRowSorter<MediaTableModel> {
		SavedBookmarkSorter(MediaTableModel model) {
			super(model);

			int index = ListUtils.indexOf(_columns, DataFields.FAVORITE_COL);
			if ( index >= 0 ) { this.setComparator(index, new FavoriteComparor()); }

			index = ListUtils.indexOf(_columns, DataFields.DATE_COL);
			if ( index >= 0 ) { this.setComparator(index, new ReverseDateComparor()); }

			//index = ListUtils.indexOf(_columns, DataFields.MEDIA_STATE_COL);
			//if ( index >= 0 ) { this.setComparator(index, new MediaStoryStateComparor()); }
		}		
	}
	
	/*public void query(Query query) {
		mSorter.setRowFilter(new QueryFilter(query));
	}
	
	public class QueryFilter extends RowFilter<MediaTableModel, Integer> {
		Query _lastQuery;
		
		public QueryFilter(Query query) {
			_lastQuery = query;
		}
		
		@Override
		public boolean include(RowFilter.Entry<? extends MediaTableModel, ? extends Integer> entry) {
			if ( _lastQuery != null ) {
				return( _lastQuery.matchesQuery(CriterionMatchers.SavedItemMatcher, 
						entry.getModel().getItemAt(entry.getIdentifier())) );
			}
			else {
				return(true);
			}
		}
	}*/
}
