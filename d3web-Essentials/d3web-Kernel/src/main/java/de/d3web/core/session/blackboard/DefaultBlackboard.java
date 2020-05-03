/*
 * Copyright (C) 2010 denkbares GmbH, Würzburg, Germany
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

package de.d3web.core.session.blackboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PSMethod.Type;
import de.d3web.core.inference.PropagationManager;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.protocol.FactProtocolEntry;
import de.d3web.core.session.values.UndefinedValue;

/**
 * The Blackboard manages all dynamic values created within the case and propagated throughout the inference system.
 *
 * @author volker_belli
 */
public class DefaultBlackboard implements Blackboard {

	private final Session session;
	private FactStorage valueStorage;
	private FactStorage interviewStorage;
	private final List<BlackboardListener> listeners = new LinkedList<>();

	private boolean sourceRecording = true;

	@Override
	public boolean isSourceRecording() {
		return sourceRecording;
	}

	@Override
	public void setSourceRecording(boolean autosaveSource) {
		this.sourceRecording = autosaveSource;
	}

	/**
	 * Creates a new Blackboard for the specified xps session.
	 *
	 * @param session the session the blackboard is created for
	 */
	public DefaultBlackboard(Session session) {
		this.session = session;
		this.valueStorage = new DefaultFactStorage();
		this.interviewStorage = new DefaultFactStorage();
	}

	/**
	 * Creates a new default blackboard by an existing one, sharing the fact storages.
	 *
	 * @param session             the session to construct this blackboard for
	 * @param factStoragesToShare the blackboard to share the fact storages
	 */
	protected DefaultBlackboard(Session session, DefaultBlackboard factStoragesToShare) {
		this.session = session;
		this.valueStorage = factStoragesToShare.valueStorage;
		this.interviewStorage = factStoragesToShare.interviewStorage;
	}

	@NotNull
	@Override
	public Session getSession() {
		return session;
	}

	@Override
	public void addValueFact(Fact fact) {

		TerminologyObject terminologyObject = fact.getTerminologyObject();
		PSMethod psMethod = fact.getPSMethod();

		// We never add a Fact with value Undefined. Instead, if we get an fact with Undefined,
		// we retract the existing fact for that PSMethod and source (if one exists).
		if (UndefinedValue.isUndefinedValue(fact.getValue())) {
			Fact oldFact = this.getValueStorage().getFact(terminologyObject, psMethod, fact.getSource());
			if (oldFact != null) {
				removeValueFact(oldFact);
			}
			return;
		}

		Value oldValue = getValue((ValueObject) terminologyObject);
		this.getValueStorage().add(fact);
		propagate(terminologyObject, oldValue, fact.getPSMethod().hasType(Type.source));
		// add the arriving fact to the protocol
		// if it was entered by a source psmethod
		if (isSourceRecording() && psMethod != null && psMethod.hasType(Type.source)) {
			getSession().getProtocol()
					.addEntry(new FactProtocolEntry(session.getPropagationManager().getPropagationTime(), fact));
		}
	}

	/**
	 * Propagates if the value of the terminology object has changed
	 */
	private void propagate(TerminologyObject terminologyObject, Value oldValue, boolean force) {
		PropagationManager propagationManager = session.getPropagationManager();
		if (force) {
			propagationManager.forcePropagate((ValueObject) terminologyObject, oldValue);
		}
		else {
			propagationManager.propagate((ValueObject) terminologyObject, oldValue);
		}
		for (BlackboardListener listener : listeners) {
			listener.factsChanged(terminologyObject);
		}
	}

