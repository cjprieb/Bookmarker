package com.purplecat.bookmarker.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.purplecat.bookmarker.Resources;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.view.swing.panels.GlassTimerPanel;
import com.purplecat.bookmarker.view.swing.panels.OnlineUpdateTab;
import com.purplecat.bookmarker.view.swing.panels.SavedMediaTab;
import com.purplecat.bookmarker.view.swing.panels.SummarySidebar;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.swing.Toolbox;

@Singleton
public class MainPanel implements ChangeListener {
	private static final String TAG = "MainPanel";
	
	@Inject Controller _controller;
	@Inject ILoggingService _logging;
	@Inject IResourceService _resources;
	@Inject Toolbox _toolbox;
	
	@Inject SavedMediaTab _savedMediaTab;
	@Inject OnlineUpdateTab _onlineUpdateTab;	
	@Inject SummarySidebar _summaryPanel;
	
	public JPanel _panel;
	public JTabbedPane _tabbedPane;

	public JPanel create(GlassTimerPanel timerGlassPane) {
		_panel = new JPanel();
		
		_tabbedPane = new JTabbedPane();
		_tabbedPane.addChangeListener(this);

		timerGlassPane.setCoverPanel(_panel);		
		_controller.observeSavedMediaLoading(new GlassPanelListObserver(timerGlassPane));
		
		_summaryPanel.create();
		_savedMediaTab.create();
		_onlineUpdateTab.create();
		
		_tabbedPane.addTab(_resources.getString(Resources.string.lblBookmarks), _savedMediaTab.getPanel());		
		_tabbedPane.addTab(_resources.getString(Resources.string.lblUpdated), _onlineUpdateTab.getPanel());	
		
		GroupLayout layout = new GroupLayout(_panel);
		_panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(_tabbedPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addComponent(_summaryPanel.getPanel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addContainerGap());
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup()
					.addComponent(_tabbedPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addComponent(_summaryPanel.getPanel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				)
				.addContainerGap());
		
		loadPreferences();
		return _panel;
	}
	
	public JMenuBar initializeMenu() {
		JMenu updateMenu = new JMenu(_resources.getString(Resources.string.menuOnline));
		updateMenu.add(new JMenuItem(new LoadUpdatesAction()));
		updateMenu.add(new JMenuItem(new StopUpdatesAction()));
		updateMenu.add(new JMenuItem(new RefreshItemAction()));
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(updateMenu);
		return menuBar;
	}
	
	public void loadPreferences() {
		_logging.debug(1, TAG, "Loading MainPanel preferences");
		Preferences prefs = Preferences.userNodeForPackage(getClass());
		
		int tabIndex = prefs.getInt("main-tabIndex", 0);
		_logging.debug(2, TAG, "Selected tab: " + tabIndex);
		if ( tabIndex >= 0 && tabIndex < _tabbedPane.getTabCount() ) {
			_tabbedPane.setSelectedIndex(tabIndex);
		}		
	}
	
	public void savePreferences() {
		_logging.debug(1, TAG, "Saving MainPanel preferences");
		Preferences prefs = Preferences.userNodeForPackage(getClass());
		prefs.putInt("main-tabIndex", _tabbedPane.getSelectedIndex());
		
		_onlineUpdateTab.savePreferences();
		_savedMediaTab.savePreferences();
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if ( e.getSource() == _tabbedPane ) {
			if ( _tabbedPane.getSelectedComponent() == _savedMediaTab.getPanel() ) {
				_savedMediaTab.updateSummaryPanel();
			}
			else if ( _tabbedPane.getSelectedComponent() == _onlineUpdateTab.getPanel() ) {
				_onlineUpdateTab.updateSummaryPanel();
			}
		}
	}
	
	public static class GlassPanelListObserver implements IListLoadedObserver<Media> {
		
		protected final GlassTimerPanel _glassPanel;
		
		public GlassPanelListObserver(GlassTimerPanel glassPanel) {
			_glassPanel = glassPanel;
			_glassPanel.setProgress(0, 0);
		}

		@Override
		public void notifyItemLoaded(Media item, int index, int total) {
			_glassPanel.setProgress(index, total);
		}

		@Override
		public void notifyListLoaded(List<Media> list) {
			_glassPanel.stopTimer();
			_glassPanel.setProgress(0, 0);
			_glassPanel.setVisible(false);
		}		
	}
	
	public class StopUpdatesAction extends AbstractAction {		
		public StopUpdatesAction() {
			this.putValue(Action.NAME, _resources.getString(Resources.string.lblStopUpdates));
			this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
			this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_T);
		}
		
		@Override 
		public void actionPerformed(ActionEvent e) {
			_controller.stopUpdates();		
		}
	}
	
	public class LoadUpdatesAction extends AbstractAction {		
		public LoadUpdatesAction() {
			this.putValue(Action.NAME, _resources.getString(Resources.string.lblLoadUpdates));
			this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, _toolbox.getMetaControl()));
			this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
		}
		
		@Override 
		public void actionPerformed(ActionEvent e) {
			_tabbedPane.setSelectedComponent(_onlineUpdateTab.getPanel());
			_onlineUpdateTab.callLoadUpdates();	
		}
	}
	
	public class RefreshItemAction extends AbstractAction {		
		public RefreshItemAction() {
			this.putValue(Action.NAME, _resources.getString(Resources.string.menuLoadSummary));
			this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
			this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
		}
		
		@Override 
		public void actionPerformed(ActionEvent e) {
			if ( _tabbedPane.getSelectedComponent() == _onlineUpdateTab.getPanel() ) {
				OnlineMediaItem item = _onlineUpdateTab.getSelectedItem();
				if ( item != null ) {
					_controller.loadMediaSummary(item._mediaId, item._titleUrl);
				}
			}
			else if ( _tabbedPane.getSelectedComponent() == _savedMediaTab.getPanel() ) {
				Media item = _savedMediaTab.getSelectedItem();
				if ( item != null ) {
					_controller.loadMediaSummary(item._id, item._titleUrl);
				}
			}
		}
	}
}
