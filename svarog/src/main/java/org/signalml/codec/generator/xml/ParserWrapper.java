/* Signal Copyright (C) 2003 Dobieslaw Ircha    <dircha@eranet.pl>
                              Artur Biesiadowski <abies@adres.pl>
                              Piotr J. Durka     <Piotr-J.Durka@fuw.edu.pl>

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Linking Signal statically or dynamically with other modules is making a
    combined work based on Signal.  Thus, the terms and conditions of the GNU
    General Public License cover the whole combination.

    As a special exception, the copyright holders of Signal give you
    permission to link Signal with independent modules that communicate with
    Signal solely through the SignalBAR interface, regardless of the license
    terms of these independent modules, and to copy and distribute the
    resulting combined work under terms of your choice, provided that
    every copy of the combined work is accompanied by a complete copy of
    the source code of Signal (the version of Signal used to produce the
    combined work), being distributed under the terms of the GNU General
    Public License plus this exception.  An independent module is a module
    which is not derived from or based on Signal.

    Note that people who make modified versions of Signal are not obligated
    to grant this special exception for their modified versions; it is
    their choice whether to do so.  The GNU General Public License gives
    permission to release a modified version without this exception; this
    exception also makes it possible to release a modified version which
    carries forward this exception.
*/
package org.signalml.codec.generator.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Text;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public interface ParserWrapper {
	public Document parse(String uri) throws Exception;

	public void setFeature(String featureId, boolean state)
	throws  SAXNotRecognizedException, SAXNotSupportedException;

	public DocumentInfo getDocumentInfo();

	public interface DocumentInfo {
		public boolean isIgnorableWhitespace(Text text);

	}
}
