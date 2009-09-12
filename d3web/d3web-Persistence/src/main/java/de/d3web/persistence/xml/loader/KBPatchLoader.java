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

package de.d3web.persistence.xml.loader;

import java.util.Iterator;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.xml.utilities.InputFilter;

/**
 * Class to load kb-patches and to update the knowledebase.
 * @author gbuscher
 */
public class KBPatchLoader extends KBLoader {
	
	public KBPatchLoader() {
		super();
	}
	
	/**
	 * Reloads all KnowledgeSlices (at the moment only RuleComplex!), that are specified in the given
	 * xml-File.
	 * @param kb KnowledgeBase (to update)
	 * @return KnowledgeBase (updated)
	 */
	public KnowledgeBase update(KnowledgeBase kb) {
		knowledgeBase = kb;
		
		Document doc = null;
		Node node = null;
		try {
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = dBuilder.parse(InputFilter.getFilteredInputSource(fileURL));
			for (int i = 0; i < doc.getChildNodes().getLength(); i++) {
				node = doc.getChildNodes().item(i);
				try {
					if (node.getNodeName().equalsIgnoreCase("KnowledgeBasePatch")) break;
				} catch (Exception ex) {}
			}
			if (node == null) throw new Exception();
		} catch (Exception x) {
			Logger.getLogger(this.getClass().getName()).warning(
				"Error while reading the XML-File\nin update()");
			return kb;
		}
		
		// first, remove the old KnowledgeSlices
		removeKnowledgeSlices(node, kb);
		
		// then add the new ones
		getKnowledgeSlices(node);
		
		return kb;
	}
	
	/**
	 * Removes all KnowledgeSlices which are specified within node.getChildNodes().
	 * @param node parent-node of the KnowledgeSlices to remove
	 * @param kb KnowledgeBase
	 */
	private void removeKnowledgeSlices(Node node, KnowledgeBase kb) {
		NodeList kbchildren = node.getChildNodes();
		NodeList slices = null;
		for (int i = 0; (i < kbchildren.getLength()) && (slices == null); i++) {
			String name = kbchildren.item(i).getNodeName();
			if (name.equalsIgnoreCase("knowledgeslices")) {
				slices = kbchildren.item(i).getChildNodes();
			}
		}
		if (slices != null) {
			for (int i = 0; i < slices.getLength(); i++) {
				String id = null;
				try {
					Node slice = slices.item(i);
					if (slice.getNodeName().equalsIgnoreCase("knowledgeslice")) {
						NamedNodeMap attr = slice.getAttributes();
						id = attr.getNamedItem("ID").getNodeValue();
						removeKnowledgeSlice(id, kb);
					}
				} catch (Exception e) {
					Logger.getLogger(this.getClass().getName()).warning(
						"Error while reading KnowledgeSlice " + id + "\n"
						+ "in removeKnowledgeSlices(..)");
				}
			}
		}
	}
	
	/**
	 * Removes the KnowledgeSlice with the specified id from the knowledgebase, if it is a RuleComplex!
	 * (for other KnowledgeSlices, KnowledgeBase do not provide remove-methods).
	 * @param id (String) of the RuleComplex to remove
	 * @param kb (KnowledgeBase)
	 * @return boolean; true, if the RuleComplex could be removed
	 */
	private boolean removeKnowledgeSlice(String id, KnowledgeBase kb) {
		Iterator iter = kb.getAllKnowledgeSlices().iterator();
		while (iter.hasNext()) {
			KnowledgeSlice slice = (KnowledgeSlice) iter.next();
			if ((slice instanceof RuleComplex) && (slice.getId().equalsIgnoreCase(id))) {
				return kb.remove(slice);
			}
		}
		return false;
	}

}
