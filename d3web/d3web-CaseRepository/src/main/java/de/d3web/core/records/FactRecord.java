package de.d3web.core.records;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Value;

public class FactRecord {

	private TerminologyObject object;
	private String psm;
	private Value value;

	public TerminologyObject getObject() {
		return object;
	}

	public String getPsm() {
		return psm;
	}

	public Value getValue() {
		return value;
	}

	public FactRecord(TerminologyObject object, String psm, Value value) {
		super();
		this.object = object;
		this.psm = psm;
		this.value = value;
	}

}
