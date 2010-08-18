/*
 * Copyright (C) 2010 denkbares GmbH, Würzburg, Germany
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

package de.d3web.core.session.blackboard;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Value;

public interface Fact {

	/**
	 * Returns the terminology object the fact is created for.
	 * 
	 * @return the fact's terminology object
	 */
	TerminologyObject getTerminologyObject();

	/**
	 * Returns the value of the fact. The possible value types depends on the
	 * type of terminology object the fact is created for. It may be:
	 * <ul>
	 * <li><b>SolutionState</b> if the terminology object is a Solution
	 * <li><b>Answer</b> or <b>IndicationState</b> if the terminology object is
	 * a Question
	 * <li><b>Indication</b> if the terminology object is a QContainer
	 * </ul>
	 * 
	 * @return the value of the fact
	 */
	Value getValue();

	/**
	 * Returns the source of this fact.
	 * <p>
	 * Consider that the source must be unique. Therefore each source can
	 * produce only one valid fact (at least for each terminology object). If a
	 * new fact with an already existing source for this terminology object is
	 * added to the blackboard, the existing fact is overwritten. This makes it
	 * easy to implement the update of facts as they should change. Here some
	 * examples of fact sources:
	 * <ul>
	 * <li>Rule: for rule-based problem solvers. Setting a new fact will
	 * automatically overwrite the existing fact derived by this rule.
	 * <li>XCLModel: for set-covering knowledge. The model can simply set an
	 * updated value for the fact, overwriting any previously set value.
	 * <li>PSMethodUser: for user-provided facts. The user can provide one value
	 * per terminology object, overwriting his previous setting.
	 * </ul>
	 * 
	 * @return the (unique) fact source
	 */
	Object getSource();

	/**
	 * Returns the PSMethod created the fact. This PSMethod is responsible to
	 * handle this fact, e.g. when merging facts using
	 * {@link PSMethod#mergeFacts(Fact[])}.
	 * 
	 * @return the PSMethod created the fact
	 */
	PSMethod getPSMethod();

	int hashCode();

	boolean equals(Object o);
}
