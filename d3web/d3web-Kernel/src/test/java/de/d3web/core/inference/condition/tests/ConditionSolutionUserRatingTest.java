/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.d3web.core.inference.condition.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondSolutionConfirmed;
import de.d3web.core.inference.condition.CondSolutionRejected;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.plugin.test.InitPluginManager;

/**
 * 
 * @author Reinhard Hatko
 * @created 22.11.2010
 */
public class ConditionSolutionUserRatingTest {

	KnowledgeBase kb;
	Solution solution1;
	Solution solution2;
	Solution solution3;
	Session session;

	@Before
	public void setup() throws Exception {

		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		session = SessionFactory.createSession(kb);

		solution1 = new Solution(kb.getRootSolution(), "Solution1");
		solution2 = new Solution(kb.getRootSolution(), "Solution2");
		solution3 = new Solution(kb.getRootSolution(), "Solution3");

	}

	// creates confirmed UserRating for Solution1
	@Test
	public void testUserRatingConfirmed() {

		PSMethodUserSelected psMethod = PSMethodUserSelected.getInstance();

		Blackboard blackboard = session.getBlackboard();

		Rating value1 = (Rating) blackboard.getValue(solution1, psMethod);

		assertThat(value1.getState(), is(State.UNCLEAR));

		blackboard.addValueFact(new DefaultFact(solution1, new Rating(State.ESTABLISHED), 0,
				psMethod, psMethod));

		Rating value2 = (Rating) blackboard.getValue(solution1, psMethod);

		assertThat(value2.getState(), is(State.ESTABLISHED));

	}

	// creates rejected UserRating for Solution2
	@Test
	public void testUserRatingRejected() {

		PSMethodUserSelected psMethod = PSMethodUserSelected.getInstance();

		Blackboard blackboard = session.getBlackboard();

		Rating value1 = (Rating) blackboard.getValue(solution2, psMethod);

		assertThat(value1.getState(), is(State.UNCLEAR));

		blackboard.addValueFact(new DefaultFact(solution2, new Rating(State.EXCLUDED), 0, psMethod,
				psMethod));

		Rating value2 = (Rating) blackboard.getValue(solution2, psMethod);

		assertThat(value2.getState(), is(State.EXCLUDED));

	}

	@Test
	public void testCondConfirmed() throws Exception {

		// inserts ratings for solution1 and solution2
		testUserRatingConfirmed();
		testUserRatingRejected();

		CondSolutionConfirmed solutionConfirmed1 = new CondSolutionConfirmed(solution1);
		CondSolutionConfirmed solutionConfirmed2 = new CondSolutionConfirmed(solution2);

		assertThat(solutionConfirmed1.eval(session), is(Boolean.TRUE));
		assertThat(solutionConfirmed2.eval(session), is(Boolean.FALSE));

	}

	@Test
	public void testCondRejected() throws Exception {

		// inserts ratings for solution1 and solution2
		testUserRatingConfirmed();
		testUserRatingRejected();

		CondSolutionRejected solutionConfirmed1 = new CondSolutionRejected(solution1);
		CondSolutionRejected solutionConfirmed2 = new CondSolutionRejected(solution2);

		assertThat(solutionConfirmed1.eval(session), is(Boolean.FALSE));
		assertThat(solutionConfirmed2.eval(session), is(Boolean.TRUE));

	}

	@Test(expected = NoAnswerException.class)
	public void testCondConfirmedNoAnswerException() throws Exception {

		// inserts ratings for solution1 and solution2
		testUserRatingConfirmed();
		testUserRatingRejected();

		CondSolutionConfirmed solutionConfirmed3 = new CondSolutionConfirmed(solution3);

		solutionConfirmed3.eval(session);
	}

	@Test(expected = NoAnswerException.class)
	public void testCondRejectedNoAnswerException() throws Exception {

		// inserts ratings for solution1 and solution2
		testUserRatingConfirmed();
		testUserRatingRejected();

		CondSolutionRejected solutionRejected3 = new CondSolutionRejected(solution3);

		solutionRejected3.eval(session);
	}

}
