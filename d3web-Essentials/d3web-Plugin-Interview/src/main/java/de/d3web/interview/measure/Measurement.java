/*
 * Copyright (C) 2016 denkbares GmbH. All rights reserved.
 */

package de.d3web.interview.measure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.denkbares.strings.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.TerminologyManager;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.Value;
import de.d3web.core.session.ValueUtils;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.core.session.protocol.ActualQContainerEntry;
import de.d3web.core.session.protocol.FactProtocolEntry;
import de.d3web.core.session.protocol.MeasurementStartProtocolEntry;
import de.d3web.core.session.protocol.Protocol;
import de.d3web.core.session.protocol.ProtocolEntry;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.interview.Form;

import static de.d3web.core.manage.KnowledgeBaseUtils.Matching.ANY_PROMPT;

/**
 * Describes a measurement that may be executed during a running session. The measurement is a property of a
 * (potentially abstract) question that represents the measurement, because usually there is some visual feedback on the
 * user interface for the measurement, e.g. indicating the user that a measurement is performed, or showing the measured
 * value.
 * <p>
 * The measurement has a starting condition that must evaluate to true to start the measurement. The user interface
 * itself may decide to automatically start the measurement, or let it manually be started through the user. In the
 * latter case, the start condition is often some special answer of the question this measurement instance is located
 * at. Additionally the measurement has a stop condition, so that the user interface should stop the measurement if that
 * condition is true. The stop condition might be the inverted start condition, but it not necessarily is (e.g. starting
 * requires some adaptation performed, where the measurement stops at some special measured value).
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 09.12.2016
 */
public class Measurement implements SessionObjectSource<Measurement.MeasurementSessionObject> {
	private static final Logger LOGGER = LoggerFactory.getLogger(Measurement.class);

	/**
	 * Property for Questions that define a measurement attached to the question or a knowledge base.
	 *
	 * @see Form#getMeasurements()
	 */
	public static final Property<Measurement> MEASUREMENT = Property.getProperty("measurement", Measurement.class);

	public enum State {
		/**
		 * Measurement was started (and is ongoing), but no actual value has been received yet
		 */
		STARTING,
		/**
		 * Measurement is ongoing normally
		 */
		MEASURING,
		/**
		 * Measurement is ongoing, but in the process of stopping (e.g. before final values like peaks are set)
		 */
		STOPPING,
		/**
		 * Measurement is ongoing, but failed. Failed measurements still need to be stopped!
		 */
		FAILED,
		/**
		 * No measurement is ongoing
		 */
		NOT_MEASURING
	}

	public static class MeasurementSessionObject implements SessionObject {
		private static final Logger LOGGER = LoggerFactory.getLogger(MeasurementSessionObject.class);
		private State state = State.NOT_MEASURING;
		private Date started = new Date();

		public MeasurementInfo getMeasurementInfo() {
			return new MeasurementInfo(state, started, -1);
		}

		public void setState(State state) {
			// we can only start, when we are not not measuring
			if (this.state == State.NOT_MEASURING && state == State.STARTING) {
				started = new Date();
				this.state = state;
			}
			// we can only measure, when we are started
			else if (this.state == State.STARTING && state == State.MEASURING) {
				this.state = state;
			}
			// we can only stop or fail when we are measuring or starting or stopping or failed
			else if ((this.state == State.MEASURING || this.state == State.STARTING || this.state == State.STOPPING || this.state == State.FAILED)
					&& (state == State.STOPPING || state == State.FAILED)) {
				this.state = state;
			}

			// we can always stop!
			else if (state == State.NOT_MEASURING) {
				this.state = state;
			}
			else if (this.state == state) {
				// nothing to do
			}
			else {
				LOGGER.error("Unhandled measurement state change: " + this.state + " -> " + state);
			}
		}

		public State getState() {
			return state;
		}

		public Date getStarted() {
			return started;
		}
	}

	@Nullable
	public static Measurement getMeasurement(@Nullable NamedObject object) {
		if (object == null) return null;
		return object.getInfoStore().getValue(MEASUREMENT);
	}

	public static boolean hasMeasurement(@Nullable NamedObject object) {
		if (object == null) return false;
		return getMeasurement(object) != null;
	}

	private final String identifier;
	private final Map<String, String> mapping = new HashMap<>();
	private final Condition startCondition;
	private final Condition stopCondition;

	public Measurement(String identifier, Condition startCondition, Condition stopCondition) {
		this(identifier, null, startCondition, stopCondition);
	}

	public Measurement(String identifier, Map<String, String> mapping, Condition startCondition, Condition stopCondition) {
		this.identifier = identifier;
		this.startCondition = startCondition;
		this.stopCondition = stopCondition;
		if (mapping != null) {
			this.mapping.putAll(mapping);
		}
	}

