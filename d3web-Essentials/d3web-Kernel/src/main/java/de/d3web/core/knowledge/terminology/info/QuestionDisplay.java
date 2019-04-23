/*
 * Copyright (C) 2018 denkbares GmbH. All rights reserved.
 */

package de.d3web.core.knowledge.terminology.info;

/**
 * Allows to further configure the way a question is displayed in the interview
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 19.07.18
 */
public enum QuestionDisplay {

	/**
	 * No specific question display is given for this question. Use fall-back or normal behavior.
	 */
	unspecified,
	/**
	 * Normal/default display for questions
	 */
	normal,
	/**
	 * Marks questions to be a hidden question. This is rarely required, e.g. for technical questions that should delay
	 * the interview until the value of the question is set programmatically.
	 */
	hidden,
	/**
	 * Marks a question to be an activity, meaning an instruction to the user to perform and action (often a
	 * QuestionChoice with just on answer "ok/next/continue".
	 */
	activity,
	/**
	 * Marks a question to be an observation, meaning an instruction to the user to observe something to answer the
	 * question.
	 */
	observation,
	/**
	 * Marks a question to be a temporary observation, meaning an instruction to the user to observe something to answer
	 * the question. Temporary means, that the phenomenon is only observable for a short time, e.g. while an activity is
	 * performed.
	 */
	temporaryObservation,
}
