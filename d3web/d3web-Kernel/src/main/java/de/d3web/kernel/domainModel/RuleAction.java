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

package de.d3web.kernel.domainModel;

import java.util.List;

/**
 * Abstract class to describe actions executed by rules,
 * when their conditions are true.
 * @author Joachim Baumeister
 */
public abstract class RuleAction implements Cloneable, java.io.Serializable {
	private RuleComplex correspondingRule;

	public RuleAction(RuleComplex theCorrespondingRule) {
		correspondingRule = theCorrespondingRule;
	}

	/**
	 * Executes the included action.
	 */
	public abstract void doIt(de.d3web.kernel.XPSCase theCase);

	/**
	 * @return all objects participating on the action.<BR>
	 * Needed from RuleComplex to manage dynamic references of 
	 * knowledge maps.
	 */
	public abstract List getTerminalObjects();

	public RuleComplex getCorrespondingRule() {
		return correspondingRule;
	}

	public abstract Class getProblemsolverContext();

	public void setCorrespondingRule(RuleComplex newCorrespondingRule) {
		correspondingRule = newCorrespondingRule;
	}

	/**
	 * @return true (default), if this action needs to be executed
	 * only once, when the corresponding rule can fire 
	 * (true -- e.g. ActionHeuristicRS),or, if the action has to 
	 * be executed each time the rule is checked 
	 * (false -- e.g. ActionSetValue/ActionAddValue)
	 */
	public boolean singleFire() {
		return true;
	}

	/**
	 * Tries to undo the included action.
	 */
	public abstract void undo(de.d3web.kernel.XPSCase theCase);
	
	/**
	 * Returns a clone of this RuleAction.<p>
	 */
	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}
	
	public abstract RuleAction copy();
}