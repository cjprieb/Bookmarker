package com.purplecat.bookmarker.view.swing.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.GroupLayout.Alignment;

import com.google.inject.Inject;
import com.purplecat.bookmarker.Resources;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.models.EStoryState;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.SavedMediaQuery;
import com.purplecat.bookmarker.view.swing.components.SavedMediaTableControl;
import com.purplecat.bookmarker.view.swing.components.StoryStateButton;
import com.purplecat.bookmarker.view.swing.observers.SavedMediaSummaryObserver;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.swing.CXableTextField;

public class SavedMediaTab {
	private JPanel _panel;
	
	@Inject Controller _controller;
	@Inject IResourceService _resources;
	@Inject SummarySidebar _summaryPanel;
	@Inject SavedMediaSummaryObserver _summaryObserver;
	@Inject SavedMediaTableControl _savedMediaTableControl;
	
	@Inject StoryStateButton _btnStoryState;
	
	private JCheckBox mChkUpdated;
	private JLabel mLblKeyword;
//	private JComboBox<KeyValueLabel> mCmbKeyword;
	private JTextField mTxtKeyword;
//	private JLabel mLblAuthor;
//	private JTextField mTxtAuthor;
//	private FavoriteStateButton mBtnFavState;
//	private GenreWindow mGenreWindow;
	private JButton mBtnReset;
//	private JLabel mLblWebsites;
//	private WebsiteComboBox	mCmbWebsites;
	
