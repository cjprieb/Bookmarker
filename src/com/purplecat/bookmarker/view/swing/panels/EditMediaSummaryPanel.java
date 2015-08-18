package com.purplecat.bookmarker.view.swing.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.inject.Inject;
import com.purplecat.bookmarker.Resources;
import com.purplecat.bookmarker.models.EFavoriteState;
import com.purplecat.bookmarker.models.EStoryState;
import com.purplecat.bookmarker.models.Place;
import com.purplecat.bookmarker.view.swing.components.FavoriteStateButton;
import com.purplecat.bookmarker.view.swing.components.PlaceEditor;
import com.purplecat.bookmarker.view.swing.components.StoryStateButton;
import com.purplecat.bookmarker.view.swing.observers.IMediaItemEditor;
import com.purplecat.commons.IResourceService;

public class EditMediaSummaryPanel implements ActionListener, ChangeListener {
	protected JPanel _panel;
	protected JPanel _categoryComboBox; //CategoryComboBox
	protected JButton _addCategoryButton;	
	protected IMediaItemEditor _mediaItemEditor;

	@Inject public PlaceEditor _placeEditor;
	@Inject public FavoriteStateButton _favStateButton;
	@Inject public StoryStateButton _storyStateButton;
	
	@Inject public IResourceService _resources; 
	
	public void create() {		
		initGUI();
		addListeners();		
	}
	
	public void setMediaItemEditor(IMediaItemEditor editor) {
		_mediaItemEditor = editor;
	}
	
	public JPanel getPanel() {
		return _panel;
	}
	
	public void setEnabled(boolean b) {
		_placeEditor.setEnabled(b);
		_storyStateButton.setEnabled(b);
		_favStateButton.setEnabled(b);
		_categoryComboBox.setEnabled(b);
		_addCategoryButton.setEnabled(b);		
	}
	
	protected void addListeners() {		
		_placeEditor.addChangeListener(this);
		_storyStateButton.addActionListener(this);
		_favStateButton.addActionListener(this);
		_addCategoryButton.addActionListener(this);
	}
	
	protected void initGUI() {	
		_panel = new JPanel();
		GroupLayout panelLayout = new GroupLayout(_panel);
		_panel.setLayout(panelLayout);
		{
			_placeEditor.enableUpdateButton(true);
			_placeEditor.createControl();
		}
		{			
			EStoryState[] order = { EStoryState.LAST_AVAILABLE_CHAPTER, EStoryState.FINISHED_BOOKMARK };
			
			_storyStateButton.includeNull(false);
			_storyStateButton.setOrder(order);
			_storyStateButton.setStoryState(null);
		}
		{
			_favStateButton.setFavoriteState(null);
		}
		{
			_categoryComboBox = new JPanel();
			_addCategoryButton = new JButton(_resources.getString(Resources.string.lblAdd));
			_addCategoryButton.setVisible(false);
			_categoryComboBox.setVisible(false);
		}
		
		panelLayout.setVerticalGroup(panelLayout.createSequentialGroup()
			.addContainerGap()
			.addComponent(_placeEditor, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			.addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(_storyStateButton, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addComponent(_favStateButton, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
//				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			.addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(_categoryComboBox, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addComponent(_addCategoryButton, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
			.addContainerGap());
		
		panelLayout.setHorizontalGroup(panelLayout.createSequentialGroup()
			.addGroup(panelLayout.createParallelGroup()
				.addComponent(_placeEditor, GroupLayout.Alignment.LEADING, 0, 135, Short.MAX_VALUE)
				.addGroup(GroupLayout.Alignment.LEADING, panelLayout.createSequentialGroup()
				    .addPreferredGap(_placeEditor, _categoryComboBox, LayoutStyle.ComponentPlacement.INDENT)
				    .addGroup(panelLayout.createParallelGroup()
				        .addComponent(_categoryComboBox, GroupLayout.Alignment.LEADING, 0, 50, Short.MAX_VALUE)
				        .addGroup(GroupLayout.Alignment.LEADING, panelLayout.createSequentialGroup()
				            .addComponent(_storyStateButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				            .addComponent(_favStateButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				            .addGap(0, 12, Short.MAX_VALUE)))
				    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				    .addComponent(_addCategoryButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)))
		    .addContainerGap());
		
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if ( e.getSource() == _placeEditor ) {
			firePlaceChanged();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ( e.getSource() == _categoryComboBox ) {
			fireCategoryChanged();
		}
		else if ( e.getSource() == _storyStateButton ) {
			fireStoryStateChanged();
		}
		else if ( e.getSource() == _favStateButton ) {
			fireFavoriteStateChanged();
		}
	}	
	
	protected void fireCategoryChanged() {
		//TODO: implement category change for summary panel;
	}
	
	protected void firePlaceChanged() {
		_mediaItemEditor.updatePlace(_placeEditor.getPlace());
	}
	
	protected void fireStoryStateChanged() {
		_mediaItemEditor.updateStoryState(_storyStateButton.getStoryState());
	}
	
	protected void fireFavoriteStateChanged() {
		_mediaItemEditor.updateFavoriteState(_favStateButton.getFavoriteState());
	}
	
	public void setPlace(Place p) {
		_placeEditor.setEnabled(p != null);
		_placeEditor.setPlace(p);
	}
	
	public void setFavoriteState(EFavoriteState state) {
		_favStateButton.setEnabled(state != null);
		_favStateButton.setFavoriteState(state);
	}
	
	public void setStoryState(EStoryState state) {
		_storyStateButton.setEnabled(state != null);
		_storyStateButton.setStoryState(state);
	}
	
	public void enablePlaceEditor(boolean b) {
		_placeEditor.setEnabled(b);
	}
	
	public void enableFavoriteStateButton(boolean b) {
		_favStateButton.setEnabled(b);
	}
	
	public void enableStoryStateButton(boolean b) {
		_storyStateButton.setEnabled(b);
	}

}
