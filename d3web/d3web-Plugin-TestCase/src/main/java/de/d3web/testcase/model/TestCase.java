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

import java.util.Collection;
import java.util.Date;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.testcase.TestCaseUtils;

/**
 * Interface describing a repeatable and testable case for knowledge base
 * evaluation. Its intended to be implemented for various case formats.
 * 
 * @author Volker Belli & Markus Friedrich (denkbares GmbH)
 * @created 23.01.2012
 */
public interface TestCase {

	/**
	 * Collection of Dates to iterate through the entries of this
	 * {@link TestCase}'s entries.
	 * <P>
	 * The iteration is ordered by the time of the entries. Therefore, the
	 * iteration starts at the time of the oldest entry and proceeds to the
	 * newer values.
	 * 
	 * @return Collection of Dates chronological ordered
	 */
	Collection<Date> chronology();

	/**
	 * Returns all findings of this TestCase associated to the specified Date.
	 * If there is no such finding in this TestCase, an empty Collection is
	 * returned.
	 * 
	 * @created 23.01.2012
	 * @param date the Date to get the Findings for
	 * @param knowledgeBase {@link KnowledgeBase}
	 * @return Findings at the specified Date
	 */
	Collection<Finding> getFindings(Date date, KnowledgeBase knowledgeBase);

	/**
	 * Returns all checks of this TestCase associated to the specified Date. If
	 * there is no such check in this TestCase, an empty Collection is returned.
	 * 
	 * @created 23.01.2012
	 * @param date the Date to get the Checks for
	 * @param knowledgeBase {@link KnowledgeBase}
	 * @return Checks at the specified date
	 */
	Collection<Check> getChecks(Date date, KnowledgeBase knowledgeBase);

	/**
	 * Returns the Date when the TestCase was originally started
	 * 
	 * @created 24.01.2012
	 * @return Date when the TestCase was started
	 */
	Date getStartDate();

	/**
	 * Checks if the kb fits to the TestCase and returns errors as a collections
	 * of Strings
	 * 
	 * @created 14.03.2012
	 * @param knowledgeBase the knowledge base we check against.
	 * @return Collections of Errors
	 */
	Collection<String> check(KnowledgeBase knowledgeBase);

	/**
	 * Applies all the finding from the given date to the given session.
	 *
	 * @param session the session to apply the finding to
	 */
	default void applyFindings(Date date, Session session) {
		TestCaseUtils.applyFindings(session, this, date);
	}

}
