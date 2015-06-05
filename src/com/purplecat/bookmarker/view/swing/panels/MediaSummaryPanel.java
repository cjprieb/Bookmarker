package com.purplecat.bookmarker.view.swing.panels;

import java.awt.Color;
import java.text.DecimalFormat;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle;
import javax.swing.text.TextAction;

import org.joda.time.DateTime;

import com.google.inject.Inject;
import com.purplecat.bookmarker.Resources;
import com.purplecat.bookmarker.extensions.PlaceExt;
import com.purplecat.bookmarker.models.Genre;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.Place;
import com.purplecat.bookmarker.view.swing.DefaultColors;
import com.purplecat.bookmarker.view.swing.DefaultSizes;
import com.purplecat.bookmarker.view.swing.html.SummaryTextArea;
import com.purplecat.bookmarker.view.swing.html.SummaryTextRow;
import com.purplecat.bookmarker.view.swing.observers.LinkClickObserver;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.utils.StringUtils;

public class MediaSummaryPanel {
	private static double 			MAX_RATING		= 10.0;
	private static DecimalFormat 	RATING_FORMAT 	= new DecimalFormat("#.00");

	protected static String TAG = "SavedMediaSummaryPanel";
	@Inject IResourceService _resources;
	@Inject ILoggingService _logger;
	@Inject LinkClickObserver.Factory _factory;
	
	JPanel _panel;
	Media _currentMedia;
	
	SummaryTextArea _dataTitle;
	SummaryTextRow _dataAltTitles;
	SummaryTextRow _dataAuthor;
	SummaryTextRow _dataLastRead;
	SummaryTextRow _dataUpdated;
	SummaryTextRow _dataGenres;
	SummaryTextRow _dataCategories;
	SummaryTextRow _dataRating;
	SummaryTextRow _dataDescription;
	SummaryTextRow _dataSummary;
	SummaryTextRow _dataStatus;
	SummaryTextRow _dataType;
	SummaryTextRow _dataLinks;
	
	JProgressBar _progress;
	
	LinkClickObserver _dataAuthorLink;
	LinkClickObserver _dataChapterLink;
	LinkClickObserver _dataSiteLink;
	LinkClickObserver _dataUpdatedLink;
	
	JPopupMenu _popup = null;
	TextAction _copyLinkURLAction;
	TextAction _openLinkAction;

	public MediaSummaryPanel() {
		super();
	}

