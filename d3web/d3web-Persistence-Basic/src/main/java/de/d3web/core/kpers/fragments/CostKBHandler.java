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
package de.d3web.core.kpers.fragments;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.kpers.fragments.FragmentHandler;
import de.d3web.core.kpers.utilities.CostObject;
import de.d3web.core.kpers.utilities.XMLUtil;
import de.d3web.kernel.domainModel.KnowledgeBase;
/**
 * Handels costs for the knowledge base 
 * @author Norman Brümmer, Markus Friedrich (denkbares GmbH)
 */
public class CostKBHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals("Cost");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof CostObject);
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {

		NamedNodeMap attr = element.getAttributes();
		String id = attr.getNamedItem("ID").getNodeValue();
		Node valueNode = attr.getNamedItem("value");
		if (valueNode == null) {
			CostObject co = null;
			NodeList cc = element.getChildNodes();

			{
				String verbalization = null;
				String unit = null;
				for (int j = 0; j < cc.getLength(); j++) {
					Node child = cc.item(j);
					if (child.getNodeName().equals("Verbalization"))
						verbalization = XMLUtil.getText(child);
					else if (child.getNodeName().equals("Unit")) {
						unit = XMLUtil.getText(child);
					}
				}
				if (verbalization != null)
					co = new CostObject(id, verbalization,
							unit == null ? null : unit);
			}

			// [MISC]:aha:legacy cost reading
			if (co == null) {
				String verb = attr.getNamedItem("verbalization").getNodeValue();
				if (verb != null) {
					Node unitNode = attr.getNamedItem("unit");
					String unit = null;
					if (unitNode != null) {
						unit = unitNode.getNodeValue();
					}
					co = new CostObject(id, verb, unit);
				}
			}

			if (co == null)
				throw new IOException("cost could not be set");
			else
				return co;

		} else {
			Double value = new Double(valueNode.getNodeValue());
			return new CostObject(id, value);
		}
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		CostObject mco = (CostObject) object;
		Element element = doc.createElement("Cost");
		element.setAttribute("ID", mco.getId());
		Element verbalisationElement = doc.createElement("Verbalization");
		verbalisationElement.setTextContent(mco.getVerbalization());
		element.appendChild(verbalisationElement);
		Element unitElement = doc.createElement("Unit");
		unitElement.setTextContent(mco.getUnit());
		element.appendChild(unitElement);
		return element;
	}

}
