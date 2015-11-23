package de.d3web.testcase.prefix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.testcase.model.Check;
import de.d3web.testcase.model.DefaultFinding;
import de.d3web.testcase.model.DescribedTestCase;
import de.d3web.testcase.model.Finding;
import de.d3web.testcase.model.TestCase;

/**
 * This class allows to add a {@link TestCase} as a prefix to another
 * {@link TestCase}. The start date will be the one of the prefix and the last
 * finding and check of the prefix will be merged with the first
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 15.02.2013
 */
public class PrefixedTestCase implements DescribedTestCase {

	private boolean initialized = false;
	private final TestCase prefix;
	private final TestCase testCase;
	private long dateDiff = 0;
	private Date mergedDate = null;
	private LinkedHashSet<Date> mergedChronology = null;

	public PrefixedTestCase(TestCase prefix, TestCase testCase) {
		this.prefix = prefix;
		this.testCase = testCase;
	}

	/**
	 * Returns the difference or offset to be applied to the dates of the actual test case so they fit behind the dates
	 * of the prefix test case
	 */
	public long getDateDiff() {
		lazyInit();
		return dateDiff;
	}

	/**
	 * Returns the date at which both test cases (prefix and actual) are merged. At this date, both testcase can have
	 * findings and checks.
	 */
	public Date getMergeDate() {
		lazyInit();
		return mergedDate;
	}

	@Override
	public String getDescription() {
		if (testCase instanceof DescribedTestCase) {
			return ((DescribedTestCase) testCase).getDescription();
		}
		return null;
	}

	/**
	 * Returns the prefix part of this PrefixedTestCase
	 */
	public TestCase getPrefix() {
		return prefix;
	}

	/**
	 * Returns the actual TestCase of this PrefixedTestCase. Meaning: The part after the prefix.
	 */
	public TestCase getTestCase() {
		return testCase;
	}

	@Override
	public boolean hasDescriptions() {
		if (prefix instanceof DescribedTestCase && testCase instanceof DescribedTestCase) {
			return ((DescribedTestCase) prefix).hasDescriptions() || ((DescribedTestCase) testCase).hasDescriptions();
		}
		else {
			return DescribedTestCase.super.hasDescriptions();
		}
	}

	private void lazyInit() {
		if (!initialized) {
			init();
		}
	}

	private void init() {
		List<Date> prefixChronology = new ArrayList<>(prefix.chronology());
		Collection<Date> testCaseChronology = testCase.chronology();
		if (testCaseChronology.isEmpty()) return;
		Date lastPrefixDate = prefixChronology.get(prefixChronology.size() - 1);
		Date firstTestCaseDate = testCaseChronology.iterator().next();
		dateDiff = firstTestCaseDate.getTime() - lastPrefixDate.getTime();
		mergedDate = lastPrefixDate;
		initialized = true;
	}

	private Date toPrefixDate(Date testCaseDate) {
		return new Date(testCaseDate.getTime() - dateDiff);
	}

	private Date toTestCaseDate(Date prefixDate) {
		return new Date(prefixDate.getTime() + dateDiff);
	}

	private DefaultFinding toPrefixFinding(Finding testCaseFinding) {
		return new DefaultFinding(
				testCaseFinding.getTerminologyObject(),
				testCaseFinding.getValue());
	}

	@Override
	public Collection<Date> chronology() {
		lazyInit();
		if (this.mergedChronology != null) {
			return this.mergedChronology;
		}
		Collection<Date> prefixChronology = prefix.chronology();
		Collection<Date> testCaseChronology = testCase.chronology();
		mergedChronology = new LinkedHashSet<>(prefixChronology.size()
				+ testCaseChronology.size());
		mergedChronology.addAll(prefixChronology);
		for (Date testCaseDate : testCaseChronology) {
			mergedChronology.add(toPrefixDate(testCaseDate));
		}
		return mergedChronology;
	}

