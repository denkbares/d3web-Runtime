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
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionHeader;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.builder.SessionBuilder;

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
	 * @param fact the fact to add
	 * @created 05.08.2011
	 */
	void addValueFact(FactRecord fact);

	/**
	 * Adds an interview {@link Fact}
	 *
	 * @param fact the fact to add
	 * @created 05.08.2011
	 */
	void addInterviewFact(FactRecord fact);

	/**
	 * Returns all value facts
	 *
	 * @return List of value facts
	 * @created 05.08.2011
	 */
	List<FactRecord> getValueFacts();

	/**
	 * Return all interview facts
	 *
	 * @return List of interview facts
	 * @created 05.08.2011
	 */
	List<FactRecord> getInterviewFacts();

	/**
	 * Returns all {@link Solution} instances, that hold one of the specified states
	 *
	 * @param states the States the diagnoses must have to be returned
	 * @param kb     {@link KnowledgeBase}
	 * @return a list of diagnoses in this case that have one of the specified states
	 */
	List<Solution> getSolutions(KnowledgeBase kb, State... states);

	/**
	 * Creates a new SessionBuilder for this record instance, that can be used to re-create a running session from this
	 * record, using the specified knowledge base.
	 *
	 * @param base the knowledge base to create the session for
	 * @return the builder to replay the record into a session
	 */
	default SessionBuilder newSessionBuilder(KnowledgeBase base) {
		return new SessionBuilder(base).id(getId()).name(getName()).protocol(getProtocol())
				.created(getCreationDate()).changed(getLastChangeDate()).info(getInfoStore());
	}

	/**
	 * Creates a new SessionBuilder for this record instance, that can be used to initialize a running session with the
	 * conents of this record. Usually the specified target session should be empty, otherwise the results are hardly to
	 * predict.
	 *
	 * @param targetSession the session to replay this records contents into
	 * @return the builder to replay the record into the session
	 */
	default SessionBuilder newSessionBuilder(Session targetSession) {
		return newSessionBuilder(targetSession.getKnowledgeBase()).target(targetSession);
	}
}