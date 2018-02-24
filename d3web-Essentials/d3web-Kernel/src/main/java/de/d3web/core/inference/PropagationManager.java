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
import de.d3web.core.session.blackboard.Blackboard;

/**
 * A PropagationManager is responsible for propagate all changes of a Session
 * through the registered PSMethods of the case.
 * <p>
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
	 * <p>
	 * <pre>
	 * try {
	 * 	session.getPropagationController().openPropagation();
	 * 	// ... do your code here ...
	 * }
	 * finally {
	 * 	session.getPropagationController().commitPropagation();
	 * }
	 * </pre>
	 * <p>
	 * </p>
	 *
	 * @throws SessionTerminatedException if the session has been terminated
	 *                                    manually and any further propagation is prevented. For nested
	 *                                    propagations, the exception is only thrown if this method closes
	 *                                    the last opened propagation frame.
	 */
	void commitPropagation() throws SessionTerminatedException;

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
	 * <p>
	 * <pre>
	 * try {
	 * 	session.getPropagationController().openPropagation();
	 * 	// ... do your code here ...
	 * }
	 * finally {
	 * 	session.getPropagationController().commitPropagation();
	 * }
	 * </pre>
	 * <p>
	 * </p>
	 */
	void openPropagation();

	/**
	 * Starts a new propagation frame with a given time. If an other propagation
	 * frame has already been opened, the specified time is ignored. For more
	 * details see PropagationController.openPropagation()
	 */
	void openPropagation(long time);

	/**
	 * Returns if there is an open propagation frame (and therefore the kernel
	 * is in propagation mode).
	 *
	 * @return if the kernel is in propagation
	 */
	boolean isInPropagation();

	/**
	 * Returns the propagation time of the current propagation. If no
	 * propagation frame has been opened, the time of the last propagation is
	 * returned.
	 *
	 * @return the propagation time of that propagation frame
	 */
	long getPropagationTime();

	/**
	 * Returns the propagation time of no return. Facts that were set/propagated
	 * at that time or earlier cannot be guaranteed to change in the same way or
	 * cause the same changes to other facts as they would, if they were  set
	 * after that time. If you want to change facts set at that time or before,
	 * it is recommended to just create a new session and replay the protocol to
	 * just before the facts were first set.
	 *
	 * @return the propagation time of no return, were facts set before that time
	 * will no longer change as expected
	 */
	long getPropagationTimeOfNoReturn();

	/**
	 * Sets the propagation time of no return. This method should only be used by
	 * problem solvers (extending PSMethod).
	 *
	 * @see #getPropagationTimeOfNoReturn()
	 */
	void setPropagationTimeOfNoReturn(long timeOfNoReturn);

	/**
	 * Terminates all propagation of this PropagationManager. The method returns
	 * immediately, also it might take some time to get the propagation
	 * terminated. After the propagation is terminated, the session is in a
	 * uncertain state: the facts in the blackboard it does not reflect the
	 * knowledge stored in the knowledge base, because the propagation has not
	 * been finished correctly.
	 * <p>
	 * After this call, every future propagation of this session will lead to a
	 * {@link SessionTerminatedException}. This usually occurs in one of the
	 * following scenarios (but not limited to):
	 * <ul>
	 * <li>committing a propagation using
	 * {@link PropagationManager#commitPropagation()}
	 * <li>adding a fact to the blackboard with no propagation frame has been
	 * opened using
	 * {@link Blackboard#addValueFact(de.d3web.core.session.blackboard.Fact)} or
	 * {@link Blackboard#addInterviewFact(de.d3web.core.session.blackboard.Fact)}
	 * </ul>
	 * <p>
	 * If a propagation is currently running in another thread, the partially
	 * propagated results will remain. The other thread will get a
	 * {@link SessionTerminatedException} soon after calling this method.
	 *
	 * @created 14.02.2013
	 * @see SessionTerminatedException
	 */
	void terminate();

	/**
	 * Returns whether the given ValueObject is currently set to be forced for
	 * propagation (this means the propagation will happen whether or not the
	 * value of the {@link ValueObject} has changed or not).
	 *
	 * @param object the {@link ValueObject} for which should be checked if it
	 *               is forced or not
	 * @return if the given {@link ValueObject} should be forced for propagation
	 * @created 21.03.2013
	 */
	boolean isForced(ValueObject object);

	/**
	 * This method does the same as
	 * {@link PropagationManager#propagate(ValueObject, Value)}, but produced
	 * {@link PropagationEntry}s will always indicate a change. This forces the
	 * propagation in PSMethods, that might otherwise only propagate in case of
	 * a change in the value of the {@link ValueObject}.
	 *
	 * @param object   the object that has been updated
	 * @param oldValue the old value of the object within the case
	 * @throws SessionTerminatedException if the session has been terminated
	 *                                    manually and any further propagation is prevented. The exception
	 *                                    is only thrown if this method is not called inside a opened
	 *                                    propagation frame. In this case the exception is thrown when the
	 *                                    propagation will be committed using {@link #commitPropagation()}
	 * @created 07.12.2012
	 */
	void forcePropagate(ValueObject object, Value oldValue) throws SessionTerminatedException;

	/**
	 * This method does the same as
	 * {@link PropagationManager#propagate(ValueObject, Value)}, but produced
	 * {@link PropagationEntry}s will always indicate a change. This forces the
	 * propagation in PSMethods, that might otherwise only propagate in case of
	 * a change in the value of the {@link ValueObject}.
	 *
	 * @param object the object that has been updated
	 * @throws SessionTerminatedException if the session has been terminated
	 *                                    manually and any further propagation is prevented. The exception
	 *                                    is only thrown if this method is not called inside a opened
	 *                                    propagation frame. In this case the exception is thrown when the
	 *                                    propagation will be committed using {@link #commitPropagation()}
	 * @created 07.12.2012
	 */
	void forcePropagate(ValueObject object) throws SessionTerminatedException;

	/**
	 * Propagates a change value of an {@link ValueObject} through the different
	 * PSMethods.
	 * <p>
	 * This method may cause other value propagations and therefore may be
	 * called recursively. It is called after the value has been updated in the
	 * case. Thus the case already contains the new value.
	 * <p>
	 * <b>Do not call this method directly! It will be called by the case to
	 * propagate facts updated into the case.</b>
	 *
	 * @param object   the object that has been updated
	 * @param oldValue the old value of the object within the case
	 * @throws SessionTerminatedException if the session has been terminated
	 *                                    manually and any further propagation is prevented. The exception
	 *                                    is only thrown if this method is not called inside a opened
	 *                                    propagation frame. In this case the exception is thrown when the
	 *                                    propagation will be committed using {@link #commitPropagation()}
	 */
	void propagate(ValueObject object, Value oldValue) throws SessionTerminatedException;

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
	 * @param object   the object that has been updated
	 * @param oldValue the old value of the object within the case
	 * @throws SessionTerminatedException if the session has been terminated
	 *                                    manually and any further propagation is prevented. The exception
	 *                                    is only thrown if this method is not called inside a opened
	 *                                    propagation frame. In this case the exception is thrown when the
	 *                                    propagation will be committed using {@link #commitPropagation()}
	 * @created 30.09.2010
	 */
	void propagate(InterviewObject object, Value oldValue) throws SessionTerminatedException;

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
	 * @param object   the object that has been updated
	 * @param oldValue the old value of the object within the case
	 * @param psMethod the PSMethod the fact will be propagated to
	 * @throws SessionTerminatedException if the session has been terminated
	 *                                    manually and any further propagation is prevented. The exception
	 *                                    is only thrown if this method is not called inside a opened
	 *                                    propagation frame. In this case the exception is thrown when the
	 *                                    propagation will be committed using {@link #commitPropagation()}
	 */
	void propagate(ValueObject object, Value oldValue, PSMethod psMethod) throws SessionTerminatedException;

	/**
	 * Adds a {@link PropagationListener} to this PropagationManager.
	 *
	 * @param listener the listener to be added
	 * @created 27.03.2012
	 */
	void addListener(PropagationListener listener);

	/**
	 * Removes a {@link PropagationListener} from this PropagationManager.
	 *
	 * @param listener the listener to be removed
	 * @created 14.02.2013
	 */
	void removeListener(PropagationListener listener);
}
