/*
 * Copyright (C) 2010 denkbares GmbH
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

import de.d3web.abstraction.formula.FormulaNumberElement;
import de.d3web.abstraction.formula.Operator;
import de.d3web.abstraction.formula.Operator.Operation;
import de.d3web.abstraction.formula.QNumWrapper;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Purpose of this test: Check, if formula-based abstraction rules act on the
 * blackboard as expected. Here, we check
 * 
 * <LI> {@link NumValue} are set as defined by a formula expression in a rule
 * action</LI>
 * 
 * <LI>Changes of values are propagated to the rule firing an updated value of
 * the rule action</LI>
 * 
 * <LI>User can overwrite the derived values</LI>
 * 
 * The test is performed by running a BMI calculation (body-mass-index), for
 * which the following rule applies:
 * 
 * BMI = weight / (height * height)
 * 
 * @author joba
 * @created 19.08.2010
 */
public class NumericAbstractionsOnBlackboardTest {

	private static Blackboard blackboard;
	private static KnowledgeBaseManagement kbm;
	private QuestionNum bmi, height, weight;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kbm = KnowledgeBaseManagement.createInstance();
		Session session = SessionFactory.createSession(kbm.getKnowledgeBase());
		blackboard = session.getBlackboard();

		bmi = kbm.createQuestionNum("BMI", kbm.getKnowledgeBase().getRootQASet());
		weight = kbm.createQuestionNum("weight", kbm.getKnowledgeBase().getRootQASet());
		height = kbm.createQuestionNum("height", kbm.getKnowledgeBase().getRootQASet());

		// Now create the formula expression:
		// Formula: (weight / (height * height))
		FormulaNumberElement fElement = new Operator(new QNumWrapper(weight),
				new Operator(new QNumWrapper(height), new QNumWrapper(height), Operation.Mult),
				Operation.Div);
		// Assignment: bmi = (weight / (height * height))

		// Rule: IF height>0 AND weight>0 THEN bmi= (weight / (height * height))
		RuleFactory.createSetValueRule("r1", bmi, fElement,
				new CondAnd(Arrays.asList(new Condition[] {
						new CondNumGreater(height, 0.0),
						new CondNumGreater(weight, 0.0) })));
	}

	@Test
	public void testStandardDerivationOfAbstraction() {
		// BMI is not defined at the beginning
		assertEquals(UndefinedValue.getInstance(), blackboard.getValue(bmi));

		blackboard.addValueFact(FactFactory.createUserEnteredFact(kbm.getKnowledgeBase(),
				"weight",
				new Double(200)));
		blackboard.addValueFact(FactFactory.createUserEnteredFact(kbm.getKnowledgeBase(),
				"height",
				new Double(1.9)));

		// BMI should be around 55.4
		double bmiValue = (Double) (blackboard.getValue(bmi).getValue());
		assertEquals(55.4, bmiValue, 0.1);

		// set weight=80, so that the rule need to update the bmi value
		blackboard.addValueFact(FactFactory.createUserEnteredFact(kbm.getKnowledgeBase(),
				"weight",
				new Double(80)));

		// BMI should be around 22.16
		bmiValue = (Double) (blackboard.getValue(bmi).getValue());
		assertEquals(22.16, bmiValue, 0.1);

		// now the user sets the bmi value manually
		// user values should always overwrite value derived by rules
		Fact userSetFact = FactFactory.createUserEnteredFact(kbm.getKnowledgeBase(), "BMI",
				new Double(30));
		blackboard.addValueFact(userSetFact);
		bmiValue = (Double) (blackboard.getValue(bmi).getValue());
		assertEquals(30, bmiValue, 0.1);

		// remove the user fact, then the last known rule derivation shoudl be
		// valid again
		blackboard.removeValueFact(userSetFact);
		// BMI should be around 22.16
		bmiValue = (Double) (blackboard.getValue(bmi).getValue());
		assertEquals(22.16, bmiValue, 0.1);
	}

}
