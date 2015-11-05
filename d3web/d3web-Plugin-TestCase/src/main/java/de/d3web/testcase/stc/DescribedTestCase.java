/*
 * Copyright (C) 2014 denkbares GmbH, Germany
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

package de.d3web.testcase.stc;

import java.util.Date;

import de.d3web.testcase.model.TestCase;

/**
 * This is a interface for TestCases also providing descriptions for entries and overall.
 * <p/>
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 10.06.2014
 */
public interface DescribedTestCase extends TestCase {

	/**
	 * Returns a description for the checks of the given date or null, if there is no description.
	 *
	 * @param date the date of the checks for which we want the description
	 */
	String getDescription(Date date);

	/**
	 * Returns the description or name of the overall test case.
	 */
	String getDescription();
}
