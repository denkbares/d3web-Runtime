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
import java.util.List;

import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestSuite;

/**
 * 
 * @author Sebastian Furth
 * @created 24/09/2010
 */
public abstract class AbstractTestSuiteWriter implements TestSuiteWriter {

	@Override
	public void write(TestSuite t, String filepath) {
		write(t.getRepository(), filepath);
	}

	/**
	 * Writes a list of SequentialTestCases in the specified file
	 * 
	 * @created 24/09/2010
	 * @param cases the SequentialTestCases
	 * @param filepath the path to the output file
	 */
	public abstract void write(List<SequentialTestCase> cases, String filepath);

	@Override
	public ByteArrayOutputStream getByteArrayOutputStream(TestSuite t) throws IOException {
		return getByteArrayOutputStream(t.getRepository());
	}

	/**
	 * Returns an ByteArrayOutputStream for a list of SequentialTestCases. This
	 * is necessary for servlets (e.g. wiki download)
	 * 
	 * @created 24/09/2010
	 * @param cases
	 * @return
	 * @throws IOException
	 */
	public abstract ByteArrayOutputStream getByteArrayOutputStream(List<SequentialTestCase> cases) throws IOException;

}
