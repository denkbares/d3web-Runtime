/*
 * Copyright (C) 2018 denkbares GmbH. All rights reserved.
 */

package de.d3web.costbenefit.inference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.ObjectNameList;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.QuestionValue;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.UndefinedValue;

/**
 * @author Volker Belli (denkbares GmbH)
 * @created 19.04.2018
 */
public class CostBenefitProperties {

	/**
	 * This property is used on measurement questions and related named objects to denotes the connector/socket, the
	 * measurement is performed on.
	 */
	public static final Property<String> MEASUREMENT_CONNECTOR = Property.getProperty("measurementConnector", String.class);

	/**
	 * Denotes the development progress of a terminology object.
	 */
	public static final Property<Double> PROGRESS = Property.getProperty("progress", Double.class);

	/**
	 * Denotes the overridden development progress of a terminology object. This can be used, to track an
	 * additional/alternative progress on the terminology objects.
	 */
	public static final Property<Double> PROGRESS_OVERRIDE = Property.getProperty("progressOverride", Double.class);

	/**
	 * Marks a Question, indicating that the value of the question cannot be changed, once it has left the init value.
	 */
	public static final Property<Boolean> FINAL_QUESTION = Property.getProperty("finalQuestion", Boolean.class);

	/**
	 * Marks a QContainer, indicating that it should not (will not) be used to prepare a system state.
	 */
	public static final Property<Boolean> TARGET_ONLY = Property.getProperty("targetOnly", Boolean.class);

	/**
	 * Marks a QContainer with a path order priority, indicating if that QContainer should be preferably used early
	 * (lower, negative numbers) or late (higher, positive numbers) in the path. This property is used by the
	 * PathExtender's PathSorter.
	 */
	public static final Property<Double> PATH_ORDER = Property.getProperty("pathOrder", Double.class);

	/**
	 * Can be used to mark QContainers, that are permanently relevant.
	 *
	 * @see ExpertMode#getApplicablePermanentlyRelevantQContainers()
	 */
	public static final Property<Boolean> PERMANENTLY_RELEVANT = Property.getProperty("permanentlyRelevant", Boolean.class);

	/**
	 * This property should not be used anymore. It is replaced by the KnowledgeSlice {@link ComfortBenefit}
	 */
	@SuppressWarnings("DeprecatedIsStillUsed")
	@Deprecated
	public static final Property<Boolean> COMFORT_BENEFIT = Property.getProperty("comfortBenefit", Boolean.class);

	/**
	 * Allows to specify the common system state question parent in a cost benefit knowledge base.
	 */
	public static final Property<String> SYSTEM_STATE_PARENT = Property.getProperty("systemStateParent", String.class);

	/**
	 * Allows to specify the no error solution of the knowledge base
	 */
	public static final Property<String> NO_ERROR_SOLUTION = Property.getProperty("noErrorSolution", String.class);

	/**
	 * Allows to specify the solution to be shown when a repair verification is complete
	 */
	public static final Property<String> VERIFICATION_COMPLETE_SOLUTION = Property.getProperty("verificationCompleteSolution", String.class);

	/**
	 * Allows to specify the parent of the test equipment availability questions
	 */
	public static final Property<String> TEST_EQUIPMENT_AVAILABILITY_PARENT = Property.getProperty("testEquipmentAvailabilityParent", String.class);

	/**
	 * Allows to specify the parent of the adapter availability questions
	 */
	public static final Property<String> ADAPTER_AVAILABILITY_PARENT = Property.getProperty("adapterAvailabilityParent", String.class);

	/**
	 * Allows to specify the type of malfunction a question stands for. If no type is set for the question, the question
	 * isn't a malfunction question
	 */
	public static final Property<MalfunctionType> MALFUNCTION_TYPE = Property.getProperty("malfunctionType", MalfunctionType.class);

	/**
	 * Property to mark a question to represent test equipment. The property value will stand for the terminology name
	 * of a question marked with a MalfunctionType 'testEquipment'
	 */
	public static final Property<String> TEST_EQUIPMENT_QUESTION = Property.getProperty("testEquipmentQuestion", String.class);

	/**
	 * Property to mark a test step as verification step for test equipment malfunctions.
	 */
	public static final Property<ObjectNameList> VERIFY_MALFUNCTIONS = Property.getProperty("verifyMalfunctions", ObjectNameList.class);

