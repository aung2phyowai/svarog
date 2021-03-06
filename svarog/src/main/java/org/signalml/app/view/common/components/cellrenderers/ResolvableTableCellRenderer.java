/* ResolvableTableCellRenderer.java created 2008-03-04
 *
 */

package org.signalml.app.view.common.components.cellrenderers;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.springframework.context.MessageSourceResolvable;

/**
 * The table cell renderer which uses the text obtained from the source of
 * messages (labels) if the {@code value} of the cell is of type
 * {@code MessageSourceResolvable}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ResolvableTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	/**
	 * {@link DefaultTableCellRenderer#getTableCellRendererComponent(JTable,
	 * Object, boolean, boolean, int, int) Creates} the label to be used as
	 * the contents of the cell.
	 * If the {@code value} is of type {@code MessageSourceResolvable} replaces
	 * the text with the text obtained from the source of messages (labels).
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (value instanceof MessageSourceResolvable) {
			renderer.setText(((MessageSourceResolvable) value).getDefaultMessage());
		}

		return renderer;
	}
}
