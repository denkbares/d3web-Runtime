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

package de.d3web.core.inference;

import java.util.List;

import de.d3web.core.session.XPSCase;

/**
 * Abstract class to describe actions executed by rules,
 * when their conditions are true.
 * @author Joachim Baumeister
 */
public abstract class RuleAction implements Cloneable, java.io.Serializable {
	
	private Rule rule = null;
	
	public Rule getCorrespondingRule() {
		return rule;
	}
	
	/**
	 * Notlösung!!!
	 * 
	 * Eine Action sollte seine Regel nicht kennen müssen. Das QASetManager Interface und seine Implementierer müssen dringend überarbeitet werden.
	 * @param rule
	 */
	public void setRule(Rule rule) {
		this.rule=rule;
	}
	
	private static final long serialVersionUID = -7734602381846137317L;
	
	/**
	 * Executes the included action.
	 */
	public abstract void doIt(de.d3web.core.session.XPSCase theCase);

	/**
	 * @return all objects participating on the action.<BR>
	 * Needed from RuleComplex to manage dynamic references of 
	 * knowledge maps.
	 */
	public abstract List getTerminalObjects();


	public abstract Class<? extends PSMethod> getProblemsolverContext();

	/**
	 * Checks if any action value (e.g. terminal objects of a formula) have
	 * changed since last call to {@link #doIt(XPSCase)}.
	 * 
	 * @see RuleComplex
	 */
	public boolean hasChangedValue(XPSCase theCase) {
		return false;
	}
	
	/**
	 * Tries to undo the included action.
	 */
	public abstract void undo(de.d3web.core.session.XPSCase theCase);
	
	/**
	 * Returns a clone of this RuleAction.<p>
	 */
	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}
	
	public abstract RuleAction copy();
}