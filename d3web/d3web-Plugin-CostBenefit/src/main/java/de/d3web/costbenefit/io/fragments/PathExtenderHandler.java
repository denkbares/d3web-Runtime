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
import de.d3web.costbenefit.inference.PathExtender;
import de.d3web.costbenefit.inference.SearchAlgorithm;

/**
 * FragmentHandler for PathExtender
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class PathExtenderHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		if (element.getNodeName().equals("searchAlgorithm")
				&& element.getAttribute("name").equals("PathExtender")) {
			return true;
		}
		return false;
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof PathExtender;
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		SearchAlgorithm subAlgorithm = null;
		for (Element e : XMLUtil.getElementList(element.getChildNodes())) {
			Object fragment = PersistenceManager.getInstance().readFragment(e, kb);
			if (fragment instanceof SearchAlgorithm) {
				subAlgorithm = (SearchAlgorithm) fragment;
				break;
			}
		}
		PathExtender algorithm = new PathExtender(subAlgorithm);
		return algorithm;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		PathExtender algorithm = (PathExtender) object;
		Element element = doc.createElement("searchAlgorithm");
		element.setAttribute("name", "PathExtender");
		Element fragment = PersistenceManager.getInstance().writeFragment(
				algorithm.getSubalgorithm(), doc);
		element.appendChild(fragment);
		return element;
	}
}
