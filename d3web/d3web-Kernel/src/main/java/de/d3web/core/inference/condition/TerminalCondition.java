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

package de.d3web.core.inference.condition;

import java.util.ArrayList;
import java.util.List;

import de.d3web.core.knowledge.terminology.NamedObject;
/**
 * Abstract condition for all terminal conditions. A terminal condition
 * contains no sub-conditions, but represents a single proposition.
 * The composite pattern is used for this. This class is the abstract class
 * for a "leaf".
 * 
 * @author Michael Wolber, joba
 */
public abstract class TerminalCondition extends AbstractCondition {
	
	private static final long serialVersionUID = 985715059851028211L;
	private List<NamedObject> terminal = new ArrayList<NamedObject>(1);

	/**
	 * Creates a new terminal condition with the specified
	 * proposition.
	 * @param conds the specified condition
	 */
	protected TerminalCondition(NamedObject idobject) {
		terminal.add(idobject);
	}

	/**
	 * Returns the one terminal object contained in this condition, 
	 * for instance a question constrained by a specific value.
	 * @return the terminal object of this condition 
	 */
	public List<? extends NamedObject> getTerminalObjects() {
		return terminal;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		else if ((other == null) || (getClass() != other.getClass())) 
			return false;
		else
			if (this.getTerminalObjects() != null && 
			   ((TerminalCondition)other).getTerminalObjects() != null)
						return this.getTerminalObjects().containsAll(((TerminalCondition)other).getTerminalObjects())
								&& ((TerminalCondition)other).getTerminalObjects().containsAll(this.getTerminalObjects());
					else return(this.getTerminalObjects() == null) && (((TerminalCondition)other).getTerminalObjects() == null);
		

	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

}