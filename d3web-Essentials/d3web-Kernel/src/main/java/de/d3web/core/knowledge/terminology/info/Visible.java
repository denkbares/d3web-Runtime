/*
 * Copyright (C) 2017 denkbares GmbH. All rights reserved.
 */

package de.d3web.core.knowledge.terminology.info;

/**
 * Specified when a question will be visible within the interview, when a parent qContainer is
 * displayed to the user.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 26.01.2017
 */
public enum Visible {
	/**
	 * The question will be visible if it is either top-level question of a relevant (indicated)
	 * qContainer or if the question is relevant (indicated) itself. This is the default value for
	 * each question.
	 */
	relevant,
	/**
	 * The question is always visible in the containing qContainer is visible. If the question is
	 * not relevant (yet), the question cannot be answered by the user.
	 */
	always,
	/**
	 * The question is never displayed in the containing qContainer, regardless if the question is a
	 * top-level question, or indicated (relevant). Note that in this case, the user may not be able
	 * to answer the qContainer, and so the user might not be able to continue with the interview.
	 * Therefore this option is usually used if the knowledge base, or the surrounding interview
	 * application, makes sure that the question will be answered for the user.
	 */
	never
}