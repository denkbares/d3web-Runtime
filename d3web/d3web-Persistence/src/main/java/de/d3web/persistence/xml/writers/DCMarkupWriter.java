/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.persistence.xml.writers;
import java.util.logging.Logger;

import de.d3web.kernel.supportknowledge.DCMarkup;
import de.d3web.persistence.xml.loader.DCMarkupUtilities;

/**
 * Generates the XML representation of a Descriptor Object
 * @author Michael Scharvogel
 */

public class DCMarkupWriter implements IXMLWriter {

	public static final String ID = DCMarkupWriter.class.getName();

	private static DCMarkupWriter instance = null;
	public static DCMarkupWriter getInstance() {
		if (instance == null) {
			instance = new DCMarkupWriter();
		}
		return instance;
	}

	/**
	 * @see AbstractXMLWriter#getXMLString(Object) 
	 */
	public String getXMLString(Object o) {
		StringBuffer sb = new StringBuffer();

		if (o == null) {
			Logger.getLogger(this.getClass().getName()).warning("null is no DCMarkup!");
		} else if (
			!(o instanceof DCMarkup)) {
				Logger.getLogger(this.getClass().getName()).warning(o.toString() + " is no DCMarkup!");
		} else {
			sb.append(DCMarkupUtilities.dcmarkupToString((DCMarkup) o));
		}
		
		return sb.toString();
	}
}