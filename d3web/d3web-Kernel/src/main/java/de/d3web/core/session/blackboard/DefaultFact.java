package de.d3web.core.session.blackboard;

import de.d3web.core.kr.TerminologyObject;
import de.d3web.core.kr.Value;
import de.d3web.kernel.psMethods.PSMethod;

public class DefaultFact implements Fact {
	
	private final TerminologyObject terminologyObject;
	private final Value value;
	private final Object source;
	private final PSMethod psMethod;
	
	public DefaultFact(TerminologyObject terminologyObject, Value value, Object source, PSMethod psMethod) {
		super();
		this.terminologyObject = terminologyObject;
		this.value = value;
		this.source = source;
		this.psMethod = psMethod;
	}

	@Override
	public PSMethod getPSMethod() {
		return psMethod;
	}

	@Override
	public Object getSource() {
		return source;
	}

	@Override
	public TerminologyObject getTerminologyObject() {
		return terminologyObject;
	}

	@Override
	public Value getValue() {
		return value;
	}

}
