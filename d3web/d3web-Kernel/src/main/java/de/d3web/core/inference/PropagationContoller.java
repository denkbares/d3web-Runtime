package de.d3web.core.inference;

import de.d3web.core.knowledge.TerminologyObject;

/**
 * A PropagationController is responsible for propagate all changes of a Session
 * through the registered PSMethods of the case.
 * 
 * For each case a single instance of a PropagationContoller implementation is
 * created/used. Even if the method propagate should not be called from outside
 * the case, the methods openPropagation and commitPropagation may be used to
 * enable optimized propagation if more than one fact is updated into the case.
 * 
 * @author Volker Belli
 */
public interface PropagationContoller {

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
	 * 	theCase.getPropagationController().openPropagation();
	 * 	// ... do your code here ...
	 * }
	 * finally {
	 * 	theCase.getPropagationController().commitPropagation();
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
	 * 	theCase.getPropagationController().openPropagation();
	 * 	// ... do your code here ...
	 * }
	 * finally {
	 * 	theCase.getPropagationController().commitPropagation();
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
	 * Returns the propagation time of the current propagation. If no
	 * propagation frame has been opened, an {@link IllegalStateException} is
	 * thrown.
	 * 
	 * @return the propagation time of that propagation frame
	 * @throws IllegalStateException if no propagation frame has been opened
	 */
	long getPropagationTime() throws IllegalStateException;

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
	void propagate(TerminologyObject object, Object oldValue, Object newValue);

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
	 * @param newValue the new value of the object within the case
	 * @param psMethod the PSMethod the fact will be propagated to
	 */
	void propagate(TerminologyObject object, Object oldValue, Object newValue, PSMethod psMethod);
}
