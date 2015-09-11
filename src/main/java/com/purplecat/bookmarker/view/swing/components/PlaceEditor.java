package com.purplecat.bookmarker.view.swing.components;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.inject.Inject;
import com.purplecat.bookmarker.Resources;
import com.purplecat.bookmarker.models.Place;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.swing.IImageRepository;
import com.purplecat.commons.swing.Toolbox;

public class PlaceEditor extends JPanel {

	private JButton mBtnUpdate;
	private JCheckBox	mChkExtra;
	private JLabel mLblExtra;
	private PlaceSpinner mTxtVolume;
	private PlaceSpinner mTxtChapter;
	private PlaceSpinner mTxtSubChapter;
	private PlaceSpinner mTxtPage;	
	private boolean _showUpdate = false;
	
	@Inject public IResourceService _resources;
	@Inject public IImageRepository _imageResources;
	@Inject public Toolbox _toolbox;
	
	public PlaceEditor() {	}
	
	public void enableUpdateButton(boolean showUpdate) {
		_showUpdate = showUpdate;
	}
	
	public void createControl() {
		initGUI();
		addListeners();	
	}
	
	/*
	 * Listeners
	 */
	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}
	
	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}
	
	@Override
	public void setEnabled(boolean b) {
		mBtnUpdate.setEnabled(b);
		mChkExtra.setEnabled(b);
		mTxtVolume.setEnabled(b);
		mTxtChapter.setEnabled(b);
		mTxtSubChapter.setEnabled(b);
		mTxtPage.setEnabled(b);
	}
	
//	@Override
//	public void setVisible(boolean b) {
//		mBtnUpdate.setVisible(b);
//		mChkExtra.setVisible(b);
//		mTxtVolume.setVisible(b);
//		mTxtChapter.setVisible(b);
//		mTxtSubChapter.setVisible(b);
//		mTxtPage.setVisible(b);
//	}
	
	protected void fireItemChanged(Class<?> cls, Object value) {
		ChangeEvent e = new ChangeEvent(this);
		for ( ChangeListener l : listenerList.getListeners(ChangeListener.class) ) {
			l.stateChanged(e);
		}
	}
	
	/*
	 * Private setup-methods
	 */
	private void addListeners() {
		UpdateAction action = new UpdateAction();
		mBtnUpdate.addMouseListener(action);
		mTxtChapter.addChangeListener(action);
		mChkExtra.addActionListener(action);
	}
	
	private void initGUI() {
		GroupLayout mPnlPlaceLayout = new GroupLayout(this);
		this.setLayout(mPnlPlaceLayout);
		{
			mTxtVolume = new PlaceSpinner("v ", 3);
			mTxtChapter = new PlaceSpinner("c ", 4);
			mTxtSubChapter = new PlaceSpinner(".", 2);					
			mTxtPage = new PlaceSpinner("pg", 2);
			mChkExtra = new JCheckBox(" ");
			mLblExtra = new JLabel();
		}
		{
			mBtnUpdate = new JButton(_imageResources.getImage(Resources.image.appGreenCheckId));
			mBtnUpdate.setVisible(_showUpdate);
			_toolbox.setButtonInsets(new Insets(2, 2, 2, 2), mBtnUpdate);
		}
		mPnlPlaceLayout.setHorizontalGroup(mPnlPlaceLayout.createSequentialGroup()
			.addContainerGap()
			.addComponent(mBtnUpdate, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(mTxtVolume, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(mTxtChapter, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(mTxtSubChapter, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(mLblExtra, 10, 10, 10)
			.addComponent(mChkExtra, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			.addGap(0, 10, Short.MAX_VALUE));
		mPnlPlaceLayout.setVerticalGroup(mPnlPlaceLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(mPnlPlaceLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(mBtnUpdate, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addComponent(mTxtVolume, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addComponent(mTxtChapter, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addComponent(mTxtSubChapter, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addComponent(mLblExtra, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addComponent(mChkExtra, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)));
	}
	
	/*
	 * Get/Set methods
	 */
	public void setPlace(Place p) {
		if ( p == null ) {
			mTxtVolume.setValue(0);
			mTxtChapter.setValue(0);
			mTxtSubChapter.setValue(0);
			mTxtPage.setValue(0);
			mChkExtra.setSelected(false);
		}
		else {
			mTxtVolume.setValue(p._volume);
			mTxtChapter.setValue(p._chapter);
			mTxtSubChapter.setValue(p._subChapter);
			mTxtPage.setValue(p._page);
			mChkExtra.setSelected(p._extra);			
		}
	}
	
	public Place getPlace() {
		Place place = new Place(mTxtVolume.getIntegerValue(), 
				mTxtChapter.getIntegerValue(),
				mTxtSubChapter.getIntegerValue(),
				mTxtPage.getIntegerValue(),
				mChkExtra.isSelected());
		return(place);
	}
	
	/*
	 * Action listener
	 */
	class UpdateAction extends MouseAdapter implements ChangeListener, ActionListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			if ( e.getClickCount() == 1 ) {
				if ( e.getSource() == mBtnUpdate ) {				
					fireItemChanged(Place.class, null);
				}
			}
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			if ( e.getSource() == mTxtChapter ) {
				mTxtSubChapter.setValue(0);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if ( mChkExtra.isSelected() ) {
				mLblExtra.setText("*");
			}
			else {
				mLblExtra.setText("");				
			}
		}
	}

}
