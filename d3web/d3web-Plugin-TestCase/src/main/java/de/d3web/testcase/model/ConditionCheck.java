/*
 * Copyright (C) 2014 denkbares GmbH, Germany
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

package de.d3web.testcase.model;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.Conditions;
import de.d3web.core.session.Session;

/**
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 05.06.2014
 */
public class ConditionCheck implements Check {

	private final Condition condition;

	/**
	 * Creates a new ConditionCheck instance for a specified condition and the
	 * condition-defining section.
	 *
	 * @param condition the condition to be checked
	 */
	public ConditionCheck(Condition condition) {
		this.condition = condition;
	}

	@Override
	public boolean check(Session session) {
		return Conditions.isTrue(condition, session);
	}

	@Override
	public String getCondition() {
		return condition.toString();
	}

	public Condition getConditionObject() {
		return condition;
	}

}
