package org.signalml.app.model.document.opensignal;

import org.signalml.app.model.document.opensignal.elements.Amplifier;
import org.signalml.app.model.document.opensignal.elements.ExperimentStatus;
import org.signalml.app.model.document.opensignal.elements.SignalParameters;
import org.signalml.app.model.monitor.MonitorRecordingDescriptor;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.domain.tag.StyledTagSet;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import multiplexer.jmx.client.JmxClient;

@XStreamAlias(value="experiment")
public class ExperimentDescriptor extends AbstractOpenSignalDescriptor {

	private String id;
	private String name;
	private String path;
	private Amplifier amplifier;
	private ExperimentStatus status;

	private String experimentIPAddress;
	private int experimentPort;

	private String multiplexerAddress;
	private int multiplexerPort;
	private JmxClient jmxClient;

	private Float backupFrequency;
	private MonitorRecordingDescriptor monitorRecordingDescriptor = new MonitorRecordingDescriptor();

	private StyledTagSet tagStyles;
	private String peerId;

	public ExperimentDescriptor(ExperimentDescriptor other) {
		this();

		this.id = other.id;
		this.name = other.name;
		this.path = other.path;
		this.amplifier = new Amplifier(other.getAmplifier());
		this.status = other.status;
		this.experimentIPAddress = other.experimentIPAddress;
		this.signalParameters = new SignalParameters(other.signalParameters);

		this.backupFrequency = other.backupFrequency;

		//other fields should be filled after sending "join_experiment" request
	}

	public ExperimentDescriptor() {
		setBackupFrequency(10.0F);
	}

	public String getName() {
		return name;
	}
	public void setName(String experimentName) {
		this.name = experimentName;
	}
	public Amplifier getAmplifier() {
		return amplifier;
	}
	public void setAmplifier(Amplifier amplifier) {
		this.amplifier = amplifier;
	}
	public ExperimentStatus getStatus() {
		return status;
	}
	public void setStatus(ExperimentStatus experimentStatus) {
		this.status = experimentStatus;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getExperimentIPAddress() {
		return experimentIPAddress;
	}
	public void setExperimentIPAddress(String experimentAddress) {
		this.experimentIPAddress = experimentAddress;
	}
	public int getExperimentPort() {
		return experimentPort;
	}
	public void setExperimentPort(int experimentPort) {
		this.experimentPort = experimentPort;
	}
	public String getMultiplexerAddress() {
		return multiplexerAddress;
	}
	public void setMultiplexerAddress(String multiplexerAddress) {
		this.multiplexerAddress = multiplexerAddress;
	}
	public int getMultiplexerPort() {
		return multiplexerPort;
	}
	public void setMultiplexerPort(int multiplexerPort) {
		this.multiplexerPort = multiplexerPort;
	}

	public Float getBackupFrequency() {
		return backupFrequency;
	}

	public void setBackupFrequency(Float backupFrequency) {
		this.backupFrequency = backupFrequency;
	}

	public JmxClient getJmxClient() {
		return jmxClient;
	}

	public void setJmxClient(JmxClient jmxClient) {
		this.jmxClient = jmxClient;
	}

	public StyledTagSet getTagStyles() {
		return tagStyles;
	}

	public void setTagStyles(StyledTagSet tagStyles) {
		this.tagStyles = tagStyles;
	}

	public MonitorRecordingDescriptor getMonitorRecordingDescriptor() {
		return monitorRecordingDescriptor;
	}

	public void setMonitorRecordingDescriptor(MonitorRecordingDescriptor monitorRecordingDescriptor) {
		this.monitorRecordingDescriptor = monitorRecordingDescriptor;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPeerId() {
		return peerId;
	}

	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}

	@Override
	public String[] getChannelLabels() {
		return amplifier.getSelectedChannelsLabels();
	}

	/**
	 * Multiplexer client to get signal from it.
	 * może nie powinno tego tu być, tylko gdzie indziej.
	 */
	//private JmxClient jmxClient;

}
