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
	 * <p>
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
	 * @param date          the Date to get the Findings for
	 * @param knowledgeBase {@link KnowledgeBase}
	 * @return Findings at the specified Date
	 * @created 23.01.2012
	 */
	Collection<Finding> getFindings(Date date, KnowledgeBase knowledgeBase);

	/**
	 * Returns all checks of this TestCase associated to the specified Date. If
	 * there is no such check in this TestCase, an empty Collection is returned.
	 *
	 * @param date          the Date to get the Checks for
	 * @param knowledgeBase {@link KnowledgeBase}
	 * @return Checks at the specified date
	 * @created 23.01.2012
	 */
	Collection<Check> getChecks(Date date, KnowledgeBase knowledgeBase);

	/**
	 * Returns the Date when the TestCase was originally started
	 *
	 * @return Date when the TestCase was started
	 * @created 24.01.2012
	 */
	Date getStartDate();

	/**
	 * Checks if the kb fits to the TestCase and returns errors as a collections
	 * of Strings
	 *
	 * @param knowledgeBase the knowledge base we check against.
	 * @return Collections of Errors
	 * @created 14.03.2012
	 */
	Collection<String> check(KnowledgeBase knowledgeBase);

	default void applyFindings(Date date, Session session) {
		TestCaseUtils.applyFindings(session, this, date, new Settings());
	}

	/**
	 * Applies all the finding from the given date to the given session.
	 *
	 * @param session  the session to apply the finding to
	 * @param settings the settings to be used while applying the findings to the session
	 */
	default void applyFindings(Date date, Session session, Settings settings) {
		TestCaseUtils.applyFindings(session, this, date, settings);
	}

	class Settings {

		private final boolean skipNumValueOutOfRange;
		private final long timeShift;

		public Settings(boolean skipNumValueOutOfRange, long timeShift) {
			this.skipNumValueOutOfRange = skipNumValueOutOfRange;
			this.timeShift = timeShift;
		}

		public Settings() {
			this(false, 0);
		}

		public Settings(boolean skipNumValueOutOfRange) {
			this(skipNumValueOutOfRange, 0);
		}

		/**
		 * The time shift to be applied to date at which the findings should be applied to the session.
		 */
		public long getTimeShift() {
			return timeShift;
		}

		/**
		 * If this is returns true, findings that try to set values outside the defined
		 * range of a question are ignored
		 */
		public boolean isSkipNumValueOutOfRange() {
			return skipNumValueOutOfRange;
		}
	}

}
