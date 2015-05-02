package com.purplecat.commons.swing.dragdrop;
/* [Heavily modified from: ] http://java.sun.com/docs/books/tutorial/index.html */

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import com.purplecat.commons.logs.ConsoleLog;
import com.purplecat.commons.logs.ILoggingService;

/**
 * use by :
 * 	    list.setTransferHandler(arrayListHandler);
 * where list contains interface ListComponent.
 * Does not move items around within a list, only from one to another.
 * @author cprieb
 *
 */
public class DragDropHandler extends TransferHandler {
	public static final String TAG = "DragDropHandler";
	
	/*
	 * Static variables
	 */
	public static DataFlavor 	sgLocalArrayListFlavor;
	public static DataFlavor 	sgSerialArrayListFlavor;
	
	static String sgLocalArrayListType = DataFlavor.javaJVMLocalObjectMimeType + ";class=java.util.ArrayList";
	static ILoggingService _logging = new ConsoleLog();//LoggingService.create();

	static {
		try {
			sgLocalArrayListFlavor = new DataFlavor(sgLocalArrayListType);
		} catch (ClassNotFoundException e) {
			System.out.println("ArrayListTransferHandler: unable to create data flavor");
		}
		sgSerialArrayListFlavor = new DataFlavor(ArrayList.class, "ArrayList");
	}

	public static boolean hasLocalArrayListFlavor(DataFlavor[] flavors) {
		if ( sgLocalArrayListFlavor == null ) {
			return(false);
		}

		for ( int i = 0; i < flavors.length; i++ ) {
			if ( flavors[i].equals(sgLocalArrayListFlavor) ) {
				return(true);
			}
		}
		return(false);
	}

	public static boolean hasSerialArrayListFlavor(DataFlavor[] flavors) {
		if ( sgSerialArrayListFlavor == null ) {
			return(false);
		}

		for ( int i = 0; i < flavors.length; i++ ) {
			if ( flavors[i].equals(sgSerialArrayListFlavor) ) {
				return(true);
			}
		}
		return(false);
	}
	
	public static ArrayList<?> convertToArrayList(Transferable t) {
		ArrayList<?> alist = null;
		try {
			if ( DragDropHandler.hasLocalArrayListFlavor(t.getTransferDataFlavors()) ) {
				_logging.debug(1, TAG, "local array list flavor");
				alist = (ArrayList<?>) t.getTransferData(sgLocalArrayListFlavor);
				_logging.debug(1, TAG, "got transfer data");
			} 
			else if ( DragDropHandler.hasSerialArrayListFlavor(t.getTransferDataFlavors()) ) {
				_logging.debug(1, TAG, "serial array list flavor");
				alist = (ArrayList<?>) t.getTransferData(sgSerialArrayListFlavor);
			}
			else {
				_logging.debug(1, TAG, "no valid flavor");
			}
		}
		catch (IOException e) {
			_logging.error(TAG, "IOException during ImportData: could not convert Transferable to Arraylist", e);
		} catch (UnsupportedFlavorException e) {
			_logging.error(TAG, "UnsupportedFlavorException during ImportData: could not convert Transferable to Arraylist", e);
		}
		return(alist);
	}

	/*
	 * Class variables
	 */
	ListComponent	mSource	= null;
	
	Object[]	mTransfers 	= null;
	int 		mAddCount 	= 0; 		// Number of items added
	
	boolean mCanMoveWithinList = false;

	public DragDropHandler() {
		
	}

	public DragDropHandler(boolean moveWithinList) {
		mCanMoveWithinList = moveWithinList;
	}