	@Override
	public Collection<Finding> getFindings(Date date, KnowledgeBase knowledgeBase) {
		lazyInit();
		if (!chronology().contains(date)) return Collections.emptyList();
		if (date.before(mergedDate)) {
			return prefix.getFindings(date, knowledgeBase);
		}
		else if (date.after(mergedDate)) {
			Collection<Finding> testCaseFindings = testCase.getFindings(toTestCaseDate(date), knowledgeBase);
			Collection<Finding> transformedFindings = new ArrayList<>(
					testCaseFindings.size());
			for (Finding testCaseFinding : testCaseFindings) {
				transformedFindings.add(toPrefixFinding(testCaseFinding));
			}
			return transformedFindings;
		}
		else {
			Collection<Finding> prefixFindings = prefix.getFindings(date, knowledgeBase);
			Collection<Finding> testCaseFindings = testCase.getFindings(toTestCaseDate(date), knowledgeBase);
			LinkedHashMap<TerminologyObject, Finding> mergedFindings = new LinkedHashMap<>();
			for (Finding prefixFinding : prefixFindings) {
				mergedFindings.put(prefixFinding.getTerminologyObject(), prefixFinding);
			}
			for (Finding testCaseFinding : testCaseFindings) {
				mergedFindings.put(testCaseFinding.getTerminologyObject(),
						toPrefixFinding(testCaseFinding));
			}
			return mergedFindings.values();
		}
	}

	@Override
	public String getDescription(Date date) {
		if (!chronology().contains(date)) return null;
		if (date.before(mergedDate) && prefix instanceof DescribedTestCase) {
			return ((DescribedTestCase) prefix).getDescription(date);
		}
		else if (date.after(mergedDate) && testCase instanceof DescribedTestCase) {
			return ((DescribedTestCase) testCase).getDescription(toTestCaseDate(date));
		}
		else {
			String comment = null;
			if (prefix instanceof DescribedTestCase) {
				comment = ((DescribedTestCase) prefix).getDescription(date);
			}
			if (testCase instanceof DescribedTestCase) {
				String tcComment = ((DescribedTestCase) testCase).getDescription(toTestCaseDate(date));
				if (comment == null) {
					comment = tcComment;
				}
				else {
					comment += "\n" + tcComment;
				}
			}
			return comment;
		}
	}

	@Override
	public Collection<Check> getChecks(Date date, KnowledgeBase knowledgeBase) {
		lazyInit();
		if (!chronology().contains(date)) return Collections.emptyList();
		if (date.before(mergedDate)) {
			return prefix.getChecks(date, knowledgeBase);
		}
		else if (date.after(mergedDate)) {
			return testCase.getChecks(toTestCaseDate(date), knowledgeBase);
		}
		else {
			Collection<Check> prefixChecks = prefix.getChecks(date, knowledgeBase);
			Collection<Check> testCaseChecks = testCase.getChecks(toTestCaseDate(date), knowledgeBase);
			ArrayList<Check> mergedChecks = new ArrayList<>(prefixChecks.size()
					+ testCaseChecks.size());
			mergedChecks.addAll(prefixChecks);
			mergedChecks.addAll(testCaseChecks);
			return mergedChecks;
		}
	}

	@Override
	public Date getStartDate() {
		lazyInit();
		return prefix.getStartDate();
	}

	@Override
	public Collection<String> check(KnowledgeBase knowledgeBase) {
		lazyInit();
		Collection<String> prefixMessages = prefix.check(knowledgeBase);
		Collection<String> testCaseMessages = testCase.check(knowledgeBase);
		ArrayList<String> mergedMessages = new ArrayList<>(prefixMessages.size()
				+ testCaseMessages.size());
		mergedMessages.addAll(prefixMessages);
		mergedMessages.addAll(testCaseMessages);
		return mergedMessages;
	}

	@Override
	public void applyFindings(Date date, Session session, Settings settings) {
		lazyInit();
		if (!chronology().contains(date)) return;
		if (date.before(mergedDate)) {
			prefix.applyFindings(date, session, settings);
		}
		else {
			Settings newSettings = new Settings(settings.isSkipNumValueOutOfRange(), settings.getTimeShift() - dateDiff);
			if (date.after(mergedDate)) {
				testCase.applyFindings(toTestCaseDate(date), session, newSettings);
			}
			else {
				prefix.applyFindings(date, session, settings);
				testCase.applyFindings(toTestCaseDate(date), session, newSettings);
			}
		}
	}

}
