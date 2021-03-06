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

import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PropagationManager;
import de.d3web.core.inference.SessionTerminatedException;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.UndefinedValue;

/**
 * The Blackboard manages all dynamic values created within the case and propagated throughout the inference system.
 *
 * @author volker_belli
 */
public interface Blackboard {

	/**
	 * Returns the session this blackboard has been created for.
	 *
	 * @return the session of this blackboard
	 */
	@NotNull
	Session getSession();

	/**
	 * Adds a new value fact to this blackboard. If an other fact for the same terminology object and with the same
	 * source has already been added, that fact will be replaced by the specified one.
	 *
	 * @param fact the fact to be added
	 * @throws SessionTerminatedException if the session has been terminated manually and any further propagation is
	 *                                    prevented. The exception is only thrown if this method is not called inside a
	 *                                    opened propagation frame. In this case the exception is thrown when the
	 *                                    propagation will be committed using {@link PropagationManager#commitPropagation()}
	 */
	void addValueFact(Fact fact) throws SessionTerminatedException;

	/**
	 * Removes a value fact from this blackboard. If the fact does not exists in the blackboard, this method has no
	 * effect.
	 *
	 * @param fact the fact to be removed
	 * @throws SessionTerminatedException if the session has been terminated manually and any further propagation is
	 *                                    prevented. The exception is only thrown if this method is not called inside a
	 *                                    opened propagation frame. In this case the exception is thrown when the
	 *                                    propagation will be committed using {@link PropagationManager#commitPropagation()}
	 */
	void removeValueFact(Fact fact) throws SessionTerminatedException;

	/**
	 * Removes all value facts with the specified source from this blackboard for the specified terminology object. If
	 * no such fact exists in the blackboard, this method has no effect.
	 *
	 * @param terminologyObject the terminology object to remove the value facts from
	 * @param source            the fact source to be removed
	 * @throws SessionTerminatedException if the session has been terminated manually and any further propagation is
	 *                                    prevented. The exception is only thrown if this method is not called inside a
	 *                                    opened propagation frame. In this case the exception is thrown when the
	 *                                    propagation will be committed using {@link PropagationManager#commitPropagation()}
	 */
	void removeValueFact(TerminologyObject terminologyObject, Object source) throws SessionTerminatedException;

	/**
	 * Returns the value of an {@link ValueObject}. The method never returns null. If there is no value set for the
	 * specified ValueObject, its default value is returned (which is e.g. {@link UndefinedValue} for Questions and a
	 * {@link Rating} with {@link State#UNCLEAR} for Solutions.
	 *
	 * @param valueObject the object to get the value for
	 * @return Value the current value for this object
	 * @created 30.09.2010
	 */
	@NotNull
	Value getValue(ValueObject valueObject);

	/**
	 * Returns the merged fact for all value facts of the specified terminology object. This method returns null, if no
	 * fact is available for the specified object.
	 *
	 * @param terminologyObject the terminology object to access the merged fact for
	 * @return the merged fact
	 */
	@Nullable
	Fact getValueFact(TerminologyObject terminologyObject);

	/**
	 * Returns a collection of all terminology objects that have a value. This means this method delivers all
	 * terminology objects that currently have at least one value fact added for it to this blackboard. The collection
	 * may be unmodifiable.
	 *
	 * @return the collection of valued terminology objects
	 */
	@NotNull
	Collection<TerminologyObject> getValuedObjects();

	/**
	 * Returns a collection of all questions that have a value. This means this method delivers all questions that
	 * currently have at lead one value fact added for it to this blackboard. The collection may be unmodifiable.
	 *
	 * @return the collection of valued questions
	 */
	@NotNull
	Collection<Question> getValuedQuestions();

	/**
	 * Returns a collection of all diagnoses that have a value. This means this method delivers all diagnoses that
	 * currently have at lead one value fact added for it to this blackboard. The collection may be unmodifiable.
	 *
	 * @return the collection of valued diagnoses
	 */
	@NotNull
	Collection<Solution> getValuedSolutions();

	/**
	 * Adds a new interview fact to this blackboard. If an other interview fact for the same terminology object and with
	 * the same source has already been added, that fact will be replaced by the specified one.
	 *
	 * @param fact the fact to be added
	 * @throws SessionTerminatedException if the session has been terminated manually and any further propagation is
	 *                                    prevented. The exception is only thrown if this method is not called inside a
	 *                                    opened propagation frame. In this case the exception is thrown when the
	 *                                    propagation will be committed using {@link PropagationManager#commitPropagation()}
	 */
	void addInterviewFact(Fact fact) throws SessionTerminatedException;