	@Override
	public boolean importData(JComponent c, Transferable t) {
		_logging.debug(0, TAG, "Importing Data");
		ListComponent	target 	= null;
		ArrayList<?> 	alist 	= null;
		boolean			dataImported	= false;
		if ( c != null && canImport(c, t.getTransferDataFlavors()) ) {
			dataImported = true;
			try {				
				if ( c instanceof ListComponent ) {
					_logging.debug(1, TAG, "importing from list component");
					target = (ListComponent)c;
				}
				else if ( c instanceof ListComponentContainer ) {
					_logging.debug(1, TAG, "importing from list container");
					target = ((ListComponentContainer)c).getListComponent();
				}
				else {
					_logging.debug(1, TAG, "importing from " + c);	
				}
				if ( hasLocalArrayListFlavor(t.getTransferDataFlavors()) ) {
					_logging.debug(1, TAG, "local array list flavor");
					alist = (ArrayList<?>) t.getTransferData(sgLocalArrayListFlavor);
					_logging.debug(1, TAG, "got transfer data");
				} 
				else if ( hasSerialArrayListFlavor(t.getTransferDataFlavors()) ) {
					_logging.debug(1, TAG, "serial array list flavor");
					alist = (ArrayList<?>) t.getTransferData(sgSerialArrayListFlavor);
				} 
				else {
					_logging.debug(1, TAG, "no valid flavor");
					dataImported = false;
				}
			} catch (UnsupportedFlavorException ufe) {
				_logging.error(TAG, "importData: unsupported data flavor", ufe);
				dataImported = false;
			} catch (IOException ioe) {
				_logging.error(TAG, "importData: I/O exception", ioe);
				dataImported = false;
			}
		}

		// At this point we use the same code to retrieve the data
		// locally or serially.

		// We'll drop at the current selected index.
		if ( dataImported == true ) {
//			DebugUtils.println(print, "dataImported == true: " + target, 1);
			int index = target.getSelectedIndex();
			_logging.debug(2, TAG, "drop index: " + index);
			_logging.debug(2, TAG, "mSource: " + mSource);
//			DebugUtils.println(print, "target: " + target, 2);
	
			// Prevent the user from dropping data back on itself.
			// For example, if the user is moving items #4,#5,#6 and #7 and
			// attempts to insert the items after item #5, this would
			// be problematic when removing the original items.
			// This is interpreted as dropping the same data on itself
			// and has no effect.
			if ( mSource.equals(target) ) {				
				//validating data...
				dataImported = false;
				Object[] values = target.getSelectedValues();
				if ( values != null && values.length > 0 && alist.size() > 0 ) {
					//don't move if same index:
					if ( values[0] != alist.get(0) ) {
						dataImported = true;

						for ( int i = alist.size()-1; i <= 0; i-- ) {
							_logging.debug(2, TAG, "adding: " + alist.get(i));
							target.add(index, alist.get(i));
						}
					}
				}
				_logging.debug(2, TAG, "allow move: " + dataImported);
			}
			else {	
				mAddCount = alist.size();
				_logging.debug(2, TAG, "add count" + mAddCount);
				for ( int i = 0; i < alist.size(); i++ ) {
					Object item = alist.get(i);
					_logging.debug(1, TAG, i + ". " + item + " " + (item != null ? item.getClass() : "<null>"));
					target.add(alist.get(i));
				}
			}
		}
		_logging.debug(0, TAG, "Data Imported (value): " + dataImported);
		return(dataImported);
	}

	@Override
	protected void exportDone(JComponent c, Transferable data, int action) {
		_logging.debug(0, TAG, "Export Done");
		if ( (action == MOVE) && (mTransfers != null) ) {

			// If we are moving items around in the same list, we
			// need to adjust the indices accordingly since those
			// after the insertion point have moved.
//			if ( mAddCount > 0 ) {
//				for ( int i = 0; i < mIndices.length; i++ ) {
//					if ( mIndices[i] > mAddIndex ) {
//						mIndices[i] += mAddCount;
//					}
//				}
//			}
			
			if ( mSource != null ) {
				_logging.debug(0, TAG, "removing indices");
				for ( int i = mTransfers.length - 1; i >= 0; i-- ) {
					mSource.removeRow(mTransfers[i]);
				}
			}
			else {
				_logging.debug(0, TAG, "export done source " + (mSource != null ? mSource.getClass() : "<null>"));				
			}
		}
		_logging.debug(1, TAG, "AddCount: " + mAddCount + " to (0)");
//		DebugUtils.println(true, "AddIndex: " + mAddIndex + " to (-1)", 1);
		
		if ( mTransfers != null ) {
			String s = "";
			for ( Object i : mTransfers ) {
				s += (s.length() == 0 ? "{" : ", ") + i;
			}
			s += "}";
			_logging.debug(1, TAG, "Indices: " + s + " to (null)");
		}
		else {
			_logging.debug(1, TAG, "Indices: null to (null)");			
		}
		mTransfers = null;
//		mAddIndex = -1;
		mAddCount = 0;
	}

