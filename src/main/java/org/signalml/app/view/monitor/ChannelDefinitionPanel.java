package org.signalml.app.view.monitor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Allows to insert channel definitions.
 *
 * @author Tomasz Sawicki
 */
public class ChannelDefinitionPanel extends JPanel implements ActionListener {

        /**
         * List of definitions.
         */
        private JList definitionsList = null;

        /**
         * Channel no. label.
         */
        private JLabel channelLabel = null;

        /**
         * Channel no. textfield.
         */
        private JTextField channelTextField = null;

        /**
         * Gain label.
         */
        private JLabel gainLabel = null;

        /**
         * Gain textfield.
         */
        private JTextField gainTextField = null;

        /**
         * Offset label.
         */
        private JLabel offsetLabel = null;

        /**
         * Offset textfield.
         */
        private JTextField offsetTextField = null;

        /**
         * Insert button.
         */
        private JButton addButton = null;

        /**
         * Remove button.
         */
        private JButton removeButton = null;

        /**
         * Message source.
         */
        private MessageSourceAccessor messageSource;

        /**
         * Default constructor.
         */
        public ChannelDefinitionPanel(MessageSourceAccessor messageSource) {

                super();
                this.messageSource = messageSource;

                CompoundBorder border = new CompoundBorder(
			new TitledBorder(messageSource.getMessage("amplifierDefinitionConfig.channelDefinitions")),
			new EmptyBorder(3, 3, 3, 3));
		setBorder(border);

                setLayout(new BorderLayout(10, 10));

                add(new JScrollPane(getDefinitionsList()), BorderLayout.CENTER);

                JPanel bottomPanel = new JPanel(new GridBagLayout());
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.CENTER;
                constraints.fill = GridBagConstraints.BOTH;

                constraints.gridx = 0;
                constraints.gridy = 0;
                constraints.gridwidth = 1;
                constraints.weightx = 0;
                bottomPanel.add(getChannelLabel(), constraints);

                constraints.gridx = 1;
                constraints.gridy = 0;
                constraints.gridwidth = 2;
                constraints.weightx = 1;
                bottomPanel.add(getChannelTextField(), constraints);

                constraints.gridx = 0;
                constraints.gridy = 1;
                constraints.gridwidth = 1;
                constraints.weightx = 0;
                bottomPanel.add(getGainLabel(), constraints);

                constraints.gridx = 1;
                constraints.gridy = 1;
                constraints.gridwidth = 2;
                constraints.weightx = 1;
                bottomPanel.add(getGainTextField(), constraints);
                
                constraints.gridx = 0;
                constraints.gridy = 2;
                constraints.gridwidth = 1;
                constraints.weightx = 0;
                bottomPanel.add(getOffsetLabel(), constraints);

                constraints.gridx = 1;
                constraints.gridy = 2;
                constraints.gridwidth = 2;
                constraints.weightx = 1;
                bottomPanel.add(getOffsetTextField(), constraints);

                JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonsPanel.add(getAddButton());
                buttonsPanel.add(getRemoveButton());

                constraints.gridx = 2;
                constraints.gridy = 3;
                constraints.gridwidth = 1;
                constraints.weightx = 0;
                bottomPanel.add(buttonsPanel, constraints);

                add(bottomPanel, BorderLayout.PAGE_END);
        }

        /**
         * Called when buttons are clicked.
         *
         * @param e action event
         */
        @Override
        public void actionPerformed(ActionEvent e) {

                if (addButton.equals(e.getSource())) {

                        Integer channelno;
                        try {
                                channelno = Integer.parseInt(channelTextField.getText());
                        } catch (NumberFormatException ex) {
                                String errorMsg = messageSource.getMessage("amplifierDefinitionConfig.channelno") +
                                                  messageSource.getMessage("error.amplifierDefinitionConfig.integer");
                                JOptionPane.showMessageDialog(this, errorMsg);
                                return;
                        }

                        Double gain;
                        try {
                                gain = Double.parseDouble(gainTextField.getText());
                        } catch (NumberFormatException ex) {
                                String errorMsg = messageSource.getMessage("amplifierDefinitionConfig.gain") +
                                                  messageSource.getMessage("error.amplifierDefinitionConfig.rational");
                                JOptionPane.showMessageDialog(this, errorMsg);
                                return;
                        }

                        Double offset;
                        try {
                                offset = Double.parseDouble(offsetTextField.getText());
                        } catch (NumberFormatException ex) {
                                String errorMsg = messageSource.getMessage("amplifierDefinitionConfig.offset") +
                                                  messageSource.getMessage("error.amplifierDefinitionConfig.rational");
                                JOptionPane.showMessageDialog(this, errorMsg);
                                return;
                        }

                        List<ChannelDefinition> definitions = getChannelDefinitions();
                        definitions.add(new ChannelDefinition(channelno, gain, offset));
                        definitionsList.setListData(definitions.toArray());

                        clearTextFields();
                }
                else if (removeButton.equals(e.getSource())) {

                        int selectedIndex = definitionsList.getSelectedIndex();

                        if (selectedIndex == -1 )
                                return;

                        List<ChannelDefinition> definitions = getChannelDefinitions();
                        definitions.remove(selectedIndex);
                        definitionsList.setListData(definitions.toArray());

                        definitionsList.setSelectedIndex(-1);
                }
        }

