/*
 * Copyright (C) 2011 denkbares GmbH, Germany
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
package de.d3web.costbenefit.blackboard;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PropagationListener;
import de.d3web.core.inference.PropagationManager;
import de.d3web.core.inference.SessionTerminatedException;
import de.d3web.core.knowledge.InfoStore;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.DefaultBlackboard;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.core.session.protocol.Protocol;

/**
 * This class represents a minimal Session that is capable to provide basic capabilities to evaluate conditions on it
 * and add facts to the session. This session does not have any problem solver or functional propagation manager. It
 * also does not provide a protocol or any reliable master data information (change date, create date, name, id, etc).
 * <p>
 * The session itself decorates an existing session, like a glass panel. You can read the values from the session
 * decorated one and overwrite them by setting new facts.
 *
 * @author volker_belli
 * @created 16.09.2011
 */
public class DecoratedSession implements DerivedSession {

	private final long time = System.currentTimeMillis();
	private final KnowledgeBase knowledgeBase;
	private final Blackboard blackboard;
	private final Session rootSession;

	public DecoratedSession(@NotNull Session other) {
		this.knowledgeBase = other.getKnowledgeBase();
		this.blackboard = new DecoratedBlackboard(this, (DefaultBlackboard) other.getBlackboard());
		this.rootSession = (other instanceof DecoratedSession)
				? ((DecoratedSession) other).rootSession
				: other;
	}

	@Override
	@NotNull
	public Session getRootSession() {
		return this.rootSession;
	}

	@Override
	public Protocol getProtocol() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void touch() {
	}

	@Override
	public void touch(Date date) {
	}

	@Override
	public Date getLastChangeDate() {
		return new Date(time);
	}

	@Override
	public Date getCreationDate() {
		return new Date(time);
	}

	@Override
	public String getId() {
		return "{decorated-session:" + rootSession.getId() + "}";
	}

	@Override
	public String getName() {
		return rootSession.getName();
	}

	@Override
	public void setName(String name) {
		rootSession.setName(name);
	}

	@Override
	public InfoStore getInfoStore() {
		throw new UnsupportedOperationException();
	}

	@Override
	public KnowledgeBase getKnowledgeBase() {
		return knowledgeBase;
	}

	@Override
	public List<? extends PSMethod> getPSMethods() {
		return Collections.emptyList();
	}

	@Override
	public <T extends PSMethod> T getPSMethodInstance(Class<T> solverClass) {
		// because of having no PSMethods,
		// we have no instance of the specified class
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public de.d3web.core.session.interviewmanager.Interview getInterview() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Blackboard getBlackboard() {
		return blackboard;
	}

	@Override
	public PropagationManager getPropagationManager() {
		return new PropagationManager() {

			@Override
			public void propagate(ValueObject object, Value oldValue, PSMethod psMethod) {
			}

			@Override
			public void propagate(InterviewObject object, Value oldValue) {
			}

			@Override
			public void propagate(ValueObject object, Value oldValue) {
			}

			@Override
			public void openPropagation(long time) {
			}

			@Override
			public void openPropagation() {
			}

			@Override
			public boolean isInPropagation() {
				return false;
			}

			@Override
			public long getPropagationIndex() {
				// as there are no PSMethods, no additional propagation committed
				return rootSession.getPropagationManager().getPropagationIndex();
			}

			@Override
			public long getPropagationTime() {
				return time;
			}

			@Override
			public long getPropagationTimeOfNoReturn() {
				return time;
			}

			@Override
			public void setPropagationTimeOfNoReturn(long timeOfNoReturn) {
			}

			@Override
			public void commitPropagation() {
			}

			@Override
			public void addListener(PropagationListener listener) {
			}

			@Override
			public void removeListener(PropagationListener listener) {
			}

			@Override
			public void forcePropagate(ValueObject object) {
			}

			@Override
			public void terminate() {
			}

			@Override
			public void forcePropagate(ValueObject object, Value oldValue) throws SessionTerminatedException {
			}

			@Override
			public boolean isForced(ValueObject object) {
				return false;
			}
		};
	}

	@Override
	public <T extends SessionObject> T getSessionObject(SessionObjectSource<T> item) {
		throw new UnsupportedOperationException();
	}
}