	@Override
	public boolean canImport(JComponent c, DataFlavor[] flavors) {
		boolean importable = false;
		String reason = "";
		if ( c == mSource && !mCanMoveWithinList ) {
			reason = "c is source and can't move inside";
			importable = false;
		}
		else if ( c == mSource && mCanMoveWithinList ) {
			reason = "c is source and moving inside list";
			importable = true;
		}
		else if ( hasLocalArrayListFlavor(flavors) ) {
			reason = "has local array list type";
			importable = true;
		}
		else if ( hasSerialArrayListFlavor(flavors) ) {
			reason = "has serial array list type";
			importable = true;
		}
		else {
			reason = "type is not supported: " + flavors;
		}
		_logging.debug(0, TAG, "canImport: " + importable + " (reason = " + reason + ")");
		return(importable);
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		ArrayListTransferable transferThis = null;
		_logging.debug(1, TAG, "createTransferable");
		if ( c != null ) {
			if ( c != null && c instanceof ListComponent ) {
				_logging.debug(2, TAG, "mSource is ListComponent");
				mSource = (ListComponent) c;
			}
			else if ( c instanceof ListComponentContainer ) {
				_logging.debug(2, TAG, "mSource is ListComponentContainer");
				mSource = ((ListComponentContainer)c).getListComponent();
			}
			else {
				_logging.debug(2, TAG, "mSource is null (c=" + c.getClass() + ")");
				mSource = null;
			}

			if ( mSource != null ) {
				_logging.debug(2, TAG, "mSource=" + mSource.getClass());
				if ( mSource.allowTransfer() ) {
					mTransfers		= mSource.getSelectedValues();
					if ( mTransfers != null && mTransfers.length > 0 ) {
						ArrayList<Object> alist = new ArrayList<Object>(mTransfers.length);
						for ( int i = 0; i < mTransfers.length; i++ ) {
							alist.add(mTransfers[i]);
						}
						transferThis = new ArrayListTransferable(alist);
					}
				}
			}
		}
		return(transferThis);
	}

	@Override
	public int getSourceActions(JComponent c) {
		return COPY_OR_MOVE;
	}

	public static class ArrayListTransferable implements Transferable {
		ArrayList<?> mData;

		public ArrayListTransferable(ArrayList<?> alist) {
			mData = alist;
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
			if ( !isDataFlavorSupported(flavor) ) {
				throw(new UnsupportedFlavorException(flavor));
			}
			return(mData);
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { sgLocalArrayListFlavor, sgSerialArrayListFlavor };
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			if ( sgLocalArrayListFlavor.equals(flavor) ) {
				return true;
			}
			if ( sgSerialArrayListFlavor.equals(flavor) ) {
				return true;
			}
			return false;
		}
	}
	
	public interface ListComponent {
		int 		getSelectedIndex();
		int[] 		getSelectedIndices();
		Object[] 	getSelectedValues();
		
		boolean		add(Object obj);
		boolean 	add(int index, Object obj);
		
		boolean		removeRow(Object obj);
		
		int			getRowCount();
		boolean		allowTransfer();
	}
	
	public static abstract class AbstractDragDropTable extends JTable implements ListComponent {		
		@Override
		public int getSelectedIndex() {
			return(super.getSelectedRow());
		}
		
		@Override
		public int[] getSelectedIndices() {
			return(super.getSelectedRows());
		}	
	}
	
	public interface ListComponentContainer {
		ListComponent getListComponent();
	}
	
	public static class ListScrollPane extends JScrollPane implements ListComponentContainer {
		private ListComponent mList;
		
		public ListScrollPane() {
		}
		
		public ListScrollPane(JComponent c, ListComponent list) {
			setListComponent(c, list);
		}
		
		public ListScrollPane(AbstractDragDropTable c) {
			setListComponent(c, c);
		}
		
		@Override
		public ListComponent getListComponent() {
			return(mList);
		}
		public void setListComponent(JComponent c, ListComponent list) {
			setViewportView(c);
			mList = (ListComponent)list;
		}
	}
}