        /**
         * Gets the frequencies list
         *
         * @return the frequencies list
         */
        private JList getDefinitionsList() {

                if (definitionsList == null) {
                        definitionsList = new JList();
                        definitionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                }
                return definitionsList;
        }

        /**
         * Gets the add button.
         *
         * @return the add button
         */
        private JButton getAddButton() {

                if (addButton == null) {
                        addButton = new JButton();
                        addButton.setText(messageSource.getMessage("amplifierDefinitionConfig.addButton"));
                        addButton.addActionListener(this);
                }
                return addButton;
        }

        /**
         * Gets the remove button.
         *
         * @return the remove button
         */
        private JButton getRemoveButton() {

                if (removeButton == null) {
                        removeButton = new JButton();
                        removeButton.setText(messageSource.getMessage("amplifierDefinitionConfig.removeButton"));
                        removeButton.addActionListener(this);
                }
                return removeButton;
        }

        /**
         * Gets the channel label
         *
         * @return the channel label
         */
        private JLabel getChannelLabel() {
                
                if (channelLabel == null) {
                        channelLabel = new JLabel();
                        channelLabel.setText(messageSource.getMessage("amplifierDefinitionConfig.channelno"));
                }
                return channelLabel;
        }

        /**
         * Gets the channel text field
         *
         * @return the channel text field
         */
        private JTextField getChannelTextField() {

                if (channelTextField == null) {
                        channelTextField = new JTextField();
                }
                return channelTextField;
        }

        /**
         * Gets the gain label
         * 
         * @return the gain label
         */
        private JLabel getGainLabel() {
                
                if (gainLabel == null) {
                        gainLabel = new JLabel();
                        gainLabel.setText(messageSource.getMessage("amplifierDefinitionConfig.gain"));
                }
                return gainLabel;
        }

        /**
         * Gets the gain text field
         *
         * @return the gain text field
         */
        private JTextField getGainTextField() {

                if (gainTextField == null) {
                        gainTextField = new JTextField();
                }
                return gainTextField;
        }

        /**
         * Gets the offset label
         * 
         * @return the offset label
         */
        private JLabel getOffsetLabel() {
                
                if (offsetLabel == null) {
                        offsetLabel = new JLabel();
                        offsetLabel.setText(messageSource.getMessage("amplifierDefinitionConfig.offset"));
                }
                return offsetLabel;
        }

        /**
         * Gets the offset text field
         *
         * @return the offset text field
         */
        private JTextField getOffsetTextField() {

                if (offsetTextField == null) {
                        offsetTextField = new JTextField();
                }
                return offsetTextField;
        }

        /**
         * Gets the list of frequencies.
         *
         * @return list of frequencies
         */
        private List<ChannelDefinition> getChannelDefinitions() {

                ArrayList<ChannelDefinition> definitions = new ArrayList<ChannelDefinition>();

                for (int i = 0; i < definitionsList.getModel().getSize(); i++) {

                        ChannelDefinition definition = (ChannelDefinition) definitionsList.getModel().getElementAt(i);
                        definitions.add(definition);
                }

                return definitions;
        }

        /**
         * Gets the channel number
         *
         * @return list of channel numbers
         */
        public List<Integer> getChannelNumbers() {

                ArrayList<Integer> numbers = new ArrayList<Integer>();

                for (ChannelDefinition definition : getChannelDefinitions()) {

                        numbers.add(definition.getNumber());
                }

                return numbers;
        }

        /**
         * Gets the gain values
         *
         * @return list of gain values
         */
        public List<Double> getGainValues() {

                ArrayList<Double> gain = new ArrayList<Double>();

                for (ChannelDefinition definition : getChannelDefinitions()) {

                        gain.add(definition.getGain());
                }

                return gain;
        }

        /**
         * Gets the offset values
         *
         * @return list of channel numbers
         */
        public List<Double> getOffsetValues() {

                ArrayList<Double> offset = new ArrayList<Double>();

                for (ChannelDefinition definition : getChannelDefinitions()) {

                        offset.add(definition.getOffset());
                }

                return offset;
        }

        /**
         * Sets the data
         *
         * @param numbers list od channel numbers
         * @param gainValues list of gain values
         * @param offsetValues list of offset values
         */
        public void setData(List<Integer> numbers, List<Double> gainValues, List<Double> offsetValues) {

                ArrayList<ChannelDefinition> definitions = new ArrayList<ChannelDefinition>();

                for (int i = 0; i < numbers.size(); i++) {

                        ChannelDefinition definition = new ChannelDefinition(numbers.get(i), gainValues.get(i), offsetValues.get(i));
                        definitions.add(definition);
                }

                definitionsList.setListData(definitions.toArray());
        }

        /**
         * Clears text fields.
         */
        public void clearTextFields() {

                channelTextField.setText("");
                gainTextField.setText("");
                offsetTextField.setText("");
        }
}