	@Override
	public MeasurementSessionObject createSessionObject(Session session) {
		return new MeasurementSessionObject();
	}

	/**
	 * Some unique identifier for the measurement. The string often contains the configuration parameters of the
	 * measurement to be performed.
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
	 * The condition when the (already running) measurement should be stopped at, as soon as the condition evaluates to
	 * false
	 *
	 * @return the condition when to stop the measurement
	 */
	public Condition getStopCondition() {
		return stopCondition;
	}

	/**
	 * To be called when the measurement is started.
	 *
	 * @param session the running session
	 */
	public void start(Session session) {
		setState(session, State.STARTING);
	}

	private void setState(Session session, State measuring) {
		//noinspection SynchronizationOnLocalVariableOrMethodParameter
		synchronized (session) {
			session.getSessionObject(this).setState(measuring);
		}
	}

	/**
	 * To be called when the measurement is stopped.
	 *
	 * @param session the running session
	 */
	public void stop(Session session) {
		setState(session, State.NOT_MEASURING);
	}

	/**
	 * To be called when the measurement is in error.
	 *
	 * @param session the running session
	 */
	public void failure(Session session) {
		setState(session, State.FAILED);
	}

	/**
	 * Returns relevant measurement variables for a given session that may be relevant
	 *
	 * @return Map of measurement variable values, or an empty map if none are available
	 */
	public Map<String, Object> getMeasurementVariables(Session session) {
		return Collections.emptyMap();
	}

