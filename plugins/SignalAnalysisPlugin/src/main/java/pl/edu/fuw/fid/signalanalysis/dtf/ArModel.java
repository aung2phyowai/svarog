package pl.edu.fuw.fid.signalanalysis.dtf;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.complex.ComplexField;
import org.apache.commons.math.linear.Array2DRowFieldMatrix;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.FieldLUDecompositionImpl;
import org.apache.commons.math.linear.FieldMatrix;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import pl.edu.fuw.fid.signalanalysis.MultiSignal;

/**
 * @author ptr@mimuw.edu.pl
 */
public class ArModel {

	private final int C;
	private final double detV;
	private final RealMatrix V;
	private final RealMatrix[] A;
	private final double freqSampling;

	public ArModel(int C, RealMatrix[] A, RealMatrix V, double freqSampling) {
		for (RealMatrix M : A) {
			if (M.getRowDimension() != C || M.getColumnDimension() != C) {
				throw new RuntimeException("matrix dimension mismatch");
			}
		}
		this.C = C;
		this.A = A;
		this.V = V;
		this.detV = new LUDecompositionImpl(V).getDeterminant();
		this.freqSampling = freqSampling;
	}

	public static ArModel compute(MultiSignal signal, int order) {
		final int N = signal.getSampleCount();
		final int C = signal.getChannelCount();

		// calculating mean values
		double[] avg = new double[C];
		for (int c=0; c<C; ++c) {
			for (double x : signal.getData(c)) {
				avg[c] += x;
			}
			avg[c] /= N;
		}

		// calculating variances
		double[] std = new double[C];
		for (int c=0; c<C; ++c) {
			for (double x : signal.getData(c)) {
				double diff = x - avg[c];
				std[c] += diff * diff;
			}
			std[c] = Math.sqrt(std[c] / N);
		}

		// creating matrix with whitened data
		RealMatrix X = new Array2DRowRealMatrix(C, N);
		for (int i=0; i<C; ++i) {
			RealMatrix row = new Array2DRowRealMatrix(signal.getData(i));
			row = row.scalarAdd(-avg[i]).scalarMultiply(1.0/std[i]);
			X.setRowMatrix(i, row.transpose());
		}

		// calculating lag correlations
		RealMatrix[] R = new RealMatrix[1+order];
		for (int s=0; s<=order; ++s) {
			R[s] = new Array2DRowRealMatrix(C, C);
			for (int i=0; i<C; ++i) {
				for (int j=0; j<C; ++j) {
					double sum = 0;
					for (int t=0; t<N-s; ++t) {
						// causality i -> j, so t < t+s
						sum += X.getEntry(i, t) * X.getEntry(j, t+s);
					}
					R[s].setEntry(i, j, sum / N);
				}
			}
		}

		// matrices for Yule-Walker equations
		RealMatrix bigMatrix = new Array2DRowRealMatrix(order*C, order*C);
		RealMatrix bigColumn = new Array2DRowRealMatrix(order*C, C);
		for (int i=0; i<order; ++i) for (int j=0; j<order; ++j) {
			int s = i - j;
			RealMatrix block = (s < 0) ? R[-s].transpose() : R[s];
			bigMatrix.setSubMatrix(block.getData(), i*C, j*C);
		}
		for (int i=0; i<order; ++i) {
			RealMatrix block = R[i+1];
			bigColumn.setSubMatrix(block.getData(), i*C, 0);
		}

		// solution of Yule-Walker equations
		RealMatrix bigMatrixInverse = new LUDecompositionImpl(bigMatrix).getSolver().getInverse();
		// TODO what if not invertible?
		RealMatrix bigSolution = bigMatrixInverse.multiply(bigColumn);

		RealMatrix[] A = new RealMatrix[1+order];
		A[0] = MatrixUtils.createRealIdentityMatrix(C).scalarMultiply(-1);
		for (int s=1; s<=order; ++s) {
			A[s] = bigSolution.getSubMatrix((s-1)*C, s*C-1, 0, C-1);
		}

		// computing residual error
		RealMatrix V = new Array2DRowRealMatrix(C, C);
		for (int t=order; t<N; ++t) {
			RealVector noise = new ArrayRealVector(C);
			for (int s=0; s<=order; ++s) {
				noise = noise.add(A[s].operate(X.getColumnVector(t-s)));
			}
			V = V.add(noise.outerProduct(noise));
		}
		V = V.scalarMultiply(1.0 / N);

		return new ArModel(C, A, V, signal.getSamplingFrequency());
	}

