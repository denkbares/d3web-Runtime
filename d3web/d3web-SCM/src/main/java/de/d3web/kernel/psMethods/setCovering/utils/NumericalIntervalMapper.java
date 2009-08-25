package de.d3web.kernel.psMethods.setCovering.utils;

import java.util.HashMap;
import java.util.Map;

import de.d3web.kernel.domainModel.NumericalInterval;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerFactory;

/**
 * This Mapper translates NumericalIntervals into AnswerChoices and back. It is
 * used for transformation.
 * 
 * @author bruemmer
 */
public class NumericalIntervalMapper {

	private Map intervalByAnswerChoice = null;
	private Map answerChoiceByInterval = null;

	private static NumericalIntervalMapper instance = null;

	private NumericalIntervalMapper() {
		intervalByAnswerChoice = new HashMap();
		answerChoiceByInterval = new HashMap();
	}

	public static NumericalIntervalMapper getInstance() {
		if (instance == null) {
			instance = new NumericalIntervalMapper();
		}
		return instance;
	}

	public void putInterval(NumericalInterval interval) {
		map(interval);
	}

	public AnswerChoice map(NumericalInterval interval) {
		AnswerChoice ans = (AnswerChoice) answerChoiceByInterval.get(interval);
		if (ans == null) {
			ans = AnswerFactory.createAnswerChoice(interval.toString(), interval.toString());
			answerChoiceByInterval.put(interval, ans);
			intervalByAnswerChoice.put(ans, interval);
		}
		return ans;
	}

	public NumericalInterval map(AnswerChoice answer) {
		return (NumericalInterval) intervalByAnswerChoice.get(answer);
	}

}
