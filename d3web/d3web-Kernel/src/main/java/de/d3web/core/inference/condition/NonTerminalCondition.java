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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.terminology.IDObject;
/**
 * Abstract condition for all non-terminal conditions. A non-terminal condition
 * is a container for other terminal or non-terminal sub-conditions.
 * The composite pattern is used for this. This class is the 
 * abstract class for a "composite".
 * 
 * @author Michael Wolber, joba
 */
public abstract class NonTerminalCondition extends AbstractCondition {

	private static final long serialVersionUID = -5469533685946905130L;
	/**
	 * The list of conditions enclosed in this {@link NonTerminalCondition}.
	 */
	protected List<AbstractCondition> terms;

	/**
	 * Creates a new non-terminal condition with the specified
	 * sub-conditions.
	 * @param conditions the specified sub-conditions
	 */
	public NonTerminalCondition(List<AbstractCondition> conditions) {
		// The interface with the plain List is currently
		// not touched. Therefore, we do a conversion here.
		terms = new ArrayList<AbstractCondition>(conditions.size());
		for (Object object : conditions) {
			terms.add((AbstractCondition)object);
		}
	}
	
	@Override
	public List<IDObject> getTerminalObjects() {
		List<IDObject> v = new ArrayList<IDObject>();
		for (AbstractCondition condition : terms) {
			v.addAll(condition.getTerminalObjects());
		}
		return v;
	}

	/**
	 * Returns the list of {@link AbstractCondition} instances
	 * enclosed in this {@link NonTerminalCondition}.
	 * @return a list containing the conditions enclosed in 
	 *         this complex condition
	 */
	public List<AbstractCondition> getTerms() {
		return terms;
	}

	/**
	 * Sets the list of {@link AbstractCondition} instances
	 * to be enclosed in this {@link NonTerminalCondition}.
	 * @param theTerms the terms to be enclosed in this condition
	 */
	public void setTerms(List<AbstractCondition> theTerms) {
		terms = theTerms;
	}

	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		else if ((other == null) || (getClass() != other.getClass())) {
			return false;
		} else {
			NonTerminalCondition otherNTC = (NonTerminalCondition) other;
			
			if ((this.getTerms()) != null && (otherNTC.getTerms() != null))
				if (this.getTerms().containsAll(otherNTC.getTerms()) &&
					otherNTC.getTerms().containsAll(this.getTerms()))
					return true;
				else
					return false;
				// both == null?
			else return ((this.getTerms()) == null && (otherNTC.getTerms() == null));
					
		}
	}
		
	@Override
	public int hashCode() {
		return getTerms().hashCode();
	}

	@Override
	public AbstractCondition copy() {
		List<AbstractCondition> newTerms  = new LinkedList<AbstractCondition>();
		Iterator<AbstractCondition> tIter = getTerms().iterator();
		while (tIter.hasNext()) {
			AbstractCondition c = tIter.next();
			newTerms.add(c.copy());
		}
		return createInstance(newTerms, this);
	}

	/**
	 * Template method. Needs to be implemented in sub-classes by their constructor.
	 * @param theTerms the conditions to be enclosed in the newly created instance
	 * @param original the original condition that is the basis of the newly created instance 
	 */
	protected abstract AbstractCondition createInstance(List<AbstractCondition> theTerms, AbstractCondition original);
}