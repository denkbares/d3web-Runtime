package de.d3web.core.session.blackboard;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Value;
import de.d3web.indication.inference.PSMethodUserSelected;

/**
 * A factory to create {@link Fact} instances.
 * @author joba
 */
public class FactFactory {

	/**
	 * Creates a new fact assigning the specified {@link Value} to the specified 
	 * {@link TerminologyObject}. The specified source is responsible for the setting
	 * the value, which acts in the context of the specified {@link PSMethod}. 
	 * @param terminologyObject the specified {@link TerminologyObject} instance
	 * @param value the specified {@link Value} instance
	 * @param source the responsible source
	 * @param psMethod the fact is created in the context of the specified {@link PSMethod}
	 * @return a newly created {@link Fact} instance
	 */
	public static Fact createFact(TerminologyObject terminologyObject,
			Value value, Object source, PSMethod psMethod) {
		return new DefaultFact(terminologyObject, value, source, psMethod);
	}

	/**
	 * A new fact is created assigning the specified {@link Value} to the specified 
	 * {@link TerminologyObject}. The source and psMethod context of this fact is the
	 * user (i.e., {@link PSMethodUserSelected}).
	 * @param terminologyObject the specified {@link TerminologyObject} instance
	 * @param value the specified {@link Value} instance
	 * @return a newly created {@link Fact} instance
	 */
	public static Fact createUserEnteredFact(
			TerminologyObject terminologyObject, Value value) {
		return new DefaultFact(terminologyObject, value, PSMethodUserSelected
				.getInstance(), PSMethodUserSelected.getInstance());
	}

}
