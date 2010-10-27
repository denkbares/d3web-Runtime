/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.empiricaltesting;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;

public final class TestSuite {

	private final TestPersistence persistence = TestPersistence.getInstance();
	private final EmpiricalTestingFunctions functions = EmpiricalTestingFunctions.getInstance();

	private List<SequentialTestCase> repository;
	private KnowledgeBase kb;

	private String name;
	private boolean useInterviewCalculator;
	private boolean derived;

	/**
	 * Default Constructor
	 */
	public TestSuite() {
		repository = new ArrayList<SequentialTestCase>();
	}

	/**
	 * Constructor, which sets the KnowledgeBase directly.
	 * 
	 * @param kb the KnowledgeBase to be set
	 */
	public TestSuite(KnowledgeBase kb) {
		setKb(kb);
	}

	/**
	 * Returns the underlying KnowledgeBase of this TestSuite.
	 * 
	 * @return the KnowledgeBase of this TestSuite
	 */
	public KnowledgeBase getKb() {
		return kb;
	}

	/**
	 * Sets the KnowledgeBase of this TestSuite.
	 * 
	 * @param kb the KnowledgeBase to be set
	 */
	public void setKb(KnowledgeBase kb) {
		this.kb = kb;
	}

	/**
	 * Sets the Name of this TestSuite
	 * 
	 * @param name desired name of this TestSuite
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the name of this TestSuite.
	 * 
	 * @return name of this TestSuite.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns true if this TestSuite uses the InteviewCalculator.
	 * 
	 * @return true if this TestSuite uses the InterviewCalculator. Else false.
	 */
	public boolean getUseInterviewCalculator() {
		return useInterviewCalculator;
	}

	/**
	 * Set this value to true, if you want to use the InteviewCalculator.
	 * 
	 * @param b boolean value representing the usage of the InterviewCalculator.
	 */
	public void setUseInterviewCalculator(boolean b) {
		this.useInterviewCalculator = b;
	}

	/**
	 * Returns the Repository of this SequentialTestCase.
	 * 
	 * @return List of SequentitalTestCases representing the repository
	 */
	public synchronized List<SequentialTestCase> getRepository() {
		return repository;
	}

	/**
	 * Sets the Repository.
	 * 
	 * @param repository the repository to set
	 */
	public void setRepository(List<SequentialTestCase> repository) {
		this.repository = repository;
	}

	/**
	 * Loads a SequentialTestCase Repository into this TestSuite. If the loading
	 * was successful deriveAllSolutions() is called.
	 * 
	 * @param casesUrl URL-formatted String pointing to a XML Test Case
	 *        Repository
	 */
	public void loadRepository(String casesUrl) {
		try {
			setRepository(persistence.loadCases(new File(casesUrl).toURI().toURL(), kb));
		}
		catch (MalformedURLException e) {
			System.err.println("Malformed URL: " + casesUrl);
			e.printStackTrace();
		}
	}

	/**
	 * Saves the TestSuite.
	 * 
	 * @param casesUrl URL-formatted String representing the output file
	 * @param bWriteDerivedSolutions Boolean representing whether
	 *        derivedSolutions are written to the output file or not
	 */
	public void saveRepository(String casesUrl, boolean bWriteDerivedSolutions) {
		try {
			persistence.writeCases(new URL(casesUrl), this, bWriteDerivedSolutions);
		}
		catch (MalformedURLException e) {
			System.err.println("Malformed URL: " + casesUrl);
			e.printStackTrace();
		}
	}

	/**
	 * Saves TestSuite without derivedSolutions
	 * 
	 * @param casesUrl URL-formatted String representing the output file
	 */
	public void saveRepositoryOnlyexpectedSolutions(String casesUrl) {
		saveRepository(casesUrl, false);
	}

	/**
	 * Saves TestSuite with derivedSolutions
	 * 
	 * @param casesUrl URL-formatted String representing the output file
	 */
	public void saveRepositoryWithDerivedSolutions(String casesUrl) {
		saveRepository(casesUrl, true);
	}

	/**
	 * Checks for Consistency of this TestSuite A TestSuite is consistent, if
	 * there exist no two sequential test cases with 1.) The first (q - 1)
	 * sequences of the cases are identical 2.) The findings in sequence q are
	 * identical but their solutions differ
	 * 
	 * @return true if this TestSuite is Consistent. Else false.
	 */
	public boolean isConsistent() {
		for (SequentialTestCase stc1 : repository) {
			for (SequentialTestCase stc2 : repository) {
				for (int i = 0; i < stc1.getCases().size() && i < stc2.getCases().size(); i++) {
					RatedTestCase rtc1 = stc1.getCases().get(i);
					RatedTestCase rtc2 = stc2.getCases().get(i);

					// when the findings are equal...
					if (rtc1.getFindings().equals(rtc2.getFindings())) {
						// ...but not the solutions...
						if (!rtc1.getExpectedSolutions().equals(
								rtc2.getExpectedSolutions())) {
							// ...the TestSuite is not consistent!
							System.err.println("Warning! TestSuite is not consistent!");
							System.err.println(rtc1);
							System.err.println(rtc2);
							return false;
						}
					}
					else break;
				}
			}
		}
		return true;
	}

