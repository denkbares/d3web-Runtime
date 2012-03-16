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

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

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
import de.d3web.empiricaltesting.CaseUtils;
import de.d3web.empiricaltesting.RatedSolution;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.testcase.TestCaseUtils;
import de.d3web.testcase.model.Check;
import de.d3web.testcase.model.DefaultFinding;
import de.d3web.testcase.model.Finding;
import de.d3web.testcase.model.TestCase;

/**
 * Wraps an {@link SequentialTestCase} to a {@link TestCase}
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 24.01.2012
 */
public class STCWrapper implements TestCase {

	private final SequentialTestCase stc;
	private Date startDate;

	public STCWrapper(SequentialTestCase stc) {
		this.stc = stc;
	}

	@Override
	public Collection<Date> chronology() {
		List<Date> dates = new LinkedList<Date>();
		if (stc.getCases().size() > 0 && stc.getCases().get(0).getTimeStamp() != null) {
			for (RatedTestCase rtc : stc.getCases()) {
				if (rtc.getTimeStamp() == null) {
					throw new IllegalArgumentException(
							"All rtcs must contain a date or none of the rtcs must contain a date, mixing is not allowed.");
				}
				Date date = rtc.getTimeStamp();
				if (dates.size() == 0 || date != dates.get(dates.size() - 1)) {
					dates.add(date);
				}
			}
		}
		else {
			long time = getStartDate().getTime();
			for (RatedTestCase rtc : stc.getCases()) {
				// increment time
				dates.add(new Date(++time));
				if (rtc.getTimeStamp() != null) {
					throw new IllegalArgumentException(
							"All rtcs must contain a date or none of the rtcs must contain a date, mixing is not allowed.");
				}
			}
		}
		return dates;
	}

	@Override
	public Collection<Finding> getFindings(Date date, KnowledgeBase kb) {
		List<Finding> findings = new LinkedList<Finding>();
		for (RatedTestCase rtc : stc.getCases()) {
			Date timeStamp = rtc.getTimeStamp();
			if (timeStamp == null) {
				// create the timestamp based on the position in the list
				timeStamp = new Date(startDate.getTime() + stc.getCases().indexOf(rtc) + 1);
			}
			if (date.equals(timeStamp)) {
				for (de.d3web.empiricaltesting.Finding f : rtc.getFindings()) {
					Question question = f.getQuestion();
					if (question.getKnowledgeBase() != kb) {
						question = kb.getManager().searchQuestion(question.getName());
						if (question == null) continue;
					}
					findings.add(new DefaultFinding(question, repairValue(f, question), date));
				}
			}
			if (timeStamp.after(date)) {
				break;
			}
		}
		return findings;
	}

	private QuestionValue repairValue(de.d3web.empiricaltesting.Finding f, Question question) {
		QuestionValue value = f.getValue();
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
		List<Check> checks = new LinkedList<Check>();
		for (RatedTestCase rtc : stc.getCases()) {
			Date timeStamp = rtc.getTimeStamp();
			if (timeStamp == null) {
				// create the timestamp based on the position in the list
				timeStamp = new Date(startDate.getTime() + stc.getCases().indexOf(rtc) + 1);
			}
			if (date.equals(timeStamp)) {
				for (RatedSolution f : rtc.getExpectedSolutions()) {
					Solution solution = f.getSolution();
					if (solution.getKnowledgeBase() != kb) {
						solution = kb.getManager().searchSolution(solution.getName());
						if (solution == null) continue;
					}
					checks.add(new DerivedSolutionCheck(solution,
							CaseUtils.getState(f.getRating())));
				}
				for (de.d3web.empiricaltesting.Finding f : rtc.getExpectedFindings()) {
					Question question = f.getQuestion();
					if (question.getKnowledgeBase() != kb) {
						question = kb.getManager().searchQuestion(question.getName());
						if (question == null) continue;
					}
					checks.add(new DerivedQuestionCheck(question, repairValue(f, question)));
				}
			}
			if (timeStamp.after(date)) {
				break;
			}
		}
		return checks;
	}

	@Override
	public Date getStartDate() {
		if (startDate == null) {
			if (stc.getStartDate() != null) {
				startDate = stc.getStartDate();
			}
			else if (stc.getCases().size() > 0) {
				RatedTestCase firstRTC = stc.getCases().get(0);
				if (firstRTC.getTimeStamp() != null) {
					startDate = firstRTC.getTimeStamp();
				}
				else {
					startDate = new Date();
				}
			}
			else {
				startDate = new Date();
			}
		}
		return startDate;
	}

	@Override
	public Collection<String> check(KnowledgeBase kb) {
		Collection<String> errors = new HashSet<String>();
		for (RatedTestCase rtc : stc.getCases()) {
			Collection<de.d3web.empiricaltesting.Finding> findings = new LinkedList<de.d3web.empiricaltesting.Finding>();
			findings.addAll(rtc.getFindings());
			findings.addAll(rtc.getExpectedFindings());
			for (de.d3web.empiricaltesting.Finding f : findings) {
				String questionName = f.getQuestion().getName();
				Question question = kb.getManager().searchQuestion(questionName);
				if (question == null) {
					errors.add("Question \"" + questionName
							+ "\" is not contained in the KB.");
				}
				else {
					TestCaseUtils.checkValues(errors, question, repairValue(f, question));
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
