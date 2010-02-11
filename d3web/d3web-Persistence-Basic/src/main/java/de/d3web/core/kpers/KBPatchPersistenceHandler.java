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

package de.d3web.core.kpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.KnowledgeBase;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.Rule;
import de.d3web.core.kpers.KnowledgeReader;
import de.d3web.core.kpers.PersistenceManager;
import de.d3web.core.kpers.progress.ProgressListener;
import de.d3web.core.kpers.utilities.Util;
import de.d3web.core.kpers.utilities.XMLUtil;

/**
 * KnowledgeReader for reading kb-patches (e.g. redefined RuleComplex).
 * @author gbuscher
 */
public class KBPatchPersistenceHandler implements KnowledgeReader {
	
	public static final String PATCH_PERSISTENCE_HANDLER = "kb-patch";


	public String getId() {
		return PATCH_PERSISTENCE_HANDLER;
	}

	@Override
	public void read(KnowledgeBase kb, InputStream stream,
			ProgressListener listener) throws IOException {
		Document doc = Util.streamToDocument(stream);
		Node node = null;
		for (int i = 0; i < doc.getChildNodes().getLength(); i++) {
			node = doc.getChildNodes().item(i);
			if (node.getNodeName().equalsIgnoreCase("KnowledgeBasePatch")) break;
			
		}
		if (node == null) {
			throw new IOException("Node KnowledgeBasePatch not found.");
		}
		
		// first, remove the old KnowledgeSlices
		removeKnowledgeSlices(node, kb, listener);
		
		// then add the new ones
		getKnowledgeSlices(node, kb, listener);
	}
	
	private void getKnowledgeSlices(Node node, KnowledgeBase kb,
			ProgressListener listener) throws IOException {
		NodeList kbchildren = node.getChildNodes();
		List<Element> slices = null;
		for (int i = 0; (i < kbchildren.getLength()) && (slices == null); i++) {
			String name = kbchildren.item(i).getNodeName();
			if (name.equalsIgnoreCase("knowledgeslices")) {
				slices = XMLUtil.getElementList(kbchildren.item(i).getChildNodes());
			}
		}
		float time = 0;
		for (Element child: slices) {
			PersistenceManager.getInstance().readFragment(child, kb);
			listener.updateProgress(0.5f+((++time/slices.size()/2)), "Patching knowledge base: adding new knowledge slices");
		}
		
	}

	/**
	 * Removes all KnowledgeSlices which are specified within node.getChildNodes().
	 * @param node parent-node of the KnowledgeSlices to remove
	 * @param kb KnowledgeBase
	 */
	private void removeKnowledgeSlices(Node node, KnowledgeBase kb, ProgressListener listener) {
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
				Node slice = slices.item(i);
				if (slice.getNodeName().equalsIgnoreCase("knowledgeslice")) {
					NamedNodeMap attr = slice.getAttributes();
					id = attr.getNamedItem("ID").getNodeValue();
					removeKnowledgeSlice(id, kb);
				}
				listener.updateProgress(((float) i+1/slices.getLength())/2, "Patching knowledge base: removing old slices");
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
		Iterator<KnowledgeSlice> iter = kb.getAllKnowledgeSlices().iterator();
		while (iter.hasNext()) {
			KnowledgeSlice slice = iter.next();
			if ((slice instanceof Rule) && (slice.getId().equalsIgnoreCase(id))) {
				return kb.remove(slice);
			}
		}
		return false;
	}
}
