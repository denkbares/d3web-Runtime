package de.d3web.core.inference;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;

public class DefaultPropagationManager implements PropagationManager {

	private class PSMethodHandler {

		private final PSMethod psMethod;
		private Map<TerminologyObject, PropagationEntry> propagationEntries = new HashMap<TerminologyObject, PropagationEntry>();

		public PSMethodHandler(PSMethod psMethod) {
			this.psMethod = psMethod;
		}

		public void addPropagationEntry(PropagationEntry newChange) {
			TerminologyObject key = newChange.getObject();
			PropagationEntry oldChange = propagationEntries.get(key);
			// if we already have an entry,
			// combine the two entries to one or annihilate them
			if (oldChange != null) {
				// assert to be sure to handle only valid propagation changes
				// TODO: use assert after D3WebCase.getValue() has been
				// corrected
				// assert Arrays.equals(oldChange.getNewValue(),
				// newChange.getOldValue());
				// check if they annihilate: a-->b && b-->a ==> no change
				if (oldChange.getOldValue().equals(newChange.getNewValue())) {
					propagationEntries.remove(key);
				}
				// otherwise we have a chain: a-->b && b-->c ==> a-->c
				else {
					PropagationEntry replaceChange = new PropagationEntry(
							key,
							oldChange.getOldValue(),
							newChange.getNewValue());
					propagationEntries.put(key, replaceChange);
				}
			}
			else {
				// we do not have a change for that object,
				// so simply remember this change
				propagationEntries.put(key, newChange);
			}
		}

		public final PSMethod getPSMethod() {
			return psMethod;
		}

		public boolean hasPropagationEntries() {
			return propagationEntries.size() > 0;
		}

		public void propagate() {
			Collection<PropagationEntry> entries = propagationEntries.values();
			propagationEntries = new HashMap<TerminologyObject, PropagationEntry>();
			try {
				// propagate the changes, using the new interface
				getPSMethod().propagate(DefaultPropagationManager.this.session, entries);
			}
			catch (Throwable e) {
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
	 * 	theCase.getPropagationManager().openPropagation();
	 * 	// ... do your code here ...
	 * }
	 * finally {
	 * 	theCase.getPropagationManager().commitPropagation();
	 * }
	 * </pre>
	 * 
	 * </p>
	 */
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
	 * 	theCase.getPropagationManager().openPropagation();
	 * 	// ... do your code here ...
	 * }
	 * finally {
	 * 	theCase.getPropagationManager().commitPropagation();
	 * }
	 * </pre>
	 * 
	 * </p>
	 */
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
	public void propagate(TerminologyObject object, Object oldValue, Object newValue) {
		propagate(object, oldValue, newValue, null);
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
	public void propagate(TerminologyObject object, Object oldValue, Object newValue, PSMethod psMethod) {
		try {
			// open propagation frame
			openPropagation();

			// add the value change to each handler
			PropagationEntry change = new PropagationEntry(object, oldValue, newValue);
			for (PSMethodHandler handler : this.psHandlers) {
				if (psMethod == null || handler.getPSMethod().equals(psMethod)) {
					handler.addPropagationEntry(change);
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
	public long getPropagationTime() throws IllegalStateException {
		if (recursiveCounter == 0) throw new IllegalStateException("no propagation fram opened");
		return propagationTime;
	}

}
