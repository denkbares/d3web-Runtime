/*
 * Copyright (C) 2013 denkbares GmbH, Germany
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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.collections.CountingSet;
import de.d3web.core.inference.LoopTerminator.LoopStatus;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.blackboard.SessionObject;

/**
 * This {@link PropagationListener} observes the reasoning process for endless
 * loops (cyclic knowledge) and terminates the propagation if a loop has been
 * detected, using the method {@link PropagationManager#terminate()}.
 * <p>
 * The loop detection algorithm is somewhat straight-forward and naive. It
 * checks how often each particular value-object will be propagated. If this
 * occurs more time than a knowledge-base specific limit the loop is detected.
 * The limit is calculated out of the knowledge base using a pessimistic
 * heuristic. This means that the cycle might been executed several times,
 * before it will be detected.
 * <p>
 * Please note that a individual instance of this listener is required for each
 * session. A single instance must not be used for multiple session.
 * 
 * @author Volker Belli (denkbares GmbH)
 * @created 14.02.2013
 */
public class LoopTerminator implements SessionObjectSource<LoopStatus> {

	private class Listener implements PropagationListener {

		@Override
		public void propagationStarted(Session session, Collection<PropagationEntry> entries) {
			getLoopStatus(session).init(entries);
		}

		@Override
		public void postPropagationStarted(Session session, Collection<PropagationEntry> entries) {
		}

		@Override
		public void propagationFinished(Session session, Collection<PropagationEntry> entries) {
		}

		@Override
		public void propagating(Session session, PSMethod psMethod, Collection<PropagationEntry> entries) {
			getLoopStatus(session).check(psMethod, entries);
		}

	}

	public static class LoopStatus implements SessionObject {

		private final Session session;
		private final PSMethod observedPSMethod;
		private final CountingSet<TerminologyObject> counter = new CountingSet<TerminologyObject>();

		private Integer maxPropagations = null; // lazy init
		private boolean terminated = false;

		private LoopStatus(Session session) {
			this.session = session;
			// only observe propagations on the psmethod with the highest prio,
			// because this will be called first for each newly created fact
			List<? extends PSMethod> psMethods = session.getPSMethods();
			this.observedPSMethod = psMethods.isEmpty() ? null : psMethods.get(0);
		}

		/**
		 * Returns if the session has already been terminated by this detector.
		 * 
		 * @created 14.02.2013
		 * @return if session has already been terminated
		 */
		public boolean hasTerminated() {
			return this.terminated;
		}

		/**
		 * Returns the objects that are identified to be participants in the
		 * loop. It is not guaranteed that all objects are detected, but the
		 * ones in the loop's "hot spot" are returned.
		 * 
		 * @created 14.02.2013
		 * @return the looping objects
		 */
		public Collection<TerminologyObject> getLoopObjects() {
			if (!terminated) return null;
			// find max propagation occurences
			int max = 0;
			for (TerminologyObject object : counter) {
				max = Math.max(max, counter.getCount(object));
			}
			// get all objects with these max propagations (or slightly below)
			Collection<TerminologyObject> result = new HashSet<TerminologyObject>();
			for (TerminologyObject object : counter) {
				if (counter.getCount(object) >= max - 1000) {
					result.add(object);
				}
			}
			return result;
		}

		private void init(Collection<PropagationEntry> entries) {
			// preserve state if already has been terminated
			if (terminated) return;
			counter.clear();
		}

		private void check(PSMethod psMethod, Collection<PropagationEntry> entries) {
			// preserve state if already has been terminated
			if (terminated) return;
			// only observe the most prioritized solver
			if (psMethod != observedPSMethod) return;

			// otherwise increase counter for each particular object
			// until termination
			for (PropagationEntry entry : entries) {
				// only count value changes, not strategic ones
				// because strategic ones cannot create cycles
				if (entry.isStrategic()) continue;
				int count = counter.inc(entry.getObject());
				if (count > getMaxPropagations()) {
					// set flag to prevent status of loop detection
					terminated = true;
					session.getPropagationManager().terminate();
					Logger.getLogger(this.getClass().getName()).severe(
							"Propagation loop detected for knowledge base '"
									+ session.getKnowledgeBase().getName()
									+ "'. The following objects are mainly involved: "
									+ getLoopObjects());
				}
			}
		}

		private int getMaxPropagations() {
			if (maxPropagations == null) {
				maxPropagations = calculateMaxPropagations(session.getKnowledgeBase());
			}
			return maxPropagations;
		}

	}

	private static final LoopTerminator instance = new LoopTerminator();
	private final PropagationListener listener = new Listener();

	public static LoopTerminator getInstance() {
		return instance;
	}

	@Override
	public LoopStatus createSessionObject(Session session) {
		return new LoopStatus(session);
	}

	/**
	 * Pessimistic heuristic, how much propagation cycles are required in the
	 * maximum to come to a stable state.
	 * 
	 * @created 14.02.2013
	 * @param knowledgeBase the knowledge base to calculate the maximum from
	 * @return the pessimistic cycle limit
	 */
	private static int calculateMaxPropagations(KnowledgeBase knowledgeBase) {
		int objectCount = knowledgeBase.getManager().getObjects(ValueObject.class).size();
		return objectCount * 5 + 1000;
	}

	/**
	 * Attaches the loop detection to the specified session. After this call,
	 * for every newly started propagation, the session's
	 * {@link PropagationManager} will be terminated is a loop will be detected.
	 * 
	 * @created 14.02.2013
	 * @param session the session to activate the loop detection for
	 */
	public void attach(Session session) {
		// create flyweight
		getLoopStatus(session);
		session.getPropagationManager().addListener(listener);
	}

	/**
	 * Attaches the loop detection to all future sessions that will be created
	 * through the {@link SessionFactory} class from the time this method has
	 * been called.
	 * 
	 * @created 14.02.2013
	 */
	public void attachToNewSessions() {
		SessionFactory.addPropagationListener(listener);
	}

	/**
	 * Detaches the loop detection to the specified session. After this call any
	 * previously added loop detection has been deactivated. If the session's
	 * {@link PropagationManager} is already being terminated, this termination
	 * will remain.
	 * 
	 * @created 14.02.2013
	 * @param session the session to deactivate the loop detection for
	 */
	public void detach(Session session) {
		session.getPropagationManager().removeListener(listener);
	}

	/**
	 * Get the loop detection status for a specified session. This may be used
	 * to check is a loop has already been detected and the objects involved in
	 * the loop.
	 * 
	 * @created 14.02.2013
	 * @param session the session to get the loop detection status for
	 * @return the loop detection status
	 */
	public LoopStatus getLoopStatus(Session session) {
		return session.getSessionObject(this);
	}

}
