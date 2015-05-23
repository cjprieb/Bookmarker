package com.purplecat.bookmarker.view.swing;

import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.table.TableRowSorter;

import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.view.swing.renderers.DataFields;
import com.purplecat.commons.TTableColumn;
import com.purplecat.commons.swing.TTable;
import com.purplecat.commons.swing.renderer.ICellRendererFactory;

public class UpdateMediaTableControl {
	private final UpdateMediaTableModel _model;
	private final TTable<OnlineMediaItem> _table;
	private final JScrollPane _scroll;
	private final TableRowSorter<UpdateMediaTableModel> _sorter;
	private final TTableColumn[] _columns;
	
	public UpdateMediaTableControl(ICellRendererFactory factory) {
		_columns = new TTableColumn[] {
				DataFields.TIME_COL,
				DataFields.ONLINE_STATE_COL,
				DataFields.TITLE_COL,
				DataFields.PLACE_COL
		};
		_model = new UpdateMediaTableModel(_columns);		
		_table = new TTable<OnlineMediaItem>(factory);
		_table.setTemplateModel(_model);		
		_scroll = new JScrollPane(_table);
		_sorter = new OnlineBookmarkSorter(_model);
		_table.setRowSorter(_sorter);
	}
	
	public UpdateMediaTableModel getModel() {
		return _model;
	}
	
	public Component getComponent() {
		return _scroll;
	}
	
	public class OnlineBookmarkSorter extends TableRowSorter<UpdateMediaTableModel> {
		OnlineBookmarkSorter(UpdateMediaTableModel model) {
			super(model);

			//int index = ListUtils.indexOf(_columns, DataFields.ONLINE_STATE_COL);
			//if ( index >= 0 ) { this.setComparator(index, new FavoriteComparor()); }

			//index = ListUtils.indexOf(_columns, DataFields.DATE_COL);
			//if ( index >= 0 ) { this.setComparator(index, new ReverseDateComparor()); }

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
