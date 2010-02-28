/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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

package de.d3web.empiricalTesting.caseConverter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import de.d3web.empiricalTesting.SequentialTestCase;
import de.d3web.empiricalTesting.TestPersistence;
import de.d3web.empiricalTesting.TestSuite;

/**
 * This Class converts CaseObject XML-Files to
 * a TestSuite and writes the created TestSuite
 * in TestSuiteXML-Format to a xml-file.
 * @author Sebastian Furth
 *
 */
public class CaseObjectToTestSuiteXML extends CaseObjectConverter{
	
	public static void main(String[] args) throws IOException {
		String workspace  = "d:/eigene projekte/temp/";
		String knowledgePath = "pflanzen.jar";
		String caseBase   = "pflanzen_cases_big_rerun.xml";
		String converted = "pflanzen_cases_big_converted.xml";
		
		CaseObjectConverter converter = new CaseObjectToTestSuiteXML();
		TestSuite t = converter.convert(workspace+knowledgePath, workspace+caseBase);

		converter.write(t, workspace+converted);
		
		System.out.println("Case successfully converted to TestSuiteXML!");
	}
	
	/**
	 * Writes a TestSuite to the specified file 
	 * using the TestSuite-XML-Format
	 * @param t The TestSuite which shall be written to a file
	 * @param the path of the output file
	 */
	@Override
	public void write(TestSuite t, String filepath) {
		write(t.getRepository(), filepath);
	}

	@Override
	public void write(List<SequentialTestCase> cases, String filepath) {
		try {
			URL convertedpath = new File(filepath).toURI().toURL();
			TestPersistence.getInstance().writeCases(convertedpath, cases, false);
		} catch (MalformedURLException e) {
			System.err.println("Error while writing TestSuiteXML file!");
		}
	}

	@Override
	public ByteArrayOutputStream getByteArrayOutputStream(
			List<SequentialTestCase> cases) throws IOException {
		ByteArrayOutputStream bstream = new ByteArrayOutputStream();
		TestPersistence.getInstance().writeCases(bstream, cases, false);
		return bstream;
	}

}
