/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.kernel.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.d3web.abstraction.formula.Operator;
import de.d3web.abstraction.formula.QNumWrapper;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.inference.condition.CondNumIn;
import de.d3web.core.inference.condition.CondNumLess;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.plugin.test.InitPluginManager;

/**
 * This test is designed to control the setting of abstract choice and num
 * questions' values based on the result of formula expressions
 * 
 * The tested knowledgebase contains the following terminology objects:
 * 
 * <b>Questions</b> Weight [num] Height [num] BMI [num] <abstract> Category [oc]
 * <abstract> - Underweight - Normal - Overweight
 * 
 * The problem solving is based on the following <b>Rules</b>:
 * 
 * Weight > 0 AND Height > 0 => BMI = (Weight / (Height * Height)) BMI < 18.5 =>
 * Category = Underweight BMI IN[18.5, 25.0] => Category = Normal BMI > 25 =>
 * Category = Overweight
 * 
 * 
 * @author Sebastian Furth (denkbares GmbH)
 * 
 */
public class FormulaExpressionAbstractionTest {

	private static KnowledgeBase kb;
	private static Session session;

	@BeforeClass
	public static void setUp() throws Exception {
		InitPluginManager.init();
	}

	@Before
	public void before() {
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		addTerminologyObjects();
		addRules();
		session = SessionFactory.createSession(kb);
	}

	private static void addTerminologyObjects() {
		// Question 'Weight'
		new QuestionNum(kb.getRootQASet(), "Weight");

		// Question 'Height'
		new QuestionNum(kb.getRootQASet(), "Height");

		// Question 'BMI'
		Question bmi = new QuestionNum(kb.getRootQASet(), "BMI");
		bmi.getInfoStore().addValue(BasicProperties.ABSTRACTION_QUESTION, Boolean.TRUE);

		// Question 'Category'
		String[] categoryAlternatives = {
				"Underweight", "Normal", "Overweight" };
		Question category = new QuestionOC(kb.getRootQASet(), "Category",
				categoryAlternatives);
		category.getInfoStore().addValue(BasicProperties.ABSTRACTION_QUESTION, Boolean.TRUE);
	}

	private static void addRules() {

		// Weight > 0 AND Height > 0 => BMI = (Weight / (Height * Height))
		QuestionNum weight = (QuestionNum) kb.getManager().searchQuestion("Weight");
		QuestionNum height = (QuestionNum) kb.getManager().searchQuestion("Height");

		Condition knownWeight = new CondNumGreater(weight, 0.0);
		Condition knownHeight = new CondNumGreater(height, 0.0);
		Condition weightAndHeight = new CondAnd(Arrays.asList(knownWeight, knownHeight));

		QNumWrapper weightWrapper = new QNumWrapper(weight);
		QNumWrapper heightWrapper = new QNumWrapper(height);
		Operator heightSquared = new Operator(heightWrapper, heightWrapper, Operator.Operation.Mult);
		Operator bmiFormula = new Operator(weightWrapper, heightSquared, Operator.Operation.Div);

		QuestionNum bmi = (QuestionNum) kb.getManager().searchQuestion("BMI");
		RuleFactory.createSetValueRule(bmi, bmiFormula, weightAndHeight);

		Question category = kb.getManager().searchQuestion("Category");

		// BMI < 18.5 => Category = Underweight
		Value underweight = KnowledgeBaseUtils.findValue(category, "Underweight");
		CondNumLess underweightCondition = new CondNumLess(bmi, 18.5);
		RuleFactory.createSetValueRule(category, underweight,
				underweightCondition);

		// BMI IN[18.5, 25.0] => Category = Normal
		Value normal = KnowledgeBaseUtils.findValue(category, "Normal");
		CondNumIn normalCondition = new CondNumIn(bmi, 18.5, 25.0);
		RuleFactory.createSetValueRule(category, normal, normalCondition);

		// BMI > 25 => Category = Overweight
		Value overweight = KnowledgeBaseUtils.findValue(category, "Overweight");
		CondNumGreater overweightCondition = new CondNumGreater(bmi, 25.0);
		RuleFactory.createSetValueRule(category, overweight,
				overweightCondition);
	}