	public void create() {		
		_controller.observeSummaryLoading(_summaryObserver);
		_savedMediaTableControl.getTable().addRowSelectionListener(_summaryObserver);

		_panel = new JPanel();
		GroupLayout layout = new GroupLayout(_panel);
		_panel.setLayout(layout);

		{			
//			mCmbKeyword = new JComboBox<KeyValueLabel>(mFieldOptions);
//			mCmbKeyword.addItemListener(KeywordItemListener);
			
			mLblKeyword = new JLabel();
			mLblKeyword.setText(_resources.getString(Resources.string.lblKeyword));
			mTxtKeyword = new CXableTextField();
			mTxtKeyword.addKeyListener(_textBoxKeyListener);
		}
//		{
//			mLblAuthor = new JLabel();
//			mLblAuthor.setText(BookmarkOptions.getString(Resources.string.lblAuthor));
//			mTxtAuthor = new CXableTextField();
//			mTxtAuthor.addKeyListener(TextBoxKeyListener);
//		}
		{
			mChkUpdated = new JCheckBox();
			mChkUpdated.setText(_resources.getString(Resources.string.lblUpdated));
			mChkUpdated.addActionListener(_queryComponentListener);
		}
//		{
//			mBtnFavState = new FavoriteStateButton();
//			mBtnFavState.addActionListener(mQueryListener);
//			mBtnFavState.setFavoriteState(EFavoriteState.UNASSIGNED);		
//		}
//		{
//			mLblWebsites = new JLabel();
//			mLblWebsites.setText(BookmarkOptions.getString(Resources.string.lblUpdateWebsites));
//			
//			mCmbWebsites = new WebsiteComboBox(EWebsiteType.ALL_SITES, BookmarkOptions.getString(Resources.string.lblAllSites));
//			mCmbWebsites.addActionListener(mQueryListener);
//		}
		{
			_btnStoryState.addActionListener(_queryComponentListener);
			_btnStoryState.setCompleteState(true);
		}
//		{
//			mGenreWindow = new GenreWindow(mPanel);
//			mGenreWindow.addActionListener(mQueryListener);
//		}
		{
			mBtnReset = new JButton();
			mBtnReset.setText(_resources.getString(Resources.string.lblReset));
			mBtnReset.addActionListener(_resetListener);			
		}
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
//						.addComponent(mLblAuthor, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
//						.addComponent(mLblWebsites, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(mLblKeyword, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup()
						.addComponent(mTxtKeyword, 10, 276, Short.MAX_VALUE)
//						.addComponent(mTxtAuthor, 10, 276, Short.MAX_VALUE)
//						.addComponent(mCmbWebsites, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					)
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
								.addGap(0, 50, Short.MAX_VALUE)
								.addComponent(mBtnReset, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						)
						.addGroup(layout.createSequentialGroup()
								.addComponent(mChkUpdated, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
//								.addComponent(mBtnFavState, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
//								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(_btnStoryState, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						)
					)
				)
//				.addComponent(mGenreWindow, 10, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
				.addComponent(_savedMediaTableControl.getComponent(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
			)
			.addContainerGap());
		
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()			
			.addGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(Alignment.CENTER)
					.addComponent(mLblKeyword, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(mTxtKeyword, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(mBtnReset, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addGroup(layout.createParallelGroup(Alignment.CENTER)
//						.addComponent(mLblAuthor, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
//						.addComponent(mTxtAuthor, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(mChkUpdated, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
//						.addComponent(mBtnFavState, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(_btnStoryState, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
//					.addGroup(layout.createParallelGroup(Alignment.CENTER)
//							.addComponent(mLblWebsites, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
//							.addComponent(mCmbWebsites, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
//					)
//					.addComponent(mGenreWindow, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			)
			.addComponent(_savedMediaTableControl.getComponent(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)				
			.addContainerGap());
	}
	
	public JPanel getPanel() {
		return _panel;
	}
	
	public void updateSummaryPanel() {
		_summaryPanel.setSummaryView(_summaryObserver.getSummaryPanel());
	}

	public Media getSelectedItem() {
		return _savedMediaTableControl.getTable().getSelectedItem();
	}
	
	private void sendQuery() {
		_savedMediaTableControl.query(getSearchQuery(new SavedMediaQuery()));
	}
	
	private SavedMediaQuery getSearchQuery(SavedMediaQuery query) {		
		if ( mTxtKeyword.getText().length() > 0 ) {
//			query.addCriterion(new Criterion<String>(getKeywordField(), mTxtKeyword.getText()));	
			query._keyword = mTxtKeyword.getText().toLowerCase();
		}
		
//		if ( mTxtAuthor.getText().length() > 0 ) {
//			query.addCriterion(new Criterion<String>(CriterionMatchers.AUTHOR_FIELD, mTxtAuthor.getText()));		
//		}
		
		if ( mChkUpdated.isSelected() ) {
//			query.addCriterion(new Criterion<Boolean>(CriterionMatchers.IS_UPDATED_FIELD, true));
			query._isUpdated = true;
		}
		
//		if ( mCmbWebsites.getSelectedIndex() > 0 ) {
//			String sSite = mCmbWebsites.getSelectedItem().toString();
//			MangaSite site = WebsiteFactory.find(sSite);
//			if ( site != null ) {
//				query.addCriterion(new Criterion<MangaSite>(CriterionMatchers.WEBSITE_FIELD, site));
//			}
//		}
		
//		SearchGenreList genres = mGenreWindow.getSearchGenres();
//		if ( genres.size() > 0 ) {
//			query.addCriterion(new Criterion<SearchGenreList>(CriterionMatchers.GENRE_FIELD, genres));
//		}
				
//		EFavoriteState favState = mBtnFavState.getFavoriteState(); 
//		if ( favState != null && favState != EFavoriteState.UNASSIGNED ) {
//			query.addCriterion(new Criterion<EFavoriteState>(CriterionMatchers.FAV_FIELD, favState));			
//		}
				
//		if ( _btnStoryState.getStoryState() != null ) {
//			query.addCriterion(new Criterion<EStoryState>(CriterionMatchers.STATUS_FIELD, mBtnStoryState.getStoryState()));
//			query._isCompleted = _btnStoryState.getStoryState() == EStoryState.FINISHED_BOOKMARK;
//		}
		
		return(query);
	}
	
	private ActionListener _queryComponentListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			sendQuery();
		}
	};
	
	private KeyListener _textBoxKeyListener = new KeyListener() {
		@Override
		public void keyPressed(KeyEvent e) {}

		@Override
		public void keyReleased(KeyEvent e) {
			sendQuery();
		}

		@Override
		public void keyTyped(KeyEvent e) {}		
	};
	
//	private ItemListener _keywordItemListener = new ItemListener() {
//		@Override
//		public void itemStateChanged(ItemEvent e) {
//			sendQuery();
//		}		
//	};
	
	private ActionListener _resetListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			mTxtKeyword.setText("");
			mChkUpdated.setSelected(false);
//			mGenreWindow.setSelectedGenres(new SearchGenreList());
//			mBtnFavState.setFavoriteState(EFavoriteState.UNASSIGNED);
			_btnStoryState.setStoryState(null);
			sendQuery();
		}
	};
}
