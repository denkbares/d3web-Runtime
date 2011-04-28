/*
 * Copyright (C) 2011 denkbares GmbH, Germany
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
package de.d3web.casegeneration;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import de.d3web.core.inference.PropagationManager;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;

/**
 * A set of facts to be set simultaneously in the current session.
 * 
 * @author volker_belli
 * @created 20.04.2011
 */
public class FactSet {

	private final Collection<Fact> valueFacts = new LinkedList<Fact>();
	private final Collection<Fact> interviewFacts = new LinkedList<Fact>();

	/**
	 * Adds a value fact to this set.
	 * 
	 * @created 20.04.2011
	 * @param fact the fact to be added
	 */
	public void addValueFact(Fact fact) {
		valueFacts.add(fact);
	}

	/**
	 * Adds a value fact to this set.
	 * 
	 * @created 20.04.2011
	 * @param fact the fact to be added
	 */
	public void addInterviewFact(Fact fact) {
		interviewFacts.add(fact);
	}

	/**
	 * Returns the value facts of this set.
	 * 
	 * @created 20.04.2011
	 * @return the value facts
	 */
	public Collection<Fact> getValueFacts() {
		return Collections.unmodifiableCollection(this.valueFacts);
	}

	/**
	 * Returns the interview facts of this set.
	 * 
	 * @created 20.04.2011
	 * @return the value facts
	 */
	public Collection<Fact> getInterviewFacts() {
		return Collections.unmodifiableCollection(this.interviewFacts);
	}

	/**
	 * Removes all facts for the specified terminology object.
	 * 
	 * @created 25.04.2011
	 * @param object the terminology object to remove the facts for
	 */
	public void removeFacts(TerminologyObject object) {
		Iterator<Fact> iterator = valueFacts.iterator();
		while (iterator.hasNext()) {
			Fact fact = iterator.next();
			if (fact.getTerminologyObject() == object) {
				iterator.remove();
			}
		}
		iterator = interviewFacts.iterator();
		while (iterator.hasNext()) {
			Fact fact = iterator.next();
			if (fact.getTerminologyObject() == object) {
				interviewFacts.remove(fact);
			}
		}
	}

	/**
	 * Applies this set of facts to the specified session at the current time.
	 * This method sets all the facts to the blackboard of this session within
	 * one transaction.
	 * 
	 * @created 20.04.2011
	 * @param session the session to be modified
	 */
	public void apply(Session session) {
		apply(session, new Date());
	}

	/**
	 * Applies this set of facts to the specified session at the specified time.
	 * This method sets all the facts to the blackboard of this session within
	 * one transaction.
	 * 
	 * @created 20.04.2011
	 * @param session the session to be modified
	 * @param time the time the values shall be set
	 */
	public void apply(Session session, Date time) {
		PropagationManager manager = session.getPropagationManager();
		Blackboard blackboard = session.getBlackboard();
		try {
			manager.openPropagation(time.getTime());
			for (Fact fact : this.valueFacts) {
				blackboard.addValueFact(fact);
			}
			for (Fact fact : this.interviewFacts) {
				blackboard.addInterviewFact(fact);
			}
		}
		finally {
			manager.commitPropagation();
		}
	}
}
