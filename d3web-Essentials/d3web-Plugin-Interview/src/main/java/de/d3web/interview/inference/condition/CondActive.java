package de.d3web.interview.inference.condition;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.TerminalCondition;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.interview.Form;
import de.d3web.interview.Interview;
import de.d3web.interview.inference.PSMethodInterview;
import de.d3web.strings.Strings;

/**
 * Checks whether the given QASets are currently active or not.
 * Active Questions or QContainer are shown to the user because they are indicated in any way (either normally,
 * repeatedly, instantly or any other indication type...)
 * <p/>
 * <b>ATTENTION: This Condition can not be used in Rules, because Rules are not notified about indication changes (only
 * fact changes).</b>
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 09.06.16
 */
public class CondActive extends TerminalCondition {

	private boolean exclusive;
	private @Nullable QContainer rootQContainer;
	private @NotNull Question[] activeQuestions;

	/**
	 * Creates a new CondActive, checking whether the given QASets are active or not. The QASets can either be a single
	 * QContainer (the active root of the next form), a single root QContainer followed by list of active Questions, or
	 * a list of active Questions only. The questions will be checked in the given order. If no root QContainer is
	 * given, the existence of a root QContainer is not checked.
	 *
	 * @param qaSets the QASets to check whether they are active or not
	 */
	public CondActive(QASet... qaSets) {
		this(false, qaSets);
	}

	/**
	 * Creates a new CondActive, checking whether the given QASets are active or not. In exclusive mode, the condition
	 * also checks, if the given QASets are the only currently active QASets. The QASets can either be a single
	 * QContainer (the active root of the next form), a single root QContainer plus a list of active Questions, or a
	 * list of active Questions only. The questions will be checked in the given order. If no root QContainer is given,
	 * the existence of a root QContainer is not checked.
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
		if (qaSets.length == 1) {
			if (qaSets[0] instanceof QContainer) {
				init(exclusive, (QContainer) qaSets[0]);
			}
			else {
				init(exclusive, null, (Question) qaSets[0]);
			}
		}
		if (qaSets.length > 1) {
			if (qaSets[0] instanceof QContainer) {
				if (Stream.of(qaSets).filter(qaSet -> qaSet instanceof QContainer).count() == 1) {
					init(exclusive, (QContainer) qaSets[0], Arrays.copyOfRange(qaSets, 1, qaSets.length, Question[].class));
				}
				else {
					throw new IllegalArgumentException("There can only be one active root QContainer in the next form.");
				}
			}
			// qaSets[0] instanceof Question
			else {
				if (Stream.of(qaSets).filter(qaSet -> qaSet instanceof QContainer).findAny().isPresent()) {
					throw new IllegalArgumentException("If the condition should check the root QContainer and its" +
							" questions, the root QContainer as to be the first in the arrays of QContainers");
				}
				else {
					init(exclusive, null, Arrays.copyOfRange(qaSets, 0, qaSets.length, Question[].class));
				}
			}
		}
	}

	/**
	 * Creates a new CondActive, checking whether the given QASets are active or not. In exclusive mode, the condition
	 * also checks, if the given QASets are the only currently active QASets.
	 *
	 * @param exclusive      if true, the condition will check if the given QASets are active exclusively
	 * @param rootQContainer the QASets to check whether they are active or not
	 */
	public CondActive(boolean exclusive, QContainer rootQContainer, Question... activeQuestions) {
		super((TerminologyObject[]) collectQASets(rootQContainer, activeQuestions));
		init(exclusive, rootQContainer, activeQuestions);
	}

	private static QASet[] collectQASets(QContainer rootQContainer, Question... activeQuestions) {
		if (rootQContainer == null && activeQuestions == null) {
			throw new IllegalArgumentException("Either the root QContainer or the active questions have to be non-null.");
		}
		if (rootQContainer == null) return activeQuestions;
		if (activeQuestions == null) return new QASet[] { rootQContainer };
		QASet[] qaSets = new QASet[activeQuestions.length + 1];
		qaSets[0] = rootQContainer;
		System.arraycopy(activeQuestions, 0, qaSets, 1, activeQuestions.length);
		return qaSets;
	}

	private void init(boolean exclusive, QContainer rootQContainer, Question... activeQuestions) {
		this.exclusive = exclusive;
		this.rootQContainer = rootQContainer;
		if (activeQuestions == null) activeQuestions = new Question[0];
		this.activeQuestions = activeQuestions;
	}

	/**
	 * The QASets of this condition to be expected active (root QContainer (if present) and active Questions in one
	 * array).
	 *
	 * @return the QASets of this condition
	 */
	@NotNull
	public QASet[] getQaSets() {
		return collectQASets(rootQContainer, activeQuestions);
	}

	/**
	 * The root QContainer to be expected active in the next form of the interview agend (if present).
	 *
	 * @return the root QContainer expected in this condition
	 */
	@Nullable
	public QContainer getRootQContainer() {
		return rootQContainer;
	}

	/**
	 * The active questions to be checked in this condition.
	 *
	 * @return the active questions to be checked in this condition
	 */
	@NotNull
	public Question[] getActiveQuestions() {
		return activeQuestions;
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
		List<Question> activeQuestionsOfForm = form.getActiveQuestions();
		QContainer rootQContainer = getRootQContainer();

		if (rootQContainer != null && !rootQContainer.equals(form.getRoot())) {
			// if there is a root defined, it has to be equal!
			return false;
		}
		// activeQuestions can not be null
		if (exclusive) {
			return activeQuestionsOfForm.equals(Arrays.asList(getActiveQuestions()));
		}
		else {
			return activeQuestionsOfForm.containsAll(Arrays.asList(getActiveQuestions()));
		}
	}

	@Override
	public String toString() {
		return Strings.concat(", ", getQaSets()) + (getQaSets().length == 1 ? " is " : " are ")
				+ (exclusive ? "exclusively " : "") + "active";
	}

}
