package com.purplecat.bookmarker.dummies;

import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;

import com.apple.eawt.AppEvent.OpenFilesEvent;
import com.purplecat.commons.swing.AppUtils.IDragDropAction;

public class DummyDragDrop implements IDragDropAction {

	@Override
	public void filesDropped(Component c, File[] files) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void listDropped(Component c, List<?> list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void objectDropped(Component c, Transferable obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stringDropped(Component c, String s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openFiles(OpenFilesEvent e) {
		// TODO Auto-generated method stub
		
	}

}
