/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.d3web.empiricaltesting.writer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.d3web.empiricaltesting.TestSuite;

/**
 * Interface for TestSuite Writers.
 * 
 * @author Sebastian Furth
 * @created 24/09/2010
 */
public interface TestSuiteWriter {

	/**
	 * Writes a test suite to the specified file
	 * 
	 * @created 24/09/2010
	 * @param t the test suite
	 * @param filepath the path to the output file
	 */
	public void write(TestSuite t, String filepath);

	/**
	 * Returns an ByteArrayOutputStream for a test suite. This is necessary for
	 * servlets (e.g. wiki download)
	 * 
	 * @created 24/09/2010
	 * @param t the testsuite
	 * @return
	 * @throws IOException
	 */
	public ByteArrayOutputStream getByteArrayOutputStream(TestSuite t) throws IOException;

}
