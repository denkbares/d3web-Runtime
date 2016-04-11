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

package de.d3web.core.inference;

import java.util.Collection;
import java.util.Collections;

import de.d3web.collections.IdentitySet;

/**
 * Encapsulates rules of one PSMethod and one KnowledgeKind to one KnowlegeSlice
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class RuleSet implements KnowledgeSlice {

	// rules that equal are entered into the set, but the same rule
	// (reference-identity) will not be inserted twice
	private final Collection<Rule> rules = new IdentitySet<Rule>();

	public Collection<Rule> getRules() {
		return Collections.unmodifiableCollection(rules);
	}

	public void removeRule(Rule r) {
		rules.remove(r);
	}

	public boolean isEmpty() {
		return rules.isEmpty();
	}

	/**
	 * Adds a Rule to the RuleSet
	 * 
	 * @created 15.02.2011
	 * @param r Rule
	 * @throws NullPointerException when the rule is null
	 */
	public void addRule(Rule r) {
		if (r == null) throw new NullPointerException();
		rules.add(r);
	}

	@Override
	public String toString() {
		return rules.toString();
	}
}
