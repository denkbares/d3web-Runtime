/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.core.manage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.d3web.abstraction.ActionAddValue;
import de.d3web.abstraction.ActionSetValue;
import de.d3web.abstraction.formula.FormulaDateExpression;
import de.d3web.abstraction.formula.FormulaExpression;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.indication.ActionClarify;
import de.d3web.indication.ActionContraIndication;
import de.d3web.indication.ActionIndication;
import de.d3web.indication.ActionInstantIndication;
import de.d3web.indication.ActionNextQASet;
import de.d3web.indication.ActionRefine;
import de.d3web.indication.ActionSuppressAnswer;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.scoring.Score;
/**
 * Factory to create various rules via the
 * appropriate Factory-methods.
 * @author Joachim Baumeister
 */
public class RuleFactory {

	/**
	 * Creates a rule to set values for a given question 
	 * with the specified parameters.
	 */
	public static Rule createAddValueRule(
		String theId,
		Question theQuestion,
		Object[] theAnswers,
		Condition theCondition) {

		return createAddValueRule(
			theId,
			theQuestion,
			theAnswers,
			theCondition,
			null);
	}

	/**
	 * Creates a rule to add values to a given question 
	 * with the specified parameters.
	 */
	public static Rule createAddValueRule(
		String theId,
		Question theQuestion,
		Object[] theAnswers,
		Condition theCondition,
		Condition theRuleException) {

		Rule rule = createRule(theId);

		ActionAddValue theAction = new ActionAddValue();
		theAction.setQuestion(theQuestion);
		theAction.setValues(theAnswers);

		setRuleParams(rule, theAction, theCondition, theRuleException);

		return rule;
	}

	/**
	 * Creates a clarification-rule with the specified parameters.
	 */
	public static Rule createClarificationRule(
		String theId,
		List<QASet> theAction,
		Solution target,
		Condition theCondition) {

		return createClarificationRule(
			theId,
			theAction,
			target,
			theCondition,
			null);
	}

	/**
	 * Creates a Clarification-rule with the specified parameters.
	 */
	public static Rule createClarificationRule(
		String theId,
		List<QASet> theAction,
		Solution target,
		Condition theCondition,
		Condition theRuleException) {

		Rule rule = createRule(theId);

		ActionClarify ruleAction = new ActionClarify();
		ruleAction.setQASets(theAction);
		ruleAction.setTarget(target);

		setRuleParams(rule, ruleAction, theCondition, theRuleException);
		return rule;
	}

	/**
	 * Creates a contra-indication-rule with the specified parameters.
	 */
	public static Rule createContraIndicationRule(
		String theId,
		List<QASet> theQASets,
		Condition theCondition) {

		return createContraIndicationRule(theId, theQASets, theCondition, null);
	}

	/**
	 * Creates a contra-indication-rule with the specified parameters.
	 */
	public static Rule createContraIndicationRule(
		String theId,
		List<QASet> theQASets,
		Condition theCondition,
		Condition theRuleException) {

		Rule rule = createRule(theId);

		ActionContraIndication theAction = new ActionContraIndication();
		theAction.setQASets(theQASets);

		setRuleParams(rule, theAction, theCondition, theRuleException);

		return rule;
	}

	/**
	 * Creates a heuristic-rule with the specified parameters.
	 * @param String theId
	 * @param Solution theDiagnosisAction
	 * @param Score theDiagnosisScore
	 * @param Condition theCondition
	 */
	public static Rule createHeuristicPSRule(
		String theId,
		Solution theDiagnosisAction,
		Score theDiagnosisScore,
		Condition theCondition) {

		return createHeuristicPSRule(
			theId,
			theDiagnosisAction,
			theDiagnosisScore,
			theCondition,
			null);
	}
	
	public static Rule createRule(String theId, PSAction theAction, Condition theCondition, Condition theException, Condition theContext) {
		Rule rule = createRule(theId);
		setRuleParams(rule, theAction, theCondition, theException);
		rule.setContext(theContext);
		return rule;
	}

