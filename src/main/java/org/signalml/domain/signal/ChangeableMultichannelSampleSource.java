/* ChangeableMultichannelSampleSource.java created 2010-09-07
 *
 */

package org.signalml.domain.signal;

import java.util.List;

/**
 * This interface represents a multichannel sample source which can be changed
 * by adding new samples to it. Additional functions like semaphore locking
 * and unlocking are available to reinforce multithreaded use.
 *
 * @author Piotr Szachewicz
 */
public interface ChangeableMultichannelSampleSource {

	/**
	 * Returns the number of new samples added counting from last call of
	 * this function.
	 *
	 * @return number of new samples added
	 */
	int getNewSamplesCount();

	/**
	 * Clears the counter of new samples used also by
	 * {@link ChangeableMultichannelSampleSource#getNewSamplesCount()}.
	 */
	void clearNewSamplesCount();

	void addSampleChunk(double[] newSamples);

	void addSamples(List<double[]> newSamples);

	void addSamples(double[] newSamples);

	/**
	 * Acquires the built in binary semaphore. It does not prevent other threads
	 * from using the {@link ChangeableMultichannelSampleSource}, unless their
	 * critical sections are surrounded by
	 * {@link ChangeableMultichannelSampleSource#lock()} and
	 * {@link ChangeableMultichannelSampleSource#unlock()}
	 * methods.
	 */
	void lock();

	/**
	 * Releases the built in binary semaphore.
	 */
	void unlock();

}
