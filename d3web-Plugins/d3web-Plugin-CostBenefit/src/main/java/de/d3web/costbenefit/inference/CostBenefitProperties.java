/*
 * Copyright (C) 2018 denkbares GmbH. All rights reserved.
 */

package de.d3web.costbenefit.inference;

import de.d3web.core.knowledge.terminology.info.Property;

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
}
