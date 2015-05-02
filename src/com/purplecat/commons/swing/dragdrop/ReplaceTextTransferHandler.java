package com.purplecat.commons.swing.dragdrop;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.TransferHandler;
import javax.swing.text.JTextComponent;

public class ReplaceTextTransferHandler extends TransferHandler {
	private static final long serialVersionUID = -1759834354728209319L;
	private JTextComponent mComponent;
	private ActionListener mAction;

	public ReplaceTextTransferHandler(JTextComponent text,
			ActionListener action) {
		mComponent = text;
		mAction = action;
	}

	@Override
	public boolean canImport(TransferHandler.TransferSupport info) {
		if (!info.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			return (false);
		}
		return (true);
	}

	@Override
	public boolean importData(TransferHandler.TransferSupport info) {
		if (!info.isDrop()) {
			return (false);
		}

		// Component component = info.getComponent();
		if (mComponent != null) {
			try {
				mComponent.setText((String) info.getTransferable()
						.getTransferData(DataFlavor.stringFlavor));

				// if ( component instanceof UpdateListener ) {
				// ((UpdateListener)component).doUpdateAction(new
				// UpdateEvent(this, mComponent));
				// }
			} catch (UnsupportedFlavorException e) {
				return (false);
			} catch (IOException e) {
				return (false);
			}
		}
		if (mAction != null) {
			mAction.actionPerformed(new ActionEvent(mComponent, 0,
					"texttransfer"));
		}

		return (true);
	}
}