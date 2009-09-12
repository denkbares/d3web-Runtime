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

package de.d3web.persistence.xml.writers.actions;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.psMethods.contraIndication.ActionContraIndication;
import de.d3web.persistence.xml.writers.IXMLWriter;
/**
 * Generates the XML representation of a ActionContraIndication Object
 * @author Michael Scharvogel
 */
public class ActionContraIndicationWriter implements IXMLWriter {

	public static final Class ID = ActionContraIndication.class;

	/**
	 * @see AbstractXMLWriter#getXMLString(Object)
	 */
	public String getXMLString(java.lang.Object o) {
		StringBuffer sb = new StringBuffer();
		List theList = null;
		Iterator iter = null;

		if (!(o instanceof ActionContraIndication)) {
			Logger.getLogger(this.getClass().getName()).warning(o.toString() + " is no ActionContraIndication");
		} else {
			ActionContraIndication theAction = (ActionContraIndication) o;

			sb.append("<Action type='ActionContraIndication'>\n");

			theList = theAction.getQASets();
			if (theList != null) {
				if (!(theList.isEmpty())) {
					sb.append("<TargetQASets>\n");

					iter = theList.iterator();
					while (iter.hasNext())
						sb.append("<QASet ID='" + ((QASet) iter.next()).getId() + "'/>\n");

					sb.append("</TargetQASets>\n");
				}
			}
			sb.append("</Action>\n");

		}

		return sb.toString();
	}
}