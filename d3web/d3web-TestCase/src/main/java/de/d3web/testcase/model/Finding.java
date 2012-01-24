/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.testcase.model;

import java.util.Date;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Value;

/**
 * An interface describing a single finding (fact) that should be restored at a
 * specific date when the {@link TestCase} is repeated.
 * 
 * @author Volker Belli & Markus Friedrich (denkbares GmbH)
 * @created 23.01.2012
 */
public interface Finding {

	/**
	 * Returns the terminology object of this Finding
	 * 
	 * @created 23.01.2012
	 * @return TerminologyObject of this Finding
	 */
	TerminologyObject getTerminologyObject();

	/**
	 * Retunrs the Value of this Finding
	 * 
	 * @created 23.01.2012
	 * @return Value of this Finding
	 */
	Value getValue();

	/**
	 * Returns the Date of this Finding
	 * 
	 * @created 23.01.2012
	 * @return Date of this Finding
	 */
	Date getDate();

}
