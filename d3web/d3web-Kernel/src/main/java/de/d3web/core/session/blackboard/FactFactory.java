package de.d3web.core.session.blackboard;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Value;

public class FactFactory {

	public static Fact createFact(TerminologyObject terminologyObject, Value value, Object source, PSMethod psMethod) {
		return new DefaultFact(terminologyObject, value, source, psMethod);
	}

}
