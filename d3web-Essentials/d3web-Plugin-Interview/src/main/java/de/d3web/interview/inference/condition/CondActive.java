package de.d3web.interview.inference.condition;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.TerminalCondition;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.interview.Form;
import de.d3web.interview.Interview;
import de.d3web.interview.inference.PSMethodInterview;
import de.d3web.strings.Strings;

/**
 * Checks whether the given QASets are currently active or not.
 * Active Questions or QContainer are shown to the user because they are indicated in any way (either normally,
 * repeatedly, instantly
 * or any other indication type...)
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 09.06.16
 */
public class CondActive extends TerminalCondition {

	private QASet[] qaSets;
	private boolean exclusive;

	/**
	 * Creates a new CondActive, checking whether the given QASets are active or not.
	 *
	 * @param qaSets the QASets to check whether they are active or not
	 */
	public CondActive(QASet... qaSets) {
		this(false, qaSets);
	}

	/**
	 * Creates a new CondActive, checking whether the given QASets are active or not. In exclusive mode, the condition
	 * also checks, if the given QASets are the only currently active QASets.
	 *
	 * @param exclusive if true, the condition will check if the given QASets are active exclusively
	 * @param qaSets    the QASets to check whether they are active or not
	 */
	public CondActive(boolean exclusive, QASet... qaSets) {
		super((TerminologyObject[]) qaSets);
		if (qaSets.length == 0) throw new IllegalArgumentException("At least one QASet is needed for CondActive.");
		if (Stream.of(qaSets).filter(qaSet -> qaSet == null).findAny().isPresent()) {
			throw new NullPointerException("QASets cannot be null");
		}
		this.exclusive = exclusive;
		this.qaSets = qaSets;
	}

	/**
	 * The QASets of this condition to be expected active.
	 *
	 * @return the QASets of this condition
	 */
	public QASet[] getQaSets() {
		return qaSets;
	}

	/**
	 * Determines whether the QASets of this condition are expected to also be the only currently active QASets
	 *
	 * @return whether the QASets should be active exclusively or not
	 */
	public boolean isExclusive() {
		return exclusive;
	}

	@Override
	public boolean eval(Session session) throws NoAnswerException, UnknownAnswerException {
		Interview interview = session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class));
		Form form = interview.nextForm();
		List<Question> activeQuestions = form.getActiveQuestions();
		boolean allActive = activeQuestions.containsAll(Arrays.asList(getQaSets()));
		if (exclusive) {
			return allActive && activeQuestions.size() == getQaSets().length;
		}
		else {
			return allActive;
		}
	}

	@Override
	public String toString() {
		return Strings.concat(", ", qaSets) + (getQaSets().length == 1 ? " is " : " are ")
				+ (exclusive ? "exclusively " : "") + "active";
	}

}
