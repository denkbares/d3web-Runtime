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

import de.d3web.core.inference.KnowledgeSlice;
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

/**
 * The Session interface represents a running xps session.
 * This interface replaces the formerly used Session.
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
	 * The blackboard manages all entered and derived facts of the session.
	 * @return
	 */
	Blackboard getBlackboard();
	
	/**
	 * Returns the {@link PropagationContoller} instance, responsible for do all
	 * propagation of this case.
	 * 
	 * @return the PropagationManager of this case
	 */
	PropagationContoller getPropagationContoller();
	
	// --- access header information ---
	/**
	 * Returns the {@link KnowledgeBase} instance this object belongs to.
	 * 
	 * @return the knowledge base used to solve this case
	 */
	KnowledgeBase getKnowledgeBase();
	//InfoStore getInfoStore(); // some information will be created/updated automatically (id, change-date, create-date), increment2
	
	// --- reserved for later implementation --- (inkrement 2)
	//Protocol getProtocol();

	//-----------------------from here on old stuff, TODO: remove?
	
	/**
	 * Returns all {@link Question} instances, that have been already answered
	 * in the case.
	 * 
	 * @return all currently answered questions in this case
	 */
	List<? extends Question> getAnsweredQuestions();

	/**
	 * Returns the {@link SessionObject} (dynamic flyweight object)
	 * corresponding to the specified {@link CaseObjectSource} instance (often
	 * this is a {@link Question} or a {@link Solution}.
	 * 
	 * @param item
	 *            Object whose case object should be returned
	 * @return the corresponding {@link SessionObject} of the given item
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
	 * @param psMethods
	 *            Only these diagnoses are considered, whose states have been
	 *            set by one of the given PSMethods
	 * @return a list of diagnoses in this case that have the state 'state'
	 */
	List<Solution> getDiagnoses(DiagnosisState state, List<? extends PSMethod> psMethods);

	/**
	 * Returns the {@link QASetManager} used in this case.
	 * 
	 * @return the {@link QASetManager} defined for this case, for example a
	 *         {@link DialogController}
	 */
	QASetManager getQASetManager();

	/**
	 * Tests, if the case is finished with respect to the problem-solving
	 * behavior, e.g., no more question are required to asked.
	 * 
	 * @return true, if there exists at least one reason to quit the case
	 */
	boolean isFinished();

	/**
	 * Adds a new reason for quitting this case.
	 * 
	 * @see Session#setFinished(boolean f)
	 * @param reasonForFinishCase
	 *            the new reason to quit the case
	 */
	void finish(Class<? extends KnowledgeSlice> reasonForFinishCase);

	/**
	 * Removes all finish reasons from the set of reasons for quitting the case.
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
	 * Assigns the specified value to the specified {@link ValuedObject},
	 * e.g., a {@link Question} or a {@link Solution} receives a new value.
	 * 
	 * @param valuedObject
	 *            the object, that receives a new value
	 * @param value
	 *            the (array of new) values for the specified
	 *            {@link ValuedObject}
	 */
	void setValue(ValuedObject valuedObject, Value value);
	

	/**
	 * Assigns the specified value to the specified {@link ValuedObject},
	 * e.g., a {@link Question} or a {@link Solution} receives a new value. The
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
	 * Assigns the specified value to the specified {@link ValuedObject},
	 * e.g., a {@link Question} or a {@link Solution} receives a new value. The
	 * {@link PSMethod} responsible of this assignment is also given
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
	 * Registers a new listener to this case. If something in this case changes,
	 * the all registered listeners will be notified.
	 * 
	 * @param listener
	 *            one new listener of this case to register
	 */
	void addListener(SessionEventListener listener);

	/**
	 * Removes the specified listener from the list of registered listeners. All
	 * listeners will be notified, if something in the case changes.
	 * 
	 * @param listener
	 *            the specified listener to be removed
	 */
	void removeListener(SessionEventListener listener);
	
	/**
	 * adds an established Diagnosis to this case
	 */
	public void addEstablishedDiagnoses(Solution diag);
	
	/**
	 * removes a Diagnosis from the list of established diagnosis
	 * 
	 * @param diag
	 *            Diagnosis to remove
	 */
	public void removeEstablishedDiagnoses(Solution diagnosis);
}