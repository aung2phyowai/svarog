/* SignalFFTTool.java created 2007-12-16
 *
 */
package org.signalml.app.view.signal;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;

import org.signalml.app.config.SignalFFTSettings;
import org.signalml.app.util.IconUtils;
import org.signalml.domain.signal.ChangeableMultichannelSampleSource;
import org.signalml.plugin.export.signal.AbstractSignalTool;

/** SignalFFTTool
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalFFTTool extends AbstractSignalTool {

	/**
	 * logger to save history of execution at
	 */
	protected static final Logger logger = Logger.getLogger(SignalFFTTool.class);

	/**
	 * The number of milliseconds between each FFT plot recalculations.
	 * Describes how often the FFT plot is recalculated and redrawn.
	 */
	protected static final int fftRefreshingPeriod = 1000;

	private SignalPlot plot;
	private SignalFFTPlot fftPlot;

	private SignalFFTSettings settings;

	/**
	 * True if the FFT refreshing thread is running, false otherwise.
	 */
	private boolean fftRefreshingThreadRunning = false;

	/**
	 * The thread which recalculates and redraws the FFT plot once in
	 * a {@link SignalFFTTool#fftRefreshingPeriod}.
	 */
	private FFTRefreshingThread fftRefreshingThread;

	public SignalFFTTool(SignalView signalView) {
		super(signalView);
		fftPlot = new SignalFFTPlot(signalView.getMessageSource());
		settings = new SignalFFTSettings();
	}

	@Override
	public Cursor getDefaultCursor() {
		return IconUtils.getCrosshairCursor();
	}

	@Override
	public void mousePressed(MouseEvent e) {

		if (SwingUtilities.isLeftMouseButton(e)) {

			Object source = e.getSource();
			if (!(source instanceof SignalPlot)) {
				plot = null;
				return;
			}
			plot = (SignalPlot) source;

			Point point = e.getPoint();
			int channel = plot.toChannelSpace(point);
			fftPlot.setSettings(settings);
			if (e.isControlDown()) {
				Dimension plotSize = settings.getPlotSize();
				int width = Math.min(800, plotSize.width * 2);
				int height = Math.min(600, plotSize.height * 2);
				Dimension size = new Dimension(width, height);
				fftPlot.setPlotSize(size);
				fftPlot.setWindowWidth(2*settings.getWindowWidth());
			}
			fftPlot.setParameters(plot, point, channel);
			showFFT(point);
			selectAround(point);
			setEngaged(true);
			e.consume();

			if (getSignalView().getActiveSignalDocument().getSampleSource() instanceof ChangeableMultichannelSampleSource)
				startFFTRefreshing();
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			stopFFTRefreshing();
			hideFFT();
			setEngaged(false);
			plot = null;
			e.consume();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {

		if (plot != null) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				Point point = e.getPoint();
				Rectangle r = new Rectangle(point.x, point.y, 1, 1);
				((SignalPlot)e.getSource()).scrollRectToVisible(r);
				if( settings.isChannelSwitching() ) {
					int channel = plot.toChannelSpace(point);
					fftPlot.setParameters(point, channel);
				} else {
					fftPlot.setFocusPoint(point);
				}
				positionFFT(point, null);
				selectAround(point);
			}
		}

	}

	public SignalFFTSettings getSettings() {
		return settings;
	}

	public void setSettings(SignalFFTSettings settings) {
		if (settings == null) {
			throw new NullPointerException("No settings");
		}
		this.settings = settings;
	}

	private void positionFFT(Point point, JLayeredPane layeredPane) {
		if (plot != null) {
			if (layeredPane == null) {
				layeredPane = plot.getRootPane().getLayeredPane();
			}
			Dimension size = fftPlot.getPreferredSize();
			int channel = fftPlot.getChannel();
			int channelY = plot.channelToPixel(channel);
			Point location = SwingUtilities.convertPoint(plot, new Point(point.x, channelY), layeredPane);
			int y;
			if (location.y > layeredPane.getHeight() / 2) {
				y = location.y - size.height;
			} else {
				y = location.y + plot.getPixelPerChannel();
			}
			fftPlot.setBounds(location.x-(size.width/2), y, size.width, size.height);
		}
	}

	private void showFFT(Point point) {
		if (plot != null) {
			fftPlot.setVisible(true);
			JLayeredPane layeredPane = plot.getRootPane().getLayeredPane();
			positionFFT(point, layeredPane);
			layeredPane.add(fftPlot, new Integer(JLayeredPane.DRAG_LAYER));
		}
	}

	private void hideFFT() {
		if (plot != null) {
			JLayeredPane layeredPane = plot.getRootPane().getLayeredPane();
			layeredPane.remove(fftPlot);
			plot.getRootPane().repaint();
		}
	}

	private void selectAround(Point point) {
		if (plot != null) {
			Float centerPosition = plot.toTimeSpace(point);
			if (centerPosition != null) {
				double offset = (((float) settings.getWindowWidth()) / plot.getSamplingFrequency()) / 2;
				Float startPosition = new Float(centerPosition.floatValue() - ((float) offset));
				Float endPosition = new Float(centerPosition.floatValue() + ((float) offset));
				if (startPosition.equals(endPosition)) {
				    getSignalView().clearSignalSelection();
				} else {
					Integer channel = fftPlot.getChannel();
					if (channel != null) {
					    getSignalView().setSignalSelection(plot,plot.getChannelSelection(startPosition, endPosition, channel));
					}
				}
			}
		}
	}

	/**
	 * Begins to refresh FFT plot once in a {@link SignalFFTTool#fftRefreshingPeriod}.
	 * Refreshing happens until {@link SignalFFTTool#stopFFTRefreshing()}
	 * method is called.
	 */
	private void startFFTRefreshing() {
		fftRefreshingThreadRunning = true;
		if (fftRefreshingThread == null || !fftRefreshingThread.isAlive()) {
			fftRefreshingThread = new FFTRefreshingThread();
			fftRefreshingThread.start();
		}
	}

	/**
	 * Stops the refreshing of the FFT plot.
	 */
	private void stopFFTRefreshing() {
		fftRefreshingThreadRunning = false;
	}

	/**
	 * A {@link Thread} which performs the operation of recalculating
	 * and redrawing the FFT plot at a distincts periods of time
	 * (specified by the {@link SignalFFTTool#fftRefreshingPeriod}).
	 */
	private class FFTRefreshingThread extends Thread {

		@Override
		public void run() {
			while (fftRefreshingThreadRunning) {
				logger.debug("FFT plot was recalculated.");
				fftPlot.recalculateAndRepaint();
				try {
					Thread.sleep(fftRefreshingPeriod);
				} catch (InterruptedException ex) {
					logger.error("InterruptedException occurred in the FFTRefreshingThread in the FignalFFTTool.");
				}
			}
		}

	}

}
