/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Pattern;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.testing.Message;
import de.d3web.testing.Utils;


/**
 * Some utility methods.
 * 
 * @author Reinhard Hatko
 * @created 26.03.2013
 */
public class D3webTestUtils {

	private D3webTestUtils() {
	}

	/**
	 * Convenience method to create error messages when working with
	 * {@link TerminologyObject}s.
	 * 
	 * @created 06.03.2013
	 * @param erroneousObjects
	 * @param failedMessage
	 * @return s an error message containing
	 */
	public static Message createErrorMessage(Collection<TerminologyObject> erroneousObjects, String failedMessage) {
		Collection<String> objectNames = new ArrayList<String>(erroneousObjects.size());
		for (TerminologyObject object : erroneousObjects) {
			objectNames.add(object.getName());
		}
		return Utils.createErrorMessage(objectNames, failedMessage, NamedObject.class);
	}



	/**
	 * Filters a list of {@link TerminologyObject}s.
	 * 
	 * @created 26.03.2013
	 * @param objects
	 * @param ignores derived from ignore parameters of a test
	 * @param additionalIgnores additional ignores as given by specific test
	 *        (e.g. name of the rootQASet)
	 * @return s the filtered List
	 */
	public static Collection<TerminologyObject> filter(Collection<TerminologyObject> objects, String[][] ignores, String... additionalIgnores) {
		Collection<Pattern> ignorePatterns = Utils.compileIgnores(ignores);
	
		for (String ignore : additionalIgnores) {
			ignorePatterns.add(Pattern.compile(ignore, Pattern.CASE_INSENSITIVE));
		}
	
		Collection<TerminologyObject> result = new LinkedList<TerminologyObject>();
	
		for (TerminologyObject object : objects) {
			if (D3webTestUtils.isIgnoredInHierarchy(object, ignorePatterns)) continue;
	
			result.add(object);
		}
	
		return result;
	}

	/**
	 * Checks, if a {@link TerminologyObject} or one of its parents is ignored,
	 * based on a list of Patterns.
	 * 
	 * @created 25.03.2013
	 * @param object the TerminologyObject to check
	 * @param ignorePatterns list of {@link Pattern}s to ignores
	 * @return true, if the object should be ignored, false otherwise
	 */
	public static boolean isIgnoredInHierarchy(TerminologyObject object, Collection<Pattern> ignorePatterns) {
		if (Utils.isIgnored(object.getName(), ignorePatterns)) return true;
	
		TerminologyObject[] parents = object.getParents();
	
		for (int i = 0; i < parents.length; i++) {
			if (isIgnoredInHierarchy(parents[i], ignorePatterns)) return true;
	
		}
	
		return false;
	}

}
