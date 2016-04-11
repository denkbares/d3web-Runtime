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
package de.d3web.core.io.fragments;

import java.io.IOException;

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.InfoStoreUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.Property.Autosave;
import de.d3web.scoring.Score;

/**
 * FragmentHanler for Diagnosis Children are ignored, hierarchies are
 * read/written by the knowledge readers/writers.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class SolutionsHandler implements FragmentHandler<KnowledgeBase> {

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals("Diagnosis");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof Solution);
	}

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		String id = element.getAttribute("name");
		String apriori = element.getAttribute("aPriProb");
		Solution diag = new Solution(persistence.getArtifact(), id);
		if (apriori != null) {
			diag.setAprioriProbability(XMLUtil.getScore(apriori));
		}
		PropertiesHandler ph = new PropertiesHandler();
		for (Element child : XMLUtil.getElementList(element.getChildNodes())) {
			if (child.getNodeName().equals(XMLUtil.INFO_STORE)) {
				XMLUtil.fillInfoStore(persistence, diag.getInfoStore(), child);
			}
			else if (ph.canRead(child)) {
				InfoStoreUtil.copyEntries(ph.read(persistence, child), diag.getInfoStore());
			}
		}
		return diag;
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		Solution diag = (Solution) object;
		Element element = persistence.getDocument().createElement("Diagnosis");
		element.setAttribute("name", diag.getName());
		Score apriori = diag.getAprioriProbability();
		if (apriori != null) {
			element.setAttribute("aPriProb", apriori.getSymbol());
		}
		XMLUtil.appendInfoStore(persistence, element, diag, Autosave.basic);
		return element;
	}
}
