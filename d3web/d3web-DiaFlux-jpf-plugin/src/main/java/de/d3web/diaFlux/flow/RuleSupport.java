/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.diaFlux.flow;

import de.d3web.core.inference.Rule;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.session.Session;

/**
 * @author Reinhard Hatko
 *
 *         Created: 20.12.2009
 */
public class RuleSupport implements ISupport {

	private final Rule rule;

	/**
	 * @param condition
	 */
	public RuleSupport(Rule rule) {
		this.rule = rule;
	}

	@Override
	public boolean isValid(Session session) {
		try {

			rule.check(session);

			return rule.canFire(session);
		}
		catch (UnknownAnswerException e) {
			return false;
		}
	}

	@Override
	public void remove(Session session, NodeData nodeData) {
		// nothing
	}


	/**
	 * @return the rule
	 */
	public Rule getRule() {
		return rule;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rule == null) ? 0 : rule.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RuleSupport other = (RuleSupport) obj;
		if (rule == null) {
			if (other.rule != null) return false;
		}
		else if (!rule.equals(other.rule)) return false;
		return true;
	}


}
