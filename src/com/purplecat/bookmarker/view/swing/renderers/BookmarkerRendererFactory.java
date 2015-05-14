package com.purplecat.bookmarker.view.swing.renderers;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.purplecat.bookmarker.models.Place;
import com.purplecat.commons.TTableColumn;
import com.purplecat.commons.swing.EnablableTableCellRenderer;
import com.purplecat.commons.swing.renderer.ICellRendererFactory;

public class BookmarkerRendererFactory implements ICellRendererFactory {
	
	@Override
	public Object getHeaderValue(TTableColumn column) {
		Object obj = column.getName();
		/*if ( column == DataFields.FAVORITE_COL ) {
			obj = BookmarkApplication.getImage(AppImages.imgEmblemHeartId);
		}
		else if ( column == DataFields.MEDIA_STATE_COL ||
				column == DataFields.SCHEDULED_STATE_COL || 
				column == DataFields.ONLINE_STATE_COL ) {
			obj = BookmarkApplication.getImage(AppImages.imgOpenBookId);
		}
		else if ( column == DataFields.FLAG_COL ) {
			obj = BookmarkApplication.getImage(AppImages.imgFlagHeaderId);
		}
		else if ( column == DataFields.WATCHED_COL ) {
			obj = BookmarkApplication.getImage(Images.appBlankId);
		}*/
		return(obj);
	}

	@Override
	public TableCellRenderer getRendererFromType(TTableColumn type) {
		TableCellRenderer renderer = null;
		if ( type.getClassType() == Place.class ) {
			renderer = new PlaceRenderer();
		}
		else if ( type == DataFields.DATE_COL ) {
			renderer = new DateTimeRenderer("MM/dd/yy");
		}
		else if ( type == DataFields.TIME_COL ) {
			renderer = new DateTimeRenderer("MM/dd HH:mm");
		}
		/*else if ( type.getClassType() == EFavoriteState.class ) {
			renderer = new FavoriteStateIconRenderer();	
		}
		else if ( type.getClassType() == GenreList.class ) {
			renderer = new GenreRenderer();	
		}
		else if ( type == DataFields.MEDIA_STATE_COL ||
				type == DataFields.SCHEDULED_STATE_COL || 
						type == DataFields.ONLINE_STATE_COL ) {
			renderer = new StoryStateIconRenderer();	
		}
		else if ( type == DataFields.NEXT_EPISODE_COL ) {
			renderer = new EpisodeProgressRenderer();
		}
		else if ( type == DataFields.FLAG_COL ) {
			renderer = new FlagIconRenderer();	
		}
		else if ( type == DataFields.RATING_COL ) {
			renderer = new RatingRenderer();	
		}
		else if ( type == DataFields.IGNORE_COL || type == DataFields.WATCHED_COL ) {
			//Use default renderer for Boolean values.
		}*/
		else {
			renderer = new EnablableTableCellRenderer();
		}
		return(renderer);
	}

	@Override
	public TableCellEditor getEditorFromType(TTableColumn type) {
		TableCellEditor editor = null;
		/*if ( type.getClassType() == Place.class ) {
			editor = new PlaceEditor();	
		}
		else if ( type.getClassType() == EFavoriteState.class ) {
//			editor = new FavoriteStateEditor();	
		}
		else if ( type == DataFields.MEDIA_STATE_COL ||
				type == DataFields.SCHEDULED_STATE_COL || 
						type == DataFields.ONLINE_STATE_COL ) {
//			editor = new StoryStateEditor();	
		}
		else if ( type == DataFields.DATE_COL ) {
			editor = new CalendarEditor("MM/dd/yy");
		}
		else if ( type == DataFields.TIME_COL ) {
			editor = new CalendarEditor("MM/dd HH:mm");
		}
		else if ( type == DataFields.FLAG_COL ) {
//			editor = new FlagEditor();	
		}
		else if ( type == DataFields.RATING_COL ) {
//			editor = new RatingEditor();	
		}*/
		return(editor);
	}

}