	public void create() {
			_panel = new JPanel();
			_panel.setBackground(DefaultColors.PANEL_BACKGROUND_COLOR);
			{
				_dataTitle = new SummaryTextArea();
				_dataTitle.setFont(_dataTitle.getFont().deriveFont(DefaultSizes.TITLE_FONT_SIZE));
				
				_dataAltTitles 		= new SummaryTextRow(_resources.getString(Resources.string.lblAltTitles));
				_dataAuthor 		= new SummaryTextRow(_resources.getString(Resources.string.lblAuthor));			
				_dataLastRead 		= new SummaryTextRow(_resources.getString(Resources.string.htmlLastUpdated));			
				_dataUpdated 		= new SummaryTextRow(_resources.getString(Resources.string.htmlUpdated));		
				_dataGenres 		= new SummaryTextRow(_resources.getString(Resources.string.lblGenres));
				_dataCategories		= new SummaryTextRow(_resources.getString(Resources.string.lblCategories));
				_dataRating			= new SummaryTextRow(_resources.getString(Resources.string.lblRating));
				_dataDescription	= new SummaryTextRow(_resources.getString(Resources.string.lblDescription));
				_dataSummary		= new SummaryTextRow(_resources.getString(Resources.string.lblSummary));
				_dataStatus			= new SummaryTextRow(_resources.getString(Resources.string.lblStatus));
				_dataType			= new SummaryTextRow(_resources.getString(Resources.string.lblType));
				_dataLinks			= new SummaryTextRow(_resources.getString(Resources.string.lblLinks));	
	
				_dataAuthorLink		= _factory.create(_dataAuthor._textArea);	
				_dataChapterLink	= _factory.create(new SummaryTextArea(_resources.getString(Resources.string.htmlLastChapter)));		
				_dataSiteLink 		= _factory.create(new SummaryTextArea(_resources.getString(Resources.string.htmlTitlePage)));	
				_dataUpdatedLink	= _factory.create(new SummaryTextArea(_resources.getString(Resources.string.htmlUpdatedChapter)));
				
				_progress = new JProgressBar();
				_progress.setIndeterminate(true);
				_progress.setVisible(false);
				
				_dataGenres.getDataComponent().setForeground(DefaultColors.TAG_LIST_COLOR);
				_dataCategories.getDataComponent().setForeground(DefaultColors.TAG_LIST_COLOR);
		
	//			mQueryAuthorAction.putValue(Action.NAME, "Search for author");
	//			mQueryAuthorAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);				
	//			_dataAuthorLink._popupMenu.add(new JMenuItem(mQueryAuthorAction));
			}
			{
				GroupLayout layout = new GroupLayout(_panel);
				_panel.setLayout(layout);
				
				layout.setVerticalGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addComponent(_dataTitle, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(_dataAltTitles._label, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(_dataAltTitles._textArea, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					)
					.addComponent(_dataAltTitles._separator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(_dataAuthor._label, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(_dataAuthor._textArea, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					)
					.addComponent(_dataAuthor._separator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(_dataLastRead._label, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(_dataLastRead._textArea, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					)
					.addComponent(_dataLastRead._separator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(_dataUpdated._label, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(_dataUpdated._textArea, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					)
					.addComponent(_dataUpdated._separator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(_dataGenres._label, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(_dataGenres._textArea, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					)
					.addComponent(_dataGenres._separator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(_dataCategories._label, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(_dataCategories._textArea, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					)
					.addComponent(_dataCategories._separator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(_dataRating._label, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(_dataRating._textArea, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					)
					.addComponent(_dataRating._separator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(_dataDescription._label, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(_dataDescription._textArea, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					)
					.addComponent(_dataDescription._separator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(_dataLinks._label, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGroup(layout.createSequentialGroup()
							.addComponent(_dataChapterLink._textControl, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(_dataSiteLink._textControl, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(_dataUpdatedLink._textControl, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
					)
					.addComponent(_dataLinks._separator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(_progress, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(_dataSummary._textArea, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(0, 10, Short.MAX_VALUE)
					.addContainerGap());
				
				layout.setHorizontalGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(layout.createParallelGroup()
						.addComponent(_dataTitle)
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup(Alignment.LEADING)
								.addComponent(_dataAltTitles._label, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(_dataAuthor._label, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(_dataLastRead._label, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(_dataUpdated._label, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(_dataGenres._label, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(_dataCategories._label, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(_dataRating._label, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(_dataDescription._label, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(_dataLinks._label, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(layout.createParallelGroup()
								.addComponent(_dataAltTitles._textArea, 10, 100, Short.MAX_VALUE)
								.addComponent(_dataAuthor._textArea, 10, 100, Short.MAX_VALUE)
								.addComponent(_dataLastRead._textArea, 10, 100, Short.MAX_VALUE)
								.addComponent(_dataUpdated._textArea, 10, 100, Short.MAX_VALUE)
								.addComponent(_dataGenres._textArea, 10, 100, Short.MAX_VALUE)
								.addComponent(_dataCategories._textArea, 10, 100, Short.MAX_VALUE)
								.addComponent(_dataRating._textArea, 10, 100, Short.MAX_VALUE)
								.addComponent(_dataDescription._textArea, 10, 100, Short.MAX_VALUE)
								.addComponent(_dataChapterLink._textControl, 10, 100, Short.MAX_VALUE)
								.addComponent(_dataSiteLink._textControl, 10, 100, Short.MAX_VALUE)
								.addComponent(_dataUpdatedLink._textControl, 10, 100, Short.MAX_VALUE)
							)
						)
						.addComponent(_progress)
						.addComponent(_dataSummary._textArea, 10, 100, Short.MAX_VALUE)
						.addComponent(_dataAltTitles._separator)
						.addComponent(_dataAuthor._separator)
						.addComponent(_dataLastRead._separator)
						.addComponent(_dataUpdated._separator)
						.addComponent(_dataGenres._separator)
						.addComponent(_dataCategories._separator)
						.addComponent(_dataRating._separator)
						.addComponent(_dataDescription._separator)
						.addComponent(_dataLinks._separator)
						)
					.addContainerGap()
					);
			}
		}

	public JPanel getPanel() {
		return _panel;
	}

	protected void setPlaceAndDateTime(Place lastReadPlace, DateTime lastReadDate,
			Place updatedPlace, DateTime updatedDate) {
				String lastReadStr = PlaceExt.formatPlaceAndDate(_resources, lastReadPlace, lastReadDate);
				_dataLastRead.setText(lastReadStr);
				_dataLastRead.setVisible(lastReadStr.length() > 0);	
			
				String updatedStr = PlaceExt.formatPlaceAndDate(_resources, updatedPlace, updatedDate);
				_dataUpdated.setText(updatedStr);
				_dataUpdated.setVisible(updatedStr.length() > 0);	
			}

	protected void setUpdateColor(boolean isUpdated, boolean isRead) {
		Color textColor = Color.black;
		if ( isUpdated ) {
			textColor = DefaultColors.UPDATE_FOREGROUND_COLOR;
		}
		else if ( isRead ) {
			textColor = DefaultColors.READ_FOREGROUND_COLOR;
		}
		_dataUpdated.getDataComponent().setForeground(textColor);
		_dataTitle.setForeground(textColor);		
	}

	protected void setSummary(String sSummary) {
		if ( !StringUtils.isNullOrEmpty(sSummary) ) {
			_dataSummary.setText("<html>" + sSummary);
		}
		else {
			_dataSummary.setText("");
		}
	}

	protected void setDescription(String desc) {		
		if ( !StringUtils.isNullOrEmpty(desc) ) {
			_dataDescription.setText(desc);
			_dataDescription.setVisible(true);
		}		
		else {
			_dataDescription.setVisible(false);			
		}
	}

	protected void setLink(LinkClickObserver ctrl, String sUrl) {
		if ( !StringUtils.isNullOrEmpty(sUrl) ) {
			ctrl._url = sUrl;
			ctrl._textControl.setVisible(true);
		}
		else {
			ctrl._url = "";
			ctrl._textControl.setVisible(false);
		}
	}
	
	protected void setRating(double dRating) {
		if ( dRating > 0 ) {
			StringBuilder ratingStr = new StringBuilder();		
			ratingStr.append(RATING_FORMAT.format(MAX_RATING * dRating));
			ratingStr.append(" / ");
			ratingStr.append(RATING_FORMAT.format(MAX_RATING));
			_dataRating.setText(ratingStr.toString());
			_dataRating.setVisible(true);
		}
		else {
			_dataRating.setText("");			
		}		
	}
	
	protected void setGenres(Iterable<Genre> genreList) {
		StringBuilder genres = new StringBuilder();
		for ( Genre genre : genreList ) {
			if ( genres.length() > 0 ) { genres.append(", "); }
			genres.append(genre._name);
		}
		_dataGenres.setText(genres.toString());		
	}
}