/*
 * Copyright (C) 2018 denkbares GmbH. All rights reserved.
 */

package de.d3web.costbenefit.inference;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.QuestionValue;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.UndefinedValue;

/**
 * @author Volker Belli (denkbares GmbH)
 * @created 19.04.2018
 */
public class CostBenefitProperties {
	/**
	 * Marks a Question, indicating that the value of the question cannot be changed, once it has left the init value.
	 */
	public static final Property<Boolean> FINAL_QUESTION = Property.getProperty("finalQuestion", Boolean.class);

	/**
	 * Marks a QContainer, indicating that it should not (will not) be used to prepare a system state.
	 */
	public static final Property<Boolean> TARGET_ONLY = Property.getProperty("targetOnly", Boolean.class);

	/**
	 * Can be used to mark QContainers, that are permanently relevant.
	 *
	 * @see ExpertMode#getApplicablePermanentlyRelevantQContainers()
	 */
	public static final Property<Boolean> PERMANENTLY_RELEVANT = Property.getProperty("permanentlyRelevant", Boolean.class);

	/**
	 * This property should not be used anymore. It is replaced by the KnowledgeSlice {@link ComfortBenefit}
	 */
	@Deprecated
	public static final Property<Boolean> COMFORT_BENEFIT = Property.getProperty("comfortBenefit", Boolean.class);

//
//	public static final Property<UUTState> UUT_STATE = Property.getProperty("uutState", UUTState.class);

	enum UUTState {
		/**
		 * "Normal" state question, that represents e.g. a switch position
		 */
		status,
		/**
		 * "Indicator" or "final question" state, that represents a checked capability of the equipment. If this
		 * question once left its init value, it is assumed to be never changed within the knowledge base.
		 */
		checkOnce,
		/**
		 * Integration state of a measurement adapter, a cable that is potentially connected into the UUT.
		 */
		measurementAdapter,
		/**
		 * Integration state of a measurement device, a device that is potentially connected to a measurement adapter.
		 */
		measurementDevice,
		/**
		 * State/result of a mechanical check.
		 */
		mechanicalCheck;

		private static UUTState autoDetect(Question stateQuestion) {
			// TODO: here some hard-coded stuff (convention) is used. Replace by adding UUT_STATE property to knowledge base
			if (stateQuestion.getInfoStore().getValue(FINAL_QUESTION)) return checkOnce;
			String name = stateQuestion.getName();
			if (devicePattern.matcher(name).matches()) return measurementDevice;
			if (adapterPattern.matcher(name).matches()) return measurementAdapter;
			if (mechanicalPattern.matcher(name).matches()) return mechanicalCheck;
			if (name.startsWith("target_state_questionnaire#")) return status;
			if (name.equals("positionTransitions")) return status;
			return null;
		}
	}

	private static final Pattern devicePattern = Pattern.compile("(target_state_questionnaire#adaptation_\\w+)_X\\d+", Pattern.CASE_INSENSITIVE);
	private static final Pattern adapterPattern = Pattern.compile("(target_state_questionnaire#adaptation_\\w+)", Pattern.CASE_INSENSITIVE);
	private static final Pattern mechanicalPattern = Pattern.compile("(target_state_questionnaire#mechanicalCheck_\\w+)", Pattern.CASE_INSENSITIVE);

	public static UUTState getUUTState(Question stateQuestion) {
		return UUTState.autoDetect(stateQuestion);
	}

	public static Collection<Question> getStateQuestions(KnowledgeBase base) {
		// TODO: here some hard-coded stuff (convention) is used. Replace by searching knowledge base for UUT state properties
		return Stream.of(base.getManager().searchQContainer("target_state_questionnaire").getChildren())
				.filter(Question.class::isInstance).map(Question.class::cast).collect(Collectors.toList());
	}

	/**
	 * Returns true if the specified question is a "Indicator" or "final question" state, that represents a checked
	 * capability of the equipment. If this question once left its init value, it is assumed to be never changed within
	 * the knowledge base.
	 */
	public static boolean isCheckOnce(TerminologyObject stateQuestion) {
		// TODO: replace by state property (or both) as soon as it exists
		return (stateQuestion instanceof Question) && stateQuestion.getInfoStore().getValue(FINAL_QUESTION);
	}

	/**
	 * Returns true if the specified question is a "normal" state question, that represents e.g. a switch position.
	 */
	public static boolean isStatus(Question stateQuestion) {
		return getUUTState(stateQuestion) == UUTState.status;
	}

	/**
	 * Returns true if the specified question is a integration state of a measurement adapter, a cable that is
	 * potentially connected into the UUT.
	 */
	public static boolean isMeasurementAdapterState(Question stateQuestion) {
		return getUUTState(stateQuestion) == UUTState.measurementAdapter;
	}

	/**
	 * Returns true if the specified question is a integration state of a measurement device, a device that is
	 * potentially connected to a measurement adapter.
	 */
	public static boolean isMeasurementDeviceState(Question stateQuestion) {
		return getUUTState(stateQuestion) == UUTState.measurementDevice;
	}

	/**
	 * Returns the measurement adapter of the specified measurement device, represented by the state question of the
	 * device. If the specified question is not the state of a measurement device, or the device is not connected to a
	 * measurement adapter, null is returned.
	 */
	public static Question getAdapterState(Question deviceStateQuestion) {
		// TODO: here some hard-coded stuff (convention) is used. Replace by UUT_STATE property and parent/child hierarchy
		Matcher matcher = devicePattern.matcher(deviceStateQuestion.getName());
		if (matcher.matches()) {
			return deviceStateQuestion.getKnowledgeBase().getManager().searchQuestion(matcher.group(1));
		}
		return null;
	}

	/**
	 * Returns the value that represents the integrated state of the specified adapter state or device state. The method
	 * return null if there is no such value.
	 */
	public static Value getIntegratedValue(Question adapterOrDeviceStateQuestion) {
		// TODO: here some hard-coded stuff (convention) is used. Replace by ???
		QuestionValue value = KnowledgeBaseUtils.findValue(adapterOrDeviceStateQuestion, adapterOrDeviceStateQuestion.getName() + "#integriert");
		return UndefinedValue.isUndefinedValue(value) ? null : value;
	}
}
