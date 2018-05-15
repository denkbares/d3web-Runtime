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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.denkbares.utils.Log;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;

public class DefaultPropagationManager implements PropagationManager {

	private final Collection<PropagationListener> listeners = new LinkedList<>();

	private final Set<ValueObject> forcedPropagationEntries = new HashSet<>();

	private final Map<ValueObject, Value> postPropagationEntries = new HashMap<>();
	private final Map<InterviewObject, Value> postInterviewPropagationEntries = new LinkedHashMap<>();

	private final Map<ValueObject, Value> globalPropagationEntries = new HashMap<>();
	private final Map<InterviewObject, Value> globalInterviewPropagationEntries = new LinkedHashMap<>();

	private class PSMethodHandler {

		private boolean hasPropagated;
		private final PSMethod psMethod;
		private final Map<ValueObject, Value> propagationEntries = new HashMap<>();
		private final Set<ValueObject> hazardPropagationEntries = new HashSet<>();
		private final Map<InterviewObject, Value> interviewPropagationEntries = new LinkedHashMap<>();

		public PSMethodHandler(PSMethod psMethod) {
			this.psMethod = psMethod;
		}

		public void addPropagationEntry(ValueObject key, Value oldValue) {
			Value oldestValue = propagationEntries.get(key);
			if (oldestValue == null) {
				propagationEntries.put(key, oldValue);
			}
			// if we already have an entry,
			// check if it could be a hazard
			else if (oldestValue.equals(session.getBlackboard().getValue(key))) {
				if (!oldValue.equals(oldestValue)) {
					hazardPropagationEntries.add(key);
				}
			}
			else {
				// if it was a hazard, it is a normal change again
				hazardPropagationEntries.remove(key);
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

		public void setPropagated(boolean hasPropagated) {
			this.hasPropagated = hasPropagated;
		}

		public boolean hasPropagated() {
			return hasPropagated;
		}

		public boolean hasPropagationEntries() {
			return !interviewPropagationEntries.isEmpty() || !propagationEntries.isEmpty();
		}

		public void propagate() {
			Collection<PropagationEntry> entries = convertMapsToEntries(propagationEntries,
					hazardPropagationEntries,
					interviewPropagationEntries, true);

			try {
				// inform the listeners
				for (PropagationListener listener : listeners) {
					listener.propagating(session, psMethod, entries);
				}
				// propagate the changes, using the new interface
				getPSMethod().propagate(DefaultPropagationManager.this.session, entries);
			}
			catch (Throwable e) { // NOSONAR
				// we catch Throwable here, to also handle runtime errors such as StackOverflow
				Log.severe("internal error in pluggable problem solver #" +
						getPSMethod().getClass(), e);
			}
			finally {
				setPropagated(true);
			}
		}
	}

	private final Session session;
	private List<PSMethodHandler> psHandlers = null;
	private volatile boolean terminated = false;
	private int recursiveCounter = 0;
	private long propagationTime;
	private long timeOfNoReturn;

	public DefaultPropagationManager(Session session) {
		this.session = session;
		this.propagationTime = session.getCreationDate().getTime();
		this.timeOfNoReturn = this.propagationTime;
	}

	private void initHandlers() {
		this.psHandlers = new LinkedList<>();
		for (PSMethod psMethod : session.getPSMethods()) {
			psHandlers.add(new PSMethodHandler(psMethod));
		}
	}

	private void destroyHandlers() {
		this.psHandlers = null;
	}

	/**
	 * Starts a new propagation frame. <p> Every propagation will be delayed until the last
	 * propagation frame has been commit. There is no essential need to call this method manually.
	 * <p> This method can be called manually before setting a bunch of question values to enabled
	 * optimized propagation throughout the PSMethods. You must ensure that to call
	 * commitPropagation() once for each call to this method under any circumstances (!) even in
	 * case of unexpected exceptions. Therefore always use the following snipplet: <p> <p/>
	 * <pre>
	 * try {
	 * 	session.getPropagationManager().openPropagation();
	 * 	// ... do your code here ...
	 * }
	 * finally {
	 * 	session.getPropagationManager().commitPropagation();
	 * }
	 * </pre>
	 * <p/> </p>
	 */
	@Override
	public void openPropagation() {
		this.openPropagation(System.currentTimeMillis());
	}

	/**
	 * Starts a new propagation frame with a given time. If an other propagation frame has already
	 * been opened, the specified time is ignored. For more details see
	 * PropagationController.openProgagation()
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
	 * Commits a propagation frame. <p> By commit the last propagation frame, the changes will be
	 * propagated throughout the PSMethods. There is no essential need to call this method manually.
	 * <p> This method can be called manually after setting a bunch of question values to enabled
	 * optimized propagation throughout the PSMethods. You must ensure that this method is called
	 * once for each call to openPropagation() under any circumstances (!) even in case of
	 * unexpected exceptions. Therefore always use the following snipplet: <p> <p/>
	 * <pre>
	 * try {
	 * 	session.getPropagationManager().openPropagation();
	 * 	// ... do your code here ...
	 * }
	 * finally {
	 * 	session.getPropagationManager().commitPropagation();
	 * }
	 * </pre>
	 * <p/> </p>
	 */
	@Override
	public void commitPropagation() {
		try {
			if (this.recursiveCounter == 1) {
				try {
					distribute();
				}
				finally {
					destroyHandlers();
				}
			}
		}
		finally {
			// this is important: by reducing the value at the methods end
			// subsequent commits (caused by distribute the value changes)
			// will enter this method with values > 1.
			this.recursiveCounter--;
		}
	}

	/**
	 * This method does all the work as it notifies all problem solvers about all changes.
	 */
	private void distribute() {
		// inform listener about starting entries
		Collection<PropagationEntry> startingEntries = convertMapsToEntries(
				globalPropagationEntries,
				Collections.emptySet(),
				globalInterviewPropagationEntries, false);
		for (PropagationListener listener : listeners) {
			listener.propagationStarted(session, startingEntries);
		}

		try {
			while (true) {
				PSMethodHandler firstHandler;

				// find first handler that requires propagation
				firstHandler = findNextHandler();

				// if no such handler exists, start post propagation
				if (firstHandler == null) {
					// inform listener about post propagation entries
					Collection<PropagationEntry> entries = convertMapsToEntries(
							postPropagationEntries,
							Collections.emptySet(),
							postInterviewPropagationEntries, true);
					for (PropagationListener listener : listeners) {
						listener.postPropagationStarted(session, entries);
					}
					for (PSMethodHandler handler : this.psHandlers) {
						if (handler.getPSMethod() instanceof PostHookablePSMethod) {
							checkTerminated();
							PostHookablePSMethod postHookablePSMethod = (PostHookablePSMethod) handler
									.getPSMethod();
							postHookablePSMethod.postPropagate(session, entries);
						}
					}
					firstHandler = findNextHandler();
					if (firstHandler == null) break;
				}
				// otherwise continue with this handler
				checkTerminated();
				firstHandler.propagate();
			}
		}
		finally {
			// inform the listeners that we are finished now,
			// even if we have been terminated
			Collection<PropagationEntry> entries = convertMapsToEntries(
					globalPropagationEntries,
					Collections.emptySet(),
					globalInterviewPropagationEntries, true);
			forcedPropagationEntries.clear();
			for (PropagationListener listener : listeners) {
				listener.propagationFinished(session, entries);
			}
			Log.fine("Propagation finished for propagation time " + getPropagationTime());
		}
	}

	private Collection<PropagationEntry> convertMapsToEntries(
			Map<ValueObject, Value> propagationEntries,
			Set<ValueObject> hazardEntries,
			Map<InterviewObject, Value> interviewPropagationEntries,
			boolean clearEntries) {

		Collection<PropagationEntry> entries = new ArrayList<>(
				propagationEntries.size() + interviewPropagationEntries.size());
		for (Map.Entry<ValueObject, Value> change : propagationEntries.entrySet()) {
			ValueObject object = change.getKey();
			Value oldValue = change.getValue();
			Value value = session.getBlackboard().getValue(object);
			PropagationEntry entry = new PropagationEntry(object, oldValue, value);
			if (forcedPropagationEntries.contains(object)) entry.setForced(true);
			if (hazardEntries.contains(object)) entry.setHazard(true);
			entries.add(entry);
		}
		for (Entry<InterviewObject, Value> change : interviewPropagationEntries.entrySet()) {
			InterviewObject object = change.getKey();
			Value oldValue = interviewPropagationEntries.get(object);
			Value value = session.getBlackboard().getIndication(object);
			PropagationEntry entry = new PropagationEntry(object, oldValue, value);
			if (forcedPropagationEntries.contains(object)) entry.setForced(true);
			if (hazardEntries.contains(object)) entry.setHazard(true);
			entry.setStrategic(true);
			entries.add(entry);
		}
		if (clearEntries) {
			propagationEntries.clear();
			interviewPropagationEntries.clear();
			hazardEntries.clear();
		}
		return entries;
	}

	/**
	 * find first handler that requires propagation
	 *
	 * @return next handler requiring propagation
	 * @created 17.11.2010
	 */
	private PSMethodHandler findNextHandler() {
		for (PSMethodHandler handler : this.psHandlers) {
			if (handler.hasPropagationEntries() || !handler.hasPropagated()) {
				return handler;
			}
		}
		return null;
	}

	@Override
	public void forcePropagate(ValueObject object) {
		forcePropagate(object, session.getBlackboard().getValue(object));
	}

	@Override
	public void forcePropagate(ValueObject object, Value oldValue) {
		forcedPropagationEntries.add(object);
		propagate(object, oldValue, null);
	}

	/**
	 * Propagates a change value of an object through the different PSMethods.
	 * <p/>
	 * This method may cause other value propagations and therefore may be called recursively. It is
	 * called after the value has been updated in the case. Thus the case already contains the new
	 * value.
	 * <p/>
	 * <b>Do not call this method directly! It will be called by the case to propagate facts updated
	 * into the case.</b>
	 *
	 * @param object   the object that has been updated
	 * @param oldValue the old value of the object within the case
	 */
	@Override
	public void propagate(ValueObject object, Value oldValue) {
		propagate(object, oldValue, null);
	}

	/**
	 * Propagates a change value of an object through one selected PSMethod. All changes that will
	 * be derived by that PSMethod will be propagated normally throughout the whole system.
	 * <p/>
	 * This method may be used after a problem solver has been added to distribute existing facts to
	 * him and enable him to derive additional facts.
	 * <p/>
	 * <b>Do not call this method directly! It will be called by the case to propagate facts updated
	 * into the case.</b>
	 *
	 * @param object   the object that has been updated
	 * @param oldValue the old value of the object within the case
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
			if (!postPropagationEntries.containsKey(object)) {
				postPropagationEntries.put(object, oldValue);
			}
			if (!globalPropagationEntries.containsKey(object)) {
				globalPropagationEntries.put(object, oldValue);
			}
		}
		finally {
			// and commit the propagation frame
			commitPropagation();
		}
	}

	@Override
	public boolean isForced(ValueObject object) {
		return forcedPropagationEntries.contains(object);
	}

	@Override
	public long getPropagationTime() {
		return propagationTime;
	}

	@Override
	public long getPropagationTimeOfNoReturn() {
		return this.timeOfNoReturn;
	}

	@Override
	public void setPropagationTimeOfNoReturn(long time) {
		if (this.timeOfNoReturn != time) {
			this.timeOfNoReturn = time;
			Log.fine("Setting propagation time of no return to " + time);
		}
	}

	/**
	 * Returns if there is an open propagation frame (and therefore the kernel is in propagation
	 * mode).
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
			if (!postInterviewPropagationEntries.containsKey(object)) {
				postInterviewPropagationEntries.put(object, oldValue);
			}
			if (!globalInterviewPropagationEntries.containsKey(object)) {
				globalInterviewPropagationEntries.put(object, oldValue);
			}
		}
		finally {
			// and commit the propagation frame
			commitPropagation();
		}
	}

	@Override
	public void addListener(PropagationListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(PropagationListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void terminate() {
		this.terminated = true;
	}

	private void checkTerminated() throws SessionTerminatedException {
		if (Thread.interrupted()) {
			// we terminate if we got interrupted,
			// because session state is undefined afterwards
			terminate();
		}
		if (this.terminated) {
			throw new SessionTerminatedException();
		}
	}
}
