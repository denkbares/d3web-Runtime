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
import java.util.Iterator;
import java.util.logging.Logger;

import de.d3web.kernel.domainModel.PriorityGroup;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.xml.utilities.XMLTools;

/**
 * Generates the XML representation of a PriorityGroup
 * @author Michael Scharvogel
 */
public class PriorityGroupWriter implements IXMLWriter {
	
	public static final String ID = PriorityGroupWriter.class.getName();

	/**
	 * @see AbstractXMLWriter#getXMLString(Object) 
	 */
	public String getXMLString(Object o) {
		StringBuffer sb = new StringBuffer();

		if (o == null) {
			Logger.getLogger(this.getClass().getName()).warning("null is no Priority Group");
		} else if (!(o instanceof PriorityGroup)) {
			Logger.getLogger(this.getClass().getName()).warning(o.toString() + " is no Priority Group");
		} else {
			PriorityGroup thePG = (PriorityGroup) o;

			// handling ParentChild Relationships...
			Iterator childIter = thePG.getChildren().iterator();
			boolean hasChildren = childIter.hasNext();

			sb.append("<PriorityGroup ID='" + thePG.getId() + "'>\n");
			sb.append("<Text><![CDATA[" + XMLTools.prepareForCDATA(thePG.getText()) + "]]></Text>\n");
			if (thePG.getMinLevel() != null) {
				sb.append("<MinLevel value='" + thePG.getMinLevel() + "'></MinLevel>\n");
			}

			if (thePG.getMaxLevel() != null) {
				sb.append("<MaxLevel value='" + thePG.getMaxLevel() + "'></MaxLevel>\n");
			}

			if (hasChildren) {
				sb.append("<Children>\n");

				while (childIter.hasNext())
					sb.append("<Child ID='" + ((QASet) childIter.next()).getId() + "'/>\n");

				sb.append("</Children>\n");
			}

			//Properties
			sb.append(new PropertiesWriter().getXMLString(thePG.getProperties()));

			sb.append("</PriorityGroup>\n");

		}

		return sb.toString();
	}
}