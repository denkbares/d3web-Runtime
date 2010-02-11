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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import de.d3web.core.KnowledgeBase;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PropagationContoller;
import de.d3web.core.inference.Rule;
import de.d3web.core.session.blackboard.XPSCaseObject;
import de.d3web.core.session.interviewmanager.DialogController;
import de.d3web.core.session.interviewmanager.QASetManager;
import de.d3web.core.terminology.Answer;
import de.d3web.core.terminology.Diagnosis;
import de.d3web.core.terminology.DiagnosisState;
import de.d3web.core.terminology.QContainer;
import de.d3web.core.terminology.Question;
import de.d3web.core.terminology.info.DCMarkedUp;
import de.d3web.core.terminology.info.PropertiesContainer;

/**
 * Every problem-solving session is represented by a an XPSCase. It manages the
 * used problem-solving methods {@link PSMethod}, the entered findings (
 * {@link Question} and {@link Answer}), derived solutions ({@link Diagnosis}).
 * The XPSCase also holds the used {@link KnowledgeBase}. <BR>
 * Most importantly, all entered answers have to set through the setValue
 * methods of XPSCase, since XPSCase acts as a mediator during problem-solving
 * between the user inputs and the problem-solvers.
 * 
 * Creation date: (30.11.2000 14:17:40)
 * 
 * @author Norman Bruemmer, joba
 */
public interface XPSCase extends DCMarkedUp, PropertiesContainer {

	/**
	 * Returns all registered {@link PSMethod} instances that also have an
	 * impact on the interview behavior.
	 * 
	 * @return all {@link PSMethod} instances influencing the interview behavior
	 */
	Collection<? extends PSMethod> getDialogControllingPSMethods();

	List<QContainer> getIndicatedQContainers();

	/**
	 * Returns all {@link Question} instances, that have been already answered
	 * in the case.
	 * 
	 * @return all currently answered questions in this case
	 */
	List<? extends Question> getAnsweredQuestions();

	/**
	 * Returns the {@link XPSCaseObject} (dynamic flyweight object)
	 * corresponding to the specified {@link CaseObjectSource} instance (often
	 * this is a {@link Question} or a {@link Diagnosis}.
	 * 
	 * @param item
	 *            Object whose case object should be returned
	 * @return the corresponding {@link XPSCaseObject} of the given item
	 */
	XPSCaseObject getCaseObject(CaseObjectSource item);

	/**
	 * Returns all {@link Diagnosis} instances contained in the knowledge base
	 * as a sequential list.
	 * 
	 * @return a list of all {@link Diagnosis} instances
	 * @deprecated use getKnowledgeBase().getDiagnoses instead
	 */
	// TODO: remove this method
	List<Diagnosis> getDiagnoses();

	/**
	 * Returns all {@link Diagnosis} instances, that hold the specified
	 * {@link DiagnosisState} in this {@link XPSCase}. All registered
	 * {@link PSMethod} instances are considered, so a {@link Diagnosis} is
	 * returned, if it haves the specified {@link DiagnosisState} for at least
	 * one {@link PSMethod}.
	 * 
	 * @param the
	 *            specified {@link DiagnosisState} that the diagnoses should
	 *            have
	 * @return a list of {@link Diagnosis} instances having the specified state
	 */
	List<Diagnosis> getDiagnoses(DiagnosisState state);

	/**
	 * Returns all {@link Diagnosis} instances, that hold the specified
	 * {@link DiagnosisState} for at least one {@link PSMethod} specified. in
	 * this {@link XPSCase}. Only the specified {@link PSMethod} instances are
	 * considered, so a {@link Diagnosis} is returned, if it haves the specified
	 * {@link DiagnosisState} for at least one of these {@link PSMethod}.
	 * 
	 * @param state
	 *            the DiagnosisState the diagnoses must have to be returned
	 * @param psMethods
	 *            Only these diagnoses are considered, whose states have been
	 *            set by one of the given PSMethods
	 * @return a list of diagnoses in this case that have the state 'state'
	 */
	List<Diagnosis> getDiagnoses(DiagnosisState state, List<? extends PSMethod> psMethods);

	/**
	 * Returns the {@link KnowledgeBase} instance this object belongs to.
	 * 
	 * @return the knowledge base used to solve this case
	 */
	KnowledgeBase getKnowledgeBase();

