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

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.costbenefit.ids.IterativeDeepeningSearchAlgorithm;
import de.d3web.costbenefit.inference.AbortStrategy;

/**
 * FragmentHandler for IterativeDeepeningSearchAlgorithm
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class IterativeDeepeningSearchAlgorithmHandler implements FragmentHandler<KnowledgeBase> {

	@Override
	public boolean canRead(Element element) {
		if (element.getNodeName().equals("searchAlgorithm")
				&& element.getAttribute("name").equals("IterativeDeepeningSearchAlgorithm")) {
			return true;
		}
		return false;
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof IterativeDeepeningSearchAlgorithm;
	}

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		IterativeDeepeningSearchAlgorithm alg = new IterativeDeepeningSearchAlgorithm();
		for (Element e : XMLUtil.getElementList(element.getChildNodes())) {
			Object readFragment = persistence.readFragment(e);
			if (readFragment instanceof AbortStrategy) {
				alg.setAbortStrategy((AbortStrategy) readFragment);
			}
		}
		return alg;
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		IterativeDeepeningSearchAlgorithm alg = (IterativeDeepeningSearchAlgorithm) object;
		Element element = persistence.getDocument().createElement("searchAlgorithm");
		element.setAttribute("name", "IterativeDeepeningSearchAlgorithm");
		AbortStrategy abortStrategy = alg.getAbortStrategy();
		if (abortStrategy != null) {
			element.appendChild(persistence.writeFragment(abortStrategy));
		}
		return element;
	}
}
