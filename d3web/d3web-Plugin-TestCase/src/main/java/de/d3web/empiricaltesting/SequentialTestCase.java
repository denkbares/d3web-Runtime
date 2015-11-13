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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.condition.CondRegex;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.QuestionValue;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.testcase.TestCaseUtils;
import de.d3web.testcase.model.Check;
import de.d3web.testcase.model.ConditionCheck;
import de.d3web.testcase.model.DefaultFinding;
import de.d3web.testcase.model.DefaultTestCase;
import de.d3web.testcase.model.Finding;
import de.d3web.testcase.model.DescribedTestCase;
import de.d3web.testcase.stc.DerivedQuestionCheck;
import de.d3web.testcase.stc.DerivedSolutionCheck;

/**
 * @deprecated use {@link DefaultTestCase} instead
 */
@SuppressWarnings("deprecation")
@Deprecated
public class SequentialTestCase extends DefaultTestCase implements DescribedTestCase {

	private final LinkedHashMap<Date, RatedTestCase> ratedTestCases = new LinkedHashMap<>();

	private Date startDate = null;
	private Date lastAddedDate = null;

	public SequentialTestCase() {
		this(Collections.<RatedTestCase>emptyList());
	}

	public SequentialTestCase(Collection<RatedTestCase> ratedTestCases) {
		for (RatedTestCase ratedTestCase : ratedTestCases) {
			addCase(ratedTestCase);
		}
	}

	@Override
	public Collection<Date> chronology() {
		return ratedTestCases.keySet();
	}

	@Override
	public Collection<Finding> getFindings(Date date, KnowledgeBase knowledgeBase) {
		RatedTestCase rtc = getCase(date);
		if (rtc == null) return Collections.emptyList();
		List<Finding> findings = new LinkedList<>();
		for (de.d3web.empiricaltesting.Finding f : rtc.getFindings()) {
			Question question = f.getQuestion();
			if (question.getKnowledgeBase() != knowledgeBase) {
				question = knowledgeBase.getManager().searchQuestion(question.getName());
				if (question == null) continue;
			}
			findings.add(new DefaultFinding(question, repairValue(f, question)));
		}
		return findings;
	}

	private QuestionValue repairValue(de.d3web.empiricaltesting.Finding f, Question question) {
		QuestionValue value = f.getValue();
		if (Unknown.getInstance().equals(value)) return value;
		if (UndefinedValue.getInstance().equals(value)) return value;
		if ((question instanceof QuestionNum && !(value instanceof NumValue))
				|| (question instanceof QuestionText && !(value instanceof TextValue))
				|| (question instanceof QuestionOC && !(value instanceof ChoiceValue))
				|| (question instanceof QuestionMC && !(value instanceof MultipleChoiceValue))
				|| (question instanceof QuestionDate && !(value instanceof DateValue))) {
			try {
				value = KnowledgeBaseUtils.findValue(question, value.getValue().toString());
			}
			catch (IllegalArgumentException ignore) {
				// handled below...
			}
			if (value == null) {
				if (question instanceof QuestionOC) {
					value = new ChoiceValue(f.getValuePrompt());
				}
				else {
					value = f.getValue();
				}
			}
		}
		return value;
	}

	@Override
	public Collection<Check> getChecks(Date date, KnowledgeBase knowledgeBase) {
		RatedTestCase rtc = getCase(date);
		List<Check> checks = new LinkedList<>();
		// create checks based on stc standard behavior
		for (RatedSolution ratedSolution : rtc.getExpectedSolutions()) {
			Solution solution = ratedSolution.getSolution();
			if (solution.getKnowledgeBase() != knowledgeBase) {
				solution = knowledgeBase.getManager().searchSolution(solution.getName());
				if (solution == null) continue;
			}
			checks.add(new DerivedSolutionCheck(solution,
					TestCaseUtils.toRating(ratedSolution.getRating())));
		}
		for (de.d3web.empiricaltesting.Finding finding : rtc.getExpectedFindings()) {
			Question question = finding.getQuestion();
			if (question.getKnowledgeBase() != knowledgeBase) {
				question = knowledgeBase.getManager().searchQuestion(question.getName());
				if (question == null) continue;
			}
			checks.add(new DerivedQuestionCheck(question, repairValue(finding, question)));
		}
		for (RegexFinding regexFinding : rtc.getExpectedRegexFindings()) {
			Question question = regexFinding.getQuestion();
			if (question.getKnowledgeBase() != knowledgeBase) {
				question = knowledgeBase.getManager().searchQuestion(question.getName());
				if (question == null) continue;
			}
			checks.add(new ConditionCheck(new CondRegex(question, regexFinding.getRegex())));
		}

		return checks;
	}

