/* CenteringCellRenderer.java created 2007-10-24
 *
 */

package org.signalml.app.view.montage;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.signalml.domain.montage.Channel;
import org.springframework.context.support.MessageSourceAccessor;

/** CenteringCellRenderer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ChannelTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		label.setText(messageSource.getMessage((Channel) value));
		return label;
	}

	public MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
	}

}