	/**
	 * Returns true if the measurement only maps to questions where each of them already have a valid answer, either
	 * comes from a problem solver of type {@link PSMethod.Type#source}, or if the values are applied by this
	 * measurement.
	 * <p>
	 * Note that 'unknown' is assumed to be a valid answer if the unknown fact is applied by a 'source' problem solver.
	 *
	 * @param session the session to detect
	 * @return true if all mapped questions are already answered
	 */
	public boolean isFullyAnswered(Session session) {
		TerminologyManager manager = session.getKnowledgeBase().getManager();
		for (String questionName : mapping.values()) {
			Question question = manager.searchQuestion(questionName);
			if ((question != null) && !isAnswered(session, question)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns true if the starting condition is fulfilled for the specified session, or if no starting condition is set
	 * at all (that means the measurement can always be started). Note that the method does not consider if the
	 * measurement is already running or not.
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
	 * Returns true if the stop condition is fulfilled for the specified session. If no condition is specified, the
	 * method returns false (in contrast to {@link #canStart(Session)}), because that means that the measurement should
	 * not be stopped automatically. Note that the method does not consider if the measurement is currently running or
	 * not.
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
	 * Returns the mapping from measurand names to the actual question names to be measured. Note that the measurand
	 * names very much depends on the actual measurement implementation.
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
	 * The method applies measured raw values to a set of question, by answering the question for the specified problem
	 * solver. The specified measurands are mapped to the actual questions of the session's knowledge base. The raw
	 * values are mapped to an answer, according to the type of the question. The following value mappings are
	 * supported:
	 *
	 * <table> <tr><th>rawValue</th><th>Question</th><th>Result</th></tr>
	 * <tr><td>null</td><td>any</td><td>the question will be undefined, by removing any previously
	 * measured value</td></tr> <tr><td>java.lang.Number</td><td>QuestionNum</td><td>the question will be answered with
	 * the numeric value</td></tr> <tr><td>java.lang.Object</td><td>QuestionText</td><td>the question will be answered
	 * with the string representation {@link #toString()} of the value</td></tr> <tr><td>java.lang.Date</td><td>QuestionDate</td><td>the
	 * question will be answered with the date value</td></tr> <tr><td>java.lang.String</td><td>QuestionChoice</td><td>the
	 * question will be answered with the choice denoted by the string, if there is a matching choice identifier or
	 * choice name of any language</td></tr> <tr><td>de.d3web.core.session.Value</td><td>Question</td><td>the question
	 * will be answered with the specified value. The value must be compatible to the question mapped by the measurand,
	 * otherwise the value is rejected, e.g. a TextValue cannot be applied to a choice question.</td></tr> </table>
	 * <p>
	 * Any unlisted combination will result in unknown. Especially when the {@link Unknown} singleton instance is
	 * specified as a raw value, it will apply 'unknown' to the question.
	 *
	 * @param session the session to apply the value to
	 * @param values  a map of measurands to values to set in the knowledge base
	 * @param time    the propagation time with which the values should be applied to the session
	 * @return the value fact that has been applied, also if unknown is applied due to an incompatible value, or null if
	 * nothing is applied or a fact has been removed
	 */
	public Collection<Fact> applyValues(Session session, Map<String, Object> values, long time) {
		setState(session, State.MEASURING);
		//noinspection SynchronizationOnLocalVariableOrMethodParameter
		synchronized (session) {
			session.getPropagationManager().openPropagation(time);
			try {
				return applyValuesStrict(session, values);
			}
			catch (IllegalArgumentException ex) {
				return applyValuesStrict(session, values.entrySet()
						.stream()
						.collect(Collectors.toMap(Map.Entry::getKey, e -> Unknown.getInstance())));
			}
			catch (Throwable e) {
				LOGGER.error("Applying measured readings threw an exception.", e);
			}
			finally {
				session.getPropagationManager().commitPropagation();
			}
		}
		return Collections.emptyList();
	}

	@SuppressWarnings("unused")
	public Collection<Fact> applyValuesStrict(Session session, Map<String, Object> values) {
		final List<Fact> facts = new ArrayList<>();

		for (Map.Entry<String, Object> value : values.entrySet()) {
			final Fact f = applyValueStrict(session, value.getKey(), value.getValue());
			if (f != null) facts.add(f);
		}

		return facts;
	}

	protected Fact applyValue(Session session, String measurand, Object rawValue) {
		try {
			return applyValueStrict(session, measurand, rawValue);
		}
		catch (IllegalArgumentException e) {
			return applyValueStrict(session, measurand, Unknown.getInstance());
		}
	}

	@Nullable
	protected Fact applyValueStrict(Session session, String measurand, Object rawValue) {
		// check if we have a question for the measurand to be applied
		Question question = session.getKnowledgeBase().getManager()
				.searchQuestion(mapping.get(measurand));
		if (question == null) {
			return null;
		}

		if (rawValue == null) {
			// if we have a null value, then remove existing answer
			removeFact(session, question);
			return null;
		}
		else {
			// otherwise convert raw value to question value and set the value
			Value value = toValue(question, rawValue);
			return addFact(session, question, value);
		}
	}

	/**
	 * Method to be called to add a fact for a measured value to a mapped question. The method may be overwritten to add
	 * special facts or fact sources/solvers.
	 *
	 * @param session  the session to set the fact in
	 * @param question the question to be set
	 * @param value    the measured value to be set
	 * @return the fact that has been added
	 * @see #removeFact(Session, Question)
	 */
	protected Fact addFact(Session session, Question question, Value value) {
		Fact fact = createFact(session, question, value);
		LOGGER.debug("Applying measurement fact " + fact.getTerminologyObject().getName() + " = " + fact.getValue());
		cleanUpProtocol(session, fact);
		session.getBlackboard().addValueFact(fact);
		session.touch(new Date(session.getPropagationManager().getPropagationTime()));
		return fact;
	}

	@NotNull
	protected Fact createFact(Session session, Question question, Value value) {
		PSMethod solver = getPSMethod(session);
		return FactFactory.createFact(question, value, solver, solver);
	}

	/**
	 * Assure that only the most recent measurement fact remains in the protocol.
	 */
	private void cleanUpProtocol(Session session, Fact fact) {
		Protocol protocol = session.getProtocol();
		List<ProtocolEntry> protocolHistory = protocol.getProtocolHistory();
		for (int i = protocolHistory.size() - 1; i >= 0; i--) {
			ProtocolEntry lastEntry = protocolHistory.get(i);
			if (lastEntry instanceof ActualQContainerEntry) return;
			if (lastEntry instanceof MeasurementStartProtocolEntry mStartEntry) {
				String questionName = mapping.get(mStartEntry.getQuestionName());
				if (questionName == null) continue;
				if (questionName.equals(fact.getTerminologyObject().getName())) {
					return;
				}
			}
			if (!(lastEntry instanceof FactProtocolEntry) && !(lastEntry instanceof MeasurementStartProtocolEntry)) {
				continue;
			}
			if (lastEntry instanceof FactProtocolEntry lastFactEntry) {
				if (lastFactEntry.getTerminologyObjectName().equals(fact.getTerminologyObject().getName())
						&& lastFactEntry.getSolvingMethodClassName().equals(fact.getPSMethod().getClass().getName())) {
					protocol.removeEntry(lastFactEntry);
				}
			}
			// do not remove anything from older measures, so stop when reaching the start of this measurement
			else if (((MeasurementStartProtocolEntry) lastEntry).getQuestionName()
					.contains(fact.getTerminologyObject().getName())) {
				break;
			}
		}
	}

	/**
	 * Method to be called to remove a fact for a measured value to a mapped question. The method may be overwritten to
	 * remove special facts the previously have been added by {@link #addFact(Session, Question, Value)}.
	 *
	 * @param session  the session to set the fact in
	 * @param question the question to be set
	 * @see #addFact(Session, Question, Value)
	 */
	protected void removeFact(Session session, Question question) {
		LOGGER.debug("Removing measurement fact of question " + question.getName());
		session.getBlackboard().removeValueFact(question, getPSMethod(session));
		session.touch(new Date(session.getPropagationManager().getPropagationTime()));
	}

	/**
	 * Method to be called to check, if the specified question already has a valid answer, either comes from a problem
	 * solver of type {@link PSMethod.Type#source}, or if the value is applied by this measurement.
	 * <p>
	 * Note that 'unknown' is assumed to be a valid answer if the unknown fact is applied by a 'source' problem solver.
	 * <p>
	 * The method may be overwritten to handle special facts the previously have been added by {@link #addFact(Session,
	 * Question, Value)}.
	 *
	 * @param session the session to detect
	 * @return true if the question is already answered
	 */
	protected boolean isAnswered(Session session, Question question) {
		Fact fact = session.getBlackboard().getValueFact(question);
		return (fact != null) && fact.getPSMethod().hasType(PSMethod.Type.source);
	}

	protected PSMethod getPSMethod(Session session) {
		// query for measurement solver
		PSMethodMeasurement solver = session.getPSMethodInstance(PSMethodMeasurement.class);
		assert solver != null : "missing problem solver in session: PSMethodMeasurement";
		return solver;
	}

	protected Value toValue(Question question, Object measurandValue) {
		if (measurandValue instanceof Value) {
			ValueUtils.requireCompatible(question, (Value) measurandValue);
			return (Value) measurandValue;
		}
		if (question instanceof QuestionChoice questionChoice && measurandValue instanceof String name) {
			Choice choice = KnowledgeBaseUtils.findChoice(questionChoice, name, ANY_PROMPT);
			if (choice != null) {
				return new ChoiceValue(choice);
			}
			throw new IllegalArgumentException("no choice " + measurandValue);
		}
		if ((question instanceof QuestionChoice) && (measurandValue instanceof Number)) {
			int index = ((Number) measurandValue).intValue();
			if (index == 0) {
				return Unknown.getInstance();
			}
			List<Choice> choices = ((QuestionChoice) question).getAllAlternatives();
			if (index >= 1 && index <= choices.size()) {
				return new ChoiceValue(choices.get(index - 1));
			}
			throw new IllegalArgumentException("no choice at index " + index);
		}
		if (question instanceof QuestionText && measurandValue instanceof Long longValue) {
			// use toUnsignedString instead of just toString for cases where the CAN message is too big to
			// fit in the positive long values
			return new TextValue(Long.toUnsignedString(longValue));
		}
		if (question instanceof QuestionText && measurandValue instanceof Collection<?> collectionValue) {
			String collectionVerbalization = collectionValue.stream()
					.map(Object::toString)
					.sorted()
					.collect(Collectors.joining(", "));
			return new TextValue(collectionVerbalization);
		}
		if (question instanceof QuestionNum && measurandValue instanceof Number numValue) {
			return new NumValue(numValue.doubleValue());
		}
		if (question instanceof QuestionNum && measurandValue instanceof String stringValue) {
			// may throw an NumberFormatException which is also an IllegalArgumentException
			return new NumValue(Double.parseDouble(stringValue));
		}
		if (question instanceof QuestionText) {
			return new TextValue(String.valueOf(measurandValue));
		}
		if ((question instanceof QuestionDate) && (measurandValue instanceof Date)) {
			return new DateValue((Date) measurandValue);
		}
		throw new IllegalArgumentException("cannot map measured value '" + measurandValue + "' to question '" + question + "'");
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" +
				"identifier='" + Strings.ellipsis(identifier, 64) + '\'' +
				", mapping=" + Strings.ellipsis(mapping.toString(), 64) +
				", startCondition=" + startCondition +
				", stopCondition=" + stopCondition +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Measurement that = (Measurement) o;
		return Objects.equals(identifier, that.identifier) && Objects.equals(mapping, that.mapping) && Objects
				.equals(startCondition, that.startCondition) && Objects.equals(stopCondition, that.stopCondition);
	}

	@Override
	public int hashCode() {
		return Objects.hash(identifier, mapping, startCondition, stopCondition);
	}

	/**
	 * Returns the state of the measurement in the context of the current session<br/>
	 * The state will be determined the following way:
	 *
	 * <ul>
	 * <li>{@link State#FAILED} if the measurement has failed</li>
	 * <li>{@link State#MEASURING} if the measurement has not failed and currently ongoing</li>
	 * <li>{@link State#NOT_MEASURING} otherwise</li>
	 * </ul>
	 *
	 * @param session the Session to check
	 * @return the state of the measurement
	 */
	public MeasurementInfo getMeasurementInfo(Session session) {
		return session.getSessionObject(this).getMeasurementInfo();
	}
}