	/**
	 * Creates a heuristic-rule with the specified parameters.
	 */
	public static Rule createHeuristicPSRule(
		String theId,
		Solution theDiagnosisAction,
		Score theDiagnosisScore,
		Condition theCondition,
		Condition theRuleException) {

		Rule rule = createRule(theId);

		ActionHeuristicPS theAction = new ActionHeuristicPS();
		theAction.setDiagnosis(theDiagnosisAction);
		theAction.setScore(theDiagnosisScore);

		setRuleParams(rule, theAction, theCondition, theRuleException);
		return rule;
	}

	/**
	 * Creates an Indication-rule with the specified parameters.
	 * @param String theId
	 * @param List theAction
	 * @param Condition theCondition
	 */
	public static Rule createIndicationRule(
		String theId,
		List<QASet> theAction,
		Condition theCondition) {

		return createIndicationRule(theId, theAction, theCondition, null);
	}

	/**
	 * Creates an Indication-rule with the specified parameters.
	 * @param String theId
	 * @param QASet one single QASet to indicate
	 * @param Condition theCondition
	 */
	public static Rule createIndicationRule(
			String theId,
			QASet singleIndication,
			Condition theCondition) {
	    	List<QASet> ind = new LinkedList<QASet>();
	    	ind.add(singleIndication);
			return createIndicationRule(theId, ind, theCondition, null);
		}

	
	/**
	 * Creates a NextQASet-rule with the specified parameters.
	 */
	public static Rule createIndicationRule(
		String theId,
		List<QASet> theAction,
		Condition theCondition,
		Condition theRuleException) {

		Rule rule = createRule(theId);

		ActionNextQASet ruleAction = new ActionIndication();
		ruleAction.setQASets(theAction);

		setRuleParams(rule, ruleAction, theCondition, theRuleException);
		return rule;
	}
	
	/**
	 * Creates a rule, that instantly (directly after current qaset) 
	 * indicates the specified qasets.
	 */
	public static Rule createInstantIndicationRule(
			String id, 
			List<QASet> theAction, 
			Condition theCondition,
			Condition theRuleException) {
		
		Rule rule = createRule(id);
		ActionNextQASet ruleAction = new ActionInstantIndication();
		ruleAction.setQASets(theAction);
		setRuleParams(rule, ruleAction, theCondition, theRuleException);
		return rule;
	}

	public static Rule createInstantIndicationRule(
			String id, 
			List<QASet> theAction, 
			Condition theCondition) {
		
		return createInstantIndicationRule(id, theAction, theCondition, null);
	}

	
	/**
	 * Creates a rule, that instantly (directly after current qaset) 
	 * indicates the specified qasets.
	 */
	public static Rule createInstantIndicationRule(
			String theId,
			QASet singleIndication,
			Condition theCondition) {
	    	List<QASet> ind = new ArrayList<QASet>();
	    	ind.add(singleIndication);
			return createInstantIndicationRule(theId, ind, theCondition, null);
	}





	
	/**
	 * Creates a Refinement-rule with the specified parameters.
	 */
	public static Rule createRefinementRule(
		String theId,
		List<QASet> theAction,
		Solution target,
		Condition theCondition) {

		return createRefinementRule(
			theId,
			theAction,
			target,
			theCondition,
			null);
	}

	/**
	 * Creates a Refinement-rule with the specified parameters.
	 */
	public static Rule createRefinementRule(
		String theId,
		List<QASet> theAction,
		Solution target,
		Condition theCondition,
		Condition theRuleException) {

		Rule rule = createRule(theId);

		ActionRefine ruleAction = new ActionRefine();
		ruleAction.setQASets(theAction);
		ruleAction.setTarget(target);

		setRuleParams(rule, ruleAction, theCondition, theRuleException);
		return rule;
	}

