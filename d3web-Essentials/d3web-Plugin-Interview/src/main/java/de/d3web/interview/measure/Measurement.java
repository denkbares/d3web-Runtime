/*
 * Copyright (C) 2016 denkbares GmbH. All rights reserved.
 */

package de.d3web.interview.measure;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.interview.Form;

import static de.d3web.core.manage.KnowledgeBaseUtils.Matching.ANY_PROMPT;

/**
 * Describes a measurement that may be executed during a running session. The measurement is a
 * property of a (potentially abstract) question that represents the measurement, because usually
 * there is some visual feedback on the user interface for the measurement, e.g. indicating the user
 * that a measurement is performed, or showing the measured value.
 * <p>
 * The measurement has a starting condition that must evaluate to true to start the measurement. The
 * user interface itself may decide to automatically start the measurement, or let it manually be
 * started through the user. In the latter case, the start condition is often some special answer of
 * the question this measurement instance is located at. Additionally the measurement has a stop
 * condition, so that the user interface should stop the measurement if that condition is true. The
 * stop condition might be the inverted start condition, but it not necessarily is (e.g. starting
 * requires some adaptation performed, where the measurement stops at some special measured value).
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 09.12.2016
 */
public class Measurement {

	/**
	 * Property for Questions that define a measurement attached to the question.
	 *
	 * @see Form#getMeasurements()
	 */
	public static final Property<Measurement> MEASUREMENT = Property.getProperty("measurement", Measurement.class);

	private final String identifier;
	private final Map<String, String> mapping = new HashMap<>();
	private final Condition startCondition;
	private final Condition stopCondition;

	public Measurement(String identifier, Map<String, String> mapping, Condition startCondition, Condition stopCondition) {
		this.identifier = identifier;
		this.startCondition = startCondition;
		this.stopCondition = stopCondition;
		if (mapping != null) this.mapping.putAll(mapping);
	}

	/**
	 * Some unique identifier for the measurement. The string often contains the configuration
	 * parameters of the measurement to be performed.
	 *
	 * @return the identifier and/or configuration string of the measurement
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * The condition which must be true before the measurement should be started.
	 *
	 * @return the starting condition
	 */
	public Condition getStartCondition() {
		return startCondition;
	}

	/**
	 * The condition when the (already running) measurement should be stopped at, as soon as the
	 * condition evaluates to false
	 *
	 * @return the condition when to stop the measurement
	 */
	public Condition getStopCondition() {
		return stopCondition;
	}

	/**
	 * Returns true if the starting condition is fulfilled for the specified session, or if no
	 * starting condition is set at all (that means the measurement can always be started). Note
	 * that the method does not consider if the measurement is already running or not.
	 *
	 * @param session the session to be checked
	 * @return if the measurement can be started
	 */
	public boolean canStart(Session session) {
		try {
			return startCondition == null || startCondition.eval(session);
		}
		catch (NoAnswerException | UnknownAnswerException e) {
			return false;
		}
	}

	/**
	 * Returns true if the stop condition is fulfilled for the specified session. If no condition is
	 * specified, the method returns false (in contrast to {@link #canStart(Session)}), because that
	 * means that the measurement should not be stopped automatically. Note that the method does not
	 * consider if the measurement is currently running or not.
	 *
	 * @param session the session to be checked
	 * @return if the measurement can be stopped
	 */
	public boolean canStop(Session session) {
		try {
			return stopCondition != null && stopCondition.eval(session);
		}
		catch (NoAnswerException | UnknownAnswerException e) {
			return false;
		}
	}

	/**
	 * Returns the mapping from measurand names to the actual question names to be measured. Note
	 * that the measurand names very much depends on the actual measurement implementation.
	 *
	 * @return the mapping from the measurand names to the question names
	 */
	public Map<String, String> getMapping() {
		return Collections.unmodifiableMap(mapping);
	}

	public void addMapping(String measurand, Question question) {
		mapping.put(measurand, question.getName());
	}

	/**
	 * The method applies a measured raw value to a question, by answering the question for the
	 * specified problem solver. The specified measurand is mapped to the actual question of the
	 * session's knowledge base. The raw value is mapped to an answer, according to the type of the
	 * question. The following value mappings are supported:
	 * <p>
	 * <table> <tr><th>rawValue</th><th>Question</th><th>Result</th></tr>
	 * <tr><td>null</td><td>any</td><td>the question will be undefined, by removing any previously
	 * measured value</td></tr> <tr><td>java.lang.Number</td><td>QuestionNum</td><td>the question
	 * will be answered with the numeric value</td></tr> <tr><td>java.lang.Object</td><td>QuestionText</td><td>the
	 * question will be answered with the string representation {@link #toString()} of the
	 * value</td></tr> <tr><td>java.lang.Date</td><td>QuestionDate</td><td>the question will be
	 * answered with the date value</td></tr> <tr><td>java.lang.String</td><td>QuestionChoice</td><td>the
	 * question will be answered with the choice denoted by the string, if there is a matching
	 * choice identifier or choice name of any language</td></tr> </table>
	 * <p>
	 * Any non-showed combination will result to unknown.
	 *
	 * @param session the session to apply the value to
	 * @param measurand the measured value identifier, will be mapped to a question
	 * @param rawValue the value that has been measured
	 */
	public void applyValue(Session session, String measurand, Object rawValue) {
		try {
			applyValueStrict(session, measurand, rawValue);
		}
		catch (IllegalArgumentException e) {
			applyValueStrict(session, measurand, Unknown.getInstance());
		}
	}

