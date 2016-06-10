package de.d3web.interview.inference.condition;

import de.d3web.core.inference.condition.CondQuestion;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.interview.Form;
import de.d3web.interview.Interview;
import de.d3web.interview.inference.PSMethodInterview;

/**
 * Checks whether the given question is currently active or not.
 * Active questions are shown to the user because they are indicated in any way (either normally, repeatedly, instantly
 * or any other indication type...)
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 09.06.16
 */
public class CondActive extends CondQuestion {

	public CondActive(Question question) {
		super(question);
	}

	@Override
	public boolean eval(Session session) throws NoAnswerException, UnknownAnswerException {
		Interview interview = session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class));
		Form form = interview.nextForm();
		return form.getActiveQuestions().contains(getQuestion());
	}

	@Override
	public String toString() {
		return getQuestion().getName() + " is active";
	}
}
