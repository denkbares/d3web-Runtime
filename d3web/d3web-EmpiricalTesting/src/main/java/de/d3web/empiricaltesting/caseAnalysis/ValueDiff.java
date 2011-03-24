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
package de.d3web.empiricaltesting.caseAnalysis;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Value;

/**
 * Stores the expected and the actually derived value of a
 * {@link TerminologyObject}.
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 * @created 24.03.2011
 */
public class ValueDiff {

	Value expected;
	Value derived;

	public ValueDiff(Value expected, Value derived) {
		this.expected = expected;
		this.derived = derived;
	}

	public boolean differ() {
		if (this.expected != null && this.derived != null) {
			return expected.equals(derived);
		}
		else if (this.expected == null && this.derived == null) {
			return false;
		}
		else {
			return true;
		}
	}

	@Override
	public String toString() {
		if (differ()) {
			return "exp: " + this.expected + " but was " + this.derived;
		}
		else {
			return "eq val: " + this.expected;
		}

	}
}
