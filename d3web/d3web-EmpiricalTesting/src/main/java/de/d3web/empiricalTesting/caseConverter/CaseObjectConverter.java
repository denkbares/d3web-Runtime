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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.CaseObject.Solution;
import de.d3web.caserepository.sax.CaseObjectListCreator;
import de.d3web.empiricalTesting.Finding;
import de.d3web.empiricalTesting.RatedSolution;
import de.d3web.empiricalTesting.RatedTestCase;
import de.d3web.empiricalTesting.SequentialTestCase;
import de.d3web.empiricalTesting.StateRating;
import de.d3web.empiricalTesting.TestSuite;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.supportknowledge.DCElement;
import de.d3web.kernel.supportknowledge.DCMarkup;
import de.d3web.persistence.xml.PersistenceManager;

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
	 * @throws MalformedURLException
	 */
	public TestSuite convert(String knowledgebase, String casespath) 
		throws MalformedURLException {
		
		KnowledgeBase knowledge = loadKnowledgeBase(knowledgebase);
		List<CaseObject> cases = loadCaseRepository(casespath, knowledge);
		return convertToTestsuite(cases, knowledge);
	}
	
	/**
	 * Creates a Test Suite from a list of CaseObjects.
	 * @param the list of CaseObjects which shall be converted
	 * @param k The underlying KnowledgeBase
	 * @return TestSuite containing the Data from the list of CaseObjects
	 */
	public TestSuite convertToTestsuite(List<CaseObject> cases, KnowledgeBase k) {
		
		TestSuite t = new TestSuite();
		t.setKb(k);
		List<SequentialTestCase> stc = new ArrayList<SequentialTestCase>();
		
		for (CaseObject co : cases) {
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
		
		List<Diagnosis> distinctDiagnosises = getDistinctDiagnosises(expectedSolutions);
		
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
			List<Diagnosis> distinctDiagnosises) {
		
		List<RatedSolution> cleanedSolutions = new ArrayList<RatedSolution>();
		
		for (Diagnosis d : distinctDiagnosises) {
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
	private List<Diagnosis> getDistinctDiagnosises(
			List<RatedSolution> expectedSolutions) {
		
		List<Diagnosis> distinctDiagnosises = new ArrayList<Diagnosis>();
		
		for (RatedSolution s : expectedSolutions) {
			Diagnosis tmp = s.getSolution();
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
		DiagnosisState state = s.getState();
		Diagnosis d = s.getDiagnosis();
		if (!(state.equals(DiagnosisState.EXCLUDED) || state.equals(DiagnosisState.UNCLEAR))) {
			StateRating r = new StateRating(state);
			rtc.addExpected(new RatedSolution(d, r));
		}
		
	}

	@SuppressWarnings("unchecked")
	private void computeFindings(RatedTestCase rtc, CaseObject co, KnowledgeBase k) {
		List<Question> questions = new ArrayList<Question>(co.getQuestions());
		for (Question q : questions) {
			Object[] answers = co.getAnswers(q).toArray();
			for (int i = 0; i < answers.length; i++) {
				Answer a = (Answer) answers[i];
				rtc.add(new Finding(q, a));
			}		
		}
	}

	private String getCaseID(CaseObject caseObject) {
		DCMarkup markup = caseObject.getDCMarkup();
		return markup.getContent(DCElement.IDENTIFIER);
	}
	
	private KnowledgeBase loadKnowledgeBase(String filePath) throws MalformedURLException {
		 URL fileURL = new File(filePath).toURI().toURL();
	     PersistenceManager mgr = PersistenceManager.getInstance();
	     return mgr.load(fileURL);
	}

	@SuppressWarnings("unchecked")
	private List<CaseObject> loadCaseRepository(String xmlFile, KnowledgeBase kb) {
		CaseObjectListCreator loader = new CaseObjectListCreator();
		return loader.createCaseObjectList(new File(xmlFile), kb);
	}

}
