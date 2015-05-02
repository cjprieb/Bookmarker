package com.purplecat.commons.swing.dragdrop;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

public class NewDragDropHandler {	
	public static final DataFlavor STRING_FLAVOR 		= new DataFlavor(String.class, "String");
	public static final DataFlavor ARRAY_FLAVOR 		= new DataFlavor(MyList.class, "MyList");
	
	private static class MyList {}
	
	public abstract static class DropTableTarget implements DropTargetListener {
		protected JTable mTable = null;
		
		public DropTableTarget(JTable table) {
			mTable = table;
			mTable.setDragEnabled(true);			
			mTable.setDropTarget( new DropTarget( mTable, this ) );
		}
		
		@Override
		public void dragEnter(DropTargetDragEvent e) {}		

		@Override
		public void dragExit(DropTargetEvent e) {}

		@Override
		public void dragOver(DropTargetDragEvent e) {
			
			boolean flavorFound = false;
			for ( DataFlavor currFlavor : e.getCurrentDataFlavors() ) {
				for ( DataFlavor prefFlavor : getPreferredFlavors() ) {
					if ( currFlavor.equals(prefFlavor) ) {
						flavorFound  = true;
						break;
					}
				}
				if ( flavorFound ) {
					break;
				}
			}

			int row = mTable.rowAtPoint(e.getLocation());
			if ( flavorFound && acceptDropOnRow(row) ) {
				e.acceptDrag(TransferHandler.COPY);		
			}
			else {
				e.rejectDrag();
			}
		}

		@Override
		public void drop(DropTargetDropEvent e) {
			boolean result = false;
			e.acceptDrop(TransferHandler.COPY);			
			try {
				Transferable transfer = e.getTransferable();
				for ( DataFlavor flavor : getPreferredFlavors() ) {
					if ( transfer.isDataFlavorSupported(flavor) ) {	
						Object data = transfer.getTransferData(flavor);
						
						result = parse(data);						
						break;
					}
				}
			} catch (UnsupportedFlavorException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.dropComplete(result);
		}		

		@Override
		public void dropActionChanged(DropTargetDragEvent e) {}
		
		protected abstract boolean		acceptDropOnRow(int row);
		protected abstract DataFlavor[] getPreferredFlavors();
		protected abstract boolean 		parse(Object data);
	}
	
	public interface TableTransferModel<Type> {
		public Type get(int row);
		public Type[] get(int[] rows);
		public DataFlavor getFlavor();
	}

	public static class TransferRowClass<Type> extends TransferHandler {
		protected JTable 					mTable			= null;
		protected TableTransferModel<Type> 	mModel			= null;
		protected TransferHandler 			mTableTransfer 	= null;
		
		public TransferRowClass(JTable table, TableTransferModel<Type> model) {
			mTable = table;
			mTable.setDragEnabled(true);
			mModel = model;
			
			mTableTransfer = table.getTransferHandler();
			table.setTransferHandler(this);
		}
		
		public TransferRowClass(JTable table) {
			mTable = table;
			mTable.setDragEnabled(true);
			
			mTableTransfer = table.getTransferHandler();
			table.setTransferHandler(this);
		}
		
		public void setModel(TableTransferModel<Type> model) {
			mModel = model;
		}
		
		@Override public int getSourceActions(JComponent c) {
			return(mTableTransfer.getSourceActions(c));
		}
		
		@Override
		protected Transferable createTransferable(JComponent c) {
			Transferable transferable = null;
			int[] rows = mTable.getSelectedRows();
			if ( mModel != null ) {
				transferable = new TypeTransferable(mModel.get(rows), mModel.getFlavor());
			}
			else {
				transferable = super.createTransferable(c);
			}
			return(transferable);
		}
	}
	
	private static class TypeTransferable implements Transferable {
		private ArrayList<Object> 	mArrayList 	= new ArrayList<Object>();
		private DataFlavor			mDataFlavor = null;
		
		public TypeTransferable(Object[] list, DataFlavor flavor) {
			for ( Object t : list ) {
				mArrayList.add(t);
			}
			mDataFlavor = flavor;
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			Object obj = null;
			if ( flavor.equals(ARRAY_FLAVOR) ) {
				obj = mArrayList;
//				DebugUtils.println(true, "getting array list data: ", 1);				
//				for ( int i = 0; i < mArrayList.size(); i++ ) {
//					DebugUtils.println(true, i + ". " + mArrayList.get(i), 2);
//				}
			}
			else if ( flavor.equals(STRING_FLAVOR) ) {
				if ( mArrayList != null ) {
					StringBuffer buf = new StringBuffer("");
					for ( Object t : mArrayList ) {
						if ( buf.length() > 0 ) {
							buf.append("\t");
						}
						buf.append(t.toString());
					}
					obj = buf.toString();
				}				
			}
			else if ( mDataFlavor != null && flavor.equals(mDataFlavor) ) {
				obj = (mArrayList != null && mArrayList.size() > 0) ? mArrayList.get(0) : null;				
			}
			else {
				throw(new UnsupportedFlavorException(flavor));
			}
			return(obj);
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			if ( mDataFlavor != null ) {
				return(new DataFlavor[] { STRING_FLAVOR, ARRAY_FLAVOR, mDataFlavor } );
			}
			else {
				return(new DataFlavor[] { STRING_FLAVOR, ARRAY_FLAVOR } );
			}
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			boolean isSupported = false;
			for ( DataFlavor f : getTransferDataFlavors() ) {
				if ( f.equals(flavor) ) {
					isSupported = true;
					break;
				}
			}
			return(isSupported);
		}		
	}

}
