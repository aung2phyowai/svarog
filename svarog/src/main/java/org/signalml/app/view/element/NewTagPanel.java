/* NewTagPanel.java created 2007-10-14
 *
 */
package org.signalml.app.view.element;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileFilter;

import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.TagDocument;
import org.signalml.plugin.export.signal.TagStyle;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Panel which allows to select which {@link TagStyle styles} should be used in
 * the new {@link TagDocument tag document}.
 * Contains 3 buttons:
 * <ul>
 * <li>the {@link #getEmptyRadio() radio button} that indicates that the
 * tag document should contain no style,</li>
 * <li>the {@link #getDefaultSleepRadio() radio button} that indicates that
 * the tag document should contain the default sleep styles,</li>
 * <li>the {@link #getFromFileRadio() radio button} that indicates that the
 * tag document should contain the same styles as in the selected file</li>
 * </ul>
 * and one {@link #getFileChooser() file chooser} which is shown when the
 * third button is selected.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class NewTagPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link MessageSourceAccessor source} of messages (labels)
	 */
	private MessageSourceAccessor messageSource;
	/**
	 * the radio button that indicates that the {@link TagDocument tag document}
	 * should contain no {@link TagStyle style}
	 */
	private JRadioButton emptyRadio = null;
	/**
	 * the radio button that indicates that the {@link TagDocument tag document}
	 * should contain the default sleep {@link TagStyle styles}
	 */
	private JRadioButton defaultSleepRadio = null;
	/**
	 * the radio button that indicates that the {@link TagDocument tag document}
	 * should contain the same {@link TagStyle styles} as in the selected file
	 */
	private JRadioButton fromFileRadio = null;

	/**
	 * the group of radio buttons which allows to select the {@link TagStyle
	 * styles} that should be located in the created {@link TagDocument tag
	 * document} ({@link #emptyRadio}, {@link #defaultSleepRadio},
	 * {@link #fromFileRadio})
	 */
	private ButtonGroup radioGroup;
	/**
	 * the {@link EmbeddedFileChooser chooser} of files which is shown when
	 * {@link #fromFileRadio} is selected
	 */
	private EmbeddedFileChooser fileChooser = null;

	/**
	 * Constructor. Sets the {@link MessageSourceAccessor message source} and
	 * initializes this panel.
	 * @param messageSource the source of messages (labels)
	 */
	public NewTagPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	/**
	 * Initializes this panel with the box layout and 3 buttons:
	 * <ul>
	 * <li>the {@link #getEmptyRadio() radio button} that indicates that the
	 * {@link TagDocument tag document} should contain no {@link TagStyle
	 * style},</li>
	 * <li>the {@link #getDefaultSleepRadio() radio button} that indicates that
	 * the tag document should contain the default sleep styles,</li>
	 * <li>the {@link #getFromFileRadio() radio button} that indicates that the
	 * tag document should contain the same styles as in the selected file</li>
	 * </ul>
	 * and one {@link #getFileChooser() file chooser} which is shown when the
	 * third button is selected.
	 */
	private void initialize() {

		setBorder(BorderFactory.createTitledBorder(messageSource.getMessage("newTag.title")));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		radioGroup = new ButtonGroup();

		add(getEmptyRadio());
		add(getDefaultSleepRadio());
		add(getFromFileRadio());

		getDefaultSleepRadio().setSelected(true);

		getFileChooser().setVisible(false);
		add(getFileChooser());

	}

	/**
	 * Returns the radio button that indicates that the {@link TagDocument tag
	 * document} should contain no {@link TagStyle style}.
	 * If the button doesn't exist it is created and added to the radio group.
	 * @return the radio button that indicates that the tag document should
	 * contain no style
	 */
	public JRadioButton getEmptyRadio() {
		if (emptyRadio == null) {
			emptyRadio = new JRadioButton();
			emptyRadio.setText(messageSource.getMessage("newTag.emptyRadio"));
			emptyRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
			radioGroup.add(emptyRadio);
		}
		return emptyRadio;
	}

	/**
	 * Returns the radio button that indicates that the {@link TagDocument tag
	 * document} should contain the default sleep {@link TagStyle styles}.
	 * If the button doesn't exist it is created and added to the radio group.
	 * @return the radio button that indicates that the tag document
	 * should contain the default sleep styles
	 */
	public JRadioButton getDefaultSleepRadio() {
		if (defaultSleepRadio == null) {
			defaultSleepRadio = new JRadioButton();
			defaultSleepRadio.setText(messageSource.getMessage("newTag.defaultSleepRadio"));
			defaultSleepRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
			radioGroup.add(defaultSleepRadio);
		}
		return defaultSleepRadio;
	}

	/**
	 * Returns the radio button that indicates that the {@link TagDocument tag
	 * document} should contain the same {@link TagStyle styles} as in the
	 * selected file.
	 * If the button doesn't exist it is created and added to the radio group.
	 * Also adds the listener to it, which shows the {@link #getFileChooser()
	 * file chooser} if this button is selected and hides the file chooser when
	 * the button is not selected.
	 * @return the radio button that indicates that the tag document
	 * should contain the same styles as in the selected file
	 */
	public JRadioButton getFromFileRadio() {
		if (fromFileRadio == null) {
			fromFileRadio = new JRadioButton();
			fromFileRadio.setText(messageSource.getMessage("newTag.fromFileRadio"));
			fromFileRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
			radioGroup.add(fromFileRadio);
			fromFileRadio.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {
					getFileChooser().setVisible(e.getStateChange() == ItemEvent.SELECTED);

					NewTagPanel.this.revalidate();
					Dimension d = NewTagPanel.this.getTopLevelAncestor().getPreferredSize();
					NewTagPanel.this.getTopLevelAncestor().setSize(d);
					NewTagPanel.this.repaint();
				}

			});
		}
		return fromFileRadio;
	}

	/**
	 * Returns the {@link EmbeddedFileChooser chooser} of files which is shown
	 * when {@link #getFromFileRadio() from file radio button} is selected.
	 * If the file chooser doens't exist it is created without multiselection
	 * and with the filter for {@link ManagedDocumentType#TAG tag files}.
	 * @return the chooser of files with {@link TagDocument tag documents}
	 */
	public EmbeddedFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new EmbeddedFileChooser();
			fileChooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
			fileChooser.setFileHidingEnabled(false);
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setAcceptAllFileFilterUsed(true);
			fileChooser.resetChoosableFileFilters();
			fileChooser.setAlignmentX(Component.LEFT_ALIGNMENT);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.resetChoosableFileFilters();
			FileFilter[] filters = ManagedDocumentType.TAG.getFileFilters(messageSource);
			for (FileFilter f : filters) {
				fileChooser.addChoosableFileFilter(f);
			}
			fileChooser.setPreferredSize(new Dimension(500,350));

			fileChooser.setInvokeDefaultButtonOnApprove(true);

		}
		return fileChooser;
	}

}