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
package de.d3web.core.kpers.rules;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.KnowledgeBase;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.kpers.KnowledgeReader;
import de.d3web.core.kpers.KnowledgeWriter;
import de.d3web.core.kpers.PersistenceManager;
import de.d3web.core.kpers.progress.ProgressListener;
import de.d3web.core.kpers.utilities.KnowledgeSliceComparator;
import de.d3web.core.kpers.utilities.Util;
import de.d3web.core.kpers.utilities.XMLUtil;

/**
 * This abstract class provides basic functions for rule handlers
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public abstract class AbstractRulePersistenceHandler implements KnowledgeWriter, KnowledgeReader{

	protected String ruletype;
	
	@Override
	public void write(KnowledgeBase kb, OutputStream stream, ProgressListener listener) throws IOException {
		Document doc = Util.createEmptyDocument();
		Element root = doc.createElement("KnowledgeBase");
		root.setAttribute("system", "d3web");
		root.setAttribute("type", ruletype);
		doc.appendChild(root);
		List<KnowledgeSlice> rules = getRules(kb);
		//sort the rules
		Collections.sort(rules, new KnowledgeSliceComparator());
		PersistenceManager pm = PersistenceManager.getInstance();
		float count = 0;
		for (KnowledgeSlice ks: rules) {
			Element element = pm.writeFragment(ks, doc);
			root.appendChild(element);
			listener.updateProgress(count++/rules.size(), "Writing "+ruletype);
		}
		Util.writeDocumentToOutputStream(doc, stream);
	}

	private List<KnowledgeSlice> getRules(KnowledgeBase kb) {
		List<KnowledgeSlice> rules = new ArrayList<KnowledgeSlice>();
		for (Class<? extends PSMethod> clazz: getProblemSolverContent()) {
			rules.addAll(kb.getAllKnowledgeSlicesFor(clazz));
		}
		rules.remove(null);
		return rules;
	}

	protected abstract List<Class<? extends PSMethod>> getProblemSolverContent();


	@Override
	public void read(KnowledgeBase kb, InputStream stream, ProgressListener listener) throws IOException {
		Document doc = Util.streamToDocument(stream);
		NodeList kbnodes = doc.getElementsByTagName("KnowledgeBase");
		if (kbnodes.getLength()!=1) {
			throw new IOException();
		}
		Node root = kbnodes.item(0);
		if (!(root.hasAttributes()
				&& root.getAttributes().getNamedItem("type") != null && root
				.getAttributes().getNamedItem("type").getNodeValue().equals(
						ruletype))) {
			throw new IOException();
		}
		List<Element> children = XMLUtil.getElementList(root.getChildNodes());
		PersistenceManager pm = PersistenceManager.getInstance();
		float count = 0;
		for (Element child: children) {
			pm.readFragment(child, kb);
			listener.updateProgress(count++/children.size(), "Reading "+ruletype);
		}
	}
	
	@Override
	public int getEstimatedSize(KnowledgeBase kb) {
		return getRules(kb).size();
	}
}
