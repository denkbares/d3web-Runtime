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
package de.d3web.shared.io.fragments;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.KnowledgeBase;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.kernel.psMethods.shared.comparators.PairRelation;
/**
 * Handles PairRelations
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class PairRelationHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equalsIgnoreCase("pairRelation");
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof PairRelation;
	}

	@Override
	public Object read(KnowledgeBase kb, Element pair) throws IOException {
		String ans1 = pair.getAttributes().getNamedItem("answer1")
				.getNodeValue();
		String ans2 = pair.getAttributes().getNamedItem("answer2")
				.getNodeValue();
		double value = new Double(pair.getAttributes().getNamedItem("value")
				.getNodeValue()).doubleValue();
		AnswerChoice answer1 = kb.searchAnswerChoice(ans1);
		AnswerChoice answer2 = kb.searchAnswerChoice(ans2);
		return new PairRelation(answer1, answer2, value);
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		Element element = doc.createElement("pairRelation");
		PairRelation pairRelation = (PairRelation) object;
		element.setAttribute("answer1", pairRelation.getAnswer1().getId());
		element.setAttribute("answer2", pairRelation.getAnswer2().getId());
		element.setAttribute("value", ""+pairRelation.getValue());
		return element;
	}
	
}
