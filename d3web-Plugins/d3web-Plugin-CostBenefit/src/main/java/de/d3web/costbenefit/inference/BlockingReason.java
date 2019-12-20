/*
 * Copyright (C) 2019 denkbares GmbH, Germany
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package de.d3web.costbenefit.inference;

/**
 * Enumeration for reasons, why QContainers or targets may be blocked for path calculation.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 20.12.2019
 */
public enum BlockingReason {
	checkOnceFalse("%s requires a check-once state that is violated"),
	cannotReach("%s requires a state that cannot be prepared"),
	contraIndicated("%s is contra indicated"),
	permanentlyRelevant("%s is permanently relevant");

	private final String message;

	BlockingReason(String message) {
		this.message = message;
	}

	/**
	 * Returns the display message for this blocking reason, for the specified blocked item.
	 *
	 * @param name the name of the blocked item
	 * @return the display message if this blocking reason
	 */
	public String getMessage(String name) {
		return String.format(message, name);
	}
}