	public static ObjectNameList getVerifyMalfunctions(QContainer testStep) {
		ObjectNameList list = testStep.getInfoStore().getValue(VERIFY_MALFUNCTIONS);
		return list == null ? new ObjectNameList() : list;
	}

	/**
	 * Allow to specify the type of a terminology object in the context of a repair verification, e.g. set it to
	 * exclusiveTestEquipmentCheck to have a test step as the only one verifying the error code no longer exists after
	 * the repair
	 */
	public static final Property<VerifyRepairType> VERIFY_REPAIR_TYPE = Property.getProperty("verifyRepairType", VerifyRepairType.class);

	/**
	 * Allows to specify the interrupt QContainer.
	 */
	public static final Property<String> INTERRUPT_QCONTAINER = Property.getProperty("interruptQContainer", String.class);

	/**
	 * Checks whether the given solution is the solution marked as the no-error-solution
	 *
	 * @param solution the solution to check
	 * @return true if the given solution is the no error solution, false otherwise
	 */
	public static boolean isNoErrorSolution(Solution solution) {
		return solution.equals(getNoErrorSolution(solution.getKnowledgeBase()));
	}

	/**
	 * Get the no error solution of the knowledge base (the solution to show if no error could be detected in the case).
	 * The no-error-solution is expected to be marked using the property {@link CostBenefitProperties#NO_ERROR_SOLUTION)
	 *
	 * @param kb the knowledge base to get the no-error-solution from
	 * @return the no-error-solution or null, if it does not exist or cannot be found.
	 */
	@Nullable
	public static Solution getNoErrorSolution(KnowledgeBase kb) {
		Solution solution = kb.getManager()
				.searchSolution(kb.getInfoStore().getValue(CostBenefitProperties.NO_ERROR_SOLUTION));
		// try fallback
		if (solution == null) {
			solution = kb.getManager().searchSolution("NoErrorSolution");
		}
		if (solution == null) {
			solution = kb.getManager().searchSolution("noErrorSolution");
		}
		return solution;
	}

	/**
	 * Get the verification-complete-solution of the knowledge base (the solution to show if the verification of a
	 * previous case is complete, e.g. after repair).
	 * The verification-complete-solution is expected to be marked using the property {@link
	 * CostBenefitProperties#VERIFICATION_COMPLETE_SOLUTION)
	 *
	 * @param kb the knowledge base to get the verification-complete-solution from
	 * @return the verification-complete-solution or null, if it does not exist or cannot be found.
	 */
	@Nullable
	public static Solution getVerificationCompleteSolution(KnowledgeBase kb) {
		Solution solution = kb.getManager()
				.searchSolution(kb.getInfoStore().getValue(CostBenefitProperties.VERIFICATION_COMPLETE_SOLUTION));
		// try fallback
		if (solution == null) {
			solution = kb.getManager().searchSolution("VerificationCompleteSolution");
		}
		return solution;
	}

	public static final String CONNECT_UMD_CHOICE_NAME = "connect_umd"; // CBX
	public static final String ADAPTED_CHOICE_NAME = "adapt"; // CBX
	public static final String DE_ADAPTED_CHOICE_NAME = "deadapt"; // CBX
	public static final String UMD_INTEGRATED_CHOICE_NAME = "#integriert"; // KnowledgeDesigner (SGP + CAN)

	public enum MalfunctionType {
		basic, testEquipment
	}

	public enum VerifyRepairType {
		/**
		 * Mark a test step as the only one verifying the error code no longer exists after the repair, in case there
		 * are multiple test steps that would verify the same
		 */
		exclusiveTestEquipmentCheck
	}

	/**
	 * Allows to specify potential choices for questions. These potential choices can for example be shown to the user
	 * so he may convert them to actual choices.
	 */
	public static final Property<ObjectNameList> POTENTIAL_CHOICES = Property.getProperty("potentialChoices", ObjectNameList.class);

	public static ObjectNameList getPotentialChoices(Question question) {
		ObjectNameList list = question.getInfoStore().getValue(POTENTIAL_CHOICES);
		return list == null ? new ObjectNameList() : list;
	}

