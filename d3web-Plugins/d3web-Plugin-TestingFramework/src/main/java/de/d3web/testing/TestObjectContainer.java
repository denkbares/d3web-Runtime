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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the actual TestObject and its name. We need this container, since
 * the test objects do not necessarily know their own names.
 * 
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 13.09.2012
 */
public class TestObjectContainer<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestObjectContainer.class);

	private final T testObject;
	private final String testObjectname;

	public TestObjectContainer(String testObjectName, T testObject) {
		this.testObject = testObject;
		if (testObjectName == null) {
			LOGGER.warn("test object name was null, using toString() as test object name");
			this.testObjectname = testObject.toString();
		}
		else {
			this.testObjectname = testObjectName;
		}
	}

	public T getTestObject() {
		return testObject;
	}

	/**
	 * Returns the name of the test object. If null was passed to the
	 * TestObjectContainer by the TestObjectProvider the toString() value of the
	 * test object is returned.
	 * 
	 * @created 15.10.2012
	 * @return name of the test object
	 */
	public String getTestObjectName() {
		return testObjectname;
	}

}
