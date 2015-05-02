package com.purplecat.commons.swing;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.DefaultEditorKit;

import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.logs.LoggingService;


public class Toolbox {
	public static boolean OPEN_BROWSER			= true;
	public static boolean USING_NIMBUS_LAF		= false;
	public static Color COLOR_PANEL_BACKGROUND 	= new Color(0xD6, 0xD9, 0xDF);
	public static Color COLOR_DUSKY_BLUE 		= new Color(0x66, 0x99, 0xCC);
	public static ILoggingService Log = LoggingService.create();
	public static String TAG = "Toolbox";
	
	public static void setZeroInsets(JButton...components) {
		Insets zeroInsets = new Insets(0, 5, 0, 5);
		
		UIDefaults def = new UIDefaults();
		def.put("Button.contentMargins", zeroInsets);
		
		for ( JButton c : components ) {
			c.setMargin(zeroInsets);
			c.putClientProperty("Nimbus.Overrides", def);
		}
	}
	
	public static void setButtonInsets(Insets insets, JButton...components) {
		
		UIDefaults def = new UIDefaults();
		def.put("Button.contentMargins", insets);
		
		for ( JButton c : components ) {
			c.setMargin(insets);
			c.putClientProperty("Nimbus.Overrides", def);
		}
	}
	
	public static void copyTextToClipboard(String text) {
		if ( text != null ) {
			StringSelection selection = new StringSelection(text);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
		}
	}

	public static void browse(String url) {
		if ( OPEN_BROWSER ) {
			Log.log(0, TAG, "Opening \"" + url + "\" in default program");
	
			url = url.replaceAll(" ", "%20");
			try {
				URI uri = new URI(url);
				Desktop.getDesktop().browse(uri);
			}
			catch (IOException e) {
				Log.error(TAG, "IOException: Could not open \"" + url + "\" in default program" + e);
			} 
			catch (URISyntaxException e) {
				Log.error(TAG, "URISyntaxException: Could not open \"" + url + "\" in default program" + e);
			} 
		}
		else {
			Log.log(0, TAG, "Mock - opening \"" + url + "\" in default program");
		}
	}

	public static void browse(URL url) {
		if ( OPEN_BROWSER ) {
			Log.log(0, TAG, "Opening \"" + url + "\" in default program");
	
			try {
				URI uri = url.toURI();
				Desktop.getDesktop().browse(uri);
			}
			catch (IOException e) {
				Log.error(TAG, "IOException: Could not open \"" + url + "\" in default program" + e);
			} 
			catch (URISyntaxException e) {
				Log.error(TAG, "URISyntaxException: Could not open \"" + url + "\" in default program" + e);
			} 
		}
		else {
			Log.log(0, TAG, "Mock - opening \"" + url + "\" in default program");
		}
	}
	
	public static void fireOnUIThread(Runnable task) {		
		if ( SwingUtilities.isEventDispatchThread() ) {
			task.run();
		}
		else {
			EventQueue.invokeLater(task);
		}
	}

	/**
	 * this is a hack function that switches from the nimbus L&F to the 
	 * windows' system L&F so that the file-chooser dialogs look better.
	 * this change is not done on the mac.
	 * @return
	 */
	public static LookAndFeel setSystemLookAndFeel() {
		if ( System.getProperty("os.name").toLowerCase().startsWith("win") ) {
			LookAndFeel oldLookAndFeel = UIManager.getLookAndFeel();
			String name = UIManager.getSystemLookAndFeelClassName();

			try {
				UIManager.setLookAndFeel(name);
				Log.log(0, TAG, "Switching look and feel to " + name);
				return(oldLookAndFeel);
			} catch (ClassNotFoundException e) {
				Log.error(TAG, "ClassNotFoundException: Look & Feel \"" + name + "\" could not be set.", e);
			} catch (InstantiationException e) {
				Log.error(TAG, "InstantiationException: Look & Feel \"" + name + "\" could not be set.", e);
			} catch (IllegalAccessException e) {
				Log.error(TAG, "IllegalAccessException: Look & Feel \"" + name + "\"  could not be set.", e);
			} catch (UnsupportedLookAndFeelException e) {
				Log.error(TAG, "UnsupportedLookAndFeelException: Look & Feel \"" + name + "\"  could not be set.", e);
			}
		}
		return(null);
	}
	
