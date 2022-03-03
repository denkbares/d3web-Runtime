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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.d3web.abstraction.inference.PSMethodAbstraction;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PSMethodInit;
import de.d3web.core.inference.PropagationListener;
import de.d3web.core.inference.SessionTerminatedException;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.indication.inference.PSMethodStrategic;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.scoring.inference.PSMethodHeuristic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for Session objects.
 * 
 * @author joba, Norman Brümmer, Georg
 */
public final class SessionFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionFactory.class);

	private static final List<PSMethod> commonPSMethods = Arrays.asList(
			PSMethodUserSelected.getInstance(),
			PSMethodStrategic.getInstance(),
			PSMethodAbstraction.getInstance(),
			PSMethodHeuristic.getInstance(),
			PSMethodInit.getInstance()
			);

	private static final Collection<PropagationListener> propagationListeners = new LinkedList<>();

	private SessionFactory() { // enforce noninstantiability
	}

	/**
	 * Returns a list of all default {@link PSMethod}s that will be used at
	 * least when creating a knowledge base.
	 * 
	 * @created 19.10.2010
	 * @return the default solvers
	 */
	public static List<PSMethod> getDefaultPSMethods() {
		return Collections.unmodifiableList(commonPSMethods);
	}

	/**
	 * Factory-method that creates instances of Session.
	 * 
	 * @param knowledgeBase the knowledge base used in the case.
	 * @return new Session instance based on the specified knowledge base
	 */
	public static Session createSession(KnowledgeBase knowledgeBase) {
		return createSession(null, knowledgeBase, new Date());
	}

	/**
	 * Factory-method that creates instances of Session.
	 * 
	 * @param name name of the session
	 * @param knowledgeBase the knowledge base used in the case.
	 * @return new Session instance based on the specified knowledge base
	 */
	public static Session createSession(String name, KnowledgeBase knowledgeBase) {
		return createSession(name, knowledgeBase, new Date());
	}

	/**
	 * Factory-method that creates instances of Session
	 * 
	 * @created 28.01.2011
	 * @param kb {@link KnowledgeBase}
	 * @param creationDate Date of creation
	 * @return {@link Session}
	 */
	public static DefaultSession createSession(KnowledgeBase kb, Date creationDate) {
		return createSession(null, kb, creationDate);
	}

	/**
	 * Factory-method that creates instances of Session
	 * 
	 * @created 27.09.2010
	 * @param id the ID
	 * @param kb {@link KnowledgeBase}
	 * @param creationDate Date of creation
	 * @return {@link Session}
	 */
	public static DefaultSession createSession(String id, KnowledgeBase kb, Date creationDate) {
		DefaultSession defaultSession = new DefaultSession(id, kb, creationDate);
		for (PropagationListener propagationListener : propagationListeners) {
			defaultSession.getPropagationManager().addListener(propagationListener);
		}
		try {
			defaultSession.initPSMethods();
		}
		catch (SessionTerminatedException e) {
			LOGGER.warn("Endless loop in initialization detected, session terminated", e);
		}
		return defaultSession;
	}

	/**
	 * Factory-method that creates instances of Session.
	 * 
	 * @param id the ID
	 * @param knowledgeBase the knowledge base used in the case.
	 * @param formStrategy the specified
	 * @return new Session instance based on the specified id, knowledge base
	 *         and form strategy
	 * @deprecated use Interview.setFormStrategy() to configure the FormStrategy
	 *             in an Interview or use the PSConfig of the PSMethodInterview
	 *             to configure the FormStrategy
	 */
	@Deprecated
	public static DefaultSession createSession(String id,
			KnowledgeBase knowledgeBase,
			de.d3web.core.session.interviewmanager.FormStrategy formStrategy, Date creationDate) {
		DefaultSession session = createSession(id, knowledgeBase, creationDate);
		session.getInterview().setFormStrategy(formStrategy);
		return session;
	}

	/**
	 * Adds a {@link PropagationListener} which will be added to each session
	 * created in this factory before the initialization of the {@link PSMethod}s
	 * 
	 * @created 23.04.2012
	 * @param propagationListener {@link PropagationListener}
	 */
	public static void addPropagationListener(PropagationListener propagationListener) {
		propagationListeners.add(propagationListener);
	}

	/**
	 * Removes a {@link PropagationListener} from being added to each created
	 * Session
	 * 
	 * @created 23.04.2012
	 */
	public static void removePropagationListener(PropagationListener propagationListener) {
		propagationListeners.remove(propagationListener);
	}
}