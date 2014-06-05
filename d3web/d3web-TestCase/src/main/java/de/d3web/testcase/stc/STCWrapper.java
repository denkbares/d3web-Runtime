/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.testcase.stc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.core.inference.condition.CondRegex;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
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
import de.d3web.empiricaltesting.CaseUtils;
import de.d3web.empiricaltesting.RatedSolution;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.RegexFinding;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.testcase.TestCaseUtils;
import de.d3web.testcase.model.Check;
import de.d3web.testcase.model.ConditionCheck;
import de.d3web.testcase.model.DefaultFinding;
import de.d3web.testcase.model.Finding;
import de.d3web.testcase.model.TestCase;

/**
 * Wraps an {@link SequentialTestCase} to a {@link TestCase}
 *
 * @author Markus Friedrich, Albrecht Striffler (denkbares GmbH)
 * @created 24.01.2012
 */
public class STCWrapper implements TestCase {

	private final SequentialTestCase stc;
	private final Map<RatedTestCase, List<Check>> additionalChecks = new IdentityHashMap<RatedTestCase, List<Check>>();

	public STCWrapper(SequentialTestCase stc) {
		this.stc = stc;
	}

	public SequentialTestCase getSequentialTestCase() {
		return this.stc;
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
	 */
	public void addChecks(RatedTestCase rtc, Check... checks) {
		if (checks.length == 0) return;
		List<Check> checksOfRTC = additionalChecks.get(rtc);
		if (checksOfRTC == null) {
			checksOfRTC = new ArrayList<Check>();
			additionalChecks.put(rtc, checksOfRTC);
		}
		for (Check check : checks) {
			checksOfRTC.add(check);
		}
	}

	@Override
	public Collection<Date> chronology() {
		return stc.chronology();
	}

	@Override
	public Collection<Finding> getFindings(Date date, KnowledgeBase kb) {
		RatedTestCase rtc = stc.getCase(date);
		if (rtc == null) return Collections.emptyList();
		List<Finding> findings = new LinkedList<Finding>();
		for (de.d3web.empiricaltesting.Finding f : rtc.getFindings()) {
			Question question = f.getQuestion();
			if (question.getKnowledgeBase() != kb) {
				question = kb.getManager().searchQuestion(question.getName());
				if (question == null) continue;
			}
			findings.add(new DefaultFinding(question, repairValue(f, question), date));
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
			catch (NumberFormatException e) {
				// nothing todo
			}
			catch (IllegalArgumentException e) {
				// nothing todo
			}
			if (value == null) {
				if (question instanceof QuestionOC) {
					value = new ChoiceValue(f.getValuePrompt().toString());
				}
				else {
					value = f.getValue();
				}
			}
		}
		return value;
	}

	@Override
	public Finding getFinding(Date date, TerminologyObject object) {
		Collection<Finding> findings = getFindings(date, object.getKnowledgeBase());
		for (Finding f : findings) {
			if (f.getTerminologyObject() == object) {
				return f;
			}
		}
		return null;
	}

	@Override
	public Collection<Check> getChecks(Date date, KnowledgeBase kb) {
		RatedTestCase rtc = stc.getCase(date);
		List<Check> checks = new LinkedList<Check>();
		// create checks based on stc standard behavior
		for (RatedSolution f : rtc.getExpectedSolutions()) {
			Solution solution = f.getSolution();
			if (solution.getKnowledgeBase() != kb) {
				solution = kb.getManager().searchSolution(solution.getName());
				if (solution == null) continue;
			}
			checks.add(new DerivedSolutionCheck(solution,
					CaseUtils.getState(f.getRating())));
		}
		for (de.d3web.empiricaltesting.Finding finding : rtc.getExpectedFindings()) {
			Question question = finding.getQuestion();
			if (question.getKnowledgeBase() != kb) {
				question = kb.getManager().searchQuestion(question.getName());
				if (question == null) continue;
			}
			checks.add(new DerivedQuestionCheck(question, repairValue(finding, question)));
		}
		for (RegexFinding regexFinding : rtc.getExpectedRegexFindings()) {
			Question question = regexFinding.getQuestion();
			if (question.getKnowledgeBase() != kb) {
				question = kb.getManager().searchQuestion(question.getName());
				if (question == null) continue;
			}
			checks.add(new ConditionCheck(new CondRegex(question, regexFinding.getRegex())));
		}

		// also add additional checks if available
		List<Check> addedChecks = additionalChecks.get(rtc);
		if (addedChecks != null) checks.addAll(addedChecks);

		// retrun the common list of checks
		return checks;
	}

	@Override
	public Date getStartDate() {
		Date startDate = stc.getStartDate();
		return startDate == null ? new Date() : startDate;
	}

	@Override
	public String toString() {
		return "SequentialTestCase(" + this.stc.getName() + ")";
	}

	@Override
	public Collection<String> check(KnowledgeBase kb) {
		Collection<String> errors = new HashSet<String>();
		for (RatedTestCase rtc : stc.getCases()) {
			Collection<de.d3web.empiricaltesting.Finding> findings = new LinkedList<de.d3web.empiricaltesting.Finding>();
			findings.addAll(rtc.getFindings());
			findings.addAll(rtc.getExpectedFindings());
			for (de.d3web.empiricaltesting.Finding finding : findings) {
				String questionName = finding.getQuestion().getName();
				Question question = kb.getManager().searchQuestion(questionName);
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
				Question question = kb.getManager().searchQuestion(questionName);
				if (question == null) {
					errors.add("Question \"" + questionName
							+ "\" is not contained in the KB.");
				}
			}
			for (RatedSolution ratedSolution : rtc.getExpectedSolutions()) {
				String solutionName = ratedSolution.getSolution().getName();
				if (kb.getManager().searchSolution(solutionName) == null) {
					errors.add("Solution \"" + solutionName
							+ "\" is not contained in the KB.");
				}
			}
		}
		return errors;
	}

}