	/**
	 * Creates a ruleComplex with the specified ID
	 */
	public static Rule createRule(String theId) {
		Rule rule = new Rule(theId);
		return rule;
	}

	/**
	 * Creates a rule to set values for a given question 
	 * with the specified parameters.
	 * @param String theId
	 * @param Question theQuestion
	 * @param Object[] theAnswers
	 * @param Condition theCondition
	 */
	public static Rule createSetValueRule(
		String theId,
		Question theQuestion,
		Object[] theAnswers,
		Condition theCondition) {

		return createSetValueRule(
			theId,
			theQuestion,
			theAnswers,
			theCondition,
			null);
	}

	/**
	 * Creates a rule to set values for a given question 
	 * with the specified parameters.
	 */
	public static Rule createSetValueRule(
		String theId,
		Question theQuestion,
		Object[] theAnswers,
		Condition theCondition,
		Condition theRuleException) {

		Rule rule = createRule(theId);

		ActionSetValue theAction = new ActionSetValue();
		theAction.setQuestion(theQuestion);
		theAction.setValues(theAnswers);

		setRuleParams(rule, theAction, theCondition, theRuleException);

		return rule;
	}

	/**
	 * Creates a rule to set values for a given question 
	 * with the specified parameters.
	 */
	public static Rule createSetValueRule(
		String theId,
		Question theQuestion,
		FormulaExpression theAnswer,
		Condition theCondition) {
		return createSetValueRule(
			theId,
			theQuestion,
			theAnswer,
			theCondition,
			null);
	}
	
	/**
	 * Creates a rule to set values for a given question 
	 * with the specified parameters.
	 */
	public static Rule createSetValueRule(
		String theId,
		Question theQuestion,
		FormulaDateExpression theAnswer,
		Condition theCondition) {
		return createSetValueRule(
			theId,
			theQuestion,
			theAnswer,
			theCondition,
			null);
	}	

	/**
	 * Creates a rule to set values for a given question 
	 * with the specified parameters.
	 */
	public static Rule createSetValueRule(
		String theId,
		Question theQuestion,
		FormulaExpression theAnswers,
		Condition theCondition,
		Condition theRuleException) {

		Rule rule = createRule(theId);

		ActionSetValue theAction = new ActionSetValue();
		theAction.setQuestion(theQuestion);
		theAction.setValues(new Object[] { theAnswers });

		setRuleParams(rule, theAction, theCondition, theRuleException);

		return rule;
	}
	

	/**
	 * Creates a rule to set values for a given question 
	 * with the specified parameters.
	 */
	public static Rule createSetValueRule(
		String theId,
		Question theQuestion,
		FormulaDateExpression theAnswers,
		Condition theCondition,
		Condition theRuleException) {

		Rule rule = createRule(theId);

		ActionSetValue theAction = new ActionSetValue();
		theAction.setQuestion(theQuestion);
		theAction.setValues(new Object[] { theAnswers });

		setRuleParams(rule, theAction, theCondition, theRuleException);

		return rule;
	}	

	/**
	 * Creates a rule to add values to a given question 
	 * with the specified parameters.
	 */
	public static Rule createSuppressAnswerRule(
		String theId,
		QuestionChoice theQuestion,
		AnswerChoice[] theAnswers,
		Condition theCondition) {

		return createSuppressAnswerRule(
			theId,
			theQuestion,
			theAnswers,
			theCondition,
			null);
	}

	/**
	 * Creates a rule to add values to a given question 
	 * with the specified parameters.
	 */
	public static Rule createSuppressAnswerRule(
		String theId,
		QuestionChoice theQuestion,
		AnswerChoice[] theAnswers,
		Condition theCondition,
		Condition theRuleException) {

		Rule rule = createRule(theId);

		ActionSuppressAnswer theAction = new ActionSuppressAnswer();
		theAction.setQuestion(theQuestion);
		theAction.setSuppress(theAnswers);

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