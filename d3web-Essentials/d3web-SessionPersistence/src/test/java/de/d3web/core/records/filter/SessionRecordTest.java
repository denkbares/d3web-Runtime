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
package de.d3web.core.records.filter;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;

import org.junit.Test;

import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.records.SessionConversionFactory;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.NumValue;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.scoring.Score;

/**
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 16.05.2012
 */
public class SessionRecordTest {

	@Test
	public void testgetSolutions() throws IOException {
		InitPluginManager.init();
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		Solution s1 = new Solution(kb, "s1");
		Solution s2 = new Solution(kb, "s2");
		Session session = SessionFactory.createSession(kb);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(s1, new Rating(Rating.State.ESTABLISHED)));
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(s2, new Rating(Rating.State.SUGGESTED)));
		SessionRecord record = SessionConversionFactory.copyToSessionRecord(session);
		List<Solution> solutions = record.getSolutions(kb, State.ESTABLISHED);
		Assert.assertEquals(1, solutions.size());
		Assert.assertEquals(s1, solutions.get(0));
		solutions = record.getSolutions(kb, State.SUGGESTED);
		Assert.assertEquals(1, solutions.size());
		Assert.assertEquals(s2, solutions.get(0));
		solutions = record.getSolutions(kb, State.SUGGESTED, State.ESTABLISHED);
		Assert.assertEquals(2, solutions.size());
	}

	@Test
	public void testgetSolutionsMerged() throws IOException {
		InitPluginManager.init();
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		Solution s1 = new Solution(kb, "s1");
		QuestionNum question = new QuestionNum(kb, "Question");
		RuleFactory.createHeuristicPSRule(s1, Score.P4, new CondNumGreater(question, 10.0));
		Session session = SessionFactory.createSession(kb);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(question, new NumValue(12.0)));
		SessionRecord record = SessionConversionFactory.copyToSessionRecord(session);
		List<Solution> solutions = record.getSolutions(kb, State.SUGGESTED);
		Assert.assertEquals(1, solutions.size());
		Assert.assertEquals(s1, solutions.get(0));
		solutions = record.getSolutions(kb, State.ESTABLISHED);
		Assert.assertEquals(0, solutions.size());
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(s1, new Rating(Rating.State.ESTABLISHED)));
		record = SessionConversionFactory.copyToSessionRecord(session);
		solutions = record.getSolutions(kb, State.SUGGESTED);
		Assert.assertEquals(0, solutions.size());
		solutions = record.getSolutions(kb, State.ESTABLISHED);
		Assert.assertEquals(1, solutions.size());
		Assert.assertEquals(s1, solutions.get(0));
	}

}
