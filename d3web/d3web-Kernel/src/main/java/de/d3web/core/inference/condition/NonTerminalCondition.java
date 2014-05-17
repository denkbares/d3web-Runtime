/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.core.inference.condition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.utils.HashCodeUtils;

/**
 * Abstract condition for all non-terminal conditions. A non-terminal condition
 * is a container for other terminal or non-terminal sub-conditions. The
 * composite pattern is used for this. This class is the abstract class for a
 * "composite".
 * 
 * @author Michael Wolber, joba
 */
public abstract class NonTerminalCondition implements Condition {

	/**
	 * The list of conditions enclosed in this {@link NonTerminalCondition}.
	 */
	private final List<Condition> terms;

	private final Set<Condition> termSet;

	private final int hash;

	/**
	 * Creates a new non-terminal condition with the specified sub-conditions.
	 * 
	 * @param conditions the specified sub-conditions
	 */
	public NonTerminalCondition(List<Condition> conditions) {
		terms = Collections.unmodifiableList(new ArrayList<Condition>(conditions));
		termSet = new HashSet<Condition>(conditions);
		// create hash code an cache it
		int temphash = HashCodeUtils.SEED;
		temphash = HashCodeUtils.hash(temphash, getClass().getName());
		temphash = HashCodeUtils.hashUnordered(temphash, terms);
		hash = temphash;
	}

	@Override
	public Collection<TerminologyObject> getTerminalObjects() {
		Set<TerminologyObject> v = new HashSet<TerminologyObject>();
		for (Condition condition : terms) {
			v.addAll(condition.getTerminalObjects());
		}
		return v;
	}

	/**
	 * Returns the list of {@link Condition} instances enclosed in this
	 * {@link NonTerminalCondition}.
	 * 
	 * @return a list containing the conditions enclosed in this complex
	 *         condition
	 */
	public List<Condition> getTerms() {
		return terms;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		else if ((other == null)
				|| (getClass() != other.getClass() || hashCode() != other.hashCode())) {
			return false;
		}
		else {
			NonTerminalCondition otherNTC = (NonTerminalCondition) other;
			return this.termSet.equals(otherNTC.termSet);
			// return this.terms.containsAll(otherNTC.terms)
			// && otherNTC.terms.containsAll(this.terms);
		}
	}

	@Override
	public int hashCode() {
		return hash;
	}
}