	/**
	 * With this method it is possible to add additional checks to a
	 * {@link RatedTestCase}, that can not be attached to the
	 * {@link RatedTestCase} itself, because it uses a different interface for
	 * {@link Check}s.<br/>
	 * <b>Attention</b> Be aware, that the given reference to the
	 * {@link RatedTestCase} has to be the same (equal is not enough) as the one
	 * present in the {@link SequentialTestCase}.
	 *
	 * @param rtc    the {@link RatedTestCase} to add the {@link Check}s to
	 * @param checks the {@link Check}s to add
	 * @created 01.11.2013
	 * @deprecated add the checks directly to the {@link RatedTestCase}s
	 */
	@Deprecated
	public void addChecks(RatedTestCase rtc, Check... checks) {
		rtc.addCheck(checks);
	}

	@Override
	public String getDescription(Date date) {
		RatedTestCase ratedTestCase = getCase(date);
		String name = ratedTestCase.getName();
		String comment = ratedTestCase.getComment();
		if (name != null && comment != null) {
			return name + "\n" + comment;
		} else if (name == null && comment == null) {
			return null;
		} else if (comment != null) {
			return comment;
		} else {
			return name;
		}

	}

	public void setStartDate(Date startDate) {
		if (this.startDate != null) {
			throw new UnsupportedOperationException(
					"Start date can only be set once");
		}
		this.startDate = startDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	@Override
	public Collection<String> check(KnowledgeBase knowledgeBase) {
		Collection<String> errors = new HashSet<>();
		for (RatedTestCase rtc : getCases()) {
			Collection<de.d3web.empiricaltesting.Finding> findings = new LinkedList<>();
			findings.addAll(rtc.getFindings());
			findings.addAll(rtc.getExpectedFindings());
			for (de.d3web.empiricaltesting.Finding finding : findings) {
				String questionName = finding.getQuestion().getName();
				Question question = knowledgeBase.getManager().searchQuestion(questionName);
				if (question == null) {
					errors.add("Question \"" + questionName
							+ "\" is not contained in the KB.");
				}
				else {
					TestCaseUtils.checkValues(errors, question, repairValue(finding, question));
				}
			}
			Collection<RegexFinding> expectedRegexFindings = rtc.getExpectedRegexFindings();
			for (RegexFinding expectedRegexFinding : expectedRegexFindings) {
				String questionName = expectedRegexFinding.getQuestion().getName();
				Question question = knowledgeBase.getManager().searchQuestion(questionName);
				if (question == null) {
					errors.add("Question \"" + questionName
							+ "\" is not contained in the KB.");
				}
			}
			for (RatedSolution ratedSolution : rtc.getExpectedSolutions()) {
				String solutionName = ratedSolution.getSolution().getName();
				if (knowledgeBase.getManager().searchSolution(solutionName) == null) {
					errors.add("Solution \"" + solutionName
							+ "\" is not contained in the KB.");
				}
			}
		}
		return errors;
	}

	/**
	 * Adds RatedTestCase to this SequentialTestCase.
	 *
	 * @param ratedTestCase The RatedTestCase which will be added
	 * @return true if the RatedTestCase was added to this SequntialTestCase
	 */
	public boolean addCase(RatedTestCase ratedTestCase) {
		Date timeStamp = ratedTestCase.getTimeStamp();
		if (startDate == null) {
			startDate = timeStamp == null ? new Date(0) : timeStamp;
		}
		if (timeStamp == null) {
			timeStamp = lastAddedDate == null ? startDate : new Date(lastAddedDate.getTime() + 1);
		}
		if (lastAddedDate == null || lastAddedDate.before(timeStamp)) {
			lastAddedDate = timeStamp;
		}
		else {
			String msg = "RatedTestCases have to be added in a sequential order.\nLast added: "
					+ lastAddedDate + " (" + lastAddedDate.getTime() + "), current: " + timeStamp
					+ " (" + timeStamp.getTime() + ")";
			throw new IllegalArgumentException(msg);
		}
		ratedTestCases.put(timeStamp, ratedTestCase);
		return true; // we just return true to accommodate the history interface
	}

	/**
	 * Adds RatedTestCase to this SequentialTestCase.
	 *
	 * @param ratedTestCase The RatedTestCase which will be added
	 * @return true if the RatedTestCase was added to this SequntialTestCase
	 * @deprecated use {@link #addCase(RatedTestCase)} instead
	 */
	@Deprecated
	public boolean add(RatedTestCase ratedTestCase) {
		return addCase(ratedTestCase);
	}

	/**
	 * Inverses the rating comparator of all RatedSolutions in all
	 * RatedTestCases of this SequentialTestCase.
	 *
	 * @deprecated no longer implemented
	 */
	@Deprecated
	public void inverseSortSolutions() {
	}

	/**
	 * Here, the name is copied and new instances of the contained test cases
	 * are created. The objects within the test cases are not created again but
	 * taken from the original one.
	 *
	 * @return a flat copy of the instance
	 */
	public SequentialTestCase flatClone() {
		SequentialTestCase newSTC = new SequentialTestCase();
		newSTC.setName(this.getName());
		for (RatedTestCase rtc : ratedTestCases.values()) {
			newSTC.addCase(rtc.flatClone());
		}
		return newSTC;
	}

	/**
	 * Shows String Representation of this SequentialTestCase
	 * <p>
	 * name: ratedTestCase, RatedTestCase, ...
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(getName() + ": ");
		for (RatedTestCase rtc : ratedTestCases.values()) {
			builder.append(rtc.toString()).append(", ");
		}
		builder.replace(builder.length() - 2, builder.length(), ""); // remove last
		// ", "
		return builder.toString();
	}

	/**
	 * Returns the name of this SequentialTestCase.
	 *
	 * @return name of this SequentialTestCase
	 */
	public synchronized String getName() {
		return getDescription();
	}

	/**
	 * Sets the name of this SequentialTestCase.
	 *
	 * @param name desired name of this SequentialTestCase
	 */
	public synchronized void setName(String name) {
		setDescription(name);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
		result = prime * result
				+ ((ratedTestCases == null) ? 0 : ratedTestCases.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof SequentialTestCase)) return false;
		SequentialTestCase other = (SequentialTestCase) obj;
		if (getDescription() == null) {
			if (other.getDescription() != null) return false;
		}
		else if (!getDescription().equals(other.getDescription())) return false;
		if (ratedTestCases == null) {
			if (other.ratedTestCases != null) return false;
		}
		else if (!ratedTestCases.equals(other.ratedTestCases)) return false;
		return true;
	}

	/**
	 * Returns the SequentialTestCase's RatedTestCases
	 *
	 * @return List of RatedTestCases
	 */
	public List<RatedTestCase> getCases() {
		return new ArrayList<>(ratedTestCases.values());
	}

	/**
	 * Returns the {@link RatedTestCase} for a given timeStamp. If there is no
	 * case, null is returned.
	 *
	 * @param timeStamp the Date of the case to return
	 * @return the case of the given timeStamp
	 * @created 31.10.2013
	 */
	public RatedTestCase getCase(Date timeStamp) {
		return ratedTestCases.get(timeStamp);
	}

	/**
	 * Tests if this SequentialTestCase contains the same RatedTestCase as
	 * another SequentialTestCase
	 *
	 * @param obj Other SequentialTestCase
	 * @return true, if RatedTestCases are equal false, if RatedTestCases aren't
	 * equal
	 */
	public boolean testTo(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof SequentialTestCase)) return false;
		SequentialTestCase other = (SequentialTestCase) obj;
		if (ratedTestCases == null) {
			if (other.ratedTestCases != null) return false;
		}
		else if (!ratedTestCases.values().containsAll(other.ratedTestCases.values())) return false;
		return true;
	}

}
