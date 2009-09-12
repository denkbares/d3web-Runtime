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

import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.persistence.xml.MockQASet;
import de.d3web.xml.utilities.XMLTools;

/**
 * Generates the XML representation of a QContainer Object
 * @author Michael Scharvogel, merz
 */
public class QContainerWriter implements IXMLWriter {
	
	public static final String ID = QContainerWriter.class.getName();

	/**
	 * @see AbstractXMLWriter#getXMLString(Object) 
	 */
	public String getXMLString(Object o) {
		String retValue = null;

		if (!(o instanceof MockQASet)) {
			Logger.getLogger(this.getClass().getName()).warning(o.toString() + " is no MockQASet !!!");
		} else {
			MockQASet mqaSet = (MockQASet) o;

			QContainer theContainer = (QContainer) mqaSet.getQASet();

			StringBuffer sb = new StringBuffer();

			String priostr = "";
			if (theContainer.getPriority() != null) {
				priostr = "' priority='" + theContainer.getPriority();
			}
			sb.append(
				"<QContainer ID='"
					+ theContainer.getId()
					+ priostr
					+ "'>\n");

			sb.append(
				"<Text><![CDATA["
					+ XMLTools.prepareForCDATA(theContainer.getText())
					+ "]]></Text>\n");

			// jetzt die Kosten

			// this way of cost writing is no more
			/*
			Set costObjects = mqaSet.getCostObjects();
			if (costObjects != null) {
				sb.append("<Costs>\n");
				Iterator iter = costObjects.iterator();
				MockCostObject mco = null;
				while (iter.hasNext()) {
					mco = (MockCostObject) iter.next();
					Double costD = (Double) theContainer.getProperties().getProperty(Property.getProperty(mco.getID())); 
					if (costD != null) {
						sb.append(
							"<Cost ID='"
								+ XMLTools.prepareForXML(mco.getID())
								+ "' value='"
								+ costD.doubleValue()
								+ "'></Cost>\n");
					}
				} // end while
				sb.append("</Costs>\n");
			} // end if
			*/

			// jetzt die Kinder	
			Iterator childIter = theContainer.getChildren().iterator();
			boolean hasChildren = childIter.hasNext();
			if (hasChildren) {
				sb.append("<Children>\n");

				while (childIter.hasNext()) {
					QASet theChild = (QASet) childIter.next();
					sb.append(
						"<Child ID='"
							+ theChild.getId() +"'");
					
					if (isLinkedChild(theContainer, theChild))
						sb.append(" link='true'");
					
					sb.append("/>\n");
				}

				sb.append("</Children>\n");
			}

			//Properties
			sb.append(new PropertiesWriter().getXMLString(theContainer.getProperties()));

			sb.append("</QContainer>\n");

			retValue = sb.toString();
		}

		return retValue;
	}

	private boolean isLinkedChild(NamedObject topQ, NamedObject theChild) {
		return topQ.getLinkedChildren().contains(theChild);
	}

}