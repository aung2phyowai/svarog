/* MontageFiltersTableModel.java created 2008-02-03
 *
 */

package org.signalml.app.montage;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageSampleFilterEvent;
import org.signalml.domain.montage.MontageSampleFilterListener;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;

/** MontageFiltersTableModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageFiltersTableModel extends AbstractTableModel implements MontageSampleFilterListener {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(MontageFiltersTableModel.class);

	public static final int INDEX_COLUMN = 0;
	public static final int DESCRIPTION_COLUMN = 1;
	public static final int EFFECT_COLUMN = 2;

	private Montage montage;
	private MessageSourceAccessor messageSource;

	public MontageFiltersTableModel(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
	}

	public MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

	public Montage getMontage() {
		return montage;
	}

	public void setMontage(Montage montage) {
		if (this.montage != montage) {
			if (this.montage != null) {
				this.montage.removeMontageSampleFilterListener(this);
			}
			this.montage = montage;
			if (montage != null) {
				montage.addMontageSampleFilterListener(this);
			}
			fireTableDataChanged();
		}
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public int getRowCount() {
		if (montage == null) {
			return 0;
		}
		return montage.getSampleFilterCount();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public String getColumnName(int column) {

		switch (column) {

		case INDEX_COLUMN :
			return messageSource.getMessage("montageFiltersTable.index");

		case DESCRIPTION_COLUMN :
			return messageSource.getMessage("montageFiltersTable.description");

		case EFFECT_COLUMN :
			return messageSource.getMessage("montageFiltersTable.effect");

		default :
			throw new IndexOutOfBoundsException();

		}

	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {

		switch (columnIndex) {

		case INDEX_COLUMN :
				return Integer.class;

		case DESCRIPTION_COLUMN :
			return String.class;

		case EFFECT_COLUMN :
			return String.class;

		default :
			throw new IndexOutOfBoundsException();

		}

	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		switch (columnIndex) {

		case INDEX_COLUMN :
			return (rowIndex+1);

		case DESCRIPTION_COLUMN :
			return montage.getSampleFilterAt(rowIndex).getDescription();

		case EFFECT_COLUMN :
			MessageSourceResolvable effectDescription = montage.getSampleFilterAt(rowIndex).getEffectDescription();
			if (effectDescription == null) {
				return montage.getSampleFilterAt(rowIndex).getDefaultEffectDescription();
			} else {
				return messageSource.getMessage(effectDescription);
			}


		default :
			throw new IndexOutOfBoundsException();

		}

	}

	@Override
	public void filterAdded(MontageSampleFilterEvent ev) {
		int[] indices = ev.getIndices();
		if (indices.length == 0) {
			return;
		}
		for (int i=0; i<(indices.length-1); i++) {
			if (indices[i] != (indices[i+1]-1)) {
				// if non-contiguous fire a total update
				fireTableDataChanged();
				return;
			}
		}
		// if contiguous fire an insert
		fireTableRowsInserted(indices[0], indices[indices.length-1]);
	}

	@Override
	public void filterChanged(MontageSampleFilterEvent ev) {
		int[] indices = ev.getIndices();
		if (indices.length == 0) {
			return;
		}
		for (int i=0; i<(indices.length-1); i++) {
			if (indices[i] != (indices[i+1]-1)) {
				// if non-contiguous fire a total update
				fireTableDataChanged();
				return;
			}
		}
		// if contiguous fire an insert
		fireTableRowsUpdated(indices[0], indices[indices.length-1]);
	}

	@Override
	public void filterExclusionChanged(MontageSampleFilterEvent ev) {
		// ignored

	}

	@Override
	public void filterRemoved(MontageSampleFilterEvent ev) {
		int[] indices = ev.getIndices();
		if (indices.length == 0) {
			return;
		}
		for (int i=0; i<(indices.length-1); i++) {
			if (indices[i] != (indices[i+1]-1)) {
				// if non-contiguous fire a total update
				fireTableDataChanged();
				return;
			}
		}
		// if contiguous fire a delete
		fireTableRowsDeleted(indices[0], indices[indices.length-1]);

	}

	@Override
	public void filtersChanged(MontageSampleFilterEvent ev) {
		fireTableDataChanged();
	}

}