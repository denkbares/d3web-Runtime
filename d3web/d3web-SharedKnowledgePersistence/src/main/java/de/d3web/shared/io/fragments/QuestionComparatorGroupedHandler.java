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
package de.d3web.shared.io.fragments;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.shared.comparators.GroupedComparator;
import de.d3web.shared.comparators.PairRelation;
import de.d3web.shared.comparators.QuestionComparator;

/**
 * Provides basic functions for QuestionComparatorGroupedHandlers
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public abstract class QuestionComparatorGroupedHandler extends QuestionComparatorHandler {

	@Override
	public Element write(Object object, Document doc) throws IOException {
		Element element = super.write(object, doc);
		GroupedComparator gc = (GroupedComparator) object;
		appendRelationGroups(doc, element, gc.getPairRelations());
		return element;
	}

	@Override
	protected void addAdditionalInformation(QuestionComparator qc, Element childNode, KnowledgeBase kb) throws IOException {
		if (childNode.getNodeName()
				.equalsIgnoreCase("pairRelations")) {
			List<Element> pairs = XMLUtil.getElementList(childNode.getChildNodes());
			for (Element pair : pairs) {
				Object readFragment = PersistenceManager.getInstance().readFragment(pair, kb);
				if (readFragment instanceof PairRelation) {
					((GroupedComparator) qc).addPairRelation((PairRelation) readFragment);
				}
			}
		}
	}

	private static void appendRelationGroups(Document doc, Element element,
			List<PairRelation> relations)
			throws IOException {
		Element pairRelationsElement = doc.createElement("pairRelations");
		for (PairRelation pr : relations) {
			pairRelationsElement.appendChild(PersistenceManager.getInstance().writeFragment(pr, doc));
		}
		element.appendChild(pairRelationsElement);
	}
}
