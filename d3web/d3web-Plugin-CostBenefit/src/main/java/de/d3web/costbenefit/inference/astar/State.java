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
package de.d3web.costbenefit.inference.astar;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;

/**
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 22.06.2011
 */
public class State {

	private final Session session;
	private final Map<Question, Value> findings = new HashMap<Question, Value>();

	public State(Session session, Set<Question> preconditionQuestions) {
		this.session = session;
		for (Question q : preconditionQuestions) {
			findings.put(q, session.getBlackboard().getValue(q));
		}
	}

	public Session getSession() {
		return session;
	}

	/**
	 * Checks if the Question q has the value v in this state
	 * 
	 * @created 24.06.2011
	 * @param q Question
	 * @param v Value
	 * @return true, if the Question q has the Value v, false otherwise
	 */
	public boolean check(Question q, Value v) {
		Value storedValue = findings.get(q);
		if (storedValue == null && v == null) {
			return true;
		}
		else if (storedValue != null) {
			return storedValue.equals(v);
		}
		else {
			return false;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof State) {
			State otherState = (State) o;
			if (findings.size() == otherState.findings.size()) {
				for (Question q : findings.keySet()) {
					if (!findings.get(q).equals(otherState.findings.get(q))) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = findings.size();
		for (Entry<Question, Value> e : findings.entrySet()) {
			hash += e.getKey().hashCode();
			hash += e.getValue().hashCode();
		}
		return hash;
	}
}
