package com.purplecat.commons.swing.renderer;

import java.awt.Component;

import com.purplecat.commons.swing.TTable;

public interface ITableRowRenderer<T> {
	public void renderRow(TTable<T> table, Component component, T item, boolean isOdd);
}
