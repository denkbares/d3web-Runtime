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

import de.d3web.core.knowledge.TerminologyObject;

/**
 * Abstract condition for all terminal conditions. A terminal condition contains
 * no sub-conditions, but represents a single proposition. The composite pattern
 * is used for this. This class is the abstract class for a "leaf".
 *
 * @author Michael Wolber, joba
 */
public abstract class TerminalCondition implements Condition {

	private final ArrayList<TerminologyObject> terminals = new ArrayList<>();

	/**
	 * Creates a new terminal condition with the specified depending objects.
	 *
	 * @param terminals the object(s) the condition depends on.
	 */
	public TerminalCondition(TerminologyObject... terminals) {
		Collections.addAll(this.terminals, terminals);
		this.terminals.trimToSize();
	}

	/**
	 * Creates a new terminal condition with the specified depending objects.
	 *
	 * @param terminals the object(s) the condition depends on.
	 */
	public TerminalCondition(Collection<? extends TerminologyObject> terminals) {
		this.terminals.addAll(terminals);
		this.terminals.trimToSize();
	}

	/**
	 * Returns the one terminal object contained in this condition, for instance
	 * a question constrained by a specific value.
	 * 
	 * @return the terminal object of this condition
	 */
	@Override
	public Collection<? extends TerminologyObject> getTerminalObjects() {
		return terminals;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (terminals.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		TerminalCondition other = (TerminalCondition) obj;
		return terminals.equals(other.terminals);
	}

}