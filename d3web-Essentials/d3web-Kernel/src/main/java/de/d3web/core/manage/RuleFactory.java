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
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

	public static Rule createContraIndicationRule(
			List<QASet> qaSets,
			Condition condition) {

		return createContraIndicationRule(qaSets, condition, null);
	}

	public static Rule createContraIndicationRule(
			List<QASet> qaSets,
			Condition condition,
			Condition exceptionCondition) {

		Rule rule = new Rule(PSMethodStrategic.class);

		ActionContraIndication theAction = new ActionContraIndication();
		theAction.setQASets(qaSets);

		setRuleParams(rule, theAction, condition, exceptionCondition);

		return rule;
	}

	public static void createContraIndicationRule(Question question,
			Condition condition) {
		createContraIndicationRule(
				Arrays.asList(new QASet[] { question }),
				condition);
	}

	public static Rule createHeuristicPSRule(
			Solution solution,
			Score score,
			Condition condition) {

		return createHeuristicPSRule(
				solution,
				score,
				condition,
				null);
	}

	public static Rule createRule(PSAction action, Condition condition, Condition exceptionCondition, Class<? extends PSMethodRulebased> psMethodContext) {
		Rule rule = new Rule(psMethodContext);
		setRuleParams(rule, action, condition, exceptionCondition);
		return rule;
	}

	public static Collection<Rule> createRules(List<PSAction> actions, Condition condition, Condition exceptionCondition, Class<? extends PSMethodRulebased> psMethodContext) {
		Set<Rule> result = new HashSet<>();
		for (PSAction action : actions) {
			result.add(createRule(action, condition, exceptionCondition, psMethodContext));
		}
		return result;
	}


	public static Rule createHeuristicPSRule(
			Solution solution,
			Score score,
			Condition condition,
			Condition exceptionCondition) {

		Rule rule = new Rule(PSMethodHeuristic.class);

		ActionHeuristicPS actionHeuristicPS = new ActionHeuristicPS();
		actionHeuristicPS.setSolution(solution);
		actionHeuristicPS.setScore(score);

		setRuleParams(rule, actionHeuristicPS, condition, exceptionCondition);
		return rule;
	}

	public static Rule createIndicationRule(
			List<QASet> qaSets,
			Condition condition) {

		return createIndicationRule(qaSets, condition, null);
	}

	public static Rule createIndicationRule(
			QASet qaSet,
			Condition condition) {
		List<QASet> ind = new LinkedList<>();
		ind.add(qaSet);
		return createIndicationRule(ind, condition, null);
	}

	public static Rule createIndicationRule(
			List<QASet> qaSets,
			Condition condition,
			Condition exceptionCondition) {

		Rule rule = new Rule(PSMethodStrategic.class);

		ActionNextQASet ruleAction = new ActionIndication();
		ruleAction.setQASets(qaSets);

		setRuleParams(rule, ruleAction, condition, exceptionCondition);
		return rule;
	}

	public static Rule createRelevantIndicationRule(QASet qaSet, Condition condition) {
		Rule rule = new Rule(PSMethodStrategic.class);

		ActionNextQASet ruleAction = new ActionRelevantIndication();
		ruleAction.setQASets(qaSet);

		setRuleParams(rule, ruleAction, condition, null);
		return rule;
	}

	public static Rule createStrategicRule(
			PSAction action,
			Condition condition) {
		return createStrategicRule(action, condition, null);
	}

	public static Rule createStrategicRule(
			PSAction action,
			Condition condition,
			Condition exceptionCondition) {

		Rule rule = new Rule(PSMethodStrategic.class);

		setRuleParams(rule, action, condition, exceptionCondition);
		return rule;
	}

	public static Rule createInstantIndicationRule(
			List<QASet> qaSets,
			Condition condition,
			Condition exceptionCondition) {

		Rule rule = new Rule(PSMethodStrategic.class);
		ActionNextQASet ruleAction = new ActionInstantIndication();
		ruleAction.setQASets(qaSets);
		setRuleParams(rule, ruleAction, condition, exceptionCondition);
		return rule;
	}

	public static Rule createInstantIndicationRule(
			List<QASet> qaSets,
			Condition condition) {

		return createInstantIndicationRule(qaSets, condition, null);
	}

	public static Rule createInstantIndicationRule(
			QASet qaSet,
			Condition condition) {
		List<QASet> ind = new ArrayList<>();
		ind.add(qaSet);
		return createInstantIndicationRule(ind, condition, null);
	}

	public static Rule createSetValueRule(
			Question question,
			Object value,
			Condition condition) {

		return createSetValueRule(
				question,
				value,
				condition,
				null);
	}

	public static Rule createSetValueRule(
			Question question,
			Object value,
			Condition condition,
			Condition exceptionCondition) {

		Rule rule = new Rule(PSMethodAbstraction.class);

		ActionSetQuestion theAction = new ActionSetQuestion();
		theAction.setQuestion(question);
		theAction.setValue(value);

		setRuleParams(rule, theAction, condition, exceptionCondition);

		return rule;
	}

	public static Rule createSetValueRule(
			Question question,
			FormulaElement valueFormula,
			Condition condition) {
		return createSetValueRule(
				question,
				valueFormula,
				condition,
				null);
	}

	public static Rule createSetValueRule(
			Question question,
			FormulaElement valueFormula,
			Condition condition,
			Condition exceptionCondition) {

		Rule rule = new Rule(PSMethodAbstraction.class);

		ActionSetQuestion theAction = new ActionSetQuestion();
		theAction.setQuestion(question);
		theAction.setValue(valueFormula);

		setRuleParams(rule, theAction, condition, exceptionCondition);

		return rule;
	}

	public static Rule createSuppressAnswerRule(
			QuestionChoice questionChoice,
			Choice[] choices,
			Condition condition) {

		return createSuppressAnswerRule(
				questionChoice,
				choices,
				condition,
				null);
	}

	public static Rule createSuppressAnswerRule(
			QuestionChoice questionChoice,
			Choice[] choices,
			Condition condition,
			Condition exceptionCondition) {

		Rule rule = new Rule(PSMethodStrategic.class);

		ActionSuppressAnswer theAction = new ActionSuppressAnswer();
		theAction.setQuestion(questionChoice);
		for (Choice choice : choices) {
			theAction.addSuppress(choice);
		}

		setRuleParams(rule, theAction, condition, exceptionCondition);

		return rule;
	}

	/**
	 * Sets the specified parameters to the specified rule.
	 */
	public static void setRuleParams(
			Rule rule,
			PSAction action,
			Condition condition,
			Condition exceptionCondition) {

		rule.setAction(action);
		rule.setCondition(condition);
		rule.setException(exceptionCondition);

	}

}