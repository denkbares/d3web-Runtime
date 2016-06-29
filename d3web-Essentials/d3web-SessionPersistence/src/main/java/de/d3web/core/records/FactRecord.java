/*
 * Copyright (C) 2011 denkbares GmbH
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

	/**
	 * Returns the name of the object values by this fact.
	 * 
	 * @created 07.11.2012
	 * @return object name the fact has been created for
	 */
	public String getObjectName() {
		return objectName;
	}

	/**
	 * Returns the class-name of the problem solver or strategic solver that has
	 * created that fact. I may be null if the fact has been merged from
	 * multiple problem solvers. In this case you usually find also at least two
	 * additional facts for the same object, denoting the original facts before
	 * merging.
	 * 
	 * @created 07.11.2012
	 * @return name of the problem solver created this fact
	 */
	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	public String getPSM() {
		return psm;
	}

	/**
	 * @deprecated use {@link #getPSM()} instead
	 */
	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@Deprecated
	public String getPsm() {
		return psm;
	}

	/**
	 * The value of this fact that has been stored for the object denoted by
	 * {@link #getObjectName()}.
	 * 
	 * @created 07.11.2012
	 * @return the value of the object, represented by this fact
	 */
	public Value getValue() {
		return value;
	}

}
