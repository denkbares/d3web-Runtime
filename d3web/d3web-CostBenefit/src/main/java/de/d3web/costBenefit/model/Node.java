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
import de.d3web.core.inference.condition.AbstractCondition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.session.XPSCase;
import de.d3web.core.terminology.Answer;
import de.d3web.core.terminology.NamedObject;
import de.d3web.core.terminology.QContainer;
import de.d3web.core.terminology.Question;
import de.d3web.core.terminology.QuestionOC;
import de.d3web.core.terminology.info.Property;
import de.d3web.costBenefit.StateTransition;
import de.d3web.kernel.psMethods.shared.Abnormality;
import de.d3web.shared.AbstractAbnormality;
import de.d3web.shared.PSMethodShared;

/**
 * QContainer Node for the virtual graph
 * Provides easy access to costbenefit issues of QContainers. 
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class Node {
	
	private QContainer qContainer;
	private List<QuestionOC> questions;
	private StateTransition st;
	private SearchModel cbm;
	
	
	
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

	private void collectQuestions(NamedObject namedObject, List<QuestionOC> result) {
		if (namedObject instanceof QuestionOC && !result.contains(namedObject)) {
			result.add((QuestionOC) namedObject);
		}
		for (NamedObject child : namedObject.getChildren()) {
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
	public boolean isApplicable(XPSCase theCase) {
		if (st==null) return false;
		AbstractCondition activationCondition = st.getActivationCondition();
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

	private void setAnswer(XPSCase theCase, Question q,
			Answer answer, Map<Question, List<?>> map) {
		map.put(q, q.getValue(theCase));
		Answer[] a = new Answer[1];
		a[0] = answer;
		theCase.setValue(q, a);
	}
	
	public double getCosts(XPSCase xpsCase) {
		if (cbm==null) return getStaticCosts();
		return cbm.getCostFunction().getCosts(qContainer, xpsCase);
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
	public Map<Question, List<?>> getExpectedValues(XPSCase testCase) {
		return answerGetterAndSetter(testCase, false);
	}
	
	/**
	 * Ensures that all questions of the Node's QContainer are answered.
	 * For unanswered Questions the expected values are set. The Method returns the
	 * unanswered questions and their previous values (which is in this case always an empty list)
	 * @param testCase
	 * @return
	 */
	public Map<Question, List<?>> setNormalValues(XPSCase testCase) {
		return answerGetterAndSetter(testCase, true);
	}
	
	private Map<Question, List<?>> answerGetterAndSetter(XPSCase testCase, boolean set) {
		List<? extends Question> answeredQuestions = testCase.getAnsweredQuestions();
		Map<Question, List<?>> undomap = new HashMap<Question, List<?>>();
		Map<Question, List<?>> expectedmap = new HashMap<Question, List<?>>();
		for (QuestionOC q: questions) {
			if (!answeredQuestions.contains(q)) {
				List<? extends KnowledgeSlice> knowledge = q.getKnowledge(new Abnormality().getProblemsolverContext(), PSMethodShared.SHARED_ABNORMALITY);
				if (knowledge==null) {
					if (set)
						Logger.getLogger(this.getClass().getName()).throwing(
								  this.getClass().getName(), "Fehler, kein Normalwert gesetzt: "+q, null);
					continue;
				}
				for (KnowledgeSlice ks: knowledge) {
					if (ks instanceof Abnormality) {
						Abnormality abnormality = (Abnormality) ks;
						List<Answer> alternatives = q.getAlternatives(testCase);
						for (Answer a: alternatives) {
							if (abnormality.getValue(a)==AbstractAbnormality.A0) {
								if (set) {
									setAnswer(testCase, q, a, undomap);
								} else {
									List<Answer> al = new LinkedList<Answer>();
									al.add(a);
									expectedmap.put(q, al);
								}
								break;
							}
						}
						break;
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
		return qContainer.getText();
	}
}
