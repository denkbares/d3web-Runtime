/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.d3web.test.empiricalTesting;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestCase;
import de.d3web.empiricaltesting.TestPersistence;
import de.d3web.empiricaltesting.caseAnalysis.functions.TestCaseAnalysis;
import de.d3web.empiricaltesting.caseAnalysis.functions.TestCaseAnalysisReport;
import de.d3web.plugin.test.InitPluginManager;

/**
 * 
 * @author jochenreutelshofer
 * @created 22.07.2013
 */
public class TestCaseAnalysisTest {

	@Test
	public void testRun() {
		try {
			InitPluginManager.init();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		KnowledgeBase kb = null;
		try {
			kb = PersistenceManager.getInstance().load(
					new File("./src/test/resources/Car faults diagnosis.d3web"));
			List<SequentialTestCase> loadedCases = TestPersistence.getInstance().loadCases(
					new FileInputStream(new File(
							"./src/test/resources/Demo_-_Test_Cases_testcase-2.xml")), kb);

			TestCase tc = new TestCase();
			tc.setKb(kb);
			tc.setRepository(loadedCases);
			TestCaseAnalysis ana = new TestCaseAnalysis();
			TestCaseAnalysisReport analysisReport = ana.runAndAnalyze(tc);
			assertTrue(analysisReport.hasDiff());
		}
		catch (FileNotFoundException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		catch (XMLStreamException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		catch (IOException e) {
			assertFalse(true);
			e.printStackTrace();
		}

	}
}
