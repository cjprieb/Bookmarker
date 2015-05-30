package com.purplecat.bookmarker.view.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.google.inject.Inject;
import com.purplecat.commons.swing.AnimatorLabel;
import com.purplecat.commons.swing.IImageRepository;

public class GlassTimerPanel extends JPanel {
	private JFrame			mFrame;
	private JComponent		mCoverPanel;
	private AnimatorLabel 	mTimer;
	private JProgressBar	mProgressBar;
	
	private IImageRepository _imageRepository;
	
	private CoverPanelBoundsListener mBoundsListener = new CoverPanelBoundsListener();
	private MouseCaptureListener mMouseListener = new MouseCaptureListener();
	
	@Inject
	public GlassTimerPanel(IImageRepository repository, JFrame frame) {
		_imageRepository = repository;
		mFrame = frame;		
		mFrame.getLayeredPane().add(this, JLayeredPane.PALETTE_LAYER);
		mProgressBar = new JProgressBar();
		
		initGui();
	}
	
	public void setCoverPanel(JComponent panel) {
		if ( mCoverPanel != panel ) {
			if ( mCoverPanel != null ) {
				mCoverPanel.removeHierarchyBoundsListener(mBoundsListener);
			}
			mCoverPanel = panel;			
			mCoverPanel.addHierarchyBoundsListener(mBoundsListener);
			updateBounds();
		}
	}
	
	protected void initGui() {
		this.setOpaque(false);
		this.addMouseListener(mMouseListener);
		this.addMouseMotionListener(mMouseListener);
		
		mTimer = new AnimatorLabel(_imageRepository);
		mFrame.addWindowListener(mTimer);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addGap(0, 10, Short.MAX_VALUE)
			.addComponent(mTimer.getComponent(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			.addGap(0, 10, Short.MAX_VALUE)
			.addComponent(mProgressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			.addContainerGap()
		);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addGap(0, 10, Short.MAX_VALUE)
					.addComponent(mTimer.getComponent(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(0, 10, Short.MAX_VALUE)
				)
					.addComponent(mProgressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
			)
			.addContainerGap()
		);
	}
	
	@Override
	public void paint(Graphics g) {
		g.setColor(new Color(0x66, 0x66, 0x66, 0x40));
		g.fillRect(0, 0, getWidth(), getHeight());
		super.paint(g);
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		if ( !b ) {
			stopTimer();
		}
	}

	public void startTimer() {
		this.setVisible(true);
		mTimer.startAnimation();
	}

	public void stopTimer() {
		mTimer.stopAnimation();
	}
	
	public void setProgress(int progress, int max) {
		mProgressBar.setValue(progress);
		mProgressBar.setMaximum(max);
	}
	
	public void updateBounds() {		
		Dimension size = mCoverPanel.getSize();
		Point location = mCoverPanel.getLocation();
		Point p = SwingUtilities.convertPoint(mCoverPanel.getParent(), location, mFrame.getLayeredPane());	

		int x = p.x;
		int y = p.y;
		int width = size.width;
		int height = size.height;
		
//		Log.logMessage(0, "update bounds - " + (mCoverPanel != null ? "using cover panel" : "using frame") );
//		Log.logMessage(1, "panel point (" + p.x + ", " + p.y + ")");
//		Log.logMessage(1, "point: (" + x + ", " + y + ")");
//		Log.logMessage(1, "size: " + width + " x " + height);
//		Log.logMessage(2, "is visible " + isVisible());
		
		setBounds(x, y, width, height);
		repaint();
	}

	public class CoverPanelBoundsListener implements HierarchyBoundsListener { 
        @Override
        public void ancestorMoved(HierarchyEvent e) {}
        
        @Override
        public void ancestorResized(HierarchyEvent e) {
        	updateBounds();
        }            
    }
	
	public class MouseCaptureListener extends MouseAdapter {
		
		public MouseCaptureListener() {}
		
		@Override
		public void mouseDragged (MouseEvent e) {}
		
		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}
	}
}