	PSMethod getPSMethodInstance(Class<? extends PSMethod> context);

	/**
	 * Returns the {@link QASetManager} used in this case.
	 * 
	 * @return the {@link QASetManager} defined for this case, for example a
	 *         {@link DialogController}
	 */
	QASetManager getQASetManager();

	/**
	 * Returns a list of all registered {@link PSMethod} instances used in this
	 * case.
	 * 
	 * @return a list of {@link PSMethod} instances registered for this case
	 */
	List<? extends PSMethod> getUsedPSMethods();

	/**
	 * Returns the {@link PropagationContoller} instance, responsible for do all
	 * propagation of this case.
	 * 
	 * @return the PropagationManager of this case
	 */
	PropagationContoller getPropagationContoller();

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
	 * @see XPSCase#setFinished(boolean f)
	 * @param reasonForFinishCase
	 *            the new reason to quit the case
	 */
	void finish(Class<? extends KnowledgeSlice> reasonForFinishCase);

	/**
	 * Returns all registered reasons for setting the case into the 'finished'
	 * state.
	 * 
	 * @return all reasons for finishing the case
	 */
	Set<Class<? extends KnowledgeSlice>> getFinishReasons();

	/**
	 * Removes a specified reason from the set of reasons for quitting the case.
	 * 
	 * @param reasonForContinueCase
	 *            the quit-reason to be removed from the reasons list.
	 */
	void continueCase(Class<? extends KnowledgeSlice> reasonForContinueCase);

	/**
	 * Sets a {@link QASetManager}, that will be used to control the interview
	 * behavior of this case.
	 * 
	 * @param cd
	 *            the {@link QASetManager} of this case
	 */
	void setQASetManager(QASetManager cd);

	/**
	 * Sets the list of registered {@link PSMethod} instances, that will be used
	 * during the problem-solving session.
	 * 
	 * @param methods
	 *            the list of registered problem-solvers
	 */
	void setUsedPSMethods(List<? extends PSMethod> methods);

	/**
	 * Assigns the specified value(s) to the specified {@link ValuedObject},
	 * e.g., a {@link Question} or a {@link Diagnosis} receives a new value.
	 * 
	 * @param ValuedObject
	 *            the object, that receives a new value
	 * @param value
	 *            the (array of new) values for the specified
	 *            {@link ValuedObject}
	 */
	void setValue(ValuedObject o, Object[] value);

	/**
	 * Assigns the specified value(s) to the specified {@link ValuedObject},
	 * e.g., a {@link Question} or a {@link Diagnosis} receives a new value. The
	 * knowledge source of this assignment is also given, here it is a
	 * {@link Rule}.
	 * 
	 * @param ValuedObject
	 *            ValuedObject the object, that receives a new value
	 * @param value
	 *            value the (array of new) values for the specified
	 *            {@link ValuedObject}
	 * @param context
	 *            the knowledge element responsible for making the assignment
	 */
	void setValue(ValuedObject o, Object[] value, Rule rule);

	/**
	 * Assigns the specified value(s) to the specified {@link ValuedObject},
	 * e.g., a {@link Question} or a {@link Diagnosis} receives a new value. The
	 * {@link PSMethod} responsible of this assignment is also given
	 * 
	 * @param ValuedObject
	 *            ValuedObject the object, that receives a new value
	 * @param value
	 *            value the (array of new) values for the specified
	 *            {@link ValuedObject}
	 * @param context
	 *            the problem-solver responsible for this assignment
	 */
	void setValue(ValuedObject o, Object[] value, Class<? extends PSMethod> context);

	/**
	 * Takes a specified log message, that is relevant for this case.
	 * 
	 * @param message
	 *            the specified log message
	 */
	void trace(String message);

	/**
	 * Registers a new listener to this case. If something in this case changes,
	 * the all registered listeners will be notified.
	 * 
	 * @param listener
	 *            one new listener of this case to register
	 */
	void addListener(XPSCaseEventListener listener);

	/**
	 * Removes the specified listener from the list of registered listeners. All
	 * listeners will be notified, if something in the case changes.
	 * 
	 * @param listener
	 *            the specified listener to be removed
	 */
	void removeListener(XPSCaseEventListener listener);

}