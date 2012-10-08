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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides the actual TestObject and its name. We need this container, since
 * the test objects do not necessarily know their own names.
 * 
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 13.09.2012
 */
public class TestObjectContainer<T> {

	private final T testObject;
	private final String testObjectname;

	public TestObjectContainer(String testObjectName, T testObject) {
		this.testObject = testObject;
		if (testObjectName == null) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
					"test object name was null, using toString() as test object name");
			this.testObjectname = testObject.toString();
		}
		else {
			this.testObjectname = testObjectName;
		}
	}

	public T getTestObject() {
		return testObject;
	}

	public String getTestObjectName() {
		return testObjectname;
	}

}
