package org.signalml.app.view.components.export;

import static org.signalml.app.util.i18n.SvarogI18n._;

import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.app.view.components.AbstractPanel;

/**
 * An abstract class for all panels containing the export options.
 *
 * @author Piotr Szachewicz
 */
public abstract class AbstractExportOptionsPanel extends AbstractPanel {

	public AbstractExportOptionsPanel() {
		super(_("Export options"));
	}

	/**
	 * Fills the fields of this panel from the given
	 * {@link SignalExportDescriptor export descriptor}.
	 * @param descriptor the export descriptor
	 */
	public abstract void fillPanelFromModel(SignalExportDescriptor descriptor);

	/**
	 * Fills the given {@link SignalExportDescriptor export descriptor}
	 * with the user input from this panel.
	 * @param descriptor the descriptor to be filled
	 */
	public abstract void fillModelFromPanel(SignalExportDescriptor descriptor);

}