	/**
	 * Returns the common system state parent of this knowledge base. If non is defined, the root QContainer will be
	 * returned.
	 *
	 * @param knowledgeBase the knowledge base for which the parent was defined, using SYSTEM_STATE_PARENT property
	 * @return the common system state parent of the knowledge base
	 */
	@NotNull
	public static QASet getSystemStateParent(KnowledgeBase knowledgeBase) {
		String parentName = knowledgeBase.getInfoStore().getValue(SYSTEM_STATE_PARENT);
		if (parentName == null) return knowledgeBase.getRootQASet();
		QContainer qContainer = knowledgeBase.getManager().searchQContainer(parentName);
		return qContainer == null ? knowledgeBase.getRootQASet() : qContainer;
	}

	/**
	 * Property to mark a question to be a state question. This means the question represents a state of the "Unit Under
	 * Test" (e.g. the machine). If the property is set, the question is a state question, otherwise it is a normal
	 * question. The value of the property then identifies the type of the state.
	 */
	public static final Property<UUTState> UUT_STATE = Property.getProperty("uutState", UUTState.class);

	public enum UUTState {
		/**
		 * "Indicator" or "final question" state, that represents a checked capability of the equipment. If this
		 * question once left its init value, it is assumed to be never changed within the knowledge base.
		 */
		checkOnce,
		/**
		 * "Normal" state question, that represents e.g. a switch position
		 */
		status,
		/**
		 * State/result of a mechanical check.
		 */
		mechanicalCheck,
		/**
		 * Integration state of a measurement adapter, a cable that is potentially connected into the UUT.
		 */
		measurementAdapter,
		/**
		 * Integration state of a measurement device, a device that is potentially connected to a measurement adapter.
		 */
		measurementDevice;

		private static UUTState autoDetect(Question stateQuestion) {
			// TODO: here some hard-coded stuff (convention) is used. Replace by adding UUT_STATE property to knowledge base
			if (stateQuestion.getInfoStore().getValue(FINAL_QUESTION)) return checkOnce;
			String name = stateQuestion.getName();
			if (name.startsWith("target_state_questionnaire#")) {
				if (devicePattern.matcher(name).matches()) return measurementDevice;
				if (adapterPattern.matcher(name).matches()) return measurementAdapter;
				if (mechanicalPattern.matcher(name).matches()) return mechanicalCheck;
				return status;
			}
			if ("positionTransitions".equals(name)) return status;
			return null;
		}
	}

	private static final Pattern devicePattern = Pattern.compile("(target_state_questionnaire#adaptation_\\w+)_X\\d+", Pattern.CASE_INSENSITIVE);
	private static final Pattern adapterPattern = Pattern.compile("(target_state_questionnaire#adaptation_\\w+)", Pattern.CASE_INSENSITIVE);
	private static final Pattern mechanicalPattern = Pattern.compile("(target_state_questionnaire#mechanicalCheck_\\w+)", Pattern.CASE_INSENSITIVE);

	/**
	 * Returns true if the question is a state question of any UUTState kind.
	 *
	 * @param stateQuestion the (potential) state question to be checked
	 * @return true for UUTState questions
	 */
	public static boolean hasUUTState(Question stateQuestion) {
		return getUUTState(stateQuestion) != null;
	}

	/**
	 * Returns null if the question is not a state question, otherwise the type of the state is returned.
	 *
	 * @param stateQuestion the (potential) state question to be checked
	 * @return the type of the state or null for non-state questions
	 */
	public static UUTState getUUTState(Question stateQuestion) {
		UUTState state = stateQuestion.getInfoStore().getValue(UUT_STATE);
		// TODO: we may remove this, as soon as the UUT-State auto-detection is applied to the knowledge base on the Knowledge Designer export or the Knowledge Designer is no longer used
		return (state == null) ? UUTState.autoDetect(stateQuestion) : state;
	}

	public static Collection<Question> getStateQuestions(KnowledgeBase base) {
		// TODO: here some hard-coded stuff (convention) is used. Replace by searching knowledge base for UUT state properties
		QContainer systemStateParent;
		String parentName = base.getInfoStore().getValue(SYSTEM_STATE_PARENT);
		// KnowledgeDesigner (SGP/CAN) backwards compatibility
		systemStateParent = base.getManager()
				.searchQContainer(Objects.requireNonNullElse(parentName, "target_state_questionnaire"));
		if (systemStateParent == null) return Collections.emptyList();
		return Stream.of(systemStateParent.getChildren())
				.filter(Question.class::isInstance).map(Question.class::cast).collect(Collectors.toList());
	}

