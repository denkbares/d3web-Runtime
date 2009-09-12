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

/*
 * Created on 27.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.d3web.persistence.xml.loader;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.kernel.supportknowledge.DCElement;
import de.d3web.kernel.supportknowledge.DCMarkup;

/**
 * @author hoernlein
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DCMarkupUtilities {
	
	private final static Collection simpleValues = Arrays.asList(new DCElement[] {
		DCElement.DATE,
		DCElement.IDENTIFIER
	});
	
	public static String dcmarkupToString(DCMarkup markup) {
		StringBuffer sb = new StringBuffer();

		Iterator iter = DCElement.getIterator();
		while (iter.hasNext())
			sb.append(dcelementToString(markup, (DCElement) iter.next()));
		
		if (sb.length() > 0) {
			sb.insert(0, "<DCMarkup>\n");
			sb.append("</DCMarkup>\n");
		}
		
		return sb.toString();
	}

	public static String dcelementToString(DCMarkup markup, DCElement element) {
		StringBuffer sb = new StringBuffer();
		
		String value = markup.getContent(element);
		if (!"".equals(value)) {
			if (simpleValues.contains(element))
				sb.append(
					"<DCElement" +					" label=\"" + element.getLabel() + "\"" +					" value=\"" + value + "\"" +					"/>\n"
				);
			else
				sb.append(
					"<DCElement label=\"" + element.getLabel() + "\">" +					"<![CDATA[" + value + "]]>" +					"</DCElement>\n"
				);
		}
		
		return sb.toString();
	}

	
	public static DCMarkup getDCMarkup(Node dcMarkupRoot) {
		
		DCMarkup ret = new DCMarkup();

		NodeList dcMList = dcMarkupRoot.getChildNodes();
		for (int i = 0; i < dcMList.getLength(); ++i) {
			Node dcelem = dcMList.item(i);
			if (dcelem.getNodeName().equals("DCElement")
				// [MISC]:aha:obsolete after supportknowledge refactoring is propagated
				|| dcelem.getNodeName().equalsIgnoreCase("Descriptor")
				) {

					String value = "";
					try {
						value = dcelem.getAttributes().getNamedItem("value").getNodeValue();
					} catch (Exception ex) {
						// ok this thing may be encoded as text value
						NodeList dcchilds = dcelem.getChildNodes();
						for (int j = 0; j < dcchilds.getLength(); j++) {
							if (dcchilds.item(j).getNodeType() == Node.CDATA_SECTION_NODE
								|| dcchilds.item(j).getNodeType() == Node.TEXT_NODE)
								value = dcchilds.item(j).getNodeValue();
						}
						if ("".equals(value.trim()))
							// ok this thing has no value
							continue;
					}

					// [MISC]:aha:obsolete after supportknowledge refactoring is propagated
					String label = "";
					try {
						label = dcelem.getAttributes().getNamedItem("label").getNodeValue();
					} catch (Exception ex) {
						label = dcelem.getAttributes().getNamedItem("name").getNodeValue();
					}
					
					DCElement dc = DCElement.getDCElementFor(label);

					if (dc == null)
						// [MISC]:aha:obsolete after supportknowledge refactoring is propagated
						if (label.equals("DC.IDENTIFIER")) dc = DCElement.IDENTIFIER;
						// else if ...

					if (dc == null)
						Logger.getLogger(DCMarkupUtilities.class.getName()).warning("getDCMarkup: no DCElement for label " + label);
					else
						ret.setContent(dc, value);
					
			}
		}
				
		return ret;
	}
	
}
