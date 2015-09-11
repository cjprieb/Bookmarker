package com.purplecat.bookmarker.view.swing.components;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.purplecat.commons.extensions.Numbers;

public class PlaceSpinner extends JSpinner {
	public PlaceSpinner(String label, int columns) {
		setModel(new PlaceModel());			
		setEditor(new PlaceSpinnerField(label, columns));
		
		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if ( e.getWheelRotation() < 0 ) {
					setValue(getNextValue());
				}
				else if ( e.getWheelRotation() > 0 ) {
					setValue(getPreviousValue());
				}
			}			
		});
	}
	
	public int getIntegerValue() {
		return(((PlaceModel)this.getModel()).mValue);
	}
	
	private class PlaceModel implements SpinnerModel {
		private ArrayList<ChangeListener> mListeners = new ArrayList<ChangeListener>();
		private int mValue = 0;

		@Override
		public void addChangeListener(ChangeListener l) {
			mListeners.add(l);
		}

		@Override
		public void removeChangeListener(ChangeListener l) {
			mListeners.remove(l);			
		}

		@Override
		public Object getNextValue() {
			return(mValue+1);
		}

		@Override
		public Object getPreviousValue() {		
			if ( mValue > 0 ) {
				return(mValue-1);
			}
			else {
				return(mValue);
			}
		}

		@Override
		public Object getValue() {
			return(mValue);
		}

		@Override
		public void setValue(Object value) {
			if ( value instanceof Integer ) {
				mValue = (Integer)value;
				fireChange();
			}
			else if ( value != null ) {
				String text = value.toString().trim();
				if ( text.length() > 0 ) {
					mValue = Numbers.parseInt(value.toString(), 0);
					fireChange();
				}
			}
		}
		
		protected void fireChange() {
			ChangeEvent e = new ChangeEvent(this);
			for ( ChangeListener l : mListeners ) {
				l.stateChanged(e);
			}
		}
	}
	
	private class PlaceSpinnerField extends JTextField implements ChangeListener, KeyListener {
		String mLabel = "";
		
		private PlaceSpinnerField(String label, int columns) {
			super(columns);
			mLabel = label;
			PlaceSpinner.this.addChangeListener(this);
			this.addKeyListener(this);
			this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK), "none");
		}	
		
		@Override
		public void stateChanged(ChangeEvent e) {
			setText( mLabel + String.valueOf(PlaceSpinner.this.getValue()) );
		}	

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if ( Character.isDigit(e.getKeyChar()) ) {
				String text = this.getText();
				PlaceSpinner.this.setValue(text.substring(mLabel.length(), text.length()));				
			} 
		}

		@Override
		public void keyTyped(KeyEvent e) {
			char c = e.getKeyChar();
			if ( !Character.isDigit(c) && c != KeyEvent.VK_DELETE && c != KeyEvent.VK_BACK_SPACE ) {
				e.consume();
			}
		}
	};
}
