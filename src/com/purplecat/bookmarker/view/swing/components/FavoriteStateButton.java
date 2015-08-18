package com.purplecat.bookmarker.view.swing.components;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;

import com.google.inject.Inject;
import com.purplecat.bookmarker.Resources;
import com.purplecat.bookmarker.extensions.FavoriteStateExt;
import com.purplecat.bookmarker.models.EFavoriteState;
import com.purplecat.commons.swing.BorderType;
import com.purplecat.commons.swing.IImageRepository;

public class FavoriteStateButton extends LabelButton {	
	private EFavoriteState 		mState;
	private EFavoriteState[] 	mStateOrder;
	private JPopupMenu			mMenu;
	
	private boolean 			_refreshMenu = false;
	
	@Inject public IImageRepository _imageRepository;
	
	public FavoriteStateButton() {		
		mStateOrder = new EFavoriteState[] {
				EFavoriteState.MEH,
				EFavoriteState.AVERAGE,
				EFavoriteState.GOOD,
				EFavoriteState.AWESOME };
	}
	
	public void setFavoriteState(EFavoriteState state) {
		mState = state;
		int key = FavoriteStateExt.getIconKey(state);
		if ( key == 0 ) {
			key = Resources.image.imgFavBlankStarId;
		}
		setIcon(_imageRepository.getRadioButtonIcon(key, BorderType.Circular, 0x666666, -1));
		_refreshMenu = true;		
	}
	
	public EFavoriteState getFavoriteState() {
		return(mState);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if ( e.getClickCount() == 1 ) {
			EFavoriteState nextState = mState;
			if ( mState != null ) {
				switch ( mState ) {
					case MEH:			nextState = EFavoriteState.AVERAGE;		break;
					case AVERAGE:		nextState = EFavoriteState.GOOD;		break;
					case GOOD:			nextState = EFavoriteState.AWESOME;		break;
					case AWESOME:		nextState = EFavoriteState.UNASSIGNED;	break;
					case UNASSIGNED:	nextState = EFavoriteState.MEH;			break;
				}
			}
			else {
				nextState = EFavoriteState.UNASSIGNED;
			}
			setFavoriteState(nextState);
		}
		super.mouseClicked(e);
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		doPopup(e);
		super.mouseReleased(e);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		doPopup(e);
		super.mousePressed(e);
	}
	
	private void doPopup(MouseEvent e) {
		if ( e.isPopupTrigger() ) {
			if ( _refreshMenu || mMenu == null ) {
				mMenu = new JPopupMenu();
				
				mMenu.add(new SelectStateAction(null));
				
				for ( EFavoriteState state : mStateOrder ) {
					mMenu.add(new SelectStateAction(state));
				}				
			}
			mMenu.show(this, e.getPoint().x, e.getPoint().y);
		}
	}
	
	public class SelectStateAction extends AbstractAction {
		public EFavoriteState mFavoriteState;
		
		SelectStateAction(EFavoriteState state) {
			mFavoriteState = state;
			int imageKey = FavoriteStateExt.getIconKey(mFavoriteState);
			putValue(Action.SMALL_ICON, _imageRepository.getImage(imageKey));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			setFavoriteState(mFavoriteState);
			performAction("popup-menu");
		}
	}

}