	@Test
	public void testTerminlogyObjectExistence() {

		// Question 'Weight'
		Question weight = kb.getManager().searchQuestion("Weight");
		assertNotNull("Question 'Weight' isn't in the Knowledgebase.", weight);

		// Question 'Height'
		Question height = kb.getManager().searchQuestion("Height");
		assertNotNull("Question 'Weight' isn't in the Knowledgebase.", height);

		// Question 'BMI'
		Question bmi = kb.getManager().searchQuestion("BMI");
		assertNotNull("Question 'BMI' isn't in the Knowledgebase.", bmi);

		// Question 'Category'
		Question category = kb.getManager().searchQuestion("Category");
		assertNotNull("Question 'Category' isn't in the Knowledgebase.", category);

		// Values of 'Category'
		Value underweight = KnowledgeBaseUtils.findValue(category, "Underweight");
		assertNotNull("Value 'Underweight' of Question 'Category' isn't in the Knowledgebase",
				underweight);
		Value normal = KnowledgeBaseUtils.findValue(category, "Normal");
		assertNotNull("Value 'Normal' of Question 'Category' isn't in the Knowledgebase", normal);
		Value overweight = KnowledgeBaseUtils.findValue(category, "Overweight");
		assertNotNull("Value 'Overweight' of Question 'Category' isn't in the Knowledgebase",
				overweight);
	}

	@Test
	public void testAbstractionProperty() {

		// TEST BMI <abstract> ?
		Question bmi = kb.getManager().searchQuestion("BMI");
		Boolean bmiAbstractionProperty = bmi.getInfoStore().getValue(
				BasicProperties.ABSTRACTION_QUESTION);
		assertEquals("Question 'BMI' isn't abstract.", Boolean.TRUE, bmiAbstractionProperty);

		// TEST Category <abstract> ?
		Question category = kb.getManager().searchQuestion("Category");
		Boolean categoryAbstractionProperty = category.getInfoStore().getValue(
				BasicProperties.ABSTRACTION_QUESTION);
		assertEquals("Question 'Category' isn't abstract.", Boolean.TRUE,
				categoryAbstractionProperty);
	}

