/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.core.inference.condition;

import de.d3web.core.session.Session;

/**
 * Utility class for Conditions
 *
 * @author Markus Friedrich (denkbares GmbH)
 * @created 04.07.2012
 */
public class Conditions {

	/**
	 * Returns true, if the condition evaluates to true. In all other cases, including unknown or
	 * undefined, false is returned.
	 *
	 * @param condition specified Condition
	 * @param session specified Session
	 * @return if the condition evaevaluatesls to true
	 * @created 04.07.2012
	 */
	public static boolean isTrue(Condition condition, Session session) {
		try {
			return condition.eval(session);
		}
		catch (NoAnswerException e) {
			return false;
		}
		catch (UnknownAnswerException e) {
			return false;
		}
	}

	/**
	 * Returns true, if the condition evaluates to false. In all other cases, including unknown or
	 * undefined, false is returned.
	 *
	 * @param condition specified Condition
	 * @param session specified Session
	 * @return if the condition evaluates to false
	 * @created 04.07.2012
	 */
	public static boolean isFalse(Condition condition, Session session) {
		try {
			return !condition.eval(session);
		}
		catch (NoAnswerException e) {
			return false;
		}
		catch (UnknownAnswerException e) {
			return false;
		}
	}

	/**
	 * Returns true, if the condition evaluates to undefined. In all other cases,
	 * including unknown, false is returned.
	 *
	 * @param condition specified Condition
	 * @param session specified Session
	 * @return if the condition evaluates to undefined
	 * @created 04.07.2012
	 */
	public static boolean isUndefined(Condition condition, Session session) {
		try {
			condition.eval(session);
			return false;
		}
		catch (NoAnswerException e) {
			return true;
		}
		catch (UnknownAnswerException e) {
			return false;
		}
	}

}
