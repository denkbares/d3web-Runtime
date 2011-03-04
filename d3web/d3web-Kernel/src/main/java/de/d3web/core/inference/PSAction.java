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

package de.d3web.core.inference;

import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;

/**
 * Abstract class to describe actions executed by a given source, when their
 * conditions are true.
 * 
 * @author Joachim Baumeister
 */
public abstract class PSAction implements Cloneable {

	/**
	 * Executes the included action.
	 * 
	 * @param session the Case
	 * @param source the object executing the action
	 * @param psmethod the psmethod of the source
	 */
	public abstract void doIt(Session session, Object source, PSMethod psmethod);

	/**
	 * @return all objects participating on the action.<BR>
	 *         Needed from RuleComplex to manage dynamic references of knowledge
	 *         maps.
	 */
	public abstract List<? extends TerminologyObject> getBackwardObjects();

	/**
	 * Returns Terminology objects, which are part of the forward Knowledge
	 * 
	 * @created 30.09.2010
	 * @return List of {@link TerminologyObject}
	 */
	public List<? extends TerminologyObject> getForwardObjects() {
		return new LinkedList<TerminologyObject>();
	}

	/**
	 * Checks if any action value (e.g. terminal objects of a formula) have
	 * changed since last call to {@link #doIt(Session)}.
	 * 
	 * @see RuleComplex
	 */
	public boolean hasChangedValue(Session session) {
		return false;
	}

	/**
	 * Tries to undo the included action.
	 * 
	 * @param psmethod psmethod the psmethod of the source
	 */
	public abstract void undo(Session session, Object source, PSMethod psmethod);

	/**
	 * Returns a clone of this RuleAction.
	 * <p>
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public abstract PSAction copy();
}