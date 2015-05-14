package com.purplecat.commons.swing.renderer;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.purplecat.commons.swing.StackLayout;
import com.purplecat.commons.swing.Toolbox;

public class IconCellRenderer implements TableCellRenderer {

    private TableCellRenderer 	mOrig 		= null;
    
    private ImageIcon 			mIcon	 	= null;
    private JPanel 				mPanel 		= new JPanel(new StackLayout());
    
    private JLabel 				mPicLabel 	= null;

    public IconCellRenderer(JTable table, ImageIcon icon) {
    	mOrig = table.getTableHeader().getDefaultRenderer();
    	mIcon = icon;
    	mPicLabel = new JLabel(icon);    	
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = mOrig.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    	if ( c instanceof JLabel ) {
    		JLabel label = (JLabel)c;
    		label.setText("");
    		
    		/*
    		 * Nimbus look-and-feel has problems placing both a sorting icon and a header icon in the same component.
    		 * So using suggestion from http://stackoverflow.com/questions/10915994/java-swing-nimbus-lf-overrides-custom-icon-in-jtable-header-after-sort-is-app
    		 * to put the header icon on top of the generated nimbus component instead of replacing it.
    		 */
	        if( Toolbox.USING_NIMBUS_LAF ){	
	        	mPanel.removeAll();                //clean the JPanel	
	            mPanel.add(c, StackLayout.BOTTOM); 
	            mPanel.add(mPicLabel, StackLayout.TOP);
	            return(mPanel);	
	        }
	        else {
	        	label.setIcon(mIcon);
	        }
    	}  
        return(c);
    }
}