	public static boolean setLookAndFeel(LookAndFeel name) {
		if ( System.getProperty("os.name").toLowerCase().startsWith("win") && name != null ) {
			try {
				UIManager.setLookAndFeel(name);
				Log.log(0, TAG, "Switching look and feel to " + name);
				return(true);
			} catch (UnsupportedLookAndFeelException e) {
				Log.error(TAG, "UnsupportedLookAndFeelException: Look & Feel \"" + name + "\"  could not be set.", e);
			}
		}	
		return(false);
	}
	
	public static Point getCenterScreenPoint(Dimension size) {		
		Point p = new Point(-1, -1);
		
		Dimension screen_d = Toolkit.getDefaultToolkit().getScreenSize();
		
		p.x = ( screen_d.width/2 - size.width/2 );
		p.y = ( screen_d.height/2 - size.height/2 );
		
		if ( p.x < 0 || p.y < 0 ) {
			p.x = 0; p.y = 0;
		}

		return(p);		
	}
	
	public static boolean setLookAndFeel(MyApplication app) {
		Log.log(0, TAG, "Setting Look and Feel");
		boolean valid = false;
		String lookAndFeelName = UIManager.getSystemLookAndFeelClassName();
		String nimbusLF = null;
		try {
			for ( LookAndFeelInfo info : UIManager.getInstalledLookAndFeels() ) {
				if ( info.getName().equals("Nimbus") ) {
					nimbusLF = info.getClassName();
					lookAndFeelName = nimbusLF;
					break;
				}
			}
			
//			if ( testLookAndFeel != null ) {
//				lookAndFeelName = testLookAndFeel;
//			}
			
			if ( app.isMacOS() ) {
				Log.log(0, TAG, "IS NOT WINDOWS!");
				System.setProperty("com.apple.laf.useScreenMenuBar", "true");
				System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Bookmarks");
			}
			
			Log.debug(1, TAG, "Setting it to: " + lookAndFeelName);		
			javax.swing.UIManager.setLookAndFeel(lookAndFeelName);


			if ( app.isMacOS() ) {			
				//Setting KeyStrokes so that Cmd+[C,V,X] works instead of Ctrl+[C,V,X] in text fields.
				//TODO: verify this works - I'm having to use Ctrl on some fields but I can't remember which
				InputMap im = (InputMap) UIManager.get("TextField.focusInputMap");
				if ( im != null ) {
					Log.log(1, TAG, "setting text field input map");
					im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
					im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
					im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);
					im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_DOWN_MASK), DefaultEditorKit.selectAllAction);
				}
				
				im = (InputMap) UIManager.get("TextArea.focusInputMap");
				if ( im != null ) {
					Log.log(1, TAG, "setting text area input map");
					im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
					im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
					im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);
					im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_DOWN_MASK), DefaultEditorKit.selectAllAction);
				}
			}
			valid = true;

		} catch (Exception e) {
			Log.error(lookAndFeelName + TAG, " is an invalid name", e);
			if ( nimbusLF != null ) {
				try {				
					USING_NIMBUS_LAF = true;
					Log.debug(1, TAG, "Resetting it to: " + nimbusLF);		
					javax.swing.UIManager.setLookAndFeel(nimbusLF);
					valid = true;
				} catch (Exception e2) {				
					USING_NIMBUS_LAF = false;
					Log.error(TAG, lookAndFeelName + " is an invalid name", e);				
				}
			}
		}
//TODO: If having repaint or UI lag issues, turn on.
//		if ( LoggingService.ENABLE_EDT_DEBUG ) {
//			RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager());
//		}
		return(valid);
	}
}
