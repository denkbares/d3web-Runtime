/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.empiricaltesting.casevisualization;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestCase;

public interface CaseVisualizer {

	/**
	 * Sets the label that should be pointed out when cases are visualized by
	 * this visualizer.
	 * 
	 * @created 21.07.2011
	 * @param label the label of the visualized cases
	 */
	void setLabel(Label label);

	/**
	 * Creates a visualized DDTree from a TestCase suite and writes it to a
	 * specified file.
	 * 
	 * @created 22.04.2011
	 * @param testsuite the test suite to be visualized
	 * @param file the file to write into
	 */
	void writeToFile(TestCase testsuite, File file) throws IOException;

	/**
	 * Creates a visualized DDTree from a list of {@link SequentialTestCase}s
	 * and writes it to a specified file.
	 * 
	 * @created 22.04.2011
	 * @param cases the sequential test cases to be visualized
	 * @param file the file to write into
	 */
	void writeToFile(List<SequentialTestCase> cases, File file) throws IOException;

	/**
	 * Creates a visualized DDTree from a list of {@link SequentialTestCase}s
	 * and writes it to a specified stream.
	 * 
	 * @created 22.04.2011
	 * @param cases the sequential test cases to be visualized
	 * @param outStream the stream to write into
	 */
	void writeToStream(List<SequentialTestCase> cases, OutputStream outStream) throws IOException;

}
