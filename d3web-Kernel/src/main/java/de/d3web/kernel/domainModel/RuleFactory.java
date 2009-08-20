package de.d3web.kernel.domainModel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.d3web.kernel.domainModel.formula.FormulaDateExpression;
import de.d3web.kernel.domainModel.formula.FormulaExpression;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.psMethods.contraIndication.ActionContraIndication;
import de.d3web.kernel.psMethods.heuristic.ActionHeuristicPS;
import de.d3web.kernel.psMethods.nextQASet.ActionClarify;
import de.d3web.kernel.psMethods.nextQASet.ActionIndication;
import de.d3web.kernel.psMethods.nextQASet.ActionInstantIndication;
import de.d3web.kernel.psMethods.nextQASet.ActionNextQASet;
import de.d3web.kernel.psMethods.nextQASet.ActionRefine;
import de.d3web.kernel.psMethods.questionSetter.ActionAddValue;
import de.d3web.kernel.psMethods.questionSetter.ActionSetValue;
import de.d3web.kernel.psMethods.suppressAnswer.ActionSuppressAnswer;
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
	public static RuleComplex createAddValueRule(
		String theId,
		Question theQuestion,
		Object[] theAnswers,
		AbstractCondition theCondition) {

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
	public static RuleComplex createAddValueRule(
		String theId,
		Question theQuestion,
		Object[] theAnswers,
		AbstractCondition theCondition,
		AbstractCondition theRuleException) {

		RuleComplex rule = createRule(theId);

		ActionAddValue theAction = new ActionAddValue(rule);
		theAction.setQuestion(theQuestion);
		theAction.setValues(theAnswers);

		setRuleParams(rule, theAction, theCondition, theRuleException);

		return rule;
	}

	/**
	 * Creates a clarification-rule with the specified parameters.
	 */
	public static RuleComplex createClarificationRule(
		String theId,
		List theAction,
		Diagnosis target,
		AbstractCondition theCondition) {

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
	public static RuleComplex createClarificationRule(
		String theId,
		List theAction,
		Diagnosis target,
		AbstractCondition theCondition,
		AbstractCondition theRuleException) {

		RuleComplex rule = createRule(theId);

		ActionClarify ruleAction = new ActionClarify(rule);
		ruleAction.setQASets(theAction);
		ruleAction.setTarget(target);

		setRuleParams(rule, ruleAction, theCondition, theRuleException);
		return rule;
	}

	/**
	 * Creates a contra-indication-rule with the specified parameters.
	 */
	public static RuleComplex createContraIndicationRule(
		String theId,
		List theQASets,
		AbstractCondition theCondition) {

		return createContraIndicationRule(theId, theQASets, theCondition, null);
	}

	/**
	 * Creates a contra-indication-rule with the specified parameters.
	 */
	public static RuleComplex createContraIndicationRule(
		String theId,
		List theQASets,
		AbstractCondition theCondition,
		AbstractCondition theRuleException) {

		RuleComplex rule = createRule(theId);

		ActionContraIndication theAction = new ActionContraIndication(rule);
		theAction.setQASets(theQASets);

		setRuleParams(rule, theAction, theCondition, theRuleException);

		return rule;
	}

	/**
	 * Creates a heuristic-rule with the specified parameters.
	 * @param String theId
	 * @param Diagnosis theDiagnosisAction
	 * @param Score theDiagnosisScore
	 * @param AbstractCondition theCondition
	 */
	public static RuleComplex createHeuristicPSRule(
		String theId,
		Diagnosis theDiagnosisAction,
		Score theDiagnosisScore,
		AbstractCondition theCondition) {

		return createHeuristicPSRule(
			theId,
			theDiagnosisAction,
			theDiagnosisScore,
			theCondition,
			null);
	}

	/**
	 * Creates a heuristic-rule with the specified parameters.
	 */
	public static RuleComplex createHeuristicPSRule(
		String theId,
		Diagnosis theDiagnosisAction,
		Score theDiagnosisScore,
		AbstractCondition theCondition,
		AbstractCondition theRuleException) {

		RuleComplex rule = createRule(theId);

		ActionHeuristicPS theAction = new ActionHeuristicPS(rule);
		theAction.setDiagnosis(theDiagnosisAction);
		theAction.setScore(theDiagnosisScore);

		setRuleParams(rule, theAction, theCondition, theRuleException);
		return rule;
	}

	/**
	 * Creates an Indication-rule with the specified parameters.
	 * @param String theId
	 * @param List theAction
	 * @param AbstractCondition theCondition
	 */
	public static RuleComplex createIndicationRule(
		String theId,
		List theAction,
		AbstractCondition theCondition) {

		return createIndicationRule(theId, theAction, theCondition, null);
	}

	/**
	 * Creates an Indication-rule with the specified parameters.
	 * @param String theId
	 * @param QASet one single QASet to indicate
	 * @param AbstractCondition theCondition
	 */
	public static RuleComplex createIndicationRule(
			String theId,
			QASet singleIndication,
			AbstractCondition theCondition) {
	    	List ind = new LinkedList();
	    	ind.add(singleIndication);
			return createIndicationRule(theId, ind, theCondition, null);
		}

	
	/**
	 * Creates a NextQASet-rule with the specified parameters.
	 */
	public static RuleComplex createIndicationRule(
		String theId,
		List theAction,
		AbstractCondition theCondition,
		AbstractCondition theRuleException) {

		RuleComplex rule = createRule(theId);

		ActionNextQASet ruleAction = new ActionIndication(rule);
		ruleAction.setQASets(theAction);

		setRuleParams(rule, ruleAction, theCondition, theRuleException);
		return rule;
	}
	
	/**
	 * Creates a rule, that instantly (directly after current qaset) 
	 * indicates the specified qasets.
	 */
	public static RuleComplex createInstantIndicationRule(
			String id, 
			List theAction, 
			AbstractCondition theCondition,
			AbstractCondition theRuleException) {
		
		RuleComplex rule = createRule(id);
		ActionNextQASet ruleAction = new ActionInstantIndication(rule);
		ruleAction.setQASets(theAction);
		setRuleParams(rule, ruleAction, theCondition, theRuleException);
		return rule;
	}

	public static RuleComplex createInstantIndicationRule(
			String id, 
			List theAction, 
			AbstractCondition theCondition) {
		
		return createInstantIndicationRule(id, theAction, theCondition, null);
	}

	
	/**
	 * Creates a rule, that instantly (directly after current qaset) 
	 * indicates the specified qasets.
	 */
	public static RuleComplex createInstantIndicationRule(
			String theId,
			QASet singleIndication,
			AbstractCondition theCondition) {
	    	List ind = new ArrayList<QASet>();
	    	ind.add(singleIndication);
			return createInstantIndicationRule(theId, ind, theCondition, null);
	}





	
	/**
	 * Creates a Refinement-rule with the specified parameters.
	 */
	public static RuleComplex createRefinementRule(
		String theId,
		List theAction,
		Diagnosis target,
		AbstractCondition theCondition) {

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
	public static RuleComplex createRefinementRule(
		String theId,
		List theAction,
		Diagnosis target,
		AbstractCondition theCondition,
		AbstractCondition theRuleException) {

		RuleComplex rule = createRule(theId);

		ActionRefine ruleAction = new ActionRefine(rule);
		ruleAction.setQASets(theAction);
		ruleAction.setTarget(target);

		setRuleParams(rule, ruleAction, theCondition, theRuleException);
		return rule;
	}

	/**
	 * Creates a ruleComplex with the specified ID
	 */
	public static RuleComplex createRule(String theId) {

		RuleComplex rule = new RuleComplex();
		rule.setId(theId);
		return rule;
	}

	/**
	 * Creates a rule to set values for a given question 
	 * with the specified parameters.
	 * @param String theId
	 * @param Question theQuestion
	 * @param Object[] theAnswers
	 * @param AbstractCondition theCondition
	 */
	public static RuleComplex createSetValueRule(
		String theId,
		Question theQuestion,
		Object[] theAnswers,
		AbstractCondition theCondition) {

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
	public static RuleComplex createSetValueRule(
		String theId,
		Question theQuestion,
		Object[] theAnswers,
		AbstractCondition theCondition,
		AbstractCondition theRuleException) {

		RuleComplex rule = createRule(theId);

		ActionSetValue theAction = new ActionSetValue(rule);
		theAction.setQuestion(theQuestion);
		theAction.setValues(theAnswers);

		setRuleParams(rule, theAction, theCondition, theRuleException);

		return rule;
	}

	/**
	 * Creates a rule to set values for a given question 
	 * with the specified parameters.
	 */
	public static RuleComplex createSetValueRule(
		String theId,
		Question theQuestion,
		FormulaExpression theAnswer,
		AbstractCondition theCondition) {
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
	public static RuleComplex createSetValueRule(
		String theId,
		Question theQuestion,
		FormulaDateExpression theAnswer,
		AbstractCondition theCondition) {
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
	public static RuleComplex createSetValueRule(
		String theId,
		Question theQuestion,
		FormulaExpression theAnswers,
		AbstractCondition theCondition,
		AbstractCondition theRuleException) {

		RuleComplex rule = createRule(theId);

		ActionSetValue theAction = new ActionSetValue(rule);
		theAction.setQuestion(theQuestion);
		theAction.setValues(new Object[] { theAnswers });

		setRuleParams(rule, theAction, theCondition, theRuleException);

		return rule;
	}
	

	/**
	 * Creates a rule to set values for a given question 
	 * with the specified parameters.
	 */
	public static RuleComplex createSetValueRule(
		String theId,
		Question theQuestion,
		FormulaDateExpression theAnswers,
		AbstractCondition theCondition,
		AbstractCondition theRuleException) {

		RuleComplex rule = createRule(theId);

		ActionSetValue theAction = new ActionSetValue(rule);
		theAction.setQuestion(theQuestion);
		theAction.setValues(new Object[] { theAnswers });

		setRuleParams(rule, theAction, theCondition, theRuleException);

		return rule;
	}	

	/**
	 * Creates a rule to add values to a given question 
	 * with the specified parameters.
	 */
	public static RuleComplex createSuppressAnswerRule(
		String theId,
		QuestionChoice theQuestion,
		Object[] theAnswers,
		AbstractCondition theCondition) {

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
	public static RuleComplex createSuppressAnswerRule(
		String theId,
		QuestionChoice theQuestion,
		Object[] theAnswers,
		AbstractCondition theCondition,
		AbstractCondition theRuleException) {

		RuleComplex rule = createRule(theId);

		ActionSuppressAnswer theAction = new ActionSuppressAnswer(rule);
		theAction.setQuestion(theQuestion);
		theAction.setSuppress(theAnswers);

		setRuleParams(rule, theAction, theCondition, theRuleException);

		return rule;
	}

	/**
	 * Sets the specified parameters to the specified rule.
	 */
	public static void setRuleParams(
		RuleComplex rule,
		RuleAction theAction,
		AbstractCondition theCondition,
		AbstractCondition theRuleException) {

		rule.setAction(theAction);
		rule.setCondition(theCondition);
		rule.setException(theRuleException);

	}

}