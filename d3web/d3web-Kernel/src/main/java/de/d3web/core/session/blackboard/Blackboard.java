package de.d3web.core.session.blackboard;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import de.d3web.core.kr.Indication;
import de.d3web.core.kr.InterviewObject;
import de.d3web.core.kr.TerminologyObject;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.domainModel.qasets.Question;

/**
 * The Blackboard manages all dynamic values created within the case and
 * propagated throughout the inference system.
 * 
 * @author volker_belli
 * 
 */
public class Blackboard {

	private final XPSCase session;
	private final FactStorage valueStorage;
	private final FactStorage interviewStorage;

	// TODO: also manage the XPSCaseObjects here

	/**
	 * Creates a new Blackboard for the specified xps session.
	 * 
	 * @param session
	 *            the session the blackboard is created for
	 */
	public Blackboard(XPSCase session) {
		this.session = session;
		this.valueStorage = new FactStorage(this);
		this.interviewStorage = new FactStorage(this);
	}

	/**
	 * Returns the session this blackboard has been created for.
	 * 
	 * @return the session of this blackboard
	 */
	public XPSCase getSession() {
		return session;
	}

	/**
	 * Adds a new value fact to this blackboard. If an other fact for the same
	 * terminology object and with the same source has already been added, that
	 * fact will be replaced by the specified one.
	 * 
	 * @param fact
	 *            the fact to be added
	 */
	public void addValueFact(Fact fact) {
		this.valueStorage.add(fact);
	}

	/**
	 * Removes a value fact from this blackboard. If the fact does not exists in
	 * the blackboard, this method has no effect.
	 * 
	 * @param fact
	 *            the fact to be removed
	 */
	public void removeValueFact(Fact fact) {
		this.valueStorage.remove(fact);
	}

	/**
	 * Removes all value facts with the specified source from this blackboard
	 * for the specified terminology object. If no such fact exists in the
	 * blackboard, this method has no effect.
	 * 
	 * @param termObject
	 *            the terminology object to remove the value facts from
	 * @param source
	 *            the fact source to be removed
	 */
	public void removeValueFact(TerminologyObject terminologyObject, Object source) {
		this.valueStorage.remove(terminologyObject, source);
	}

	/**
	 * Returns the merged fact for all value facts of the specified terminology
	 * object.
	 * 
	 * @param terminologyObject
	 *            the terminology object to access the merged fact for
	 * @return the merged fact
	 */
	public Fact getValueFact(TerminologyObject terminologyObject) {
		return this.valueStorage.getAggregator(terminologyObject).getMergedFact();
	}

	/**
	 * Returns a collection of all terminology objects that have a value. This
	 * means this method delivers all terminology objects that currently have at
	 * lead one value fact added for it to this blackboard. The collection may
	 * be unmodifiable.
	 * 
	 * @return the collection of valued terminology objects
	 */
	public Collection<TerminologyObject> getValuedObjects() {
		return Collections.unmodifiableCollection(
				this.valueStorage.getValuedObjects());
	}

	/**
	 * Returns a collection of all questions that have a value. This means this
	 * method delivers all questions that currently have at lead one value fact
	 * added for it to this blackboard. The collection may be unmodifiable.
	 * 
	 * @return the collection of valued questions
	 */
	public Collection<Question> getValuedQuestions() {
		Collection<Question> result = new LinkedList<Question>();
		for (TerminologyObject object : getValuedObjects()) {
			if (object instanceof Question) {
				result.add((Question) object);
			}
		}
		return result;
	}

	/**
	 * Returns a collection of all diagnoses that have a value. This means this
	 * method delivers all diagnoses that currently have at lead one value fact
	 * added for it to this blackboard. The collection may be unmodifiable.
	 * 
	 * @return the collection of valued diagnoses
	 */
	public Collection<Diagnosis> getValuedSolutions() {
		Collection<Diagnosis> result = new LinkedList<Diagnosis>();
		for (TerminologyObject object : getValuedObjects()) {
			if (object instanceof Diagnosis) {
				result.add((Diagnosis) object);
			}
		}
		return result;
	}

