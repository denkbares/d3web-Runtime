/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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

package de.d3web.testcase.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.w3c.dom.Document;

import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.testcase.model.TestCase;

/**
 * This class provides the management features to load and save
 * {@link TestCase}s to a file system in the xml format.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 28.10.15
 */
public class TestCasePersistenceManager {

	private static TestCasePersistenceManager instance = null;

	public static TestCasePersistenceManager getInstance() {
		if (instance == null) instance = new TestCasePersistenceManager();
		return instance;
	}

	private TestCasePersistenceManager() {
		// singleton...
	}

	public void saveTestCase(OutputStream outputStream, TestCase testCase) {

	}

	public TestCase loadTestCase(InputStream inputStream) throws IOException {
		Document document = XMLUtil.streamToDocument(inputStream);
		TestCasePersistence testCasePersistence = new TestCasePersistence(document);
		return (TestCase) testCasePersistence.readFragment(document.getDocumentElement());
	}

}
