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
package de.d3web.costBenefit.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Answer;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.Value;
import de.d3web.core.session.Session;
import de.d3web.core.session.values.Choice;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.costBenefit.inference.StateTransition;
import de.d3web.shared.Abnormality;
import de.d3web.shared.AbstractAbnormality;
import de.d3web.shared.PSMethodShared;

/**
 * QContainer Node for the virtual graph.
 * Provides easy access to cost-benefit issues of QContainers.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class Node {
	
	private final QContainer qContainer;
	private final List<QuestionOC> questions;
	private StateTransition st;
	private final SearchModel cbm;
	
	
	
	public Node(QContainer qcon, SearchModel cbm) {
		this.cbm=cbm;
		this.qContainer=qcon;
		this.questions = new LinkedList<QuestionOC>();
		collectQuestions(this.qContainer, this.questions);
		Collection<KnowledgeSlice> allKnowledge = qContainer.getAllKnowledge();
		for (KnowledgeSlice ks: allKnowledge) {
			if (ks instanceof StateTransition) st = (StateTransition) ks;
		}
	}

	private void collectQuestions(TerminologyObject namedObject, List<QuestionOC> result) {
		if (namedObject instanceof QuestionOC && !result.contains(namedObject)) {
			result.add((QuestionOC) namedObject);
		}
		for (TerminologyObject child : namedObject.getChildren()) {
			collectQuestions(child, result);
		}
	}
	
	/**
	 * Checks if the Node is applicable in theCase.
	 * A node is applicable if the preconditions of the associated QContainer
	 * are matching theCase.
	 * @param theCase
	 * @return
	 */
	public boolean isApplicable(Session theCase) {
		if (st==null) return false;
		Condition activationCondition = st.getActivationCondition();
		if (activationCondition==null) return true;
		try {
			return activationCondition.eval(theCase);
		}
		catch (NoAnswerException e) {
		}
		catch (UnknownAnswerException e) {
		}
		// if there is no answer (or the answer is unknown),
		// the QContainer is not applicable
		return false;
	}
	
	public QContainer getQContainer() {
		return qContainer;
	}

	public List<QuestionOC> getQuestions() {
		return questions;
	}

	public StateTransition getStateTransition() {
		return st;
	}

	private void setAnswer(Session theCase, Question q,
			Value answer, Map<Question, Value> map) {
		map.put(q, q.getValue(theCase));
		theCase.setValue(q, answer);
	}
	
	public double getCosts(Session session) {
		if (cbm==null) return getStaticCosts();
		return cbm.getCostFunction().getCosts(qContainer, session);
	}
	
	public double getStaticCosts() {
		Object property = qContainer.getProperties().getProperty(Property.COST);
		if (property == null) {
			return 0;
		} else {
			return (Double) property;
		}
	}
	
	/**
	 * Returns the expected values of all Questions of the Node's QContainer
	 * @param testCase
	 * @return
	 */
	public Map<Question, Value> getExpectedValues(Session testCase) {
		return answerGetterAndSetter(testCase, false);
	}
	
	/**
	 * Ensures that all questions of the Node's QContainer are answered.
	 * For unanswered Questions the expected values are set. The Method returns the
	 * unanswered questions and their previous values (which is in this case always an empty list)
	 * @param testCase
	 * @return
	 */
	public Map<Question, Value> setNormalValues(Session testCase) {
		return answerGetterAndSetter(testCase, true);
	}
	
	private Map<Question, Value> answerGetterAndSetter(Session testCase, boolean set) {
		List<? extends Question> answeredQuestions = testCase.getAnsweredQuestions();
		Map<Question, Value> undomap = new HashMap<Question, Value>();
		Map<Question, Value> expectedmap = new HashMap<Question, Value>();
		for (QuestionOC q: questions) {
			if (!answeredQuestions.contains(q)) {
				KnowledgeSlice ks = q.getKnowledge(new Abnormality().getProblemsolverContext(), PSMethodShared.SHARED_ABNORMALITY);
				if (ks==null) {
					if (set)
						Logger.getLogger(this.getClass().getName()).throwing(
								  this.getClass().getName(), "Fehler, kein Normalwert gesetzt: "+q, null);
					continue;
				}
				if (ks instanceof Abnormality) {
					Abnormality abnormality = (Abnormality) ks;
					List<Answer> alternatives = q.getAlternatives(testCase);
					for (Answer a: alternatives) {
						ChoiceValue avalue = new ChoiceValue((Choice) a);
						if (abnormality.getValue(avalue) == AbstractAbnormality.A0) {
							if (set) {
								setAnswer(testCase, q, avalue, undomap);
							} else {
								expectedmap.put(q, avalue);
							}
							break;
						}
					}
				}
			} else {
				expectedmap.put(q, q.getValue(testCase));
			}
		}
		if (set) {
			return undomap;
		} else {
			return expectedmap;
		}
	}
	
	@Override
	public boolean equals(Object o) {
		return (o instanceof Node)&&(this.qContainer.equals(((Node)o).qContainer));
	}
	
	@Override
	public int hashCode() {
		return qContainer.hashCode();
	}

	@Override
	public String toString() {
		return qContainer.getName();
	}
}
