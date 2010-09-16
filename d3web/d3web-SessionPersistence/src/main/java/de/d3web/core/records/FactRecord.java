/*
 * Copyright (C) 2010 denkbares GmbH
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