	/**
	 * Adds a new interview fact to this blackboard. If an other interview fact
	 * for the same terminology object and with the same source has already been
	 * added, that fact will be replaced by the specified one.
	 * 
	 * @param fact
	 *            the fact to be added
	 */
	public void addInterviewFact(Fact fact) {
		this.interviewStorage.add(fact);
	}

	/**
	 * Removes a interview fact from this blackboard. If the interview fact does
	 * not exists in the blackboard, this method has no effect.
	 * 
	 * @param fact
	 *            the fact to be removed
	 */
	public void removeInterviewFact(Fact fact) {
		this.interviewStorage.remove(fact);
	}

	/**
	 * Removes all interview facts with the specified source from this
	 * blackboard for the specified terminology object. If no such fact exists
	 * in the blackboard, this method has no effect.
	 * 
	 * @param termObject
	 *            the terminology object to remove the interview facts from
	 * @param source
	 *            the fact source to be removed
	 */
	public void removeInterviewFact(TerminologyObject terminologyObject, Object source) {
		this.interviewStorage.remove(terminologyObject, source);
	}

	/**
	 * Removes all interview facts from this blackboard for the specified
	 * terminology object. If no such fact exists in the blackboard, this method
	 * has no effect.
	 * 
	 * @param termObject
	 *            the terminology object to remove the interview facts from
	 */
	public void removeInterviewFacts(TerminologyObject terminologyObject) {
		this.interviewStorage.remove(terminologyObject);
	}

	/**
	 * Returns the merged fact for all interview facts of the specified
	 * terminology object.
	 * 
	 * @param terminologyObject
	 *            the terminology object to access the merged fact for
	 * @return the merged fact
	 */
	public Fact getInterviewFact(TerminologyObject terminologyObject) {
		return this.interviewStorage.getAggregator(terminologyObject).getMergedFact();
	}

	/**
	 * Returns a collection of all terminology objects that have been rated for
	 * the usage in the interview. This means the method delivers all
	 * terminology objects that currently have at lead one interview fact added
	 * for it to this blackboard.
	 * 
	 * @return the collection of interview rated terminology objects
	 */
	public Collection<TerminologyObject> getInterviewObjects() {
		return Collections.unmodifiableCollection(
				this.interviewStorage.getValuedObjects());
	}

	/**
	 * Returns the current rating of the diagnosis. The returned rating is the
	 * merged rating over all problem solvers available. This is a typed
	 * shortcut for accessing the value {@link Fact} of the {@link Diagnosis}
	 * and read out its current value.
	 * 
	 * @param solution
	 *            the solution to take the rating from
	 * @return the total rating of the solution
	 */
	public DiagnosisState getState(Diagnosis solution) {
		return (DiagnosisState) getValueFact(solution).getValue();
	}

	/**
	 * Returns the current answer of the question. The returned answer is the
	 * merged answer over all problem solvers available. This is a typed
	 * shortcut for accessing the value {@link Fact} of the {@link Question} and
	 * read out its current value.
	 * 
	 * @param question
	 *            the question to take the rating from
	 * @return the answer of the question
	 */
	public Answer getAnswer(Question question) {
		return (Answer) getValueFact(question).getValue();
	}

	/**
	 * Returns the current indication state of the interview element. The
	 * returned indication state is the merged indication over all strategic
	 * solvers available. This is a typed shortcut for accessing the interview
	 * {@link Fact} of the {@link QASet} and read out its current value.
	 * 
	 * @param question
	 *            the question to take the rating from
	 * @return the indication of the interview element
	 */
	public Indication getIndication(InterviewObject interviewElement) {
		return (Indication) getInterviewFact(interviewElement).getValue();
	}

}
