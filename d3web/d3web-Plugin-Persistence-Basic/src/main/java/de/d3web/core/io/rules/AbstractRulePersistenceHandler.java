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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.inference.PSMethodRulebased;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.io.FragmentManager;
import de.d3web.core.io.KnowledgeReader;
import de.d3web.core.io.KnowledgeWriter;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.IDObjectComparator;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;

/**
 * This abstract class provides basic functions for rule handlers
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public abstract class AbstractRulePersistenceHandler implements KnowledgeWriter, KnowledgeReader {

	/**
	 * 
	 * @author Markus Friedrich (denkbares GmbH)
	 * @created 31.01.2011
	 */
	private final class RuleComparator implements Comparator<Rule> {

		@Override
		public int compare(Rule o1, Rule o2) {
			// get all idobjects of the conditions and try to sort the rules by
			// the ids of them
			Collection<? extends TerminologyObject> terminalObjects = o1.getCondition().getTerminalObjects();
			Collection<? extends TerminologyObject> terminalObjects2 = o2.getCondition().getTerminalObjects();
			int comparator = compareIDObjectLists(terminalObjects, terminalObjects2);
			if (comparator != 0) return comparator;
			// conditions contain the same idobjects, try to compare actions
			List<? extends TerminologyObject> backwardObjects = o1.getAction().getBackwardObjects();
			List<? extends TerminologyObject> backwardObjects2 = o2.getAction().getBackwardObjects();
			comparator = compareIDObjectLists(backwardObjects, backwardObjects2);
			if (comparator != 0) return comparator;
			// actions contain the same idodjects, compare by toString
			return o1.toString().compareTo(o2.toString());
		}

		public int compareIDObjectLists(Collection<? extends TerminologyObject> terminalObjects, Collection<? extends TerminologyObject> terminalObjects2) {
			List<TerminologyObject> allTerminalObjects = new LinkedList<TerminologyObject>();
			allTerminalObjects.addAll(terminalObjects);
			allTerminalObjects.addAll(terminalObjects2);
			Collections.sort(allTerminalObjects, new IDObjectComparator());
			for (TerminologyObject o : allTerminalObjects) {
				if (!terminalObjects.contains(o)) {
					return -1;
				}
				if (!terminalObjects2.contains(o)) {
					return 1;
				}
			}
			return 0;
		}
	}

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
		Collections.sort(rules, new RuleComparator());
		FragmentManager pm = PersistenceManager.getInstance();
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
		try {
			for (RuleSet rs : kb.getAllKnowledgeSlicesFor(PSMethodRulebased.getForwardKind(getProblemSolverContent()))) {
				rules.addAll(rs.getRules());
			}
		}
		catch (NoSuchElementException e) {
			// nothing todo, occurs when there is no rule of the psm in the kb
		}
		return rules;
	}

	protected abstract Class<? extends PSMethodRulebased> getProblemSolverContent();

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
		FragmentManager pm = PersistenceManager.getInstance();
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
