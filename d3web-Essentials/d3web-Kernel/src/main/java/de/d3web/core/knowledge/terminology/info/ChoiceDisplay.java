/*
 * Copyright (C) 2018 denkbares GmbH. All rights reserved.
 */

package de.d3web.core.knowledge.terminology.info;

/**
 * Defines the wanted display of choice values.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 28.01.2018
 */
public enum ChoiceDisplay {
	/**
	 * Display the value as a normal (plain) value to the user. If the value is editable, the client application decide
	 * how the value is displayed, usually as buttons (or similar) to be clicked. If not editable, usually only the
	 * selected choice(s) are displayed.
	 */
	normal,

	/**
	 * Displays the value as a digital measured value, e.g. is 15-segment characters.
	 */
	digital,

	/**
	 * Displays the choices as e.g. a drop-down field, often combined with some ability to enter text to filter the
	 * potential values during typing. This option may e.g. be used for large numbers of potential answer values.
	 */
	filter,

	/**
	 * Displays the choices as a checklist, with checked entries for each selected choice. Mostly useful for mc
	 * questions. Especially if the question is abstract (read-only), the implementer of the user interface should
	 * ensure that all choices are displayed, even is not selected (yet).
	 */
	checklist
}
