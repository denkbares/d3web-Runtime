package de.d3web.core.session.interviewmanager;

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;

public interface Interview {

	Form nextForm();
	
	/**
	 * Optional configuration: Explicitly set a strategy that defines how the nextForm 
	 * method computes the next form. On example is the 
	 * {@link NextUnansweredQuestionFormStrategy}.
	 * @param strategy the specified FormStrategy
	 */
	void setFormStrategy(FormStrategy strategy);
	
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

	/**
	 * Test, if the specified {@link InterviewObject} instance is ACTIVE
	 * with respect to the given state on the {@link InterviewAgenda}.
	 * @param interviewObject the specified {@link InterviewObject} instance
	 * @return true, if the specified {@link InterviewObject} instance has an active state on the {@link InterviewAgenda}
	 */
	boolean isActive(InterviewObject interviewObject);
}
