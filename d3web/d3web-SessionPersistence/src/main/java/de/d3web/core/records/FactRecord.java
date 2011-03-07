/*
 * Copyright (C) 2010 denkbares GmbH
 * 
 * This is free software for non commercial use
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 */
package de.d3web.core.records;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Value;

/**
 * This is a persistent version of a fact.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 15.09.2010
 */
public class FactRecord {

	private final String objectName;
	private final String psm;
	private final Value value;

	public FactRecord(String objectName, String psm, Value value) {
		this.objectName = objectName;
		this.psm = psm;
		this.value = value;
	}

	public FactRecord(TerminologyObject object, String psm, Value value) {
		this(object.getName(), psm, value);
	}

	public String getObjectName() {
		return objectName;
	}

	public String getPsm() {
		return psm;
	}

	public Value getValue() {
		return value;
	}

}
