/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.costBenefit;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Node;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.terminology.Answer;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.CaseFactory;
import de.d3web.core.session.XPSCase;
import de.d3web.costBenefit.inference.PSMethodCostBenefit;
import de.d3web.costBenefit.inference.StateTransition;

/**
 * Provides basic static functions for the CostBenefit package.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class Util {

	public static XPSCase copyCase(XPSCase theCase) {
		XPSCase testCase = CaseFactory
				.createXPSCase(theCase.getKnowledgeBase(), new LinkedList<PSMethod>());
		List<? extends Question> answeredQuestions = new LinkedList<Question>(
				theCase.getAnsweredQuestions());
		for (Question q : answeredQuestions) {
			List<Answer> a = ((QuestionOC) q).getValue(theCase);
			Answer[] aarray = new Answer[a.size()];
			a.toArray(aarray);
			testCase.setValue(q, aarray);
		}
		return testCase;
	}

	public static void undo(XPSCase theCase, Map<Question, List<?>> undo) {
		for (Entry<Question, List<?>> entry : undo.entrySet()) {
			entry.getKey().setValue(theCase, entry.getValue().toArray());
			if (entry.getValue().size() == 0) {
				theCase.getAnsweredQuestions().remove(entry.getKey());
			}
		}
	}

	public static String getAttribute(String name, Node node) {
		if ((node != null) && (node.getAttributes() != null)) {
			try {
				return node.getAttributes().getNamedItem(name).getNodeValue();
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}
	
	public static void setQuestion(XPSCase theCase, String question, String answer) {
		KnowledgeBaseManagement kbm = KnowledgeBaseManagement.createInstance(theCase.getKnowledgeBase());
		theCase.getPropagationContoller().openPropagation();
		QuestionOC question1 = (QuestionOC) kbm.findQuestion(question);
		theCase.setValue(question1, new Answer[] { kbm.findAnswer(question1, answer) });
		theCase.getPropagationContoller().commitPropagation();
	}
	
	public static StateTransition extractStateTransition(QContainer qcon) {
		List<? extends KnowledgeSlice> knowledge = qcon.getKnowledge(PSMethodCostBenefit.class, StateTransition.STATE_TRANSITION);
		for (KnowledgeSlice ks: knowledge) {
			if (ks instanceof StateTransition) {
				return (StateTransition) ks;
			}
		}
		return null;
	}
}