	@Override
	public void addBlackboardListner(BlackboardListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeBlackboardListner(BlackboardListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public void removeValueFact(Fact fact) {
		Value oldValue = getValue((ValueObject) fact.getTerminologyObject());
		this.getValueStorage().remove(fact);
		propagate(fact.getTerminologyObject(), oldValue, false);
	}

	/**
	 * Removes all value facts with the specified source from this blackboard for the specified terminology object. If
	 * no such fact exists in the blackboard, this method has no effect.
	 *
	 * @param terminologyObject the terminology object to remove the value facts from
	 * @param source            the fact source to be removed
	 */
	@Override
	public void removeValueFact(TerminologyObject terminologyObject, Object source) {
		Value oldValue = getValue((ValueObject) terminologyObject);
		this.getValueStorage().remove(terminologyObject, source);
		propagate(terminologyObject, oldValue, false);
	}

	@NotNull
	@Override
	public Value getValue(ValueObject valueObject) {
		return getValueFromFact(valueObject, this.getValueStorage().getMergedFact(valueObject));
	}

	@NotNull
	@Override
	public Collection<TerminologyObject> getValuedObjects() {
		return Collections.unmodifiableCollection(
				this.getValueStorage().getValuedObjects());
	}

	@NotNull
	@Override
	public Collection<Question> getValuedQuestions() {
		Collection<Question> result = new LinkedList<>();
		for (TerminologyObject object : getValuedObjects()) {
			if (object instanceof Question) {
				result.add((Question) object);
			}
		}
		return result;
	}

	@NotNull
	@Override
	public Collection<Solution> getValuedSolutions() {
		Collection<Solution> result = new LinkedList<>();
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
		this.getInterviewStorage().add(fact);
		propagateIndicationChange(fact.getTerminologyObject(), oldValue);
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
		this.getInterviewStorage().remove(fact);
		propagateIndicationChange(factObject, oldValue);
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

		this.getInterviewStorage().remove(terminologyObject, source);

		propagateIndicationChange(terminologyObject, oldValue);
	}

	private void propagateIndicationChange(TerminologyObject interviewObject, Value oldValue) {
		session.getPropagationManager().propagate((InterviewObject) interviewObject, oldValue);
	}

	@Override
	public void removeInterviewFacts(TerminologyObject terminologyObject) {
		Fact oldFact = getInterviewFact(terminologyObject);
		if (oldFact != null) {
			Value oldValue = oldFact.getValue();
			this.getInterviewStorage().remove(terminologyObject);
			propagateIndicationChange(terminologyObject, oldValue);
		}
	}

	@Nullable
	@Override
	public Fact getInterviewFact(TerminologyObject terminologyObject) {
		return this.getInterviewStorage().getMergedFact(terminologyObject);
	}

	@Nullable
	@Override
	public Fact getInterviewFact(TerminologyObject terminologyObject, PSMethod psmethod) {
		return this.getInterviewStorage().getMergedFact(terminologyObject, psmethod);
	}

	@NotNull
	@Override
	public Collection<InterviewObject> getInterviewObjects() {
		Collection<TerminologyObject> objects = this.getInterviewStorage().getValuedObjects();
		ArrayList<InterviewObject> result = new ArrayList<>(objects.size());
		for (TerminologyObject object : objects) {
			result.add((InterviewObject) object);
		}
		return Collections.unmodifiableCollection(result);
	}

	@NotNull
	@Override
	public Rating getRating(Solution solution) {
		return (Rating) getValue(solution);
	}

	@NotNull
	@Override
	public Value getValue(ValueObject object, PSMethod psmethod) {
		return getValueFromFact(object, getValueStorage().getMergedFact(object, psmethod));
	}

	@NotNull
	@Override
	public Value getValue(ValueObject object, PSMethod psmethod, Object source) {
		return getValueFromFact(object, getValueStorage().getFact(object, psmethod, source));
	}

	@NotNull
	@Override
	public Indication getIndication(InterviewObject interviewElement) {
		Fact fact = getInterviewFact(interviewElement);
		return getValueFromFact(interviewElement, fact);
	}

	@NotNull
	private Indication getValueFromFact(InterviewObject interviewElement, Fact fact) {
		if (fact == null) {
			return interviewElement.getDefaultInterviewValue();
		}
		else {
			return (Indication) fact.getValue();
		}
	}

	@NotNull
	@Override
	public Indication getIndication(InterviewObject interviewElement, PSMethod psMethod) {
		Fact fact = getInterviewFact(interviewElement, psMethod);
		return getValueFromFact(interviewElement, fact);
	}

	@NotNull
	@Override
	public List<Question> getAnsweredQuestions() {
		List<Question> questions = new LinkedList<>();
		// for (Question q :
		// session.getKnowledgeBase().getManager().getQuestions()) {
		for (TerminologyObject object : getValuedObjects()) {
			if (object instanceof Question) {
				Fact mergedFact = getValueStorage().getMergedFact(object);
				if (mergedFact != null && UndefinedValue.isNotUndefinedValue(mergedFact.getValue())) {
					questions.add((Question) object);
				}
			}
		}
		return questions;
	}

	@NotNull
	@Override
	public List<Solution> getSolutions(Rating.State state) {
		List<Solution> result = new LinkedList<>();
		if (state == State.UNCLEAR) {
			// if the state is unclear
			// we can have to look at all objects
			for (Solution diag : getSession().getKnowledgeBase().getManager().getSolutions()) {
				if (getRating(diag).hasState(state)) {
					result.add(diag);
				}
			}
		}
		else {
			// if the state is not unclear
			// we can restrict looking at values objects (instead of all)
			for (Object object : getValuedObjects()) {
				if (object instanceof Solution) {
					Solution diag = (Solution) object;
					if (getRating(diag).hasState(state)) {
						result.add(diag);
					}
				}
			}
		}
		return result;
	}

	@NotNull
	@Override
	public Rating getRating(Solution solution, PSMethod psmethod) {
		Fact mergedFact = getValueStorage().getMergedFact(solution, psmethod);
		if (mergedFact == null) {
			return new Rating(State.UNCLEAR);
		}
		else {
			return (Rating) mergedFact.getValue();
		}
	}

	@NotNull
	@Override
	public Collection<PSMethod> getContributingPSMethods(TerminologyObject object) {
		return getValueStorage().getContributingPSMethods(object);
	}

	@NotNull
	@Override
	public Collection<PSMethod> getIndicatingPSMethods(TerminologyObject object) {
		return getInterviewStorage().getContributingPSMethods(object);
	}

	@Nullable
	@Override
	public Fact getValueFact(TerminologyObject valueObject) {
		return this.getValueStorage().getMergedFact(valueObject);
	}

	@Nullable
	@Override
	public Fact getValueFact(TerminologyObject terminologyObject, PSMethod psmethod) {
		return this.getValueStorage().getMergedFact(terminologyObject, psmethod);
	}

	@Override
	public boolean hasValueFact(TerminologyObject terminologyObject, PSMethod psmethod) {
		return this.getValueStorage().hasFact(terminologyObject, psmethod);
	}

	@Override
	public boolean hasValueFact(TerminologyObject terminologyObject) {
		return this.getValueStorage().hasFact(terminologyObject);
	}

	@NotNull
	private Value getValueFromFact(ValueObject object, Fact fact) {
		if (fact == null) {
			return object.getDefaultValue();
		}
		return fact.getValue();
	}

	protected FactStorage getValueStorage() {
		return valueStorage;
	}

	protected void setValueStorage(FactStorage valueStorage) {
		this.valueStorage = valueStorage;
	}

	protected FactStorage getInterviewStorage() {
		return interviewStorage;
	}

	protected void setInterviewStorage(FactStorage interviewStorage) {
		this.interviewStorage = interviewStorage;
	}

	@NotNull
	@Override
	public Collection<Fact> getInterviewFacts(TerminologyObject terminologyObject) {
		return this.getInterviewStorage().getAllFacts(terminologyObject);
	}
}
