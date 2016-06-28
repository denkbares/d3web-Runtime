/*
 * Copyright (C) 2012 denkbares GmbH
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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

import de.d3web.core.inference.condition.CondAnd;
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
import de.d3web.costbenefit.inference.ConditionalValueSetter;
import de.d3web.costbenefit.inference.PSMethodCostBenefit;
import de.d3web.costbenefit.inference.StateTransition;
import de.d3web.costbenefit.inference.ValueTransition;
import de.d3web.costbenefit.inference.astar.AStarPath;
import de.d3web.costbenefit.inference.astar.DividedTransitionHeuristic;
import de.d3web.costbenefit.inference.astar.State;
import de.d3web.costbenefit.model.SearchModel;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Tests the behavior of the heuristics in combination with final questions
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 25.09.2012
 */
public class TestHeuristicFinalQuestionBehaviour {

	/**
	 * The values of final questions cannot be changed once they have been set.
	 * If the value is set to a value used to a precondition, this part of the
	 * precondition can be ignored, which leads to a better calculation of the
	 * predicted costs
	 * 
	 * @throws IOException
	 * 
	 * @created 25.09.2012
	 */
	@Test
	public void ignoringAnsweredFinalQuestionsForCosts() throws IOException {
		InitPluginManager.init();
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		QuestionOC nonfinalQuestion = new QuestionOC(kb, "nonfinal");
		QuestionOC finalQuestion = new QuestionOC(kb, "final");
		Choice choice = new Choice("a");
		ChoiceValue value = new ChoiceValue(choice);
		nonfinalQuestion.addAlternative(choice);
		finalQuestion.addAlternative(choice);
		finalQuestion.getInfoStore().addValue(PSMethodCostBenefit.FINAL_QUESTION, true);
		QContainer transition = new QContainer(kb, "transitionalQContainer");
		QContainer target = new QContainer(kb, "target");
		LinkedList<ValueTransition> transitions = new LinkedList<>();
		transitions.add(new ValueTransition(finalQuestion,
				Collections.singletonList(new ConditionalValueSetter(value,
						new CondAnd(Collections.emptyList())))));
		transitions.add(new ValueTransition(nonfinalQuestion,
				Collections.singletonList(new ConditionalValueSetter(value,
						new CondAnd(Collections.emptyList())))));
		new StateTransition(new CondAnd(Arrays.asList(new CondEqual(finalQuestion,
				value),
				new CondEqual(nonfinalQuestion, value))),
				Collections.emptyList(), target);
		new StateTransition(new CondAnd(Collections.emptyList()), transitions,
				transition);

		Session session = SessionFactory.createSession(kb);
		DividedTransitionHeuristic heuristic = new DividedTransitionHeuristic();
		AStarPath emptyPath = new AStarPath(null, null, 0);
		SearchModel model = new SearchModel(session);
		heuristic.init(model);
		double distance = heuristic.getDistance(model, emptyPath,
				new State(session, Collections.emptyMap()), target);
		Assert.assertEquals(1.0, distance, 0);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(finalQuestion, value));
		heuristic.init(model);
		distance = heuristic.getDistance(model, emptyPath,
				new State(session, Collections.emptyMap()), target);
		Assert.assertEquals(1.0, distance, 0);
	}

}
