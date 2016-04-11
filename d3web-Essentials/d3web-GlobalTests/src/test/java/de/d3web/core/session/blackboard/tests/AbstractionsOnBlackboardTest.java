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
package de.d3web.core.session.blackboard.tests;

import static junit.framework.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Purpose of this test: An abstraction rule defines the value of an abstraction
 * question. Afterwards, the user "overwrites" the this value manually. Check,
 * whether the user's value remains in the Blackboard.
 * 
 * This test uses the computation of the body-mass-index BMI:
 * 
 * bmi = (weight / height * height)
 * 
 * Later, the bmi is overwritten manually. For simplicity, the bmi is computed
 * by the formula, but the following rule:
 * 
 * IF weight == 200 AND height = 2 THEN bmi = 50
 * 
 * @author joba
 * @created 11.08.2010
 */
public class AbstractionsOnBlackboardTest {

	private static Blackboard blackboard;
	private static KnowledgeBase kb;
	private QuestionNum bmi, height, weight;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		Session session = SessionFactory.createSession(kb);
		blackboard = session.getBlackboard();

		bmi = new QuestionNum(kb.getRootQASet(), "BMI");
		weight = new QuestionNum(kb.getRootQASet(), "weight");
		height = new QuestionNum(kb.getRootQASet(), "height");

		// Rule: IF height=2 AND weight=200 THEN bmi=50
		RuleFactory.createSetValueRule(bmi, new NumValue(50),
				new CondAnd(Arrays.asList(new Condition[] {
						new CondEqual(height, new NumValue(2)),
						new CondEqual(weight, new NumValue(200)) })));
	}

	@Test
	public void testStandardDerivationOfAbstraction() {
		// BMI is not defined at the beginning
		assertEquals(UndefinedValue.getInstance(), blackboard.getValue(bmi));

		blackboard.addValueFact(FactFactory.createUserEnteredFact(kb,
				"weight",
				new Double(200)));
		blackboard.addValueFact(FactFactory.createUserEnteredFact(kb,
				"height",
				new Double(1.9)));
		// BMI still undefined, since the rule couldn't fire
		assertEquals(UndefinedValue.getInstance(), blackboard.getValue(bmi));

		// set height=2, so that the rule can fire and the bmi is set
		blackboard.addValueFact(FactFactory.createUserEnteredFact(kb,
				"height",
				new Double(2.0)));

		// when bmi=50 then the rule hase fired
		assertEquals(new NumValue(50), blackboard.getValue(bmi));
	}

	@Test
	public void testDerivationOfAbstractionAndUserOverwrite() {
		// BMI is not defined at the beginning
		assertEquals(UndefinedValue.getInstance(), blackboard.getValue(bmi));

		// set the weight/height, so that rule can fire
		blackboard.addValueFact(FactFactory.createUserEnteredFact(kb,
				"weight",
				new Double(200)));
		blackboard.addValueFact(FactFactory.createUserEnteredFact(kb,
				"height",
				new Double(2)));

		// when bmi=50 then the rule hase fired
		assertEquals(new NumValue(50), blackboard.getValue(bmi));

		// now, user overwrite the bmi value
		blackboard.addValueFact(FactFactory.createUserEnteredFact(kb,
				"BMI",
				new Double(10)));

		// check, if the user's input dominated over the rule derivation
		assertEquals(new NumValue(10), blackboard.getValue(bmi));

	}

	@Test
	public void testUserOverwriteAndThenDerivationOfAbstraction() {
		// BMI is not defined at the beginning
		assertEquals(UndefinedValue.getInstance(), blackboard.getValue(bmi));

		// user overwrite the abstraction question: bmi
		blackboard.addValueFact(FactFactory.createUserEnteredFact(kb,
				"BMI",
				new Double(10)));

		// check, if the user's input is in the blackboard
		assertEquals(new NumValue(10), blackboard.getValue(bmi));

		// set the weight/height, so that rule can fire
		blackboard.addValueFact(FactFactory.createUserEnteredFact(kb,
				"weight",
				new Double(200)));
		blackboard.addValueFact(FactFactory.createUserEnteredFact(kb,
				"height",
				new Double(2)));

		// check, if the user's input dominated over the rule derivation (which
		// would be 50)
		assertEquals(new NumValue(10), blackboard.getValue(bmi));
	}
}
