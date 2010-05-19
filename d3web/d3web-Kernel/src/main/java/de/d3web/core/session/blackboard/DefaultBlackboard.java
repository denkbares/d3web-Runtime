package de.d3web.core.session.blackboard;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PropagationContoller;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.info.Num2ChoiceSchema;
import de.d3web.core.session.DefaultSession;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.indication.inference.PSMethodUserSelected;

/**
 * The Blackboard manages all dynamic values created within the case and
 * propagated throughout the inference system.
 * 
 * @author volker_belli
 * 
 */
public class DefaultBlackboard implements Blackboard {

	private final DefaultSession session;
	private final FactStorage valueStorage;
	private final FactStorage interviewStorage;

	// TODO: also manage the SessionObjects here

	/**
	 * Creates a new Blackboard for the specified xps session.
	 * 
	 * @param session the session the blackboard is created for
	 */
	public DefaultBlackboard(DefaultSession session) {
		this.session = session;
		this.valueStorage = new FactStorage(this);
		this.interviewStorage = new FactStorage(this);
	}

	@Override
	public Session getSession() {
		return session;
	}

	@Override
	public void addValueFact(Fact fact) {
		// First: add the arriving fact to the protocol,
		// if it was entered by the user
		PSMethod psMethod = fact.getPSMethod();
		if (psMethod != null && psMethod.equals(PSMethodUserSelected.getInstance())) {
			getSession().getProtocol().addEntry(fact);
		}

		Value oldValue;
		TerminologyObject terminologyObject = fact.getTerminologyObject();
		if (terminologyObject instanceof Solution) {
			oldValue = getRating((Solution) terminologyObject);
		}
		else if (terminologyObject instanceof Question) {
			oldValue = getValue((Question) terminologyObject);
		}
		else {
			oldValue = getValueFact(terminologyObject).getValue();
		}
		this.valueStorage.add(fact);
		Fact newFact = getValueFact(terminologyObject);
		Value newValue = newFact.getValue();
		if (newValue != oldValue) {
			PropagationContoller propagationContoller = session.getPropagationContoller();
			propagationContoller.openPropagation();
			propagationContoller.propagate(terminologyObject,
					oldValue, newValue);
			propagationContoller.commitPropagation();
			session.notifyListeners(terminologyObject);
		}
	}

	@Override
	public void removeValueFact(Fact fact) {
		this.valueStorage.remove(fact);
	}

	/**
	 * Removes all value facts with the specified source from this blackboard
	 * for the specified terminology object. If no such fact exists in the
	 * blackboard, this method has no effect.
	 * 
	 * @param termObject the terminology object to remove the value facts from
	 * @param source the fact source to be removed
	 */
	public void removeValueFact(TerminologyObject terminologyObject, Object source) {
		this.valueStorage.remove(terminologyObject, source);
	}

	@Override
	public Fact getValueFact(TerminologyObject terminologyObject) {
		return this.valueStorage.getAggregator(terminologyObject).getMergedFact();
	}

	@Override
	public Collection<TerminologyObject> getValuedObjects() {
		return Collections.unmodifiableCollection(
				this.valueStorage.getValuedObjects());
	}

	@Override
	public Collection<Question> getValuedQuestions() {
		Collection<Question> result = new LinkedList<Question>();
		for (TerminologyObject object : getValuedObjects()) {
			if (object instanceof Question) {
				result.add((Question) object);
			}
		}
		return result;
	}

	@Override
	public Collection<Solution> getValuedSolutions() {
		Collection<Solution> result = new LinkedList<Solution>();
		for (TerminologyObject object : getValuedObjects()) {
			if (object instanceof Solution) {
				result.add((Solution) object);
			}
		}
		return result;
	}

	@Override
	public void addInterviewFact(Fact fact) {
		// Besides adding the new fact to the interview management, we also do
		// the notification
		// of this new indication fact: this notification may be removed due to
		// Session/Blackboard
		// refactoring, i.e., when the notification is done at an upper place

		InterviewObject factObject = (InterviewObject) fact.getTerminologyObject();

		Value oldValue = getIndication(factObject);

		this.interviewStorage.add(fact);

		Value newValue = getIndication(factObject);
		propagateIndicationChange(fact.getTerminologyObject(), oldValue, newValue);
	}

