package com.purplecat.bookmarker.view.swing.ignored;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;

import com.purplecat.commons.IFormatter;
import com.purplecat.bookmarks.model.styles.CalendarFormatter;
import com.purplecat.bookmarks.model.styles.DateFormatter;
import com.purplecat.bookmarks.model.styles.MovieFormatter;
import com.purplecat.bookmarks.model.styles.PlaceFormatter;

public class FormatterFactories {
	
	private static class TextFieldFormatter extends AbstractFormatter {
		IFormatter<?> mFormatter;
		
		public TextFieldFormatter(IFormatter<?> formatter) {
			mFormatter = formatter;
		}

		@Override
		public Object stringToValue(String text) throws ParseException {
			return(mFormatter.stringToValue(text));
		}

		@Override
		public String valueToString(Object value) throws ParseException {
			return(mFormatter.objectToString(value));
		}
	}
	
	public static class DateFormatterFactory  extends AbstractFormatterFactory {	
		private TextFieldFormatter mDateFormatter = null;

		@Override
		public AbstractFormatter getFormatter(JFormattedTextField field) {
			if ( mDateFormatter == null ) {
				mDateFormatter = new TextFieldFormatter(new DateFormatter());
			}
			return (mDateFormatter);
		}
	} 
	
	public static class PlaceFormatterFactory extends AbstractFormatterFactory {
		private TextFieldFormatter mEditorFormatter = null;
		private TextFieldFormatter mDisplayFormatter = null;

		/*
		 * Inherited from AbstractFormatterFactory
		 */
		@Override
		public AbstractFormatter getFormatter(JFormattedTextField field) {
			if ( field.hasFocus() ) {
				if ( mEditorFormatter == null ) {
					mEditorFormatter = new TextFieldFormatter(new PlaceFormatter(true));
				}
				return (mEditorFormatter);
			}
			else {
				if ( mDisplayFormatter == null ) {
					mDisplayFormatter = new TextFieldFormatter(new PlaceFormatter(false));
				}
				return (mDisplayFormatter);
			}	
		}		
	}
	
	public static class MovieFormatterFactory extends AbstractFormatterFactory {
		private TextFieldFormatter mEditorFormatter = null;
		private TextFieldFormatter mDisplayFormatter = null;

		/*
		 * Inherited from AbstractFormatterFactory
		 */
		@Override
		public AbstractFormatter getFormatter(JFormattedTextField field) {
			if ( field.hasFocus() ) {
				if ( mEditorFormatter == null ) {
					mEditorFormatter = new TextFieldFormatter(new MovieFormatter(true));
				}
				return (mEditorFormatter);
			}
			else {
				if ( mDisplayFormatter == null ) {
					mDisplayFormatter = new TextFieldFormatter(new MovieFormatter(false));
				}
				return (mDisplayFormatter);
			}	
		}		
	}
	
	public static class CalendarFormatterFactory extends AbstractFormatterFactory {
		private String 				mFormatString = "";
		private TextFieldFormatter 	mEditorFormatter = null;
		private TextFieldFormatter 	mDisplayFormatter = null;
		
		public CalendarFormatterFactory(String format) {
			mFormatString = format;
		}

		/*
		 * Inherited from AbstractFormatterFactory
		 */
		@Override
		public AbstractFormatter getFormatter(JFormattedTextField field) {
			if ( field.hasFocus() ) {
				if ( mEditorFormatter == null ) {
					mEditorFormatter = new TextFieldFormatter(new CalendarFormatter(true, mFormatString));
				}
				return (mEditorFormatter);
			}
			else {
				if ( mDisplayFormatter == null ) {
					mDisplayFormatter = new TextFieldFormatter(new CalendarFormatter(false, mFormatString));
				}
				return (mDisplayFormatter);
			}	
		}		
	}

}
