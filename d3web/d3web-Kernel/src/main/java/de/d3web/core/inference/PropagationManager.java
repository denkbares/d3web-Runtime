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

package de.d3web.core.inference;

import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.session.Value;

/**
 * A PropagationManager is responsible for propagate all changes of a Session
 * through the registered PSMethods of the case.
 * 
 * For each case a single instance of a PropagationManager implementation is
 * created/used. Even if the method propagate should not be called from outside
 * the case, the methods openPropagation and commitPropagation may be used to
 * enable optimized propagation if more than one fact is updated into the case.
 * 
 * @author Volker Belli
 */
public interface PropagationManager {

	/**
	 * Commits a propagation frame.
	 * <p>
	 * By commit the last propagation frame, the changes will be propagated
	 * throughout the PSMethods. There is no essential need to call this method
	 * manually.
	 * <p>
	 * This method can be called manually after setting a bunch of question
	 * values to enabled optimized propagation throughout the PSMethods. You
	 * must ensure that this method is called once for each call to
	 * openPropagation() under any circumstances (!) even in case of unexpected
	 * exceptions. Therefore always use the following snipplet:
	 * <p>
	 * 
	 * <pre>
	 * try {
	 * 	session.getPropagationController().openPropagation();
	 * 	// ... do your code here ...
	 * }
	 * finally {
	 * 	session.getPropagationController().commitPropagation();
	 * }
	 * </pre>
	 * 
	 * </p>
	 */
	void commitPropagation();

	/**
	 * Starts a new propagation frame.
	 * <p>
	 * Every propagation will be delayed until the last propagation frame has
	 * been commit. There is no essential need to call this method manually.
	 * <p>
	 * This method can be called manually before setting a bunch of question
	 * values to enabled optimized propagation throughout the PSMethods. You
	 * must ensure that to call commitPropagation() once for each call to this
	 * method under any circumstances (!) even in case of unexpected exceptions.
	 * Therefore always use the following snipplet:
	 * <p>
	 * 
	 * <pre>
	 * try {
	 * 	session.getPropagationController().openPropagation();
	 * 	// ... do your code here ...
	 * }
	 * finally {
	 * 	session.getPropagationController().commitPropagation();
	 * }
	 * </pre>
	 * 
	 * </p>
	 */
	void openPropagation();

	/**
	 * Starts a new propagation frame with a given time. If an other propagation
	 * frame has already been opened, the specified time is ignored. For more
	 * details see PropagationController.openProgagation()
	 * 
	 * @see PropagationController.openProgagation()
	 */
	void openPropagation(long time);

	/**
	 * Returns if there is an open propagation frame (and therefore the kernel
	 * is in propagation mode).
	 * 
	 * @return if the kernel is in propagation
	 */
	public boolean isInPropagation();

	/**
	 * Returns the propagation time of the current propagation. If no
	 * propagation frame has been opened, the time of the last propagation is
	 * returned.
	 * 
	 * @return the propagation time of that propagation frame
	 */
	long getPropagationTime();

	/**
	 * Propagates a change value of an {@link ValueObject} through the different
	 * PSMethods.
	 * <p>
	 * This method may cause other value propagations and therefore may be
	 * called recursevely. It is called after the value has been updated in the
	 * case. Thus the case already contains the new value.
	 * <p>
	 * <b>Do not call this method directly! It will be called by the case to
	 * propagate facts updated into the case.</b>
	 * 
	 * @param object the object that has been updated
	 * @param oldValue the old value of the object within the case
	 */
	void propagate(ValueObject object, Value oldValue);

	/**
	 * Propagates a change value of an {@link InterviewObject} through the
	 * different PSMethods.
	 * <p>
	 * This method may cause other value propagations and therefore may be
	 * called recursevely. It is called after the value has been updated in the
	 * case. Thus the case already contains the new value.
	 * <p>
	 * <b>Do not call this method directly! It will be called by the case to
	 * propagate facts updated into the case.</b>
	 * 
	 * @created 30.09.2010
	 * @param object the object that has been updated
	 * @param oldValue the old value of the object within the case
	 */
	void propagate(InterviewObject object, Value oldValue);

	/**
	 * Propagates a change value of an object through one selected PSMethod. All
	 * changes that will be derived by that PSMethod will be propagated normally
	 * throughout the whole system.
	 * <p>
	 * This method may be used after a problem solver has been added to
	 * distribute existing facts to him and enable him to derive additional
	 * facts.
	 * <p>
	 * <b>Do not call this method directly! It will be called by the case to
	 * propagate facts updated into the case.</b>
	 * 
	 * @param object the object that has been updated
	 * @param oldValue the old value of the object within the case
	 * @param psMethod the PSMethod the fact will be propagated to
	 */
	void propagate(ValueObject object, Value oldValue, PSMethod psMethod);
}
