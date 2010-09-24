/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.core.session;

import java.util.Date;
import java.util.List;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PropagationManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.DCMarkedUp;
import de.d3web.core.knowledge.terminology.info.PropertiesContainer;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.core.session.interviewmanager.Interview;
import de.d3web.core.session.protocol.Protocol;

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
	/**
	 * The interview controls the dialog behavior of the session, i.e. the
	 * indication of {@link Question} and {@link QContainer} instances.
	 * 
	 * @return Interview
	 */
	Interview getInterview();

	/**
	 * The {@link Blackboard} manages all entered and all derived facts of this
	 * {@link Session}.
	 * 
	 * @return the blackboard instance used in this session
	 */
	Blackboard getBlackboard();

	/**
	 * Returns the {@link PropagationManager} instance, responsible for all
	 * propagation actions of this session.
	 * 
	 * @return the propagation manager of this session
	 */
	PropagationManager getPropagationManager();

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

	// --- full implementation is reserved for later implementation ---
	// (inkrement 2)
	Protocol getProtocol();

	/**
	 * Registers a new listener to this session. If something in this session
	 * changes, then all registered listeners will be notified.
	 * 
	 * @param listener one new listener of this session to register
	 */
	void addListener(SessionEventListener listener);

	/**
	 * Removes the specified listener from the list of registered listeners. All
	 * listeners will be notified, if something in the session changes.
	 * 
	 * @param listener the specified listener to be removed
	 */
	void removeListener(SessionEventListener listener);

	// -----------------------from here on old stuff, TODO: remove?

	/**
	 * Returns the {@link SessionObject} (dynamically created flyweight object)
	 * corresponding to the specified {@link CaseObjectSource} instance (often
	 * this is a {@link Question} or a {@link Solution}.
	 * 
	 * @param item the specified object for which the corresponding session
	 *        object should be returned
	 * @return the corresponding {@link SessionObject} of the specified object
	 */
	SessionObject getCaseObject(CaseObjectSource item);

	/**
	 * This method should be called, when the session is edited manually
	 * 
	 * @created 24.09.2010
	 */
	void edited();

	/**
	 * This method should be called, when the session was edited at the given
	 * date
	 * 
	 * @created 24.09.2010
	 * @param date the Date when the Session was edited
	 */
	void edited(Date date);

	/**
	 * Returns the creation date of the Session
	 * 
	 * @created 24.09.2010
	 * @return the creation date
	 */
	Date getCreationDate();

	/**
	 * Returns the Date of the last change on this Session
	 * 
	 * @created 24.09.2010
	 * @return the date of the last edit
	 */
	Date getLastChangeDate();

}