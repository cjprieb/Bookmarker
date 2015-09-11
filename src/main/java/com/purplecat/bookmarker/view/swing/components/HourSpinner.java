package com.purplecat.bookmarker.view.swing.components;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.ParseException;
import java.util.ArrayList;

import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.purplecat.bookmarker.Resources;
import com.purplecat.commons.IResourceService;

/*
 * Place an HourSpinnerField next to an HourSpinner to show whether the 
 * units are in minutes or hours.
 */
public class HourSpinner extends JSpinner {
	public HourSpinner(IResourceService resources) {		
		setModel(new HourModel());			
		setEditor(new HourSpinnerField(resources, this));
		
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
	
	public double getHourValue() {
		return(((HourModel)this.getModel()).mHourValue);
	}
	
	private class HourModel implements SpinnerModel {
		private ArrayList<ChangeListener> mListeners = new ArrayList<ChangeListener>();
		private int mHourValue = 0;

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
			int newValue = mHourValue;			
			if ( mHourValue < 8 ){
				newValue += 1;
			}
			else if ( mHourValue < 24 ) {
				newValue += 4;
			}
			else {
				newValue += 12;
			}
			return(newValue);
		}

		@Override
		public Object getPreviousValue() {
			int newValue = mHourValue;			
			if ( mHourValue <= 1 ) {
				newValue = 0;
			}
			else if ( mHourValue <= 8 ){
				newValue -= 1;
			}
			else if ( mHourValue <= 24 ) {
				newValue -= 4;
			}
			else {
				newValue -= 12;
			}
			return(newValue);
		}

		@Override
		public Object getValue() {
			return(mHourValue);
		}

		@Override
		public void setValue(Object value) {
			if ( value instanceof Integer ) {
				mHourValue = (Integer)value;
				fireChange();
			}
		}
		
		protected void fireChange() {
			ChangeEvent e = new ChangeEvent(this);
			for ( ChangeListener l : mListeners ) {
				l.stateChanged(e);
			}
		}
	}
	
	public static class HourSpinnerField extends JFormattedTextField implements ChangeListener {
		
		public HourSpinnerField(IResourceService resources, JSpinner spinner) {
			super(new HourFormatField(resources));
			this.setColumns(6);
			spinner.addChangeListener(this);
			this.setHorizontalAlignment(JTextField.RIGHT);
		}	
		
		@Override
		public void stateChanged(ChangeEvent e) {
			setValue( ((JSpinner)e.getSource()).getValue() );
		}	
	};
	
	public static class HourFormatField extends AbstractFormatter {
		private final IResourceService _resources;
		
		public HourFormatField(IResourceService resources) {
			_resources = resources;
		}
		

		@Override
		public Object stringToValue(String text) throws ParseException {
			int space = text.indexOf(' ');
			if ( space < 0 ) {
				throw (new ParseException("ParseError: " + text + " contains no space.", 0));
			}
			else {
				try {
					int i = Integer.valueOf(text.substring(0, space));
					return(i);
				}
				catch (NumberFormatException e) {
					throw (new ParseException("ParseError: " + text + " contains an invalid number", space));
				}
			}
		}

		@Override
		public String valueToString(Object value) throws ParseException {
			String s = "";
			if ( value == null )  value = 0.0;
			
			if ( value instanceof Integer ) {
				int i = (Integer)value;
				s = (i + " " + _resources.getString(Resources.string.lblAbbrHour));
			}
			else {
				throw (new ParseException("ParseError: " + value + " is not a double.", 0));
			}
			return(s);
		}
		
	}
}
