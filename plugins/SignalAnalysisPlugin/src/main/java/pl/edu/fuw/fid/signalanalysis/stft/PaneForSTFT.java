package pl.edu.fuw.fid.signalanalysis.stft;

import java.io.IOException;
import java.net.URL;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.signalml.math.fft.WindowType;
import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.signal.ChannelSamples;
import org.signalml.plugin.export.signal.ExportedSignalSelection;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import pl.edu.fuw.fid.signalanalysis.ImageChart;
import pl.edu.fuw.fid.signalanalysis.SimpleSignal;

/**
 * @author ptr@mimuw.edu.pl
 */
public class PaneForSTFT {

	final BorderPane root;
	final ObservableList<WindowType> windowTypeItems;
	final ObservableList<Integer> windowSizeItems;
	final ImageChart chart;
	final ImageRendererForSTFT renderer;

	public PaneForSTFT(SvarogAccessSignal signalAccess, ExportedSignalSelection selection) throws IOException, NoActiveObjectException {
		root = new BorderPane();
		windowTypeItems = FXCollections.observableArrayList(
			WindowType.RECTANGULAR,
			WindowType.BARTLETT,
			WindowType.GAUSSIAN,
			WindowType.HAMMING,
			WindowType.HANN,
			WindowType.WELCH
		);
		windowSizeItems = FXCollections.observableArrayList(
			32, 64, 128, 256, 512, 1024
		);

		URL url = getClass().getResource("SettingsPanelForSTFT.fxml");
		FXMLLoader fxmlLoader = new FXMLLoader(url);
		VBox settingsPanel = fxmlLoader.load();

		final ComboBox windowType = (ComboBox) fxmlLoader.getNamespace().get("windowTypeComboBox");
		windowType.setItems(windowTypeItems);

		final ComboBox windowSize = (ComboBox) fxmlLoader.getNamespace().get("windowSizeComboBox");
		windowSize.setItems(windowSizeItems);
		windowSize.getSelectionModel().selectFirst();

		final double selectionLength = selection.getLength();
		final ChannelSamples samples = signalAccess.getActiveProcessedSignalSamples(selection.getChannel(), (float) selection.getPosition(), (float) selectionLength);
		final double samplingFrequency = samples.getSamplingFrequency();
		final double nyquistFrequency = 0.5 * samplingFrequency;

		final NumberAxis xAxis = new NumberAxis(0.0, selectionLength, 1);
		final NumberAxis yAxis = new NumberAxis(0.0, nyquistFrequency, 1);
		xAxis.setLabel("czas [s]");
		yAxis.setLabel("częstotliwość [Hz]");

		final Slider maxFrequency = (Slider) fxmlLoader.getNamespace().get("frequencySlider");
		maxFrequency.setMax(nyquistFrequency);
		maxFrequency.setValue(nyquistFrequency);
		maxFrequency.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double maxFreq = Math.round(newValue.doubleValue());
				yAxis.setUpperBound(maxFreq);
			}
		});

		SimpleSignal signal = new SimpleSignal() {
			@Override
			public double[] getData() {
				return samples.getSamples();
			}

			@Override
			public double getSamplingFrequency() {
				return samplingFrequency;
			}
		};
		renderer = new ImageRendererForSTFT(signal);
		chart = new ImageChart(xAxis, yAxis, renderer);

		windowType.getSelectionModel().select(renderer.getWindowType());
		windowSize.getSelectionModel().select(renderer.getWindowLength());

		windowType.setOnAction(new EventHandler() {
			@Override
			public void handle(Event event) {
				WindowType wt = (WindowType) windowType.getSelectionModel().getSelectedItem();
				if (wt != null) {
					renderer.setWindowType(wt);
					chart.refreshChartImage();
				}
			}
		});

		windowSize.setOnAction(new EventHandler() {
			@Override
			public void handle(Event event) {
				Integer ws = (Integer) windowSize.getSelectionModel().getSelectedItem();
				if (ws != null) {
					renderer.setWindowLength(ws);
					chart.refreshChartImage();
				}
			}
		});

		final CheckBox padToHeight = (CheckBox) fxmlLoader.getNamespace().get("padToHeightCheckBox");
		padToHeight.setSelected(renderer.getPadToHeight());
		padToHeight.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				renderer.setPadToHeight(newValue);
				chart.refreshChartImage();
			}
		});

		root.setCenter(chart);
		root.setLeft(settingsPanel);
	}

	public Pane getPane() {
		return root;
	}

}
