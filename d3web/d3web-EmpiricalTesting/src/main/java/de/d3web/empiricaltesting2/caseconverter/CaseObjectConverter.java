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

package de.d3web.empiricaltesting2.caseconverter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.CaseRepository;
import de.d3web.caserepository.CaseObject.Solution;
import de.d3web.caserepository.sax.CaseRepositoryReader;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.info.DCElement;
import de.d3web.core.knowledge.terminology.info.DCMarkup;
import de.d3web.empiricaltesting2.Finding;
import de.d3web.empiricaltesting2.RatedSolution;
import de.d3web.empiricaltesting2.RatedTestCase;
import de.d3web.empiricaltesting2.SequentialTestCase;
import de.d3web.empiricaltesting2.StateRating;
import de.d3web.empiricaltesting2.TestSuite;

/**
 * This class converts CaseObject XML-Files to a TestSuite.
 * The created TestSuite can be written to a file by
 * using either CaseObjectToKnOffice class or
 * CaseObjectToTestSuiteXML class.
 * @author Sebastian Furth
 *
 */
public abstract class CaseObjectConverter {
		
	/**
	 * Creates a Test Suite from a CaseObject XML-File.
	 * @param knowledgebase Path to the underlying KnowledgeBase
	 * @param casespath Path to the CaseObject XML-File
	 * @return TestSuite containing the Data from the CaseObject File
	 * @throws IOException
	 */
	public TestSuite convert(String knowledgebase, String casespath) 
		throws IOException {
		
		KnowledgeBase knowledge = loadKnowledgeBase(knowledgebase);
		CaseRepository cases = loadCaseRepository(casespath, knowledge);
		return convertToTestsuite(cases, knowledge);
	}
	
	/**
	 * Creates a Test Suite from a list of CaseObjects.
	 * @param the list of CaseObjects which shall be converted
	 * @param k The underlying KnowledgeBase
	 * @return TestSuite containing the Data from the list of CaseObjects
	 */
	public TestSuite convertToTestsuite(CaseRepository cases, KnowledgeBase k) {
		
		TestSuite t = new TestSuite();
		t.setKb(k);
		List<SequentialTestCase> stc = new ArrayList<SequentialTestCase>();
		Iterator<CaseObject> iter = cases.iterator();
		
		while (iter.hasNext()) {
			
			CaseObject co = iter.next();
			
			// new SequentialTestCase
			SequentialTestCase s = new SequentialTestCase();
			s.setName(getCaseID(co));
			
			// new RatedTestCase
			RatedTestCase rtc = computeRTC(co, k);
			cleanExpectedSolutions(rtc);
			s.add(rtc);
			
			// Add STC to STC-List
			stc.add(s);
		}
		
		// Set STC-List as test suite Repository
		t.setRepository(stc);
		return t;
		
	}
	
	public abstract void write(TestSuite t, String filepath);

	public abstract void write(List<SequentialTestCase> cases, String filepath);
	
	public abstract ByteArrayOutputStream getByteArrayOutputStream(List<SequentialTestCase> cases) throws IOException;
	
	private RatedTestCase computeRTC(CaseObject co, KnowledgeBase k) {
		RatedTestCase rtc = new RatedTestCase();
		rtc.setName("RTC");
		computeFindings(rtc, co, k);
		computeExpectedSolutions(rtc, co);
		return rtc;
	}
	
	/**
	 * Cleans equal Solutions with different Ratings.
	 * The Solution with the highest Rating remains in the RTC.
	 * @param rtc The RatedTestCase which shall be cleaned.
	 */
	private void cleanExpectedSolutions(RatedTestCase rtc) {
		List<RatedSolution> expectedSolutions = rtc.getExpectedSolutions();
		
		List<de.d3web.core.knowledge.terminology.Solution> distinctDiagnosises = getDistinctSolutions(expectedSolutions);
		
		rtc.setExpectedSolutions(getCleanedSolutions(expectedSolutions, distinctDiagnosises));
	}

