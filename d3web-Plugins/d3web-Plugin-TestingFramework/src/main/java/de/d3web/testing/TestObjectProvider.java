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
package de.d3web.testing;

import java.util.List;

/**
 * TestObjectProvider interface for providing test-objects.
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 04.05.2012
 */
public interface TestObjectProvider {

	/**
	 * Delivers a test objects of the given class for a regular expression or
	 * for an identifier in a {@link TestObjectContainer}. The
	 * {@link TestObjectContainer} contains the object itself and the name of
	 * the object.
	 * 
	 * @created 22.05.2012
	 * @param clazz Class of the test-object
	 * @param name regex for the desired test-object instance.
	 * @return
	 */
	<T> List<TestObjectContainer<T>> getTestObjects(Class<T> clazz, String name);

	String EXTENSION_POINT_ID = "TestObjectProvider";

}
