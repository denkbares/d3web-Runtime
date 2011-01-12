/*
 * Copyright (C) 2010 denkbares GmbH, Würzburg, Germany
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

package de.d3web.core.session.blackboard;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PropagationManager;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.indication.inference.PSMethodUserSelected;

/**
 * A factory to create {@link Fact} instances.
 * 
 * @author joba
 */
public final class FactFactory {

	private FactFactory() { // enforce noninstantiability
	}

	/**
	 * Creates a new fact assigning the specified {@link Value} to the specified
	 * {@link TerminologyObject}. The specified source is responsible for the
	 * setting the value, which acts in the context of the specified
	 * {@link PSMethod}.
	 * 
	 * @param session TODO
	 * @param terminologyObject the specified {@link TerminologyObject} instance
	 * @param value the specified {@link Value} instance
	 * @param source the responsible source
	 * @param psMethod the fact is created in the context of the specified
	 *        {@link PSMethod}
	 * 
	 * @return a newly created {@link Fact} instance
	 */
	public static Fact createFact(Session session,
			TerminologyObject terminologyObject, Value value, Object source, PSMethod psMethod) {
		long time = System.currentTimeMillis();

		return new DefaultFact(terminologyObject, value, time, source, psMethod);
	}

	/**
	 * A new fact is created assigning the specified {@link Value} to the
	 * specified {@link TerminologyObject}. The source and psMethod context of
	 * this fact is the user (i.e., {@link PSMethodUserSelected}).
	 * 
	 * @param terminologyObject the specified {@link TerminologyObject} instance
	 * @param value the specified {@link Value} instance
	 * @return a newly created {@link Fact} instance
	 */
	public static Fact createUserEnteredFact(
			TerminologyObject terminologyObject, Value value) {

		long time = System.currentTimeMillis();

		return new DefaultFact(terminologyObject, value, time, PSMethodUserSelected.getInstance(),
				PSMethodUserSelected.getInstance());
	}

	/**
	 * A new fact is created assigning a {@link ChoiceValue} to a
	 * {@link QuestionChoice}. The {@link QuestionChoice} and the {@link Choice}
	 * are searched by their ids in the given {@link KnowledgeBase}. The source
	 * and psMethod context of this fact is the user (i.e.,
	 * {@link PSMethodUserSelected}).
	 * 
	 * @param kb {@link KnowledgeBase}
	 * @param questionID ID of the {@link QuestionChoice}
	 * @param answerID ID of the {@link Choice}
	 * @return a newly created {@link Fact} instance or null, if the
	 *         {@link QuestionChoice} or {@link Choice} could not be found
	 */
	public static Fact createUserEnteredFact(KnowledgeBase kb, String questionID, String answerID) {
		Question question = kb.getManager().searchQuestion(questionID);
		if (question == null) {
			// if not found, then try to find a question with this name
			question = (Question) kb.getManager().searchObjectForName(questionID);
		}
		if (question instanceof QuestionChoice) {
			QuestionChoice qc = (QuestionChoice) question;
			KnowledgeBaseManagement kbm = KnowledgeBaseManagement.createInstance(kb);
			Choice choice = kbm.findChoice(qc, answerID);
			if (choice != null) {
				return createUserEnteredFact(qc, new ChoiceValue(choice));
			}
		}
		return null;
	}

	/**
	 * A new fact is created assigning a {@link NumValue} to a
	 * {@link QuestionNum}. The {@link QuestionNum} is searched by its id in the
	 * given {@link KnowledgeBase}. The source and psMethod context of this fact
	 * is the user (i.e., {@link PSMethodUserSelected}).
	 * 
	 * @param kb {@link KnowledgeBase}
	 * @param questionID ID of the {@link QuestionNum}
	 * @param value the Double value of the question
	 * @return a newly created {@link Fact} instance or null, if the
	 *         {@link QuestionNum} could not be found
	 */
	public static Fact createUserEnteredFact(KnowledgeBase kb, String questionID, Double value) {
		return createUserEnteredFact(kb, questionID, new NumValue(value));
	}

	/**
	 * A new fact is created assigning a {@link Value} to a {@link Question}.
	 * The {@link Question} is searched by its id in the given
	 * {@link KnowledgeBase}. The source and psMethod context of this fact is
	 * the user (i.e., {@link PSMethodUserSelected}).
	 * 
	 * @param kb {@link KnowledgeBase}
	 * @param questionID ID of the {@link Question}
	 * @param value the {@link Value} of the question
	 * @return a newly created {@link Fact} instance or null, if the
	 *         {@link Question} could not be found
	 */
	public static Fact createUserEnteredFact(KnowledgeBase kb, String questionID, Value value) {
		Question question = kb.getManager().searchQuestion(questionID);
		if (question == null) {
			// if not found, then try to find a question with this name
			question = (Question) kb.getManager().searchObjectForName(questionID);
		}
		if (question != null) {
			return createUserEnteredFact(question, value);
		}
		return null;
	}

	/**
	 * A new fact is created, that represents the specified indication of a
	 * specified terminology object. The indication is performed in the context
	 * of the specified source and problem-solving action.
	 * 
	 * @created 18.08.2010
	 * @param terminologyObject the specified terminologyObject to be indicated
	 * @param indication the specified indication type
	 * @param source the object (e.g., rule or user) actually indicating the
	 *        {@link TerminologyObject}
	 * @param psMethodContext the context problem-solving method indicating the
	 *        {@link TerminologyObject}
	 * @return a fact representing the specified indication
	 */
	public static Fact createIndicationFact(Session session, TerminologyObject terminologyObject,
			Indication indication, Object source, PSMethod psMethodContext) {

		long time = getCurrentTime(session);

		return new DefaultFact(terminologyObject, indication, time, source, psMethodContext);
	}

	/**
	 * This method returns the current time of the session, ie the time of the
	 * current propagation if a propagation is opened, and the current system
	 * time otherwise.
	 * 
	 * @created 18.11.2010
	 * @param session
	 * @return the current time of the session
	 */
	public static long getCurrentTime(Session session) {

		PropagationManager propagationManager = session.getPropagationManager();

		if (!propagationManager.isInPropagation()) {
			return System.currentTimeMillis();
		}
		else {
			return propagationManager.getPropagationTime();
		}

	}

}
