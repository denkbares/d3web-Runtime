/*
 * Copyright (C) 2016 denkbares GmbH. All rights reserved.
 */

package de.d3web.core.knowledge.terminology.info;

/**
 * Defines the way a (abstract) numeric question should be displayed. The value is only a
 * hint to the rendering dialog. It is not required to implement all different display types, but
 * there must be a reasonable fallback if one or any of the types are not implemented.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 04.12.2016
 */
public enum NumDisplay {
	/**
	 * Display the value as a normal (plain) value to the user.
	 */
	normal,

	/**
	 * Displays the value as an analog measured value, e.g. as a gauge display.
	 */
	analog,

	/**
	 * Displays the value as a digital measured value, e.g. as a digital multi-meter
	 */
	digital,

	/**
	 * Displays the value in hexadecimal notation (unsigned)
	 */
	hexadecimal,

	/**
	 * Displays the value as a graph representation, displayed over the elapsing time.
	 */
	graph
}
