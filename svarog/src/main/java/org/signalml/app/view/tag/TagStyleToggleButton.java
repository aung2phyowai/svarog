/* TagStyleToggleButton.java created 2007-10-10
 *
 */

package org.signalml.app.view.tag;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JToggleButton;

import org.signalml.plugin.export.signal.TagStyle;

/** TagStyleToggleButton
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStyleToggleButton extends JToggleButton implements TagStyleSelector {

	private static final long serialVersionUID = 1L;

	private static final Dimension PREFERRED_SIZE = new Dimension(28,28);

	private TagStyle tagStyle;
	private Icon cachedIcon;

	private TagIconProducer tagIconProducer;

	public TagStyleToggleButton(TagStyle tagStyle, TagIconProducer tagIconProducer) {
		this.tagStyle = tagStyle;
		this.tagIconProducer = tagIconProducer;
		reset();
	}

	@Override
	public TagStyle getTagStyle() {
		return tagStyle;
	}

	@Override
	public Icon getIcon() {
		if (cachedIcon == null) {
			cachedIcon = tagIconProducer.getIcon(tagStyle);
		}

		return cachedIcon;
	}

	@Override
	public Dimension getPreferredSize() {
		return PREFERRED_SIZE;
	}

	public void reset() {
		cachedIcon = null;
		setToolTipText(tagStyle.getDescription());
		repaint();
	}

}