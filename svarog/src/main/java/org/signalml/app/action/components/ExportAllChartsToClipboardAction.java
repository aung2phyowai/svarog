/* ExportAllChartsToClipboardAction.java created 2008-01-15
 *
 */

package org.signalml.app.action.components;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;
import org.jfree.chart.JFreeChart;
import org.signalml.app.util.ImageTransferable;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/** ExportAllChartsToClipboardAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class ExportAllChartsToClipboardAction extends AbstractSignalMLAction implements ClipboardOwner {

	protected static final Logger logger = Logger.getLogger(ExportAllChartsToClipboardAction.class);

	private static final long serialVersionUID = 1L;

	public ExportAllChartsToClipboardAction() {
		super();
		setText(_("Copy all charts to clipboard"));
		setIconPath("org/signalml/app/icon/clipboard.png");
		setToolTip(_("Copy all charts to clipboard"));
	}

	protected abstract int getChartCount();

	protected abstract Rectangle getChartBounds(int index);

	protected abstract JFreeChart getChart(int index);

	@Override
	public void actionPerformed(ActionEvent ev) {

		int chartCount = getChartCount();
		if (chartCount == 0) {
			return;
		}

		Rectangle rect = new Rectangle(0,0,0,0);
		int i;

		for (i=0; i<chartCount; i++) {
			rect.add(getChartBounds(i));
		}

		BufferedImage image = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) image.getGraphics();

		for (i=0; i<chartCount; i++) {

			getChart(i).draw(g, getChartBounds(i));

		}

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new ImageTransferable(image), this);

	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(true);
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// don't care
	}

}