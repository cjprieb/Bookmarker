package com.purplecat.commons.swing.dragdrop;
/* [Heavily modified from: ] http://java.sun.com/docs/books/tutorial/index.html */

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import com.google.inject.Inject;
import com.purplecat.commons.logs.ILoggingService;
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
	
	public static final String TAG = "TableRowDragDropHandler";

	JTable 	mSource 	= null;	
	IEditableList mTransferHandler = null;
	int[]	mTransfers 	= null;
	
	@Inject public ILoggingService _logging;

	public TableRowDragDropHandler(IEditableList hdn) {
		mTransferHandler = hdn;
	}

	@Override
	public boolean importData(JComponent c, Transferable t) {
		_logging.debug(0, TAG, "Importing Data");
		
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
			
			_logging.debug(2, TAG, "drop index: " + index);
			_logging.debug(2, TAG, "mSource: " + mSource);
	
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
						_logging.debug(2, TAG, "adding: " + alist.get(i));
						mTransferHandler.addRowAt(index, (Integer)alist.get(i));
					}
				}
			}
			_logging.debug(2, TAG, "allow move: " + dataImported);
		}
		_logging.debug(0, TAG, "Data Imported (value): " + dataImported);
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
		_logging.debug(1, TAG, "createTransferable");
		if ( c != null && c instanceof JTable ) {
			_logging.debug(2, TAG, "mSource is JTable");
			mSource = (JTable)c;
		}

		if ( mSource != null ) {
			_logging.debug(2, TAG, "mSource=" + mSource.getClass());
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