	@Test
	public void testSetValues() {

		Question height = kb.getManager().searchQuestion("Height");
		Question weight = kb.getManager().searchQuestion("Weight");
		QuestionNum bmi = (QuestionNum) kb.getManager().searchQuestion("BMI");
		Question category = kb.getManager().searchQuestion("Category");

		// SET 'Height' = 1.7
		Value heightValue = new NumValue(1.7);
		session.getBlackboard().addValueFact(
				FactFactory.createFact(height, heightValue,
						PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		// TEST 'Height' == 1.7
		Value currentHeightValue = session.getBlackboard().getValue(height);
		assertEquals("Question 'Height' has wrong value", heightValue, currentHeightValue);

		// SET 'Weight' = 56.0
		Value weightValue = new NumValue(56.0);
		session.getBlackboard().addValueFact(
				FactFactory.createFact(weight, weightValue,
						PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		// TEST 'Weight' == 56.0
		Value currentWeightValue = session.getBlackboard().getValue(weight);
		assertEquals("Question 'Weight' has wrong value", weightValue, currentWeightValue);

		// TEST 'BMI' == 19.37716..
		NumValue currentBmiValue = (NumValue) session.getBlackboard().getValue(bmi);
		Double bmiValue = 56 / (1.7 * 1.7);
		assertEquals("Abstract Question 'BMI' has wrong value", bmiValue,
				currentBmiValue.getValue());

		// TEST 'Category' == 'Normal'
		Value normal = KnowledgeBaseUtils.findValue(category, "Normal");
		Value categoryValue = session.getBlackboard().getValue(category);
		assertEquals("Abstract Question 'Category' has wrong value", normal, categoryValue);
	}

	@Test
	public void testChangeValues() {
		testSetValues();

		Question height = kb.getManager().searchQuestion("Height");
		Question weight = kb.getManager().searchQuestion("Weight");
		QuestionNum bmi = (QuestionNum) kb.getManager().searchQuestion("BMI");
		Question category = kb.getManager().searchQuestion("Category");

		// SET 'Weight' = 80.0
		Value weightValue = new NumValue(80.0);
		session.getBlackboard().addValueFact(
				FactFactory.createFact(weight, weightValue,
						PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		// TEST 'Weight' == 80.0
		Value currentWeightValue = session.getBlackboard().getValue(weight);
		assertEquals("Question 'Weight' has wrong value", weightValue, currentWeightValue);

		/*
		 * TODO: The change of Weight isn't applied to the BMI Value
		 */

		// TEST 'BMI' == 27.68166..
		Value currentBmiValue = session.getBlackboard().getValue(bmi);
		Double bmiValue = 80 / (1.7 * 1.7);
		assertEquals("Abstract Question 'BMI' has wrong value", bmiValue,
				currentBmiValue.getValue());

		// TEST 'Category' == 'Overweight'
		Value overweight = KnowledgeBaseUtils.findValue(category, "Overweight");
		Value categoryValue = session.getBlackboard().getValue(category);
		assertEquals("Abstract Question 'Category' has wrong value", overweight, categoryValue);

		// SET 'Height' = 2.1
		Value heightValue = new NumValue(2.1);
		session.getBlackboard().addValueFact(
				FactFactory.createFact(height, heightValue,
						PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		// TEST 'Height' == 2.1
		Value currentHeightValue = session.getBlackboard().getValue(height);
		assertEquals("Question 'Height' has wrong value", heightValue, currentHeightValue);

		/*
		 * TODO: The change of Height isn't applied to the BMI Value
		 */

		// TEST 'BMI' == 18.14058..
		currentBmiValue = session.getBlackboard().getValue(bmi);
		bmiValue = 80 / (2.1 * 2.1);
		assertEquals("Abstract Question 'BMI' has wrong value", bmiValue,
				currentBmiValue.getValue());

		// TEST 'Category' == 'Overweight'
		Value underweight = KnowledgeBaseUtils.findValue(category, "Underweight");
		categoryValue = session.getBlackboard().getValue(category);
		assertEquals("Abstract Question 'Category' has wrong value", underweight, categoryValue);
	}

	@Test
	public void testSetUndefinedValue() {

		Question weight = kb.getManager().searchQuestion("Weight");
		Question height = kb.getManager().searchQuestion("Height");
		Question bmi = kb.getManager().searchQuestion("BMI");
		Question category = kb.getManager().searchQuestion("Category");

		// SET 'Weight' = 'UNDEFINED'
		session.getBlackboard().addValueFact(
				FactFactory.createFact(weight,
						UndefinedValue.getInstance(),
						PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		// TEST 'Weight' == 'UNDEFINED'
		Value weightValue = session.getBlackboard().getValue(weight);
		assertEquals("Question 'Weight' has wrong value", UndefinedValue.getInstance(), weightValue);

		// SET 'Height' = 'UNDEFINED'
		session.getBlackboard().addValueFact(
				FactFactory.createFact(height,
						UndefinedValue.getInstance(),
						PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		// TEST 'Height' == 'UNDEFINED'
		Value heightValue = session.getBlackboard().getValue(height);
		assertEquals("Question 'Height' has wrong value", UndefinedValue.getInstance(), heightValue);

		// TEST 'BMI' == 'UNDEFINED'
		Value bmiValue = session.getBlackboard().getValue(bmi);
		assertEquals("Abstract Question 'BMI' has wrong value", UndefinedValue.getInstance(),
				bmiValue);

		// TEST 'Category' == 'UNDEFINED'
		Value categoryValue = session.getBlackboard().getValue(category);
		assertEquals("Abstract Question 'Category' has wrong value", UndefinedValue.getInstance(),
				categoryValue);
	}

}
