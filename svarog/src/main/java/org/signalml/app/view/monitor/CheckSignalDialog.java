/* CheckSignalDialog.java created 2010-10-24
 *
 */

package org.signalml.app.view.monitor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Window;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import java.util.HashMap;

import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractDialog;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.app.model.MontageDescriptor;
import org.signalml.app.view.montage.VisualReferenceModel;
import org.signalml.util.SvarogConstants;

import org.springframework.context.support.MessageSourceAccessor;

/**
 * Checks and represents the signal state.
 *
 * @author Tomasz Sawicki
 */

public class CheckSignalDialog extends AbstractDialog {

        /**
         * The delay between each check in miliseconds.
         */
        private static final int DELAY = 1000;

        /**
         * Minimum windows size.
         */
        private static final int WINDOW_SIZE = 500;

        /**
         * Amplifier type.
         */
        private static final String AMP_TYPE = "TMSI-porti7";

        /**
         * The current montage.
         */
        private Montage currentMontage;

        /**
         * The currently open document.
         */
        private MonitorSignalDocument monitorSignalDocument;

        /**
         * A component showing the signal status.
         */
        private CheckSignalDisplay checkSignalDisplay;

        /**
         * A current visual reference model.
         */
        private VisualReferenceModel visualReferenceModel;

        /**
         * Timer used to invoke {@link #timerClass} {@link TimerClass#run()}.
         */
        private Timer timer;

        /**
         * Object which periodically does all the checking.
         */
        private TimerClass timerClass;


        public CheckSignalDialog(MessageSourceAccessor messageSource, Window w, boolean isModal) {

                super(messageSource, w, isModal);
        }

        /**
         * Sets window's title and size, then calls {@link AbstractDialog#initialize()}.
         */
	@Override
	protected void initialize() {

		setTitle(messageSource.getMessage("checkSignal.title"));
		setPreferredSize(SvarogConstants.MIN_ASSUMED_DESKTOP_SIZE);
		super.initialize();
		setMinimumSize(new Dimension(WINDOW_SIZE, WINDOW_SIZE));
	}

        /**
         * Creates the interface - a panel with {@link #checkSignalDisplay}.
         *
         * @return the interface
         */
        @Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());
                JPanel editorPanel = new JPanel(new BorderLayout());

                visualReferenceModel = new VisualReferenceModel(messageSource);
		checkSignalDisplay = new CheckSignalDisplay(visualReferenceModel);
		checkSignalDisplay.setBackground(Color.WHITE);

                JScrollPane editorScrollPane = new JScrollPane(checkSignalDisplay, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		checkSignalDisplay.setViewport(editorScrollPane.getViewport());

		editorPanel.setBorder(new CompoundBorder(new TitledBorder(messageSource.getMessage("checkSignal.label")), new EmptyBorder(3,3,3,3)));
                editorPanel.add(editorScrollPane, BorderLayout.CENTER);

		interfacePanel.add(editorPanel, BorderLayout.CENTER);
		return interfacePanel;

	}

        /**
         * Sets the montage and starts the {@link #timer}.
         *
         * @param model  model the model from which this dialog will be filled
         * @throws SignalMLException TODO when it is thrown
         */
        @Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		MontageDescriptor descriptor = (MontageDescriptor) model;
		Montage montage = descriptor.getMontage();
		SignalDocument signalDocument = descriptor.getSignalDocument();

		if (montage == null) {
                        currentMontage = new Montage(new SourceMontage(signalDocument));
                } else {
                        currentMontage = new Montage(montage);
                }

		getOkButton().setVisible(true);
		getRootPane().setDefaultButton(getOkButton());

		setMontage(currentMontage);

                monitorSignalDocument = (MonitorSignalDocument) signalDocument;

                timerClass = new TimerClass(checkSignalDisplay, monitorSignalDocument, AMP_TYPE);
                timerClass.actionPerformed(null);

                timer = new Timer(DELAY, timerClass);
                timer.start();
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		MontageDescriptor descriptor = (MontageDescriptor) model;
		descriptor.setMontage(currentMontage);

	}

        /**
         * Sets the montage for the component.
         *
         * @param montage {@link Montage} object
         */
	private void setMontage(Montage montage) {

		visualReferenceModel.setMontage(montage);

	}

        /**
         * Stops the timer on dialog close. Then calls {@link AbstractDialog#onDialogClose()}.
         */
        @Override
	protected void onDialogClose() {

		super.onDialogClose();
                timer.stop();
		setMontage(null);
	}

        @Override
        public boolean isCancellable() {

		return false;
	}

        @Override
	public boolean supportsModelClass(Class<?> clazz) {

		return MontageDescriptor.class.isAssignableFrom(clazz);
        }

        /**
         * This class' instance is passed to the {@link Timer} object of a {@link CheckSignalDialog}.
         * It gets a {@link GenericAmplifierDiagnosis} object and calls it's {@link GenericAmplifierDiagnosis#signalState()} method
         * constantly in a seperate thread with a given delay.
         */
        private class TimerClass implements ActionListener, Runnable {

                private HashMap<String, Boolean> channels;
                private GenericAmplifierDiagnosis amplifierDiagnosis;
                private CheckSignalDisplay checkSignalDisplay;

                /**
                 * Constructor which gets a {@link GenericAmplifierDiagnosis} based on a {@link MonitorSignalDocument} object
                 * for a given amplifier model.
                 *
                 * @param checkSignalDisplay A {@link CheckSignalDisplay} object to which the information from a amplifier diagnosis will be passed
                 * @param monitorSignalDocument A document representing the signal from an amplifier
                 * @param ampType Name of an amplifier model
                 */
                public TimerClass(CheckSignalDisplay checkSignalDisplay, MonitorSignalDocument monitorSignalDocument, String ampType) {

                        this.checkSignalDisplay = checkSignalDisplay;
                        amplifierDiagnosis = AmplifierDignosisManufacture.getAmplifierDiagnosis(ampType, monitorSignalDocument);
                }

                /**
                 * Compares given channel state to {@link #channels}.
                 *
                 * @param newChannelState Given channel state
                 * @return false if the state is the same, true if there is a difference
                 */
                private boolean compareChannels(HashMap<String, Boolean> newChannelState) {

                        if (channels == null || newChannelState == null) {

                                if (channels == null && newChannelState == null)
                                        return false;
                                else
                                        return true;

                        } else {

                                for (String s : monitorSignalDocument.getSourceChannelLabels()) {

                                        if (channels.get(s) != newChannelState.get(s))
                                                return true;
                                }
                        }

                        return false;
                }

                /**
                 * Calls the {@link #run()} method in a seperate thread.
                 *
                 * @param e {@link ActionEvent} object
                 */
                @Override
                public void actionPerformed(ActionEvent e) {

                        (new Thread(this)).start();
                }

                /**
                 * Gets the signal state, compares it with the old signal state and if there
                 * is a difference: redraws the signal display component.
                 */
                @Override
                public void run() {

                        HashMap<String, Boolean> channels2 = amplifierDiagnosis.signalState();
                        if (compareChannels(channels2)) {

                                channels = channels2;
                                checkSignalDisplay.setChannelsState(channels);
                        }
                }
        }
}