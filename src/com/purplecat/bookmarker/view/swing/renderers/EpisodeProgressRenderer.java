package com.purplecat.bookmarker.view.swing.renderers;

import java.awt.Component;
import java.util.Calendar;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.purplecat.bookmarks.model.Episode;
import com.purplecat.bookmarks.model.ScheduledItem;
import com.purplecat.bookmarks.model.styles.NextDateFormatter;

public class EpisodeProgressRenderer extends JProgressBar implements TableCellRenderer {
	
	public EpisodeProgressRenderer() { 
		super(JProgressBar.HORIZONTAL); 
		this.setStringPainted(true);
		this.setBorderPainted(false);
		this.setString("");
	}
	
	@Override
    public Component getTableCellRendererComponent(JTable table, Object val, boolean isSelected, boolean hasFocus, int row, int col)  
    {  
		setEnabled(table.isEnabled());
		setValue(val);
		setBackground(table.getBackground());
		return(this);
    }  
	
	public void setValue(Object value) {
		String s = "";
		if ( value != null && value instanceof ScheduledItem ) {
			ScheduledItem 	item 	= (ScheduledItem)value;
			Calendar 		today 	= Calendar.getInstance();
			Episode 		nextEp	= item.getNextUnwatchedEpisode();
			Episode 		latestEp= item.getLatestWatchedEpisode();
			int 			watched = ScheduledItem.getWatchedCount(item);
			int 			unwatched = ScheduledItem.getUnwatchedCount(item, today);
						
			if ( nextEp == null ) {// no next episode so probably a finished season/show
				s = String.format("%d/%d", watched, (watched+unwatched));		
			}
			else if ( latestEp == null ) {
				s = "new";
			}
			else {
				Calendar c = nextEp != null ? nextEp.getDateAvailable() : null;
				if ( c != null && c.after(today) ) {
					//Show date of next chapter
					s = "next " + NextDateFormatter.dateToString(c, false);
				}
				else {
					s = String.format("%d/%d", watched, (watched+unwatched));
				}
			}
			setMaximum(watched+unwatched);
			setValue(watched);
		}
		else {
			setMaximum(0);
			setValue(0);
		}
		setString(s);
	}

}
