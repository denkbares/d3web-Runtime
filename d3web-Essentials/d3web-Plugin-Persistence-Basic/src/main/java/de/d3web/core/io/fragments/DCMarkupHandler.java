/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg denkbares GmbH
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.core.io.fragments;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.utils.Log;
import de.d3web.utils.Triple;

/**
 * Handler for DCMarkups DCMarkups are no longer used, this handler is used to
 * parse old files. It won't write anything
 * 
 * @author hoernlein, Markus Friedrich (denkbares GmbH)
 */
@Deprecated
public class DCMarkupHandler {

	public static final String MARKUPTAG = "head";
	public static final String PROFILE = "http://dublincore.org/documents/dcq-html/";
	public static final String TAG = "meta";

	public boolean canRead(Element element) {
		String profile = element.getAttribute("profile");
		return element.getNodeName().equals("DCMarkup")
				// former nodename in previous versions of the persistence
				|| element.getNodeName().equals("KnowledgeBaseDescriptor")
				// new format since 2010
				|| (element.getNodeName().equals(MARKUPTAG) && profile.equals(PROFILE));
	}

	public Triple<String, Property<?>, Locale> read(KnowledgeBase kb, Element element) throws IOException {
		Map<String, String> parsedValues = new HashMap<String, String>();
		NodeList dcMList = element.getChildNodes();
		for (int i = 0; i < dcMList.getLength(); ++i) {
			Node dcelem = dcMList.item(i);
			if (dcelem.getNodeName().equalsIgnoreCase(TAG)) {
				Element dceElement = (Element) dcelem;
				String name = dceElement.getAttribute("name");
				String content = dceElement.getAttribute("content");
				parsedValues.put(name.toLowerCase(), content);
			}
			// persistence version till 2009
			else if (checkOldNodeName(dcelem)) {
				Node valueNode = dcelem.getAttributes().getNamedItem("value");
				String value = "";
				if (valueNode != null) {
					value = valueNode.getNodeValue();
				}
				else {
					// ok this thing may be encoded as text value
					NodeList dcchilds = dcelem.getChildNodes();
					for (int j = 0; j < dcchilds.getLength(); j++) {
						if (dcchilds.item(j).getNodeType() == Node.CDATA_SECTION_NODE
								|| dcchilds.item(j).getNodeType() == Node.TEXT_NODE) value = dcchilds.item(
								j).getNodeValue();
					}
					if (value.trim().isEmpty())
					// ok this thing has no value
					continue;
				}

				// [MISC]:aha:obsolete after supportknowledge refactoring is
				// propagated
				String label = "";
				Node labelNode = dcelem.getAttributes().getNamedItem("label");
				if (labelNode != null) {
					label = labelNode.getNodeValue();
				}
				else {
					label = dcelem.getAttributes().getNamedItem("name").getNodeValue();
				}
				parsedValues.put(label.toLowerCase(), value);

			}
		}
		try {
			Property<Object> property = Property.getUntypedProperty(parsedValues.get("dc.subject"));
			String language = parsedValues.get("dc.language");
			Locale locale = null;
			if (language != null) locale = new Locale(language);
			return new Triple<String, Property<?>, Locale>(parsedValues.get("dc.source"),
					property, locale);
		}
		catch (NoSuchElementException e) {
			Log.warning("Property " + parsedValues.get("dc.subject") + " is not in use any more.");
			return null;
		}
	}

	/**
	 * Used to check the old markup (prior to 2009) of the persistence
	 * 
	 * @param dcelem node instance of the DCMarkup element
	 * @return true, if valid node, though it is the old version of the markup
	 */
	private boolean checkOldNodeName(Node dcelem) {
		return dcelem.getNodeName().equals("DCElement")
				// in former versions of persistence, the DCElments were named
				// Descriptor
				|| dcelem.getNodeName().equalsIgnoreCase("Descriptor");
	}
}
