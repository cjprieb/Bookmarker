package com.purplecat.bookmarker.view.swing;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.List;

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
import com.purplecat.bookmarker.view.swing.panels.GlassTimerPanel;
import com.purplecat.bookmarker.view.swing.panels.OnlineUpdateTab;
import com.purplecat.bookmarker.view.swing.panels.SavedMediaTab;
import com.purplecat.bookmarker.view.swing.panels.SummarySidebar;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.swing.Toolbox;
import com.sun.glass.events.KeyEvent;

@Singleton
public class MainPanel implements ChangeListener {
	
	@Inject Controller _controller;
	@Inject IResourceService _resources;
	@Inject Toolbox _toolbox;
	
	@Inject SavedMediaTab _savedMediaTab;
	@Inject OnlineUpdateTab _onlineUpdateTab;	
	@Inject SummarySidebar _summaryPanel;
	
	public JPanel _panel;
	public JTabbedPane _tabbedPane;

	public JPanel create(GlassTimerPanel timerGlassPane) {
		_panel = new JPanel();
		_panel.setPreferredSize(new Dimension(850, 600));
		
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
		
		return _panel;
	}
	
	public JMenuBar initializeMenu() {
		JMenu updateMenu = new JMenu(_resources.getString(Resources.string.menuOnline));
		updateMenu.add(new JMenuItem(new LoadUpdatesAction()));
		updateMenu.add(new JMenuItem(new StopUpdatesAction()));
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(updateMenu);
		return menuBar;
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
			_controller.loadUpdateMedia(_onlineUpdateTab.getHoursAgo());
		}
	}
}
