/* OpenSignalDescriptor.java created 2007-09-18
 * 
 */

package org.signalml.app.model;

import org.signalml.codec.SignalMLCodec;
import org.signalml.domain.signal.SignalType;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalSampleType;

/** OpenSignalDescriptor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenSignalDescriptor {

	public enum OpenSignalMethod {
		USE_SIGNALML,
		RAW		
	};
		
	private SignalType type;
	
	private OpenSignalMethod method;
	
	// for signalML signals
	private SignalMLCodec codec;
	private SignalParameterDescriptor parameters = new SignalParameterDescriptor();
	
	// for raw signals
	private RawSignalDescriptor rawSignalDescriptor = new RawSignalDescriptor();
			
	public OpenSignalDescriptor() {
		// XXX currently all signals are treated as EEG - there is no way to change this in the GUI
		type = SignalType.EEG_10_20;
		
		rawSignalDescriptor.setSamplingFrequency(128F);
		rawSignalDescriptor.setChannelCount(1);
		rawSignalDescriptor.setSampleType(RawSignalSampleType.FLOAT);
		rawSignalDescriptor.setByteOrder(RawSignalByteOrder.LITTLE_ENDIAN);
		rawSignalDescriptor.setCalibration(1F);
		
	}
	
	public SignalType getType() {
		return type;
	}

	public void setType(SignalType type) {
		this.type = type;
	}

	public OpenSignalMethod getMethod() {
		return method;
	}

	public void setMethod(OpenSignalMethod method) {
		this.method = method;
	}

	public SignalMLCodec getCodec() {
		return codec;
	}

	public void setCodec(SignalMLCodec codec) {
		this.codec = codec;
	}

	public SignalParameterDescriptor getParameters() {
		return parameters;
	}

	public void setParameters(SignalParameterDescriptor parameters) {
		this.parameters = parameters;
	}

	public RawSignalDescriptor getRawSignalDescriptor() {
		return rawSignalDescriptor;
	}

	public void setRawSignalDescriptor(RawSignalDescriptor rawSignalDescriptor) {
		this.rawSignalDescriptor = rawSignalDescriptor;
	}
		
}
