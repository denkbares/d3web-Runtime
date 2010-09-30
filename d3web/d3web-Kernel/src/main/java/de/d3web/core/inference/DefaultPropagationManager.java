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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;

public class DefaultPropagationManager implements PropagationManager {

	private class PSMethodHandler {

		private final PSMethod psMethod;
		private Map<ValueObject, Value> propagationEntries = new HashMap<ValueObject, Value>();
		private Map<InterviewObject, Value> interviewPropagationEntries = new HashMap<InterviewObject, Value>();

		public PSMethodHandler(PSMethod psMethod) {
			this.psMethod = psMethod;
		}

		public void addPropagationEntry(ValueObject key, Value oldValue) {
			// if we already have an entry,
			// combine the two entries to one or annihilate them
			if (!propagationEntries.containsKey(key)) {
				// we do not have a change for that object,
				// so simply remember this change
				propagationEntries.put(key, oldValue);
			}
		}

		public void addInterviewPropagationEntry(InterviewObject key, Value oldValue) {
			if (!interviewPropagationEntries.containsKey(key)) {
				interviewPropagationEntries.put(key, oldValue);
			}
		}

		public final PSMethod getPSMethod() {
			return psMethod;
		}

		public boolean hasPropagationEntries() {
			return propagationEntries.size() > 0;
		}

		public void propagate() {
			try {
				Collection<PropagationEntry> entries = new ArrayList<PropagationEntry>(
						propagationEntries.size());
				for (Map.Entry<ValueObject, Value> change : propagationEntries.entrySet()) {
					ValueObject object = change.getKey();
					Value oldValue = change.getValue();
					Value value = session.getBlackboard().getValue(object);
					PropagationEntry entry = new PropagationEntry(object, oldValue, value);
					entries.add(entry);
				}
				for (Map.Entry<InterviewObject, Value> change : interviewPropagationEntries.entrySet()) {
					InterviewObject object = change.getKey();
					Value oldValue = change.getValue();
					Value value = session.getBlackboard().getIndication(object);
					PropagationEntry entry = new PropagationEntry(object, oldValue, value);
					entries.add(entry);
				}
				propagationEntries.clear();
				// propagate the changes, using the new interface
				getPSMethod().propagate(DefaultPropagationManager.this.session, entries);
			}
			catch (Throwable e) { // NOSONAR because execution has to continue
									// after catch
				Logger.getLogger("Kernel").log(
						Level.SEVERE,
						"internal error in pluggable problem solver #" +
								getPSMethod().getClass(),
						e);
			}
		}
	}

	private final Session session;
	private List<PSMethodHandler> psHandlers = null;
	private int recursiveCounter = 0;
	private long propagationTime = 0;

	public DefaultPropagationManager(Session session) {
		this.session = session;
	}

	private void initHandlers() {
		this.psHandlers = new LinkedList<PSMethodHandler>();
		for (PSMethod psMethod : session.getPSMethods()) {
			psHandlers.add(new PSMethodHandler(psMethod));
		}
	}

	private void destroyHandlers() {
		this.psHandlers = null;
	}

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
	 * 	session.getPropagationManager().openPropagation();
	 * 	// ... do your code here ...
	 * }
	 * finally {
	 * 	session.getPropagationManager().commitPropagation();
	 * }
	 * </pre>
	 * 
	 * </p>
	 */
	@Override
	public void openPropagation() {
		this.openPropagation(System.currentTimeMillis());
	}

	/**
	 * Starts a new propagation frame with a given time. If an other propagation
	 * frame has already been opened, the specified time is ignored. For more
	 * details see PropagationController.openProgagation()
	 * 
	 * @see PropagationController.openProgagation()
	 */
	@Override
	public void openPropagation(long time) {
		this.recursiveCounter++;
		if (this.recursiveCounter == 1) {
			this.propagationTime = time;
			initHandlers();
		}
	}

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
	 * 	session.getPropagationManager().openPropagation();
	 * 	// ... do your code here ...
	 * }
	 * finally {
	 * 	session.getPropagationManager().commitPropagation();
	 * }
	 * </pre>
	 * 
	 * </p>
	 */
	@Override
	public void commitPropagation() {
		if (this.recursiveCounter == 1) {
			distribute();
			destroyHandlers();
		}
		// this is important: by reducing the value at the methods end
		// subsequent commits (caused by distribute the value changes)
		// will enter this method with values > 1.
		this.recursiveCounter--;
	}

	/**
	 * This method does all the work as it notifies all problem solvers about
	 * all changes.
	 */
	private void distribute() {
		while (true) {
			PSMethodHandler firstHandler = null;

			// find first handler that requires propagation
			for (PSMethodHandler handler : this.psHandlers) {
				if (handler.hasPropagationEntries()) {
					firstHandler = handler;
					break;
				}
			}

			// if no such handler exists, propagation if finished
			if (firstHandler == null) {
				break;
			}

			// otherwise continue with this handler
			firstHandler.propagate();
		}
	}

	/**
	 * Propagates a change value of an object through the different PSMethods.
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
	 * @param newValue the new value of the object within the case
	 */
	@Override
	public void propagate(ValueObject object, Value oldValue) {
		propagate(object, oldValue, null);
	}

	/**
	 * Propagates a change value of an object through one selected PSMethod. All
	 * changes that will be derived by that PSMethod will be propagated normally
	 * thoughout the whole system.
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
	 * @param newValue the new value of the object within the case
	 * @param psMethod the PSMethod the fact will be propagated to
	 */
	@Override
	public void propagate(ValueObject object, Value oldValue, PSMethod psMethod) {
		try {
			// open propagation frame
			openPropagation();

			// add the value change to each handler
			for (PSMethodHandler handler : this.psHandlers) {
				if (psMethod == null || handler.getPSMethod().equals(psMethod)) {
					handler.addPropagationEntry(object, oldValue);
				}
			}
		}
		finally {
			// and commit the propagation frame
			commitPropagation();
		}
	}

	/**
	 * Returns the propagation time of the current propagation. If no
	 * propagation frame has been opened, an {@link IllegalStateException} is
	 * thrown.
	 * 
	 * @return the propagation time of that propagation frame
	 * @throws IllegalStateException if no propagation frame has been opened
	 */
	@Override
	public long getPropagationTime() throws IllegalStateException {
		if (recursiveCounter == 0) {
			throw new IllegalStateException("no propagation frame opened");
		}
		return propagationTime;
	}

	/**
	 * Returns if there is an open propagation frame (and therefore the kernel
	 * is in propagation mode).
	 * 
	 * @return if the kernel is in propagation
	 */
	@Override
	public boolean isInPropagation() {
		return (recursiveCounter > 0);
	}

	@Override
	public void propagate(InterviewObject object, Value oldValue) {
		try {
			// open propagation frame
			openPropagation();

			// add the value change to each handler
			for (PSMethodHandler handler : this.psHandlers) {
				handler.addInterviewPropagationEntry(object, oldValue);
			}
		}
		finally {
			// and commit the propagation frame
			commitPropagation();
		}
	}

}
