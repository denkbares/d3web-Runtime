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
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.kpers.fragments.FragmentHandler;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.supportknowledge.DCElement;
import de.d3web.kernel.supportknowledge.DCMarkup;
/**
 * Handler for DCMarkups
 * 
 * @author hoernlein, Markus Friedrich (denkbares GmbH)
 */
public class DCMarkupHandler implements FragmentHandler {

	public static final String MARKUPTAG = "head";
	public static final String PROFILE = "http://dublincore.org/documents/dcq-html/";
	public static final String TAG = "meta";
	
	@Override
	public boolean canRead(Element element) {
		String profile = element.getAttribute("profile");
		return element.getNodeName().equals("DCMarkup")
			//former nodename in previous versions of the persistence
			||element.getNodeName().equals("KnowledgeBaseDescriptor")
			//new format since 2010
			||(element.getNodeName().equals(MARKUPTAG)&&profile.equals(PROFILE));
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof DCMarkup);
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		DCMarkup ret = new DCMarkup();
		NodeList dcMList = element.getChildNodes();
		for (int i = 0; i < dcMList.getLength(); ++i) {
			Node dcelem = dcMList.item(i);
			if (dcelem.getNodeName().equalsIgnoreCase(TAG)) {
				Element dceElement  = (Element) dcelem;
				String name = dceElement.getAttribute("name");
				String content = dceElement.getAttribute("content");
				DCElement dce = DCElement.getDCElementFor(name);
				ret.setContent(dce, content);
			}
			//persistence version till 2009
			else if (checkOldNodeName(dcelem)) {
					Node valueNode = dcelem.getAttributes().getNamedItem("value");
					String value = "";
					if (valueNode != null){
						value = valueNode.getNodeValue();
					} else {
						// ok this thing may be encoded as text value
						NodeList dcchilds = dcelem.getChildNodes();
						for (int j = 0; j < dcchilds.getLength(); j++) {
							if (dcchilds.item(j).getNodeType() == Node.CDATA_SECTION_NODE
								|| dcchilds.item(j).getNodeType() == Node.TEXT_NODE)
								value = dcchilds.item(j).getNodeValue();
						}
						if (value.trim().isEmpty())
							// ok this thing has no value
							continue;
					}

					// [MISC]:aha:obsolete after supportknowledge refactoring is propagated
					String label = "";
					Node labelNode = dcelem.getAttributes().getNamedItem("label");
					if (labelNode != null) {
						label = labelNode.getNodeValue();
					} else {
						label = dcelem.getAttributes().getNamedItem("name").getNodeValue();
					}
					DCElement dc = DCElement.getDCElementFor(label);

					if (dc == null) {
						// [MISC]:aha:obsolete after supportknowledge refactoring is propagated
						if (label.equals("DC.IDENTIFIER")) dc = DCElement.IDENTIFIER;
						// else if ...
					}
					if (dc == null) {
						throw new IOException("getDCMarkup: no DCElement for label " + label);
					}
					ret.setContent(dc, value);
					
			}
		}
				
		return ret;
	}

	private boolean checkOldNodeName(Node dcelem) {
		return dcelem.getNodeName().equals("DCElement")
			// in former versions of persistence, the DCElments were named Descriptor
			|| dcelem.getNodeName().equalsIgnoreCase("Descriptor");
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		Element element = doc.createElement(MARKUPTAG);
		element.setAttribute("profile", PROFILE);
		DCMarkup dcMarkup = (DCMarkup) object;
		Iterator<DCElement> iter = DCElement.getIterator();
		while (iter.hasNext()) {
			DCElement dcelement = iter.next();
			String value = dcMarkup.getContent(dcelement);
			if (!"".equals(value)) {
				Element dcElementNode = doc.createElement(TAG);
				dcElementNode.setAttribute("name", dcelement.getLabel());
				dcElementNode.setAttribute("content", value);
				element.appendChild(dcElementNode);
			}
		}
		return element;
	}
}
