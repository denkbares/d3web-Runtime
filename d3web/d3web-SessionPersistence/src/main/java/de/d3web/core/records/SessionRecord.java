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

import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.SessionHeader;
import de.d3web.core.session.blackboard.Fact;

/**
 * Represents a persistent session
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 20.09.2010
 */
public interface SessionRecord extends SessionHeader {

	/**
	 * Adds a value {@link Fact}
	 * 
	 * @created 05.08.2011
	 * @param fact
	 */
	void addValueFact(FactRecord fact);

	/**
	 * Adds an interview {@link Fact}
	 * 
	 * @created 05.08.2011
	 * @param fact
	 */
	void addInterviewFact(FactRecord fact);

	/**
	 * Returns all value facts
	 * 
	 * @created 05.08.2011
	 * @return List of value facts
	 */
	List<FactRecord> getValueFacts();

	/**
	 * Return all interview facts
	 * 
	 * @created 05.08.2011
	 * @return List of interview facts
	 */
	List<FactRecord> getInterviewFacts();

	/**
	 * Sets the name of the SessionRecord
	 * 
	 * @created 05.08.2011
	 * @param name
	 */
	void setName(String name);

	/**
	 * Returns all {@link Solution} instances, that hold one of the specified
	 * states
	 * 
	 * @param statea the States the diagnoses must have to be returned
	 * @param kb {@link KnowledgeBase}
	 * @return a list of diagnoses in this case that have one of the specified
	 *         states
	 */
	public List<Solution> getSolutions(KnowledgeBase kb, State... states);

}