	/**
	 * The method applies a measured raw value to a question, by answering the question for the
	 * specified problem solver. The specified measurand is mapped to the actual question of the
	 * session's knowledge base. The raw value is mapped to an answer, according to the type of the
	 * question. The following value mappings are supported:
	 * <p>
	 * <table> <tr><th>rawValue</th><th>Question</th><th>Result</th></tr>
	 * <tr><td>null</td><td>any</td><td>the question will be undefined, by removing any previously
	 * measured value</td></tr> <tr><td>java.lang.Number</td><td>QuestionNum</td><td>the question
	 * will be answered with the numeric value</td></tr> <tr><td>java.lang.Object</td><td>QuestionText</td><td>the
	 * question will be answered with the string representation {@link #toString()} of the
	 * value</td></tr> <tr><td>java.lang.Date</td><td>QuestionDate</td><td>the question will be
	 * answered with the date value</td></tr> <tr><td>java.lang.String</td><td>QuestionChoice</td><td>the
	 * question will be answered with the choice denoted by the string, if there is a matching
	 * choice identifier or choice name of any language</td></tr> <tr><td>java.lang.Number</td><td>QuestionChoice</td><td>the
	 * question will be answered with the choice of the denoted index, where the first choice is "1"
	 * and "0" is "unknown".</td></tr> </table>
	 * <p>
	 * Any non-showed combination will result in an IllegalArgumentException.
	 *
	 * @param session the session to apply the value to
	 * @param measurand the measured value identifier, will be mapped to a question
	 * @param rawValue the value that has been measured
	 */
	public void applyValueStrict(Session session, String measurand, Object rawValue) {
		// check if we have a question for the measurand to be applied
		Question question = session.getKnowledgeBase().getManager()
				.searchQuestion(mapping.get(measurand));
		if (question == null) return;

		// check if we have a null value, then remove existing answer
		PSMethod solver = getPSMethod(session);
		if (rawValue == null) {
			session.getBlackboard().removeValueFact(question, solver);
		}

		// otherwise convert raw value to question value and set the value
		Value value = toValue(question, rawValue);
		Fact fact = FactFactory.createFact(question, value, solver, solver);
		session.getBlackboard().addValueFact(fact);
	}

	private PSMethod getPSMethod(Session session) {
		// query for measurement solver
		PSMethodMeasurement solver = session.getPSMethodInstance(PSMethodMeasurement.class);
		assert solver != null : "missing problem solver in session: PSMethodMeasurement";
		return solver;
	}

	private Value toValue(Question question, Object rawValue) {
		if (rawValue instanceof Value) return (Value) rawValue;
		if ((question instanceof QuestionChoice) && (rawValue instanceof String)) {
			String name = (String) rawValue;
			Choice choice = KnowledgeBaseUtils.findChoice((QuestionChoice) question, name, ANY_PROMPT);
			if (choice != null) return new ChoiceValue(choice);
			throw new IllegalArgumentException("no choice " + rawValue);
		}
		if ((question instanceof QuestionChoice) && (rawValue instanceof Number)) {
			int index = ((Number) rawValue).intValue();
			if (index == 0) return Unknown.getInstance();
			List<Choice> choices = ((QuestionChoice) question).getAllAlternatives();
			if (index >= 1 && index <= choices.size()) {
				return new ChoiceValue(choices.get(index - 1));
			}
			throw new IllegalArgumentException("no choice at index " + index);
		}
		if ((question instanceof QuestionNum) && (rawValue instanceof Number)) {
			return new NumValue(((Number) rawValue).doubleValue());
		}
		if ((question instanceof QuestionNum) && (rawValue instanceof String)) {
			// may throw an NumberFormatException which is also an IllegalArgumentException
			return new NumValue(Double.parseDouble((String) rawValue));
		}
		if (question instanceof QuestionText) {
			return new TextValue(String.valueOf(rawValue));
		}
		if ((question instanceof QuestionDate) && (rawValue instanceof Date)) {
			return new DateValue((Date) rawValue);
		}
		throw new IllegalArgumentException("cannot map measured value '" + rawValue + "' to question '" + question + "'");
	}
}
