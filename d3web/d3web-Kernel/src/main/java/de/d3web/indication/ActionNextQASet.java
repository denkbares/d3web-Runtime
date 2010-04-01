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

package de.d3web.indication;

import java.util.List;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.PSAction;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.session.XPSCase;
import de.d3web.indication.inference.PSMethodNextQASet;

/**
 * This abstract class is representing the Action of an indication rule.
 * Specialize this in order to implement a new indication type. Creation date:
 * (19.06.2001 18:21:07)
 * 
 * @author Christian Betz
 */
public abstract class ActionNextQASet extends PSAction {
	
	private List<QASet> qasets;

	/**
	 * Indicates all QASets specified by "setQASets"-Method
	 */
	public void doIt(XPSCase theCase, Rule rule) {
		doItWithContext(theCase, rule);
	}

	protected void doItWithContext(XPSCase theCase, Rule rule) {
		for (QASet nextQASet : getQASets()) {
			nextQASet.activate(theCase, rule, rule.getProblemsolverContext());

			if (nextQASet instanceof QContainer) {
				theCase.getIndicatedQContainers().add((QContainer) nextQASet);
			}
		}
	}

	/**
	 * @return PSMethodNextQASet.class
	 */
	public Class<? extends PSMethod> getProblemsolverContext() {
		return PSMethodNextQASet.class;
	}

	/**
	 * @return List of QASets this Action can indicate
	 */
	public List<QASet> getQASets() {
		return qasets;
	}

	/**
	 * @return all objects participating on the action.<BR>
	 *         same as getQASets()
	 */
	public List<QASet> getTerminalObjects() {
		return getQASets();
	}

	/**
	 * sets a List of QASets that this Action can activate
	 */
	public void setQASets(List<QASet> qasets) {
		this.qasets = qasets;
	}

	/**
	 * Deactivates all activated QASets
	 */
	@Override
	public void undo(XPSCase theCase, Rule rule) {
		for (QASet qaset : getQASets()) {
			qaset.deactivate(theCase, rule, rule.getProblemsolverContext());
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "@"
				+ Integer.toHexString(hashCode()) + getQASets();
	}

}