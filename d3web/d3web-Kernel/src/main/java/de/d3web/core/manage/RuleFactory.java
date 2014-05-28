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

package de.d3web.core.manage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.d3web.abstraction.ActionSetQuestion;
import de.d3web.abstraction.formula.FormulaElement;
import de.d3web.abstraction.inference.PSMethodAbstraction;
import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.PSMethodRulebased;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.indication.ActionContraIndication;
import de.d3web.indication.ActionIndication;
import de.d3web.indication.ActionInstantIndication;
import de.d3web.indication.ActionNextQASet;
import de.d3web.indication.ActionRelevantIndication;
import de.d3web.indication.ActionSuppressAnswer;
import de.d3web.indication.inference.PSMethodStrategic;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.scoring.Score;
import de.d3web.scoring.inference.PSMethodHeuristic;

/**
 * Factory to create various rules via the appropriate Factory-methods.
 * 
 * @author Joachim Baumeister
 */
public final class RuleFactory {

	private RuleFactory() { // enforce noninstantiability
	}

	/**
	 * Creates a contra-indication-rule with the specified parameters.
	 */
	public static Rule createContraIndicationRule(
			List<QASet> theQASets,
			Condition theCondition) {

		return createContraIndicationRule(theQASets, theCondition, null);
	}

	/**
	 * Creates a contra-indication-rule with the specified parameters.
	 */
	public static Rule createContraIndicationRule(
			List<QASet> theQASets,
			Condition theCondition,
			Condition theRuleException) {

		Rule rule = new Rule(PSMethodStrategic.class);

		ActionContraIndication theAction = new ActionContraIndication();
		theAction.setQASets(theQASets);

		setRuleParams(rule, theAction, theCondition, theRuleException);

		return rule;
	}

	public static void createContraIndicationRule(Question question,
			Condition theCondition) {
		createContraIndicationRule(
				Arrays.asList(new QASet[] { question }),
				theCondition);
	}

	/**
	 * Creates a heuristic-rule with the specified parameters.
	 * 
	 * @param String theId
	 * @param Solution theDiagnosisAction
	 * @param Score theDiagnosisScore
	 * @param Condition theCondition
	 */
	public static Rule createHeuristicPSRule(
			Solution theDiagnosisAction,
			Score theDiagnosisScore,
			Condition theCondition) {

		return createHeuristicPSRule(
				theDiagnosisAction,
				theDiagnosisScore,
				theCondition,
				null);
	}

	public static Rule createRule(PSAction theAction, Condition theCondition, Condition theException, Class<? extends PSMethodRulebased> psMethodContext) {
		Rule rule = new Rule(psMethodContext);
		setRuleParams(rule, theAction, theCondition, theException);
		return rule;
	}

	/**
	 * Creates a heuristic-rule with the specified parameters.
	 */
	public static Rule createHeuristicPSRule(
			Solution theDiagnosisAction,
			Score theDiagnosisScore,
			Condition theCondition,
			Condition theRuleException) {

		Rule rule = new Rule(PSMethodHeuristic.class);

		ActionHeuristicPS theAction = new ActionHeuristicPS();
		theAction.setSolution(theDiagnosisAction);
		theAction.setScore(theDiagnosisScore);

		setRuleParams(rule, theAction, theCondition, theRuleException);
		return rule;
	}

	/**
	 * Creates an Indication-rule with the specified parameters.
	 * 
	 * @param String theId
	 * @param List theAction
	 * @param Condition theCondition
	 */
	public static Rule createIndicationRule(
			List<QASet> theAction,
			Condition theCondition) {

		return createIndicationRule(theAction, theCondition, null);
	}

	/**
	 * Creates an Indication-rule with the specified parameters.
	 * 
	 * @param String theId
	 * @param QASet one single QASet to indicate
	 * @param Condition theCondition
	 */
	public static Rule createIndicationRule(
			QASet singleIndication,
			Condition theCondition) {
		List<QASet> ind = new LinkedList<QASet>();
		ind.add(singleIndication);
		return createIndicationRule(ind, theCondition, null);
	}

	/**
	 * Creates a standard indication rule with the specified parameters.
	 */
	public static Rule createIndicationRule(
			List<QASet> theAction,
			Condition theCondition,
			Condition theRuleException) {

		Rule rule = new Rule(PSMethodStrategic.class);

		ActionNextQASet ruleAction = new ActionIndication();
		ruleAction.setQASets(theAction);

		setRuleParams(rule, ruleAction, theCondition, theRuleException);
		return rule;
	}

	/**
	 * Creates a relevant indication rule with the specified parameters
	 * 
	 * @created 15.04.2013
	 * @param theAction the QASet being relevant indicated by the created rule
	 * @param theCondition the condition the rule evals
	 * @return the created Rule
	 */
	public static Rule createRelevantIndicationRule(QASet theAction, Condition theCondition) {
		Rule rule = new Rule(PSMethodStrategic.class);

		ActionNextQASet ruleAction = new ActionRelevantIndication();
		ruleAction.setQASets(theAction);

		setRuleParams(rule, ruleAction, theCondition, null);
		return rule;
	}

