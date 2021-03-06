/* LegacyTagConstants.java created 2007-11-18
 *
 */

package org.signalml.domain.tag;

/**
 * This class contains constants describing the outlook (fill, outline) of tags.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class LegacyTagConstants {

	/**
	 * solid background fill style
	 */
	public static final byte FILL_SOLID = (byte) 0;

	public static final byte DRAW_MODE_COPY = (byte) 4;

	//how will the outline of the tag look
	public static final byte OUTLINE_MODE_SOLID = (byte) 0;
	public static final byte OUTLINE_MODE_DASH = (byte) 1;
	public static final byte OUTLINE_MODE_DOT = (byte) 2;
	public static final byte OUTLINE_MODE_DASHDOT = (byte) 3;
	public static final byte OUTLINE_MODE_DASHDOTDOT = (byte) 4;
	public static final byte OUTLINE_MODE_CLEAR = (byte) 5;
	public static final byte OUTLINE_MODE_INSIDEFRAME = (byte) 6;

}
