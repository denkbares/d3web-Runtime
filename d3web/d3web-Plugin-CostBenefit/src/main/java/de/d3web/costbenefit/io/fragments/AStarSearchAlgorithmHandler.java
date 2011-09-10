/*
 * Copyright (C) 2010 denkbares GmbH
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
package de.d3web.costbenefit.io.fragments;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.costbenefit.inference.AbortStrategy;
import de.d3web.costbenefit.inference.astar.AStarAlgorithm;
import de.d3web.costbenefit.inference.astar.Heuristic;

/**
 * FragmentHandler for AStarSearchAlgorithm
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class AStarSearchAlgorithmHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		if (element.getNodeName().equals("searchAlgorithm")
				&& element.getAttribute("name").equals("AStarSearchAlgorithm")) {
			return true;
		}
		return false;
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof AStarAlgorithm;
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		AStarAlgorithm alg = new AStarAlgorithm();
		String processorsString = element.getAttribute("multiCore");
		if (!processorsString.isEmpty()) {
			try {
				alg.setMultiCore(Boolean.valueOf(processorsString));
			}
			catch (IllegalArgumentException e) {
				throw new IOException(
						"Attribute multiCore of AStarAlgorithm must be 'true' or 'false'");
			}
		}
		for (Element e : XMLUtil.getElementList(element.getChildNodes())) {
			PersistenceManager pm = PersistenceManager.getInstance();
			Object fragment = pm.readFragment(e, kb);
			if (fragment instanceof Heuristic) {
				alg.setHeuristic((Heuristic) fragment);
			}
			if (fragment instanceof AbortStrategy) {
				alg.setAbortStrategy((AbortStrategy) fragment);
			}
		}
		return alg;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		AStarAlgorithm alg = (AStarAlgorithm) object;
		Element element = doc.createElement("searchAlgorithm");
		element.setAttribute("name", "AStarSearchAlgorithm");
		element.setAttribute("multiCore", String.valueOf(alg.isMultiCore()));

		// write heuristic
		PersistenceManager pm = PersistenceManager.getInstance();
		element.appendChild(pm.writeFragment(alg.getHeuristic(), doc));
		// write abort strategy
		AbortStrategy abortStrategy = alg.getAbortStrategy();
		if (abortStrategy != null) {
			element.appendChild(pm.writeFragment(
					abortStrategy, doc));
		}
		return element;
	}
}
