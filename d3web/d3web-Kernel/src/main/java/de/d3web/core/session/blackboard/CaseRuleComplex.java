/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.core.session.blackboard;
import de.d3web.core.inference.Rule;
/**
 * Stores the dynamic, user specific values for a RuleComplex
 * object. It corresponds to the static RuleComplex object.<br>
 * Values to be stored:<br>
 * <li> Current state of the rule (fired/not fired)
 * @author Christian Betz, joba
 * @see Rule
 */
public class CaseRuleComplex extends XPSCaseObject {
	private boolean fired = false;

	/**
	 * Creates a new CaseRuleComlplex. The dynamic store for
	 * the given RuleComplex.
	 */
	public CaseRuleComplex(Rule rule) {
		super(rule);
	}

	/**
	 * Creation date: (04.07.00 14:01:25)
	 * @return the current firing-state of the corresponding rule.
	 */
	public boolean hasFired() {
		return fired;
	}

	/**
	 * Creation date: (04.07.00 14:01:25)
	 * @param fired the new dynamic firing-state of the corresponding rule.
	 */
	public void setFired(boolean fired) {
		this.fired = fired;
	}
}