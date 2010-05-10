package de.d3web.core.session.interviewmanager;

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;

public interface Interview {

	/**
	 * Returns the Form object to be answered next / now. Answers should
	 * usually only be given by a source solver. Usually three types of
	 * interviews should be distinguished:
	 * <ul>
	 * <li> {@link Questionnaire}: All successor questions (direct and indirect
	 * children) of the questionnaire should be answered by the caller as long
	 * as they are relevant, see
	 * {@link Question#isValid(de.d3web.kernel.Session)}.
	 * <li> {@link Question}: The caller should only answer exactly that
	 * question.
	 * <li> {@link Solution}: The caller should present the user exactly that
	 * solution and "answer" it by setting a state for this solution.
	 * </ul>
	 * 
	 * @return
	 */
	Form nextForm();
	
	/**
	 * Interface to notify the Interview, that the value of a fact has changed.
	 * @param changedFact the changed fact with the new and the old value
	 */
	void notifyFactChange(PropagationEntry changedFact);

	/**
	 * Returns the agenda of the currently running {@link Interview} instance.
	 * @return the {@link InterviewAgenda} instance of the currently running {@link Interview}.
	 */
	InterviewAgenda getInterviewAgenda();
}
