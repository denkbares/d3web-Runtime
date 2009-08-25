package de.d3web.kernel.psMethods.setCovering;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.NumericalInterval;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondNumEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondNumIn;
import de.d3web.kernel.domainModel.ruleCondition.CondOr;

/**
 * This is a factory class for all SCNode types
 * 
 * @author bates
 */
public class SCNodeFactory {

	/**
	 * Creates an observable finding from question and answers
	 * 
	 * @param question
	 * @param answers
	 * @return ovserbable finding
	 */
	public static ObservableFinding createObservableFinding(Question question, Object[] answers) {
		// type check
		if ((question == null) || (answers == null)) {
			throw new IllegalArgumentException(
					"Neither the question nor the answers should be null!");
		} else {
			ObservableFinding ret = new ObservableFinding();
			ret.setNamedObject(question);
			for (int i = 0; i < answers.length; ++i) {
				Answer ans = (Answer) answers[i];
				ans.setQuestion(question);
			}
			ret.setAnswers(answers);
			return ret;
		}
	}

	/**
	 * Creates a SCDiagnosis with default apriori-probability
	 * 
	 * @param diagnosis
	 *            the corresponding diagnosis
	 */
	public static SCDiagnosis createSCDiagnosis(Diagnosis diagnosis) {
		return createSCDiagnosis(diagnosis, 1);
	}

	/**
	 * creates a SCDiagnosis with the given apriori-probability
	 * 
	 * @param diagnosis
	 *            corresponding diagnosis
	 * @param aprioriProbability
	 */
	public static SCDiagnosis createSCDiagnosis(Diagnosis diagnosis, double aprioriProbability) {
		// type check
		if (diagnosis == null) {
			throw new IllegalArgumentException("Diagnosis must not be null");
		}
		// creating the node
		SCDiagnosis ret = new SCDiagnosis();
		ret.setNamedObject(diagnosis);
		return ret;
	}

	/**
	 * creates a finding with the given condition. The Question will be taken
	 * from the condition
	 * 
	 * @param question
	 *            corresponding question
	 * @param condition
	 *            Condition observed findings may fulfill
	 */
	public static PredictedFinding createPredictedFinding(AbstractCondition condition) {
		// type check
		if (condition == null) {
			throw new IllegalArgumentException("The condition must not be null!");
		} else {
			PredictedFinding ret = new PredictedFinding();
			ret.setCondition(condition);
			return ret;
		}
	}

	/**
	 * creates a finding with the given question and condition
	 * 
	 * @param question
	 *            corresponding question
	 * @param condition
	 *            Condition observed findings may fulfill
	 */
	public static PredictedFinding createPredictedFinding(Question question,
			AbstractCondition condition) {
		// type check
		if ((question == null) || (condition == null)) {
			throw new IllegalArgumentException(
					"Neither the question nor the condition should be null!");
		} else {
			PredictedFinding ret = new PredictedFinding();
			ret.setNamedObject(question);
			ret.setCondition(condition);
			return ret;
		}
	}

	public static PredictedFinding createFindingEquals(Question question, Object answer) {
		if (answer == null) {
			return createFindingEquals(question, null);
		} else {
			return createFindingEquals(question, new Object[]{answer});
		}

	}

	public static PredictedFinding createFindingNumEquals(QuestionNum question, Double answer) {
		if ((question == null) || (answer == null)) {
			throw new IllegalArgumentException(
					"Neither the question nor the answer should be null!");
		}
		PredictedFinding ret = new PredictedFinding();
		CondNumEqual condition = new CondNumEqual(question, answer);
		ret.setCondition(condition);
		return ret;
	}

	public static PredictedFinding createFindingEquals(Question question, Object[] answers) {
		if ((question == null) || (answers == null)) {
			throw new IllegalArgumentException(
					"Neither the question nor the answers should be null!");
		}

		PredictedFinding ret = new PredictedFinding();
		CondEqual condition = new CondEqual(question, Arrays.asList(answers));
		ret.setCondition(condition);
		return ret;
	}

	/**
	 * Creates a multiple-choice finding with the given question and answers
	 * which is a PredictedFinding with OR-Condition
	 * 
	 * @param question
	 *            corresponding question
	 * @param answers
	 *            value of question
	 */
	public static PredictedFinding createFindingOR(QuestionChoice question, Object[] answers) {
		if ((question == null) || (answers == null)) {
			throw new IllegalArgumentException(
					"Neither the question nor the answers should be null!");
		}

		PredictedFinding ret = new PredictedFinding();
		List items = new LinkedList();
		for (int i = 0; i < answers.length; ++i) {
			items.add(new CondEqual(question, (Answer) answers[i]));
		}
		CondOr condition = new CondOr(items);
		ret.setCondition(condition);

		return ret;
	}

	/**
	 * Creates an AND-multiple-choice finding with the given question and
	 * answers which is a PredictedFinding with EQUALS-Condition (contains-all)
	 * 
	 * @param question
	 *            corresponding question
	 * @param answers
	 *            value of question
	 */
	public static PredictedFinding createFindingAND(QuestionChoice question, Object[] answers) {
		if ((question == null) || (answers == null)) {
			throw new IllegalArgumentException(
					"Neither the question nor the answers should be null!");
		}

		PredictedFinding ret = new PredictedFinding();
		CondEqual condition = new CondEqual(question, Arrays.asList(answers));
		ret.setCondition(condition);

		return ret;
	}

	/**
	 * creates a numerical finding with the given question, answers and a
	 * NumericalInterval for covering check.
	 * 
	 * @param question
	 *            corresponding question
	 * @param interval
	 *            NumericalInterval for covering check
	 * 
	 */
	public static PredictedFinding createFindingNum(QuestionNum questionNum,
			NumericalInterval interval) {
		// type check
		if ((questionNum == null) || (interval == null)) {
			throw new IllegalArgumentException(
					"Neither the question nor the interval should be null!");
		}

		PredictedFinding ret = new PredictedFinding();
		CondNumIn condition = new CondNumIn(questionNum, interval);
		ret.setCondition(condition);

		return ret;
	}

}
