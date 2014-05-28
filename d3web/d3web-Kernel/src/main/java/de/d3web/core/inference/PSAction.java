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
public abstract class PSAction {

	/**
	 * Executes the included action.
	 *
	 * @param session  the Case
	 * @param source   the object executing the action
	 * @param psmethod the psmethod of the source
	 */
	public abstract void doIt(Session session, Object source, PSMethod psmethod);

	/**
	 * Returns all {@link TerminologyObject}s the action has direct influence on
	 * by modifying them. E.g. these are the question(s) that may be changed by
	 * the action. The method is utilized by {@link Rule} to manage dynamic
	 * references of knowledge maps.
	 *
	 * @return the backward (potentially modified) objects
	 */
	public abstract List<? extends TerminologyObject> getBackwardObjects();

	/**
	 * Returns all {@link TerminologyObject}s, which are part of the forward
	 * knowledge, that means that the action and the action's outcome depends on
	 * these objects. For example the list contains all objects a value-setting
	 * action requires to calculate the value to be set.
	 *
	 * @return the forward (utilized) objects
	 * @created 30.09.2010
	 */
	public List<? extends TerminologyObject> getForwardObjects() {
		return new LinkedList<TerminologyObject>();
	}

	/**
	 * Tries to undo the included action.
	 *
	 * @param psmethod psmethod the psmethod of the source
	 */
	public abstract void undo(Session session, Object source, PSMethod psmethod);

	/**
	 * Redoes the action if necessary. This method is called if the action remains active, e.g. if a rule has checked
	 * due to some fact changes but the precondition remains true. Should be implemented if the result depends on the
	 * forward objects.
	 *
	 * @param psmethod psmethod the psmethod of the source
	 */
	public void update(Session session, Object source, PSMethod psmethod) {

	}

}