	/**
	 * Creates a rule with the specified action and condition
	 * 
	 */
	public static Rule createStrategicRule(
			PSAction theAction,
			Condition theCondition) {
		return createStrategicRule(theAction, theCondition, null);
	}

	/**
	 * Creates a rule with the specified action, condition and exception
	 * 
	 */
	public static Rule createStrategicRule(
			PSAction theAction,
			Condition theCondition,
			Condition theRuleException) {

		Rule rule = new Rule(PSMethodStrategic.class);

		setRuleParams(rule, theAction, theCondition, theRuleException);
		return rule;
	}

	/**
	 * Creates a rule, that instantly (directly after current qaset) indicates
	 * the specified qasets.
	 */
	public static Rule createInstantIndicationRule(
			List<QASet> theAction,
			Condition theCondition,
			Condition theRuleException) {

		Rule rule = new Rule(PSMethodStrategic.class);
		ActionNextQASet ruleAction = new ActionInstantIndication();
		ruleAction.setQASets(theAction);
		setRuleParams(rule, ruleAction, theCondition, theRuleException);
		return rule;
	}

	public static Rule createInstantIndicationRule(
			List<QASet> theAction,
			Condition theCondition) {

		return createInstantIndicationRule(theAction, theCondition, null);
	}

	/**
	 * Creates a rule, that instantly (directly after current qaset) indicates
	 * the specified qasets.
	 */
	public static Rule createInstantIndicationRule(
			QASet singleIndication,
			Condition theCondition) {
		List<QASet> ind = new ArrayList<QASet>();
		ind.add(singleIndication);
		return createInstantIndicationRule(ind, theCondition, null);
	}

	/**
	 * Creates a rule to set values for a given question with the specified
	 * parameters.
	 * 
	 * @param String theId
	 * @param Question theQuestion
	 * @param Object theValue
	 * @param Condition theCondition
	 */
	public static Rule createSetValueRule(
			Question theQuestion,
			Object theValue,
			Condition theCondition) {

		return createSetValueRule(
				theQuestion,
				theValue,
				theCondition,
				null);
	}

	/**
	 * Creates a rule to set values for a given question with the specified
	 * parameters.
	 */
	public static Rule createSetValueRule(
			Question theQuestion,
			Object theValue,
			Condition theCondition,
			Condition theRuleException) {

		Rule rule = new Rule(PSMethodAbstraction.class);

		ActionSetQuestion theAction = new ActionSetQuestion();
		theAction.setQuestion(theQuestion);
		theAction.setValue(theValue);

		setRuleParams(rule, theAction, theCondition, theRuleException);

		return rule;
	}

	/**
	 * Creates a rule to set values for a given question with the specified
	 * parameters.
	 */
	public static Rule createSetValueRule(
			Question theQuestion,
			FormulaElement theAnswer,
			Condition theCondition) {
		return createSetValueRule(
				theQuestion,
				theAnswer,
				theCondition,
				null);
	}

	/**
	 * Creates a rule to set values for a given question with the specified
	 * parameters.
	 */
	public static Rule createSetValueRule(
			Question theQuestion,
			FormulaElement theValue,
			Condition theCondition,
			Condition theRuleException) {

		Rule rule = new Rule(PSMethodAbstraction.class);

		ActionSetQuestion theAction = new ActionSetQuestion();
		theAction.setQuestion(theQuestion);
		theAction.setValue(theValue);

		setRuleParams(rule, theAction, theCondition, theRuleException);

		return rule;
	}

	/**
	 * Creates a rule to add values to a given question with the specified
	 * parameters.
	 */
	public static Rule createSuppressAnswerRule(
			QuestionChoice theQuestion,
			Choice[] theAnswers,
			Condition theCondition) {

		return createSuppressAnswerRule(
				theQuestion,
				theAnswers,
				theCondition,
				null);
	}

	/**
	 * Creates a rule to add values to a given question with the specified
	 * parameters.
	 */
	public static Rule createSuppressAnswerRule(
			QuestionChoice theQuestion,
			Choice[] theAnswers,
			Condition theCondition,
			Condition theRuleException) {

		Rule rule = new Rule(PSMethodStrategic.class);

		ActionSuppressAnswer theAction = new ActionSuppressAnswer();
		theAction.setQuestion(theQuestion);
		for (Choice choice : theAnswers) {
			theAction.addSuppress(choice);
		}

		setRuleParams(rule, theAction, theCondition, theRuleException);

		return rule;
	}

	/**
	 * Sets the specified parameters to the specified rule.
	 */
	public static void setRuleParams(
			Rule rule,
			PSAction theAction,
			Condition theCondition,
			Condition theRuleException) {

		rule.setAction(theAction);
		rule.setCondition(theCondition);
		rule.setException(theRuleException);

	}

}