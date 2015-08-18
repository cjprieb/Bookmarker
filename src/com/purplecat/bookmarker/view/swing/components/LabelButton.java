package com.purplecat.bookmarker.view.swing.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

public class LabelButton extends JLabel implements MouseListener {
	
	public LabelButton() {
		this.addMouseListener(this);
	}
	
	public void addActionListener(ActionListener l) {
		this.listenerList.add(ActionListener.class, l);
	}
	
	public void removeActionListener(ActionListener l) {
		this.listenerList.remove(ActionListener.class, l);
	}
	
	protected void performAction(String type) {
		for ( ActionListener l : listenerList.getListeners(ActionListener.class) ) {
			l.actionPerformed(new ActionEvent(this, 0, type));
		}		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if ( e.getClickCount() == 1 ) {
			performAction("mouse-clicked");
		}
	}

	@Override public void mouseEntered(MouseEvent e) {}

	@Override public void mouseExited(MouseEvent e) {}

	@Override public void mousePressed(MouseEvent e) {}

	@Override public void mouseReleased(MouseEvent e) {}

}
