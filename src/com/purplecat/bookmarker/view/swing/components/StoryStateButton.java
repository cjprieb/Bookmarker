package com.purplecat.bookmarker.view.swing.components;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;

import com.google.inject.Inject;
import com.purplecat.bookmarker.Resources;
import com.purplecat.bookmarker.extensions.StoryStateExt;
import com.purplecat.bookmarker.models.EStoryState;
import com.purplecat.commons.swing.IImageRepository;

public class StoryStateButton extends LabelButton {
	private EStoryState 	mCurrentState;
	
	private boolean 				_includeNull;	
	private JPopupMenu				_menu;	
	private List<SelectStateAction> _actionList; 
	private boolean					_refreshMenu;
	
	@Inject public IImageRepository _imageRepository;
	
	public StoryStateButton() {
		_actionList = new ArrayList<SelectStateAction>();
	}
	
	public void setOrder(EStoryState[] stateOrder) {
		_refreshMenu = true;
		_actionList.clear();
		if ( _includeNull ) {
			_actionList.add(new SelectStateAction(null));
		}
		for ( EStoryState state : stateOrder ) {
			_actionList.add(new SelectStateAction(state));
		}
	}
	
	public void includeNull(boolean b) {
		if ( _includeNull != b ) {
			_refreshMenu = true;
		}
		_includeNull = b;
		
		if ( _includeNull ) {
			if ( _actionList.size() == 0 || 
					_actionList.get(0).mStoryState != null ) {
				_actionList.add(0, new SelectStateAction(null));
			}
		}
		else {
			if ( _actionList.size() > 0 && 
					_actionList.get(0).mStoryState == null ) {
				_actionList.remove(0);
			}			
		}
	}
	
	private void doPopup(MouseEvent e) {
		if ( e.isPopupTrigger() ) {
			if ( _refreshMenu || _menu == null ) {
				_menu = new JPopupMenu();
				for ( SelectStateAction action : _actionList ) {
					_menu.add(action);
				}
			}
			
			_menu.show(this, e.getPoint().x, e.getPoint().y);
		}
	}
	
	public void setCompleteState(boolean bState) {
		setStoryState(bState ? EStoryState.FINISHED_BOOKMARK : EStoryState.LAST_AVAILABLE_CHAPTER);
	}
	
	public boolean getCompleteState() {
		return(mCurrentState == EStoryState.FINISHED_BOOKMARK);
	}
	
	public void setStoryState(EStoryState state) {
		mCurrentState = state;
		int imageKey = StoryStateExt.getImageKey(mCurrentState, false);
		if ( imageKey == 0 ){
			imageKey = Resources.image.appBlankCircleId;
		}
		setIcon(_imageRepository.getImage(imageKey));
	}
	
	public EStoryState getStoryState() {
		return(mCurrentState);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if ( e.getClickCount() == 1 && !e.isPopupTrigger() ) {
			EStoryState nextState = null;//mCurrentState;
			boolean matchingStateFound = false;
			for ( SelectStateAction action : _actionList ) {
				if ( action.mStoryState == mCurrentState ) {
					matchingStateFound = true;
				}
				else if ( matchingStateFound ) {
					nextState = action.mStoryState; // will never be null, since that is always the first one
					break;
				}
			}
			if ( matchingStateFound && nextState == null ) {
				//at the end, so select first.
				if ( _includeNull && _actionList.size() > 1 ) {
					nextState = _actionList.get(1).mStoryState;
				}
				else if ( !_includeNull && _actionList.size() > 0 ) {
					nextState = _actionList.get(0).mStoryState;
				}
			}
			
			if ( nextState != null ) {
				setStoryState(nextState);
			}
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
	
	public class SelectStateAction extends AbstractAction {
		public EStoryState mStoryState;
		
		SelectStateAction(EStoryState state) {
			mStoryState = state;
			int imageKey = StoryStateExt.getImageKey(mStoryState, false);
			putValue(Action.SMALL_ICON, _imageRepository.getImage(imageKey));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			setStoryState(mStoryState);
			performAction("popup-menu");
		}
	}

}
