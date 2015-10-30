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

package de.d3web.empiricaltesting;

import java.util.ArrayList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;

/**
 * @deprecated use {@link de.d3web.testcase.model.TestCase} instead
 */
@Deprecated
public final class TestCase {

	private String name;
	private List<SequentialTestCase> repository;
	private KnowledgeBase kb;
	private boolean interviewAgendaRelevant;

	/**
	 * Creates a new collection of {@link SequentialTestCase} instances.
	 */
	public TestCase() {
		repository = new ArrayList<SequentialTestCase>();
	}

	/**
	 * Returns the underlying {@link KnowledgeBase} of this instance.
	 * 
	 * @return the KnowledgeBase of this instance
	 */
	public KnowledgeBase getKb() {
		return kb;
	}

	/**
	 * Sets the {@link KnowledgeBase} of this instance.
	 * 
	 * @param kb the {@link KnowledgeBase} to be set
	 */
	public void setKb(KnowledgeBase kb) {
		this.kb = kb;
	}

	/**
	 * Sets the name of this instance
	 * 
	 * @param name the given name of this instance
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the name of this instance.
	 * 
	 * @return the name of this instance.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns true, if this uses the InteviewCalculator.
	 * 
	 * @return true if this instance uses the InterviewCalculator; false
	 *         otherwise.
	 */
	public boolean interviewAgendsIsRelevant() {
		return interviewAgendaRelevant;
	}

	/**
	 * Set this value to true, if you want to use the InteviewCalculator.
	 * 
	 * @param b boolean value representing the usage of the InterviewCalculator.
	 */
	public void setInterviewAgendsIsRelevant(boolean b) {
		this.interviewAgendaRelevant = b;
	}

	/**
	 * Returns the repository of this test case.
	 * 
	 * @return all cases included in the repository
	 */
	public synchronized List<SequentialTestCase> getRepository() {
		return repository;
	}

	/**
	 * Sets the collection of {@link SequentialTestCase} instances used in this
	 * text case.
	 * 
	 * @param repository the repository to set
	 */
	public void setRepository(List<SequentialTestCase> repository) {
		this.repository = repository;
	}

	/**
	 * Checks for consistency of this TestSuite A TestSuite is consistent, if
	 * there exist no two sequential test cases with
	 * <OL>
	 * <li>The first (q - 1) sequences of the cases are identical</li>
	 * <li>The findings in sequence q are identical but their solutions differ</li>
	 * </OL>
	 * 
	 * @return true if the tests are consistent; false otherwise.
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
							return false;
						}
					}
					else break;
				}
			}
		}
		return true;
	}

}
