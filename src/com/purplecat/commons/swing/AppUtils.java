package com.purplecat.commons.swing;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;

import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.OpenFilesHandler;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;
import com.purplecat.commons.swing.dragdrop.FileDrop;

public class AppUtils {
	
	public interface IQuitAction {
		public void onQuit();
	}
	
	public interface IDragDropAction extends FileDrop.Listener, OpenFilesHandler {}
	
	public static void setApplicationIcons(MyApplication app, JFrame frame, ArrayList<Image> list) {		
		if ( frame != null && !app.isMacOS() ) {
			frame.setIconImages(list);
		}
		else {
			int sz = list.size();
			if ( sz > 0 ) {
				app.setDockImage(list.get(sz-1));
			}
		}
	}
	
	public static class QuitOnCloseWindowListener extends WindowAdapter implements QuitHandler, Runnable {
		IQuitAction mQuit;
		
		public QuitOnCloseWindowListener(IQuitAction quitAction) {
			mQuit = quitAction;
		}
		
		/**
		 * Occurs when the window is closed (through the [x] or [red] buttons)
		 */
		@Override 
		public void windowClosing(WindowEvent e) {
			if ( mQuit != null ) {
				mQuit.onQuit();
			}
			
			System.exit(0);
		}

		/**
		 * Occurs when a keyboard combo is used to invoke a quit
		 */
		@Override
		public void handleQuitRequestWith(QuitEvent e, QuitResponse response) {
			if ( mQuit != null ) {
				mQuit.onQuit();
			}
			response.performQuit();
			
			System.exit(0);
		}

		/**
		 * Not sure when this runs. - From the button or menu, I think
		 */
		@Override
		public void run() {
			if ( mQuit != null ) {
				mQuit.onQuit();
			}
			
			System.exit(0);
		}
	}

}
