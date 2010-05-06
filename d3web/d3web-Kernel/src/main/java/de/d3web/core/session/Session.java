/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.core.session;

import java.util.List;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PropagationContoller;
import de.d3web.core.inference.Rule;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.DiagnosisState;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.DCMarkedUp;
import de.d3web.core.knowledge.terminology.info.PropertiesContainer;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.core.session.interviewmanager.DialogController;
import de.d3web.core.session.interviewmanager.QASetManager;
import de.d3web.core.session.values.UndefinedValue;

/**
 * The Session interface represents an active problem-solving session. Here,
 * values of answered questions are submitted and derived states of solutions
 * are retrieved. <br>
 * Note: This interface replaces the formerly used XPSCase.
 * 
 * @author Norman Bruemmer, joba, Volker Belli (denkbares GmbH)
 */
public interface Session extends DCMarkedUp, PropertiesContainer {

	// --- manage problem solvers ---
	/**
	 * Returns a list of all registered {@link PSMethod} instances used in this
	 * case. The list is sorted by the priority.
	 * 
	 * @return a list of {@link PSMethod} instances registered for this case
	 */
	List<? extends PSMethod> getPSMethods();

	PSMethod getPSMethodInstance(Class<? extends PSMethod> solverClass);

	// --- access information ---
	Interview getInterviewManager();

	/**
	 * The {@link Blackboard} manages all entered and all derived facts of this
	 * {@link Session}.
	 * 
	 * @return the blackboard instance used in this session
	 */
	Blackboard getBlackboard();

	/**
	 * Returns the {@link PropagationContoller} instance, responsible for all
	 * propagation actions of this session.
	 * 
	 * @return the propagation controller of this session
	 */
	PropagationContoller getPropagationContoller();

	// --- access header information ---
	/**
	 * Returns the {@link KnowledgeBase} instance that is used in this session
	 * for the problem-solving task.
	 * 
	 * @return the knowledge base used in this session
	 */
	KnowledgeBase getKnowledgeBase();

	// InfoStore getInfoStore(); // some information will be created/updated
	// automatically (id, change-date, create-date), increment2

	// --- reserved for later implementation --- (inkrement 2)
	// Protocol getProtocol();

	// -----------------------from here on old stuff, TODO: remove?

	/**
	 * Returns all {@link Question} instances, that have already been answered
	 * in this session.
	 * 
	 * @return all currently answered questions of this session
	 */
	List<? extends Question> getAnsweredQuestions();

	/**
	 * Returns the {@link SessionObject} (dynamically created flyweight object)
	 * corresponding to the specified {@link CaseObjectSource} instance (often
	 * this is a {@link Question} or a {@link Solution}.
	 * 
	 * @param item
	 *            the specified object for which the corresponding session
	 *            object should be returned
	 * @return the corresponding {@link SessionObject} of the specified object
	 */
	SessionObject getCaseObject(CaseObjectSource item);

	/**
	 * Returns all {@link Solution} instances, that hold the specified
	 * {@link DiagnosisState} for at least one {@link PSMethod} specified. in
	 * this {@link Session}. Only the specified {@link PSMethod} instances are
	 * considered, so a {@link Solution} is returned, if it haves the specified
	 * {@link DiagnosisState} for at least one of these {@link PSMethod}.
	 * 
	 * @param state
	 *            the DiagnosisState the diagnoses must have to be returned
	 * @return a list of diagnoses in this case that have the state 'state'
	 */
	List<Solution> getSolutions(DiagnosisState state);

	/**
	 * Returns the {@link QASetManager} used in this case, that is responsible
	 * for the dialog management.
	 * 
	 * @return the {@link QASetManager} defined for this case, for example a
	 *         {@link DialogController}
	 */
	QASetManager getQASetManager();

	/**
	 * Removes all finish reasons from the set of reasons for quitting the
	 * session and thus enables the continuation of the session.
	 */
	void continueCase();

	/**
	 * Sets a {@link QASetManager}, that will be used to control the interview
	 * behavior of this case.
	 * 
	 * @param cd
	 *            the {@link QASetManager} of this case
	 */
	void setQASetManager(QASetManager cd);

	/**
	 * Assigns the specified value to the specified {@link ValuedObject}, e.g.,
	 * a {@link Question} or a {@link Solution} receives a new value.
	 * 
	 * @param valuedObject
	 *            the object, that receives a new value
	 * @param value
	 *            the (array of new) values for the specified
	 *            {@link ValuedObject}
	 */
	void setValue(ValuedObject valuedObject, Value value);

	/**
	 * Returns the value of the specified {@link Question} valid in this
	 * {@link Session}; returns {@link UndefinedValue} if no value is assigned.
	 * 
	 * @param question
	 *            the specified {@link Question}
	 * @return the value of the question valid in this session;
	 *         {@link UndefinedValue} if no value is assigned.
	 * @author joba
	 * @date 15.04.2010
	 */
	public Value getValue(Question question);

	/**
	 * Assigns the specified value to the specified {@link ValuedObject}, e.g.,
	 * a {@link Question} or a {@link Solution} receives a new value. The
	 * knowledge source of this assignment is also given, here it is a
	 * {@link Rule}.
	 * 
	 * @param valuedObject
	 *            ValuedObject the object, that receives a new value
	 * @param value
	 *            value the (array of new) values for the specified
	 *            {@link ValuedObject}
	 * @param rule
	 *            the knowledge element responsible for making the assignment
	 */
	void setValue(ValuedObject valuedObject, Value value, Rule rule);

	/**
	 * Assigns the specified value to the specified {@link ValuedObject}, e.g.,
	 * a {@link Question} or a {@link Solution} receives a new value. The
	 * {@link PSMethod} responsible of this assignment is also given.
	 * 
	 * @param valuedObject
	 *            ValuedObject the object, that receives a new value
	 * @param value
	 *            value the (array of new) values for the specified
	 *            {@link ValuedObject}
	 * @param context
	 *            the problem-solver responsible for this assignment
	 */
	void setValue(ValuedObject valuedObject, Value value, Class<? extends PSMethod> context);

	/**
	 * Registers a new listener to this session. If something in this session
	 * changes, then all registered listeners will be notified.
	 * 
	 * @param listener
	 *            one new listener of this session to register
	 */
	void addListener(SessionEventListener listener);

	/**
	 * Removes the specified listener from the list of registered listeners. All
	 * listeners will be notified, if something in the session changes.
	 * 
	 * @param listener
	 *            the specified listener to be removed
	 */
	void removeListener(SessionEventListener listener);

	/**
	 * Adds an established {@link Solution} to this {@link Session}.
	 * 
	 * @param solution
	 *            the specified solution
	 * @author joba
	 * @date 15.04.2010
	 */
	public void addEstablishedSolution(Solution solution);

	/**
	 * Removes the specified solution from the list of established solutions.
	 * 
	 * @param solution
	 *            the specified solution
	 * @author joba
	 * @date 15.04.2010
	 */
	public void removeEstablishedSolution(Solution solution);
}