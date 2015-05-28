package com.purplecat.commons.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JLabel;
import javax.swing.Timer;

import com.google.inject.Inject;
import com.purplecat.commons.IResourceService;


/* 
 * A template for animation applications.
 */
public class AnimatorLabel extends WindowAdapter implements ActionListener {
	protected int 		mFrameNumber 	= -1;
    protected Timer 	mTimer			= null;
    protected JLabel 	mLabel			= null;
    protected boolean	mStarted		= false;
    
    protected final IImageRepository _images;
    
    @Inject
    public AnimatorLabel(IImageRepository images) {
    	_images = images;
    	
    	mLabel = new JLabel();
        mLabel.setIcon(_images.getImage("process-done.png"));
        

        //Set up a timer that calls this object's action handler.
        int delay = 50;
        mTimer = new Timer(delay, this);
        mTimer.setInitialDelay(0);
        mTimer.setCoalesce(true);
    }
    
    public JLabel getComponent() {
    	return mLabel;
    }

    public void startAnimation() {
    	mFrameNumber = 0;
        //Start animating!
    	if ( !mStarted ) {
	    	mStarted = true;
	        mTimer.start();
    	}
    }

    public void stopAnimation() {
        //Stop the animating thread.
    	if ( mStarted ) {
    		mStarted = false;
    		mTimer.stop();
            mLabel.setIcon(_images.getImage("process-done.png"));
    	}
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        //Advance the animation frame.
    	mLabel.setIcon(_images.getTimerIcon("process-working.png", mFrameNumber));
        mFrameNumber++;
    }
    
	@Override
    public void windowIconified(WindowEvent e) {
        stopAnimation();
    }
	
	@Override
    public void windowDeiconified(WindowEvent e) {
        startAnimation();
    }
	
	@Override
    public void windowClosing(WindowEvent e) {}    
}