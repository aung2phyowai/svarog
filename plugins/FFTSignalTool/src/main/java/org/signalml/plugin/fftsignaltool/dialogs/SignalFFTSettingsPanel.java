/* SignalFFTSettingsPanel.java created 2007-12-17
 *
 */
package org.signalml.plugin.fftsignaltool.dialogs;

import static org.signalml.plugin.fftsignaltool.FFTSignalTool._;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.math.fft.WindowType;
import org.signalml.plugin.export.i18n.SvarogAccessI18n;
import org.signalml.plugin.fftsignaltool.SignalFFTSettings;

/**
 * Panel which allows to select the parameters of the FFT. Contains 6
 * sub-panels:
 * <ul>
 * <li>The panel which allows to select the size of the FFT window (the number
 * of samples). The size can be selected from predefined values (powers of 2
 * from 64 to 2048) or entered custom.</li>
 * <li>The panel which allows to select the parameters of the view such as the
 * frequency range and the number of labels on the x axis.</li>
 * <li>The panel which allows to select the width of the FFT plot (the number of
 * pixels). The size can be selected from predefined values from 300 to 800.</li>
 * <li>The panel which allows to select the height of the FFT plot (the number
 * of pixels). The size can be selected from predefined values from 200 to 700.</li>
 * <li>The {@link FFTWindowTypePanel panel} which allows to select the
 * {@link WindowType type} of the FFT window,</li>
 * <li>The panel which allows to select other options of the FFT plot:
 * <ul>
 * <li>if the channel should be switched when the mouse goes up or down,</li>
 * <li>if the amplitude should be displayed in the logarithmic scale,</li>
 * <li>if the plot should be rendered using {@link XYSplineRenderer splines},</li>
 * <li>if the title describing the FFT plot (name of the channel, time interval
 * and number of samples) should be visible,</li>
 * <li>if the labels for frequencies axis should be visible,</li>
 * <li>if the label for power axis should be visible,</li>
 * </ul>
 * </li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class SignalFFTSettingsPanel extends JPanel {

	/**
	 * the serialization constant
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the logger
	 */
	protected static final Logger logger = Logger
										   .getLogger(SignalFFTSettingsPanel.class);

	/**
	 * the array with possible sizes of FFT window (number of samples)
	 */
	private int[] possibleWindowWidths = new int[] { 64, 128, 256, 512, 1024,
			2048
												   };

	/**
	 * the array with possible widths of a FFT plot (pixels)
	 */
	private int[] possiblePlotWidths = new int[] { 300, 400, 500, 600, 700, 800 };

	/**
	 * the array with possible heights of a FFT plot (pixels)
	 */
	private int[] possiblePlotHeights = new int[] { 200, 300, 400, 500, 600,
			700
												  };

	/**
	 * the {@link FFTWindowTypePanel panel} which allows to select the
	 * {@link WindowType type} of the FFT window
	 */
	private FFTWindowTypePanel fftWindowTypePanel;

	/**
	 * the radio button which allows to select custom (specified in in
	 * {@link #customWindowWidthTextField}) size of the FFT window (number of
	 * samples)
	 */
	private JRadioButton customWindowWidthRadioButton;

	/**
	 * the array with radio buttons for {@link #possibleWindowWidths possible
	 * sizes} of a FFT window
	 */
	private JRadioButton[] windowWidthRadioButtons;
	/**
	 * the array with radio buttons for {@link #possiblePlotWidths possible
	 * widths} of a FFT plot (pixels)
	 */
	private JRadioButton[] plotWidthRadioButtons;
	/**
	 * the array with buttons for {@link #possiblePlotHeights possible heights}
	 * of a FFT plot (pixels)
	 */
	private JRadioButton[] plotHeightRadioButtons;

	/**
	 * the check-box which tells if the channel should be switched (checked)
	 * when the mouse goes up or down or should remain the same (checked)
	 */
	private JCheckBox channelSwitchingCheckBox;
	/**
	 * the check-box which tells if the amplitude should be displayed in the
	 * logarithmic scale (checked)
	 */
	private JCheckBox logarithmicCheckBox;
	/**
	 * the check-box which tells if the plot should be rendered using
	 * {@link XYSplineRenderer splines}
	 */
	private JCheckBox splineCheckBox;
	/**
	 * the check-box which tells if the signal should be antialiased
	 */
	private JCheckBox antialiasCheckBox;
	/**
	 * the check-box which tells if the title describing the FFT plot (name of
	 * the channel, time interval and number of samples) should be visible
	 * (checked)
	 */
	private JCheckBox titleVisibleCheckBox;
	/**
	 * the check-box which tells if the labels for frequencies axies should be
	 * visible (checked)
	 */
	private JCheckBox frequencyAxisLabelsVisibleCheckBox;
	/**
	 * the check-box which tells if the label for power axis should be visible
	 * (checked)
	 */
	private JCheckBox powerAxisLabelsVisibleCheckBox;

	/**
	 * the text field which allows to enter the size (number of samples) of the
	 * FFT window
	 */
	private JTextField customWindowWidthTextField;

	/**
	 * the text field which allows to enter the beginning of the visible
	 * frequency range (Hz)
	 */
	private JTextField visibleRangeStartTextField;
	/**
	 * the text field which allows to enter the end of the visible frequency
	 * range (Hz)
	 */
	private JTextField visibleRangeEndTextField;
	private JTextField customMinYAxis;
	private JTextField customMaxYAxis;
	/**
	 * the text field which allows to enter how many labels should be displayed
	 * on the X axis
	 */
	private JTextField maxLabelCountTextField;

	private JRadioButton autoScaleYAxisRadioButton;

	/**
	 * the group of {@link #windowWidthRadioButtons radio buttons} for the FFT
	 * window size
	 */
	private ButtonGroup windowWidthButtonGroup;
	private ButtonGroup autoScaleYAxisButtonGroup;
	/**
	 * the group of {@link #plotWidthRadioButtons radio buttons} for the FFT
	 * plot width (pixels)
	 */
	private ButtonGroup plotWidthButtonGroup;
	/**
	 * the group of {@link #plotHeightRadioButtons radio buttons} for the FFT
	 * plot height (pixels)
	 */
	private ButtonGroup plotHeightButtonGroup;

	/**
	 * the cached {@link #customWindowWidthTextField custom size} of the FFT
	 * window (number of samples)
	 */
	private String cachedWindowWidth = null;

	/**
	 * {@code true} if the panel has a cross which closes it, {@code false}
	 * otherwise
	 */
	private boolean hasCloseCross;

	private JRadioButton fixedYAxisRadioButton;

	/**
	 * Constructor. Sets the {@link SvarogAccessI18n message source} and
	 * initializes this panel.
	 *
	 * @param hasCloseCross
	 *            {@code true} if the panel should has a cross which closes it,
	 *            {@code false} otherwise
	 */
	public SignalFFTSettingsPanel(
		boolean hasCloseCross) {
		super();
		this.hasCloseCross = hasCloseCross;
		initialize();
	}

	/**
	 * Initializes this panel with BoxLayout and 6 sub-panels:
	 * <ul>
	 * <li>The panel which allows to select the size of the FFT window (the
	 * number of samples). The size can be selected from predefined values
	 * (powers of 2 from 64 to 2048) or entered custom.</li>
	 * <li>The panel which allows to select the parameters of the view such as
	 * the frequency range and the number of labels on the x axis.</li>
	 * <li>The panel which allows to select the width of the FFT plot (the
	 * number of pixels). The size can be selected from predefined values from
	 * 300 to 800.</li>
	 * <li>The panel which allows to select the height of the FFT plot (the
	 * number of pixels). The size can be selected from predefined values from
	 * 200 to 700.</li>
	 * <li>The {@link FFTWindowTypePanel panel} which allows to select the
	 * {@link WindowType type} of the FFT window,</li>
	 * <li>The panel which allows to select other options of the FFT plot:
	 * <ul>
	 * <li>if the channel should be switched when the mouse goes up or down,</li>
	 * <li>if the amplitude should be displayed in the logarithmic scale,</li>
	 * <li>if the plot should be rendered using {@link XYSplineRenderer splines}
	 * ,</li>
	 * <li>if the title describing the FFT plot (name of the channel, time
	 * interval and number of samples) should be visible,</li>
	 * <li>if the labels for frequencies axis should be visible,</li>
	 * <li>if the label for power axis should be visible,</li>
	 * </ul>
	 * </li>
	 * </ul>
	 */
	private void initialize() {

		windowWidthButtonGroup = new ButtonGroup();
		plotWidthButtonGroup = new ButtonGroup();
		plotHeightButtonGroup = new ButtonGroup();

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel windowWidthPanel = new JPanel(new GridLayout(3, 3, 3, 3));

		CompoundBorder border = new CompoundBorder(new TitledCrossBorder(
					_("FFT window width"),
					hasCloseCross), new EmptyBorder(3, 3, 3, 3));
		windowWidthPanel.setBorder(border);

		ItemListener cachingListener = new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
				if (selected) {
					cachedWindowWidth = ((JRadioButton) e.getItem()).getText();
				}
			}

		};

		windowWidthRadioButtons = new JRadioButton[possibleWindowWidths.length];
		int i;
		for (i = 0; i < possibleWindowWidths.length; i++) {
			windowWidthRadioButtons[i] = new JRadioButton(
				Integer.toString(possibleWindowWidths[i]));
			windowWidthButtonGroup.add(windowWidthRadioButtons[i]);
			windowWidthPanel.add(windowWidthRadioButtons[i]);
			windowWidthRadioButtons[i].addItemListener(cachingListener);
		}

		windowWidthRadioButtons[0].setSelected(true);

		customWindowWidthRadioButton = new JRadioButton(
			_("Custom"));
		windowWidthButtonGroup.add(customWindowWidthRadioButton);
		windowWidthPanel.add(customWindowWidthRadioButton);

		customWindowWidthTextField = new JTextField("");
		customWindowWidthTextField.setPreferredSize(new Dimension(80, 25));
		customWindowWidthTextField.setEnabled(false);

		windowWidthPanel.add(customWindowWidthTextField);

		customWindowWidthRadioButton.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
				if (selected) {
					if (customWindowWidthTextField.getText().isEmpty()) {
						if (cachedWindowWidth == null) {
							customWindowWidthTextField
							.setText(windowWidthRadioButtons[0]
									 .getText());
						} else {
							customWindowWidthTextField
							.setText(cachedWindowWidth);
						}
					}
				}
				customWindowWidthTextField.setEnabled(selected);
			}

		});

		JPanel fftViewPanel = new JPanel(new GridLayout(3, 2, 3, 3));
		{
			border = new CompoundBorder(
				new TitledBorder(_("FFT view")),
				new EmptyBorder(3, 3, 3, 3));
			fftViewPanel.setBorder(border);

			JLabel label = new JLabel(
				_("Show fq range [Hz]"));

			InputVerifier intInputVerifier = new InputVerifier() {
				@Override
				public boolean verify(JComponent input) {
					try {
						String text = ((JTextField) input).getText().trim();
						if (text.length() == 0 || Integer.parseInt(text) >= 0)
							return true;
						else {
							JOptionPane
							.showMessageDialog(
								input.getParent(),
								_("Positive integer required"));
							return false;
						}
					} catch (NumberFormatException nfe) {
						JOptionPane
						.showMessageDialog(
							input.getParent(),
							_("Positive integer required"));
						return false;
					}
				}
			};

			JPanel rangePanel = new JPanel();
			visibleRangeStartTextField = new JTextField();
			visibleRangeStartTextField.setColumns(5);
			visibleRangeStartTextField.setInputVerifier(intInputVerifier);
			visibleRangeEndTextField = new JTextField();
			visibleRangeEndTextField.setColumns(5);
			visibleRangeEndTextField.setInputVerifier(intInputVerifier);
			rangePanel.add(label);
			rangePanel.add(visibleRangeStartTextField);
			rangePanel.add(new JLabel("-"));
			rangePanel.add(visibleRangeEndTextField);


			JPanel countPanel = new JPanel();

			label = new JLabel(
				_("X-axis label count"));
			countPanel.add(label);

			maxLabelCountTextField = new JTextField();
			maxLabelCountTextField.setColumns(5);
			maxLabelCountTextField.setInputVerifier(intInputVerifier);
			countPanel.add(maxLabelCountTextField);
			fftViewPanel.add(countPanel);
			fftViewPanel.add(rangePanel);

			autoScaleYAxisButtonGroup = new ButtonGroup();

			autoScaleYAxisRadioButton = new JRadioButton(
					_("Fit Y-axis to data"));
			fixedYAxisRadioButton = new JRadioButton(
					_("Keep Y-axis fixed"));
			autoScaleYAxisButtonGroup.add(autoScaleYAxisRadioButton);
			autoScaleYAxisButtonGroup.add(fixedYAxisRadioButton);
			fixedYAxisRadioButton.setSelected(true);
			fftViewPanel.add(autoScaleYAxisRadioButton);

			InputVerifier doubleInputVerifier = new InputVerifier() {
				@Override
				public boolean verify(JComponent input) {
					try {
						String text = ((JTextField) input).getText().trim();
						if (text.length() == 0 || Double.parseDouble(text) >= 0)
							return true;
						else {
							JOptionPane
									.showMessageDialog(
											input.getParent(),
											_("Positive double required"));
							return false;
						}
					} catch (NumberFormatException nfe) {
						JOptionPane
								.showMessageDialog(
										input.getParent(),
										_("Positive double required"));
						return false;
					}
				}
			};
			JPanel yAxisRangePanel = new JPanel();
			yAxisRangePanel.add(fixedYAxisRadioButton);
			customMinYAxis = new JTextField();
			customMinYAxis.setColumns(5);
			customMinYAxis.setInputVerifier(doubleInputVerifier);
			customMaxYAxis = new JTextField();
			customMaxYAxis.setColumns(5);
			customMaxYAxis.setInputVerifier(doubleInputVerifier);
			yAxisRangePanel.add(customMinYAxis);
			yAxisRangePanel.add(new JLabel("-"));
			yAxisRangePanel.add(customMaxYAxis);
			fftViewPanel.add(yAxisRangePanel);
			fixedYAxisRadioButton.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					customMinYAxis.setEnabled(((JRadioButton)e.getSource()).isSelected());
					customMaxYAxis.setEnabled(((JRadioButton)e.getSource()).isSelected());
				}
			});
		}

		JPanel plotWidthPanel = new JPanel(new GridLayout(1, 4, 3, 3));

		border = new CompoundBorder(new TitledBorder(
										_("Plot window width")),
									new EmptyBorder(3, 3, 3, 3));
		plotWidthPanel.setBorder(border);

		Dimension radioSize = new Dimension(50, 20);

		plotWidthRadioButtons = new JRadioButton[possiblePlotWidths.length];
		for (i = 0; i < possiblePlotWidths.length; i++) {
			plotWidthRadioButtons[i] = new JRadioButton(
				Integer.toString(possiblePlotWidths[i]));
			plotWidthRadioButtons[i].setPreferredSize(radioSize);
			plotWidthButtonGroup.add(plotWidthRadioButtons[i]);
			plotWidthPanel.add(plotWidthRadioButtons[i]);
		}

		plotWidthRadioButtons[0].setSelected(true);

		JPanel plotHeightPanel = new JPanel(new GridLayout(1, 4, 3, 3));

		border = new CompoundBorder(new TitledBorder(
										_("Plot window height")),
									new EmptyBorder(3, 3, 3, 3));
		plotHeightPanel.setBorder(border);

		plotHeightRadioButtons = new JRadioButton[possiblePlotHeights.length];
		for (i = 0; i < possiblePlotHeights.length; i++) {
			plotHeightRadioButtons[i] = new JRadioButton(
				Integer.toString(possiblePlotHeights[i]));
			plotHeightRadioButtons[i].setPreferredSize(radioSize);
			plotHeightButtonGroup.add(plotHeightRadioButtons[i]);
			plotHeightPanel.add(plotHeightRadioButtons[i]);
		}

		plotHeightRadioButtons[0].setSelected(true);

		fftWindowTypePanel = new FFTWindowTypePanel(false);

		JPanel optionsPanel = new JPanel(new GridLayout(4, 2, 3, 3));

		border = new CompoundBorder(new TitledBorder(
										_("Options")),
									new EmptyBorder(3, 3, 3, 3));
		optionsPanel.setBorder(border);

		channelSwitchingCheckBox = new JCheckBox(
			_("Sticky channel"));
		logarithmicCheckBox = new JCheckBox(
			_("Logarithmic"));
		antialiasCheckBox = new JCheckBox(
			_("Antialias"));
		splineCheckBox = new JCheckBox(
			_("Use splines (experimental)"));
		titleVisibleCheckBox = new JCheckBox(
			_("Title"));
		frequencyAxisLabelsVisibleCheckBox = new JCheckBox(
			_("Freq. lables"));
		powerAxisLabelsVisibleCheckBox = new JCheckBox(
			_("Power labels"));

		optionsPanel.add(channelSwitchingCheckBox);
		optionsPanel.add(logarithmicCheckBox);
		optionsPanel.add(antialiasCheckBox);
		optionsPanel.add(splineCheckBox);
		optionsPanel.add(frequencyAxisLabelsVisibleCheckBox);
		optionsPanel.add(powerAxisLabelsVisibleCheckBox);
		optionsPanel.add(titleVisibleCheckBox);

		add(windowWidthPanel);
		add(fftViewPanel);
		add(plotWidthPanel);
		add(plotHeightPanel);
		add(fftWindowTypePanel);
		add(optionsPanel);

		Dimension size = getPreferredSize();
		if (size.width < 150) {
			size.width = 150;
		}
		setPreferredSize(size);

	}

	/**
	 * Fills the fields of this panel from the given {@link SignalFFTSettings
	 * model}:
	 * <ul>
	 * <li>the width and height of the plot,
	 * <li>
	 * <li>the size of the FFT window (number of samples),</li>
	 * <li>the {@link WindowType type} of the FFT window,</li>
	 * <li>the additional options such as if the title and labels should be
	 * visible or the signal should be antialiased.</li>
	 * </ul>
	 *
	 * @param settings
	 *            the model
	 */
	public void fillPanelFromModel(SignalFFTSettings settings) {

		int windowWidth = settings.getWindowWidth();
		int i;
		boolean found = false;
		for (i = 0; i < possibleWindowWidths.length; i++) {
			if (windowWidth == possibleWindowWidths[i]) {
				windowWidthRadioButtons[i].setSelected(true);
				found = true;
				break;
			}
		}
		if (!found) {
			customWindowWidthRadioButton.setSelected(true);
			customWindowWidthTextField.setText(Integer.toString(windowWidth));
		}

		if (settings.getVisibleRangeStart() > Integer.MIN_VALUE) {
			visibleRangeStartTextField.setText(""
											   + settings.getVisibleRangeStart());
		} else {
			visibleRangeStartTextField.setText("");
		}

		if (settings.getVisibleRangeEnd() < Integer.MAX_VALUE) {
			visibleRangeEndTextField
			.setText("" + settings.getVisibleRangeEnd());
		} else {
			visibleRangeEndTextField.setText("");
		}

		if (settings.getMaxLabelCount() < Integer.MAX_VALUE) {
			maxLabelCountTextField.setText("" + settings.getMaxLabelCount());
		} else {
			maxLabelCountTextField.setText("");
		}

		autoScaleYAxisRadioButton.setSelected(settings.isAutoScaleYAxis());
		customMaxYAxis.setText(""+settings.getMaxPowerAxis());
		customMinYAxis.setText(""+settings.getMinPowerAxis());

		Dimension plotSize = settings.getPlotSize();
		for (i = 0; i < possiblePlotWidths.length; i++) {
			if (plotSize.width == possiblePlotWidths[i]) {
				plotWidthRadioButtons[i].setSelected(true);
				break;
			}
		}
		for (i = 0; i < possiblePlotHeights.length; i++) {
			if (plotSize.height == possiblePlotHeights[i]) {
				plotHeightRadioButtons[i].setSelected(true);
				break;
			}
		}

		fftWindowTypePanel.fillPanelFromModel(settings);

		channelSwitchingCheckBox.setSelected(!settings.isChannelSwitching()); // note
		// the
		// description
		// of
		// this
		// field
		// causes
		// a
		// yes/no
		// meaning
		// inversion
		logarithmicCheckBox.setSelected(settings.isLogarithmic());
		antialiasCheckBox.setSelected(settings.isAntialias());
		splineCheckBox.setSelected(settings.isSpline());
		titleVisibleCheckBox.setSelected(settings.isTitleVisible());
		frequencyAxisLabelsVisibleCheckBox.setSelected(settings
				.isFrequencyAxisLabelsVisible());
		powerAxisLabelsVisibleCheckBox.setSelected(settings
				.isPowerAxisLabelsVisible());

	}

	/**
	 * Stores the user input in the given {@link SignalFFTSettings model}:
	 * <ul>
	 * <li>the width and height of the plot,
	 * <li>
	 * <li>the size of the FFT window (number of samples),</li>
	 * <li>the {@link WindowType type} of the FFT window,</li>
	 * <li>the additional options such as if the title and labels should be
	 * visible or the signal should be antialiased.</li>
	 * </ul>
	 *
	 * @param settings
	 *            the model
	 */
	public void fillModelFromPanel(SignalFFTSettings settings) {

		int i;
		boolean found = false;
		for (i = 0; i < possibleWindowWidths.length; i++) {
			if (windowWidthRadioButtons[i].isSelected()) {
				settings.setWindowWidth(possibleWindowWidths[i]);
				found = true;
				break;
			}
		}
		if (!found) {
			if (customWindowWidthRadioButton.isSelected()) {
				settings.setWindowWidth(Integer
										.parseInt(customWindowWidthTextField.getText()));
			} else {
				throw new RuntimeException("Nothing selected");
			}
		}

		String val = visibleRangeStartTextField.getText().trim();
		if (val.length() > 0) {
			try {
				settings.setVisibleRangeStart(Integer.parseInt(val));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				settings.setVisibleRangeStart(Integer.MIN_VALUE);

			}
		} else {
			settings.setVisibleRangeStart(Integer.MIN_VALUE);
		}
		val = visibleRangeEndTextField.getText().trim();
		if (val.length() > 0) {
			try {
				settings.setVisibleRangeEnd(Integer.parseInt(val));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				settings.setVisibleRangeEnd(Integer.MAX_VALUE);

			}
		} else {
			settings.setVisibleRangeEnd(Integer.MAX_VALUE);
		}
		val = maxLabelCountTextField.getText().trim();
		if (val.length() > 0) {
			try {
				settings.setMaxLabelCount(Integer.parseInt(val));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				settings.setMaxLabelCount(Integer.MAX_VALUE);
			}
		} else {
			settings.setMaxLabelCount(Integer.MAX_VALUE);
		}
		settings.setAutoScaleYAxis(autoScaleYAxisRadioButton.isSelected());
		settings.setMaxPowerAxis(Double.parseDouble(customMaxYAxis.getText()));
		settings.setMinPowerAxis(Double.parseDouble(customMinYAxis.getText()));

		Dimension plotSize = new Dimension(600, 200);
		for (i = 0; i < possiblePlotWidths.length; i++) {
			if (plotWidthRadioButtons[i].isSelected()) {
				plotSize.width = possiblePlotWidths[i];
				break;
			}
		}
		for (i = 0; i < possiblePlotHeights.length; i++) {
			if (plotHeightRadioButtons[i].isSelected()) {
				plotSize.height = possiblePlotHeights[i];
				break;
			}
		}

		settings.setPlotSize(plotSize);

		fftWindowTypePanel.fillModelFromPanel(settings);

		settings.setChannelSwitching(!channelSwitchingCheckBox.isSelected()); // note
		// the
		// description
		// of
		// this
		// field
		// causes
		// a
		// yes/no
		// meaning
		// inversion
		settings.setLogarithmic(logarithmicCheckBox.isSelected());
		settings.setAntialias(antialiasCheckBox.isSelected());
		settings.setSpline(splineCheckBox.isSelected());
		settings.setTitleVisible(titleVisibleCheckBox.isSelected());
		settings.setFrequencyAxisLabelsVisible(frequencyAxisLabelsVisibleCheckBox
											   .isSelected());
		settings.setPowerAxisLabelsVisible(powerAxisLabelsVisibleCheckBox
										   .isSelected());

	}

	/**
	 * Validates this panel. This panel can be invalid only in the custom window
	 * size is selected and the entered value is in bad format or to small (
	 * {@code < 8}).
	 *
	 * @param errors
	 *            the object in which the errors are stored
	 */
	public void validatePanel(ValidationErrors errors) {

		if (customWindowWidthRadioButton.isSelected()) {
			try {
				int windowWidth = Integer.parseInt(customWindowWidthTextField
												   .getText());
				if (windowWidth < 8) {
					errors.addError(_("Bad window width. Must be an integer greater than 7"));
				}
			} catch (NumberFormatException ex) {
				errors.addError(_("Bad window width. Must be an integer greater than 7"));
			}
		}

		fftWindowTypePanel.validatePanel(errors);
		double maxY = Double.parseDouble(customMaxYAxis.getText());
		double minY = Double.parseDouble(customMinYAxis.getText());
		if (maxY <= minY)
			errors.addError(_("Bad power axis range - min must be lower than max"));

		if (fixedYAxisRadioButton.isSelected() &&
				logarithmicCheckBox.isSelected() && minY == 0)
			errors.addError(_("For logarithmic scale for Y axis min cannot be equal to 0."));

	}

}
