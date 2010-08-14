package de.d3web.core.session.blackboard;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.indication.inference.PSMethodUserSelected;

/**
 * A factory to create {@link Fact} instances.
 * 
 * @author joba
 */
public class FactFactory {

	/**
	 * Creates a new fact assigning the specified {@link Value} to the specified
	 * {@link TerminologyObject}. The specified source is responsible for the
	 * setting the value, which acts in the context of the specified
	 * {@link PSMethod}.
	 * 
	 * @param terminologyObject the specified {@link TerminologyObject} instance
	 * @param value the specified {@link Value} instance
	 * @param source the responsible source
	 * @param psMethod the fact is created in the context of the specified
	 *        {@link PSMethod}
	 * @return a newly created {@link Fact} instance
	 */
	public static Fact createFact(TerminologyObject terminologyObject,
			Value value, Object source, PSMethod psMethod) {
		return new DefaultFact(terminologyObject, value, source, psMethod);
	}

	/**
	 * A new fact is created assigning the specified {@link Value} to the
	 * specified {@link TerminologyObject}. The source and psMethod context of
	 * this fact is the user (i.e., {@link PSMethodUserSelected}).
	 * 
	 * @param terminologyObject the specified {@link TerminologyObject} instance
	 * @param value the specified {@link Value} instance
	 * @return a newly created {@link Fact} instance
	 */
	public static Fact createUserEnteredFact(
			TerminologyObject terminologyObject, Value value) {
		return new DefaultFact(terminologyObject, value, PSMethodUserSelected.class,
				PSMethodUserSelected.getInstance());
	}

	/**
	 * A new fact is created assigning a {@link ChoiceValue} to a
	 * {@link QuestionChoice}. The {@link QuestionChoice} and the {@link Choice}
	 * are searched by their ids in the given {@link KnowledgeBase}. The source
	 * and psMethod context of this fact is the user (i.e.,
	 * {@link PSMethodUserSelected}).
	 * 
	 * @param kb {@link KnowledgeBase}
	 * @param questionID ID of the {@link QuestionChoice}
	 * @param answerID ID of the {@link Choice}
	 * @return a newly created {@link Fact} instance or null, if the
	 *         {@link QuestionChoice} or {@link Choice} could not be found
	 */
	public static Fact createUserEnteredFact(KnowledgeBase kb, String questionID, String answerID) {
		Question question = kb.searchQuestion(questionID);
		if (question == null) {
			// if not found, then try to find a question with this name
			question = (Question) kb.searchObjectForName(questionID);
		}
		if (question != null && question instanceof QuestionChoice) {
			QuestionChoice qc = (QuestionChoice) question;
			KnowledgeBaseManagement kbm = KnowledgeBaseManagement.createInstance(kb);
			Choice choice = kbm.findChoice(qc, answerID);
			if (choice != null) {
				return createUserEnteredFact(qc, new ChoiceValue(choice));
			}
		}
		return null;
	}

	/**
	 * A new fact is created assigning a {@link NumValue} to a
	 * {@link QuestionNum}. The {@link QuestionNum} is searched by its id in the
	 * given {@link KnowledgeBase}. The source and psMethod context of this fact
	 * is the user (i.e., {@link PSMethodUserSelected}).
	 * 
	 * @param kb {@link KnowledgeBase}
	 * @param questionID ID of the {@link QuestionNum}
	 * @param value the Double value of the question
	 * @return a newly created {@link Fact} instance or null, if the
	 *         {@link QuestionChoice} could not be found
	 */
	public static Fact createUserEnteredFact(KnowledgeBase kb, String questionID, Double value) {
		Question question = kb.searchQuestion(questionID);
		if (question == null) {
			// if not found, then try to find a question with this name
			question = (Question) kb.searchObjectForName(questionID);
		}
		if (question != null && question instanceof QuestionNum) {
			QuestionNum qn = (QuestionNum) question;
			return createUserEnteredFact(qn, new NumValue(value));
		}
		return null;
	}
}