	/**
	 * Returns true if the specified question is a "Indicator" or "final question" state, that represents a checked
	 * capability of the equipment. If this question once left its init value, it is assumed to be never changed within
	 * the diagnostic session.
	 */
	public static boolean isCheckOnce(TerminologyObject stateQuestion) {
		return (stateQuestion instanceof Question) && getUUTState((Question) stateQuestion) == UUTState.checkOnce;
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
	 * Returns the malfunction type of the specified question, or null if the question is not a malfunction.
	 */
	@Nullable
	public static MalfunctionType getMalfunctionType(Question question) {
		return question.getInfoStore().getValue(MALFUNCTION_TYPE);
	}

	/**
	 * Returns true if the specified question is a malfunction.
	 */
	public static boolean isMalfunction(Question question) {
		return getMalfunctionType(question) != null;
	}

	/**
	 * Returns the measurement adapter of the specified measurement device, represented by the state question of the
	 * device. If the specified question is not the state of a measurement device, or the device is not connected to a
	 * measurement adapter, null is returned.
	 *
	 * @deprecated this method only works for the old KnowledgeDesigner knowledge bases, NOT for CBX!
	 */
	@SuppressWarnings("DeprecatedIsStillUsed")
	@Deprecated
	public static Question getAdapterState(Question deviceStateQuestion) {
		// TODO: here some hard-coded stuff (convention) is used. Replace by UUT_STATE property and parent/child hierarchy
		Matcher matcher = devicePattern.matcher(deviceStateQuestion.getName());
		if (matcher.matches()) {
			return deviceStateQuestion.getKnowledgeBase().getManager().searchQuestion(matcher.group(1));
		}
		return null;
	}

	/**
	 * Returns the choice indicating that a adapter is adapted in regards to a given adapter state question.
	 */
	@NotNull
	public static List<Choice> getAdaptedChoice(Question stateQuestion) {
		List<Choice> adaptChoices = new ArrayList<>();
		if (stateQuestion instanceof QuestionChoice && getUUTState(stateQuestion) == UUTState.measurementAdapter) {
			for (Choice choice : ((QuestionChoice) stateQuestion).getAllAlternatives()) {
				if (choice.getName().startsWith(ADAPTED_CHOICE_NAME)) {
					adaptChoices.add(choice);
				}
			}
		}
		return adaptChoices;
	}

	/**
	 * Returns the value that represents the integrated state of the specified adapter state or device state. The method
	 * return null if there is no such value.
	 */
	public static Value getIntegratedValue(Question adapterOrDeviceStateQuestion) {
		QuestionValue value = KnowledgeBaseUtils.findValue(adapterOrDeviceStateQuestion, CONNECT_UMD_CHOICE_NAME);
		if (value == null) { // SGP/CAN backwards compatibility
			value = KnowledgeBaseUtils.findValue(adapterOrDeviceStateQuestion, adapterOrDeviceStateQuestion.getName() + UMD_INTEGRATED_CHOICE_NAME);
		}
		if (value == null && adapterOrDeviceStateQuestion instanceof QuestionChoice) {
			value = new ChoiceValue(((QuestionChoice) adapterOrDeviceStateQuestion).getAllAlternatives().get(0));
		}
		return UndefinedValue.isUndefinedValue(value) ? null : value;
	}

	/**
	 * Returns true if the specified QContainer is explicitly marked to be permanently relevant, which means that the
	 * user is always allowed to use this QContainer, independently from a calculated path.
	 */
	public static boolean isPermanentlyRelevant(QContainer container) {
		if (container == null) return false;
		return container.getInfoStore().getValue(CostBenefitProperties.PERMANENTLY_RELEVANT);
	}

	/**
	 * Returns true if the specified QContainer is explicitly marked to only be used as a path target, but not being
	 * used to prepare stated for other targets.
	 */
	public static boolean isTargetOnly(QContainer container) {
		if (container == null) return false;
		return container.getInfoStore().getValue(CostBenefitProperties.TARGET_ONLY);
	}
}
