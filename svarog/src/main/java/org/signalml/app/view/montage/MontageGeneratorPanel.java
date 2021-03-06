/* EditMontageReferencePanel.java created 2007-10-24
 *
 */
package org.signalml.app.view.montage;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.montage.MontageGeneratorListModel;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.common.components.CompactButton;
import org.signalml.app.view.common.components.ResolvableComboBox;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.common.dialogs.errors.ValidationErrorsDialog;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.montage.SourceChannel;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.montage.SourceMontageEvent;
import org.signalml.domain.montage.SourceMontageListener;
import org.signalml.domain.montage.generators.IMontageGenerator;
import org.springframework.validation.Errors;

/**
 * The panel which allows to select the {@link MontageGenerator montage
 * generator} and generate a {@link Montage montage} using it.
 * The new montage is generated on the basis of the {@link #getMontage()
 * current montage}.
 * <p>
 * This panel contains three elements:
 * <ul>
 * <li>the {@link #getGeneratorComboBox() generator combo-box},</li>
 * <li>the {@link #getReloadButton() reload button},</li>
 * <li>the {@link #getShowErrorsButton() show errors button},</li></ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageGeneratorPanel extends JPanel {

	/** the default serialization constant. */
	private static final long serialVersionUID = 1L;

	/** the logger. */
	protected static final Logger logger = Logger.getLogger(MontageGeneratorPanel.class);

	/** the {@link Montage montage} that is edited. */
	private Montage montage;

	/**
	 * the combo-box which allows to select the {@link MontageGenerator montage
	 * genarator}.
	 * When the generator is selected from the list it is used to {@link
	 * #tryGenerate(MontageGenerator) generate} a new {@link Montage} on the
	 * basis of the {@link #montage old one}.
	 */
	private JComboBox generatorComboBox;

	/**
	 * the {@link MontageGeneratorListModel model} for the
	 * {@link #generatorComboBox}.
	 */
	private MontageGeneratorListModel montageGeneratorListModel;

	/**
	 * the dialog with errors which is shown when:
	 * <ul>
	 * <li>the {@link #getMontage() montage} can not be used to generate the.
	 * {@link Montage montage} using a {@link #getGeneratorComboBox() selected}
	 * {@link MontageGenerator generator},</li>
	 * <li>when {@link #getShowErrorsAction() ShowErrorsAction} is performed,</li>
	 * </ul>
	 */
	private ValidationErrorsDialog errorsDialog;

	/**
	 * the {@link ShowErrorsAction action} which displays the.
	 * errors dialog if the current {@link #getMontage()
	 * montage} can not be used to generate the {@link Montage montage} using a
	 * currently {@link #getGeneratorComboBox() selected}
	 * {@link MontageGenerator generator}
	 */
	private ShowErrorsAction showErrorsAction;

	/**
	 * the {@link ReloadAction action} which
	 * {@link #tryGenerate(MontageGenerator) generates} a new {@link Montage}
	 * using a currently {@link #getGeneratorComboBox() selected}
	 * {@link MontageGenerator} (on the basis of the {@link #montage old
	 * montage}).
	 */
	private ReloadAction reloadAction;

	/** the button for {@link #getShowErrorsAction() showErrorsAction}. */
	private CompactButton showErrorsButton;

	/** the button for {@link #getReloadAction() reloadAction}. */
	private CompactButton reloadButton;

	/**
	 * the {@link MontagePropertyListener listener} associated with the change
	 * of the {@link MontageGenerator montage generator} for the
	 * {@link MontageGeneratorPanel#montage montage}.
	 */
	private MontagePropertyListener montagePropertyListener;

	/**
	 * the listener associated with the addition/removal/change of a.
	 * {@link SourceChannel source channel} in a {@link #getMontage() montage}
	 */
	private SourceMontageChangeListener sourceMontageChangeListener;

	/**
	 * <code>true</code> if there is an ongoing event associated with the.
	 * {@link #getGeneratorComboBox() generator combo-box}, which means there
	 * will be no reaction on changes in the combo-box, <code>false</code>
	 * otherwise
	 */
	private boolean lockComboEvents = false;

	/**
	 * Constructor. Sets the source of messages and {@link #initialize()
	 * initializes} this panel
	 */
	public MontageGeneratorPanel() {
		super();
		initialize();
	}

	/**
	 * Initializes this panel with 3 elements:
	 * <ul>
	 * <li>the {@link #getGeneratorComboBox() generator combo-box},</li>
	 * <li>the {@link #getReloadButton() reload button},</li>
	 * <li>the {@link #getShowErrorsButton() show errors button},</li></ul>
	 */
	private void initialize() {

		montagePropertyListener = new MontagePropertyListener();
		sourceMontageChangeListener = new SourceMontageChangeListener();

		setLayout(new BorderLayout());

		JPanel choicePanel = new JPanel();
		choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.X_AXIS));

		CompoundBorder border = new CompoundBorder(
			new TitledBorder(_("Choose generator")),
			new EmptyBorder(3,3,3,3)
		);
		choicePanel.setBorder(border);

		choicePanel.add(new JLabel(_("Generator")));
		choicePanel.add(Box.createHorizontalStrut(5));
		choicePanel.add(Box.createHorizontalGlue());
		choicePanel.add(getGeneratorComboBox());
		choicePanel.add(Box.createHorizontalStrut(10));
		choicePanel.add(getReloadButton());
		choicePanel.add(Box.createHorizontalStrut(5));
		choicePanel.add(getShowErrorsButton());

		add(choicePanel, BorderLayout.CENTER);

	}

	/**
	 * Gets the {@link Montage montage} that is edited.
	 *
	 * @return the {@link Montage montage} that is edited
	 */
	public Montage getMontage() {
		return montage;
	}

	/**
	 * Sets the {@link Montage montage} that is edited.
	 *
	 * @param montage
	 *            the new {@link Montage montage} that is edited
	 */
	public void setMontage(Montage montage) {
		if (this.montage != montage) {
			if (this.montage != null) {
				this.montage.removePropertyChangeListener(Montage.MONTAGE_GENERATOR_PROPERTY, montagePropertyListener);
				this.montage.removeSourceMontageListener(sourceMontageChangeListener);
			}
			this.montage = montage;
			IMontageGenerator generator = null;
			if (montage != null) {
				montage.addPropertyChangeListener(Montage.MONTAGE_GENERATOR_PROPERTY, montagePropertyListener);
				montage.addSourceMontageListener(sourceMontageChangeListener);
				getMontageGeneratorListModel().setEegSystem(montage.getEegSystem());
				generator = montage.getMontageGenerator();
			} else {
				getMontageGeneratorListModel().setEegSystem(null);
			}
			quietSetSelectedGenerator(generator);
		}
	}

	/**
	 * Gets the validation dialog with errors which is shown when:
	 * <ul>
	 * <li>the {@link #getMontage() montage} can not be used to generate the.
	 *
	 * @return the {@link ValidationErrorsDialog} with errors which is shown when:
	 *         <ul>
	 *         <li>the {@link #getMontage() montage} can not be used to generate
	 *         the
	 */
	public ValidationErrorsDialog getErrorsDialog() {
		return errorsDialog;
	}

	/**
	 * Sets the dialog with errors which is shown when:
	 * <ul>
	 * <li>the {@link #getMontage() montage} can not be used to generate the.
	 *
	 * @param errorsDialog
	 *            the new validation errors dialog with errors which is shown
	 *            when:
	 *            <ul>
	 *            <li>the {@link #getMontage() montage} can not be used to
	 *            generate the
	 */
	public void setErrorsDialog(ValidationErrorsDialog errorsDialog) {
		this.errorsDialog = errorsDialog;
	}

	/**
	 * Gets the {@link MontageGeneratorListModel model} for the
	 * {@link #generatorComboBox}.
	 *
	 * @return the {@link MontageGeneratorListModel model} for the
	 *         {@link #generatorComboBox}
	 */
	protected MontageGeneratorListModel getMontageGeneratorListModel() {
		if (montageGeneratorListModel == null) {
			montageGeneratorListModel = new MontageGeneratorListModel();
		}
		return montageGeneratorListModel;
	}

	/**
	 * Gets the combo-box which allows to select the {@link MontageGenerator
	 * montage genarator}.
	 *
	 * @return the combo-box which allows to select the {@link MontageGenerator
	 *         montage genarator}
	 */
	public JComboBox getGeneratorComboBox() {
		if (generatorComboBox == null) {
			generatorComboBox = new ResolvableComboBox();
			generatorComboBox.setModel(getMontageGeneratorListModel());
			generatorComboBox.setPreferredSize(new Dimension(300,25));

			generatorComboBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					if (lockComboEvents) {
						return;
					}

					Object item = getGeneratorComboBox().getSelectedItem();
					if (montage == null) {
						return;
					}
					if (!(item instanceof IMontageGenerator)) {
						montage.setMontageGenerator(null);
						setEnableds();
						return;
					}

					IMontageGenerator generator = (IMontageGenerator) item;
					setEnableds();
					tryGenerate(generator);

				}

			});

		}
		return generatorComboBox;
	}

	/**
	 * Gets the {@link ShowErrorsAction action} which displays the.
	 *
	 * @return the {@link ShowErrorsAction action} which displays the
	 */
	public ShowErrorsAction getShowErrorsAction() {
		if (showErrorsAction == null) {
			showErrorsAction = new ShowErrorsAction();
		}
		return showErrorsAction;
	}

	/**
	 * Gets the {@link ReloadAction action} which
	 * {@link #tryGenerate(MontageGenerator) generates} a new {@link Montage}
	 * using a currently {@link #getGeneratorComboBox() selected}
	 * {@link MontageGenerator} (on the basis of the {@link #montage old
	 * montage}).
	 *
	 * @return the {@link ReloadAction action} which
	 *         {@link #tryGenerate(MontageGenerator) generates} a new
	 *         {@link Montage} using a currently {@link #getGeneratorComboBox()
	 *         selected} {@link MontageGenerator} (on the basis of the
	 *         {@link #montage old montage})
	 */
	public ReloadAction getReloadAction() {
		if (reloadAction == null) {
			reloadAction = new ReloadAction();
		}
		return reloadAction;
	}

	/**
	 * Gets the button for {@link #getShowErrorsAction() showErrorsAction}.
	 *
	 * @return the button for {@link #getShowErrorsAction() showErrorsAction}
	 */
	public CompactButton getShowErrorsButton() {
		if (showErrorsButton == null) {
			showErrorsButton = new CompactButton(getShowErrorsAction());
		}
		return showErrorsButton;
	}

	/**
	 * Gets the button for {@link #getReloadAction() reloadAction}.
	 *
	 * @return the button for {@link #getReloadAction() reloadAction}
	 */
	public CompactButton getReloadButton() {
		if (reloadButton == null) {
			reloadButton = new CompactButton(getReloadAction());
		}
		return reloadButton;
	}

	/**
	 * Selects the provided {@link MontageGenerator montage generator} in the
	 * {@link #getGeneratorComboBox() generator combo-box}.
	 * While doing that {@link #lockComboEvents suppresses} all the events
	 * associated with this combo-box.
	 * @param generator the {@link MontageGenerator montage generator} to be
	 * selected
	 */
	private void quietSetSelectedGenerator(IMontageGenerator generator) {

		try {
			lockComboEvents = true;
			if (generator == null) {
				getGeneratorComboBox().setSelectedIndex(0);
			} else {
				getGeneratorComboBox().setSelectedItem(generator);
			}
		} finally {
			lockComboEvents = false;
		}
		getGeneratorComboBox().repaint();
		setEnableds();

	}

	/**
	 * Tries to generate a new {@link Montage montage} on the basis of the
	 * current {@link #getMontage() montage} using the provided {@link
	 * MontageGenerator montage generator}:
	 * <ol>
	 * <li>{@link MontageGenerator#validateSourceMontage(SourceMontage, Errors)
	 * Checks} if the current montage can be used with the provided generator.
	 * If not shows the errors.</li>
	 * <li>{@link MontageGenerator#createMontage(Montage) creates} the montage
	 * on the basis of the current montage,</li>
	 * </ol>
	 * @param generator the {@link MontageGenerator montage generator} to be
	 * used
	 */
	public void tryGenerate(IMontageGenerator generator) {

		ValidationErrors errors = new ValidationErrors();
		generator.validateSourceMontage(montage, errors);

		if (errors.hasErrors()) {
			errorsDialog.showDialog(errors);

			if (montage.getMontageGenerator() != null)
				montageGeneratorListModel.setSelectedItem(montage.getMontageGenerator());
			else
				montageGeneratorListModel.setSelectedItem(MontageGeneratorListModel.NO_GENERATOR);

			return;
		}

		try {
			generator.createMontage(montage);
		} catch (MontageException ex) {
			logger.error("Montage generation failed", ex);
			Dialogs.showExceptionDialog(this, ex);
			quietSetSelectedGenerator(null);
			return;
		}

	}

	/**
	 * Enables or disables {@link #getShowErrorsAction() show errors} and
	 * {@link #getReloadAction() reload} button.
	 * If there are errors during the
	 * {@link MontageGenerator#validateSourceMontage(SourceMontage, Errors)
	 * validation} of the {@link #getMontage() montage} show errors button is
	 * enabled and reload button is disabled. If there were no errors the
	 * situation is symmetrical.
	 */
	public void setEnableds() {

		Object item = getGeneratorComboBox().getSelectedItem();
		if (!(item instanceof IMontageGenerator)) {
			getShowErrorsAction().setEnabled(false);
			getReloadAction().setEnabled(false);
			return;
		}

		IMontageGenerator generator = (IMontageGenerator) item;

		if (montage != null) {
			ValidationErrors errors = new ValidationErrors();
			generator.validateSourceMontage(montage, errors);
			boolean hasErrors = errors.hasErrors();

			getShowErrorsAction().setEnabled(hasErrors);
			getReloadAction().setEnabled(!hasErrors);
		}

	}

	/**
	 * Action which displays an error dialog if the current {@link #getMontage()
	 * montage} can not be used to generate a {@link Montage montage} using a
	 * currently {@link #getGeneratorComboBox() selected}
	 * {@link MontageGenerator generator}.
	 */
	protected class ShowErrorsAction extends AbstractAction {

		/** the default serialization constant. */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Creates a new action and sets a label
		 * and an icon for the button associated with this action.
		 */
		public ShowErrorsAction() {
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/errormedium.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION, _("Click to see errors"));
		}

		/**
		 * When the action is performed shows
		 * the errors that appeared during the {@link
		 * MontageGenerator#validateSourceMontage(SourceMontage, Errors)
		 * validation} of the {@link MontageGeneratorPanel#getGeneratorComboBox()
		 * selected} {@link Montage}.
		 */
		@Override
		public void actionPerformed(ActionEvent ev) {

			Object item = getGeneratorComboBox().getSelectedItem();
			if (!(item instanceof IMontageGenerator)) {
				return;
			}

			IMontageGenerator generator = (IMontageGenerator) item;

			ValidationErrors errors = new ValidationErrors();
			generator.validateSourceMontage(montage, errors);

			if (errors.hasErrors()) {
				errorsDialog.showDialog(errors);
			}

		}

	}

	/**
	 * Action which {@link MontageGeneratorPanel#tryGenerate(MontageGenerator)
	 * generates} a new {@link Montage} using a currently {@link
	 * MontageGeneratorPanel#getGeneratorComboBox() selected} {@link
	 * MontageGenerator} (on the basis of the {@link
	 * MontageGeneratorPanel#getMontage() old montage}).
	 */
	protected class ReloadAction extends AbstractAction {

		/** the default serialization constant. */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Creates a new action and sets a label
		 * and an icon for the button associated with this action.
		 */
		public ReloadAction() {
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/reloadmedium.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION, _("Generate again"));
		}

		/**
		 * When the action is performed {@link
		 * MontageGeneratorPanel#tryGenerate(MontageGenerator) generates}
		 * a new {@link Montage} using a {@link
		 * MontageGeneratorPanel#getGeneratorComboBox() selected}
		 * {@link MontageGenerator generator}.
		 */
		@Override
		public void actionPerformed(ActionEvent ev) {

			Object item = getGeneratorComboBox().getSelectedItem();
			if (!(item instanceof IMontageGenerator)) {
				return;
			}

			IMontageGenerator generator = (IMontageGenerator) item;
			tryGenerate(generator);

		}

	}

	/**
	 * The listener associated with the change of the {@link MontageGenerator
	 * montage generator} for the {@link MontageGeneratorPanel#montage montage}.
	 * <p>
	 * When the event occurs:
	 * <ul>
	 * <li>updates the state of the
	 * {@link MontageGeneratorPanel#getGeneratorComboBox() generatorComboBox},</li>
	 * <li>updates the state of the
	 * {@link MontageGeneratorPanel#getReloadAction() reload} and
	 * {@link MontageGeneratorPanel#getShowErrorsAction() show errors} actions,</li>
	 * <li>{@link MontageGenerator#validateSourceMontage(SourceMontage, Errors)
	 * validates} the {@link Montage montage}.</li>
	 * </ul>
	 */
	protected class MontagePropertyListener implements PropertyChangeListener {

		/**
		 * When the property is changed:
		 * <ul>
		 * <li>updates the state of the {@link
		 * MontageGeneratorPanel#getGeneratorComboBox() generatorComboBox},</li>
		 * <li>updates the state of the {@link
		 * MontageGeneratorPanel#getReloadAction() reload} and {@link
		 * MontageGeneratorPanel#getShowErrorsAction() show errors} actions,</li>
		 * <li>{@link MontageGenerator#validateSourceMontage(SourceMontage, Errors)
		 * validates} the {@link Montage montage}.</li></ul>
		 */
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			quietSetSelectedGenerator((IMontageGenerator) evt.getNewValue());
		}

	}

	/**
	 * The listener associated with the addition/removal/change of a.
	 *
	 * {@link SourceChannel source channel} in a montage.
	 * <p>
	 * When any of these events occurs:
	 * <ul>
	 * <li>updates the state of the
	 * {@link MontageGeneratorPanel#getReloadAction() reload} and
	 * {@link MontageGeneratorPanel#getShowErrorsAction() show errors} actions,</li>
	 * <li>{@link MontageGenerator#validateSourceMontage(SourceMontage, Errors)
	 * validates} the {@link Montage montage}.</li>
	 * </ul>
	 */
	protected class SourceMontageChangeListener implements SourceMontageListener {

		/**
		 * <ul>
		 * <li>Updates the state of the {@link
		 * MontageGeneratorPanel#getReloadAction() reload} and {@link
		 * MontageGeneratorPanel#getShowErrorsAction() show errors} actions,</li>
		 * <li>{@link MontageGenerator#validateSourceMontage(SourceMontage, Errors)
		 * validates} the {@link Montage montage}.</li></ul>
		 */
		private void onChange() {
			if (getGeneratorComboBox().getSelectedItem() instanceof IMontageGenerator) {
				setEnableds();
			}
		}

		/**
		 * @see #onChange()
		 */
		@Override
		public void sourceMontageChannelAdded(SourceMontageEvent ev) {
			onChange();
		}

		/**
		 * @see #onChange()
		 */
		@Override
		public void sourceMontageChannelChanged(SourceMontageEvent ev) {
			onChange();
		}

		/**
		 * @see #onChange()
		 */
		@Override
		public void sourceMontageChannelRemoved(SourceMontageEvent ev) {
			onChange();
		}

		@Override
		public void sourceMontageEegSystemChanged(SourceMontageEvent ev) {
			getMontageGeneratorListModel().setEegSystem(montage.getEegSystem());
		}

	}

}
