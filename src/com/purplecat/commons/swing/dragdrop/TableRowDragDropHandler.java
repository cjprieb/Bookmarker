package com.purplecat.commons.swing.dragdrop;
/* [Heavily modified from: ] http://java.sun.com/docs/books/tutorial/index.html */

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.logs.LoggingService;
import com.purplecat.commons.swing.dragdrop.DragDropHandler.ArrayListTransferable;


/**
 * use by :
 * 	    list.setTransferHandler(arrayListHandler);
 * where list contains interface ListComponent.
 * Does not move items around within a list, only from one to another.
 * @author cprieb
 *
 */
public class TableRowDragDropHandler extends TransferHandler {
	
	static String tag = "TableRowDragDropHandler";

	JTable 	mSource 	= null;	
	IEditableList mTransferHandler = null;
	int[]	mTransfers 	= null;
	ILoggingService Log = LoggingService.create();

	public TableRowDragDropHandler(IEditableList hdn) {
		mTransferHandler = hdn;
	}

	@Override
	public boolean importData(JComponent c, Transferable t) {
		Log.debug(0, tag, "Importing Data");
		
		ArrayList<?> 	alist 			= null;		
		boolean			dataImported	= false;
		
		if ( c != null && canImport(c, t.getTransferDataFlavors()) && c == mSource ) {
			alist = DragDropHandler.convertToArrayList(t);
			dataImported = ( alist != null );
		}

		// At this point we use the same code to retrieve the data
		// locally or serially.

		// We'll drop at the current selected index.
		if ( dataImported == true ) {
			int index = mSource.getSelectedRow();
			
			Log.debug(2, tag, "drop index: " + index);
			Log.debug(2, tag, "mSource: " + mSource);
	
			// Prevent the user from dropping data back on itself.
			// For example, if the user is moving items #4,#5,#6 and #7 and
			// attempts to insert the items after item #5, this would
			// be problematic when removing the original items.
			// This is interpreted as dropping the same data on itself
			// and has no effect.
			
			//validating data...
			dataImported = false;
			int row = mSource.getSelectedRow();
			if ( row >= 0 && alist.size() > 0 ) {
				//don't move if same index:
				if ( row != (Integer)alist.get(0) ) {
					dataImported = true;

					for ( int i = alist.size()-1; i <= 0; i-- ) {
						Log.debug(2, tag, "adding: " + alist.get(i));
						mTransferHandler.addRowAt(index, (Integer)alist.get(i));
					}
				}
			}
			Log.debug(2, tag, "allow move: " + dataImported);
		}
		Log.debug(0, tag, "Data Imported (value): " + dataImported);
		return(dataImported);
	}

	@Override
	protected void exportDone(JComponent c, Transferable data, int action) {
		mTransfers = null;
	}

	@Override
	public boolean canImport(JComponent c, DataFlavor[] flavors) {
		return(c == mSource);
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		ArrayListTransferable transferThis = null;
		Log.debug(1, tag, "createTransferable");
		if ( c != null && c instanceof JTable ) {
			Log.debug(2, tag, "mSource is JTable");
			mSource = (JTable)c;
		}

		if ( mSource != null ) {
			Log.debug(2, tag, "mSource=" + mSource.getClass());
			mTransfers = mSource.getSelectedRows();
			if ( mTransfers.length > 0 ) {
				ArrayList<Object> alist = new ArrayList<Object>();
				for ( int i = 0; i < mTransfers.length; i++ ) {
					alist.add(mTransfers[i]);
				}
				transferThis = new ArrayListTransferable(alist);
			}
		}
		return(transferThis);
	}

	@Override
	public int getSourceActions(JComponent c) {
		return COPY_OR_MOVE;
	}
}
