/* ChooseFilesForMonitorRecordingPanel.java created 2010-11-03
 *
 */
package org.signalml.app.view.monitor;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.model.MonitorRecordingDescriptor;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.app.view.element.FileSelectPanel;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/**
 * Represents a panel used to select files used to record monitor.
 *
 * @author Piotr Szachewicz
 */
public class ChooseFilesForMonitorRecordingPanel extends JPanel {

	private final MessageSourceAccessor messageSource;

	private FileSelectPanel selectSignalRecordingFilePanel;
	private FileSelectPanel selectTagsRecordingFilePanel;
	private DisableTagRecordingPanel disableTagRecordingPanel;

	/**
	 * Constructor.
	 *
	 * @param messageSource the message source accessor capable of resolving
	 * localized message codes
	 */
	public ChooseFilesForMonitorRecordingPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	private void initialize() {
		setLayout(new GridLayout(3, 1, 10, 5));
		CompoundBorder border = new CompoundBorder(
			new TitledBorder(messageSource.getMessage("startMonitorRecording.chooseFilesTitle")),
			new EmptyBorder(3, 3, 3, 3));
		setBorder(border);
		add(getSelectSignalRecordingFilePanel());
		add(getSelectTagsRecordingFilePanel());
		add(getDisableTagRecordingPanel());
	}

	protected FileSelectPanel getSelectSignalRecordingFilePanel() {
		if (selectSignalRecordingFilePanel == null) {
			selectSignalRecordingFilePanel = new FileSelectPanel(messageSource, messageSource.getMessage("startMonitorRecording.chooseSignalFileLabel"));
		}
		return selectSignalRecordingFilePanel;
	}

	protected FileSelectPanel getSelectTagsRecordingFilePanel() {
		if (selectTagsRecordingFilePanel == null) {
			selectTagsRecordingFilePanel = new FileSelectPanel(messageSource, messageSource.getMessage("startMonitorRecording.chooseTagFileLabel"));
		}
		return selectTagsRecordingFilePanel;
	}

	protected DisableTagRecordingPanel getDisableTagRecordingPanel() {
		if (disableTagRecordingPanel == null) {
			disableTagRecordingPanel = new DisableTagRecordingPanel();
		}
		return disableTagRecordingPanel;
	}

	/**
	 * Fills the model with the data from this panel (user input).
	 * @param model the model to be filled.
	 */
	public void fillModelFromPanel(Object model) {
		MonitorRecordingDescriptor monitorRecordingDescriptor = ((OpenMonitorDescriptor) model).getMonitorRecordingDescriptor();
		monitorRecordingDescriptor.setSignalRecordingFilePath(getSelectSignalRecordingFilePanel().getFileName());
		monitorRecordingDescriptor.setTagsRecordingFilePath(getSelectTagsRecordingFilePanel().getFileName());
		monitorRecordingDescriptor.setTagsRecordingDisabled(getDisableTagRecordingPanel().isTagRecordingDisabled());
	}

	/**
	 * Sets this panel to be enabled or disabled.
	 * @param enabled false to disable this panel, true otherwise
	 */
	@Override
	public void setEnabled(boolean enabled) {

		super.setEnabled(enabled);

		for (Component component : this.getComponents()) {
			component.setEnabled(enabled);
		}

		if (getDisableTagRecordingPanel().isTagRecordingDisabled())
			getSelectTagsRecordingFilePanel().setEnabled(false);

	}

	/**
	 * Resets the signal and tag recording filenames to empty strings.
	 */
	public void resetFileNames() {
		getSelectSignalRecordingFilePanel().setFileName("");
		getSelectTagsRecordingFilePanel().setFileName("");
	}

	/**
	 * Checks if this panel is properly filled.
	 * @param model the model for this panel
	 * @param errors the object in which errors are stored
	 */
	public void validatePanel(Object model, Errors errors) {

		String recordingFileName = getSelectSignalRecordingFilePanel().getFileName();
		String tagRecordingFileName = getSelectTagsRecordingFilePanel().getFileName();

		if (recordingFileName.isEmpty()) {
			errors.reject("error.startMonitorRecording.incorrectSignalFile");
		}
		else if ((new File(recordingFileName)).exists()) {
			int anwser = JOptionPane.showConfirmDialog(null, messageSource.getMessage("startMonitorRecording.signalFileAlreadyExistsMessage"));
			if (anwser == JOptionPane.CANCEL_OPTION || anwser == JOptionPane.NO_OPTION)
				errors.reject("");
		}

		if (getDisableTagRecordingPanel().isTagRecordingEnabled() && tagRecordingFileName.isEmpty()) {
			errors.reject("error.startMonitorRecording.incorrectTagFile");
		}
		else if (getDisableTagRecordingPanel().isTagRecordingEnabled() && (new File(tagRecordingFileName)).exists()) {
			int anwser = JOptionPane.showConfirmDialog(null, messageSource.getMessage("startMonitorRecording.tagFileAlreadyExistsMessage"));
			if (anwser == JOptionPane.CANCEL_OPTION || anwser == JOptionPane.NO_OPTION)
				errors.reject("");
		}

	}

	/**
	 * Represents a panel that contains a checkbox to enable/disable tag recording.
	 */
	private class DisableTagRecordingPanel extends JPanel {

		private JCheckBox disableTagRecordingCheckBox = null;

		/**
		 * Constructor. Creates a new {@link DisableTagRecordingPanel}.
		 */
		public DisableTagRecordingPanel() {

			disableTagRecordingCheckBox = new JCheckBox();
			disableTagRecordingCheckBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						getSelectTagsRecordingFilePanel().setEnabled(false);
					} else if (e.getStateChange() == ItemEvent.DESELECTED) {
						getSelectTagsRecordingFilePanel().setEnabled(true);
					}
				}
			});
			add(disableTagRecordingCheckBox);
			add(new JLabel(messageSource.getMessage("startMonitorRecording.doNotRecordTagsLabel")));
		}

		/**
		* Sets this panel to be enabled or disabled.
		* @param enabled false to disable this panel, true otherwise
		*/
		@Override
		public void setEnabled(boolean enabled) {
			super.setEnabled(enabled);

			for (Component component : this.getComponents()) {
				component.setEnabled(enabled);
			}
		}

		/**
		 * Returns whether tag recording was disabled using this panel.
		 * @return true if tag recording was disabled, false otherwise
		 */
		public boolean isTagRecordingDisabled() {
			return disableTagRecordingCheckBox.isSelected();
		}

		/**
		 * Returns whether tag recording was enabled using this panel.
		 * @return true if tag recording was enabled, false otherwise
		 */
		public boolean isTagRecordingEnabled() {
			return !isTagRecordingDisabled();
		}

		/**
		 * Sets the status of the tag recording checkbox.
		 * @param disable true to disable tag recording, false otherwise
		 */
		public void setTagRecordingDisabled(boolean disable) {
			disableTagRecordingCheckBox.setSelected(disable);
		}
	}

}
