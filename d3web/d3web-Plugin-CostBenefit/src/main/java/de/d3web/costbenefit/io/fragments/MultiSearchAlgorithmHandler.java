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
import java.util.Arrays;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.costbenefit.inference.MultiSearchAlgorithm;
import de.d3web.costbenefit.inference.MultiSearchAlgorithm.Mode;
import de.d3web.costbenefit.inference.SearchAlgorithm;

/**
 * FragmentHandler for IteraticeDeepeningSearchAlgorithm
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class MultiSearchAlgorithmHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		if (element.getNodeName().equals("searchAlgorithm")
				&& element.getAttribute("name").equals("MultiSearchAlgorithm")) {
			return true;
		}
		return false;
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof MultiSearchAlgorithm;
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		MultiSearchAlgorithm algorithm = new MultiSearchAlgorithm();
		String modeString = element.getAttribute("mode");
		if (!modeString.isEmpty()) {
			try {
				Mode mode = Mode.valueOf(modeString);
				algorithm.setMode(mode);
			}
			catch (IllegalArgumentException e) {
				throw new IOException("unknown mode '" + modeString +
						"' in MultiSearchAlgorithm; allowed values are " +
						Arrays.asList(Mode.values()));
			}
		}
		for (Element e : XMLUtil.getElementList(element.getChildNodes())) {
			Object fragment = PersistenceManager.getInstance().readFragment(e, kb);
			if (fragment instanceof SearchAlgorithm) {
				algorithm.addSearchAlgorithms((SearchAlgorithm) fragment);
			}
		}
		return algorithm;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		MultiSearchAlgorithm algorithm = (MultiSearchAlgorithm) object;
		Element element = doc.createElement("searchAlgorithm");
		element.setAttribute("name", "MultiSearchAlgorithm");
		element.setAttribute("mode", algorithm.getMode().toString());
		for (SearchAlgorithm childAlgorithm : algorithm.getSearchAlgorithms()) {
			Element fragment = PersistenceManager.getInstance().writeFragment(childAlgorithm, doc);
			element.appendChild(fragment);
		}
		return element;
	}
}