	/**
	 * Returns a List of RatedSolutions in which each solution is unique
	 * @param expectedSolutions The List of uncleaned RatedSolutions
	 * @param distinctDiagnosises The List of different Diagnosises
	 * @return List of RatedSolution in which each solution is unique
	 */
	private List<RatedSolution> getCleanedSolutions(
			List<RatedSolution> expectedSolutions,
			List<de.d3web.core.knowledge.terminology.Solution> distinctDiagnosises) {
		
		List<RatedSolution> cleanedSolutions = new ArrayList<RatedSolution>();
		
		for (de.d3web.core.knowledge.terminology.Solution d : distinctDiagnosises) {
			List<RatedSolution> equalDiagnosises = new ArrayList<RatedSolution>();
			for (RatedSolution s : expectedSolutions) {
				if (s.getSolution().equals(d)) {
					equalDiagnosises.add(s);
				}
			}
			cleanedSolutions.add(findSolutionWithHighestRating(equalDiagnosises));
		}
		
		return cleanedSolutions;
	}

	/**
	 * Returns a List of distinct Diagnosises
	 * @param expectedSolutions a List of RatedSolutions
	 * @return
	 */
	private List<de.d3web.core.knowledge.terminology.Solution> getDistinctSolutions(
			List<RatedSolution> expectedSolutions) {
		
		List<de.d3web.core.knowledge.terminology.Solution> distinctDiagnosises = new ArrayList<de.d3web.core.knowledge.terminology.Solution>();
		
		for (RatedSolution s : expectedSolutions) {
			de.d3web.core.knowledge.terminology.Solution tmp = s.getSolution();
			if (!distinctDiagnosises.contains(tmp)) {
				distinctDiagnosises.add(tmp);
			}
		}
		return distinctDiagnosises;
	}

	private void computeExpectedSolutions(RatedTestCase rtc, CaseObject co) {

		List<CaseObject.Solution> solutions = new ArrayList<CaseObject.Solution>(co.getSolutions());
		for (CaseObject.Solution s : solutions) {
			addRatedSolution(rtc, s);
		}
		
	}
	
	private RatedSolution findSolutionWithHighestRating(List<RatedSolution> equalSolutions) {
		RatedSolution s = equalSolutions.get(0);
		for (int j = 1; j < equalSolutions.size(); j++) {
			RatedSolution temp = equalSolutions.get(j);
			if (temp.getRating().compareTo(s.getRating()) == 1) {
				s = temp;
			}
		}
		return s;
	}

	private void addRatedSolution(RatedTestCase rtc, Solution s) {
		Rating state = s.getState();
		de.d3web.core.knowledge.terminology.Solution d = s.getSolution();
		if (state.isRelevant()) {
			StateRating r = new StateRating(state);
			rtc.addExpected(new RatedSolution(d, r));
		}
		
	}

	private void computeFindings(RatedTestCase rtc, CaseObject co, KnowledgeBase k) {
		List<Question> questions = new ArrayList<Question>(co.getQuestions());
		for (Question q : questions) {
			rtc.add(new Finding(q, co.getValue(q)));
		}
	}

	private String getCaseID(CaseObject caseObject) {
		DCMarkup markup = caseObject.getDCMarkup();
		return markup.getContent(DCElement.IDENTIFIER);
	}
	
	private KnowledgeBase loadKnowledgeBase(String filePath) throws IOException {
		 PersistenceManager mgr = PersistenceManager.getInstance();
	     return mgr.load(new File(filePath));
	}

	@SuppressWarnings("unchecked")
	private CaseRepository loadCaseRepository(String xmlFile, KnowledgeBase kb) {
		CaseRepositoryReader loader = new CaseRepositoryReader();
		return loader.createCaseRepository(new File(xmlFile), kb);
	}

}
