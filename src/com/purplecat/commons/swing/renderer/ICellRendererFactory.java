package com.purplecat.commons.swing.renderer;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.purplecat.commons.TTableColumn;

public interface ICellRendererFactory {

	Object getHeaderValue(TTableColumn type);

	TableCellRenderer getRendererFromType(TTableColumn type);

	TableCellEditor getEditorFromType(TTableColumn type);

}