	@Override
	public void removeInterviewFact(Fact fact) {
		// Besides removing the fact to the interview management, we also do the
		// notification
		// of this deletion: this notification may be removed due to
		// Session/Blackboard
		// refactoring, i.e., when the notification is done at an upper place
		InterviewObject factObject = (InterviewObject) fact.getTerminologyObject();
		Value oldValue = getIndication(factObject);

		this.interviewStorage.remove(fact);

		Value newValue = getIndication(factObject);
		propagateIndicationChange(factObject, oldValue, newValue);
	}

	@Override
	public void removeInterviewFact(TerminologyObject terminologyObject, Object source) {
		// Besides removing the fact to the interview management, we also do the
		// notification
		// of this deletion: this notification may be removed due to
		// Session/Blackboard
		// refactoring, i.e., when the notification is done at an upper place

		InterviewObject factObject = (InterviewObject) terminologyObject;
		Value oldValue = getIndication(factObject);

		this.interviewStorage.remove(terminologyObject, source);

		Value newValue = getIndication(factObject);
		propagateIndicationChange(terminologyObject, oldValue, newValue);
	}

	private void propagateIndicationChange(TerminologyObject interviewObject, Value oldValue,
			Value newValue) {
		PropagationEntry entry = new PropagationEntry(interviewObject, oldValue,
				newValue);
		session.getInterviewManager().notifyFactChange(entry);
	}

	@Override
	public void removeInterviewFacts(TerminologyObject terminologyObject) {
		this.interviewStorage.remove(terminologyObject);
	}

	@Override
	public Fact getInterviewFact(TerminologyObject terminologyObject) {
		return this.interviewStorage.getAggregator(terminologyObject).getMergedFact();
	}

	@Override
	public Collection<TerminologyObject> getInterviewObjects() {
		return Collections.unmodifiableCollection(
				this.interviewStorage.getValuedObjects());
	}

	@Override
	public Rating getRating(Solution solution) {
		Fact valueFact = getValueFact(solution);
		if (valueFact != null) {
			return (Rating) valueFact.getValue();
		}
		else {
			return new Rating(Rating.State.UNCLEAR);
		}
	}

	@Override
	public Value getValue(Question question) {
		Fact fact = getValueFact(question);
		if (fact == null) {
			return UndefinedValue.getInstance();
		}
		else {
			Value value = fact.getValue();
			if (question instanceof QuestionOC && value instanceof NumValue) {
				QuestionOC qoc = (QuestionOC) question;
				Num2ChoiceSchema schema = qoc.getSchemaForQuestion();
				NumValue numValue = (NumValue) value;
				if (schema != null) {
					return schema.getValueForNum((Double) numValue.getValue(),
							qoc.getAllAlternatives(), session);
				}
			}
			return value;
		}
	}

	@Override
	public Value getValue(Question object, PSMethod psmethod) {
		Fact mergedFact = valueStorage.getAggregator(object).getMergedFact(psmethod);
		if (mergedFact == null) {
			return UndefinedValue.getInstance();
		}
		else {
			return mergedFact.getValue();
		}
	}

	@Override
	public Indication getIndication(InterviewObject interviewElement) {
		Fact fact = getInterviewFact(interviewElement);
		if (fact == null) {
			return Indication.getDefaultIndication();
		}
		else {
			return (Indication) getInterviewFact(interviewElement).getValue();
		}
	}

	@Override
	public List<Question> getAnsweredQuestions() {
		List<Question> questions = new LinkedList<Question>();
		for (Question q : session.getKnowledgeBase().getQuestions()) {
			Fact mergedFact = valueStorage.getAggregator(q).getMergedFact();
			if (mergedFact != null && UndefinedValue.isNotUndefinedValue(mergedFact.getValue())) {
				questions.add(q);
			}
		}
		return questions;
	}

	@Override
	public List<Solution> getSolutions(Rating.State state) {
		List<Solution> result = new LinkedList<Solution>();
		for (Solution diag : getSession().getKnowledgeBase().getSolutions()) {
			if (getRating(diag).getState().equals(state)) {
				result.add(diag);
			}
		}
		return result;
	}

	@Override
	public Rating getRating(Solution solution, PSMethod psmethod) {
		Fact mergedFact = valueStorage.getAggregator(solution).getMergedFact(psmethod);
		if (mergedFact == null) {
			return new Rating(State.UNCLEAR);
		}
		else {
			return (Rating) mergedFact.getValue();
		}
	}
}