	/**
	 * Derives the Solutions for all SequentialTestCases in this Repositoty
	 */
	public void deriveAllSolutions() {
		if (!derived) {
			for (SequentialTestCase stc : repository) {
				stc.deriveSolutions(kb);
			}
			derived = true;
		}
	}

	/**
	 * Calculates the TotalPrecision of this (consistent!) TestSuite.
	 * 
	 * @return 0, if this TestSuite is not consistent. Else: The Total Precision
	 */
	public double totalPrecision() {
		double prec = 0;
		if (!isConsistent()) {
			return prec;
		}
		deriveAllSolutions();
		for (SequentialTestCase stc : repository) {
			prec += functions.precision(stc, DerivedSolutionsCalculator.getInstance(), false);
		}
		prec /= repository.size();
		return prec;
	}

	/**
	 * Calculates the TotalPrecision of this (consistent!) TestSuite.
	 * NonSequential means, that only the last RatedTestCase of each
	 * SequentialTestCase is taken into account.
	 * 
	 * @return 0, if this TestSuite is not consistent. Else: The Total Precision
	 */
	public double totalNonSequentialPrecision() {
		double prec = 0;
		if (!isConsistent()) {
			return prec;
		}
		deriveAllSolutions();
		for (SequentialTestCase stc : repository) {
			prec += functions.precision(stc, DerivedSolutionsCalculator.getInstance(), true);
		}
		prec /= repository.size();
		return prec;
	}

	/**
	 * Calculates the total Interview-Precision of this TestSuite. During the
	 * Interview-Calculation the TestSuite checks if the asked question in the
	 * dialog is exactly the one which was expected in the RatedTestCase.
	 * 
	 * @return Double value representing the total Interview-Precision.
	 */
	public double totalPrecisionInterview() {
		double prec = 0;
		if (!isConsistent()) {
			return prec;
		}
		deriveAllSolutions();
		for (SequentialTestCase stc : repository) {
			prec += functions.precision(stc, new InterviewCalculator(kb), false);
		}
		prec /= repository.size();
		return prec;
	}

	/**
	 * Calculates the TotalRecall of this (consistent!) TestSuite
	 * 
	 * @return 0, if this TestSuite is not consistent. Else: The Total Recall
	 */
	public double totalRecall() {
		double rec = 0;
		if (!isConsistent()) {
			return rec;
		}
		deriveAllSolutions();
		for (SequentialTestCase stc : repository) {
			rec += functions.recall(stc, DerivedSolutionsCalculator.getInstance(), false);
		}
		rec /= repository.size();
		return rec;
	}

	/**
	 * Calculates the TotalRecall of this (consistent!) TestSuite. NonSequential
	 * means, that only the last RatedTestCase of each SequentialTestCase is
	 * taken into account.
	 * 
	 * @return 0, if this TestSuite is not consistent. Else: The Total Recall
	 */
	public double totalNonSequentialRecall() {
		double rec = 0;
		if (!isConsistent()) {
			return rec;
		}
		deriveAllSolutions();
		for (SequentialTestCase stc : repository) {
			rec += functions.recall(stc, DerivedSolutionsCalculator.getInstance(), true);
		}
		rec /= repository.size();
		return rec;
	}

	/**
	 * Calculates the total Interview-Recall of this TestSuite. During the
	 * Interview-Calculation the TestSuite checks if the asked question in the
	 * dialog is exactly the one which was expected in the RatedTestCase.
	 * 
	 * @return Double value representing the total Interview-Recall.
	 */
	public double totalRecallInterview() {
		double rec = 0;
		if (!isConsistent()) {
			return rec;
		}
		deriveAllSolutions();
		for (SequentialTestCase stc : repository) {
			rec += functions.recall(stc, new InterviewCalculator(kb), false);
		}
		rec /= repository.size();
		return rec;
	}

	// TODO: Nicht nur eine Antwort (auf eine Frage) sondern mehrere
	// Antworten auf mehrere (erste) Fragen möglich
	public TestSuite getPartiallyAnsweredSuite(Choice answer) {
		TestSuite ret = new TestSuite();
		for (SequentialTestCase stc : getRepository()) {
			if (stc.getCases().get(0).getFindings().get(0).getValue().equals(answer)) ret.getRepository().add(
					stc);
		}
		return ret;
	}
}