	public ArModelData[][] computeSpectralData(int spectrumSize, boolean normalized) {
		ArModelData[][] data = new ArModelData[C][C];
		for (int i=0; i<C; ++i) for (int j=0; j<C; ++j) {
			data[i][j] = new ArModelData(spectrumSize);
		}
		final double nyquist = 0.5 * getSamplingFrequency();
		for (int f=0; f<spectrumSize; ++f) {
			double freq = f * nyquist / spectrumSize;
			RealMatrix H = computeTransferMatrix(freq, normalized);


			for (int i=0; i<C; ++i) for (int j=0; j<C; ++j) {
				double value = H.getEntry(i, j);
				data[i][j].freqcs[f] = freq;
				data[i][j].values[f] = value;
			}
		}
		return data;
	}

	public RealMatrix computeTransferMatrix(double freq, boolean normalize) {
		FieldMatrix<Complex> S = new Array2DRowFieldMatrix<Complex>(ComplexField.getInstance(), C, C);
		for (int s=0; s<A.length; ++s) {
			Complex exp = new Complex(0, -2*Math.PI*s*freq/freqSampling).exp();
			for (int i=0; i<C; ++i) for (int j=0; j<C; ++j) {
				double val = A[s].getEntry(i, j);
				S.addToEntry(i, j, exp.multiply(val));
			}
		}
		FieldMatrix<Complex> H = new FieldLUDecompositionImpl(S).getSolver().getInverse();

		RealMatrix DTF = new Array2DRowRealMatrix(C, C);
		for (int i=0; i<C; ++i) {
			for (int j=0; j<C; ++j) {
				Complex h = H.getEntry(i, j);
				double re = h.getReal();
				double im = h.getImaginary();
				DTF.setEntry(i, j, re*re + im*im);
			}
		}
		if (normalize) {
			// entry (i, j) represents causality i ->
			for (int j=0; j<C; ++j) {
				double norm = 0;
				for (int i=0; i<C; ++i) {
					norm += DTF.getEntry(i, j);
				}
				norm = 1.0 / norm;
				for (int i=0; i<C; ++i) {
					DTF.multiplyEntry(i, j, norm);
				}
			}
		}

		FieldMatrix<Complex> Hplus = new FieldLUDecompositionImpl(S).getSolver().getInverse();
		FieldMatrix<Complex> cV = new Array2DRowFieldMatrix<Complex>(ComplexField.getInstance(), C, C);
		for (int i=0; i<C; ++i) for (int j=0; j<C; ++j) {
			cV.setEntry(i, j, new Complex(V.getEntry(i, j), 0));
			Hplus.setEntry(i, j, H.getEntry(j, i).conjugate());
		}
		FieldMatrix<Complex> spectrum = H.multiply(cV).multiply(Hplus);
		for (int i=0; i<C; ++i) {
			DTF.setEntry(i, i, spectrum.getEntry(i, i).abs());
		}
		return DTF;
	}

	private static String exportMatrix(RealMatrix M) {
		boolean comma = false;
		String result = "[";
		for (int r=0; r<M.getRowDimension(); ++r) {
			if (comma) result += ",";
			result += exportRow(M.getRow(r));
			comma = true;
		}
		result += "]";
		return result;
	}

	private static String exportRow(double[] row) {
		boolean comma = false;
		String result = "[";
		for (double v : row) {
			if (comma) result += ",";
			result += v;
			comma = true;
		}
		result += "]";
		return result;
	}

	public String exportCoefficients() {
		boolean comma = false;
		String result = "[";
		for (int i=1; i<A.length; ++i) {
			if (comma) result += ",";
			result += exportMatrix(A[i]);
			comma = true;
		}
		result += "]";
		return result;
	}

	public int getChannelCount() {
		return C;
	}

	public double getErrorDeterminant() {
		return detV;
	}

	public double getSamplingFrequency() {
		return freqSampling;
	}

}