	/**
	 * Removes a interview fact from this blackboard. If the interview fact does not exists in the blackboard, this
	 * method has no effect.
	 *
	 * @param fact the fact to be removed
	 * @throws SessionTerminatedException if the session has been terminated manually and any further propagation is
	 *                                    prevented. The exception is only thrown if this method is not called inside a
	 *                                    opened propagation frame. In this case the exception is thrown when the
	 *                                    propagation will be committed using {@link PropagationManager#commitPropagation()}
	 */
	void removeInterviewFact(Fact fact) throws SessionTerminatedException;

	/**
	 * Removes all interview facts with the specified source from this blackboard for the specified terminology object.
	 * If no such fact exists in the blackboard, this method has no effect.
	 *
	 * @param terminologyObject the terminology object to remove the interview facts from
	 * @param source            the fact source to be removed
	 * @throws SessionTerminatedException if the session has been terminated manually and any further propagation is
	 *                                    prevented. The exception is only thrown if this method is not called inside a
	 *                                    opened propagation frame. In this case the exception is thrown when the
	 *                                    propagation will be committed using {@link PropagationManager#commitPropagation()}
	 */
	void removeInterviewFact(TerminologyObject terminologyObject, Object source) throws SessionTerminatedException;

	/**
	 * Returns all interview facts from this blackboard for the specified terminology object. If no such fact exists in
	 * the blackboard, an empty collection is returned
	 *
	 * @param terminologyObject the terminology object to access the interview facts from
	 * @return collection of interview facts
	 * @created 14.05.2013
	 */
	@NotNull
	Collection<Fact> getInterviewFacts(TerminologyObject terminologyObject);

	/**
	 * Removes all interview facts from this blackboard for the specified terminology object. If no such fact exists in
	 * the blackboard, this method has no effect.
	 *
	 * @param terminologyObject the terminology object to remove the interview facts from
	 * @throws SessionTerminatedException if the session has been terminated manually and any further propagation is
	 *                                    prevented. The exception is only thrown if this method is not called inside a
	 *                                    opened propagation frame. In this case the exception is thrown when the
	 *                                    propagation will be committed using {@link PropagationManager#commitPropagation()}
	 */
	void removeInterviewFacts(TerminologyObject terminologyObject) throws SessionTerminatedException;

	/**
	 * Returns the merged fact for all interview facts of the specified terminology object. This method returns null, if
	 * no fact is available for the specified object.
	 *
	 * @param terminologyObject the terminology object to access the merged fact for
	 * @return the merged fact
	 */
	@Nullable
	Fact getInterviewFact(TerminologyObject terminologyObject);

	/**
	 * Returns a collection of all terminology objects that have been rated for the usage in the interview. This means
	 * the method delivers all terminology objects that currently have at least one interview fact added for it to this
	 * blackboard.
	 *
	 * @return the collection of interview rated terminology objects
	 */
	@NotNull
	Collection<InterviewObject> getInterviewObjects();

	/**
	 * Returns the current rating of the diagnosis. The returned rating is the merged rating over all problem solvers
	 * available. This is a typed shortcut for accessing the value {@link Fact} of the {@link Solution} and read out its
	 * current value. The method never returns null, it returns an {@link State#UNCLEAR} {@link Rating} if the solution
	 * is not rated yet.
	 *
	 * @param solution the solution to take the rating from
	 * @return the total rating of the solution
	 */
	@NotNull
	Rating getRating(Solution solution);

	/**
	 * Returns the Value of a {@link ValueObject}, calculated by the specified psmethod. The method never returns null.
	 * If there is no value set for the specified ValueObject, its default value is returned (which is e.g. {@link
	 * UndefinedValue} for Questions and a {@link Rating} with {@link State#UNCLEAR} for Solutions.
	 *
	 * @param object   the object to get the value for
	 * @param psmethod PSMethod
	 * @return Value the current value for this object and psmethod
	 * @created 30.09.2010
	 */
	@NotNull
	Value getValue(ValueObject object, PSMethod psmethod);

	/**
	 * Returns the Value of a {@link ValueObject}, calculated by the specified source object. The method never returns
	 * null. If there is no value set for the specified ValueObject, its default value is returned (which is e.g. {@link
	 * UndefinedValue} for Questions and a {@link Rating} with {@link State#UNCLEAR} for Solutions.
	 *
	 * @param object   the object to get the value for
	 * @param psmethod the PSMethod derived the value
	 * @param source   the source object that derived that value
	 * @return Value the current value for this object, psmethod and source
	 * @created 30.09.2010
	 */
	@NotNull
	Value getValue(ValueObject object, PSMethod psmethod, Object source);

	/**
	 * Returns the Value of a Solution, calculated by the specified psmethod. The method never returns null, it returns
	 * an {@link State#UNCLEAR} {@link Rating} if the solution is not rated yet by the specified {@link PSMethod}.
	 *
	 * @param solution Solution
	 * @param psmethod PSMethod
	 * @return Rating
	 */
	@NotNull
	Rating getRating(Solution solution, PSMethod psmethod);

