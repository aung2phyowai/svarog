/* OpenMonitorDialog.java created 2010-11-09
 *
 */
package org.signalml.app.view.dialog;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;
import multiplexer.jmx.client.JmxClient;

import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.element.MonitorChannelSelectPanel;
import org.signalml.app.view.element.MonitorRecordingPanel;
import org.signalml.app.view.element.MonitorSignalParametersPanel;
import org.signalml.app.view.element.MultiplexerConnectionPanel;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractDialog;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/**
 * Represents a dialog which is shown when the user wants to open a new monitor
 * document.
 *
 * @author Piotr Szachewicz
 */
public class OpenMonitorDialog extends AbstractDialog implements PropertyChangeListener {

	public static final int TIMEOUT_MILIS = 500;
	public static final int TRYOUT_COUNT = 10;
	public static final Color SUCCESS_COLOR = Color.GREEN;
	public static final Color FAILURE_COLOR = Color.RED;
	private ApplicationConfiguration applicationConfig;
	private ViewerElementManager viewerElementManager = null;
	private MultiplexerConnectionPanel multiplexerConnectionPanel = null;
	private MonitorSignalParametersPanel monitorSignalParametersPanel = null;
	private MonitorChannelSelectPanel monitorChannelSelectPanel = null;
	private MonitorRecordingPanel monitorRecordingPanel = null;

	/**
	 * Creates a new
	 * @param messageSource the message source accessor capable of resolving localized message codes
	 * @param viewerElementManager a {@link ViewerElementManager} which contains
	 * a {@link JmxClient}.
	 */
	public OpenMonitorDialog(MessageSourceAccessor messageSource, ViewerElementManager viewerElementManager) {
		super(messageSource);
		initialize(viewerElementManager);
	}

	/**
	 *
	 * @param messageSource the message source accessor capable of resolving localized message codes
	 * @param viewerElementManager a {@link ViewerElementManager} which contains
	 * a {@link JmxClient}.
	 * @param f the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public OpenMonitorDialog(MessageSourceAccessor messageSource,
		ViewerElementManager viewerElementManager,
		Window f,
		boolean isModal) {
		super(messageSource, f, isModal);
		initialize(viewerElementManager);
	}

	/**
	 * Initializes this window and sets the {@link ViewerElementManager}.
	 * @param viewerElementManager a {@link ViewerElementManager} which contains
	 * a {@link JmxClient}.
	 */
	private void initialize(ViewerElementManager viewerElementManager) {
		this.viewerElementManager = viewerElementManager;
		this.setTitle(messageSource.getMessage("openMonitor.title"));
	}

	/**
	 * Creates the interface of this dialog.
	 * @return the interface of this dialog
	 */
	@Override
	protected JComponent createInterface() {
		JPanel interfacePanel = new JPanel(new GridLayout(2, 2, 10, 10));

		interfacePanel.add(getMultiplexerConnectionPanel());
		interfacePanel.add(getMonitorSignalParametersPanel());
		interfacePanel.add(getMonitorChannelSelectPanel());
		interfacePanel.add(getMonitorRecordingPanel());

		return interfacePanel;
	}

	/**
	 * Returns if the model can be of the given type.
	 * @param clazz the type of the model
	 * @return true the model can be of the given type, false otherwise
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return OpenMonitorDescriptor.class.isAssignableFrom(clazz);
	}

	/**
	 * Fills the fields of this dialog from the given model.
	 * @param model the model from which this dialog will be filled.
	 * @throws SignalMLException TODO when it is thrown
	 */
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		OpenMonitorDescriptor openMonitorDescriptor = (OpenMonitorDescriptor) model;

		getMultiplexerConnectionPanel().fillPanelFromModel(openMonitorDescriptor);

		getMonitorSignalParametersPanel().fillPanelFromModel(openMonitorDescriptor);
		getMonitorChannelSelectPanel().fillPanelFromModel(openMonitorDescriptor);

