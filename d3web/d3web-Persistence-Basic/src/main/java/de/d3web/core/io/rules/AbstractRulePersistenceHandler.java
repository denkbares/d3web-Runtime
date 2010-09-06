/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.core.io.rules;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.io.KnowledgeReader;
import de.d3web.core.io.KnowledgeWriter;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;

/**
 * This abstract class provides basic functions for rule handlers
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public abstract class AbstractRulePersistenceHandler implements KnowledgeWriter, KnowledgeReader {

	protected String ruletype;

	@Override
	public void write(KnowledgeBase kb, OutputStream stream, ProgressListener listener) throws IOException {
		Document doc = Util.createEmptyDocument();
		Element root = doc.createElement("KnowledgeBase");
		root.setAttribute("system", "d3web");
		root.setAttribute("type", ruletype);
		doc.appendChild(root);
		List<Rule> rules = new ArrayList<Rule>(getRules(kb));
		// sort the rules
		Collections.sort(rules, new Comparator<Rule>() {

			@Override
			public int compare(Rule o1, Rule o2) {
				return (o1.getId().compareTo(o2.getId()));
			}
		});
		PersistenceManager pm = PersistenceManager.getInstance();
		float count = 0;
		for (Rule r : rules) {
			Element element = pm.writeFragment(r, doc);
			root.appendChild(element);
			listener.updateProgress(count++ / rules.size(), "Writing " + ruletype);
		}
		Util.writeDocumentToOutputStream(doc, stream);
	}

	private Set<Rule> getRules(KnowledgeBase kb) {
		Set<Rule> rules = new HashSet<Rule>();
		for (KnowledgeSlice slice : kb.getAllKnowledgeSlicesFor(getProblemSolverContent())) {
			if (slice instanceof RuleSet) {
				RuleSet rs = (RuleSet) slice;
				rules.addAll(rs.getRules());
			}
		}

		rules.remove(null);

		return rules;
	}

	protected abstract Class<? extends PSMethod> getProblemSolverContent();

	@Override
	public void read(KnowledgeBase kb, InputStream stream, ProgressListener listener) throws IOException {
		Document doc = Util.streamToDocument(stream);
		NodeList kbnodes = doc.getElementsByTagName("KnowledgeBase");
		if (kbnodes.getLength() != 1) {
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
		List<Rule> rules = new ArrayList<Rule>();
		for (Element child : children) {
			rules.add((Rule) pm.readFragment(child, kb));
			listener.updateProgress(count++ / children.size(), "Reading " + ruletype);
		}
		for (Rule r : rules) {
			r.setProblemsolverContext(getProblemSolverContent());
		}
	}

	@Override
	public int getEstimatedSize(KnowledgeBase kb) {
		return getRules(kb).size();
	}
}
