/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *                    denkbares GmbH
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
package de.d3web.core.io.fragments;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.KnowledgeBase;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.terminology.info.Properties;
import de.d3web.core.terminology.info.Property;
/**
 * Handler for properties
 * @author hoernlein, Markus Friedrich (denkbares GmbH)
 */
public class PropertiesHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals("Properties");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof Properties);
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		Properties ret = new Properties();
		List<Element> proplist = XMLUtil.getElementList(element.getChildNodes());
		for (Element prop: proplist) {
			if (prop.getNodeName().equals("Property")) {
				// [MISC]:aha:obsolete after supportknowledge refactoring is propagated
				String name = "";
				if (prop.getAttributes().getNamedItem("name")!=null) {
					name = prop.getAttributes().getNamedItem("name").getNodeValue();
				} else {
					name = prop.getAttributes().getNamedItem("descriptor").getNodeValue();
				}
				Property property = Property.getProperty(name);
				if (property == null) {
					// [MISC]:aha:obsolete after supportknowledge refactoring is propagated
					if (name.equals("isTherapy")) {
						property = Property.IS_THERAPY;
					} else {
						throw new IOException("Property "+name+" is not a valid property.");
					}
				}
				String textContent = prop.getTextContent();
				if (textContent!=null && !textContent.trim().equals("")) {
					Object o = XMLUtil.getPrimitiveValue(textContent,prop.getAttribute("class"));
					ret.setProperty(property, o);
				} else {
					List<Element> childNodes = XMLUtil.getElementList(prop.getChildNodes());
					if (childNodes.size()==0) {
						ret.setProperty(property, "");
					} else if (childNodes.size()==1) {
						ret.setProperty(property, PersistenceManager.getInstance().readFragment(childNodes.get(0), kb));
					}
					else {
						throw new IOException("Property must have exactly one child.");
					}
				}
			}
		}
		return ret;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		Element element = doc.createElement("Properties");
		Properties properties = (Properties) object;
		Collection<Property> basicPropertys = Property.getBasicPropertys();
		if (basicPropertys.size() > 0) {
			for (Property p: basicPropertys) {
				Object value = properties.getProperty(p);
				if (value!=null) {
					Element propertyElement = doc.createElement("Property");
					propertyElement.setAttribute("name", p.getName());
					propertyElement.setAttribute("class", value.getClass().getName());
					if (value instanceof String||value instanceof Integer || value instanceof Double || value instanceof Boolean || value instanceof URL) {
						propertyElement.setTextContent(value.toString());
					} else {
						propertyElement.appendChild(PersistenceManager.getInstance().writeFragment(value, doc));
					}
					element.appendChild(propertyElement);
				}
			}
		}
		return element;
	}

}
