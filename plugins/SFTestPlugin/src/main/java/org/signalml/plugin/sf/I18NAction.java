package org.signalml.plugin.sf;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.signalml.plugin.export.PluginAuth;
import org.signalml.plugin.export.SvarogAccess;

/**
 * Run I18N methods with invalid PluginAuth.
 */
public class I18NAction extends AbstractAction {
	private final SvarogAccess sa;

	private static final long serialVersionUID = 1L;

	public I18NAction(SvarogAccess sa) {
		super("sf.i18n with invalid pluginauth");
		this.sa = sa;
	}

	@Override
	public void actionPerformed(ActionEvent a) {
		sa.getI18nAccess().translateN(
		       new PluginAuth(){public String getID(){return "x";}},
		       "I18nBundle",
		       "Deleted one file", "Deleted {0} files", 11);
	}
}