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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;

/**
 * Encapsulates rules of one PSMethod and one MethodKind to one KnowlegeSlice
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class RuleSet implements KnowledgeSlice {

	private static int count = 0;
	private final List<Rule> rules = new ArrayList<Rule>();
	private final Class<? extends PSMethod> psContext;
	private final String id;

	public RuleSet(Class<? extends PSMethod> psContext) {
		this.psContext = psContext;
		id = "RuleSet" + count++;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Class<? extends PSMethod> getProblemsolverContext() {
		return psContext;
	}

	@Override
	public boolean isUsed(Session session) {
		for (Rule r : rules) {
			if (r.isUsed(session)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void remove() {
		for (Rule r : new ArrayList<Rule>(rules)) {
			r.remove();
		}
	}

	public static RuleSet getRuleSet(KnowledgeBase kb, Class<? extends PSMethod> problemsolver, MethodKind methodKind) {
		Object knowledge = kb.getKnowledge(problemsolver, methodKind);
		if (knowledge == null) {
			return new RuleSet(problemsolver);
		}
		else {
			return (RuleSet) knowledge;
		}
	}

	public List<Rule> getRules() {
		return Collections.unmodifiableList(rules);
	}

	public void removeRule(Rule r) {
		rules.remove(r);
	}

	public boolean isEmpty() {
		return rules.isEmpty();
	}

	public void addRule(Rule r) {
		rules.add(r);
	}

	@Override
	public String toString() {
		return rules.toString();
	}
}
