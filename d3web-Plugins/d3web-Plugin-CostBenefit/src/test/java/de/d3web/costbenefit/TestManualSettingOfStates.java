/*
 * Copyright (C) 2013 denkbares GmbH
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

import java.io.IOException;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import com.denkbares.plugin.test.InitPluginManager;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.costbenefit.inference.AbortException;
import de.d3web.costbenefit.inference.ConditionalValueSetter;
import de.d3web.costbenefit.inference.ExpertMode;
import de.d3web.costbenefit.inference.PSMethodStateTransition.StateTransitionFact;
import de.d3web.costbenefit.inference.StateTransition;
import de.d3web.costbenefit.inference.ValueTransition;
import de.d3web.interview.Form;
import de.d3web.interview.Interview;

/**
 * This class tests setting states that are used in preconditions and value
 * transitions, without blocking the Costbenefit
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 29.11.2013
 */
public class TestManualSettingOfStates {

	@Test
	public void test() throws IOException, AbortException {
		InitPluginManager.init();
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		// State QContainer
		QContainer states = new QContainer(kb, "States");
		QuestionOC statusA = new QuestionOC(states, "StatusA");
		Choice choiceA_A = new Choice("ValueA_A");
		Choice choiceA_B = new Choice("ValueA_B");
		ChoiceValue valueA_A = new ChoiceValue(choiceA_A);
		ChoiceValue valueA_B = new ChoiceValue(choiceA_B);
		statusA.addAlternative(choiceA_A);
		statusA.addAlternative(choiceA_B);
		// QContainer1 (always applicable, always setting statusA to valueA_B
		QContainer qContainer1 = new QContainer(kb, "QContainer1");
		new StateTransition(null, Collections.singletonList(new ValueTransition(statusA,
				Collections.singletonList(new ConditionalValueSetter(valueA_B, null)))), qContainer1);
		QuestionOC next = new QuestionOC(qContainer1, "Next");
		Choice okChoice = new Choice("ok");
		next.addAlternative(okChoice);
		ChoiceValue okValue = new ChoiceValue(okChoice);
		// QContainer2 (precondition valueA_B, no post transition
		QContainer qContainer2 = new QContainer(kb, "QContainer2");
		new StateTransition(new CondEqual(statusA, valueA_B),
				Collections.emptyList(), qContainer2);
		QuestionOC finish = new QuestionOC(qContainer2, "Finish");
		Choice doneChoice = new Choice("ok");
		finish.addAlternative(doneChoice);
		ChoiceValue doneValue = new ChoiceValue(doneChoice);
		// start session
		Session session = SessionFactory.createSession(kb);
		session.getBlackboard().addValueFact(new StateTransitionFact(session, statusA, valueA_A));
		ExpertMode expertMode = ExpertMode.getExpertMode(session);
		for (int i = 0; i <= 2; i++) {
			answerInterviewAndResetState(statusA, valueA_A, valueA_B, next, okValue, qContainer2,
					finish, doneValue, session, expertMode);
		}
	}

	private void answerInterviewAndResetState(QuestionOC statusA, ChoiceValue valueA_A, ChoiceValue valueA_B, QuestionOC next, ChoiceValue okValue, QContainer qContainer2, QuestionOC finish, ChoiceValue doneValue, Session session, ExpertMode expertMode) throws AbortException {
		expertMode.selectTarget(qContainer2);
		Interview interview = Interview.get(session);
		Form nextForm = interview.nextForm();
		Assert.assertEquals(next, nextForm.getActiveQuestions().get(0));
		session.getBlackboard().addValueFact(FactFactory.createUserEnteredFact(next, okValue));
		// value should be overwritten by state transition
		Assert.assertEquals(valueA_B, session.getBlackboard().getValue(statusA));
		nextForm = interview.nextForm();
		Assert.assertEquals(finish, nextForm.getActiveQuestions().get(0));
		session.getBlackboard().addValueFact(FactFactory.createUserEnteredFact(finish, doneValue));
		Assert.assertEquals(valueA_B, session.getBlackboard().getValue(statusA));
		session.getBlackboard().addValueFact(new StateTransitionFact(session, statusA, valueA_A));
		// manual entered state transition should have overwritten the value of
		// the state transition
		Assert.assertEquals(valueA_A, session.getBlackboard().getValue(statusA));
	}

}
