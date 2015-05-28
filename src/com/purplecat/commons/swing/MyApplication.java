package com.purplecat.commons.swing;

import java.awt.EventQueue;
import java.awt.Image;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.apple.eawt.Application;
import com.google.inject.Inject;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.swing.AppUtils.IDragDropAction;
import com.purplecat.commons.swing.AppUtils.IQuitAction;
import com.purplecat.commons.swing.AppUtils.QuitOnCloseWindowListener;

public abstract class MyApplication implements IQuitAction {	
	
	public static final String TAG = "MyApplication";
	
	public void startApplication() {		
		EventQueue.invokeLater(new Runnable() {
			@Override
		    public void run() {	
				initializeFrame();
				finalSetup();
		    }
		});
	}
	
	private ArrayList<Runnable> 		mQuitActions 	= new ArrayList<Runnable>();
	protected QuitOnCloseWindowListener	mQuitAction		= null;
	protected JFrame 					mFrame 			= null;
	protected IDragDropAction			mDragDropAction	= null;
	
	@Inject ILoggingService _logging;
	@Inject Toolbox _toolbox;
		
	/**
	 * This function allows the application to setup the main
	 * panel and the file drop action.
	 */
	abstract protected void setupMainPanel(JFrame frame);
	
	/**
	 * If there isn't an application icon created, return NULL.
	 * @return The application icon that should be displayed in the taskbar/dock.  
	 */
	abstract protected ImageIcon getApplicationIcon();
	
	/**
	 * This function should return the value of APP_NAME as determined by the 
	 * package name: "/com/purplecat/[APP_NAME]/" It SHOULD NOT begin or end with a slash.
	 * @return the application's source path 
	 */
	abstract protected String getApplicationName();
	
	/**
	 * 
	 * @return The title of the application (for the title bar and taskbar/dock)
	 */
	abstract protected String getApplicationTitle();
	
	/**
	 * Setups the actions for menus and buttons
	 * ActionRepository.updateAction(ActionIds.[ID NAME], [Runnable])
	 */
	abstract protected void setupActions();
	
	/**
	 * The last function called before displaying the frame.
	 */
	abstract protected void finalInitialization();
	
	/**
	 * 
	 * @return The task to run on a separate thread once the frame is visible
	 */
	abstract protected Runnable getOnVisibleAction();
	
	/**
	 * Actions to perform when the application quits, 
	 * such as saving preferences, files, and clearing the log buffer. 
	 */
	abstract protected void applicationQuit();
	
	/**
	 * Sets up the main frame and sets the application values.
	 */
	protected void initializeFrame() {		
		mFrame = new JFrame();		
		setupMainPanel(mFrame);	
		mFrame.pack();
		
		//Default Frame setup
		mFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mFrame.setTitle(getApplicationTitle());
		
		//Set preferences
		
		//Set Quit Action
		mQuitAction = new QuitOnCloseWindowListener(this);
		mFrame.addWindowListener(mQuitAction);
		Application theApplication = Application.getApplication();	
		if ( theApplication != null ) {
			theApplication.setQuitHandler(mQuitAction);
		}
		
		//Set Dock Image
		ImageIcon image = getApplicationIcon();		
		if ( image != null ) {
			setDockImage(image.getImage());
		}
		
		//Call abstract functions (implemented by children)
		setupActions();
		finalInitialization();
		mFrame.setVisible(true);
	}
	
	/**
	 * Called once the frame is visible and runs the OnVisibleAction
	 * on a separate thread so that objects can be loaded from the
	 * database or files.
	 */
	protected void finalSetup() {
		Runnable loadFunction = getOnVisibleAction();
		if ( loadFunction != null ) {
			new Thread(loadFunction).start();
		}
	}
	
	/**
	 * Sizes and positions frame based on the last saved postions
	 * @param prefs - the child app's prefs (since we want them separate per app)
	 * @param frame
	 */
	protected void loadPreferences(Preferences prefs, boolean setDimensions) {		
		int extendedState = prefs.getInt("extendedState", mFrame.getExtendedState());
		if ( extendedState == JFrame.MAXIMIZED_BOTH ) {
			mFrame.setExtendedState(extendedState);
		}
		else {		
			int width = prefs.node("size").getInt("width", -1);
			int height = prefs.node("size").getInt("height", -1);
			_logging.log(1, TAG, "orig/width " + mFrame.getSize().width);
			_logging.log(1, TAG, "orig/height " + mFrame.getSize().height);
			if ( width > 0 && height > 0 ) {
				//mFrame.setPreferredSize(new Dimension(width, height));
				//mFrame.setSize(width, height);
			}
			_logging.log(1, TAG, "size/width " + width);
			_logging.log(1, TAG, "size/height " + height);
			
			int x = prefs.node("location").getInt("x", -1);
			int y = prefs.node("location").getInt("y", -1);
			if ( x >= 0 && y >= 0 ) {
				mFrame.setLocation(x, y);
			}
			else {
				mFrame.setLocation(_toolbox.getCenterScreenPoint(mFrame.getSize()));
			}		
			_logging.log(1, TAG, "location/x " + x);
			_logging.log(1, TAG, "location/y " + y);
		}
	}
	
	public void setDockImage(Image image) {
		if ( isMacOS() ) {
			Application theApplication = Application.getApplication();			
			if ( theApplication != null && image != null ) {
				theApplication.setDockIconImage(image);
			}
		}
		else {
			mFrame.setIconImage(image);
		}
	}
	
	public boolean isMacOS() {
		return(!System.getProperty("os.name").toLowerCase().startsWith("win"));
	}
	
	@Override 
	public final void onQuit() {
		_logging.log(1, TAG, "Program Exiting");
		applicationQuit();
		
		for ( Runnable action : mQuitActions ) {
			action.run();
		}
	}
	
	public void setFileDropAction(IDragDropAction dragDropAction) {
		mDragDropAction = dragDropAction;
		Application theApplication = Application.getApplication();
		if ( theApplication != null && dragDropAction != null ) {
			theApplication.setOpenFileHandler(dragDropAction);
		}
	}

}