	/**
	 * Returns the current indication state of the interview element. The returned indication state is the merged
	 * indication over all strategic solvers available. This is a typed shortcut for accessing the interview {@link
	 * Fact} of the {@link QASet} and read out its current value. The method never returns null, it returns an {@link
	 * Indication.State#NEUTRAL} indication if the object is not indicated yet.
	 *
	 * @param interviewElement the question to take the rating from
	 * @return the indication of the interview element
	 */
	@NotNull
	Indication getIndication(InterviewObject interviewElement);

	/**
	 * Return a list of all answered questions.
	 *
	 * @return List of answered questions
	 * @created 11.05.2010
	 */
	@NotNull
	List<Question> getAnsweredQuestions();

	/**
	 * Returns all {@link Solution} instances, that hold the specified {@link Rating}.
	 *
	 * @param state the Rating the diagnoses must have to be returned
	 * @return a list of diagnoses in this case that have the state 'state'
	 */
	@NotNull
	List<Solution> getSolutions(Rating.State state);

	/**
	 * Returns the Value Fact of a particular {@link PSMethod} for the specified {@link TerminologyObject}. The method
	 * returns null of no such fact has been added.
	 *
	 * @param terminologyObject {@link TerminologyObject}
	 * @param psmethod          {@link PSMethod}
	 * @return {@link Fact}
	 * @created 21.09.2010
	 */
	@Nullable
	Fact getValueFact(TerminologyObject terminologyObject, PSMethod psmethod);

	/**
	 * Returns all value facts from this blackboard for the specified terminology object. If no such fact exists in the
	 * blackboard, an empty collection is returned
	 *
	 * @param terminologyObject the terminology object to access the value facts from
	 * @return collection of value facts
	 */
	@NotNull
	Collection<Fact> getValueFacts(TerminologyObject terminologyObject);

	/**
	 * Returns whether the {@link TerminologyObject} has a Value Fact for the given {@link PSMethod}.
	 *
	 * @param terminologyObject {@link TerminologyObject}
	 * @param psmethod          {@link PSMethod}
	 * @return {@link Fact}
	 * @created 21.09.2012
	 */
	boolean hasValueFact(TerminologyObject terminologyObject, PSMethod psmethod);

	/**
	 * Returns whether the {@link TerminologyObject} has a Value Fact.
	 *
	 * @param terminologyObject {@link TerminologyObject}
	 * @return {@link Fact}
	 * @created 21.09.2012
	 */
	boolean hasValueFact(TerminologyObject terminologyObject);

	/**
	 * Returns the Interview Fact of a particular {@link PSMethod} for the specified {@link TerminologyObject}. The
	 * method returns null of no such fact has been added.
	 *
	 * @param terminologyObject {@link TerminologyObject}
	 * @param psmethod          {@link PSMethod}
	 * @return {@link Fact}
	 * @created 21.09.2010
	 */
	@Nullable
	Fact getInterviewFact(TerminologyObject terminologyObject, PSMethod psmethod);

	/**
	 * Returns the {@link Indication} of one {@link PSMethod} of a {@link TerminologyObject}.The method never returns
	 * null, it returns an {@link Indication.State#NEUTRAL} indication if the object is not indicated yet by the
	 * specified strategic solver.
	 *
	 * @param interviewElement {@link InterviewObject}
	 * @param psMethod         the strategic solver that derived the requested indication
	 * @return the indication of the specified strategic solver
	 * @created 21.09.2010
	 */
	@NotNull
	Indication getIndication(InterviewObject interviewElement, PSMethod psMethod);

	/**
	 * Returns a collection of all problem and strategic solvers, which added at least one value fact for the {@link
	 * TerminologyObject}.
	 *
	 * @param object {@link TerminologyObject}
	 * @return {@link Collection} of {@link PSMethod}
	 * @created 24.09.2010
	 */
	@NotNull
	Collection<PSMethod> getContributingPSMethods(TerminologyObject object);

	/**
	 * Returns a collection of all problem and strategic solvers, which added at least one interview fact for the {@link
	 * TerminologyObject}.
	 *
	 * @param object {@link TerminologyObject}
	 * @return {@link Collection} of {@link PSMethod}
	 * @created 24.09.2010
	 */
	@NotNull
	Collection<PSMethod> getIndicatingPSMethods(TerminologyObject object);

	/**
	 * Adds a new {@link BlackboardListener} to this blackboard.
	 *
	 * @param listener the listener to be added
	 * @created 16.09.2011
	 */
	void addBlackboardListner(BlackboardListener listener);

	/**
	 * Removes an existing {@link BlackboardListener} from this blackboard.
	 *
	 * @param listener the listener to be removed
	 * @created 16.09.2011
	 */
	void removeBlackboardListner(BlackboardListener listener);

	void setSourceRecording(boolean autosaveSource);

	boolean isSourceRecording();
}
