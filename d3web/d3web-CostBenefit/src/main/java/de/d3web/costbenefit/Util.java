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
package de.d3web.costbenefit;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Node;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.costbenefit.inference.PSMethodCostBenefit;
import de.d3web.costbenefit.inference.StateTransition;
import de.d3web.indication.inference.PSMethodUserSelected;

/**
 * Provides basic static functions for the CostBenefit package.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class Util {

	public static Session copyCase(Session session) {
		Session testCase = SessionFactory
				.createSession(session.getKnowledgeBase(), new LinkedList<PSMethod>());
		Blackboard blackboard = session.getBlackboard();
		List<? extends Question> answeredQuestions = new LinkedList<Question>(
				blackboard.getAnsweredQuestions());
		for (Question q : answeredQuestions) {
			Fact fact = blackboard.getValueFact(q);
			testCase.getBlackboard().addValueFact(fact);
		}
		return testCase;
	}

	public static void undo(Session session, List<Fact> facts) {
		for (Fact fact : facts) {
			session.getBlackboard().removeValueFact(fact);
		}
	}

	public static String getAttribute(String name, Node node) {
		if ((node != null) && (node.getAttributes() != null)) {
			Node namedItem = node.getAttributes().getNamedItem(name);
			if (namedItem != null) {
				return namedItem.getNodeValue();
			}
		}
		return null;
	}

	public static void setQuestion(Session session, String question, String answer) {
		KnowledgeBaseManagement kbm = KnowledgeBaseManagement.createInstance(session.getKnowledgeBase());
		session.getPropagationManager().openPropagation();
		QuestionOC question1 = (QuestionOC) kbm.findQuestion(question);
		session.getBlackboard().addValueFact(
				FactFactory.createFact(question1, kbm.findValue(question1, answer),
				PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));
		session.getPropagationManager().commitPropagation();
	}

	public static StateTransition extractStateTransition(QContainer qcon) {
		KnowledgeSlice knowledge = qcon.getKnowledge(PSMethodCostBenefit.class,
				StateTransition.STATE_TRANSITION);
		if (knowledge != null) {
			if (knowledge instanceof StateTransition) {
				return (StateTransition) knowledge;
			}
		}
		return null;
	}
}
