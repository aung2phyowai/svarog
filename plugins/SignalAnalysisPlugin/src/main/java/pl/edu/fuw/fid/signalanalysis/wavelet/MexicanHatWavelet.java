package pl.edu.fuw.fid.signalanalysis.wavelet;

import org.apache.commons.math.complex.Complex;

/**
 * @author ptr@mimuw.edu.pl
 */
public class MexicanHatWavelet extends MotherWavelet {

	private final static double NORM = 1.0 / Math.sqrt(2*Math.PI);

	@Override
	public double getBasicFrequency() {
		return 1.0; // TODO
	}

	@Override
	public double getHalfWidth() {
		return 4.0;
	}

	@Override
	public String getLabel() {
		return "mexican hat";
	}

	@Override
	public Complex value(double t) {
		return new Complex(NORM * (1.0-t*t) * Math.exp(-0.5*t*t), 0.0);
	}

}