		if (viewerElementManager.getJmxClient() == null) {
			getOkButton().setEnabled(false);
		} else {
			getOkButton().setEnabled(true);
		}

	}

	/**
	 * Fills the model with the data from this dialog (user input).
	 * @param model the model to be filled
	 * @throws SignalMLException TODO when it is thrown
	 */
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		OpenMonitorDescriptor m = (OpenMonitorDescriptor) model;

		getMultiplexerConnectionPanel().fillModelFromPanel(m);

		m.setJmxClient(viewerElementManager.getJmxClient());
		m.setTagClient(viewerElementManager.getTagClient());

		getMonitorSignalParametersPanel().fillModelFromPanel(m);

		getMonitorChannelSelectPanel().fillModelFromPanel(m);

		getMonitorRecordingPanel().fillModelFromPanel(m);
	}

	protected MultiplexerConnectionPanel getMultiplexerConnectionPanel() {
		if (multiplexerConnectionPanel == null) {
			multiplexerConnectionPanel = new MultiplexerConnectionPanel(viewerElementManager, applicationConfig);
			multiplexerConnectionPanel.getConnectAction().addPropertyChangeListener(this);
			multiplexerConnectionPanel.getDisconnectAction().addPropertyChangeListener(this);
		}
		return multiplexerConnectionPanel;
	}

	protected MonitorSignalParametersPanel getMonitorSignalParametersPanel() {
		if (monitorSignalParametersPanel == null) {
			monitorSignalParametersPanel = new MonitorSignalParametersPanel(messageSource, applicationConfig);
		}
		return monitorSignalParametersPanel;
	}

	protected MonitorChannelSelectPanel getMonitorChannelSelectPanel() {
		if (monitorChannelSelectPanel == null) {
			monitorChannelSelectPanel = new MonitorChannelSelectPanel(messageSource);
		}
		return monitorChannelSelectPanel;
	}

	protected MonitorRecordingPanel getMonitorRecordingPanel() {
		if (monitorRecordingPanel == null) {
			monitorRecordingPanel = new MonitorRecordingPanel(messageSource);
		}
		return monitorRecordingPanel;
	}

	/**
	 * Cancels the connection associated with this dialog.
	 */
	public void cancelConnection() {
		getMultiplexerConnectionPanel().cancel();
	}

	/**
	 * Sets the {@link ApplicationConfiguration configuration} of Svarog.
	 * @param applicationConfig the configuration of Svarog.
	 */
	public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
		this.applicationConfig = applicationConfig;
		getMultiplexerConnectionPanel().setApplicationConfiguration(applicationConfig);
		getMonitorSignalParametersPanel().setApplicationConfiguration(applicationConfig);
	}

	/**
	 * Updates this dialog in reponse to changes in the multiplexer connection status.
	 *
	 * @param evt an event descibing a change.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();

		if ("metadataRetrieved".equals(propertyName)) {
			System.out.println("Metadata retrieved");
			try {
				fillDialogFromModel(getCurrentModel());
			} catch (SignalMLException ex) {
				Logger.getLogger(OpenMonitorDialog.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		else if ("disconnected".equals(propertyName)) {
			try {
				OpenMonitorDescriptor m = ((OpenMonitorDescriptor) getCurrentModel());
				m.setSamplingFrequency(null);
				m.setChannelCount(null);
				m.setChannelLabels(null);


				fillDialogFromModel(getCurrentModel());
			} catch (SignalMLException ex) {
				Logger.getLogger(OpenMonitorDialog.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

	}

	/**
	 * Checks if this dialog is properly filled.
	 * @param model the model for this dialog
	 * @param errors the object in which errors are stored
	 * @throws SignalMLException TODO when it is thrown
	 */
	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {

		super.validateDialog(model, errors);

		fillModelFromDialog(getCurrentModel());

		OpenMonitorDescriptor openMonitorDescriptor = (OpenMonitorDescriptor) model;

		if (openMonitorDescriptor.getSelectedChannelList() == null || openMonitorDescriptor.getSelectedChannelList().length == 0) {
			errors.reject("error.openMonitor.noChannelsSelected");
		}

		getMonitorRecordingPanel().validatePanel(model, errors);

	}
}
